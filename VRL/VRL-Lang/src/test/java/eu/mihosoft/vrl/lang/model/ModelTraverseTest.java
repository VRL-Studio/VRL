package eu.mihosoft.vrl.lang.model;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.junit.Test;

import eu.mihosoft.transverse.BuilderHook;
import eu.mihosoft.transverse.Buildlet;
import eu.mihosoft.transverse.Validatelet;
import eu.mihosoft.transverse.ValidationHook;
import eu.mihosoft.vrl.instrumentation.VRLVisualizationTransformation;
import groovy.lang.GroovyClassLoader;

public class ModelTraverseTest {

	@Test
	public void testIfStatementTraverse() {

		CompilationUnitDeclaration unit = createLangModelCompileAndCompareTest("class A { public void run(){ if(1>3) {run_if();} else {run_else();}}}");

		Buildlet<IfDeclaration, String> buildlet = (in) -> {
			return Scope2Code.getCode(in.getInvocation().get());
		};
		BuilderHook hook = new BuilderHook(buildlet);
		ModelTraverse traverse = new ModelTraverse(hook);
		traverse.traverse(unit);
		String res = hook.getResults().entrySet().iterator().next().getValue()
				.toString();
		assertEquals("if (1 > 3) {\n    run_if();\n}\n", res);
	}

	@Test
	public void testIfStatementValidation() {
		CompilationUnitDeclaration unit = createLangModelCompileAndCompareTest("class A { public void run(){ if(1>3) {run_if();} else {run_else();}}}");

		Validatelet<IfDeclaration> v1 = (in, ctx) -> {
			if (in.getVariables().size() > 0) {
				ctx.fail("No variables allowed in if declaration");
			}
		};

		ValidationHook hook = new ValidationHook(v1);

		ModelTraverse traverse = new ModelTraverse(hook);
		traverse.traverse(unit);
		assertEquals(0, hook.getMessages().size());

		unit = createLangModelCompileAndCompareTest("class A { public void run(){ if(1>3) { int i = 0; run_if();} else {run_else();}}}");
		traverse.traverse(unit);
		assertEquals(1, hook.getMessages().size());
	}

	private CompilationUnitDeclaration createLangModelCompileAndCompareTest(
			String code) {
		UIBinding.scopes.clear();

		// checking whether sample code compiles and generate model
		boolean successCompile = false;
		try {
			CompilerConfiguration conf = new CompilerConfiguration();
			conf.addCompilationCustomizers(new ASTTransformationCustomizer(
					new VRLVisualizationTransformation()));
			GroovyClassLoader gcl = new GroovyClassLoader(this.getClass()
					.getClassLoader(), conf);
			gcl.parseClass(code);
			successCompile = true;
			gcl.close();
		} catch (Exception ex) {
			Logger.getLogger(LangModelTest.class.getName()).log(Level.SEVERE,
					null, ex);
		}

		Assert.assertTrue("Sample code must compile", successCompile);
		Assert.assertTrue("UIBindings.scopes must be initialized",
				UIBinding.scopes != null);
		Assert.assertTrue("UIBindings must contain exactly one scope, got "
				+ UIBinding.scopes.size(), UIBinding.scopes.size() == 1);

		// generating new code from model
		String newCode = "";
		for (Collection<Scope> scopeList : UIBinding.scopes.values()) {
			for (Scope s : scopeList) {
				if (s instanceof CompilationUnitDeclaration) {
					newCode = Scope2Code
							.getCode((CompilationUnitDeclaration) s);
					break;
				}
			}
		}

		System.out.println("---- old code ----");
		System.out.println(newCode);

		// checking whether new code compiles
		successCompile = false;
		UIBinding.scopes.clear();
		try {
			CompilerConfiguration cfg = new CompilerConfiguration();
			cfg.addCompilationCustomizers(new ASTTransformationCustomizer(
					new VRLVisualizationTransformation()));
			GroovyClassLoader gcl = new GroovyClassLoader(
					LangModelTest.class.getClassLoader(), cfg);
			gcl.parseClass(newCode, "MyFileClass.groovy");
			successCompile = true;
		} catch (Exception ex) {
			Logger.getLogger(LangModelTest.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		Assert.assertTrue("Sample code generated from model must compile",
				successCompile);

		String newNewCode = "";
		// checking whether code from new model is identical to new code
		CompilationUnitDeclaration unit = null;
		for (Collection<Scope> scopeList : UIBinding.scopes.values()) {
			for (Scope s : scopeList) {
				if (s instanceof CompilationUnitDeclaration) {
					unit = (CompilationUnitDeclaration) s;
					newNewCode = Scope2Code.getCode(unit);
					break;
				}
			}
		}

		System.out.println("---- new code ----");
		System.out.println(newNewCode);

		Assert.assertTrue("Code strings must be identical",
				newCode.equals(newNewCode));

		return unit;
	}
}
