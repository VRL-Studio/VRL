package eu.mihosoft.vrl.v3d;

// # class Vector
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

// Represents a 3D vector.
//
// Example usage:
//
//     new CSG.Vector(1, 2, 3);
//     new CSG.Vector([1, 2, 3]);
//     new CSG.Vector({ x: 1, y: 2, z: 3 });
public class Vector {

    public double x;
    public double y;
    public double z;

    public Vector(double x, double y, double z) {

        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Vector clone() {
        return new Vector(x, y, z);
    }

    public Vector negated() {
        return new Vector(-x, -y, -z);
    }

    public Vector plus(Vector v) {
        return new Vector(x + v.x, y + v.y, z + v.z);
    }

    public Vector minus(Vector v) {
        return new Vector(x - v.x, y - v.y, z - v.z);
    }

    public Vector times(double a) {
        return new Vector(x * a, y * a, z * a);
    }

    public Vector dividedBy(double a) {
        return new Vector(x / a, y / a, z / a);
    }

    public double dot(Vector a) {
        return this.x * a.x + this.y * a.y + this.z * a.z;
    }

    public Vector lerp(Vector a, double t) {
        return this.plus(a.minus(this).times(t));
    }

    public double length() {
        return Math.sqrt(this.dot(this));
    }

    public Vector unit() {
        return this.dividedBy(this.length());
    }

    public Vector cross(Vector a) {
        return new Vector(
                this.y * a.z - this.z * a.y,
                this.z * a.x - this.x * a.z,
                this.x * a.y - this.y * a.x
        );
    }

    public String toStlString() {
        return this.x + " " + this.y + " " + this.z;
    }

    public Vector transform(Matrix4d matrix4x4) {

        Vector3d vec = new Vector3d(x, y, z);
        matrix4x4.transform(vec);

        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;

        return this;
    }

    public Vector transformed(Matrix4d matrix4x4) {
        return clone().transform(matrix4x4);
    }

}
