/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff1;

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
import eu.mihosoft.vrl.lang.model.Variable;
import eu.mihosoft.vrl.lang.model.diff1.actions.DecreaseIndexAction;
import eu.mihosoft.vrl.lang.model.diff1.actions.DeleteAction;
import eu.mihosoft.vrl.lang.model.diff1.actions.IncreaseIndexAction;
import eu.mihosoft.vrl.lang.model.diff1.actions.InsertAction;
import eu.mihosoft.vrl.lang.model.diff1.actions.RenameAction;
import java.util.ArrayList;
import java.util.LinkedHashSet;
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
                + "public class Class1 {\n"
                + "void method1(){}\n"
                //                + "void method2(){}\n"
                //                + "}\n"
                //                + "public class NewClass {\n"
                //                + "public void method1(){}\n"
                + "}");
        CompilationUnitDeclaration targetModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff;\n"
                + "public class Class1 {\n"
                + "void method2(){}\n"
                + "public void newMeth(){}\n"
                + "}\n"
        //                + "public class Keine {}"
        );

       // classAStar(sourceModel, targetModel);
        classAStar(targetModel,sourceModel);
        //aStar(new ArrayList<>(sourceModel.getDeclaredClasses().get(0).getDeclaredMethods()), new ArrayList<>(targetModel.getDeclaredClasses().get(0).getDeclaredMethods()));
        //convertTreeToList(sourceModel);
        //++++++++++++++++++++++++++++++++++ Remove Subtree ++++++++++++++++++++++++++++++++++++++++
//        List<CodeEntityWithIndex> sourceList = convertTreeToList(sourceModel, 0);
//        CEList source = new CEList(sourceList);
//        sourceList.removeAll(source.getSubtree(source.get(2)));
//        for (CodeEntityWithIndex codeEntityWithIndex : sourceList) {
//            System.out.println("Source List after remove " + codeEntityWithIndex.getName());
//        }
        //++++++++++++++++++++++++++++++++++ Add Subtree ++++++++++++++++++++++++++++++++++++++++
//
//        List<CodeEntityWithIndex> sourceList1 = convertTreeToList(sourceModel, 0);
//        CEList source1 = new CEList(sourceList1);
//        sourceList1.addAll(2, source1.getSubtree(source1.get(2)));
//        for (CodeEntityWithIndex codeEntityWithIndex : sourceList1) {
//            System.out.println("Source List after remove " + codeEntityWithIndex.getName());
//        }
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

        CEList source = new CEList(sourceModel, 0);
        CEList target = new CEList(targetModel, source.size());
        ArrayList<CodeEntity> listOfEntities = new ArrayList<>((new LinkedHashSet<CodeEntity>(target.getListWithCodeEntities())));

//        ArrayList<String> targetNames = target.getCodeEntitieNames();
//        ArrayList<String> names = new ArrayList<>((new LinkedHashSet<String>(targetNames)));     
        System.out.println("");
        System.out.println("####################################################");
        System.out.println("Source List: ");
        for (int i = 0; i < source.size(); i++) {
            System.out.println(source.get(i).getTreeIndex() + ": " + source.get(i).getName());
        }
        System.out.println("####################################################");
        System.out.println("Target List: ");
        for (int i = 0; i < target.size(); i++) {
            System.out.println(target.get(i).getTreeIndex() + ": " + target.get(i).getName());
        }

        IncreaseIndexAction increaseIndex = new IncreaseIndexAction();
        DecreaseIndexAction decreaseIndex = new DecreaseIndexAction();
        DeleteAction delete = new DeleteAction();
        ArrayList<Action<CEList>> allActions = new ArrayList<>();

        listOfEntities.stream().forEach((entity) -> {
            allActions.add(new RenameAction(entity));
        });
        target.getEntities().stream().forEach((e) -> {
            allActions.add(new InsertAction(e, target));
        });
        allActions.add(increaseIndex);
        allActions.add(decreaseIndex);
        allActions.add(delete);

        CEListState sourceState = new CEListState(source);
        WorldDescription<CEList> StringListWD
                = new WorldDescription<>(sourceState, new CEListGoal(target),
                        allActions, new Heuristic());

        AStar<CEList> solverOND = new AStar<>(StringListWD);
        solverOND.run();

        return null;
    }
//
//    /**
//     *
//     * @param sourceList
//     * @param targetList
//     */
//    public static void aStar(List<CodeEntity> sourceList, List<CodeEntity> targetList) {
//
//        ArrayList<CodeEntity> listOfEntities = new ArrayList<CodeEntity>(new LinkedHashSet<CodeEntity>(targetList));
//        System.out.println("");
//        System.out.println("####################################################");
//        System.out.println("Source List: ");
//        for (int i = 0; i < sourceList.size(); i++) {
//            System.out.println(i + ": " + SimilarityMetric.getCodeEntityName(sourceList.get(i)));
//        }
//        System.out.println("####################################################");
//
//        System.out.println("Target List: ");
//        for (int i = 0; i < targetList.size(); i++) {
//            System.out.println(i + ": " + SimilarityMetric.getCodeEntityName(targetList.get(i)));
//        }
//
//        IncreaseIndexAction increaseIndex = new IncreaseIndexAction();
//        DecreaseIndexAction decreaseIndex = new DecreaseIndexAction();
//        DeleteAction delete = new DeleteAction();
//        ArrayList<Action<CEList>> allActions = new ArrayList<>();
//        allActions.add(increaseIndex);
//        listOfEntities.stream().forEach((entity) -> {
//            allActions.add(new RenameAction(entity));
//            allActions.add(new InsertAction(entity));
//        });
//
//        allActions.add(delete);
//        allActions.add(decreaseIndex);
//
//        WorldDescription<CodeEntityList> StringListWD
//                = new WorldDescription<>(new CodeEntityListState(new CodeEntityList(sourceList)), new CodeEntityListGoal(new CodeEntityList(targetList)),
//                        allActions, new Heuristic());
//
//        AStar<CodeEntityList> solverOND = new AStar<>(StringListWD);
//        solverOND.run();
//
//        System.out.println("done.");
//
//    }

    public static SourceUnit fromCode(String code) throws Exception {
        SourceUnit sourceUnit = SourceUnit.create("Test.groovy", code);
        CompilationUnit compUnit = new CompilationUnit();
        compUnit.addSource(sourceUnit);
        compUnit.compile(Phases.CANONICALIZATION);
        return sourceUnit;
    }

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static ArrayList<CodeEntityWithIndex> convertTreeToList(Scope codeEntity, int index) { // TODO Set Index optimieren
        ArrayList<CodeEntityWithIndex> codeEntities = new ArrayList();
        codeEntity.visitScopeAndAllSubElements((CodeEntity e) -> {
            if (e instanceof Scope || e instanceof Variable) {
                codeEntities.add(new CodeEntityWithIndex(0, e));
//                if (e instanceof MethodDeclaration) { // diese Methoden sollen nicht mehr in Modell vorkommen
//                    MethodDeclaration md = (MethodDeclaration) e;
//                    if (!md.getName().equals("this$dist$invoke$1") && !md.getName().equals("this$dist$set$1") && !md.getName().equals("this$dist$get$1")) {
//                        codeEntities.add(new CodeEntityWithIndex(0, e));
//                    }
//                } else {
//                    codeEntities.add(new CodeEntityWithIndex(0, e));
//                }
            }
        });
        for (int i = index; i < codeEntities.size() + index; i++) {
            codeEntities.get(i - index).setTreeIndex(i);
            System.out.println("Entity -> " + codeEntities.get(i - index).getTreeIndex() + " Name: " + SimilarityMetric.getCodeEntityName(codeEntities.get(i - index).getCodeEntity()));
        }
        return codeEntities;
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
}
