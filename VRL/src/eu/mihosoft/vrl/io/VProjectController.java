/* 
 * VProjectController.java
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

import eu.mihosoft.vrl.dialogs.NewComponentDialog;
import eu.mihosoft.vrl.io.vrlx.*;
import eu.mihosoft.vrl.asm.ByteCodeUtil;
import eu.mihosoft.vrl.asm.ClassFileDependency;
import eu.mihosoft.vrl.asm.CompilationUnit;
import eu.mihosoft.vrl.lang.InstanceCreator;
import eu.mihosoft.vrl.lang.ProjectBuilder;
import eu.mihosoft.vrl.lang.VBuildResult;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.lang.groovy.GroovyCodeEditorComponent;
import eu.mihosoft.vrl.lang.groovy.GroovyCompiler;
import eu.mihosoft.vrl.lang.visual.ClassInfoObject;
import eu.mihosoft.vrl.lang.visual.StartObject;
import eu.mihosoft.vrl.lang.visual.StopObject;
import eu.mihosoft.vrl.reflection.*;
import eu.mihosoft.vrl.system.*;
import eu.mihosoft.vrl.visual.VDialog.DialogType;
import eu.mihosoft.vrl.visual.*;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOException;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.apache.tools.ant.BuildException;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * Controls a project. Use an instance of this class to work with VRL projects.
 * Creating/deleting session entries, compiling a project and loading/saving it
 * are some of the key features.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VProjectController {

    /**
     * Project that shall be controlled.
     */
    private VProject project;
    /**
     * Parant container of the canvas.
     */
    private JComponent canvasParent;
    /**
     * Recent project manager of this controller.
     */
    private RecentFilesManager recentProjectManager;
    /**
     * Recent session manager of this controller.
     */
    private RecentFilesManager recentSessionManager;
    /**
     * Canvas configurator used to specify pre-/post loading actions.
     */
    private CanvasConfigurator configurator;
    /**
     * Name of the currently visualized session entry.
     */
    private String currentEntry = null;
    /**
     * Session history controller of this controller.
     */
    private SessionHistoryImpl sessionHistoryController;
    /**
     * Project library controller.
     */
    private ProjectLibraryController libraryController;
    /**
     * defines whether to visualize if currently saving session (canvas turns
     * dark)
     */
    private boolean visualSaveIndication = false;
    /**
     * defines whether to flush project (add changes to project archive file)
     */
    private boolean flushOnSave = true;
    /**
     * defines whether to commit changes when saving the project.
     */
    private boolean commitOnSave = true;
    /**
     * defines the maximum number of repair attempts (project build).
     */
    private int maxRepairAttempts = 20;
    /**
     * Session disposables. (threads and other resources that shall be disposed
     * on session close)
     */
    HashMap<String, ArrayList<Disposable>> sessionDisposablesByName =
            new HashMap<String, ArrayList<Disposable>>();
    private static final String ASK_FOR_SAVE_BEFORE_CLOSE_TEXT =
            "<p>Closing current Project.<p>"
            + "<p>Do you want to save the current session?</p><br>"
            + "<p><b>Unsaved changes will be lost!</b></p>";

    /**
     * Constructor.
     *
     * @param canvasParent parent of the canvas
     * @param configurator configurator used when loading
     */
    public VProjectController(JComponent canvasParent,
            CanvasConfigurator configurator) {
        this.canvasParent = canvasParent;
        this.configurator = configurator;

        sessionHistoryController = new SessionHistoryImpl(this);

        this.libraryController = new ProjectLibraryController(this);
    }

    /**
     * Returns the name of the current session.
     *
     * @return the name of the current session
     */
    public String getCurrentSession() {
        if (currentEntry == null) {
            currentEntry = "Main";
        }

        currentEntry = project.getFullEntryName(currentEntry);
        currentEntry = project.getEntryNameWithoutDefaultPackage(currentEntry);

        return currentEntry;
    }

    /**
     * Adds a session disposable. Session disposables are disposed when the
     * current session will be closed.
     *
     * @param d disposable
     * @throws IllegalStateException if no session has been opened before
     * calling this method
     */
    public void addSessionDisposable(Disposable d) {
        if (isProjectOpened() && getCurrentSession() != null) {

            test123(d);
        } else {
            throw new IllegalStateException(
                    "Cannot add session disposable. No session opened!");
        }
    }

    /**
     * Adds a session thread. Session threads are terminated after the current
     * session has been closed. <p><b>Note:</b> this method will first try to
     * interrupt session threads. If this does not work the thread will be
     * terminated after one second.</p>
     *
     * @param t session thread
     * @throws IllegalStateException if no session has been opened before
     * calling this method
     */
    public void addSessionThread(final Thread t) {
        addSessionThread(t, 100, 10);
    }

    /**
     * Adds a session thread. Session threads are terminated after the current
     * session has been closed. <p><b>Note:</b> this method will first try to
     * interrupt session threads. If this does not work the thread will be
     * terminated after the specified timeout/retries.</p>
     *
     * @param t session thread
     * @param timeout timeout (in milliseconds)
     * @param retries number of retries
     * @throws IllegalStateException if no session has been opened before
     * calling this method
     */
    @SuppressWarnings("deprecation") // we really need thread.stop()
    public void addSessionThread(
            final Thread t, final long timeout, final int retries) {

        addSessionDisposable(new Disposable() {
            @Override
            public void dispose() {
                Thread tImpl = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // try to gracefully stop the thread
                        try {
                            t.interrupt();
                        } catch (Throwable tr) {
                            //
                        }

                        // wait for thread termination
                        int tries = 0;
                        while (t.isAlive() && tries < retries) {
                            tries++;
                            try {
                                Thread.sleep(timeout);
                            } catch (InterruptedException ex) {
                                //
                            }
                        }

                        // force thread termination
                        if (t.isAlive()) {
                            try {
                                t.stop();
                            } catch (Throwable tr) {
                                //
                            }
                        }
                    }
                });

                tImpl.start();
            }
        });
    }

    /**
     * Disposes all session disposables. Call this method when closing the
     * specified session.
     *
     * @param name name of the session that will be closed
     */
    private void disposeSessionDisposables(String name) {

        name = project.getFullEntryName(name);
        name = project.getEntryNameWithoutDefaultPackage(name);

        Collection<Disposable> disposables = sessionDisposablesByName.get(name);

        if (disposables != null) {

            for (Disposable d : disposables) {
                try {
                    d.dispose();
                } catch (Throwable tr) {
                    tr.printStackTrace(System.err);
                }
            }

            // remove disposables
            disposables.clear();
        }
    }

    /**
     * Opens a session by name.
     *
     * @param name name of the session to open
     * @return <code>true</code> if successful; <code>false</code> otherwise
     * @throws IOException
     */
    public boolean open(String name) throws IOException {
        return open(name, true, false);
    }

    /**
     * Opens a session by name.
     *
     * @param name name of the session to open
     * @param ask defines whether to ask the user if he/she wants to save the
     * current session before opening
     * @param compile defines whether to compile the project when opening a
     * session
     * @return <code>true</code> if successful; <code>false</code> otherwise
     * @throws IOException
     */
    public boolean open(String name, boolean ask, boolean compile) throws IOException {

        name = getProject().getFullEntryName(name);

        if (getCurrentCanvas() == null && ask) {
            throw new IllegalStateException("No canvas available!");
        }

//        if (getCurrentCanvas() != null
//                && ask
//                && getCurrentSession().equals(name)) {
//            VDialog.showMessageDialog(getCurrentCanvas(),
//                    "Cannot open Session:",
//                    "The session is currently open.");
//            return;
//        }

        if (ask) {

            String displayName =
                    project.getEntryNameWithoutDefaultPackage(name);

//            if (VDialog.showConfirmDialog(getCurrentCanvas(),
//                    "Open Session:",
//                    "<html><div align=Center>"
//                    + "<p>Do you want to load component "
//                    + Message.EMPHASIZE_BEGIN
//                    + displayName + Message.EMPHASIZE_END + "<p>"
//                    + "<p><b>Unsaved changes will be lost!</b></p>"
//                    + "</div></html>",
//                    VDialog.DialogType.YES_NO) != VDialog.YES) {
//                return false;
//            }

            int answer = VDialog.showConfirmDialog(getCurrentCanvas(),
                    "Open Session:",
                    "<html><div align=Center>"
                    + "<p>Loading component/session "
                    + Message.EMPHASIZE_BEGIN
                    + displayName + Message.EMPHASIZE_END + ".<p>"
                    + "<p>Do you want to save the current session?</p><br>"
                    + "<p><b>Unsaved changes will be lost!</b></p>"
                    + "</div></html>",
                    new String[]{"Save", "Discard", "Cancel"});

            if (answer == 0) {
                saveProject(true, false);
            } else if (answer == 1) {
                // nothing to do
            } else if (answer == 2) {
                return false;
            }
        }

        closeAll();

        VisualCanvas existingCanvas = project.openedEntriesByName.get(name);

        name = VLangUtils.dotToSlash(name);
//
        ArrayList<Component> canvasList =
                VSwingUtil.getAllChildren(canvasParent, Canvas.class);

        for (Component component : canvasList) {
            canvasParent.remove(component);

            if (component instanceof VisualCanvas) {
                ((VisualCanvas) component).dispose();
                project.openedEntriesByCanvas.remove((VisualCanvas) component);
            }
        }

        if (existingCanvas != null) {
            System.out.println(">> closeExisting");
            close(name);
            existingCanvas.dispose();
        }

        VisualCanvas mainCanvas = new VisualCanvas();

        mainCanvas.setProjectController(this);
        mainCanvas.setActive(false);

        Exception exception = null;

        try {

            // VisualCanvas canvas = new VisualCanvas();

            canvasParent.add(mainCanvas);

            // convert used plugins to compatible format
            Collection<PluginDependency> usedPlugins = new ArrayList<PluginDependency>();
            for (AbstractPluginDependency pDep : project.getProjectInfo().getPluginDependencies()) {
                usedPlugins.add(pDep.toPluginDependency());
            }

            VRL.addCanvas(mainCanvas, usedPlugins);

            if (configurator != null) {
                configurator.configurePreLoad(mainCanvas);
            }

            // add project classpath
            mainCanvas.getClassLoader().addURL(
                    getProject().getContentLocation().toURI().toURL());

            if (compile) {
                build();
            }

            project.openSessionEntry(mainCanvas, name);

            try {
                sessionHistoryController.addSession(name);
            } catch (Throwable tr) {
                Logger.getLogger(VProjectController.class.getName()).
                        log(Level.SEVERE, null, tr);
            }

            if (recentSessionManager != null) {
                recentSessionManager.addRecentSession(
                        project.getEntryNameWithoutDefaultPackage(name), null);
            }

            project.openedEntriesByName.put(name, mainCanvas);
            project.openedEntriesByCanvas.put(mainCanvas, name);

            currentEntry = name;

            if (configurator != null) {
                configurator.configurePostLoad(mainCanvas);
            }

            // set the session filename before adding the canvas to the
            // studio frame
            mainCanvas.setSessionFileName(project.getFile().getAbsolutePath());

            addComponentClassesToCurrentCanvas();

            // focus request is highly important because otherwise
            // vshortcut listeners do not work ?!?
            // TODO 22.08.2011: is this a bug related to swing, vrl or foxtrot?
            canvasParent.requestFocus();
        } catch (Exception ex) {
            exception = ex;
        } finally {
            mainCanvas.setActive(true);
        }

        GroovyCodeEditorComponent.updateAllCodeEditorsOnCanvas(getCurrentCanvas());

        if (exception != null && exception instanceof IOException) {
            throw (IOException) exception;
        }

        return true;
    }

//    /**
//     * Creates a session entry with the specified name.
//     *
//     * @param name name of the session to create
//     * @throws IOException
//     */
//    private void createSessionEntry(String name) throws IOException {
//        VisualCanvas canvas = new VisualCanvas();
//        project.saveSessionEntry(canvas, name);
//        canvas.dispose();
//    }
    /**
     * Creates a session entry with the specified name.
     *
     * @param name name of the session to create
     * @param infoObj class info object that shall be assigned to the canvas
     * @throws IOException
     */
    private void createSessionEntry(
            String name, ClassInfoObject infoObj) throws IOException {
        VisualCanvas canvas = new VisualCanvas();

        canvas.getSession().setInfo(new AbstractComponentClassInfo(infoObj));

        StartObject startObj = new StartObject();
        StopObject stopObject = new StopObject();
        VisualObject vStart = canvas.addObject(startObj);
        VisualObject vStop = canvas.addObject(stopObject, new Point(200, 0));

        ControlFlowConnector controlflowStart =
                vStart.getObjectRepresentation().getControlFlowOutput();

        ControlFlowConnector controlflowStop =
                vStop.getObjectRepresentation().getControlFlowInput();

        canvas.getControlFlowConnections().add(controlflowStart, controlflowStop);

        project.saveSessionEntry(
                canvas, VLangUtils.dotToSlash(name), true, true, true);

        build(false, false);

        canvas.dispose();
    }

    /**
     * Deletes the specified session entry.
     *
     * @param name name of the session to delete
     * @return <code>true</code> if successful; <code>false</code> otherwise
     * @throws IOException
     * @throws BuildException
     */
    private boolean deleteSessionEntry(String name)
            throws IOException, BuildException {

        boolean result = false;

        if (!project.containsEntry(name)) {

            System.err.println("Entry not found: " + name);

            result = false;
        } else {

            System.out.println("deleting file: "
                    + project.getSessionFileByEntryName(name).getAbsolutePath());
            System.out.println("deleting file: "
                    + project.getSourceFileByEntryName(name).getAbsolutePath());
            System.out.println("deleting file: "
                    + project.getClassFileByEntryName(name).getAbsolutePath());

            File codeSrc = project.getSourceFileByEntryName(name);
            File sessionSrc = project.getSessionFileByEntryName(name);
            File classSrc = project.getClassFileByEntryName(name);

            // filelocking issue that can only be fixed by manually triggering
            // the gc.
            System.gc();
            getCurrentCanvas().getClassLoader().close();
            System.gc();

            boolean codeDeleted = !codeSrc.exists()
                    || (codeSrc.exists() && codeSrc.delete());
            boolean sessionDeleted = !sessionSrc.exists()
                    || (sessionSrc.exists() && sessionSrc.delete());
            boolean classDeleted = !classSrc.exists()
                    || (classSrc.exists() && classSrc.delete());

            System.out.println(">> src deleted: " + codeDeleted);
            System.out.println(">> vrlx deleted: " + sessionDeleted);
            System.out.println(">> cls deleted: " + classDeleted);

            result = codeDeleted && sessionDeleted && classDeleted;

            // close version management window if opened as we add
            // new version 
            // (live update of version list is currently not supported)
            VersionManagement.closeDialog(getCurrentCanvas());

            String nameForMsg = project.getEntryNameWithoutDefaultPackage(name).replace("/", ".");

            project.getProjectFile().commit(
                    "session/component \"" + nameForMsg + "\" deleted.");

            // TODO: remove this clean-step to improve deletion-speed!
            // Currently we need to delete all classfiles because we 
            // do not know which files come from the session-entry that shall
            // be deleted
//            ProjectBuilder.clean(this);
//            result = result && ProjectBuilder.build(this);

            project.flush();
        }

        return result;
    }

    /**
     * Deletes a session entry. Deletion won't be performed if the entry that
     * shall be deleted is required by other entries. If this is the case this
     * method will show a dialog message that informs the user about the
     * dependency problem.
     *
     * @param name name of the entry to delete
     * @return <code>true</code> if successful; <code>false</code> otherwise
     * @throws IOException
     */
    public boolean delete(String name) throws IOException {

        if (getCurrentCanvas() != null
                && project.getFullEntryName(name).equals(
                project.getFullEntryName("Main"))) {
            VDialog.showMessageDialog(getCurrentCanvas(),
                    "Cannot delete Session:",
                    "Main session cannot be deleted.");
            return false;
        }


        if (getCurrentCanvas() != null
                && project.getFullEntryName(getCurrentSession()).equals(
                project.getFullEntryName(name))) {
            VDialog.showMessageDialog(getCurrentCanvas(),
                    "Cannot delete Session/Component:",
                    "Close the session before deleting it.");
            return false;
        }

        ArrayList<String> deps = project.getSessionsDependingOn(name);

        String fullName =
                VLangUtils.slashToDot(project.getFullEntryName(name)).trim();

        // searches classes that need the session entry that shall be deleted
        // stores these dependencies in a string list
        for (ClassFileDependency clsDep : getNamesOfUsedClasses()) {

            String classFileClassName =
                    getProject().getClassNameFromFile(clsDep.getFile());

            classFileClassName = VLangUtils.slashToDot(classFileClassName);

            if (classFileClassName.equals(fullName)) {
                continue;
            }

            for (String depName : clsDep.getDependencies()) {
                String outerCls = depName.split("\\$")[0].trim();
                boolean equals = outerCls.equals(fullName);

                if (equals) {
                    deps.add(
                            project.getEntryNameWithoutDefaultPackage(classFileClassName));
                }
            }
        }

        // convert the dependencies to a HTML string
        String depsList = "<p>";

        for (String d : deps) {
            if (!depsList.contains(d)) {
                depsList += Message.EMPHASIZE_BEGIN
                        + d + Message.EMPHASIZE_END + "<br>";
            }
        }

        depsList += "</p><br>";

        // show dependencies via dialog and prevent deletion
        if (!deps.isEmpty()) {
            VDialog.showMessageDialog(getCurrentCanvas(),
                    "Cannot delete Session/Component:",
                    "<html><div align=Center>"
                    + "<p>The following Sessions/Components depend on "
                    + Message.EMPHASIZE_BEGIN
                    + project.getEntryNameWithoutDefaultPackage(
                    project.getFullEntryName(name))
                    + Message.EMPHASIZE_END + ":<p>"
                    + depsList
                    + "<p><b>Remove this component in the above listed"
                    + " Sessions/Components first!</b></p>"
                    + "</div></html>");
            return false;
        }

        getCurrentCanvas().setActive(false);

        boolean result = deleteSessionEntry(
                project.getFullEntryName(name));

        if (result) {
            recentSessionManager.removeRecentSession(
                    project.getEntryNameWithoutDefaultPackage(
                    project.getFullEntryName(name)));
            sessionHistoryController.remove(project.getFullEntryName(name));
            build(false, false);

        } else {
            getCurrentCanvas().setActive(true);
            VDialog.showMessageDialog(getCurrentCanvas(),
                    "Cannot delete Session/Component:",
                    "This is probably a filelocking issue.");

        }

        removeInnerClassFilesOf(name, false);

        GroovyCodeEditorComponent.updateAllCodeEditorsOnCanvas(getCurrentCanvas());

        getCurrentCanvas().setActive(true);

        return result;
    }

    /**
     * Copies the specified entry. A dialog will appear and ask the user about
     * the destination name. This dialog checks that the name is valid. If
     * copying is not possible because the destination already exists copying
     * will not be performed.
     *
     * @param name name of the entry to copy
     * @return <code>true</code> if successful; <code>false</code> otherwise
     * @throws IOException
     */
    public boolean copy(String name) throws IOException {

        ClassInfoObject info = NewComponentDialog.show(getCurrentCanvas(),
                "Copy Component");

        if (info != null) {

            name = project.getEntryNameWithoutDefaultPackage(name);
            String newName = project.getEntryNameWithoutDefaultPackage(
                    VLangUtils.dotToSlash(info.getClassName()));

            String srcCmpNameForMessage = project.getEntryNameWithoutDefaultPackage(
                    project.getFullEntryName(name)).replace("/", ".");
            String dstCmpNameForMessage = newName.replace("/", ".");

            if (project.containsEntry(info.getClassName())) {
                VDialog.showMessageDialog(getCurrentCanvas(),
                        "Cannot Copy Component:",
                        "<html>Component " + Message.EMPHASIZE_BEGIN
                        + dstCmpNameForMessage
                        + Message.EMPHASIZE_END
                        + " already exists!</html>");

                return false;
            }


            if (!copyEntry(name, info)) {

                String message = "Component " + Message.EMPHASIZE_BEGIN
                        + srcCmpNameForMessage
                        + Message.EMPHASIZE_END + " cannot be copied to "
                        + Message.EMPHASIZE_BEGIN
                        + dstCmpNameForMessage
                        + Message.EMPHASIZE_END + ".";

                getCurrentCanvas().getMessageBox().addMessage(
                        "Cannot copy Component:", message, MessageType.ERROR);

                System.err.println(message);

                return false;
            }

            // close version management window if opened as we add
            // new version 
            // (live update of version list is currently not supported)
            VersionManagement.closeDialog(getCurrentCanvas());

            project.getProjectFile().commit("component copied:\n"
                    + " --> from: " + srcCmpNameForMessage + "\n"
                    + " --> to:   " + dstCmpNameForMessage);

            if (!open(info.getClassName())) {
                addComponentClassesToCurrentCanvas();
            }
        } // end if info!=null

        return false;
    }

    /**
     * Copies the specified session entry to the specified destination.
     *
     * @param oldName name of the entry to copy
     * @param newClsInfo cls info that contains the destination name
     * @return <code>true</code> if successful; <code>false</code> otherwise
     * @throws IOException
     */
    private boolean copyEntry(String oldName, ClassInfoObject newClsInfo)
            throws IOException {

        String newName = newClsInfo.getClassName();

        oldName = project.getFullEntryName(oldName);
        newName = project.getFullEntryName(newName);

        System.out.println(">> copy session entry \""
                + oldName + "\" to \"" + newName + "\".");

        if (!project.containsEntry(oldName)) {
            throw new FileNotFoundException(
                    "source file \"" + oldName + "\" cannot be found.");
        }

        if (project.containsEntry(newName)) {
            throw new FileNotFoundException(
                    "destination file \"" + newName + "\" already exists.");
        }

        // create destination directory if it does not exist
        File destinationFolder =
                project.getSessionFileByEntryName(newName).getParentFile();
        destinationFolder.mkdirs();

        // copy session file
        IOUtil.copyFile(project.getSessionFileByEntryName(oldName),
                project.getSessionFileByEntryName(newName));

        IOModel model = VRLXAbstractSession.getIOModel();

        SessionFile file = null;

        try {
            file = model.loadFile(project.getSessionFileByEntryName(newName));
        } catch (IOException ex) {
            IOUtil.deleteDirectory(project.getSessionFileByEntryName(newName));
            throw ex;
        }

        // Modify AbstractSession

        SessionEntryFile sessionEntryFile =
                model.getFile(file,
                VRLXAbstractSession.CONTENT_PATH);

        Object o = model.getFileContent(
                sessionEntryFile, AbstractSession.class);

        if (o == null) {
            return false;
        }

        AbstractSession abstractSession = (AbstractSession) o;

        abstractSession.getInfo().setComponentName(newName);

        // Modify AbstractCodes

        sessionEntryFile = model.getFile(file, VRLXReflection.CODE_PATH);

        o = null;

        o = model.getFileContent(
                sessionEntryFile, AbstractCodes.class);

        if (o == null) {
            return false;
        }

        AbstractCodes codes = (AbstractCodes) o;

        codes.clear();

        try {
            model.saveFile(file, project.getSessionFileByEntryName(newName));
        } catch (IOException ex) {
            IOUtil.deleteDirectory(project.getSessionFileByEntryName(newName));
            throw ex;
        }

        AbstractCode code = project.createComponentClassStubCode(newClsInfo);

        String importString = "package "
                + VLangUtils.slashToDot(
                VLangUtils.packageNameFromFullClassName(newName)) + "\n\n";

        Iterable<String> imports = new GroovyCompiler().getImports();
        for (String imp : imports) {
            importString += imp;
        }

        code.setCode(importString + "\n\n" + code.getCode());


        File codeFile = project.getSourceFileByEntryName(newName);

        TextSaver saver = new TextSaver();
        saver.saveFile(code.getCode(), codeFile, ".groovy");

        ProjectBuilder.build(this);


        return true;
    }

    /**
     * Builds the project.
     *
     * @return <code>true</code> if the project could be successfully compiled;
     * <code>false</code> otherwise
     */
    public boolean build() {

        return build(false, false);
    }

    /**
     * Determines whether the classes visualized on the current canvas reference
     * each other. If so, it is likely that a full project build needs to
     * trigger reload of the current canvas.
     *
     * @return <code>true</code> if the classes visualized on the current canvas
     * reference each other; <code>false</code> otherwie
     */
    public boolean canvasClassesReferenceEachOther() {
        Collection<CompilationUnit> namesOfClassesOnCanvas =
                getNamesOfClassesDefinedOnCanvas();

        Collection<Collection<String>> namesOfClassesUsedOnCanvas =
                getNamesOfClassesUsedOnCanvas();

        // check whether intersection occurs
        for (CompilationUnit n : namesOfClassesOnCanvas) {

            for (Collection<String> m : namesOfClassesUsedOnCanvas) {

                for (String innerOfN : n.getClassNames()) {
                    if (m.contains(innerOfN)) {
                        System.out.println(
                                ">> Project: class-referenced: " + innerOfN);
                        return true;
                    }
                }

            }
        }

        return false;
    }

    /**
     * Returns the names of all classes on the current canvas that use the
     * specified class.
     *
     * @param className class name
     * @return the names of all classes on the current canvas that use the
     * specified class
     */
    public Collection<String> getNamesOfClassesThatUse(String className) {
        Collection<String> result = new ArrayList<String>();

        Collection<CompilationUnit> namesOfDefinedClasses =
                getNamesOfDefinedClasses();

        for (CompilationUnit cu : namesOfDefinedClasses) {
            Set<String> classesUsedBy = null;
            try {
                classesUsedBy =
                        ByteCodeUtil.getClassesUsedBy(cu.getFile(), "");

            } catch (IOException ex) {
                System.out.println(
                        " >> cannot analyze .class file: " + cu.getFile());
                ex.printStackTrace(System.err);

                continue;
            }

            if (classesUsedBy.contains(className)) {
                result.add(cu.getClassName());
            }
        }

        return result;
    }

    /**
     * Returns the names of all classes used by the classes that are defined in
     * this project.
     *
     * @return the names of all classes used by the classes that are defined in
     * this project
     */
    public Collection<ClassFileDependency> getNamesOfUsedClasses() {
        ArrayList<File> files = IOUtil.listFiles(getProject().
                getContentLocation(), new String[]{".class"});

        Collection<ClassFileDependency> result =
                new ArrayList<ClassFileDependency>();

        for (File f : files) {

            // we have to exclude inner classes from dependency check
            // as they always have a dependency to the enclosing (outer) class.
            String absPath = f.getAbsolutePath().replace("\\", "/");
            String[] pathElements = absPath.split("/");
            if (pathElements.length > 0
                    && pathElements[pathElements.length - 1].contains("$")) {
                continue;
            }

            try {
                Collection<String> classNames =
                        ByteCodeUtil.getClassesUsedBy(f, "");

                result.add(new ClassFileDependencyImpl(f, classNames));
            } catch (Exception ex) {
                Logger.getLogger(VProjectController.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }

    /**
     * Returns the names of all classes used by the classes visualized on the
     * current canvas.
     *
     * @return the names of all classes used by the classes visualized on the
     * current canvas
     */
    public Collection<Collection<String>> getNamesOfClassesUsedOnCanvas() {

        ArrayList<Collection<String>> result =
                new ArrayList<Collection<String>>();

        for (File f : getClassFilesOfClassesDefinedOnCanvas()) {

            try {
                Collection<String> classNames =
                        ByteCodeUtil.getClassesUsedBy(f, "");

                result.add(classNames);
            } catch (Exception ex) {
                Logger.getLogger(VProjectController.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }

    /**
     * Returns the .class files of the classes that are defined by the current
     * canvas.
     *
     * @return the .class files of the classes that are defined by the current
     * canvas
     */
    public Collection<File> getClassFilesOfClassesDefinedOnCanvas() {
        ArrayList<File> files = new ArrayList<File>();

        // get classfiles from classes on canvas
        VisualCanvas canvas = getCurrentCanvas();

        Collection<Object> objects = canvas.getInspector().getObjects();

        Set<Class> classes = new HashSet<Class>();

        Collection<String> definedClasses = new ArrayList<String>();

        // convert defined classes to flat collection which simplifies
        // contains check
        for (CompilationUnit cu : getNamesOfDefinedClasses()) {
            definedClasses.addAll(cu.getClassNames());
        }

        for (Object o : objects) {

            if (!definedClasses.contains(o.getClass().getName())) {
                continue;
            }

            classes.add(canvas.getClassLoader().reloadClass(o.getClass()));
        }

        // finally get the files
        for (Class cls : classes) {
            files.add(VJarUtil.getClassLocation(cls));
        }

        return files;
    }

    /**
     * Returns the names of all classes that are defined by the current canvas.
     *
     * @return the names of all classes that are defined by the current canvas
     */
    public Collection<CompilationUnit> getNamesOfClassesDefinedOnCanvas() {

        ArrayList<CompilationUnit> result = new ArrayList<CompilationUnit>();

        for (File f : getClassFilesOfClassesDefinedOnCanvas()) {

            try {
                result.add(new CompilationUnitImpl(f,
                        ByteCodeUtil.getFirstClassNameIn(f),
                        ByteCodeUtil.getClassNames(f)));
            } catch (Exception ex) {
                Logger.getLogger(VProjectController.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }

    /**
     * Returns the names of the classes that are defined by this project.
     *
     * @return the names of the classes that are defined by this project
     */
    public Collection<CompilationUnit> getNamesOfDefinedClasses() {
        ArrayList<File> files = IOUtil.listFiles(getProject().
                getContentLocation(), new String[]{".class"});

        ArrayList<CompilationUnit> result = new ArrayList<CompilationUnit>();

        for (File f : files) {

            try {
                result.add(new CompilationUnitImpl(f,
                        ByteCodeUtil.getFirstClassNameIn(f),
                        ByteCodeUtil.getClassNames(f)));
            } catch (Exception ex) {
                Logger.getLogger(VProjectController.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }

    /**
     * Removes classfiles of inner classes. The purpose of this method is to
     * allow removal of associated classfiles if the specified class shall be
     * changed (recompiled). In many cases no
     * <code>clean()</code> on the whole project is necessary.
     *
     * @param clsName class name
     * @param excludeOuterCls defines whether to exclude the class file of the
     * outer class (toplevel class) from removal (may be usefull if this class
     * will be recompiled)
     * @see #build(boolean, boolean)
     * @see ProjectBuilder#clean(eu.mihosoft.vrl.io.VProjectController)
     */
    public void removeInnerClassFilesOf(
            String clsName, boolean excludeOuterCls) {

        // expand entry name if short version given
        clsName = getProject().getFullEntryName(clsName);

        File classFile = getProject().getClassFileByEntryName(clsName);

        System.out.println(
                ">> Remove classfiles of " + classFile);

        Collection<String> classNamesFromClassFile = new ArrayList<String>();

        try {
            classNamesFromClassFile =
                    ByteCodeUtil.getClassNames(classFile);

            if (excludeOuterCls) {
                // remove this class from list.
                // the classfile will be overwritten automatically by the compiler
                classNamesFromClassFile.remove(clsName);
            }

        } catch (IOException ex) {
            // some classfiles are missing (not critical)
        }

        if (!classNamesFromClassFile.isEmpty()) {
            getCurrentCanvas().getClassLoader().close();

            for (String n : classNamesFromClassFile) {
                File f =
                        getProject().
                        getClassFileByEntryName(n);

                System.out.println(
                        " --> deleting: " + f + " [" + f.delete() + "]");
            }
        }

        GroovyCodeEditorComponent.updateAllCodeEditorsOnCanvas(getCurrentCanvas());
    }

    /**
     * Builds the project. To prevent cleaning of the whole project one may
     * consider {@link #removeInnerClassFilesOf(java.lang.String) } instead of
     * setting the
     * <code>clean</code> property to
     * <code>true</code>.
     *
     * @param clean defines whether to clean the project before building it
     * @param showInfoMsg defines whether to show an info message if the project
     * has been successfully compiled (error message will always be shown in
     * case of build failure). This value is currently ignored!
     * @return <code>true</code> if the project could be successfully compiled;
     * <code>false</code> otherwise
     */
    public boolean build(boolean clean, boolean showInfoMsg) {

        VBuildResult result = null;

        VisualCanvas canvas = getCurrentCanvas();

        if (clean) {
            ProjectBuilder.clean(this);
        }

        int numberOfRepairAttempts = 0;

        result = ProjectBuilder.build(this);

        // if build is not successful try to repair
        if (!result.isSuccessful()) {
            getCurrentCanvas().setActive(false, true);
            System.out.println("----------------- PROJECT REPAIR (BEGIN) -----------------");
            System.out.println(">> Project code does not compile! Repairing:");
        }

        String brokenEntriesMsgString =
                "<b>Broken Entries:</b>"
                + "<ul>";

        while (!result.isSuccessful() && numberOfRepairAttempts < maxRepairAttempts) {

            numberOfRepairAttempts++;

            String msg = ">> Project code does not compile. Trying to repair (attempt "
                    + numberOfRepairAttempts + ")...<br>";


            System.out.println(" --> trying to repair (attempt "
                    + numberOfRepairAttempts + ")");

            for (String broken : result.getBrokenEntries()) {
                System.out.println("   --> broken: " + broken);
            }

            for (String en : result.getBrokenEntries()) {

                File entryFile = getProject().getSourceFileByEntryName(en);

                try {
                    TextLoader loader = new TextLoader();
                    String text = (String) loader.loadFile(entryFile);
                    String[] lines = text.split("\n");

                    String oldCode = "// -------- BROKEN CODE --------\n";

                    for (String l : lines) {
                        oldCode += "//" + l + "\n";
                    }

                    String packageName = VLangUtils.packageNameFromFullClassName(en);
                    packageName = VLangUtils.slashToDot(packageName);
                    String shortClassName = VLangUtils.shortNameFromFullClassName(en);

                    String newCode = ""
                            + "// # This Code Does Not Work Anymore\n"
                            + "//\n"
                            + "// Replacement code has been automatically generated to\n"
                            + "// ensure that the project can be compiled. Most probably\n"
                            + "// an API change led to broken code.\n"
                            + "//\n"
                            + "// # Repairing The Code:\n"
                            + "//\n"
                            + "// - remove everything from \"-- REPLACEMENT CODE --\n"
                            + "//   to \"-- BROKEN CODE\" --\n"
                            + "// - uncomment the remaining content: select it\n"
                            + "//   and press `CTRL+T`\n"
                            + "// - press the `compile` button\n"
                            + "// - now it is necessary to manually fix the problematic\n"
                            + "//   parts of the code (check error messages)\n\n"
                            + "// -------- REPLACEMENT CODE --------\n"
                            + "package " + packageName + ";\n"
                            + "import eu.mihosoft.vrl.annotation.*;\n\n"
                            + "@ComponentInfo(name=\"" + shortClassName + "\", category=\"Broken!\")\n"
                            + "public class " + shortClassName + " implements Serializable {\n"
                            + "  public static final long serialVersionUID = 1L;\n\n"
                            + "}\n\n";

                    newCode += oldCode;

                    TextSaver saver = new TextSaver();
                    saver.saveFile(newCode, entryFile, ".groovy");

                } catch (IOException ex) {
                    Logger.getLogger(VProjectController.class.getName()).log(Level.SEVERE, null, ex);
                }

                brokenEntriesMsgString += "<li>" + en + "</li>";
            }

            brokenEntriesMsgString += "</ul>";


            VMessage.warning("Project needs to be repaired:",
                    msg + "<br>" + brokenEntriesMsgString);

            VSwingUtil.newWaitController().requestConcurrentWait(new ProceedRequest() {
                @Override
                public boolean proceed() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(VProjectController.class.getName()).
                                log(Level.SEVERE, null, ex);
                    }

                    return true;
                }
            });

            result = ProjectBuilder.build(this);

        } // end while

        if (showInfoMsg) {
            System.out.println("ProjectBuilder.build(): showInfo value ignored!");
        }

//            if (showInfoMsg) {
//                MessageBox mBox = getCurrentCanvas().getMessageBox();
//                Message m =
//                        mBox.addMessage(
//                        "Project Compiled:",
//                        ">> project successfully compiled.", MessageType.INFO);
//                mBox.messageRead(m);
//            }

        if (canvas != null && !result.isSuccessful()) {

            String errorMessage = result.getErrorMessage();

            if (errorMessage == null) {
                errorMessage = "unknown error";
            }

            errorMessage = errorMessage.replace(
                    "org.codehaus.groovy.control.MultipleCompilationErrorsException: startup failed:",
                    "");

            canvas.getMessageBox().addMessage("Project Compilation failed:",
                    ">> compilation failed because of the following error(s):<br>"
                    + "<pre><code>"
                    + errorMessage
                    + "</code></pre>", MessageType.ERROR);
        }

        if (numberOfRepairAttempts > 0) {
            try {
                getProject().getProjectFile().commit("project repaired (see Broken! category)");
            } catch (IOException ex) {
                Logger.getLogger(VProjectController.class.getName()).
                        log(Level.SEVERE, null, ex);
            }

            System.out.println("----------------- PROJECT REPAIR (END) -----------------");

            System.out.println(">> project repaired.");

            VMessage.info("Project successfully repaired:",
                    "The project could be repaired. Please check the entries in the "
                    + Message.EMPHASIZE_BEGIN + "Broken!" + Message.EMPHASIZE_END + " category of the "
                    + Message.EMPHASIZE_BEGIN + "Manage Components" + Message.EMPHASIZE_END + " window\n"
                    + "It contains all broken entries.<br><br>"
                    + brokenEntriesMsgString);
        }

        getCurrentCanvas().setActive(true, true);

        // repair end

        // add updated classes to canvas etc.

        addComponentClassesToCurrentCanvas();

        VSwingUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean reload = canvasClassesReferenceEachOther();
                    if (reload) {
                        save(getCurrentSession(), false, false, "compiled project", false);
                        open(getCurrentSession(), false, false);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(VProjectController.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }
        });

        if (!result.isSuccessful()) {
            System.out.println("-------------- PROJECT REPAIR (END) --------------");
            System.out.println(">> FATAL: cannot repair project!\n");
            System.out.println(">> broken entries:");

            for (String broken : result.getBrokenEntries()) {
                System.out.println(" --> " + broken);
            }
        }

        // EXPERIMENTAL 04.05.2012
//        Set<Class<?>> updatedClasses = new HashSet<Class<?>>();
//
//        for (Object o : canvas.getInspector().getObjects()) {
//           
//            Class<?> cls = canvas.getClassLoader().reloadClass(o.getClass());
//
//            updatedClasses.add(cls);
//        }
//
//        for (Class<?> ucls : updatedClasses) {
//            try {
//                canvas.getInspector().replaceAllObjects(ucls, new InstanceCreator(canvas));
//            } catch (InterfaceChangedException ex) {
//                Logger.getLogger(VProjectController.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

        GroovyCodeEditorComponent.updateAllCodeEditorsOnCanvas(getCurrentCanvas());

        return result.isSuccessful();
    }

    /**
     * Adds the component classes to the current canvas. This method may also be
     * called to update the component classes.
     */
    public void addComponentClassesToCurrentCanvas() {

        Collection<File> componentClasses = IOUtil.listFiles(
                project.getContentLocation(), new String[]{".class"});

//        Collection<Class<?>> result = new ArrayList<Class<?>>();

        VisualCanvas canvas = getCurrentCanvas();

        String currentSessionClassName = VLangUtils.slashToDot(
                getProject().getFullEntryName(getCurrentSession()));

//        System.out.println("Removing: " + currentSessionClassName);
//        canvas.getClassLoader().removeClassByName(currentSessionClassName);

        canvas.getClassLoader().updateClassLoader();

        for (File f : componentClasses) {

            String className = getProject().getClassNameFromFile(f);

            // only allow class files that are defined by a session or groovy
            // code. classes defined by AbstractCode, GroovyWindow etc. are
            // excluded
            boolean isSessionComponent =
                    project.getSourceFileByEntryName(className).isFile();

            if (!isSessionComponent) {
                continue;
            }

            // replace slashes with dots
            className = VLangUtils.slashToDot(className);

            try {

                Class<?> cls = canvas.getClassLoader().loadClass(className);
//                result.add(cls);

                canvas.addClass(cls);

            } catch (ClassNotFoundException ex) {
                Logger.getLogger(VProjectController.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

        ComponentManagement.updateContent();

//        for (Object o : instances) {
//                    // convert from inspector id to window id
//                    Collection<Integer> windowIDs =
//                            canvas.getInspector().
//                            getCanvasWindowIDs(o);
//
//                    for (Integer winID : windowIDs) {
//                        if (winID != null) {
//                            canvas.getWindows().
//                                    removeObject(winID);
//                        }
//                    }
//                }

//        return result;
    }

    /**
     * Creates a new visual component. A dialog will ask the user about the
     * component name etc.
     *
     * @throws IOException
     */
    public void createComponent() throws IOException {
        VisualCanvas canvas = getCurrentCanvas();

        if (!isProjectOpened()) {
            VDialog.showMessageDialog(canvas, "No Project opened",
                    "Open a project to create new components.");
            return;
        }

        if (getProject() != null && getCurrentCanvas() != null) {

            if (!askForSave("New Component:", ASK_FOR_SAVE_BEFORE_CLOSE_TEXT)) {
                return;
            }
        }

        ClassInfoObject clsInfo = NewComponentDialog.show(canvas);

        if (clsInfo == null) {
            return;
        }

        String sessionName = VLangUtils.dotToSlash(clsInfo.getClassName());

        if (clsInfo.getMethodName() == null
                || clsInfo.getMethodName().trim().isEmpty()) {
            clsInfo.setMethodName("run");
        }

        if (sessionName.equals(getCurrentSession())) {
            VDialog.showMessageDialog(canvas, "Session already exists",
                    "The session to create must be closed before"
                    + " it can be overwritten.");
            return;
        }

        VDialog.AnswerType answer = VDialog.YES;

        if (project.containsEntry(sessionName)) {

            answer = VDialog.showConfirmDialog(canvas,
                    "Session already exists",
                    "<html>Do you want to overwrite session "
                    + Message.EMPHASIZE_BEGIN
                    + sessionName
                    + Message.EMPHASIZE_END
                    + "?</html>",
                    DialogType.YES_NO);
        }

        if (answer == VDialog.YES) {
            createSessionEntry(sessionName, clsInfo);
        }

        open(sessionName, false, true);
    }

    /**
     * Indicates if the project is currently opened.
     *
     * @return <code>true</code> if project is open; <code>false</code>
     * otherwise
     */
    public boolean isProjectOpened() {
        return project != null && project.isOpened();
    }

    /**
     * Creates a new project at the specified location. If necessary, this
     * method will show a dialog that asks whether to overwrite already existing
     * files or whether to close the current project.
     *
     * @param f project file
     * @param askForClose defines whether to ask if the current project shall be
     * closed before opening the new one
     * @param askIfOverwriteCurrentProject defines whether to ask if current
     * project shall be overwritten
     * @return <code>true</code> if the project has be created;
     * <code>false</code> otherwise
     * @throws IOException
     */
    public boolean newProject(File f, boolean askForClose, boolean askIfOverwriteCurrentProject)
            throws IOException {

        boolean newProjectIsCurrentProject =
                getProject() != null && getProject().getFile().equals(f);

        if (askIfOverwriteCurrentProject) {
            if (newProjectIsCurrentProject
                    && getCurrentCanvas() != null
                    && VDialog.showConfirmDialog(getCurrentCanvas(),
                    "Overwrite Current Project:",
                    "<html><div align=Center>"
                    + "<p>Do you want to overwrite the current project?<p>"
                    + "<p><b>Current project will be lost!</b></p>"
                    + "</div></html>",
                    VDialog.DialogType.YES_NO) != VDialog.YES) {
                return false;
            }
        }

        // check whether to ask the user 
        boolean ask = askForClose
                && !newProjectIsCurrentProject
                && getProject() != null
                && getProject().isOpened()
                && getCurrentCanvas() != null;

        if (ask) {
            if (ask) {
                if (!askForSave("Close Current Project (load):",
                        ASK_FOR_SAVE_BEFORE_CLOSE_TEXT)) {
                    return false;
                }
            }
        }

        closeProject();

        if (newProjectIsCurrentProject) {
            IOUtil.deleteDirectory(getProject().getFile());
        }

        setProject(VProject.create(f));

        // register version controller listener (cleans and builds project)
        getVersionController().addVersionEventListener(new VersionEventListener() {
            @Override
            public void preCheckout(RevCommit rev) {
                //
            }

            @Override
            public void postCheckout(RevCommit rev) {
                build(true, false);
            }
        });

        AbstractComponentClassInfo clsInfo = new AbstractComponentClassInfo();

        clsInfo.setComponentName("Main");

        clsInfo.setMethodName("run");
        clsInfo.setComponentDescription("Main Component");

        createSessionEntry(
                "Main", clsInfo.toClassInfo());

        getProject().getProjectFile().commit("initial session created");

        closeProject();

        return true;
    }

    /**
     * Loads a project from file.
     *
     * @param f project file
     * @param askForClose defines whether to ask the user if the current project
     * shall be closed (only relevant if a project is currently open)
     * @return <code>true</code> if successful; <code>false</code> otherwise
     * @throws IOException
     */
    public boolean loadProject(File f, boolean askForClose)
            throws IOException {

        if (askForClose) {
            if (!askForSave("Close Current Project (load):",
                    ASK_FOR_SAVE_BEFORE_CLOSE_TEXT)) {
                return false;
            }
        }

        closeProject();

        try {
            setProject(VProject.open(f));

            // register version controller listener (cleans and builds project)
            getVersionController().addVersionEventListener(
                    new VersionEventListener() {
                        @Override
                        public void preCheckout(RevCommit rev) {
                            //
                        }

                        @Override
                        public void postCheckout(RevCommit rev) {
                            build(true, false);
                        }
                    });

        } catch (Exception ex) {

            String message = ex.getMessage();

            if (ex.getCause() != null && ex.getCause().getMessage() != null) {
                message += "<br>" + ex.getCause().getMessage();
            }

            getCurrentCanvas().getMessageBox().addMessage(
                    "Cannot load Project:", message, MessageType.ERROR);

            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else {
                throw new IIOException(message, ex);
            }
        }

        Collection<AbstractPluginDependency> pluginDependencies =
                new ArrayList<AbstractPluginDependency>();

        try {
            pluginDependencies =
                    getProject().getProjectInfo().getPluginDependencies();
        } catch (Throwable tr) {
            System.out.println(">> warning: no plugin dependencies in project!");
        }

        // check for required plugins
        PluginDependencyCheck check = VRL.verify(pluginDependencies);

        if (!check.isValid()) {

            String depString = "The following plugins are missing:";

            depString += "<ul>";

            for (PluginDependency dep : check.getMissingDependencies()) {
                depString += "<li> =&gt; " + dep.toString() + "</li>";
            }

            depString += "</ul>";
            depString += "<b>Warning:</b> do not load this project"
                    + " unless you are sure that all necessary<br>plugins"
                    + " are active. Incorrectly loaded content that"
                    + " depends on"
                    + " the<br>missing plugins cannot be saved correctly.<br>"
                    + " <br>"
                    + " If you try to save it the content will probably be lost!<br><br>"
                    + " <b>Solution:</b> Abort loading, add the missing plugins"
                    + " and try to load this<br>project again.<br><br>";
            depString += "<b>Note:</b> if this message still persists "
                    + " consult the plugin developers and/or<br>"
                    + Constants.WRITE_VRL_BUG_REPORT + "."
                    + "<br><br>"
                    + "<b>Abort Loading?</b><br>";

            if (VDialog.showConfirmDialog(getCurrentCanvas(),
                    "Missing Plugins:",
                    "<html><div align=left>" + depString + "</div></html>",
                    DialogType.YES_NO) == VDialog.YES) {
                closeProject();

                return false;
            } else {

                depString = "<b>Please read carefully:</b><br><br>" + depString;

                if (VDialog.showConfirmDialog(getCurrentCanvas(),
                        "Missing Plugins:",
                        "<html><div align=left>" + depString + "</div></html>",
                        DialogType.YES_NO) == VDialog.YES) {
                    closeProject();

                    return false;
                }
            }
        }


        open("Main", false, false);

        return true;
    }

    /**
     * Saves the current project.
     *
     * @param commitChanges defines whether to commit changes
     * @param showSaveConfirmMsg defines whether to show a confirmation message
     * @throws IOException
     */
    private void saveProject(boolean commitChanges,
            boolean showSaveConfirmMsg) throws IOException {
        saveAll(null, commitChanges, null, showSaveConfirmMsg);
    }

    /**
     * Saves the current project.
     *
     * @param showSaveConfirmMsg defines whether to show a confirmation message
     * @throws IOException
     */
    public void saveProject(
            boolean showSaveConfirmMsg) throws IOException {
        saveAll(null, isCommitOnSave(), null, showSaveConfirmMsg);
    }

    /**
     * Saves the current project.
     *
     * @param commitChanges defines whether to commit changes
     * @param l a commit listener that allows to react on commit action (may be
     * null)
     * @param showSaveConfirmMsg defines wether to show a confirmation message
     * @throws IOException
     */
    private void saveProject(boolean commitChanges, CommitListener l,
            boolean showSaveConfirmMsg) throws IOException {
        saveAll(null, commitChanges, l, showSaveConfirmMsg);
    }

    /**
     * Saves the current project.
     *
     * @param l a commit listener that allows to react on commit action (may be
     * null)
     * @param showSaveConfirmMsg defines wether to show a confirmation message
     * @throws IOException
     */
    public void saveProject(CommitListener l,
            boolean showSaveConfirmMsg) throws IOException {
        saveAll(null, isCommitOnSave(), l, showSaveConfirmMsg);
    }

    /**
     * Saves the current project to a new location. <p><b>Note:</b> the project
     * location changes permanently to the new location. That is, calling
     * {@link #saveProject(boolean, boolean) } etc. will save changes to the new
     * location.</p>
     *
     * @param commitChanges defines whether to commit changes
     * @param showSaveConfirmMsg defines wether to show a confirmation message
     * @throws IOException
     */
    private void saveProjectAs(File dest, boolean commitChanges,
            boolean showSaveConfirmMsg) throws IOException {

        saveAll(dest, commitChanges, null, showSaveConfirmMsg);
    }

    /**
     * Saves the current project to a new location. <p><b>Note:</b> the project
     * location changes permanently to the new location. That is, calling
     * {@link #saveProject(boolean, boolean) } etc. will save changes to the new
     * location.</p>
     *
     * @param showSaveConfirmMsg defines wether to show a confirmation message
     * @throws IOException
     */
    public void saveProjectAs(File dest,
            boolean showSaveConfirmMsg) throws IOException {

        saveAll(dest, isCommitOnSave(), null, showSaveConfirmMsg);
    }

    /**
     * Saves the current project to a new location. <p><b>Note:</b> the project
     * location changes permanently to the new location. That is, calling
     * {@link #saveProject(boolean, boolean) } etc. will save changes to the new
     * location.</p>
     *
     * @param commitChanges defines whether to commit changes
     * @param l commit listener that allows to react on commit action (may be
     * null)
     * @param showSaveConfirmMsg defines wether to show a confirmation message
     * @throws IOException
     */
    private void saveProjectAs(File dest, boolean commitChanges,
            CommitListener l, boolean showSaveConfirmMsg) throws IOException {
        saveAll(dest, commitChanges, l, showSaveConfirmMsg);
    }

    /**
     * Saves the current project to a new location. <p><b>Note:</b> the project
     * location changes permanently to the new location. That is, calling
     * {@link #saveProject(boolean, boolean) } etc. will save changes to the new
     * location.</p>
     *
     * @param l commit listener that allows to react on commit action (may be
     * null)
     * @param showSaveConfirmMsg defines wether to show a confirmation message
     * @throws IOException
     */
    public void saveProjectAs(File dest,
            CommitListener l, boolean showSaveConfirmMsg) throws IOException {
        saveAll(dest, isCommitOnSave(), l, showSaveConfirmMsg);
    }

    /**
     * Closes the current project.
     *
     * @throws IOException
     */
    public void closeProject() throws IOException {

        closeProject(false, null);
    }

    /**
     * Saves the specified session entry.
     *
     * @param name name of the session to save.
     * @param commitChanges defines whether to commit changes
     * @param compile defines whether to compile the project
     * @param commitMessage the commit message
     * @param showSaveConfirmMsg defines wether to show a confirmation message
     * @throws IOException
     */
    public void save(
            String name, boolean commitChanges, boolean compile,
            String commitMessage, boolean showSaveConfirmMsg)
            throws IOException {

        name = getProject().getFullEntryName(name);


        VisualCanvas canvas = getSessionCanvas(name);

        if (canvas == null) {
            throw new IllegalArgumentException(
                    "Entry \"" + name + "\" not opened!");
        }

        canvas.setActive(false, isVisualSaveIndication());

        canvas.getEffectPane().
                setCursor(new Cursor(Cursor.WAIT_CURSOR));

        IOException exception = null;

        try {
            project.saveSessionEntry(canvas, name, commitChanges, false, showSaveConfirmMsg);

            if (compile) {
                build();
                addComponentClassesToCurrentCanvas();
            }

            if (commitChanges
                    && project.getProjectFile().hasUncommittedChanges()) {

                if (commitMessage == null) {
                    project.getProjectFile().commit("<!--file saved-->");
                } else {
                    getProject().getProjectFile().commit(commitMessage);
                }
            }
        } catch (IOException ex) {
            exception = ex;
        } finally {
            canvas.setActive(true, isVisualSaveIndication());
            canvas.getEffectPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        if (exception != null) {
            throw exception;
        }

    }

    /**
     * Closes the specified session.
     *
     * @param name name of the session to close
     * @throws IOException
     */
    public void close(String name) throws IOException {

        name = getProject().getFullEntryName(name);

        if (!project.isOpened()) {
            return;
        }

        VisualCanvas canvas = project.openedEntriesByName.get(name);

        if (canvas == null) {
            throw new IllegalArgumentException(
                    "Entry \"" + name + "\" not opened!");
        } else {
            canvas.clearCanvas();
            canvas.setActive(false);
        }

        project.openedEntriesByName.remove(name);
        project.openedEntriesByCanvas.remove(canvas);

        if (getCurrentSession().equals(name)) {
            currentEntry = null;
        }

        disposeSessionDisposables(name);
    }

    /**
     * Closes all sessions of the current project.
     *
     * @throws IOException if the project could not be closed
     */
    public void closeAll() throws IOException {

        if (project == null) {
            return;
        }

        Collection<String> names = new ArrayList<String>();

        names.addAll(project.openedEntriesByName.keySet());

        for (String entry : names) {
            close(entry);
        }

        // commit changes on close if "commit on save" is disabled and uncommited changes exist
        if (!isCommitOnSave() && getProject().getProjectFile().hasUncommittedChanges()) {
            getProject().getProjectFile().commit("project closed.");
        }
    }

    /**
     * Closes the project. Optionally a dialog asks whether to save the current
     * project. The dialog gives the options: save, discard, cancel.
     *
     * @param askForClose defines whether to ask the user before closing the
     * project
     * @param title dialog title (optional, <code>null</code> is valid)
     * @return <code>true</code> if the project has been closed;
     * <code>false</code> otherwise (depends on user decision)
     * @throws IOException if the project could not be closed
     */
    public boolean closeProject(boolean askForClose, String title)
            throws IOException {

        if (askForClose) {
            if (!askForSave(title, ASK_FOR_SAVE_BEFORE_CLOSE_TEXT)) {
                return false;
            }
        }

        if (getProject() != null && isProjectOpened()) {

            if (recentProjectManager != null) {
                recentProjectManager.addRecentSession(
                        project.getFile().getAbsolutePath(),
                        getCurrentCanvas().screenshot());
            }

            // prevents file locking issues on windows
            System.gc();
            getCurrentCanvas().getClassLoader().close();

            closeAll();
            project.close();
            currentEntry = null;

            sessionHistoryController.clear();

            if (recentSessionManager != null) {
                recentSessionManager.clear();
            }
        }

        return true;
    }

    /**
     * Asks the user whether to save the current project. If the user either
     * clicks on "save" or "discard" this method will return
     * <code>true</code>. It returns
     * <code>false</code> if the user clicks on "cancel" or if saving is not
     * possible.
     *
     * @param title title of the question dialog
     * @param the message text (html)
     * @return <code>true</code> if the user clicks on "save" or "discard";
     * <code>false</code> if the user clicks on "cancel" or if saving is not
     * possible
     */
    public boolean askForSave(String title, String text) {
        try {
            if (getProject() != null
                    && getProject().isOpened()
                    && getCurrentCanvas() != null) {

                if (title == null || title.length() == 0) {
                    title = "Close Current Project:";
                }

                int answer = VDialog.showConfirmDialog(getCurrentCanvas(),
                        title,
                        "<html><div align=Center>"
                        + text
                        + "</div></html>",
                        new String[]{"Save", "Discard", "Cancel"});

                if (answer == 0) {
                    saveProject(true, false);
                } else if (answer == 1) {
                    // nothing to do
                } else if (answer == 2) {
                    return false;
                }
            }
        } catch (IOException ex) {
            VDialog.AnswerType result =
                    VDialog.showConfirmDialog(getCurrentCanvas(),
                    "Error while saving project!",
                    "Dou you still want to proceed?", VDialog.YES_NO);
            if (result != VDialog.YES) {
                return false;
            }
        }

        return true;
    }

    /**
     * Indicates whether the current project has uncommitted visual changes.
     *
     * @return <code>true</code> if the current project has uncommitted visual
     * changes
     */
    private boolean hasVisualChanges() {
        return !getProject().getProjectFile().getUncommittedChanges(
                ".vrlx").isEmpty();
    }

    /**
     * Indicates whether the current project has uncommitted code changes.
     *
     * @return <code>true</code> if the current project has uncommitted code
     * changes
     */
    private boolean hasCodeChanges() {
        return !getProject().getProjectFile().getUncommittedChanges(
                ".groovy", ".java").isEmpty();
    }

    /**
     * Indicates whether the current project has other uncommitted changes
     * (other than code or visual).
     *
     * @return <code>true</code> if the current project has other uncommitted
     * changes (other than code or visual)
     */
    private boolean hasOtherChanges() {

        // if we don'T have any chages no further checks are necessary
        if (!getProject().getProjectFile().hasUncommittedChanges()) {
            return false;
        }

        int sizeVisual = getProject().getProjectFile().getUncommittedChanges(
                ".vrlx").size();

        int sizeCode = getProject().getProjectFile().getUncommittedChanges(
                ".groovy", ".java").size();

        int sizeAll = getProject().getProjectFile().
                getUncommittedChanges().size();


        // if number of changes is different from
        // visual changes and code changes we must have other changes
        return sizeAll != (sizeVisual + sizeCode);
    }

    /**
     * Saves all opened sessions and flushes the project (writes to archive).
     *
     * @param dest archive destination (if null, previous destination will be
     * used)
     * @param commitChanges defines whether to commit changes
     * @param l commit listener that allows to react on commit action (may be
     * null)
     * @throws IOException
     */
    private void saveAll(File dest, boolean commitChanges, CommitListener l,
            boolean showSaveConfirmMsg) throws IOException {

        for (String entry : project.openedEntriesByName.keySet()) {
            save(entry, false, false,
                    "",
                    showSaveConfirmMsg);
        }

        // save project info
        getProject().saveProjectInfo();

        // prevents file locking issues on windows
        System.gc();
        getCurrentCanvas().getClassLoader().close();

        if (l != null && commitChanges
                && !getProject().getProjectFile().hasUncommittedChanges()) {
            Message m = getCurrentCanvas().getMessageBox().addMessage(
                    "No Changes to Commit:",
                    ">> The session has been successfully saved."
                    + " But nothing has changed. "
                    + "Thus, no new version will be created.",
                    MessageType.INFO);

            getCurrentCanvas().getMessageBox().messageRead(m);
        }

        String msgSuffix = "changes:";

        boolean needsComma = false;

        if (hasCodeChanges()) {
            msgSuffix += " code";
            needsComma = true;
            build(false, false);
        } else {

            System.out.println(
                    ">> compiling not necessary (no relevant changes).");
        }

        if (hasVisualChanges()) {
            if (needsComma) {
                msgSuffix += ",";
            }
            needsComma = true;
            msgSuffix += " visual";
        }

        if (hasOtherChanges()) {
            if (needsComma) {
                msgSuffix += ",";
            }
            needsComma = true;
            msgSuffix += " other";
        }

        if (commitChanges
                && getProject().getProjectFile().hasUncommittedChanges()) {

            if (l == null) {
                getProject().getProjectFile().commit(
                        "project saved (" + msgSuffix + ")");
            } else {
                getProject().getProjectFile().commit(l.commit());
            }
        }

        // prevents file locking issues on windows
        System.gc();
        getCurrentCanvas().getClassLoader().close();

        // if no dest specified use old archive location
        if (dest == null) {
            if (isFlushOnSave()) {
                getProject().flush();
            }
        } else {
            getProject().switchToNewArchive(dest);
            // add new project classpath
            getCurrentCanvas().getClassLoader().addURL(
                    getProject().getContentLocation().toURI().toURL());
            getCurrentCanvas().getClassLoader().updateClassLoader();
        }
    }

    /**
     * Returns the name of the session currently visualized on the specified
     * canvas.
     *
     * @param canvas canvas that visualizes the session
     * @return the name of the session currently visualized on the specified
     * canvas
     */
    public String getSessionName(VisualCanvas canvas) {

        return project.openedEntriesByCanvas.get(canvas);
    }

    /**
     * Returns the canvas that visualizes the specified session.
     *
     * @param name name of the session
     * @return the canvas that visualizes the specified session or
     * <code>null</code> if no such canvas exists
     */
    public VisualCanvas getSessionCanvas(String name) {

        name = getProject().getFullEntryName(name);

        return project.openedEntriesByName.get(name);
    }

    /**
     * Returns the current canvas. This is the canvas the user currently
     * interacts with.
     *
     * @return the current canvas or <code>null</code> if no current canvas
     * exists
     */
    public VisualCanvas getCurrentCanvas() {
        VisualCanvas result = null;

        ArrayList<Component> canvasList =
                VSwingUtil.getAllChildren(canvasParent, VisualCanvas.class);


        if (!canvasList.isEmpty()) {

            result = (VisualCanvas) canvasList.get(0);

            if (canvasList.size() > 1) {
                System.err.println(
                        "WARNING: more than one canvas per canvas-parent"
                        + " may cause error in VProjectController!");
            }
        }

        if (result == null) {
            result = getSessionCanvas(getCurrentSession());
        }

        if (result == null) {
            Collection<String> values = project.openedEntriesByCanvas.values();
            if (!values.isEmpty()) {
                result = getSessionCanvas(values.iterator().next());
            }
        }

        return result;
    }

    /**
     * Returns the recent projects manager used to manage previoulsy opened
     * projects.
     *
     * @return the recent projects manager used to manage previoulsy opened
     * projects
     */
    public RecentFilesManager getRecentProjectsManager() {
        return recentProjectManager;
    }

    /**
     * Initializes the recent projects manager with the specified menu.
     *
     * @param loadRecentSessionsMenu menu
     */
    public void initRecentProjectsManager(JMenu loadRecentSessionsMenu) {

        File etc = VRL.getPropertyFolderManager().getEtcFolder();
        File projectCache = new File(etc, "project-cache.xml");

        this.recentProjectManager = new RecentFilesManager(projectCache);
        recentProjectManager.initController(
                loadRecentSessionsMenu, new LoadSessionRequest() {
            @Override
            public void request(final String fileName) {

                try {

                    loadProject(new File(fileName), true);

                } catch (IOException ex) {
                    Logger.getLogger(VProjectController.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }
        });

    }

    /**
     * Initializes the recent sessions manager with the specified menu.
     *
     * @param loadRecentSessionsMenu menu
     */
    public void initRecentSessionsManager(JMenu loadRecentSessionsMenu) {
        this.recentSessionManager = new RecentFilesManager();
        recentSessionManager.initController(
                loadRecentSessionsMenu, new LoadSessionRequest() {
            @Override
            public void request(final String fileName) {

                try {
                    open(fileName);
                } catch (IOException ex) {
                    Logger.getLogger(VProjectController.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }
        });

    }

    /**
     * Returns the project file of the current project (.vrlp, archive).
     *
     * @return the project file of the current project (.vrlp, archive)
     */
    public File getFile() {
        return project.getFile();
    }

    /**
     * Returns the current project.
     *
     * @return the current project
     */
    public VProject getProject() {
        return project;
    }

    /**
     * Defines the current project.
     *
     * @param project project
     */
    private void setProject(VProject project) {
        this.project = project;

        libraryController.projectDefined();
    }

    /**
     * Returns the version controller of the current project. This is the
     * recommended way to switch between different versions.
     *
     * @return the version controller of the current project
     */
    public VersionController getVersionController() {
        return project.getProjectFile();
    }

    /**
     * Returns the session history controller of the current project.
     *
     * @return the session history controller of the current project
     */
    public SessionHistoryController getSessionHistoryController() {
        return sessionHistoryController;
    }

    /**
     * Returns the library controller of the current project.
     *
     * @return the library controller of the current project
     */
    public ProjectLibraryController getLibraryController() {
        return libraryController;
    }

    /**
     * @return the visualSaveIndication
     */
    public boolean isVisualSaveIndication() {
        return visualSaveIndication;
    }

    /**
     * @param visualSaveIndication the visualSaveIndication to set
     */
    public void setVisualSaveIndication(boolean visualSaveIndication) {
        this.visualSaveIndication = visualSaveIndication;
    }

    /**
     * @return the flushOnSave
     */
    public boolean isFlushOnSave() {
        return flushOnSave;
    }

    /**
     * @param flushOnSave the flushOnSave to set
     */
    public void setFlushOnSave(boolean flushOnSave) {
        this.flushOnSave = flushOnSave;
    }

    /**
     * @return <code>true</code> if commiting on save; <code>false</code>
     * otherise
     */
    public boolean isCommitOnSave() {
        return commitOnSave;
    }

    /**
     * Defines whether to commit when saving the project (see {@link #saveProject(boolean)
     * } etc).
     *
     * @param commitOnSave the state to set
     */
    public void setCommitOnSave(boolean commitOnSave) {
        this.commitOnSave = commitOnSave;
    }

    private void test123(Disposable d) {
        if (sessionDisposablesByName.get(getCurrentSession()) == null) {
            sessionDisposablesByName.put(
                    getCurrentSession(), new ArrayList<Disposable>());
        }

        sessionDisposablesByName.get(getCurrentSession()).add(d);
    }
}

/**
 * Default implementation of the session history controller interface.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class SessionHistoryImpl implements SessionHistoryController {

    private ArrayList<String> entries = new ArrayList<String>();
    private int maxSize = 100;
    private int pointer = 0;
    private boolean browsingHistory;
    private VProjectController projectController;
    private final Object lock = new Object();
    private JMenuItem nextItem;
    private JMenuItem previousItem;

    /**
     * Constructor.
     *
     * @param projectController project controller to operate on
     */
    public SessionHistoryImpl(VProjectController projectController) {
        this.projectController = projectController;
    }

    /**
     * Adds a session to this controller.
     *
     * @param name name of the session to add
     */
    public void addSession(String name) {

        synchronized (lock) {

            // remove old history branch
            if (!isBrowsingHistory()) {

                int size = entries.size();

                // remove old history
                for (int i = pointer + 1; i < size; i++) {
                    // we must not delete entry with index i
                    // because index shift will occur for all elements with
                    // index > i
                    // thus, we always delete the last entry.
                    entries.remove(entries.size() - 1);
                }
            }

            if (isBrowsingHistory()) {
                setBroswingHistory(false);

            } else {

                // add entry
                entries.add(name);

                int size = entries.size();

                // remove oldest entries and ensure max capacity
                for (int i = maxSize; i < size; i++) {
                    entries.remove(i - maxSize);
                }

                // adding new entry to history, pointer must point to
                // the newly added entry
                reset();
            }

            updateItemStates();

        }
    }

    /**
     * Returns the name of the previous session in history.
     *
     * @param move defines whether to move back in history
     * @return the name of the previous session in history
     */
    private String getPreviousSession(boolean move) {

        synchronized (lock) {

            if (!hasPreviousSession()) {
                throw new IllegalStateException(
                        "no previous version available");
            }

            String result = entries.get(pointer - 1);

            if (move) {
                pointer--;
                updateItemStates();
            }

            return result;
        }
    }

    /**
     * Returns the name of the next session in history.
     *
     * @param move defines whether to move forward in history
     * @return the name of the next session in history
     */
    private String getNextSession(boolean move) {

        synchronized (lock) {

            if (!hasNextSession()) {
                throw new IllegalStateException("no next version available");
            }

            String result = entries.get(pointer + 1);

            if (move) {
                pointer++;
                updateItemStates();
            }

            return result;
        }
    }

    @Override
    public boolean hasNextSession() {

        synchronized (lock) {
            return pointer < entries.size() - 1;
        }
    }

    @Override
    public boolean hasPreviousSession() {

        synchronized (lock) {
            return pointer > 0 && !entries.isEmpty();

        }
    }

    @Override
    public void reset() {
        pointer = Math.max(0, entries.size() - 1);
    }

    /**
     * Defines whether currently browsing history.
     *
     * @param browsingHistory defines whether currently browsing history
     */
    void setBroswingHistory(boolean browsingHistory) {
        this.browsingHistory = browsingHistory;
    }

    /**
     * Indicates whether currently browsing history.
     *
     * @return <code>true</code> if currently browsing history;
     * <code>false</code> otherwise
     */
    boolean isBrowsingHistory() {
        return browsingHistory;
    }

    @Override
    public void clear() {
        entries.clear();
    }

    /**
     * removes the specified session from history.
     *
     * @param name name of the session to remove
     */
    public void remove(String name) {
        for (int i = 0; i < entries.size(); i++) {
            if (name.equals(entries.get(i))) {
                entries.set(i, null);
            }
        }
    }

    @Override
    public List<String> getHistory() {
        List<String> result = new ArrayList<String>();

        for (String s : entries) {
            if (s != null) {
                result.add(s);
            }
        }

        return result;
    }

    @Override
    public int getPosition() {
        return pointer;
    }

    @Override
    public boolean loadPreviousSession() throws IOException {

        synchronized (lock) {
            setBroswingHistory(true);

            String name = getPreviousSession(false);

            boolean loaded = name == null || projectController.open(name);

            if (loaded && hasPreviousSession()) {
                getPreviousSession(); // move backward
            }

            if (name == null) {
                loadPreviousSession();
            }

            updateItemStates();

            return loaded;
        }
    }

    @Override
    public boolean loadNextSession() throws IOException {
        synchronized (lock) {
            setBroswingHistory(true);

            String name = getNextSession(false);

            boolean loaded = name == null || projectController.open(name);

            if (loaded && hasNextSession()) {
                getNextSession(); // move forward
            }

            if (name == null) {
                loadNextSession();
            }

            updateItemStates();

            return loaded;
        }
    }

    /**
     * Updates the item status, e.g., updates
     * <code>setEnabled(...)</code> of the corresponding ui elements.
     */
    private void updateItemStates() {

        if (nextItem != null) {
            nextItem.setEnabled(hasNextSession());
        }

        if (previousItem != null) {
            previousItem.setEnabled(hasPreviousSession());
        }
    }

    @Override
    public String getPreviousSession() {
        return getPreviousSession(true);
    }

    @Override
    public String getNextSession() {
        return getNextSession(true);
    }

    @Override
    public void setNextItem(JMenuItem item) {
        this.nextItem = item;

        updateItemStates();

        ActionListener[] listeners = item.getActionListeners();

        for (ActionListener actionListener : listeners) {
            item.removeActionListener(actionListener);
        }

        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (SessionHistoryImpl.this.hasNextSession()) {
                    try {
                        SessionHistoryImpl.this.loadNextSession();
                    } catch (Exception ex) {
                        Logger.getLogger(SessionHistoryImpl.class.getName()).
                                log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    @Override
    public void setPreviousItem(JMenuItem item) {
        this.previousItem = item;

        updateItemStates();

        ActionListener[] listeners = item.getActionListeners();

        for (ActionListener actionListener : listeners) {
            item.removeActionListener(actionListener);
        }

        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (SessionHistoryImpl.this.hasPreviousSession()) {
                    try {
                        SessionHistoryImpl.this.loadPreviousSession();
                    } catch (Exception ex) {
                        Logger.getLogger(SessionHistoryImpl.class.getName()).
                                log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }
}

/**
 * Default classfile dependency implementation.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class ClassFileDependencyImpl implements ClassFileDependency {

    private File file;
    private Collection<String> dependencies;

    /**
     * Constructor.
     *
     * @param file class file
     * @param dependencies dependencies of the class file
     */
    public ClassFileDependencyImpl(File file,
            Collection<String> dependencies) {
        this.file = file;
        this.dependencies = dependencies;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public Collection<String> getDependencies() {
        return dependencies;
    }
}

/**
 * Default compilation unit implementation.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class CompilationUnitImpl implements CompilationUnit {

    private File file;
    private Collection<String> classNames;
    private String className;

    /**
     * Constructor.
     *
     * @param file source file
     * @param className name of the first public class in the specified file
     * @param classNames names of all classes defined in the specified file
     */
    public CompilationUnitImpl(File file, String className,
            Collection<String> classNames) {
        this.file = file;
        this.className = className;
        this.classNames = classNames;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public Collection<String> getClassNames() {
        return classNames;
    }

    @Override
    public String getClassName() {
        return className;
    }
}
