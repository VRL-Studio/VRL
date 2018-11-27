/* 
 * ControlFlowUtils.java
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

package eu.mihosoft.vrl.lang.visual;

import eu.mihosoft.vrl.reflection.ControlFlowConnection;
import eu.mihosoft.vrl.reflection.ControlFlowConnector;
import eu.mihosoft.vrl.reflection.ControlFlowValueObject;
import eu.mihosoft.vrl.reflection.DefaultMethodRepresentation;
import eu.mihosoft.vrl.reflection.DefaultObjectRepresentation;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.reflection.TypeRepresentationContainer;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.visual.Connection;
import eu.mihosoft.vrl.visual.Connector;
import eu.mihosoft.vrl.visual.Message;
import eu.mihosoft.vrl.visual.MessageBox;
import eu.mihosoft.vrl.visual.MessageType;
import eu.mihosoft.vrl.visual.ValueObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility methods that enable the analysis of the session control-flow.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class ControlFlowUtils {

    private static final String errorMsgTitle =
            "Cannot create invocation object:";

    // no instanciation allowed
    private ControlFlowUtils() {
        throw new AssertionError(); // not in this class either!
    }

    /**
     * Indicates whether a start-object has been added to the specified canvas.
     * @param canvas canvas to analyze
     * @return <code>true</code> if a start-object has been added to the
     *         specified canvas; <code>false</code> otherwise
     */
    public static boolean startObjectExists(VisualCanvas canvas) {

        Collection<Object> startObjects =
                canvas.getInspector().
                getObjectsByClassName(StartObject.class.getName());

        return !startObjects.isEmpty();
    }

    /**
     * Indicates whether a stop-object has been added to the specified canvas.
     * @param canvas canvas to analyze
     * @return <code>true</code> if a stop-object has been added to the
     *         specified canvas; <code>false</code> otherwise
     */
    public static boolean stopObjectExists(VisualCanvas canvas) {

        Collection<Object> stopObjects =
                canvas.getInspector().
                getObjectsByClassName(StopObject.class.getName());

        return !stopObjects.isEmpty();
    }

    /**
     * Returns the start-object of the specified canvas.
     * @param canvas canvas
     * @return the start-object or <code>null</code> if no start-object has been
     *         added to the specified canvas
     */
    public static StartObject getStartObject(VisualCanvas canvas) {

        if (!startObjectExists(canvas)) {
            return null;
        }

        Collection<Object> startObjects =
                canvas.getInspector().
                getObjectsByClassName(StartObject.class.getName());

        return (StartObject) startObjects.iterator().next();
    }

    /**
     * Returns the stop-object of the specified canvas.
     * @param canvas canvas
     * @return the stop-object or <code>null</code> if no stop-object has been
     *         added to the specified canvas
     */
    public static StopObject getStopObject(VisualCanvas canvas) {

        if (!stopObjectExists(canvas)) {
            return null;
        }

        Collection<Object> stopObjects =
                canvas.getInspector().
                getObjectsByClassName(StopObject.class.getName());

        return (StopObject) stopObjects.iterator().next();
    }

    /**
     * Indicates whether the start-object of the specified canvas is connected.
     * @param canvas canvas to analyze
     * @return <code>true</code> if the specified start-object is connected;
     *         <code>false</code> otherwise
     */
    public static boolean startObjectConnected(VisualCanvas canvas) {
        Connector startConnector = getStartConnector(
                canvas);

        return canvas.getControlFlowConnections().
                alreadyConnected(startConnector);
    }

    /**
     * Indicates whether the stop-object of the specified canvas is connected.
     * @param canvas canvas to analyze
     * @param startObject stop-object to analyze
     * @return <code>true</code> if the specified stop-object is connected;
     *         <code>false</code> otherwise
     */
    public static boolean stopObjectConnected(VisualCanvas canvas) {

        Connector stopConnector = getStopConnector(canvas);

        return canvas.getControlFlowConnections().
                alreadyConnected(stopConnector);
    }

    /**
     * Returns the connector of the specified start-object
     * @param canvas canvas
     * @param startObject start-object
     * @return the connector of the start-object
     */
    public static ControlFlowConnector getStartConnector(VisualCanvas canvas) {
//        DefaultObjectRepresentation startRep =
//                canvas.getInspector().getObjectRepresentationsByReference(
//                getStartObject(canvas)).get(0);
//        DefaultMethodRepresentation startMRep = startRep.getMethods().get(0);
//        return startMRep.getControlflowOutput();

        return canvas.getInspector().getObjectRepresentationsByReference(
                getStartObject(canvas)).get(0).getControlFlowOutput();
    }

    /**
     * Returns the connector of the specified stop-object
     * @param canvas canvas
     * @param startObject stop-object
     * @return the connector of the stop-object
     */
    public static ControlFlowConnector getStopConnector(VisualCanvas canvas) {
//        DefaultObjectRepresentation stopRep =
//                canvas.getInspector().getObjectRepresentationsByReference(
//                getStopObject(canvas)).get(0);
//        DefaultMethodRepresentation stopMRep = stopRep.getMethods().get(0);
//        return stopMRep.getControlflowInput();

        return canvas.getInspector().getObjectRepresentationsByReference(
                getStopObject(canvas)).get(0).getControlFlowInput();
    }

    /**
     * Returns the connections (path) between the given connectors.
     * @param c1 first connector
     * @param c2 second connector
     * @return the connections (path) between the given connectors or an
     *         empty list if no path exists
     */
    public static Collection<ControlFlowConnection> getPath(
            ControlFlowConnector c1, ControlFlowConnector c2) {
        ArrayList<ControlFlowConnection> result =
                new ArrayList<ControlFlowConnection>();

        ControlFlowConnector nextConnector = c1;

        if (c1.isInput()) {
            nextConnector = getNextOutput(c2);
        }

        boolean reachedEnd = false;
        boolean noConnection = false;
        boolean circularConnection = false;

        boolean firstRun = true;

        while (!reachedEnd) {
            ControlFlowConnection connection = getConnection(nextConnector);

            nextConnector = getNextOutput(nextConnector);

            noConnection = nextConnector == null;

            if (!firstRun && !noConnection) {
                circularConnection = connection.contains(c1);
            }

            reachedEnd = connection == null
                    || connection.contains(c2)
                    || noConnection
                    || circularConnection;

            if (connection != null) {
                result.add(connection);
            }

            firstRun = false;
        }

        if (noConnection || circularConnection) {
            result.clear();
        }

        if (circularConnection) {
            System.out.println("Circular Control-Flow-Connections!");
        }

        return result;
    }

    /**
     * Returns the connections (path) between the given methods.
     * @param c1 first method
     * @param c2 second method
     * @return the connections (path) between the given methods or an
     *         empty list if no path exists
     */
    public static Collection<ControlFlowConnection> getPath(
            DefaultObjectRepresentation o1, DefaultObjectRepresentation o2) {
        return getPath(o1.getControlFlowOutput(), o2.getControlFlowInput());
    }

    /**
     * Returns the next output connector relative to the given connector.
     * @param c connector
     * @return the next output connector relative to the given connector or
     *         <code>null</code> if no connection exists
     */
    public static ControlFlowConnector getNextOutput(ControlFlowConnector c) {
        ControlFlowConnector result = c;

        VisualCanvas canvas = (VisualCanvas) c.getMainCanvas();

        if (!canvas.getControlFlowConnections().alreadyConnected(c)) {
            return null;
        }

        if (c.isInput()) {
            ControlFlowValueObject inputVObj =
                    (ControlFlowValueObject) c.getValueObject();
            result = inputVObj.getObject().getControlFlowOutput();
        } else {
            ControlFlowConnector receiver =
                    (ControlFlowConnector) getConnection(c).getReceiver();

            ControlFlowValueObject inputVObj =
                    (ControlFlowValueObject) receiver.getValueObject();
            result = inputVObj.getObject().getControlFlowOutput();
        }

        return result;

    }

    /**
     * Returns the connection that contains the given connector.
     * @param c connector
     * @return the connection that contains the given connector or
     *         <code>null</code> if no such connection exists
     */
    public static ControlFlowConnection getConnection(ControlFlowConnector c) {

        VisualCanvas canvas = (VisualCanvas) c.getMainCanvas();

        ControlFlowConnection result = null;

        List<Connection> connections =
                canvas.getControlFlowConnections().getAllWith(c);

        // we only allow one connection, thus we can return the first entry
        if (!connections.isEmpty()) {
            result = (ControlFlowConnection) connections.get(0);
        }

        return result;
    }

    /**
     * Returns the method the specified control-flow connector belongs to.
     * @param c connector
     * @return the method the specified control-flow connector belongs to or
     *         <code>null</code> if no such method exists
     */
    public static DefaultObjectRepresentation getObject(ControlFlowConnector c) {
        DefaultObjectRepresentation result = null;

        ControlFlowValueObject vObj =
                (ControlFlowValueObject) c.getValueObject();

        result = vObj.getObject();

        return result;
    }

    /**
     * Indicates session consistency.
     * @param canvas canvas to analyze
     * @return <code>true</code> if the specified session is consistent;
     *         <code>false</code> otherwise
     */
    public static boolean validateSession(VisualCanvas canvas) {
        MessageBox mBox = canvas.getMessageBox();

        if (!ControlFlowUtils.startObjectExists(canvas)) {
            mBox.addMessage(errorMsgTitle,
                    ">> Start-Object missing!", MessageType.ERROR);
            return false;
        }
        if (!ControlFlowUtils.stopObjectExists(canvas)) {
            mBox.addMessage(errorMsgTitle,
                    ">> Stop-Object missing!", MessageType.ERROR);
            return false;
        }
        if (!startObjectConnected(canvas)) {
            mBox.addMessage(errorMsgTitle,
                    ">> Control-Flow connections not consistent! Start-object"
                    + " output is not connected.", MessageType.ERROR);
            return false;
        }
        if (!stopObjectConnected(canvas)) {
            mBox.addMessage(errorMsgTitle,
                    ">> Control-Flow connections not consistent! Stop-object"
                    + " input is not connected.", MessageType.ERROR);
            return false;
        }


        ControlFlowConnector startOutput = getStartConnector(canvas);
        ControlFlowConnector stopInput = getStopConnector(canvas);

        boolean ambiguosControlFlow = getPath(startOutput, stopInput).isEmpty();

//        // check whether control-flow path is ambiguous
//        for (Connection c : canvas.getControlFlowConnections()) {
//            ControlFlowConnector sender = (ControlFlowConnector) c.getSender();
//
//            // ignore start and stop object
//            ControlFlowValueObject sObj =
//                    (ControlFlowValueObject) sender.getValueObject();
//            Class<?> clazz = canvas.getInspector().
//                    getObject(sObj.getObject().getObjectID()).getClass();
//            if ((StartObject.class.isAssignableFrom(clazz))
//                    || (StopObject.class.isAssignableFrom(clazz))) {
//                continue;
//            }
//
//            // check if path to stop object exists
//            if (getPath(sender, stopInput).isEmpty()) {
//                ambiguosControlFlow = true;
//                break;
//            }
//        } // end for


        // checks whether start and stop objects are connected
        if (ambiguosControlFlow) {
            mBox.addMessage(errorMsgTitle,
                    ">> Control-Flow is ambiguous! Please ensure that "
                    + Message.EMPHASIZE_BEGIN + "Start"
                    + Message.EMPHASIZE_END
                    + " and "
                    + Message.EMPHASIZE_BEGIN + "Stop"
                    + Message.EMPHASIZE_END
                    + " are connected.",
                    MessageType.ERROR);
            return false;
        }

        // check if all methods that have data-connections are part of the
        // control flow

        boolean invalidDataConnections = false;
        Collection<DefaultMethodRepresentation> inocationMethods =
                getInvocationList(canvas);

        Collection<DefaultMethodRepresentation> allMethods =
                getAllMethodsWithDataConnections(canvas);

        for (DefaultMethodRepresentation mRep : allMethods) {

            for (Connection c : canvas.getDataConnections().getAllWith(
                    mRep.getOutputConnector())) {
                ValueObject value = c.getReceiver().getValueObject();

                // ignore inputs, as they are not part of the controlflow
                Class<?> objClass = canvas.getInspector().
                        getObject(mRep.getParentObject()).getClass();
                if (InputObject.class.isAssignableFrom(objClass)) {
                    continue;
                }

                if (value instanceof TypeRepresentationContainer) {

                    TypeRepresentationContainer tCont =
                            (TypeRepresentationContainer) value;

                    // ignore outputs, as they are not part of the controlflow
                    objClass = canvas.getInspector().
                            getObject(tCont.getTypeRepresentation().
                            getParentMethod().getParentObject()).getClass();
                    if (OutputObject.class.isAssignableFrom(objClass)) {
                        continue;
                    }

                    invalidDataConnections =
                            inocationMethods.contains(tCont.getMethod())
                            != inocationMethods.contains(mRep);

                    if (invalidDataConnections) {
                        break;
                    }
                }
            }

        }

        if (invalidDataConnections) {
            mBox.addMessage("Invalid Data Connections:",
                    ">> only methods that are part of the control flow may have"
                    + " data connections to methods in the control flow!"
                    + " Either remove the data connections"
                    + " from those objects (methods will be ignored)"
                    + " or add them to the control flow.",
                    MessageType.ERROR);
            return false;
        }

        // everything is fine
        return true;
    }

    public static boolean isOutputConnected(VisualCanvas canvas,
            DefaultMethodRepresentation mRep) {
        return canvas.getDataConnections().alreadyConnected(mRep.getOutputConnector());
    }

    public static boolean isInputConnected(VisualCanvas canvas,
            DefaultMethodRepresentation mRep) {

        for (TypeRepresentationBase tRep : mRep.getParameters()) {
            boolean connected = canvas.getDataConnections().alreadyConnected(
                    tRep.getConnector());

            if (connected) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns all method representations that have data connections.
     * @param canvas canvas
     * @return all method representations that have data connections
     */
    public static Collection<DefaultMethodRepresentation> getAllMethodsWithDataConnections(
            VisualCanvas canvas) {
        ArrayList<DefaultMethodRepresentation> result =
                new ArrayList<DefaultMethodRepresentation>();

        for (Connection c : canvas.getDataConnections()) {

            // senders
            TypeRepresentationContainer tCont =
                    (TypeRepresentationContainer) c.getSender().getValueObject();

            DefaultMethodRepresentation mRep =
                    tCont.getTypeRepresentation().getParentMethod();

            Object parentObject = canvas.getInspector().
                    getObject(mRep.getParentObject().getObjectID());

            boolean ignoreMethod = parentObject instanceof StartObject
                    || parentObject instanceof StopObject
                    || parentObject instanceof ClassInfoObject
                    || parentObject instanceof InputObject
                    || parentObject instanceof OutputObject;

            if (!result.contains(mRep) && !ignoreMethod) {
                result.add(mRep);
            }

            // receivers
            tCont =
                    (TypeRepresentationContainer) c.getReceiver().getValueObject();

            mRep =
                    tCont.getTypeRepresentation().getParentMethod();

            parentObject = canvas.getInspector().
                    getObject(mRep.getParentObject().getObjectID());

            ignoreMethod = parentObject instanceof StartObject
                    || parentObject instanceof StopObject
                    || parentObject instanceof ClassInfoObject
                    || parentObject instanceof InputObject
                    || parentObject instanceof OutputObject;

            if (!result.contains(mRep) && !ignoreMethod) {
                result.add(mRep);
            }
        }

        return result;
    }

    /**
     * Returns the invocation list (list of method representations) defined by
     * control-flow path. Start- and StopObject methods are not part of this
     * list.
     * @param canvas canvas
     * @return the invocation list (list of method representations) defined by
     * control-flow path
     */
    public static Collection<DefaultMethodRepresentation> getInvocationList(
            VisualCanvas canvas) {

        ControlFlowConnector startConnector =
                ControlFlowUtils.getStartConnector(canvas);

        startConnector = getNextOutput(startConnector);

        ControlFlowConnector stopConnector =
                ControlFlowUtils.getStopConnector(canvas);

        Collection<ControlFlowConnection> connections =
                getPath(startConnector, stopConnector);

        return getPathAsMethodList(connections);
    }

    /**
     * Converts a list of connections to a list of the corresponding methods
     * and returns it.
     * @param connections connection list to convert
     * @return a list of connections to a list of the corresponding methods
     */
    public static Collection<DefaultMethodRepresentation> getPathAsMethodList(
            Collection<ControlFlowConnection> connections) {
        ArrayList<DefaultMethodRepresentation> methods =
                new ArrayList<DefaultMethodRepresentation>();

        for (ControlFlowConnection c : connections) {
            DefaultObjectRepresentation oRep =
                    getObject((ControlFlowConnector) c.getSender());

            methods.addAll(oRep.getInvocationList());
        }

        return methods;
    }

    public static boolean isMethodControlFlowStatement(
            DefaultMethodRepresentation mRep) {
        VisualCanvas canvas = (VisualCanvas) mRep.getMainCanvas();
        return canvas.getInspector().getObject(
                mRep.getParentObject().
                getObjectID()) instanceof ControlFlowStatement;
    }

    public static ControlFlowStatement getControlFlowStatement(
            DefaultMethodRepresentation mRep) {
        ControlFlowStatement result = null;

        VisualCanvas canvas = (VisualCanvas) mRep.getMainCanvas();

        if (isMethodControlFlowStatement(mRep)) {
            result = (ControlFlowStatement) canvas.getInspector().getObject(
                    mRep.getParentObject().
                    getObjectID());
        }

        return result;
    }
}
