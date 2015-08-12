/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface InstrumentationEventType {
    
    public static final InstrumentationEventType ANY = new InstrumentationEventTypeImpl("InstrumentationEventType:ANY");
    public static final InstrumentationEventType ROOT = ANY;
    public static final InstrumentationEventType INVOCATION = new InstrumentationEventTypeImpl(ANY, "InstrumentationEventType:INVOCATION");
    public static final InstrumentationEventType PRE_INVOCATION = new InstrumentationEventTypeImpl(INVOCATION, "InstrumentationEventType:PRE_INVOCATION");
    public static final InstrumentationEventType POST_INVOCATION = new InstrumentationEventTypeImpl(INVOCATION, "InstrumentationEventType:POST_INVOCATION");

    String getName();

    InstrumentationEventType getSuperType();
}
