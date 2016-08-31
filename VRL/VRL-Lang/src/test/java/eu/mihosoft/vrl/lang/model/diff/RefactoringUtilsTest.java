/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff;

import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import static eu.mihosoft.vrl.lang.model.diff.CodeEntityList.groovy2Model;
import eu.mihosoft.vrl.lang.model.diff.actions.RefactoringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class RefactoringUtilsTest {

    @Test
    public void testRefactoringClass() throws Exception {
        CompilationUnitDeclaration sourceModel = groovy2Model(""
                + "package eu.mihosoft1.vrl1.lang1.model1.diff1;\n"
                + "class Class1 {\n"
                + "String method11(){\n"
                + "return null; \n"
                + "}\n"
                + "void method12(){\n"
                + "}\n"
                + "}"
        );

        CompilationUnitDeclaration targetModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff;\n"
                + "class Class3 {\n"
                + "void method21(){}\n"
                + "void method22(){}\n"
                + "}"
        );

//        createClassRefactoringTest(sourceModel, targetModel);
        sourceModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diffSource;\n"
                + "class SourceClass {\n"
                + "private static final SourceClass NAME;\n"
                + "SourceClass method(SourceClass param){\n"
                + "while(true) {\n"
                + "SourceClass vInWhile = param;\n"
                + "}\n"
                + "if(2 > 0) {\n"
                + "SourceClass vInIf = param;}\n"
                + "SourceClass variable = param;\n"
                + "method(param)\n"
                + "return param;\n"
                + "}\n"
                + "}"
        );

        targetModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diffTarget;\n"
                + "class TargetClass {\n"
                + "private static final TargetClass NAME;\n"
                + "TargetClass method(TargetClass param){\n"
                + "while(true) {\n"
                + "TargetClass vInWhile = param;\n"
                + "}\n"
                + "if(2 > 0) {\n"
                + "TargetClass vInIf = param;}\n"
                + "TargetClass variable = param;"
                + "method(param)\n"
                + "return param; \n"
                + "}\n"
                + "}"
        );

        sourceModel = groovy2Model(""
                + "package sourcePackage;\n"
                + "public class SourceClass {\n"
                + "SourceClass variable1;\n"
                + "int count;\n"
                + "SourceClass method (SourceClass param) {\n"
                + "count = 1;\n"
                + "SourceClass variable2 = param;\n"
                + "while (variable1.count < variable2.count) {\n"
                + "variable1.count = variable1.count + variable2.count;\n"
                + "}\n"
                + "for (int i = 0; i < 10; i++) {\n"
                + "if (variable1.count < 20) {\n"
                + "variable1.count = variable1.count + i;\n"
                + "} else if (variable1.count < 30) {\n"
                + "if (variable2.count > 1) {\n"
                + "variable1.count = variable1.count * variable2.count;\n"
                + "}\n"
                + "}\n"
                + "}\n"
                + "return variable1;\n"
                + "}\n"
                + "}");

        targetModel = groovy2Model(""
                + "package targetPackage;\n"
                + "public class TargetClass {\n"
                + "TargetClass variable1;\n"
                + "int count;\n"
                + "TargetClass method (TargetClass param) {\n"
                + "count = 1;\n"
                + "TargetClass variable2 = param;\n"
                + "while (variable1.count < variable2.count) {\n"
                + "variable1.count = variable1.count + variable2.count;\n"
                + "}\n"
                + "for (int i = 0; i < 10; i++) {\n"
                + "if (variable1.count < 20) {\n"
                + "variable1.count = variable1.count + i;\n"
                + "} else if (variable1.count < 30) {\n"
                + "if (variable2.count > 1) {\n"
                + "variable1.count = variable1.count * variable2.count;\n"
                + "}\n"
                + "}\n"
                + "}\n"
                + "return variable1;\n"
                + "}\n"
                + "}");

        createClassRefactoringTest(sourceModel, targetModel);

    }

    private void createClassRefactoringTest(CompilationUnitDeclaration sourceModel, CompilationUnitDeclaration targetModel) {
        CodeEntityList source = new CodeEntityList(sourceModel);

        CodeEntityList target = new CodeEntityList(source, true);
        System.out.println(sourceModel);
        System.out.println("####################################################");
        RefactoringUtils.renameClassRefactoring(sourceModel.getDeclaredClasses().get(0).getClassType(), targetModel.getDeclaredClasses().get(0).getClassType(), sourceModel);

        // source.updateCodeEntityList(sourceModel);
        System.out.println(sourceModel);

        String newCode = Scope2Code.getCode(sourceModel);

       
        System.out.println(newCode);

        System.out.println("source==target: " + source.equals(target));

        Assert.assertFalse("States with different names must not be equal:", source.equals(target));
    }

}
