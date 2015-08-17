package eu.mihosoft.vrl.instrumentation.composites;

import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.control.SourceUnit;

import com.google.common.base.Objects;

import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.BinaryOperatorInvocationImpl;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.IArgument;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.Operator;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class BinaryExpressionPart extends
		AbstractCodeBuilderPart<BinaryExpression, Invocation, CodeEntity> {

	public BinaryExpressionPart(SourceUnit sourceUnit,
			VisualCodeBuilder builder, CodeLineColumnMapper mapper) {
		super(sourceUnit, builder, mapper);
	}

	@Override
	public Invocation transform(BinaryExpression s, CodeEntity current,
			TransformContext context) {
		Scope parent = getParentScope(current, Scope.class);

		Operator operator = convertOperator(s);
		IArgument leftArg = convertToArgument("BinaryExpression.leftArgument",
				s.getLeftExpression(), context);
		IArgument rightArg = convertToArgument(
				"BinaryExpression.rightArgument", s.getRightExpression(),
				context);

		boolean emptyAssignment = (Objects.equal(Argument.NULL, rightArg) && operator == Operator.ASSIGN);

		if (!emptyAssignment) {

			Invocation invocation = new BinaryOperatorInvocationImpl(parent,
					leftArg, rightArg, operator);

			setCodeRange(invocation, s);

			return invocation;
		}

		return null;
	}

	@Override
	public Class<BinaryExpression> getAcceptedType() {
		return BinaryExpression.class;
	}

	@Override
	public Class<CodeEntity> getParentType() {
		return CodeEntity.class;
	}

}
