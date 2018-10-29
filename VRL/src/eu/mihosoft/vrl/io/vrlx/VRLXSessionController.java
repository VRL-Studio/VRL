/* 
 * VRLXSessionController.java
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

package eu.mihosoft.vrl.io.vrlx;

import eu.mihosoft.vrl.animation.Animation;
import eu.mihosoft.vrl.animation.AnimationTask;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.system.Constants;
import eu.mihosoft.vrl.system.PluginDependency;
import eu.mihosoft.vrl.system.PluginDependencyCheck;
import eu.mihosoft.vrl.system.VParamUtil;
import eu.mihosoft.vrl.system.VRL;
import eu.mihosoft.vrl.visual.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public final class VRLXSessionController
        implements SessionLoader, SessionSaver {

    private FileFormat format;

    /**
     * Constructor.
     *
     * @param format file format
     */
    public VRLXSessionController(FileFormat format) {
        this.format = format;
    }

    public static void showErrorMessage(
            Canvas canvas, String title, Exception ex) {

        showErrorMessage(canvas, title, null, ex);
    }

    public static void showErrorMessage(
            Canvas canvas, String title, String message) {

        showErrorMessage(canvas, title, message, null);
    }

    public static void showErrorMessage(
            Canvas canvas, String title, String message, Exception ex) {

        if (title == null) {
            title = "File Error:";
        }

        if (message == null) {
            message = "";
        }

        if (ex != null) {
            message = ex.toString();

            if (ex.getCause() != null) {
                message += "<b><b>" + ex.getCause().toString();
            }

            Logger.getLogger(VRLXSessionController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        canvas.getMessageBox().addMessage(
                title,
                message, null, MessageType.ERROR);
    }

    @Override
    public boolean loadSession(final Canvas canvas, final File f)
            throws FileNotFoundException {
        if (!(canvas instanceof VisualCanvas)) {
            throw new IllegalArgumentException("Canvas class \""
                    + canvas.getClass() + "\" not supported!\n"
                    + "only \"" + VisualCanvas.class.getName()
                    + "\" and derived classes are supported!");
        }

        VisualCanvas vCanvas = (VisualCanvas) canvas;

        vCanvas.setSessionFileName(f.getAbsolutePath());

        Animation a = new Animation();

        LoadAnimationTask task = new LoadAnimationTask(this, f, canvas);
        a.addFrameListener(task);
        a.setDuration(0.5);

        canvas.setLoadingSession(true);
        canvas.getAnimationManager().addAnimation(a);

        VSwingUtil.newWaitController().requestWait(new ProceedRequest() {

            @Override
            public boolean proceed() {
                return !canvas.isLoadingSession();
            }
        });

        return task.getResult();
    }

    static class LoadAnimationTask implements AnimationTask {

        private VRLXSessionController controller;
        private File f;
        private Canvas canvas;
        private boolean result;

        public LoadAnimationTask(
                VRLXSessionController controller, File f, Canvas canvas) {
            this.controller = controller;
            this.f = f;
            this.canvas = canvas;
        }

        @Override
        public void firstFrameStarted() {
        }

        @Override
        public void frameStarted(double time) {
        }

        @Override
        public void lastFrameStarted() {
            try {
                result = controller.loadSessionWithoutEffect(canvas, f);
            } catch (Throwable tr) {
                Logger.getLogger(VRLXSessionController.class.getName()).
                        log(Level.SEVERE, null, tr);
            } finally {
                canvas.setLoadingSession(false);
            }
        }

        /**
         * @return the result
         */
        public boolean getResult() {
            return result;
        }
    }

    static class SaveAnimationTask implements AnimationTask {

        private VRLXSessionController controller;
        private File f;
        private Canvas canvas;
        private boolean result;
        private boolean showConfirmationMessage;

        public SaveAnimationTask(
                VRLXSessionController controller, File f, Canvas canvas,
                boolean showConfirmationMessage) {
            this.controller = controller;
            this.f = f;
            this.canvas = canvas;
            this.showConfirmationMessage = showConfirmationMessage;
        }

        @Override
        public void firstFrameStarted() {
        }

        @Override
        public void frameStarted(double time) {
        }

        @Override
        public void lastFrameStarted() {
            result = controller.saveSessionWithoutEffect(
                    canvas, f, showConfirmationMessage);
        }

        /**
         * @return the result
         */
        public boolean getResult() {
            return result;
        }
    }

    public boolean loadSessionWithoutEffect(Canvas canvas, File f) {
        boolean result = true;

        System.out.println(">> Loading File " + f.getAbsolutePath());

        if (!(canvas instanceof VisualCanvas)) {
            throw new IllegalArgumentException("Canvas class \""
                    + canvas.getClass() + "\" not supported!\n"
                    + "only \"" + VisualCanvas.class.getName()
                    + "\" and derived classes are supported!");
        }

        VisualCanvas vCanvas = (VisualCanvas) canvas;

        Thread.currentThread().setContextClassLoader(vCanvas.getClassLoader());

        // ********************************************************
        // INIT
        // ********************************************************

        // while loading we need to disable effects due to time dependent
        // reference problems
        boolean effectState = canvas.isDisableEffects();

        canvas.setLoadingSession(true);
        canvas.setDisableEffects(true);

        SessionFile file = null;

        boolean loadingError = false;

        try {
            file = format.getModel().loadFile(f);
        } catch (Exception ex) {
            loadingError = true;
            String message = ex.toString();
            
            ex.printStackTrace(System.err);

            if (ex.getCause() != null) {
                message += "<b><b>" + ex.getCause().toString();
            }

            canvas.getMessageBox().addMessage("Can't load file!",
                    message, null, MessageType.ERROR);
        }

        if (loadingError && file != null) {
            canvas.getMessageBox().addMessage("Can't load file!",
                    " still trying to proceed. File is possibly broken!",
                    null, MessageType.ERROR);
        }

        // ********************************************************
        // FILE VERSION
        // ********************************************************

        if (file == null || file.getVersionInfo() == null) {
            canvas.getMessageBox().addMessage("Can't load file!",
                    ">> no file version info found!",
                    null, MessageType.ERROR);

            canvas.setDisableEffects(effectState);
            canvas.setLoadingSession(false);
            canvas.setPresentationMode(!canvas.getWindowGroups().isEmpty());

            return false;
        }

        if (!Constants.fileVersionCompatibility.verify(
                file.getVersionInfo(), canvas)) {

            canvas.setDisableEffects(effectState);
            canvas.setLoadingSession(false);
            canvas.setPresentationMode(!canvas.getWindowGroups().isEmpty());

            return false;
        }

        // ********************************************************
        // PLUGIN DEPENDENCIES
        // ********************************************************

        PluginDependencyCheck check = VRL.verify(file.getPluginDependencies());

        if (!check.isValid()) {

            String depString = ">> the following plugins are missing:";

            depString += "<ul>";

            for (PluginDependency dep : check.getMissingDependencies()) {
                depString += "<li> =&gt; " + dep.toString() + "</li>";
            }

            depString += "</ul>";
            depString += ">> still trying to proceed.<br>";
            depString += "<br>>> <b>Warning:</b> do not save this file under the"
                    + " same filename unless you are sure that everything has"
                    + " been loaded correctly. Incorrectly loaded content that"
                    + " depends on"
                    + " the missing plugins will probably be lost!"
                    + " Add the missing plugins and try to load this"
                    + " file again.<br>";
            depString += "<br>>> <b>Note:</b> if this message still persists "
                    + " consult the plugin developers and/or "
                    + Constants.WRITE_VRL_BUG_REPORT + ".";

            canvas.getMessageBox().addMessage("File depends on missing plugins:",
                    depString,
                    null, MessageType.WARNING);
        }

        // ********************************************************
        // LOAD TASK
        // ********************************************************

        try {
            result = format.performLoadTasks(canvas, file);
        } catch (Exception ex) {
            String message = ex.toString();
            
            ex.printStackTrace(System.err);

            if (ex.getCause() != null) {
                message += "<b><b>" + ex.getCause().toString();
            }

            canvas.getMessageBox().addMessage("Can't load file!",
                    message, null, MessageType.ERROR);
        }

        // ********************************************************
        // FINALIZE
        // ********************************************************

        // if effects where enabled before loading they will be
        // enabled again
        canvas.setDisableEffects(effectState);

        // try to call the postInit() method of the initializer
        try {
            canvas.getSessionInitializers().postInit(canvas);
        } catch (Exception ex) {
            String message = ">> init method returns errors:<br><br>";

            message += ex.toString();

            if (ex.getCause() != null) {
                message += "<br>>> " + ex.getCause().toString();
            }

            canvas.getMessageBox().addMessage(
                    "Error while calling session initializer:", ">> "
                    + message, MessageType.ERROR);
        }

        canvas.setLoadingSession(false);

        canvas.setPresentationMode(!canvas.getWindowGroups().isEmpty());

        return result;
    }

    /**
     * Saves the current canvas session to file.
     *
     * @param f the destionation file
     * @throws java.io.FileNotFoundException
     */
    public boolean saveSession(final Canvas canvas, File f,
            boolean showConfirmationMsg) {

        VParamUtil.throwIfNull(canvas, f);

        canvas.setSessionFileName(f.getAbsolutePath());

        Animation a = new Animation();

        SaveAnimationTask task = new SaveAnimationTask(
                this, f, canvas, showConfirmationMsg);

        a.addFrameListener(task);
        a.setDuration(0.1);

        canvas.setSavingSession(true);
        canvas.getAnimationManager().addAnimation(a);
        VSwingUtil.newWaitController().requestWait(new ProceedRequest() {

            @Override
            public boolean proceed() {

                return !canvas.isSavingSession();
            }
        });

        return task.getResult();
    }

    /**
     * Saves the current canvas session to file.
     *
     * @param f the destionation file
     * @throws java.io.FileNotFoundException
     */
    @Override
    public boolean saveSession(final Canvas canvas, File f) {
        return saveSession(canvas, f, true);
    }

    public boolean saveSessionWithoutEffect(Canvas canvas, File f,
            boolean showMessage) {

        System.out.println(">> Saving File " + f.getAbsolutePath());

        boolean result = true;

        try {

            SessionFile file = format.getModel().newFile(
                    Constants.fileVersionCompatibility.getCurrentVersion(),
                    f.getAbsolutePath());

            // use project dependencies if canvas is a visualcanvas
            if (canvas instanceof VisualCanvas) {
                
                //TODO check if better approach is possible
                try {
                    file.setPluginDependencies(
                            ((VisualCanvas) canvas).getProjectController().
                            getProject().getProjectInfo().
                            getPluginDependencies());
                } catch (NullPointerException ex) {
                    System.out.println(
                            ">> warning: no plugin dependencies in project!s");
                }
                
            } else {
                // we do not have information on project dependencies and
                // use all available plugins as session dependency
                file.setPluginDependencies(VRL.getAvailablePlugins());
            }

            try {
                canvas.getSessionInitializers().preSave(canvas);
            } catch (Exception ex) {
                System.err.println(">> Warning<"
                        + canvas.getSessionInitializers().getClass()
                        + ">: error in canvas pre-save:");
                String message = ex.getMessage();
                if (ex.getCause() != null) {
                    message += "caused by: \n >> " + ex.getCause();
                }
                System.err.print(">> " + message);

                canvas.getMessageBox().addMessage(
                        "Warning: problem while calling session initializer",
                        ">> "
                        + message, MessageType.WARNING);
            }

            canvas.setSavingSession(true);

            try {
                format.performSaveTasks(canvas, file);
            } catch (Exception ex) {
                String message = ex.toString();
                
                ex.printStackTrace();

                if (ex.getCause() != null) {
                    message += "<b><b>" + ex.getCause().toString();
                }

                canvas.getMessageBox().addMessage("Can't save file!",
                        message, null, MessageType.ERROR);

                result = false;
            }

            // ********************************************************
            // WRITE OBJECT
            // ********************************************************

            format.getModel().saveFile(file, f);

            if (result && showMessage) {
                Message m = canvas.getMessageBox().addUniqueMessage(
                        "File Saved:",
                        ">> file has been successfully saved!", null,
                        MessageType.INFO, 5);
                canvas.getMessageBox().messageRead(m);
            }

            result = true;

        } catch (Exception ex) {
            String message = ex.toString();
            
            ex.printStackTrace();

            if (ex.getCause() != null) {
                message += "<b><b>" + ex.getCause().toString();
            }

            canvas.getMessageBox().addMessage("Can't save file!",
                    message, null, MessageType.ERROR);

            Logger.getLogger(VisualCanvas.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        canvas.setSavingSession(false);

        try {
            canvas.getSessionInitializers().postSave(canvas);
        } catch (Exception ex) {
            System.err.println(">> Warning: error in canvas finalization:");
            String message = ex.getMessage();
            if (ex.getCause() != null) {
                message += "caused by: \n >> " + ex.getCause();
            }
            System.err.print(">> " + message);

            canvas.getMessageBox().addMessage(
                    "Warning: problem while calling session initializer",
                    ">> " + message, MessageType.WARNING);

        }

        return result;
    }
} // end class

