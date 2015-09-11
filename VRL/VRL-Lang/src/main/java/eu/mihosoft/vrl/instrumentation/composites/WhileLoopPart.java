package eu.mihosoft.vrl.instrumentation.composites;

import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.BinaryOperatorInvocationImpl;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.IArgument;
import eu.mihosoft.vrl.lang.model.IType;
import eu.mihosoft.vrl.lang.model.Operator;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;
import eu.mihosoft.vrl.lang.model.WhileDeclaration;

public class WhileLoopPart
		extends
		AbstractCodeBuilderPart<WhileStatement, WhileDeclaration, ControlFlowScope> {

	public WhileLoopPart(SourceUnit sourceUnit, VisualCodeBuilder builder,
			CodeLineColumnMapper mapper) {
		super(sourceUnit, builder, mapper);
	}

	@Override
	public WhileDeclaration transform(WhileStatement s,
			ControlFlowScope parent, TransformContext context) {

		BooleanExpression expression = s.getBooleanExpression();

		if (expression == null || expression.getExpression() == null) {
			throwErrorMessage("while-loop: must contain boolean"
					+ " expression! No expression is not supported", s);

			return null;
		}

		if (expression.getExpression() instanceof ConstantExpression) {
			ConstantExpression constExpression = (ConstantExpression) expression
					.getExpression();

			if (constExpression.isNullExpression()) {
				throwErrorMessage("while-loop: must contain boolean"
						+ " expression! Null expression is not supported.", s);
			} else {

				IType expressionType = Type.fromObject(
						constExpression.getValue(), true);

				if (!Type.BOOLEAN.equals(expressionType)
						&& !new Type("java.lang.Boolean")
								.equals(expressionType)) {
					throwErrorMessage("while-loop: must contain boolean"
							+ " expression! '" + expressionType
							+ "' is not supported.", s);
				}
			}
		} else if (expression.getExpression() instanceof BinaryExpression) {
			Operator operator = convertOperator((BinaryExpression) expression
					.getExpression());

			if (!BinaryOperatorInvocationImpl.booleanOperator(operator)) {
				throwErrorMessage("while-loop: must contain boolean"
						+ " expression! Only boolean binary operations are "
						+ "supported.", s);
			}
		} else if (expression.getExpression() instanceof MethodCallExpression) {

			MethodCallExpression mExp = (MethodCallExpression) expression
					.getExpression();

			if (!convertMethodReturnType(mExp).equals(Type.BOOLEAN)) {
				throwErrorMessage("while-loop: must contain boolean"
						+ " expression! Only boolean method calls are "
						+ "supported.", s);
			}
		} else if (expression.getExpression() instanceof StaticMethodCallExpression) {
			StaticMethodCallExpression mExp = (StaticMethodCallExpression) expression
					.getExpression();

			if (!convertMethodReturnType(mExp).equals(Type.BOOLEAN)) {
				throwErrorMessage("while-loop: must contain boolean"
						+ " expression! Only boolean method calls are "
						+ "supported.", s);
			}
		} else {
			throwErrorMessage("while-loop: must contain boolean"
					+ " expression!" + expression.getExpression(), s);
		}

		IArgument arg = convertToArgument("WhileLoop.condition", s
				.getBooleanExpression().getExpression(), context);

		WhileDeclaration inv = builder.invokeWhileLoop(parent, arg);

		setCodeRange(inv, s);
		addCommentsToScope(inv, comments);

		return inv;
	}

	@Override
	public void postTransform(WhileDeclaration obj, WhileStatement in,
			ControlFlowScope parent, TransformContext context) {

	}

	@Override
	public Class<WhileStatement> getAcceptedType() {
		return WhileStatement.class;
	}

	@Override
	public Class<ControlFlowScope> getParentType() {
		return ControlFlowScope.class;
	}
}
