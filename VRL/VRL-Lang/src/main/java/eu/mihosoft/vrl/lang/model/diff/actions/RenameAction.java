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
public class RenameAction extends Action<CodeEntityList> {

    int index = 0;

//    public boolean verify(State<CodeEntityList> s) {
//        s = s.clone();
//
//        effect.apply(s);
//        return precond.verify(s);
//    }
    public RenameAction(CodeEntity entity) {

        setName("Rename to " + "(" + SimilarityMetric.getCodeEntityName(entity) + ")");

        precond.add(new ConditionPredicate<CodeEntityList>() {

            @Override
            public boolean verify(State<CodeEntityList> s) {
                s = s.clone();
                index = s.get(0).getIndex();

                boolean result = false;

                if (index < s.get(0).size() && index > -1) {
                    CodeEntity currentElement = s.get(0).get(index);

                    double similarity;
                    if (entity instanceof CompilationUnitDeclaration) {
                        similarity = SimilarityMetric.packageSimilarity(currentElement, entity);
                    } else {
                        similarity = SimilarityMetric.nameSimilarity(currentElement, entity);
                    }

                    result = similarity > 0.6
                            && !s.get(0).compNames(currentElement, entity);
                }

                System.out.println("RENAME-Precond: " + result);

                return result;

            }

            @Override
            public String getName() {
                return "rename";
            }

        });

        effect.add(new EffectPredicate<CodeEntityList>() {

            @Override
            public void apply(State<CodeEntityList> s) {

                CodeEntity codeEnity = s.get(0).get(index);

                if (entity instanceof CompilationUnitDeclaration && codeEnity instanceof CompilationUnitDeclaration) {
                    System.out.println("RENAME CompilationUnitDeclaration");
                    IModelCommands.getInstance().setCUDeclPackageName(entity, codeEnity);
                    s.get(0).updateList(s.get(0).getRoot(codeEnity));
                } else if (entity instanceof ClassDeclaration && codeEnity instanceof ClassDeclaration) {
                    System.out.println("RENAME ClassDeclaration");
                    ClassDeclaration cls = (ClassDeclaration) entity;
                    IModelCommands.getInstance().setScopeName(cls.getName(), codeEnity);
                    s.get(0).updateList(s.get(0).getRoot(codeEnity));
                } else if (entity instanceof MethodDeclaration && codeEnity instanceof MethodDeclaration) {
                    System.out.println("RENAME MethodDeclaration");
                    IModelCommands.getInstance().setMethodName(entity, codeEnity);
                    s.get(0).updateList(s.get(0).getRoot(codeEnity));
                } else if (entity instanceof Variable && codeEnity instanceof Variable) {
                    System.out.println("RENAME Variable");
                    IModelCommands.getInstance().setVariableName(entity, codeEnity);
                    s.get(0).updateList(s.get(0).getRoot(codeEnity));
                } else {
                    System.out.println("############################# ELSE ############################");
                    s.get(0).setOnPos(index, entity);
                }
              //  s.get(0).setOnPos(index, entity);
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

    @Override
    public String toString() {
        return getName();
    }

}
