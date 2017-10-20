/* 
 * FlipEffect.java
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

package eu.mihosoft.vrl.effects;

import eu.mihosoft.vrl.animation.Animation;
import eu.mihosoft.vrl.animation.FrameListener;
import eu.mihosoft.vrl.animation.LinearInterpolation;
import eu.mihosoft.vrl.ext.com.jhlabs.image.PerspectiveFilter;
import eu.mihosoft.vrl.visual.ImageUtils;
import eu.mihosoft.vrl.visual.VComponent;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Implementation of a blur effect. As it is CPU intensive it is not recommended
 * to use it for big components.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class FlipEffect implements Effect {

    private float transparency = 0.f;
    transient private BufferedImage buffer;
    private VComponent component;
    private EffectPainter effectPainter;
    private boolean active = false;
    private float flipValue = 0;
    private String name = "FlipEffect";
    private boolean flipped = false;

    /**
     * Constructor.
     * @param e the effect painter to use
     */
    public FlipEffect(VComponent e) {
        effectPainter = e;
        component = e;
    }

    @Override
    public void paint(BufferedImage img) {

        if (active) {
            if (buffer == null || buffer.getWidth() != getComponent().getWidth()
                    || buffer.getHeight() != getComponent().getHeight()) {
                buffer = ImageUtils.createCompatibleImage(
                        getComponent().getWidth(), getComponent().getHeight());
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

            // clear image
            g2 = img.createGraphics();
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.Clear);
            g2.setPaint(new Color(0.f, 0.f, 0.f, 0.f));
            g2.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
            g2.setComposite(originalComposite);
            g2.dispose();

            rotateImage(img);         
        }
    }

    private void rotateImage(BufferedImage img) {
        float v = flipValue;

        if (v >= Math.PI / 2) {
            v = (float) (Math.PI - v);
        }

        int w = img.getWidth() / 2;

        float x = (float) Math.abs(w * (Math.cos(v)));
        float y = (float) Math.sin(v) * (img.getHeight() * 0.15f);

        float x0 = w - x;
        float y0 = 0;
        float x1 = w + x;
        float y1 = y;
        float x2 = x1;
        float y2 = img.getHeight() - y;
        float x3 = x0;
        float y3 = img.getHeight();

        PerspectiveFilter pf = new PerspectiveFilter();

        boolean switchSides = component.isFrontVisible() && (flipValue >= Math.PI / 2)
                || !component.isFrontVisible() && (flipValue <= Math.PI / 2) && !isFlipped();

        if (switchSides) {
            flipped = true;
            if (component.isFrontVisible()) {
                component.showBack();
            } else {
                component.showFront();
            }
        }

        if (flipValue >= Math.PI / 2) {
            pf.setCorners(x1, y0, x0, y1, x3, y2, x2, y3);
        } else {
            pf.setCorners(x0, y0, x1, y1, x2, y2, x3, y3);
        }

        pf.filter(buffer, img);
    }

    @Override
    public Animation getAnimation() {
        return new FlipAnimation(this);
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
    public VComponent getComponent() {
        return component;
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

    void setFlipValue(float value) {
        this.flipValue = value;
    }

    /**
     * @return the initialized
     */
    final boolean isFlipped() {
        return flipped;
    }

    /**
     * @param initialized the initialized to set
     */
    final void reset() {
        this.flipped = false;
    }
}

/**
 * Animation for the flip effect.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class FlipAnimation extends Animation implements FrameListener {

    private LinearInterpolation flipValue;
    private FlipEffect effect;

    /**
     * Constructor.
     * @param effect the effect to animate
     */
    public FlipAnimation(FlipEffect effect) {
        this.effect = effect;
        addFrameListener(this);

        // add targets
        if (effect.getComponent().isFrontVisible()) {
            flipValue = new LinearInterpolation(0, Math.PI);
        } else {
            flipValue = new LinearInterpolation(Math.PI, 0);
        }

        getInterpolators().add(flipValue);
    }

    @Override
    public void frameStarted(double time) {
        if (getTime() == 0.0) {
            effect.setActive(true);
            effect.reset();
        }

        effect.setTransparency((float) flipValue.getValue());
        effect.setFlipValue((float) flipValue.getValue());
        effect.getComponent().repaint();

        if (getTime() == 1.0) {
            effect.setActive(false);
            // prevents memory leaks
            if (getRepeats() >= getNumberOfRepeats()) {
                effect = null;
            }
        }
    }
}
