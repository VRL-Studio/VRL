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

    public BreakInvocationImpl(Scope parent) {

        super(parent, "", null, "break", Type.VOID, false, false, true);

        getNode().setTitle("break");
    }

}
