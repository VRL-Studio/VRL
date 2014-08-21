/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

public class DeclarationInvocationImpl extends InvocationImpl implements DeclarationInvocation {

    private final Variable declaredVariable;
    
    public DeclarationInvocationImpl(Scope parent, IType type, String varName) {
        super(parent, "", null, "declare " + varName, type, false, false, true, new IArgument[0]);
        this.declaredVariable = parent.createVariable(type, varName);
        getNode().setTitle("declare " + varName);
    }
    
    public DeclarationInvocationImpl(Scope parent, Variable declaredVariable) {
        super(parent, "", null, "declare " + declaredVariable.getName(), declaredVariable.getType(), false, false, true, new IArgument[0]);
        this.declaredVariable = declaredVariable;
        getNode().setTitle("declare " + declaredVariable.getName());
    }

    @Override
    public Variable getDeclaredVariable() {
        return this.declaredVariable;
    }
}
