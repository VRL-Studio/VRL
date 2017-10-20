/* 
 * AppearanceGenerator.java
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

import eu.mihosoft.vrl.annotation.ParamInfo;
import java.awt.Color;
import java.io.Serializable;
import javax.media.j3d.*;
import javax.vecmath.Color3f;

/**
 * An appearance generator simplifies the process of generating java 3d
 * appearances.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class AppearanceGenerator implements Serializable {

    private static final long serialVersionUID = 6417649672007872680L;
    private static final int DEFAULT_VERTEX_COLORING_TYPE =
            Material.DIFFUSE;

    /**
     * Returns a solid appearance. This method is intended for combined
     * solid/wireframe appearances.
     *
     * @param color the color of the appearance
     * @param polygonOffset the polygon offset
     * @return the appearance
     * @see <a
     * href="http://www.opengl.org/resources/faq/technical/polygonoffset.htm">Official
     * OpenGL FAQ</a>
     */
    public Appearance getColoredAppearance(Color color, Float polygonOffset, int vertexColoringType, boolean volumeRendering) {
        Appearance appearance = new Appearance();

        if (!volumeRendering) {

            PolygonAttributes pa = new PolygonAttributes();
            pa.setCullFace(PolygonAttributes.CULL_NONE);  // see both sides of shape
            pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);
            // push back in z-buffer to prevent drawing bugs when combine solid
            // with wireframe
            // reference:
            //   http://www.opengl.org/resources/faq/technical/polygonoffset.htm
            pa.setBackFaceNormalFlip(true);
            pa.setPolygonOffset(polygonOffset);
            pa.setPolygonOffsetFactor(polygonOffset);
            appearance.setPolygonAttributes(pa);
        }

//        LineAttributes la = new LineAttributes();
//        la.setLineAntialiasingEnable(true);
//        la.setLineWidth(1.f);

//        a.setLineAttributes(la);

        Material mat = new Material();

        float r = color.getRed() / 255.f;
        float g = color.getGreen() / 255.f;
        float b = color.getBlue() / 255.f;
        float a = color.getAlpha() / 255.f;

       
//        if (volumeRendering) {
//            mat.setCapability(Material.ALLOW_COMPONENT_WRITE);
//            mat.setAmbientColor(r, g, b);
//            mat.setEmissiveColor(r, g, b);
//            mat.setSpecularColor(r, g, b);
//        }
//        else {
//            mat.setSpecularColor(1.f, 1.f, 1.f);
//        }
        
//        mat.setAmbientColor(r, g, b);
        
        
        mat.setSpecularColor(1.f, 1.f, 1.f);
        mat.setDiffuseColor(r, g, b); 
        
        mat.setShininess(20.f);

//        if (!volumeRendering) {
            mat.setLightingEnable(true);
//        }

        mat.setColorTarget(vertexColoringType);

        if (volumeRendering) {
            TransparencyAttributes ta = new TransparencyAttributes();
            ta.setTransparencyMode(TransparencyAttributes.BLEND_ZERO);
            ta.setTransparency(1.f - a);
            appearance.setTransparencyAttributes(ta);
        }

        appearance.setMaterial(mat);

        return appearance;
    }

    /**
     * Returns a solid appearance. This method is intended for combined
     * solid/wireframe appearances.
     *
     * @param color the color of the appearance
     * @param polygonOffset the polygon offset
     * @return the appearance
     * @see <a
     * href="http://www.opengl.org/resources/faq/technical/polygonoffset.htm">Official
     * OpenGL FAQ</a>
     */
    public Appearance getColoredAppearance(Color color, Float polygonOffset) {
        return getColoredAppearance(
                color, polygonOffset, DEFAULT_VERTEX_COLORING_TYPE, false);
    }

    /**
     * Returns a solid appearance. This method does not set custom polygon
     * offset. For combined solid/wireframe appearances use
     * {@link AppearanceGenerator#getColoredAppearance(java.awt.Color, java.lang.Float)}
     * instead.
     *
     * @param c the color of the appearance
     * @return the appearance
     */
    public Appearance getColoredAppearance(Color c, int vertexColoringType, boolean volumeRendering) {
        return getColoredAppearance(c, 0F, vertexColoringType, volumeRendering);
    }

    /**
     * Returns a solid appearance. This method does not set custom polygon
     * offset. For combined solid/wireframe appearances use
     * {@link AppearanceGenerator#getColoredAppearance(java.awt.Color, java.lang.Float)}
     * instead.
     *
     * @param c the color of the appearance
     * @return the appearance
     */
    public Appearance getColoredAppearance(Color c, boolean volumeRendering) {
        return getColoredAppearance(c, DEFAULT_VERTEX_COLORING_TYPE, volumeRendering);
    }

    /**
     * Returns a wired appearance.
     *
     * @param color the color of the appearance
     * @param lineWidth the width of the lines (wires)
     * @param lighting defines whether to enable lighting
     * @return the appearance
     */
    public Appearance getLinedAppearance(
            Color color, Float lineWidth, boolean lighting, int vertexColoringType) {
        Appearance a = new Appearance();
        PolygonAttributes pa = new PolygonAttributes();
        pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        pa.setCullFace(PolygonAttributes.CULL_NONE);
        pa.setBackFaceNormalFlip(true);
        a.setPolygonAttributes(pa);

        LineAttributes la = new LineAttributes();
        la.setLineAntialiasingEnable(true);
        la.setLineWidth(lineWidth);

        a.setLineAttributes(la);

        float r = color.getRed() / 255.f;
        float g = color.getGreen() / 255.f;
        float b = color.getBlue() / 255.f;

        if (lighting) {
            Material mat = new Material();
            mat.setDiffuseColor(r, g, b);
//        mat.setDiffuseColor(1.f, 0.4f, 0.1f);
            mat.setSpecularColor(1.f, 1.f, 1.f);
            mat.setShininess(20.f);

            mat.setColorTarget(vertexColoringType);

            mat.setLightingEnable(true);
            a.setMaterial(mat);
        } else {
            a.setColoringAttributes(
                    new ColoringAttributes(new Color3f(r, g, b),
                    ColoringAttributes.NICEST));
        }

        return a;
    }

    /**
     * Returns a wired appearance.
     *
     * @param color the color of the appearance
     * @param lineWidth the width of the lines (wires)
     * @param lighting defines whether to enable lighting
     * @return the appearance
     */
    public Appearance getLinedAppearance(
            Color color, Float lineWidth, boolean lighting) {
        return getLinedAppearance(color, lineWidth,
                lighting, DEFAULT_VERTEX_COLORING_TYPE);
    }

    /**
     * Returns a wired appearance without lighting.
     *
     * @param color the color of the appearance
     * @param lineWidth the width of the lines (wires)
     * @return the appearance
     */
    public Appearance getLinedAppearance(
            Color color, Float lineWidth, int vertexColoringType) {
        return getLinedAppearance(color, lineWidth, false, vertexColoringType);
    }

    /**
     * Returns a wired appearance without lighting.
     *
     * @param color the color of the appearance
     * @param lineWidth the width of the lines (wires)
     * @return the appearance
     */
    public Appearance getLinedAppearance(
            Color color, Float lineWidth) {
        return getLinedAppearance(
                color, lineWidth, DEFAULT_VERTEX_COLORING_TYPE);
    }

    /**
     * Returns a wired appearance without lighting and anti-aliasing.
     *
     * @param color the color of the appearance
     * @param lineWidth the width of the lines (wires)
     * @return the appearance
     */
    public Appearance getLinedAppearanceWithoutLighting(Color color,
            Float lineWidth) {
        Appearance a = new Appearance();
        PolygonAttributes pa = new PolygonAttributes();
        pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        pa.setCullFace(PolygonAttributes.CULL_NONE);
        pa.setBackFaceNormalFlip(true);
        a.setPolygonAttributes(pa);

        LineAttributes la = new LineAttributes();
        la.setLineAntialiasingEnable(false);
        la.setLineWidth(lineWidth);

        a.setLineAttributes(la);

        float r = color.getRed() / 255.f;
        float g = color.getGreen() / 255.f;
        float b = color.getBlue() / 255.f;

        ColoringAttributes ca =
                new ColoringAttributes(r, g, b, ColoringAttributes.SHADE_FLAT);
        a.setColoringAttributes(ca);

        return a;
    }

    /**
     * Returns a dotted appearance.
     *
     * @param color the color of the appearance
     * @param pointSize the size of the dots
     * @return the appearance
     */
    public Appearance getDottedAppearance(Color color, Float pointSize,
            int vertexColoringType) {
        Appearance a = new Appearance();
        PolygonAttributes pa = new PolygonAttributes();
        pa.setPolygonMode(PolygonAttributes.POLYGON_POINT);
        pa.setCullFace(PolygonAttributes.CULL_NONE);
        pa.setBackFaceNormalFlip(true);
        a.setPolygonAttributes(pa);

        PointAttributes pointAttribute = new PointAttributes();

        pointAttribute.setPointAntialiasingEnable(true);
        pointAttribute.setPointSize(pointSize);

        a.setPointAttributes(pointAttribute);

        Material mat = new Material();

        float r = color.getRed() / 255.f;
        float g = color.getGreen() / 255.f;
        float b = color.getBlue() / 255.f;

        mat.setDiffuseColor(r, g, b);
//        mat.setDiffuseColor(1.f, 0.4f, 0.1f);
        mat.setSpecularColor(1.f, 1.f, 1.f);
        mat.setShininess(20.f);

        mat.setColorTarget(vertexColoringType);

        mat.setLightingEnable(true);
        a.setMaterial(mat);

        return a;
    }

    /**
     * Returns a dotted appearance.
     *
     * @param color the color of the appearance
     * @param pointSize the size of the dots
     * @return the appearance
     */
    public Appearance getDottedAppearance(Color color, Float pointSize) {
        return getDottedAppearance(
                color, pointSize, DEFAULT_VERTEX_COLORING_TYPE);
    }

    /**
     * Joins appearances to an appearance array.
     *
     * @param a an appearance
     * @param b an appearance
     * @param c an appearance
     * @return the appearance array
     */
    public AppearanceArray join(
            @ParamInfo(name = "appearance", nullIsValid = true) Appearance a,
            @ParamInfo(name = "appearance", nullIsValid = true) Appearance b,
            @ParamInfo(name = "appearance", nullIsValid = true) Appearance c) {
        AppearanceArray array = new AppearanceArray();
        if (a != null) {
            array.add(a);
        }
        if (b != null) {
            array.add(b);
        }
        if (c != null) {
            array.add(c);
        }
        return array;
    }
}
