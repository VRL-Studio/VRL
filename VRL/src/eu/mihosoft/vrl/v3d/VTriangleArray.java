/* 
 * VTriangleArray.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2007–2017 by Michael Hoffer
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

import eu.mihosoft.vrl.annotation.ObjectInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import javafx.scene.shape.TriangleMesh;
import javax.media.j3d.Geometry;
import javax.media.j3d.Material;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * A triangle array. For loading triangle arrays from file see
 * {@link TxT2Geometry}. A triangle array can be used to create Java 3D shapes
 * or VRL geometries.
 *
 * <p>
 * <b>Note:</b> the memory footprint of VTriangleArray based geometries is
 * significantly higher than using Shape3D. Therefore, do not use it for highly
 * complex geometries (#Triangles > 10^5)</p>
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 *
 * @see eu.mihosoft.vrl.types.Shape3DType
 * @see eu.mihosoft.vrl.v3d.VGeometry3D
 * @see Shape3DArray
 *
 */
@ObjectInfo(serializeParam = false)
public class VTriangleArray extends ArrayList<Triangle> {

    private static final long serialVersionUID = 1L;
    private transient TriangleArray triangleArray;
    private boolean triangleArrayOutdated;
    private float scaleFactor;
    private Vector3f offset;

    public VTriangleArray() {
    }

    public VTriangleArray(TriangleArray triangleArray) {
        this.triangleArray = triangleArray;

        Triangle t = null;

        for (int i = 0; i < triangleArray.getVertexCount(); i++) {

            int nodeIndex = i % 3;

            if (nodeIndex == 0) {
                t = new Triangle();
            }

            Point3f p3f = new Point3f();

            triangleArray.getCoordinate(i, p3f);

            Color3f c = new Color3f();

            triangleArray.getColor(i, c);

            t.setNode(nodeIndex, new Node(p3f, c));
        }
    }

    /**
     * Returns a java 3d triangle array.
     *
     * @return a java 3d triangle array
     */
    public TriangleArray getTriangleArray() {
        return getTriangleArray(true);
    }

    /**
     * Returns a java 3d triangle array.
     *
     * @return a java 3d triangle array
     */
    public TriangleArray getTriangleArray(boolean vertexColoring) {
        if (triangleArray == null || triangleArrayOutdated) {
            int numberOfTriangles = this.size();

            if (vertexColoring) {
                triangleArray = new TriangleArray(numberOfTriangles * 3,
                        TriangleArray.COORDINATES
                        | TriangleArray.NORMALS
                        | TriangleArray.COLOR_3);
            } else {
                triangleArray = new TriangleArray(numberOfTriangles * 3,
                        TriangleArray.COORDINATES
                        | TriangleArray.NORMALS);
            }

            int triangleCount = 0;
            for (Triangle t : this) {
                triangleArray.setCoordinate(triangleCount * 3,
                        t.getNodeOne().getLocation());
                triangleArray.setCoordinate(triangleCount * 3 + 1,
                        t.getNodeTwo().getLocation());
                triangleArray.setCoordinate(triangleCount * 3 + 2,
                        t.getNodeThree().getLocation());

                triangleArray.setNormal(triangleCount * 3, t.getNormal());
                triangleArray.setNormal(triangleCount * 3 + 1, t.getNormal());
                triangleArray.setNormal(triangleCount * 3 + 2, t.getNormal());

                if (vertexColoring) {
                    Color3f c1 = t.getNodeOne().getColor();

                    if (c1 != null) {
                        triangleArray.setColor(triangleCount * 3, c1);
                    }

                    Color3f c2 = t.getNodeTwo().getColor();

                    if (c2 != null) {
                        triangleArray.setColor(triangleCount * 3 + 1, c2);
                    }

                    Color3f c3 = t.getNodeThree().getColor();

                    if (c3 != null) {
                        triangleArray.setColor(triangleCount * 3 + 2, c3);
                    }
                }

                triangleCount++;
            }
            triangleArrayOutdated = false;
        }
        return triangleArray;
    }

    /**
     * Returns a JavaFX3D triangle mesh.
     *
     * @param vertexColoring vertex coloring (currently not supported)
     * @return a JavaFX3D triangle mesh inside a mesh container
     */
    public JFXMeshContainer getJFXTriangleMesh(boolean vertexColoring) {

        TriangleMesh mesh = new TriangleMesh();

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;

        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        int counter = 0;
        for (Triangle p : this) {
            Node firstVertex = p.getNodeOne();
            Node secondVertex = p.getNodeTwo();
            Node thirdVertex = p.getNodeThree();

            if (firstVertex.getLocation().x < minX) {
                minX = firstVertex.getLocation().x;
            }
            if (firstVertex.getLocation().y < minY) {
                minY = firstVertex.getLocation().y;
            }
            if (firstVertex.getLocation().z < minZ) {
                minZ = firstVertex.getLocation().z;
            }

            if (firstVertex.getLocation().x > maxX) {
                maxX = firstVertex.getLocation().x;
            }
            if (firstVertex.getLocation().y > maxY) {
                maxY = firstVertex.getLocation().y;
            }
            if (firstVertex.getLocation().z > maxZ) {
                maxZ = firstVertex.getLocation().z;
            }

            mesh.getPoints().addAll(
                    (float) firstVertex.getLocation().x,
                    (float) firstVertex.getLocation().y,
                    (float) firstVertex.getLocation().z);

            mesh.getTexCoords().addAll(0); // texture (not covered)
            mesh.getTexCoords().addAll(0);

            if (secondVertex.getLocation().x < minX) {
                minX = secondVertex.getLocation().x;
            }
            if (secondVertex.getLocation().y < minY) {
                minY = secondVertex.getLocation().y;
            }
            if (secondVertex.getLocation().z < minZ) {
                minZ = secondVertex.getLocation().z;
            }

            if (secondVertex.getLocation().x > maxX) {
                maxX = firstVertex.getLocation().x;
            }
            if (secondVertex.getLocation().y > maxY) {
                maxY = firstVertex.getLocation().y;
            }
            if (secondVertex.getLocation().z > maxZ) {
                maxZ = firstVertex.getLocation().z;
            }

            mesh.getPoints().addAll(
                    (float) secondVertex.getLocation().x,
                    (float) secondVertex.getLocation().y,
                    (float) secondVertex.getLocation().z);

            mesh.getTexCoords().addAll(0); // texture (not covered)
            mesh.getTexCoords().addAll(0);

            mesh.getPoints().addAll(
                    (float) thirdVertex.getLocation().x,
                    (float) thirdVertex.getLocation().y,
                    (float) thirdVertex.getLocation().z);

            mesh.getTexCoords().addAll(0); // texture (not covered)
            mesh.getTexCoords().addAll(0);

            if (thirdVertex.getLocation().x < minX) {
                minX = thirdVertex.getLocation().x;
            }
            if (thirdVertex.getLocation().y < minY) {
                minY = thirdVertex.getLocation().y;
            }
            if (thirdVertex.getLocation().z < minZ) {
                minZ = thirdVertex.getLocation().z;
            }

            if (thirdVertex.getLocation().x > maxX) {
                maxX = firstVertex.getLocation().x;
            }
            if (thirdVertex.getLocation().y > maxY) {
                maxY = firstVertex.getLocation().y;
            }
            if (thirdVertex.getLocation().z > maxZ) {
                maxZ = firstVertex.getLocation().z;
            }

            mesh.getFaces().addAll(
                    counter, // first vertex
                    0, // texture (not covered)
                    counter + 1, // second vertex
                    0, // texture (not covered)
                    counter + 2, // third vertex
                    0 // texture (not covered)
            );
            
            counter += 3;

        } // end for triangle

        return new JFXMeshContainer(
                new Vector3d(minX, minY, minZ),
                new Vector3d(maxX, maxY, maxZ), mesh);
    }

    /**
     * Adds a triangle to this array and triggers normal computation.
     *
     * @param t the triangle to add
     */
    public void addTriangle(Triangle t) {
        this.add(t);
    }

    /**
     * Generates triangle indices including node indices. Existing indices will
     * be overwritten.
     */
    public void generateIndices() {
        for (int i = 0; i < size(); i++) {
            Triangle t = get(i);
            t.setIndex(i);
            t.getNodeOne().setIndex(t.getIndex() * 3);
            t.getNodeTwo().setIndex(t.getIndex() * 3 + 1);
            t.getNodeThree().setIndex(t.getIndex() * 3 + 2);
        }
    }

    @Override
    public boolean add(Triangle t) {
        triangleArrayOutdated = true;
        return super.add(t);
    }

    /**
     * Returns this array as geometry list.
     *
     * @return this array as geometry list
     */
    public GeometryList toGeometryList(boolean vertexColoring) {

        GeometryList result = new GeometryList();

        for (Triangle t : this) {
            VTriangleArray tA = new VTriangleArray();
            tA.addTriangle(t);
            result.add(tA.getTriangleArray(vertexColoring));
        }
        return result;
    }

    /**
     * Returns this array as geometry list.
     *
     * @return this array as geometry list
     */
    public GeometryList toGeometryList() {

        return toGeometryList(true);
    }

    /**
     * Returns this array as indexed geometry list.
     *
     * @return this array as indexed geometry list
     */
    public IndexedGeometryList toIndexedGeometryList() {

        return toIndexedGeometryList(true);
    }

    /**
     * Returns this array as indexed geometry list.
     *
     * @return this array as indexed geometry list
     */
    public IndexedGeometryList toIndexedGeometryList(boolean vertexColoring) {

        IndexedGeometryList result = new IndexedGeometryList();

        for (Triangle t : this) {
            VTriangleArray tA = new VTriangleArray();
            tA.addTriangle(t);
            Geometry g = tA.getTriangleArray(vertexColoring);
            result.add(g, t.getIndex());
        }

        return result;
    }

    /**
     * Returns triangle by index.
     *
     * @param i the index of the triangle that is to be returned
     * @return the triangle or <code>null</code> if no such triangle exists
     */
    public Triangle getByIndex(Integer i) {
        Triangle result = null;

        for (Triangle t : this) {
            if (t.getIndex() == i) {
                result = t;
                break;
            }
        }

        return result;
    }

    public void centerNodes() {
        Nodes nodes = new Nodes();

        for (Triangle t : this) {
            nodes.addNode(t.getNodeOne().getIndex(), t.getNodeOne());
            nodes.addNode(t.getNodeTwo().getIndex(), t.getNodeTwo());
            nodes.addNode(t.getNodeThree().getIndex(), t.getNodeThree());
        }

        nodes.centerNodes();

        scaleFactor = nodes.getScaleFactor();
        offset = nodes.getOffset();
    }

    public float getScaleFactor() {

        if (scaleFactor == 0) {
            scaleFactor = 1.f;
        }

        return scaleFactor;
    }

    /**
     * @return the offset
     */
    public Vector3f getOffset() {

        if (offset == null) {
            offset = new Vector3f();
        }

        return offset;
    }
}
