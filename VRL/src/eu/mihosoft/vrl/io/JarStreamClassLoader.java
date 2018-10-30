/* 
 * JarStreamClassLoader.java
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class loader can be used to load all classes of a jar input stream. This
 * is useful to search a jar file for specific classes or classes that implement
 * a specific interface.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class JarStreamClassLoader extends ByteArrayClassLoader {

    /**
     * Constructor.
     */
    public JarStreamClassLoader() {
    }

    /**
     * Constructor.
     * @param jarInStream the stream to read from
     * @throws IOException
     */
    public JarStreamClassLoader(JarInputStream jarInStream) throws IOException {
        addClassesFromStream(jarInStream);
    }
    

    /**
     * <p>
     * Adds all classes and resources from a given jar input stream. Jar entries
     * that are jar files will be recusrively read and all contained classes
     * snd resources will be loaded.
     * </p>
     * <p>
     * <b>Warning:</b> This method is not well tested and not all packages can
     * be loaded!
     * </p>
     * @param jarInStream the stream to read from
     * @return a list containing all classes of the stream that could be loaded
     * @throws IOException
     */
    public ArrayList<Class<?>> addClassesFromStream(JarInputStream jarInStream)
            throws IOException {
        ArrayList<Class<?>> streamClasses = new ArrayList<Class<?>>();

        // the current jar entry
        JarEntry entry = jarInStream.getNextJarEntry();

        // iterate through all entries
        while (entry != null) {
            String name = entry.getName();

            // indicates whether the current entry is in folder or if it is
            // in the root folder of the jar
//            boolean isInDirectory = name.lastIndexOf("/") > 0;


            // indicates whether the entry is a file
            boolean isFile = !entry.isDirectory();

            // indicates whether the entry is a class file
            boolean isClassFile = isFile && name.endsWith(".class");

            // indicates whether the entry is a jar file
            boolean isJarFile = isFile && name.endsWith(".jar");

            if (isClassFile) {
                String className = VJarUtil.pathToClassName(name);
                byte[] classData = VJarUtil.readCurrentJarEntry(jarInStream);
                Class<?> c = addClass(className, classData);
                if (c != null) {
                    streamClasses.add(c);
                }
            } else if (isJarFile) {
                System.out.println(">> entering \"" + name + "\":");
                // loads the jar file in a byte array, converts it to a
                // jar input stream and recursively calls this loadClasses
                // method
                byte[] jarData = VJarUtil.readCurrentJarEntry(jarInStream);
                ByteArrayInputStream byteStream =
                        new ByteArrayInputStream(jarData);
                JarInputStream jarStream =
                        new JarInputStream(byteStream);
                addClassesFromStream(jarStream);
            } else if (isFile) {
                byte[] fileData = VJarUtil.readCurrentJarEntry(jarInStream);
                addResource(name, fileData);
            }

            entry = jarInStream.getNextJarEntry();
        }

        int numberOfResolvedClasses = Integer.MAX_VALUE;

        // resolves class dependencies
        while (numberOfResolvedClasses > 0) {
            ArrayList<Class<?>> resolvedClasses = resolveClasses();
            numberOfResolvedClasses = resolvedClasses.size();
            streamClasses.addAll(resolvedClasses);
        }

        System.out.println(">> Class Resolution finished!");

        jarInStream.close();

        return streamClasses;
    }

    /**
     * Returns the class names of all classes in a given jar stream.
     * @param jarInStream
     * @return a list containing the class names of all classes in the
     *         jar stream
     * @throws IOException
     */
    public ArrayList<String> getClassNamesFromStream(
            JarInputStream jarInStream) throws IOException {
        ArrayList<String> result = new ArrayList<String>();

        // the current jar entry
        JarEntry entry = jarInStream.getNextJarEntry();

        // iterate through all entries
        while (entry != null) {
            String name = entry.getName();

            // indicates whether the current entry is in folder or if it is
            // in the root folder of the jar
            boolean isInDirectory = name.lastIndexOf("/") > 0;

            // indicates that the entry is a class file
            boolean isClassFile = name.endsWith(".class");

            if (isInDirectory && isClassFile) {
                String className = VJarUtil.pathToClassName(name);
                result.add(className);
            }

            entry = jarInStream.getNextJarEntry();
        }

        jarInStream.close();

        return result;
    }

    /**
     * Removes all classes from this class loader that are defined in a given
     * jar stream.
     * @param jarInStream the stream
     */
    public void removeClassesFromStream(JarInputStream jarInStream) {
        ArrayList<String> classNames = null;
        try {
            classNames = getClassNamesFromStream(jarInStream);
        } catch (IOException ex) {
            Logger.getLogger(JarStreamClassLoader.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        for (String c : classNames) {
            getClasses().remove(c);
        }
    }
}
