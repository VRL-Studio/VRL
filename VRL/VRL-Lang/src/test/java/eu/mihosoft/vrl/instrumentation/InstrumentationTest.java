/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

import eu.mihosoft.vrl.lang.model.CodeRange;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import eu.mihosoft.vrl.lang.model.UIBinding;
import eu.mihosoft.vrl.lang.model.Variable;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.util.GroovyScriptEngine;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.junit.Test;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class InstrumentationTest {

	@Test
	public void testMethodCallInstrumentation() {
		CompilerConfiguration conf = new CompilerConfiguration();
		conf.addCompilationCustomizers(new ASTTransformationCustomizer(
				new VRLInstrumentationTransformation()));
		GroovyShell shell = new GroovyShell(conf);
		shell.parse(
				"@eu.mihosoft.vrl.instrumentation.VRLInstrumentation\n"
						+ "public class A {\n"
						+ "    public boolean m3(int i) {return i < 3;}\n"
						+ "    \n" + "    public void m2(int p1) {\n"
						+ "        for(int i = 0; m3(i);i++) {\n"
						+ "            A.m1(A.m1(1));\n" + "        }"
						+ "    }\n" + "    public static int m1(int p1) {\n"
						+ "        println(\"p1: \" + (p1+1));\n"
						+ "        return p1+1;\n" + "    }\n"
						+ "    public static void main(String[] args) {"
						+ "        A a = new A();" + "        a.m2(1);"
						+ "    }" + "}").run();
		assertEquals(1, conf.getCompilationCustomizers().size());
	}

	@Test
	public void testVRLVisualizationTransformationDuplicateMethods() {
		CompilerConfiguration conf = new CompilerConfiguration();
		conf.addCompilationCustomizers(new ASTTransformationCustomizer(
				new VRLVisualizationTransformation()));
		GroovyClassLoader loader = new GroovyClassLoader(this.getClass()
				.getClassLoader(), conf);

		UIBinding.scopes.clear();
		String classX = "package x.y.z;\npublic class X {\n  public int foo(){\n    return 0;\n  }\n}\n";
		loader.parseClass(classX);

		// get scopes in CompilationUnit -> class X
		// then get scopes within class X -> should be a list containing only
		// method foo()
		Collection<Scope> scopes = UIBinding.scopes.values().iterator().next()
				.get(0).getScopes().get(0).getScopes();

		// assert foo is only added once
		assertEquals(1, scopes.size());
		Iterator<Scope> iter = scopes.iterator();
		assertEquals("foo", iter.next().getName());
	}
}
