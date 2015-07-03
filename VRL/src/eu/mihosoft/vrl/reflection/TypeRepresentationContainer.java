/* 
 * TypeRepresentationContainer.java
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

import eu.mihosoft.vrl.lang.visual.InputValue;
import eu.mihosoft.vrl.lang.visual.OutputValue;
import eu.mihosoft.vrl.types.InputValueType;
import eu.mihosoft.vrl.types.OutputValueType;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.JPanel;
import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.CanvasWindow;
import eu.mihosoft.vrl.visual.ConnectionResult;
import eu.mihosoft.vrl.visual.ConnectionStatus;
import eu.mihosoft.vrl.visual.ConnectorType;
import eu.mihosoft.vrl.visual.Connector;
import eu.mihosoft.vrl.visual.IDObject;
import eu.mihosoft.vrl.visual.Message;
import eu.mihosoft.vrl.visual.MessageType;
import eu.mihosoft.vrl.visual.VBoxLayout;
import eu.mihosoft.vrl.visual.ValueObject;

/**
 * A type representation container is a swing container that not only contains a
 * type representation but also a connector.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class TypeRepresentationContainer extends JPanel implements IDObject, ValueObject {

    private static final long serialVersionUID = -4536765749700128367L;
    private Connector connector;
    private TypeRepresentationBase type;
    private DefaultMethodRepresentation method;
    private int ID;

    /**
     * Constructor.
     *
     * @param type the type representation that is to be displayed
     * @param method the method the visualized value belongs to
     * @param connectorType the type of the connector type
     * @param mainCanvas the main cavas which displays the container
     */
    public TypeRepresentationContainer(TypeRepresentationBase type,
            DefaultMethodRepresentation method, ConnectorType connectorType,
            Canvas mainCanvas) {
//        super(null);

        this.type = type;
        this.method = method;

        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.LINE_AXIS);

        this.setLayout(layout);

        setAlignmentY(0.5f);

        boolean weAreInput = connectorType == ConnectorType.INPUT;
        boolean weAreOutput = connectorType == ConnectorType.OUTPUT;

        if (weAreInput) {
            this.setAlignmentY(LEFT_ALIGNMENT);
        }

        if (weAreOutput) {
            this.setAlignmentY(RIGHT_ALIGNMENT);
        }

        boolean hideConnector
                = type.isHideConnector()
                || type.getType().equals(void.class);

        if (weAreInput) {
            this.applyComponentOrientation(
                    ComponentOrientation.LEFT_TO_RIGHT);
            connector = new Input((VisualCanvas) mainCanvas);
            connector.setValueObject(this);

            JPanel p = new JPanel() {

                @Override
                public void paintComponent(Graphics g) {
                    // invisible
                }
            };

            VBoxLayout boxLayout = new VBoxLayout(p, VBoxLayout.X_AXIS);
            p.setLayout(boxLayout);
            p.setAlignmentY(0.5f);

            p.add(connector);
            connector.setAlignmentY(0.5f);

            this.add(p);

            if (hideConnector) {
                connector.setVisible(false);
            }

            connector.getTransferable().
                    setToolTipText("<html><b>type:</b> "
                            + TypeUtil.getParamTypeName(
                                    type.getParamInfo(), type.getType()) + "</html>");

        }

        if (weAreOutput) {

            this.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            connector = new Output((VisualCanvas) mainCanvas);
            connector.setValueObject(this);

            JPanel p = new JPanel() {

                @Override
                public void paintComponent(Graphics g) {
                    // invisible
                }
            };

            VBoxLayout boxLayout = new VBoxLayout(p, VBoxLayout.X_AXIS);
            p.setLayout(boxLayout);
            p.setAlignmentY(0.5f);

            p.add(connector);

            connector.setAlignmentY(0.5f);

            this.add(p, BorderLayout.EAST);

            if (hideConnector) {
                connector.setVisible(false);
            }

            connector.getTransferable().
                    setToolTipText("<html><b>type:</b> "
                            + TypeUtil.getReturnTypeName(
                                    type.getParentMethod().getDescription().getMethodInfo(),
                                    type.getType()) + "</html>");
        }

        type.setConnector(connector);

        type.setAlignmentX(0.0f);

        this.add((Component) type);
        this.add(Box.createHorizontalGlue());

        setOpaque(false);

    }

    @Override
    public Insets getInsets() {
        return new Insets(0, 3, 0, 3);
    }

    /**
     * Returns the connector of the container.
     *
     * @return
     */
    @Override
    public Connector getConnector() {
        return connector;
    }

    @Override
    protected void paintComponent(Graphics g) {
        // invisible
    }

    /**
     * Returns the type representation of the container.
     *
     * @return the type representation of the container
     */
    public TypeRepresentationBase getTypeRepresentation() {
        return type;
    }

//    /**
//     * Returns the type representation of the container.
//     * @return the type representation of the container
//     */
//    public TypeRepresentationBase getTypeRepresentationBase() {
//        return (TypeRepresentationBase) type;
//    }
    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * Returns the method the type representation container is atached to.
     *
     * @return the method the type representation container is atached to
     */
    public DefaultMethodRepresentation getMethod() {
        return method;
    }

    @Override
    public Class<?> getType() {
        return getTypeRepresentation().getType();
    }

    @Override
    public ConnectionResult compatible(ValueObject obj) {

        if (!(obj instanceof TypeRepresentationContainer)) {

            return new ConnectionResult(null,
                    ConnectionStatus.ERROR_VALUE_TYPE_MISSMATCH);
        }

        TypeRepresentationContainer valueObj
                = (TypeRepresentationContainer) obj;

        TypeRepresentationBase input = null;
        TypeRepresentationBase output = null;

        if (getTypeRepresentation().isInput()) {
            input = getTypeRepresentation();
            output = valueObj.getTypeRepresentation();
        } else {
            output = getTypeRepresentation();
            input = valueObj.getTypeRepresentation();
        }

        boolean isInputOrOutputValue = getType().equals(InputValue.class)
                || getType().equals(OutputValue.class)
                || obj.getType().equals(InputValue.class)
                || obj.getType().equals(OutputValue.class);

        if (isInputOrOutputValue) {

            // for parameter object, i.e., inputValue/outputValue objects
            // we only allow one connection
            if (getConnector().isOutput()) {
                // if this connector is already connected we do not allow
                // another connection
                boolean alreadyConnected
                        = getMethod().getMainCanvas().
                        getDataConnections().alreadyConnected(connector);

                if (alreadyConnected) {
                    return new ConnectionResult(
                            new Message("Cannot establish connection:",
                                    ">> this output connector is already"
                                    + " connected! Only one connection is allowed for"
                                    + " parameter definitions.",
                                    MessageType.ERROR),
                            ConnectionStatus.ERROR_INVALID);
                }
            } else {
                // if the output is already connected we do not allow another
                // connection
                boolean outputAlreadyConnected
                        = getMethod().getMainCanvas().
                        getDataConnections().alreadyConnected(
                                obj.getConnector());

                if (outputAlreadyConnected) {
                    return new ConnectionResult(
                            new Message("Cannot establish connection:",
                                    ">> this output connector is already"
                                    + " connected! Only one connection is allowed for"
                                    + " parameter definitions.",
                                    MessageType.ERROR),
                            ConnectionStatus.ERROR_INVALID);
                }
            }
        }

        if (input.isOutput()) {
            return new ConnectionResult(null,
                    ConnectionStatus.ERROR_BOTH_ARE_OUTPUTS);
        } else if (output.isInput()) {
            return new ConnectionResult(null,
                    ConnectionStatus.ERROR_BOTH_ARE_INPUTS);
        }

        if (input instanceof OutputValueType
                || output instanceof InputValueType) {
            return new ConnectionResult(null,
                    ConnectionStatus.VALID);
        }

        return input.compatible(output);
    }

    @Override
    public CanvasWindow getParentWindow() {
        return getTypeRepresentation().
                getParentMethod().getParentObject().getParentWindow();
    }

    @Override
    public void setValue(Object o) {
        getTypeRepresentation().setValue(o);
    }

    @Override
    public Object getValue() {
        return getTypeRepresentation().getViewValue();
    }

    @Override
    public void setOutdated() {
        getTypeRepresentation().setDataOutdated();
    }
}
