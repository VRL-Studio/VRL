/* 
 * PulseIcon.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007–2018 by Michael Hoffer,
 * Copyright (c) 2015–2018 G-CSC, Uni Frankfurt,
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn)
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

import eu.mihosoft.vrl.animation.Animation;
import eu.mihosoft.vrl.animation.AnimationInterpolation;
import eu.mihosoft.vrl.animation.AnimationTask;
import eu.mihosoft.vrl.animation.FrameListener;
import eu.mihosoft.vrl.animation.LinearInterpolation;
import eu.mihosoft.vrl.visual.ImageUtils;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * PulseIcon is an animated pulse emitter. Its purpose is to be used as GUI
 * effect. One example is error visualization at specific components.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class PulseIcon extends JPanel implements CanvasChild, BufferedPainter {

    private static final long serialVersionUID = -8941791256683819506L;
    /**
     * main canvas
     */
    private Canvas mainCanvas;
    /**
     * canvas child that is used to define the location of the animation
     */
    private CanvasChild canvasChild;
    /**
     * image buffer
     */
    transient private BufferedImage img;
    /**
     * the shape of the icon
     */
    private Shape shape = new Ellipse2D.Double();
    /**
     * the thickness of the shape
     */
    private float shapeThickness;
    /**
     * defines the distance between the top/left border of the Graphics object
     * and the origin; this is necessary because the stroke thickness
     */
    private int topLeftSpacing;
    /**
     * defines the distance between the bottom/right border of the Graphics
     * object and the origin; this is necessary because the stroke thickness
     */
    private int bottomRightSpacing;
    /**
     * x coordinate of the shape
     */
    private double valueX;
    /**
     * y coordinate of the shape
     */
    private double valueY;
    /**
     * width of the shape
     */
    private double valueW;
    /**
     * height of the shape
     */
    private double valueH;
    /**
     * scale value of the shape
     */
    private double scaleValue;
    /**
     * transparency of the icon
     */
    private double transparency;
    /**
     * animation target used to compute x coordinate
     */
    private AnimationInterpolation scaleTargetX;
    /**
     * animation target used to compute y coordinate
     */
    private AnimationInterpolation scaleTargetY;
    /**
     * animation target used to compute width
     */
    private AnimationInterpolation scaleTargetW;
    /**
     * animation target used to compute height
     */
    private AnimationInterpolation scaleTargetH;
    /**
     * defines the number of animation repeats
     */
    private int repeats = 3;
    /**
     * defines the duration of the animation
     */
    private double duration = 1.0;
    /**
     * defines the icon color
     */
    private Color color = Color.GREEN;
    /**
     * defines the message type of the icon
     */
    private MessageType messageType;
    /**
     * currently used animation
     */
    private Animation currentAnimation;

    /**
     * Constructor.
     *
     * @param mainCanvas the main Canvas object
     * @param c the canvas child
     * @param type icon type of the message
     */
    public PulseIcon(Canvas mainCanvas, CanvasChild c, MessageType type) {
        setMainCanvas(mainCanvas);

        messageType = type;

        setSize(new Dimension(60, 60));
        setPreferredSize(new Dimension(60, 60));

        setMessageType(type);

        topLeftSpacing = (int) shapeThickness;
        bottomRightSpacing = (int) shapeThickness + topLeftSpacing;

        scaleTargetX = new LinearInterpolation(getWidth() / 2.0, topLeftSpacing);
        scaleTargetY = new LinearInterpolation(getHeight() / 2.0, topLeftSpacing);
        scaleTargetW = new LinearInterpolation(0, getWidth() - bottomRightSpacing);
        scaleTargetH = new LinearInterpolation(0, getHeight() - bottomRightSpacing);

        canvasChild = c;

        setOpaque(false);
    }

    /**
     * Constructor.
     *
     * @param mainCanvas the main Canvas object
     * @param type icon type of the message
     */
    public PulseIcon(Canvas mainCanvas, MessageType type) {
        setMainCanvas(mainCanvas);

        messageType = type;

        setSize(new Dimension(65, 65));
        setPreferredSize(new Dimension(65, 65));

        setMessageType(type);

        topLeftSpacing = (int) shapeThickness;
        bottomRightSpacing = (int) shapeThickness + topLeftSpacing;

        scaleTargetX = new LinearInterpolation(getWidth() / 2.0, topLeftSpacing);
        scaleTargetY = new LinearInterpolation(getHeight() / 2.0, topLeftSpacing);
        scaleTargetW = new LinearInterpolation(0, getWidth() - bottomRightSpacing);
        scaleTargetH = new LinearInterpolation(0, getHeight() - bottomRightSpacing);

        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        BufferedImage buffer = getImage();

        VScale scale = TransformingParent.getScale(getMainCanvas());
        if (!scale.isIdentity()) {
            g2.setTransform(AffineTransform.getScaleInstance(scale.getScaleX(), scale.getScaleY()));
        }

        g2.drawImage(buffer, 0, 0, getWidth(), getHeight(), null);
    }

    /**
     * Defines the size of the icon.
     *
     * @param t scale value t in (0,1)
     */
    public void setScaleValue(double t) {

        scaleValue = t;

        scaleTargetX.step(t);
        scaleTargetY.step(t);
        scaleTargetW.step(t);
        scaleTargetH.step(t);

        valueX = scaleTargetX.getValue();
        valueY = scaleTargetY.getValue();

        valueW = scaleTargetW.getValue();
        valueH = scaleTargetH.getValue();

        shape = new Ellipse2D.Double(valueX, valueY, valueW, valueH);
    }

    /**
     * Sets alpha composite of the specified graphics object.
     *
     * @param g2 the graphics object that is to be changed
     */
    public void setAlphaComposite(Graphics2D g2) {
        float value = (float) transparency;
        AlphaComposite ac1
                = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                        value);
        g2.setComposite(ac1);
    }

    /**
     * Renders the ico shape.
     *
     * @param g2 the Graphics object that is used for rendering
     */
    public void drawShape(Graphics2D g2) {
        setAlphaComposite(g2);
        g2.setColor(color);
        g2.draw(shape);
    }

    /**
     * Effects that are to be applied after drawing the shape.
     *
     * @param img the image the effect is to be applied to
     * @return the resulting image
     */
    private BufferedImage postProcessing(BufferedImage img) {
        float value = 1.0f / 9;

        float[] BLUR = {
            value, value, value,
            value, value, value,
            value, value, value
        };

        ConvolveOp vBlurOp = new ConvolveOp(new Kernel(3, 3, BLUR));

        for (int i = 0; i < 1; i++) {
            img = vBlurOp.filter(img, null);
        }

        img = ImageUtils.radialGradientMask(img, 1.0f, 0.0f);

        return img;
    }

    /**
     * Returns an image containing the rendered icon.aa
     *
     * @return the rendered image
     */
    BufferedImage getImage() {
        if (img == null || img.getWidth() != getWidth()
                || img.getHeight() != getHeight()) {
            img = new BufferedImage(getWidth(), getHeight(),
                    BufferedImage.TYPE_INT_ARGB);

            shapeThickness = getWidth() / 8;
            topLeftSpacing = (int) shapeThickness;
            bottomRightSpacing = (int) shapeThickness + topLeftSpacing;

            scaleTargetX = new LinearInterpolation(getWidth() / 2.0, topLeftSpacing);
            scaleTargetY = new LinearInterpolation(getHeight() / 2.0, topLeftSpacing);

            scaleTargetW = new LinearInterpolation(0, getWidth() - bottomRightSpacing);
            scaleTargetH = new LinearInterpolation(0, getHeight() - bottomRightSpacing);

            Graphics2D g2 = img.createGraphics();

            BasicStroke stroke = new BasicStroke(shapeThickness,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

            g2.setStroke(stroke);

            drawShape(g2);

            g2.dispose();

            img = postProcessing(img);
        }

        return img;
    }

    /**
     * Returns an image containing several animation frames of the icon.
     *
     * @return the rendered image
     */
    public BufferedImage getStillImage() {
        return getStillImage(getWidth(), getHeight());
    }

    /**
     * Returns an image containing several animation frames of the icon.
     *
     * @return the rendered image
     */
    public BufferedImage getStillImage(int w, int h) {
        if (img == null || img.getWidth() != w
                || img.getHeight() != h) {
            img = new BufferedImage(w, h,
                    BufferedImage.TYPE_INT_ARGB);

            shapeThickness = w / 8;
            topLeftSpacing = (int) shapeThickness;
            bottomRightSpacing = (int) shapeThickness + topLeftSpacing;

            scaleTargetX = new LinearInterpolation(w / 2.0, topLeftSpacing);
            scaleTargetY = new LinearInterpolation(w / 2.0, topLeftSpacing);

            scaleTargetW = new LinearInterpolation(0, h - bottomRightSpacing);
            scaleTargetH = new LinearInterpolation(0, h - bottomRightSpacing);

            Graphics2D g2 = img.createGraphics();

            BasicStroke stroke = new BasicStroke(shapeThickness,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

            g2.setStroke(stroke);

            setScaleValue(0.2);
            setTransparency(1.0f);
            setAlphaComposite(g2);
            drawShape(g2);

            setScaleValue(0.6);
            setTransparency(0.75f);
            setAlphaComposite(g2);
            drawShape(g2);

            setScaleValue(1.0);
            setTransparency(0.5f);
            setAlphaComposite(g2);
            drawShape(g2);

            g2.dispose();

            img = postProcessing(img);
        }

        return img;
    }

    /**
     * Starts pulse animation at location of CanvasChild c.
     *
     * @param c the CanvasChild used to define the location of the animation
     */
    public void pulse(CanvasChild c) {
        if (c != null) {
            canvasChild = c;
            setLocation(c);
        }
        pulseAnimation(0.0, duration, repeats);
    }

    /**
     * Starts pulse animation at location of CanvasChild c.
     *
     * @param c the CanvasChild used to define the location of the animation
     * @param transparency the initial transparency of the animation
     */
    public void pulse(CanvasChild c, double transparency) {
        if (c != null) {
            canvasChild = c;
            setLocation(c);
        }
        pulseAnimation(transparency, 0.0, duration, repeats);
    }

    /**
     * Starts pulse animation at location of CanvasChild c.
     *
     * @param c the CanvasChild used to define the location of the animation
     * @param transparency the initial transparency of the animation
     * @param duration the duration of the animation
     * @param repeats number of repeats
     */
    public void pulse(CanvasChild c, double transparency,
            double duration, int repeats) {
        setLocation(c);
        pulseAnimation(0.0, duration, repeats);
    }

    /**
     * Starts pulse animation at location of CanvasChild c.
     */
    public void pulse() {
        pulse(getCanvasChild());
    }

    /**
     * Starts pulse animation at location of CanvasChild c.
     *
     * @param repeats number of repeats
     */
    public void pulse(int repeats) {
        pulse(1.0, duration, repeats);
    }

    /**
     * Starts pulse animation at location of CanvasChild c.
     *
     * @param transparency the initial transparency of the animation
     * @param duration the duration of the animation
     * @param repeats number of repeats
     */
    public void pulse(double transparency, double duration, int repeats) {
        if (getCanvasChild() != null) {
            setLocation(getCanvasChild());
        }
        pulseAnimation(transparency, 0.0, duration, repeats);
    }

    /**
     * Defines the location of the animation.
     *
     * @param c the CanvasChild used to define the location of the animation
     */
    public void setLocation(CanvasChild c) {
        canvasChild = c;

        Point pos = mainCanvas.getAbsPos((JComponent) c, true);

        Component comp = null;

        if (pos != null) {
            comp = (Component) c;

            if (getParent() != mainCanvas.getEffectPane()) {
                mainCanvas.getEffectPane().add(this);
            }
            pos = new Point(pos.x - this.getWidth() / 2 + comp.getWidth() / 2,
                    pos.y - this.getHeight() / 2 + comp.getHeight() / 2);
            super.setLocation(pos);
        }
    }

    /**
     * Defines the location of the icon center.
     *
     * @param p the location to set
     */
    public void setCenterLocation(Point p) {
        super.setLocation(p.x - this.getWidth() / 2,
                p.y - this.getHeight() / 2);
    }

    /**
     * Defines the location of the icon center.
     *
     * @param x the x value to set
     * @param y the y value to set
     */
    public void setCenterLocation(int x, int y) {
        super.setLocation(x - this.getWidth() / 2,
                y - this.getHeight() / 2);
    }

    /**
     * Updates the location of the animation if the location of the
     * corresponding CanvasChild object has changed.
     */
    public void updateLocation() {
        if (getCanvasChild() != null) {
            setLocation(getCanvasChild());
        }
    }

    /**
     * Starts pulse animation.
     *
     * @param offset the offset of the animation
     * @param duration the duration of the animation
     * @param repeats number of repeats
     */
    private void pulseAnimation(double offset, double duration, int repeats) {
        pulseAnimation(1.0, offset, duration, repeats);
    }

    /**
     * Starts pulse animation.
     *
     * @param transparency the initial transparency of the animation
     * @param offset the offset of the animation
     * @param duration the duration of the animation
     * @param repeats number of repeats
     */
    private void pulseAnimation(double transparency,
            double offset, double duration, int repeats) {
        final Animation a = new PulseIconAnimation(this, transparency);
        a.setOffset(offset);
        a.setDuration(duration);

        a.addFrameListener(new AnimationTask() {

            @Override
            public void firstFrameStarted() {
                //
            }

            @Override
            public void frameStarted(double time) {
                //
            }

            @Override
            public void lastFrameStarted() {
                if (a.getRepeats() >= a.getNumberOfRepeats()) {
                    setCurrentAnimation(null);
                    getMainCanvas().getEffectPane().remove(PulseIcon.this);
                }
            }
        });

        if (getCurrentAnimation() != null) {
            getCurrentAnimation().requestDeletion();
        }
        this.setCurrentAnimation(a);

        getMainCanvas().getAnimationManager().addAnimation(a, repeats);
//        offset += duration * repeats;
//        removeFromEffectPane(offset);
    }

//    /**
//     * Removes the PulseIcon object from the effect pane
//     * @param offset time until the object shall be removed
//     */
//    public void removeFromEffectPane(double offset) {
//        Animation a = new PulseIconRemoveAnimation(this);
//        a.setOffset(offset);
//        a.setDuration(1);
//        getMainCanvas().getAnimationManager().addAnimation(a, 1);
//    }
    @Override
    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    @Override
    public void setMainCanvas(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;
    }

    @Override
    public void contentChanged() {
        img = null;
    }

    /**
     * Returns the transparency of the icon
     *
     * @param transparency the transparency of the icon
     */
    public void setTransparency(double transparency) {
        this.transparency = transparency;
    }

    /**
     * Returns the color of the icon.
     *
     * @return the color of the icon
     */
    public Color getColor() {
        return color;
    }

    /**
     * Defines the color of the icon.
     *
     * @param color the color that is to be defined as icon color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Defines the message type of the icon
     *
     * @param type the messagetype
     */
    public void setMessageType(MessageType type) {
        switch (type) {
            case INFO:
                setColor(Color.GREEN);
                repeats = 8;
                duration = 1.0;
                break;
            case WARNING:
                setColor(Color.YELLOW);
                repeats = 8;
                duration = 0.8;
                break;
            case WARNING_SINGLE:
                setColor(Color.YELLOW);
                repeats = 1;
                duration = 0.8;
                break;
            case ERROR:
                setColor(Color.RED);
                repeats = 8;
                duration = 0.5;
                break;
            case ERROR_SINGLE:
                setColor(Color.RED);
                repeats = 1;
                duration = 1.0;
                break;
            case INFO_SINGLE:
                setColor(Color.GREEN);
                repeats = 1;
                duration = 1.0;
                break;
            case SILENT:
                setColor(VSwingUtil.TRANSPARENT_COLOR);
                repeats = 1;
                duration = 1.0;
                break;
        }
    }

//    @Override
//    protected void finalize() throws Throwable {
//
//        System.out.println(getClass().getName() + ": Finalize!!!");
//
//        super.finalize();
//    }
    /**
     * Returns the CanvasChild object that is used to define the location of the
     * pulse animation.
     *
     * @return the CanvasChild object that is used to define the location of the
     * pulse animation
     */
    public CanvasChild getCanvasChild() {
        return canvasChild;
    }

    /**
     * Removes the canvas child from this pulse icon. This is sometimes
     * necessary to allow the garbage collector to delete the parent components
     * of the canvas child, e.g., object representations.
     */
    public void removeCanvasChild() {
        canvasChild = null;
    }

    /**
     * Returns the message type of the icon.
     *
     * @return the message type of the icon
     */
    public MessageType getMessageType() {
        return messageType;
    }

    /**
     * Returns the current animation.
     *
     * @return the current animation if such an animation exists;
     * <code>null</code> otherwise
     */
    public Animation getCurrentAnimation() {
        return currentAnimation;
    }

    /**
     * Defines the current animation.
     *
     * @param currentAnimation the animation to set
     */
    private void setCurrentAnimation(Animation currentAnimation) {
        this.currentAnimation = currentAnimation;
    }
//    @Override
//    protected void finalize() throws Throwable {
//
//        System.out.println(getClass().getName() + ": Finalize!!!");
//
//        super.finalize();
//    }
}

/**
 * PulseIconAnimation defines the animation used by PulseIcon.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class PulseIconAnimation extends Animation implements FrameListener {

    /**
     * PulseIcon object that is to be animated
     */
    private PulseIcon icon;
    /**
     * maximum icon transparency
     */
    private double transparency;

    /**
     * Constructor.
     *
     * @param icon the PulseIcon object that is to be animated
     * @param transparency the transparency of the pulse icon
     */
    public PulseIconAnimation(PulseIcon icon, double transparency) {
        addFrameListener(this);
        getInterpolators().add(new LinearInterpolation(0.0, 1.0));
        this.icon = icon;
        this.transparency = transparency;
    }

    @Override
    public void frameStarted(double time) {
        double value = getInterpolators().get(0).getValue();

        try {
            icon.updateLocation();
            icon.setScaleValue(value);
            icon.setTransparency((1.0 - value) * transparency);
            icon.contentChanged();
            icon.revalidate();
            icon.repaint();
        } catch (Exception ex) {
            // visual bugs only
        }
    }
}
//
///**
// * PulseIconRemoveAnimation is used to remove a PusleIcon object from the effect
// * pane of the MainCanvas object.
// * @author Michael Hoffer <info@michaelhoffer.de>
// */
//class PulseIconRemoveAnimation extends Animation implements FrameListener {
//
//    /**
//     * the icon that is to be removed
//     */
//    private PulseIcon icon;
//
//    /**
//     * Constructor.
//     * @param icon the icon that is to be removed
//     */
//    public PulseIconRemoveAnimation(PulseIcon icon) {
//        addFrameListener(this);
//
//        this.icon = icon;
//    }
//
//    @Override
//    public void frameStarted(double time) {
//        if (time == 0) {
//            icon.getMainCanvas().getEffectPane().remove(icon);
////            System.out.println("PulseIcon: removed!");
//        }
//    }
//}

