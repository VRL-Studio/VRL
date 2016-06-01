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
public class StateEqualsTest {

    //@Test
    public void testEquals() throws Exception {
        CompilationUnitDeclaration sourceModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff1;\n"
                + "class Class1 {\n"
                + "void variable(){}\n"
                + "}"
        );

        CodeEntityList source = new CodeEntityList(sourceModel);

        CodeEntityList target = new CodeEntityList(source, true);

        IModelCommands commands = IModelCommands.getInstance();

        CompilationUnitDeclaration targetModel = (CompilationUnitDeclaration) target.get(0);
        ClassDeclaration cls = targetModel.getDeclaredClasses().get(0);

        commands.setScopeName("Class2", cls);

        System.out.println(Scope2Code.getCode(sourceModel));
        System.out.println(Scope2Code.getCode(targetModel));

        System.out.println("source==target: " + source.equals(target));

        Assert.assertFalse("States with different names must not be equal:", source.equals(target));

    }

    //@Test
    public void testDeleteMethod() throws Exception {
        CompilationUnitDeclaration sourceModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff1;\n"
                + "class Class1 {\n"
                + "void variable(){}\n"
                + "}"
        );

        CodeEntityList source = new CodeEntityList(sourceModel);
        CodeEntityList target = new CodeEntityList(source, true);

        IModelCommands commands = IModelCommands.getInstance();

        CompilationUnitDeclaration targetModel = (CompilationUnitDeclaration) target.get(0);
        ClassDeclaration cls = targetModel.getDeclaredClasses().get(0);
        MethodDeclaration meth = targetModel.getDeclaredClasses().get(0).getDeclaredMethods().get(1);

        commands.removeMethodFromClass(cls, meth);

        System.out.println("SOURCE MODEL: ");
        System.out.println(Scope2Code.getCode(sourceModel));
        System.out.println("TARGET MODEL: ");
        System.out.println(Scope2Code.getCode(targetModel));

        System.out.println("source==target: " + source.equals(target));
        System.out.println("");
        System.out.println("UPDATE LIST");
        target.updateCodeEntityList(targetModel);
        System.out.println("");
        System.out.println("UPDATE TARGET: ");
        System.out.println(Scope2Code.getCode((CompilationUnitDeclaration) target.get(0)));

        Assert.assertFalse("States with different names must not be equal:", source.equals(target));

    }

    @Test
    public void testDeleteClass() throws Exception {
        CompilationUnitDeclaration sourceModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff1;\n"
                + "class Class1 {\n"
                + "void variable(){}\n"
                + "void variable1(){}\n"
                + "}\n"
                + "class Class2 {\n"
                + "void variable(){}\n"
                + "}"
        );

        CodeEntityList source = new CodeEntityList(sourceModel);
        CodeEntityList target = new CodeEntityList(source, true);

        IModelCommands commands = IModelCommands.getInstance();

        CompilationUnitDeclaration targetModel = (CompilationUnitDeclaration) target.get(0);
        ClassDeclaration cls1 = targetModel.getDeclaredClasses().get(1);
        ClassDeclaration cls2 = targetModel.getDeclaredClasses().get(0);
        MethodDeclaration meth1 = cls2.getDeclaredMethods().get(0);
        MethodDeclaration meth2 = cls2.getDeclaredMethods().get(1);

        commands.removeScope(targetModel, cls1);
        target.updateCodeEntityList(targetModel);
        commands.removeMethodFromClass(cls2, meth1);
        target.updateCodeEntityList(targetModel);
        commands.removeMethodFromClass(cls2, meth2);
        System.out.println("SOURCE MODEL: ");
        System.out.println(Scope2Code.getCode(sourceModel));
        System.out.println("TARGET MODEL: ");
        System.out.println(Scope2Code.getCode(targetModel));

        System.out.println("source==target: " + source.equals(target));
        System.out.println("");
        target.updateCodeEntityList(targetModel);

        System.out.println("");
        System.out.println("UPDATE TARGET: ");
        System.out.println(Scope2Code.getCode((CompilationUnitDeclaration) target.get(0)));

        Assert.assertFalse("States with different names must not be equal:", source.equals(target));

    }

    //@Test
    public void testRenameDeleteElem() throws Exception {
        CompilationUnitDeclaration sourceModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff1;\n"
                + "class Class1 {\n"
                + "void variable(){}\n"
                + "}\n"
                + "class Class2 {\n"
                + "void variable(){}\n"
                + "}"
        );
        CompilationUnitDeclaration model = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff;\n"
                + "class Class1 {\n"
                + "void variable(){}\n"
                + "}\n"
                + "class Class2 {\n"
                + "void variable(){}\n"
                + "}"
        );

        CodeEntityList source = new CodeEntityList(sourceModel);
        CodeEntityList target = new CodeEntityList(source, true);

        IModelCommands commands = IModelCommands.getInstance();

        CompilationUnitDeclaration targetModel = (CompilationUnitDeclaration) target.get(0);
        ClassDeclaration cls1 = targetModel.getDeclaredClasses().get(1);
        ClassDeclaration cls2 = targetModel.getDeclaredClasses().get(0);
        MethodDeclaration meth2 = cls2.getDeclaredMethods().get(0);

        commands.setCUDeclPackageName(model, targetModel);

        System.out.println("SOURCE MODEL: ");
        System.out.println(Scope2Code.getCode(sourceModel));
        System.out.println("TARGET MODEL: ");
        System.out.println(Scope2Code.getCode(targetModel));

        System.out.println("source==target: " + source.equals(target));
        System.out.println("");
        target.updateCodeEntityList(CodeEntityList.getRoot(cls1));
        commands.removeScope(targetModel, cls2);
        target.updateCodeEntityList(CodeEntityList.getRoot(cls1));

        System.out.println("TARGET MODEL: ");
        System.out.println(Scope2Code.getCode(targetModel));
        Assert.assertFalse("States with different names must not be equal:", source.equals(target));

    }

}
