/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

import java.io.Reader;

/**
 * TODO example like string ('hamburger' substring 4,8)
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface ICodeRange extends Comparable<ICodeRange> {

    /**
     * Returns the beginning index, inclusive.
     *
     * @return the beginning index, inclusive
     */
    ICodeLocation getBegin();

    /**
     * Returns the ending index, exclusive
     *
     * @return the ending index, exclusive
     */
    ICodeLocation getEnd();

    /**
     * Determines whether this code range contains the specified code range.
     *
     * @param o code range
     * @return {@code true} if this code range contains the specified code
     * range; {@code false} otherwise
     */
    boolean contains(ICodeRange o);

    /**
     * Determines whether this code range contains the specified code location.
     *
     * @param o code location
     * @return {@code true} if this code range contains the specified code
     * location; {@code false} otherwise
     */
    boolean contains(ICodeLocation o);

    /**
     * Returns the intersection of this code range and the specified code range.
     *
     * @param o code range
     * @return the intersection of this code range and the specified code range
     */
    ICodeRange intersection(ICodeRange o);

    /**
     * Returns the size of this code range (number of characters).
     *
     * @return size of this code range (number of characters)
     */
    public int size();

    /**
     * Determines whether this code range is empty ({@code size==0}).
     *
     * @return {@code true} if this code range is not empty; {@code false}
     * otherwise
     */
    public boolean isEmpty();

    public void setSource(Reader r);
    public Reader getSource();
}
