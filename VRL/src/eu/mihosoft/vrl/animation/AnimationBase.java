/* 
 * AnimationBase.java
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
 * Defines the interface for animation objects used by AnimationManager.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface AnimationBase extends Serializable {

    /**
     * Returns the animation manager that manages this animation.
     * @return the animation manager that manages this animation
     */
    public AnimationManager getAnimationManager();

    /**
     * Returns the duration of the animation.
     * @return the duration of the animation
     */
    public double getDuration();

    /**
     * Returns the time til starting to run the animation.
     * @return the time til starting to run the animation
     */
    public double getOffset();

    /**
     * Returns the time value of the animation.
     * @return the time value of the animation, range [0,1]
     */
    public double getTime();

    /**
     * Resets the animation.
     */
    public void reset();

    /**
     * Runs the animation. This method has to be called once for each time step.
     */
    public void run();

    /**
     * Defines the animation manager that has to manage this animation.
     * @param animationManager the animation manager that has to manage this
     * animation
     */
    public void setAnimationManager(AnimationManager animationManager);

    /**
     * Defines the duration of the animation.
     * @param duration the duration of the animation
     */
    public void setDuration(double duration);

    /**
     * Defines the time difference between adding the animation to the animation
     * manager til starting zo run the animation.
     * @param offset the time difference between adding the animation to the 
     * animation manager til starting zo run the animation
     */
    public void setOffset(double offset);

    /**
     * Restores the current offset, i.e., sets it to its initial value which
     * was specified by <code>setOffset()</code>. This method should only be
     * called by animation groups.
     */
    public void restoreOffset();

    /**
     * Returns a copy of the animation.
     * @return a copy of the animation
     */
//    public AnimationBase copy();

    
    /**
     * adds a frame listener to the animation.
     * @param listener the frame listener that is to be added to the animation
     */
    public void addFrameListener(FrameListener listener);

    /**
     * Returns the frame listener of the animation.
     * @return the frame listeners of the animation
     */
    public ArrayList<FrameListener> getFrameListeners();

    /**
     * Determines whether this animation is to be removed after execution or
     * not. Setting this value to <code>false</code> is equivalent to 
     * <code>repeats=inf</code>.
     * @return <code>true</code> if the animation is to be removed after
     *  execution; <code>false</code> otherwise
     */
    public boolean deleteAfterExecution();

    /**
     * Defines how many times this animation is to be repeated.
     * @param repeats number of repeats
     */
    public void setNumberOfRepeats(int repeats);

    /**
     * Determines how many times this animation is to be repeated.
     * @return number of times this animation is to be repeated
     */
    public int getNumberOfRepeats();

    /**
     * Returns how many times this animation has been repeated until this
     * method has been called. 
     * @return number of times this animation has been repeated until this
     * method has been called
     */
    public int getRepeats();

    /**
     * Resets the number of repeats.
     */
    public void clearRepeats();

    /**
     * Requests deletion of the animation at next frame.
     */
    public void requestDeletion();

    /**
     * Indicates whether deletion is requested.
     * @return <code>true</code> if the animation is to be deleted;
     * <code>false</code> otherwise
     */
    public boolean deletionRequested();
}
