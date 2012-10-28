/* 
 * CloseIcon.java
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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import javax.swing.JPanel;

/**
 * This component can be used to display a close icon.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class CloseIcon extends VComponent implements CanvasChild, Serializable,
        MouseListener, MouseMotionListener {

    private static final long serialVersionUID = 4657655765709194246L;
    /**
     * an action listener that is notified whenever this object receives a mouse
     * event (single left button click).
     */
    private CanvasActionListener actionListener;
    /**
     * defines the current color of this object
     */
    protected Color currentColor;
    /**
     * defines if the object is active or not
     */
    private boolean active = false;
//    private boolean clicked = false;

    /**
     * Constructor.
     *
     * @param mainCanvas the main canvas object
     */
    public CloseIcon(Canvas mainCanvas) {
        setMainCanvas(mainCanvas);
        setPreferredSize(new Dimension(28, 28));
//        setMinimumSize(new Dimension(100,100));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        setOpaque(false);
    }

    public CloseIcon() {
        setPreferredSize(new Dimension(28, 28));
//        setMinimumSize(new Dimension(100,100));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
//        Color color = getStyle().getMessageBoxTextColor();

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Composite original = g2.getComposite();

        float alpha = getStyle().getBaseValues().getFloat(
                CanvasWindow.TRANSPARENCY_KEY);
        AlphaComposite ac1 = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, alpha);

        g2.setComposite(ac1);

        computeCurrentColor();

        g2.setColor(getCurrentColor());

        float circleThickness = getWidth() / 8;

        if (getWidth() < 15) {
            circleThickness = getWidth() / 5.0f;
        }

        BasicStroke stroke = new BasicStroke(circleThickness,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        g2.setStroke(stroke);

        int topLeftSpacing = (int) circleThickness;
        int bottomRightSpacing = (int) circleThickness + topLeftSpacing;

        g2.drawOval(topLeftSpacing, topLeftSpacing,
                getWidth() - bottomRightSpacing,
                getHeight() - bottomRightSpacing);

        float crossThickness = circleThickness;

        stroke = new BasicStroke(crossThickness,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        g2.setStroke(stroke);

        int spacing = (int) (getWidth() / 2.8);

        if (getWidth() < 20) {
            spacing = (int) (getWidth() / 2.5);
        }

        g2.drawLine(spacing, spacing, getWidth() - spacing, getHeight() - spacing);
        g2.drawLine(getWidth() - spacing, spacing, spacing, getHeight() - spacing);

        g2.setComposite(original);
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
    }

    @Override
    public void mousePressed(MouseEvent ev) {
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        active = true;
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        active = false;
        repaint();
    }

    /**
     * Deactivates this icon.
     */
    public void deactivate() {
        active = false;
        repaint();
    }

    /**
     * Returns the action listener that is associated with this object.
     *
     * @return the action listener that is associated with this object
     */
    public ActionListener getActionListener() {
        return actionListener;
    }

    /**
     * Defines the action listener that is to be associated with this object.
     *
     * @param actionListener the action listener that is to be associated with
     * this object
     */
    public void setActionListener(CanvasActionListener actionListener) {
        this.actionListener = actionListener;
    }

    /**
     * Fires an action. This method is called whenever the corresponding mouse
     * event occures.
     *
     * @param event the mouse event
     */
    private void fireAction(ActionEvent event) {
        if (actionListener != null) {
            actionListener.actionPerformed(event);
        }
    }

    /**
     * Computes the color that is to be used depending on the active state.
     */
    protected void computeCurrentColor() {
        if (isActive()) {
            currentColor = getStyle().getBaseValues().getColor(
                    CanvasWindow.ACTIVE_ICON_COLOR_KEY);
        } else {
            currentColor = getStyle().getBaseValues().getColor(
                    CanvasWindow.ICON_COLOR_KEY);
        }
    }

    /**
     * @return the currentColor
     */
    public Color getCurrentColor() {
        return currentColor;
    }

    /**
     * @return the active
     */
    protected boolean isActive() {
        return active;
    }
}
