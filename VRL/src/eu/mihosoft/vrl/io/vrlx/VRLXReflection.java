/* 
 * VRLXReflection.java
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

package eu.mihosoft.vrl.io.vrlx;

import eu.mihosoft.vrl.reflection.CallOptionsEvaluator;
import eu.mihosoft.vrl.reflection.ReferenceTask;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.reflection.VisualObjects;
import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.Connection;
import eu.mihosoft.vrl.visual.WindowGroups;

/**
 * Format definition for the VRL reflection package.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public final class VRLXReflection {

    private static final DynamicFileFormat format;
    public static final String VRLX_REFLECTION =
            VRLXVisual.VRLX_BASE + "/reflection";
    
    public static final String CODE_PATH = 
                VRLX_REFLECTION + "/code";

    static {

        format = new FileFormatTemplate(
                new DefaultIOModel(new XMLEntryFactory()));

        getVRLXFormat().addTask(
                VRLXVisual.VRLX_VISUAL + "/session-initializer",
                new VRLXVisual.SessionInitializerTask());
        getVRLXFormat().addTask(
                VRLXVisual.VRLX_VISUAL + "/style",
                new VRLXVisual.StyleTask());
        getVRLXFormat().addTask(
                VRLX_REFLECTION + "/class-loader",
                new ClassLoaderTask());
        getVRLXFormat().addTask(
                VRLX_REFLECTION + "/code",
                new CodeTask());
        getVRLXFormat().addTask(
                VRLXVisual.VRLX_VISUAL + "/windows",
                new WindowTask());
        getVRLXFormat().addTask(
                VRLX_REFLECTION + "/data-connections",
                new DataConnectionsTask());
        getVRLXFormat().addTask(
                VRLX_REFLECTION + "/control-flow-connections",
                new ControlFlowConnectionsTask());
        getVRLXFormat().addTask(
                VRLXVisual.VRLX_VISUAL + "/window-groups",
                new WindowGroupTask());
        getVRLXFormat().addTask(
                VRLXVisual.VRLX_VISUAL + "/session-code",
                new VRLXAbstractSession.AbstractSessionTask());

//        getVRLXFormat().addTask(
//                VRLXVisual.VRLX_VISUAL + "/session-code",
//                new VRLXImport.ImportSessionTask(TaskType.SAVE));
    }

    // no instanciation allowed
    private VRLXReflection() {
        throw new AssertionError(); // not in this class either!
    }

    /**
     * Returns the file format.
     * @return the file format
     */
    public static DynamicFileFormat getVRLXFormat() {
        return format;
    }

    public static class ClassLoaderTask implements SessionTask {

        @Override
        public boolean load(Canvas canvas, SessionEntry entry) {
//            VisualCanvas vCanvas = VisualCanvas.asVisualCanvas(canvas);
//
//            AbstractClassEntries classes = null;
//            
//            classes = (AbstractClassEntries) format.getModel().getFileContent(
//                    entry,
//                    AbstractClassEntries.class);
//            
//            if (classes == null) {
//                VRLXSessionController.showErrorMessage(
//                        canvas, null,
//                        "Error while loading Classes:");
//                return false;
//            }
//
//            ClassEntryClassLoader loader = new ClassEntryClassLoader();
//
//            loader.addClasses(classes.asClassEntries());
//
//            vCanvas.setClassLoader(new VClassLoader(loader));
//            
//            vCanvas.getCompilerProvider().addClassFiles(
//                    classes.asClassFileObject());

            return true;
        }

        @Override
        public boolean save(Canvas canvas, SessionEntry entry) {
//            VisualCanvas vCanvas = VisualCanvas.asVisualCanvas(canvas);
//
//            SessionEntryFile file = format.getModel().asFile(entry);
//
//            if (file == null) {
//                return false;
//            }
//            
//            Collection<ClassEntry> classes = new ArrayList<ClassEntry>();
//            
//            for(VCompiler c : vCanvas.getCompilerProvider().getCompilers()) {
//                classes.addAll(c.getClasses());
//            }
//
//            file.setContent(new AbstractClassEntries(classes));

            return true;
        }

        @Override
        public TaskType getType() {
            return TaskType.LOAD_AND_SAVE;
        }
    }

    public static class CodeTask implements SessionTask {

        @Override
        public TaskType getType() {
            return TaskType.LOAD_AND_SAVE;
        }

        @Override
        public boolean load(Canvas canvas, SessionEntry entry) {
            boolean result = false;
            AbstractCodes codes = null;
            codes = (AbstractCodes) format.getModel().getFileContent(
                    entry,
                    AbstractCodes.class);
            if (codes == null) {
                VRLXSessionController.showErrorMessage(
                        canvas, null,
                        "Error while loading Codes:");
            } else {

                VisualCanvas vCanvas =
                        VisualCanvas.asVisualCanvas(canvas);

                try {
                    vCanvas.getCodes().addAll(codes);

//                    if (canvas.getSessionInitializer() != null) {
                    canvas.getSessionInitializers().codesLoaded(canvas);
//                    }

                    if (!vCanvas.isCodesAlreadyCompiled()) {
                        vCanvas.getCodes().addToClassPath(vCanvas);
                    }

                    Thread.currentThread().setContextClassLoader(
                            vCanvas.getClassLoader());

                    System.out.println(">>> Classloader defined");

                } catch (Exception ex) {
//                    String message = ex.toString();
//
//                    if (ex.getCause() != null) {
//                        message += "<b><b>" + ex.getCause().toString();
//                    }

//                    canvas.getMessageBox().addMessage(
//                            "Error while loading Codes:",
//                            message, null, MessageType.ERROR);

                    VRLXSessionController.showErrorMessage(
                            canvas,
                            "Error while loading Codes:", ex);

                }

                result = true;
            }
            return result;
        }

        @Override
        public boolean save(Canvas canvas, SessionEntry entry) {

            VisualCanvas vCanvas = VisualCanvas.asVisualCanvas(canvas);
            vCanvas.getCodes().removeUnusedCodes(vCanvas);

            Thread.currentThread().setContextClassLoader(
                    vCanvas.getClassLoader());

            SessionEntryFile file = format.getModel().asFile(entry);

            if (file == null) {
                return false;
            }

            file.setContent(vCanvas.getCodes());

            return true;
        }
    }

    public static class DataConnectionsTask implements SessionTask {

        @Override
        public TaskType getType() {
            return TaskType.LOAD_AND_SAVE;
        }

        @Override
        public boolean load(Canvas canvas, SessionEntry entry) {
            boolean result = false;
            AbstractDataConnections connections = null;
            connections = (AbstractDataConnections) format.getModel().
                    getFileContent(
                    entry,
                    AbstractDataConnections.class);
            if (connections == null) {
                VRLXSessionController.showErrorMessage(
                        canvas, null,
                        "Error while loading Data Connections:");
            } else {
                VisualCanvas vCanvas =
                        VisualCanvas.asVisualCanvas(canvas);
                connections.addToCanvas(vCanvas, vCanvas.getDataConnections());

                result = true;
            }
            return result;
        }

        @Override
        public boolean save(Canvas canvas, SessionEntry entry) {

            SessionEntryFile file = format.getModel().asFile(entry);

            if (file == null) {
                return false;
            }

            file.setContent(canvas.getWindowGroups());

            AbstractDataConnections connections = new AbstractDataConnections();
            for (Connection c : canvas.getDataConnections()) {
                connections.add(new AbstractDataConnection(c));
            }

            file.setContent(connections);

            return true;
        }
    }

    public static class ControlFlowConnectionsTask implements SessionTask {

        @Override
        public TaskType getType() {
            return TaskType.LOAD_AND_SAVE;
        }

        @Override
        public boolean load(Canvas canvas, SessionEntry entry) {
            boolean result = false;
            AbstractControlFlowConnections connections = null;
            connections = (AbstractControlFlowConnections) format.getModel().getFileContent(
                    entry,
                    AbstractControlFlowConnections.class);
            if (connections == null) {
                VRLXSessionController.showErrorMessage(
                        canvas, null,
                        "Error while loading Control Flow Connections:");
            } else {
                VisualCanvas vCanvas =
                        VisualCanvas.asVisualCanvas(canvas);
                connections.addToCanvas(vCanvas,
                        vCanvas.getControlFlowConnections());

                result = true;
            }
            return result;
        }

        @Override
        public boolean save(Canvas canvas, SessionEntry entry) {

            SessionEntryFile file = format.getModel().asFile(entry);

            if (file == null) {
                return false;
            }

            file.setContent(canvas.getWindowGroups());

            AbstractControlFlowConnections connections =
                    new AbstractControlFlowConnections();
            for (Connection c : canvas.getControlFlowConnections()) {
                connections.add(new AbstractControlFlowConnection(c));
            }

            file.setContent(connections);

            return true;
        }
    }

    public static class WindowTask extends VRLXVisual.WindowTask {

        @Override
        public boolean load(Canvas canvas, SessionEntry entry) {

            boolean result = super.load(canvas, entry);


            if (result == true) {
                VisualCanvas vCanvas = (VisualCanvas) canvas;

                CallOptionsEvaluator eval =
                        new CallOptionsEvaluator(vCanvas.getInspector());

                eval.evaluateAll(vCanvas);
            }

            return result;
        }

        @Override
        public boolean save(Canvas canvas, SessionEntry entry) {

//            VisualCanvas vCanvas = (VisualCanvas) canvas;

            ((VisualObjects) canvas.getWindows()).deleteNonSerializableObjects();

            return super.save(canvas, entry);
        }
    }

    public static class WindowGroupTask implements SessionTask {

        @Override
        public TaskType getType() {
            return TaskType.LOAD_AND_SAVE;
        }

        @Override
        public boolean load(Canvas canvas, SessionEntry entry) {
            boolean result = false;
            WindowGroups windowGroups = null;
            windowGroups = (WindowGroups) format.getModel().getFileContent(
                    entry,
                    WindowGroups.class);
            if (windowGroups == null) {
                VRLXSessionController.showErrorMessage(
                        canvas, null,
                        "Error while loading Window Groups:");
            } else {
                VisualCanvas vCanvas =
                        VisualCanvas.asVisualCanvas(canvas);

                canvas.setWindowGroups(windowGroups);
                canvas.getWindowGroups().setMainCanvas(canvas);

                // while loading we need to disable effects due to time 
                // dependent reference problems
                boolean effectState = canvas.isDisableEffects();

                canvas.setDisableEffects(false);

                // ********************************************************
                // REFERENCE TASKS (UIWINDOW)
                // ********************************************************

                // resolve ui component references (UIWindow)
                for (ReferenceTask task : vCanvas.getReferenceTasks()) {
                    task.resolve();
                }

                // if effects where enabled before loading they will be
                // enabled again
                canvas.setDisableEffects(effectState);

                result = true;
            }
            return result;
        }

        @Override
        public boolean save(Canvas canvas, SessionEntry entry) {
            SessionEntryFile file = format.getModel().asFile(entry);

            if (file == null) {
                return false;
            }

            file.setContent(canvas.getWindowGroups());

            return true;
        }
    }
}
