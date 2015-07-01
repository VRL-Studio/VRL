/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.v3d;

/**
 * Bounding box for CSGs.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Bounds {

    private final Vector3d center;
    private final Vector3d bounds;
    private final Vector3d min;
    private final Vector3d max;


    /**
     * Constructor.
     *
     * @param min min x,y,z values
     * @param max max x,y,z values
     */
    public Bounds(Vector3d min, Vector3d max) {
        this.center = new Vector3d(
                (max.x + min.x) / 2,
                (max.y + min.y) / 2,
                (max.z + min.z) / 2);

        this.bounds = new Vector3d(
                Math.abs(max.x - min.x),
                Math.abs(max.y - min.y),
                Math.abs(max.z - min.z));

        this.min = min.clone();
        this.max = max.clone();
    }

    @Override
    public Bounds clone() {
        return new Bounds(min.clone(), max.clone());
    }

    /**
     * Returns the position of the center.
     *
     * @return the center position
     */
    public Vector3d getCenter() {
        return center;
    }

    /**
     * Returns the bounds (width,height,depth).
     *
     * @return the bounds (width,height,depth)
     */
    public Vector3d getBounds() {
        return bounds;
    }



    /**
     * Indicates whether the specified point is contained within this bounding
     * box (check includes box boundary).
     *
     * @param v vertex to check
     * @return {@code true} if the point is contained within this bounding box;
     * {@code false} otherwise
     */
    public boolean contains(Vector3d v) {
        boolean inX = min.x <= v.x && v.x <= max.x;
        boolean inY = min.y <= v.y && v.y <= max.y;
        boolean inZ = min.z <= v.z && v.z <= max.z;

        return inX && inY && inZ;
    }

    /**
     * Indicates whether the specified bounding box intersects with this
     * bounding box (check includes box boundary).
     *
     * @param b box to check
     * @return {@code true} if the bounding box intersects this bounding box;
     * {@code false} otherwise
     */
    public boolean intersects(Bounds b) {

        if (b.getMin().x > this.getMax().x || b.getMax().x < this.getMin().x) {
            return false;
        }
        if (b.getMin().y > this.getMax().y || b.getMax().y < this.getMin().y) {
            return false;
        }
        if (b.getMin().z > this.getMax().z || b.getMax().z < this.getMin().z) {
            return false;
        }

        return true;

    }

    /**
     * @return the min x,y,z values
     */
    public Vector3d getMin() {
        return min;
    }

    /**
     * @return the max x,y,z values
     */
    public Vector3d getMax() {
        return max;
    }

    @Override
    public String toString() {
        return "[center: " + center + ", bounds: " + bounds + "]";
    }

}
