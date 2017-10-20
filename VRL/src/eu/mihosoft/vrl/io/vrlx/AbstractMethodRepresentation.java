/* 
 * AbstractMethodRepresentation.java
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

package eu.mihosoft.vrl.io.vrlx;

import eu.mihosoft.vrl.reflection.CustomParamData;
import eu.mihosoft.vrl.reflection.DefaultMethodRepresentation;
import eu.mihosoft.vrl.reflection.TypeRepresentation;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.visual.MessageType;
import java.util.ArrayList;

/**
 * Abstract representation of a method. This class is only used for XML
 * serialization.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class AbstractMethodRepresentation {

    /**
     * the data of the parameter values
     */
    private ArrayList<AbstractParameter> valueData;
    /**
     * the data of the return value
     */
    private AbstractParameter returnValueData;
    /**
     * defines whether the method is visible
     */
    private Boolean visibility;
    /**
     * the method id
     */
    private Integer methodId;
    /**
     * defines the parameter types
     */
    private Class[] parameterTypes;
    /**
     *
     */
    private ArrayList<String> parameterTypeNames;
    /**
     * defines the method name
     */
    private String methodName;
    /**
     * defines whether the method is minimized
     */
    private boolean minimized;
    /**
     * parameter groups
     */
    private AbstractParameterGroups parameterGroup;
    
    private Integer visualMethodID;

    /**
     * Constructor.
     */
    public AbstractMethodRepresentation() {
        //
        valueData = new ArrayList<AbstractParameter>();
        parameterTypeNames = new ArrayList<String>();
        parameterGroup = new AbstractParameterGroups();
    }

    /**
     * Constructor. <p> Creates an AbstractMethodRepresentation object from a
     * method representation </p>
     *
     * @param mRep the method representation
     */
    public AbstractMethodRepresentation(DefaultMethodRepresentation mRep) {
        valueData = new ArrayList<AbstractParameter>();
        parameterTypeNames = new ArrayList<String>();
        parameterGroup = new AbstractParameterGroups(mRep.getParameterGroup());

        String methodApiName = mRep.getDescription().getMethodName() + "(...)";

        for (TypeRepresentationBase t : mRep.getParameters()) {
            try {
                valueData.add(new AbstractParameter((TypeRepresentationBase) t));
            } catch (Exception ex) {

                ex.printStackTrace(System.err);

                String msgTitle = "Errors while saving Parameter(s):";
                String msg = ">> Cannot save Parameter(s) of method \""
                        + methodApiName+ "\"";

                System.err.println(">> " + msgTitle);
                System.err.println(" --> " + msg);
                System.err.println(" --> still proceeding...");

                if (mRep.getMainCanvas() != null) {
                    mRep.getMainCanvas().getMessageBox().
                            addMessage(
                            msgTitle, msg,
                            t.getConnector(),
                            MessageType.WARNING);
                }

            }
        }

        setReturnValueData(new AbstractParameter(mRep.getReturnValue()));

        setVisibility(mRep.isVisible());
        setMethodId(mRep.getID());
        setVisualMethodID(mRep.getVisualMethodID());
        setMethodName(mRep.getDescription().getMethodName());
        setMinimized(mRep.isMinimized());

        setParameterTypes(mRep.getDescription().getParameterTypes());

        for (Class<?> c : parameterTypes) {
            parameterTypeNames.add(c.getName());
        }
    }

    /**
     * Returns the value data.
     *
     * @return the value data (parameter values)
     */
    public ArrayList<AbstractParameter> getValueData() {
        return valueData;
    }

    /**
     * Defines the value data.
     *
     * @param valueData the value data to set
     */
    public void setValueData(ArrayList<AbstractParameter> valueData) {
        this.valueData = valueData;
    }

    /**
     * Indicates whether the method representation is visible.
     *
     * @return <code>true</code> if the method representation is visible;
     * <code>false</code> otherwise
     */
    public Boolean getVisibility() {
        return visibility;
    }

    /**
     * Defines whether the method representation shall be visible.
     *
     * @param visibility the visibility to set
     */
    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

    /**
     * Assigns properties to method representation. This method is used to
     * assign properties loaded from XML session file.
     *
     * @param m the method representation that is associated with this abstract
     * method representation
     */
    public void assignProperties(DefaultMethodRepresentation m) {

        System.out.println("****METHOD: " + m.getName());

        for (TypeRepresentationBase t : m.getParameters()) {
            System.out.println(" --> Param: " + t.getValueName());
        }
        
        m.setVisualMethodID(getVisualMethodID());

        getParameterGroup().assignProperties(m.getParameterGroup());

        ArrayList<Object> values =
                new ArrayList<Object>();
        ArrayList<String> valueOptions = new ArrayList<String>();
        ArrayList<CustomParamData> customParamDataList =
                new ArrayList<CustomParamData>();

        for (AbstractParameter p : this.getValueData()) {
            values.add(p.getValueObject());
            valueOptions.add(p.getValueOptions());
            customParamDataList.add(p.getCustomData());
        }

        for (int i = 0; i < valueOptions.size(); i++) {
            m.getParameter(i).setValueOptions(valueOptions.get(i));
            m.getParameter(i).evaluateValueOptions();
        }

        m.setParameters(values);

        for (int i = 0; i < customParamDataList.size(); i++) {

            CustomParamData paramData = customParamDataList.get(i);

            // to support old versions (pre 3.8.11, 03.05.2010) we create an
            // empty custom paramDataObject to prevent null pointer exceptions
            if (paramData != null) {
                m.getParameter(i).setCustomData(paramData);
            }

            m.getParameter(i).setCustomData(customParamDataList.get(i));
            m.getParameter(i).evaluateCustomParamData();
        }


        m.getReturnValue().setValueOptions(
                returnValueData.getValueOptions());

        try {
            m.getReturnValue().evaluateValueOptions();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }


        Object returnValue = returnValueData.getValueObject();

        if (returnValue != null) {
            m.getReturnValue().setValue(returnValue);
            m.getReturnValue().setUpToDate(true);
        } else {
            m.getReturnValue().setUpToDate(false);
        }

        CustomParamData paramData = returnValueData.getCustomData();

        // to support old versions (pre 3.8.11, 03.05.2010) we create an
        // empty custom paramDataObject to prevent null pointer exceptions
        if (paramData != null) {
            m.getReturnValue().setCustomData(paramData);
        }

        try {
            m.getReturnValue().evaluateCustomParamData();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        if (getMinimized()) {
            m.minimize();
        }
        
        System.out.println(" --> DONE");
    }

    /**
     * Returns the method id.
     *
     * @return the method id
     */
    public Integer getMethodId() {
        return methodId;
    }

    /**
     * Defines the method id.
     *
     * @param methodId the method id to set
     */
    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }

    /**
     * Returns the data of the return value.
     *
     * @return the return value data
     */
    public AbstractParameter getReturnValueData() {
        return returnValueData;
    }

    /**
     * Defines the return value data.
     *
     * @param returnValueData the return value data to set
     */
    public void setReturnValueData(AbstractParameter returnValueData) {
        this.returnValueData = returnValueData;
    }

    /**
     * Returns the parameter types of this method.
     *
     * @return the parameter types
     */
    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * Defines the parameter types of this method.
     *
     * @param parameterTypes the parameter types to set
     */
    public void setParameterTypes(Class[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    /**
     * Returns the name of this method.
     *
     * @return the method name
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Defines the name of this method.
     *
     * @param methodName the method name to set
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Returns the parameter type names of this method.
     *
     * @return the parameter type names
     */
    public ArrayList<String> getParameterTypeNames() {
        return parameterTypeNames;
    }

    /**
     * Defines the parameter types of this method.
     *
     * @param parameterTypeNames the value to set
     */
    public void setParameterTypeNames(ArrayList<String> parameterTypeNames) {
        this.parameterTypeNames = parameterTypeNames;
    }

    /**
     * @return the minimized
     */
    public boolean getMinimized() {
        return minimized;
    }

    /**
     * @param minimized the minimized to set
     */
    public void setMinimized(boolean minimized) {
        this.minimized = minimized;
    }

    /**
     * @return the parameterGroups
     */
    public AbstractParameterGroups getParameterGroup() {
        return parameterGroup;
    }

    /**
     * @param parameterGroups the parameterGroups to set
     */
    public void setParameterGroup(AbstractParameterGroups parameterGroup) {

        this.parameterGroup = parameterGroup;
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
