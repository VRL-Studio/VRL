/* 
 * SessionClassUtils.java
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
package eu.mihosoft.vrl.lang.visual;

import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.io.vrlx.AbstractCode;
import eu.mihosoft.vrl.lang.CodeBuilder;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.lang.groovy.GroovyCompiler;
import eu.mihosoft.vrl.reflection.DefaultMethodRepresentation;
import eu.mihosoft.vrl.reflection.DefaultObjectRepresentation;
import eu.mihosoft.vrl.reflection.MethodDescription;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.reflection.TypeRepresentationContainer;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.reflection.VisualObject;
import eu.mihosoft.vrl.system.VClassLoaderUtil;
import eu.mihosoft.vrl.system.VParamUtil;
import eu.mihosoft.vrl.types.ArrayBaseType;
import eu.mihosoft.vrl.types.MultipleOutputType;
import eu.mihosoft.vrl.visual.CanvasWindow;
import eu.mihosoft.vrl.visual.Connection;
import eu.mihosoft.vrl.visual.Connections;
import eu.mihosoft.vrl.visual.Connector;
import eu.mihosoft.vrl.visual.ConnectorType;
import eu.mihosoft.vrl.visual.MessageType;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converts an opened session to valid Java/Groovy source code.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class SessionClassUtils {

    // no instanciation allowed
    private SessionClassUtils() {
        throw new AssertionError(); // not in this class either!
    }

    /**
     * Analyzes the specified canvas and converts its content to source code.
     *
     * @param canvas canvas to analyze
     * @return source code
     */
    public static AbstractCode createSessionClassCode(VisualCanvas canvas) {
        return createSessionClassCode(canvas, null);
    }

    /**
     * Analyzes the specified canvas and converts its content to source code.
     *
     * @param canvas canvas to analyze
     * @return source code
     */
    public static AbstractCode createSessionClassCode(VisualCanvas canvas,
            ClassInfoObject info) {

        boolean pulseEffectState = canvas.getEffectPane().isPulseEffect();

        canvas.getEffectPane().enablePulseEffect(false);

        if (!validateSession(canvas)) {
            return null;
        }

        invokeInputsAndOutputs(canvas);

        ClassInfoObject classInfo = info;

        if (classInfo == null) {
            classInfo = getClassInfo(canvas);
            invokeInfoObject(canvas);
        }

        CodeBuilder code = new CodeBuilder();

        // add imports
        Iterable<String> imports = new GroovyCompiler().getImports();

        for (String imp : imports) {
            code.append(imp);
        }

        code.newLine().newLine();

        code.addLine("@ComponentInfo(" + classInfo.getComponentInfo() + ")").
                addLine("@ObjectInfo(" + classInfo.getObjectInfo() + ")").
                addLine("public class " + classInfo.getClassName()
                        + " implements Serializable {").
                incIndentation().
                addLine("private static final long serialVersionUID=1L;").
                addLine(
                        getMethodHeaderCode(getOutputParameter(canvas),
                                classInfo.getMethodInfo(),
                                classInfo.getMethodName(),
                                getInputParameter(canvas),
                                code.getIndentString())
                        + " {").
                incIndentation().
                addLine("").
                addLine(getBodyCode(canvas, code.getIndentString())).
                decIndentation().
                addLine("}").
                decIndentation().
                addLine("}");

        AbstractCode result = new AbstractCode();

        result.setPackageName(classInfo.getPackageName());

        StringBuilder builder = new StringBuilder(code.getCode());

        for (AbstractCode c : canvas.getCodes()) {
            builder.append("\n\n").append(c.getCode());
        }

        result.setCode(builder.toString());

        canvas.getEffectPane().enablePulseEffect(pulseEffectState);

        return result;
    }

    /**
     * Validates the session.
     *
     * @param canvas
     * @return
     * <code>true</code> if the specified canvas session is valid;
     * <code>false</code> otherwise
     */
    private static boolean validateSession(VisualCanvas canvas) {

        if (!ControlFlowUtils.validateSession(canvas)) {
            return false;
        }

        return true;
    }

    /**
     * Returns a collection containing all input objects of the specified
     * canvas.
     *
     * @param canvas canvas
     * @return a collection containing all input objects of the specified canvas
     */
    private static Collection<InputObject> getInputs(VisualCanvas canvas) {
        Collection<Object> inputs
                = canvas.getInspector().
                getObjectsByClassName(InputObject.class.getName());

        ArrayList<InputObject> result = new ArrayList<InputObject>();

        for (Object o : inputs) {
            result.add((InputObject) o);
        }

        return result;
    }

    /**
     * Returns a collection containing all output objects of the specified
     * canvas.
     *
     * @param canvas canvas
     * @return a collection containing all output objects of the specified
     * canvas
     */
    private static OutputObject getOutput(VisualCanvas canvas) {
        Collection<Object> outputs
                = canvas.getInspector().
                getObjectsByClassName(OutputObject.class.getName());

        if (!outputs.isEmpty()) {
            return (OutputObject) outputs.iterator().next();
        }

        return null;
    }

    /**
     * Indicates whether an output object exists.
     *
     * @param canvas canvas
     * @return
     * <code>true</code> if an output object exists;
     * <code>false</code> otherwise
     */
    private static boolean outputExists(VisualCanvas canvas) {
        return getOutput(canvas) != null;
    }

    /**
     * Returns the class info object of the specified canvas.
     *
     * @param canvas canvas
     * @return the class info object of the specified canvas or
     * <code>null</code> if no such object exists
     */
    private static ClassInfoObject getClassInfo(VisualCanvas canvas) {
        Collection<Object> classInfoObjects
                = canvas.getInspector().
                getObjectsByClassName(ClassInfoObject.class.getName());

        if (!classInfoObjects.isEmpty()) {
            return (ClassInfoObject) classInfoObjects.iterator().next();
        }

        return null;
    }

    /**
     * Indicates whether a class info object exists.
     *
     * @param canvas canvas
     * @return
     * <code>true</code> if an class info object exists;
     * <code>false</code> otherwise
     */
    private static boolean classInfoExists(VisualCanvas canvas) {
        return getClassInfo(canvas) != null;
    }

    /**
     * Invokes the methods of all input and output objects. This ensures that
     * the objects use the data the user has entered in the text fields of the
     * type representations.
     *
     * @param canvas canvas
     */
    private static void invokeInputsAndOutputs(VisualCanvas canvas) {

        // invoke inputs
        for (InputObject o : getInputs(canvas)) {

            // multiple views false, thus we can savely use get(0)
            DefaultObjectRepresentation oRep
                    = canvas.getInspector().
                    getObjectRepresentationsByReference(o).get(0);

            DefaultMethodRepresentation m
                    = oRep.getMethodBySignature(
                            "info", new Class<?>[]{String.class});

            if (m != null) {
                try {
                    m.automaticInvocation(canvas.getInspector());
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(SessionClassUtils.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }
        }

        if (outputExists(canvas)) {
            // output
            // multiple views false, thus we can savely use get(0)
            DefaultObjectRepresentation oRep
                    = canvas.getInspector().
                    getObjectRepresentationsByReference(
                            getOutput(canvas)).get(0);

            DefaultMethodRepresentation m1
                    = oRep.getMethodBySignature(
                            "info",
                            new Class<?>[]{String.class, OutputValue.class});
            try {
                m1.automaticInvocation(canvas.getInspector());
            } catch (InvocationTargetException ex) {
                Logger.getLogger(SessionClassUtils.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Invokes the methods of the class info object. This ensures that the
     * objects use the data the user has entered in the text fields of the type
     * representations.
     *
     * @param canvas canvas
     */
    public static void invokeInfoObject(VisualCanvas canvas) {
        if (classInfoExists(canvas)) {
            // class-info
            // multiple views false, thus we can savely use get(0)
            DefaultObjectRepresentation oRep
                    = canvas.getInspector().
                    getObjectRepresentationsByReference(
                            getClassInfo(canvas)).get(0);

//            DefaultMethodRepresentation m1 =
//                    oRep.getMethodBySignature(
//                    "info", new Class<?>[]{
//                String.class, String.class, String.class});
            DefaultMethodRepresentation m2
                    = oRep.getMethodBySignature(
                            "additionalInfo",
                            new Class<?>[]{
                                String.class, String.class,
                                String.class, String.class, String.class});
            try {
                //            m1.automaticInvocation(canvas.getInspector());
                m2.automaticInvocation(canvas.getInspector());
            } catch (InvocationTargetException ex) {
                Logger.getLogger(SessionClassUtils.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Returns the type of the input connector the specified input object is
     * connected to.
     *
     * @param canvas canvas
     * @param input input object to check
     * @return the type of the input connector the specified input object is
     * connected to or
     * <code>void.class</code> if it is not connected
     */
    private static Class<?> getInputType(VisualCanvas canvas,
            InputObject input) {

        Class<?> result = void.class;

        Connection connection = getInputConnection(canvas, input);

        // if connection exist add the receiver to the result collection
        if (connection != null) {
            // we can use first one because we only allow one connection
            result = connection.getReceiver().
                    getValueObject().getType();
        }

        return result;
    }

    /**
     * Returns the connection of the specified input object.
     *
     * @param canvas canvas
     * @param input input object
     * @return the connection of the specified input object or
     * <code>null</code> if no such connection exists
     */
    private static Connection getInputConnection(VisualCanvas canvas,
            InputObject input) {

        Connection result = null;

        // multiple views false, thus we can savely use get(0)
        DefaultObjectRepresentation oRep
                = canvas.getInspector().
                getObjectRepresentationsByReference(input).get(0);

        // get the method representation
        DefaultMethodRepresentation m
                = oRep.getMethodBySignature(
                        "info", new Class<?>[]{String.class});

        // take the return value connector and get the connections
        Connector c = m.getReturnValue().getConnector();
        Collection<Connection> connections
                = canvas.getDataConnections().getAllWith(c);

        // if connection exist return it
        if (!connections.isEmpty()) {
            // we can use first one because we only allow one connection
            result = connections.iterator().next();
        }

        return result;
    }

    /**
     * Returns the parameter object of the specified input object.
     *
     * @param canvas canvas
     * @return the parameter object of the specified input object
     */
    private static Collection<Parameter> getInputParameter(
            VisualCanvas canvas) {
        ArrayList<Parameter> result = new ArrayList<Parameter>();

        Collection<InputObject> inputs = getInputs(canvas);

        int paramIndex = 0;

        for (InputObject iO : inputs) {

            Class<?> infoType = getInputType(canvas, iO);

            if (!infoType.equals(void.class)) {
                result.add(new Parameter(
                        infoType, "p" + paramIndex,
                        iO.getParamInfo(), getInputConnection(canvas, iO)));
                paramIndex++;
            }
        }

        return result;
    }

    /**
     * Returns the parameter object of the specified output object
     *
     * @param canvas canvas
     * @return the parameter object of the specified output object
     */
    private static Parameter getOutputParameter(VisualCanvas canvas) {

        Connector outputSender = getOutputSender(canvas);

        if (!outputExists(canvas) || outputSender == null) {
            return new Parameter(void.class, "", "", null);
        }

        Class<?> type = outputSender.getValueObject().getType();

        OutputObject output = getOutput(canvas);

        String paramInfo = output.getParamInfo();

        // we use the method info from the sender if no custom info is defined
        if (paramInfo == null || paramInfo.trim().equals("")) {

            paramInfo = "";

            TypeRepresentationBase tRep = ((TypeRepresentationContainer) outputSender.getValueObject()).getTypeRepresentation();

            MethodDescription mDesc = tRep.getParentMethod().getDescription();

            if (mDesc.getMethodInfo() != null) {
                String valueName = mDesc.getMethodInfo().valueName();
                String valueStyle = mDesc.getMethodInfo().valueStyle();
                String valueOptions = mDesc.getMethodInfo().valueOptions();

                paramInfo = "valueName=\""
                        + VLangUtils.addEscapeCharsToCode(valueName) + "\", ";
                paramInfo += "valueStyle=\""
                        + VLangUtils.addEscapeCharsToCode(valueStyle) + "\", ";
                paramInfo += "valueOptions=\""
                        + VLangUtils.addEscapeCharsToCode(valueOptions) + "\"";
            }
        }

        // get(0) is save because only one connection is allowed
        Connection connection
                = canvas.getDataConnections().
                getAllWith(getOutputReceiver(canvas)).get(0);

        return new Parameter(
                type, "", paramInfo, connection);
    }

    /**
     * Returns the sender connector of the connection to the output object.
     *
     * @param canvas canvas
     * @return the sender connector of the connection to the output object or
     * <code>null</code> if no such connection exists
     */
    private static Connector getOutputSender(
            VisualCanvas canvas) {

        Connector result = null;

        Connector c = getOutputReceiver(canvas);

        Collection<Connection> connections
                = canvas.getDataConnections().getAllWith(c);

        // if connection exist return the sender as result
        if (!connections.isEmpty()) {
            // we can use 0 because we only allow one connection
            result = connections.iterator().next().getSender();
        }

        return result;
    }

    /**
     * Returns the input connector of the output object.
     *
     * @param canvas canvas
     * @return the input connector of the output object or
     * <code>null</code> if the specified canvas does not contain an output
     * object
     */
    private static Connector getOutputReceiver(
            VisualCanvas canvas) {

        // if no outputs exist we can exit
        if (!outputExists(canvas)) {
            return null;
        }

        // invoke inputs
        OutputObject o = getOutput(canvas);

        // multiple views false, thus we can savely use get(0)
        DefaultObjectRepresentation oRep
                = canvas.getInspector().
                getObjectRepresentationsByReference(o).get(0);

        // get the method representation
        DefaultMethodRepresentation m
                = oRep.getMethodBySignature(
                        "info",
                        new Class<?>[]{String.class, OutputValue.class});

        // take the input value connector and get the connections
        // connector of outputvalue (see OutputObject)
        return m.getConnector(2);
    }

    /**
     * Returns the code of the specified method header.
     *
     * @param output output parameter
     * @param methodInfo method info string without enclosing
     * <code>@MethodInfo()</code>
     * @param methodName method name without left and right parenthesis
     * @param params input paramter list
     * @param indent indent to use (necessary for correct code formatting)
     * @return the code of the specified method header
     */
    private static String getMethodHeaderCode(Parameter output,
            String methodInfo, String methodName,
            Collection<Parameter> params, String indent) {

        StringBuilder builder = new StringBuilder();

        boolean methodInfoNeedsComma = methodInfo.length() > 0
                && output.paramInfo.length() > 0;

        builder. // methodInfo
                append("@MethodInfo(").append(methodInfo);

        if (methodInfoNeedsComma) {
            builder.append(", ");
        }

        String returnTypeString = "void";

        if (output.getType().isArray()) {
//            returnTypeString = output.type.getComponentType().getName() + "[]";
            returnTypeString = VClassLoaderUtil.arrayClass2Code(output.type.getName());
        } else {
            returnTypeString = output.type.getName();
        }

        builder.append(output.paramInfo).append(")\n").
                // return type
                append(indent).append("public ").append(returnTypeString).
                // method name
                append(" ").append(methodName);

        builder.append("( ");

        boolean firstRun = true;

        // method parameters
        for (Parameter p : params) {

            System.out.println("P: ");

            System.out.println(">> p-info: " + p.getParamInfo());

            if (firstRun) {
                firstRun = false;

            } else {
                builder.append(",");
            }

            builder.append("\n").append(indent).append("    ");

            String paramTypeString = "";

            if (p.getType().isArray()) {
//                paramTypeString = p.type.getComponentType().getName() + "[]";
                paramTypeString = VClassLoaderUtil.arrayClass2Code(p.type.getName());
            } else {
                paramTypeString = p.type.getName();
            }

            builder.append("@ParamInfo(").append(p.getParamInfo()).append(") ").
                    append(paramTypeString).append(" ").
                    append(p.getName());
        }

        builder.append(" )");

        return builder.toString();
    }

    /**
     * Returns the code that is resposible for object instanciation.
     *
     * @param objects objects
     * @param indent indent to use (necessary for correct code formatting)
     * @return the code that is resposible for object instanciation
     */
    private static String getObjectInstanciationCode(
            Collection<Object> objects, String indent) {

        StringBuilder builder = new StringBuilder("// instances\n");

        for (Object o : objects) {

            String className = o.getClass().getName();
            builder.append(indent).append("    ").
                    append(className).append(" ").
                    append(getInstanceName(objects, o)).
                    append(" = new ").append(className).append("();\n");
        }

        return builder.toString();
    }

    /**
     * Returns a unique variable name for the specified object.
     *
     * @param objects all instances managed by the current inspector
     * @param o object
     * @return a unique variable name for the specified object
     */
    private static String getInstanceName(
            Collection<Object> objects, Object o) {

        VParamUtil.throwIfNull(objects, o);

        int varIndex = 0;

        for (Object obj : objects) {
            if (obj == o) {
                break;
            }
            varIndex++;
        }

        return "obj" + varIndex;
    }

    /**
     * Returns a unique variable name for the specified connection.
     *
     * @param canvas canvas
     * @param connections data connections of the canvas
     * @param con connection
     * @return a unique variable name for the specified connection
     */
    private static String getVariableName(VisualCanvas canvas,
            Collection<Connection> connections, Connection con) {
        return getVariableName(canvas, connections, con.getSender());
    }

    /**
     * Returns a unique variable name for the specified connector. <p>
     * <b>Warning:</b> this method does not indicate if the specified arguments
     * are illegal. This may cause wrong results. </p>
     *
     * @param canvas canvas
     * @param connections data connections of the canvas
     * @param receiver connector (Input)
     * @return a unique variable name for the specified connector
     */
    private static String getVariableName(VisualCanvas canvas,
            Collection<Connection> connections, Connector receiver) {

        if (receiver.getType() != ConnectorType.OUTPUT) {
            throw new IllegalArgumentException(
                    "only output connectors supported!");
        }

        int varIndex = 0;

        // if we are connected to an input parameter use it instead
        // of an automatically generated variable name
        for (Parameter p : getInputParameter(canvas)) {
            if (p.getConnection().contains(receiver)) {
                return p.getName();
            }
        }

        for (Connection c : connections) {
            if (c.getSender().equals(receiver)) {
                break;
            }

            varIndex++;
        }

        return "v" + varIndex;
    }

    /**
     * Returns the variable declaration code. All variables are initialized with
     * <code>null</code>
     *
     * @param canvas canvas
     * @param connections data connections of the canvas
     * @param methods all methods that are part of the controlflow of the canvas
     * @param indent indent to use (necessary for correct code formatting)
     * @return returns the variable declaration code
     */
    private static String getVariableDeclarationCode(VisualCanvas canvas,
            Connections connections,
            Collection<DefaultMethodRepresentation> methods, String indent) {

        StringBuilder builder
                = new StringBuilder(indent);

        builder.append("// variable declarations\n").append(indent);

        for (DefaultMethodRepresentation method : methods) {

            boolean notVoid = !method.getReturnValue().getType().equals(void.class);
            boolean connected = connections.alreadyConnected(method.getReturnValue().getConnector());

            boolean checkForMultipleOutput = method.getReturnValue().getClass().equals(MultipleOutputType.class);
            boolean arraySubElementsAreConnected = false;
            ArrayList<TypeRepresentationContainer> connectedSubTypes = new ArrayList<TypeRepresentationContainer>();

            if (checkForMultipleOutput) {
//                 arraySubElementsAreConnected = connections.alreadyConnected();
                String msg = " ### -->> MultipleOutputType detected";
                System.out.println(msg);
                System.err.println(msg);

                MultipleOutputType mot = (MultipleOutputType) method.getReturnValue();

                ArrayList typeContainers = mot.getTypeContainers();

                for (int i = 0; i < typeContainers.size(); i++) {
                    System.out.println("viewValue[ " + i + " ].class " + typeContainers.get(i).getClass());

                    TypeRepresentationContainer trepContainer = (TypeRepresentationContainer) typeContainers.get(i);
                    TypeRepresentationBase trep = trepContainer.getTypeRepresentation();
                    System.out.println("trep.getName() = " + trep.getName());

                    Class typ = trepContainer.getType();
                    System.out.println("typ.getName() = " + typ.getName());

                    boolean isConnected = connections.alreadyConnected(trep.getConnector());
                    System.out.println("subConnector isConnected = " + isConnected);

                    if (isConnected) {
                        connectedSubTypes.add(trepContainer);
                        arraySubElementsAreConnected = isConnected;
                    }
                }

            }// if checkForMultipleOutput

            boolean methodReturnsData = notVoid && (connected || arraySubElementsAreConnected);

            // we only need to declare a variable if this method
            // returns something
            if (methodReturnsData) {

//                if (method.getReturnValue() instanceof ArrayBaseType) {
//                } else {
                String returnValueType = "";

                if (method.getReturnValue().getType().isArray()) {
//                    returnValueType =
//                            method.getReturnValue().getType().
//                            getComponentType().getName() + "[]";
                    returnValueType = VClassLoaderUtil.arrayClass2Code(
                            method.getReturnValue().getType().getName());

                    if (arraySubElementsAreConnected) {
                        for (int i = 0; i < connectedSubTypes.size(); i++) {

                            returnValueType = connectedSubTypes.get(i).getType().getName();
                            TypeRepresentationBase trep = connectedSubTypes.get(i).getTypeRepresentation();
                            Connector subConnector = trep.getConnector();
//                            System.out.println("trep.getValueAsCode() = \n"+ trep.getValueAsCode());

                            builder.append(returnValueType).
                                    append(" ").
                                    append(getVariableName(canvas, connections, subConnector)).
//                                    append(" = null;").
                                    append(" = " + trep.getValueAsCode() + ";").
                                    append("\n").append(indent);
                        }

                    }

                } else {
                    returnValueType
                            = method.getReturnValue().getType().getName();
                }

                builder.append(returnValueType).
                        append(" ").
                        append(getVariableName(canvas, connections,
                                        method.getReturnValue().getConnector())).
                        append(" = null;").
                        append("\n").append(indent);
//                }
            }
        }

        return builder.toString();
    }

    /**
     * Returns the method invocation code. Return values of connected methods
     * are stored in the previously defined variables (see
     * {@link #getVariableDeclarationCode(
     * eu.mihosoft.vrl.reflection.VisualCanvas,
     * eu.mihosoft.vrl.visual.Connections,
     * java.util.Collection, java.lang.String)} )
     *
     * @param method method to invoke
     * @param indent indent to use (necessary for correct code formatting)
     * @return method invocation code
     */
    private static String getMethodInvocationCode(
            DefaultMethodRepresentation method, String indent) {

        StringBuilder builder
                = new StringBuilder(indent);

        VisualCanvas canvas = (VisualCanvas) method.getMainCanvas();
        Connections connections = canvas.getDataConnections();

        // controlflow statements
        if (ControlFlowUtils.isMethodControlFlowStatement(method)) {
            try {
                method.automaticInvocation(canvas.getInspector());
            } catch (InvocationTargetException ex) {
                Logger.getLogger(SessionClassUtils.class.getName()).
                        log(Level.SEVERE, null, ex);
            }

            ControlFlowStatement statement
                    = ControlFlowUtils.getControlFlowStatement(
                            method);

            builder.append(statement.getCode()).append("\n");

            return builder.toString();
        }

        Object parentObject = canvas.getInspector().
                getObject(method.getParentObject().getObjectID());

        // if we are a reference method we only add our instance name and return
        // examples:
        // 1) if method's return value is assigned to a variable:
        //    v0 = o2;
        // 2) if method's return value is not assigned to a variable:
        //    o1 = o2;
        // 3) if method's input and output are connected:
        //    v1 = o1; o1 = o2;
        if (method.isReferenceMethod()) {

            boolean returnValueConnected
                    = method.getMainCanvas().getDataConnections().
                    alreadyConnected(method.getReturnValue().getConnector());

            boolean inputValuesConnected = method.getMainCanvas().
                    getDataConnections().
                    alreadyConnected(method.getParameter(0).getConnector());

            if (inputValuesConnected) {
                builder.append(getInstanceName(
                        canvas.getInspector().getObjects(),
                        parentObject)).append(" = ");

                // only one connection allowed. thus, get(0) is save
                Connection c
                        = connections.getAllWith(
                                method.getParameter(0).
                                getConnector()).get(0);

                builder.append(
                        getVariableName(canvas, connections, c)).
                        append(";");
            }

            // we add a newline if no code for input value assignment has to
            // be added
            if (inputValuesConnected && !returnValueConnected) {
                builder.append("\n");
            }

            if (returnValueConnected) {

                // only one connection allowed. thus, get(0) is save
                Connection c
                        = connections.getAllWith(
                                method.getReturnValue().
                                getConnector()).get(0);

                builder.append(getVariableName(canvas, connections, c)).
                        append(" = ");
                builder.append(getInstanceName(
                        canvas.getInspector().getObjects(),
                        parentObject)).append(";\n");
            }

            if (inputValuesConnected || returnValueConnected) {
                return builder.toString();
            } else {
                return "";
            }
        }

        // we are no reference method and thus we are still in this method
        boolean methodReturnsData = !method.getReturnValue().
                getType().equals(void.class)
                && connections.alreadyConnected(
                        method.getReturnValue().getConnector());

        // we only need to declare a variable if this method
        // returns something
        if (methodReturnsData) {
            builder.append(getVariableName(canvas, connections,
                    method.getReturnValue().getConnector())).append(" = ");
        }

        String methodName = getInstanceName(
                canvas.getInspector().getObjects(), parentObject)
                + "." + method.getDescription().getMethodName();

        builder.append(methodName).append("( ");

        ArrayList<Connector> doNotSupportCodeGeneration
                = new ArrayList<Connector>();

        // now we add the parameter values to the method
        boolean firstRun = true;
        for (TypeRepresentationBase tRep : method.getParameters()) {

            if (firstRun) {
                firstRun = false;

            } else {
                builder.append(", ");
            }

            boolean paramConnected
                    = connections.alreadyConnected(tRep.getConnector());

            if (paramConnected) {

                // only one connection allowed. thus, get(0) is save
                Connection c
                        = connections.getAllWith(tRep.getConnector()).get(0);

                builder.append(getVariableName(canvas, connections, c));
            } else if (tRep instanceof ArrayBaseType) {
                // check whether using ArrayBaseType because arrays are handled
                // differently (array connectors are never connected). We only
                // operate on the type representations of the array elements
                generateArrayBaseClassCode(builder,
                        canvas, connections,
                        doNotSupportCodeGeneration,
                        (ArrayBaseType) tRep);
            } else {

                String code = null;

                try {
                    code = tRep.getValueAsCode();
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                if (code == null) {

                    code = "null as "
                            + VClassLoaderUtil.arrayClass2Code(
                                    tRep.getType().getName());

                    if (tRep.isWarningIfNoCodeGeneration()) {
                        doNotSupportCodeGeneration.add(tRep.getConnector());
                    }
                }

                builder.append(code);
            }
//            }
        } // end for

        builder.append(" );\n");

        if (!doNotSupportCodeGeneration.isEmpty()) {
            for (Connector c : doNotSupportCodeGeneration) {
                canvas.getMessageBox().addMessage("Warning: code-generation"
                        + " may be broken", "This type representation does not"
                        + " support code-generation! \"<b>null</b>\" is used"
                        + " instead. Thus, only values that are"
                        + " connected will work in automatically generated code!",
                        c, MessageType.WARNING);
            }
        }

        return builder.toString();
    }

    /**
     * Generates param code for array type representations that delegate element
     * code generation to their clid representations.
     *
     * @param arrayType array type representation
     */
    private static void generateArrayBaseClassCode(
            StringBuilder builder,
            VisualCanvas canvas,
            Connections connections,
            ArrayList<Connector> doNotSupportCodeGeneration,
            ArrayBaseType arrayType) {
        builder.append("[");

        boolean firstSubElementRun = true;
        for (TypeRepresentationContainer tCont : arrayType.getTypeContainers()) {

            if (firstSubElementRun) {
                firstSubElementRun = false;

            } else {
                builder.append(", ");
            }

            boolean paramConnected
                    = connections.alreadyConnected(tCont.getConnector());

            if (paramConnected) {

                // only one connection allowed. thus, get(0) is save
                Connection c
                        = connections.getAllWith(
                                tCont.getConnector()).get(0);

                builder.append(getVariableName(canvas, connections, c));
            } else {

                String code
                        = tCont.getTypeRepresentation().getValueAsCode();

                if (code == null) {
                    code = "null as "
                            + tCont.getTypeRepresentation().getType().getName();
                    doNotSupportCodeGeneration.add(tCont.getConnector());
                }

                builder.append(code);
            }

        } // end for

        builder.append("] as ").
                append(
                        arrayType.getType().getComponentType().getName()).append("[]");
    }

    /**
     * Returns the code of the method body. This includes object instanciation,
     * variable declarations and method invocation code.
     *
     * @param canvas canvas
     * @param indent indent to use (necessary for correct code formatting)
     * @return code of the method body
     */
    private static String getBodyCode(VisualCanvas canvas, String indent) {

        Collection<Object> objects = canvas.getInspector().getObjects();

        // connected objects
        ArrayList<Object> usedObjects = new ArrayList<Object>();

        for (Object object : objects) {
            Collection<DefaultObjectRepresentation> oReps
                    = canvas.getInspector().
                    getObjectRepresentationsByReference(object);

            for (DefaultObjectRepresentation oRep : oReps) {
                boolean isInUse = canvas.getControlFlowConnections().
                        alreadyConnected(oRep.getControlFlowInput());

                if (isInUse) {
                    usedObjects.add(object);
                    break;
                }
            }
        }

        StringBuilder builder = new StringBuilder();

        builder.append(getObjectInstanciationCode(usedObjects, indent)).
                append("\n\n");

        // methods for controlflow
        Collection<DefaultMethodRepresentation> controlFlowMethods
                = ControlFlowUtils.getInvocationList(canvas);

//        // methods for variables and initialization code
//        Collection<DefaultMethodRepresentation> methods =
//                new ArrayList<DefaultMethodRepresentation>();
//        for (CanvasWindow w : canvas.getWindows()) {
//            if (w instanceof VisualObject) {
//                VisualObject vObj = (VisualObject) w;
//                methods.addAll(
//                        vObj.getObjectRepresentation().getInvocationList());
//            }
//        }
        builder.append(getVariableDeclarationCode(canvas,
                canvas.getDataConnections(),
                controlFlowMethods, indent + "    ")).
                append("\n\n");

//        builder.append(indent).append("    // initializer method calls\n");
//        
//        
//        for (DefaultMethodRepresentation mRep : methods) {
//
//            boolean referenceMethod = mRep.isReferenceMethod()
//                    || mRep.isCustomReferenceMethod();
//
//            if (referenceMethod 
//                    && ControlFlowUtils.isOutputConnected(canvas, mRep)) {
//                builder.append(getMethodInvocationCode(mRep, indent + "    "));
//            }
//        }
        builder.append("\n");

        builder.append(indent).append("    // method calls\n");

        for (DefaultMethodRepresentation mRep : controlFlowMethods) {

            builder.append(getMethodInvocationCode(mRep, indent + "    "));
        }

        builder.append("\n");

        // return value
        Connector c = getOutputSender(canvas);

        if (c != null) {
            builder.append(indent).append("    ").append("return ").
                    append(getVariableName(
                                    canvas, canvas.getDataConnections(), c)).append(";\n");
        }

        return builder.toString();
    }

    /**
     * Parameter class.
     */
    private static class Parameter {

        private Class<?> type;
        private String name;
        private String paramInfo;
        private Connection connection;

        public Parameter(Class<?> type, String name,
                String paramInfo, Connection connection) {
            this.type = type;
            this.name = name;
            this.paramInfo = paramInfo;
            this.connection = connection;
        }

        public String getParamInfo() {

            // if no custom param info has been defined use the param info of
            // the corresponding type representation
            // (from the receiver connector)
            if (paramInfo == null || paramInfo.trim().equals("")) {
                paramInfo = "";
                TypeRepresentationContainer tCont
                        = (TypeRepresentationContainer) getConnection().
                        getReceiver().getValueObject();

                TypeRepresentationBase tRep = tCont.getTypeRepresentation();

                ArrayList<TypeRepresentationBase> params
                        = tCont.getTypeRepresentation().getParentMethod().
                        getParameters();

                int id = params.indexOf(tRep);

                ParamInfo info = null;

                if (id >= 0 && id < tCont.getTypeRepresentation().
                        getParentMethod().getDescription().getParamInfos().length) {

                    info = tCont.getTypeRepresentation().
                            getParentMethod().getDescription().getParamInfos()[id];
                }

                if (info != null) {

                    paramInfo = "name=\"" + VLangUtils.addEscapeCharsToCode(
                            info.name()) + "\", style=\""
                            + VLangUtils.addEscapeCharsToCode(info.style())
                            + "\", options=\""
                            + VLangUtils.addEscapeCharsToCode(
                                    info.options()) + "\"";
                } else {
                    System.err.println("ParamInfo not found!");
                }
            }

            return paramInfo;
        }

        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }

        public Connection getConnection() {
            return connection;
        }
    }
}
