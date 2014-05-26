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
public enum Operator {
    
    /**
     * Basic arithmetic operations.
     */
    PLUS,
    MINUS,
    TIMES,
    DIV,
    
    /**
     * Assignments.
     */
    ASSIGN,
    PLUS_ASSIGN,
    MINUS_ASSIGN,
    TIMES_ASSIGN,
    DIV_ASSIGN,
    INC_ONE,
    DEC_ONE,
    
    /**
     * Boolean operations.
     */
    EQUALS,
    NOT_EQUALS,
    LESS,
    LESS_EQUALS,
    GREATER,
    GREATER_EQUALS,
    OR,
    AND
    
}
