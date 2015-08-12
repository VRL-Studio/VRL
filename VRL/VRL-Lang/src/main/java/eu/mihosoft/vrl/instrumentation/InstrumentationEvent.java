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
public interface InstrumentationEvent {

    /**
     *
     * @return event type
     */
    InstrumentationEventType getType();

    /**
     * 
     * @return event source
     */
    InstrumentationSource getSource();

    /**
     * 
     * @return time stamp as specified in {@link System#nanoTime() }
     */
    long getTimeStamp();
}
