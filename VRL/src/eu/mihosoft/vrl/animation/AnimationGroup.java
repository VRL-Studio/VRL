/* 
 * AnimationGroup.java
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

import java.util.ArrayList;

/**
 * AnimationGroup defines a group of animations. It is possible to group 
 * an arbitrary number of animations to one single animation. This simplifies
 * the process of pattern creation. An animation group is treated just like
 * any animation, i.e., the animation manager does not distinguish between
 * regular animations and animation groups.
 * 
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class AnimationGroup implements AnimationBase {

    private static final long serialVersionUID = 3413039092442618234L;
    /**
     * list of all animations that belong to this animation group
     */
    private ArrayList<AnimationBase> animations =
            new ArrayList<AnimationBase>();
    /**
     * duration if the animation
     */
    private double duration = 0;
    /**
     * the animation manager which manages this animation
     */
    private AnimationManager animationManager;
    /**
     * the time til starting to run the animation
     */
    private double offset = 0;
    /**the time difference between adding the animation to the animation
     * manager til starting to run the animation
     */
    private double offsetBackup = 0;
    /**
     * time value of the current step, range [0,1]
     */
    private double t = 0;
    /**
     * time value of the next step, range [0,1]
     */
    private double tNew = 0;
    /**
     * increment of the time step, depends on fps and duration
     */
    private double step = 0;
    /**
     * frame listeners of the animation
     */
    private ArrayList<FrameListener> frameListeners =
            new ArrayList<FrameListener>();
    /**
     * defines whether the animation is to be removed after execution,
     * equivalent to repeats=inf
     */
    private boolean deleteAfterExecution = true;
    /**
     * number of times this animation has been repeated
     */
    private int repeats = 0;
    /**
     * number of times this animation is to be repeated
     */
    private int repeatsBackup = 1;
    /**
     * defines whether deletion is requested (at next frame)
     */
    private boolean deletionRequested;

//    public AnimationGroup(){
//
//    }
    /**
     * Constructor.
     * @param animationManager the animation manager that has to manage this
     * animation
     */
    public AnimationGroup(AnimationManager animationManager) {
        this.animationManager = animationManager;
    }

    /**
     * Adds an animation to the end of this animation group.
     * @param a the animation that is to be added at the end of this animation 
     * group
     * @param repeats the number of times the animation a shall be added to this
     * animation manager
     */
    public void append(AnimationBase a, int repeats) {
//        AnimationBase copy = a.copy();
        a.setAnimationManager(animationManager);
        a.setNumberOfRepeats(repeats);
        double tmpOffset = a.getOffset() + animationManager.getTOL();
//        System.out.println("AnimationSet:\n>> animation added (starting at: " +
//                (duration + tmpOffset) + ")");
//        System.out.println(">> animation added (stopping at: " +
//                (duration + tmpOffset + a.getDuration()) + ")");
        a.setOffset(tmpOffset + duration);
        duration += tmpOffset + a.getDuration();
        animations.add(a);
    }
//    public void append(AnimationBase a) {
//        AnimationBase copy = a.copy();
//        double tmpOffset = copy.getOffset() + animationManager.getTOL();
//        System.out.println("AnimationSet:\n>> animation added (starting at: "
//                + (duration + tmpOffset) + ")");
//        System.out.println(">> animation added (stopping at: " +
//                (duration + tmpOffset + copy.getDuration()) + ")");
//        copy.setOffset(tmpOffset + duration);
//        duration += tmpOffset + copy.getDuration();
//        animations.add(copy);
//    }

    /**
     * Adds an animation to the end of this animation group.
     * @param a the animation that is to be added at the end of this animation 
     * group
     */
    public void append(AnimationBase a) {
        append(a, 1);
    }

    /**
     * Adds an animation to the end of this animation group and deletes equal
     * animations. Animations are defined as equal if there class object is
     * equal.
     * @param a the animation that is to be added at the end of this animation
     * group
     */
    public void appendUniqueAnimation(AnimationBase a) {
        ArrayList<AnimationBase> delList = new ArrayList<AnimationBase>();

        for (AnimationBase i : animations) {
            if (i.getClass().equals(a.getClass())) {
                delList.add(i);
            }
        }

        for (AnimationBase i : delList) {
            animations.remove(i);
//            System.out.println(">> AnimationGroup: equal animation removed!");
        }

        append(a);
    }

//    public void append(AnimationBase a, int repeats) {
//        for (int i = 0; i < repeats; i++) {
//            append(a);
//        }
//    }
    /**
     * Adds an animation to this animation group. The animation is not added to
     * the end of the animation group. This method is usefull if animations
     * shall be running at the same time. To specify the position of the 
     * animation in this group use the setOffset() of the animation.
     * @param a the animation that is to be added to this animation 
     * group
     * @param repeats the number of times the animation a shall be added to this
     * animation group
     */
    public void add(AnimationBase a, int repeats) {
//        AnimationBase copy = a.copy();
        a.setAnimationManager(animationManager);
        a.setNumberOfRepeats(repeats);
//        System.out.println(
//                "AnimationGroup:\n>> animation added (starting at: " +
//                a.getOffset() + ", stopping at: " +
//                (a.getOffset() + a.getDuration()) + ")");

        double animDuration = a.getOffset() + a.getDuration();

        duration = Math.max(animDuration, duration);

        animations.add(a);
    }

    /**
     * Adds an animation to group. If an AnimationBase object with equal
     * class object already exists it will be deleted.
     * @param a the animation that is to be added
     * @param repeats the number of times the animation is to be repeated
     */
    public void addUniqueAnimation(AnimationBase a, int repeats) {
        ArrayList<AnimationBase> delList = new ArrayList<AnimationBase>();

        for (AnimationBase i : animations) {
            if (i.getClass().equals(a.getClass())) {
                delList.add(i);
            }
        }

        for (AnimationBase i : delList) {
            remove(i);
        }

        add(a, repeats);
    }

    /**
     * Adds an animation to this animation group. The animation is not added to
     * the end of the animation group. This method is usefull if animations
     * shall be running at the same time. To specify the position of the 
     * animation in this group use the setOffset() of the animation.
     * @param a the animation that is to be added to this animation 
     * group
     */
    public void add(AnimationBase a) {
        add(a, 1);
    }

    @Override
    public AnimationManager getAnimationManager() {
        return animationManager;
    }

    @Override
    public double getDuration() {
        return duration;
    }

    @Override
    public double getOffset() {
        return offsetBackup;
    }

    @Override
    public double getTime() {
        return t;
    }

    /**
     * Removes an animation from this animation group.
     * @param a the animation to remove
     * @return TODO
     */
    public boolean remove(AnimationBase a) {
        return animations.remove(a);
    }

    @Override
    public void reset() {
        t = 0.0;
        tNew = 0.0;
        step = 1.0 / (animationManager.getFps() * duration);

        for (AnimationBase a : animations) {
            a.reset();
            a.restoreOffset();

//            System.out.println(">> offset: " +
//            a.getOffset() + " duration: " +
//            a.getDuration() + "t: " + a.getTime());
        }

//        offset = offsetBackup;
        offset = 0;
    }

    @Override
    public void restoreOffset() {
        offset = offsetBackup;
    }

    /**
     * Prints the animations of this group (offset and duration).
     */
    public void showAnimations() {
//        System.out.println("AnimationGroup.showAnimations():");
        int i = 0;
        for (AnimationBase a : animations) {
//            System.out.println(">> animation(" + i + "): offset=" +
//                    a.getOffset() +
//                    ", duration=" + a.getDuration() +
//                    ", repeats=" + a.getNumberOfRepeats());
            i++;
        }
    }

    @Override
    public void run() {
        if (Double.compare(offset, 0) > 0) {
            offset -= 1.0 / animationManager.getFps();
            // t has to be set to zero because otherwise
            // in some occasions (offset > 0 and repeats > 0) the animation
            // will never be deleted from the animation manager
            t = 0;
        }
        if (offset < animationManager.getTOL()) {
            step = 1.0 / (animationManager.getFps() * duration);

            t = tNew;

            if (t >= 1.0) {
                t = 1.0;
                repeats += 1;
//                System.out.println("Run: " + repeats);
            }

//            System.out.println("Run: " + t);

            for (AnimationBase a : animations) {
                a.run();
            }

            tNew += step;


            notifyListeners();
        }
    }

    /**
     * Notifies the frame listeners.
     */
    private void notifyListeners() {
        for (FrameListener frameListener : frameListeners) {

            if (frameListener != null) {
                frameListener.frameStarted(getTime());

                // TODO performance issues or not?
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
    public void setAnimationManager(AnimationManager animationManager) {
        this.animationManager = animationManager;
        for (AnimationBase a : animations) {
            a.setAnimationManager(animationManager);
        }
    }

    @Override
    public void setDuration(double newDuration) {
        //compute scale factor
        double scale = newDuration / duration;

        // apply scale factor
        for (AnimationBase a : animations) {
            a.setDuration(a.getDuration() * scale);
            a.setOffset(a.getOffset() * scale);
        }

        duration = newDuration;
    }

    @Override
    public void setOffset(double offset) {
        this.offset = offset;
//        offsetBackup = offset;
    }

    /**
     * Scales this animation group.
     * @param s the scale factor
     */
    public void scale(double s) {
        setDuration(getDuration() * s);
    }

//    @Override
//    public AnimationBase copy() {
//        AnimationGroup a = new AnimationGroup(animationManager);
//        a.duration = duration;
//        a.setOffset(offset);
//        a.setFrameListener(frameListener);
//        a.setDeleteAfterExecution(deleteAfterExecution);
//        a.setNumberOfRepeats(repeatsBackup);
//        for (AnimationBase i : animations) {
//            a.animations.add(i.copy());
//        }
//        return a;
//    }
    @Override
    public void addFrameListener(FrameListener listener) {
        this.frameListeners.add(listener);
    }

    @Override
    public ArrayList<FrameListener> getFrameListeners() {
        return this.frameListeners;
    }

    public void setDeleteAfterExecution(boolean deleteAfterExecution) {
        this.deleteAfterExecution = deleteAfterExecution;
    }

    @Override
    public boolean deleteAfterExecution() {
        return deleteAfterExecution;
    }

    @Override
    public void setNumberOfRepeats(int repeats) {
        this.repeats = 0;
        this.repeatsBackup = repeats;
    }

    @Override
    public int getNumberOfRepeats() {
        return repeatsBackup;
    }

    @Override
    public int getRepeats() {
        return repeats;
    }

    @Override
    public void clearRepeats() {
        repeats = 0;
    }

    @Override
    public void requestDeletion() {
        this.deletionRequested = true;
    }

    @Override
    public boolean deletionRequested() {
        return deletionRequested;
    }
// necessary for XMLEncoder support (java bean conventions)
//    public ArrayList<AnimationBase> getAnimations(){
//        return animations;
//    }
//
//    public void setAnimations(ArrayList<AnimationBase> animations){
//        this.animations = animations;
//    }
//    @Override
//    protected void finalize() throws Throwable {
//
//        System.out.println(getClass().getName() + ": Finalize!!!");
//
//        super.finalize();
//    }
}
