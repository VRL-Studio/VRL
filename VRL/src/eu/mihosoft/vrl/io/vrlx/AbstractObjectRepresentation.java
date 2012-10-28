/* 
 * AbstractObjectRepresentation.java
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

package eu.mihosoft.vrl.io.vrlx;

import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.io.Base64;
import eu.mihosoft.vrl.reflection.CallOptionEvaluationTask;
import eu.mihosoft.vrl.reflection.CallOptionsEvaluator;
import eu.mihosoft.vrl.reflection.ComponentUtil;
import eu.mihosoft.vrl.reflection.DefaultMethodRepresentation;
import eu.mihosoft.vrl.reflection.DefaultObjectRepresentation;
import eu.mihosoft.vrl.reflection.MethodDescription;
import eu.mihosoft.vrl.reflection.MethodIdentifier;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.reflection.VisualObject;
import eu.mihosoft.vrl.reflection.VisualObjectInspector;
import eu.mihosoft.vrl.visual.CanvasWindow;
import eu.mihosoft.vrl.visual.IDArrayList;
import eu.mihosoft.vrl.visual.IDTable;
import eu.mihosoft.vrl.visual.Message;
import eu.mihosoft.vrl.visual.MessageType;
import eu.mihosoft.vrl.visual.WindowContentProvider;
import java.awt.Component;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract representation of an object. This class is only used for XML
 * serialization.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class AbstractObjectRepresentation
        extends ArrayList<AbstractMethodRepresentation>
        implements WindowContentProvider {

    private static final long serialVersionUID = 6770317538206416765L;
    /**
     * serialized object (binary serialization, base64 with gz compression)
     */
    private String objectData;
    /**
     * the id of the object
     */
    private Integer objID;
    private Integer visualID;
    private IDArrayList<IDTable> connectorIDTables;
    private ArrayList<MethodIdentifier> methodOrder;

    /**
     * Constructor.
     */
    public AbstractObjectRepresentation() {
        //
    }

    /**
     * Constructor.
     * <p>
     * Creates a new instance from visual object.
     * </p>
     * @param obj the visual object that is to be used for instanciation
     * @throws NotSerializableException
     */
    public AbstractObjectRepresentation(VisualObject obj)
            throws NotSerializableException {

        objID = obj.getObjectRepresentation().getObjectID();
        visualID = obj.getObjectRepresentation().getID();

        VisualCanvas visualCanvas = (VisualCanvas) obj.getMainCanvas();

        Object result = visualCanvas.getInspector().getObject(objID);

        if (result instanceof Serializable) {
            Serializable serializable = (Serializable) result;
            objectData = Base64.encodeObject(serializable, Base64.GZIP);

            if (objectData == null) {
                throw new NotSerializableException("Object of class "
                        + result.getClass() + " not serializable!");
            }
        }

        Collection<DefaultMethodRepresentation> methods =
                obj.getObjectRepresentation().getInvocationList();
        for (DefaultMethodRepresentation mRep : methods) {

            // if mRep is not a reference method, add it
            if (!mRep.isReferenceMethod() && !mRep.isCustomReferenceMethod()) {
                this.add(new AbstractMethodRepresentation(mRep));
            }
        }

        setMethodOrder(obj.getObjectRepresentation().getMethodOrder());

        setConnectorIDTables(
                obj.getObjectRepresentation().getConnectorIDTables());

//        System.out.println("IDTable: " + getConnectorIDTables());

    }

    /**
     * Assigns properties to object representation. This method is used to
     * assign properties loaded from XML session file.
     * @param o the o representation that is associated with this abstract
     * object representation
     */
    public synchronized void assignProperties(VisualCanvas canvas,
            final DefaultObjectRepresentation o) {

        for (AbstractMethodRepresentation m : this) {

            // get the correct method representation - not by id but by
            // signature
//            DefaultMethodRepresentation method =
//                    o.getMethodBySignature(m.getMethodName(),
//                    m.getParameterTypeNames());

            MethodDescription method =
                    o.getMethodDescriptionBySignature(m.getMethodName(),
                    m.getParameterTypeNames());


            if (method == null) {

                Class<?> cls = canvas.getInspector().getObject(o).
                        getClass();

                String msg = ">> Warning: Cannot assign properties for"
                        + " method \"" + cls.getName()
                        + "." + m.getMethodName()
                        + "()\" because the interface of the method has changed"
                        + " or the method has been removed.";
                System.err.println(msg);
                canvas.getMessageBox().addMessage(
                        "Cannot assign method properties:",
                        msg, o, MessageType.WARNING);
                continue;
            }

            // the id might be different so we change it to the correct value
            method.setMethodID(m.getMethodId());

            DefaultMethodRepresentation mRep = null;

            if (m.getVisibility()
                    && (getMethodOrder() == null || getMethodOrder().
                    contains(new MethodIdentifier(method, visualID)))) {
                mRep = o.addMethodToView(method);
            }
//            else {
//                o.removeMethodFromView(method);
//            }


            if (mRep != null) {
                m.assignProperties(mRep);
            }
            
        } // end for m

        String missingMethods = "";

        ArrayList<Integer> indicesToDelete = new ArrayList<Integer>();

        // check that all methods in order do exist:
        for (int i = 0; i < getMethodOrder().size(); i++) {
            MethodIdentifier mID = getMethodOrder().get(i);
            if (o.getMethodByIdentifier(mID) == null) {
                if (i > 0) {
                    missingMethods += ", ";
                }
                missingMethods += mID.getMethodName() + "()";
                indicesToDelete.add(i);
            }
        }

        // delete misssing methods
        for (Integer i : indicesToDelete) {
            getMethodOrder().remove(i);
        }

        o.setMethodOrder(getMethodOrder());

        if (!missingMethods.isEmpty()) {
            String msg = "The interface of component "
                    + Message.EMPHASIZE_BEGIN + o.getName()
                    + Message.EMPHASIZE_END + " has changed.<br><br>"
                    + "The following methods are no longer available: "
                    + missingMethods + "<br><br>"
                    + "<b>Note:</b> The above listed methods have been removed"
                    + " from the controlflow."
                    + " Please check the controlflow of this"
                    + " component!";
            System.err.println(msg);
            canvas.getMessageBox().addMessage(
                    "Interface changed:",
                    msg, o, MessageType.WARNING);
        }
    }

    @Override
    public ArrayList<Component> getContent(Canvas mainCanvas) {
        ArrayList<Component> result = new ArrayList<Component>();

        if (mainCanvas instanceof VisualCanvas) {
            VisualCanvas visualCanvas = (VisualCanvas) mainCanvas;
            VisualObjectInspector inspector = visualCanvas.getInspector();


            // check if object has already been added
            Object object = inspector.getObject(getObjID());

            // if not deserialize the object and add it to the inspector
            if (object == null) {
                object = decodeObject(visualCanvas);
                inspector.addObject(object, getObjID());
            }

            DefaultObjectRepresentation oRep =
                    inspector.generateObjectRepresentation(object,
                    getConnectorIDTables(), getVisualID());

            result.add(oRep);
        }

        return result;
    }

    /**
     * Decodes and returns the object that is saved as compressed base64 string
     * using the class loader of the specified canvas object.
     * @param mainCanvas the canvas object that is used for class loading
     * @return the object that is saved as compressed base64 string
     */
    public Object decodeObject(VisualCanvas mainCanvas) {
        Object result = null;
        if (objectData != null) {
            result = Base64.decodeToObject(
                    objectData, mainCanvas.getClassLoader());
        }
        return result;
    }

    /**
     * Returns the base64 encoded object data as string.
     * @return the base64 encoded object data as string
     */
    public String getObjectData() {
        return objectData;
    }

    /**
     * Sets the object data string.
     * @param objectData the object data to set
     */
    public void setObjectData(String objectData) {
        this.objectData = objectData;
    }

    /**
     * Returns the object id.
     * @return the object id
     */
    public Integer getObjID() {
        return objID;
    }

    /**
     * Defines the object id.
     * @param objID the object id to set
     */
    public void setObjID(Integer objID) {
        this.objID = objID;
    }

    /**
     * @return the connectorIDTables
     */
    public IDArrayList<IDTable> getConnectorIDTables() {
        return connectorIDTables;
    }

    /**
     * @param connectorIDTables the connectorIDTables to set
     */
    public final void setConnectorIDTables(
            IDArrayList<IDTable> connectorIDTables) {
        this.connectorIDTables = connectorIDTables;
    }

    /**
     * @return the visualID
     */
    public Integer getVisualID() {
        return visualID;
    }

    /**
     * @param visualID the visualID to set
     */
    public void setVisualID(Integer visualID) {
        this.visualID = visualID;
    }

    @Override
    public CanvasWindow newWindow(
            Canvas canvas, AbstractWindow abstractWindow) {
        CanvasWindow window = null;

        VisualCanvas mainCanvas = (VisualCanvas) canvas;

        window = new VisualObject(
                this, (VisualCanvas) mainCanvas);

        mainCanvas.getWindows().addWithID(window, abstractWindow.getObjID());

        window.setLocation(abstractWindow.getLocation());
        window.setTitleBarSize(abstractWindow.getTitleBarSize());

        VisualObject vObj = (VisualObject) window;

        this.assignProperties(mainCanvas,
                vObj.getObjectRepresentation());

        // add source icon if compiled from abstract source
        int inspectorID = vObj.getObjectRepresentation().getObjectID();

//        Object o = mainCanvas.getInspector().getObject(inspectorID);

//        AbstractCode code =
//                mainCanvas.getCodes().getByClass(o.getClass());

//        if (code != null || ComponentUtil.isVisualSessionComponent(o.getClass())) {
            try {
                vObj.addSourceIcon();
            } catch (Exception ex) {
            }
//        }

        if (!abstractWindow.getVisible()) {
            window.hideWindow();
        }

//        CallOptionsEvaluator callOptionsEvaluator =
//                new CallOptionsEvaluator(mainCanvas.getInspector());
//
//
//        mainCanvas.getCallOptionEvaluationTasks().add(
//                new CallOptionEvaluationTask(
//                mainCanvas.getInspector().getObjectDescription(o),
//                getVisualID(),
//                callOptionsEvaluator));

        return window;
    }

    /**
     * @return the methodOrder
     */
    public ArrayList<MethodIdentifier> getMethodOrder() {
        return methodOrder;
    }

    /**
     * @param methodOrder the methodOrder to set
     */
    public final void setMethodOrder(ArrayList<MethodIdentifier> methodOrder) {
        this.methodOrder = methodOrder;
    }
}
