/* 
 * IDArrayMap.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007–2018 by Michael Hoffer,
 * Copyright (c) 2015–2018 G-CSC, Uni Frankfurt,
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn)
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

package eu.mihosoft.vrl.visual;

import java.util.HashMap;

/**
 * A map that can store multiple entries per key.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class IDArrayMap<T extends IDObject> extends HashMap<Object, IDArrayList> {

    /**
     * Adds a value to this map and associates the specified value with the
     * specified key in this map.
     * @param key key with which the specified value is to be associated
     * @param o the value to add
     * @return <code>true</code>
     * (as specified by {@link eu.mihosoft.vrl.visual.IDArrayList#add})
     */
    public boolean add(Object key, T o) {
        return getValues(key).add(o);
    }

    /**
     * Adds a value to this map and associates the specified value with the
     * specified key in this map.
     * @param key key with which the specified value is to be associated
     * @param o the value to add
     * @param id the id to set
     * @return <code>true</code>
     * (as specified by {@link eu.mihosoft.vrl.visual.IDArrayList#addWithID})
     */
    public boolean addWithID(Object key, T o, int id) {
        return getValues(key).addWithID(o, id);
    }

    /**
     * Removes an an entry defined by its keys.
     * @param key1 the first key
     * @param key2 the second key
     * @return <code>true</code>
     * (as specified by {@link eu.mihosoft.vrl.visual.IDArrayList#removeByID})
     */
    public boolean remove(Object key1, int key2) {
        return getValues(key1).removeByID(key2);
    }

    /**
     * Returns the values associated with the specified key.
     * @param key the key of the values to return
     * @return the values associated with a given key
     */
     @SuppressWarnings( "unchecked" )
    public IDArrayList<T> getValues(Object key) {
        IDArrayList<T> list = (IDArrayList<T>) get(key);
        if (list == null) {
            list = new IDArrayList<T>();
            put(key, list);
        }
        return list;
    }
}
