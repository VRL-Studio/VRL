/* 
 * EffectManager.java
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

import eu.mihosoft.vrl.animation.Animation;
import eu.mihosoft.vrl.animation.FrameListener;
import eu.mihosoft.vrl.visual.BufferedPainter;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.Serializable;
import java.util.ArrayList;
import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.CanvasChild;

/**
 * <p>
 * An effect manager can be used to dynamically apply effects to the
 * <code>paint()</code> method of effect painters. Effect painters usually are
 * Swing components. Examples for effects are blurring, fade in and fade out
 * effects.
 * </p>
 * <p>
 * The purpose of this class is to provide an easy to use way to post process
 * the visual appearance of Swing components by combining effects. In contrast
 * to an animation manager effect managers are local objects. That is, there is
 * not one control instance managing different components but one effect manager
 * per effect painter (component).
 * </p>
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class EffectManager implements CanvasChild, Serializable, BufferedPainter {

    private static final long serialVersionUID = -7261354050103918349L;
    transient private BufferedImage buffer;
//    private boolean imageUpdated = false;
    private Canvas mainCanvas;
    private ArrayList<Effect> effects = new ArrayList<Effect>();
    private boolean disabled = false;

    /**
     * Constructor.
     * @param mainCanvas the main canvas 
     */
    public EffectManager(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;
    }

    /**
     * Returns the appearance effect of this effect manager.
     * @return the appearance effect of this effect manager if such an effect
     *             exists; <code>null</code> otherwise
     */
    public Effect getAppearanceEffect() {
        Effect result = null;
        for (Effect e : effects) {
            if (e instanceof AppearanceEffect) {
                result = e;
            }
        }
        return result;
    }

    /**
     * Returns the disappearance effect of this effect manager.
     * @return the disappearance effect of this effect manager if such an effect
     *             exists; <code>null</code> otherwise
     */
    public Effect getDisappearanceEffect() {
        Effect result = null;
        for (Effect e : effects) {
            if (e instanceof DisappearanceEffect) {
                result = e;
            }
        }
        return result;
    }

    /**
     * Returns a list containing all effects of this effect manager.
     * @return a list containing all effects of this effect manager
     */
    public ArrayList<Effect> getEffects() {
        return effects;
    }

    /**
     * Returns an effec by name.
     * @param name the name of the effect that is to be returned
     * @return the requested effect or <code>null</code> if no such effect
     *         exists
     */
    public Effect getEffectByName(String name) {
        Effect result = null;

        for (Effect e : effects) {
            if (e.getName().equals(name)) {
                result = e;
                break;
            }
        }

        return result;
    }

    /**
     * Paints the effect.
     * @param e the effect painter
     * @param g the graphics context
     */
    public void paint(EffectPainter e, Graphics g) {

        Component c = (Component) e;

        if (buffer == null || buffer.getWidth() != c.getWidth()
                || buffer.getHeight() != c.getHeight()) {

            GraphicsConfiguration gc =
                    GraphicsEnvironment.getLocalGraphicsEnvironment().
                    getDefaultScreenDevice().getDefaultConfiguration();

            buffer = gc.createCompatibleImage(c.getWidth(), c.getHeight(),
                    Transparency.TRANSLUCENT);

//            buffer = ImageEffects.createVolatileImage(c.getWidth(), c.getHeight(),
//                    Transparency.TRANSLUCENT);
        }

        boolean effectsAreActive = false;

        for (Effect effect : effects) {
            if (effect.isActive()) {
                effectsAreActive = true;
                break;
            }
        }

        if (!effectsAreActive) {
            e.paintWithoutEffects(g);
        } else {

//            if (!imageUpdated) 
            {

                Graphics2D g2 = buffer.createGraphics();

                Composite originalComposite = g2.getComposite();
                g2.setComposite(AlphaComposite.Clear);
                g2.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
                g2.setComposite(originalComposite);

                e.paintWithoutEffects(g2);

                g2.dispose();

//                imageUpdated = true;
            }

            for (Effect effect : effects) {
                if (effect.isActive()) {
                    effect.paint(buffer);
                }
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(buffer, 0, 0, null);
        }
    }

//    private VolatileImage createVolatileImage(
//            int width, int height, int transparency) {
//        GraphicsEnvironment ge =
//                GraphicsEnvironment.getLocalGraphicsEnvironment();
//        GraphicsConfiguration gc =
//                ge.getDefaultScreenDevice().getDefaultConfiguration();
//        VolatileImage image = null;
//
//        image = gc.createCompatibleVolatileImage(width, height, transparency);
//
//        int valid = image.validate(gc);
//
//        if (valid == VolatileImage.IMAGE_INCOMPATIBLE) {
//            image = this.createVolatileImage(width, height, transparency);
//            return image;
//        }
//
//        return image;
//    }
//    public void clearBuffer(BufferedImage img) {
//
//        Graphics2D g2 = img.createGraphics();
//
//        g2.setComposite(AlphaComposite.Clear);
//        g2.fillRect(0, 0, img.getWidth(), img.getHeight());
//        
//        g2.dispose();
//    }
    @Override
    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    @Override
    public void setMainCanvas(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;
    }

    /**
     * Starts the appearance effect of this effect manager if such an effect is
     * defined
     * @param e the effect painter
     * @param duration the effect duration
     */
    public void startAppearanceEffect(EffectPainter e, double duration, FrameListener f) {
        if (!mainCanvas.isDisableEffects() && !isDisabled()) {
            Effect appearanceEffect =
                    e.getEffectManager().getAppearanceEffect();
            if (appearanceEffect != null) {
                Animation a = appearanceEffect.getAnimation();
                if (a != null) {
                    a.setDuration(duration);
                    if (f != null) {
                        a.addFrameListener(f);
                    }
                    mainCanvas.getAnimationManager().addAnimation(a);
                }
            }
        } else {
            e.getEffectManager().getAppearanceEffect().setActive(false);
        }
    }

    /**
     * Starts the appearance effect of this effect manager if such an effect is
     * defined
     * @param e the effect painter
     * @param duration the effect duration
     */
    public void startAppearanceEffect(EffectPainter e, double duration) {
        startAppearanceEffect(e, duration, null);
    }

    /**
     * Starts the specified effect.
     * @param e the effect painter
     * @param id the effect id
     * @param duration the effect duration
     */
    public void startEffect(EffectPainter e, int id, double duration, FrameListener f) {
        if (!mainCanvas.isDisableEffects() && !isDisabled()) {
            Effect effect = e.getEffectManager().getEffects().get(id);
            if (effect != null) {
                Animation a = effect.getAnimation();
                if (a != null) {
                    a.setDuration(duration);
                    if (f != null) {
                        a.addFrameListener(f);
                    }
                    mainCanvas.getAnimationManager().addAnimation(a);
                }
            }
        } else {
            e.getEffectManager().getEffects().get(id).setActive(false);
        }
    }

    /**
     * Starts the specified effect.
     * @param e the effect painter
     * @param id the effect id
     * @param duration the effect duration
     */
    public void startEffect(EffectPainter e, int id, double duration) {
        startEffect(e, id, duration, null);
    }

    /**
     * Starts the specified effect.
     * @param name the effect name
     * @param duration the effect duration
     */
    public void startEffect(String name, double duration, FrameListener f) {
        Effect effect = getEffectByName(name);
        if (!mainCanvas.isDisableEffects() && !isDisabled()) {
            if (effect != null) {
                Animation a = effect.getAnimation();
                if (a != null) {
                    a.setDuration(duration);
                    if (f != null) {
                        a.addFrameListener(f);
                    }
                    mainCanvas.getAnimationManager().addAnimation(a);
                }
            }
        } else {
            effect.setActive(false);
        }
    }

    /**
     * Starts the specified effect.
     * @param name the effect name
     * @param duration the effect duration
     */
    public void startEffect(String name, double duration) {
        startEffect(name, duration, null);
    }

    /**
     * Starts the given effect.
     * @param effect the effect to start
     * @param duration the effect duration
     */
    public void startEffect(Effect effect, double duration, FrameListener f) {
        if (!mainCanvas.isDisableEffects() && !isDisabled()) {
            if (effect != null) {
                Animation a = effect.getAnimation();
                if (a != null) {
                    a.setDuration(duration);
                    if (f != null) {
                        a.addFrameListener(f);
                    }
                    mainCanvas.getAnimationManager().addAnimation(a);
                }
            }
        } else {
            effect.setActive(false);
        }
    }

    /**
     * Starts the given effect.
     * @param effect the effect to start
     * @param duration the effect duration
     */
    public void startEffect(Effect effect, double duration) {
        startEffect(effect, duration, null);
    }

//    public void startEffect(EffectPainter e, String name, double duration) {
//        if (!mainCanvas.isDisableEffects() && !isDisabled()) {
//            Effect effect = e.getEffectManager().getEffectByName(name);
//            if (effect != null) {
//                Animation a = effect.getAnimation();
//                if (a != null) {
//                    a.setDuration(duration);
//                    mainCanvas.getAnimationManager().addAnimation(a);
//                }
//            }
//        } else {
//            e.getEffectManager().getEffectByName(name).setActive(false);
//        }
//    }
    //
    /**
     * Starts the disappearance effect of this effect manager if such an effect
     * is defined
     * @param e the effect painter
     * @param duration the effect duration
     */
    public void startDisappearanceEffect(
            EffectPainter e, double duration, FrameListener f) {
        if (!mainCanvas.isDisableEffects() && !isDisabled()) {
            Effect disappearanceEffect =
                    e.getEffectManager().getDisappearanceEffect();
            if (disappearanceEffect != null) {
                Animation a = disappearanceEffect.getAnimation();
                if (a != null) {
                    a.setDuration(duration);
                    if (f != null) {
                        a.addFrameListener(f);
                    }
                    mainCanvas.getAnimationManager().addAnimation(a);
                }
            }
        } else {
            e.getEffectManager().getDisappearanceEffect().setActive(false);
        }
    }

    /**
     * Starts the disappearance effect of this effect manager if such an effect
     * is defined
     * @param e the effect painter
     * @param duration the effect duration
     */
    public void startDisappearanceEffect(EffectPainter e, double duration) {
        startDisappearanceEffect(e, duration, null);
    }

    /**
     * Indicates whether the effect manager is disabled.
     * @return <code>true</code> if the effect is disabled;
     *         <code>false</code> otherwise
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Defines whether this effect manager shall be disabled.
     * @param disabled the state to set
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        if (disabled) {
            contentChanged();
        }
    }

    @Override
    public void contentChanged() {
        buffer = null;

        for (Effect e : effects) {
            e.contentChanged();
        }
    }
}
