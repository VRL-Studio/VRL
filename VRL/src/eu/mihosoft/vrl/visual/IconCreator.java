/* 
 * IconCreator.java
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

package eu.mihosoft.vrl.visual;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class IconCreator {
//    private BufferedImage image;
//    public IconCreator(BufferedImage img) {
//        this.image = img;
//    }

    public BufferedImage process(BufferedImage image, int size) {

        int insets = size / 12;

        BufferedImage result =
                ImageUtils.createCompatibleImage(
                size + insets * 2, size + insets * 2);

        Graphics2D g2 = result.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

        // paint scaled image
        Image tmp = image.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        g2.drawImage(tmp, insets, insets, null);

        // paint icon frame
        g2.setColor(Color.black);
//        g2.setStroke(new BasicStroke(insets));

        Shape outer = new RoundRectangle2D.Double(
                0, 0, size + insets * 2,
                size + insets * 2,
                insets*1.6, insets*1.6);

        Shape inner = new RoundRectangle2D.Double(
                insets, insets,
                size, size, insets*0.8, insets*0.8);

        Area a1 = new Area(outer);
        Area a2 = new Area(inner);

        a1.subtract(a2);

        g2.setColor(new Color(40,40,40,255));

        g2.fill(a1);

        g2.setColor(new Color(255,255,255,255));
        g2.setStroke(new BasicStroke(3));

        float thickness = 2;

        outer = new RoundRectangle2D.Double(
                thickness/2, thickness/2, size + insets * 2-thickness-1,
                size + insets * 2 - thickness-1,
                insets*1.2, insets*1.2);

        inner = new RoundRectangle2D.Double(
                insets + thickness/2, insets + thickness / 2,
                size - thickness-1, size - thickness-1, insets*0.5, insets*0.5);

        g2.draw(outer);
        g2.draw(inner);

        g2.dispose();

        return result;
    }
}
