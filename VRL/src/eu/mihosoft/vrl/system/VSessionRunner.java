/* 
 * VSessionRunner.java
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
package eu.mihosoft.vrl.system;

import eu.mihosoft.vrl.io.VJarUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VSessionRunner {

    /**
     * Runs the main method of the specified object or fails with error if the
     * object does not provide such a method.
     *
     * @param o object to run
     * @param args command line arguments
     * @deprecated since 09.04.2014. This method remains just for compatibility
     * reasons and should not be used anymore.
     */
    @Deprecated
    public void run(Object o, String[] args) {
        run(args);
    }

    /**
     * Runs the main method of the project's main method if the project is on
     * the system path. Fails with error otherwise.
     *
     * @param args command line arguments
     */
    public void run(String[] args) {

        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("=> VRL-" + eu.mihosoft.vrl.system.Constants.VERSION);
        System.out.println("=> Groovy-" + groovy.lang.GroovySystem.getVersion());
        System.out.println("--------------------------------------------------------------------------------");

        // load libraries and plugins
        VRL.initAll(args);

        // creates a custom classloader that has the plugin classloaders as parent.
        // it delegates classes to systemclassloader only if not from the project
        // jar since the class versions in the system classloader are not in the
        // plugin classloader hierarchy.
        Object m = null;

        try {

            // find project jar
            File projectJarFile = VJarUtil.getClassLocation(getClass().getClassLoader().
                    loadClass("eu.mihosoft.vrl.user.Main"));

            // find all project classes
            final Collection<String> projectClassNames = VJarUtil.getClassNamesFromJar(projectJarFile);

            // custom classloader which loads project classes explicitly from the project jar
            // and not from the system classloader
            ClassLoader clsLoader = new URLClassLoader(new URL[]{projectJarFile.toURI().toURL()},
                    VRL.getConsoleAppClassLoader()) {

                        Map<String, Class<?>> loadedClasses = new HashMap<String, Class<?>>();

                        @Override
                        protected Class<?> loadClass(String name, boolean resolve)
                        throws ClassNotFoundException {

                            // special case for project classes
                            if (projectClassNames.contains(name)) {

                                // if already loaded return the project class
                                if (loadedClasses.containsKey(name)) {
                                    return loadedClasses.get(name);
                                }

                                // if not already loaded load the project class
                                try {
                                    Class<?> cls = findClass(name);
                                    loadedClasses.put(name, cls);
                                    return cls;
                                } catch (ClassNotFoundException ex) {
                                    //Logger.getLogger(VSessionRunner.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (LinkageError er) {
                                    Logger.getLogger(VSessionRunner.class.getName()).log(Level.SEVERE, null, er);
                                }
                            }

                            // if not a project class then use normal
                            // classloader delegation
                            Class<?> cls = getParent().loadClass(name);
                            return cls;
                        }
                    };

            m = clsLoader.loadClass("eu.mihosoft.vrl.user.Main").newInstance();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(VSessionRunner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(VSessionRunner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(VSessionRunner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(VSessionRunner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VSessionRunner.class.getName()).log(Level.SEVERE, null, ex);
        }

        // classloading was not successful
        if (m == null) {
            System.out.println(
                    VTerminalUtil.red(">> ERROR: cannot invoke Main.run(String[])! Class not found!\n"));
        }

        System.out.println("--------------------------------------------------------------------------------");
        try {
            runMain(m, args);
        } catch (Throwable tr) {
            Logger.getLogger(VSessionRunner.class.getName()).
                    log(Level.SEVERE, null, tr);
        }
        System.out.println("--------------------------------------------------------------------------------");
    }

    /**
     * Filter console app arguments. E.g.: 
     * [arg1,arg2,arg3,--console-app-args,csapparg1,csapparg2,csapparg3] -&gt; [csapparg1,csapparg2,csapparg3]
     * @param args arguments to filter
     * @return console app arguments 
     */
    private String[] filterConsoleAppArgs(String[] args) {
        int consoleAppArgsIndex = -1;
        for (int i = 0; i < args.length; i++) {
            if ("--console-app-args".equals(args[i])) {
                consoleAppArgsIndex = i;
                break;
            }
        }

        if (consoleAppArgsIndex >= 0) {
            String[] result = new String[args.length - (consoleAppArgsIndex+1) ];

            for (int i = 0; i < result.length; i++) {
                result[i] = args[consoleAppArgsIndex+i+1];
            }
            
            return result;
        } else {
            return args;
        }
    }

    private void runMain(Object m, String[] args) {

        // search for run method
        Method run = null;

        String[] consoleAppArgs = filterConsoleAppArgs(args);

        // 1.) try with string array
        try {
            run = m.getClass().getMethod("run", String[].class);
            System.out.println(
                    VTerminalUtil.green(">> invoking Main.run(String[])\n"));
            run.invoke(m, (Object) consoleAppArgs);
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
