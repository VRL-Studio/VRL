/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff;

import eu.mihosoft.vrl.base.IOUtil;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class RefactorControlFlowScopeTest {

    @Test
    public void testInserCFS() throws Exception {
        CompilationUnitDeclaration sourceModel = CodeEntityList.groovy2Model(
                IOUtil.convertStreamToString(
                        MainClass.class.getResourceAsStream("TestCase02Source.groovy"))
        );

        CompilationUnitDeclaration targetModel = CodeEntityList.groovy2Model(
                IOUtil.convertStreamToString(
                        MainClass.class.getResourceAsStream("TestCase02Target.groovy"))
        );

        createCFScopeTest(sourceModel, targetModel);
    }

    private void createCFScopeTest(CompilationUnitDeclaration sourceModel, CompilationUnitDeclaration targetModel) {
        CodeEntityList source = new CodeEntityList(sourceModel);
        CodeEntityList target = new CodeEntityList(targetModel);

        System.out.println("####################################################");

        String newCode = Scope2Code.getCode(sourceModel);
 
       MethodDeclaration meth = sourceModel.getDeclaredClasses().get(0).getDeclaredMethods().get(0);
       MethodDeclaration meth1 = targetModel.getDeclaredClasses().get(0).getDeclaredMethods().get(0);

        System.out.println("Method " + Scope2Code.getCode(meth));
        System.out.println(Scope2Code.getCode(meth.getControlFlow().getInvocations().get(0)));
        if (meth1.getControlFlow().getInvocations().get(0).getMethodName().equals(meth.getControlFlow().getInvocations().get(0).getMethodName())) {
            
        }
        System.out.println("source==target: " + source.equals(target));

        Assert.assertFalse("States with different names must not be equal:", source.equals(target));
    }

}
