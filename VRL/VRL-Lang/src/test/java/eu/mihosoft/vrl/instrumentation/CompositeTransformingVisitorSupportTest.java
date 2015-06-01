package eu.mihosoft.vrl.instrumentation;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Consumer;

import javafx.collections.ObservableList;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.junit.Test;

import eu.mihosoft.vrl.instrumentation.CompositeTransformingVisitorSupport.Root;
import eu.mihosoft.vrl.lang.model.BinaryOperatorInvocation;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CodeEvent;
import eu.mihosoft.vrl.lang.model.CodeEventHandler;
import eu.mihosoft.vrl.lang.model.Comment;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.ControlFlow;
import eu.mihosoft.vrl.lang.model.DataFlow;
import eu.mihosoft.vrl.lang.model.DeclarationInvocation;
import eu.mihosoft.vrl.lang.model.ICodeEventType;
import eu.mihosoft.vrl.lang.model.ICodeRange;
import eu.mihosoft.vrl.lang.model.IExtends;
import eu.mihosoft.vrl.lang.model.IModifiers;
import eu.mihosoft.vrl.lang.model.IParameters;
import eu.mihosoft.vrl.lang.model.IType;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.ScopeInvocation;
import eu.mihosoft.vrl.lang.model.ScopeType;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.lang.model.Variable;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder_Impl;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class CompositeTransformingVisitorSupportTest {

	static class ModuleTransformer implements
			TransformPart<ModuleNode, CompilationUnitDeclaration, Root> {
		VisualCodeBuilder builder;

		public ModuleTransformer(VisualCodeBuilder builder) {
			this.builder = builder;
		}

		@Override
		public CompilationUnitDeclaration transform(Stack<Object> stackIn,
				ModuleNode obj, Stack<Object> stackOut, Root parent) {
			String packageName = "";
			if (obj.getPackageName() != null)
			{
				packageName = obj.getPackageName(); 
			}
			CompilationUnitDeclaration decl = builder.declareCompilationUnit(
					obj.getMainClassName() + ".groovy", packageName);
			parent.setRootObject(decl);
			return decl;
		}

		@Override
		public Class<ModuleNode> getAcceptedType() {
			return ModuleNode.class;
		}

		@Override
		public Class<Root> getParentType() {
			return Root.class;
		}

		@Override
		public boolean accepts(Stack<Object> stackIn, ModuleNode obj,
				Stack<Object> stackOut, Root parent) {
			return true;
		}

	}

	static class ClassTransformer
			implements
			TransformPart<ClassNode, ClassDeclaration, CompilationUnitDeclaration> {
		VisualCodeBuilder builder;
		CompilationUnitDeclaration rootScope;

		public ClassTransformer(VisualCodeBuilder builder) {
			this.builder = builder;
			this.rootScope = builder.declareCompilationUnit(
					"test" + this.hashCode(), this.getClass().getPackage()
							.getName());
		}

		@Override
		public ClassDeclaration transform(Stack<Object> stackIn, ClassNode obj,
				Stack<Object> stackOut, CompilationUnitDeclaration parent) {
			ClassDeclaration cls = builder.declareClass(parent, new Type("Test"
					+ obj.getName(), false));
			return cls;
		}

		@Override
		public Class<ClassNode> getAcceptedType() {
			return ClassNode.class;
		}

		@Override
		public Class<CompilationUnitDeclaration> getParentType() {
			return CompilationUnitDeclaration.class;
		}

		@Override
		public boolean accepts(Stack<Object> stackIn, ClassNode obj,
				Stack<Object> stackOut, CompilationUnitDeclaration parent) {
			return true;
		}

	}

	static class MethodTransformer implements
			TransformPart<MethodNode, MethodDeclaration, ClassDeclaration> {
		VisualCodeBuilder builder;

		public MethodTransformer(VisualCodeBuilder builder) {
			this.builder = builder;
		}

		@Override
		public MethodDeclaration transform(Stack<Object> stackIn,
				MethodNode obj, Stack<Object> stackOut, ClassDeclaration parent) {
			MethodDeclaration decl = builder.declareMethod(parent, new Type(obj
					.getReturnType().getName()), obj.getName());
			return decl;
		}

		@Override
		public Class<MethodNode> getAcceptedType() {
			return MethodNode.class;
		}

		@Override
		public Class<ClassDeclaration> getParentType() {
			return ClassDeclaration.class;
		}

		@Override
		public boolean accepts(Stack<Object> stackIn, MethodNode obj,
				Stack<Object> stackOut, ClassDeclaration parent) {
			return true;
		}

	}

	@GroovyASTTransformation
	public static class CompositeASTTransformation implements ASTTransformation {
		private CompositeTransformingVisitorSupport support;

		public CompositeASTTransformation(
				CompositeTransformingVisitorSupport support) {
			this.support = support;
		}

		@Override
		public void visit(ASTNode[] nodes, SourceUnit source) {
			support.visitModuleNode(source.getAST());
		}

	}

	@Test
	public void simpleTransform() {
		VisualCodeBuilder_Impl builder = new VisualCodeBuilder_Impl();
		CompositeTransformingVisitorSupport support = new CompositeTransformingVisitorSupport(
				null, new ClassTransformer(builder), new MethodTransformer(
						builder), new ModuleTransformer(builder));
		CompositeASTTransformation transform = new CompositeASTTransformation(
				support);
		String script = "class X { public int foo(int param) { return param+1; } }";
		CompilerConfiguration conf = new CompilerConfiguration();
		conf.addCompilationCustomizers(new ASTTransformationCustomizer(
				transform));
		GroovyShell shell = new GroovyShell(conf);
		Script foo = shell.parse(script);
		assertNotNull(support.getRoot().getRootObject());

		CompilationUnitDeclaration cu = (CompilationUnitDeclaration) support.getRoot()
				.getRootObject();
		assertEquals("X.groovy", cu.getName());
		
		assertEquals(1, cu.getDeclaredClasses().size());
		
		ClassDeclaration cls = cu.getDeclaredClasses().get(0);
		assertEquals("TestX", cls.getName());
		

		assertEquals(1, cls.getDeclaredMethods().size());
		assertEquals("foo", cls.getDeclaredMethods().iterator().next()
				.getName());
	}

}
