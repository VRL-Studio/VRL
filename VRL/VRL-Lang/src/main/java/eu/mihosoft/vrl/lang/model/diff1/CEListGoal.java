/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.lang.model.diff1;

import eu.mihosoft.ai.astar.Condition;


/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class CEListGoal extends Condition<CEList>{
    
    private final CEList goalList;

    public CEListGoal(CEList goalList) {
        this.goalList = goalList;
        add(new CEListPredicate(goalList));
    }
     public CEList getCodeEntityList() { // getPos() --> use in heuristic functions
        return goalList;
    }
}
