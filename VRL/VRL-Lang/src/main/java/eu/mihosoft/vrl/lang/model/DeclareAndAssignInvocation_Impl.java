/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class DeclareAndAssignInvocation_Impl extends BinaryOperatorInvocationImpl implements DeclareAndAssignInvocation {

    private DeclarationInvocationImpl declInv;
    
    public DeclareAndAssignInvocation_Impl(String id, Scope parent, IType varType, String varName, Argument assignmentArg) {
        super(id, parent, Argument.constArg(Type.STRING, varName), assignmentArg, Operator.ASSIGN);
        declInv = new DeclarationInvocationImpl(id, parent, varType, varName);
        setReturnType(Type.VOID);
        getNode().setTitle("decl&assign");
    }
    
        public DeclareAndAssignInvocation_Impl(String id, Scope parent, Variable var, Argument assignmentArg) {
        super(id, parent, Argument.constArg(Type.STRING, var.getName()), assignmentArg, Operator.ASSIGN);
        declInv = new DeclarationInvocationImpl(id, parent, var);
        setReturnType(Type.VOID);
        getNode().setTitle("decl&assign");
    }

    @Override
    public void setOperator(Operator op) {
        throw new UnsupportedOperationException(
                "DeclareAndAssignOP does not support custom operators.");
    }

    @Override
    public Variable getDeclaredVariable() {
        return declInv.getDeclaredVariable();
    }

     @Override
    public void setDeclaredVariable(Variable variable) { // added  Joanna
        declInv.setDeclaredVariable(variable);
    }

}
