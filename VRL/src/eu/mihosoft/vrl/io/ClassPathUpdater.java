/* 
 * ClassPathUpdater.java
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

import eu.mihosoft.vrl.system.VTerminalUtil;
import eu.mihosoft.vrl.visual.SplashScreenGenerator;
import java.lang.reflect.InvocationTargetException;

import java.lang.reflect.Method;

import java.io.File;
import java.io.FileFilter;

import java.io.IOException;

import java.net.URL;

import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Allows programs to modify the classpath during runtime.
 */
public class ClassPathUpdater {

    /**
     * Used to find the method signature.
     */
    private static final Class<?>[] PARAMETERS = new Class<?>[]{URL.class};
    /**
     * Class containing the private addURL method.
     */
    private static final Class<?> CLASS_LOADER = URLClassLoader.class;

    /**
     * Adds a new path to the classloader. If the given string points to a file,
     * then that file's parent file (i.e., directory) is used as the directory
     * to add to the classpath. If the given string represents directory, then
     * the directory is directly added to the classpath.
     *
     * @param s the directory to add to the classpath (or a file, which will
     * relegate to its directory).
     * @throws IOException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void add(String s)
            throws IOException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        add(new File(s));

    }

    /**
     * Adds a new path to the classloader. If the given file object is a file,
     * then its parent file (i.e., directory) is used as the directory to add to
     * the classpath. If the given string represents a directory, then the
     * directory it represents is added.
     *
     * @param f the directory (or enclosing directory if a file) to add to the
     * classpath.
     * @throws IOException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     *
     */
    public static void addDir(File f)
            throws IOException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        f = f.isDirectory() ? f : f.getParentFile();
        add(f.toURI().toURL());
    }

    /**
     * Adds a new path to the classloader. If the given file object is a file,
     * then its parent file (i.e., directory) is used as the directory to add to
     * the classpath. If the given string represents a directory, then the
     * directory it represents is added.
     *
     * @param f the directory (or enclosing directory if a file) to add to the
     * classpath.
     *
     * @throws IOException
     */
    public static void add(File f) throws IOException {
        add(f.toURI().toURL());
    }

    /**
     * Adds a new file to the classloader.
     *
     * @param url the file to include when searching the classpath.
     * @throws IOException
     */
    public static void add(URL url)
            throws IOException {
        try {

            String message = ">> adding \"" + url + "\" to system classpath";

            System.out.println(message);
            SplashScreenGenerator.printBootMessage(message);

            try {
                ClassLoader classLoader = getClassLoader(); // test if urlclassloader is present

                Method method = CLASS_LOADER.getDeclaredMethod("addURL", PARAMETERS);
                method.setAccessible(true);
                method.invoke(classLoader, new Object[]{url});

            } catch (RuntimeException ex) {

                message = ">> ERROR: The system classloader of the current"
                        + " JRE does not support dynamic classloading at runtime.";

                System.out.println(VTerminalUtil.red(message));
                SplashScreenGenerator.printBootMessage(message);
            }

        } catch (IllegalAccessException ex) {
            Logger.getLogger(ClassPathUpdater.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ClassPathUpdater.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ClassPathUpdater.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ClassPathUpdater.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ClassPathUpdater.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (RuntimeException ex) {
            Logger.getLogger(ClassPathUpdater.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Adds all jar files in the given directory (recursive).
     *
     * @param dir the directory
     */
    public static void addAllJarsInDirectory(File dir) {
        if (dir != null && dir.isDirectory()) {

            for (File f : dir.listFiles(new FileFilter() {

                @Override
                public boolean accept(File pathName) {
                    return pathName.getName().toLowerCase().endsWith(".jar")
                            || pathName.isDirectory();
                }
            })) {

                if (f.isFile()) {
                    try {
                        add(f);
                    } catch (IOException ex) {
                        Logger.getLogger(ClassPathUpdater.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }
                } else if (f.isDirectory()) {
                    addAllJarsInDirectory(f);
                }
            }

        } else {
            System.err.println(">> ClassPathUpdater.addAllJarsInDir(\""
                    + dir + "\"): directory does not exist!");
        }
    }

    private static URLClassLoader getClassLoader() {

        if (ClassLoader.getSystemClassLoader() instanceof URLClassLoader) {
            return (URLClassLoader) ClassLoader.getSystemClassLoader();
        } else {
            throw new RuntimeException("The system classloader of the current"
                    + " JRE does not support dynamic classloading at runtime.");
        }

    }
}
