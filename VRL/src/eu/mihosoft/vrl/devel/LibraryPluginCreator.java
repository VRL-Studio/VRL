/* 
 * LibraryPluginCreator.java
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

package eu.mihosoft.vrl.devel;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.io.IOUtil;
import eu.mihosoft.vrl.io.TextSaver;
import eu.mihosoft.vrl.lang.Patterns;
import eu.mihosoft.vrl.lang.groovy.GroovyCompiler;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.system.Constants;
import eu.mihosoft.vrl.system.PluginConfiguratorGenerator;
import eu.mihosoft.vrl.system.PluginIdentifier;
import eu.mihosoft.vrl.system.PluginInfo;
import eu.mihosoft.vrl.types.CanvasRequest;
import eu.mihosoft.vrl.visual.Message;
import eu.mihosoft.vrl.visual.MessageType;
import eu.mihosoft.vrl.visual.VDialog;
import groovy.lang.GroovyClassLoader;
import java.io.*;
import java.util.ArrayList;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
@ComponentInfo(name = "Library Plugin Creator", category = "VRL/Development",
        description="Creates a VRL Plugin for external Libraries")
@ObjectInfo(controlFlowIn = true, controlFlowOut = true)
public class LibraryPluginCreator implements Serializable {

    private static final long serialVersionUID = 1L;
    private String pluginName;
    private String pluginVersion;
    private String description;
    private File destination;
    private File[] libraries;

    @MethodInfo(name = "",
    interactive = true, hide = false)
    public void create(
            CanvasRequest cReq,
            @ParamInfo(name = "Name:",
            style = "plugin-name") String name,
            @ParamInfo(name = "Version:",
            style = "plugin-version") String version,
            @ParamInfo(name = "Description:", style = "editor") String description,
            @ParamInfo(name = "Destination", style = "save-folder-dialog") File dest,
            @ParamInfo(name = " -- Libraries: --", style = "load-dialog",
            options = "minArraySize=1;endings=[\".jar\"];description=\"Java Library - *.jar\"") File[][] libraries) {
        this.pluginName = name;
        this.pluginVersion = version;
        this.destination = dest;
        this.description = description;

        ArrayList<File> libraryFilesList = new ArrayList<File>();

        for (int i = 0; i < libraries.length; i++) {

            if (libraries[i] == null) {
                continue;
            }

            for (int j = 0; j < libraries[i].length; j++) {

                if (libraries[i][j] == null) {
                    continue;
                }

                libraryFilesList.add(libraries[i][j]);
            }
        }

        this.libraries = new File[libraryFilesList.size()];
        this.libraries = libraryFilesList.toArray(this.libraries);

        if (cReq != null) {
            generatePackage(pluginName,
                    this.pluginVersion,
                    this.description,
                    this.destination,
                    this.libraries,
                    new CreateLibraryPluginActionImpl(cReq.getCanvas()), false);
        } else {
            generatePackage(pluginName,
                    this.pluginVersion,
                    this.description,
                    this.destination,
                    this.libraries, null, false);
        }
    }

//    @MethodInfo(name = "",
//    interactive = true, hide = true)
//    public void create(
//            @ParamInfo(name = "Name:",
//            style = "plugin-name") String name,
//            @ParamInfo(name = "Version:") String version,
//            @ParamInfo(name = "Description:", style = "silent") String description,
//            @ParamInfo(name = "Destination", style = "save-folder-dialog") File dest,
//            @ParamInfo(name = " -- Libraries: --", style = "load-dialog",
//            options = "minArraySize=1;endings=[\".jar\"];description=\"Java Library - *.jar\"") File[][] libraries) {
//        create(null, name, version, description, dest, libraries);
//    }
    private static class CreateLibraryPluginActionImpl
            implements eu.mihosoft.vrl.devel.CreateLibraryPluginAction {

        private VisualCanvas canvas;

        public CreateLibraryPluginActionImpl(VisualCanvas canvas) {
            this.canvas = canvas;
        }

        @Override
        public boolean overwrite(File destFile) {
            return VDialog.showConfirmDialog(canvas,
                    "Overwrite file?",
                    "<html><div align=center>Shall the file "
                    + Message.EMPHASIZE_BEGIN
                    + destFile
                    + Message.EMPHASIZE_END
                    + " be overwritten?.</div></html>",
                    VDialog.DialogType.YES_NO)
                    == VDialog.YES;
        }

        @Override
        public void destIsNoFolder(File dest) {
            VDialog.showMessageDialog(canvas,
                    "Folder does not exist",
                    "<html><div align=center>"
                    + "The folder "
                    + Message.EMPHASIZE_BEGIN
                    + dest
                    + Message.EMPHASIZE_END
                    + " does not exist.<br>"
                    + "Please select an existing folder.</div></html>");
        }

        @Override
        public void cannotCreate(Exception ex) {
            canvas.getMessageBox().addUniqueMessage(
                    "Cannot Create Plugin",
                    ex.toString(), null, MessageType.ERROR);
        }

        @Override
        public void created(File f) {
            canvas.getMessageBox().addMessage(
                    "Created Plugin:",
                    ">> the plugin "
                    + f.getName() + " has been created.",
                    MessageType.INFO);
        }
    }; // end action

    @MethodInfo(noGUI = true)
    private static void generatePackage(
            final String name,
            final String version,
            final String description,
            final File dest,
            final File[] libs,
            final CreateLibraryPluginAction action,
            final boolean multiThreaded) {

        Runnable r = new Runnable() {

            @Override
            public void run() {
                generatePackage(name, version, description, dest, libs, action);
            }
        };


        if (multiThreaded) {
            Thread t = new Thread(r);
            t.start();
        } else {
            r.run();
        }
    }

    private static void generatePackage(
            String name, String version, String description, File dest, File[] libs,
            CreateLibraryPluginAction action) {
        PluginIdentifier pId = new PluginIdentifier(name, version);

        System.out.println("name: " + name + ", description: " + description);

        if (!dest.isDirectory()) {
            if (action != null) {
                action.destIsNoFolder(dest);
                return;
            } else {
                throw new IllegalArgumentException(
                        "The File "
                        + dest
                        + " does not exist or is no directory!");
            }
        }

        File destFile = new File(dest, name + ".jar");

        if (destFile.exists()) {
            if (action != null) {
                if (!action.overwrite(destFile)) {
                    return;
                }
            }
        }

        File tmpFolder = null;

        try {
            tmpFolder = IOUtil.createTempDir();
        } catch (IOException ex) {
            Logger.getLogger(LibraryPluginCreator.class.getName()).
                    log(Level.SEVERE, null, ex);

            if (action != null) {
                action.cannotCreate(ex);
            }

            return;
        }

        // copy libraries
        for (File file : libs) {

            IOException exception = null;

            try {
                IOUtil.unzip(file, tmpFolder);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(LibraryPluginCreator.class.getName()).
                        log(Level.SEVERE, null, ex);
                exception = ex;
            } catch (IOException ex) {
                Logger.getLogger(LibraryPluginCreator.class.getName()).
                        log(Level.SEVERE, null, ex);
                exception = ex;
            }

            if (exception != null) {
                if (action != null) {
                    action.cannotCreate(exception);
                }
            }
        }

        // create PluginConfigurator
        String imports = "";

        // add imports
        GroovyCompiler gCompiler = new GroovyCompiler();
        for (String importString : gCompiler.getImports()) {
            imports += importString;
        }

        // final source code
        String code = PluginConfiguratorGenerator.generate(
                new PluginInfo(name, version, description));
        
        code = code.replaceAll(PluginConfiguratorGenerator.pluginExportRegEx,
                "disableAccessControl(true);");

        // remove package definition
        code = code.replaceFirst(Patterns.PACKAGE_DEFINITION_STRING, "");

//        // replace version definition
//        code = code.replaceFirst(
//                "/\\*\\<VRL_PLUGIN_VERSION\\>\\*/.*/\\*\\</VRL_PLUGIN_VERSION\\>\\*/",
//                "\"" + VLangUtils.addEscapesToCode(version) + "\"");

        // final code
        code = "package eu.mihosoft.vrl.user;\n" + imports + "\n" + code;

        // Configure Compiler
        CompilerConfiguration conf = new CompilerConfiguration();
        conf.setTargetDirectory(tmpFolder);

        // Compile & Create .class File
        GroovyClassLoader gcl = new GroovyClassLoader(
                LibraryPluginCreator.class.getClassLoader());
        CompilationUnit cu = new CompilationUnit(gcl);
        cu.configure(conf);
        cu.addSource("PluginConfigurator", code);
        
        File codeFile = new File(tmpFolder,"eu/mihosoft/vrl/user/");
        codeFile.mkdirs();
        codeFile = new File(codeFile,"PluginConfigurator.groovy");
        
        TextSaver saver = new TextSaver();
        try {
            saver.saveFile(code, codeFile, ".groovy");
        } catch (IOException ex) {
            Logger.getLogger(LibraryPluginCreator.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            cu.compile();
        } catch (Exception ex) {
            if (action != null) {
                action.cannotCreate(ex);
            }
        }

        try {

            // Construct a string version of a manifest
            StringBuilder sbuf = new StringBuilder();
            sbuf.append("Manifest-Version: 1.0\n");
            sbuf.append("Created-By: VRL-" + Constants.VERSION + "\n");
            // sbuf.append("Java-Bean: False\n");

            // Convert the string to a input stream
            InputStream is = new ByteArrayInputStream(sbuf.toString().
                    getBytes("UTF-8"));

            File meta_inf = new File(tmpFolder.getAbsolutePath() + "/META-INF");
            meta_inf.mkdir();

            Manifest manifest = new Manifest(is);

            manifest.write(new FileOutputStream(
                    new File(meta_inf.getAbsolutePath() + "/MANIFEST.MF")));

        } catch (IOException ex) {
            Logger.getLogger(
                    Compiler.class.getName()).log(Level.SEVERE, null, ex);

            if (action != null) {
                action.cannotCreate(ex);
            }

            return;
        }

        try {
            IOUtil.zipContentOfFolder(
                    tmpFolder, destFile);
        } catch (IOException ex) {
            Logger.getLogger(LibraryPluginCreator.class.getName()).
                    log(Level.SEVERE, null, ex);

            if (action != null) {
                action.cannotCreate(ex);
            }
        }

        if (action != null) {
            action.created(destFile);
        }
    }
}
