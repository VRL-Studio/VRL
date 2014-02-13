/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.instrumentation;

import eu.mihosoft.vrl.lang.model.ICodeRange;
import eu.mihosoft.vrl.lang.model.Scope;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class ScopeInvocationImpl extends InvocationImpl implements ScopeInvocation {

    private final Scope scope;

    public ScopeInvocationImpl(Scope s) {
        super(s, "", null, "scope", false, true, true, "", new Variable[0]);
        this.scope = s;
    }

    /**
     * @return the scope
     */
    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public boolean isScope() {
        return true;
    }

    @Override
    public ICodeRange getRange() {
        return scope.getRange();
    }

}

