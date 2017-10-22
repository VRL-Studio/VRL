/* 
 * V3DMath.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2007–2017 by Michael Hoffer
 * 
 * This file is part of Visual Reflection Library (VRL).
 *
 * VRL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * see: http://opensource.org/licenses/LGPL-3.0
 *      file://path/to/VRL/src/eu/mihosoft/vrl/resources/license/lgplv3.txt
 *
 * VRL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * This version of VRL includes copyright notice and attribution requirements.
 * According to the LGPL this information must be displayed even if you modify
 * the source code of VRL. Neither the VRL Canvas attribution icon nor any
 * copyright statement/attribution may be removed.
 *
 * Attribution Requirements:
 *
 * If you create derived work you must do three things regarding copyright
 * notice and author attribution.
 *
 * First, the following text must be displayed on the Canvas:
 * "based on VRL source code". In this case the VRL canvas icon must be removed.
 * 
 * Second, the copyright notice must remain. It must be reproduced in any
 * program that uses VRL.
 *
 * Third, add an additional notice, stating that you modified VRL. A suitable
 * notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Please cite the publication(s) listed below.
 *
 * Publications:
 *
 * M. Hoffer, C. Poliwoda, & G. Wittum. (2013). Visual reflection library:
 * a framework for declarative GUI programming on the Java platform.
 * Computing and Visualization in Science, 2013, 16(4),
 * 181–192. http://doi.org/10.1007/s00791-014-0230-y
 */

package eu.mihosoft.vrl.v3d;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

/**
 * This class provides functions to easily translate and rotate java 3d objects.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class V3DMath {

    /**
     * Defines the x axis.
     */
    public static Vector3d X_AXIS = new Vector3d(1, 0, 0);
    /**
     * Defines the y axis.
     */
    public static Vector3d Y_AXIS = new Vector3d(0, 1, 0);
    /**
     * Defines the z axis.
     */
    public static Vector3d Z_AXIS = new Vector3d(0, 0, 1);

    /**
     * Returns the global coordinates of a given transform object.
     * @param t3d the transform object
     * @param globalT3d the local coordinates to virtual world coordinates
     *        transform object
     * @return the global coordinates of the specified transform object
     */
    public static Point3d getGlobalCoordinates(
            Transform3D t3d, Transform3D globalT3d) {
        Point3d result = new Point3d();
        Vector3d transVec = new Vector3d();

        t3d.get(transVec);
        result.set(transVec);
        globalT3d.transform(result);

        return result;
    }

    /**
     * Returns the global coordinates of a given transform group.
     * @param tGroup the transform group
     * @return the global coordinates of the given transform group
     */
    public static Point3d getGlobalCoordinates(TransformGroup tGroup) {
        Transform3D globalT3d = new Transform3D();
        Transform3D t3d = new Transform3D();

        tGroup.getLocalToVworld(globalT3d);
        tGroup.getTransform(t3d);

        return getGlobalCoordinates(t3d, globalT3d);
    }

    /**
     * Applies a translation to a given transform object.
     * @param t3d the transform object to translate
     * @param transVec the vector that describes the translation
     */
    public static void translate(Transform3D t3d, Vector3d transVec) {
        Transform3D translationT3d = new Transform3D();
        translationT3d.setTranslation(transVec);
        t3d.mul(translationT3d);
    }

    /**
     * Translates a given transform group.
     * @param tGroup the transform group to translate
     * @param transVec the vector that describes the translation
     */
    public static void translate(TransformGroup tGroup, Vector3d transVec) {
        Transform3D t3d = new Transform3D();
        tGroup.getTransform(t3d);
        translate(t3d, transVec);
        tGroup.setTransform(t3d);
    }

    /**
     * Applies a rotation to a given transform object.
     * @param t3d the transform object to rotate
     * @param rot the rotation as axis-angle (x,y,z,angle)
     *
     */
    public static void rotate(Transform3D t3d, AxisAngle4d rot) {
        Transform3D rotT3d = new Transform3D();

        rotT3d.setRotation(rot);

        t3d.mul(rotT3d);
    }

    /**
     * Applies a rotation to a given transform object.
     * @param t3d the transform object to rotate
     * @param axis the rotation axis
     * @param angle the rotation angle in radians
     */
    public static void rotate(Transform3D t3d, Vector3d axis, double angle) {
        rotate(t3d, new AxisAngle4d(axis, angle));
    }

    /**
     * Rotates a given point around the origin.
     * @param p the point to rotate
     * @param rot the rotation as axis-angle (x,y,z,angle)
     */
    public static void rotate(Point3d p, AxisAngle4d rot) {
        Transform3D t3d = new Transform3D();
        t3d.setRotation(rot);
        t3d.transform(p);
    }

    /**
     * Rotates a given point around the origin.
     * @param p the point to rotate
     * @param axis the rotation axis
     * @param angle the rotation angle in radians
     */
    public static void rotate(Point3d p, Vector3d axis, double angle) {
        rotate(p, new AxisAngle4d(axis, angle));
    }

    /**
     * Rotates a given transform group.
     * @param tGroup the transform group to rotate
     * @param rot the rotation as axis-angle (x,y,z,angle)
     */
    public static void rotate(
            TransformGroup tGroup, AxisAngle4d rot) {
        Transform3D t3d = new Transform3D();

        tGroup.getTransform(t3d);
        rotate(t3d, rot);

        tGroup.setTransform(t3d);
    }

    /**
     * Rotates a given transform group.
     * @param tGroup the transform group to rotate
     * @param axis the rotation axis
     * @param angle the rotation angle in radians
     */
    public static void rotate(
            TransformGroup tGroup, Vector3d axis, double angle) {
        rotate(tGroup, new AxisAngle4d(axis, angle));
    }

    /**
     * <p>
     * Transforms a given global vector from global to local coordinate system
     * of the given transform group node. The transformation affects just the
     * rotational part of the transformation. It is assumed that the vector is a
     * direction vector.
     * </p>
     * <p>
     * A use case of this global to local transformation could be the following
     * situation:
     * </p>
     * <p>
     * a child node of the given transform group node shall be translated. This
     * has to be done in the local coordinate system of the transform group. But
     * the given translation vector uses global coordinates.
     * </p>
     * <p>
     * To express the translation in local coordinates it is necessary to
     * transform the vector to the local coordinate system which is basically
     * equivalent to the elimination of the rotation of the transform group.
     * This is identical to the rotation of the local coordinate system relative
     * to the global coordinate system.
     * </p>
     * @param vec the vector to transform
     * @param localGroup the transform group
     */
    public static void globalToLocal(Vector3d vec, TransformGroup localGroup) {

        // get transform object from localGroup
        Transform3D transGroupTransform = new Transform3D();
        localGroup.getTransform(transGroupTransform);

        // get rotation of localGroup transform
        Quat4d rotation = new Quat4d();
        transGroupTransform.get(rotation);

        // invert rotation angle
        AxisAngle4d rotation2 = new AxisAngle4d();
        rotation2.set(rotation);
        
        // Mac OS X may show an error. But it compiles anyway.
        //
        // Reason:
        //
        // This is because Mac OS X comes with its own Java3D
        // version. Some IDEs have it in their classpath.
        //
        // See https://github.com/miho/VRL/issues/15 for further reference
        //
        double angle = rotation2.getAngle(); 
        rotation2.setAngle(-angle);

        // set inverted rotation
        transGroupTransform.setRotation(rotation2);

        // apply the transformation
        transGroupTransform.transform(vec);
    }
}
