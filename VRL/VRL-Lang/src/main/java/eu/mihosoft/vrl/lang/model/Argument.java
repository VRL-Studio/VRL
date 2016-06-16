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
public final class Argument extends CodeEntityImpl implements IArgument {

    private final ArgumentType argType;
    private final Variable variable;
    private final ConstantValue constant;
    private final Invocation invocation;
    private  IType constType;// final

    public static final IArgument NULL = new Argument(ArgumentType.NULL, null, null, null, null);

    private Argument(ArgumentType argType, Variable variable, ConstantValue constant, IType constType, Invocation invocation) {
        this.argType = argType;
        this.variable = variable;
        this.constant = constant;
        this.constType = constType;
        this.invocation = invocation;
    }

    public static IArgument constArg(ConstantValue constant) {
        IArgument result = new Argument(ArgumentType.CONSTANT, null, constant, null, null);

        return result;
    }

    public static IArgument varArg(Variable v) {
        IArgument result = new Argument(ArgumentType.VARIABLE, v, null, null, null);
        return result;
    }

    public static IArgument invArg(Invocation i) {
        IArgument result = new Argument(ArgumentType.INVOCATION, null, null, null, i);

        return result;
    }

    public static IArgument nullArg() {
        return NULL;
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
    public Optional<ConstantValue> getConstant() {
        return Optional.ofNullable(constant);
    }

    @Override
    public IType getType() {
        if (getArgType() == ArgumentType.CONSTANT) {
            return getConstant().get().getType();
        } else if (getArgType() == ArgumentType.VARIABLE) {
            return getVariable().get().getType();
        } else if (getArgType() == ArgumentType.INVOCATION) {
            return getInvocation().get().getReturnType();
        }
        return Type.VOID;
    }

    @Override
    public String toString() {
        return "[Argument: argType=" + getArgType() + ", type=" + getType() + "]";
    }

    @Override
    public void setConstType(IType type) {
        this.constType = type;
    }

}
