/* 
 * AnimatedLayout.java
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

package eu.mihosoft.vrl.animation;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.JComponent;

/**
 * This class allows to animate any layout that implements the
 * LayoutManager interface, i.e., this animated layout defines a linear
 * transition for the bounds of all components based on the
 * {@link java.awt.LayoutManager#layoutContainer(java.awt.Container) }
 * method.
 * @see java.awt.LayoutManager
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class AnimatedLayout implements LayoutManager2 {
//    private ArrayList<Dimension> sizesBefore = new ArrayList<Dimension>();

    private AnimationManager animationManager = new AnimationManager();
    private double duration = 0.5;
    private boolean firstRun = true;
    private boolean animated = false;
    private LayoutManager layout;
    private ArrayList<FrameListener> frameListeners =
            new ArrayList<FrameListener>();
    private ArrayList<JComponent> excludedComponents =
            new ArrayList<JComponent>();

    /**
     * Constructor.
     * @param l the layout manager to animate
     */
    public AnimatedLayout(LayoutManager l) {

        if (l == null) {
            throw new IllegalArgumentException(
                    "LayoutManager must be defined!");
        }

        setLayout(l);
    }

    @Override
    public void layoutContainer(final Container parent) {

        if (firstRun) {
            if (isAnimated()) {
                animateLayout(parent);
            } else {
                layout.layoutContainer(parent);
            }
        }
    }

    /**
     * Creates the layout animation.
     * @param parent the container
     */
    @SuppressWarnings("element-type-mismatch")
    private void animateLayout(Container parent) {

        ArrayList<Rectangle> boundsBefore = new ArrayList<Rectangle>();

        // store current component bounds
        for (Component c : parent.getComponents()) {

            if (!excludedComponents.contains(c)) {
                boundsBefore.add(c.getBounds());
            }
            
            firstRun = false;
        }

        AnimationGroup group = new AnimationGroup(animationManager);

        for (FrameListener l : frameListeners) {
            group.addFrameListener(l);
        }

        group.setDuration(getDuration());

        layout.layoutContainer(parent);


        // store target component bounds
        ArrayList<Rectangle> boundsAfter = new ArrayList<Rectangle>();
        for (int i = 0; i < boundsBefore.size(); i++) {
            Component c = parent.getComponent(i);

            if (!excludedComponents.contains(c)) {
                boundsAfter.add(c.getBounds());
                c.setBounds(boundsBefore.get(i));
            }
        }

        // define animation group based on componentbound animations
        for (int i = 0; i < boundsBefore.size(); i++) {
            Animation a = new ComponentBoundsAnimation(
                    (JComponent) parent.getComponent(i),
                    boundsBefore.get(i),
                    boundsAfter.get(i));
            a.setDuration(getDuration());
            group.add(a);
        }

        boolean beforeDiffersFromAfter = false;

        for (int i = 0; i < boundsBefore.size(); i++) {
            if (!boundsBefore.get(i).equals(boundsAfter.get(i))) {
                beforeDiffersFromAfter = true;
                break;
            }
        }

        if (beforeDiffersFromAfter) {

            group.addFrameListener(new AnimationTask() {

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
                    firstRun = true;
                }
            });

            animationManager.addUniqueAnimation(group);
        } else {
            firstRun = true;
        }
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        layout.addLayoutComponent(name, comp);


    }

    @Override
    public void removeLayoutComponent(Component comp) {
        layout.removeLayoutComponent(comp);


    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return layout.preferredLayoutSize(parent);


    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return layout.minimumLayoutSize(parent);


    }

    /**
     * @return the layout
     */
    public LayoutManager getLayout() {
        return layout;
    }

    /**
     * @param layout the layout to set
     */
    private void setLayout(LayoutManager layout) {
        this.layout = layout;

    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        if (layout instanceof LayoutManager2) {
            ((LayoutManager2) layout).addLayoutComponent(comp, constraints);
        }
    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        Dimension result = new Dimension();
        if (layout instanceof LayoutManager2) {
            result = ((LayoutManager2) layout).maximumLayoutSize(target);
        }

        return result;
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        float result = 0;

        if (layout instanceof LayoutManager2) {
            result = ((LayoutManager2) layout).getLayoutAlignmentX(target);
        }

        return result;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        float result = 0;

        if (layout instanceof LayoutManager2) {
            result = ((LayoutManager2) layout).getLayoutAlignmentY(target);
        }

        return result;
    }

    @Override
    public void invalidateLayout(Container target) {

        if (layout instanceof LayoutManager2) {
            ((LayoutManager2) layout).invalidateLayout(target);
        }
    }

    /**
     * @return the duration
     */
    public double getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(double duration) {
        this.duration = duration;
    }

    /**
     * Indicates whether this layout is animated.
     * @return <code>true</code> if this layout is animated;
     *         <code>false> otherwise
     */
    public boolean isAnimated() {
        return animated;
    }

    /**
     * Indicates whether this layout shall be animated.
     * @param animated the state to set
     */
    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    /**
     * Adds a frame listener to the animation of this layout.
     * @param l the frame listener to add
     */
    public void addFrameListener(FrameListener l) {
        frameListeners.add(l);
    }

    /**
     * Removes a frame fistener from the animation of this layout.
     * @param l the frame listener to remove
     */
    public void removeFrameListener(FrameListener l) {
        frameListeners.remove(l);
    }

    /**
     * Removes all frame listeners from the animation of this layout.
     */
    public void removeAllFrameListeners() {
        frameListeners.clear();
    }

    /**
     * Excludes the specified component from the layout animation.
     * @param c the component to exclude
     */
    public void excludeFromAnimation(JComponent c) {
        getExcludedComponents().add(c);
    }

    /**
     * @return the excluded components
     */
    public ArrayList<JComponent> getExcludedComponents() {
        return excludedComponents;
    }

    /**
     * Defines a list of components to be excluded from the layout animation
     * @param excludedComponents the components to be excluded from layout animation
     */
    public void setExcludedComponents(ArrayList<JComponent> excludedComponents) {
        this.excludedComponents = excludedComponents;
    }
}
