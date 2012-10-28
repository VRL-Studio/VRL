/* 
 * AbstractTypeRepresentationReference.java
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

import eu.mihosoft.vrl.reflection.DefaultMethodRepresentation;
import eu.mihosoft.vrl.reflection.DefaultObjectRepresentation;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.reflection.TypeRepresentationContainer;
import eu.mihosoft.vrl.reflection.VisualCanvas;

/**
 * Abstract type representation references are used to define abstract ui
 * dependencies.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class AbstractTypeRepresentationReference {

    private Integer objectReprenentationID;
    private Integer methodID;
    private Integer parameterID;
    private Integer visualID;
    private Boolean isReturnValue;

    /**
     * Constructor.
     */
    public AbstractTypeRepresentationReference() {
    }

    /**
     * Constructor.
     * @param t the type representation to that is to be referenced
     */
    public AbstractTypeRepresentationReference(TypeRepresentationBase t) {
        DefaultObjectRepresentation objRep = t.getParentMethod().getParentObject();
        TypeRepresentationContainer tContainer =
                (TypeRepresentationContainer) t.getConnector().getValueObject();
        DefaultMethodRepresentation mRep = t.getParentMethod();
        setObjectReprenentationID(objRep.getObjectID());
        setMethodID(mRep.getID());
        setParameterID(tContainer.getID());
        setIsReturnValue(t.isOutput());
        setVisualID(t.getParentMethod().getParentObject().getID());
    }

    /**
     * Returns the type representation that is defined by this reference.
     * @param mainCanvas the main canvas
     * @return the type representation that is defined by this reference or
     *         <code>null</code> if no such type representation exists
     */
    public TypeRepresentationBase getReference(VisualCanvas mainCanvas) {

        TypeRepresentationBase result = null;

        try {
            DefaultMethodRepresentation m =
                    mainCanvas.getInspector().
                    getObjectRepresentations(getObjectReprenentationID()).getById(getVisualID()).
                    getMethods().getById(getMethodID());

            if (getIsReturnValue()) {
                result = m.getReturnValue();
            } else {
                result = m.getParameter(getParameterID());
            }
        } catch (Exception ex) {
            System.out.println(">> WARNING: abstract reference cannot be resolved!");
            System.out.println(">>> ------------------------");
            System.out.println(toString());
            System.out.println(">>> ------------------------");
        }

        return result;
    }

    /**
     * Returns the id of the object representation.
     * @return the id of the object representation
     */
    public Integer getObjectReprenentationID() {
        return objectReprenentationID;
    }

    /**
     * Defines the id of the object representation.
     * @param objectReprenentationID the id to set
     */
    public void setObjectReprenentationID(Integer objectReprenentationID) {
        this.objectReprenentationID = objectReprenentationID;
    }

    /**
     * Returns the id of the method representation.
     * @return the id of the method representation
     */
    public Integer getMethodID() {
        return methodID;
    }

    /**
     * Defines the id of the method representation.
     * @param methodID the id to set
     */
    public void setMethodID(Integer methodID) {
        this.methodID = methodID;
    }

    /**
     * Returns the id of the parameter (type representation).
     * @return the id of the parameter (type representation)
     */
    public Integer getParameterID() {
        return parameterID;
    }

    /**
     * Defines the parameter id of this reference.
     * @param parameterID the id to set
     */
    public void setParameterID(Integer parameterID) {
        this.parameterID = parameterID;
    }

    /**
     * Indicates whether the referenced type representation is a return type
     * representation.
     * @return <code>true</code> if the referenced type representation is a
     * return type representation; <code>false</code> otherwise
     */
    public Boolean getIsReturnValue() {
        return isReturnValue;
    }

    /**
     * Defines if the referenced type representation is a return type
     * representation.
     * @param isReturnValue the state to set
     */
    public void setIsReturnValue(Boolean isReturnValue) {
        this.isReturnValue = isReturnValue;
    }

    @Override
    public String toString() {
        return super.toString() + "\n"
                + "- objectRepresentationID: " + objectReprenentationID + "\n"
                + "- methodID: " + methodID + "\n"
                + "- parameterID: " + parameterID + "\n"
                + "- isReturnValue: " + isReturnValue;

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
}
