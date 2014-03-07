package eu.mihosoft.vrl.v3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// # class Polygon
// Represents a convex polygon. The vertices used to initialize a polygon must
// be coplanar and form a convex loop. They do not have to be `CSG.Vertex`
// instances but they must behave similarly (duck typing can be used for
// customization).
//
// Each convex polygon has a `shared` property, which is shared between all
// polygons that are clones of each other or were split from the same polygon.
// This can be used to define per-polygon properties (such as surface color).
public class Polygon {

    public final List<Vertex> vertices;
    public final boolean shared;
    public final Plane plane;

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

    public void flip() {
        vertices.forEach((vertex) -> {
            vertex.flip();
        });
        Collections.reverse(vertices);
        plane.flip();
    }

    public String toStlString() {
        String result = "";
        
        if (this.vertices.size() >= 3) // should be!
        {
	    // STL requires triangular polygons. If our polygon has more vertices, create
            // multiple triangles:
            String firstVertexStl = this.vertices.get(0).toStlString();
            for (int i = 0; i < this.vertices.size() - 2; i++) {
                result += "facet normal " + this.plane.normal.toStlString() + "\nouter loop\n";
                result += firstVertexStl+"\n";
                result += this.vertices.get(i + 1).toStlString() + "\n";
                result += this.vertices.get(i + 2).toStlString() + "\n";
                result += "endloop\nendfacet\n";
            }
        }
        return result;
    }

    void translate(Vector v) {
        vertices.forEach((vertex) -> {
            vertex.pos = vertex.pos.plus(v);
        });
    }
}
