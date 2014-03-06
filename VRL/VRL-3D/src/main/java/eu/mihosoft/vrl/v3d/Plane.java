package eu.mihosoft.vrl.v3d;

// # class Plane

import java.util.ArrayList;
import java.util.List;

// Represents a plane in 3D space.
public class Plane {

    // `CSG.Plane.EPSILON` is the tolerance used by `splitPolygon()` to decide if a
    // point is on the plane.
    public static final double EPSILON = 1e-5;

    public Vector normal;
    public double w;

    public Plane(Vector normal, double w) {
        this.normal = normal;
        this.w = w;
    }

    public static Plane fromPoints(Vector a, Vector b, Vector c) {
        Vector n = b.minus(a).cross(c.minus(a)).unit();
        return new Plane(n, n.dot(a));
    }

    @Override
    public Plane clone() {
        return new Plane(normal.clone(), w);
    }

    public void flip() {
        normal = normal.negated();
        w = -w;
    }


    // Split `polygon` by this plane if needed, then put the polygon or polygon
    // fragments in the appropriate lists. Coplanar polygons go into either
    // `coplanarFront` or `coplanarBack` depending on their orientation with
    // respect to this plane. Polygons in front or in back of this plane go into
    // either `front` or `back`.
    public void splitPolygon(Polygon polygon, List<Polygon> coplanarFront, List<Polygon> coplanarBack, List<Polygon> front, List<Polygon> back) {
        final int COPLANAR = 0;
        final int FRONT = 1;
        final int BACK = 2;
        final int SPANNING = 3;


        // Classify each point as well as the entire polygon into one of the above
        // four classes.
        int polygonType = 0;
        List<Integer> types = new ArrayList<Integer>();
        for (int i = 0; i < polygon.vertices.size(); i++) {
            double t = this.normal.dot(polygon.vertices.get(i).pos) - this.w;
            int type = (t < -Plane.EPSILON) ? BACK : (t > Plane.EPSILON) ? FRONT : COPLANAR;
            polygonType |= type;
            types.add(type);
        }

        // Put the polygon in the correct list, splitting it when necessary.
        switch (polygonType) {
            case COPLANAR:
                (this.normal.dot(polygon.plane.normal) > 0 ? coplanarFront : coplanarBack).add(polygon);
                break;
            case FRONT:
                front.add(polygon);
                break;
            case BACK:
                back.add(polygon);
                break;
            case SPANNING:
                List<Vertex> f = new ArrayList<Vertex>();
                List<Vertex> b = new ArrayList<Vertex>();
                for (int i = 0; i < polygon.vertices.size(); i++) {
                    int j = (i + 1) % polygon.vertices.size();
                    int ti = types.get(i);
                    int tj = types.get(j);
                    Vertex vi = polygon.vertices.get(i);
                    Vertex vj = polygon.vertices.get(j);
                    if (ti != BACK) f.add(vi);
                    if (ti != FRONT) b.add(ti != BACK ? vi.clone() : vi);
                    if ((ti | tj) == SPANNING) {
                        double t = (this.w - this.normal.dot(vi.pos)) / this.normal.dot(vj.pos.minus(vi.pos));
                        Vertex v = vi.interpolate(vj, t);
                        f.add(v);
                        b.add(v.clone());
                    }
                }
                if (f.size() >= 3) front.add(new Polygon(f, polygon.shared));
                if (b.size() >= 3) back.add(new Polygon(b, polygon.shared));
                break;
        }
    }
}
