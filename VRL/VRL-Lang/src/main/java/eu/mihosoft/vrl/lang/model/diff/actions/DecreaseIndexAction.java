/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff.actions;

import eu.mihosoft.ai.astar.Action;
import eu.mihosoft.ai.astar.ConditionPredicate;
import eu.mihosoft.ai.astar.EffectPredicate;
import eu.mihosoft.ai.astar.State;
import eu.mihosoft.vrl.lang.model.diff.CodeEntityList;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class DecreaseIndexAction extends Action<CodeEntityList> {

    public boolean verify(State<CodeEntityList> s) {
        s = s.clone();
        effect.apply(s);
        return precond.verify(s);
    }

    public DecreaseIndexAction() {
        setName("decrease index: i--");
        
        precond.add(new ConditionPredicate<CodeEntityList>() {

            @Override
            public boolean verify(State<CodeEntityList> s) {

                s = s.clone();
                int index = s.get(0).getIndex() - 1;
                return index > 0;
            }

            @Override
            public String getName() {
                return "decrease index";
            }
        });

        effect.add(new EffectPredicate<CodeEntityList>() {

            @Override
            public void apply(State<CodeEntityList> s) {
                s.get(0).decreaseIndex();
            }

            @Override
            public String getName() {
                return "decrease index";
            }
        });

    }

    @Override
    public double getCosts(State<CodeEntityList> s) {
        return 1;
    }

}
