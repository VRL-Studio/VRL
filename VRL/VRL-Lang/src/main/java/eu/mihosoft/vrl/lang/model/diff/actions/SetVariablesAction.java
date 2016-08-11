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
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.Variable;
import eu.mihosoft.vrl.lang.model.diff.CodeEntityList;
import eu.mihosoft.vrl.lang.model.diff.SimilarityMetric;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class SetVariablesAction extends Action<CodeEntityList> {

    int index = 0;

    public SetVariablesAction(CodeEntity codeEntityVar) {
        setName("Set Variables in " + '"' + SimilarityMetric.getCodeEntityName(codeEntityVar) + '"');

        precond.add(new ConditionPredicate<CodeEntityList>() {

            @Override
            public boolean verify(State<CodeEntityList> s) {
                s = s.clone();
                index = s.get(0).getIndex();
                boolean result = false;

                if (index > -1 && index < s.get(0).size()) {

                    CodeEntity currentElement = s.get(0).get(index);

                    if (currentElement instanceof Scope && codeEntityVar instanceof Scope) {
                        Scope currentScope = (Scope) currentElement;
                        Scope variablesScope = (Scope) codeEntityVar;
                        if (!currentScope.getName().equals("this$dist$invoke$1") && !currentScope.getName().equals("this$dist$set$1")
                                && !currentScope.getName().equals("this$dist$get$1")
                                && currentScope.getClass().equals(variablesScope.getClass())) {

                            if (currentScope.getVariables().size() != currentScope.getVariables().size()) {
                                result = true;
                            } else {
                                List<Variable> list1 = new ArrayList<>(currentScope.getVariables());
                                List<Variable> list2 = new ArrayList<>(variablesScope.getVariables());
                                for (int i = 0; i < list1.size(); i++) {
                                    if (!list1.get(i).getType().equals(list2.get(i).getType())) {
                                        result = true;
                                        break;
                                    }
                                }
                            }

                        }
                    }
                }

                return result;
            }

            @Override
            public String getName() {
                return "set return type";
            }

        });

        effect.add(new EffectPredicate<CodeEntityList>() {

            @Override
            public void apply(State<CodeEntityList> s) {
                Scope currentScope = (Scope) s.get(0).get(index);
                Scope variablesScope = (Scope) codeEntityVar;
                IModelCommands.getInstance().setVariables(currentScope, variablesScope);
            }

            @Override
            public String getName() {
                return "set variables";
            }
        });
    }

    @Override

    public double getCosts(State<CodeEntityList> s) {
        return 1;
    }

    @Override
    public String toString() {
        return getName();
    }

}
