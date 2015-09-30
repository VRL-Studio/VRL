/* 
 * ICodeRange.java
 *
 * Copyright (c) 2009–2014 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2014 by Michael Hoffer
 * 
 * This file is part of Visual Reflection Library (VRL).
 *
 * VRL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * see: http://opensource.org/licenses/LGPL-3.0
 *      file://path/to/VRL/src/eu/mihosoft/vrl/resources/license/lgplv3.txt
 *
 * VRL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * This version of VRL includes copyright notice and attribution requirements.
 * According to the LGPL this information must be displayed even if you modify
 * the source code of VRL. Neither the VRL Canvas attribution icon nor any
 * copyright statement/attribution may be removed.
 *
 * Attribution Requirements:
 *
 * If you create derived work you must do three things regarding copyright
 * notice and author attribution.
 *
 * First, the following text must be displayed on the Canvas or an equivalent location:
 * "based on VRL source code".
 * 
 * Second, the copyright notice must remain. It must be reproduced in any
 * program that uses VRL.
 *
 * Third, add an additional notice, stating that you modified VRL. In addition
 * you must cite the publications listed below. A suitable notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Publications:
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, in press.
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
     * Returns the hull of this code range and the specified code range.
     *
     * @param o code range
     * @return the hull of this code range and the specified code range
     */
    ICodeRange hull(ICodeRange o);

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
