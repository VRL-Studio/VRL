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
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.Variable;
import eu.mihosoft.vrl.lang.model.diff.CodeEntityList;
import eu.mihosoft.vrl.lang.model.diff.SimilarityMetric;
import java.util.List;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class InsertAction extends Action<CodeEntityList> {

    int index;
    int cost = 1;

    public boolean verify(State<CodeEntityList> s) {
        s = s.clone();

        effect.apply(s);
        return precond.verify(s);
    }

    public InsertAction(CodeEntity entity) {

        setName("insert " + '"' + SimilarityMetric.getCodeEntityName(entity) + '"');
        precond.add(new ConditionPredicate<CodeEntityList>() {

            @Override
            public boolean verify(State<CodeEntityList> s) {

                s = s.clone();
                index = s.get(0).getIndex();

                if (index < s.get(0).size() + 1 && index > 0) {
                    CodeEntity preCodeEntity = s.get(0).get(index - 1);

                    if (entity instanceof Scope) {
                        Scope scope = (Scope) entity;
                        cost = s.get(0).subtreeSize(scope) + scope.getVariables().size();
                        if (cost == 0) {
                            cost = 1;
                        }
                    }

                    if (entity instanceof ClassDeclaration) {
                        ClassDeclaration cls = (ClassDeclaration) entity;
                        String name = VLangUtils.shortNameFromFullClassName(cls.getName());
                        if (s.get(0).getClassNames().contains(name)) {
                            return false;
                        }
                    } else if (entity instanceof MethodDeclaration) {
                        MethodDeclaration methToInsert = (MethodDeclaration) entity;
                        if (preCodeEntity instanceof ClassDeclaration) {
                            ClassDeclaration classDecl = (ClassDeclaration) preCodeEntity;
                            return checkMethod(classDecl.getDeclaredMethods(), methToInsert);
                        } else if (preCodeEntity instanceof MethodDeclaration) {
                            MethodDeclaration preMeth = (MethodDeclaration) preCodeEntity;
                            return checkMethod(preMeth.getClassDeclaration().getDeclaredMethods(), methToInsert);
                        }
                    }
                } else {
                    return false;
                }
                return true;
            }

            @Override
            public String getName() {
                return "insert " + '"' + SimilarityMetric.getCodeEntityName(entity) + '"';
            }

        });

        effect.add(new EffectPredicate<CodeEntityList>() {

            @Override
            public void apply(State<CodeEntityList> s) {

                CodeEntity preCodeEntity = s.get(0).get(index - 1);

                if (preCodeEntity instanceof CompilationUnitDeclaration && entity instanceof ClassDeclaration) { // add Class to CUD on Position 0
                    CompilationUnitDeclaration cud = (CompilationUnitDeclaration) preCodeEntity;
                    ClassDeclaration cd = (ClassDeclaration) entity;
                    IModelCommands.getInstance().insertScope(cud, 0, cd);
                    s.get(0).updateCodeEntityList(preCodeEntity);
                } else if (preCodeEntity instanceof ClassDeclaration) {
                    ClassDeclaration class1 = (ClassDeclaration) preCodeEntity;
                    if (entity instanceof ClassDeclaration && class1.getDeclaredMethods().isEmpty()) { //solange es die default Groovy-Methoden gibt wird dieser Fall nie auftreten
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
                        IModelCommands.getInstance().insertMethodToClass(class1, 0, meth);
                    } else if (entity instanceof Variable) {
                        Variable var = (Variable) entity;
                        IModelCommands.getInstance().insertVariableToScope(class1, var);
                    }
                    s.get(0).updateCodeEntityList(preCodeEntity);
                } else if (preCodeEntity instanceof MethodDeclaration) {
                    MethodDeclaration meth1 = (MethodDeclaration) preCodeEntity;
                    ClassDeclaration class1 = (ClassDeclaration) meth1.getParent();
                    CompilationUnitDeclaration class1Parent = (CompilationUnitDeclaration) class1.getParent();

                    if (entity instanceof ClassDeclaration && class1.getDeclaredMethods().indexOf(meth1) == class1.getDeclaredMethods().size() - 1) {
                        ClassDeclaration class2 = (ClassDeclaration) entity;
                        int class1Pos = class1Parent.getDeclaredClasses().indexOf(class1);
                        IModelCommands.getInstance().insertScope(class1Parent, class1Pos + 1, class2);
                    } else if (entity instanceof MethodDeclaration) {
                        MethodDeclaration meth2 = (MethodDeclaration) entity;
                        int meth1Pos = class1.getDeclaredMethods().indexOf(meth1);
                        IModelCommands.getInstance().insertMethodToClass(class1, meth1Pos + 1, meth2);
                    } else if (entity instanceof Variable) {
                        Variable var = (Variable) entity;
                        IModelCommands.getInstance().insertVariableToScope(meth1, var);
                    }
                    s.get(0).updateCodeEntityList(preCodeEntity);
                }
            }

            @Override
            public String getName() {
                return "insert";
            }

        });
    }

    @Override

    public double getCosts(State<CodeEntityList> s) {
        return cost;
    }

    private boolean checkMethod(List<MethodDeclaration> methodList, MethodDeclaration currentMethod) {
        for (MethodDeclaration meth : methodList) {
            if (meth.getReturnType().equals(currentMethod.getReturnType()) && meth.getParameters().getParamenters().size() == currentMethod.getParameters().getParamenters().size()) {
                for (int i = 0; i < meth.getParameters().getParamenters().size(); i++) {
                    if (!meth.getParameters().getParamenters().get(i).getType().equals(currentMethod.getParameters().getParamenters().get(i).getType())) {
                        return true;
                    }
                }
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

}
