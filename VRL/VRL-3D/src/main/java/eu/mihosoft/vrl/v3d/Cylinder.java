/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.v3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A solid cylinder.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Cylinder implements Primitive{
    
    private Vector start;
    private Vector end;
    private double radius;
    private int numSlices;

    /**
     * Constructor.
     */
    public Cylinder() {
        this.start = new Vector(0, -1, 0);
        this.end = new Vector(0, 1, 0);
        this.radius = 1;
        this.numSlices = 16;
    }

    /**
     * Constructor.
     * 
     * @param start 
     * @param end
     * @param radius
     * @param numSlices 
     */
    public Cylinder(Vector start, Vector end, double radius, int numSlices) {
        this.start = start;
        this.end = end;
        this.radius = radius;
        this.numSlices = numSlices;
    }

    @Override
    public List<Polygon> toPolygons() {
        final Vector s = getStart();
        Vector e = getEnd();
        final Vector ray = e.minus(s);
        final double r = getRadius();
        int numSlices = getNumSlices();
        final Vector axisZ = ray.unit();
        boolean isY = (Math.abs(axisZ.y) > 0.5);
        final Vector axisX = new Vector(isY ? 1 : 0, !isY ? 1 : 0, 0).cross(axisZ).unit();
        final Vector axisY = axisX.cross(axisZ).unit();
        Vertex startV = new Vertex(s, axisZ.negated());
        Vertex endV = new Vertex(e, axisZ.unit());
        List<Polygon> polygons = new ArrayList<>();

        for (int i = 0; i < numSlices; i++) {
            double t0 = i / (double) numSlices, t1 = (i + 1) / (double) numSlices;
            polygons.add(new Polygon(Arrays.asList(
                    startV,
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
                            endV,
                            cylPoint(axisX, axisY, axisZ, ray, s, r, 1, t1, 1),
                            cylPoint(axisX, axisY, axisZ, ray, s, r, 1, t0, 1)),
                    false)
            );
        }
        
        return polygons;
    }
    
    private Vertex cylPoint(
            Vector axisX, Vector axisY, Vector axisZ, Vector ray, Vector s,
            double r, double stack, double slice, double normalBlend) {
        double angle = slice * Math.PI * 2;
        Vector out = axisX.times(Math.cos(angle)).plus(axisY.times(Math.sin(angle)));
        Vector pos = s.plus(ray.times(stack)).plus(out.times(r));
        Vector normal = out.times(1.0 - Math.abs(normalBlend)).plus(axisZ.times(normalBlend));
        return new Vertex(pos, normal);
    }

    /**
     * @return the start
     */
    public Vector getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(Vector start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public Vector getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(Vector end) {
        this.end = end;
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

    
}
