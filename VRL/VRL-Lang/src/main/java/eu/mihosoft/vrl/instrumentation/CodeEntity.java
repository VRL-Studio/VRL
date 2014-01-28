/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface CodeEntity {
    public void setId(String id);
    public String getId();
    
    public void setCode(String code);
    public String getCode();
    
    public void setLocation(Location location);
    public Location getLocation();
    
}
