/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.ICodeEventType;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class InstrumentationEventImpl implements InstrumentationEvent{
    private final InstrumentationEventType type;
    private final InstrumentationSource source;
    private final long timeStamp;

    InstrumentationEventImpl(
            InstrumentationEventType type,
            InstrumentationSource source) {
        this.type = type;
        this.source = source;
        this.timeStamp = System.nanoTime();
    }

    /**
     *
     * @return event type
     */
    @Override
    public InstrumentationEventType getType() {
        return this.type;
    }

    /**
     * 
     * @return event source
     */
    @Override
    public InstrumentationSource getSource() {
        return this.source;
    }

    /**
     * 
     * @return time stamp as specified in {@link System#nanoTime() }
     */
    @Override
    public long getTimeStamp() {
        return this.timeStamp;
    }
}
