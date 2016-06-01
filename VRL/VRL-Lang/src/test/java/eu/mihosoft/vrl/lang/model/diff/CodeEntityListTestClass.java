/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff;

import eu.mihosoft.vrl.instrumentation.CompositeTransformingVisitorSupport;
import static eu.mihosoft.vrl.instrumentation.CompositeTransformingVisitorSupportTest.fromCode;
import eu.mihosoft.vrl.instrumentation.VRLVisualizationTransformation;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import org.codehaus.groovy.control.SourceUnit;
import org.junit.Test;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class CodeEntityListTestClass {

    @Test
    public void testCreateClass1() throws Exception {

        CompilationUnitDeclaration sourceModel = groovy2Model(""
                + "class A {\n"
                + "  void meth(){}\n"
                + "  void c1(){}\n"
               // + "  void c2(){}\n"
                + "}");
        CompilationUnitDeclaration targetModel = groovy2Model(""
                + "class A {\n"
                + "  void meth(){}\n"
                + "  void c2(){}\n"
                + "  void c3(){}\n"
                + "}"
        );

//        CommandList cmds = classAStar(sourceModel, targetModel);

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

//    private CommandList classAStar(CompilationUnitDeclaration sourceModel, CompilationUnitDeclaration targetModel) {
//        
//        List<CodeEntity> sourceList = new ArrayList<>(sourceModel.getScopes().get(0).getScopes()); //methoden
//        List<CodeEntity> targetList = new ArrayList<>(targetModel.getScopes().get(0).getScopes());
//        
//        System.out.println("source List" + sourceList.toString());
//        System.out.println("target List" + targetList.toString());
//        InsertAction insert = new InsertAction(targetList);
//        DecreaseIndexAction increaseIndex = new DecreaseIndexAction( "i");
//        DecreaseIndexAction decreaseIndex = new DecreaseIndexAction("d");
//        DeleteAction delete = new DeleteAction();
//        RenameAction rename = new RenameAction(targetList);
//
//        ArrayList<Action<CodeEntityList>> allActions = new ArrayList<>();
//
//        allActions.add(delete);
//        allActions.add(insert);
//        //allActions.add(rename);
//        allActions.add(increaseIndex);
//        allActions.add(decreaseIndex);
//
//        WorldDescription<CodeEntityList> StringListWD
//                = new WorldDescription<>(new CodeEntityListState(new CodeEntityList(sourceList)), new CodeEntityListGoal(new CodeEntityList(targetList)),
//                        allActions, new Heuristic());
//
//        AStar<CodeEntityList> solverOND = new AStar<>(StringListWD);
//        
//        solverOND.run();
//        System.out.println("done.");
//        return null;
//    }

}
