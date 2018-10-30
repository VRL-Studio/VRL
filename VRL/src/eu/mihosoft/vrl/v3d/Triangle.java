/* 
 * Triangle.java
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

package eu.mihosoft.vrl.v3d;

import eu.mihosoft.vrl.system.VParamUtil;
import java.io.Serializable;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * This class defines a triangle.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Triangle implements LocationChangedListener, Serializable {

    private static final long serialVersionUID = 1L;
    private Node nodeOne;
    private Node nodeTwo;
    private Node nodeThree;
    private Vector3f normal;
    private boolean normalUpToDate;
    private int index;

    /**
     * Constructor.
     */
    public Triangle() {
    }

    /**
     * Constructor.
     * @param nodeOne the first node
     * @param nodeTwo the second node
     * @param nodeThree the third node
     */
    public Triangle(Node nodeOne, Node nodeTwo, Node nodeThree) {
        this.index = 0;
        this.nodeOne = nodeOne;
        this.nodeTwo = nodeTwo;
        this.nodeThree = nodeThree;
    }

    /**
     * Constructor.
     * @param index the index
     * @param nodeOne the first node
     * @param nodeTwo the second node
     * @param nodeThree the third node
     */
    public Triangle(int index, Node nodeOne, Node nodeTwo, Node nodeThree) {
        this.index = index;
        this.nodeOne = nodeOne;
        this.nodeTwo = nodeTwo;
        this.nodeThree = nodeThree;
    }

    /**
     * Returns the first node of this triangle.
     * @return the first node of this triangle
     */
    public Node getNodeOne() {
        return nodeOne;
    }

    /**
     * Defines the first node of this triangle.
     * @param nodeOne the node to set
     */
    public void setNodeOne(Node nodeOne) {
        this.nodeOne = nodeOne;
        normalUpToDate = false;
        nodeOne.addLocationChangedListener(this);
    }

    /**
     * Returns the second node of this triangle.
     * @return the second node of this triangle
     */
    public Node getNodeTwo() {
        return nodeTwo;
    }

    /**
     * Defines the second node of this triangle.
     * @param nodeTwo the node to set
     */
    public void setNodeTwo(Node nodeTwo) {
        this.nodeTwo = nodeTwo;
        normalUpToDate = false;
        nodeTwo.addLocationChangedListener(this);
    }

    /**
     * Returns the third node of this triangle.
     * @return the third node of this triangle
     */
    public Node getNodeThree() {
        return nodeThree;
    }

    /**
     * Defines the third node of this triangle.
     * @param nodeThree the node to set
     */
    public void setNodeThree(Node nodeThree) {
        this.nodeThree = nodeThree;
        normalUpToDate = false;
        nodeThree.addLocationChangedListener(this);
    }

    /**
     * Defines the i-th node of the triangle.
     * @param i node index (valid values are 0..2)
     * @param n node to set
     */
    public void setNode(int i, Node n) {
        
        if (i < 0 || i > 2 ) {
            throw new IllegalArgumentException(
                    "Index out of range (valid values are 0..2).");
        }
        
        VParamUtil.throwIfNull(n);
        
        switch (i) {
            case 0:
                setNodeOne(n);
                break;
            case 1:
                setNodeTwo(n);
                break;
            case 2:
                setNodeThree(n);
                break;
        }
    }

    /**
     * Computes the normal of this triangle. This method only computes the
     * normal if the normal is not up to date. The normal is defined as outdated
     * whenever a new node has been assigned.
     */
    private void computeNormal() {
        if (!normalUpToDate) {
            Point3f p1 = nodeOne.getLocation();
            Point3f p2 = nodeTwo.getLocation();
            Point3f p3 = nodeThree.getLocation();

            Vector3f v1 = new Vector3f(
                    p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
            Vector3f v2 = new Vector3f(p3.x - p1.x, p3.y - p1.y, p3.z - p1.z);

            normal = new Vector3f();
            normal.cross(v1, v2);
            normal.normalize();

            normalUpToDate = true;
        }
    }

    /**
     * Returns the normal of this triangle.
     * @return the normal of this triangle
     */
    public Vector3f getNormal() {
        computeNormal();
        return normal;
    }

    /**
     * Returns the index of this triangle.
     * @return the index of this triangle
     */
    public int getIndex() {
        return index;
    }

    /**
     * Defines the index of this triangle.
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Triangle[" + index + "]: "
                + nodeOne.toString()
                + ", " + nodeTwo.toString()
                + ", " + nodeThree.toString()
                + ", Normal: " + getNormal();
    }

    @Override
    public void locationChanged(Node n) {
        normalUpToDate = false;
    }
}
