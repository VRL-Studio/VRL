package eu.mihosoft.vrl.instrumentation.composites;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.control.SourceUnit;
import org.junit.Test;

import eu.mihosoft.vrl.instrumentation.CompositeTransformingVisitorSupportTest;
import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.instrumentation.transform.DefaultProxy;
import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlow;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.DeclarationInvocationImpl;
import eu.mihosoft.vrl.lang.model.ICodeRange;
import eu.mihosoft.vrl.lang.model.IdRequest;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.ScopeType;
import eu.mihosoft.vrl.lang.model.SimpleForDeclaration;
import eu.mihosoft.vrl.lang.model.Variable;
import eu.mihosoft.vrl.lang.model.VariableFactory;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder_Impl;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.IdGenerator;
import eu.mihosoft.vrl.workflow.VFlow;

public class ForLoopPartTest {

	ForLoopPart part;
	ForStatement statement;
	ControlFlowScope scope;
	TransformContext context;

	@Test
	public void testSimpleForLoop() throws Exception {
		createHarness("for (int i=0; i<10; i++){System.out.println(i);}");
		SimpleForDeclaration decl = part.transform(statement, scope, context);
		part.postTransform(decl, statement, scope, context);
		assertEquals(0, decl.getFrom());
		assertEquals(9, decl.getTo());
		assertEquals(1, decl.getInc());
	}

	@Test
	public void testSimpleForLoopWithGE() throws Exception {
		createHarness("for (int i=0; i<=10; i++){System.out.println(i);}");
		scope = fixture();
		SimpleForDeclaration decl = part.transform(statement, scope, context);
		part.postTransform(decl, statement, scope, context);
		assertEquals(0, decl.getFrom());
		assertEquals(10, decl.getTo());
		assertEquals(1, decl.getInc());
	}

	void createHarness(String forLoopCode) throws Exception {
		VisualCodeBuilder_Impl builder = new VisualCodeBuilder_Impl();
		StateMachine stateMachine = new StateMachine();

		builder.setIdRequest(new IdRequest() {

			private IdGenerator generator = FlowFactory.newIdGenerator();

			@Override
			public String request() {
				String result = generator
						.newId(this.getClass().getSimpleName());
				return result;
			}
		});

		SourceUnit sourceUnit = CompositeTransformingVisitorSupportTest
				.fromCode(forLoopCode);
		statement = (ForStatement) sourceUnit.getAST().getStatementBlock()
				.getStatements().get(0);
		Reader in = sourceUnit.getSource().getReader();
		CodeLineColumnMapper mapper = new CodeLineColumnMapper();
		mapper.init(in);
		scope = fixture();
		context = createProxy(new DynaProxy() {
			public <T> T _resolve(String key, Object obj, Class<T> cls) {
				return cls.cast(new DeclarationInvocationImpl());
			}
		}, TransformContext.class);
		part = new ForLoopPart(stateMachine, sourceUnit, builder, mapper);
	}

	@Test
	public void testProxy() throws Exception {
		ControlFlowScope scope = createProxy(new DynaProxy() {
			public String _getName() {
				return "Test";
			}
		}, ControlFlowScope.class);
		assertEquals("Test", scope.getName());
	}

	private ControlFlowScope fixture() {
		return createProxy(new DynaProxy() {
			public ScopeType _getType() {
				return ScopeType.METHOD;
			}

			public VFlow _getFlow() {
				return FlowFactory.newFlow();
			}

			public void _addScope(Scope s) {

			}

			public void _setRange(ICodeRange r) {

			}

			public Variable _getVariable(String varName) {
				return createProxy(Variable.class);
			}

			public ControlFlow _getControlFlow() {
				return createProxy(new DynaProxy() {
					public void _callScope(Scope s) {

					}

				}, ControlFlow.class);
			}
		}, ControlFlowScope.class);
	}

	private <T> T createProxy(DynaProxy handler, Class<T> t, Class<?>... cls) {
		return t.cast(Proxy.newProxyInstance(this.getClass().getClassLoader(),
				cls, handler));
	}

	private <T> T createProxy(DynaProxy handler, Class<T> t) {
		return createProxy(handler, t, t);
	}

	private <T> T createProxy(Class<T> t) {
		return createProxy(new DynaProxy() {
		}, t, t);
	}

	abstract static class DynaProxy implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			try {
				Method m = this.getClass().getMethod("_" + method.getName(),
						method.getParameterTypes());
				return m.invoke(this, args);
			} catch (NoSuchMethodException e) {
				return null;
			}
		}
	}
}
