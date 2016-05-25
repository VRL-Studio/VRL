/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.lang.model.diff;

import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import static eu.mihosoft.vrl.lang.model.diff.MainClass.groovy2Model;
import eu.mihosoft.vrl.lang.model.diff.actions.IModelCommands;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class StateEqualsTest {
    @Test
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
}
