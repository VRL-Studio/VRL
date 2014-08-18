/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.reflection;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class WorkflowEventImpl implements WorkflowEvent{

    private String eventType;

    public WorkflowEventImpl(String eventType) {
        this.eventType = eventType;
    }
    
    
    
    @Override
    public String getEventType() {
        return eventType;
    }
    
}
