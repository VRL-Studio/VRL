/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.lang.model;

/**
 *
 * @author miho
 */
public interface BinaryOperatorInvocation extends Invocation{
    IArgument getLeftArgument();  
    IArgument getRightArgument();
    Operator getOperator();
}
