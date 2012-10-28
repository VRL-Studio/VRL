/* 
 * ClassFileLoader.java
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

import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.MessageBox;
import eu.mihosoft.vrl.visual.MessageList;
import eu.mihosoft.vrl.visual.MessageType;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads Java class files and source files. If the source file hasn't been
 * compiled this will be automatically be done.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ClassFileLoader implements FileLoader {

    private Canvas mainCanvas;

    /**
     * Constructor.
     * @param mainCanvas the main canvas object
     */
    public ClassFileLoader(Canvas mainCanvas) {
        setMainCanvas(mainCanvas);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object loadFile(File file) throws IOException {
        // Create a File object on the root of the directory containing the
        // class file

        Object o = null;

        String className = file.getName();

        boolean isCodeFile =
                className.lastIndexOf(new String(".java").toLowerCase()) > -1;

        if (isCodeFile) {
//            System.out.println("Compile: " + file.getPath());
            compile(file.getPath());
            int extIndex =
                    className.lastIndexOf(new String(".java").toLowerCase());
            className = className.substring(0, extIndex) + ".class";
        }

        try {
            // Convert File to a URL
            URL url = new URL("file://" + file.getParent() + "/");
//            URL url = new URL("file:/home/miho/plugins/");
            URL[] urls = new URL[]{url};

//            System.out.println("PATH:" + file.getParent() + "/");

            // Create a new class loader with the directory
            ClassLoader cl = new URLClassLoader(urls);

            // Load in the class; MyClass.class should be located in
            // the directory file:/c:/myclasses/com/mycompany


            int extIndex =
                    className.lastIndexOf(new String(".class").toLowerCase());

            className = className.substring(0, extIndex);

            System.out.println("load class: " + className);

            Class cls = cl.loadClass(className);

            try {

                o = cls.getConstructor().newInstance();

            } catch (NoSuchMethodException ex) {
                Logger.getLogger(
                        ClassFileLoader.class.getName()).
                        log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(
                        ClassFileLoader.class.getName()).
                        log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(
                        ClassFileLoader.class.getName()).
                        log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(
                        ClassFileLoader.class.getName()).
                        log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(
                        ClassFileLoader.class.getName()).
                        log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(
                        ClassFileLoader.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        } catch (MalformedURLException e) {
            System.out.println("URL error!");
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found!");
        }

        return o;
    }

    /**
     * Compiles java source files. The resulting class file will be saved in the
     * same directory as the source file.
     * @param fileName the file name
     */
    public void compile(String fileName) {
        try {
            MessageBox mBox = mainCanvas.getMessageBox();
            mBox.addUniqueMessage("Compiler Messages:",
                    "compiling " + fileName,
                    null, MessageType.INFO);

            ProcessBuilder pBuilder = new ProcessBuilder("javac", fileName);
            pBuilder.directory(new File(fileName).getParentFile());

//            Process process = Runtime.getRuntime().exec("javac " + fileName);


            Process process = pBuilder.start();

            BufferedReader br =
                    new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
            String line = null;


            MessageList messages = new MessageList();

            messages.setMessageListSize(10);

            boolean notFinished = true;
            while (notFinished) {
                try {
                    while (br.ready()) {
                        line = br.readLine();
                        messages.addMessage(line);

                        mBox.addUniqueMessage("Compiler Messages:",
                                messages.getMessages(),
                                null, MessageType.ERROR);
                    }
                    process.exitValue();
                    notFinished = false;
                } catch (IOException ex) {
                    Logger.getLogger(ClassFileLoader.class.getName()).
                            log(Level.SEVERE, null, ex);
                } catch (IllegalThreadStateException ex) {
                    notFinished = true;
                }
            }
            //

//            while ((line = br.readLine()) != null) {
//                if (mainCanvas == null) {
//                    System.out.println(line);
//                } else {
//                    messages.addMessage(line);
//                    MessageBox mBox = mainCanvas.getMessageBox();
//                    mBox.addUniqueMessage("Compiler Messages:", line,
//                            mBox, MessageType.INFO);
//                }
//            }

            boolean somethingWentWrong = messages.getMessages().length() > 0;

            if (!somethingWentWrong) {
                mBox.addUniqueMessage("Compiler Messages:",
                        "compiling " + fileName + "<br><br>finished!",
                        null, MessageType.INFO);
            }


        } catch (IOException ex) {
            Logger.getLogger(ClassFileLoader.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns the main canvas.
     * @return the the main canvas
     */
    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    /**
     * Defines the main canvas object.
     * @param mainCanvas the canvas to set
     */
    public void setMainCanvas(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;
    }
}
