package eu.mihosoft.vrl.instrumentation.composites;

import java.util.Stack;

import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.control.SourceUnit;

import com.google.common.base.Objects;

import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.DeclarationInvocation;
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
	public DeclarationInvocation transform(Stack<Object> stackIn,
			DeclarationExpression s, Stack<Object> stackOut,
			ControlFlowScope currentScope) {
		if (currentScope instanceof SimpleForDeclaration_Impl
                && !stateMachine.getBoolean("for-loop:declaration")) {

			// TODO hmm, returns null in this case, but an DeclarationInvocation else...
            SimpleForDeclaration_Impl forD = (SimpleForDeclaration_Impl) currentScope;

            if (!stateMachine.getBoolean("for-loop:declaration")) {

                String varType = s.getVariableExpression().getType().getNameWithoutPackage();
                String varName = s.getVariableExpression().getAccessedVariable().getName();

                if (!(Objects.equal(varType, "int") || Objects.equal(varType, "Integer"))) {
                    throwErrorMessage("In for-loop: variable '" + varName
                            + "' must be of type integer!", s.getVariableExpression());
                }

                forD.setVarName(s.getVariableExpression().getName(), setCodeRange(s));

                if (!(s.getRightExpression() instanceof ConstantExpression)) {
                    throwErrorMessage("In for-loop: variable '" + forD.getVarName()
                            + "' must be initialized with an integer constant!", s);
                }

                ConstantExpression ce = (ConstantExpression) s.getRightExpression();

                if (!(ce.getValue() instanceof Integer)) {
                    throwErrorMessage("In for-loop: variable '" + forD.getVarName()
                            + "' must be initialized with an integer constant!", s);
                }

                forD.setFrom((Integer) ce.getValue());

                stateMachine.setBoolean("for-loop:declaration", true);
            }
            
            return null;

        } else {

            stateMachine.setBoolean("variable-declaration", true);

            DeclarationInvocation declInv
                    = builder.declareVariable(currentScope,
                            new Type(s.getVariableExpression().getType().getName(), true),
                            s.getVariableExpression().getName());

            setCodeRange(declInv, s);

            stateMachine.setBoolean("variable-declaration", false);

            return declInv;
        }
	}

	@Override
	public Class<DeclarationExpression> getAcceptedType() {
		return DeclarationExpression.class;
	}

	@Override
	public Class<ControlFlowScope> getParentType() {
		return ControlFlowScope.class;
	}

	@Override
	public boolean accepts(Stack<Object> stackIn, DeclarationExpression obj,
			Stack<Object> stackOut, ControlFlowScope parent) {
		return true;
	}

}
