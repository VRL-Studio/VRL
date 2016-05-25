/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff1;

import eu.mihosoft.ai.astar.ConditionPredicate;
import eu.mihosoft.ai.astar.EffectPredicate;
import eu.mihosoft.ai.astar.State;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
class CEListPredicate implements ConditionPredicate<CEList>, EffectPredicate<CEList> {

    private final CEList entities;

    public CEListPredicate(CEList entities) {
        this.entities = new CEList(entities, true);
    }

    @Override
    public boolean verify(State<CEList> state) {
        if (state.get(0).equals(entities)) {
            for (int i = 0; i < state.get(0).size(); i++) {
                System.out.println("Source State End " + state.get(0).getEntityName(i));
            }
        }
        return state.get(0).equals(entities);
    }

    @Override
    public String getName() {
        return "code entity - pred";
    }

    @Override
    public void apply(State<CEList> state) {
        state.set(0, entities);
    }

}
