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
import eu.mihosoft.vrl.lang.VLangUtilsNew;
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

    public boolean verify(State<CodeEntityList> s) {
        s = s.clone();

        effect.apply(s);
        return precond.verify(s);
    }

    public RenameAction(CodeEntity nameEntity) {

        setName("Rename to " + '"' + SimilarityMetric.getCodeEntityName(nameEntity) + '"');
        precond.add(new ConditionPredicate<CodeEntityList>() {

            @Override
            public boolean verify(State<CodeEntityList> s) {
                s = s.clone();
                index = s.get(0).getIndex();

                boolean result = false;
                boolean bool = true;

                if (index > -1 && index < s.get(0).size()) {

                    CodeEntity currentElement = s.get(0).get(index);

                    if (nameEntity instanceof ClassDeclaration) {
                        ClassDeclaration cls = (ClassDeclaration) nameEntity;
                        String name = VLangUtilsNew.shortNameFromFullClassName(cls.getName());
                        if (s.get(0).getNames().contains(name)) {
                            int elemPos = s.get(0).getNames().indexOf(name);
                            if (s.get(0).get(elemPos) instanceof ClassDeclaration) {
                                bool = false;
                            }
                        }
                    } else if (nameEntity instanceof MethodDeclaration) {
                        MethodDeclaration meth = (MethodDeclaration) nameEntity;
                        if (meth.getName().equals("this$dist$invoke$1") || meth.getName().equals("this$dist$set$1") || meth.getName().equals("this$dist$get$1")) {
                            bool = false;
                        }
                    }

//                        if (nameEntity instanceof CompilationUnitDeclaration) {
//                            similarity = SimilarityMetric.packageSimilarity(currentElement, nameEntity);
//                        } 
                    double similarity = 1;
                    if (nameEntity instanceof MethodDeclaration) {
                        similarity = SimilarityMetric.nameSimilarity(currentElement, nameEntity);
                    }

                    result = similarity > 0.6
                            && !s.get(0).compNames(currentElement, nameEntity)
                            && currentElement.getClass().equals(nameEntity.getClass()) && bool;

                }

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

                CodeEntity currentCodeEntity = s.get(0).get(index);

                if (nameEntity instanceof CompilationUnitDeclaration && currentCodeEntity instanceof CompilationUnitDeclaration) {
                    CompilationUnitDeclaration cud = (CompilationUnitDeclaration) nameEntity;
                    IModelCommands.getInstance().setCUDeclPackageName(cud.getPackageName(), currentCodeEntity);
                    s.get(0).updateCodeEntityList(currentCodeEntity);
                } else if (nameEntity instanceof ClassDeclaration && currentCodeEntity instanceof ClassDeclaration) { // Refactoring Utils 
                    ClassDeclaration cls = (ClassDeclaration) nameEntity;
                    IModelCommands.getInstance().setClassType(cls.getClassType(), currentCodeEntity);
                    IModelCommands.getInstance().setScopeName(cls.getName(), currentCodeEntity);
                    s.get(0).updateCodeEntityList(currentCodeEntity);
                } else if (nameEntity instanceof MethodDeclaration && currentCodeEntity instanceof MethodDeclaration) {
                    MethodDeclaration methName = (MethodDeclaration) nameEntity;
                    IModelCommands.getInstance().setMethodName(methName.getName(), currentCodeEntity);
                    s.get(0).updateCodeEntityList(currentCodeEntity);
                } else if (nameEntity instanceof Variable && currentCodeEntity instanceof Variable) {
                    Variable variable = (Variable) nameEntity;
                    IModelCommands.getInstance().setVariableName(variable.getName(), currentCodeEntity);
                    s.get(0).updateCodeEntityList(currentCodeEntity);
                }
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
