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
                
                if (index < s.get(0).size() && index > -1) {
                    CodeEntity currentElement = s.get(0).get(index);
                    
                    double similarity;
                    if (nameEntity instanceof CompilationUnitDeclaration) {
                        similarity = SimilarityMetric.packageSimilarity(currentElement, nameEntity);
                    } else {
                        similarity = SimilarityMetric.nameSimilarity(currentElement, nameEntity);
                    }
                    
                    result = similarity > 0.6
                            && !s.get(0).compareNames(currentElement, nameEntity);
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
                
                CodeEntity currentCodeEntity = s.get(0).get(index);
                
                if (nameEntity instanceof CompilationUnitDeclaration && currentCodeEntity instanceof CompilationUnitDeclaration) {
                    System.out.println("RENAME CompilationUnitDeclaration");
                    IModelCommands.getInstance().setCUDeclPackageName(nameEntity, currentCodeEntity);
                } else if (nameEntity instanceof ClassDeclaration && currentCodeEntity instanceof ClassDeclaration) {
                    System.out.println("RENAME ClassDeclaration");
                    ClassDeclaration cls = (ClassDeclaration) nameEntity;
                    IModelCommands.getInstance().setScopeName(cls.getName(), currentCodeEntity);
                } else if (nameEntity instanceof MethodDeclaration && currentCodeEntity instanceof MethodDeclaration) {
                    System.out.println("RENAME MethodDeclaration");
                    IModelCommands.getInstance().setMethodName(nameEntity, currentCodeEntity);
                } else if (nameEntity instanceof Variable && currentCodeEntity instanceof Variable) {
                    System.out.println("RENAME Variable");
                    IModelCommands.getInstance().setVariableName(nameEntity, currentCodeEntity);
                } 
                s.get(0).updateCodeEntityList(currentCodeEntity);
                
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
