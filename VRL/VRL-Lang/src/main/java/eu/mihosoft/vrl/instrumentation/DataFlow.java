/* 
 * DataFlow.java
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

package eu.mihosoft.vrl.instrumentation;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface DataFlow {

    public List<DataRelation> getRelations();

    public List<DataRelation> getRelationsForSender(Invocation invocation);

    public List<DataRelation> getRelationsForReceiver(Invocation invocation);

//    public void addSender(String retValName, Invocation result);
    public void create(ControlFlow controlflow);
}

class DataFlowImpl implements DataFlow {

    List<DataRelation> relations = new ArrayList<>();
    ListMultimap<Invocation, DataRelation> relationsForSender = ArrayListMultimap.create();
    ListMultimap<Invocation, DataRelation> relationsForReceiver = ArrayListMultimap.create();

    void createDataRelation(Invocation sender, Invocation receiver) {
        DataRelationImpl relation = new DataRelationImpl(sender, receiver);

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
        
        System.out.println(">> creating dataflow: ");
        
        Map<String, Invocation> senders = new HashMap<>();

        for (Invocation i : controlFlow.getInvocations()) {
            System.out.println(" --> i:" + i.getMethodName());
            if (!i.isVoid()) {
                System.out.println("  |--> potential sender with var " + i.getReturnValueName());
                senders.put(i.getReturnValueName(), i);
            }
        }

        for (Invocation receiver : controlFlow.getInvocations()) {
            for (Variable v : receiver.getArguments()) {

                Invocation sender = senders.get(v.getName());

                System.out.println(">> searching sender for " + v.getName() + " with type " + v.getType());
                
                if (sender != null) {
                    System.out.println(
                            " --> sender found for '"
                            + v.getName()
                            + "', " + sender.getMethodName());
                    
                    createDataRelation(sender, receiver);
                }
            }
        }

        for (Invocation i : controlFlow.getInvocations()) {
            if (i instanceof ScopeInvocation) {
                Scope subScope = ((ScopeInvocation) i).getScope();
                subScope.getDataFlow().create(subScope.getControlFlow());
            }
        }

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
//        boolean isClassOrScript = getType() == ScopeType.CLASS || getType() == ScopeType.NONE;
//
//        if (isClassOrScript) {
//            for (Scope s : getScopes()) {
//                s.generateDataFlow();
//            }
//        }
//    }
}