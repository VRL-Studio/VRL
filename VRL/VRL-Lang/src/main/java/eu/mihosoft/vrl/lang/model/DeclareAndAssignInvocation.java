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
public interface DeclareAndAssignInvocation extends BinaryOperatorInvocation, DeclarationInvocation {

    @Override
    public default boolean isArrayAccessOperator() {
        return false;
    }

    @Override
    public default Operator getOperator() {
        return Operator.ASSIGN;
    }

    @Override
    public default void setOperator(Operator op) {
        throw new UnsupportedOperationException(
                "DeclareAndAssignOP does not support custom operators.");
    }
    
    

}
