/* 
 * InvocationImpl.java
 *
 * Copyright (c) 2009–2014 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2014 by Michael Hoffer
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
 * First, the following text must be displayed on the Canvas or an equivalent location:
 * "based on VRL source code".
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
 * Computing and Visualization in Science, in press.
 */
package eu.mihosoft.vrl.lang.model;

import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import eu.mihosoft.vrl.workflow.WorkflowUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class InvocationImpl implements Invocation {

    private String id;
    private ObjectProvider objProvider;
    private final String methodName;
    private final ObservableList<Argument> arguments = FXCollections.observableArrayList();
    private final boolean constructor;
    private boolean Void;
//    private String code;
    private Scope parent;
    private boolean Static;
    private ICodeRange location;
    private IType returnType;
//    private final Variable returnValue;
    private VNode node;
    private ObservableCodeImpl observableCode;
    private boolean textRenderingEnabled = true;

    private final ObservableMap<String, Object> metadata = FXCollections.observableHashMap();

    public InvocationImpl(
            Scope parent,
            String id,
            ObjectProvider objProvider, String methodName, IType returnType,
            boolean constructor, boolean isStatic, Argument... args) {
        this.parent = parent;
        this.id = id;
        this.objProvider = objProvider;
        this.methodName = methodName;
        this.constructor = constructor;
        this.Void = Type.VOID.equals(returnType);

        this.Static = isStatic;
        this.returnType = returnType;

        arguments.addAll(Arrays.asList(args));

//        if (isVoid) {
//            returnValue = null;
//        } else {
//            returnValue = parent.createVariable(this);
//        }
        init(objProvider, parent);

    }

    private void init(ObjectProvider objProvider, Scope oldParent) {

//        if (varName != null && !varName.isEmpty()) {
//            Variable var = null;
//            try {
//                var = parent.getVariable(varName);
//            } catch (IllegalArgumentException ex) {
//                // will be checked later (see if below)
//            }
//
//            if (!isStatic() && !isScope() && var == null) {
//
//                throw new IllegalArgumentException(
//                        "Variable '"
//                        + varName
//                        + "' does not exist in scope '" + parent.getName() + "'!");
//            } else if (varName != null) {
//                // check whether varName is a valid type
//                Type type = new Type(varName);
//            }
//        }
        if (isScope()) {
            // nothing (see ScopeInvocationImpl)
        } else {

            boolean newNode = node == null || parent != oldParent;

            if (newNode) {
                if (node != null) {
                    oldParent.getFlow().remove(node);
                }
                node = parent.getFlow().newNode();
//            } else {
//                List<Connector> delList = new ArrayList<>();
//                delList.addAll(node.getConnectors());
//                for (Connector c : delList) {
//                    System.out.println("del: " + c + ", node: " + node + ", inv: " + methodName);
//                    node.removeConnector(c);
//                    // TODO 19.08.2015 restore output connections
//                }

                node.getValueObject().setValue(this);

                Connector controlflowInput = node.setMainInput(
                        node.addInput(WorkflowUtil.CONTROL_FLOW));

                controlflowInput.getVisualizationRequest().set(
                        VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);

                controlflowInput.setMaxNumberOfConnections(1);

                Connector controlflowOutput = node.setMainOutput(
                        node.addOutput(WorkflowUtil.CONTROL_FLOW));

                controlflowOutput.
                        getVisualizationRequest().set(
                                VisualizationRequest.KEY_CONNECTOR_AUTO_LAYOUT, true);

                controlflowOutput.setMaxNumberOfConnections(1);

                int argIndex = 0;
                for (Argument arg : arguments) {
                    node.addInput(WorkflowUtil.DATA_FLOW).getValueObject().
                            setValue(new ArgumentValue(argIndex, arg));
                    argIndex++;
                }

                if (!Objects.equals(returnType, Type.VOID)) {
                    Connector output = node.addOutput(WorkflowUtil.DATA_FLOW);
                    output.getValueObject().setValue(returnType);
                    node.setMainOutput(output);
                }

            }

            node.setTitle(objProvider.toString() + "." + methodName + "()");

        }
    }

    @Override
    public void setObjectProvider(ObjectProvider objProvider) {
        this.objProvider = objProvider;
        init(objProvider, parent);
    }

    @Override
    public ObjectProvider getObjectProvider() {
        return objProvider;
    }

    @Override
    public ObservableList<Argument> getArguments() {
        return arguments;
    }

    @Override
    public boolean isConstructor() {
        return constructor;
    }

    @Override
    public boolean isVoid() {
        return Void;
    }

    @Override
    public String toString() {

        String result = "[ ";

        if (this instanceof ScopeInvocationImpl) {
            ScopeInvocationImpl scopeInvocation = (ScopeInvocationImpl) this;
            result += "scopeType: " + scopeInvocation.getScope().getType() + ", ";
        }

        result += "constructor=" + constructor + ", objProv=" + objProvider + ", mName=" + methodName /*+ ", retVal=" + returnValue*/ + ", args=[";

        for (Argument a : arguments) {
            result += a + ", ";
        }

        result += "] ]";

        return result;
    }

    /**
     * @return the id
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean isScope() {
        return false;
    }

    /**
     * @return the Static
     */
    @Override
    public boolean isStatic() {
        return Static;
    }

    /**
     * @param Static the Static to set
     */
    public void setStatic(boolean Static) {
        this.Static = Static;
    }

    /**
     * @return the location
     */
    @Override
    public ICodeRange getRange() {
        return this.location;
    }

    /**
     * @param location the location to set
     */
    @Override
    public void setRange(ICodeRange location) {
        this.location = location;
    }

    /**
     * @return the parent
     */
    @Override
    public Scope getParent() {
        return this.parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InvocationImpl other = (InvocationImpl) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.objProvider, other.objProvider)) {
            return false;
        }
        if (!Objects.equals(this.methodName, other.methodName)) {
            return false;
        }
        if (!Objects.equals(this.arguments, other.arguments)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
        hash = 37 * hash + Objects.hashCode(this.objProvider);
        hash = 37 * hash + Objects.hashCode(this.methodName);
        hash = 37 * hash + Objects.hashCode(this.arguments);
        return hash;
    }

    /**
     * @return the returnType
     */
    @Override
    public IType getReturnType() {
        return returnType;
    }

    /**
     * @param returnType the returnType to set
     */
    protected void setReturnType(IType returnType) {

        this.Void = Type.VOID.equals(returnType);

        List<List<Connector>> receivers = new ArrayList<>();

        List<Connector> connectorsToRemove
                = new ArrayList<>(
                        node.getOutputs().filtered(
                                o -> o.getType().equals(WorkflowUtil.DATA_FLOW)));

//        node.getOutputs().removeAll(connectorsToRemove);
        for (Connector ctr : connectorsToRemove) {
            List<Connector> receiversForC
                    = node.getFlow().getConnections(ctr.getType()).
                    getAllWith(ctr).stream().
                    map(conn -> conn.getReceiver()).
                    collect(Collectors.toList());

            receivers.add(receiversForC);

            node.removeConnector(ctr);
        }

        if (!Objects.equals(returnType, Type.VOID)) {
            Connector output = node.addOutput(WorkflowUtil.DATA_FLOW);
            output.getValueObject().setValue(returnType);
            node.setMainOutput(output);

            if (!receivers.isEmpty()) {
                for (Connector rec : receivers.get(0)) {
                    node.getFlow().connect(output, rec);
                }
            }

        }

        this.returnType = returnType;
    }

    @Override
    public VNode getNode() {
        return this.node;
    }

    private ObservableCodeImpl getObservable() {
        if (observableCode == null) {
            observableCode = new ObservableCodeImpl();
        }

        return observableCode;
    }

    @Override
    public void addEventHandler(ICodeEventType type, CodeEventHandler eventHandler) {
        getObservable().addEventHandler(type, eventHandler);
    }

    @Override
    public void removeEventHandler(ICodeEventType type, CodeEventHandler eventHandler) {
        getObservable().removeEventHandler(type, eventHandler);
    }

    @Override
    public void fireEvent(CodeEvent evt) {
        getObservable().fireEvent(evt);

        if (!evt.isCaptured() && getParent() != null) {
            getParent().fireEvent(evt);
        }
    }

    /**
     * @return the textRenderingEnabled
     */
    @Override
    public boolean isTextRenderingEnabled() {
        return textRenderingEnabled;
    }

    /**
     * @param textRenderingEnabled the textRenderingEnabled to set
     */
    public void setTextRenderingEnabled(boolean textRenderingEnabled) {
        this.textRenderingEnabled = textRenderingEnabled;
    }

    void setParent(Scope parent) {
        Scope oldParent = this.parent;
        this.parent = parent;
        if (parent != oldParent) {
            init(objProvider, oldParent);
        }
    }

    /**
     * @return the metadata
     */
    @Override
    public ObservableMap<String, Object> getMetaData() {
        return metadata;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }
}
