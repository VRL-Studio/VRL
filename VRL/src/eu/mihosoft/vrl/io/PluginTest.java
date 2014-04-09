/* 
 * PluginTest.java
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

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@Deprecated
public class PluginTest {


    private static ArrayList<Class<?>> loadClasses(File f) throws Exception {
        URL tmpFile = f.toURI().toURL();

        List<String> classNames =
                VJarUtil.getClassNamesFromStream(
                new JarInputStream(tmpFile.openStream()));

        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

        URLClassLoader loader = new URLClassLoader(new URL[]{tmpFile});

        for (String n : classNames) {
            try {
                classes.add(loader.loadClass(n));
//                System.out.println(">> added: " + n);

//                Message m =
//                        mainCanvas.getMessageBox().addUniqueMessage(
//                        "Loading Plugin:",
//                        ">> added class: \"<b><tt>" +
//                        n +
//                        "</b></tt>\"", null,
//                        MessageType.INFO, 5);
//
//                mainCanvas.getMessageBox().messageRead(m);

            } catch (NoClassDefFoundError ex) {
                System.out.println(">> ERROR: cannot add \"" + n +
                        "\"");
                System.out.println(" > cause: " + ex.toString());

//                Message m =
//                        mainCanvas.getMessageBox().addUniqueMessage(
//                        "Loading Plugin:",
//                        ">> cannot add class: \"<b><tt>" +
//                        n +
//                        "</b></tt>\"<br>" +
//                        " > cause: " + ex.toString(), null,
//                        MessageType.INFO, 5);
//
//                mainCanvas.getMessageBox().messageRead(m);
            } catch (Exception ex) {
                System.out.println(">> ERROR: cannot add \"" + n +
                        "\"");
                System.out.println(" > cause: " + ex.toString());

//                Message m =
//                        mainCanvas.getMessageBox().addUniqueMessage(
//                        "Loading Plugin:",
//                        ">> cannot add class: \"<b><tt>" +
//                        n +
//                        "</b></tt>\"<br>" +
//                        " > cause: " + ex.toString(), null,
//                        MessageType.INFO, 5);
//
//                mainCanvas.getMessageBox().messageRead(m);
            }
        }

        return classes;
    }


    public static void main(String[] args){
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        for (int i = 0; i < 100; i++) {
            try {
                classes.addAll(loadClasses(new File("/home/miho/VRL/plugins/VRL-Extensions.jar")));
            } catch (Exception ex) {
                Logger.getLogger(PluginTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.gc();
        }
    }

}
