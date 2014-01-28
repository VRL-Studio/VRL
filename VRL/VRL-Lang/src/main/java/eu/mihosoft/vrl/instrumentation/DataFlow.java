/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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