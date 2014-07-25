/* 
 * ScopeImpl.java
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

import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.ValueObject;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class ScopeImpl implements Scope {

    private String id;
    Scope parent;
    ScopeType type;
    private final String name;
    Object[] scopeArgs;
    Map<String, Variable> variables = new HashMap<>();
    ControlFlow controlFlow;
    DataFlow dataFlow;
    private final ObservableList<Scope> scopes = FXCollections.observableArrayList();
//    private String code;
//    private List<Scope> readOnlyScopes;
    private ICodeRange range;
    private final ObservableList<Comment> comments = FXCollections.observableArrayList();
    private final ScopeInvocation invocation;
    private VFlow flow;
    private ObservableCodeImpl observableCode;
    private boolean textRenderingEnabled = true;

    public ScopeImpl(String id, Scope parent, ScopeType type, String name, VFlow flowParent, Object... scopeArgs) {
        this.id = id;
        this.parent = parent;

        this.type = type;
        this.name = name;

        if (flowParent == null) {

            if (parent != null) {
                flowParent = parent.getFlow();
            } else {
                flowParent = FlowFactory.newFlow();
            }
        }

        flow = flowParent.newSubFlow();

        flow.getModel().setTitle(name);
        flow.setVisible(true);
        flow.getModel().getValueObject().setValue(this);

        initScopeListeners();

        this.scopeArgs = scopeArgs;
        this.controlFlow = new ControlFlowImpl(this);
        this.dataFlow = new DataFlowImpl();

        if (parent != null) {
            if (this.parent instanceof ScopeImpl) {
                ((ScopeImpl) this.parent).addScope(this);
            } else {
                throw new UnsupportedOperationException("Unsupported parent scope specified."
                        + " Only " + ScopeImpl.class + " based implementations are supported!");
            }

            if (parent.getType() != ScopeType.CLASS && parent.getType()
                    != ScopeType.NONE && parent.getType() != ScopeType.COMPILATION_UNIT) {
                invocation = parent.getControlFlow().callScope(this);
            } else {
                invocation = null;
            }

        } else {

            invocation = null;

        }
    }

    private void initScopeListeners() {
        scopes.addListener((ListChangeListener.Change<? extends Scope> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Scope s : c.getAddedSubList()) {
                        if (s.getParent() != this) {
                            throw new UnsupportedOperationException(
                                    "Todo (21.06.2014): Flow model needs support for adding scopes!"
                                    + "Switching scope parent currently not supported!");
                        }
                    }
                }

                if (c.wasRemoved()) {
                    for (Scope s : c.getRemoved()) {
                        flow.getNodes().remove(s.getNode());
                    }
                }
            }
            
            fireEvent(new CodeEvent(CodeEventType.CHANGE, this));
        });

        flow.getNodes().addListener((ListChangeListener.Change<? extends VNode> c) -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    c.getRemoved().stream().filter(n -> n.getValueObject().getValue() instanceof ScopeImpl).
                            map(n -> (ScopeImpl) n.getValueObject().getValue()).
                            forEach(s -> removeScope(s));
                }
            }
        });
    }

    public ScopeImpl(String id, Scope parent, ScopeType type, String name, Object... scopeArgs) {
        this(id, parent, type, name, null, scopeArgs);
    }

    @Override
    public ScopeType getType() {
        return type;
    }

    @Override
    public Object[] getScopeArgs() {
        return scopeArgs;
    }

    @Override
    public Collection<Variable> getVariables() {
        return variables.values();
    }

    @Override
    public Variable getVariable(String name) {
        Variable result = variables.get(name);

        if (result == null && getParent() != null) {
            result = getParent().getVariable(name);
        }

        if (result == null) {

            String parentName = "<unknown>";

            if (parent != null) {
                parentName = parent.getName();
            }

//            throw new IllegalArgumentException(
//                    "Variable '"
//                    + name
//                    + "' does not exist in scope '" + parentName + "'!");
        }

        return result;
    }

    @Override
    public Variable createVariable(IType type, String varName) {
        DeclarationInvocation inv = getControlFlow().declareVariable(id, type, varName);
        return inv.getDeclaredVariable();
    }
    
    Variable createParamVariable(IType type, String varName) {
        return createParamVariable(type, varName, null);
    }
    
    Variable createParamVariable(IType type, String varName, ICodeRange range) {
        DeclarationInvocationImpl inv = (DeclarationInvocationImpl)getControlFlow().declareVariable(id, type, varName);
        inv.setTextRenderingEnabled(false);
        inv.setRange(range);
        return inv.getDeclaredVariable();
    }

    Variable _createVariable(IType type, String varName) {

        if (getVariable(varName) != null) {
            throw new IllegalArgumentException("Variable '" + varName + "' does already exist!");
        }

        Variable variable = new VariableImpl(this, type, varName, null, false, null);
        variables.put(varName, variable);
        return variable;
    }

//    @Override
//    public Variable createVariable(IType type) {
//        String varNamePrefix = "vrlInternalVar";
//
//        int counter = 0;
//        String varName = varNamePrefix + counter;
//
//        while (getVariable(varName) != null) {
//            counter++;
//            varName = varNamePrefix + counter;
//        }
//
//        return createVariable(type, varName);
//    }
//    @Override
//    public Variable createVariable(Invocation invocation) {
//        String varNamePrefix = "vrlInvocationVar";
//
//        int counter = 0;
//        String varName = varNamePrefix + counter;
//
//        while (getVariable(varName) != null) {
//            counter++;
//            varName = varNamePrefix + counter;
//        }
//
//        Variable variable = new VariableImpl(this, varName, invocation);
//        variables.put(varName, variable);
//        return variable;
//    }
//    @Override
//    public Variable createStaticVariable(IType type) {
//
//        DeclarationInvocation inv = getControlFlow().declareStaticVariable(id, type, name);
//        
//        return inv.getDeclaredVariable();
//    }
//
//    Variable _createStaticVariable(IType type) {
//        Variable variable = VariableImpl.createStaticVar(parent, type, null);
//        variables.put(variable.getName(), variable);
//        return variable;
//    }
    @Override
    public BinaryOperatorInvocation assignConstant(String varName, Object constant) {
        Variable var = getVariable(varName);

        if (var == null) {
            throw new IllegalArgumentException("Variable " + varName + " does not exist!");
        }

        var.setValue(constant);
        var.setConstant(true);

        return getControlFlow().assignConstant(id, varName, Argument.constArg(Type.fromObject(constant, false), constant));
    }

    @Override
    public BinaryOperatorInvocation assignVariable(String varNameDest, String varNameSrc) {
        Variable varDest = getVariable(varNameDest);
        Variable varSrc = getVariable(varNameSrc);

        if (varDest == null) {
            throw new IllegalArgumentException("Variable " + varNameDest + " does not exist!");
        }

        if (varSrc == null) {
            throw new IllegalArgumentException("Variable " + varNameSrc + " does not exist!");
        }

        System.out.println(">> assignment: " + varNameDest + "=" + varNameSrc);

        System.err.println("WARNING: impl missing!!!");
        return getControlFlow().assignVariable(id, varNameSrc, null);
    }

    @Override
    public BinaryOperatorInvocation assignInvocationResult(String varName, Invocation invocation) {
        Variable varDest = getVariable(varName);

        if (varDest == null) {
            throw new IllegalArgumentException("Variable " + varName + " does not exist!");
        }

        return getControlFlow().assignInvocationResult(id, varName, invocation);
    }

    @Override
    public ControlFlow getControlFlow() {
        return controlFlow;
    }

    @Override
    public Scope getParent() {
        return parent;
    }

    /**
     * @return the scopes
     */
    @Override
    public ObservableList<Scope> getScopes() {

//        if (readOnlyScopes == null) {
//            readOnlyScopes = Collections.unmodifiableList(scopes);
//        }
//
//        return readOnlyScopes;
        return scopes;
    }

    @Override
    public String toString() {
        String result = "Scope:" + type;

        result += "\n>> Variables:\n";

        for (Variable v : variables.values()) {
            result += " --> " + v.toString() + "\n";
        }

        result += "\n>> ControlFlow:\n" + controlFlow.toString();

        result += "\n>> SubScopes:\n";

        for (Scope s : scopes) {
            result += s.toString() + "\n";
        }

        return result;
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
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

//    /**
//     * @return the code
//     */
//    @Override
//    public String getCode() {
//        return code;
//    }
//
//    /**
//     * @param code the code to set
//     */
//    @Override
//    public void setCode(String code) {
//        this.code = code;
//    }
    @Override
    public DataFlow getDataFlow() {
        return dataFlow;
    }

    @Override
    public void generateDataFlow() {

        System.out.println("DATAFLOW---------------------------------");

        getDataFlow().create(controlFlow);

//        for (Invocation i : controlFlow.getInvocations()) {
////            System.out.println("invocation: " + i);
//            for (IArgument a : i.getArguments()) {
//                System.out.println("--> arg: " + a + ", " + i);
//            }
//
//            if (i instanceof ScopeInvocation) {
//                ScopeImpl invocationScope = (ScopeImpl) ((ScopeInvocation) i).getScope();
//                invocationScope.generateDataFlow();
//            }
//        }
//
//        boolean isClassOrScript = getType() == ScopeType.CLASS || getType() == ScopeType.NONE || getType() == ScopeType.COMPILATION_UNIT;
//
//        if (isClassOrScript) {
//            for (Scope s : getScopes()) {
//                ((ScopeImpl)s).generateDataFlow();
//            }
//        }
    }

    @Override
    public Scope createScope(String id, ScopeType type, String name, Object[] args) {
        Scope scope = new ScopeImpl(id, this, type, name, args);

        return scope;
    }

    @Override
    public Scope getScopeById(String id) {
        for (Scope s : scopes) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    void addScope(Scope s) {
        scopes.add(s);
    }

    /**
     * @return the location
     */
    @Override
    public ICodeRange getRange() {
        return range;
    }

    /**
     * @param location the location to set
     */
    @Override
    public void setRange(ICodeRange location) {
        this.range = location;
    }

    @Override
    public List<Comment> getComments() {
        return comments;
    }

    @Override
    public void createComment(String id, ICodeRange range, String comment) {
        this.comments.add(new CommentImpl(id, range, comment));
    }

    @Override
    public boolean removeScope(Scope s) {
        boolean result = scopes.remove(s);

        if (result) {
            flow.getNodes().remove(s.getNode());
        }

        return result;
    }

    @Override
    public DeclarationInvocation declareVariable(String id, IType type, String varName) {
        return getControlFlow().declareVariable(id, type, varName);
    }

    /**
     * @return the invocation
     */
    @Override
    public Optional<ScopeInvocation> getInvocation() {
        return Optional.ofNullable(invocation);
    }

    @Override
    public VFlow getFlow() {
        return this.flow;
    }

    @Override
    public VNode getNode() {
        return this.flow.getModel();
    }

    @Override
    public void visitScopeAndAllSubElements(Consumer<CodeEntity> consumer) {
        consumer.accept(this);
        getControlFlow().getInvocations().forEach(consumer);
        getComments().forEach(consumer);

        for (Scope scope : getScopes()) {
            scope.visitScopeAndAllSubElements(consumer);
        }
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
    public boolean isTextRenderingEnabled() {
        return textRenderingEnabled;
    }

}
