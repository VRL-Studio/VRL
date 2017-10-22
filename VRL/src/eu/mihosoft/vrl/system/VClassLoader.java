/* 
 * VClassLoader.java
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

package eu.mihosoft.vrl.system;

import eu.mihosoft.vrl.io.VURLClassLoader;
import eu.mihosoft.vrl.system.VParamUtil;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classloader used to load classes from sourcecode that is provided by
 * <code>.vrlx</code> files.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VClassLoader extends ClassLoader {

    private HashMap<String, Class<?>> classes = new HashMap<String, Class<?>>();
    private Set<URL> urls = new HashSet<URL>();
    private VURLClassLoader loader;

    public VClassLoader() {
        //
    }

    /**
     * Constructor.
     */
    public VClassLoader(ClassLoader parent) {
        super(parent);
//        super(VRL.getExternalPluginClassLoader());
    }

    private void openIfClosed() {
        if (loader == null) {
            loader = new VURLClassLoader(
                    urls.toArray(new URL[urls.size()]), getParent());
        }
    }

    private void closeIfOpened() throws IOException {
        if (loader != null) {
            loader.close();
            loader = null;
        }

        System.gc();
    }

    public void close() throws IOException {
        closeIfOpened();
    }

//    /**
//     * Constructor.
//     */
//    public VClassLoader(ClassLoader loader) {
//
//        super(loader);
//
//        if (loader == null) {
//            throw new IllegalArgumentException("Argument \"null\" not supported!");
//        }
//    }
//    @Override
//    public Class<?> loadClass(String className) throws ClassNotFoundException {
//        return findClass(className);
//    }
//    @Override
//    public Class<?> loadClass(String className) throws ClassNotFoundException {
//
//        Class<?> result = null;
//
//        // first try to load session classes
//        try {
//            result = findClass(className);
//        } catch (Throwable tr) {
//            //
//        }
//
//        // load plugin classes if not defined in session
//        // this solves the problem of editing a plugin while the plugin is
//        // loaded
//        if (result == null) {
//            result =  super.loadClass(className);
//        }
//        
//        return result;
//    }
    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {

        Class<?> result = null;

        // try to load classes from session and public plugin classloader
        try {
            openIfClosed();
            result = loader.loadClass(className);
        } catch (Exception ex) {
            //
        }

        // if this does not work check if classes map contains the class
        if (result == null) {
            result = getClasses().get(className);
        }

        // not found. throw exception.
        if (result == null) {
            throw new ClassNotFoundException("class name: " + className);
        }

        return result;
    }

    /**
     * Adds a class object to this class loader.
     *
     * @param c the class to add
     */
    public void addClass(Class<?> c) {

//        System.out.println("VClassLoader: adding class: " + c.getName());
        getClasses().put(c.getName(), c);
    }

    /**
     * Updates the url classloader to allow reloading new versions of classes
     * and reloads all classes currently used by the canvas.
     */
    public void updateClassLoader() {

        try {
            closeIfOpened();
        } catch (IOException ex) {
            Logger.getLogger(VClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

        loader = new VURLClassLoader(
                urls.toArray(new URL[urls.size()]), getParent());

//        System.out.println("LOADER-update:" + loader);

        for (String clsName : classes.keySet()) {
            try {
                addClass(loader.loadClass(clsName));
            } catch (ClassNotFoundException ex) {
//                Logger.getLogger(VClassLoader.class.getName()).
//                        log(Level.SEVERE, null, ex);
            }
        }

        try {
            closeIfOpened();
        } catch (IOException ex) {
            Logger.getLogger(VClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addURL(URL url) {

        urls.add(url);

//        loader = new VURLClassLoader(
//                urls.toArray(new URL[urls.size()]), getParent());
////        System.out.println("LOADER-addURL:" + loader);

//        closeIfOpened();

    }

    public void removeURL(URL url) {

        urls.remove(url);

//        if (loader != null) {
//            loader.close();
//        }

//        loader = new VURLClassLoader(
//                urls.toArray(new URL[urls.size()]), getParent());
////        System.out.println("LOADER-rmURL:" + loader);
    }

    public void clearURLs() {

        urls.clear();

    }

    /**
     * Removes a class object from this class loader.
     */
    public void removeClass(Class<?> c) {

        Class<?> cls = getClasses().remove(c.getName());

//        System.out.println("REMOVING: " + cls);
    }

    /**
     * Removes a class object from this class loader.
     */
    public void removeClassByName(String name) {

        VParamUtil.throwIfNull(name);

        Class<?> cls = getClasses().remove(name);

//        System.out.println("REMOVING by name: " + cls);
    }

    /**
     * Returns the classes.
     *
     * @return the classes
     */
    public HashMap<String, Class<?>> getClasses() {

        return classes;
    }

    /**
     * Removes all classes.
     */
    public void clear() {

        classes.clear();


//        if (loader != null) {
//            loader.close();
//        }
//
//        loader = new VURLClassLoader(urls.toArray(new URL[urls.size()]));

//        System.out.println("LOADER-clear:" + loader);
    }

    @Override
    protected void finalize() {
        try {
            super.finalize();
        } catch (Throwable ex) {
            Logger.getLogger(VClassLoader.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

//        System.err.println("VClassLoader finalized!");
    }

    /**
     * Returns an updated version of the specified class.
     */
    public Class<?> reloadClass(Class<?> cls) {

        Class<?> result = cls;

        try {
            result = loadClass(cls.getName());
        } catch (Exception ex) {
            System.out.println(">> canvas-classloader: cannot update class " + cls);
        }

        return result;
    }
}
