/* 
 * VOffscreenCanvas3D.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007–2018 by Michael Hoffer,
 * Copyright (c) 2015–2018 G-CSC, Uni Frankfurt,
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn)
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

package eu.mihosoft.vrl.types;

import java.awt.GraphicsConfigTemplate;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Screen3D;
import javax.swing.JComponent;

/**
 * Offscreen canvas for rendering Java 3D scenes.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VOffscreenCanvas3D extends Canvas3D {

    /**
     * Constructor.
     * @param gcIn the graphics configuration to use
     */
    public VOffscreenCanvas3D(GraphicsConfiguration gcIn) {
        super(gcIn, true);

        // 10.07.2011 Mac OS X is getting worse.
        // Since 10.6.8 we noticed several Java related anomalies.
        // Also openGL bindings have changed.
        //
        // The following configuration should fix Java3d related freezing bugs
        // concerning "J3D-RenderStructureUpdateThread-5" crashes (native code).
        // On OS X >= 10.6.8 screen must be initialized even if the offscreen
        // canvas is not used. The configuration is just a dummy configuration
        // that seems to fix our issues. This workaround seems to be unknown
        // Should we publish this?
        Screen3D sOff = getScreen3D();
        sOff.setSize(32, 20);
        sOff.setPhysicalScreenWidth(0.3); // correct value?
        sOff.setPhysicalScreenHeight(0.2);// correct value?
    }

    @Override
    public void postRender() {
    }

    /**
     * Renders the scene and returns the result as buffered image
     * @param width the width of the image
     * @param height the height of the image
     * @return the rendered scene as buffered image
     */
    public BufferedImage doRender(
            int width, int height) {

        Screen3D sOff = getScreen3D();
        sOff.setSize(width, height);
        sOff.setPhysicalScreenWidth(0.3); // correct value?
        sOff.setPhysicalScreenHeight(0.2);// correct value?

        BufferedImage bImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);

        ImageComponent2D buffer2D = new ImageComponent2D(
                ImageComponent.FORMAT_RGBA, bImage);

        setOffScreenBuffer(buffer2D);

        renderOffScreenBuffer();

        waitForOffScreenRendering();

        bImage = getOffScreenBuffer().getImage();

        return bImage;
    }
}
