/* 
 * ControlFlowValueObject.java
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

package eu.mihosoft.vrl.reflection;

import eu.mihosoft.vrl.visual.CanvasWindow;
import eu.mihosoft.vrl.visual.ConnectionResult;
import eu.mihosoft.vrl.visual.ConnectionStatus;
import eu.mihosoft.vrl.visual.Connections;
import eu.mihosoft.vrl.visual.Connector;
import eu.mihosoft.vrl.visual.Message;
import eu.mihosoft.vrl.visual.MessageType;
import eu.mihosoft.vrl.visual.ValueObject;
import java.awt.Component;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ControlFlowValueObject implements ValueObject {

    private DefaultObjectRepresentation oRep;
    private ControlFlowConnector connector;
    private Connections connections;

    public ControlFlowValueObject(
            Connections connections,
            DefaultObjectRepresentation oRep,
            ControlFlowConnector c) {
        this.oRep = oRep;
        this.connector = c;
        this.connections = connections;
    }

    @Override
    public Class<?> getType() {
        return getClass();
    }

    @Override
    public CanvasWindow getParentWindow() {
        return oRep.getParentWindow();
    }

    @Override
    public void setValue(Object o) {
        //
    }

    @Override
    public Object getValue() {
        //
        return null;
    }

    @Override
    public void setOutdated() {
        //
    }

    @Override
    public ConnectionResult compatible(ValueObject obj) {
        boolean sameType = obj.getType().equals(getType());

        if (!sameType) {
            return new ConnectionResult(
                    null, ConnectionStatus.ERROR_VALUE_TYPE_MISSMATCH);
        }

        boolean isInput = getConnector().isInput();

        ConnectionResult result = new ConnectionResult(
                null, ConnectionStatus.VALID);

        if (!isInput) {
            // the connector we drag from is an output

            // if this connector is already connected we do not allow another
            // connection
            boolean alreadyConnected =
                    connections.alreadyConnected(connector);

            if (alreadyConnected) {
                result = new ConnectionResult(
                        new Message("Cannot establish connection:",
                        ">> this control-flow output connector is already"
                        + " connected! Only one connection is allowed.",
                        MessageType.ERROR),
                        ConnectionStatus.ERROR_INVALID);
            }
        } else {
            // the connector we drag from is an input


            // if the output is already connected we do not allow another
            // connection
            boolean outputAlreadyConnected =
                    connections.alreadyConnected(obj.getConnector());

            if (outputAlreadyConnected) {
                result = new ConnectionResult(
                        new Message("Cannot establish connection:",
                        ">> the control-flow output connector is already"
                        + " connected! Only one connection is allowed",
                        MessageType.ERROR),
                        ConnectionStatus.ERROR_INVALID);
            }
        }

        return result;
    }

    public DefaultObjectRepresentation getObject() {
        return oRep;
    }

    @Override
    public Component getParent() {
        return oRep;
    }

    @Override
    public Connector getConnector() {
        return connector;
    }
}
