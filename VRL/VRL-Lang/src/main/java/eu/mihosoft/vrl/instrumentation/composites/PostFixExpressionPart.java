package eu.mihosoft.vrl.instrumentation.composites;

import java.util.Stack;

import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.SimpleForDeclaration;
import eu.mihosoft.vrl.lang.model.SimpleForDeclaration_Impl;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;


public class PostFixExpressionPart extends
		AbstractCodeBuilderPart<PostfixExpression, CodeEntity, SimpleForDeclaration> {

	public PostFixExpressionPart(StateMachine stateMachine, SourceUnit sourceUnit,
			VisualCodeBuilder builder, CodeLineColumnMapper mapper) {
		super(stateMachine, sourceUnit, builder, mapper);
	}

	@Override
	public CodeEntity transform(Stack<Object> stackIn, PostfixExpression obj,
			Stack<Object> stackOut, SimpleForDeclaration parent) {
		
		SimpleForDeclaration_Impl ARRGGGH = (SimpleForDeclaration_Impl) parent;
        if ("++".equals(obj.getOperation().getText())) {
            ARRGGGH.setInc(1);
        } else if ("--".equals(obj.getOperation().getText())) {
        	ARRGGGH.setInc(-1);
        }

        if (ARRGGGH.getInc() > 0 && ">=".
                equals("stateMachine.getString(\"for-loop:compareOperation")) {
//            throw new IllegalStateException("In for-loop: infinite loops"
//                    + " are not supported! Change '>=' to '<=' to prevent that."
//            );
            throwErrorMessage("In for-loop: infinite loops"
                    + " are not supported! Change '>=' to '<=' to prevent that.", obj
            );
        }

        if (ARRGGGH.getInc() < 0 && "<=".
                equals("stateMachine.getString(\"for-loop:compareOperation")) {
//            throw new IllegalStateException("In for-loop: infinite loops"
//                    + " are not supported! Change '<=' to '>=' to prevent that."
//            );
            throwErrorMessage("In for-loop: infinite loops"
                    + " are not supported! Change '<=' to '>=' to prevent that.", obj);
        }
		// TODO: Pure Validation!
		return null;
	}

	@Override
	public Class<PostfixExpression> getAcceptedType() {
	
		return PostfixExpression.class;
	}

	@Override
	public Class<SimpleForDeclaration> getParentType() {		
		return SimpleForDeclaration.class;
	}

	@Override
	public boolean accepts(Stack<Object> stackIn, PostfixExpression obj,
			Stack<Object> stackOut, SimpleForDeclaration parent) {
		return true;
	}

}
