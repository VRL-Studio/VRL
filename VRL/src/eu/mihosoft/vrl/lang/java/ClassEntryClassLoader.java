/* 
 * ClassEntryClassLoader.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2007–2017 by Michael Hoffer
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

package eu.mihosoft.vrl.lang.java;

import eu.mihosoft.vrl.lang.ClassEntry;
import eu.mihosoft.vrl.system.VClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.tools.JavaFileObject.Kind;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ClassEntryClassLoader extends ClassLoader {

    private Map<String, ClassEntry> cache =
            new HashMap<String, ClassEntry>();
    private Map<String, ClassFileObject> cache2 =
            new HashMap<String, ClassFileObject>();

    public ClassEntryClassLoader() {
        super(ClassLoader.getSystemClassLoader());
    }

    public ClassEntryClassLoader(ClassLoader loader) {
        super(loader);
        if (loader == null) {
            throw new IllegalArgumentException("Argument \"null\" not supported!");
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        System.out.println("LOADCLASS: " + name);
        return super.loadClass(name);
    }

    public void add(String name, ClassFileObject obj) {
//        ByteArrayJavaFileObject co = cache2.get(name);
//        
//        if (co == null) {
//            cache2.put(name, obj);
//        }

        cache2.put(name, obj);
    }

    public void add(ClassEntry obj) {
//        ClassEntry co = cache.get(obj.getName());
//
//        if (co == null) {
//            cache.put(obj.getName(), obj);
//        }

        cache.put(obj.getName(), obj);
    }

    private byte[] getClassBytes(String name) throws Exception {
        byte[] result = null;

        ClassEntry cE = cache.get(name);
        if (cE != null && cE.getDataAsBytes().length > 0) {
            result = cE.getDataAsBytes();
        } else {
            ClassFileObject obj = cache2.get(name);
            result = obj.getClassBytes();
        }

        return result;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> cls = null;

        try {
            byte[] ba = getClassBytes(name);
            if (ba==null) {
                throw new Exception();
            }
            cls = defineClass(name, ba, 0, ba.length);
        } catch (Exception ex) {
            throw new ClassNotFoundException("class name: " + name, ex);
        }
        return cls;
    }

    /**
     * Returns the classes defined in this class loader.
     * @return the classes defined in this class loader
     */
    public Collection<ClassEntry> getClasses() {
        ArrayList<ClassEntry> result = new ArrayList<ClassEntry>();

        result.addAll(cache.values());

        for (String name : cache2.keySet()) {

            result.add(new ClassEntry(name, cache2.get(name)));
        }

        return result;

//        return cache.values();
    }

    /**
     * Adds classes to this class loader. 
     * @param classes classes to add
     */
    public void addClasses(Collection<ClassEntry> classes) {
        for (ClassEntry e : classes) {
            add(e);
        }
    }

    /**
     * Clears this class loader, i.e., removes all defined classes.
     */
    public void clear() {
        cache.clear();
    }

    void delete(String className) {
        if (cache.remove(className) != null) {
            System.out.println("DELETING0: " + className);
        }

        if (cache2.remove(className) != null) {
            System.out.println("DELETING1: " + className);
        }

        if (getParent() instanceof VClassLoader) {

            try {
                Class<?> cls = getParent().loadClass(className);
                if (cls != null) {
                    System.out.println("DELETING2: " + className);
                    ((VClassLoader) getParent()).removeClass(cls);
                }
            } catch (ClassNotFoundException ex) {
//                Logger.getLogger(ClassEntryClassLoader.class.getName()).
//                        log(Level.SEVERE, null, ex);
            }
        }
    }
//    public void close() {
//        System.out.println("CLOSE:");
//        for(String n : cache2.keySet()) {
//            cache.put(n, new ClassEntry(n, cache2.get(n)));
//            
//        }
//        cache2.clear();
//    }
}
