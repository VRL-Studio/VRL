/* 
 * VSessionRunner.java
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
package eu.mihosoft.vrl.system;

import eu.mihosoft.vrl.io.ClassPathUpdater;
import eu.mihosoft.vrl.io.IOUtil;
import eu.mihosoft.vrl.io.VJarUtil;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VSessionRunner {

    public void run(Object m, String[] args) {

        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("=> VRL-" + eu.mihosoft.vrl.system.Constants.VERSION);
        System.out.println("=> Groovy-" + groovy.lang.GroovySystem.getVersion());
        System.out.println("--------------------------------------------------------------------------------");

        // load libraries and plugins
        VRL.initAll(args);

        
        ClassPathUpdater.addAllJarsInDirectory(
                VRL.getPropertyFolderManager().getPluginFolder());

        System.out.println("--------------------------------------------------------------------------------");
        try {
            runMain(m, args);
        } catch (Throwable tr) {
            Logger.getLogger(VSessionRunner.class.getName()).
                    log(Level.SEVERE, null, tr);
        }
        System.out.println("--------------------------------------------------------------------------------");
    }

    private void runMain(Object m, String[] args) {

        // search for run method

        Method run = null;

        // 1.) try with string array
        try {
            run = m.getClass().getMethod("run", String[].class);
            System.out.println(
                    VTerminalUtil.green(">> invoking Main.run(String[])\n"));
            run.invoke(m, (Object) args);
        } catch (NoSuchMethodException ex) {
            //
        } catch (SecurityException ex) {
            //
        } catch (IllegalAccessException ex) {
            Logger.getLogger(VSessionRunner.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(VSessionRunner.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(VSessionRunner.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        // 2.) try with parameter-less 
        if (run == null) {
            try {
                run = m.getClass().getMethod("run");
                System.out.println(
                        VTerminalUtil.green(">> invoking Main.run()\n"));
                run.invoke(m);
            } catch (NoSuchMethodException ex) {
                //
            } catch (SecurityException ex) {
                //
            } catch (IllegalAccessException ex) {
                Logger.getLogger(VSessionRunner.class.getName()).
                        log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(VSessionRunner.class.getName()).
                        log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(VSessionRunner.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

        // 3.) give up if still nothing found
        if (run == null) {
            System.err.println(VTerminalUtil.red(
                    ">> aborting: cannot find run-Method "
                    + "in class eu.mihosoft.vrl.user.Main\n"
                    + " --> void run() or void run(String[] args)"
                    + "are supported."));
            return;
        }
    }
}
