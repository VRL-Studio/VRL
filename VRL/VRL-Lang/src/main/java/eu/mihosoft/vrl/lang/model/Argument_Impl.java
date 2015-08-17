/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

import java.util.Optional;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
final class Argument_Impl implements Argument {

    private final ArgumentType argType;
    private final Variable variable;
    private final Object constant;
    private final Invocation invocation;
    private final IType constType;

    public static final Argument NULL = new Argument_Impl(ArgumentType.NULL, null, null, null, null);

    Argument_Impl(ArgumentType argType, Variable variable, Object constant, IType constType, Invocation invocation) {
        this.argType = argType;
        this.variable = variable;
        this.constant = constant;
        this.constType = constType;
        this.invocation = invocation;
    }

    @Override
    public ArgumentType getArgType() {
        return this.argType;
    }

    @Override
    public Optional<Variable> getVariable() {
        return Optional.ofNullable(variable);
    }

    @Override
    public Optional<Invocation> getInvocation() {
        return Optional.ofNullable(invocation);
    }

    @Override
    public Optional<Object> getConstant() {
        return Optional.ofNullable(constant);
    }

    @Override
    public IType getType() {
        if (getArgType() == ArgumentType.CONSTANT) {
            return constType;
        } else if (getArgType() == ArgumentType.VARIABLE) {
            return getVariable().get().getType();
        } else if (getArgType() == ArgumentType.INVOCATION) {
            return getInvocation().get().getReturnType();
        }
        return Type.VOID;
    }

    @Override
    public String toString() {
        String valueString = ", val='"+getConstant().orElse("?").toString()+"'";
        return "[Argument: argType=" + getArgType() + ", type=" + getType() + valueString + "]";
    }

}
