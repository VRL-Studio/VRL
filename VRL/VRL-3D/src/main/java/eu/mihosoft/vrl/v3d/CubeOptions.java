package eu.mihosoft.vrl.v3d;

/**
 * @author nplekhanov
 */
public class CubeOptions {
    private Vector center = new Vector(0,0,0);
    private double[] radius = {1,1,1};

    public void setCenter(Vector center) {
        this.center = center;
    }

    public void setRadius(double... radius) {
        this.radius = radius;
    }

    public Vector getCenter() {
        return center;
    }

    public double[] getRadius() {
        return radius;
    }
}
