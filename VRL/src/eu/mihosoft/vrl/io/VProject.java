/* 
 * VProject.java
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
package eu.mihosoft.vrl.io;

import eu.mihosoft.vrl.io.vrlx.AbstractCode;
import eu.mihosoft.vrl.io.vrlx.AbstractComponentClassInfo;
import eu.mihosoft.vrl.io.vrlx.FileFormat;
import eu.mihosoft.vrl.io.vrlx.FileVersionInfo;
import eu.mihosoft.vrl.io.vrlx.VRLXReflection;
import eu.mihosoft.vrl.io.vrlx.VRLXSessionController;
import eu.mihosoft.vrl.lang.CodeBuilder;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.lang.visual.ClassInfoObject;
import eu.mihosoft.vrl.lang.visual.SessionClassUtils;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.system.*;
import eu.mihosoft.vrl.visual.Disposable;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * VRL Project object
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VProject {

    private VersionedFile projectFile;
    private FileFormat format = VRLXReflection.getVRLXFormat();
    private VRLXSessionController sessionController =
            new VRLXSessionController(format);
    private ProjectFileInfo projectInfo;
    HashMap<String, VisualCanvas> openedEntriesByName =
            new HashMap<String, VisualCanvas>();
    HashMap<VisualCanvas, String> openedEntriesByCanvas =
            new HashMap<VisualCanvas, String>();
    /**
     * the name of the file-info file
     */
    public static final String PROJECT_INFO_NAME = "vproject-info.xml";
    public static final String PROJECT_INFO_DIR = "META-INF/VRL";
    public static final String PROJECT_PAYLOAD_VERSIONING = "META-INF/VRL/payload/versioning/";
    public static final String PROJECT_PAYLOAD_NO_VERSIONING = "META-INF/VRL/payload/no-versioning/";

    private VProject(VersionedFile projectFile) {
        this.projectFile = projectFile;
        updateGitIgnore();
    }

    /**
     * @return the projectFile
     */
    public VersionedFile getProjectFile() {
        return projectFile;
    }

    /**
     * Returns the content location of this project.
     *
     * @return the content location of this project
     */
    public File getContentLocation() {
        return projectFile.getContent();
    }

    public URL getContentLocationAsURL() {
        try {
            return new URL("file://"
                    + projectFile.getContent().getAbsolutePath() + "/");
        } catch (MalformedURLException ex) {
            Logger.getLogger(VProject.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Returns the name of the top-level class defined in the specified class
     * file (uses / as package element separator).
     *
     * @param f file to analyze
     * @return he name of the top-level class defined in the specified class
     * file
     */
    public String getClassNameFromFile(File f) {
        // remove .class ending
        String className = f.getAbsolutePath();

        if (className.endsWith(".class")) {
            className = className.substring(
                    0, f.getAbsolutePath().lastIndexOf(".class"));
        } else if (className.endsWith(".groovy")) {
            className = className.substring(
                    0, f.getAbsolutePath().lastIndexOf(".groovy"));
        } else {
            throw new IllegalArgumentException(
                    "According to the file ending the specified file is no"
                    + "suppoerted class/source file! File: " + f);
        }

        // remove absolute path + the / or \ after the path and ensure /
        // is used for classpath on windows
        className = className.substring(
                getContentLocation().getAbsolutePath().length() + 1,
                className.length()).replace('\\', '/');

        return className;
    }

    public static VProject create(File f) throws IOException {

        VProject prj = new VProject(
                new VersionedFile(f).create().open());

        prj.createFileInfo();

        prj.projectInfo = prj.loadFileInfo();

        writeManifest(prj.getContentLocation());

        prj.createMainClass();
        prj.getProjectFile().commit("project created.");

        return prj;
    }

    /**
     * Returns the versioned payload folder of this project. It can be used to
     * include plugins and other resources to the project.
     *
     * @return the versioned payload folder of this project
     */
    public File getVersionedPayloadFolder() {
        return new File(getContentLocation(), PROJECT_PAYLOAD_VERSIONING);
    }

    /**
     * Returns the non-versioned payload folder of this project. It can be used
     * to include plugins and other resources to the project.
     *
     * @return the non-versioned payload folder of this project
     */
    public File getNonVersionedPayloadFolder() {
        return new File(getContentLocation(), PROJECT_PAYLOAD_NO_VERSIONING);
    }

    private void createMainClass() throws IOException {
        CodeBuilder builder = new CodeBuilder();

        builder.addLine("package eu.mihosoft.vrl.user;").
                addLine("").
                addLine("import eu.mihosoft.vrl.system.*;").
                addLine("import eu.mihosoft.vrl.annotation.*;").
                addLine("").
                addLine("@ComponentInfo(ignore=true)").
                addLine("class VSessionMainClass {").
                incIndentation().
                addLine("public static void main(String[] args) {").
                incIndentation().
                addLine("VSessionRunner r = new VSessionRunner();").
                addLine("r.run(args);").
                decIndentation().
                addLine("}").
                decIndentation().
                addLine("}");

        String code = builder.getCode();

        TextSaver saver = new TextSaver();

        new File(getContentLocation().getAbsolutePath()
                + "/eu/mihosoft/vrl/user/").mkdirs();

        saver.saveFile(code,
                new File(getContentLocation().getAbsolutePath()
                + "/eu/mihosoft/vrl/user/VSessionMainClass.groovy"), "");
    }

    private void createFileInfo() throws IOException {
        saveFileInfo(new ProjectFileInfo(
                new FileVersionInfo("0.1", "VRL Project File")));
    }

    /**
     * Creates a project info file in the specified directory.
     *
     * @param contentDir target location
     * @throws IOException
     */
    private void saveFileInfo(ProjectFileInfo info)
            throws IOException {

        if (info == null) {
            info = new ProjectFileInfo(
                    new FileVersionInfo("0.1", "VRL Project File"));
        }

        File contentDir = getContentLocation();

        File projectInfoDir = new File(contentDir.getPath()
                + "/" + PROJECT_INFO_DIR);

        if (!projectInfoDir.exists()) {
            projectInfoDir.mkdirs();
        }

        File versionInfo =
                new File(contentDir.getPath()
                + "/" + PROJECT_INFO_DIR + "/" + PROJECT_INFO_NAME);


        XMLEncoder e = null;

        try {
            e = new XMLEncoder(
                    new BufferedOutputStream(
                    new FileOutputStream(versionInfo)));

            e.writeObject(info);
        } catch (IOException ex) {
            throw new IOException(ex);
        } finally {
            if (e != null) {
                e.close();
            }
        }
    }

    /**
     * Updates the plugin dependency info. This slould be done whenever this
     * project is being saved. But saving the project can only be performed from
     * the controller of this project.
     */
    private void setPluginDependencyInformation(Collection<PluginDependency> deps) {

        Collection<AbstractPluginDependency> usedPlugins =
                new ArrayList<AbstractPluginDependency>();
        for (PluginDependency pDep : deps) {
            usedPlugins.add(new AbstractPluginDependency(pDep));
        }

        // store info about used plugins
//        projectInfo.setPluginDependencies(VRL.getAvailablePlugins());
        projectInfo.setPluginDependencies(usedPlugins);
    }

    /**
     * Returns the version info from the specified location.
     *
     * @return the version info from the specified location or <code>null</code>
     * if no version info exists at the specified location
     * @throws IOException
     * @throws IllegalStateException if this file is currently not open
     */
    private ProjectFileInfo loadFileInfo()
            throws IOException {

        File contentDir = getContentLocation();

        // file has to be opened
        if (!contentDir.isDirectory()) {
            throw new IllegalStateException("File not opened!");
        }

        File projectInfoFile =
                new File(contentDir.getPath()
                + "/" + PROJECT_INFO_DIR + "/" + PROJECT_INFO_NAME);

        // compatibility to old projects before 10.12.2012
        // can be removed if compatibility is not necessary
        if (!projectInfoFile.exists()) {
            projectInfoFile =
                    new File(contentDir.getPath() + "/" + PROJECT_INFO_NAME);

        }


        // stop here if the version info file does not exists
        if (!projectInfoFile.exists()) {
            System.out.println("Version Info does not exist!");
            return null;
        }

        // decode the version info
        XMLDecoder d = null;

        try {
            d = new XMLDecoder(
                    new BufferedInputStream(
                    new FileInputStream(projectInfoFile)));

            Object result = d.readObject();

            if (!(result instanceof ProjectFileInfo)) {
                throw new IOException("The file \""
                        + projectInfoFile.getPath()
                        + "\" does not contain a valid project file info");
            }

            return (ProjectFileInfo) result;

        } catch (Exception ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            }
        } finally {
            if (d != null) {
                d.close();
            }
        }

        // no version info found
        return null;
    }

    public static VProject open(File f) throws IOException {

        System.out.println(">> opening VProject: " + f.getAbsolutePath());

        // please check that excludes are in sync with updateGitIgnore()!
        VProject prj = new VProject(
                new VersionedFile(f)
                .setExcludeEndingsFromCleanup(".class")
                .excludePathsFromCleanup(PROJECT_PAYLOAD_NO_VERSIONING).
                cleanup().open());

        prj.projectInfo = prj.loadFileInfo();

        return prj;
    }

    public VProject flush() throws IOException {
        saveFileInfo(projectInfo);
        projectFile.flush();
        return this;
    }

    /**
     * Switches this versioned file to a new archive location. This method
     * implies copying of the tmp folder and one additional flushing to the new
     * archive.
     *
     * @param dest new archive destination
     * @throws IOException if switchin is not possible
     */
    public VProject switchToNewArchive(File dest) throws IOException {
        saveFileInfo(projectInfo);
        projectFile.switchToNewArchive(dest);
        return this;
    }

    /**
     * Saves project info (including plugin dependencies). This has to be done
     * before commit. Otherwise changes will be lost after next checkout.
     */
    public void saveProjectInfo() {

        //updatePluginDependencyInformation();

        try {
            saveFileInfo(projectInfo);
        } catch (IOException ex) {
            Logger.getLogger(VProject.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public boolean isOpened() {
        return projectFile.isOpened();
    }

    public VProject close() throws IOException {
        if (!isOpened()) {
            return this;
        }
        saveFileInfo(projectInfo);
        projectFile.close();

        return this;
    }

    public void openSessionEntry(VisualCanvas canvas, String name)
            throws IOException {
        if (!projectFile.isOpened()) {
            projectFile.open(false);
        }

        sessionController.loadSession(canvas,
                getSessionFileByEntryName(name));
    }

//    public void loadMainSession(VisualCanvas canvas) throws IOException {
//        loadSessionEntry(canvas, "main.vrlx");
//    }
//
//    public void saveMainSession(VisualCanvas canvas) throws IOException {
//        saveSessionEntry(canvas, "main.vrlx");
//    }
    public void saveSessionEntry(
            VisualCanvas canvas, String name) throws IOException {

        saveSessionEntry(canvas, VLangUtils.dotToSlash(name), true, true, true);
    }

    public void saveSessionEntry(
            VisualCanvas canvas, String name, boolean commitChanges, boolean flush,
            boolean showSaveConfirmMsg)
            throws IOException {

        if (name.endsWith("/")) {
            throw new IllegalArgumentException(
                    "entry name specifies a directory!"
                    + " Only files may be specified.");
        }

        name = VLangUtils.dotToSlash(name);

        // add default package
        if (VLangUtils.isShortName(name)) {
            name = "eu/mihosoft/vrl/user/" + name;
        }

        File sessionFile = getSessionFileByEntryName(name);

        File path = sessionFile.getParentFile();

        if (path != null) {
            path.mkdirs();
        }

        // code generation BEGIN
        AbstractCode code = null;

        String packageString = "package "
                + VLangUtils.slashToDot(
                VLangUtils.packageNameFromFullClassName(name)) + ";\n\n";

        AbstractComponentClassInfo info = null;

        try {
            info = canvas.getSession().getInfo();

            if (info != null) {
                info.setComponentName(
                        VLangUtils.shortNameFromFullClassName(name));
                code = SessionClassUtils.createSessionClassCode(canvas,
                        info.toClassInfo());

            } else {
                // this shouldn't happen (but it may if this is a session created with vrl <= 0.4.0)
                System.err.println(">> SessionInfo is null!");
                System.err.println(" --> generating empty info.");
                info = new AbstractComponentClassInfo();
                info.setComponentName(VLangUtils.shortNameFromFullClassName(name));
                info.setClassPath(VLangUtils.packageNameFromFullClassName(name));
                info.setComponentDescription("recovered component from corrupted project");
                info.setMethodName("run");
            }
        } catch (Exception ex) {
            System.err.println(
                    "ERROR while generating code for \"" + name + "\".");
            ex.printStackTrace(System.err);
        }

        if (code == null) {
            // create stub (component is accessible via its class)
            code = new AbstractCode();

            String componentInfo = "name=\""
                    + VLangUtils.shortNameFromFullClassName(name) + "\", "
                    + "category=\"Custom\"" + ", description=\""
                    + info.getComponentDescription() + "\"";

            String objectInfo = "name=\""
                    + VLangUtils.shortNameFromFullClassName(name) + "\"";

            if (info != null) {
                ClassInfoObject infoObj = info.toClassInfo();
                componentInfo = infoObj.getComponentInfo();
                objectInfo = infoObj.getObjectInfo();
            }

            String codeString =
                    "// recovery code (created by VProject, code was null)\n"
                    + "import eu.mihosoft.vrl.system.*;\n"
                    + "import eu.mihosoft.vrl.annotation.*;\n\n"
                    + "@ComponentInfo("
                    + componentInfo + ")\n"
                    + "@ObjectInfo(" + objectInfo + ")\n"
                    + "public class "
                    + VLangUtils.shortNameFromFullClassName(name)
                    + " implements java.io.Serializable {\n"
                    + "    private static final long serialVersionUID = 1L;\n"
                    + "    public void run(){\n"
                    + "        println \">> run(): auto-generated stub: "
                    + "implementation missing!\"\n"
                    + "    }\n"
                    + "}";


            for (AbstractCode c : canvas.getCodes()) {
                codeString += "\n\n" + c.getCode();
            }

            code.setCode(codeString);

        }

//        Iterable<String> imports = new GroovyCompiler().getImports();
//        for (String imp : imports) {
//            importString += imp;
//        }
//
        code.setCode(packageString + code.getCode());

        canvas.getSession().setCode(code);


        File codeFile = new File(
                getContentLocation().getAbsoluteFile()
                + "/" + name + ".groovy");

        TextSaver saver = new TextSaver();
        saver.saveFile(code.getCode(), codeFile, ".groovy");


        // code generation END

        sessionController.saveSession(canvas, sessionFile, showSaveConfirmMsg);

//        if (projectFile.hasUncommittedChanges() && commitChanges) {
//            projectFile.commit("file saved");
//        }

        if (flush) {
            flush();
        }
    }

    public AbstractCode createComponentClassStubCode(ClassInfoObject info) {

        String name = info.getClassName();
        name = VLangUtils.shortNameFromFullClassName(name);

        String description = info.getDescription();
        description = VLangUtils.addEscapesToCode(description);

        AbstractCode code = new AbstractCode();

        String codeString = "@ComponentInfo("
                + "name=\""
                + VLangUtils.shortNameFromFullClassName(name) + "\", "
                + "category=\"" + "Custom" + "\", description=\"" + description + "\")\n"
                + "public class "
                + VLangUtils.shortNameFromFullClassName(name)
                + " implements java.io.Serializable {"
                + " private static final long serialVersionUID = 1L;"
                + " public void run(){"
                + "println \">> run(): auto-generated stub: "
                + "implementation missing!\"}"
                + "}";

        code.setCode(codeString);

        return code;
    }

    ArrayList<String> getSessionsDependingOn(String name) {
        ArrayList<String> result = new ArrayList<String>();

        String fullName = getFullEntryName(name);

        fullName = VLangUtils.slashToDot(fullName);


        ArrayList<File> codeFiles =
                IOUtil.listFiles(getContentLocation(),
                new String[]{".groovy"});

        TextLoader loader = new TextLoader();

        for (File f : codeFiles) {
            String code = "";
            try {
                code = (String) loader.loadFile(f);
            } catch (IOException ex) {
                Logger.getLogger(VProject.class.getName()).
                        log(Level.SEVERE, null, ex);
            }

            System.out.println(">> File : " + f.getAbsolutePath()
                    + ", searching for: " + fullName);

            if (VLangUtils.isClassUsedInCode(code, fullName,
                    getShortClassNamesInPackage(
                    VLangUtils.packageNameFromCode(code)))) {

                String classNameOfClassDependingOn = getClassNameFromFile(f);
                classNameOfClassDependingOn =
                        VLangUtils.slashToDot(classNameOfClassDependingOn);

                if (!fullName.equals(classNameOfClassDependingOn)) {
                    result.add(getEntryNameWithoutDefaultPackage(
                            classNameOfClassDependingOn));
                    System.out.println(" --> class is used.");
                }
            }
        }

        return result;
    }

    public List<String> getShortClassNamesInPackage(String packageName) {
        ArrayList<File> classFiles = IOUtil.listFiles(
                getContentLocation(), new String[]{".class"});

        List<String> result = new ArrayList<String>();

        packageName = VLangUtils.dotToSlash(packageName);

        System.out.println(">> Searching for classes in: " + packageName);

        for (File file : classFiles) {
            String className = getClassNameFromFile(file);

            String pkgName = VLangUtils.packageNameFromFullClassName(
                    className);

            if (packageName.equals(pkgName)) {
                String shortName =
                        VLangUtils.shortNameFromFullClassName(
                        className);
                result.add(shortName);
                System.out.println(" --> found: " + shortName);
            }
        }

        return result;
    }

    public String getFullEntryName(String name) {
        // add default package
        if (VLangUtils.isShortName(name)) {
            name = "eu/mihosoft/vrl/user/" + name;
        }

        return VLangUtils.dotToSlash(name);
    }

    public String getEntryNameWithoutDefaultPackage(String name) {

        name = VLangUtils.dotToSlash(name);

        if (VLangUtils.packageNameFromFullClassName(name).
                equals("eu/mihosoft/vrl/user")) {
            return name.replaceFirst("eu/mihosoft/vrl/user/", "");
        }

        return name;
    }

    public File getClassFileByEntryName(String name) {
        VParamUtil.throwIfNull(name);

        String fileName = VLangUtils.dotToSlash(name);

        // add default package
        if (VLangUtils.isShortName(name)) {
            fileName = "eu/mihosoft/vrl/user/" + fileName;
        }

        if (!fileName.toLowerCase().endsWith(".class")) {
            fileName = fileName + ".class";
        }

        return new File(getContentLocation() + "/" + fileName);
    }

    public File getSessionFileByEntryName(String name) {
        VParamUtil.throwIfNull(name);

        String fileName = VLangUtils.dotToSlash(name);

        // add default package
        if (VLangUtils.isShortName(name)) {
            fileName = "eu/mihosoft/vrl/user/" + fileName;
        }

        if (!fileName.toLowerCase().endsWith(".vrlx")) {
            fileName = fileName + ".vrlx";
        }

        return new File(getContentLocation() + "/" + fileName);
    }

    public File getSourceFileByEntryName(String name) {
        VParamUtil.throwIfNull(name);

        String fileName = VLangUtils.dotToSlash(name);

        // add default package
        if (VLangUtils.isShortName(name)) {
            fileName = "eu/mihosoft/vrl/user/" + fileName;
        }


        if (!fileName.toLowerCase().endsWith(".groovy")) {
            fileName = fileName + ".groovy";
        }

        return new File(getContentLocation() + "/" + fileName);
    }

    /**
     *
     * @return the project file (archive)
     */
    public File getFile() {
        return projectFile.getFile();
    }

    /**
     * @return the projectInfo
     */
    public ProjectFileInfo getProjectInfo() {
        return projectInfo;
    }

    public boolean containsEntry(String name) {

        name = VLangUtils.dotToSlash(name);

        return getSessionFileByEntryName(name).isFile()
                || getSourceFileByEntryName(name).isFile();
    }

    /**
     * Writes a default manifest file to the specified location (directory).
     *
     * @param location location (directory)
     * @throws IOException
     */
    private static void writeManifest(File location) throws IOException {

        VParamUtil.throwIfNotValid(
                VParamUtil.VALIDATOR_EXISTING_FOLDER, location);

        // Construct a string version of a manifest
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("Manifest-Version: 1.0\n");
        sbuf.append("Created-By: VRL-" + Constants.VERSION + "\n");
        sbuf.append("Main-Class: eu.mihosoft.vrl.user.VSessionMainClass\n");
        sbuf.append("Class-Path: lib/VRL.jar\n");

        // Convert the string to an input stream
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(sbuf.toString().
                    getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(VJarUtil.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        File meta_inf = new File(location.getAbsolutePath() + "/META-INF");
        meta_inf.mkdir();

        Manifest manifest = new Manifest(is);

        manifest.write(new FileOutputStream(
                new File(meta_inf.getAbsolutePath() + "/MANIFEST.MF")));
    }

    private VProject updateGitIgnore() {
        
        // please check that excludes are in sync with open()!
        
        getProjectFile().setExcludeEndingsFromCleanup(
                ".class").excludePathsFromCleanup(PROJECT_PAYLOAD_NO_VERSIONING);
        
        TextSaver saver = new TextSaver();
        try {
            // note: keep excludes in sync with gitignore!
            saver.saveFile(
                    "*.class\n"
                    + PROJECT_PAYLOAD_NO_VERSIONING,
                    new File(getContentLocation().getAbsolutePath()
                    + "/.gitignore"), "");
            
            
        } catch (IOException ex) {
            Logger.getLogger(VProject.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return this;
    }
}
