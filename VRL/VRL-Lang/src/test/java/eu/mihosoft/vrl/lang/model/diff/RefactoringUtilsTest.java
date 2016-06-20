/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff;

import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import static eu.mihosoft.vrl.lang.model.diff.CodeEntityListTestClass.groovy2Model;
import eu.mihosoft.vrl.lang.model.diff.actions.RefactoringUtils;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class RefactoringUtilsTest {

    @Test
    public void testRefactoringClass() throws Exception {
        CompilationUnitDeclaration sourceModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff;\n"
                + "class Class1 {\n"
                + "Class1(){}\n"
                + "String method11(){\n"
                + "return null; \n"
                + "}\n"
                + "void method12(){}\n"
                + "}"
        );

        CompilationUnitDeclaration targetModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff;\n"
                + "class Class2 {\n"
                + "void method21(){}\n"
                + "void method22(){}\n"
                + "}"
        );

        CodeEntityList source = new CodeEntityList(sourceModel);

        CodeEntityList target = new CodeEntityList(source, true);

        RefactoringUtils.renameClassRefactoring(sourceModel.getDeclaredClasses().get(0).getClassType(), targetModel.getDeclaredClasses().get(0).getClassType(), sourceModel);

        System.out.println("Class Type " + sourceModel.getDeclaredClasses().get(0).getClassType().getFullClassName());

        source.updateCodeEntityList(sourceModel);

        System.out.println("source==target: " + source.equals(target));

        Assert.assertFalse("States with different names must not be equal:", source.equals(target));

    }

}
