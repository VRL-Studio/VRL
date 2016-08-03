/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff;

import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import static eu.mihosoft.vrl.lang.model.diff.CodeEntityListTestClass.groovy2Model;
import eu.mihosoft.vrl.lang.model.diff.actions.IModelCommands;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class ChangeTypeTest {

    @Test
    public void testRefactoringType() throws Exception {
        CompilationUnitDeclaration sourceModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff;\n"
                + "class Class1{\n"
                + "Class1 method(double i1){"
                + "return null;\n"
                + "}\n"
                + "}"
        );

        CompilationUnitDeclaration targetModel = groovy2Model(""
                + "package eu2.mihosoft2;\n"
                + "class Class2{\n"
                + "Class2 method(int i22){"
                + "return null;\n"
                + "}\n"
                + "}"
        );

        sourceModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff;\n"
                + "class Class1{\n"
                + "int method(int i1){"
                + "return null;\n"
                + "}\n"
                + "}"
        );

        targetModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff;\n"
                + "class Class1{\n"
                + "int method(double i2){"
                + "return null;\n"
                + "}\n"
                + "}"
        );
        createSetParameterTest(sourceModel, targetModel);

    }

    private void createRefactoringTypeTest(CompilationUnitDeclaration sourceModel, CompilationUnitDeclaration targetModel) {

        CodeEntityList source = new CodeEntityList(sourceModel);
        CodeEntityList target = new CodeEntityList(source, true);

        System.out.println(Scope2Code.getCode(sourceModel));
        System.out.println("####################################################");
        IModelCommands.getInstance().setScopeName(targetModel.getDeclaredClasses().get(0).getName(), sourceModel.getDeclaredClasses().get(0));
        source.updateCodeEntityList(sourceModel);
        IModelCommands.getInstance().setMethodReturnType(targetModel.getDeclaredClasses().get(0).getDeclaredMethods().get(0).getReturnType(), sourceModel.getDeclaredClasses().get(0).getDeclaredMethods().get(0));
        source.updateCodeEntityList(sourceModel);
        IModelCommands.getInstance().setTypeInParameter(targetModel.getDeclaredClasses().get(0).getDeclaredMethods().get(0).getReturnType(), sourceModel.getDeclaredClasses().get(0).getDeclaredMethods().get(0).getParameters().getParamenters().get(0));
        source.updateCodeEntityList(sourceModel);
        System.out.println(sourceModel);

        String newCode = Scope2Code.getCode(sourceModel);

        System.out.println(newCode);

        System.out.println("source==target: " + source.equals(target));

        Assert.assertFalse("States with different names must not be equal:", source.equals(target));
    }

    private void createSetParameterTest(CompilationUnitDeclaration sourceModel, CompilationUnitDeclaration targetModel) {
        CodeEntityList source = new CodeEntityList(sourceModel);
        CodeEntityList target = new CodeEntityList(source, true);
        CodeEntityList targetMod = new CodeEntityList(targetModel);
        

        System.out.println(Scope2Code.getCode(sourceModel));
        System.out.println("####################################################");
        IModelCommands.getInstance().setMethodParameters(targetModel.getDeclaredClasses().get(0).getDeclaredMethods().get(0).getParameters(), sourceModel.getDeclaredClasses().get(0).getDeclaredMethods().get(0));
        source.updateCodeEntityList(sourceModel);
        System.out.println(Scope2Code.getCode(sourceModel));
        System.out.println("source==target: " + source.equals(target));
        //Assert.assertFalse("States with different names must not be equal:", source.equals(target));
        CompilationUnitDeclaration targetCud = (CompilationUnitDeclaration) target.get(0);
        System.out.println("Parameters " + targetCud.getDeclaredClasses().get(0).getDeclaredMethods().get(0).getParameters().getParamenters().equals(sourceModel.getDeclaredClasses().get(0).getDeclaredMethods().get(0).getParameters().getParamenters()));
        System.out.println("sourceModel == targetModel " + source.equals(targetMod));
        System.out.println(Scope2Code.getCode(targetModel));
        System.out.println("Paramteters "+ targetCud.getDeclaredClasses().get(0).getDeclaredMethods().get(0).getParameters());
        System.out.println("Paramteters "+ sourceModel.getDeclaredClasses().get(0).getDeclaredMethods().get(0).getParameters());
    }

}
