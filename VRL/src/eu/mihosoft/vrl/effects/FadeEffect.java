/* 
 * FadeEffect.java
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
import eu.mihosoft.vrl.visual.Connection;
import eu.mihosoft.vrl.visual.CanvasWindow;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Effect for fading components in and out.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class FadeEffect implements Effect {

    private static final long serialVersionUID = -4742239574684835631L;
    private float transparency = 0.f;
    transient private BufferedImage buffer;
    private Component component;
    private EffectPainter effectPainter;
    private boolean active = true;
    private boolean fadeIn = false;
    private String name = "FadeEffect";
    private FadeAnimation animation;

    /**
     * Constructor.
     * @param e the effect painter
     */
    public FadeEffect(EffectPainter e) {
        effectPainter = e;
        component = (Component) e;
        animation = new FadeAnimation(this);
    }

    @Override
    public void paint(BufferedImage img) {

        if (buffer == null || buffer.getWidth() != getComponent().getWidth() ||
                buffer.getHeight() != getComponent().getHeight()) {
//                buffer =
//                        new BufferedImage(getComponent().getWidth(),
//                        getComponent().getHeight(), BufferedImage.TYPE_INT_ARGB);


            GraphicsConfiguration gc =
                    GraphicsEnvironment.getLocalGraphicsEnvironment().
                    getDefaultScreenDevice().getDefaultConfiguration();

            buffer = gc.createCompatibleImage(getComponent().getWidth(),
                    getComponent().getHeight(),
                    Transparency.TRANSLUCENT);

//                 buffer = ImageEffects.createVolatileImage(getComponent().getWidth(),
//                         getComponent().getHeight(), Transparency.TRANSLUCENT);
        }

        Graphics2D g2 = buffer.createGraphics();

        g2.setComposite(AlphaComposite.Clear);
        g2.setPaint(new Color(0.f, 0.f, 0.f, 0.f));
        g2.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

        AlphaComposite ac1 =
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                getTransparency());
        g2.setComposite(ac1);

        g2.drawImage(img, 0, 0, null);
        g2.dispose();

        g2 = img.createGraphics();

        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.Clear);
        g2.setPaint(new Color(0.f, 0.f, 0.f, 0.f));
        g2.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
        g2.setComposite(originalComposite);

        g2.drawImage(buffer, 0, 0, null);

        g2.dispose();
    }

    @Override
    public Animation getAnimation() {
        animation.reset();
        animation.clearRepeats();
        return animation;
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
        animation.updateTargets();
    }

    /**
     * Defines the transparency of this effect without updating the effect
     * animation.
     * @param transparency the transparency to set
     */
    public void setTransparencyWithoutUpdate(float transparency) {
        this.transparency = transparency;
    }

    /**
     * Returns the component of this effect.
     * @return the component of this effect
     */
    public Component getComponent() {
        return component;
    }

    /**
     * Defines the component of this effect.
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

        // if effect is to be disabled we set transparency to its final value
        if (!isActive()) {
            if (isFadeIn()) {
                setTransparency(1.f);
            } else {
                setTransparency(0.f);
            }
        }

        if (!active) {
            contentChanged();
        }
    }

    @Override
    public EffectPainter getEffectPainter() {
        return effectPainter;
    }

    /**
     * Indicates whether this fade effect is in "fade-in" or "fade-out" mode.
     * @return <code>true</code> if this effect is in "fade-in" mode;
     *         <code>false</code> otherwise
     */
    public boolean isFadeIn() {
        return fadeIn;
    }

    /**
     * Defines whether this effect shall be in "fade-in" mode.
     * @param fadeIn the state to set
     */
    public void setFadeIn(boolean fadeIn) {
        this.fadeIn = fadeIn;
//        if (fadeIn) {
//            setTransparency(0.f);
//        } else {
//            setTransparency(1.f);
//        }
        animation.updateTargets();
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
 * Animation used to animate fade effects.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class FadeAnimation extends Animation implements FrameListener {

    private LinearInterpolation alphaTarget;
    private FadeEffect effect;
    private float initialTransparency;

    /**
     * Constructor.
     * @param effect the effect to animate
     */
    public FadeAnimation(FadeEffect effect) {
        this.effect = effect;
        addFrameListener(this);

        initialTransparency = effect.getTransparency();

        if (effect.isFadeIn()) {
            // add targets
            alphaTarget = new LinearInterpolation(initialTransparency, 1.f);
        } else {
            // add targets
            alphaTarget = new LinearInterpolation(initialTransparency, 0.f);
        }


        getInterpolators().add(alphaTarget);
    }

    /**
     * updates the start and stop values of the alpha target
     * (depending on the current fade mode)
     */
    void updateTargets() {
        initialTransparency = effect.getTransparency();
        if (effect.isFadeIn()) {
            alphaTarget.setStartValue(initialTransparency);
            alphaTarget.setStopValue(1.f);
        } else {
            alphaTarget.setStartValue(initialTransparency);
            alphaTarget.setStopValue(0.f);
        }
    }


    @Override
    public void frameStarted(double time) {
        if (getTime() == 0.0) {
            effect.setActive(true);
//            effect.getComponent().setVisible(true);
        }

        Component c = effect.getComponent();
//        // TODO put that inside of another frame listener
//        if (c instanceof DefaultMethodRepresentation) {
//            DefaultMethodRepresentation obj = (DefaultMethodRepresentation) c;
//
//            ArrayList<Connection> connections = obj.getConnections();
//
//            for (Connection connection : connections) {
//                if (connection.getSender().
//                        getValueObject().getMethod().isVisible() &&
//                        connection.getReceiver().getValueObject().getMethod().isVisible()) {
//                    connection.setTransparency((float) alphaTarget.getValue());
//                }
//            }
//        }

        // TODO put that inside of another frame listener
        if (c instanceof CanvasWindow && ((CanvasWindow)c).getMainCanvas()!=null) {
            CanvasWindow obj = (CanvasWindow) c;
            List<Connection> connections =
                    obj.getMainCanvas().getDataConnections().getAllWith(obj);
            
            connections.addAll(
                    obj.getMainCanvas().getControlFlowConnections().getAllWith(obj));

            for (Connection connection : connections) {
                try {
                    CanvasWindow sender =
                            connection.getSender().
                            getValueObject().getParentWindow();
                    CanvasWindow receiver =
                            connection.getReceiver().getValueObject().
                            getParentWindow();

                    boolean cIsSender = c.equals(sender);
                    boolean cIsReceiver = !cIsSender;

                    float transparency = (float) alphaTarget.getValue();

                    if (cIsSender) {
                        FadeEffect e =
                                (FadeEffect) receiver.getEffectManager().
                                getEffectByName("FadeEffect");

                        if (e.isActive() && !e.isFadeIn()) {
                            transparency = e.getTransparency();
                        } else {
                            transparency =
                                    Math.min(e.getTransparency(), transparency);
                        }
                    } else if (cIsReceiver) {
                        FadeEffect e =
                                (FadeEffect) sender.getEffectManager().
                                getEffectByName("FadeEffect");

                        if (e.isActive() && !e.isFadeIn()) {
                            transparency = e.getTransparency();
                        } else {
                            transparency =
                                    Math.min(e.getTransparency(), transparency);
                        }
                    }

                    connection.setTransparency(transparency);

                    if (getTime() == 0.0 && effect.isFadeIn()) {
                        connection.setVisible(true);
                    }
                    else if(getTime() == 1.0 && !effect.isFadeIn()) {
                        connection.setVisible(false);
                    }

                } catch (Exception ex) {
                    // sometimes null pointer exceptions occur because of
                    // unreachable senders etc.
                    // crashes with ProxyObjects

                }
            }
        } // if canvas window

        effect.setTransparencyWithoutUpdate((float) alphaTarget.getValue());
        effect.getComponent().repaint();

        if (getTime() == 1.0) {
            effect.setActive(false);
//            effect.setTransparency(initialTransparency);

//            // prevents memory leaks
//            if (getRepeats() >= getNumberOfRepeats()) {
//                effect = null;
//            }
        }
    }
}
