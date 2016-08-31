/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff;

import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import eu.mihosoft.vrl.lang.model.Variable;
import static eu.mihosoft.vrl.lang.model.diff.CodeEntityList.groovy2Model;
import eu.mihosoft.vrl.lang.model.diff.actions.IModelCommands;
import java.util.ArrayList;
import java.util.List;
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
        //createSetParameterTest(sourceModel, targetModel);

        sourceModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff;\n"
                + "class Class1{\n"
                + "int method(double i2){"
                + "int j1 = i2;\n"
                + "return null;\n"
                + "}\n"
                + "}"
        );

        targetModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff2;\n"
                + "class Class2{\n"
                + "int method(double i2){"
                + "double j1 = i2;\n"
                + "return null;\n"
                + "}\n"
                + "}"
        );

        createSetVariableTypeTest(sourceModel, targetModel);
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
        Assert.assertFalse("States with different names must not be equal:", source.equals(target));
        CompilationUnitDeclaration targetCud = (CompilationUnitDeclaration) target.get(0);
        System.out.println("Parameters " + targetCud.getDeclaredClasses().get(0).getDeclaredMethods().get(0).getParameters().getParamenters().equals(sourceModel.getDeclaredClasses().get(0).getDeclaredMethods().get(0).getParameters().getParamenters()));
        System.out.println("sourceModel == targetModel " + source.equals(targetMod));
        System.out.println("Target Model " + Scope2Code.getCode(targetModel));

    }

    private void createSetVariableTypeTest(CompilationUnitDeclaration sourceModel, CompilationUnitDeclaration targetModel) {
        CodeEntityList source = new CodeEntityList(sourceModel);
        CodeEntityList target = new CodeEntityList(source, true);
        CodeEntityList targetMod = new CodeEntityList(targetModel);

        System.out.println("Target Model: ");
        System.out.println(Scope2Code.getCode(targetModel));
        System.out.println("####################################################");
        System.out.println("Source Model Bevore: ");
        System.out.println(Scope2Code.getCode(sourceModel));
        IModelCommands.getInstance().setVariables(sourceModel.getDeclaredClasses().get(0).getDeclaredMethods().get(0), targetModel.getDeclaredClasses().get(0).getDeclaredMethods().get(0));
        source.updateCodeEntityList(targetModel);
        Scope s1 = sourceModel.getDeclaredClasses().get(0).getDeclaredMethods().get(0);
        Scope s2 = targetModel.getDeclaredClasses().get(0).getDeclaredMethods().get(0);

        List<Variable> list = new ArrayList<>(s1.getVariables());
        List<Variable> list1 = new ArrayList<>(s2.getVariables());
        for (int i = 0; i < list1.size(); i++) {
            if (list.get(i).getType().equals(list1.get(i).getType())) {
                System.out.println("equal list");
            }

        }
        System.out.println("####################################################");
        System.out.println("Source Model After: ");
        System.out.println(Scope2Code.getCode(sourceModel));
        System.out.println(sourceModel);
//        System.out.println("source==target: " + source.equals(target));
//        //Assert.assertFalse("States with different names must not be equal:", source.equals(target));
    }

}
