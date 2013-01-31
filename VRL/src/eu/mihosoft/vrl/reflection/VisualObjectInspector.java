/* 
 * VisualObjectInspector.java
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
package eu.mihosoft.vrl.reflection;

import eu.mihosoft.vrl.asm.CompilationUnit;
import eu.mihosoft.vrl.lang.InstanceCreator;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.lang.VWorkflowException;
import eu.mihosoft.vrl.lang.visual.ClassInfoObject;
import eu.mihosoft.vrl.lang.visual.InputObject;
import eu.mihosoft.vrl.lang.visual.OutputObject;
import eu.mihosoft.vrl.lang.visual.StartObject;
import eu.mihosoft.vrl.lang.visual.StopObject;
import eu.mihosoft.vrl.system.VParamUtil;
import eu.mihosoft.vrl.system.VRL;
import eu.mihosoft.vrl.visual.CanvasWindow;
import eu.mihosoft.vrl.visual.Connector;
import eu.mihosoft.vrl.visual.IDArrayList;
import eu.mihosoft.vrl.visual.IDArrayMap;
import eu.mihosoft.vrl.visual.IDTable;
import eu.mihosoft.vrl.visual.MessageBox;
import eu.mihosoft.vrl.visual.MessageType;
import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents an object inspector that is connected to a visual
 * representation. Adding objects and invoking methods will have an effect on
 * the visual representation.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VisualObjectInspector extends ObjectInspector {

    private static final long serialVersionUID = 1674189302728167206L;
    private VisualCanvas mainCanvas;
    private CallOptionsEvaluator callOptionsEvaluator;
    private IDArrayMap<DefaultObjectRepresentation> visualizations =
            new IDArrayMap<DefaultObjectRepresentation>();

    /**
     * Constructor.
     *
     * @param mainCanvas canvas the inspector is connected to
     */
    public VisualObjectInspector(VisualCanvas mainCanvas) {
        this.mainCanvas = mainCanvas;
        callOptionsEvaluator = new CallOptionsEvaluator(this);
    }

    /**
     * Returns all object representations associated with the specified object.
     *
     * @param o the object
     * @return all object representations associated with the specified object
     */
    public IDArrayList<DefaultObjectRepresentation> getObjectRepresentationsByReference(Object o) {
        return getVisualizations().getValues(o);
    }

    /**
     * Returns all object representations associated with the specified object.
     *
     * @param oDesc the object description
     * @return all object representations associated with the specified object
     */
    public IDArrayList<DefaultObjectRepresentation> getObjectRepresentations(
            ObjectDescription oDesc) {
        return getVisualizations().getValues(
                getMainCanvas().getInspector().getObject(oDesc.getID()));
    }

    /**
     * Returns all object representations associated with the specified object.
     *
     * @param objID the object id
     * @return all object representations associated with the specified object
     */
    public IDArrayList<DefaultObjectRepresentation> getObjectRepresentations(
            int objID) {
        return getVisualizations().getValues(
                getMainCanvas().getInspector().getObject(objID));
    }

    /**
     * Returns an object representation specified by id.
     *
     * @param objID object id
     * @param visualID visual id
     * @return the requested object representation or <code>null</code> if no
     * such object representation exists
     */
    public DefaultObjectRepresentation getObjectRepresentation(
            int objID, int visualID) {
        return getObjectRepresentations(objID).getById(visualID);
    }

    /**
     * Returns an object representation specified by object identifier.
     *
     * @param o object identifier
     * @return the requested object representation or <code>null</code> if no
     * such object representation exists
     */
    public DefaultObjectRepresentation getObjectRepresentation(ObjectIdentifier o) {
        return getObjectRepresentation(o.getObjectID(), o.getVisualID());
    }

    /**
     * Returns an object specified by object representation.
     *
     * @param oRep object representation
     * @return the requested object or <code>null</code> if no such object
     * exists
     */
    public Object getObject(DefaultObjectRepresentation oRep) {
        return getObject(oRep.getObjectID());
    }

    /**
     * Returns the class of the instance specified by the given object
     * representation.
     *
     * @param oRep object representation
     * @return the class of the instance specified by the given object
     * representation or <code>null</code> if no such class exists
     */
    public Class<?> getObjectClass(DefaultObjectRepresentation oRep) {
        Object o = getObject(oRep);

        Class cls = null;

        if (o != null) {
            cls = o.getClass();
        }

        return cls;
    }

    /**
     * Returns an object specified by method identifier.
     *
     * @param oRep method identifier
     * @return the requested object or <code>null</code> if no such object
     * exists
     */
    public Object getObject(MethodIdentifier mId) {
        return getObject(mId.getObjectID());
    }

    /**
     * Returns an object representation specified by method identifier.
     *
     * @param m method identifier
     * @return the requested object representation or <code>null</code> if no
     * such object representation exists
     */
    public DefaultObjectRepresentation getObjectRepresentation(
            MethodIdentifier m) {
        return getObjectRepresentations(
                m.getObjectID()).getById(m.getVisualID());
    }

    /**
     * Returns a method representation by method description.
     *
     * @param desc a description of the requested method representation
     * @return the requested method representation if a representation as
     * requested with <code>desc</code> exists; returns <code>false</code>
     * otherwise.
     */
    public ArrayList<DefaultMethodRepresentation> getMethodRepresentations(
            MethodDescription desc) {
        ArrayList<DefaultMethodRepresentation> result =
                new ArrayList<DefaultMethodRepresentation>();

        ArrayList<DefaultObjectRepresentation> oReps =
                getObjectRepresentations(desc.getObjectID());

        for (DefaultObjectRepresentation oRep : oReps) {
            for (DefaultMethodRepresentation method : oRep.getMethods()) {
                if (method.getDescription().getMethodID() == desc.getMethodID()) {
                    result.add(method);
                }
            }
        }

        return result;
    }

    /**
     * Returns a method representation by method identifier.
     *
     * @param desc a method identifier of the requested method representation
     * @return the requested method representation if a representation as
     * requested with <code>desc</code> exists; returns <code>false</code>
     * otherwise
     */
    public DefaultMethodRepresentation getMethodRepresentation(MethodIdentifier desc) {

        IDArrayList<DefaultObjectRepresentation> oReps =
                getObjectRepresentations(desc.getObjectID());

        DefaultMethodRepresentation result = null;

//        System.out.println("SIZE: " + oReps.size());
//
//        System.out.println("ID: " + desc.getVisualID());
//
//        System.out.println("OREP: " + oReps.getById(desc.getVisualID()));

        for (DefaultMethodRepresentation method : oReps.getById(desc.getVisualID()).getMethods()) {
//            if (method.getDescription().getMethodID() == desc.getMethodID()) {
//                result = method;
//            }
            if (new MethodIdentifier(method.getDescription(), desc.getVisualID()).equals(desc)) {
                result = method;
            }
        }

        return result;
    }

//    /**
//     * Returns the parent CanvasWindow of a given object.
//     * @param o the object
//     * @return the canvas window <code>null</code> if such a
//     *         window could not be found
//     */
//    public VisualObject getCanvasWindows(Object o) {
//
//        int id = getMainCanvas().getInspector().getCanvasWindowID(o);
//
//        VisualObject vObj = (VisualObject) getMainCanvas().getWindows().getById(id);
//
//        return vObj;
//    }
//    /**
//     * Returns an object representation.
//     * @param o the object of the object representation that is to be returned
//     * @return the object representation or <code>null</code> if such an
//     *         object representation could not be found
//     */
//    public DefaultObjectRepresentation getObjectRepresentationByReference(Object o) {
//
//        VisualObject vObj = getCanvasWindow(o);
//
//        DefaultObjectRepresentation oRep = vObj.getObjectRepresentation();
//
//        return oRep;
//    }
    public static DefaultMethodRepresentation chooseMethodRepresentation(
            ArrayList<DefaultMethodRepresentation> methods, int visualID) {
        DefaultMethodRepresentation result = null;

        for (DefaultMethodRepresentation mRep : methods) {
            if (mRep.getParentObject().getID() == visualID) {
                result = mRep;
            }
        }

        return result;
    }

    /**
     * Returns a method representation by method name and parameter types.
     *
     * @param o the object the method belongs to
     * @param methodName the method name
     * @param paramTypes the parameter types of the method
     * @return the method representation or <code>null</code> if such a method
     * representation could not be found
     */
    public ArrayList<DefaultMethodRepresentation> getMethodRepresentations(Object o,
            String methodName, Class<?>[] paramTypes) {

        ArrayList<DefaultObjectRepresentation> oReps =
                getObjectRepresentationsByReference(o);

        ArrayList<DefaultMethodRepresentation> result =
                new ArrayList<DefaultMethodRepresentation>();

        for (DefaultObjectRepresentation oRep : oReps) {

            for (DefaultMethodRepresentation mRep : oRep.getMethods()) {

                boolean paramTypesAreEqual =
                        mRep.getDescription().getParameterTypes().length
                        == paramTypes.length;

                if (paramTypesAreEqual) {

                    for (int i = 0; i < paramTypes.length; i++) {
                        Class<?> class1 = paramTypes[i];
                        Class<?> class2 =
                                mRep.getDescription().getParameterTypes()[i];

                        if (!class1.equals(class2)) {
                            paramTypesAreEqual = false;
                            break;
                        }
                    }
                }

                if (mRep.getDescription().getMethodName().equals(methodName)
                        && paramTypesAreEqual) {
                    result.add(mRep);
                    break;
                }
            }
        } // end for all oReps

        return result;
    }

    /**
     * <p> Returns an object representation for the specified object. The
     * specified object has to be added to the inspector before calling this
     * method. Returns
     * <code>null</code> otherwise. </p> <p> <b>Note:</b> this method should
     * only be used to load from file or in other rare cases where restoring
     * previously assigned ids is required. </p>
     *
     * @param o the object to represent
     * @param visualID the visual id that shall be assigned to the object
     * representation, if <code>null</code> it will be ignored and a new one
     * will be created
     * @param connectorIDTables the connector id tables to restore previosly
     * assigned ids
     * @return the object representation of the specified object or
     * <code>null</code> if the specified object has not been added to this
     * object inspector before calling this method
     */
    public DefaultObjectRepresentation generateObjectRepresentation(Object o,
            IDArrayList<IDTable> connectorIDTables, Integer visualID) {
        DefaultObjectRepresentation result = null;

        ObjectDescription oDesc =
                getMainCanvas().getInspector().getObjectDescription(o);

        if (oDesc != null) {

            result = new DefaultObjectRepresentation(
                    getMainCanvas().getInspector(),
                    getMainCanvas().getTypeFactory(),
                    oDesc, getMainCanvas(), connectorIDTables);

            if (visualID == null) {
                getVisualizations().add(o, result);
            } else {
                getVisualizations().addWithID(o, result, visualID);
            }
        }

        return result;
    }

    /**
     * <p> Replaces the instance of the specified object representation with a
     * specified instance. Returns
     * <code>null</code> if replacement cannot be performed. </p>
     *
     * @param newInstance the instance to use
     * @param oldInstance the old instance (optional, may be null)
     *
     * @return the object representation or <code>null</code> if replacement
     * cannot be performed
     */
    private DefaultObjectRepresentation replaceInstance(
            DefaultObjectRepresentation oRep,
            Object newInstance, Object oldInstance) {

        System.out.println(">> Inspector: replacing instance with ID "
                + oRep.getObjectID());

        DefaultObjectRepresentation result = null;

        ObjectDescription oDesc =
                getMainCanvas().getInspector().
                getObjectDescription(newInstance);

        // if oldinstance was not specified search it by id
        if (oldInstance == null) {
            oldInstance = getObject(oRep.getObjectID());
        }

        if (oDesc == null) {
            System.out.println(
                    " --> adding object because it cannot be found: "
                    + newInstance);

            addObject(newInstance);

            oDesc =
                    getMainCanvas().getInspector().
                    getObjectDescription(newInstance);
        }

        if (oDesc == null) {
            throw new IllegalStateException("new instance not available");
        }

        if (oldInstance != newInstance) {

            getVisualizations().get(oldInstance).
                    remove(oRep);

            if (getVisualizations().get(oldInstance).isEmpty()) {
                System.out.println(" --> Inspector: removing object "
                        + oRep.getObjectID());
                removeObject(oRep.getObjectID());
            }

            Object returnValue = null;

            boolean hasNonCustomReferenceMethod =
                    oRep.getReferenceMethod() != null
                    && oRep.getReferenceMethod().isReferenceMethod();

            // get param value of reference method
            if (hasNonCustomReferenceMethod) {
//                returnValue = oRep.getReferenceMethod().
//                        getDescription().getReturnValue();

                returnValue = null;
            }

            oRep.setDescription(oDesc);

            // get param value of reference method
            if (hasNonCustomReferenceMethod) {
                oRep.getReferenceMethod().
                        getDescription().setReturnValue(returnValue);
            }

            getVisualizations().add(newInstance, oRep);

            result = oRep;
        } else {
            System.out.println(" --> Inspector: oldinstance == newInstance");
        }

        return result;
    }

    @Override
    public synchronized void replaceObject(Object newObj, int objID)
            throws InterfaceChangedException {

        Object oldObject = getObject(objID);

        super.replaceObject(newObj, objID);

//        IDArrayList<DefaultObjectRepresentation> oldVisualizations =
//                visualizations.getValues(oldObject);
//
//        // remove all mappings with old key
//        visualizations.remove(oldObject);
//
//        // add old mappings with new key
//        for (DefaultObjectRepresentation oRep : oldVisualizations) {
//            visualizations.add(newObj, oRep);
//        }

        Collection<DefaultObjectRepresentation> oReps =
                getObjectRepresentationsByReference(oldObject);

        Collection<DefaultObjectRepresentation> oldOReps =
                new ArrayList<DefaultObjectRepresentation>(oReps);

        for (DefaultObjectRepresentation oRep : oldOReps) {
            replaceInstance(oRep, newObj, oldObject);
        }

    }

    /**
     * Returns an object representation for an object. The specified object has
     * to be added to the inspector before calling this method. Returns
     * <code>null</code> otherwise.
     *
     * @param o the object to represent
     * @return the object representation of the specified object or
     * <code>null</code> if the object has not been added to this object
     * inspector before calling this method
     */
    public DefaultObjectRepresentation generateObjectRepresentation(Object o) {
        return generateObjectRepresentation(o, null, null);
    }

    /**
     * Returns an object representation for an object. The description of the
     * specified object has to be added to the inspector before calling this
     * method. Returns
     * <code>null</code> otherwise.
     *
     * @param o description of the object to represent
     * @return the object representation of the specified object or
     * <code>null</code> if the object has not been added to this object
     * inspector before calling this method
     */
    public DefaultObjectRepresentation generateObjectRepresentation(
            ObjectDescription oDesc) {
        return generateObjectRepresentation(getObject(oDesc.getID()));
    }

    @Override
    public void removeObject(int objID) {
        getVisualizations().remove(getObject(objID));
        super.removeObject(objID);
    }

    /**
     * Removes an object representation from this inspector. If an object holds
     * no representations it will be removed from this inspector.
     *
     * @param objID id of the object
     * @param visualID id of the object representation to remove
     */
    public void removeObjectRepresentation(int objID, int visualID) {
        Object o = getObject(objID);
        getVisualizations().remove(o, visualID);
        if (getVisualizations().getValues(o).isEmpty()) {
            removeObject(objID);
        }
    }

    /**
     * Invokes method without refreshing the visual representation of the
     * method.
     *
     * @param methodDescription the description of the method that is to be
     * invoked.
     * @param visualID the visual id
     */
    public void invoke(MethodDescription methodDescription, int visualID)
            throws InvocationTargetException {

        VParamUtil.throwIfNull(methodDescription);

        invoke(methodDescription, visualID, false);
    }

    /**
     * Invokes method, optionally refreshes the visual representation of the
     * method.
     *
     * @param methodDescription the description of the method that is to be
     * invoked.
     * @param the visual id
     * @param refreshCanvas defines visual representation on the canvas shall be
     * refreshed
     */
    public void invoke(MethodDescription methodDescription, int visualID,
            boolean refreshCanvas) throws InvocationTargetException {

        VParamUtil.throwIfNull(methodDescription);

        if (refreshCanvas) {

            final DefaultMethodRepresentation method =
                    chooseMethodRepresentation(
                    getMethodRepresentations(methodDescription), visualID);

//            method.invoke(this);
            this.invoke(methodDescription);

            method.getReturnValue().setReturnTypeOutdated();
            Object returnValue = methodDescription.getReturnValue();
            method.getReturnValue().setValue(returnValue);
            method.sendReturnValueData();

        } else {
            this.invoke(methodDescription);
        }

        if (methodDescription.getMethodType() == MethodType.REFERENCE) {

            referenceMethodCalled(methodDescription, visualID);
        }
    }

    /**
     * Invokes method from GUI. It has the same effect as pressing the invoke
     * button on the canvas. That is, input parameters from visual
     * representation will be used.
     *
     * @param methodDescription description of the method that is to be invoked.
     */
    public void invokeFromInvokeButton(
            MethodDescription methodDescription, int visualID) {
        DefaultMethodRepresentation method = chooseMethodRepresentation(
                getMethodRepresentations(methodDescription), visualID);

        method.invokeAsCallParent();
    }

    /**
     * Invokes a method and refreshes its visual representation.
     *
     * @param o the object of the method that is to be invoked
     * @param methodName name of the method that is to be invoked
     * @param params all of the methods parameters
     * @return the return value of the method
     */
    public Object invoke(Object o,
            int visualID, String methodName, Object... params)
            throws InvocationTargetException {

        ArrayList<Class<?>> paramTypes = new ArrayList<Class<?>>();

        for (Object p : params) {
            Class<?> c = p.getClass();
            paramTypes.add(c);
        }

        MethodDescription methodDescription =
                this.getMethodDescription(o, methodName,
                paramTypes.toArray(new Class<?>[]{}));

        DefaultMethodRepresentation method = chooseMethodRepresentation(
                getMethodRepresentations(methodDescription), visualID);

        method.setParameters(new ArrayList<Object>(Arrays.asList(params)));

        methodDescription.setParameters(params);

        if (methodDescription != null) {
            this.invoke(methodDescription, visualID, true);

            return methodDescription.getReturnValue();
        }

        return null;
    }

    /**
     * Invokes method from GUI. That is, input parameters from visual
     * representation will be used.
     *
     * @param o the object of the method that is to be invoked
     * @param methodName name of the method that is to be invoked
     * @param types all parameter types
     * @return the return value of the method
     */
    public Object invokeFromGUI(
            Object o, int visualID, String methodName, Object... types)
            throws InvocationTargetException {

        MethodDescription methodDescription =
                this.getMethodDescriptionFromGUI(o, methodName, types);

        boolean allAreClassObjects = true;

        for (Object p : types) {
            if (!(p instanceof Class<?>)) {
                allAreClassObjects = false;
                break;
            }
        }

        DefaultMethodRepresentation method = chooseMethodRepresentation(
                getMethodRepresentations(methodDescription), visualID);

        Object params[] = new Object[method.getParameters().size()];

        int i = 0;
        for (TypeRepresentation t : method.getParameters()) {
            params[i] = t.getValue();
            i++;
        }

        methodDescription.setParameters(params);

        if (allAreClassObjects && (methodDescription != null)) {
            this.invoke(methodDescription, visualID, true);

        } else {
            System.out.println(">> Error: wrong parameter types!");
            return null;
        }
        return methodDescription.getReturnValue();
    }

    private void referenceMethodCalled(MethodDescription mDesc, int visualID) {

        if (mDesc.getParameters()[0] != null) {

            Object newInstance = mDesc.getParameters()[0];

            replaceInstance(getObjectRepresentation(mDesc.getObjectID(),
                    visualID), newInstance, null);
        }
    }

    /**
     * Invokes method from GUI. It has the same effect as pressing the invoke
     * button on the canvas. That is, input parameters from visual
     * representation and method dependencies will be used.
     *
     * @param o the object of the method that is to be invoked
     * @param methodName name of the method that is to be invoked
     * @param types all parameter types
     * @return the return value of the method
     */
    public Object invokeFromInvokeButton(
            Object o, int visualID, String methodName, Object... types) {

        MethodDescription methodDescription =
                this.getMethodDescriptionFromGUI(o, methodName, types);

        boolean allAreClassObjects = true;

        for (Object p : types) {
            if (!(p instanceof Class<?>)) {
                allAreClassObjects = false;
                break;
            }
        }

        if (allAreClassObjects && (methodDescription != null)) {
            this.invokeFromInvokeButton(methodDescription, visualID);
        } else {
            System.out.println(">> Error: wrong parameter types!");
            return null;
        }
        return methodDescription.getReturnValue();
    }

    public ArrayList<CanvasWindow> getCanvasWindows(Object o) {
        ArrayList<CanvasWindow> result = new ArrayList<CanvasWindow>();

        for (DefaultObjectRepresentation oRep : getObjectRepresentationsByReference(o)) {
            result.add(oRep.getParentWindow());
        }

        return result;
    }

    /**
     * Returns the canvas window of the specified object.
     *
     * @param o object
     * @param visualID id of the visualization to return
     * @return the canvas window of the specified object
     */
    public CanvasWindow getCanvasWindow(Object o, int visualID) {
        return getObjectRepresentationsByReference(o).getById(visualID).
                getParentWindow();
    }

    /**
     * Returns the main canvas.
     *
     * @return the main canvas
     */
    public VisualCanvas getMainCanvas() {
        return mainCanvas;
    }

    /**
     * Returns the call option evaluator of this inspector.
     *
     * @return the call option evaluator of this inspector
     */
    public CallOptionsEvaluator getCallOptionsEvaluator() {
        return callOptionsEvaluator;
    }

    /**
     * Defines the call option evaluator of this inspector.
     *
     * @param callOptionsEvaluator the call option evaluator to set
     */
    public void setCallOptionsEvaluator(
            CallOptionsEvaluator callOptionsEvaluator) {
        this.callOptionsEvaluator = callOptionsEvaluator;
    }

    /**
     * Returns the canvas window ids of a given object.
     *
     * @param o the object
     * @return the canvas window ids if the object has been added to the canvas
     */
    public Collection<Integer> getCanvasWindowIDs(Object o) {
        ArrayList<Integer> result = new ArrayList<Integer>();

        int inspectorID = getObjectID(o);

        for (CanvasWindow w : getMainCanvas().getWindows()) {
            if (w instanceof VisualObject) {
                VisualObject vObj = (VisualObject) w;
                if (vObj.getObjectRepresentation().getObjectID() == inspectorID) {
                    result.add(vObj.getID());
                }
            }
        }

        return result;
    }

    @Override
    public void generateErrorMessage(String message, MethodDescription mDesc) {

        super.generateErrorMessage(message, mDesc);

        ObjectDescription oDesc = getObjectDescription(getObject(mDesc.getObjectID()));
        String methodName = VLangUtils.shortNameFromFullClassName(oDesc.getName())
                + "." + mDesc.getMethodName() + "()";

        MessageBox mBox = getMainCanvas().getMessageBox();
        mBox.addUniqueMessage("Method \"" + methodName + "\" can't be invoked:",
                message,
                null, MessageType.ERROR);

    }

    @Override
    public void generateErrorMessage(MethodDescription mDesc, Throwable ex) {

        super.generateErrorMessage(mDesc, ex);

        ObjectDescription oDesc = getObjectDescription(getObject(mDesc.getObjectID()));
        String methodName = VLangUtils.shortNameFromFullClassName(oDesc.getName())
                + "." + mDesc.getMethodName() + "()";

        MessageBox mBox = getMainCanvas().getMessageBox();

        if (ex instanceof VWorkflowException) {

            VWorkflowException vex = (VWorkflowException) ex;

            mBox.addMessage(vex.getTitle(), vex.getMessage(), MessageType.ERROR);
        } else {

            mBox.addUniqueMessage("Method \"" + methodName + "\" can't be invoked:",
                    ex.toString(),
                    null, MessageType.ERROR);
        }

        System.err.println(ex.getMessage());

        Collection<CompilationUnit> classesInSession =
                VRL.getCurrentProjectController().getNamesOfDefinedClasses();

        Collection<String> classNamesInSession = new ArrayList<String>();

        for (CompilationUnit cu : classesInSession) {
            classNamesInSession.addAll(cu.getClassNames());
        }

        for (StackTraceElement ste : ex.getStackTrace()) {

            int lineNumber = ste.getLineNumber();
            
            String className = ste.getClassName();
            
            if (classNamesInSession.contains(className)) {
                lineNumber -=3;
            }

            String msg = " --> at "
                    + ste.getClassName() + "." + ste.getMethodName()
                    + "(" + ste.getFileName() + ":" + lineNumber + ")";

            if (classNamesInSession.contains(className)) {
                System.err.println(
                        "\n--------------------------\n"
                        + msg + " <-- PROJECT CLASS!\n"
                        +"--------------------------\n");
            } else {
                System.err.println(msg);
            }

        }

    }

    /**
     * Returns a collection containing all instances controlled by this
     * inspector except control objects.
     *
     * @return a collection containing all instances controlled by this except
     * control objects
     */
    @Override
    public Collection<Object> getObjects() {
        ArrayList<Object> result = new ArrayList<Object>();

        for (Object o : super.getObjects()) {
            if (o instanceof InputObject
                    || o instanceof OutputObject
                    || o instanceof ClassInfoObject
                    || o instanceof StartObject
                    || o instanceof StopObject) {
                // TODO should we do something here?
                continue;
            } else {
                result.add(o);
            }
        }

        return result;
    }
//    @Override
//    public synchronized Pair<ArrayList<Object>, ArrayList<Object>> replaceAllObjects(
//            Class<?> cls, InstanceCreator creator)
//            throws InterfaceChangedException {
//
//        System.out.println(">> Replace Objects of type " + cls.getName());
//
//
//        Pair<ArrayList<Object>, ArrayList<Object>> replacement =
//                super.replaceAllObjects(cls, creator);
//
//        ArrayList<Object> oldInstances = replacement.getFirst();
//        ArrayList<Object> newInstances = replacement.getSecond();
//
////        for (int i = 0; i < oldInstances.size(); i++) {
////            Object oldObj = oldInstances.get(i);
////            
////        }
//
//        for (int i = 0; i < oldInstances.size(); i++) {
//
//            System.out.println(" --> # Instances: " + oldInstances.size());
//
//            Object oldObj = oldInstances.get(i);
//            Object newObj = newInstances.get(i);
//
//            ArrayList<DefaultObjectRepresentation> oReps =
//                    getObjectRepresentationsByReference(oldObj);
//
//            System.out.println(" --> # oReps for Obj " + i + ": " + oldInstances.size());
//
//            for (DefaultObjectRepresentation oRep : oReps) {
//                replaceInstance(oRep, newObj);
//            }
//
//        }
//
//        return replacement;
//    }

    /**
     * @return the visualizations
     */
    private IDArrayMap<DefaultObjectRepresentation> getVisualizations() {
        return visualizations;
    }
}
