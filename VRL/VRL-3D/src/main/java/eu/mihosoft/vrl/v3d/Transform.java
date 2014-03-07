/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.v3d;

import javax.vecmath.Matrix4d;

/**
 *
 * @author miho
 */
public class Transform {

    private Matrix4d m;

    public Transform() {
        m = new Matrix4d();
        m.m00 = 1;
        m.m11 = 1;
        m.m22 = 1;
        m.m33 = 1;
    }
    
    public static Transform unity() {
        return new Transform();
    }

    private Transform(Matrix4d m) {
        this.m = m;
    }

    // Create a rotation matrix for rotating around the x axis
    public Transform rotX(double degrees) {
        double radians = degrees * Math.PI * (1.0 / 180.0);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double elemenents[] = {
            1, 0, 0, 0, 0, cos, sin, 0, 0, -sin, cos, 0, 0, 0, 0, 1};
        m.mul(new Matrix4d(elemenents));
        return this;
    }

// Create a rotation matrix for rotating around the y axis
    public Transform rotY(double degrees) {
        double radians = degrees * Math.PI * (1.0 / 180.0);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double elemenents[] = {
            cos, 0, -sin, 0, 0, 1, 0, 0, sin, 0, cos, 0, 0, 0, 0, 1};
        m.mul(new Matrix4d(elemenents));
        return this;
    }

// Create a rotation matrix for rotating around the z axis
    public Transform rotZ(double degrees) {
        double radians = degrees * Math.PI * (1.0 / 180.0);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double elemenents[] = {
            cos, sin, 0, 0, -sin, cos, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
        m.mul(new Matrix4d(elemenents));
        return this;
    }

// Create an affine matrix for translate:
    public Transform translate(Vector vec) {
        // parse as CSG.Vector3D, so we can pass an array or a CSG.Vector3D
        double elemenents[] = {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, vec.x, vec.y, vec.z, 1};
        m.mul(new Matrix4d(elemenents));
        return this;
    }

// Create an affine matrix for mirroring into an arbitrary plane:
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

// Create an affine matrix for scaling:
    public Transform scale(Vector vec) {
        // parse as CSG.Vector3D, so we can pass an array or a CSG.Vector3D
        double elemenents[] = {
            vec.x, 0, 0, 0, 0, vec.y, 0, 0, 0, 0, vec.z, 0, 0, 0, 0, 1};
        m.mul(new Matrix4d(elemenents));
        return this;
    }
    
    Matrix4d getMat4d() {
        return m;
    }
    
    public Vector transform(Vector vec) {
        double x, y;
        x = m.m00*vec.x + m.m01*vec.y + m.m02*vec.z + m.m03;
        y = m.m10*vec.x + m.m11*vec.y + m.m12*vec.z + m.m13;
        vec.z =  m.m20*vec.x + m.m21*vec.y + m.m22*vec.z + m.m23;
        vec.x = x;
        vec.y = y;
        
        return vec;
    }
    
     /**
     * Performs an SVD normalization of the underlying matrix to calculate
     * and return the uniform scale factor. If the matrix has non-uniform
     * scale factors, the largest of the x, y, and z scale factors will
     * be returned. This transformation is not modified.
     * @return  the scale factor of this transformation
     */
    public double getScale() {
        return m.getScale();
    }
 
}
