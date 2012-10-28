/* 
 * Sphere.java
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
 * Third, add an additional notice, stating that you modified VRL. In addition
 * you must cite the publications listed below. A suitable notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Publications:
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, 2011, in press.
 */

package eu.mihosoft.vrl.v3d;

import java.awt.Color;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3f;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Sphere extends VGeometry3D {

    private double scale;

    public Sphere(double x, double y, double z, double scale) {
        init(x, y, z, scale, null);
    }

    public Sphere(double x, double y, double z, double scale, Color c) {
        init(x, y, z, scale, c);
    }

    private void init(double x, double y, double z, double scale, Color c) {
        this.scale = scale;

        OBJ2Geometry loader = new OBJ2Geometry();
        try {
            VTriangleArray t = loader.loadAsVTriangleArray(
                    getClass().getResourceAsStream(
                    "/eu/mihosoft/vrl/v3d/sphere-01.obj"));

            setGeometry(t);

            translate((float) x, (float) y, (float) z);

        } catch (IOException ex) {
            Logger.getLogger(Sphere.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        if (c == null) {
            c = new Color(0.8f, 0.6f, 0.3f);
        }

        setAppearance(new VGeometry3DAppearance(
                null, c, 0.f, false));
    }

    private void translate(float x, float y, float z) {

        Set<Node> alreadyConverted = new HashSet<Node>();

        Transform3D t3d = new Transform3D();

        t3d.setScale(scale);

        t3d.setTranslation(new Vector3f(x, y, z));

        for (Triangle t : getGeometry()) {

            if (!alreadyConverted.contains(t.getNodeOne())) {
                t3d.transform(t.getNodeOne().getLocation());
                alreadyConverted.add(t.getNodeOne());
            }
            if (!alreadyConverted.contains(t.getNodeTwo())) {
                t3d.transform(t.getNodeTwo().getLocation());
                alreadyConverted.add(t.getNodeTwo());
            }
            if (!alreadyConverted.contains(t.getNodeThree())) {
                t3d.transform(t.getNodeThree().getLocation());
                alreadyConverted.add(t.getNodeThree());
            }

        }
    }
}
