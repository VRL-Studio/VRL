/* 
 * Animation.java
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
import java.util.ArrayList;

/**
 * Animation is an implementation of AnimationBase used by AnimationManager.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Animation implements AnimationBase {

    private static final long serialVersionUID = 1553628992596509727L;
    /**
     * list of the animation targets used by this animation
     */
    private ArrayList<AnimationInterpolation> targets =
            new ArrayList<AnimationInterpolation>();
    /**
     * duration of the animation
     */
    private double duration;
    /**
     * the time til starting to run the animation
     */
    private double offset = 0;
    /**the time difference between adding the animation to the animation
     * manager til starting to run the animation
     */
    private double offsetBackup = 0;
    /**
     * time value of the next step, range [0,1]
     */
    private double tNew = 0;
    /**
     * time value of the current step, range [0,1]
     */
    private double t = 0;
    /**
     * increment of the time step, depends on fps and duration
     */
    private double step = 0;
    /**
     * the animation manager which manages this animation
     */
    private AnimationManager animationManager;
    /**
     * frame listeners of the animation
     */
    private ArrayList<FrameListener> frameListeners =
            new ArrayList<FrameListener>();
    /**
     * defines whether the animation is to be removed after execution,
     * if false this is equivalent to repeats=inf
     */
    private boolean deleteAfterExecution = true;
    /**
     * number of times this animation has been repeated
     */
    private int repeats = 0;
    /**
     * number of times this animation is to be repeated, i.e., played
     */
    private int repeatsBackup = 1;
    /**
     * defines whether deletion is requested (at next frame)
     */
    private boolean deletionRequested;

    @Override
    public void reset() {
        tNew = 0.0;
        if (animationManager != null) {
            step = 1.0 / (animationManager.getFps() * duration);
        }

        offset = 0;
    }

    @Override
    public void restoreOffset() {
        offset = offsetBackup;
    }

    @Override
    public double getTime() {
        return t;
    }

    /**
     * Defines the animation targets of the animation.
     * @param targets the targets of the animation
     */
    public void setTargets(ArrayList<AnimationInterpolation> targets) {
        this.targets = targets;
    }

    /**
     * Adds an animation interpolation to this animation.
     * @param t the target to add
     */
    public void addInterpolation(AnimationInterpolation t) {
        targets.add(t);
    }

    /**
     * Removes an animation interpolation from this animation.
     * @param t the target to remove
     */
    public void removeInterpolation(AnimationInterpolation t) {
        targets.remove(t);
    }

    /**
     * Returns the animation targets of this animation.
     * @return the animation targets of this animation
     */
    public ArrayList<AnimationInterpolation> getInterpolators() {
        return targets;
    }

    @Override
    public void setDuration(double duration) {
        this.duration = duration;
    }

    @Override
    public double getDuration() {
        return duration;
    }

    @Override
    public int getRepeats() {
        return this.repeats;
    }

    @Override
    public int getNumberOfRepeats() {
        return this.repeatsBackup;
    }

    @Override
    public void setNumberOfRepeats(int repeats) {
        this.repeats = 0;
        this.repeatsBackup = repeats;
    }

    @Override
    public void clearRepeats() {
        repeats = 0;
    }

    /**
     * Does currently nothing.
     */
    protected void update() {
        //
    }

    @Override
    public synchronized void run() {
        if (Double.compare(offset, 0) > 0) {
            offset -= 1.0 / animationManager.getFps();
            // t has to be set to zero because otherwise
            // in some occasions (offset > 0 and repeats > 0) the animation
            // will never be deleted from the animation manager
            t = 0;
            tNew = 0;
        }
        if ((offset < animationManager.getTOL()) /*&& (1.0 - getTime() > 0)*/) {
            step = 1.0 / (animationManager.getFps() * duration);

            t = tNew;

            if (t >= 1.0) {
                t = 1.0;
                repeats += 1;
            }

            for (AnimationInterpolation target : getInterpolators()) {
                target.step(t);
            }

//            System.out.println("  >>>Run: " + t);

            tNew += step;

            notifyListeners();
        }
    }

    /**
     * Notifies frame listeners.
     */
    private void notifyListeners() {
        for (FrameListener frameListener : frameListeners) {

            if (frameListener != null) {
                frameListener.frameStarted(getTime());

//              TODO performance issues or not?
                if (frameListener instanceof AnimationTask) {
                    AnimationTask task = (AnimationTask) frameListener;

                    if (getTime() == 0.0) {
                        task.firstFrameStarted();
                    }

                    if (getTime() == 1.0) {
                        task.lastFrameStarted();
                    }
                }
            }
        }
    }

    @Override
    public AnimationManager getAnimationManager() {
        return animationManager;
    }

    @Override
    public void setAnimationManager(AnimationManager animationManager) {
        this.animationManager = animationManager;
    }

    @Override
    public double getOffset() {
        return offsetBackup;
//        return offset;
    }

    @Override
    public void setOffset(double offset) {
        this.offset = offset;
        this.offsetBackup = offset;
    }

//    @Override
//    public AnimationBase copy() {
//        Animation a = new Animation(); // TODO this concept can't work!
//        a.setAnimationManager(animationManager);
//        a.setDuration(duration);
//        a.setOffset(offset);
//        a.setFrameListener(frameListener);
//        a.setDeleteAfterExecution(deleteAfterExecution);
//        a.setNumberOfRepeats(repeatsBackup);
//        a.setTargets(targets);
//
//        return a;
//    }
    @Override
    public void addFrameListener(FrameListener listener) {
        frameListeners.add(listener);
    }

    @Override
    public ArrayList<FrameListener> getFrameListeners() {
        return frameListeners;
    }

    @Override
    public boolean deleteAfterExecution() {
        return deleteAfterExecution;
    }

    /**
     * Defines whether to delete this animation after execution 
     * (default is <code>true</code>). If set to <code>false</code> this is
     * equivalent to <code>repeats=inf</code>.
     * @param deleteAfterExecution the state to set
     */
    public void setDeleteAfterExecution(boolean deleteAfterExecution) {
        this.deleteAfterExecution = deleteAfterExecution;
    }

    @Override
    public void requestDeletion() {
        this.deletionRequested = true;
    }

    @Override
    public boolean deletionRequested() {
        return deletionRequested;
    }
//
//    @Override
//    protected void finalize() throws Throwable {
//
//        System.out.println(getClass().getName() + ": Finalize!!!");
//
//        super.finalize();
//    }
}
