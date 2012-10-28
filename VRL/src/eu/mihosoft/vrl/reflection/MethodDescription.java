/* 
 * MethodDescription.java
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

import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.types.VisualIDRequest;
import java.lang.annotation.Annotation;

/**
 * Describes a method with all informations accessible from the Java Reflection
 * API, except annotations.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public final class MethodDescription {

    private int objectID;
    private int methodID;
    private String methodName;
    private String methodTitle;
    private Object[] parameters;
    private String[] parameterNames;
    private Class[] parameterTypes;
    private ParamInfo[] paramInfos;
    private ParamGroupInfo[] paramGroupInfos;
    private Object returnValue;
    private String returnValueName;
    private Class returnType;
    private boolean interactive;
    private MethodInfo methodInfo;
    private OutputInfo outputInfo;
    private MethodType methodType = MethodType.DEFAULT;

    /**
     * Constructor.
     */
    public MethodDescription() {
        setParameters(new Object[]{});
        setParameterNames(new String[]{});
        setParameterTypes(new Class[]{});
        setParamInfos(new ParamInfo[]{});

        setInteractive(true);
    }

    /**
     * Constructor.
     *
     * @param methodName the method name
     */
    public MethodDescription(String methodName) {
        setMethodName(methodName);
        setMethodTitle(methodName);

    }

    /**
     * Constructor.
     *
     * @param objectID the ID of the object
     * @param methodID the ID of the method
     * @param methodName the name of the method
     * @param methodTitle the title of the method
     * @param parameters the parameters of the method
     * @param parameterTypes the parameter types of the method
     * @param parameterNames the names of the parameters
     * @param paramInfos parameter annotations
     * @param returnType the return type of the method
     * @param returnValueName the name of the return value representation
     * @param interactive defines whether method is displayed in interactive
     * mode
     * @param methodInfo method annotation
     */
    public MethodDescription(int objectID, int methodID, String methodName,
            String methodTitle,
            Object[] parameters,
            Class[] parameterTypes,
            String[] parameterNames,
            ParamInfo[] paramInfos,
            ParamGroupInfo[] paramGroupInfos,
            Class returnType,
            String returnValueName,
            boolean interactive,
            MethodInfo methodInfo,
            OutputInfo outputInfo) {

        setObjectID(objectID);
        setMethodID(methodID);
        setMethodName(methodName);
        setMethodTitle(methodTitle);
        setParameters(parameters);
        setParameterNames(parameterNames);
        setParameterTypes(parameterTypes);
        setParamInfos(paramInfos);
        setParamGroupInfos(paramGroupInfos);
        setReturnType(returnType);
        setReturnValueName(returnValueName);
        setInteractive(interactive);
        setMethodInfo(methodInfo);
        setOutputInfo(outputInfo);
    }

    /**
     * Returns the ID of the object the method belongs to.
     *
     * @return the ID of the object the method belongs to
     */
    public int getObjectID() {
        return objectID;
    }

    /**
     * Defines the ID of the object the method belongs to.
     *
     * @param objectID the ID of the object the method belongs to
     */
    public void setObjectID(int objectID) {
        this.objectID = objectID;
    }

    /**
     * Returns the method ID.
     *
     * @return the method ID
     */
    public int getMethodID() {
        return methodID;
    }

    /**
     * Defines the method ID.
     *
     * @param methodID the method ID
     */
    public void setMethodID(int methodID) {
        this.methodID = methodID;
    }

    /**
     * Returns the name of the method.
     *
     * @return the name of the method
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Defines the name of the method.
     *
     * @param methodName the name of the method
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Returns the parameters of the method.
     *
     * @return the parameters of the method
     */
    public Object[] getParameters() {
        return parameters;
    }

    /**
     * Defines the parameters of the method.
     *
     * @param parameters the parameters of the method
     */
    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    /**
     * Returns the parameter types of the method.
     *
     * @return the parameter types of the method
     */
    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * Defines the parameter types of the method.
     *
     * @param parameterTypes the parameter types of the method
     */
    public void setParameterTypes(Class[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    /**
     * Returns the return value of the method.
     *
     * @return the return value of the method
     */
    public Object getReturnValue() {
        return returnValue;
    }

    /**
     * Defines the return value of the method.
     *
     * @param returnValue the return value of the method
     */
    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    /**
     * Returns the return type of the method.
     *
     * @return the return type of the method
     */
    public Class getReturnType() {
        return returnType;
    }

    /**
     * Defines the return type of the method.
     *
     * @param returnType the return type of the method
     */
    public void setReturnType(Class returnType) {
        this.returnType = returnType;
    }

    /**
     * Defines the names of the parameters.
     *
     * @param parameterNames the names of the parameters
     */
    public void setParameterNames(String[] parameterNames) {
        this.parameterNames = parameterNames;
    }

    /**
     * Returns the parameter names.
     *
     * @return the parameter names
     */
    public String[] getParameterNames() {
        return this.parameterNames;
    }

    /**
     * Indicates whether method is to be displayed in interactive mode.
     *
     * @return <code>true</code> if method is to be displayed in interactive
     * mode; <code>false</code> otherwise
     */
    public boolean isInteractive() {
        return interactive;
    }

    /**
     * Defines whether the method is to be displayed in interactive mode.
     *
     * @param interactive <code>true</code> if method is to be displayed in
     * interactive mode; <code>false</code> otherwise
     */
    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    /**
     * Returns the title of the method.
     *
     * @return the title of the method
     */
    public String getMethodTitle() {
        return methodTitle;
    }

    /**
     * Defines the title of the method.
     *
     * @param methodTitle the title of the method
     */
    public void setMethodTitle(String methodTitle) {
        this.methodTitle = methodTitle;
    }

    /**
     * Returns the parameter informations.
     *
     * @return the parameter informations
     */
    public ParamInfo[] getParamInfos() {
        return paramInfos;
    }

    /**
     * Defines the parameter informations
     *
     * @param paramInfos the informations to set
     */
    public void setParamInfos(ParamInfo[] paramInfos) {
        this.paramInfos = paramInfos;
    }

    /**
     * Returns the parameter group informations.
     *
     * @return the parameter informations
     */
    public ParamGroupInfo[] getParamGroupInfos() {
        return paramGroupInfos;
    }

    /**
     * Defines the parameter group informations
     *
     * @param paramInfos the informations to set
     */
    public void setParamGroupInfos(ParamGroupInfo[] paramGroupInfos) {
        this.paramGroupInfos = paramGroupInfos;
    }

    /**
     * Returns the method information.
     *
     * @return the method information
     */
    public MethodInfo getMethodInfo() {
        return methodInfo;
    }

    /**
     * Defines the method information.
     *
     * @param methodInfo the method information
     */
    public void setMethodInfo(MethodInfo methodInfo) {
        this.methodInfo = methodInfo;
    }

//    private void writeObject(ObjectOutputStream oos) throws IOException {
//
//        returnValue = null;
//        parameters = null;
//
//        oos.defaultWriteObject();
//    }
    /**
     * Returns the return value name.
     *
     * @return the return value name
     */
    public String getReturnValueName() {
        return returnValueName;
    }

    /**
     * Defines the return value name
     *
     * @param returnValueName the name to set
     */
    public void setReturnValueName(String returnValueName) {
        this.returnValueName = returnValueName;
    }

    public MethodIdentifier toMethodIdentifier(int visualID) {
        return new MethodIdentifier(this, visualID);
    }

    @Override
    public String toString() {
//        return getMethodName() + "()";

        return getSignature();
    }

    public String getSignature() {
        String result = "<html><b>" + getMethodName() + "(</b>";

        boolean first = true;

        for (int i = 0; i < parameterTypes.length; i++) {

            Class<?> p = parameterTypes[i];

            // visual idrequest in not of interest for average users
            if (VisualIDRequest.class.isAssignableFrom(p)) {
                continue;
            }

            if (!first) {
                result += ", ";

            }

            first = false;

            result += TypeUtil.getParamTypeName(paramInfos[i], p);
        }

        result += "<b>)</b> : "
                + TypeUtil.getReturnTypeName(getMethodInfo(), getReturnType())
                + "</html>";

        return result;
    }

    /**
     * @return the methodType
     */
    MethodType getMethodType() {
        return methodType;
    }

    /**
     * @param methodType the methodType to set
     */
    void setMethodType(MethodType methodType) {
        this.methodType = methodType;
    }

    /**
     * Creates a reference method description.
     *
     * @param objID
     * @param objectType
     * @return
     */
    public static MethodDescription createReferenceMethod(
            int objID, Class<?> objectType) {

        MethodInfo mInfo = new MethodInfo() {
            @Override
            public String name() {
                return "this";
            }

            @Override
            public boolean interactive() {
                return true;
            }

            @Override
            public boolean hide() {
                return true;
            }

            @Override
            public boolean ignore() {
                return false;
            }

            @Override
            public boolean noGUI() {
                return false;
            }

            @Override
            public String valueName() {
                return "ref";
            }

            @Override
            public String valueStyle() {
                return "default";
            }

            @Override
            public boolean inheritGUI() {
                return false;
            }

            @Override
            public String callOptions() {
                return "";
            }

            @Override
            public String valueOptions() {
                return "";
            }

            @Override
            public String buttonText() {
                return "invoke";
            }

            @Override
            public boolean initializer() {
                return false;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String valueTypeName() {
                return "";
            }

            @Override
            public boolean askIfClose() {
                return false;
            }

            @Override
            public boolean hideCloseIcon() {
                return false;
            }

            @Override
            public String propertyOf() {
                return "";
            }

            @Override
            public String parentName() {
                return "";
            }
        };

        MethodDescription referenceMethod = new MethodDescription(
                objID, // object id
                0, // method id will be defined later
                "this", // method name
                "this", // method title
                new Object[0], // params
                new Class<?>[]{objectType}, // param types
                new String[]{"ref"}, // param names
                new ParamInfo[]{new ParamInfo() {
                @Override
                public String name() {
                    return "ref";
                }

                @Override
                public String style() {
                    return "default";
                }

                @Override
                public boolean nullIsValid() {
                    return true;
                }

                @Override
                public String options() {
                    return "";
                }

                @Override
                public String typeName() {
                    return "";
                }

                @Override
                public Class<? extends Annotation> annotationType() {
                    return null;
                }
            }}, // param infos
                new ParamGroupInfo[]{new ParamGroupInfo() {
                @Override
                public String group() {
                    return "default";
                }

                @Override
                public Class<? extends Annotation> annotationType() {
                    return null;
                }
            }}, // param group infos
                objectType, // return type
                "ref", // return value name
                true, // interactive
                // no method info
                mInfo,
                new OutputInfo() {
                    @Override
                    public String[] elemNames() {
                        return new String[0];
                    }

                    @Override
                    public String[] elemStyles() {
                        return new String[0];
                    }

                    @Override
                    public String[] elemOptions() {
                        return new String[0];
                    }

                    @Override
                    public String[] elemTypeNames() {
                        return new String[0];
                    }

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return null;
                    }

                    @Override
                    public String name() {
                        return "";
                    }

                    @Override
                    public String style() {
                        return "default";
                    }

                    @Override
                    public String options() {
                        return "";
                    }

                    @Override
                    public String typeName() {
                        return "";
                    }

                    @Override
                    public Class<?>[] elemTypes() {
                        return new Class<?>[0];
                    }
                });

        referenceMethod.setMethodType(MethodType.REFERENCE);

        return referenceMethod;
    }

    /**
     * @return the outputInfo
     */
    public OutputInfo getOutputInfo() {
        return outputInfo;
    }

    /**
     * @param outputInfo the outputInfo to set
     */
    public void setOutputInfo(OutputInfo outputInfo) {
        this.outputInfo = outputInfo;
    }
}
