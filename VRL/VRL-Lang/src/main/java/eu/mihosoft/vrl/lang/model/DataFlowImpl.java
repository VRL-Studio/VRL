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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import eu.mihosoft.vrl.lang.workflow.WorkflowUtil;
import eu.mihosoft.vrl.workflow.Connection;
import eu.mihosoft.vrl.workflow.ConnectionResult;
import eu.mihosoft.vrl.workflow.Connections;
import eu.mihosoft.vrl.workflow.Connector;
import eu.mihosoft.vrl.workflow.VNode;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ListChangeListener;

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

        System.out.println("sender: " + relationsForSender.get(sender).size());

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
        
        String rand = "rand: "+(int)(Math.random()*100) + " :: ";

        System.out.println( rand + ">> creating dataflow: ");

//        Map<Integer, Invocation> senders = new HashMap<>();
//        for (Invocation i : controlFlow.getInvocations()) {
//            System.out.println(" --> i:" + i.getMethodName());
//            if (!i.isVoid()) {
//                System.out.println("  |--> potential sender with var " + i.getReturnValue().get().getName());
//                senders.put(i.getReturnValue().get().getName(), i);
//            }
//        }
        for (Invocation receiver : controlFlow.getInvocations()) {
            System.out.println(rand+ " -> receiver: " + receiver);
            int argIndex = 0;
            for (IArgument a : receiver.getArguments()) {

//                Variable v = a.getVariable().get();
//                Invocation sender = senders.get(v.getName());
                System.out.println(rand+ ">> searching sender for " + receiver.getMethodName() + " " + a);
                Invocation sender = null;

                if (a.getArgType() == ArgumentType.INVOCATION) {
                    sender = a.getInvocation().get();
                } else if (a.getArgType() == ArgumentType.VARIABLE) {
                    sender = a.getVariable().get().getDeclaration();
                }

                if (sender != null) {
                    System.out.println(rand +
                            " --> sender found " + sender.getMethodName());
                    createDataRelation(sender, receiver, a, argIndex);
                } else {
                    System.out.println(rand + " -> argType " + a.getArgType() + " not supported!");
                }
                argIndex++;
            } // end for arg
        }

        // create visual dataflow
        updateVisualDataFlowFromModel(controlFlow);

//        // create subflows
//        for (Invocation i : controlFlow.getInvocations()) {
//            if (i instanceof ScopeInvocation) {
//                Scope subScope = ((ScopeInvocation) i).getScope();
//                subScope.getDataFlow().create(subScope.getControlFlow());
//            }
//        }
        // create subflows
        for (Scope scope : controlFlow.getParent().getScopes()) {
            scope.getDataFlow().create(scope.getControlFlow());
        }

//                boolean isClassOrScript = getType() == ScopeType.CLASS || getType() == ScopeType.NONE || getType() == ScopeType.COMPILATION_UNIT;
//
//        if (isClassOrScript) {
//            for (Scope s : getScopes()) {
//                ((ScopeImpl)s).generateDataFlow();
//            }
//        }
    }

    private void updateVisualDataFlowFromModel(ControlFlow controlFlow) {
        if (!currentlyUpdatingFromUI) {
            currentlyUpdatingFromModel = true;

            Connections connections = controlFlow.getParent().getFlow().
                    getConnections(WorkflowUtil.DATA_FLOW);

//            connections.getConnections().clear();
            for (Connection c : new ArrayList<Connection>(connections.getConnections())) {
                controlFlow.getParent().getFlow().getConnections(WorkflowUtil.DATA_FLOW).remove(c);
            }

            for (DataRelation dR : getRelations()) {
                Connector sConn = dR.getSender().getNode().
                        getMainOutput(WorkflowUtil.DATA_FLOW);
                Connector rConn = dR.getReceiver().getNode().
                        getInputs().filtered(i -> i.getType().equals(WorkflowUtil.DATA_FLOW)).get(dR.getReceiverArgIndex());

                ConnectionResult compatible = controlFlow.getParent().getFlow().connect(sConn, rConn);

                if (!compatible.getStatus().isCompatible()) {
                    System.err.println("Connection not compatible! " + compatible.getStatus().getMessage());
                }
            }

            currentlyUpdatingFromModel = false;
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

//                                Connector output = variableConnectors.get(
//                                        getVariableId(senderN, senderInv.getReturnValue().get().getName()));
//                                Connector input = variableConnectors.get(
//                                        getVariableId(receiverN, senderInv.getReturnValue().get().getName()));
                    // TODO if (senderInv equals variableinvocation) only invocations supported 18.02.2014
                    try {

//                        int argIndex = connectorsToArgIndex.get(conn.getReceiver().getId());
                        int argIndex = connectorsToArgIndex(conn.getReceiver(), receiverInv);

                        IArgument arg = Argument.NULL;

                        if (senderInv instanceof DeclarationInvocation) {
                            arg = Argument.varArg(((DeclarationInvocation) senderInv).getDeclaredVariable());
                        } else if (senderInv instanceof Invocation) {
                            arg = Argument.invArg(senderInv);
                        } else {
                            System.err.println("NOT Supported as argument: " + senderInv);
                        }

                        receiverInv.getArguments().set(
                                argIndex,
                                arg);
//
//                                    System.out.println("argIndex: " + argIndex + "argument: " + senderInv + ", recInv: " + receiverInv);

//                        senderInv.getParent().generateDataFlow();
                        ((ScopeImpl) senderInv.getParent()).generateDataFlow();
                    } catch (Exception ex) {
                        ex.printStackTrace(System.err);
                    }
                }
            }

            if (change.wasRemoved()) {
                for (Connection conn : change.getRemoved()) {
                    VNode senderN = conn.getSender().getNode();
                    VNode receiverN = conn.getReceiver().getNode();

//                    Invocation senderInv = nodeInvocations.get(senderN.getId());
//                    Invocation receiverInv = nodeInvocations.get(receiverN.getId());
                    Invocation senderInv = (Invocation) senderN.getValueObject().getValue();//nodeInvocations.get(senderN.getId());
                    Invocation receiverInv = (Invocation) receiverN.getValueObject().getValue();//nodeInvocations.get(receiverN.getId());

                    try {

//                        int argIndex = connectorsToArgIndex.get(conn.getReceiver().getId());
                        int argIndex = connectorsToArgIndex(conn.getReceiver(), receiverInv);

                        receiverInv.getArguments().set(
                                argIndex,
                                Argument.NULL);

//                                    System.out.println("argIndex: " + argIndex + "argument: " + senderInv + ", recInv: " + receiverInv);
//                        senderInv.getParent().generateDataFlow();
//                        ((ScopeImpl) senderInv.getParent()).generateDataFlow();
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
                getConnections(WorkflowUtil.DATA_FLOW).getConnections().addListener(
                        (ListChangeListener.Change<? extends Connection> c) -> {
//                            currentlyUpdatingBecauseOfUIEvent = true;
//                            create(controlFlow);
//                            currentlyUpdatingBecauseOfUIEvent = false;

                            updateDataFlowFromVisualChange(controlFlow, c);

                            controlFlow.getParent().fireEvent(new CodeEvent(CodeEventType.CHANGE, controlFlow.getParent()));

                        });
        listenersInitialized = true;
    }

//    public void generateDataFlow() {
//
//        System.out.println("DATAFLOW---------------------------------");
//
//        for (Invocation i : controlFlow.getInvocations()) {
////            System.out.println("invocation: " + i);
//            for (Variable v : i.getArguments()) {
//                System.out.println("--> varname: " + v.getName() + ", " + i);
//            }
//
//            if (i instanceof ScopeInvocation) {
//                ((ScopeInvocation) i).getScope().generateDataFlow();
//            }
//        }
//
//        boolean isClassOrScript = getArgType() == ScopeType.CLASS || getArgType() == ScopeType.NONE;
//
//        if (isClassOrScript) {
//            for (Scope s : getScopes()) {
//                s.generateDataFlow();
//            }
//        }
//    }
}
