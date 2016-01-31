/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.io;

import java.util.List;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface VExtensionFileFilter {

    String getDescription();

    /**
     * Returns the endings accepted by this file filter.
     *
     * @return the accepted endings
     */
    List<String> getExtensions();
    
}
