/* 
 * Ruler.java
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
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * A ruler is a small component that allows to resize other swing components.
 * VRL uses it mainly to resize type representations. In most cases this also
 * affects the parent window and thus resizes the window too.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Ruler extends JPanel {

    private static final long serialVersionUID = -3044471426618440346L;
    /**
     * the target that is to be resized
     */
    private Component target;
    /**
     * the current location of the mouse event (relative to screen coordinates)
     */
    private Point location;
    /**
     * the initial cursor location ( relative to component coordinates)
     */
    private Point initialCursorLocation;
    /**
     * the initial size of the component
     */
    private Dimension initialSize;
    /**
     *
     */
    private Canvas mainCanvas;
    
    private Collection<MouseMotionListener> externalListeners =
            new ArrayList<MouseMotionListener>();

    /**
     * Constructor.
     * @param target target that is to be resized
     */
    public Ruler(final Component target) {
        this.target = target;

        this.setPreferredSize(new Dimension(20, 20));
        this.setMinimumSize(new Dimension(20, 20));
        this.setMaximumSize(new Dimension(20, 20));

        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {

                getMainCanvas().getEffectPane().
                        setCursor(
                        Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                getMainCanvas().getEffectPane().
                        setCursor(
                        Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                initSizes(e);
            }
        });

        this.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                location = e.getLocationOnScreen();
                resizeTarget();
                
                for (MouseMotionListener eL : externalListeners) {
                    eL.mouseDragged(e);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                for (MouseMotionListener eL : externalListeners) {
                    eL.mouseMoved(e);
                }
            }
        });

        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }
    
    public void addExternalListener(MouseMotionListener l) {
        getExternalListeners().add(l);
    }
    
    public boolean removeExternalListener(MouseMotionListener l) {
        return getExternalListeners().remove(l);
    }

    private Canvas getMainCanvas() {
        if (mainCanvas==null) {
            mainCanvas =
                (Canvas) VSwingUtil.getParent(target, Canvas.class);
        }

        return mainCanvas;
    }

    /**
     * Resizes the target component.
     */
    private void resizeTarget() {

        int directionW = getLocationOnScreen().x - location.x;
        int directionH = getLocationOnScreen().y - location.y;

        Point direction = new Point(directionW, directionH);

        int w = direction.x + initialCursorLocation.x;
        int h = direction.y + initialCursorLocation.y;

        int sizeX = initialSize.width - w;
        int sizeY = initialSize.height - h;

        boolean xTooBig = sizeX > target.getMaximumSize().width;
        boolean xTooSmall = sizeX < target.getMinimumSize().width;

        boolean yTooBig = sizeY > target.getMaximumSize().height;
        boolean yTooSmall = sizeY < target.getMinimumSize().height;

        if (xTooBig) {
            sizeX = target.getMaximumSize().width;
        }

        if (xTooSmall) {
            sizeX = target.getMinimumSize().width;
        }

        if (yTooBig) {
            sizeY = target.getMaximumSize().height;
        }

        if (yTooSmall) {
            sizeY = target.getMinimumSize().height;
        }

        target.setPreferredSize(new Dimension(sizeX, sizeY));
        target.setSize(new Dimension(sizeX, sizeY));

        ArrayList<Container> containers =
                VSwingUtil.getAllParents(target, JComponent.class);

        for (Container c : containers) {
            if (c != target) {
                JComponent jC = (JComponent) c;
//                System.out.println("C: " + c.getClass());
                jC.setMinimumSize(null);
                jC.setMaximumSize(null);
                jC.setPreferredSize(null);
                jC.revalidate();
            } else if (CanvasWindow.class.isAssignableFrom(c.getClass())) {
                break;
            }
        }

        // the initial size is now the current target size
        initialSize = target.getSize();
    }

    /**
     * Initializes size variables.
     * @param e the mouse event
     */
    private void initSizes(MouseEvent e) {
//        initialLocation = e.getLocationOnScreen();
        initialSize = target.getSize();
        initialCursorLocation = e.getPoint();

        CanvasWindow window = (CanvasWindow) VSwingUtil.getParent(
                target, CanvasWindow.class);
        if (window != null) {
            window.setMinimumSize(null);
            window.setMaximumSize(null);
            window.setPreferredSize(null);
            window.revalidate();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getMainCanvas().getStyle().
                getBaseValues().getColor(CanvasWindow.BORDER_COLOR_KEY));

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setStroke(new BasicStroke(0.8f));
        g2.drawLine(0, getHeight(), getWidth(), 0);
        g2.drawLine(0 + 4, getHeight(), getWidth(), 0 + 4);
        g2.drawLine(0 + 8, getHeight(), getWidth(), 0 + 8);

        g2.setColor(new Color(
                255 - g2.getColor().getRed(),
                255 - g2.getColor().getGreen(),
                255 - g2.getColor().getBlue(),
                g2.getColor().getAlpha()));

        g2.setStroke(new BasicStroke(0.8f));
        g2.drawLine(1, getHeight(), getWidth(), 1);
        g2.drawLine(0 + 5, getHeight(), getWidth(), 0 + 5);
        g2.drawLine(0 + 9, getHeight(), getWidth(), 0 + 9);
    }

    /**
     * @return the externalListeners
     */
    public Collection<MouseMotionListener> getExternalListeners() {
        return externalListeners;
    }
}
