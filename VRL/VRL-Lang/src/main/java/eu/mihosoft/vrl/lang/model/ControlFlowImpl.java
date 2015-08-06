/* 
 * ControlFlowImpl.java
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

import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.WorkflowUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class ControlFlowImpl implements ControlFlow {

    private final ObservableList<Invocation> invocations = FXCollections.observableArrayList();

    private final Scope parent;
    private final VFlow flow;

    private boolean currentlyUpdatingConnections;
    private boolean currentlyUpdatingInvocations;

    public ControlFlowImpl(Scope parent) {
        this.parent = parent;
        this.flow = parent.getFlow();

        initListeners();
    }

    private void initListeners() {
        invocations.addListener((Observable observable) -> {
            if (!currentlyUpdatingInvocations) {
                updateConnections();
                // update invocation parent;
                for (Invocation invocation : invocations) {
                    ((InvocationImpl) invocation).setParent(getParent());
                }
            }
        });
//        invocations.addListener((ListChangeListener.Change<? extends Invocation> c) -> {
//            while (c.next()) {
//                if (c.wasAdded()) {
//                    c.getAddedSubList().stream().forEach((inv) -> {
//                        System.out.println("inv-add: " + inv);
//                        ((InvocationImpl) inv).setParent(getParent());
//                    });
//                }
//            }
//        });

        flow.getConnections(WorkflowUtil.CONTROL_FLOW).getConnections().addListener(
                new ListChangeListener<Connection>() {

                    @Override
                    public void onChanged(ListChangeListener.Change<? extends Connection> c) {
                        if (!currentlyUpdatingConnections) {
                            updateInvocations();
                        }
                    }
                });

        flow.getNodes().addListener((ListChangeListener.Change<? extends VNode> c) -> {
            if (c.next()) {
                if (!c.getRemoved().isEmpty()) {
                    if (!currentlyUpdatingConnections) {
                        updateInvocations();
                    }
                }
            }
        });

    }

    private void updateConnections() {
        currentlyUpdatingConnections = true;
        flow.getConnections(WorkflowUtil.CONTROL_FLOW).getConnections().clear();
        Invocation prevInvocation = null;
        for (Invocation invocation : invocations) {
            if (prevInvocation != null) {
                flow.connect(prevInvocation.getNode(), invocation.getNode(),
                        WorkflowUtil.CONTROL_FLOW);
            }

            prevInvocation = invocation;
        }
        currentlyUpdatingConnections = false;
    }

    private void updateInvocations() {
        currentlyUpdatingInvocations = true;

        List<Invocation> prevInvocations = new ArrayList<>(invocations);

        invocations.clear();

        List<VNode> roots = flow.getNodes().filtered(WorkflowUtil.
                nodeNotConnected(WorkflowUtil.CONTROL_FLOW));

        List<List<VNode>> paths = new ArrayList<>();

        // follow controlflow from roots to end
        roots.forEach(
                r -> {
                    List<VNode> path = WorkflowUtil.getPathInLayerFromRoot(
                            r, WorkflowUtil.CONTROL_FLOW);
                    paths.add(path);
                }
        );

        paths.forEach(path -> path.forEach(
                node -> {
                    Object valueObject = node.getValueObject().getValue();
                    if (valueObject instanceof Invocation) {
                        getInvocations().add((Invocation) valueObject);
                    }
                }
        )
        );

        currentlyUpdatingInvocations = false;

        // fire code changed event if invocations differ
        boolean changed = invocations.size() != prevInvocations.size();

        if (!changed) {
            for (int i = 0; i < prevInvocations.size(); i++) {
                if (!invocations.get(i).equals(prevInvocations.get(i))) {
                    changed = true;
                    break;
                }
            }
        }

        if (changed) {
            getParent().fireEvent(new CodeEvent(CodeEventType.CHANGE, parent));
        }
    }

    @Override
    public Invocation createInstance(String id, IType type, IArgument... args) {
        Invocation result = new InvocationImpl(parent, id, type.getFullClassName(),
                "<init>", type, true, true, args);
        getInvocations().add(result);
        return result;
    }

    @Override
    public Invocation callMethod(String id, String varName, String mName, IType returnType,
            IArgument... args) {
        Invocation result = new InvocationImpl(parent, id, varName, mName, returnType,
                false, false, args);
        getInvocations().add(result);
        return result;
    }

    @Override
    public Invocation callStaticMethod(String id, IType type, String mName, IType returnType,
            IArgument... args) {
        Invocation result = new InvocationImpl(parent, id, type.getFullClassName(), mName,
                returnType, false, true, args);
        getInvocations().add(result);
        return result;
    }

    @Override
    public ScopeInvocation callScope(Scope scope) {
        ScopeInvocation result = new ScopeInvocationImpl(scope);
        getInvocations().add(result);
        return result;
    }

    @Override
    public String toString() {
        String result = "[\n";
        for (Invocation invocation : getInvocations()) {
            result += invocation.toString() + "\n";
        }

        result += "]";

        return result;
    }

    /**
     * @return the invocations
     */
    @Override
    public List<Invocation> getInvocations() {
        return invocations;
    }

    @Override
    public Invocation callMethod(String id, String varName,
            MethodDeclaration mDec, IArgument... args) {

        if (mDec.getModifiers().getModifiers().contains(Modifier.STATIC)) {
            return callStaticMethod(id, mDec.getClassDeclaration().getClassType(),
                    varName, mDec.getReturnType(), args);
        } else {
            return callMethod(id, varName, mDec.getName(), mDec.getReturnType(), args);
        }

    }

    @Override
    public boolean isUsedAsInput(Invocation invocation) {

        return returnInvTargetIfPresent(invocation).isPresent();
    }

    @Override
    public Optional<Invocation> returnInvTargetIfPresent(Invocation invocation) {

        for (Invocation inv : invocations) {

            if (inv instanceof ScopeInvocation) {
                ScopeInvocation sInv = (ScopeInvocation) inv;
                if (sInv.getScope() instanceof WhileDeclaration) {
                    WhileDeclaration whileD = (WhileDeclaration) sInv.getScope();
                    if (invocation == whileD.getCheck()) {
                        return Optional.of(inv);
                    }
                }
            }

            for (IArgument arg : inv.getArguments()) {

                if (arg.getArgType() == ArgumentType.INVOCATION) {
                    if (arg.getInvocation().get().equals(invocation)) {
                        return Optional.of(inv);
                    }
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public BinaryOperatorInvocation assignConstant(String id, String varName, IArgument arg) {
        Variable var = parent.getVariable(varName);

        if (var == null) {
            throw new IllegalArgumentException("Variable " + varName + " does not exist!");
        }

        BinaryOperatorInvocation invocation = new BinaryOperatorInvocationImpl(parent,
                Argument.varArg(var), arg, Operator.ASSIGN);

        getInvocations().add(invocation);

        return invocation;
    }

    @Override
    public BinaryOperatorInvocation assignVariable(String id, String varName, IArgument arg) {
        Variable var = parent.getVariable(varName);

        if (var == null) {
            throw new IllegalArgumentException("Variable " + varName + " does not exist!");
        }

        BinaryOperatorInvocation invocation = new BinaryOperatorInvocationImpl(parent,
                Argument.varArg(var), arg, Operator.ASSIGN);

        getInvocations().add(invocation);

        return invocation;
    }

    @Override
    public BinaryOperatorInvocation assignInvocationResult(String id, String varName, Invocation invocation) {
        Variable var = parent.getVariable(varName);

        if (var == null) {
            throw new IllegalArgumentException("Variable " + varName + " does not exist!");
        }

        BinaryOperatorInvocation result = new BinaryOperatorInvocationImpl(parent,
                Argument.varArg(var), Argument.invArg(invocation), Operator.ASSIGN);

        getInvocations().add(result);

        return result;
    }

    @Override
    public DeclarationInvocation declareVariable(String id, IType type, String varName) {
        VariableImpl var = (VariableImpl) ((ScopeImpl) parent)._createVariable(type, varName);

        DeclarationInvocationImpl invocation = new DeclarationInvocationImpl(parent, var);

        var.setDeclaration(invocation);

        getInvocations().add(invocation);

        return invocation;
    }

//     @Override
//    public DeclarationInvocation declareStaticVariable(String id, IType type, String varName) {
//        VariableImpl var = (VariableImpl)((ScopeImpl)parent)._createStaticVariable(type, varName);
//
//        DeclarationInvocationImpl invocation = new DeclarationInvocationImpl(parent, var);
//        
//        var.setDeclaration(invocation);
//
//        getInvocations().add(invocation);
//
//        return invocation;
//    }
    @Override
    public BinaryOperatorInvocation invokeOperator(String id, IArgument leftArg,
            IArgument rightArg, Operator operator) {
        BinaryOperatorInvocation invocation = new BinaryOperatorInvocationImpl(parent,
                leftArg, rightArg, operator);

        getInvocations().add(invocation);

        return invocation;
    }

    @Override
    public Scope getParent() {
        return parent;
    }

    @Override
    public ReturnStatementInvocation returnValue(String id, IArgument arg) {
        ReturnStatementInvocation invocation = new ReturnStatementInvocationImpl(parent, arg);

        getInvocations().add(invocation);

        return invocation;
    }

    @Override
    public BreakInvocation invokeBreak(String id) {
        BreakInvocation invocation = new BreakInvocationImpl(parent);

        getInvocations().add(invocation);

        return invocation;
    }

    @Override
    public ContinueInvocation invokeContinue(String id) {
        ContinueInvocation invocation = new ContinueInvocationImpl(parent);

        getInvocations().add(invocation);

        return invocation;
    }

    @Override
    public NotInvocation invokeNot(String id, IArgument arg) {
        NotInvocation invocation = new NotInvocationImpl(parent, arg);

        getInvocations().add(invocation);

        return invocation;
    }

}
