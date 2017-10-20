/* 
 * CreatePluginInfoTask.java
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

package eu.mihosoft.vrl.system;

import eu.mihosoft.vrl.io.IOUtil;
import eu.mihosoft.vrl.lang.*;
import java.io.File;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.Task;

/**
 * EXPERIMENTAL!
 * 
 * The purpose of this class is to find a compile-time alternative to
 * {@link PluginCache}
 * 
 * Can be used via the following ant task:
 * <pre>
 * <target name="-post-compile">
        
        <echo message="CLASSPATH: ${javac.classpath}"/>
        
        <property name="base.dir" location="." />
        
        <taskdef name="create-plugin-info"
                 classname="eu.mihosoft.vrl.system.CreatePluginInfoTask">
            <classpath>
                <path path="${javac.classpath}"/>
            </classpath>
        </taskdef>
        
        <create-plugin-info>
        </create-plugin-info>

        
    </target>
 * </pre>
 * 
 * This approach has several disadvantages. Don't use it for production.
 * This class is still in development.
 * 
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class CreatePluginInfoTask extends Task {

    @Override
    public void execute() {

        // base dir (dir were the build.xml file is located)
        String baseDirName = getProject().getProperty("base.dir");

        // build dir, where the compiled classes are located
        String classesDirName = getProject().getProperty("build.classes.dir");

        // classpath of the plugin dependencies, using : as separator
        String depClassPath = getProject().getProperty("javac.classpath");

        // create dependencies classloader
        String[] deps = depClassPath.split(":");

        URL[] depURLs = new URL[deps.length];

        for (int i = 0; i < deps.length; i++) {
            try {
                depURLs[i] = new File(deps[i]).toURI().toURL();
            } catch (MalformedURLException ex) {
                Logger.getLogger(CreatePluginInfoTask.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

        URLClassLoader depsClassLoader = new URLClassLoader(depURLs);

        // load pluginconfigurator interface via deps classloader to ensure
        // compatibility with loaded plugin classes
        // (allows isAssignable() checks)
        Class<?> pluginConfiguratorInterface = null;

        try {
            pluginConfiguratorInterface =
                    depsClassLoader.loadClass(
                    PluginConfigurator.class.getName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CreatePluginInfoTask.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        // prepare classloader that can load plugin classes

        File classesDir = new File(baseDirName + "/" + classesDirName);
        List<Class<?>> pluginClasses = new ArrayList<Class<?>>();

        List<File> classFiles = IOUtil.listFiles(
                classesDir, new String[]{".class"});

        URL[] urls = new URL[classFiles.size()];

        for (int i = 0; i < classFiles.size(); i++) {
            try {
                urls[i] = classFiles.get(i).toURI().toURL();
            } catch (MalformedURLException ex) {
                Logger.getLogger(CreatePluginInfoTask.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

        URLClassLoader loader = null;

        try {
            loader = new URLClassLoader(
                    new URL[]{classesDir.toURI().toURL()}, depsClassLoader);
        } catch (MalformedURLException ex) {
            Logger.getLogger(CreatePluginInfoTask.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        // search for plugin classes, i.e., classes that are instantiable
        // and implement the PluginConfigurator interface

        for (File f : classFiles) {
            try {

                String clsName = VLangUtils.getClassNameFromFile(
                        classesDir, f);

                clsName = VLangUtils.slashToDot(clsName);

                Class<?> cls = loader.loadClass(clsName);
                try {
                    if (pluginConfiguratorInterface.isAssignableFrom(cls)
                            && !cls.isInterface()
                            && !Modifier.isAbstract(cls.getModifiers())
                            && cls.newInstance() != null) {

                        pluginClasses.add(cls);

                        System.out.println("CLS: " + cls.getName());
                    }
                } catch (InstantiationException ex) {
                    Logger.getLogger(CreatePluginInfoTask.class.getName()).
                            log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(CreatePluginInfoTask.class.getName()).
                            log(Level.SEVERE, null, ex);
                }

            } catch (ClassNotFoundException ex) {

                System.out.println("ex: " + ex);

                Logger.getLogger(CreatePluginInfoTask.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }



    }
}
