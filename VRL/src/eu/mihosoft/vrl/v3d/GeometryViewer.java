/* 
 * GeometryViewer.java
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

import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.v3d.AppearanceArray;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import java.awt.Color;
import java.io.Serializable;
import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;

/**
 * Shows java 3d geometries.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class GeometryViewer implements Serializable {
    private static final long serialVersionUID = 8897955487677292219L;

    private boolean wireView;

    /**
     * Converts a geometry into a shape.
     * @param g the geometry
     * @param appearances the appearance
     * @return the shape 
     */
    @MethodInfo(name = "view geometry:", valueName = " ")
    public Shape3DArray viewGeometry(
            Geometry g,
            @ParamInfo(name = "appearances (optional):",
	    nullIsValid = true) AppearanceArray appearances) {

        Shape3DArray result = new Shape3DArray();

        if (appearances == null) {
            if (isWireView()) {
                result.add(new Shape3D(g, getLinedAppearance(Color.black)));
            }
            result.add(new Shape3D(g, getColoredAppearance()));
        } else {
            for (Appearance a : appearances) {
                result.add(new Shape3D(g, a));
            }
        }

        return result;
    }

    /**
     * Returns a wired appearance.
     * @param color the color of the appearance
     * @return the appearance
     */
    private Appearance getLinedAppearance(Color color) {
        Appearance a = new Appearance();
        PolygonAttributes pa = new PolygonAttributes();
        pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        pa.setCullFace(PolygonAttributes.CULL_NONE);  // see both sides of shape
        pa.setBackFaceNormalFlip(true);
        a.setPolygonAttributes(pa);

        LineAttributes la = new LineAttributes();
        la.setLineAntialiasingEnable(true);
        la.setLineWidth(0.1f);

        a.setLineAttributes(la);

        Material mat = new Material();

        float r = color.getRed() / 255.f;
        float g = color.getGreen() / 255.f;
        float b = color.getBlue() / 255.f;

        mat.setDiffuseColor(r, g, b);
//        mat.setDiffuseColor(1.f, 0.4f, 0.1f);
        mat.setSpecularColor(1.f, 1.f, 1.f);
        mat.setShininess(20.f);

        mat.setLightingEnable(true);
        a.setMaterial(mat);

        return a;
    }

    /**
     * Returns a solid appearance.
     * @return the appearance
     */
    private Appearance getColoredAppearance() {
        Appearance a = new Appearance();
        PolygonAttributes pa = new PolygonAttributes();
        pa.setCullFace(PolygonAttributes.CULL_NONE);  // see both sides of shape
        pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);
        pa.setBackFaceNormalFlip(true);
        a.setPolygonAttributes(pa);

        LineAttributes la = new LineAttributes();
        la.setLineAntialiasingEnable(true);
        la.setLineWidth(1.f);

        a.setLineAttributes(la);

        Material mat = new Material();

        mat.setSpecularColor(1.f, 1.f, 0.8f);
        mat.setDiffuseColor(1.f, 0.4f, 0.1f);
        mat.setShininess(20.f);

        mat.setLightingEnable(true);
        a.setMaterial(mat);

        return a;
    }

    /**
     * Indicates whether to show wires (in default mode).
     * @return <code>true</code> if wires are to be shown; <code>false</code>
     *         otherwise
     */
    @MethodInfo(hide = true)
    public boolean isWireView() {
        return wireView;
    }

    /**
     * Defines whether to show wires (in default mode).
     * @param wireView the state to set
     */
    @MethodInfo(hide = true)
    public void setWireView(boolean wireView) {
        this.wireView = wireView;
    }
}
