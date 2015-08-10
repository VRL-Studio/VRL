package eu.mihosoft.vrl.instrumentation.composites;

import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.IArgument;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class BooleanExpressionPart extends
		AbstractCodeBuilderPart<BooleanExpression, IArgument, Scope> {

	public BooleanExpressionPart(StateMachine stateMachine,
			SourceUnit sourceUnit, VisualCodeBuilder builder,
			CodeLineColumnMapper mapper) {
		super(stateMachine, sourceUnit, builder, mapper);
	}

	@Override
	public IArgument transform(BooleanExpression obj, Scope parent,
			TransformContext ctx) {
		return ctx.resolve("BooleanExpression.child", obj.getExpression(),
				IArgument.class);
	}

	@Override
	public Class<BooleanExpression> getAcceptedType() {
		return BooleanExpression.class;
	}

	@Override
	public Class<Scope> getParentType() {
		return Scope.class;
	}

}
