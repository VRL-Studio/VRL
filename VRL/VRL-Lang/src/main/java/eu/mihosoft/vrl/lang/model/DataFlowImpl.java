/* 
 * DataFlowImpl.java
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

import com.google.common.base.Objects;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.ConnectionEvent;
import eu.mihosoft.vrl.workflow.ConnectionResult;
import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.ThruConnector;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.VisualizationRequest;
import eu.mihosoft.vrl.workflow.WorkflowUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class DataFlowImpl implements DataFlow {

    List<DataRelation> relations = new ArrayList<>();
    ListMultimap<Invocation, DataRelation> relationsForSender = ArrayListMultimap.create();
    ListMultimap<Invocation, DataRelation> relationsForReceiver = ArrayListMultimap.create();
    private boolean currentlyUpdatingFromModel;
    private boolean currentlyUpdatingFromUI;
    private boolean listenersInitialized = false;

    void createDataRelation(Invocation sender, Invocation receiver, IArgument receiverArg, int receiverArgIndex) {
        DataRelationImpl relation = new DataRelationImpl(sender, receiver, receiverArg, receiverArgIndex);

        relations.add(relation);
        relationsForSender.put(sender, relation);

        relationsForReceiver.put(receiver, relation);
    }

    @Override
    public List<DataRelation> getRelations() {
        return relations;
    }

    @Override
    public List<DataRelation> getRelationsForSender(Invocation invocation) {
        return relationsForSender.get(invocation);
    }

    @Override
    public List<DataRelation> getRelationsForReceiver(Invocation invocation) {
        return relationsForReceiver.get(invocation);
    }

    @Override
    public void create(ControlFlow controlFlow) {

        relations.clear();
        relationsForSender.clear();
        relationsForReceiver.clear();

        initListeners(controlFlow);

        for (Invocation receiver : controlFlow.getInvocations()) {
            int argIndex = 0;
            for (IArgument a : receiver.getArguments()) {

                Invocation sender = null;

                if (a.getArgType() == ArgumentType.INVOCATION) {
                    sender = a.getInvocation().get();
                } else if (a.getArgType() == ArgumentType.VARIABLE) {
                    sender = a.getVariable().get().getDeclaration();
                }

                if (sender != null) {
                    createDataRelation(sender, receiver, a, argIndex);
                }
                argIndex++;
            } // end for arg
        }

        // create visual dataflow
        updateVisualDataFlowFromModel(controlFlow);

        // create subflows
        for (Scope scope : controlFlow.getParent().getScopes()) {
            scope.getDataFlow().create(scope.getControlFlow());
        }

    }

    private void updateVisualDataFlowFromModel(ControlFlow controlFlow) {
        if (!currentlyUpdatingFromUI) {
            currentlyUpdatingFromModel = true;

            Connections connections = controlFlow.getParent().getFlow().
                    getConnections(WorkflowUtil.DATA_FLOW);

            for (Connection c : new ArrayList<>(connections.getConnections())) {

                // for now we filter thru connector
                if (c.getSender() instanceof ThruConnector
                        || c.getReceiver() instanceof ThruConnector) {
                    continue;
                }

                controlFlow.getParent().getFlow().getConnections(WorkflowUtil.DATA_FLOW).remove(c);
            }

            for (DataRelation dR : getRelations()) {
                Connector sConn = dR.getSender().getNode().
                        getMainOutput(WorkflowUtil.DATA_FLOW);
                Connector rConn = dR.getReceiver().getNode().
                        getInputs().filtered(i -> i.getType().
                                equals(WorkflowUtil.DATA_FLOW)).
                        get(dR.getReceiverArgIndex());

                if (dR.getSender() instanceof DeclarationInvocation) {
                    if (!createRefToVarDecl(
                            dR.getReceiver().getParent(),
                            (DeclarationInvocation) dR.getSender(), rConn)) {
                        System.err.println(
                                "ERROR: Cannot find route to variable declaration! "
                                + ((DeclarationInvocation) dR.getSender()).
                                getDeclaredVariable());
                    }
                } else {
                    ConnectionResult compatible = controlFlow.getParent().
                            getFlow().connect(sConn, rConn);

                    if (!compatible.getStatus().isCompatible()) {
                        System.err.println("Connection not compatible! "
                                + compatible.getStatus().getMessage());
                    }
                }

            }

            currentlyUpdatingFromModel = false;
        }
    }

    private List<Scope> getAncestors(Scope scope) {
        List<Scope> result = new ArrayList<>();

        Scope parent = scope.getParent();

        while (parent != null) {
            result.add(parent);
            parent = parent.getParent();
        }

        return result;
    }

    private List<Scope> getScopeAndAncestors(Scope scope) {
        List<Scope> result = new ArrayList<>();
        result.add(scope);
        Scope parent = scope.getParent();

        while (parent != null) {
            result.add(parent);
            parent = parent.getParent();
        }

        return result;
    }

    private boolean createRefToVarDecl(
            Scope scope, DeclarationInvocation vDecl, Connector receiver) {

        boolean currentlyUpdatingFromUiPrev = currentlyUpdatingFromUI;

        currentlyUpdatingFromUI = true;

        Connector prevInput = receiver;

        for (Scope sc : getScopeAndAncestors(scope)) {

            Optional<Connector> ref = getVarDeclOrRef(sc, vDecl);

            if (ref.isPresent()) {

                System.out.println("ref: " + ref.get().getNode().
                        getValueObject().getValue()
                        + " -> prevIn: " + prevInput.getNode().
                        getValueObject().getValue());

                ConnectionResult connRes
                        = sc.getFlow().connect(ref.get(), prevInput);

                if (prevInput != receiver
                        && connRes.getStatus().isCompatible()) {
                    connRes.getConnection().getVisualizationRequest().
                            set(VisualizationRequest.KEY_DISABLE_EDITING, true);
                }

                return true;

            }

            ThruConnector tI
                    = sc.getFlow().addThruInput(WorkflowUtil.DATA_FLOW);
            tI.getInnerNode().setTitle(
                    "ref " + vDecl.getDeclaredVariable().getName());
            tI.getInnerNode().getValueObject().setValue(vDecl);

            tI.getInnerConnector().addConnectionEventListener(
                    new EventHandler<eu.mihosoft.vrl.workflow.ConnectionEvent>() {

                        @Override
                        public void handle(eu.mihosoft.vrl.workflow.ConnectionEvent event) {

                            boolean hasConnectionsToThruInputs = tI.getInnerNode().getFlow().
                            getConnections(tI.getInnerConnector().getType()).
                            getAllWith(tI.getInnerConnector()).stream().
                            filter(conn -> conn.getReceiver() instanceof ThruConnector).
                            findAny().isPresent();

                            tI.getInnerNode().getVisualizationRequest().
                            set(VisualizationRequest.KEY_NODE_NOT_REMOVABLE,
                                    hasConnectionsToThruInputs);
                        }
                    });

            ConnectionResult connRes = sc.getFlow().
                    connect(tI.getInnerConnector(), prevInput);

            if (connRes.getStatus().isCompatible()) {
                if (prevInput != receiver
                        && connRes.getStatus().isCompatible()) {
                    connRes.getConnection().getVisualizationRequest().
                            set(VisualizationRequest.KEY_DISABLE_EDITING, true);
                }

                tI.getInnerNode().getFlow().getNodes().addListener(
                        new ListChangeListener<VNode>() {

                            @Override
                            public void onChanged(
                                    ListChangeListener.Change<? extends VNode> c) {
                                        tI.getInnerNode().getFlow().getNodes().
                                        removeListener(this);
                                        while (c.next()) {
                                            if (c.getRemoved().contains(tI.getInnerNode())) {
                                                tI.getNode().getConnectors().remove(tI);
                                            }
                                        }
                                    }
                        });
            }

            System.out.println("ti-inner: " + tI.getInnerNode().
                    getValueObject().getValue() + " -> prevIn: "
                    + prevInput.getNode().getValueObject().getValue());

            prevInput = tI;
        }

        currentlyUpdatingFromUI = currentlyUpdatingFromUiPrev;

        return false;
    }

    private Optional<Connector> getVarDeclOrRef(Scope scope,
            DeclarationInvocation vDecl) {

        if (scope.getVariables().contains(vDecl.getDeclaredVariable())) {
            System.out.println("ref: isDecl");
            return Optional.of(vDecl.getNode().
                    getMainOutput(WorkflowUtil.DATA_FLOW));
        }

        Predicate<ThruConnector> thruConnectorHasRefToDecl
                = c -> c.getInnerNode().getValueObject().
                getValue() instanceof DeclarationInvocation;

        Predicate<ThruConnector> thruConnectorReferencesVar
                = c -> Objects.equal(vDecl.getDeclaredVariable(),
                        ((DeclarationInvocation) c.getInnerNode().
                        getValueObject().
                        getValue()).getDeclaredVariable());

        final Optional<ThruConnector> result
                = scope.getFlow().getThruInputs().stream().
                filter(thruConnectorHasRefToDecl).
                filter(thruConnectorReferencesVar).findFirst();

        if (result.isPresent()) {
            System.out.println("ref: isThru");
            return Optional.of(result.get().getInnerConnector());
        } else {
            return Optional.empty();
        }
    }

    private void updateDataFlowFromVisualChange(ControlFlow controlFlow,
            ListChangeListener.Change<? extends Connection> change) {

        if (currentlyUpdatingFromModel) {
            return;
        }

        currentlyUpdatingFromUI = true;

        while (change.next()) {
            if (change.wasAdded()) {
                for (Connection conn : change.getAddedSubList()) {

                    // for now we filter thru connector
                    if (conn.getSender() instanceof ThruConnector
                            || conn.getReceiver() instanceof ThruConnector) {
                        continue;
                    }

                    VNode senderN = conn.getSender().getNode();
                    VNode receiverN = conn.getReceiver().getNode();

                    Invocation senderInv = (Invocation) senderN.getValueObject().getValue();//nodeInvocations.get(senderN.getId());
                    Invocation receiverInv = (Invocation) receiverN.getValueObject().getValue();//nodeInvocations.get(receiverN.getId());

                    int numConnections
                            = senderN.getOutputs().filtered(
                                    WorkflowUtil.moreThanConnections(1, "data")).size();

                    if (numConnections > 0 && !(senderInv instanceof DeclarationInvocation)) {
                        // TODO introduce var declaration to prevent multiple method calls for each dataflow connection 19.02.2014
                        System.err.println("NORE THAN ONE DATAFLOW CONN!!! for invocation " + senderInv);
                    }

                    // TODO if (senderInv equals variableinvocation) only invocations supported 18.02.2014
                    try {

                        int argIndex = connectorsToArgIndex(conn.getReceiver(), receiverInv);

                        IArgument arg = Argument.NULL;

                        if (senderInv instanceof DeclarationInvocation) {
                            arg = Argument.varArg(((DeclarationInvocation) senderInv).getDeclaredVariable());
                        } else if (senderInv instanceof Invocation) {
                            arg = Argument.invArg(senderInv);
                            
                            controlFlow.getInvocations().remove(senderInv);
                            
                            int targetArgInvIndex = controlFlow.getInvocations().indexOf(receiverInv);
                            
                            controlFlow.getInvocations().add(targetArgInvIndex, senderInv);
                            
                        } else {
                            System.err.println("NOT Supported as argument: " + senderInv);
                        }

                        receiverInv.getArguments().set(
                                argIndex,
                                arg);

                        ((ScopeImpl) senderInv.getParent()).generateDataFlow();
                    } catch (Exception ex) {
                        ex.printStackTrace(System.err);
                    }
                }
            }

            if (change.wasRemoved()) {

                for (Connection conn : change.getRemoved()) {

                    // for now we filter thru connector
                    if (conn.getSender() instanceof ThruConnector
                            || conn.getReceiver() instanceof ThruConnector) {
                        continue;
                    }

                    VNode senderN = conn.getSender().getNode();
                    VNode receiverN = conn.getReceiver().getNode();

                    Invocation senderInv = (Invocation) senderN.getValueObject().getValue();
                    Invocation receiverInv = (Invocation) receiverN.getValueObject().getValue();

                    try {

                        List<DataRelation> delList = new ArrayList<>();

                        for (DataRelation dR : getRelationsForSender(senderInv)) {
                            if (dR.getReceiver().equals(receiverInv)) {
                                delList.add(dR);
                            }
                        }

                        for (DataRelation dR : delList) {
                            getRelations().remove(dR);
                        }

                        int argIndex = connectorsToArgIndex(conn.getReceiver(), receiverInv);

                        receiverInv.getArguments().set(
                                argIndex,
                                Argument.NULL);


                    } catch (Exception ex) {
                        ex.printStackTrace(System.err);
                    }

                }
            }
        } // end while

        currentlyUpdatingFromUI = false;
//        create(controlFlow);
    }

    private int connectorsToArgIndex(Connector connector, Invocation receiverInvocation) {
        // TODO: ony use this for inputs and only for dataflow (args) 04.06.2014
        return ((ArgumentValue) connector.getValueObject().getValue()).getArgIndex();
    }

    private void initListeners(ControlFlow controlFlow) {
        if (listenersInitialized) {
            return;
        }
        controlFlow.getParent().getFlow().
                getConnections(WorkflowUtil.DATA_FLOW).getConnections().
                addListener(
                        (ListChangeListener.Change<? extends Connection> c) -> {

                            updateDataFlowFromVisualChange(controlFlow, c);

                            controlFlow.getParent().fireEvent(
                                    new CodeEvent(CodeEventType.CHANGE,
                                            controlFlow.getParent()));

                        });
        listenersInitialized = true;
    }

}
