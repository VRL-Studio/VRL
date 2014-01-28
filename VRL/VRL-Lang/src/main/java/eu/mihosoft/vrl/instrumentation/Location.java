/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.instrumentation;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface Location {

    /**
     * @return the firstColumn
     */
    int getFirstColumn();

    /**
     * @return the firstLine
     */
    int getFirstLine();

    /**
     * @return the lastColumn
     */
    int getLastColumn();

    /**
     * @return the lastLine
     */
    int getLastLine();
    
}
