/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface ICodeLocation extends Comparable<ICodeLocation>{

    /**
     * @return the line
     */
    int getLine();

    /**
     * @return the column
     */
    int getColumn();
    
    /**
     * 
     * @return the char index
     */
    int getCharIndex();

    /** 
     * @return the code
     */
//    String getCode();

    boolean isColumnValid();

    boolean isIndexValid();

    boolean isLineValid();
    
    boolean isConvertible();
}
