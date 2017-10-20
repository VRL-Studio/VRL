/* 
 * FullScreenCloseIcon.java
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

package eu.mihosoft.vrl.visual;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.Border;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FullScreenCloseIcon extends CloseIcon  implements ShadowPainter {

    private float iconInset = 20f; // in percent
    private Shape shape;

    public FullScreenCloseIcon(Canvas mainCanvas) {
        super(mainCanvas);
        FullScreenIconShadowBorder border =
                new FullScreenIconShadowBorder(this);

        setBorder(border);
    }

    @Override
    protected void paintComponent(Graphics g) {
//        Color color = getMainCanvas().getStyle().getMessageBoxTextColor();

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Composite original = g2.getComposite();

        AlphaComposite ac1 = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 0.8f);

        g2.setComposite(ac1);

//        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));


        g2.setColor(computeCurrentCircleColor());

        int realIconInset = (int) ((getWidth() / 100.d) * getIconInset());

        int circleWidth = getWidth() - 2 * realIconInset;

        float circleThickness = circleWidth / 8;

        if (circleWidth < 15) {
            circleThickness = circleWidth / 5.0f;
        }

        float crossThickness = circleWidth / 12f;

        if (circleWidth < 15) {
            crossThickness = circleWidth / 5.0f;
        }

        BasicStroke stroke = new BasicStroke(circleThickness,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        g2.setStroke(stroke);

        int topLeftSpacing = (int) circleThickness + realIconInset;
        int bottomRightSpacing = (int) circleThickness + topLeftSpacing + realIconInset;

        g2.setColor(computeCurrentBackgroundColor());

        g2.fillOval(topLeftSpacing, topLeftSpacing,
                getWidth() - bottomRightSpacing,
                getHeight() - bottomRightSpacing);

        setShape(new Ellipse2D.Float(topLeftSpacing, topLeftSpacing,
                getWidth() - bottomRightSpacing+1,
                getHeight() - bottomRightSpacing+1));

        g2.setColor(computeCurrentCircleColor());

        g2.drawOval(topLeftSpacing, topLeftSpacing,
                getWidth() - bottomRightSpacing,
                getHeight() - bottomRightSpacing);


        stroke = new BasicStroke(crossThickness,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        g2.setStroke(stroke);

        int spacing = (int) (circleWidth / 2.8) + realIconInset;

        if (circleWidth < 20) {
            spacing = (int) (circleWidth / 2.5) + realIconInset;
        }

        g2.drawLine(spacing, spacing, getWidth() - spacing, getHeight() - spacing);
        g2.drawLine(getWidth() - spacing, spacing, spacing, getHeight() - spacing);

        g2.setComposite(original);
    }

    /**
     * Computes the color that is to be used depending on the active state.
     */
    protected Color computeCurrentCircleColor() {

        Color result = null;

        if (isActive()) {
            result = getMainCanvas().getStyle().getBaseValues().getColor(
                    CanvasWindow.ACTIVE_ICON_COLOR_KEY);
        } else {
            result = Color.white;
        }

        return result;
    }

    /**
     * @return the iconInset
     */
    public float getIconInset() {
        return iconInset;
    }

    /**
     * @param iconInset the iconInset to set
     */
    public void setIconInset(float iconInset) {
        this.iconInset = iconInset;
    }

    /**
     * Computes the color that is to be used depending on the active state.
     */
    protected Color computeCurrentBackgroundColor() {
        return Color.black;
    }
//
//    /**
//     * Computes the color that is to be used depending on the active state.
//     */
//    protected Color computeCurrentLineColor() {
//        return ColorUtils.invertColor(
//                getMainCanvas().getStyle().getWindowIconColor());
//    }

    @Override
    public Shape getShape() {
        return shape;
    }

    /**
     * @param shape the shape to set
     */
    protected void setShape(Shape shape) {
        this.shape = shape;
    }
}
