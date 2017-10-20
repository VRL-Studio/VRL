/* 
 * Selection.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2012 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2012 by Michael Hoffer
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
 * First, the following text must be displayed on the Canvas:
 * "based on VRL source code". In this case the VRL canvas icon must be removed.
 * 
 * Second, the copyright notice must remain. It must be reproduced in any
 * program that uses VRL.
 *
 * Third, add an additional notice, stating that you modified VRL. A suitable
 * notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Please cite the publication(s) listed below.
 *
 * Publications:
 *
 * M. Hoffer, C. Poliwoda, & G. Wittum. (2013). Visual reflection library:
 * a framework for declarative GUI programming on the Java platform.
 * Computing and Visualization in Science, 2013, 16(4),
 * 181–192. http://doi.org/10.1007/s00791-014-0230-y
 */

package eu.mihosoft.vrl.types;

import java.io.Serializable;
import java.util.Collection;

/**
 * A selection consists of a collection and
 * a selected item. It can be used as simple model for JComboBox based type
 * representations.
 * @see java.util.Collection
 * @see javax.swing.JComboBox
 * @see eu.mihosoft.vrl.types.SelectionInputType
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Selection implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private Collection<?> collection;
    private Integer selectedIndex;
    private Object selectedObject;

    /**
     * Constructor.
     */
    public Selection() {
    }

    /**
     * Constructor.
     * @param collection
     */
    public Selection(Collection<?> collection) {
        this.collection = collection;
    }


    /**
     * Returns the selected object.
     * @return the selected object
     */
    public Object getSelectedObject() {
        return selectedObject;
    }

    /**
     * Defines the selected object
     * @param selectedObject object the object to set
     */
    public void setSelectedObject(Object selectedObject) {
        this.selectedObject = selectedObject;
    }

    /**
     * Returns the collection.
     * @return the collection
     */
    public Collection<?> getCollection() {
        return collection;
    }

    /**
     * Defines the collection.
     * @param collection the collection to set
     */
    public void setCollection(Collection<?> collection) {
        this.collection = collection;
    }

    /**
     * Returns the index of the selected object.
     * @return the index of the selected object
     */
    public Integer getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Defines the index of the selected object.
     * @param selectedIndex the index to set
     */
    public void setSelectedIndex(Integer selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
}
