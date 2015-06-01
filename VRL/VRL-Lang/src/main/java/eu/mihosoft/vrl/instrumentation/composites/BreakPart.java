package eu.mihosoft.vrl.instrumentation.composites;

import java.util.Stack;

import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.lang.model.BreakInvocation;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ContinueInvocation;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class BreakPart
		extends
		AbstractCodeBuilderPart<BreakStatement, BreakInvocation, ControlFlowScope> {

	public BreakPart(StateMachine stateMachine, SourceUnit sourceUnit,
			VisualCodeBuilder builder, CodeLineColumnMapper mapper) {
		super(stateMachine, sourceUnit, builder, mapper);
	}

	@Override
	public BreakInvocation transform(Stack<Object> stackIn,
			BreakStatement obj, Stack<Object> stackOut,
			ControlFlowScope parent) {
		return builder.invokeBreak(parent);
	}

	@Override
	public Class<BreakStatement> getAcceptedType() {
		return BreakStatement.class;
	}

	@Override
	public Class<ControlFlowScope> getParentType() {
		return ControlFlowScope.class;
	}

	@Override
	public boolean accepts(Stack<Object> stackIn, BreakStatement obj,
			Stack<Object> stackOut, ControlFlowScope parent) {
		return true;
	}

}
