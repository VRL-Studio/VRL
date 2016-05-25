package eu.mihosoft.vrl.lang.model.diff1.actions;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import eu.mihosoft.ai.astar.Action;
import eu.mihosoft.ai.astar.ConditionPredicate;
import eu.mihosoft.ai.astar.EffectPredicate;
import eu.mihosoft.ai.astar.State;
import eu.mihosoft.vrl.lang.model.diff1.CEList;
import eu.mihosoft.vrl.lang.model.diff1.CodeEntityWithIndex;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class DeleteAction extends Action<CEList> {

    List<CodeEntityWithIndex> nodesToRemove = new ArrayList<>();

    public boolean verify(State<CEList> s) {
        s = s.clone();

        effect.apply(s);
        return precond.verify(s);
    }

    public DeleteAction() {

        setName("delete");

        precond.add(new ConditionPredicate<CEList>() {

            @Override
            public boolean verify(State<CEList> s) {
                s = s.clone();
                int index = s.get(0).getIndex();
                return index < s.get(0).size() && index > -1 && s.get(0).size() > 0;
            }

            @Override
            public String getName() {
                return "delete";
            }
        });

        effect.add(new EffectPredicate<CEList>() {

            @Override
            public void apply(State<CEList> s) {
               // int index = s.get(0).getIndex();
                nodesToRemove = s.get(0).getSubtree(s.get(0).getCurrentCodeEntityWithIndex());
                s.get(0).getEntities().removeAll(nodesToRemove); // Knoten - eindeutige ID 
                
                
              //  CodeEntity ce = s.get(0).getCodeEntity(0);
                
//                if(ce instanceof MethodDeclaration && ce.getParent() instanceof ClassDeclaration) {
//                    ClassDeclaration cls = (ClassDeclaration) ce.getParent();
//                    cls.getDeclaredMethods().remove(ce);
//                    
//                    // oder besser model commands
//                }
                
//                for (int i = index; i < index + nodesToRemove.size(); i++) {
//                    s.get(0).getEntities().remove(index);
//                }

                //s.get(0).remove(s.get(0).getIndex());
            }

            @Override
            public String getName() {
                return "delete";
            }
        });

    }

    @Override
    public double getCosts(State<CEList> s) {
        return nodesToRemove.size();
    }

}
