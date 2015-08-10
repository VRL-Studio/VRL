/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class ObservableCodeImpl implements ObservableCode, EventSender<CodeEvent> {

    private final Map<ICodeEventType, List<CodeEventHandler>> handlers = new HashMap<>();
    private final List<CodeEventHandler> handlerList = new ArrayList<>();

    @Override
    public void addEventHandler(ICodeEventType type, CodeEventHandler eventHandler) {
        getHandlersOfType(type).add(eventHandler);
        handlerList.add(eventHandler);
    }

    @Override
    public void removeEventHandler(ICodeEventType type, CodeEventHandler eventHandler) {
        getHandlersOfType(type).remove(eventHandler);
        handlerList.remove(eventHandler);
    }

    private List<CodeEventHandler> getHandlersOfType(ICodeEventType type) {
        List<CodeEventHandler> result = handlers.get(type);

        if (result == null) {
            result = new ArrayList<>();
            handlers.put(type, result);
        }

        return result;
    }

    public List<ICodeEventType> getThisTypeAndItsAncestors(ICodeEventType type) {
        List<ICodeEventType> result = new ArrayList<>();

        while (type != CodeEventType.ROOT) {
            result.add(type);
            type = type.getSuperType();
        }

        return result;
    }

    @Override
    public void fireEvent(CodeEvent evt) {

        ICodeEventType type = evt.getType();
        
        ICodeEventType prevType = null;

        while (prevType != CodeEventType.ROOT) {
            
            prevType = type;

            for (CodeEventHandler h : getHandlersOfType(type)) {
                h.handle(evt);
            }

            type = type.getSuperType();
        }
    }

}
