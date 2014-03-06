package eu.mihosoft.vrl.v3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Example usage:
//
//     var cube = CSG.cube();
//     var sphere = CSG.sphere({ radius: 1.3 });
//     var polygons = cube.subtract(sphere).toPolygons();
//
// ## Implementation Details
//
// All CSG operations are implemented in terms of two functions, `clipTo()` and
// `invert()`, which remove parts of a BSP tree inside another BSP tree and swap
// solid and empty space, respectively. To find the union of `a` and `b`, we
// want to remove everything in `a` inside `b` and everything in `b` inside `a`,
// then combine polygons from `a` and `b` into one solid:
//
//     a.clipTo(b);
//     b.clipTo(a);
//     a.build(b.allPolygons());
//
// The only tricky part is handling overlapping coplanar polygons in both trees.
// The code above keeps both copies, but we need to keep them in one tree and
// remove them in the other tree. To remove them from `b` we can clip the
// inverse of `b` against `a`. The code for union now looks like this:
//
//     a.clipTo(b);
//     b.clipTo(a);
//     b.invert();
//     b.clipTo(a);
//     b.invert();
//     a.build(b.allPolygons());
//
// Subtraction and intersection naturally follow from set operations. If
// union is `A | B`, subtraction is `A - B = ~(~A | B)` and intersection is
// `A & B = ~(~A | ~B)` where `~` is the complement operator.
//
// ## License
//
// Copyright (c) 2011 Evan Wallace (http://madebyevan.com/), under the MIT license.
// # class CSG
// Holds a binary space partition tree representing a 3D solid. Two solids can
// be combined using the `union()`, `subtract()`, and `intersect()` methods.
public class CSG {

    private List<Polygon> polygons;

    private CSG() {
    }

    // Construct a CSG solid from a list of `CSG.Polygon` instances.
    public static CSG fromPolygons(List<Polygon> polygons) {
        CSG csg = new CSG();
        csg.polygons = polygons;
        return csg;
    }

    @Override
    public CSG clone() {
        CSG csg = new CSG();
        csg.polygons = new ArrayList<>();
        for (Polygon polygon : polygons) {
            csg.polygons.add(polygon.clone());
        }
        return csg;
    }

    public List<Polygon> toPolygons() {
        return polygons;
    }

    // Return a new CSG solid representing space in either this solid or in the
    // solid `csg`. Neither this solid nor the solid `csg` are modified.
    //
    //     A.union(B)
    //
    //     +-------+            +-------+
    //     |       |            |       |
    //     |   A   |            |       |
    //     |    +--+----+   =   |       +----+
    //     +----+--+    |       +----+       |
    //          |   B   |            |       |
    //          |       |            |       |
    //          +-------+            +-------+
    //
    public CSG union(CSG csg) {
        Node a = new Node(this.clone().polygons);
        Node b = new Node(csg.clone().polygons);
        a.clipTo(b);
        b.clipTo(a);
        b.invert();
        b.clipTo(a);
        b.invert();
        a.build(b.allPolygons());
        return CSG.fromPolygons(a.allPolygons());
    }

    // Return a new CSG solid representing space in this solid but not in the
    // solid `csg`. Neither this solid nor the solid `csg` are modified.
    //
    //     A.subtract(B)
    //
    //     +-------+            +-------+
    //     |       |            |       |
    //     |   A   |            |       |
    //     |    +--+----+   =   |    +--+
    //     +----+--+    |       +----+
    //          |   B   |
    //          |       |
    //          +-------+
    //
    public CSG subtract(CSG csg) {
        Node a = new Node(this.clone().polygons);
        Node b = new Node(csg.clone().polygons);
        a.invert();
        a.clipTo(b);
        b.clipTo(a);
        b.invert();
        b.clipTo(a);
        b.invert();
        a.build(b.allPolygons());
        a.invert();
        return CSG.fromPolygons(a.allPolygons());
    }

    // Return a new CSG solid representing space both this solid and in the
    // solid `csg`. Neither this solid nor the solid `csg` are modified.
    //
    //     A.intersect(B)
    //
    //     +-------+
    //     |       |
    //     |   A   |
    //     |    +--+----+   =   +--+
    //     +----+--+    |       +--+
    //          |   B   |
    //          |       |
    //          +-------+
    //
    public CSG intersect(CSG csg) {
        Node a = new Node(this.clone().polygons);
        Node b = new Node(csg.clone().polygons);
        a.invert();
        b.clipTo(a);
        b.invert();
        a.clipTo(b);
        b.clipTo(a);
        a.build(b.allPolygons());
        a.invert();
        return CSG.fromPolygons(a.allPolygons());
    }

    // Return a new CSG solid with solid and empty space switched. This solid is
    // not modified.
    public CSG inverse() {
        CSG csg = this.clone();
        for (Polygon polygon : polygons) {
            polygon.flip();
        }
        return csg;
    }

    // Construct an axis-aligned solid cuboid. Optional parameters are `center` and
    // `radius`, which default to `[0, 0, 0]` and `[1, 1, 1]`. The radius can be
    // specified using a single number or a list of three numbers, one for each axis.
    //
    // Example code:
    //
    //     var cube = CSG.cube({
    //       center: [0, 0, 0],
    //       radius: 1
    //     });
    public static CSG cube(CubeOptions options) {

        Vector c = options.getCenter();
        double[] r = options.getRadius();

        int[][][] a = {
            {{0, 4, 6, 2}, {-1, 0, 0}},
            {{1, 3, 7, 5}, {+1, 0, 0}},
            {{0, 1, 5, 4}, {0, -1, 0}},
            {{2, 6, 7, 3}, {0, +1, 0}},
            {{0, 2, 3, 1}, {0, 0, -1}},
            {{4, 5, 7, 6}, {0, 0, +1}}
        };
        List<Polygon> polygons = new ArrayList<>();
        for (int[][] info : a) {
            List<Vertex> vertexes = new ArrayList<>();
            for (int i : info[0]) {
                Vector pos = new Vector(
                        c.x + r[0] * (2 * Utils.doubleNegate(i & 1) - 1),
                        c.y + r[1] * (2 * Utils.doubleNegate(i & 2) - 1),
                        c.z + r[2] * (2 * Utils.doubleNegate(i & 4) - 1)
                );
                vertexes.add(new Vertex(pos, new Vector(
                        (double) info[1][0],
                        (double) info[1][1],
                        (double) info[1][2]
                )));
            }
            polygons.add(new Polygon(vertexes, false));
        }
        return CSG.fromPolygons(polygons);
    }

    private static Vertex sphereVertex(Vector c, double r, double theta, double phi) {
        theta *= Math.PI * 2;
        phi *= Math.PI;
        Vector dir = new Vector(
                Math.cos(theta) * Math.sin(phi),
                Math.cos(phi),
                Math.sin(theta) * Math.sin(phi)
        );
        return new Vertex(c.plus(dir.times(r)), dir);
    }

    // Construct a solid sphere. Optional parameters are `center`, `radius`,
    // `slices`, and `stacks`, which default to `[0, 0, 0]`, `1`, `16`, and `8`.
    // The `slices` and `stacks` parameters control the tessellation along the
    // longitude and latitude directions.
    //
    // Example usage:
    //
    //     var sphere = CSG.sphere({
    //       center: [0, 0, 0],
    //       radius: 1,
    //       slices: 16,
    //       stacks: 8
    //     });
    public static CSG sphere(SphereOptions options) {
        final Vector c = options.getCenter();
        final double r = options.getRadius();
        int slices = options.getSlices();
        int stacks = options.getStacks();
        List<Polygon> polygons = new ArrayList<>();

        for (int i = 0; i < slices; i++) {
            for (int j = 0; j < stacks; j++) {
                final List<Vertex> vertices = new ArrayList<>();

                vertices.add(sphereVertex(c, r, i / (double) slices, j / (double) stacks));
                if (j > 0) {
                    vertices.add(sphereVertex(c, r, (i + 1) / (double) slices, j / (double) stacks));
                }
                if (j < stacks - 1) {
                    vertices.add(sphereVertex(c, r, (i + 1) / (double) slices, (j + 1) / (double) stacks));
                }
                vertices.add(sphereVertex(c, r, i / (double) slices, (j + 1) / (double) stacks));
                polygons.add(new Polygon(vertices, false));
            }
        }
        return CSG.fromPolygons(polygons);
    }

    private static Vertex cylPoint(
            Vector axisX, Vector axisY, Vector axisZ, Vector ray, Vector s,
            double r, double stack, double slice, double normalBlend) {
        double angle = slice * Math.PI * 2;
        Vector out = axisX.times(Math.cos(angle)).plus(axisY.times(Math.sin(angle)));
        Vector pos = s.plus(ray.times(stack)).plus(out.times(r));
        Vector normal = out.times(1.0 - Math.abs(normalBlend)).plus(axisZ.times(normalBlend));
        return new Vertex(pos, normal);
    }

    // Construct a solid cylinder. Optional parameters are `start`, `end`,
    // `radius`, and `slices`, which default to `[0, -1, 0]`, `[0, 1, 0]`, `1`, and
    // `16`. The `slices` parameter controls the tessellation.
    //
    // Example usage:
    //
    //     var cylinder = CSG.cylinder({
    //       start: [0, -1, 0],
    //       end: [0, 1, 0],
    //       radius: 1,
    //       slices: 16
    //     });
    public static CSG cylinder(CylinderOptions options) {

        final Vector s = options.getStart();
        Vector e = options.getEnd();
        final Vector ray = e.minus(s);
        final double r = options.getRadius();
        int slices = options.getSlices();
        final Vector axisZ = ray.unit();
        boolean isY = (Math.abs(axisZ.y) > 0.5);
        final Vector axisX = new Vector(isY ? 1 : 0, !isY ? 1 : 0, 0).cross(axisZ).unit();
        final Vector axisY = axisX.cross(axisZ).unit();
        Vertex start = new Vertex(s, axisZ.negated());
        Vertex end = new Vertex(e, axisZ.unit());
        List<Polygon> polygons = new ArrayList<>();

        for (int i = 0; i < slices; i++) {
            double t0 = i / (double) slices, t1 = (i + 1) / (double) slices;
            polygons.add(new Polygon(Arrays.asList(
                    start,
                    cylPoint(axisX, axisY, axisZ, ray, s, r, 0, t0, -1),
                    cylPoint(axisX, axisY, axisZ, ray, s, r, 0, t1, -1)),
                    false));
            polygons.add(new Polygon(Arrays.asList(
                    cylPoint(axisX, axisY, axisZ, ray, s, r, 0, t1, 0),
                    cylPoint(axisX, axisY, axisZ, ray, s, r, 0, t0, 0),
                    cylPoint(axisX, axisY, axisZ, ray, s, r, 1, t0, 0),
                    cylPoint(axisX, axisY, axisZ, ray, s, r, 1, t1, 0)),
                    false));
            polygons.add(new Polygon(
                    Arrays.asList(
                            end,
                            cylPoint(axisX, axisY, axisZ, ray, s, r, 1, t1, 1),
                            cylPoint(axisX, axisY, axisZ, ray, s, r, 1, t0, 1)),
                    false)
            );
        }
        return fromPolygons(polygons);
    }

    public String toStlString() {
        StringBuilder sb = new StringBuilder("solid v3d.csg\n");
        this.polygons.stream().forEach(
                (Polygon p) -> {
                    sb.append(p.toStlString()).append("\n");
                });
        sb.append("endsolid v3d.csg\n");
        return sb.toString();
    }
}
