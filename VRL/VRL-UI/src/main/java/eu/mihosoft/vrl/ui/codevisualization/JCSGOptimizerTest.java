/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.ui.codevisualization;

import eu.mihosoft.vrl.instrumentation.VRLVisualizationTransformation;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import eu.mihosoft.vrl.lang.model.UIBinding;
import eu.mihosoft.vrl.lang.model.transform.BooleanJCSGOptimizer;
import eu.mihosoft.vrl.lang.model.transform.InstrumentCode;
import groovy.lang.GroovyClassLoader;
import java.util.Objects;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;

/**
 *
 * @author miho
 */
public class JCSGOptimizerTest {

    public static void main(String[] args) {
        // clear model
        UIBinding.scopes.clear();

        // configure groovy compiler with model importer (groovy ast -> model)
        CompilerConfiguration ccfg = new CompilerConfiguration();
        ccfg.addCompilationCustomizers(new ASTTransformationCustomizer(
                new VRLVisualizationTransformation()));
        GroovyClassLoader gcl = new GroovyClassLoader(
                new GroovyClassLoader(), ccfg);

        // code to compile
        String code = ""
                + "package mypackage\n"
                + "import eu.mihosoft.vrl.v3d.jcsg.*;\n"
                + "\n"
                + "public class Main {\n"
                + "    public static final void main(String[] args) {\n"
                + "         CSG csg1 = new Cube().toCSG();\n"
                + "         CSG csg2 = new Sphere().toCSG();\n"
                + "         // case 1 (no side effects)\n"
                + "         csg1.union(csg2)\n"
                + "         // case 2 (self union)\n"
                + "         CSG csg3 = csg1.union(csg1);\n"
                + "         // case 3 (self intersect)\n"
                + "         CSG csg4 = csg1.intersect(csg1);\n"
                + "         // chained case 1\n"
                + "         CSG csg5 = csg1.union(csg1.intersect(csg1))\n"
                + "         // chained case 2\n"
                + "         csg1.union(csg1.intersect(csg2)).intersect(csg3)\n"
                + "         // chained case 3\n"
                + "         CSG csg6 = csg3.difference(csg1.union(csg2))\n"
                + "         // third rule as argument\n"
                + "         CSG csg7 = csg1.intersect(csg1.union(csg2))\n"
                + "         // third rule as inv-obj provider\n"
                + "         CSG csg8 = csg1.intersect(csg1.union(csg2)).difference(csg3)\n"
                + "    }\n"
                + "}";

        System.out.println("old code:\n\n" + code);

        // compile the code and execute model importer
        try {
            gcl.parseClass(code, "Script");
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        // obtain compilation unit (e.g., .groovy file)
        CompilationUnitDeclaration cud
                = (CompilationUnitDeclaration) UIBinding.scopes.values().
                iterator().next().get(0);

        String newCode = null;
        String prevNewCode = null;
        int counter = 0;
        while (!Objects.equals(newCode, prevNewCode) || newCode == null) {

            System.out.println("-- PASS " + ++counter + " --");
            prevNewCode = newCode;

            // apply transformation
            cud = new BooleanJCSGOptimizer().transform(cud);

            // model -> code
            newCode = Scope2Code.getCode(cud);
            
             System.out.println("\nnew code:\n\n" + newCode);
        }

//        System.out.println("\nnew code:\n\n" + newCode);
    }
}
