/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff;

import eu.mihosoft.ai.astar.Condition;
import eu.mihosoft.ai.astar.State;
import eu.mihosoft.ai.astar.WorldDescription;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class Heuristic implements eu.mihosoft.ai.astar.Heuristic<CodeEntityList> {

    public Heuristic() {
    }

    @Override
    public double estimate(State<CodeEntityList> state, Condition<CodeEntityList> goal, WorldDescription<CodeEntityList> wd) {
        CodeEntityList currentList = state.get(0);

        if (!(goal instanceof CodeEntityListGoal)) {
            return (int) Double.MAX_VALUE;
        }

        CodeEntityList goalList = ((CodeEntityListGoal) goal).getCodeEntityList();

        double lcsSize = LongestCommonSubsequence.lcs(goalList.getEntities(), currentList.getEntities()).size();
        double goalListSize = goalList.size();
        double currListSize = currentList.size();
        double max = Math.max(goalListSize, currListSize);
        double min = Math.min(goalListSize, currListSize);
//        System.out.println("Distanz " + Math.abs(lcsSize - max));

        return Math.abs(lcsSize - max);
        //return 0.0;
    }

}
