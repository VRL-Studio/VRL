/* 
 * Variable.java
 *
 * Copyright (c) 2009–2014 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2014 by Michael Hoffer
 * 
 * This file is part of Visual Reflection Library (VRL).
 *
 * VRL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * see: http://opensource.org/licenses/LGPL-3.0
 *      file://path/to/VRL/src/eu/mihosoft/vrl/resources/license/lgplv3.txt
 *
 * VRL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * This version of VRL includes copyright notice and attribution requirements.
 * According to the LGPL this information must be displayed even if you modify
 * the source code of VRL. Neither the VRL Canvas attribution icon nor any
 * copyright statement/attribution may be removed.
 *
 * Attribution Requirements:
 *
 * If you create derived work you must do three things regarding copyright
 * notice and author attribution.
 *
 * First, the following text must be displayed on the Canvas or an equivalent location:
 * "based on VRL source code".
 * 
 * Second, the copyright notice must remain. It must be reproduced in any
 * program that uses VRL.
 *
 * Third, add an additional notice, stating that you modified VRL. In addition
 * you must cite the publications listed below. A suitable notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Publications:
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, in press.
 */
package eu.mihosoft.vrl.lang.model;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface Variable {

    public String getName();

    public IType getType();

    public Object getValue();

    public boolean isConstant();

    public Scope getScope();

    public void setValue(Object value);

    public void setConstant(boolean b);
    
    public Variable setModifiers(IModifiers modifiers);
    
    public Optional<IModifiers> getModifiers();
    
    public boolean isField();

//    public boolean isReturnValue();
    
    public DeclarationInvocation getDeclaration();

//    public Optional<Invocation> getInvocation();
    
}

class VariableImpl implements Variable {

    private final Scope scope;
    private final IType type;
    private final String varName;
    private Object value;
    private boolean constant;
//    private Invocation invocation;
    private DeclarationInvocation invocation;
    private IModifiers modifiers;

    VariableImpl(Scope scope, IType type, String varName, Object value, boolean constant, DeclarationInvocation invocation) {
        this.scope = scope;
        this.type = type;
        this.varName = varName;
        this.value = value;
        this.constant = constant;
        this.invocation = invocation;
    }

//    // static var initializer
//    private VariableImpl(Scope scope, IType type, DeclarationInvocation invocation) {
//        this.scope = scope;
//        this.type = type;
//        this.varName = type.getFullClassName();
//        
//        
//    }

    VariableImpl(Scope scope, String varName, DeclarationInvocation invocation) {
        this.scope = scope;
        this.type = invocation.getReturnType();
//        this.invocation = invocation;
        this.varName = varName;
        this.invocation = invocation;
    }
    
    @Override
    public Variable setModifiers(IModifiers modifiers) {
        this.modifiers = modifiers;
        return this;
    }

//    public static VariableImpl createStaticVar(Scope scope, IType type, DeclarationInvocation invocation) {
//        return new VariableImpl(scope, type, invocation);
//    }

    @Override
    public String getName() {
        return varName;
    }

    @Override
    public IType getType() {
        return type;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public boolean isConstant() {
        return constant;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return "[ const=" + constant + ", type=" + type + ", name=" + varName + ", val=" + value + " ]";
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public void setConstant(boolean b) {
        this.constant = b;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VariableImpl other = (VariableImpl) obj;
        if (!Objects.equals(this.scope, other.scope)) {
            return false;
        }
        if (!Objects.equals(this.varName, other.varName)) {
            return false;
        }
        
        if (!Objects.equals(this.invocation, other.invocation)) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.scope);
        hash = 83 * hash + Objects.hashCode(this.varName);
        return hash;
    }

//    /**
//     * @return the invocation
//     */
//    @Override
//    public Optional<Invocation> getInvocation() {
//        return Optional.ofNullable(invocation);
//    }
//
//    @Override
//    public boolean isReturnValue() {
//        return invocation!=null;
//    }

    @Override
    public DeclarationInvocation getDeclaration() {
        return invocation;
    }

    /**
     * @param invocation the invocation to set
     */
    public void setDeclaration(DeclarationInvocation invocation) {
        this.invocation = invocation;
    }

    @Override
    public Optional<IModifiers> getModifiers() {
//        if (modifiers == null) {
//            modifiers = new Modifiers();
//        }
        
        return Optional.ofNullable(modifiers);
    }

    @Override
    public boolean isField() {
        return modifiers !=null;
    }

}
