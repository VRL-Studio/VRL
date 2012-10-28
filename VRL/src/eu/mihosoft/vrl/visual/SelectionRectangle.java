/* 
 * SelectionRectangle.java
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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public final class SelectionRectangle implements GlobalForegroundPainter {

    private Rectangle bounds;
    private Canvas canvas;
    public static final String FILL_COLOR_KEY =
            "SelectionRectangle:fillColor";
    public static final String BORDER_COLOR_KEY =
            "SelectionRectangle:borderColor";
    public static final String STROKE_DASH_KEY =
            "SelectionRectangle:strokeDash";
    public static final String STROKE_WIDTH_KEY =
            "SelectionRectangle:strokeWidth";
    public static final String CUSTOM_STROKE_KEY =
            "SelectionRectangle:customStroke";
    private float strokeWidth = 2;

    public SelectionRectangle(Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void paintGlobal(Graphics g) {
        if (bounds != null) {
            Graphics2D g2 = (Graphics2D) g;

            Color fillColor = (Color) canvas.getStyle().
                    getBaseValues().get(FILL_COLOR_KEY);

            Color borderColor = (Color) canvas.getStyle().
                    getBaseValues().get(BORDER_COLOR_KEY);

            g2.setColor(fillColor);
            g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

            g2.setColor(borderColor);

            if ((Boolean) canvas.getStyle().
                    getBaseValues().get(CUSTOM_STROKE_KEY)) {

                float[] dash = (float[]) canvas.getStyle().
                        getBaseValues().get(STROKE_DASH_KEY);

                strokeWidth = (Float) canvas.getStyle().
                        getBaseValues().get(STROKE_WIDTH_KEY);

                if (dash != null) {
                    g2.setStroke(new BasicStroke(strokeWidth,
                            BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER, 100.0f, dash, 0.0f));
                } else {
                    g2.setStroke(new BasicStroke(strokeWidth));
                }

            }

            g2.drawRect(
                    bounds.x,
                    bounds.y,
                    bounds.width,
                    bounds.height);
        }
    }

    public void setBounds(Rectangle r) {

        if (r == null) {
            return;
        }

        r.x = Math.max(r.x, 0);
        r.y = Math.max(r.y, 0);
        r.width = Math.max(r.width, 1);
        r.height = Math.max(r.height, 1);

        int offset = Math.max((int) (strokeWidth / 2) * 10, 10);

        if (bounds != null) {
            VSwingUtil.repaintRequest(canvas, bounds.x - offset, bounds.y - offset,
                    bounds.width + offset * 2, bounds.height + offset * 2);
        }

        bounds = r;

        VSwingUtil.repaintRequest(canvas, bounds.x - offset, bounds.y - offset,
                bounds.width + offset * 2, bounds.height + offset * 2);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void reset() {
        setBounds(bounds);
        bounds = null;
    }
}
