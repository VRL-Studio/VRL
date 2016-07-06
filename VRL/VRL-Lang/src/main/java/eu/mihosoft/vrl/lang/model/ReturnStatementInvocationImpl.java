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
public class ReturnStatementInvocationImpl extends InvocationImpl implements ReturnStatementInvocation {

    public ReturnStatementInvocationImpl(Scope parent, IArgument arg) {

        super(parent, "return " + parent.getId(), null, "return", Type.ANY, false, true, arg);

        getNode().setTitle("return ");
    }

    public ReturnStatementInvocationImpl(Scope parent) {
        super(parent, "", null, "return", null, false, true, new IArgument[0]);
    }

    @Override
    public IArgument getArgument() {
        return getArguments().get(0);
    }

    public void setArgument(IArgument arg) {
        getArguments().add(arg);
    }

    @Override
    public IType getReturnType() {
        return getArgument().getType();
    }
}
