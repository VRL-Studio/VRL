/* 
 * CanvasWindowTitleBar.java
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
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Title bar of the window.
 */
public class CanvasWindowTitleBar extends VComponent
        implements IconBoxChangedListener {

    private static final long serialVersionUID = 4580796216773952333L;
    private CanvasLabel title;
//        private MakeDraggable draggable;
    int width = 0;
    int height = 0;
    // please notice that if
    // iconSize %2 = 0 iconBoxSize must be odd
    // to ensure that icon is vertically centered
    private int iconSize = 20;
    private IconBox iconBox = new IconBox();
    private Component horizontalInsetComponent;
    private CanvasWindow parentWindow;
    private JComponent leftContainer = new TransparentPanel();
    private JComponent rightContainer = new TransparentPanel();

    /**
     * Constructor.
     */
    CanvasWindowTitleBar(CanvasWindow parentWindow) {
//            super(new GridLayout());
        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.LINE_AXIS);
        setLayout(layout);

        VBoxLayout leftLayout = new VBoxLayout(
                leftContainer, VBoxLayout.LINE_AXIS);

        leftContainer.setLayout(leftLayout);
        leftContainer.setBorder(new EmptyBorder(0, 7, 0, 0));

        add(leftContainer);

        VBoxLayout rightLayout = new VBoxLayout(
                rightContainer, VBoxLayout.LINE_AXIS);

        rightContainer.setLayout(rightLayout);
        rightContainer.setBorder(new EmptyBorder(0, 0, 0, 5));

        setMainCanvas(parentWindow.getMainCanvas());

        this.parentWindow = parentWindow;

        title = new CanvasLabel(this, "Title");
        title.setHorizontalAlignment(SwingConstants.CENTER);

        title.setForeground(getStyle().getBaseValues().getColor(
                Canvas.TEXT_COLOR_KEY));

        horizontalInsetComponent
                = Box.createHorizontalStrut(iconBox.getIconBoxWidth());

        add(Box.createGlue());
        add(horizontalInsetComponent);
        add(title);
        add(Box.createGlue());

        iconBox.setIconBoxListener(this);

        // this defines implicitly the minimum titlebar height
        iconBox.add(Box.createRigidArea(new Dimension(0, getIconSize())));

        add(iconBox);

        setPainterKey(CanvasWindow.TITLEBAR_PAINTER_KEY);

//            CloseIcon closeIcon = new CloseIcon(mainCanvas);
//            closeIcon.setActionListener(new CanvasActionListener() {
//
//                @Override
//                public void actionPerformed(ActionEvent e) {
////                    windowRemoved();
//                    CanvasWindow.this.close();
//                }
//            });
//
//            closeIcon.setMinimumSize(new Dimension(iconSize, iconSize));
//            closeIcon.setPreferredSize(new Dimension(iconSize, iconSize));
//            closeIcon.setMaximumSize(new Dimension(iconSize, iconSize));
//
//            iconBox.addIcon(closeIcon);
        getParentWindow().setPopup(new JPopupMenu());

        /**
         * open popup with button 3.
         */
        class PopupListener extends MouseAdapter {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 3 && getParentWindow().isMenuEnabled()) {

                    VScale scale = TransformingParent.getScale(getMainCanvas());

                    if (scale.isIdentity()) {
                        getParentWindow().getPopup().show(
                                e.getComponent(), e.getX(), e.getY());
                    } else {
                        Point absPos = VSwingUtil.getAbsPos(parentWindow);
                        absPos.x /= scale.getScaleX();
                        absPos.y /= scale.getScaleY();

                        getParentWindow().getPopup().show(
                                getMainCanvas(),
                                absPos.x + (int) (e.getX() / scale.getScaleX()),
                                absPos.y + (int) (e.getY() / scale.getScaleY()));
                    }

                    getParentWindow().getMainCanvas().getWindows().setAllInactive();

                    getParentWindow().setActive(true);
                }
            }
        }

        addMouseListener(new PopupListener());

        MouseControl control = new MouseControl(getParentWindow());

        addMouseMotionListener(control);
        addMouseListener(control);

        setOpaque(false);

//        setPainter(new BackgroundPainter());
//        setPainter(new Painter() {
//
//            @Override
//            public void paint(Graphics g, Style s, Dimension d) {
//
//                Graphics2D g2 = (Graphics2D) g;
//
//                g2.setStroke(new BasicStroke(1));
//
//                g2.setColor(Color.BLACK);
//
//                g2.drawRect(0, 0, d.width-1, d.height-1);
//
//                g2.setColor(new Color(80,80,80,120));
//            }
//        });
        add(rightContainer);
    }

    public void setLeftConnector(Connector c) {
        leftContainer.removeAll();
        leftContainer.add(c);
    }

    public void setRightConnector(Connector c) {
        rightContainer.removeAll();
        rightContainer.add(c);
    }

    /**
     * Returns the title label of the window.
     *
     * @return the title label of the window
     */
    JLabel getTitleLabel() {
        return this.title;
    }

    /**
     * Defines the title of this window.
     *
     * @param t the title to set
     */
    public void setTitle(String t) {
        title.setText(t);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public void iconBoxChanged(IconBox iconBox) {
        Dimension d = horizontalInsetComponent.getMinimumSize();
        d.setSize(iconBox.getIconBoxWidth(), d.height);
        horizontalInsetComponent.setPreferredSize(d);

        revalidate();
    }

    /**
     * Adds an icon to this title bar.
     *
     * @param c the icon to add
     */
    public void addIcon(Component c) {
        iconBox.addIcon(c);
    }

    /**
     * Removes an icon from the title bar.
     *
     * @param c the icon to remove
     */
    public void removeIcon(Component c) {
        iconBox.removeIcon(c);
    }

    /**
     * @return the parentWindow
     */
    public CanvasWindow getParentWindow() {
        return parentWindow;
    }

    /**
     * @return the iconSize
     */
    public int getIconSize() {
        return iconSize;
    }

    /**
     * @param iconSize the iconSize to set
     */
    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
    }

    @Override
    public void dispose() {
        try {
            MouseListener[] mouseListeners = getMouseListeners();
            for (MouseListener l : mouseListeners) {
                removeMouseListener(l);
            }
            MouseMotionListener[] mouseMotionListeners = getMouseMotionListeners();
            for (MouseMotionListener l : mouseMotionListeners) {
                removeMouseMotionListener(l);
            }
        } catch (Exception ex) {
            //
        } finally {
            super.dispose();
        }
    }
//    @Override
//    public Painter getPainter() {
//
//        setPainter(getStyle().getBaseValues().
//                getPainter(CanvasWindow.TITLEBAR_PAINTER_KEY));
//
//        return super.getPainter();
//    }
} // end class CanvasTitleBar

class TitleBarPainter implements Painter, BufferedPainter {

    transient private BufferedImage buffer;
    private CanvasWindowTitleBar parent;

    @Override
    public void paint(Graphics g, Style s, Dimension d) {

        if (buffer == null
                || buffer.getWidth() != d.width
                || buffer.getHeight() != d.height) {

            buffer = ImageUtils.createCompatibleImage(d.width, d.height);

            Graphics2D g2 = buffer.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Style style = s;

            Composite original = g2.getComposite();

            AlphaComposite ac1
                    = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                            style.getBaseValues().getFloat(
                                    CanvasWindow.TITLE_TRANSPARENCY_KEY));
            g2.setComposite(ac1);

            Color upperColor = style.getBaseValues().getColor(
                    CanvasWindow.UPPER_TITLE_COLOR_KEY);
            Color lowerColor = style.getBaseValues().getColor(
                    CanvasWindow.LOWER_TITLE_COLOR_KEY);

            if (this.parent.getParentWindow().isActive()) {
                upperColor = style.getBaseValues().getColor(
                        CanvasWindow.UPPER_ACTIVE_TITLE_COLOR_KEY);
                lowerColor = style.getBaseValues().getColor(
                        CanvasWindow.LOWER_ACTIVE_TITLE_COLOR_KEY);
            }

            GradientPaint paint = new GradientPaint(0, 0,
                    upperColor,
                    0, d.height,
                    lowerColor,
                    false);

            g2.setPaint(paint);

            g2.fill(new RoundRectangle2D.Double(0, 0, d.width - 0.5,
                    d.height, 20, 20));

            g2.setComposite(original);

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setStroke(new BasicStroke(style.getBaseValues().getFloat(
                    CanvasWindow.BORDER_THICKNESS_KEY)));
            g2.setColor(style.getBaseValues().getColor(
                    CanvasWindow.BORDER_COLOR_KEY));

            g2.draw(new RoundRectangle2D.Double(0, 0, d.width - 1.0,
                    d.height - 0.5, 19.5, 19.5));

            g2.dispose();
        }

        g.drawImage(buffer, 0, 0, null);
    }

    @Override
    public void contentChanged() {
        buffer = null;
    }

    private void setParent(VComponent parent) {
        if (parent == null) {
            throw new IllegalArgumentException(
                    "Argument \"null\" is not supported!");
        }

        if (!CanvasWindowTitleBar.class.isAssignableFrom(parent.getClass())) {
            throw new IllegalArgumentException(
                    "Only \"CanvasWindowTitleBar\" is supported as parent!");
        }
        this.parent = (CanvasWindowTitleBar) parent;
    }

    @Override
    public Painter newInstance(VComponent parent) {
        TitleBarPainter result = new TitleBarPainter();
        result.setParent(parent);
        return result;
    }
}
