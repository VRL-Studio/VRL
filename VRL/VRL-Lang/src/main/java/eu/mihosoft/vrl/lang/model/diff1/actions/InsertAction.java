/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff1.actions;

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
public class InsertAction extends Action<CEList> {

    List<CodeEntityWithIndex> nodesToInsert = new ArrayList<>();

    public boolean verify(State<CEList> s) {
        s = s.clone();

        effect.apply(s);
        return precond.verify(s);
    }

    public InsertAction(CodeEntityWithIndex targetEntity, CEList target) {

        setName("insert " + '"' + targetEntity.getName() + '"');

        precond.add(new ConditionPredicate<CEList>() {

            @Override
            public boolean verify(State<CEList> s) {

                s = s.clone();
                int index = s.get(0).getIndex();
                setName("insert " + '"' + targetEntity.getCodeEntity().getClass().getSimpleName() + " (" + targetEntity.getTreeIndex() +", "+targetEntity.getName()+")" +'"'); // structure and name similarity prüfen 
                nodesToInsert = target.getSubtree(targetEntity);

                return index < s.get(0).size() + 1 && index > -1 && nodesToInsert.size() > 0;
            }

            @Override
            public String getName() {
                return "insert " + '(' + targetEntity.getTreeIndex() + ',' + targetEntity.getName() + ')';
            }

        });

        effect.add(new EffectPredicate<CEList>() {

            @Override
            public void apply(State<CEList> s) {
                int index = s.get(0).getIndex();
                s.get(0).getEntities().addAll(index, nodesToInsert);

//                if (index < s.get(0).size()) {
//                    s.get(0).addOnPos(index, targetEntity);
//                } else if (index == s.get(0).size()) {
//                    s.get(0).add(targetEntity); // end of the list
//                } else {
//                    System.out.println("Index out of bounds!!!!!!!!!!!!!!!!!!");
//                }
            }

            @Override
            public String getName() {
                return "insert " + '(' + targetEntity.getTreeIndex() + ',' + targetEntity.getName() + ')';
            }

        });

    }

    @Override
    public double getCosts(State<CEList> s) {
        return nodesToInsert.size();
    }

}
