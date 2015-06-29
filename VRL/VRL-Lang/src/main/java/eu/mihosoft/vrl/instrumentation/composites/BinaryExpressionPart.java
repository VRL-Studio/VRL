package eu.mihosoft.vrl.instrumentation.composites;

import java.util.Stack;

import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.control.SourceUnit;

import com.google.common.base.Objects;

import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.IArgument;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.Operator;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.SimpleForDeclaration;
import eu.mihosoft.vrl.lang.model.SimpleForDeclaration_Impl;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class BinaryExpressionPart extends
		AbstractCodeBuilderPart<BinaryExpression, Invocation, CodeEntity> {

	public BinaryExpressionPart(StateMachine stateMachine,
			SourceUnit sourceUnit, VisualCodeBuilder builder,
			CodeLineColumnMapper mapper) {
		super(stateMachine, sourceUnit, builder, mapper);
	}

	@Override
	public Invocation transform(BinaryExpression s,
			CodeEntity current, TransformContext context) {
		Scope parent = getParentScope(current, Scope.class);
		if (stateMachine.getBoolean("for-loop")
				&& !stateMachine.getBoolean("for-loop:compareExpression")
				&& !stateMachine.getBoolean("for-loop:incExpression")) {

		} else if (stateMachine.getBoolean("for-loop")
				&& stateMachine.getBoolean("for-loop:declaration")
				&& stateMachine.getBoolean("for-loop:compareExpression")
				&& !stateMachine.getBoolean("for-loop:incExpression")) {

		} else {

			if (!stateMachine.getReturnVariables().containsKey(s)) {

				Operator operator = convertOperator(s);
				IArgument leftArg = convertToArgument("BinaryExpression.leftArgument",
						s.getLeftExpression(), context);
				IArgument rightArg = convertToArgument("BinaryExpression.rightArgument",
						s.getRightExpression(), context);

				boolean emptyAssignment = (Objects.equal(Argument.NULL,
						rightArg) && operator == Operator.ASSIGN);

				if (!emptyAssignment) {

					Invocation invocation = builder.invokeOperator(
							parent, leftArg, rightArg, operator);

					setCodeRange(invocation, s);

					stateMachine.getReturnVariables().put(s, invocation);

					return invocation;
				}
			}
		}
		return null;
	}

	@Override
	public void postTransform(Invocation obj, BinaryExpression s,
			CodeEntity current, TransformContext context) {
		ControlFlowScope currentScope = getParentScope(current, ControlFlowScope.class);
		if (stateMachine.getBoolean("for-loop")
				&& !stateMachine.getBoolean("for-loop:compareExpression")
				&& !stateMachine.getBoolean("for-loop:incExpression")) {

			SimpleForDeclaration_Impl forD = (SimpleForDeclaration_Impl) currentScope;

			if (stateMachine.getBoolean("for-loop:declaration")
					&& !stateMachine.getBoolean("for-loop:compareExpression")) {

				if (!(s.getLeftExpression() instanceof VariableExpression)) {
					throwErrorMessage(
							"In for-loop: only binary"
									+ " expressions of the form 'a <= b'/'a >= b' with a, b being"
									+ " constant integers are supported!", s);
				}

				if (!"<=".equals(s.getOperation().getText())
						&& !">=".equals(s.getOperation().getText())) {
					throwErrorMessage(
							"In for-loop: only binary"
									+ " expressions of the form 'a <= b' or 'a >= b' with a, b being"
									+ " constant integers are supported!", s);
				}

				stateMachine.setString("for-loop:compareOperation", s
						.getOperation().getText());

				if (!(s.getRightExpression() instanceof ConstantExpression)) {
					throwErrorMessage(
							"In for-loop: only binary"
									+ " expressions of the form 'a <= b' or 'a >= b' with a, b being"
									+ " constant integers are supported!", s);
				}

				ConstantExpression ce = (ConstantExpression) s
						.getRightExpression();

				if (!(ce.getValue() instanceof Integer)) {
					// throw new IllegalStateException("In for-loop: value '" +
					// ce.getValue()
					// + "' is not an integer constant! ");

					throwErrorMessage(
							"In for-loop: only binary"
									+ " expressions of the form 'a <= b' or 'a >= b' with a, b being"
									+ " constant integers are supported!", s);
				}

				forD.setTo((int) ce.getValue());

				stateMachine.setBoolean("for-loop:compareExpression", true);
			}
		} else if (stateMachine.getBoolean("for-loop")
				&& stateMachine.getBoolean("for-loop:declaration")
				&& stateMachine.getBoolean("for-loop:compareExpression")
				&& !stateMachine.getBoolean("for-loop:incExpression")) {

			SimpleForDeclaration_Impl forD = (SimpleForDeclaration_Impl) currentScope;

			if (!"+=".equals(s.getOperation().getText())
					&& !"-=".equals(s.getOperation().getText())) {
				throw new IllegalStateException(
						"In for-loop: inc/dec '"
								+ s.getOperation().getText()
								+ "' not spupported! Must be '+=' or '-=' or '++' or '--'!");
			}

			if (!(s.getRightExpression() instanceof ConstantExpression)) {
				throwErrorMessage("In for-loop: variable '" + forD.getVarName()
						+ "' must be initialized with an integer constant!", s);
			}

			ConstantExpression ce = (ConstantExpression) s.getRightExpression();

			if (!(ce.getValue() instanceof Integer)) {
				throwErrorMessage(
						"In for-loop: inc/dec must be an integer constant!", s);
			}

			if ("+=".equals(s.getOperation().getText())) {
				forD.setInc((int) ce.getValue());
			} else if ("-=".equals(s.getOperation().getText())) {
				forD.setInc(-(int) ce.getValue());
			}

			if (forD.getInc() > 0
					&& ">=".equals(stateMachine
							.getString("for-loop:compareOperation"))) {
				throwErrorMessage(
						"In for-loop: infinite loops"
								+ " are not supported! Change '>=' to '<=' to prevent that.",
						s);
			}

			if (forD.getInc() < 0
					&& "<=".equals(stateMachine
							.getString("for-loop:compareOperation"))) {
				// throw new IllegalStateException("In for-loop: infinite loops"
				// + " are not supported! Change '<=' to '>=' to prevent that."
				// );
				throwErrorMessage(
						"In for-loop: infinite loops"
								+ " are not supported! Change '<=' to '>=' to prevent that.",
						s);
			}

			// System.out.println("s: " + s.getOperation().getText() + ", " +
			// forD.getInc());
			// System.exit(0);
			// if (forD.getInc() < 0 && "<=".equals(s.getOperation().getText()))
			// {
			// throw new IllegalStateException("In for-loop: infinite loops"
			// + " are not supported! Change '<=' to '>=' to prevent that."
			// );
			// }
			stateMachine.setBoolean("for-loop:incExpression", true);

			//
		}
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
