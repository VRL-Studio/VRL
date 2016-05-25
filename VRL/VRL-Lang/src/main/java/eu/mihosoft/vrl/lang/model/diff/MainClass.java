/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff;

import eu.mihosoft.ai.astar.AStar;
import eu.mihosoft.ai.astar.Action;
import eu.mihosoft.ai.astar.WorldDescription;
import eu.mihosoft.vrl.instrumentation.CompositeTransformingVisitorSupport;
import eu.mihosoft.vrl.instrumentation.VRLVisualizationTransformation;
import eu.mihosoft.vrl.lang.command.CommandList;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import eu.mihosoft.vrl.lang.model.diff.actions.DecreaseIndexAction;
import eu.mihosoft.vrl.lang.model.diff.actions.DeleteAction;
import eu.mihosoft.vrl.lang.model.diff.actions.IncreaseIndexAction;
import eu.mihosoft.vrl.lang.model.diff.actions.InsertAction;
import eu.mihosoft.vrl.lang.model.diff.actions.RenameAction;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.control.SourceUnit;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class MainClass {

    public static void main(String[] args) throws Exception {
        CompilationUnitDeclaration sourceModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff;\n"
                + "class Class {\n"
                + "void variable(){}\n"
                + "}"
        );

        CompilationUnitDeclaration targetModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff;\n"
                + "class Class {\n"
                + "void variable(){}\n"
                + "void method(int i){}\n"
                //                + "  void method2(int i){}\n"
                //                + "}\n"
                //                + "class Class5 {\n"
                //                + "void method1(){}\n"
                //                + "  void method2(int i){}\n"
                + "}"
        );

       //0classAStar(sourceModel, targetModel);
        classAStar(targetModel, sourceModel);
        //aStar(new ArrayList<>(sourceModel.getDeclaredClasses().get(0).getDeclaredMethods()), new ArrayList<>(targetModel.getDeclaredClasses().get(0).getDeclaredMethods()));
        //convertTreeToList(sourceModel);
        
        System.out.println("Solution: ");
        
        
        // TODO: apply commands to source
        
        Scope2Code.getCode(sourceModel);
    }

    static CompilationUnitDeclaration groovy2Model(String groovyCode) throws Exception {
        SourceUnit src = fromCode(groovyCode);
        CompositeTransformingVisitorSupport visitor = VRLVisualizationTransformation
                .init(src);
        visitor.visitModuleNode(src.getAST());
        CompilationUnitDeclaration model = (CompilationUnitDeclaration) visitor
                .getRoot().getRootObject();

        return model;
    }

    static String model2Groovy(CompilationUnitDeclaration cuDecl) throws Exception {
        return Scope2Code.getCode(cuDecl);
    }

    static CompilationUnitDeclaration clone(CompilationUnitDeclaration input) throws Exception {
        return groovy2Model(model2Groovy(input));
    }

    /**
     *
     * @param sourceModel
     * @param targetModel
     * @return list of changes
     */
    private static CommandList classAStar(CompilationUnitDeclaration sourceModel, CompilationUnitDeclaration targetModel) {

        CodeEntityList source = new CodeEntityList(sourceModel);
        CodeEntityList target = new CodeEntityList(targetModel);
        ArrayList<CodeEntity> listOfEntities = new ArrayList<CodeEntity>(new LinkedHashSet<CodeEntity>(target.getEntities()));

        System.out.println("");
        System.out.println("####################################################");
        System.out.println("Source List: ");
        for (int i = 0; i < source.size(); i++) {
            System.out.println(i + ": " + SimilarityMetric.getCodeEntityName(source.get(i)));
        }
        System.out.println("####################################################");
        System.out.println("Target List: ");
        for (int i = 0; i < target.size(); i++) {
            System.out.println(i + ": " + SimilarityMetric.getCodeEntityName(target.get(i)));
        }

        IncreaseIndexAction increaseIndex = new IncreaseIndexAction();
        DecreaseIndexAction decreaseIndex = new DecreaseIndexAction();
        DeleteAction delete = new DeleteAction();
        ArrayList<Action<CodeEntityList>> allActions = new ArrayList<>();

        listOfEntities.stream().forEach((entity) -> {
//            allActions.add(new RenameAction(entity));
//            allActions.add(new InsertAction(entity));
        });
        allActions.add(increaseIndex);
        allActions.add(decreaseIndex);
        allActions.add(delete);
        
        System.out.println("Actions: ");
        
        for (Action<CodeEntityList> action : allActions) {
            System.out.println(" -> " + action);
        }

        WorldDescription<CodeEntityList> StringListWD
                = new WorldDescription<>(new CodeEntityListState(source), new CodeEntityListGoal(target),
                        allActions, new Heuristic());

        AStar<CodeEntityList> solverOND = new AStar<>(StringListWD);
        solverOND.run();

        System.out.println("done.");
        return null;
    }

    /**
     *
     * @param sourceList
     * @param targetList
     */
    public static void aStar(List<CodeEntity> sourceList, List<CodeEntity> targetList) {

        ArrayList<CodeEntity> listOfEntities = new ArrayList<CodeEntity>(new LinkedHashSet<CodeEntity>(targetList));
        System.out.println("");
        System.out.println("####################################################");
        System.out.println("Source List: ");
        for (int i = 0; i < sourceList.size(); i++) {
            System.out.println(i + ": " + SimilarityMetric.getCodeEntityName(sourceList.get(i)));
        }
        System.out.println("####################################################");

        System.out.println("Target List: ");
        for (int i = 0; i < targetList.size(); i++) {
            System.out.println(i + ": " + SimilarityMetric.getCodeEntityName(targetList.get(i)));
        }

        IncreaseIndexAction increaseIndex = new IncreaseIndexAction();
        DecreaseIndexAction decreaseIndex = new DecreaseIndexAction();
        DeleteAction delete = new DeleteAction();
        ArrayList<Action<CodeEntityList>> allActions = new ArrayList<>();
        allActions.add(increaseIndex);
        listOfEntities.stream().forEach((entity) -> {
            allActions.add(new RenameAction(entity));
            allActions.add(new InsertAction(entity));
        });

        allActions.add(delete);
        allActions.add(decreaseIndex);

        WorldDescription<CodeEntityList> StringListWD
                = new WorldDescription<>(new CodeEntityListState(new CodeEntityList(sourceList)), new CodeEntityListGoal(new CodeEntityList(targetList)),
                        allActions, new Heuristic());

        AStar<CodeEntityList> solverOND = new AStar<>(StringListWD);
        solverOND.run();

        System.out.println("done.");

    }

    public static SourceUnit fromCode(String code) throws Exception {
        SourceUnit sourceUnit = SourceUnit.create("Test.groovy", code);
        CompilationUnit compUnit = new CompilationUnit();
        compUnit.addSource(sourceUnit);
        compUnit.compile(Phases.CANONICALIZATION);
        return sourceUnit;
    }

    private static CodeEntity getRootParent(CodeEntity codeEntity) {
        CodeEntity ce = codeEntity;
        while (ce.getParent() != null) {
            ce = ce.getParent();
        }
        System.out.println("************************************************");
        System.out.println("Parent " + ce.toString());
        return ce;
    }

    public static ArrayList convertTreeToList(Scope codeEntity) {
        ArrayList<CodeEntity> codeEntities = new ArrayList();
        codeEntity.visitScopeAndAllSubElements((e) -> {
            // System.out.println("Entity -> Id: " + e.getId() + " -- Name: " + SimilarityMetric.getCodeEntityName(e) + " -- Simplename: " + e.getClass().getSimpleName() + "--");
            codeEntities.add(e);

        });
        for (int i = 0; i < codeEntities.size(); i++) {
            System.out.println("Entity -> " + i + " Name: " + SimilarityMetric.getCodeEntityName(codeEntities.get(i)));
        }
        return null;
    }

}
