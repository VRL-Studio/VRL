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
import eu.mihosoft.vrl.lang.model.Variable;
import eu.mihosoft.vrl.lang.model.diff.CodeEntityList;
import eu.mihosoft.vrl.lang.model.diff.SimilarityMetric;

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

    int index = 0;

    public DeleteAction() {

        setName("delete");

        precond.add(new ConditionPredicate<CodeEntityList>() {

            @Override
            public boolean verify(State<CodeEntityList> s) {
                s = s.clone();
                
                index = s.get(0).getIndex();
                return index < s.get(0).size() && index > -1 && s.get(0).size() > 0;
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
                CodeEntity rootEntity = s.get(0).getRoot(currentEntity);

                if (currentEntity instanceof ClassDeclaration && currentEntity.getParent() instanceof CompilationUnitDeclaration) {
                    IModelCommands.getInstance().removeClassFromCUD(currentEntity.getParent(), currentEntity);
                } else if (currentEntity instanceof MethodDeclaration && currentEntity.getParent() instanceof ClassDeclaration) {
                    CodeEntity parent = currentEntity.getParent();
                    ClassDeclaration cd = 
                            IModelCommands.getInstance().removeMethodFromClass(parent, currentEntity);
                    System.out.println("REMOVE " + SimilarityMetric.getName(currentEntity));
                    for (MethodDeclaration m : cd.getDeclaredMethods()) {
                        System.out.println("Declared Methods " + m.getName());
                    }
                    System.out.println("+++++++++++++++++++++++++++++++++++++++++");
                    cd.visitScopeAndAllSubElements((CodeEntity e) -> {
                        if (e instanceof MethodDeclaration || e instanceof Variable) {
                            System.out.println("VISIT " + SimilarityMetric.getName(e));
                        }
                    });
                    System.out.println("++++++++++++++++++END REMOVE+++++++++++++++++++++++");
                    

                } else if (currentEntity.getParent() != null) {
                    IModelCommands.getInstance().removeScope(currentEntity.getParent(), currentEntity);
                }
                s.get(0).remove(index);
            }

            @Override
            public String getName() {
                return "delete";
            }
        });

    }

    @Override
    public double getCosts(State<CodeEntityList> s) {
        return 1;
    }
}
