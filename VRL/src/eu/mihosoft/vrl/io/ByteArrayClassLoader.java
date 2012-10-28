/* 
 * ByteArrayClassLoader.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2012 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2012 by Michael Hoffer
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
 * Third, add an additional notice, stating that you modified VRL. In addition
 * you must cite the publications listed below. A suitable notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Publications:
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, 2011, in press.
 */

package eu.mihosoft.vrl.io;

import java.lang.*;
import java.net.*;
import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class loader that constructs classes from byte arrays.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ByteArrayClassLoader extends ClassLoader {

    private HashMap<String, Class<?>> classes = new HashMap<String, Class<?>>();
    private ArrayDeque<UnresolvedClass> unresolvedClasses =
            new ArrayDeque<UnresolvedClass>();
    private HashMap<String, URL> resources = new HashMap<String, URL>();

    /**
     * Constructor.
     */
    public ByteArrayClassLoader() {
        super(ByteArrayClassLoader.class.getClassLoader());
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return findClass(className);
    }

    @Override
    public URL getResource(String name) {
        System.out.println("Resource returned: " + name);
        return resources.get(name);
    }
    
    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream result = null;
        URL url = getResource(name);
        if (url !=null) {
            try {
                result = url.openStream();
            } catch (IOException ex) {
                Logger.getLogger(ByteArrayClassLoader.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }


    public void addResource(String name, byte[] data) {
        System.out.println(">> Adding resource \"" + name + "\"");
        try {
//            resources.put(name, new URL(null, name,
//                    new InputStreamURLStreamHandler(
//                    new ByteArrayInputStream(data))));

            URL url = new URL("inputstream", "", 0, name,
                    new InputStreamURLStreamHandler(
                    new ByteArrayInputStream(data)));

            resources.put(name, url);

        } catch (MalformedURLException ex) {
            Logger.getLogger(ByteArrayClassLoader.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Adds a class from byte array.
     * @param name the class name
     * @param data the class file as byte array
     * @return the class that has been added
     */
    public Class<?> addClass(String name, byte[] data) {
        Class<?> result = getClasses().get(name);
        if (result == null) {
            try {
                result = getParent().loadClass(name);
            } catch (ClassNotFoundException ex) {
                try {
                    //System.out.println("Class: " + name);
                    Class<?> c = defineClass(name, data, 0, data.length);
                    classes.put(name, c);
                    result = c;
                } catch (NoClassDefFoundError e) {
                    unresolvedClasses.addFirst(new UnresolvedClass(name, data));
//                } catch (java.lang.LinkageError e) {
//                    System.out.println(">> class \"<b><tt>" + name +
//                            "</b></tt>\" can't be added because it's already" +
//                            " defined.");
//                    result = findLoadedClass("name");
                }
            } catch (java.lang.SecurityException e) {
                System.out.println(">> class \"<b><tt>" + name +
                        "</b></tt>\" can't be added.");
            }
        }
        return result;
    }

    /**
     * Resolves class definition conflicts. This can occur if another class
     * has to be defined before because the current class depends on it. To
     * be sure that all resolvable conflicts have been solved this method
     * has to be called as long as it returns <code>true</code> which means
     * there are still resolvable conflicts left.
     * @return all classes that could be loaded after resolve
     */
    public ArrayList<Class<?>> resolveClasses() {

        ArrayList<Class<?>> result = new ArrayList<Class<?>>();

//        int sizeBefore = unresolvedClasses.size();

        int maxIter = unresolvedClasses.size();

        for (int i = 0; i < maxIter; i++) {
            UnresolvedClass uC = unresolvedClasses.pollLast();
            Class c = addClass(uC.getName(), uC.getData());
            if (c != null) {
                result.add(c);
            }
        }

//        int sizeAfter = unresolvedClasses.size();

        return result;
    }

    @Override
    protected Class<?> findClass(String className) {
        Class<?> result = null;
        result = (Class<?>) classes.get(className);
        if (result == null) {
            try {
                return findSystemClass(className);
            } catch (Exception e) {
            }

        }
        return result;
    }

    /**
     * Returns the classes.
     * @return the classes
     */
    public HashMap<String, Class<?>> getClasses() {
        return classes;
    }
}


