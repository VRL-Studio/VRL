/* 
 * AlphaAnimation.java
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

package eu.mihosoft.vrl.animation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Alpha animation.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class AlphaAnimation extends Animation implements FrameListener {
    private static final long serialVersionUID = -7840929640339176807L;

    private BufferedImage image;
    private LinearInterpolation alpha;

    /**
     * Constructor.
     * @param image the image to animate
     * @param start the start value
     * @param stop the stop value
     */
    public AlphaAnimation(BufferedImage image, float start, float stop) {
        this.image = image;
         addFrameListener(this);

        // add targets     
        alpha = new LinearInterpolation(start, stop);

        getInterpolators().add(alpha);

    }
    
    /**
     * Draws alpha mask on source image.
     * @param img source image
     * @return source image with new alpha mask
     */
    private BufferedImage alphaMask(BufferedImage img, float alpha) {
        BufferedImage result = 
                new BufferedImage(img.getWidth(), img.getHeight(), 
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = result.createGraphics();

        g2.drawImage(img, 0, 0, null);

        g2.setComposite(AlphaComposite.DstIn);

        g2.setColor(new Color(1.0f, 1.0f, 1.0f, alpha));

        g2.fillRect(0, 0, img.getWidth(), img.getHeight());
        g2.dispose();
        return result;
    }

    @Override
    public void frameStarted(double time) {
        Graphics2D g2 = image.createGraphics();
        float value = (float) alpha.getValue();
        g2.drawImage(alphaMask(image,value), 0, 0, null);
    }
}
