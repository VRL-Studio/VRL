/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

import eu.mihosoft.vrl.lang.model.EventSender;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class InstrumentationEventSender implements EventSender<InstrumentationEvent> {

    private final Map<InstrumentationEventType, List<InstrumentationEventHandler>> handlers = new HashMap<>();
    private final List<InstrumentationEventHandler> handlerList = new ArrayList<>();

    public void addEventHandler(InstrumentationEventType type, InstrumentationEventHandler eventHandler) {
        getHandlersOfType(type).add(eventHandler);
        handlerList.add(eventHandler);
    }

    public void removeEventHandler(InstrumentationEventType type, InstrumentationEventHandler eventHandler) {
        getHandlersOfType(type).remove(eventHandler);
        handlerList.remove(eventHandler);
    }

    private List<InstrumentationEventHandler> getHandlersOfType(InstrumentationEventType type) {
        List<InstrumentationEventHandler> result = handlers.get(type);

        if (result == null) {
            result = new ArrayList<>();
            handlers.put(type, result);
        }

        return result;
    }

    public List<InstrumentationEventType> getThisTypeAndItsAncestors(InstrumentationEventType type) {
        List<InstrumentationEventType> result = new ArrayList<>();

        while (type != InstrumentationEventType.ROOT) {
            result.add(type);
            type = type.getSuperType();
        }

        return result;
    }

    @Override
    public void fireEvent(InstrumentationEvent evt) {

        InstrumentationEventType type = evt.getType();
        
        InstrumentationEventType prevType = null;

        while (prevType != InstrumentationEventType.ROOT) {
            
            prevType = type;

            for (InstrumentationEventHandler h : getHandlersOfType(type)) {
                h.handle(evt);
            }

            type = type.getSuperType();
        }
    }

}
