package eu.mihosoft.vrl.instrumentation.composites;

import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.control.SourceUnit;

import com.google.common.base.Objects;

import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.DeclarationInvocation;
import eu.mihosoft.vrl.lang.model.DeclarationInvocationImpl;
import eu.mihosoft.vrl.lang.model.Operator;
import eu.mihosoft.vrl.lang.model.SimpleForDeclaration;
import eu.mihosoft.vrl.lang.model.SimpleForDeclaration_Impl;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class ForLoopPart
		extends
		AbstractCodeBuilderPart<ForStatement, SimpleForDeclaration, ControlFlowScope> {

	private static final String KEY_SIMPLE_FOR_DECLARATION = "SimpleForDeclaration.declaration";

	public ForLoopPart(SourceUnit sourceUnit, VisualCodeBuilder builder,
			CodeLineColumnMapper mapper) {
		super(sourceUnit, builder, mapper);
	}

	@Override
	public SimpleForDeclaration transform(ForStatement s,
			ControlFlowScope currentScope, TransformContext context) {
		System.out.println(" --> FOR-LOOP: " + s.getVariable());

		// predeclaration, ranges will be defined later
		SimpleForDeclaration decl = builder.invokeForLoop(currentScope, null,
				0, 0, 0, Operator.GREATER_EQUALS);

		ClosureListExpression expr = (ClosureListExpression) s
				.getCollectionExpression();
		context.resolve(KEY_SIMPLE_FOR_DECLARATION, expr.getExpression(0),
				DeclarationInvocation.class);

		setCodeRange(currentScope, s);
		addCommentsToScope(currentScope, comments);
		return decl;
	}

	@Override
	public void postTransform(SimpleForDeclaration obj, ForStatement s,
			ControlFlowScope scope, TransformContext context) {

		SimpleForDeclaration_Impl forD = (SimpleForDeclaration_Impl) obj;
		ClosureListExpression expr = (ClosureListExpression) s
				.getCollectionExpression();

		checkDeclaration(obj, expr.getExpression(0));

		// TODO this is quite hacky
		DeclarationInvocation declaration = context.resolve(
				KEY_SIMPLE_FOR_DECLARATION, expr.getExpression(0),
				DeclarationInvocation.class);

		DeclarationInvocationImpl impl = (DeclarationInvocationImpl) declaration;
		impl.setTextRenderingEnabled(false);

		forD.setOperation(convertOperator(expr.getExpression(1)));

		checkLoopCondition(obj, expr.getExpression(1));
		checkLoopExpression((SimpleForDeclaration_Impl) obj,
				expr.getExpression(2));
	}

	private void checkLoopExpression(SimpleForDeclaration_Impl forD,
			Expression expression) {

		if (expression instanceof PostfixExpression) {
			PostfixExpression obj = (PostfixExpression) expression;

			if ("++".equals(obj.getOperation().getText())) {
				forD.setInc(1);
			} else if ("--".equals(obj.getOperation().getText())) {
				forD.setInc(-1);
			}

			if (forD.getInc() < 0
					&& Operator.LESS_EQUALS.equals(forD.getOperation())) {
				// throw new IllegalStateException("In for-loop: infinite loops"
				// + " are not supported! Change '<=' to '>=' to prevent that."
				// );
				throwErrorMessage(
						"In for-loop: infinite loops"
								+ " are not supported! Change '<=' to '>=' to prevent that.",
						obj);
			}
		} else if (expression instanceof BinaryExpression) {
			BinaryExpression s = (BinaryExpression) expression;
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
					&& Operator.GREATER_EQUALS.equals(forD.getOperation())) {
				throwErrorMessage(
						"In for-loop: infinite loops"
								+ " are not supported! Change '>=' to '<=' to prevent that.",
						s);
			}

			if (forD.getInc() < 0
					&& Operator.LESS_EQUALS.equals(forD.getOperation())) {
				throwErrorMessage(
						"In for-loop: infinite loops"
								+ " are not supported! Change '<=' to '>=' to prevent that.",
						s);
			}
		}

	}

	private void checkLoopCondition(SimpleForDeclaration obj,
			Expression expression) {
		if (expression instanceof BinaryExpression) {
			BinaryExpression expr = (BinaryExpression) expression;
			if (!(expr.getLeftExpression() instanceof VariableExpression)) {
				throwErrorMessage(
						"In for-loop: only binary"
								+ " expressions of the form 'a <= b'/'a >= b' with a, b being"
								+ " constant integers are supported!",
						expr.getLeftExpression());
			}

			int adjust = 0;
			switch (expr.getOperation().getText()) {
			case "<=":
			case ">=":
			case "==":
				break;
			case ">":
				adjust = +1;
				break;
			case "<":
				adjust = -1;
				break;
			default:
				throwErrorMessage(
						"In for-loop: only binary"
								+ " expressions of the form 'a <= b' or 'a >= b' with a, b being"
								+ " constant integers are supported!", expr);
			}

			if (!(expr.getRightExpression() instanceof ConstantExpression)) {
				throwErrorMessage(
						"In for-loop: only binary"
								+ " expressions of the form 'a <= b' or 'a >= b' with a, b being"
								+ " constant integers are supported!",
						expr.getRightExpression());
			}

			ConstantExpression ce = (ConstantExpression) expr
					.getRightExpression();
			((SimpleForDeclaration_Impl) obj).setTo(((int) ce.getValue())
					+ adjust);
		} else {
			// TODO
		}
	}

	private void checkDeclaration(SimpleForDeclaration forD,
			Expression expression) {
		if (expression instanceof DeclarationExpression) {
			DeclarationExpression s = (DeclarationExpression) expression;
			String varType = s.getVariableExpression().getType()
					.getNameWithoutPackage();
			String varName = s.getVariableExpression().getAccessedVariable()
					.getName();

			if (!(Objects.equal(varType, "int") || Objects.equal(varType,
					"Integer"))) {
				throwErrorMessage("In for-loop: variable '" + varName
						+ "' must be of type integer!",
						s.getVariableExpression());
			}

			((SimpleForDeclaration_Impl) forD).setVarName(s
					.getVariableExpression().getName(), setCodeRange(s));

			if (!(s.getRightExpression() instanceof ConstantExpression)) {
				throwErrorMessage("In for-loop: variable '" + forD.getVarName()
						+ "' must be initialized with an integer constant!", s);
			}

			ConstantExpression ce = (ConstantExpression) s.getRightExpression();

			if (!(ce.getValue() instanceof Integer)) {
				throwErrorMessage("In for-loop: variable '" + forD.getVarName()
						+ "' must be initialized with an integer constant!", s);
			}

			((SimpleForDeclaration_Impl) forD).setFrom((Integer) ce.getValue());
		} else {
			// TODO
		}

	}

	@Override
	public Class<ForStatement> getAcceptedType() {
		return ForStatement.class;
	}

	@Override
	public Class<ControlFlowScope> getParentType() {
		return ControlFlowScope.class;
	}
}
