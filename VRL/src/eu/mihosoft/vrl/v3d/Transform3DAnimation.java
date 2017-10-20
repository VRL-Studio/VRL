/* 
 * Transform3DAnimation.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2012 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2012 by Michael Hoffer
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

import eu.mihosoft.vrl.animation.Animation;
import eu.mihosoft.vrl.animation.AnimationInterpolation;
import eu.mihosoft.vrl.animation.FrameListener;
import eu.mihosoft.vrl.animation.LinearInterpolation;
import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix4d;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public abstract class Transform3DAnimation extends Animation implements FrameListener{
    private Transform3D transform3D = new Transform3D();
    private static int VALUE_SIZE = 16; // 4x4 matrix, this will never change

    public Transform3DAnimation() {
    }


    public Transform3DAnimation(Transform3D start, Transform3D stop) {
        init(start, stop);
    }

    public void init(Transform3D start, Transform3D stop) {
        double[] startValues = new double[VALUE_SIZE];
        start.get(startValues);
        double[] stopValues = new double[VALUE_SIZE];
        stop.get(stopValues);

        for (int i = 0; i < 16;i++) {
            addInterpolation(new LinearInterpolation(startValues[i], stopValues[i]));
        }

        addFrameListener(new FrameListener() {

            @Override
            public void frameStarted(double time) {
                assignValuesToTransfrom3D();
            }
        });

        addFrameListener(this);
    }



    private double[] getTargetValues() {
        double[] result = new double[VALUE_SIZE];
        for (int i = 0; i < 16; i++) {
            result[i] = getInterpolators().get(i).getValue();
        }
        return result;
    }

    private void assignValuesToTransfrom3D() {
        double[] values = getTargetValues();
        transform3D.set(values);
    }

    @Override
    public abstract void frameStarted(double time);

    /**
     * @return the t3d
     */
    public Transform3D getTransform3D() {
        return transform3D;
    }
}
