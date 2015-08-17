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
class ArgumentValue {
    private int argIndex;
    private Argument argument;

    public ArgumentValue(int argIndex, Argument argument) {
        this.argIndex = argIndex;
        this.argument = argument;
    }

    /**
     * @return the argIndex
     */
    public int getArgIndex() {
        return argIndex;
    }

    /**
     * @return the argument
     */
    public Argument getArgument() {
        return argument;
    }
    
    
}
