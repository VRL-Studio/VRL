/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VariableFactory {

    public static Variable createObjectVariable(Scope scope, Type type, String varName) {
        return new VariableImpl(scope, type, varName, null, false);
    }

    public static Variable createConstantVariable(Scope scope, Type type, String varName, Object constant) {
        return new VariableImpl(scope, type, varName, constant, true);
    }
}
