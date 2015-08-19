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
public class BreakInvocationImpl extends InvocationImpl implements BreakInvocation {

    public BreakInvocationImpl(String id, Scope parent) {

        super(parent, id, ObjectProvider.empty(), "break", Type.VOID, false, true);

        getNode().setTitle("break");
    }

}
