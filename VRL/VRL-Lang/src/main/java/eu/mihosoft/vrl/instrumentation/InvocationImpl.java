/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.instrumentation;

import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.ICodeRange;
import eu.mihosoft.vrl.lang.model.Invocation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class InvocationImpl implements Invocation {

    private String id;
    private final String varName;
    private final String methodName;
    private final String returnValueName;
    private final List<Variable> arguments = new ArrayList<>();
    private final boolean constructor;
    private final boolean Void;
//    private String code;
    private final Scope parent;
    private boolean Static;
    private ICodeRange location;

    public InvocationImpl(
            Scope parent,
            String id,
            String varName, String methodName,
            boolean constructor, boolean isVoid, boolean isStatic, String retValName, Variable... args) {
        this.parent = parent;
        this.id = id;
        this.varName = varName;
        this.methodName = methodName;
        this.constructor = constructor;
        this.Void = isVoid;
        this.returnValueName = retValName;
        this.Static = isStatic;

        arguments.addAll(Arrays.asList(args));

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
    public String getReturnValueName() {
        return returnValueName;
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

        result += "constructor=" + constructor + ", var=" + varName + ", mName=" + methodName + ", retValName=" + returnValueName + ", args=[";

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
    public ICodeRange getRange() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setRange(ICodeRange location) {
        this.location = location;
    }

}
