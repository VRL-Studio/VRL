package eu.mihosoft.vrl.instrumentation.composites;

import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.instrumentation.TransformContext;
import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.IArgument;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class ConstantExpressionPart
		extends
		AbstractCodeBuilderPart<ConstantExpression, IArgument, ControlFlowScope> {

	public ConstantExpressionPart(StateMachine stateMachine,
			SourceUnit sourceUnit, VisualCodeBuilder builder,
			CodeLineColumnMapper mapper) {
		super(stateMachine, sourceUnit, builder, mapper);
	}

	@Override
	public IArgument transform(ConstantExpression obj, ControlFlowScope parent,
			TransformContext ctx) {

		if (obj.isNullExpression()) {
			return Argument.NULL;
		} else {
			return Argument.constArg(new Type(obj.getType().getName(),
					true), obj.getValue());
		}
	}

	@Override
	public Class<ConstantExpression> getAcceptedType() {
		return ConstantExpression.class;
	}

	@Override
	public Class<ControlFlowScope> getParentType() {
		return ControlFlowScope.class;
	}

}
