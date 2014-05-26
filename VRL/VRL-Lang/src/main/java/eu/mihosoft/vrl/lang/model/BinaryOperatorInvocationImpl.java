/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

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
        } else if(booleanOperator(operator)) {
            if(leftArg.getArgType()==ArgumentType.VARIABLE
                &&  rightArg.getArgType()==ArgumentType.VARIABLE  ) {
                
                // check that leftArg and rightArg == const or var
                
            }
        }

        setReturnType(retType);
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

}
