package eu.mihosoft.vrl.lang.model.diff1.actions;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import eu.mihosoft.ai.astar.Action;
import eu.mihosoft.ai.astar.ConditionPredicate;
import eu.mihosoft.ai.astar.EffectPredicate;
import eu.mihosoft.ai.astar.State;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.Variable;
import eu.mihosoft.vrl.lang.model.diff1.CEList;
import eu.mihosoft.vrl.lang.model.diff1.CodeEntityWithIndex;
import eu.mihosoft.vrl.lang.model.diff1.SimilarityMetric;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class RenameAction extends Action<CEList> {

    public boolean verify(State<CEList> s) {
        s = s.clone();

        effect.apply(s);
        return precond.verify(s);
    }

    public RenameAction(CodeEntity entity) {

        precond.add(new ConditionPredicate<CEList>() {

            @Override
            public boolean verify(State<CEList> s) {
                s = s.clone();
                int index = s.get(0).getIndex();
                
                if (index < s.get(0).size() && index > -1){ 
                    CodeEntityWithIndex currentElement = s.get(0).get(index);
                    setName("Rename to " + "(" + SimilarityMetric.getCodeEntityName(entity) + ")");
                    
                    double similarity;
                    if (entity instanceof CompilationUnitDeclaration) {
                        similarity = SimilarityMetric.packageSimilarity(currentElement.getCodeEntity(), entity);
                    } else {
                        similarity = SimilarityMetric.nameSimilarity(currentElement.getCodeEntity(), entity);
                    }

                    return similarity > 0.5
                            && !s.get(0).compNames(currentElement.getCodeEntity(), entity);
                } else {
                    return false;
                }

            }

            @Override
            public String getName() {
                return "rename";
            }

        });

        effect.add(new EffectPredicate<CEList>() {
            @Override
            public void apply(State<CEList> s) {

                int index = s.get(0).getIndex();
                CodeEntityWithIndex currentElement = s.get(0).get(index);

                if (entity instanceof Scope && currentElement.getCodeEntity() instanceof Scope) {
                    Scope enityScope = (Scope) entity;
                    //IModelCommands.getInstance().setScopeName(enityScope.getName(), currentElement);
                    s.get(0).get(currentElement).setCodeEntity(entity);
                  //  s.get(0).setCodeEntityInListOnCurrPos(enityScope); //funktioniert ebanfalls
                } else if (entity instanceof Variable && currentElement.getCodeEntity() instanceof Variable) { // TODO
                    Variable variable = (Variable) entity;
                    //IModelCommands.getInstance().setVariableName(variable.getName(), codeEntity);
                    s.get(0).get(currentElement).setCodeEntity(entity);
                } else {
                }
            }

            @Override
            public String getName() {
                return "rename";
            }
        });
    }

    @Override
    public double getCosts(State<CEList> s) {
        return 1;
    }

}
