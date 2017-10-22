/* 
 * BlurEffect.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2007–2017 by Michael Hoffer
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

import eu.mihosoft.vrl.animation.Animation;
import eu.mihosoft.vrl.animation.FrameListener;
import eu.mihosoft.vrl.animation.LinearInterpolation;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.VolatileImage;

/**
 * Implementation of a blur effect. As it is CPU intensive it is not recommended
 * to use it for big components.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class BlurEffect implements Effect {

    private float transparency = 0.f;
    transient private BufferedImage buffer;
    private Component component;
    private EffectPainter effectPainter;
    private boolean active = false;
    private float blurValue = 0;
    private String name = "BlurEffect";

    /**
     * Constructor.
     * @param e the effect painter to use
     */
    public BlurEffect(EffectPainter e) {
        effectPainter = e;
        component = (Component) e;
    }

    @Override
    public void paint(BufferedImage img) {

        if (active) {
            if (buffer == null || buffer.getWidth() != getComponent().getWidth() ||
                    buffer.getHeight() != getComponent().getHeight()) {
                buffer =
                        new BufferedImage(getComponent().getWidth(),
                        getComponent().getHeight(), BufferedImage.TYPE_INT_ARGB);
            }

            Graphics2D g2 = buffer.createGraphics();

            g2.setComposite(AlphaComposite.Clear);
            g2.setPaint(new Color(0.f, 0.f, 0.f, 0.f));
            g2.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

            AlphaComposite ac1 =
                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    1.0f);
            g2.setComposite(ac1);

            g2.drawImage(img, 0, 0, null);
            g2.dispose();

            float value = 1.0f / 9;

            float[] BLUR = {
                value, value, value,
                value, value, value,
                value, value, value
            };
            ConvolveOp vBlurOp = new ConvolveOp(new Kernel(3, 3, BLUR));
//            buffer = vBlurOp.filter(buffer, null);

//             float value = 1.0f/4;
//            
//             float[] BLUR = {value,value,value,value};
//
//            ConvolveOp vBlurOp = new ConvolveOp(new Kernel(2, 2, BLUR));

            for (int i = 0; i / 10f < getBlurValue(); i++) {
                buffer = vBlurOp.filter(buffer, null);
            }
            g2 = img.createGraphics();

            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.Clear);
            g2.setPaint(new Color(0.f, 0.f, 0.f, 0.f));
            g2.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
            g2.setComposite(originalComposite);

            g2.drawImage(buffer, 0, 0, null);

            g2.dispose();
        }
    }

//    public void paint2(BufferedImage img) {
//
//        if (active) {
//            if (buffer == null || buffer.getWidth() != getComponent().getWidth() ||
//                    buffer.getHeight() != getComponent().getHeight()) {
//                buffer =
//                        new BufferedImage(getComponent().getWidth(),
//                        getComponent().getHeight(), BufferedImage.TYPE_INT_ARGB);
//            }
//
//            Graphics2D g2 = buffer.createGraphics();
//
//            Composite originalComposite = g2.getComposite();
//
//            g2.setComposite(AlphaComposite.Clear);
//            g2.setPaint(new Color(0.f, 0.f, 0.f, 0.f));
//            g2.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
//
//            g2.setComposite(originalComposite);
//
//            g2.drawImage(img, 0, 0, null);
//
//            g2.dispose();
//
////            float value = 1.0f / 9;
////
////            float[] BLUR = {
////                value, value, value,
////                value, value, value,
////                value, value, value
////            };
////
////            ConvolveOp vBlurOp = new ConvolveOp(new Kernel(3, 3, BLUR));
////            buffer = vBlurOp.filter(buffer, null);
//
//            g2 = img.createGraphics();
//
//            originalComposite = g2.getComposite();
//            g2.setComposite(AlphaComposite.Clear);
//            g2.setPaint(new Color(0.f, 0.f, 0.f, 0.f));
//            g2.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
//            g2.setComposite(originalComposite);
//
//            g2.drawImage(buffer, 0, 0, null);
//
//            g2.dispose();
//        }
//    }
    @Override
    public Animation getAnimation() {
        return new BlurAnimation(this);
    }

    /**
     * Returns the transparency of this effect.
     * @return the transparency of this effect
     */
    public float getTransparency() {
        return transparency;
    }

    /**
     * Defines the transparency of this effect.
     * @param transparency the transparency to set
     */
    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }

    /**
     * Returns the animated component.
     * @return the animated component
     */
    public Component getComponent() {
        return component;
    }

    /**
     * Defines the component to animate.
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
     * Returns the blur value.
     * @return the blur value
     */
    public float getBlurValue() {
        return blurValue;
    }

    /**
     * Defines the blur value.
     * @param blurValue the value to set
     */
    public void setBlurValue(float blurValue) {
        this.blurValue = blurValue;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void contentChanged() {
        buffer = null;
    }
}

/**
 * Blur animation for the blur effect.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class BlurAnimation extends Animation implements FrameListener {

    private static final long serialVersionUID = -2949916006577045684L;
    private LinearInterpolation alphaTarget;
    private BlurEffect effect;

    /**
     * Constructor.
     * @param effect the effect to animate
     */
    public BlurAnimation(BlurEffect effect) {
        this.effect = effect;
        addFrameListener(this);

        // add targets     
        alphaTarget = new LinearInterpolation(0, 1);

        getInterpolators().add(alphaTarget);
    }

    @Override
    public void frameStarted(double time) {
        if (getTime() == 0.0) {
            effect.setActive(true);
            System.out.println("TIME: " + getTime());
        }
        if (getTime() == 1.0) {
            effect.setActive(false);
            // prevents memory leaks
            if (getRepeats() >= getNumberOfRepeats()) {
                effect = null;
            }
        }
        effect.setTransparency((float) alphaTarget.getValue());
        effect.setBlurValue(1.0f - (float) alphaTarget.getValue());
        effect.getComponent().repaint();
    }
}
