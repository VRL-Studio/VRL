/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface ScopeInvocation extends Invocation{

    /**
     * @return the scope
     */
    Scope getScope();

    
}
