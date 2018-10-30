/* 
 * VURLClassLoader.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007–2018 by Michael Hoffer,
 * Copyright (c) 2015–2018 G-CSC, Uni Frankfurt,
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn)
 * 
 * This file is part of Visual Reflection Library (VRL).
 *
 * VRL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * see: http://opensource.org/licenses/LGPL-3.0
 *      file://path/to/VRL/src/eu/mihosoft/vrl/resources/license/lgplv3.txt
 *
 * VRL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * This version of VRL includes copyright notice and attribution requirements.
 * According to the LGPL this information must be displayed even if you modify
 * the source code of VRL. Neither the VRL Canvas attribution icon nor any
 * copyright statement/attribution may be removed.
 *
 * Attribution Requirements:
 *
 * If you create derived work you must do three things regarding copyright
 * notice and author attribution.
 *
 * First, the following text must be displayed on the Canvas:
 * "based on VRL source code". In this case the VRL canvas icon must be removed.
 * 
 * Second, the copyright notice must remain. It must be reproduced in any
 * program that uses VRL.
 *
 * Third, add an additional notice, stating that you modified VRL. A suitable
 * notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Please cite the publication(s) listed below.
 *
 * Publications:
 *
 * M. Hoffer, C. Poliwoda, & G. Wittum. (2013). Visual reflection library:
 * a framework for declarative GUI programming on the Java platform.
 * Computing and Visualization in Science, 2013, 16(4),
 * 181–192. http://doi.org/10.1007/s00791-014-0230-y
 */

package eu.mihosoft.vrl.io;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.*;
import java.util.jar.JarFile;

/**
 * A closeable URL classloader. This allows to replace jar-Files without waiting
 * for unpredictable GC behavior which eventually releases file locks etc.
 *
 * Additionally it unloads native libraries when closing this classloader.
 * 
 * Based on ideas from 
 * {@link http://management-platform.blogspot.com/2009/01/classloaders-keeping-jar-files-open.html}
 * and
 * {@link http://loracular.blogspot.com/2009/12/dynamic-class-loader-with.html}
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VURLClassLoader extends URLClassLoader {

    protected HashSet<String> setJarFileNames2Close = new HashSet<String>();

    public VURLClassLoader(URL[] urls) {
        super(urls);
    }

    public VURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public VURLClassLoader(
            URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    @Override
    public void close() throws IOException {
        setJarFileNames2Close.clear();
        try {
          closeClassLoader(this);
        } catch(Throwable tr) {
            // won't work with newer jdk's
            // but super.close() whould be sufficient there since
            // the implementation has changed a bit
        }
        super.close();
        finalizeNativeLibs(this);
        cleanupJarFileFactory();
    }

    private static Class classForName(String name) throws ClassNotFoundException {
        try {
            ClassLoader contextClassLoader =
                    Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                return contextClassLoader.loadClass(name);
            } else {
                return Class.forName(name);
            }
        } catch (Exception e) {
            return Class.forName(name);
        }
    }

    /**
     * cleanup jar file factory cache
     */
    @SuppressWarnings({"nls", "unchecked"})
    public boolean cleanupJarFileFactory() {
        boolean res = false;
        Class classJarURLConnection = null;
        try {
            classJarURLConnection =
                    classForName("sun.net.www.protocol.jar.JarURLConnection");
        } catch (ClassNotFoundException e) {
            //ignore
        }
        if (classJarURLConnection == null) {
            return res;
        }
        Field f = null;
        try {
            f = classJarURLConnection.getDeclaredField("factory");
        } catch (NoSuchFieldException e) {
            //ignore
        }
        if (f == null) {
            return res;
        }
        f.setAccessible(true);
        Object obj = null;
        try {
            obj = f.get(null);
        } catch (IllegalAccessException e) {
            //ignore
        }
        if (obj == null) {
            return res;
        }
        Class classJarFileFactory = obj.getClass();
        //
        HashMap fileCache = null;
        try {
            f = classJarFileFactory.getDeclaredField("fileCache");
            f.setAccessible(true);
            obj = f.get(null);
            if (obj instanceof HashMap) {
                fileCache = (HashMap) obj;
            }
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
            //ignore
        }
        HashMap urlCache = null;
        try {
            f = classJarFileFactory.getDeclaredField("urlCache");
            f.setAccessible(true);
            obj = f.get(null);
            if (obj instanceof HashMap) {
                urlCache = (HashMap) obj;
            }
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
            //ignore
        }
        if (urlCache != null) {
            HashMap urlCacheTmp = (HashMap) urlCache.clone();
            Iterator it = urlCacheTmp.keySet().iterator();
            while (it.hasNext()) {
                obj = it.next();
                if (!(obj instanceof JarFile)) {
                    continue;
                }
                JarFile jarFile = (JarFile) obj;
                if (setJarFileNames2Close.contains(jarFile.getName())) {
                    try {
                        jarFile.close();
                    } catch (IOException e) {
                        //ignore
                    }
                    if (fileCache != null) {
                        fileCache.remove(urlCache.get(jarFile));
                    }
                    urlCache.remove(jarFile);
                }
            }
            res = true;
        } else if (fileCache != null) {
            // urlCache := null
            HashMap fileCacheTmp = (HashMap) fileCache.clone();
            Iterator it = fileCacheTmp.keySet().iterator();
            while (it.hasNext()) {
                Object key = it.next();
                obj = fileCache.get(key);
                if (!(obj instanceof JarFile)) {
                    continue;
                }
                JarFile jarFile = (JarFile) obj;
                if (setJarFileNames2Close.contains(jarFile.getName())) {
                    try {
                        jarFile.close();
                    } catch (IOException e) {
                        //ignore
                    }
                    fileCache.remove(key);
                }
            }
            res = true;
        }
        setJarFileNames2Close.clear();
        return res;
    }

    /**
     * close jar files of cl
     *
     * @param cl
     * @return
     */
    @SuppressWarnings({"nls", "unchecked"})
    public boolean closeClassLoader(ClassLoader cl) {

        boolean res = false;
        if (cl == null) {
            return res;
        }
        Class classURLClassLoader = URLClassLoader.class;
        Field f = null;
        try {
            f = classURLClassLoader.getDeclaredField("ucp");
        } catch (NoSuchFieldException e1) {
            //ignore
        }
        if (f != null) {
            f.setAccessible(true);
            Object obj = null;
            try {
                obj = f.get(cl);
            } catch (IllegalAccessException e1) {
                //ignore
            }
            if (obj != null) {
                final Object ucp = obj;
                f = null;
                try {
                    f = ucp.getClass().getDeclaredField("loaders");
                } catch (NoSuchFieldException e1) {
                    //ignore
                }
                if (f != null) {
                    f.setAccessible(true);
                    ArrayList loaders = null;
                    try {
                        loaders = (ArrayList) f.get(ucp);
                        res = true;
                    } catch (IllegalAccessException e1) {
                        //ignore
                    }
                    for (int i = 0; loaders != null && i < loaders.size(); i++) {
                        obj = loaders.get(i);
                        f = null;
                        try {
                            f = obj.getClass().getDeclaredField("jar");
                        } catch (NoSuchFieldException e) {
                            //ignore
                        }
                        if (f != null) {
                            f.setAccessible(true);
                            try {
                                obj = f.get(obj);
                            } catch (IllegalAccessException e1) {
                                // ignore
                            }
                            if (obj instanceof JarFile) {
                                final JarFile jarFile = (JarFile) obj;
                                setJarFileNames2Close.add(jarFile.getName());
                                //try {
                                //	jarFile.getManifest().clear();
                                //} catch (IOException e) {
                                //	// ignore
                                //}
                                try {
                                    jarFile.close();
                                } catch (IOException e) {
                                    // ignore
                                }
                            }
                        }
                    }
                }
            }
        }
        return res;
    }

    /**
     * finalize native libraries
     *
     * @param cl
     * @return
     */
    @SuppressWarnings({"nls", "unchecked"})
    public boolean finalizeNativeLibs(ClassLoader cl) {
        boolean res = false;
        Class classClassLoader = ClassLoader.class;
        java.lang.reflect.Field nativeLibraries = null;
        try {
            nativeLibraries =
                    classClassLoader.getDeclaredField("nativeLibraries");
        } catch (NoSuchFieldException e1) {
            //ignore
        }
        if (nativeLibraries == null) {
            return res;
        }
        nativeLibraries.setAccessible(true);
        Object obj = null;
        try {
            obj = nativeLibraries.get(cl);
        } catch (IllegalAccessException e1) {
            //ignore
        }
        if (!(obj instanceof Vector)) {
            return res;
        }
        res = true;
        Vector java_lang_ClassLoader_NativeLibrary = (Vector) obj;
        for (Object lib : java_lang_ClassLoader_NativeLibrary) {
            java.lang.reflect.Method finalize = null;
            try {
                finalize =
                        lib.getClass().getDeclaredMethod(
                        "finalize", new Class[0]);
            } catch (NoSuchMethodException e) {
                //ignore
            }
            if (finalize != null) {
                finalize.setAccessible(true);
                try {
                    finalize.invoke(lib, new Object[0]);
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {
                    //ignore
                }
            }
        }
        return res;
    }
}
