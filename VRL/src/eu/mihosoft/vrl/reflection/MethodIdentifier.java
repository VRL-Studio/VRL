/* 
 * MethodIdentifier.java
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

import java.util.Arrays;

/**
 * Identifies a method.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class MethodIdentifier {

    private Integer objectID;
    private Integer methodID;
    private String methodName;
    private String[] parameterTypeNames;
    private Integer visualID;
    private Integer visualMethodID;

    /**
     * Constructor.
     */
    public MethodIdentifier() {
        //
    }

    /**
     * Constructor.
     *
     * @param mDesc the method representation that defines the method.
     */
    public MethodIdentifier(
            DefaultMethodRepresentation mRep) {

        MethodDescription mDesc = mRep.getDescription();

        setObjectID(mDesc.getObjectID());
        setMethodID(mDesc.getMethodID());
        setMethodName(mDesc.getMethodName());

        // we use only class names instead of classes as comparisons may
        // fail if the requested class is temporarily unavailable
        String[] tmp = new String[mDesc.getParameterTypes().length];
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = mDesc.getParameterTypes()[i].getName();
        }

        setParameterTypeNames(tmp);

        setVisualID(mRep.getParentObject().getID());
        setVisualMethodID(mRep.getVisualMethodID());
    }

    /**
     * Constructor.
     *
     * @param mDesc the method description that defines the method.
     */
    public MethodIdentifier(
            MethodDescription mDesc, int visualID, int visualMethodID) {
        setObjectID(mDesc.getObjectID());
        setMethodID(mDesc.getMethodID());
        setMethodName(mDesc.getMethodName());

        // we use only class names instead of classes as comparisons may
        // fail if the requested class is temporarily unavailable
        String[] tmp = new String[mDesc.getParameterTypes().length];
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = mDesc.getParameterTypes()[i].getName();
        }

        setParameterTypeNames(tmp);

        setVisualID(visualID);
        setVisualMethodID(visualMethodID);
    }

    /**
     * Returns the object id.
     *
     * @return the objectID
     */
    public Integer getObjectID() {
        return objectID;
    }

    /**
     * Defines the object id.
     *
     * @param objectID the id to set
     */
    public final void setObjectID(Integer objectID) {
        this.objectID = objectID;
    }

    /**
     * Returns the method id.
     *
     * @return the method id
     */
    public Integer getMethodID() {
        return methodID;
    }

    /**
     * Defines the method id.
     *
     * @param methodID the method id to set
     */
    public final void setMethodID(Integer methodID) {
        this.methodID = methodID;
    }

    /**
     * Returns the method name.
     *
     * @return the method name
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Defines the method name.
     *
     * @param methodName the method name to set
     */
    public final void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Returns the parameter types
     *
     * @return the parameter types
     */
    public String[] getParameterTypeNames() {
        return parameterTypeNames;
    }

    /**
     * Defines the parameter types.
     *
     * @param parameterTypes the parameter types to set
     */
    public final void setParameterTypeNames(String[] parameterTypeNames) {
        this.parameterTypeNames = parameterTypeNames;
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
    public final void setVisualID(Integer visualID) {
        this.visualID = visualID;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof MethodIdentifier)) {
            return false;
        }

        return getSignature().equals(((MethodIdentifier) o).getSignature());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.methodName != null ? this.methodName.hashCode() : 0);
        hash = 23 * hash + Arrays.deepHashCode(this.parameterTypeNames);
        hash = 23 * hash + (this.visualID != null ? this.visualID.hashCode() : 0);
        hash = 23 * hash + (this.visualMethodID != null ? this.visualMethodID.hashCode() : 0);
        return hash;
    }

    private String getSignature() {
        String paramTypeString = "";

        if (parameterTypeNames != null) {
            for (String p : parameterTypeNames) {
                paramTypeString += p + ";";
            }
        } else {
            System.err.println(">> MethodIdentifier.getSignature(): "
                    + "parameterTypeNames == null!");
        }

        String methodString = "v-id:" + getVisualID()
                + ";v-method-id:" + getVisualMethodID() + ";"
                + getMethodName() + ";" + paramTypeString + ";";

        return methodString;
    }
    
    @Override
    public String toString() {
        return getSignature();
    }

    /**
     * @return the visualMethodID
     */
    public Integer getVisualMethodID() {
        return visualMethodID;
    }

    /**
     * @param visualMethodID the visualMethodID to set
     */
    public void setVisualMethodID(Integer visualMethodID) {
        this.visualMethodID = visualMethodID;
    }
}
