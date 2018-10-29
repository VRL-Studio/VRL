/* 
 * ColorizeEffect.java
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

package eu.mihosoft.vrl.effects;

/*
 * SelectionEffect.java
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2009 Michael Hoffer &lt;info@michaelhoffer.de&gt;
 *
 * Supported by the Goethe Center for Scientific Computing of Prof. Wittum
 * (http://gcsc.uni-frankfurt.de)
 *
 * This file is part of Visual Reflection Library (VRL).
 *
 * VRL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * VRL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
import eu.mihosoft.vrl.visual.ShapeDescription;

import eu.mihosoft.vrl.animation.Animation;
import eu.mihosoft.vrl.animation.FrameListener;
import eu.mihosoft.vrl.animation.LinearInterpolation;
import eu.mihosoft.vrl.visual.ImageUtils;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Colorize effect for GUI components.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ColorizeEffect implements Effect {

    private static final long serialVersionUID = -457018804175715139L;
    private float transparency = 0.f;
    private Color color;
    transient private BufferedImage buffer;
    transient private BufferedImage childComponentBuffer;
    private Component component;
    private Component childComponent;
    private EffectPainter effectPainter;
    private boolean active = false;
    private boolean uncolorize = false;
    private String name = "ColorizeEffect";
    private ArrayList<FrameListener> frameListeners =
            new ArrayList<FrameListener>();
    private Animation currentAnination;

    /**
     * Constructor.
     * @param e the effect painter
     */
    public ColorizeEffect(EffectPainter e) {
        effectPainter = e;
        component = (Component) e;
    }

    /**
     * Defines whether this effect shall be an uncolorize effect
     * (fading out color).
     * @param value the state to set
     */
    public void setUncolorize(boolean value) {
        uncolorize = value;
    }

    /**
     * Defines this effect as uncolorize effect (fading out color).
     */
    public void defineAsUncolorizeEffect() {
        setUncolorize(true);
    }

    /**
     * Defines this effect as colorize effect (fading in color).
     */
    public void defineAsColorizeEffect() {
        setUncolorize(false);
    }

    /**
     * Returns the current animation if this effect.
     * @return the current animation if this effect
     */
    protected Animation getCurrentAnimation() {
        return currentAnination;
    }

    /**
     * Defines the current animation of this effect.
     * @param a the animation to define as current animation
     */
    protected void setCurrentAnimation(Animation a) {
        currentAnination = a;
    }

    /**
     * Computes the size of the child component.
     * @return the size of the child component
     */
    public Dimension computeChildComponentSize() {
        int size = Math.min(
                component.getWidth(), component.getHeight());
        return new Dimension(size, size);
    }

    /**
     * Computes the location of the child component.
     * @return the location of the child component
     */
    public Point computeChildComponentLocation() {
        Point result = new Point();

        result.x = component.getWidth() / 2 - childComponentBuffer.getWidth() / 2;
        result.y = component.getHeight() / 2 - childComponentBuffer.getHeight() / 2;

        return result;
    }

    @Override
    public void paint(BufferedImage img) {

        if (buffer == null || buffer.getWidth() != getComponent().getWidth()
                || buffer.getHeight() != getComponent().getHeight()) {

            buffer = ImageUtils.createCompatibleImage(
                    getComponent().getWidth(), getComponent().getHeight());

            if (getChildComponent() != null) {
                Dimension childComponentSize = computeChildComponentSize();


                childComponentBuffer =
                        ImageUtils.createCompatibleImage(
                        childComponentSize.width,
                        childComponentSize.height);
            }
        }

        Graphics2D g2 = buffer.createGraphics();

        Composite originalComposite = g2.getComposite();

        g2.setComposite(AlphaComposite.Clear);
        g2.setPaint(new Color(0.f, 0.f, 0.f, 0.f));
        g2.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

        g2.setComposite(originalComposite);

        g2.drawImage(img, 0, 0, null);

        AlphaComposite ac1 =
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                getTransparency());
        g2.setComposite(ac1);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getColor());

        if (component instanceof ShapeDescription) {
            ShapeDescription s = (ShapeDescription) component;
            g2.fill(s.getShape());
        } else {
            g2.fillRect(0, 0, img.getWidth(), img.getHeight());
        }

        g2.dispose();

        g2 = img.createGraphics();

        originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.Clear);
        g2.setPaint(new Color(0.f, 0.f, 0.f, 0.f));
        g2.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
        g2.setComposite(originalComposite);

        g2.drawImage(buffer, 0, 0, null);

        if (getChildComponent() != null) {
            ImageUtils.clearImage(childComponentBuffer);

            childComponent.setSize(childComponentBuffer.getWidth(),
                    childComponentBuffer.getHeight());

            Graphics2D childG2 = childComponentBuffer.createGraphics();

            childComponent.paint(childG2);

            Point childComponentLocation = computeChildComponentLocation();

            g2.drawImage(childComponentBuffer,
                    childComponentLocation.x,
                    childComponentLocation.y, null);
        }

        g2.dispose();
    }

    @Override
    public Animation getAnimation() {
        currentAnination = new ColorizeAnimation(this);

        for (FrameListener l : frameListeners) {
            currentAnination.addFrameListener(l);
        }

        return currentAnination;
    }

    /**
     * Adds a frame listener to the animation of this effect.
     * @param l the listener to add
     */
    public void addFrameListener(FrameListener l) {
        frameListeners.add(l);
    }

    /**
     * Removes a frame listener from the animation of this effect.
     * @param l the listener to remove
     */
    public void removeFrameListener(FrameListener l) {
        frameListeners.remove(l);
    }

    /**
     * Returns the animation of this effect.
     * @return the animation of this effect
     */
    public float getTransparency() {
        return transparency;
    }

    /**
     * Defines the transparency of this effect.
     * @param transparency the transparency of this effect
     */
    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }

    /**
     * Returns the associated component.
     * @return the associated component
     */
    public Component getComponent() {
        return component;
    }

    /**
     * Defines the component this effect shall be applied to.
     * @param component the component to set
     */
    public void setComponent(Component component) {
        this.component = component;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;

        if (!active) {
            contentChanged();
        }
    }

    @Override
    public EffectPainter getEffectPainter() {
        return effectPainter;
    }

    /**
     * Returns the color of this effect.
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Defines the color of this effect.
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Indicates whether this effect is an unselection effect.
     * @return <code>true</code> if this effect is an unselection effect;
     *         <code>false</code> otherwise
     */
    public boolean isUncolorize() {
        return uncolorize;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the child component. See {@link SpinningWheelEffect} for child
     * component usage.
     * @return the child component
     */
    public Component getChildComponent() {
        return childComponent;
    }

    /**
     * Defines the child component. See {@link SpinningWheelEffect} for child
     * component usage.
     * @param childComponent the component to set
     */
    public void setChildComponent(Component childComponent) {
        this.childComponent = childComponent;
    }

    @Override
    public void contentChanged() {
        buffer = null;
    }
}

/**
 * Colorize animation for selection effects.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class ColorizeAnimation extends Animation implements FrameListener {

    private LinearInterpolation alphaTarget;
    private ColorizeEffect effect;
    private float initialTransparency;

    /**
     * Constructor.
     * @param effect the effect to animate
     */
    public ColorizeAnimation(ColorizeEffect effect) {
        this.effect = effect;
        addFrameListener(this);

        initialTransparency = effect.getTransparency();

        if (effect.isUncolorize()) {
            // add targets
            alphaTarget = new LinearInterpolation(initialTransparency, 0.f);
        } else {
            // add targets
            alphaTarget = new LinearInterpolation(initialTransparency, 1.f);
        }

        getInterpolators().add(alphaTarget);

//        setDeleteAfterExecution(false);
    }

    @Override
    public void frameStarted(double time) {
        if (getTime() == 0.0) {
            effect.setActive(true);
            effect.getComponent().setVisible(true);

        }

        effect.setTransparency((float) alphaTarget.getValue());

        if (getTime() < 1.0) {
            effect.getComponent().repaint();
        }
        
        if (getTime() == 1.0) {
            if (effect.isUncolorize()) {
                effect.setActive(false);

            }
//            effect.setTransparency(initialTransparency);

            if (getRepeats() >= getNumberOfRepeats() && deleteAfterExecution()) {
                effect = null;
            }
        }
    }
}
