/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.types;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.reflection.TypeTemplate;
import javax.swing.JComponent;

/**
 * Type prepresentation for <code>javax.swing.JComponent</code>.
 * 
 * Style name: "silent"
 * 
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@TypeInfo(type=JComponent.class, input = true, output = true, style="silent")
public class SilentJComponentType extends TypeTemplate {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public SilentJComponentType() {
        setValueName("");
    }
}