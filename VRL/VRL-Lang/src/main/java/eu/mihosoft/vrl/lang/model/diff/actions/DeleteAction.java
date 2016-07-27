package eu.mihosoft.vrl.lang.model.diff.actions;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import eu.mihosoft.ai.astar.Action;
import eu.mihosoft.ai.astar.ConditionPredicate;
import eu.mihosoft.ai.astar.EffectPredicate;
import eu.mihosoft.ai.astar.State;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.Variable;
import eu.mihosoft.vrl.lang.model.diff.CodeEntityList;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class DeleteAction extends Action<CodeEntityList> {

    public boolean verify(State<CodeEntityList> s) {
        s = s.clone();

        effect.apply(s);
        return precond.verify(s);
    }

    int index;
    int cost = 1;

    public DeleteAction() {

        setName("delete");

        precond.add(new ConditionPredicate<CodeEntityList>() {

            @Override
            public boolean verify(State<CodeEntityList> s) {
                s = s.clone();

                index = s.get(0).getIndex();
                if (index < s.get(0).size() && index > 0) {
                    if (s.get(0).get(index) instanceof Scope) {
                        cost = s.get(0).subtreeSize((Scope) s.get(0).get(index));
                    } else {
                        cost = 1;
                    }
                }

                if (cost == 0) {
                    cost = 1;
                }

                boolean bool = true;
                if (index < s.get(0).size()) {
                    if (s.get(0).get(index) instanceof MethodDeclaration) {
                        MethodDeclaration meth = (MethodDeclaration) s.get(0).get(index);
                        if (meth.getName().equals("this$dist$invoke$1") || meth.getName().equals("this$dist$set$1") || meth.getName().equals("this$dist$get$1")) {
                            bool = false;
                        }
                    }
                }
                return bool && index < s.get(0).size() && index > 0 && s.get(0).size() > 0;
            }

            @Override
            public String getName() {
                return "delete";
            }
        });

        effect.add(new EffectPredicate<CodeEntityList>() {

            @Override
            public void apply(State<CodeEntityList> s) {
                CodeEntity currentEntity = s.get(0).get(index);

                if (currentEntity instanceof ClassDeclaration && currentEntity.getParent() instanceof CompilationUnitDeclaration) {
                    CompilationUnitDeclaration cud = (CompilationUnitDeclaration) currentEntity.getParent();
                    ClassDeclaration cd = (ClassDeclaration) currentEntity;
                    if (cud.getDeclaredClasses().size() > 1) {
                        IModelCommands.getInstance().removeScope(cud, cd);
                        s.get(0).updateCodeEntityList(cud);
                    }
                } else if (currentEntity instanceof MethodDeclaration && currentEntity.getParent() instanceof ClassDeclaration) {
                    ClassDeclaration cd = (ClassDeclaration) currentEntity.getParent();
                    MethodDeclaration meth = (MethodDeclaration) currentEntity;
                    IModelCommands.getInstance().removeMethodFromClass(cd, meth);
                    s.get(0).updateCodeEntityList(cd);
                } else if (currentEntity instanceof Variable) {
                    if (currentEntity.getParent() instanceof ClassDeclaration) {
                        System.out.println("Variable and ClassDeclaration");
                    } else if (currentEntity.getParent() instanceof MethodDeclaration) {
                        System.out.println("Variable and MethodDeclaration");
                    }
                }
            }

            @Override
            public String getName() {
                return "delete";
            }
        });
    }

    @Override
    public double getCosts(State<CodeEntityList> s) {
        return cost;
    }
}
