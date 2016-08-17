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
import eu.mihosoft.vrl.lang.VLangUtilsNew;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.diff.CodeEntityList;
import eu.mihosoft.vrl.lang.model.diff.SimilarityMetric;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class RefactoringClassAction extends Action<CodeEntityList> {

    int index = 0;

    public boolean verify(State<CodeEntityList> s) {
        s = s.clone();

        effect.apply(s);
        return precond.verify(s);
    }

    public RefactoringClassAction(CodeEntity entity) {

        setName("Refactoring Class in " + '"' + SimilarityMetric.getCodeEntityName(entity) + '"');

        precond.add(new ConditionPredicate<CodeEntityList>() {

            @Override
            public boolean verify(State<CodeEntityList> s) {

                s = s.clone();
                index = s.get(0).getIndex();
                Boolean bool = false;

                if (index > -1 && index < s.get(0).size()) {

                    CodeEntity currentElement = s.get(0).get(index);
                    if (currentElement instanceof ClassDeclaration && entity instanceof ClassDeclaration) {
                        ClassDeclaration classEntityFrom = (ClassDeclaration) currentElement;
                        ClassDeclaration classEntityTo = (ClassDeclaration) entity;
                        String name = VLangUtilsNew.shortNameFromFullClassName(classEntityTo.getName());

                        if (!classEntityFrom.getName().equals(classEntityTo.getName())) {
                            if (s.get(0).getNames().contains(name)) {
                                int elemPos = s.get(0).getNames().indexOf(name);
                                if (s.get(0).get(elemPos) instanceof ClassDeclaration == false) {
                                    bool = true;
                                }
                            } else {
                                bool = true;
                            }
                        }
                    }
                }
                return bool;
            }

            @Override
            public String getName() {
                return "Refactoring";
            }

        });

        effect.add(new EffectPredicate<CodeEntityList>() {

            @Override
            public void apply(State<CodeEntityList> s) {

                ClassDeclaration classEntityFrom = (ClassDeclaration) s.get(0).get(index);
                ClassDeclaration classEntityTo = (ClassDeclaration) entity;

                RefactoringUtils.renameClassRefactoring(classEntityFrom.getClassType(), classEntityTo.getClassType(), (CompilationUnitDeclaration) CodeEntityList.getRoot(classEntityFrom));

                //s.get(0).updateCodeEntityListAllEntities(classEntityFrom);
                s.get(0).updateCodeEntityList(classEntityFrom);

                System.out.println("XxXXXXxXXXXXXXXXXXXXXXXXXXXxxxXXXXxxxXXXXxxXXXXxx");

            }

            @Override
            public String getName() {
                return "rename";
            }
        });
    }

    @Override
    public double getCosts(State<CodeEntityList> s) {
        return 1;
    }
}
