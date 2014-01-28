/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.instrumentation;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface IType {

    String getFullClassName();

    /**
     * @return the packageName
     */
    String getPackageName();

    /**
     * @return the shortName
     */
    String getShortName();
    
}
