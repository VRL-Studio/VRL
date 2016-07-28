/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff;

import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import static eu.mihosoft.vrl.lang.model.diff.CodeEntityListTestClass.groovy2Model;
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
                + "package eu.mihosoft1.vrl1.lang1.model1.diff1;\n"
                + "class Class1 {\n"
                + "Class1 method11(Class1 param1){\n"
                + "while(2 < 3) { Class1 vInWhile = param1;}\n"
                + "if(2 < 3) { Class1 vInIf = param1;}\n"
                + "method11(param1);\n"
                + "Class1 variable1 = param1;"
                + "return param1; \n"
                + "}\n"
                + "void method12(){\n"
                + "}\n"
                + "}"
        );

        targetModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff;\n"
                + "class Class3 {\n"
                + "Class3 method11(Class3 param1){\n"
                + "while(2 < 3) { Class3 vInWhile = param1;}\n"
                + "if(2 < 3) { Class3 vInIf = param1;}\n"
                + "method11(param1);\n"
                + "Class3 variable1 = param1;"
                + "return param1; \n"
                + "}\n"
                + "void method22(){}\n"
                + "}"
        );

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
