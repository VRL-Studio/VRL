/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff;

import eu.mihosoft.ai.astar.AStar;
import eu.mihosoft.ai.astar.Action;
import eu.mihosoft.ai.astar.WorldDescription;
import eu.mihosoft.vrl.base.IOUtil;
import eu.mihosoft.vrl.instrumentation.VRLVisualizationTransformation;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import eu.mihosoft.vrl.lang.model.UIBinding;
import eu.mihosoft.vrl.lang.model.diff.actions.DecreaseIndexAction;
import eu.mihosoft.vrl.lang.model.diff.actions.DeleteAction;
import eu.mihosoft.vrl.lang.model.diff.actions.IncreaseIndexAction;
import eu.mihosoft.vrl.lang.model.diff.actions.InsertAction;
import eu.mihosoft.vrl.lang.model.diff.actions.RefactoringClassAction;
import eu.mihosoft.vrl.lang.model.diff.actions.RenameAction;
import eu.mihosoft.vrl.lang.model.diff.actions.SetParametersAction;
import eu.mihosoft.vrl.lang.model.diff.actions.SetReturnTypeAction;
import eu.mihosoft.vrl.lang.model.diff.actions.SetVariablesAction;
import groovy.lang.GroovyClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class MainClass {

    public static void main(String[] args) throws Exception {

        CompilationUnitDeclaration sourceModel = CodeEntityList.groovy2Model(
                IOUtil.convertStreamToString(
                        MainClass.class.getResourceAsStream("TestCase02Source.groovy"))
        );

        CompilationUnitDeclaration targetModel = CodeEntityList.groovy2Model(
                IOUtil.convertStreamToString(
                        MainClass.class.getResourceAsStream("TestCase02Target.groovy"))
        );

        System.out.println("");
        System.out.println("");
        classAStar(sourceModel, targetModel);
//        classAStar(targetModel, sourceModel);

        System.out.println("+++++++++++++++ Source Model +++++++++++++++++");
        System.out.println(Scope2Code.getCode(sourceModel));

        System.out.println("+++++++++++++++ Target Model +++++++++++++++++");
        System.out.println(Scope2Code.getCode(targetModel));

        // System.out.println("Solution: ");
        // TODO: apply commands to source
        //System.out.println(Scope2Code.getCode(targetModel));
    }

    static CompilationUnitDeclaration groovy2Model(String groovyCode) throws Exception {

        UIBinding.scopes.clear();

        CompilerConfiguration ccfg = new CompilerConfiguration();

        ccfg.addCompilationCustomizers(new ASTTransformationCustomizer(
                new VRLVisualizationTransformation()));

        GroovyClassLoader gcl = new GroovyClassLoader(
                new GroovyClassLoader(), ccfg);

        gcl.parseClass(groovyCode);

        CompilationUnitDeclaration model
                = (CompilationUnitDeclaration) UIBinding.scopes.values().iterator().next().get(0);

        return model;
    }

    static String model2Groovy(CompilationUnitDeclaration cuDecl) {
        return Scope2Code.getCode(cuDecl);
    }

    /**
     *
     * @param sourceModel
     * @param targetModel
     * @return list of changes
     */
    private static void classAStar(CompilationUnitDeclaration sourceModel, CompilationUnitDeclaration targetModel) throws Exception {

        CodeEntityList source = new CodeEntityList(sourceModel);
        CodeEntityList target = new CodeEntityList(targetModel);

        ArrayList<CodeEntity> insertList = new ArrayList<>(target.getEntities()); // doppelte Elemente 
        insertList.remove(0); // remove CUD
        ArrayList<CodeEntity> refactoringList = new ArrayList<>();
        ArrayList<CodeEntity> methodList = new ArrayList<>();

        Map<String, CodeEntity> renameMap = new HashMap<>();
        for (CodeEntity codeEntity : target.getEntities()) {
            String name = SimilarityMetric.getCodeEntityName(codeEntity);
            if (!renameMap.containsKey(name)) {
                renameMap.put(name, codeEntity);
            } else if (!renameMap.get(name).getClass().equals(codeEntity.getClass())) {
                name = name + codeEntity.getClass().getSimpleName();
                renameMap.put(name, codeEntity);
            }
        }

        System.out.println("Rename Map " + renameMap.keySet().toString());
        ArrayList<CodeEntity> renameList = new ArrayList<>(renameMap.values());

        System.out.println("");
        System.out.println("####################################################");
        System.out.println("Source List: ");
        for (int i = 0; i < source.size(); i++) {
            System.out.println(i + ": " + source.getEntityName(i));
        }
        System.out.println("####################################################");
        System.out.println("Target List: ");
        for (int i = 0; i < target.size(); i++) {
            System.out.println(i + ": " + target.getEntityName(i));
            if (target.get(i) instanceof ClassDeclaration) {
                refactoringList.add(target.get(i));
            } else if (target.get(i) instanceof MethodDeclaration) {
                methodList.add(target.get(i));
            }
        }
        System.out.println("####################################################");

        IncreaseIndexAction increaseIndex = new IncreaseIndexAction();
        DecreaseIndexAction decreaseIndex = new DecreaseIndexAction();
        DeleteAction delete = new DeleteAction();

        ArrayList<Action<CodeEntityList>> allActions = new ArrayList<>();

        //++++++++++++++++++++++++++++++++++++++++++++++++++
        methodList.stream().forEach((entity) -> {
            allActions.add(new SetReturnTypeAction(entity)); //returnType
            allActions.add(new SetParametersAction(entity)); //param
        });

        target.getEntities().stream().forEach((entity) -> {
            if (entity instanceof Scope) {
                Scope scope = (Scope) entity;
                if (!scope.getVariables().isEmpty()) {
                    allActions.add(new SetVariablesAction(entity)); // set variables
                }
            }
        });

        renameList.stream().forEach((entity) -> {
            allActions.add(new RenameAction(entity)); // rename
        });
        //++++++++++++++++++++++++++++++++++++++++++++++++++
//
        refactoringList.stream().forEach((entity) -> {
            allActions.add(new RefactoringClassAction(entity)); //refactor = retunrType + rename (Class and Type)
        });
//        //++++++++++++++++++++++++++++++++++++++++++++++++++
        insertList.stream().forEach((entity) -> {
            allActions.add(new InsertAction(entity)); //insert
        });
        allActions.add(delete); //delete

        allActions.add(increaseIndex); // ++
        allActions.add(decreaseIndex); // --

        WorldDescription<CodeEntityList> StringListWD
                = new WorldDescription<>(new CodeEntityListState(source), new CodeEntityListGoal(target),
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

}
