package eu.mihosoft.vrl.v3d;

/**
 * @author nplekhanov
 */
public class CylinderOptions {
    private Vector start = new Vector(0, -1, 0);
    private Vector end = new Vector(0, 1, 0);
    private double radius = 1;
    private int slices = 16;

    public Vector getStart() {
        return start;
    }

    public void setStart(Vector start) {
        this.start = start;
    }

    public Vector getEnd() {
        return end;
    }

    public void setEnd(Vector end) {
        this.end = end;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getSlices() {
        return slices;
    }

    public void setSlices(int slices) {
        this.slices = slices;
    }
}
