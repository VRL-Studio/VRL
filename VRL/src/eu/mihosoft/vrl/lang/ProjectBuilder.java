/* 
 * ProjectBuilder.java
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

package eu.mihosoft.vrl.lang;

import eu.mihosoft.vrl.io.IOUtil;
import eu.mihosoft.vrl.io.VProjectController;
import eu.mihosoft.vrl.system.Constants;
import eu.mihosoft.vrl.system.VRL;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ProjectBuilder {

    public static VBuildResult build(VProjectController vpc, boolean overwriteBuildScript) {
        Project p = prepare(vpc, overwriteBuildScript);
        ProjectBuildListener buildListener = new ProjectBuildListener(vpc);
        p.addBuildListener(buildListener);

        BuildException exception = null;
        try {
            p.fireBuildStarted();
            p.init();
            ProjectHelper helper = ProjectHelper.getProjectHelper();
            p.addReference("ant.projectHelper", helper);
            helper.parse(p, getBuildFile(vpc));

            p.executeTarget(p.getDefaultTarget());
            p.fireBuildFinished(null);
         
        } catch (BuildException e) {
            p.fireBuildFinished(e);
            exception = e;
        }

        return buildListener.getBuildResult();
    }

    public static VBuildResult build(VProjectController vpc) {
        return build(vpc, true);
    }

    private static File getBuildFile(VProjectController vpc) {
        return new File(
                vpc.getProject().getContentLocation().getAbsolutePath() + "/build.xml");
    }

    private static Project prepare(VProjectController vpc) {
        return prepare(vpc, true);
    }

    private static Project prepare(VProjectController vpc, boolean overwriteBuildScript) {
        if (!vpc.getProject().isOpened()) {
            throw new IllegalStateException(
                    "Project must be opened to be compiled!");
        }

        File buildFile = getBuildFile(vpc);

        if (overwriteBuildScript) {
            try {
                InputStream in =
                        ProjectBuilder.class.getClassLoader().
                        getResourceAsStream(
                        "eu/mihosoft/vrl/lang/build-template.xml");

                IOUtil.saveStreamToFile(in, buildFile);

            } catch (IOException ex) {
                Logger.getLogger(ProjectBuilder.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

//        File buildFile = new File("/home/miho/tmp/anttest/build.xml");
        Project p = new Project();
        p.setUserProperty("file.encoding", "UTF-8");
        p.setUserProperty("ant.project.name", vpc.getProject().getFile().getName());
        p.setUserProperty("ant.file", buildFile.getAbsolutePath());

        p.setProperty("vrl.version", Constants.VERSION);
        //

        String projectClassPath = "";

        // add plugin jars
        for (File f : IOUtil.listFiles(new File(Constants.PLUGIN_DIR),
                new String[]{".jar"})) {
            projectClassPath += f.getAbsolutePath() + ":";
        }

        // add custom-lib jars
        for (File f : IOUtil.listFiles(new File(Constants.CUSTOM_LIB_DIR),
                new String[]{".jar"})) {
            projectClassPath += f.getAbsolutePath() + ":";
        }

        // add lib jars
        for (File f : IOUtil.listFiles(new File(Constants.LIB_DIR),
                new String[]{".jar"})) {
            projectClassPath += f.getAbsolutePath() + ":";
        }

        // add project lib jars
        for (File f : IOUtil.listFiles(
                vpc.getLibraryController().getLibFolderLocation(),
                new String[]{".jar"})) {
            projectClassPath += f.getAbsolutePath() + ":";
        }

        // defines project classpath (for groovy compiler)
        p.setProperty("vrl.project-classpath", projectClassPath);

        DefaultLogger consoleLogger = new DefaultLogger();
        consoleLogger.setErrorPrintStream(System.err);
        consoleLogger.setOutputPrintStream(System.out);
        consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
        p.addBuildListener(consoleLogger);

        return p;
    }

    /**
     * Cleans the specified project.
     *
     * To prevent cleaning of the whole project one may considder
     * {@link VProjectController#removeInnerClassFilesOf(java.lang.String) }
     * instead of setting the
     * <code>clean</code> property to
     * <code>true</code>.
     *
     * @param vpc project controller
     * @throws BuildException
     *
     */
    public static void clean(VProjectController vpc) throws BuildException {
        Project p = prepare(vpc);
        try {
            p.fireBuildStarted();
            p.init();
            ProjectHelper helper = ProjectHelper.getProjectHelper();
            p.addReference("ant.projectHelper", helper);
            helper.parse(p, getBuildFile(vpc));

            p.executeTarget("clean");
            p.fireBuildFinished(null);
        } catch (BuildException e) {
            p.fireBuildFinished(e);
            throw e;
        }
    }
}

class ProjectBuildListener implements BuildListener {

    private boolean successful = false;
    private boolean buildRunning = false;
    private VBuildResultImpl buildResult;
    private VProjectController vpc;

    public ProjectBuildListener(VProjectController vpc) {
        this.vpc = vpc;
        buildResult = new VBuildResultImpl();
    }

    @Override
    public void buildStarted(BuildEvent be) {
        buildRunning = true;
    }

    @Override
    public void buildFinished(BuildEvent be) {
        buildRunning = false;
        successful = be.getException() == null;
        buildResult.setSuccessful(successful);
    }

    @Override
    public void targetStarted(BuildEvent be) {
//        System.out.println("target-started: " + be.getTarget().getName());
    }

    @Override
    public void targetFinished(BuildEvent be) {
//        System.out.println("target-finished: " + be.getTarget().getName());
//        if (be.getException() != null) {
//            System.out.println(" --> ex: " + be.getException());
//        }
    }

    @Override
    public void taskStarted(BuildEvent be) {
//        System.out.println("task-started: " + be.getTask().getTaskName());
    }

    @Override
    public void taskFinished(BuildEvent be) {
//        System.out.println("task-finished: " + be.getTask().getTaskName());
        if (be.getException() != null) {
//            System.out.println(" --> ex: " + be.getException());
            if (be.getException().getCause() != null) {
                
                buildResult.setException(be.getException().getCause());

                String projectLocationPath = vpc.getProject().
                        getContentLocation().getAbsolutePath();

                String errorMsg = be.getException().getCause().getMessage();

                String[] lines = errorMsg.split("\n");

                for (String l : lines) {
                    if (l.startsWith(projectLocationPath)) {
                        String entryName = l.replace(projectLocationPath, "");
                        entryName = entryName.split(":")[0];
                        if (entryName.startsWith("/")) {
                            entryName = entryName.substring(1);
                        }
                        
                        buildResult.addBrokenCodeEntry(
                                entryName.substring(0,
                                entryName.length() - ".groovy".length()));
                    }
                }
            }
        }
    }

    @Override
    public void messageLogged(BuildEvent be) {
        //System.out.println("msg: " +be.getMessage());
    }

    /**
     * @return the successful
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * @return the buildRunning
     */
    public boolean isBuildRunning() {
        return buildRunning;
    }

    /**
     * @return the buildResult
     */
    public VBuildResultImpl getBuildResult() {
        return buildResult;
    }
}
