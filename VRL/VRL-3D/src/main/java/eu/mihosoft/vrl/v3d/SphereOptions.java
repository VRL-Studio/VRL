package eu.mihosoft.vrl.v3d;

/**
 * @author nplekhanov
 */
public class SphereOptions {
    private Vector center = new Vector(0,0,0);
    private double radius = 1;
    private int slices = 16;
    private int stacks = 8;

    public void setCenter(Vector center) {
        this.center = center;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setSlices(int slices) {
        this.slices = slices;
    }

    public void setStacks(int stacks) {
        this.stacks = stacks;
    }

    public Vector getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    public int getSlices() {
        return slices;
    }

    public int getStacks() {
        return stacks;
    }
}
