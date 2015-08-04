package eu.mihosoft.vrl.instrumentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Reader;
import java.lang.reflect.Proxy;
import java.util.Collection;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.junit.Test;

import eu.mihosoft.vrl.instrumentation.CompositeTransformingVisitorSupport.Root;
import eu.mihosoft.vrl.instrumentation.composites.BinaryExpressionPart;
import eu.mihosoft.vrl.instrumentation.composites.BreakPart;
import eu.mihosoft.vrl.instrumentation.composites.ClassNodePart;
import eu.mihosoft.vrl.instrumentation.composites.ConstantExpressionPart;
import eu.mihosoft.vrl.instrumentation.composites.ContinuePart;
import eu.mihosoft.vrl.instrumentation.composites.DeclarationExpressionPart;
import eu.mihosoft.vrl.instrumentation.composites.FieldPart;
import eu.mihosoft.vrl.instrumentation.composites.ForLoopPart;
import eu.mihosoft.vrl.instrumentation.composites.IfStatementPart;
import eu.mihosoft.vrl.instrumentation.composites.MethodCallExpressionPart;
import eu.mihosoft.vrl.instrumentation.composites.MethodNodePart;
import eu.mihosoft.vrl.instrumentation.composites.ModuleNodePart;
import eu.mihosoft.vrl.instrumentation.composites.PostFixExpressionPart;
import eu.mihosoft.vrl.instrumentation.composites.ReturnStatementPart;
import eu.mihosoft.vrl.instrumentation.composites.WhileLoopPart;
import eu.mihosoft.vrl.instrumentation.transform.DefaultProxy;
import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.instrumentation.transform.TransformPart;
import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.ControlFlow;
import eu.mihosoft.vrl.lang.model.IArgument;
import eu.mihosoft.vrl.lang.model.IdRequest;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.lang.model.UIBinding;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder_Impl;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.IdGenerator;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class CompositeTransformingVisitorSupportTest {

	@Test
	public void proxy() throws Exception {

		IArgument nullArg = Argument.nullArg();
		DefaultProxy<IArgument> proxy = new DefaultProxy<IArgument>("test",
				null, IArgument.class);
		proxy.setProxied(nullArg);
		IArgument arg = (IArgument) Proxy.newProxyInstance(this.getClass()
				.getClassLoader(), new Class[] { IArgument.class }, proxy);
		assertEquals(nullArg.getArgType(), arg.getArgType());

	}

	static class ModuleTransformer implements
			TransformPart<ModuleNode, CompilationUnitDeclaration, Root> {
		VisualCodeBuilder builder;

		public ModuleTransformer(VisualCodeBuilder builder) {
			this.builder = builder;
		}

		@Override
		public CompilationUnitDeclaration transform(ModuleNode obj,
				Root parent, TransformContext context) {
			String packageName = "";
			if (obj.getPackageName() != null) {
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
		public void postTransform(CompilationUnitDeclaration obj,
				ModuleNode in, Root parent, TransformContext context) {

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
		public ClassDeclaration transform(ClassNode obj,
				CompilationUnitDeclaration parent, TransformContext context) {
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
		public void postTransform(ClassDeclaration obj, ClassNode in,
				CompilationUnitDeclaration parent, TransformContext context) {

		}

	}

	static class MethodTransformer implements
			TransformPart<MethodNode, MethodDeclaration, ClassDeclaration> {
		VisualCodeBuilder builder;

		public MethodTransformer(VisualCodeBuilder builder) {
			this.builder = builder;
		}

		@Override
		public MethodDeclaration transform(MethodNode obj,
				ClassDeclaration parent, TransformContext context) {
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
		public void postTransform(MethodDeclaration obj, MethodNode in,
				ClassDeclaration parent, TransformContext context) {

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

		CompilationUnitDeclaration cu = (CompilationUnitDeclaration) support
				.getRoot().getRootObject();
		assertEquals("X.groovy", cu.getName());

		assertEquals(1, cu.getDeclaredClasses().size());

		ClassDeclaration cls = cu.getDeclaredClasses().get(0);
		assertEquals("TestX", cls.getName());

		assertEquals(4, cls.getDeclaredMethods().size());
		assertEquals("foo", cls.getDeclaredMethods().iterator().next()
				.getName());
	}

	@Test
	public void testModuleNodePart() throws Exception {

		SourceUnit source = fromCode("class A {}");
		CompositeTransformingVisitorSupport visitor = VRLVisualizationTransformation.init(source);
		visitor.visitModuleNode(source.getAST());
		assertNotNull(visitor.getRoot().getRootObject());
		CompilationUnitDeclaration decl = (CompilationUnitDeclaration) visitor
				.getRoot().getRootObject();
		assertEquals(1, decl.getDeclaredClasses().size());
	}

	@Test
	public void testDeferredResolution() throws Exception {
		// ReturnPart uses deferred resolution to connect the constant
		// expression "0" with the
		// ReturnInvocation instance.
		SourceUnit src = fromCode("class A{ void run(){ return 0; }}");
		CompositeTransformingVisitorSupport visitor = VRLVisualizationTransformation.init(src);
		visitor.visitModuleNode(src.getAST());
		CompilationUnitDeclaration decl = (CompilationUnitDeclaration) visitor
				.getRoot().getRootObject();
		ControlFlow flow = decl.getDeclaredClasses().get(0)
				.getDeclaredMethods().get(0).getControlFlow();
		assertEquals(Integer.valueOf(0),
				flow.getInvocations().get(0).getArguments().get(0)
						.getConstant().get().getValue(Integer.class));
	}

	@Test
	public void testWhileLoopTransform() throws Exception {
		SourceUnit src = fromCode("class A{ void run(){while(1>3) { run(); }}}");
		CompositeTransformingVisitorSupport visitor = VRLVisualizationTransformation.init(src);
		visitor.visitModuleNode(src.getAST());
		System.out.println(visitor.getRoot().getRootObject());

	}

	void printBindings() {
		for (Collection<Scope> scopeList : UIBinding.scopes.values()) {
			for (Scope s : scopeList) {

				System.out.println("scope: " + s);

				if (s instanceof CompilationUnitDeclaration) {
					System.out.println(Scope2Code
							.getCode((CompilationUnitDeclaration) s));
					break;
				}
			}
		}
	}

	public static SourceUnit fromCode(String code) throws Exception {
		SourceUnit sourceUnit = SourceUnit.create("Test.groovy", code);
		CompilationUnit compUnit = new CompilationUnit();
		compUnit.addSource(sourceUnit);
		compUnit.compile(Phases.CANONICALIZATION);
		return sourceUnit;
	}
}
