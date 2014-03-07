/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.v3d;

import javax.vecmath.Matrix4d;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Transform {

    private final Matrix4d m;

    /**
     * Constructor.
     *
     * Creates a unit transform.
     */
    public Transform() {
        m = new Matrix4d();
        m.m00 = 1;
        m.m11 = 1;
        m.m22 = 1;
        m.m33 = 1;
    }

    /**
     * Returns a new unity transform.
     *
     * @return unity transform
     */
    public static Transform unity() {
        return new Transform();
    }

    /**
     * Constructor.
     *
     * @param m matrix
     */
    private Transform(Matrix4d m) {
        this.m = m;
    }

    /**
     * Applies rotation around the x axis.
     *
     * @param degrees degrees
     * @return this transform
     */
    public Transform rotX(double degrees) {
        double radians = degrees * Math.PI * (1.0 / 180.0);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double elemenents[] = {
            1, 0, 0, 0, 0, cos, sin, 0, 0, -sin, cos, 0, 0, 0, 0, 1
        };
        m.mul(new Matrix4d(elemenents));
        return this;
    }

    /**
     * Applies rotation around the y axis.
     *
     * @param degrees degrees
     * @return this transform
     */
    public Transform rotY(double degrees) {
        double radians = degrees * Math.PI * (1.0 / 180.0);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double elemenents[] = {
            cos, 0, -sin, 0, 0, 1, 0, 0, sin, 0, cos, 0, 0, 0, 0, 1
        };
        m.mul(new Matrix4d(elemenents));
        return this;
    }

    /**
     * Applies rotation around the z axis.
     *
     * @param degrees degrees
     * @return this transform
     */
    public Transform rotZ(double degrees) {
        double radians = degrees * Math.PI * (1.0 / 180.0);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double elemenents[] = {
            cos, sin, 0, 0, -sin, cos, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1
        };
        m.mul(new Matrix4d(elemenents));
        return this;
    }

    /**
     * Applies translation to this transform.
     * @param vec translation vector
     * @return this transform
     */
    public Transform translate(Vector vec) {
        double elemenents[] = {
            1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, vec.x, vec.y, vec.z, 1
        };
        m.mul(new Matrix4d(elemenents));
        return this;
    }

    /**
     * Applies a mirror operation to this transform.
     * @param plane the plane that defines the mirror operation
     * @return this transform
     */
    public Transform mirror(Plane plane) {
        double nx = plane.normal.x;
        double ny = plane.normal.y;
        double nz = plane.normal.z;
        double w = plane.w;
        double elemenents[] = {
            (1.0 - 2.0 * nx * nx), (-2.0 * ny * nx), (-2.0 * nz * nx), 0,
            (-2.0 * nx * ny), (1.0 - 2.0 * ny * ny), (-2.0 * nz * ny), 0,
            (-2.0 * nx * nz), (-2.0 * ny * nz), (1.0 - 2.0 * nz * nz), 0,
            (-2.0 * nx * w), (-2.0 * ny * w), (-2.0 * nz * w), 1
        };
        m.mul(new Matrix4d(elemenents));
        return this;
    }

    /**
     * Applies a scale operation to this transform.
     * @param vec vector that specifies scale (x,y,z)
     * @return this transform
     */
    public Transform scale(Vector vec) {
        double elemenents[] = {
            vec.x, 0, 0, 0, 0, vec.y, 0, 0, 0, 0, vec.z, 0, 0, 0, 0, 1};
        m.mul(new Matrix4d(elemenents));
        return this;
    }

    /**
     * Applies this transform to the specified vector.
     *
     * @param vec vector to transform
     * @return the specified vector
     */
    public Vector transform(Vector vec) {
        double x, y;
        x = m.m00 * vec.x + m.m01 * vec.y + m.m02 * vec.z + m.m03;
        y = m.m10 * vec.x + m.m11 * vec.y + m.m12 * vec.z + m.m13;
        vec.z = m.m20 * vec.x + m.m21 * vec.y + m.m22 * vec.z + m.m23;
        vec.x = x;
        vec.y = y;

        return vec;
    }

    /**
     * Performs an SVD normalization of the underlying matrix to calculate and
     * return the uniform scale factor. If the matrix has non-uniform scale
     * factors, the largest of the x, y, and z scale factors will be returned.
     * This transformation is not modified.
     *
     * @return the scale factor of this transformation
     */
    public double getScale() {
        return m.getScale();
    }

}
