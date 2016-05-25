/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff;

import eu.mihosoft.ai.astar.ConditionPredicate;
import eu.mihosoft.ai.astar.EffectPredicate;
import eu.mihosoft.ai.astar.State;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
class CodeEntityListPredicate implements ConditionPredicate<CodeEntityList>, EffectPredicate<CodeEntityList> {

    private final CodeEntityList entities;

    public CodeEntityListPredicate(CodeEntityList entities) {
        this.entities = new CodeEntityList(entities, true);
    }

    @Override
    public boolean verify(State<CodeEntityList> state) {
        return state.get(0).equals(entities);
    }

    @Override
    public String getName() {
        return "code entity - pred";
    }

    @Override
    public void apply(State<CodeEntityList> state) {
        state.set(0, entities);
    }

}
