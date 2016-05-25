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

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class IncreaseIndexAction extends Action<CEList> {

    public boolean verify(State<CEList> s) {
        s = s.clone();

        effect.apply(s);
        return precond.verify(s);
    }

    public IncreaseIndexAction() {

        precond.add(new ConditionPredicate<CEList>() {

            @Override
            public boolean verify(State<CEList> s) {

                s = s.clone();
                int index = s.get(0).getIndex();
                setName("increase index  ");// + (index + 1));
                return index < s.get(0).size();
            }

            @Override
            public String getName() {
                return "increase index";
            }
        });

        effect.add(new EffectPredicate<CEList>() {

            @Override
            public void apply(State<CEList> s) {
                s.get(0).increaseIndex();
            }

            @Override
            public String getName() {
                return "increase index";
            }
        });
    }

    @Override
    public double getCosts(State<CEList> s) {
        return 1;
    }

}
