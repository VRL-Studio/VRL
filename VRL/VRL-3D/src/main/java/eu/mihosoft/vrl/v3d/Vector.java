package eu.mihosoft.vrl.v3d;

/**
 * 3D Vector.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Vector {

    public double x;
    public double y;
    public double z;

    /**
     * Creates a new vector.
     *
     * @param x x value
     * @param y y value
     * @param z z value
     */
    public Vector(double x, double y, double z) {

        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Vector clone() {
        return new Vector(x, y, z);
    }

    /**
     * Returns a negated copy of this vector.
     *
     * <b>Note:</b> this vector is not modified
     *
     * @return a negated copy of this vector
     */
    public Vector negated() {
        return new Vector(-x, -y, -z);
    }

    /**
     * Returns the sum of this vector and the specified vector.
     *
     * @param v the vector to add
     *
     * <b>Note:</b> this vector is not modified
     *
     * @return the sum of this vector and the specified vector
     */
    public Vector plus(Vector v) {
        return new Vector(x + v.x, y + v.y, z + v.z);
    }

    /**
     * Returns the difference of this vector and the specified vector.
     *
     * @param v the vector to subtract
     *
     * <b>Note:</b> this vector is not modified
     *
     * @return the difference of this vector and the specified vector
     */
    public Vector minus(Vector v) {
        return new Vector(x - v.x, y - v.y, z - v.z);
    }

    /**
     * Returns the product of this vector and the specified value.
     *
     * @param a the value
     *
     * <b>Note:</b> this vector is not modified
     *
     * @return the product of this vector and the specified value
     */
    public Vector times(double a) {
        return new Vector(x * a, y * a, z * a);
    }

    /**
     * Returns this vector devided by the specified value.
     *
     * @param a the value
     *
     * <b>Note:</b> this vector is not modified
     *
     * @return this vector devided by the specified value
     */
    public Vector dividedBy(double a) {
        return new Vector(x / a, y / a, z / a);
    }

    /**
     * Returns the dot product of this vector and the specified vector.
     * 
     * <b>Note:</b> this vector is not modified
     *
     * @param a the second vector
     * 
     * @return the dot product of this vector and the specified vector
     */
    public double dot(Vector a) {
        return this.x * a.x + this.y * a.y + this.z * a.z;
    }

    /**
     * 
     * @param a
     * @param t
     * @return
     */
    public Vector lerp(Vector a, double t) {
        return this.plus(a.minus(this).times(t));
    }

    /**
     * Returns the magnitude of this vector.
     *
     * <b>Note:</b> this vector is not modified
     *
     * @return the magnitude of this vector
     */
    public double length() {
        return Math.sqrt(this.dot(this));
    }

    /**
     * Returns a normalized copy of this vector with {@code length}.
     *
     * <b>Note:</b> this vector is not modified
     *
     * @return a normalized copy of this vector with {@code length}
     */
    public Vector unit() {
        return this.dividedBy(this.length());
    }

    /**
     * Returns the cross product of this vector and the specified vector.
     *
     * <b>Note:</b> this vector is not modified
     *
     * @param a the vector
     *
     * @return the cross product of this vector and the specified vector.
     */
    public Vector cross(Vector a) {
        return new Vector(
                this.y * a.z - this.z * a.y,
                this.z * a.x - this.x * a.z,
                this.x * a.y - this.y * a.x
        );
    }

   /**
     * Returns this vector in STL string format.
     * 
     * @return this vector in STL string format
     */
    public String toStlString() {
        return this.x + " " + this.y + " " + this.z;
    }

    /**
     * Applies the specified transformation to this vector.
     *
     * @param transform the transform to apply
     * @return this vector
     */
    public Vector transform(Transform transform) {
        return transform.transform(this);
    }

    /**
     * Returns a transformed copy of this vector.
     *
     * @param transform the transform to apply
     *
     * <b>Note:</b> this vector is not modified
     *
     * @return a transformed copy of this vector
     */
    public Vector transformed(Transform transform) {
        return clone().transform(transform);
    }

}
