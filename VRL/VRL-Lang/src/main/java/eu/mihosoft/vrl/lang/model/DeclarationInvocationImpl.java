/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

public class DeclarationInvocationImpl extends InvocationImpl implements DeclarationInvocation {

    private Variable declaredVariable;//final
    
    public DeclarationInvocationImpl(String id, Scope parent, IType type, String varName) {
        super(parent, id, ObjectProvider.empty(), "declare " + varName, type, false, true, new Argument[0]);
        this.declaredVariable = parent.createVariable(type, varName);
        getNode().setTitle("declare " + varName);
    }
    
    public DeclarationInvocationImpl(String id, Scope parent, Variable declaredVariable) {
        super(parent, id, ObjectProvider.empty(), "declare " + declaredVariable.getName(), declaredVariable.getType(), false, true, new Argument[0]);
        this.declaredVariable = declaredVariable;
        getNode().setTitle("declare " + declaredVariable.getName());
    }

    @Override
    public Variable getDeclaredVariable() {
        return this.declaredVariable;
    }
     @Override
    public void setDeclaredVariable(Variable variable) { // added  Joanna
        declaredVariable = variable;
    }
}
