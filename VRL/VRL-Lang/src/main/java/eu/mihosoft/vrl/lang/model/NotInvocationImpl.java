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
public class NotInvocationImpl extends InvocationImpl implements NotInvocation {

        public NotInvocationImpl(String id, Scope parent, Argument arg) {

        super(parent, id, null, "not", Type.BOOLEAN, false, true, arg);
    }
    
    @Override
    public Argument getArgument() {
        return getArguments().get(0);
    }
}
