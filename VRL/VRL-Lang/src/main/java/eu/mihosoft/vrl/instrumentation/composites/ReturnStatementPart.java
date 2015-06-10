package eu.mihosoft.vrl.instrumentation.composites;

import java.util.Stack;

import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.instrumentation.TransformContext;
import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.IArgument;
import eu.mihosoft.vrl.lang.model.ReturnStatementInvocation;
import eu.mihosoft.vrl.lang.model.ReturnStatementInvocationImpl;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class ReturnStatementPart
		extends
		AbstractCodeBuilderPart<ReturnStatement, ReturnStatementInvocation, ControlFlowScope> {

	public ReturnStatementPart(StateMachine stateMachine, SourceUnit unit, VisualCodeBuilder builder,
			CodeLineColumnMapper mapper) {
		super(stateMachine, unit, builder, mapper);
	}

	@Override
	public ReturnStatementInvocation transform(
			ReturnStatement s, ControlFlowScope parent, TransformContext context) {
		ReturnStatementInvocation inv = new ReturnStatementInvocationImpl(parent);
		context.bind("returnExpression", s.getExpression());
		return inv;
	}

	@Override
	public void postTransform(ReturnStatementInvocation obj,
			ReturnStatement in, ControlFlowScope parent,
			TransformContext context) {
		ReturnStatementInvocationImpl inv = (ReturnStatementInvocationImpl) obj;
		IArgument arg = context.resolve("returnExpression", IArgument.class);
		inv.setArgument(arg);
	}
	
	@Override
	public Class<ReturnStatement> getAcceptedType() {
		return ReturnStatement.class;
	}

	@Override
	public Class<ControlFlowScope> getParentType() {
		return ControlFlowScope.class;
	}
}
