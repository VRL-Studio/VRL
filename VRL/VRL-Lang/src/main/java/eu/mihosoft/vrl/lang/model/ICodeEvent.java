/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.lang.model;

import java.util.List;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface ICodeEvent {

    List<CodeEntity> getAdded();

    List<CodeEntity> getModified();

    List<CodeEntity> getRemoved();

    CodeEntity getSource();

    ICodeEventType getType();
    
    void capture();
    
    boolean isCaptured();
}
