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
public interface ObjectProvider {

    Optional<String> getVariableName();

    Optional<Invocation> getInvocation();

    Optional<IType> getClassObject();
    
    Optional<IType> getType();

    default boolean isEmpty() {
        return !getVariableName().isPresent() && !getInvocation().isPresent();
    }

    static ObjectProvider fromVariable(String varName, IType type) {
        return new ObjectProvider_Impl(varName, type);
    }

    static ObjectProvider fromInvocation(Invocation inv) {
        return new ObjectProvider_Impl(inv);
    }

    static ObjectProvider fromClassObject(IType type) {
        return new ObjectProvider_Impl(type);
    }

    static ObjectProvider empty() {
        return ObjectProvider_Impl.EMPTY;
    }

}

class ObjectProvider_Impl implements ObjectProvider {

    private final Optional<String> variableName;
    private final Optional<IType> variableType;
    private final Optional<Invocation> invocation;
    private final Optional<IType> classObject;
    
    static final ObjectProvider EMPTY = new ObjectProvider_Impl();

    public ObjectProvider_Impl() {
        this.variableName = Optional.empty();
        this.variableType = Optional.empty();
        this.invocation = Optional.empty();
        this.classObject = Optional.empty();
    }

    public ObjectProvider_Impl(String varName, IType type) {
        this.variableName = Optional.of(varName);
        this.variableType = Optional.of(type);
        this.invocation = Optional.empty();
        this.classObject = Optional.empty();
    }

    public ObjectProvider_Impl(Invocation invocation) {
        this.invocation = Optional.of(invocation);
        this.variableName = Optional.empty();
        this.variableType = Optional.empty();
        this.classObject = Optional.empty();
    }

    public ObjectProvider_Impl(IType type) {
        this.invocation = Optional.empty();
        this.variableName = Optional.empty();
        this.variableType = Optional.empty();
        this.classObject = Optional.of(type);
    }

    /**
     * @return the variable
     */
    @Override
    public Optional<String> getVariableName() {
        return variableName;
    }

    /**
     * @return the invocation
     */
    @Override
    public Optional<Invocation> getInvocation() {
        return invocation;
    }

    @Override
    public String toString() {
        return getVariableName().
                orElse(getInvocation().
                        map(inv -> inv.getMethodName()).orElse(""));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ObjectProvider_Impl other = (ObjectProvider_Impl) obj;
        if (!Objects.equals(this.variableName, other.variableName)) {
            return false;
        }
        if (!Objects.equals(this.invocation, other.invocation)) {
            return false;
        }
        if (!Objects.equals(this.classObject, other.classObject)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.variableName);
        hash = 89 * hash + Objects.hashCode(this.invocation);
        hash = 89 * hash + Objects.hashCode(this.classObject);
        return hash;
    }

    @Override
    public Optional<IType> getClassObject() {
        return classObject;
    }

    @Override
    public Optional<IType> getType() {
        return Optional.ofNullable(getClassObject().
                orElse(getInvocation().map(inv->inv.getReturnType()).
                        orElse(variableType.orElse(null))));
    }

}
