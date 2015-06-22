package eu.mihosoft.vrl.instrumentation.composites;

import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.DeclarationInvocation;
import eu.mihosoft.vrl.lang.model.IArgument;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.Variable;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class VariableExpressionPart
		extends
		AbstractCodeBuilderPart<VariableExpression, IArgument, Invocation> {

	public VariableExpressionPart(StateMachine stateMachine,
			SourceUnit sourceUnit, VisualCodeBuilder builder,
			CodeLineColumnMapper mapper) {
		super(stateMachine, sourceUnit, builder, mapper);
	}

	@Override
	public IArgument transform(VariableExpression obj, Invocation parent,
			TransformContext ctx) {
		Variable v = parent.getParent().getVariable(obj.getName());
		return Argument.varArg(v);
	}

	@Override
	public Class<VariableExpression> getAcceptedType() {
		return VariableExpression.class;
	}

	@Override
	public Class<Invocation> getParentType() {
		return Invocation.class;
	}

}
