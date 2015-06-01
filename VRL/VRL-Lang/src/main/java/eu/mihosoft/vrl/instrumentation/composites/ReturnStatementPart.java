package eu.mihosoft.vrl.instrumentation.composites;

import java.util.Stack;

import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.IArgument;
import eu.mihosoft.vrl.lang.model.ReturnStatementInvocation;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class ReturnStatementPart
		extends
		AbstractCodeBuilderPart<ReturnStatement, ReturnStatementInvocation, ControlFlowScope> {

	public ReturnStatementPart(StateMachine stateMachine, SourceUnit unit, VisualCodeBuilder builder,
			CodeLineColumnMapper mapper) {
		super(stateMachine, unit, builder, mapper);
	}

	@Override
	public ReturnStatementInvocation transform(Stack<Object> stackIn,
			ReturnStatement s, Stack<Object> stackOut, ControlFlowScope parent) {
		IArgument arg = convertExpressionToArgument(s.getExpression(), parent);
		return builder.returnValue(parent, arg);
	}

	@Override
	public Class<ReturnStatement> getAcceptedType() {
		return ReturnStatement.class;
	}

	@Override
	public Class<ControlFlowScope> getParentType() {
		return ControlFlowScope.class;
	}

	@Override
	public boolean accepts(Stack<Object> stackIn, ReturnStatement obj,
			Stack<Object> stackOut, ControlFlowScope parent) {
		return true;
	}
}
