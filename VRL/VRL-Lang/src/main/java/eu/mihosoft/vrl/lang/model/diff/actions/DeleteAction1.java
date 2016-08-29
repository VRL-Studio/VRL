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
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.diff.CodeEntityList;
import eu.mihosoft.vrl.lang.model.diff.SimilarityMetric;
import java.util.ArrayList;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class DeleteAction1 extends Action<CodeEntityList> {

    public boolean verify(State<CodeEntityList> s) {
        s = s.clone();

        effect.apply(s);
        return precond.verify(s);
    }

    public DeleteAction1(CodeEntity codeEntity) {

        setName("delete");

        precond.add(new ConditionPredicate<CodeEntityList>() {

            @Override
            public boolean verify(State<CodeEntityList> s) {
                s = s.clone();

                if (s.get(0).getEntities().contains(codeEntity)) {
                    setName("delete " + '"' + SimilarityMetric.getCodeEntityName(codeEntity) + '"' + " index " + s.get(0).getEntities().indexOf(codeEntity));
                }
                return s.get(0).getEntities().contains(codeEntity) && s.get(0).size() > 0;
            }

            @Override
            public String getName() {
                return "delete";
            }
        });

        effect.add(new EffectPredicate<CodeEntityList>() {

            @Override
            public void apply(State<CodeEntityList> s) {

                ArrayList strings = new ArrayList();
                for (int i = 0; i < s.get(0).getEntities().size(); i++) {
                    strings.add(SimilarityMetric.getCodeEntityName(s.get(0).get(i)));
                }
                System.out.println("--- Bevore " + strings.toString());

                s.get(0).remove(codeEntity);
                
                strings.clear();
                for (int i = 0; i < s.get(0).getEntities().size(); i++) {
                    strings.add(SimilarityMetric.getCodeEntityName(s.get(0).get(i)));
                }
                System.out.println("--- After " + strings.toString());
                
                System.out.println("--------------------------------------------");

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
