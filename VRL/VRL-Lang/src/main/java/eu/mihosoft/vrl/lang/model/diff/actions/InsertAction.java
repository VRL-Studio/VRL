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
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.Variable;
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
                boolean bool = true;
                if (entity instanceof MethodDeclaration) {
                    MethodDeclaration meth = (MethodDeclaration) entity;
                    if (meth.getName().equals("this$dist$invoke$1") || meth.getName().equals("this$dist$set$1") || meth.getName().equals("this$dist$get$1")) {
                        bool = false;
                    }
                }
                return bool && index < s.get(0).size() + 1 && index > -1;
            }

            @Override
            public String getName() {
                return "insert " + '"' + SimilarityMetric.getCodeEntityName(entity) + '"';
            }

        });

        effect.add(new EffectPredicate<CodeEntityList>() {

            @Override
            public void apply(State<CodeEntityList> s) {

                if (index > 0) {
                    if (s.get(0).get(index - 1) instanceof CompilationUnitDeclaration && entity instanceof ClassDeclaration) { // add Class to CUD on Position 0
                        CompilationUnitDeclaration cud = (CompilationUnitDeclaration) s.get(0).get(index - 1);
                        ClassDeclaration cd = (ClassDeclaration) entity;
                        IModelCommands.getInstance().insertScope(cud, 0, cd);
                        System.out.println(" CUD --> Class ");
                    } else if (s.get(0).get(index - 1) instanceof ClassDeclaration) {
                        ClassDeclaration class1 = (ClassDeclaration) s.get(0).get(index - 1);
                        if (entity instanceof ClassDeclaration) { // two classes 
                            ClassDeclaration class2 = (ClassDeclaration) entity;
                            CompilationUnitDeclaration class1Parent = (CompilationUnitDeclaration) class1.getParent();
                            int class1Pos = class1Parent.getDeclaredClasses().indexOf(class1);
                            if (class1Pos == class1Parent.getDeclaredClasses().size() - 1) {// end of the list
                                IModelCommands.getInstance().insertScope(class1Parent, class2);
                            } else {
                                IModelCommands.getInstance().insertScope(class1Parent, class1Pos + 1, class2);
                            }
                        } else if (entity instanceof MethodDeclaration) {
                            MethodDeclaration meth = (MethodDeclaration) entity;
                            IModelCommands.getInstance().insertScope(class1, 0, meth);

                        } else if (entity instanceof Variable) {
                            Variable var = (Variable) entity;
                            IModelCommands.getInstance().insertScope(class1, 0, var);
                        }

                    } else if (s.get(0).get(index - 1) instanceof MethodDeclaration) {
                        MethodDeclaration meth1 = (MethodDeclaration) s.get(0).get(index - 1);
                        ClassDeclaration meth1Parent = (ClassDeclaration) meth1.getParent();
                        CompilationUnitDeclaration math1Ancestor = (CompilationUnitDeclaration) meth1Parent.getParent();
                        if (entity instanceof ClassDeclaration) {
                            ClassDeclaration class2 = (ClassDeclaration) entity;
                            int math1ParentPos = math1Ancestor.getDeclaredClasses().indexOf(meth1Parent);
                            System.out.println("POSITION " + math1ParentPos);
                            if (math1ParentPos == math1Ancestor.getDeclaredClasses().size() - 1) {// end of the list
                                IModelCommands.getInstance().insertScope(math1Ancestor, class2);
                                System.out.println("Insert " + class2.getName());
                            } else {
                                IModelCommands.getInstance().insertScope(math1Ancestor, math1ParentPos + 1, class2);
                            }
                        } else if (entity instanceof MethodDeclaration) {
                            MethodDeclaration meth2 = (MethodDeclaration) entity;
                            int meth1Pos = meth1Parent.getDeclaredMethods().indexOf(meth1);
                            if (meth1Pos == meth1Parent.getDeclaredMethods().size() - 1) {// end of the list
                                IModelCommands.getInstance().insertScope(meth1Parent, meth2);
                            } else {
                                IModelCommands.getInstance().insertScope(meth1Parent, meth1Pos + 1, meth2);
                            }
                        } else if (entity instanceof Variable) {
                            Variable var = (Variable) entity;
                            IModelCommands.getInstance().insertScope(meth1, var);
                        }

                    } else if (s.get(0).get(index - 1) instanceof Variable) {
                        Variable var = (Variable) s.get(0).get(index - 1);
                        if (var.getParent() instanceof MethodDeclaration) {
                            MethodDeclaration parent = (MethodDeclaration) var.getParent();
                            if (entity instanceof Variable) {
                                IModelCommands.getInstance().insertScope(parent, entity);
                            } else if (entity instanceof ClassDeclaration) {
                                ClassDeclaration class1 = (ClassDeclaration) parent.getParent();
                                ClassDeclaration class2 = (ClassDeclaration) entity;
                                CompilationUnitDeclaration math1Ancestor = (CompilationUnitDeclaration) class1.getParent();

                                if (math1Ancestor.getDeclaredClasses().indexOf(class1) == math1Ancestor.getDeclaredClasses().size() - 1) {// end of the list
                                    IModelCommands.getInstance().insertScope(math1Ancestor, class2);
                                } else {
                                    IModelCommands.getInstance().insertScope(math1Ancestor, math1Ancestor.getDeclaredClasses().indexOf(class1) + 1, class2);
                                }
                            } else if (entity instanceof MethodDeclaration) {
                                MethodDeclaration meth2 = (MethodDeclaration) entity;
                                ClassDeclaration class1 = (ClassDeclaration) meth2.getParent();
                                if (class1.getDeclaredMethods().indexOf(parent) == class1.getDeclaredMethods().size() - 1) {// end of the list
                                    IModelCommands.getInstance().insertScope(class1, meth2);
                                } else {
                                    IModelCommands.getInstance().insertScope(class1, class1.getDeclaredMethods().indexOf(parent) + 1, meth2);
                                }
                            }
                        } else if (var.getParent() instanceof ClassDeclaration) {
                            ClassDeclaration parent = (ClassDeclaration) var.getParent();
                            if (entity instanceof Variable) {
                                IModelCommands.getInstance().insertScope(parent, entity);
                            } else if (entity instanceof ClassDeclaration) {
                                ClassDeclaration class1 = (ClassDeclaration) parent.getParent();
                                ClassDeclaration class2 = (ClassDeclaration) entity;
                                CompilationUnitDeclaration math1Ancestor = (CompilationUnitDeclaration) class1.getParent();

                                if (math1Ancestor.getDeclaredClasses().indexOf(class1) == math1Ancestor.getDeclaredClasses().size() - 1) {// end of the list
                                    IModelCommands.getInstance().insertScope(math1Ancestor, class2);
                                } else {
                                    IModelCommands.getInstance().insertScope(math1Ancestor, math1Ancestor.getDeclaredClasses().indexOf(class1) + 1, class2);
                                }
                            } else if (entity instanceof MethodDeclaration) {
                                MethodDeclaration meth2 = (MethodDeclaration) entity;
                                IModelCommands.getInstance().insertScope(parent, 0, meth2);
                            }
                        }

                    }
                    s.get(0).updateCodeEntityList(CodeEntityList.getRoot(s.get(0).get(index - 1)));
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
