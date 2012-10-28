/* 
 * PreviousWindowGroupApplet.java
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

import eu.mihosoft.vrl.visual.CanvasActionListener;
import eu.mihosoft.vrl.visual.DockApplet;
import eu.mihosoft.vrl.visual.MessageType;
import eu.mihosoft.vrl.visual.PulseIcon;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * Dock applet with prevoius window group functionality for presentation sessions.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class PreviousWindowGroupApplet extends DockApplet {

    /**
     * Constructor.
     * @param canvas the canvas
     */
    public PreviousWindowGroupApplet(final Canvas canvas) {
        super(canvas);

        setIcon(generateIcon(new Dimension(50, 50)));

        setActionListener(new CanvasActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.getWindowGroupController().showPreviousGroup();
            }
        });
    }

    /**
     * Generates next icon.
     * @param size the icon size.
     * @return the icon
     */
    @Override
    protected ImageIcon generateIcon(Dimension size) {

        int inset = 0; // currently we use border insets

        BufferedImage buffer =
                new BufferedImage(size.width, size.height,
                BufferedImage.TYPE_INT_ARGB);

        Color color = getMainCanvas().getStyle().getBaseValues().
                getColor(MessageBox.TEXT_COLOR_KEY);

        Graphics2D g2 = buffer.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Composite original = g2.getComposite();

        Style style = getMainCanvas().getStyle();

        float alpha = style.getBaseValues().getFloat(
                CanvasWindow.TRANSPARENCY_KEY);
        AlphaComposite ac1 = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, alpha);

        g2.setComposite(ac1);


        g2.setColor(style.getBaseValues().getColor(MessageBox.ICON_COLOR_KEY));


        float lineThickness = 1;

        BasicStroke stroke = new BasicStroke(lineThickness,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        g2.setStroke(stroke);

        int topLeftSpacing = (int) lineThickness + inset;
        int bottomRightSpacing = (int) lineThickness + topLeftSpacing;

        Polygon triangle = new Polygon();

        triangle.addPoint(size.width - bottomRightSpacing - getInsets().right,
                topLeftSpacing + getInsets().top);
        triangle.addPoint(size.width - bottomRightSpacing - getInsets().right,
                size.height - bottomRightSpacing - getInsets().bottom);
        triangle.addPoint(0 + topLeftSpacing + getInsets().left,
                (size.height - getInsets().bottom - getInsets().top) / 2 -
                (int) lineThickness + getInsets().top);

        g2.fill(triangle);

        g2.dispose();

        return new ImageIcon(buffer);
    }


//     @Override
//    public void resizeApplet(Dimension d) {
//        super.resizeApplet(d);
//
//        setIcon(generateIcon(getSize()));
//    }
}
