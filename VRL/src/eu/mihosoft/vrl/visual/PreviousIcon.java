/* 
 * PreviousIcon.java
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

package eu.mihosoft.vrl.visual;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import javax.swing.JPanel;

/**
 * This icon has triangular shape and can be used as scroll icon to browse
 * through messages or changing window groups.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class PreviousIcon extends JPanel implements CanvasChild, Serializable,
        MouseListener, MouseMotionListener {
    private static final long serialVersionUID = 5410444806828267807L;

    /**
     * the main canvas object
     */
    private Canvas mainCanvas;
    /**
     * an action listener that is notified whenever this object receives a
     * mouse event (single left button click).
     */
    private CanvasActionListener actionListener;
    /**
     * defines if the object is active or not
     */
    private boolean isActive;

    /**
     * crrently not in use.
     */
    private boolean lockState = false;

    /**
     * Constructor.
     * @param mainCanvas the main canvas
     */
    public PreviousIcon(Canvas mainCanvas) {
        setMainCanvas(mainCanvas);
        setPreferredSize(new Dimension(40, 60));
        setMinimumSize(new Dimension(40, 60));
        setMaximumSize(new Dimension(40, 60));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Color color = mainCanvas.getStyle().getBaseValues().getColor(
                MessageBox.TEXT_COLOR_KEY);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Composite original = g2.getComposite();

        Style style = getMainCanvas().getStyle();

        float alpha = style.getBaseValues().getFloat(
                CanvasWindow.TRANSPARENCY_KEY);
        AlphaComposite ac1 = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, alpha);

        g2.setComposite(ac1);

        if (isActive) {
            g2.setColor(style.getBaseValues().getColor(
                    MessageBox.ACTIVE_ICON_COLOR_KEY));
        } else {
            g2.setColor(style.getBaseValues().getColor(
                    MessageBox.ICON_COLOR_KEY));
        }

        float lineThickness = 1;

        BasicStroke stroke = new BasicStroke(lineThickness,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        g2.setStroke(stroke);

        int topLeftSpacing = (int) lineThickness;
        int bottomRightSpacing = (int) lineThickness + topLeftSpacing;

        Polygon triangle = new Polygon();

        triangle.addPoint(getWidth() - bottomRightSpacing - getInsets().right,
                topLeftSpacing + getInsets().top);
        triangle.addPoint(getWidth() - bottomRightSpacing - getInsets().right,
                getHeight() - bottomRightSpacing - getInsets().bottom);
        triangle.addPoint(0 + topLeftSpacing + getInsets().left,
                (getHeight() - getInsets().bottom - getInsets().top) / 2
                - (int) lineThickness + getInsets().top);


        g2.fill(triangle);

        g2.setComposite(original);
    }

    @Override
    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    @Override
    public void setMainCanvas(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;
    }

    @Override
    public void mouseDragged(MouseEvent ev) {
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        fireAction(new ActionEvent(this, 0, "clicked"));
        lockState = true;
    }

    @Override
    public void mousePressed(MouseEvent ev) {
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        isActive = true;
//        if (!lockState) {
//            stateChanged();
//        }
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        isActive = false;
//        if (!lockState) {
//            stateChanged();
//        }
//        lockState = false;
        repaint();
    }

    /**
     * Returns the action listener of this icon.
     * @return the action listener of this icon
     */
    public CanvasActionListener getActionListener() {
        return actionListener;
    }

    /**
     * Defines the action listener of this icon.
     * @param actionListener the action listener of this icon
     */
    public void setActionListener(CanvasActionListener actionListener) {
        this.actionListener = actionListener;
    }

    /**
     * Fires an action.
     * @param event the action event
     */
    private void fireAction(ActionEvent event) {
        if (actionListener != null) {
            actionListener.actionPerformed(event);
        }
    }

    @Override
    public Insets getInsets() {
        return new Insets(0, 5, 0, 0);
    }

    /**
     * Currently empty. TODO: can this method be removed?
     */
    public void stateChanged() {
        //
    }
}
