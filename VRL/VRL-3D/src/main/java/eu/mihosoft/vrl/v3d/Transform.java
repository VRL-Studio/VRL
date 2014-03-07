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

    Matrix4d m;

    public Transform() {

    }

    // Create a rotation matrix for rotating around the x axis
    public static Matrix4d rotationX(double degrees) {
        double radians = degrees * Math.PI * (1.0 / 180.0);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double elemenents[] = {
            1, 0, 0, 0, 0, cos, sin, 0, 0, -sin, cos, 0, 0, 0, 0, 1};
        return new Matrix4d(elemenents);
    }

// Create a rotation matrix for rotating around the y axis
    public static Matrix4d rotationY(double degrees) {
        double radians = degrees * Math.PI * (1.0 / 180.0);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double elemenents[] = {
            cos, 0, -sin, 0, 0, 1, 0, 0, sin, 0, cos, 0, 0, 0, 0, 1};
        return new Matrix4d(elemenents);
    }

// Create a rotation matrix for rotating around the z axis
    public static Matrix4d rotationZ(double degrees) {
        double radians = degrees * Math.PI * (1.0 / 180.0);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double elemenents[] = {
            cos, sin, 0, 0, -sin, cos, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
        return new Matrix4d(elemenents);
    }

// Create an affine matrix for translation:
    public static Matrix4d translation(Vector v) {
        // parse as CSG.Vector3D, so we can pass an array or a CSG.Vector3D
        Vector vec = v.clone();
        double elemenents[] = {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, vec.x, vec.y, vec.z, 1};
        return new Matrix4d(elemenents);
    }

// Create an affine matrix for mirroring into an arbitrary plane:
    public static Matrix4d mirror(Plane plane) {
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
        return new Matrix4d(elemenents);
    }

// Create an affine matrix for scaling:
    public static Matrix4d scale(Vector v) {
        // parse as CSG.Vector3D, so we can pass an array or a CSG.Vector3D
        Vector vec = v.clone();
        double elemenents[] = {
            vec.x, 0, 0, 0, 0, vec.y, 0, 0, 0, 0, vec.z, 0, 0, 0, 0, 1};
        return new Matrix4d(elemenents);
    }

}
