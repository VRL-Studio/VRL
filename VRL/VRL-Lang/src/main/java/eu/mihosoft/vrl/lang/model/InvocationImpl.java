/* 
 * InvocationImpl.java
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

import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.ICodeRange;
import eu.mihosoft.vrl.lang.model.Invocation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class InvocationImpl implements Invocation {

    private String id;
    private final String varName;
    private final String methodName;
    private final List<Variable> arguments = new ArrayList<>();
    private final boolean constructor;
    private final boolean Void;
//    private String code;
    private final Scope parent;
    private boolean Static;
    private ICodeRange location;
    private final IType returnType;
    private final Variable returnValue;

    public InvocationImpl(
            Scope parent,
            String id,
            String varName, String methodName, IType returnType,
            boolean constructor, boolean isVoid, boolean isStatic, Variable... args) {
        this.parent = parent;
        this.id = id;
        this.varName = varName;
        this.methodName = methodName;
        this.constructor = constructor;
        this.Void = isVoid;
        
        this.Static = isStatic;
        this.returnType = returnType;

        arguments.addAll(Arrays.asList(args));
        
        if (isVoid) {
            returnValue = null;
        } else {
            returnValue = parent.createVariable(this);
        }

        Variable var = null;
        
        try{
            var = parent.getVariable(varName);
        } catch(IllegalArgumentException ex) {
            // will be checked later (see if below)
        }

        if (!isStatic && !isScope() && var == null) {
            
            throw new IllegalArgumentException(
                    "Variable '"
                    + varName
                    + "' does not exist in scope '" + parent.getName() + "'!");
        } else if (varName != null) {
            // check whether varName is a valid type
            Type type = new Type(varName);
        }
    }

    @Override
    public String getVariableName() {
        return varName;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public List<Variable> getArguments() {
        return arguments;
    }

    @Override
    public boolean isConstructor() {
        return constructor;
    }

    @Override
    public boolean isVoid() {
        return Void;
    }

    @Override
    public String toString() {

        String result = "[ ";

        if (this instanceof ScopeInvocationImpl) {
            ScopeInvocationImpl scopeInvocation = (ScopeInvocationImpl) this;
            result += "scopeType: " + scopeInvocation.getScope().getType() + ", ";
        }

        result += "constructor=" + constructor + ", var=" + varName + ", mName=" + methodName + ", retVal=" + returnValue + ", args=[";

        for (Variable a : arguments) {
            result += a + ", ";
        }

        result += "]";

        return result;
    }

    /**
     * @return the id
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean isScope() {
        return false;
    }

//    /**
//     * @return the code
//     */
//    @Override
//    public String getCode() {
//        return code;
//    }
//
//    /**
//     * @param code the code to set
//     */
//    @Override
//    public void setCode(String code) {
//        this.code = code;
//    }

    /**
     * @return the Static
     */
    @Override
    public boolean isStatic() {
        return Static;
    }

    /**
     * @param Static the Static to set
     */
    public void setStatic(boolean Static) {
        this.Static = Static;
    }

    /**
     * @return the location
     */
    @Override
    public ICodeRange getRange() {
        return this.location;
    }

    /**
     * @param location the location to set
     */
    @Override
    public void setRange(ICodeRange location) {
        this.location = location;
    }

    /**
     * @return the parent
     */
    @Override
    public Scope getParent() {
        return this.parent;
    }

    @Override
    public Optional<Variable> getReturnValue() {
        return Optional.of(returnValue);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InvocationImpl other = (InvocationImpl) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.varName, other.varName)) {
            return false;
        }
        if (!Objects.equals(this.methodName, other.methodName)) {
            return false;
        }
        if (!Objects.equals(this.arguments, other.arguments)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.id);
        hash = 23 * hash + Objects.hashCode(this.varName);
        hash = 23 * hash + Objects.hashCode(this.methodName);
        hash = 23 * hash + Objects.hashCode(this.arguments);
        return hash;
    }

    /**
     * @return the returnType
     */
    public IType getReturnType() {
        return returnType;
    }

    
   

}
