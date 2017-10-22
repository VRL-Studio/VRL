/* 
 * VConnector.java
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

import eu.mihosoft.vrl.lang.visual.InputValue;
import eu.mihosoft.vrl.lang.visual.OutputValue;
import eu.mihosoft.vrl.types.ArrayBaseType;
import eu.mihosoft.vrl.visual.CanvasWindow;
import eu.mihosoft.vrl.visual.Connection;
import eu.mihosoft.vrl.visual.Connections;
import eu.mihosoft.vrl.visual.Connector;
import eu.mihosoft.vrl.visual.DataProcessingMode;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VConnector extends Connector {

    public VConnector(VisualCanvas mainCanvas) {
        super(mainCanvas, mainCanvas.getDataConnections());
    }

//    /**
//     * Returns the typerepresentation container that is associated with this
//     * connector.
//     * @return the typerepresentation container that is associated with this
//     * connector
//     */
//    @Override
//    public TypeRepresentationContainer getValueObject() {
//        return valueObject;
//    }
//
//    /**
//     * Defines the typerepresentation container that is to be associated with
//     * this connector.
//     * @param valueObject the typerepresentation container that is to be
//     * associated with this  connector.
//     */
//    public void setValueObject(TypeRepresentationContainer valueObject) {
//        this.valueObject = valueObject;
//    }
    /**
     * <p> Collects the data from the object that is associated with the senders
     * type representation. </p>
     *
     * @param autoInvoke defines whether <code>receiveData()</code> shall
     * recursively invoke all methods that need to be called to get the
     * necessary input data (i.e., if the data is outdated).
     * @param methodDependencies call trace that stores method dependencies <p>
     * <b>Remark:</b> <code>autoInvoke</code> can be overruled by the currently
     * used data processing method, i.e., using      <code>
     *                                  DataProcessingMode.UPDATE_OFF
     * </code> does not allow auto invoke. </p> <p> <b>Warning:</b> this method
     * will only have an effect if the connector from which the method is called
     * is an input connector. </p>
     */
    public void receiveData(boolean autoInvoke, CallTrace methodDependencies) {
        
        System.out.println(" --> receive: " + ((TypeRepresentationContainer)getValueObject()).getMethod().getVisualMethodID() );
        System.out.println(" --> receive: " + getId() + ", isInput: " + isInput() + ", " + getType());

        fireAction(new ActionEvent(this, 0, RECEIVE_ACTION));

        if (this.isInput()) {

            Connections allConnections =
                    getMainCanvas().getDataConnections();

            for (Connection connection : allConnections.getAllWith(this)) {

                System.out.println("CONN: " + connection);

                TypeRepresentationContainer senderValueObject =
                        (TypeRepresentationContainer) connection.getSender().getValueObject();
                TypeRepresentationContainer receivererValueObject =
                        (TypeRepresentationContainer) connection.getReceiver().getValueObject();
                TypeRepresentationContainer thisValueObject =
                        (TypeRepresentationContainer) getValueObject();

                if (autoInvoke) {
                    DefaultMethodRepresentation m = senderValueObject.getMethod();

                    boolean isUpToDate =
                            senderValueObject.getTypeRepresentation().
                            isReturnTypeUpToDate();

//                    System.out.println("M: " + m + ", mDeps: " + methodDependencies);

                    if (getMainCanvas().getDataProcessingMode()
                            != DataProcessingMode.UPDATE_OFF) {
                        if (!isUpToDate) {
                            if (methodDependencies != null) {
                                methodDependencies.addCall(m);
                                try {
                                    m.invoke(methodDependencies);
//                                    System.out.println("*********************");
                                } catch (InvocationTargetException ex) {
                                    Logger.getLogger(
                                            VConnector.class.getName()).
                                            log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }

                    if (getMainCanvas().getDataProcessingMode()
                            == DataProcessingMode.UPDATE_OFF) {
                        // nothig to do
                    }

                } // end if autoInvoke



                // 1:
                // If a connection is made then existing output values are
                // sent to their corresponding receiver
                // (the input type representation of it).
                //
                // 2:
                // If an output value is changed because its method is invoked
                // it sends the value to connected receivers
                // (the input type representation of them).
                //
                // As 1 and 2 always assure that the data of connected receivers
                // (the input type representation of them) is correct it is not
                // necessary to explicitly call the setValue() method of the
                // input type representations if the parent method is invoked
                // (user clicks button).
                //
                // We assume that methodDependencies is null if 1 or 2 occure.
                // If we invoke a method via invoke button, this is not true
                // nothing will be done instead.
                //
                // we generally ignore instances of InputValue and OutputValue
                boolean isInputOrOutputValue =
                        (senderValueObject.getType().equals(InputValue.class)
                        || senderValueObject.getType().equals(OutputValue.class));
                
                if (isInputOrOutputValue) {
                    break;
                }

                if (methodDependencies == null) {
                    Object value =
                            senderValueObject.getTypeRepresentation().getValue();

                    // TODO isn't this the same as the receivererValueObject?

                    thisValueObject.setValue(value);
                }

                Object value =
                        senderValueObject.getTypeRepresentation().getValue();
                thisValueObject.setValue(value);


                // get the corresponding return type representation
                // and define it as not up to date
                receivererValueObject.setOutdated();

            } // for allConnections with

        } // end if input
    }

    /**
     * <p> Collects the data from the object that is associated with the senders
     * type representation. </p> <b>Remark:</b> does not use auto invoke. </p>
     * <p> <b>Warning:</b> this method will only have an effect if the connector
     * from which the method is called is an input connector. </p>
     */
    @Override
    public void receiveData() {
        receiveData(false, null);
    }

    /**
     * Sends the object that is associated with the type representation of the
     * sending connector to all connected receivers, i.e. input connectors. <p>
     * <b>Warning:</b> This method will only have an effect if the connector
     * from which the method is called is an output connector. </p>
     */
    @Override
    public void sendData() {
        Connections allConnections =
                this.getMainCanvas().getDataConnections();

        if (this.isOutput()) {
            for (Connection connection : allConnections.getAllWith(this)) {
                connection.getReceiver().receiveData();
            }

            // send data for array types
            if (this.getValueObject() instanceof TypeRepresentationContainer) {
                TypeRepresentationBase tRep = 
                        ((TypeRepresentationContainer) this.getValueObject()).getTypeRepresentation();
                if (tRep instanceof ArrayBaseType) {
                    ArrayBaseType arrayType = (ArrayBaseType) tRep;
                    
                    for (TypeRepresentationContainer tCont : arrayType.getTypeContainers()) {
                        tCont.getConnector().sendData();
                    }
                }
            }
        }
    }

    /**
     * The purpose of this method is to simplify the code. It is not necessary
     * to check the connector type to ensure correct data processing.
     */
    public void processData() {
        sendData();
        receiveData();
    }

    /**
     * Sends the object that is associated with the type representation of the
     * this connector to the specified receiver. <p> <b>Warning:</b> This method
     * will only have an effect if the connector from which the method is called
     * is an output connector and the specified receiver is an input connector.
     * </p>
     *
     * @param c the receiving connector
     */
    public void sendData(VConnector c) {
        if (this.isOutput() && c.isInput()) {
            c.receiveData();
        }
    }
//    public void processData() {
//        Connections allConnections = this.getMainCanvas().getConnections();
//
//        if (this instanceof Input) {
//            for (Connection connection : allConnections.getAllWith(this)) {
//                Object value = connection.getSender().getValueObject().
//                        getTypeRepresentation().getValue();
//
//                this.getValueObject().getTypeRepresentation().setValue(value);
//            }
//        }
//    }

    @Override
    public void connectionAdded(Connection connection) {
        //
    }

    @Override
    public void connectionRemoved(Connection connection) {

        // we generally ignore instances of InputValue and OutputValue
        boolean isInputOrOutputValue =
                (connection.getSender().getValueObject().
                getType().equals(InputValue.class)
                || connection.getSender().getValueObject().
                getType().equals(OutputValue.class));

        if (isInput() && !isInputOrOutputValue) {
            // delete value that is stored inside the
            // type representation
            getValueObject().setValue(null);

            getValueObject().setOutdated();
        }
    }

    @Override
    public Point getAbsPos() {
        Point location = super.getAbsPos();

        if (location == null) {
            return new Point(0, 0);
        }

        TypeRepresentationContainer valueObject = (TypeRepresentationContainer) getValueObject();
        CanvasWindow object = valueObject.getParentWindow();

        // if object representation is not visible it is necessary to compute
        // different x location
        if (!valueObject.getMethod().isVisible()
                || !valueObject.getMethod().getParentObject().isVisible()) {
            int x = location.x;

            if (isInput()) {
                x = object.getX() + object.getTitleBar().getX() + 5;
            } else {
                x = object.getX() + object.getTitleBar().getX()
                        + object.getTitleBar().getWidth() - object.getInsets().right;
            }
            location = new Point(x, location.y);
        }

        // if methods are not shown but only listed in the methodList inside
        // ObjectRepresentation it is necessary to compute different locations
        if (!valueObject.getMethod().isVisible()) {
            int y = object.getY() + object.getInsets().top
                    + object.getTitleBar().getHeight() / 2 - getHeight() / 2;
            int x = location.x;

            if (isInput()) {
                x = object.getX() + object.getTitleBar().getX() + 5;
            } else {
                x = object.getX() + object.getTitleBar().getX()
                        + object.getTitleBar().getWidth() - object.getInsets().right;
            }
            location = new Point(x, y);
        }

        return location;
    }
}
