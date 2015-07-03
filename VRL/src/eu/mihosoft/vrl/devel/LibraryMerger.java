/* 
 * LibraryMerger.java
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
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.types.CanvasRequest;
import eu.mihosoft.vrl.visual.Message;
import eu.mihosoft.vrl.visual.MessageType;
import eu.mihosoft.vrl.visual.VDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
@ComponentInfo(name = "Library Merger", category = "VRL/Development",
        description="Merges multiple Jar-Files into one")
@ObjectInfo(controlFlowIn = true, controlFlowOut = true)
public class LibraryMerger implements Serializable {

    private static final long serialVersionUID = 1L;
    private File destination;
    private File[] libraries;

    @MethodInfo(name = "",
    interactive = true, hide = false)
    public void merge(
            CanvasRequest cReq,
            @ParamInfo(name = "Destination", style = "save-dialog",
            options = "endings=[\".jar\"];description=\"Java Library - *.jar\"") File dest,
            @ParamInfo(name = " -- Libraries: --", style = "load-dialog",
            options = "minArraySize=1;endings=[\".jar\"];description=\"Java Library - *.jar\"") File[][] libraries) {
        this.destination = dest;

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
            mergePackage(
                    this.destination,
                    this.libraries,
                    new MergeLibraryPluginActionImpl(cReq.getCanvas()), false);
        } else {
            mergePackage(
                    this.destination,
                    this.libraries, null, false);
        }
    }

    private static class MergeLibraryPluginActionImpl
            implements eu.mihosoft.vrl.devel.MergeLibrariesAction {

        private VisualCanvas canvas;

        public MergeLibraryPluginActionImpl(VisualCanvas canvas) {
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
        public void cannotMerge(Exception ex) {
            canvas.getMessageBox().addUniqueMessage(
                    "Cannot Merge Libraries",
                    ex.toString(), null, MessageType.ERROR);
        }

        @Override
        public void merged(File f) {
            canvas.getMessageBox().addMessage(
                    "Merged Libraries:",
                    ">> the library "
                    + f.getName() + " has been created.",
                    MessageType.INFO);
        }

        @Override
        public void illegalDest(File destFile) {
            canvas.getMessageBox().addUniqueMessage(
                    "Cannot Merge Libraries", ">> File "
                    + Message.EMPHASIZE_BEGIN
                    + destFile
                    + Message.EMPHASIZE_END
                    + " does not specify a valid Java library."
                    + " Java libraries must end with <b>.jar</b>",
                    null, MessageType.ERROR);
        }
    }; // end action

    @MethodInfo(noGUI = true)
    private static void mergePackage(
            final File dest,
            final File[] libs,
            final MergeLibrariesAction action,
            final boolean multiThreaded) {

        Runnable r = new Runnable() {

            @Override
            public void run() {
                mergePackage(dest, libs, action);
            }
        };


        if (multiThreaded) {
            Thread t = new Thread(r);
            t.start();
        } else {
            r.run();
        }
    }

    private static void mergePackage(
            File dest, File[] libs,
            MergeLibrariesAction action) {

        if (!dest.getName().endsWith(".jar")) {
            if (action != null) {
                action.illegalDest(dest);
                return;
            } else {
                throw new IllegalArgumentException("Library must end with .jar");
            }
        }

        if (dest.exists()) {
            if (action != null) {
                if (!action.overwrite(dest)) {
                    return;
                }
            }
        }

        File tmpFolder = null;

        try {
            tmpFolder = IOUtil.createTempDir();
        } catch (IOException ex) {
            Logger.getLogger(LibraryMerger.class.getName()).
                    log(Level.SEVERE, null, ex);

            if (action != null) {
                action.cannotMerge(ex);
            }

            return;
        }

        // copy libraries
        for (File file : libs) {

            IOException exception = null;

            try {
                IOUtil.unzip(file, tmpFolder);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(LibraryMerger.class.getName()).
                        log(Level.SEVERE, null, ex);
                exception = ex;
            } catch (IOException ex) {
                Logger.getLogger(LibraryMerger.class.getName()).
                        log(Level.SEVERE, null, ex);
                exception = ex;
            }

            if (exception != null) {
                if (action != null) {
                    action.cannotMerge(exception);
                }
            }
        }

        try {
            IOUtil.zipContentOfFolder(
                    tmpFolder, dest);
        } catch (IOException ex) {
            Logger.getLogger(LibraryMerger.class.getName()).
                    log(Level.SEVERE, null, ex);

            if (action != null) {
                action.cannotMerge(ex);
            }
        }

        if (action != null) {
            action.merged(dest);
        }
    }
}
