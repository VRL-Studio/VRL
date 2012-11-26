/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang;

import eu.mihosoft.vrl.system.VMessage;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VDebug {
    private static Map<String,Boolean> states = new HashMap<String, Boolean>();

    public static void setBreak(String id) {
        if (!states.containsKey(id)) {
            states.put(id, Boolean.TRUE);
            
            while(states.get(id)) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(VDebug.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }
            
            states.remove(id);
            
        } else {
            VMessage.exception("Cannot set Breakpoint",
                    "The breakpoint " + id + " does already exist!");
        }
    }
    
    public static Collection<String> getBreaks() {
        return states.keySet();
    }
    
    public static void advance(String id) {
        if (!states.containsKey(id)) {
            VMessage.exception("Cannot set Breakpoint",
                    "The breakpoint " + id + " does not exist!");
        }
        
        states.put(id, Boolean.FALSE);
    }
}
