/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

import java.util.Objects;

/**
 *
 * @author miho
 */
public class BinaryOperatorInvocationImpl extends InvocationImpl implements BinaryOperatorInvocation {

    private final IArgument leftArg;
    private final IArgument rightArg;
    private final Operator operator;

    public BinaryOperatorInvocationImpl(Scope parent, IArgument leftArg, IArgument rightArg, Operator operator) {

        super(parent, "", null, "op ", Type.VOID, false, false, true, new IArgument[]{leftArg, rightArg});

        this.leftArg = leftArg;
        this.rightArg = rightArg;
        this.operator = operator;

        IType retType = Type.VOID;

        if (assignmentOperator(operator)) {
            if (leftArg.getArgType() != ArgumentType.VARIABLE) {
                throw new IllegalArgumentException("Left Argument must be a variable!");
            } else {
                retType = leftArg.getVariable().get().getType();
            }
        } else if (booleanOperator(operator)) {
            if (leftArg.getArgType() == ArgumentType.VARIABLE
                    && rightArg.getArgType() == ArgumentType.VARIABLE) {

                // check that leftArg and rightArg == const or var
            }
        }

        setReturnType(retType);
    }

    private boolean pureAssignmentOperator(Operator operator) {
        return operator == Operator.ASSIGN;
    }

    private boolean assignmentOperator(Operator operator) {
        return operator == Operator.ASSIGN
                || operator == Operator.PLUS_ASSIGN
                || operator == Operator.MINUS_ASSIGN
                || operator == Operator.TIMES_ASSIGN
                || operator == Operator.INC_ONE
                || operator == Operator.DEC_ONE;
    }

    private boolean booleanOperator(Operator operator) {
        return operator == Operator.EQUALS
                || operator == Operator.NOT_EQUALS
                || operator == Operator.LESS_EQUALS
                || operator == Operator.GREATER_EQUALS
                || operator == Operator.LESS
                || operator == Operator.GREATER_EQUALS
                || operator == Operator.OR
                || operator == Operator.AND;
    }

    private boolean basicArithmeticOperator(Operator operator) {
        return operator == Operator.PLUS
                || operator == Operator.MINUS
                || operator == Operator.TIMES
                || operator == Operator.DIV;
    }

    private void validateInputs(Operator operator, IArgument leftArg, IArgument rightArg) {

        boolean isVariableL = leftArg.getArgType() == ArgumentType.VARIABLE;
        boolean isVariableR = rightArg.getArgType() == ArgumentType.VARIABLE;

        boolean isAssignmentOperator = assignmentOperator(operator);
        boolean isPureAssignmentOperator = pureAssignmentOperator(operator);
        boolean isBooleanOperator = booleanOperator(operator);
        boolean isBasicArithmeticOperator = basicArithmeticOperator(operator);

        boolean isNumberL = number(leftArg);
        boolean isNumberR = number(rightArg);

        if (isAssignmentOperator) {
            if (!isVariableL) {
                throw new IllegalArgumentException("Left argument must be a variable!");
            }
//            if (isPureAssignmentOperator) {
//                if (rightArg.getArgType() != ArgumentType.VARIABLE) {
//                    throw new IllegalArgumentException("Right argument must be a variable!");
//                }
//            }
        }
//        else if (booleanOperator(operator)) {
//            if (leftArg.getArgType() == ArgumentType.VARIABLE
//                    && rightArg.getArgType() == ArgumentType.VARIABLE) {
//
//                // check that leftArg and rightArg == const or var
//            }
//        } else if (basicArithmeticOperator(operator)) {
//            if (!leftArg.getArgType().) {
//                
//            }
//        }
    }

    @Override
    public IArgument getLeftArgument() {
        return this.leftArg;
    }

    @Override
    public IArgument getRightArgument() {
        return this.rightArg;
    }

    @Override
    public Operator getOperator() {
        return this.operator;
    }

    private boolean number(IArgument leftArg) {
        return Objects.equals(leftArg.getType(),Type.INT)
                || Objects.equals(leftArg.getType(),Type.LONG)
                || Objects.equals(leftArg.getType(),Type.SHORT)
                || Objects.equals(leftArg.getType(),Type.FLOAT)
                || Objects.equals(leftArg.getType(),Type.DOUBLE);
    }

}
