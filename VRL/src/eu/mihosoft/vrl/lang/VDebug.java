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

    private static final Map<String, Boolean> states =
            new HashMap<String, Boolean>();
    
    private static Boolean disableAll = false;

    public static void setBreak(String id) {
        
        if (disableAll) {
            return;
        }
        
        if (!states.containsKey(id)) {
            states.put(id, Boolean.TRUE);

            System.out.println(">> VDebug: waiting at breakpoint [" + id + "]");

            while (synchronizedGet(id)) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(VDebug.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }

            synchronized (states) {
                states.remove(id);
            }

        } else {
            VMessage.exception("Cannot set breakpoint",
                    "The breakpoint " + id + " does already exist!");
        }
    }

    private static Boolean synchronizedGet(String id) {
        synchronized (states) {
            return states.get(id);
        }
    }

    private static Boolean synchronizedPut(String id, Boolean value) {
        synchronized (states) {
            return states.put(id, value);
        }
    }

    private static Collection<String> synchronizedGetKeySet() {
        synchronized (states) {
            return states.keySet();
        }
    }

    private static Boolean synchronizedContains(String id) {
        synchronized (states) {
            return states.containsKey(id);
        }
    }

    public static Collection<String> getBreaks() {
        return states.keySet();
    }
    
    public static String at() {
        String msg = "VDebug: currently at: " + getBreaks() + "";
        System.out.println(msg);
        return msg;
    }

    public static void disableAll() {
        disableAll = true;
    }

    public static void enableAll() {
        disableAll = false;
    }
    
    public static void step(String id) {

        if (!synchronizedContains(id)) {
            VMessage.exception("Cannot step over breakpoint",
                    "Position not at breakpoint [" + id + "]!");
            return;
        }

        synchronizedPut(id, Boolean.FALSE);
    }
    
    public static void step() {
        for (String id : synchronizedGetKeySet()) {
            synchronizedPut(id, Boolean.FALSE);
        }
    }
}
