package eu.mihosoft.vrl.instrumentation.composites;

import java.util.Stack;

import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.instrumentation.TransformContext;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.ControlFlowStatement;
import eu.mihosoft.vrl.lang.model.SimpleForDeclaration;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class ForLoopPart
		extends
		AbstractCodeBuilderPart<ForStatement, SimpleForDeclaration, ControlFlowScope> {

	public ForLoopPart(StateMachine stateMachine, SourceUnit sourceUnit,
			VisualCodeBuilder builder, CodeLineColumnMapper mapper) {
		super(stateMachine, sourceUnit, builder, mapper);
	}

	@Override
	public SimpleForDeclaration transform(
			ForStatement s, 
			ControlFlowScope currentScope, TransformContext context) {
		System.out.println(" --> FOR-LOOP: " + s.getVariable());

		// predeclaration, ranges will be defined later
		SimpleForDeclaration decl = builder.invokeForLoop(
				currentScope, null, 0, 0, 0);
		setCodeRange(currentScope, s);
		addCommentsToScope(currentScope, comments);
		stateMachine.push("for-loop", true);
		return decl;
	}
	
	@Override
	public void postTransform(SimpleForDeclaration obj, ForStatement s, ControlFlowScope scope, TransformContext context) {
		
		if (!stateMachine.getBoolean("for-loop:declaration")) {
			throwErrorMessage("For loop must contain a variable declaration "
					+ "such as 'int i=0'!", s.getVariable());
		}

		if (!stateMachine.getBoolean("for-loop:compareExpression")) {
			throwErrorMessage(
					"for-loop: must contain binary"
							+ " expressions of the form 'a <= b'/'a >= b' with a, b being"
							+ " constant integers!", s);
		}

		if (!stateMachine.getBoolean("for-loop:incExpression")) {
			throwErrorMessage("for-loop: must contain binary"
					+ " expressions of the form 'i+=a'/'i-=a' with i being"
					+ " an integer variable and a being a constant integer!", s);
		}

		stateMachine.pop();
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
