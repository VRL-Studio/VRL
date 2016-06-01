/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff;

import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
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

    @Test
    public void testInsertClassOnFirstPos() throws Exception { // (targetCUD, cls, ....)
        CompilationUnitDeclaration sourceModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff1;\n"
                + "class Class1 {\n"
                + "void variable(){}\n"
                + "}\n"
                + "class Class2 {\n"
                + "void method(){}\n"
                + "}"
        );

        CodeEntityList source = new CodeEntityList(sourceModel);
        CodeEntityList target = new CodeEntityList(source, true);

        IModelCommands commands = IModelCommands.getInstance();

        CompilationUnitDeclaration targetCUD = (CompilationUnitDeclaration) target.get(0);
        CompilationUnitDeclaration sourceCUD = (CompilationUnitDeclaration) source.get(0);
        ClassDeclaration cls = sourceCUD.getDeclaredClasses().get(0);

        System.out.println("SOURCE MODEL: ");
        System.out.println(Scope2Code.getCode(sourceCUD));
        commands.setScopeName("Class4", cls);// Class1 -> Class4
        commands.insertScope(targetCUD, 0, cls);
        System.out.println("TARGET MODEL: ");
        System.out.println(Scope2Code.getCode(targetCUD));

        System.out.println("source==target: " + source.equals(target));
        System.out.println("");
        target.updateCodeEntityList(targetCUD);
        System.out.println("");

        Assert.assertFalse("States with different names must not be equal:", source.equals(target));

    }

    //@Test
    public void testInsertClass() throws Exception { // (targetCUD, ... , cls1, cls2, ...)
        CompilationUnitDeclaration sourceModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff1;\n"
                + "class Class1 {\n"
                + "void variable(){}\n"
                + "}\n"
                + "class Class2 {\n"
                + "void method(){}\n"
                + "}"
        );

        CodeEntityList source = new CodeEntityList(sourceModel);
        CodeEntityList target = new CodeEntityList(source, true);

        IModelCommands commands = IModelCommands.getInstance();
        
        CompilationUnitDeclaration sourceCUD = (CompilationUnitDeclaration) source.get(0);
        CompilationUnitDeclaration targetCUD = (CompilationUnitDeclaration) target.get(0);

        ClassDeclaration cls1 = targetCUD.getDeclaredClasses().get(0);
        ClassDeclaration cls2 = sourceCUD.getDeclaredClasses().get(1);

        System.out.println("SOURCE MODEL: ");
        System.out.println(Scope2Code.getCode(sourceCUD));
        commands.setScopeName("Class4", cls2);// Class2 -> Class4
        commands.insertScope(targetCUD, targetCUD.getDeclaredClasses().indexOf(cls1)+1, cls2);
        System.out.println("TARGET MODEL: ");
        System.out.println(Scope2Code.getCode(targetCUD));

        System.out.println("source==target: " + source.equals(target));
        System.out.println("");
        target.updateCodeEntityList(targetCUD);
        System.out.println("");

        Assert.assertFalse("States with different names must not be equal:", source.equals(target));

    }
    //@Test
    public void testInsertClassEndOfList() throws Exception { // (targetCUD, ... , cls1, cls2)
        CompilationUnitDeclaration sourceModel = groovy2Model(""
                + "package eu.mihosoft.vrl.lang.model.diff1;\n"
                + "class Class1 {\n"
                + "void variable(){}\n"
                + "}\n"
                + "class Class2 {\n"
                + "void method(){}\n"
                + "}"
        );

        CodeEntityList source = new CodeEntityList(sourceModel);
        CodeEntityList target = new CodeEntityList(source, true);

        IModelCommands commands = IModelCommands.getInstance();
        
        CompilationUnitDeclaration sourceCUD = (CompilationUnitDeclaration) source.get(0);
        CompilationUnitDeclaration targetCUD = (CompilationUnitDeclaration) target.get(0);

        ClassDeclaration cls1 = targetCUD.getDeclaredClasses().get(0);
        ClassDeclaration cls2 = sourceCUD.getDeclaredClasses().get(1);

        System.out.println("SOURCE MODEL: ");
        System.out.println(Scope2Code.getCode(sourceCUD));
        commands.setScopeName("Class4", cls2);// Class2 -> Class4
        commands.insertScope(targetCUD, cls2);
        System.out.println("TARGET MODEL: ");
        System.out.println(Scope2Code.getCode(targetCUD));

        System.out.println("source==target: " + source.equals(target));
        System.out.println("");
        target.updateCodeEntityList(targetCUD);
        System.out.println("");

        Assert.assertFalse("States with different names must not be equal:", source.equals(target));

    }

}
