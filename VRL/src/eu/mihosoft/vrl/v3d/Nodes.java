/* 
 * Nodes.java
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

package eu.mihosoft.vrl.v3d;

import com.sun.j3d.utils.geometry.Box;
import java.util.HashMap;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * A collection of nodes.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Nodes {

    private HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();
    private float minX;
    private float maxX;
    private float minY;
    private float maxY;
    private float minZ;
    private float maxZ;
    private float xDimension;
    private float yDimension;
    private float zDimension;
    
    private float scaleFactor;
    private Vector3f offset;

    /**
     * Adds a node to this collection.
     * @param index the index of the node
     * @param node the node to add
     */
    public void addNode(int index, Node node) {
        nodes.put(index, node);

        // compute node array dimensions
        minX = Math.min(node.getLocation().x, minX);
        maxX = Math.max(node.getLocation().x, maxX);
        minY = Math.min(node.getLocation().y, minY);
        maxY = Math.max(node.getLocation().y, maxY);
        minZ = Math.min(node.getLocation().z, minZ);
        maxZ = Math.max(node.getLocation().z, maxZ);

        xDimension = maxX - minX;
        yDimension = maxY - minY;
        zDimension = maxZ - minZ;
    }

    /**
     * Returns node by its index.
     * @param index the index of the node that is to be returned
     * @return the node with the specified index if such a node exists;
     *         <code>null</code> otherwise
     */
    public Node getNode(int index) {
        Node result = nodes.get(index);
        result.setIndex(index);
        return result;
    }

    /**
     * Returns the center offset.
     * @return the center offset
     */
    private Vector3f getCenterOffset() {
        return new Vector3f(minX + xDimension / 2,
                minY + yDimension / 2, minZ + zDimension / 2);
    }

    /**
     * Centers the nodes.
     */
    public void centerNodes() {
        offset = getCenterOffset();

        float maxDimension = Math.max(xDimension, yDimension);
        maxDimension = Math.max(maxDimension, zDimension);

        scaleFactor = 10 / maxDimension;

        for (Object o : nodes.keySet()) {
            Node n = nodes.get(o);

            Point3f location = n.getLocation();

            location.x -= offset.x;
            location.y -= offset.y;
            location.z -= offset.z;

            location.x *= scaleFactor;
            location.y *= scaleFactor;
            location.z *= scaleFactor;

//            System.out.println("X: " + (maxX - minX)/2);
//            System.out.println("OFFSET: " + offset);

            n.setLocation(location);
        }
    }

    /**
     * @return the scaleFactor
     */
    public float getScaleFactor() {
        return scaleFactor;
    }
    
    public Vector3f getOffset() {
        return offset;
    }
}
