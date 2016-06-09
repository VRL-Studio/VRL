/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff;

import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import static eu.mihosoft.vrl.lang.model.diff.CodeEntityListTestClass.groovy2Model;
import eu.mihosoft.vrl.lang.model.diff.actions.IModelCommands;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class InsertElementTest {

//    @Test
    public void testInsertClassOnFirstPos() throws Exception { // (targetModel, cls, ....)
        CompilationUnitDeclaration sourceModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff;\n"
                + "class Class1 {\n"
                + "void method1(){}\n"
                + "}\n"
                + "class Class2 {\n"
                + "void method2(){}\n"
                + "}"
        );

        CodeEntityList source = new CodeEntityList(sourceModel);

        CodeEntityList target = new CodeEntityList(source, true);

        IModelCommands commands = IModelCommands.getInstance();

        CompilationUnitDeclaration targetModel = (CompilationUnitDeclaration) target.get(0);
        ClassDeclaration cls = sourceModel.getDeclaredClasses().get(0);

        System.out.println("SOURCE MODEL: ");
        System.out.println(Scope2Code.getCode(sourceModel));

        commands.setScopeName("InsertedClass", cls);
        commands.insertScope(targetModel, 0, cls);

        System.out.println("TARGET MODEL: ");
        System.out.println(Scope2Code.getCode(targetModel));

        System.out.println("source==target: " + source.equals(target));
        System.out.println("");
        target.updateCodeEntityList(targetModel);
        System.out.println("");

        Assert.assertFalse("States with different names must not be equal:", source.equals(target));

    }

//    @Test
    public void testInsertClass() throws Exception { // (targetModel, ... , cls1, cls2, ...)
        CompilationUnitDeclaration sourceModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff;\n"
                + "class Class1 {\n"
                + "void method1(){}\n"
                + "}\n"
                + "class Class2 {\n"
                + "void method2(){}\n"
                + "}"
        );

        CodeEntityList source = new CodeEntityList(sourceModel);
        CodeEntityList target = new CodeEntityList(source, true);

        IModelCommands commands = IModelCommands.getInstance();

        CompilationUnitDeclaration targetModel = (CompilationUnitDeclaration) target.get(0);

        ClassDeclaration clsTarget = targetModel.getDeclaredClasses().get(0);
        ClassDeclaration clsSource = sourceModel.getDeclaredClasses().get(0);

        System.out.println("SOURCE MODEL: ");
        System.out.println(Scope2Code.getCode(sourceModel));

        commands.setScopeName("InsertedClass", clsSource);
        commands.insertScope(targetModel, targetModel.getDeclaredClasses().indexOf(clsTarget) + 1, clsSource);

        System.out.println("TARGET MODEL: ");
        System.out.println(Scope2Code.getCode(targetModel));

        System.out.println("source==target: " + source.equals(target));
        System.out.println("");
        target.updateCodeEntityList(targetModel);
        System.out.println("");

        Assert.assertFalse("States with different names must not be equal:", source.equals(target));

    }

    //@Test
    public void testInsertClassEndOfList() throws Exception { // (targetModel, ... , cls1, cls2)
        CompilationUnitDeclaration sourceModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff;\n"
                + "class Class1 {\n"
                + "void method1(){}\n"
                + "}\n"
                + "class Class2 {\n"
                + "void method2(){}\n"
                + "}"
        );

        CodeEntityList source = new CodeEntityList(sourceModel);
        CodeEntityList target = new CodeEntityList(source, true);

        IModelCommands commands = IModelCommands.getInstance();

        CompilationUnitDeclaration targetModel = (CompilationUnitDeclaration) target.get(0);

        ClassDeclaration clsSource = sourceModel.getDeclaredClasses().get(0);

        System.out.println("SOURCE MODEL: ");
        System.out.println(Scope2Code.getCode(sourceModel));

        commands.setScopeName("InsertedClass", clsSource);
        commands.insertScope(targetModel, clsSource);

        System.out.println("TARGET MODEL: ");
        System.out.println(Scope2Code.getCode(targetModel));

        System.out.println("source==target: " + source.equals(target));
        System.out.println("");
        target.updateCodeEntityList(targetModel);
        System.out.println("");

        Assert.assertFalse("States with different names must not be equal:", source.equals(target));

    }

    @Test
    public void testInsertMethod() throws Exception { // (targetModel, cls, ....)
        CompilationUnitDeclaration sourceModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff;\n"
                + "class Class1 {\n"
                + "void method1(){}\n"
                + "void method2(){}\n"
                + "}\n"
                + "class Class2 {\n"
                + "}"
        );

        CodeEntityList source = new CodeEntityList(sourceModel);
        CodeEntityList target = new CodeEntityList(source, true);

        IModelCommands commands = IModelCommands.getInstance();

        CompilationUnitDeclaration targetModel = (CompilationUnitDeclaration) target.get(0);
        ClassDeclaration class2 = targetModel.getDeclaredClasses().get(1);
        MethodDeclaration meth1 = sourceModel.getDeclaredClasses().get(0).getDeclaredMethods().get(0);

        System.out.println("SOURCE MODEL: ");
        System.out.println(Scope2Code.getCode(sourceModel));
        commands.insertMethodToClass(class2, 0, meth1);
        System.out.println("TARGET MODEL: ");
        System.out.println(Scope2Code.getCode(targetModel));
        target.updateCodeEntityList(targetModel);
        target.updateCodeEntityList(targetModel);
        System.out.println("source==target: " + source.equals(target));
        System.out.println("");

        System.out.println("");

        Assert.assertFalse("States with different names must not be equal:", source.equals(target));

    }

}
