/* 
 * Wheel.java
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

import eu.mihosoft.vrl.animation.Animation;
import eu.mihosoft.vrl.animation.AnimationManager;
import eu.mihosoft.vrl.animation.FrameListener;
import eu.mihosoft.vrl.animation.LinearInterpolation;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Wheel component used to generate a "spinning wheel" animation.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Wheel extends JPanel {

    private int alphaValues[];
    private int numberOfDots;
    private int dotSize = 25;
    private WheelAnimation animation;
//    private Canvas mainCanvas;
    private AnimationManager animationManager;

    /**
     * Constructor.
     * @param numberOfDots the number of dots (circles)
     * @param mainCanvas the main canvas object
     */
    public Wheel(int numberOfDots, AnimationManager animationManager) {
        this.numberOfDots = numberOfDots;
//        this.mainCanvas = mainCanvas;
        this.animationManager = animationManager;
        init();
    }

    /**
     * Initializes this component.
     */
    private void init() {
        alphaValues = new int[getNumberOfDots()];
        for (int i = 0; i < numberOfDots; i++) {
            alphaValues[i] = 0;
        }

        setOpaque(false);
    }

    /**
     * Defines the current location of the spot.
     * @param x the location to set
     */
    void setSpotLocation(int x) {
        for (int i = 0; i < getNumberOfDots(); i++) {
//            int index = (x + i) % getNumberOfDots();
//            alphaValues[index] = (char) (255 / getNumberOfDots() * i);

            alphaValues[i] = Math.max(0, alphaValues[i] - 3);
        }
        alphaValues[x] = 255;
    }

    @Override
    public void paintComponent(Graphics g) {
        final Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.white);

        g2.setStroke(new BasicStroke(getDotSize(),
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_MITER));

        final int centerX = (int) (getWidth() / 2);
        final int centerY = (int) (getHeight() / 2);

        for (int i = 0; i < getNumberOfDots(); i++) {

            double t = i * 2 * Math.PI / getNumberOfDots();

            int x = (int) (Math.cos(t) * (getWidth() / 2 - getDotSize())) + centerX;
            int y = (int) (Math.sin(t) * (getHeight() / 2 - getDotSize())) + centerY;

            g2.setColor(new Color(255, 255, 255, alphaValues[i]));

            g2.drawLine(x, y, x, y);
        }

    }

    /**
     * Returns the number of dots.
     * @return the number of dots
     */
    public int getNumberOfDots() {
        return numberOfDots;
    }


    /**
     * Starts the spinning animation.
     * @param duration the animation duration
     * @parem offset the animation offset
     * @param l a frame listener to add to the wheel animation or
     *          <code>null</code> if no listener shall be added
     */
    public void startSpin(double offset, double duration, FrameListener l) {
        setVisible(true);
        if (animation != null) {
            animation.requestDeletion();
        }
        animation = new WheelAnimation(this);
        animation.setOffset(duration);
        animation.setDuration(duration);
        animation.setDeleteAfterExecution(false);

        if (l!=null) {
            animation.addFrameListener(l);
        }

        getAnimationManager().addAnimation(animation);
    }


    /**
     * Starts the spinning animation.
     * @param duration the animation duration
     * @param l a frame listener to add to the wheel animation or
     *          <code>null</code> if no listener shall be added
     */
    public void startSpin(double duration, FrameListener l) {
        startSpin(0, duration, l);
    }




    /**
     * Starts the spinning animation.
     * @param duration the animation duration
     */
    public void startSpin(double duration) {
        startSpin(duration, null);
    }

    /**
     * Starts the spinning animation.
     */
    public void startSpin() {
        startSpin(1);
    }

    /**
     * Stops the spinning animation.
     */
    public void stopSpin() {
        setVisible(false);
        if (animation != null) {
            animation.requestDeletion();
            animation = null;
        }
    }

    /**
     * @return the animationManager
     */
    public AnimationManager getAnimationManager() {
        return animationManager;
    }

    /**
     * @param animationManager the animationManager to set
     */
    public void setAnimationManager(AnimationManager animationManager) {
        this.animationManager = animationManager;
    }

    /**
     * @return the dotSize
     */
    public int getDotSize() {
        return dotSize;
    }

    /**
     * @param dotSize the dotSize to set
     */
    public void setDotSize(int dotSize) {
        this.dotSize = dotSize;
    }
//    /**
//     * Returns the main canvas.
//     * @return the mainCanvas the main canvas
//     */
//    public Canvas getMainCanvas() {
//        return mainCanvas;
//    }
//
//    /**
//     * Defines the main canvas.
//     * @param mainCanvas the canvas to set
//     */
//    public void setMainCanvas(Canvas mainCanvas) {
//        this.mainCanvas = mainCanvas;
//    }
}

/**
 * Animates a Wheel component.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class WheelAnimation extends Animation implements FrameListener {

    private Wheel wheel;
    private LinearInterpolation posTarget;

    /**
     * Constructor.
     * @param wheel the wheel component
     */
    public WheelAnimation(Wheel wheel) {
        this.wheel = wheel;
        addFrameListener(this);

        // add targets
        posTarget = new LinearInterpolation(0, wheel.getNumberOfDots() - 1);

        getInterpolators().add(posTarget);
    }

    @Override
    public void frameStarted(double time) {

        int index = (int) Math.round(posTarget.getValue());

        wheel.setSpotLocation(index);
        wheel.repaint();
    }
}
