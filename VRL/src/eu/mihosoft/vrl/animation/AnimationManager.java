/* 
 * AnimationManager.java
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

package eu.mihosoft.vrl.animation;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

/**
 * AnimationManager manages animations. It is also responsible for repainting
 * the MainCanvas object if necessary.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class AnimationManager implements Serializable {

    private static final long serialVersionUID = -6339194667810508884L;
    /**
     * defines how many times the run method of the animation manager is called;
     * this is not necessarily the same as the repraint rate of the canvas
     */
    private double fps = 100;
    /**
     * defines the tolerance
     */
    private double TOL = Double.MIN_VALUE;
    /**
     * the list of animations that are managed by this manager
     */
    private final ArrayList<AnimationBase> animations =
            new ArrayList<AnimationBase>();
    /**
     * the list of animations that are managed by this manager
     */
    private final ArrayList<AnimationBase> addedAnims =
            new ArrayList<AnimationBase>();
    /**
     * the repaint thread that is responsible for repainting the main canvas
     */
    transient private RepaintThread repaintThread;
    /**
     * the new time value
     */
    private long startTime = 0;
    /**
     * the last time value
     */
    private long oldTime = 0;
    /**
     * the number of frames since last time measurement
     */
    private long numberOfFrames = 0;
    /**
     * Defines how often time and fps should be measured
     */
    private int timeMeasureInterval = 5; // every n-th frame
    /**
     * defines whether to print fps to std out
     */
    private boolean showFPS = false;

    /**
     * Constructor.
     */
    public AnimationManager() {
        setRepaintThread(new RepaintThread(this));
    }

    /**
     * Constructor.
     *
     * @param repaintThread the repaint thread
     */
    public AnimationManager(RepaintThread repaintThread) {
        setRepaintThread(repaintThread);
    }

    /**
     * Adds an animation to the manager.
     *
     * @param a the animation that is to be added to the manager
     */
    public synchronized void addAnimation(AnimationBase a) {
        addAnimation(a, 1);
    }

    /**
     * Adds an animation to the manager.
     *
     * @param a the animation that is to be added
     * @param repeats the number of times the animation is to be repeated
     */
    public synchronized void addAnimation(AnimationBase a, int repeats) {
        a.setAnimationManager(this);
//        System.out.println("AnimationManager:\n>> animation added!");

        a.setNumberOfRepeats(repeats);

        //NEW
//        animations.add(a);
        addedAnims.add(a);

        if (repaintThread != null) {
//            oldTime = System.nanoTime();
            oldTime = 0;
            repaintThread.start();
        }
    }

    /**
     * Adds an animation to the manager. If an AnimationBase object with equal
     * class object already exists it will be deleted.
     *
     * @param a the animation that is to be added
     * @param repeats the number of times the animation is to be repeated
     */
    public synchronized void addUniqueAnimation(AnimationBase a, int repeats) {
        ArrayList<AnimationBase> delList = new ArrayList<AnimationBase>();

        for (AnimationBase i : animations) {
            if (i.getClass().equals(a.getClass())) {
                delList.add(i);
            }
        }

        for (AnimationBase i : delList) {
            removeAnimation(i);
        }

        addAnimation(a, repeats);
    }

    /**
     * Adds an animation to the manager. If an AnimationBase object with equal
     * class object already exists it will be deleted.
     *
     * @param a the animation that is to be added
     */
    public synchronized void addUniqueAnimation(AnimationBase a) {
        addUniqueAnimation(a, 1);
    }

    /**
     * Removes an animation from this manager.
     *
     * @param a the naimation that is to be removed
     */
    public synchronized void removeAnimation(AnimationBase a) {
//        System.out.println("AnimationManager:\n>> animation removed!");
        animations.remove(a);
    }

    /**
     * Determines how many times the run method of the animation manager is
     * called per second. This is not necessarily the same as the repraint rate
     * of the canvas.
     *
     * @return the number of times the run method of the animation manager is
     * called
     */
    public double getFps() {
        return fps;
    }

    /**
     *
     * Defines how many times the run method of the animation manager is called
     * per second. This is not necessarily the same as the repraint rate of the
     * canvas.
     *
     * @param fps the number of times the run method of the animation manager is
     * called
     */
    protected void setFps(double fps) {
        this.fps = fps;
    }

    /**
     * Runs the animation manager. This method has to be called once for each
     * time step.
     */
    synchronized void run() {

        // add pending animations
        animations.addAll(addedAnims);
        addedAnims.clear();

        if (numberOfFrames == timeMeasureInterval) {
            startTime = System.nanoTime();
            TOL = 1.0 / getFps();
        }

        if (animations.size() > 0) {

            ArrayList<AnimationBase> delList = new ArrayList<AnimationBase>();

            try {

                for (AnimationBase a : animations) {

                    if (a.deletionRequested()) {
                        delList.add(a);
                    } else {
                        a.run();
                    }

                    if (a.getTime() == 1.0) {

                        boolean deleteAnimation = a.deleteAfterExecution()
                                && (a.getRepeats() >= a.getNumberOfRepeats());

                        if (deleteAnimation) {
                            delList.add(a);
                        } else {
                            a.reset();
                        }
                    }
                }

                for (AnimationBase a : delList) {
                    removeAnimation(a);
                    a.reset();
//                System.out.println("DEL Anim: " + a.getClass().getName());
                }

            } catch (java.util.ConcurrentModificationException ex) {
                // we ignore this as the worst that can happen are unomportant
                // visual inconsistencies
                ex.printStackTrace(System.err);
            }

        } else {
            if (repaintThread != null) {
                repaintThread.stop();
            }
        }

        if (oldTime != 0 && numberOfFrames == timeMeasureInterval) {
            long diff = (startTime - oldTime) / timeMeasureInterval;
            if (diff == 0) {
                setFps(30);
            } else {
                setFps((long) 1e9 / diff);
            }
        }
//        System.out.println(">> AnimationManager: Frame: " +
//                numberOfFrames + " FPS=" +
//                getFps());

        if (isShowFPS()) {
            System.out.println(">> AnimationManager: FPS=" + getFps());
        }
        if (numberOfFrames == timeMeasureInterval) {
            oldTime = startTime;
            numberOfFrames = 0;
        }
        numberOfFrames++;
    }

    /**
     * Returns the tolerance value.
     *
     * @return the tolerance value
     */
    public double getTOL() {
        return TOL;
    }

    /**
     * Returns the repaint thread used by this animation manager.
     *
     * @return the repaint thread used by this animation manager
     */
    public RepaintThread getRepaintThread() {
        return repaintThread;
    }

    /**
     * Defines the repaint thread that is to be used by this animation manager
     *
     * @param repaintThread the repaint thread that is to be used by this
     * animation manager
     */
    public void setRepaintThread(RepaintThread repaintThread) {
        this.repaintThread = repaintThread;
    }

    /**
     * Indicates whether to show frames per seconds (fps).
     *
     * @return
     * <code>true</code> if fps is to be shown;
     * <code>false</code> otherwise
     */
    public boolean isShowFPS() {
        return showFPS;
    }

    /**
     * Defines whether to show frames per second (printed to std out).
     *
     * @param showFPS the state to set
     */
    public void setShowFPS(boolean showFPS) {
        this.showFPS = showFPS;
    }

    /**
     * Defines the maximum number of frames per second.
     *
     * @param fps the frame rate to set
     */
    public void setMaxFPS(int fps) {
        setFps(fps);
        repaintThread.setSleepTime(1000 / fps);
    }
//    @Override
//    protected void finalize() throws Throwable {
//
//        System.out.println(getClass().getName() + ": Finalize!!!");
//
//        super.finalize();
//    }
}
