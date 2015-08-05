package eu.mihosoft.vrl.instrumentation.composites;

import java.util.Stack;

import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.control.SourceUnit;

import com.google.common.base.Objects;

import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.DeclarationInvocation;
import eu.mihosoft.vrl.lang.model.DeclarationInvocationImpl;
import eu.mihosoft.vrl.lang.model.SimpleForDeclaration;
import eu.mihosoft.vrl.lang.model.SimpleForDeclaration_Impl;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class DeclarationExpressionPart
		extends
		AbstractCodeBuilderPart<DeclarationExpression, DeclarationInvocation, ControlFlowScope> {

	public DeclarationExpressionPart(StateMachine stateMachine,
			SourceUnit sourceUnit, VisualCodeBuilder builder,
			CodeLineColumnMapper mapper) {
		super(stateMachine, sourceUnit, builder, mapper);
	}

	@Override
	public DeclarationInvocation transform(DeclarationExpression s,
			ControlFlowScope currentScope, TransformContext context) {

			DeclarationInvocation declInv = builder.declareVariable(
					currentScope, new Type(s.getVariableExpression().getType()
							.getName(), true), s.getVariableExpression()
							.getName(), convertToArgument("DeclarationExpression.initVal", s.getRightExpression(), context));
			if (currentScope instanceof SimpleForDeclaration)
			{
				((DeclarationInvocationImpl)declInv).setTextRenderingEnabled(false);
			}
			setCodeRange(declInv, s);

			return declInv;
	}

	@Override
	public void postTransform(DeclarationInvocation obj,
			DeclarationExpression s, ControlFlowScope currentScope,
			TransformContext context) {
		
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
