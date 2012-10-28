/* 
 * IndexedGeometryList.java
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
 * Computing and Visualization in Science, 2011, in press.
 */

package eu.mihosoft.vrl.v3d;

import java.util.ArrayList;
import java.util.HashMap;
import javax.media.j3d.Geometry;

/**
 * A list of java 3d geometries.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class IndexedGeometryList {

    private HashMap<Geometry, Integer> indexMap =
            new HashMap<Geometry, Integer>();
    private GeometryList geometries = new GeometryList();

    /**
     * Returns the index of a given geometry.
     * @param g the geometry
     * @return the index
     */
    public Integer getIndex(Geometry g) {
        return getIndexMap().get(g);
    }


    /**
     * Adds a geometry to this list.
     * @param g the geometry to be added
     * @param i the index
     * @return <code>true</code> (as specified by {@link java.util.Collection#add})
     */
    public boolean add(Geometry g, Integer i) {
        getIndexMap().put(g, i);
        return getGeometries().add(g);
    }

    /**
     * Removes a geometry from this list.
     * @param g the geometry to remove
     * @return <code>true</code> (as specified by {@link java.util.Collection#add})
     */
    public boolean remove(Geometry g) {
        indexMap.remove(g);
        return geometries.remove(g);
    }


    /**
     * Returns the index map.
     * @return the index map
     */
    public HashMap<Geometry, Integer> getIndexMap() {
        return indexMap;
    }

    /**
     * Returns the geometry list.
     * @return the geometry list
     */
    public GeometryList getGeometries() {
        return geometries;
    }

}
