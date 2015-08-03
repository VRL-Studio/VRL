/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.transform;

import com.rits.cloning.Cloner;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder_Impl;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class KryoTest {

    public static void main(String[] args) {


        VisualCodeBuilder codeBuilder = new VisualCodeBuilder_Impl();

        CompilationUnitDeclaration cu = codeBuilder.declareCompilationUnit("MyUnit", "my.package01");

        System.out.println(cu);
        
        Cloner cloner = new Cloner();
        
        CompilationUnitDeclaration cu2 = cloner.deepClone(cu);
    }
}
