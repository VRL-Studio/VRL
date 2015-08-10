package eu.mihosoft.vrl.instrumentation.composites;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.control.SourceUnit;
import org.junit.Test;

import eu.mihosoft.vrl.instrumentation.CompositeTransformingVisitorSupportTest;
import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlow;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.DeclarationInvocationImpl;
import eu.mihosoft.vrl.lang.model.ICodeRange;
import eu.mihosoft.vrl.lang.model.IdRequest;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.ScopeType;
import eu.mihosoft.vrl.lang.model.Variable;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder_Impl;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.IdGenerator;
import eu.mihosoft.vrl.workflow.VFlow;

public class CompositeTestUtil<GroovyType, PartType> {

	protected ControlFlowScope scope;
	protected TransformContext context;
	protected GroovyType statement;
	protected StateMachine stateMachine;
	protected SourceUnit sourceUnit;
	protected PartType part;

	protected Map<String, Object> resolutionMap = new HashMap<>();

	protected ControlFlowScope fixture(Class<? extends ControlFlowScope> cls) {
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
		}, cls);
	}

	public static <T> T createProxy(DynaProxy handler, Class<T> t,
			Class<?>... cls) {
		return t.cast(Proxy.newProxyInstance(
				CompositeTestUtil.class.getClassLoader(), cls, handler));
	}

	public static <T> T createProxy(DynaProxy handler, Class<T> t) {
		return createProxy(handler, t, t);
	}

	public static <T> T createProxy(Class<T> t) {
		return createProxy(new DynaProxy() {
		}, t, t);
	}

	public abstract static class DynaProxy implements InvocationHandler {

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

	protected void resolveAs(String key, Object o) {
		resolutionMap.put(key, o);
	}

	protected void createHarness(String code, Class<GroovyType> cls,
			Class<PartType> partClass) throws Exception {
		resolutionMap.clear();
		VisualCodeBuilder_Impl builder = new VisualCodeBuilder_Impl();
		stateMachine = new StateMachine();

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
				.fromCode(code);
		statement = cls.cast(sourceUnit.getAST().getStatementBlock()
				.getStatements().get(0));
		Reader in = sourceUnit.getSource().getReader();
		CodeLineColumnMapper mapper = new CodeLineColumnMapper();
		mapper.init(in);
		scope = fixture(ControlFlowScope.class);
		context = createProxy(new DynaProxy() {
			public <T> T _resolve(String key, Object obj, Class<T> cls) {
				return cls.cast(resolutionMap.get(key));
			}
		}, TransformContext.class);
		part = partClass.getConstructor(StateMachine.class, SourceUnit.class,
				VisualCodeBuilder.class, CodeLineColumnMapper.class)
				.newInstance(stateMachine, sourceUnit, builder, mapper);
	}
}
