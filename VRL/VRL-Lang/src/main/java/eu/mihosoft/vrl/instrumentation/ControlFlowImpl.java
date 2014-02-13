/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.instrumentation;

import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.ControlFlow;
import eu.mihosoft.vrl.lang.model.IType;
import eu.mihosoft.vrl.lang.model.Invocation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class ControlFlowImpl implements ControlFlow {

    private final List<Invocation> invocations = new ArrayList<>();
    
    private final Scope parent;

    public ControlFlowImpl(Scope parent) {
        this.parent = parent;
    }

    

    @Override
    public Invocation createInstance(String id, IType type, String varName, Variable... args) {
        Invocation result = new InvocationImpl(parent,id, type.getFullClassName(), "<init>", true, false, true, varName, args);
        getInvocations().add(result);
        return result;
    }

    @Override
    public Invocation callMethod(String id, String varName, String mName, boolean isVoid, String retValueName, Variable... args) {
        Invocation result = new InvocationImpl(parent, id, varName, mName, false, isVoid, false, retValueName, args);
        getInvocations().add(result);
        return result;
    }
    
    @Override
    public Invocation callStaticMethod(String id, IType type, String mName, boolean isVoid, String retValueName, Variable... args) {
        Invocation result = new InvocationImpl(parent, id, type.getFullClassName(), mName, false, isVoid, true, retValueName, args);
        getInvocations().add(result);
        return result;
    }

    @Override
    public ScopeInvocation callScope(Scope scope) {
        ScopeInvocation result = new ScopeInvocationImpl(scope);
        getInvocations().add(result);
        return result;
    }

    @Override
    public String toString() {
        String result = "[\n";
        for (Invocation invocation : getInvocations()) {
            result += invocation.toString() + "\n";
        }

        result += "]";

        return result;
    }

    /**
     * @return the invocations
     */
    @Override
    public List<Invocation> getInvocations() {
        return invocations;
    }

}

