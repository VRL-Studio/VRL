/* 
 * Polygon.java
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
package eu.mihosoft.vrl.v3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a convex polygon.
 *
 * Each convex polygon has a {@code shared} property, disthich is shared
 * betdisteen all polygons that are clones of each other or where split from the
 * same polygon. This can be used to define per-polygon properties (such as
 * surface color).
 */
public final class Polygon {

    /**
     * Polygon vertices
     */
    public final List<Vertex> vertices;
    /**
     * Shared property (can be used for shared color etc.).
     */
    public final PropertyStorage shared;
    /**
     * Plane defined by this polygon.
     *
     * <b>Note:</b> uses first three vertices to define the plane.
     */
    public final Plane plane;

    /**
     * Constructor. Creates a new polygon that consists of the specified
     * vertices.
     *
     * <b>Note:</b> the vertices used to initialize a polygon must be coplanar
     * and form a convex loop.
     *
     * @param vertices polygon vertices
     * @param shared shared property
     */
    public Polygon(List<Vertex> vertices, PropertyStorage shared) {
        this.vertices = vertices;
        this.shared = shared;
        this.plane = Plane.createFromPoints(
                vertices.get(0).pos,
                vertices.get(1).pos,
                vertices.get(2).pos);
    }

    public Polygon(List<Vertex> vertices) {
        this.vertices = vertices;
        this.shared = new PropertyStorage();
        this.plane = Plane.createFromPoints(
                vertices.get(0).pos,
                vertices.get(1).pos,
                vertices.get(2).pos);
    }

    @Override
    public Polygon clone() {
        List<Vertex> newVertices = new ArrayList<>();
        this.vertices.forEach((vertex) -> {
            newVertices.add(vertex.clone());
        });
        return new Polygon(newVertices, shared);
    }

    /**
     * Flips this polygon.
     *
     * @return this polygon
     */
    public Polygon flip() {
        vertices.forEach((vertex) -> {
            vertex.flip();
        });
        Collections.reverse(vertices);

        plane.flip();

        return this;
    }

    /**
     * Returns a flipped copy of this polygon.
     *
     * <b>Note:</b> this polygon is not modified.
     *
     * @return a flipped copy of this polygon
     */
    public Polygon flipped() {
        return clone().flip();
    }

    /**
     * Returns this polygon in STL string format.
     *
     * @return this polygon in STL string format
     */
    public String toStlString() {
        return toStlString(new StringBuilder()).toString();
    }

    /**
     * Returns this polygon in STL string format.
     *
     * @param sb string builder
     *
     * @return the specified string builder
     */
    public StringBuilder toStlString(StringBuilder sb) {

        if (this.vertices.size() >= 3) {

            // TODO: improve the triangulation?
            //
            // STL requires triangular polygons.
            // If our polygon has more vertices, create
            // multiple triangles:
            String firstVertexStl = this.vertices.get(0).toStlString();
            for (int i = 0; i < this.vertices.size() - 2; i++) {
                sb.
                        append("  facet normal ").append(
                                this.plane.normal.toStlString()).append("\n").
                        append("    outer loop\n").
                        append("      ").append(firstVertexStl).append("\n").
                        append("      ");
                this.vertices.get(i + 1).toStlString(sb).append("\n").
                        append("      ");
                this.vertices.get(i + 2).toStlString(sb).append("\n").
                        append("    endloop\n").
                        append("  endfacet\n");
            }
        }

        return sb;
    }

    /**
     * Translates this polygon.
     *
     * @param v the vector that defines the translation
     * @return this polygon
     */
    public Polygon translate(Vector3d v) {
        vertices.forEach((vertex) -> {
            vertex.pos = vertex.pos.plus(v);
        });
        return this;
    }

    /**
     * Returns a translated copy of this polygon.
     *
     * <b>Note:</b> this polygon is not modified
     *
     * @param v the vector that defines the translation
     *
     * @return a translated copy of this polygon
     */
    public Polygon translated(Vector3d v) {
        return clone().translate(v);
    }

    /**
     * Applies the specified transformation to this polygon.
     *
     * <b>Note:</b> if the applied transformation performs a mirror operation
     * the vertex order of this polygon is reversed.
     *
     * @param transform the transformation to apply
     *
     * @return this polygon
     */
    public Polygon transform(Transform transform) {

        this.vertices.stream().forEach(
                (v) -> {
                    v.transform(transform);
                });

        if (transform.getScale() < 0) {
            // the transformation includes mirroring. We need to reverse the 
            // vertex order in order to preserve the inside/outside orientation:
            Collections.reverse(vertices);
        }
        return this;
    }

    /**
     * Returns a transformed copy of this polygon.
     *
     * <b>Note:</b> if the applied transformation performs a mirror operation
     * the vertex order of this polygon is reversed.
     *
     * <b>Note:</b> this polygon is not modified
     *
     * @param transform the transformation to apply
     * @return a transformed copy of this polygon
     */
    public Polygon transformed(Transform transform) {
        return clone().transform(transform);
    }

    /**
     * Creates a polygon from the specified point list.
     *
     * @param points the points that define the polygon
     * @param shared shared property storage
     * @return a polygon defined by the specified point list
     */
    public static Polygon createFromPoints(List<Vector3d> points,
            PropertyStorage shared) {
        return fromPoints(points, shared, null);
    }

    /**
     * Creates a polygon from the specified point list.
     *
     * @param points the points that define the polygon
     * @return a polygon defined by the specified point list
     */
    public static Polygon fromPoints(List<Vector3d> points) {
        return fromPoints(points, new PropertyStorage(), null);
    }
    
        /**
     * Creates a polygon from the specified points.
     *
     * @param points the points that define the polygon
     * @return a polygon defined by the specified point list
     */
    public static Polygon fromPoints(Vector3d... points) {
        return fromPoints(Arrays.asList(points), new PropertyStorage(), null);
    }

    /**
     * Creates a polygon from the specified point list.
     *
     * @param points the points that define the polygon
     * @param shared
     * @param plane may be null
     * @return a polygon defined by the specified point list
     */
    private static Polygon fromPoints(
            List<Vector3d> points, PropertyStorage shared, Plane plane) {
        Vector3d normal
                = (plane != null) ? plane.normal.clone() : new Vector3d(0, 0, 0);

        List<Vertex> vertices = new ArrayList<>();

        for (Vector3d p : points) {
            Vector3d vec = p.clone();
            Vertex vertex = new Vertex(vec, normal);
            vertices.add(vertex);
        }

        return new Polygon(vertices, shared);
    }

    /**
     * Extrudes this polygon into the specified direction.
     *
     * @param dir direction
     *
     * @return a CSG object that consists of the extruded polygon
     */
    public CSG extrude(Vector3d dir) {
        List<Polygon> newPolygons = new ArrayList<>();

        Polygon polygon1 = this;
        double direction = polygon1.plane.normal.dot(dir);

        if (direction > 0) {
            polygon1 = polygon1.flipped();
        }

        newPolygons.add(polygon1);
        Polygon polygon2 = polygon1.translated(dir);
        int numvertices = this.vertices.size();
        for (int i = 0; i < numvertices; i++) {
            List<Vector3d> sidefacepoints = new ArrayList<>();
            int nexti = (i < (numvertices - 1)) ? i + 1 : 0;
            sidefacepoints.add(polygon1.vertices.get(i).pos);
            sidefacepoints.add(polygon2.vertices.get(i).pos);
            sidefacepoints.add(polygon2.vertices.get(nexti).pos);
            sidefacepoints.add(polygon1.vertices.get(nexti).pos);
            Polygon sidefacepolygon = Polygon.createFromPoints(
                    sidefacepoints, this.shared);
            newPolygons.add(sidefacepolygon);
        }
        
        polygon2 = polygon2.flipped();
        newPolygons.add(polygon2);
        return CSG.fromPolygons(newPolygons);
    }

}
