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
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.diff.CodeEntityList;
import eu.mihosoft.vrl.lang.model.diff.SimilarityMetric;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class SetParametersAction extends Action<CodeEntityList> {

    int index = 0;

    public SetParametersAction(CodeEntity codeEntity) {
        setName("Set params in " + '"' + SimilarityMetric.getCodeEntityName(codeEntity) + '"');

        precond.add(new ConditionPredicate<CodeEntityList>() {

            @Override
            public boolean verify(State<CodeEntityList> s) {
                s = s.clone();
                index = s.get(0).getIndex();
                boolean result = false;

                if (index > -1 && index < s.get(0).size()) {

                    CodeEntity currentElement = s.get(0).get(index);
                    if (currentElement instanceof MethodDeclaration && codeEntity instanceof MethodDeclaration) {
                        MethodDeclaration currentMethod = (MethodDeclaration) currentElement;
                        MethodDeclaration methodType = (MethodDeclaration) codeEntity;
                        if (!currentMethod.getName().equals("this$dist$invoke$1") && !currentMethod.getName().equals("this$dist$set$1") && !currentMethod.getName().equals("this$dist$get$1") && !currentMethod.getParameters().equals(methodType.getParameters())) {
                            result = true;
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

                MethodDeclaration currentMethod = (MethodDeclaration) s.get(0).get(index);
                MethodDeclaration methodParam = (MethodDeclaration) codeEntity;
                IModelCommands.getInstance().setMethodParameters(methodParam.getParameters(), currentMethod);

            }

            @Override
            public String getName() {
                return "set param in method";
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
