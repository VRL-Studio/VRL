package eu.mihosoft.vrl.instrumentation.composites;

import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.ElseIfDeclaration;
import eu.mihosoft.vrl.lang.model.IArgument;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class IfStatementPart
		extends
		AbstractCodeBuilderPart<IfStatement, ElseIfDeclaration, ControlFlowScope> {

	public IfStatementPart(StateMachine stateMachine, SourceUnit sourceUnit,
			VisualCodeBuilder builder, CodeLineColumnMapper mapper) {
		super(stateMachine, sourceUnit, builder, mapper);
	}

	@Override
	public ElseIfDeclaration transform(IfStatement s,
			ControlFlowScope currentScope, TransformContext context) {
		boolean isElseIf = stateMachine.getBoolean("else-statement:else-is-if");

		stateMachine.push("if-statement", true);

		ElseIfDeclaration decl = null;

		if (s.getBooleanExpression().getExpression() == null) {
			throwErrorMessage("if-statement: must contain boolean"
					+ " expression!", s.getBooleanExpression());
		}

		if (!(currentScope instanceof ControlFlowScope)) {
			throwErrorMessage(
					"If-Statement can only be invoked inside ControlFlowScopes!",
					s);
		}

		if (isElseIf) {
			decl = builder.invokeElseIf((ControlFlowScope) currentScope,
					context.resolve("elseClause", s.getBooleanExpression()
							.getExpression(), IArgument.class));

		} else {
			// TODO decl = builder.invokeIf((ControlFlowScope) currentScope,
			// convertExpressionToArgument(
			// s.getBooleanExpression().getExpression(), currentScope));
		}

		setCodeRange(currentScope, s);
		addCommentsToScope(currentScope, comments);

		Statement elseBlock = s.getElseBlock();
		if (elseBlock instanceof EmptyStatement) {
			// dispatching to EmptyStatement will not call back visitor,
			// must call our visitEmptyStatement explicitly

			// TODO visitEmptyStatement is noop
			// visitEmptyStatement((EmptyStatement) elseBlock);
		} else {

			stateMachine.push("else-statement", true);

			boolean elseIsIf = (elseBlock instanceof IfStatement);

			stateMachine.setBoolean("else-statement:else-is-if", elseIsIf);

			if (elseIsIf) {
				// TODO visitIfElse((IfStatement) elseBlock);
			} else {
				// TODO decl = builder.invokeElse((ControlFlowScope)
				// currentScope);
				setCodeRange(currentScope, s);
				addCommentsToScope(currentScope, comments);
			}

			stateMachine.pop();
		}

		stateMachine.pop();

		return decl;

	}

	@Override
	public Class<IfStatement> getAcceptedType() {
		return IfStatement.class;
	}

	@Override
	public Class<ControlFlowScope> getParentType() {
		return ControlFlowScope.class;
	}
}
