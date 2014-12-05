/* 
 * AbstractDataConnection.java
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

import eu.mihosoft.vrl.reflection.MethodIdentifier;
import eu.mihosoft.vrl.reflection.DefaultMethodRepresentation;
import eu.mihosoft.vrl.reflection.DefaultObjectRepresentation;
import eu.mihosoft.vrl.reflection.TypeRepresentationContainer;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.visual.CanvasChild;
import eu.mihosoft.vrl.visual.Connection;
import eu.mihosoft.vrl.visual.Connections;
import eu.mihosoft.vrl.visual.Connector;
import eu.mihosoft.vrl.visual.Message;
import eu.mihosoft.vrl.visual.MessageType;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Defines a connection between two type representations. Abstract connections
 * are only used for XML serialization.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class AbstractDataConnection {

    /**
     * the sender method
     */
    private MethodIdentifier senderMethod;
    /**
     * the receiver method
     */
    private MethodIdentifier receiverMethod;
    /**
     * the id of the receiving parameter
     */
    private int paramReceiver;
    /**
     * the id of the sending parameter
     */
    private int paramSender;
    /**
     * the id of the receiving parameter
     */
    private String paramReceiverKey;
    /**
     * the id of the sending parameter
     */
    private String paramSenderKey;

    /**
     * Constructor.
     */
    public AbstractDataConnection() {
        //
    }

    /**
     * Constructor.
     * <p>
     * Creates an abstract connection from a connection object.
     * </p>
     *
     * @param connection the connection
     */
    public AbstractDataConnection(Connection connection) {
        Connector sender = connection.getSender();
        Connector receiver = connection.getReceiver();

        DefaultMethodRepresentation mRepSender
                = ((TypeRepresentationContainer) sender.getValueObject()).getMethod();

        DefaultMethodRepresentation mRepReceiver
                = ((TypeRepresentationContainer) receiver.getValueObject()).getMethod();

        senderMethod = new MethodIdentifier(
                mRepSender.getDescription(),
                mRepSender.getParentObject().getID(), mRepSender.getVisualMethodID());
        receiverMethod = new MethodIdentifier(
                mRepReceiver.getDescription(),
                mRepReceiver.getParentObject().getID(), mRepReceiver.getVisualMethodID());

        paramSender = sender.getID();
        paramReceiver = receiver.getID();

        paramSenderKey = sender.getId();
        paramReceiverKey = receiver.getId();

    }

//    public void setParamReceiverByName(String paramName) {
//        for (int i = 0; i < receiverMethod.getParameterNames().length; i++) {
//            String name = receiverMethod.getParameterNames()[i];
//            if (name.equals(paramName)) {
//                // the first connector is always the
//                // output connector,
//                // that's why we do an index shift
//                paramReceiver = i + 1;
//            }
//        }
//    }
    /**
     * Returns the sender method as method identifier.
     *
     * @return the sender method
     */
    public MethodIdentifier getSenderMethod() {
        return senderMethod;
    }

    /**
     * Defines the sender method via method identifier.
     *
     * @param senderMethod the sender method to set
     */
    public void setSenderMethod(MethodIdentifier senderMethod) {
        this.senderMethod = senderMethod;
    }

    /**
     * Returns the receiver method as method identifier.
     *
     * @return the receiver method
     */
    public MethodIdentifier getReceiverMethod() {
        return receiverMethod;
    }

    /**
     * Defines the receiver method via method identifier.
     *
     * @param receiverMethod the receiver method to set
     */
    public void setReceiverMethod(MethodIdentifier receiverMethod) {
        this.receiverMethod = receiverMethod;
    }

    /**
     * Returns the parameter receiver id.
     *
     * @return the receiver id
     */
    public int getParamReceiver() {
        return paramReceiver;
    }

    /**
     * Defines the parameter receiver id.
     *
     * @param paramReceiver the param receiver id to set
     */
    public void setParamReceiver(int paramReceiver) {
        this.paramReceiver = paramReceiver;
    }

    /**
     * Returns the parameter sender id.
     *
     * @return the sender id
     */
    public int getParamSender() {
        return paramSender;
    }

    /**
     * Defines the parameter sender id.
     *
     * @param paramSender the param sender id to set
     */
    public void setParamSender(int paramSender) {
        this.paramSender = paramSender;
    }

    /**
     * Adds the connection to the canvas.
     *
     * @param mainCanvas the canvas the connection is to be added to
     */
    public void addToCanvas(VisualCanvas mainCanvas, Connections connections) {
        DefaultMethodRepresentation senderMethodRep
                = mainCanvas.getInspector().getMethodRepresentation(senderMethod);

        DefaultMethodRepresentation receiverMethodRep
                = mainCanvas.getInspector().getMethodRepresentation(receiverMethod);

        if (senderMethodRep == null || receiverMethodRep == null) {

            DefaultObjectRepresentation senderObject
                    = mainCanvas.getInspector().
                    getObjectRepresentation(senderMethod);
            DefaultObjectRepresentation receiverObject
                    = mainCanvas.getInspector().
                    getObjectRepresentation(receiverMethod);

            String senderName = mainCanvas.getInspector().
                    getObject(senderMethod).getClass().getName()
                    + "." + senderMethod.getMethodName();
            String receiverName = mainCanvas.getInspector().
                    getObject(receiverMethod).getClass().getName()
                    + "." + receiverMethod.getMethodName();

            String interfaceChangedName = "";
            CanvasChild interfaceChangedObject = senderObject;
            CanvasChild connectedObject = receiverObject;

            if (senderMethodRep == null) {
                interfaceChangedName = senderName;
                interfaceChangedObject = senderObject;
                connectedObject = receiverObject;
            } else {
                interfaceChangedName = receiverName;
                interfaceChangedObject = receiverObject;
                connectedObject = senderObject;
            }

            if (senderMethodRep == null && receiverMethodRep == null) {
                interfaceChangedName = senderName + " and " + receiverName;
            }

            String msg = ">> <b>Warning:</b> "
                    + "Cannot create data connection"
                    + "<br><br> >>> <b>Connection:</b> "
                    + Message.EMPHASIZE_BEGIN
                    + senderName + Message.EMPHASIZE_END
                    + " ----> " + Message.EMPHASIZE_BEGIN
                    + receiverName + Message.EMPHASIZE_END
                    + "<br> >>> <b>Reason:</b> the interface of "
                    + Message.EMPHASIZE_BEGIN + interfaceChangedName
                    + Message.EMPHASIZE_END
                    + " has changed or has been removed.";

            System.err.println(msg);

            mainCanvas.getMessageBox().addMessage(
                    "Cannot create data connection:",
                    msg, interfaceChangedObject, MessageType.WARNING);

            mainCanvas.getEffectPane().pulse(connectedObject,
                    MessageType.WARNING);

            return;
        }

        Connector sender = null;
        Connector receiver = null;

        if (getParamSenderKey() == null || getParamReceiverKey() == null) {

            System.out.println(
                    ">> Warning: deprecated file format (before 03.08.2012):"
                    + " --> using legacy id's for referencing connectors");

            int senderID = this.getParamSender();
            int receiverID = this.getParamReceiver();

            try {
                sender = senderMethodRep.getConnector(senderID);
                receiver = receiverMethodRep.getConnector(receiverID);
            } catch (Exception ex) {
                Logger.getLogger(AbstractDataConnection.class.getName()).
                            log(Level.SEVERE, null, ex);
            }

        } else {
            System.out.println(">> new id's: " + getParamSenderKey() + " -> " + getParamReceiverKey());
            sender = senderMethodRep.getConnectorByKey(getParamSenderKey());
            receiver = receiverMethodRep.getConnectorByKey(getParamReceiverKey());
        }

        if (sender == null || receiver == null) {
            System.out.println("Warning: cannot restore connection.");

        } else {

            connections.add(sender, receiver);
            Connection c = connections.get(sender, receiver);

            double senderTransparency
                    = senderMethodRep.getParentObject().
                    getParentWindow().getTransparency();

            double receiverTransparency
                    = receiverMethodRep.getParentObject().
                    getParentWindow().getTransparency();

            c.setTransparency(
                    (float) Math.min(senderTransparency, receiverTransparency));
        }

    }

    /**
     * @return the paramReceiverKey
     */
    public String getParamReceiverKey() {
        return paramReceiverKey;
    }

    /**
     * @param paramReceiverKey the paramReceiverKey to set
     */
    public void setParamReceiverKey(String paramReceiverKey) {
        this.paramReceiverKey = paramReceiverKey;
    }

    /**
     * @return the paramSenderKey
     */
    public String getParamSenderKey() {
        return paramSenderKey;
    }

    /**
     * @param paramSenderKey the paramSenderKey to set
     */
    public void setParamSenderKey(String paramSenderKey) {
        this.paramSenderKey = paramSenderKey;
    }
}
