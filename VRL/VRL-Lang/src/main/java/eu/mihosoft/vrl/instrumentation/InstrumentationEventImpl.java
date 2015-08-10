/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.ICodeEventType;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class InstrumentationEventImpl implements InstrumentationEvent{
    private final InstrumentationEventType type;
    private final InstrumentationSource source;
    private final long timeStamp;
    private final Date date;

    InstrumentationEventImpl(
            InstrumentationEventType type,
            InstrumentationSource source) {
        this.type = type;
        this.source = source;
        this.timeStamp = System.nanoTime();
        this.date = new Date(timeStamp);
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
    
    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        String evtType = "ANY";
        
        if(getType()==InstrumentationEventType.PRE_INVOCATION) {
            evtType = "PRE_INV";
        } else if (getType()==InstrumentationEventType.POST_INVOCATION) {
            evtType = "POST_INV";
        }
            
            
        return "[ type: " + evtType + ", src: " + getSource() + ", time-stamp: " + date.toString() + "]";
    }
}
