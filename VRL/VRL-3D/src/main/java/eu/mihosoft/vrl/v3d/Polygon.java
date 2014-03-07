package eu.mihosoft.vrl.v3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a convex polygon. The vertices used to initialize a polygon must
 * be coplanar and form a convex loop. They do not have to be `CSG.Vertex`
 * instances but they must behave similarly (duck typing can be used for
 * customization).
 *
 * Each convex polygon has a `shared` property, which is shared between all
 * polygons that are clones of each other or were split from the same polygon.
 * This can be used to define per-polygon properties (such as surface color).
 */
public class Polygon {

    public final List<Vertex> vertices;
    public final boolean shared;
    public final Plane plane;

    /**
     * Constructor.
     *
     * @param vertices
     * @param shared
     */
    public Polygon(List<Vertex> vertices, boolean shared) {
        this.vertices = vertices;
        this.shared = shared;
        this.plane = Plane.fromPoints(
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
     * <b>Note:</b> this polygon is not modified
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
        String result = "";

        if (this.vertices.size() >= 3) {
            // STL requires triangular polygons.
            // If our polygon has more vertices, create
            // multiple triangles:
            String firstVertexStl = this.vertices.get(0).toStlString();
            for (int i = 0; i < this.vertices.size() - 2; i++) {
                result
                        += "  facet normal " + this.plane.normal.toStlString() + "\n"
                        + "    outer loop\n"
                        + "      " + firstVertexStl + "\n"
                        + "      " + this.vertices.get(i + 1).toStlString() + "\n"
                        + "      " + this.vertices.get(i + 2).toStlString() + "\n"
                        + "    endloop\n"
                        + "  endfacet\n";
            }
        }
        return result;
    }

    /**
     * Translates this polygon.
     *
     * @param v the vector that defines the translation
     * @return this polygon
     */
    public Polygon translate(Vector v) {
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
     * @return a translated copy of this polygon
     */
    public Polygon translated(Vector v) {
        return clone().translate(v);
    }

    /**
     * Applies the specified transformation to this polygon.
     *
     * <b>Note:</b> if the applied transformation performs a mirror operation
     * the vertex order of this polygon is reversed.
     *
     * @param transform the transformation to apply
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
     * @param shared
     * @return a polygon defined by the specified point list
     */
    public static Polygon createFromPoints(List<Vector> points, boolean shared) {
        return createFromPoints(points, shared, null);
    }

    /**
     * Creates a polygon from the specified point list.
     *
     * @param points the points that define the polygon
     * @param shared
     * @param plane may be null
     * @return a polygon defined by the specified point list
     */
    private static Polygon createFromPoints(List<Vector> points, boolean shared, Plane plane) {
        Vector normal = (plane != null) ? plane.normal.clone() : new Vector(0, 0, 0);

        List<Vertex> vertices = new ArrayList<>();
        points.forEach((Vector p) -> {
            Vector vec = p.clone();
            Vertex vertex = new Vertex(vec, normal);
            vertices.add(vertex);
        });

        return new Polygon(vertices, shared);
    }

    /**
     * Extrudes this polygon into the specified direction.
     *
     * @param dir direction
     * @return a CSG object that consists of the extruded polygon
     */
    public CSG extrude(Vector dir) {
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
            List<Vector> sidefacepoints = new ArrayList<>();
            int nexti = (i < (numvertices - 1)) ? i + 1 : 0;
            sidefacepoints.add(polygon1.vertices.get(i).pos);
            sidefacepoints.add(polygon2.vertices.get(i).pos);
            sidefacepoints.add(polygon2.vertices.get(nexti).pos);
            sidefacepoints.add(polygon1.vertices.get(nexti).pos);
            Polygon sidefacepolygon = Polygon.createFromPoints(sidefacepoints, this.shared);
            newPolygons.add(sidefacepolygon);
        }
        polygon2 = polygon2.flipped();
        newPolygons.add(polygon2);
        return CSG.fromPolygons(newPolygons);
    }

}
