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
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.Scope;
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

                if (index > -1 && index < s.get(0).size()) {
                    CodeEntity currentElement = s.get(0).get(index);
                    if (currentElement.getClass().equals(nameEntity.getClass())) {
                        if (s.get(0).compNames(currentElement, nameEntity)) {
                            return false;
                        } else {
                            if (nameEntity instanceof ClassDeclaration) {
                                ClassDeclaration cls = (ClassDeclaration) nameEntity;
                                if (s.get(0).getClassNames().contains(VLangUtils.shortNameFromFullClassName(cls.getName()))) {
                                    return false;
                                } else if (nameEntity instanceof MethodDeclaration) {
                                    MethodDeclaration renameMethod = (MethodDeclaration) nameEntity;
                                    MethodDeclaration currMethod = (MethodDeclaration) currentElement;
                                    for (MethodDeclaration meth : currMethod.getClassDeclaration().getDeclaredMethods()) {
                                        if (meth.getReturnType().equals(renameMethod.getReturnType()) && meth.getParameters().getParamenters().size() == renameMethod.getParameters().getParamenters().size()) {
                                            for (int i = 0; i < meth.getParameters().getParamenters().size(); i++) {
                                                if (!meth.getParameters().getParamenters().get(i).getType().equals(renameMethod.getParameters().getParamenters().get(i).getType())) {
                                                    return true;
                                                }
                                            }
                                            return false;
                                        } else {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
                return true;
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
                    IModelCommands.getInstance().setCUDeclPackageName(cud.getPackageName(), (CompilationUnitDeclaration) currentCodeEntity);
                    s.get(0).updateCodeEntityList(currentCodeEntity);
                } else if (nameEntity instanceof ClassDeclaration && currentCodeEntity instanceof ClassDeclaration) { // Refactoring Utils 
                    ClassDeclaration cls = (ClassDeclaration) nameEntity;
                    IModelCommands.getInstance().setClassType(cls.getClassType(), (ClassDeclaration) currentCodeEntity);
                    IModelCommands.getInstance().setScopeName(cls.getName(), (Scope) currentCodeEntity);
                    s.get(0).updateCodeEntityList(currentCodeEntity);
                } else if (nameEntity instanceof MethodDeclaration && currentCodeEntity instanceof MethodDeclaration) {
                    MethodDeclaration methName = (MethodDeclaration) nameEntity;
                    MethodDeclaration method = (MethodDeclaration) currentCodeEntity;
                    IModelCommands.getInstance().setMethodName(methName.getName(), method);
                    s.get(0).updateCodeEntityList(currentCodeEntity);
                } else if (nameEntity instanceof Variable && currentCodeEntity instanceof Variable) {
                    Variable variable = (Variable) nameEntity;
                    IModelCommands.getInstance().setVariableName(variable.getName(), (Variable) currentCodeEntity);
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
