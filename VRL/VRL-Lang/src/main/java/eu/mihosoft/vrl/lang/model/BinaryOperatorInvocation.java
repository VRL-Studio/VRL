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
public interface BinaryOperatorInvocation extends Invocation{
    Argument getLeftArgument();  
    Argument getRightArgument();

    Operator getOperator();

    void setOperator(Operator op);

    boolean isArrayAccessOperator();
}
