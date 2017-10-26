/* 
 * VisualObject.java
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
package eu.mihosoft.vrl.reflection;

import eu.mihosoft.vrl.annotation.AskIfCloseMethodInfo;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.io.TextLoader;
import eu.mihosoft.vrl.io.vrlx.AbstractCode;
import eu.mihosoft.vrl.io.vrlx.AbstractObjectRepresentation;
import eu.mihosoft.vrl.lang.InstanceCreator;
import eu.mihosoft.vrl.lang.groovy.GroovyCodeEditorComponent;
import eu.mihosoft.vrl.lang.groovy.GroovyCodeWindow;
import eu.mihosoft.vrl.lang.visual.InputObject;
import eu.mihosoft.vrl.lang.visual.OutputObject;
import eu.mihosoft.vrl.visual.*;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.io.NotSerializableException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;

/**
 * This class represents the visual identity of a Java object. It is the attempt
 * to provide a fully functional representation of any Java object that can be
 * analyzed by Javas Reflection API. For the automatic GUI generation to work it
 * is neccessary that a so called <code>TypeRepresentation</code> exists for all
 * types that are used by the object. These type representations have to be
 * added to the type factory of the visual canvas.
 *
 * @see TypeRepresentation
 * @see TypeRepresentationFactory
 * @see DefaultObjectRepresentation
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VisualObject extends CanvasWindow {

    private static final long serialVersionUID = -6377844990488909120L;
    /**
     * Object representation.
     */
    private final DefaultObjectRepresentation objectRepresentation;
    private EditSourceIcon sourceIcon;
    private AbstractCode code;

    /**
     * Creates a new instance of VisualObject.
     *
     * @param inspector the object inspector the object is connected to
     * @param oDesc the object description that is to be visualized
     * @param mainCanvas the mainCanvas on which the object is to be displayed
     */
    public VisualObject(VisualObjectInspector inspector,
            ObjectDescription oDesc,
            final VisualCanvas mainCanvas) {

        super("", mainCanvas);

        objectRepresentation = inspector.generateObjectRepresentation(oDesc);

        this.add(objectRepresentation);

        CapabilityChangedListener capabilityChangedListener
                = new CapabilityChangedListener() {

            @Override
            public void capabilityChanged(
                    CapabilityManager manager, Integer bit) {
                defineCapabilities();
            }
        };

        addCapabilityListener(capabilityChangedListener);

        initialize();
    }

    /**
     * Creates a new instance of VisualObject.
     *
     * @param contentProvider the content provider to use for GUI generation
     * @param mainCanvas the mainCanvas on which the object is to be displayed
     */
    public VisualObject(AbstractObjectRepresentation contentProvider,
            VisualCanvas mainCanvas) {
        super("", mainCanvas);

        objectRepresentation
                = (DefaultObjectRepresentation) contentProvider.getContent(
                        mainCanvas).get(0);

        add(objectRepresentation);

        initialize();
    }

    private void initialize() {
        final VisualCanvas vCanvas = (VisualCanvas) getMainCanvas();

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent ce) {
                //
            }

            @Override
            public void componentMoved(ComponentEvent ce) {
                try {
                    List<Connection> connections
                            = VisualObject.this.getMainCanvas().
                                    getControlFlowConnections().
                                    getAllWith(VisualObject.this);

                    //
                    // new redraw request for each connection to prevent
                    // connection redraw bug where the intersection of
                    // the connection and the window shawdow border was
                    // repainted before the other parts.
                    //
                    // this resulted in:
                    //    |       (this is where the bug occurs)
                    //  o-|-------\    |
                    // ___|        \   ↓  ______
                    //              ---  |______|
                    //                 --|-o    |
                    //                   |______|
                    //
                    for (Connection connection : connections) {
                        connection.contentChanged();
                    }
                } catch (Exception ex) {
                }
            }
        });

        this.validate();

        addActionListener(new CanvasActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(CanvasWindow.ACTIVE_ACTION)) {

                    Object o = vCanvas.getInspector().getObject(
                            getObjectRepresentation().getObjectID());

                    for (CanvasWindow vObj : vCanvas.getInspector().getCanvasWindows(o)) {
                        if (vObj != VisualObject.this) {
                            vObj.setActive(true);
                        }
                    }
                } else if (e.getActionCommand().equals(CanvasWindow.CLOSE_ACTION)) {
                    closeControlFlowConnection();
                }
            }

        });

        String title = objectRepresentation.getName();

//      ***
        // TODO should we automatically name parameter objects?
//        int objID = objectRepresentation.getObjectID();
//        Object obj = vCanvas.getInspector().getObject(objID);
//
//        if (obj instanceof InputObject) {
//
//            int numInstances =
//                    vCanvas.getInspector().
//                    getObjectsByClassName(obj.getClass().getName()).size();
//
//            for (int i = 1; i <= numInstances; i++) {
//
//                String newTitle = title + " " + numInstances;
//                
//                if (vCanvas.getInspector()
//            }
//        }
//      ***
        this.setTitle(title);

        initMenu();

        // define reference connectors
        ObjectInfo objInfo = getObjectRepresentation().
                getDescription().getInfo();

        if (objInfo == null || objInfo.referenceIn()) {
            getTitleBar().setLeftConnector(
                    getObjectRepresentation().getReferenceInput());
        }

        if (objInfo == null || objInfo.referenceOut()) {
            getTitleBar().setRightConnector(
                    getObjectRepresentation().getReferenceOutput());
        }

    }

    /**
     * Closes the controlflow connection if this object is removed, i.e., sender
     * is connected with receiver.
     */
    private void closeControlFlowConnection() {

        ControlFlowConnector input = getObjectRepresentation().getControlFlowInput();
        ControlFlowConnector output = getObjectRepresentation().getControlFlowOutput();

        List<Connection> inputConnections = getMainCanvas().
                getControlFlowConnections().getAllWith(input);

        List<Connection> outputConnections = getMainCanvas().
                getControlFlowConnections().getAllWith(output);

        // we cannot connect if no input or output present
        if (inputConnections.isEmpty() || outputConnections.isEmpty()) {
            return;
        }

        for (Connection senderConnection : inputConnections) {
            for (Connection receiverConnection : outputConnections) {
                Connector sender = senderConnection.getSender();
                Connector receiver = receiverConnection.getReceiver();

                getMainCanvas().getControlFlowConnections().add(sender, receiver);

            }
        }
    }

    @Override
    protected boolean isAskIfClose() {
        boolean result = false;

        DefaultObjectRepresentation oRep = getObjectRepresentation();

        ObjectInfo oInfo = null;

        if (oRep != null) {
            oInfo = oRep.getDescription().getInfo();
        }

        if (oInfo != null) {
            result = oInfo.askIfClose();
        }

        final VisualCanvas vCanvas = (VisualCanvas) getMainCanvas();

        Object obj = vCanvas.getInspector().getObject(oRep);

        Method askMethod = null;

        for (Method m : obj.getClass().getDeclaredMethods()) {

            if (m.getAnnotation(AskIfCloseMethodInfo.class) == null) {
                continue;
            }

            if (!m.getReturnType().equals(Boolean.class)
                    && !m.getReturnType().equals(boolean.class)) {
                continue;
            }

            if (m.getParameterTypes().length != 0) {
                continue;
            }

            askMethod = m;
            break;
        }

        if (askMethod != null) {
            askMethod.setAccessible(true);
            try {
                result = (Boolean) askMethod.invoke(obj, new Object[]{});
            } catch (IllegalAccessException ex) {
                Logger.getLogger(VisualObject.class.getName()).
                        log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(VisualObject.class.getName()).
                        log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(VisualObject.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }

    private void initMenu() {

        ObjectInfo info = objectRepresentation.getDescription().getInfo();

        final VisualCanvas vCanvas = (VisualCanvas) getMainCanvas();

        getPopup().addSeparator();

        JMenuItem item;

        item = new JMenuItem("New Instance");

        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println("objRep:" + getObjectRepresentation());
                System.out.println("id:" + getObjectRepresentation().getObjectID());
                System.out.println("obj:" + vCanvas.getInspector().getObject(
                        getObjectRepresentation().getObjectID()));

                int yOffset = vCanvas.getStyle().getBaseValues().
                        getFloat(CanvasWindow.SHADOW_WIDTH_KEY).intValue();

                ComponentUtil.addObject(vCanvas.getInspector().
                        getObject(getObjectRepresentation().
                                getObjectID()).getClass(), vCanvas,
                        new Point(getLocation().x + 20,
                                getLocation().y + yOffset + 20),
                        false);
            }
        });

        getPopup().add(item);

        if (info == null || info.multipleViews()) {

            item = new JMenuItem("New Reference");

            item.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    System.out.println("objRep:" + getObjectRepresentation());
                    System.out.println("id:" + getObjectRepresentation().getObjectID());
                    System.out.println("obj:" + vCanvas.getInspector().getObject(
                            getObjectRepresentation().getObjectID()));

                    VisualObject vObj = vCanvas.addObject(
                            vCanvas.getInspector().getObject(
                                    getObjectRepresentation().getObjectID()),
                            new Point(getLocation().x + 20,
                                    getLocation().y + 20));

                    vObj.addSourceIcon();

                }
            });

            getPopup().add(item);

        } // end if

        getPopup().add(item);

        getPopup().addSeparator();

        item = new JMenuItem("Reset Instance");

        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                Class<?> cls = vCanvas.getInspector().getObjectClass(
                        getObjectRepresentation());

                // reload class
                cls = vCanvas.getClassLoader().reloadClass(cls);

                try {
                    vCanvas.getInspector().replaceAllObjects(cls,
                            new InstanceCreator(vCanvas)).getFirst();
                } catch (InterfaceChangedException ex) {
                    vCanvas.getMessageBox().addMessage(
                            "Cannot replace instance:",
                            ">> this visual instance cannot be replaced. "
                            + "Reason unknown!", MessageType.ERROR);
                }
            }
        });

        getPopup().add(item);

        initControlflowMenuActions();
    }

    private void initControlflowMenuActions() {

        getPopup().addSeparator();

        JMenuItem item = new JMenuItem("Reconnect Controlflow of Selected Objects");

        item.addActionListener((ActionEvent e) -> {
            createControlFlowByPositionOfSelectedObjects();
        });

        getPopup().add(item);

        item = new JMenuItem("Remove from ControlFlow");

        item.addActionListener((ActionEvent e) -> {
            closeControlFlowConnection();

            getMainCanvas().getControlFlowConnections().
                    removeAllWith(VisualObject.this);
        });

        getPopup().add(item);
    }

    private void createControlFlowByPositionOfSelectedObjects() {
        List<VisualObject> selectedObjects = new ArrayList<VisualObject>();

        for (Selectable s : getMainCanvas().getClipBoard()) {
            if ((s instanceof VisualObject)
                    && !(s instanceof InputObject)
                    && !(s instanceof OutputObject)) {
                selectedObjects.add((VisualObject) s);
            }
        }

        // no objects to connect
        if (selectedObjects.size() < 2) {
            return;
        }

        Comparator<VisualObject> compareByDistance
                = (VisualObject o1, VisualObject o2) -> {
                    Point o1Loc = o1.getLocation();
                    Point o2Loc = o2.getLocation();

                    int dist1Squared = o1Loc.x * o1Loc.x + o1Loc.y * o1Loc.y;
                    int dist2Squared = o2Loc.x * o2Loc.x + o2Loc.y * o2Loc.y;

                    if (dist1Squared < dist2Squared) {
                        return -1;
                    }

                    if (dist1Squared > dist2Squared) {
                        return 1;
                    }

                    if (o1Loc.x < o2Loc.x) {
                        return -1;
                    }
                    if (o1Loc.x > o2Loc.x) {
                        return +1;
                    }

                    if (o1Loc.y < o2Loc.y) {
                        return -1;
                    }
                    if (o1Loc.y > o2Loc.y) {
                        return +1;
                    }

                    return 0;
                };

        Collections.sort(selectedObjects, compareByDistance);

        // remove previous connections
        for (int i = 0; i < selectedObjects.size(); i++) {

            ControlFlowConnector selectedIn = selectedObjects.get(i).
                    getObjectRepresentation().getControlFlowInput();
            ControlFlowConnector selectedOut = selectedObjects.get(i).
                    getObjectRepresentation().getControlFlowOutput();

            if (i > 0) {
                getMainCanvas().getControlFlowConnections().
                        removeAllWith(selectedIn);
            }

            if (i < selectedObjects.size() - 1) {
                getMainCanvas().getControlFlowConnections().
                        removeAllWith(selectedOut);
            }

        }

        VisualObject prev = null;

        for (VisualObject vObj : selectedObjects) {

            if (prev != null) {
                getMainCanvas().getControlFlowConnections().add(
                        prev.getObjectRepresentation().getControlFlowOutput(),
                        vObj.getObjectRepresentation().getControlFlowInput());
            }

            prev = vObj;
        }
    }

    private void updateCode() {
        try {
            VisualCanvas mainCanvas
                    = (VisualCanvas) getMainCanvas();
            // gather code
            int objID = getObjectRepresentation().getObjectID();
            Object obj = mainCanvas.getInspector().getObject(objID);
            code
                    = mainCanvas.getCodes().getByName(
                            obj.getClass().getName());
        } catch (Exception ex) {
            // cannot add icon
            ex.printStackTrace(System.err);
        }
    }

    /**
     * Adds a source icon to this visual object.
     *
     */
    public void addSourceIcon() {

        if (getMainCanvas().getCapabilityManager().
                isCapable(CanvasCapabilities.ALLOW_EDIT)) {

            updateCode();

            if (sourceIcon == null) {

                final VisualCanvas vCanvas
                        = (VisualCanvas) getMainCanvas();
                int objID = getObjectRepresentation().getObjectID();
                final String className = vCanvas.getInspector().
                        getObject(objID).getClass().getName();
                final Class<?> componentClass = vCanvas.getInspector().
                        getObject(objID).getClass();

                sourceIcon = new EditSourceIcon(getMainCanvas());
                sourceIcon.setActionListener(new CanvasActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        VisualCanvas mainCanvas
                                = (VisualCanvas) getMainCanvas();

                        updateCode();

                        if (code == null
                                && ComponentUtil.isVisualSessionComponent(
                                        componentClass)) {
                            try {
//                                System.out.println(">> adding source icon: visual");
                                vCanvas.getProjectController().open(className);

                            } catch (IOException ex) {
                                Logger.getLogger(VisualObject.class.getName()).
                                        log(Level.SEVERE, null, ex);
                            } finally {
                                return; // finished
                            }
                        } else if (code == null
                                && ComponentUtil.isCodeSessionComponent(
                                        componentClass)) {
                            try {
//                                System.out.println(">> adding source icon: code");
                                File codeFile
                                        = vCanvas.getProjectController().
                                                getProject().
                                                getSourceFileByEntryName(
                                                        componentClass.getName());

                                TextLoader loader = new TextLoader();
                                String text = (String) loader.loadFile(codeFile);

                                GroovyCodeEditorComponent editComponent
                                        = new GroovyCodeEditorComponent(text);

                                CanvasWindow window
                                        = vCanvas.addObject(editComponent);

                                int winPosX = getX()
                                        - (window.getWidth() - getWidth()) / 2;
                                int winPosY = getY();

                                window.setLocation(winPosX, winPosY);

//                                Class<?> componentClass =
//                                        vCanvas.getInspector().
//                                        getObjectClass(objectRepresentation);
//
//                                String title = "Groovy Code: "
//                                        + vCanvas.getProjectController().
//                                        getProject().
//                                        getEntryNameWithoutDefaultPackage(
//                                        componentClass.getName());
//                                
//                                window.setTitle(title);
                            } catch (IOException ex) {
                                Logger.getLogger(VisualObject.class.getName()).
                                        log(Level.SEVERE, null, ex);
                            } finally {
                                return; // finished
                            }
                        }

//                        System.out.println(">> adding source icon: deprecated");
//
//                        System.out.println("CODE: " + code);

                        try {
                            String componentName = componentClass.getName();
                            File codeFile
                                    = mainCanvas.getProjectController().getProject().
                                            getSourceFileByEntryName(componentName);

                            TextLoader loader = new TextLoader();
                            String code = (String) loader.loadFile(codeFile);

                            GroovyCodeEditorComponent editWindow
                                    = new GroovyCodeEditorComponent(code);

                            CanvasWindow window
                                    = mainCanvas.addObject(editWindow);

                            int winPosX = getX()
                                    - (window.getWidth() - getWidth()) / 2;
                            int winPosY = getY();

                            window.setLocation(winPosX, winPosY);

//                        String title = "Groovy Code: " 
//                                +tree.canvas.getProjectController().
//                                getProject().
//                                getEntryNameWithoutDefaultPackage(componentName);
//                        
//                        w.setTitle(title);
                        } catch (IOException ex) {
                            Logger.getLogger(
                                    ComponentCellRenderer.class.getName()).
                                    log(Level.SEVERE, null, ex);
                        }

//                        Class<?> componentClass =
//                                vCanvas.getInspector().
//                                getObjectClass(objectRepresentation);
//
//                        String title = "Groovy Code: "
//                                + vCanvas.getProjectController().
//                                getProject().
//                                getEntryNameWithoutDefaultPackage(
//                                componentClass.getName());
//                        
//                        window.setTitle(title);
                    }
                });

                if (code != null
                        || ComponentUtil.isVisualSessionComponent(componentClass)
                        || ComponentUtil.isCodeSessionComponent(componentClass)) {

                    sourceIcon.setMinimumSize(new Dimension(20, 20));
                    sourceIcon.setPreferredSize(new Dimension(20, 20));
                    sourceIcon.setMaximumSize(new Dimension(20, 20));

                    addIcon(sourceIcon);
                }

            }
        } else {
            throw new NotCapableOfException("Not capable of editing source"
                    + " code! Thus, no source code icon will be added. ");
        }
    }

    public void removeSourceIcon() {
        if (sourceIcon != null) {
            removeIcon(sourceIcon);
            sourceIcon = null;
        }
    }

    @Override
    public WindowContentProvider getContentProvider() {
        try {
            setContentProvider(new AbstractObjectRepresentation(this));
        } catch (NotSerializableException ex) {
//            getMainCanvas().getMessageBox().addMessage("", TOOL_TIP_TEXT_KEY, type);
        }
        return super.getContentProvider();
    }

//    /**
//     * Returns all connectors associated with this object.
//     * @return all connectors associated with this object
//     */
//    @Override
//    public ArrayList<Connector> getConnectors() {
//        return objectRepresentation.getConnectors();
//    }
    /**
     * Returns selected connector.
     *
     * @return selected connector
     */
    public Connector getSelected() {

//        for (Connector c : objectRepresentation.getConnectors()) {
//            if (c.isSelected()) {
//                return c;
//            }
//
//        }
//
//        for (ControlFlowConnector c : controlflowConnectors) {
//           if (c.isSelected()) {
//               return c;
//           }
//        }
        for (Connector c : getConnectors()) {
            if (c.isSelected()) {
                return c;
            }
        }

        return null;
    }

    /**
     * Unselect all connectors.
     */
    public void unselectConnectors() {
//        if (objectRepresentation.getConnectors() != null) {
//            for (Connector c : objectRepresentation.getConnectors()) {
//                c.setSelected(false);
//            }
//
//        }
//
//        for (ControlFlowConnector c : controlflowConnectors) {
//            c.setSelected(false);
//        }

        for (Connector c : getConnectors()) {
            c.setSelected(false);
        }
    }

    /**
     * Returns the object representation of this object.
     *
     * @return the object representation of this object
     */
    public DefaultObjectRepresentation getObjectRepresentation() {
        return objectRepresentation;
    }

    @Override
    public void minimize() {

//        for (Component c : getObjectRepresentation().getComponents()) {
//            c.setPreferredSize(new Dimension(c.getWidth(), 0));
//            c.setMaximumSize(new Dimension(c.getWidth(), 0));
//            if (c instanceof JComponent) {
//                JComponent jC = (JComponent) c;
//                jC.revalidate();
//            }
//        }
        getObjectRepresentation().revalidate();

        super.minimize();
    }

    @Override
    public void maximize() {

//        for (Component c : getObjectRepresentation().getComponents()) {
//            c.setPreferredSize(null);
//            c.setMaximumSize(null);
//             if (c instanceof JComponent) {
//                JComponent jC = (JComponent) c;
//                jC.revalidate();
//            }
//        }
        getObjectRepresentation().revalidate();
        super.maximize();
    }

    @Override
    public void dispose() {

        if (isDisposed()) {
            return;
        }

        try {
            getMainCanvas().getMessageBox().removeObjectReferences(
                    getObjectRepresentation());

            getObjectRepresentation().dispose();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        } finally {
            super.dispose();
        }
    }

    @Override
    protected void defineCapabilities() {
        super.defineCapabilities();
        if (getMainCanvas().getCapabilityManager().
                isCapable(CanvasCapabilities.ALLOW_EDIT)) {
            addSourceIcon();

        } else {
            removeSourceIcon();
        }
    }
}
