/* 
 * CanvasGrid.java
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
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import javax.swing.JPanel;

/**
 * Implementation of a background grid for the Canvas class.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class CanvasGrid extends JPanel implements GlobalBackgroundPainter,
        CanvasChild, StyleChangedListener {

    private int xOffset;
    private int yOffset;
    private Canvas mainCanvas;
    public static final String ENABLE_GRID_KEY = "CanvasGrid:visibility";
    public static final String GRID_COLOR_KEY = "CanvasGrid:color";
    private Boolean visible = false;
    private Color color = Color.BLACK;

    /**
     * Constructor.
     * @param mainCanvas th main canvas object
     * @param xOffset the x offset of the grid
     * @param yOffset the y offset of the grid
     */
    public CanvasGrid(Canvas mainCanvas, int xOffset, int yOffset) {
        setMainCanvas(mainCanvas);

        setXoffset(xOffset);
        setYoffset(yOffset);
    }

    @Override
    public void paintGlobal(Graphics g) {
        if (visible) {
            paintComponent(g);
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);

        Stroke oldStroke = g2.getStroke();

        BasicStroke stroke = new BasicStroke(1);

        g2.setStroke(stroke);
        g2.setColor(color);

        for (int x = 0; x < getWidth(); x += getXoffset()) {
            g2.draw(new Line2D.Double(x, 0, x, getHeight() - 1));
        }
        for (int y = 0; y < getHeight(); y += getYoffset()) {
            g2.draw(new Line2D.Double(0, y, getWidth() - 1, y));
        }

        g2.setStroke(oldStroke);
    }

    /**
     * Returns the x offset of the grid.
     * @return the x offset of the grid
     */
    public int getXoffset() {
        return xOffset;
    }

    /**
     * Defines the x offset of the grid.
     * @param xoffset the x offset of the grid
     */
    public void setXoffset(int xoffset) {
        this.xOffset = xoffset;
    }

    /**
     * Returns the  yoffset of the grid.
     * @return the y offset of the grid
     */
    public int getYoffset() {
        return yOffset;
    }

    /** Defines the  yoffset of the grid.
     * 
     * @param yoffset the  yoffset of the grid
     */
    public void setYoffset(int yoffset) {
        this.yOffset = yoffset;
    }

    @Override
    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    @Override
    public void setMainCanvas(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;
        styleChanged(mainCanvas.getStyle());
    }

    @Override
    public void styleChanged(Style style) {
        visible = style.getBaseValues().getBoolean(ENABLE_GRID_KEY);
        if (visible != null) {
            setVisible(visible);
        }
        color = style.getBaseValues().getColor(GRID_COLOR_KEY);
        if (color == null) {
            color = Color.BLACK;
        }
    }
}
