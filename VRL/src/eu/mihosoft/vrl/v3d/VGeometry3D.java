/* 
 * VGeometry3D.java
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

import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.visual.VGraphicsUtil;
import java.awt.Color;
import java.io.Serializable;
import java.util.Optional;
import javafx.scene.paint.PhongMaterial;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;

/**
 * Represents a 3D Geometry.
 * <p>
 * <b>Note:</b> the memory footprint of VGeometry3D based geometries is
 * significantly higher than using Shape3D. Therefore, do not use it for highly
 * complex geometries (#Triangles > 10^5). Furthermore, VGeometry3D is very
 * limited. It only supports triangles. Please consider an external
 * visualization solution, such as VTK.</p>
 * <p>
 * However, VGeometry3D is highly useful for small visualizations, e.g.,
 * function plotters that are directly implemented inside VRL projects.</p>
 *
 * @see: http://www.vtk.org/
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@ObjectInfo(serializeParam = false)
public class VGeometry3D implements Serializable {

    private static final long serialVersionUID = 1L;
    private VGeometry3DAppearance appearance;
    private VTriangleArray geometry;
    private double[] orientation;
    public static final double[] DEFAULT_LOCATION = new double[16];

    public VGeometry3D() {
        setAppearance(new VGeometry3DAppearance());
    }

    public VGeometry3D(VTriangleArray geometry) {
        setAppearance(new VGeometry3DAppearance());
        setGeometry(geometry);
    }

    public VGeometry3D(VTriangleArray geometry,
            Color solidColor,
            Color wireColor,
            float wireThickness,
            boolean lighting) {

        setAppearance(new VGeometry3DAppearance(wireColor,
                solidColor, wireThickness, lighting));

        this.geometry = geometry;
    }

    public VGeometry3D(VTriangleArray geometry,
            Color solidColor,
            Color wireColor,
            float wireThickness,
            boolean lighting,
            boolean vertexColoring,
            boolean volumeRendering) {

        setAppearance(new VGeometry3DAppearance(wireColor,
                solidColor, wireThickness, lighting, vertexColoring,
                Material.DIFFUSE, volumeRendering));

        this.geometry = geometry;
    }

    public VGeometry3D(VTriangleArray geometry,
            Color solidColor,
            Color wireColor,
            float wireThickness,
            boolean lighting,
            boolean vertexColoring,
            int vertexColoringType,
            boolean volumeRendering) {

        setAppearance(new VGeometry3DAppearance(wireColor,
                solidColor, wireThickness, lighting, vertexColoring,
                vertexColoringType, volumeRendering));

        this.geometry = geometry;
    }

    public VGeometry3D(VTriangleArray geometry,
            VGeometry3DAppearance appearance) {
        setGeometry(geometry);
        setAppearance(appearance);
    }

    public void setGeometry(VTriangleArray geometry) {
        this.geometry = geometry;
    }

    public VTriangleArray getGeometry() {
        return geometry;
    }

    public Optional<JFXMeshContainer> generateJavaFXNode() {

        if (VGraphicsUtil.NO_3D) {
            return Optional.empty();
        }

        boolean solid = getAppearance().getSolidColor() != null;
        boolean wire = getAppearance().getWireColor() != null;
        boolean vertexColoring = getAppearance().isVertexColoring();

        javafx.scene.paint.Material mat;
        
        

        if (solid) {
            javafx.scene.paint.PhongMaterial phongMat = new PhongMaterial();
            Color solidCol = getAppearance().getSolidColor();
            
            phongMat.setDiffuseColor(
                    javafx.scene.paint.Color.rgb(
                            solidCol.getRed(),
                            solidCol.getGreen(),
                            solidCol.getBlue(),
                            solidCol.getAlpha())
            );
        }

        return Optional.of(getGeometry().getJFXTriangleMesh(vertexColoring, mat));
    }

    public Shape3DArray generateShape3DArray() {
        AppearanceGenerator aG = new AppearanceGenerator();

        Shape3DArray result = new Shape3DArray();

        if (VGraphicsUtil.NO_3D) {
            return result;
        }

        boolean onlySolid = getAppearance().getSolidColor() != null && getAppearance().getWireColor() == null;
        boolean onlyWire = getAppearance().getWireColor() != null && getAppearance().getSolidColor() == null;
        boolean nothing = getAppearance().getWireColor() == null & getAppearance().getSolidColor() == null;
        boolean solidAndWire = !nothing && !onlySolid && !onlyWire;

        float thickness = 0.5F;

        if (getAppearance().getWireThickness() != null) {
            thickness = getAppearance().getWireThickness();
        }

        if (onlySolid) {
            result.add(new Shape3D(getGeometry().getTriangleArray(
                    getAppearance().isVertexColoring()),
                    aG.getColoredAppearance(getAppearance().getSolidColor(),
                            getAppearance().isVolumeRendering())));
        } else if (onlyWire) {
            result.add(new Shape3D(getGeometry().getTriangleArray(false),
                    aG.getLinedAppearance(getAppearance().getWireColor(),
                            thickness, getAppearance().getLighting())));
        } else if (solidAndWire) {
            if (!getAppearance().isVertexColoring()) {
                result.add(new Shape3D(getGeometry().getTriangleArray(false),
                        aG.getLinedAppearance(getAppearance().getWireColor(),
                                thickness, getAppearance().getLighting())));

                result.add(new Shape3D(getGeometry().getTriangleArray(
                        getAppearance().isVertexColoring()),
                        aG.getColoredAppearance(getAppearance().getSolidColor(), 1F)));
            } else {
                result.add(new Shape3D(getGeometry().getTriangleArray(
                        getAppearance().isVertexColoring()),
                        aG.getColoredAppearance(getAppearance().getSolidColor(),
                                getAppearance().isVolumeRendering())));
            }
        } else if (nothing) {

            if (!getAppearance().isVertexColoring()) {
                result.add(new Shape3D(getGeometry().getTriangleArray(false),
                        aG.getLinedAppearance(Color.black,
                                thickness, getAppearance().getLighting())));

                result.add(new Shape3D(getGeometry().getTriangleArray(
                        getAppearance().isVertexColoring()),
                        aG.getColoredAppearance(Color.white, 1F)));
            } else {
                result.add(new Shape3D(getGeometry().getTriangleArray(
                        getAppearance().isVertexColoring()),
                        aG.getColoredAppearance(Color.white, false)));
            }
        }

        return result;
    }

    /**
     * @return the appearance
     */
    public VGeometry3DAppearance getAppearance() {
        return appearance;
    }

    /**
     * @param appearance the appearance to set
     */
    public void setAppearance(VGeometry3DAppearance appearance) {
        this.appearance = appearance;
    }

    /**
     * @return the orientation
     */
    public double[] getOrientation() {
        return orientation;
    }

    /**
     * @param orientation the orientation to set
     */
    public void setOrientation(double[] orientation) {
        this.orientation = orientation;
    }

    public void resetOrientation() {
        this.orientation = DEFAULT_LOCATION;
    }
}
