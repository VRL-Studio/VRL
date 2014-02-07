/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

import eu.mihosoft.vrl.lang.model.ICodeRange;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface CodeEntity {
    public void setId(String id);
    public String getId();
    
//    public void setCode(String code);
//    public String getCode();
    
    public void setRange(ICodeRange location);
    public ICodeRange getRange();
    
}
