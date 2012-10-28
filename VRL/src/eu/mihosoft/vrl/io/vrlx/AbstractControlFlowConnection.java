/* 
 * AbstractControlFlowConnection.java
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

import eu.mihosoft.vrl.reflection.ControlFlowValueObject;
import eu.mihosoft.vrl.reflection.MethodIdentifier;
import eu.mihosoft.vrl.reflection.DefaultMethodRepresentation;
import eu.mihosoft.vrl.reflection.DefaultObjectRepresentation;
import eu.mihosoft.vrl.reflection.ObjectIdentifier;
import eu.mihosoft.vrl.reflection.TypeRepresentationContainer;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.visual.CanvasChild;
import eu.mihosoft.vrl.visual.Connection;
import eu.mihosoft.vrl.visual.Connections;
import eu.mihosoft.vrl.visual.Connector;
import eu.mihosoft.vrl.visual.Message;
import eu.mihosoft.vrl.visual.MessageType;

/**
 * Defines a connection between two type representations. Abstract connections
 * are only used for XML serialization.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class AbstractControlFlowConnection {

    /**
     * the sender method
     */
    private ObjectIdentifier senderObject;
    /**
     * the receiver method
     */
    private ObjectIdentifier receiverObject;

    /**
     * Constructor.
     */
    public AbstractControlFlowConnection() {
        //
    }

    /**
     * Constructor.
     * <p>
     * Creates an abstract connection from a connection object.
     * </p>
     * @param connection the connection
     */
    public AbstractControlFlowConnection(Connection connection) {
        Connector sender = connection.getSender();
        Connector receiver = connection.getReceiver();

        DefaultObjectRepresentation oRepSender =
                ((ControlFlowValueObject) sender.getValueObject()).getObject();

        DefaultObjectRepresentation oRepReceiver =
                ((ControlFlowValueObject) receiver.getValueObject()).getObject();


        senderObject = new ObjectIdentifier(oRepSender);
        receiverObject = new ObjectIdentifier(oRepReceiver);
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
     * Adds the connection to the canvas.
     * @param mainCanvas the canvas the connection is to be added to
     */
    public void addToCanvas(VisualCanvas mainCanvas, Connections connections) {
        
        
        if (senderObject==null || receiverObject == null) {
            return;
        }
        
        DefaultObjectRepresentation senderObjRep =
                mainCanvas.getInspector().
                getObjectRepresentation(senderObject);

        DefaultObjectRepresentation receiverObjRep =
                mainCanvas.getInspector().
                getObjectRepresentation(receiverObject);

        Connector sender = senderObjRep.getControlFlowOutput();

        Connector receiver = receiverObjRep.getControlFlowInput();

        Connection c = connections.add(sender, receiver);

        double senderTransparency =
                senderObjRep.
                getParentWindow().getTransparency();

        double receiverTransparency =
                receiverObjRep.
                getParentWindow().getTransparency();

        c.setTransparency(
                (float) Math.min(senderTransparency, receiverTransparency));

    }

    /**
     * @return the senderObject
     */
    public ObjectIdentifier getSenderObject() {
        return senderObject;
    }

    /**
     * @param senderObject the senderObject to set
     */
    public void setSenderObject(ObjectIdentifier senderObject) {
        this.senderObject = senderObject;
    }

    /**
     * @return the receiverObject
     */
    public ObjectIdentifier getReceiverObject() {
        return receiverObject;
    }

    /**
     * @param receiverObject the receiverObject to set
     */
    public void setReceiverObject(ObjectIdentifier receiverObject) {
        this.receiverObject = receiverObject;
    }

}
