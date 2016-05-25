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
import eu.mihosoft.vrl.lang.model.diff.CodeEntityList;
import eu.mihosoft.vrl.lang.model.diff.SimilarityMetric;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class InsertAction extends Action<CodeEntityList> {

    public boolean verify(State<CodeEntityList> s) {
        s = s.clone();

        effect.apply(s);
        return precond.verify(s);
    }

    int index = 0;

    public InsertAction(CodeEntity entity) {

        setName("insert " + '"' + SimilarityMetric.getCodeEntityName(entity) + '"');

        precond.add(new ConditionPredicate<CodeEntityList>() {

            @Override
            public boolean verify(State<CodeEntityList> s) {

                s = s.clone();
                index = s.get(0).getIndex();
                setName("insert " + '"' + SimilarityMetric.getCodeEntityName(entity) + '"');
                return index < s.get(0).size() + 1 && index > -1;
            }

            @Override
            public String getName() {
                return "insert " + '"' + SimilarityMetric.getCodeEntityName(entity) + '"';
            }

        });

        effect.add(new EffectPredicate<CodeEntityList>() {

            @Override
            public void apply(State<CodeEntityList> s) {

//                if (index == 0 && entity instanceof CompilationUnitDeclaration) { // first elem is always CUD
//                    s.get(0).setOnPos(index, entity);
//                    
//                } else if (index > 0) {
//                    if (s.get(0).get(index - 1) instanceof CompilationUnitDeclaration && s.get(0).get(index) instanceof ClassDeclaration) { // add Class to CUD on Position 0
//                        IModelCommands.getInstance().insertClassToCUD(s.get(0).get(index - 1), 0, s.get(0).get(index));
//                    } else if (s.get(0).get(index - 1) instanceof ClassDeclaration) {
//                        if (s.get(0).get(index) instanceof ClassDeclaration) { // two classes 
//                            ClassDeclaration class1 = (ClassDeclaration) s.get(0).get(index - 1);
//                            ClassDeclaration class2 = (ClassDeclaration) s.get(0).get(index);
//                            CompilationUnitDeclaration parentCUD = (CompilationUnitDeclaration) class1.getParent();
//
//                            if (parentCUD.getDeclaredClasses().indexOf(class1) == parentCUD.getDeclaredClasses().size() - 1) {// end of the list
//                                IModelCommands.getInstance().insertClassToCUD(parentCUD, class2);
//                            } else {
//                                IModelCommands.getInstance().insertClassToCUD(parentCUD, parentCUD.getDeclaredClasses().indexOf(class1) + 1, class2);
//                            }
//
//                        } else if (s.get(0).get(index) instanceof MethodDeclaration) { // class and method
//                            IModelCommands.getInstance().insertMethodToClass(s.get(0).get(index - 1), 0, s.get(0).get(index));
//                        }
//                    } else if (s.get(0).get(index - 1) instanceof MethodDeclaration) {
//                        if (s.get(0).get(index) instanceof MethodDeclaration) { // two methods
//                            MethodDeclaration meth1 = (MethodDeclaration) s.get(0).get(index - 1);
//                            MethodDeclaration meth2 = (MethodDeclaration) s.get(0).get(index);
//                            ClassDeclaration parent = (ClassDeclaration) meth1.getClassDeclaration();
//                            if (parent.getDeclaredMethods().indexOf(meth1) == parent.getDeclaredMethods().size() - 1) {// end of the list
//                                IModelCommands.getInstance().insertMethodToClass(parent, meth2);
//                            } else {
//                                IModelCommands.getInstance().insertMethodToClass(parent, parent.getDeclaredMethods().indexOf(meth1) + 1, meth2);
//                            }
//
//                        } else if (s.get(0).get(index) instanceof Variable) { // method variable
//                            IModelCommands.getInstance().insertVariableToMethod(s.get(0).get(index - 1), s.get(0).get(index));
//                        }
//                    }
//                    //s.get(0).updateList(s.get(0).getRoot(s.get(0).get(index - 1)));
//                }

                if (index < s.get(0).size()) {
                    s.get(0).addOnPos(index, entity);
                } else if (index == s.get(0).size()) {
                    s.get(0).add(entity); // end of the list
                } else {
                    System.out.println("Index out of bounds!!!!!!!!!!!!!!!!!!");
                }

            }

            @Override
            public String getName() {
                return "insert " + '"' + SimilarityMetric.getCodeEntityName(entity) + '"';
            }

        });

    }

    @Override

    public double getCosts(State<CodeEntityList> s) {
        return 1;
    }

}
