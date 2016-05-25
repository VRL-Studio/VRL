/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.lang.model.diff;

import eu.mihosoft.ai.astar.Condition;


/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class CodeEntityListGoal extends Condition<CodeEntityList>{
    
    private final CodeEntityList goalList;

    public CodeEntityListGoal(CodeEntityList goalList) {
        this.goalList = goalList;
        add(new CodeEntityListPredicate(goalList));
    }
     public CodeEntityList getCodeEntityList() { // getPos() --> use in heuristic functions
        return goalList;
    }
}
