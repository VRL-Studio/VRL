package eu.mihosoft.vrl.instrumentation.composites;

import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.DeclarationInvocation;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class DeclarationExpressionPart
		extends
		AbstractCodeBuilderPart<DeclarationExpression, DeclarationInvocation, ControlFlowScope> {

	public DeclarationExpressionPart(SourceUnit sourceUnit,
			VisualCodeBuilder builder, CodeLineColumnMapper mapper) {
		super(sourceUnit, builder, mapper);
	}

	@Override
	public DeclarationInvocation transform(DeclarationExpression s,
			ControlFlowScope currentScope, TransformContext context) {

		DeclarationInvocation declInv = builder.declareVariable(
				currentScope,
				new Type(s.getVariableExpression().getType().getName(), true),
				s.getVariableExpression().getName(),
				convertToArgument("DeclarationExpression.initVal",
						s.getRightExpression(), context));

		setCodeRange(declInv, s);

		return declInv;
	}

	@Override
	public Class<DeclarationExpression> getAcceptedType() {
		return DeclarationExpression.class;
	}

	@Override
	public Class<ControlFlowScope> getParentType() {
		return ControlFlowScope.class;
	}
}
