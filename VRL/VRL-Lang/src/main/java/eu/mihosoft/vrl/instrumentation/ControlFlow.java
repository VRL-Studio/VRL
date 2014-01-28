/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface ControlFlow {

    public Invocation createInstance(String id, IType type, String varName, Variable... args);

    public Invocation callMethod(String id, String varName, String mName, boolean isVoid, String retValueName, Variable... args);
    
    public Invocation callStaticMethod(String id, IType type, String mName, boolean isVoid, String retValueName, Variable... args);

    public ScopeInvocation callScope(Scope scope);

    public List<Invocation> getInvocations();
}

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
