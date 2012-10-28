/* 
 * Dock.java
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

import eu.mihosoft.vrl.animation.Animation;
import eu.mihosoft.vrl.animation.AnimationManager;
import eu.mihosoft.vrl.animation.FrameListener;
import eu.mihosoft.vrl.animation.LinearInterpolation;
import eu.mihosoft.vrl.visual.ImageUtils;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


/**
 * A Dock implementation inspired by Mac OS X.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Dock extends VComponent implements BufferedPainter,
        ShadowPainter {

    private static final long serialVersionUID = 8606123948127537167L;
    private transient BufferedImage buffer;
    private int dockHeight = 30;
    private int maxDockHeight = 50;
    private transient Shape shape;
    private float transparency = 1.0f;
    private ArrayList<DockApplet> dockApplets = new ArrayList<DockApplet>();
    public static final String DOCK_HEIGHT_KEY = "Dock:height";

    /**
     * Constructor.
     * @param mainCanvas the main canvas
     */
    public Dock(Canvas mainCanvas) {
        setMainCanvas(mainCanvas);
        setBorder(new ShadowBorder(this));

        setPreferredSize(new Dimension(60,
                maxDockHeight));

        setLayout(new GridLayout());

        setOpaque(false);

        mainCanvas.getStyleManager().
                addStyleChangedListener(new StyleChangedListener() {

            @Override
            public void styleChanged(Style style) {
                int height = (Integer) style.getBaseValues().get(DOCK_HEIGHT_KEY);

                resizeDockHeight(height);
            }
        });
    }

    /**
     * Adds an applet to the dock.
     * @param applet the dock applet to be added
     */
    public void addDockApplet(DockApplet applet) {
        applet.setVisible(true);
        applet.setDock(this);
        dockApplets.add(applet);

        this.add(applet);

        int height =
                (Integer) getMainCanvas().getStyle().
                getBaseValues().get(DOCK_HEIGHT_KEY);

        resizeDockHeight(height);
    }

    /**
     * Removes a dock applet from the dock.
     * @param applet the applet to remove
     */
    public void removeDockApplet(DockApplet applet) {
        this.remove(applet);
        dockApplets.remove(applet);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (buffer == null
                || buffer.getWidth() != getWidth()
                || buffer.getHeight() != getHeight()) {

            buffer = new BufferedImage(getWidth(), getHeight(),
                    BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2 = buffer.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Style style = getMainCanvas().getStyle();

            g2.setStroke(new BasicStroke(1));
            g2.setColor(style.getBaseValues().getColor(MessageBox.BOX_COLOR_KEY));

            int top = getInsets().top;
            int left = getInsets().left;
            int bottom = getInsets().bottom;
            int right = getInsets().right;

            int arcWidth = 20;
            int arcHeight = 20;

            shape = new RoundRectangle2D.Double(left,
                    top, getWidth() - left - right,
                    getHeight() - top - bottom, arcWidth, arcHeight);

            // define the shape that is created by adding rectangular shape to 
            // round rectangle
            Shape shape2 = new Rectangle2D.Double(
                    (double) left,
                    (double) top + arcHeight,
                    (double) (getWidth() - left - right),
                    (double) (getHeight() - top - bottom - arcHeight));

            Area objectOne = new Area(shape);
            Area objectTwo = new Area(shape2);

            objectOne.add(objectTwo);
            shape = objectOne;

            g2.fill(shape);

            g2.setStroke(new BasicStroke(2));
            g2.setColor(style.getBaseValues().getColor(MessageBox.ICON_COLOR_KEY));

            g2.draw(getShape());

            // TODO find another way than redefining shape
            // currently this has to be done because of stroke
            shape =
                    new RoundRectangle2D.Double(left, top, getWidth() - left - right + 1,
                    getHeight() - top - bottom + 1, 20, 20);

            // define the shape that is created by adding rectangular shape to 
            // round rectangle
            shape2 = new Rectangle2D.Double(
                    (double) left,
                    (double) top + arcHeight,
                    (double) (getWidth() - left - right + 1),
                    (double) (getHeight() - top - bottom - arcHeight + 1));

            objectOne = new Area(shape);
            objectTwo = new Area(shape2);

            objectOne.add(objectTwo);
            shape = objectOne;

            g2.dispose();

            buffer = ImageUtils.gradientMask(buffer, 0.2f, 0.55f);
        }

        Graphics2D g2 = (Graphics2D) g;

        AlphaComposite ac1 =
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                getTransparency());
        g2.setComposite(ac1);

        g2.drawImage(buffer, 0, 0, getWidth(), getHeight(), null);
    }

    @Override
    public void contentChanged() {
        buffer = null;

        super.contentChanged();
    }


    /**
     * Defines the dock height. This method should not be used for
     * manual dock resizing. Use {@link Dock#resizeDockHeight(int) } instead.
     * @param dockHeight the height to set
     */
    void setDockHeight(int dockHeight) {
        this.dockHeight = dockHeight;
    }

    /**
     * Returns the dock height.
     * @return the dock height
     */
    public int getDockHeight() {
        return dockHeight;
    }

    /**
     * Returns the maximum height of the dock.
     * @return the maximum height of the dock
     */
    public int getMaxDockHeight() {
        return maxDockHeight;
    }

    /**
     * Defines the maximum dock height. This method should not be used for
     * manual dock resizing. Use {@link Dock#resizeDockHeight(int) } instead.
     * @param maxDockHeight the maximum dock height
     */
    public void setMaxDockHeight(int maxDockHeight) {
        this.maxDockHeight = maxDockHeight;
    }

    /**
     * Resizes the dock and its dock applets.
     * @param d the size
     */
    protected void resizeDock(Dimension d) {
        int appletWidth = getInsets().left + getInsets().right;

        for (DockApplet dockApplet : dockApplets) {
            dockApplet.resizeApplet(d);
            appletWidth += dockApplet.getAppletSize().width;
        }

        int preferredHeight = d.height
                + getInsets().top + getInsets().bottom;

        int maxHeight = Math.max(preferredHeight, maxDockHeight);
        int maxWidth = Math.max(appletWidth, d.width);

        setMaxDockHeight(maxHeight);
        setDockHeight(maxHeight);

        setPreferredSize(new Dimension(maxWidth, maxDockHeight));
    }

    /**
     * This method resizes the dock height. It also takes care to resize the
     * dock applets.
     * @param h the height
     */
    public void resizeDockHeight(int h) {
        int w = 0;
//        h += getInsets().top + getInsets().bottom;

        setMaxDockHeight(h);

        resizeDock(new Dimension(w, h));
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    /**
     * Returns the transparency of the dock.
     * @return the transparency of the dock
     */
    public float getTransparency() {
        return transparency;
    }

    /**
     * Defines the transparency of the dock.
     * @param transparency the transparency to set
     */
    public void setTransparency(float transparency) {
        this.transparency = transparency;
        contentChanged();

        for (DockApplet a : dockApplets) {
            a.setTransparency(transparency);
        }
    }

    /**
     * Fades out the dock.
     * @param offset the animation offset
     * @param duration the duration of the animation
     */
    void fadeOut(double offset, double duration) {
        Animation a = new FadeOutDockAnimation(this);
        a.setOffset(offset);
        a.setDuration(duration);

        AnimationManager manager = getMainCanvas().getAnimationManager();

        getMainCanvas().getAnimationManager().addUniqueAnimation(a, 1);
    }

    /**
     * Fades in the dock.
     * @param offset the animation offset
     * @param duration the duration of the animation
     */
    void fadeIn(double offset, double duration) {
        Animation a = new FadeInDockAnimation(this);
        a.setOffset(offset);
        a.setDuration(duration);

        AnimationManager manager = getMainCanvas().getAnimationManager();

        getMainCanvas().getAnimationManager().addUniqueAnimation(a, 1);
    }

    /**
     * Fades out tthe animation.
     */
    void fadeOut() {
        fadeOut(0, 1.0);
    }

    /**
     * Fades in the animation.
     */
    void fadeIn() {
        fadeIn(0, 1.0);
    }
}

/**
 * Fade in animation.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class FadeInDockAnimation extends Animation implements FrameListener {

    private Dock dock;

    /**
     * Constructor.
     * @param dock
     */
    public FadeInDockAnimation(Dock dock) {
        addFrameListener(this);
        getInterpolators().add(new LinearInterpolation(dock.getTransparency(),
                1.0f));
        this.dock = dock;
    }

    @Override
    public void frameStarted(double time) {
        if (getTime() == 0.0) {
//            dock.setVisible(true);
        }

        float value = (float) getInterpolators().get(0).getValue();

        dock.setTransparency(value);

        if (getTime() == 1.0) {
            //
        }
    }
}

/**
 * Fade out animation.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class FadeOutDockAnimation extends Animation implements FrameListener {

    private Dock dock;

    /**
     * Constructor.
     * @param dock
     */
    public FadeOutDockAnimation(Dock dock) {
        addFrameListener(this);

        getInterpolators().add(new LinearInterpolation(dock.getTransparency(),
                0.3f));
        this.dock = dock;
    }

    @Override
    public void frameStarted(double time) {
        if (getTime() == 0.0) {
//            dock.setVisible(true);
        }

        float value = (float) getInterpolators().get(0).getValue();
        dock.setTransparency(value);

        if (getTime() == 1.0) {
            //
        }
    }
}
