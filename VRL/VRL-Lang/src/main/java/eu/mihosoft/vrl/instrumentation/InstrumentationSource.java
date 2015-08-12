/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

import java.util.Optional;

/**
 * Instrumentation source.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface InstrumentationSource {

    /**
     * 
     * @return Id of the code entity
     */
    String getId();
    /**
     * 
     * @return name of the code entity
     */
    String getName();
    
    /**
     * 
     * @return optional return value of the instrumented invocation
     */
    Optional<Object> getReturnValue();
    
    /**
     * 
     * @return arguments of the instrumented invocation
     */
    Object[] getArguments();
    
    /**
     * 
     * @return {@code true} if the instrumented code entity is an invocation;
     * {@code false} otherwise
     */
    boolean isInvocation();
}
