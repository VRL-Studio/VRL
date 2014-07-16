/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.reflection;

/**
 * Workflow event. To receive workflow events component classes must implement
 * the method 
 * <code>public void handleVRLWorkflowEvent(WorkflowEvent event)</code>. Events
 * can be triggered by calling {@link VisualCanvas#fireEvent(java.lang.String) 
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface WorkflowEvent {
    public String getEventType();
    
    public static final String STOP_WORKFLOW = "vrl:workflow:stop";
}
