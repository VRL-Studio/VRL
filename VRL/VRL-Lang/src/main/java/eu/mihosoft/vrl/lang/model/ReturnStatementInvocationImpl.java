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

    public ReturnStatementInvocationImpl(String id, Scope parent, Argument arg ) {

        super(parent, id, ObjectProvider.empty(), "return", arg.getType(), false, true, arg);

        getNode().setTitle("return ");
    }

    
    @Override
    public Argument getArgument() {
        return getArguments().get(0);
    }


}
