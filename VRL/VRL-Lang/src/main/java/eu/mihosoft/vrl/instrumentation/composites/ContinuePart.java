package eu.mihosoft.vrl.instrumentation.composites;

import java.util.Stack;

import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ContinueInvocation;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class ContinuePart
		extends
		AbstractCodeBuilderPart<ContinueStatement, ContinueInvocation, ControlFlowScope> {

	public ContinuePart(StateMachine stateMachine, SourceUnit sourceUnit,
			VisualCodeBuilder builder, CodeLineColumnMapper mapper) {
		super(stateMachine, sourceUnit, builder, mapper);
	}

	@Override
	public ContinueInvocation transform(Stack<Object> stackIn,
			ContinueStatement obj, Stack<Object> stackOut,
			ControlFlowScope parent) {
		return builder.invokeContinue(parent);
	}

	@Override
	public Class<ContinueStatement> getAcceptedType() {
		return ContinueStatement.class;
	}

	@Override
	public Class<ControlFlowScope> getParentType() {
		return ControlFlowScope.class;
	}

	@Override
	public boolean accepts(Stack<Object> stackIn, ContinueStatement obj,
			Stack<Object> stackOut, ControlFlowScope parent) {
		return true;
	}

}
