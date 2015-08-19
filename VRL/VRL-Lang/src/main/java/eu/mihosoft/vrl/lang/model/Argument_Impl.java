/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

import java.util.Objects;
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


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.argType);
        hash = 97 * hash + Objects.hashCode(this.variable);
        hash = 97 * hash + Objects.hashCode(this.constant);
        hash = 97 * hash + Objects.hashCode(this.invocation);
        hash = 97 * hash + Objects.hashCode(this.constType);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Argument_Impl other = (Argument_Impl) obj;
        if (this.argType != other.argType) {
            return false;
        }
        if (!Objects.equals(this.variable, other.variable)) {
            return false;
        }
        if (!Objects.equals(this.constant, other.constant)) {
            return false;
        }
        if (!Objects.equals(this.invocation, other.invocation)) {
            return false;
        }
        if (!Objects.equals(this.constType, other.constType)) {
            return false;
        }
        return true;
    }
    
    

}
