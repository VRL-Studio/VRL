/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

import java.util.Optional;

/**
 * Instrumentation source.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class InstrumentationSourceImpl implements InstrumentationSource {

    private final String id;
    private final String name;
    private final Object[] args;
    private final Optional<Object> retVal;
    private final boolean invocation;
    private static final Object[] EMPTY_ARGS = new Object[0];
    
    private final String argsString;
    private final String retValString;

    public InstrumentationSourceImpl(
            String id,
            String name,
            Object[] args,
            Object retVal,
            boolean invocation) {
        this.id = id;
        this.name = name;
        if (args == null) {
            args = EMPTY_ARGS;
        }
        this.args = args;
        this.retVal = Optional.ofNullable(retVal);
        this.invocation = invocation;
        
        
        String[] argsStr = new String[args.length];
        
        for (int i = 0; i < argsStr.length; i++) {
            String s = args[i] != null ? args[i].toString() : "null";
            argsStr[i] = "'" + s + "'";
        }
        
        argsString = String.join(", ", argsStr);
        
        retValString = "[" + this.retVal.orElse("null").toString() + "]";
    }

    /**
     *
     * @return Id of the code entity
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     *
     * @return name of the code entity
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return optional return value of the instrumented invocation
     */
    @Override
    public Optional<Object> getReturnValue() {
        return this.retVal;
    }

    /**
     *
     * @return arguments of the instrumented invocation
     */
    @Override
    public Object[] getArguments() {
        return this.args;
    }

    /**
     *
     * @return {@code true} if the instrumented code entity is an invocation;
     * {@code false} otherwise
     */
    @Override
    public boolean isInvocation() {
        return this.invocation;
    }

    @Override
    public String toString() {
        return "[ id: " + id + ", name: " + name 
                + ", args: [" + argsString + "], ret-val: " + retValString + " ]";
    }
    
    
}
