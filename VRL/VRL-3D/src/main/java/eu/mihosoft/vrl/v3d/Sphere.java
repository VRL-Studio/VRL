/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.v3d;

import java.util.ArrayList;
import java.util.List;

/**
 * A solid sphere.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Sphere implements Primitive {

    private Vector center;
    private double radius;
    private int numSlices;
    private int numStacks;

    /**
     * Constructor.
     *
     */
    public Sphere() {
        init();
    }

    public Sphere(double radius) {
        init();
        this.radius = radius;
    }

    public Sphere(Vector center, double radius, int numSlices, int numStacks) {
        this.center = center;
        this.radius = radius;
        this.numSlices = numSlices;
        this.numStacks = numStacks;
    }

    private void init() {
        center = new Vector(0, 0, 0);
        radius = 1;
        numSlices = 16;
        numStacks = 8;
    }

    private Vertex sphereVertex(Vector c, double r, double theta, double phi) {
        theta *= Math.PI * 2;
        phi *= Math.PI;
        Vector dir = new Vector(
                Math.cos(theta) * Math.sin(phi),
                Math.cos(phi),
                Math.sin(theta) * Math.sin(phi)
        );
        return new Vertex(c.plus(dir.times(r)), dir);
    }

    @Override
    public List<Polygon> toPolygons() {
        final Vector c = getCenter();
        final double r = getRadius();
        int slices = getNumSlices();
        int stacks = getNumStacks();
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
        return polygons;
    }

    /**
     * @return the center
     */
    public Vector getCenter() {
        return center;
    }

    /**
     * @param center the center to set
     */
    public void setCenter(Vector center) {
        this.center = center;
    }

    /**
     * @return the radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * @param radius the radius to set
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * @return the numSlices
     */
    public int getNumSlices() {
        return numSlices;
    }

    /**
     * @param numSlices the numSlices to set
     */
    public void setNumSlices(int numSlices) {
        this.numSlices = numSlices;
    }

    /**
     * @return the numStacks
     */
    public int getNumStacks() {
        return numStacks;
    }

    /**
     * @param numStacks the numStacks to set
     */
    public void setNumStacks(int numStacks) {
        this.numStacks = numStacks;
    }

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

