/* 
 * FadeOutEffect.java
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
import eu.mihosoft.vrl.animation.LinearInterpolation;
import eu.mihosoft.vrl.reflection.DefaultMethodRepresentation;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import eu.mihosoft.vrl.visual.CanvasWindow;
import eu.mihosoft.vrl.visual.Connection;
import java.util.List;

/**
 * Effect for fading out components.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class FadeOutEffect implements DisappearanceEffect {

    private static final long serialVersionUID = 2833250455516417389L;
    private float transparency = 1.f;
    transient private BufferedImage buffer;
    private Component component;
    private EffectPainter effectPainter;
    private boolean active = false;
    private Animation fadeoutAnimation;
    private String name = "FadeOutEffect";

    /**
     * Constructor.
     * @param e the effect to animate
     */
    public FadeOutEffect(EffectPainter e) {
        effectPainter = e;
        component = (Component) e;
        fadeoutAnimation = new FadeOutAnimation(this);
    }

    @Override
    public void paint(BufferedImage img) {
        if (buffer == null || buffer.getWidth() != getComponent().getWidth()
                || buffer.getHeight() != getComponent().getHeight()) {
//            buffer =
//                    new BufferedImage(getComponent().getWidth(),
//                    getComponent().getHeight(), BufferedImage.TYPE_INT_ARGB);


            GraphicsConfiguration gc =
                    GraphicsEnvironment.getLocalGraphicsEnvironment().
                    getDefaultScreenDevice().getDefaultConfiguration();

            buffer = gc.createCompatibleImage(getComponent().getWidth(),
                    getComponent().getHeight(),
                    Transparency.TRANSLUCENT);

//            buffer = ImageEffects.createVolatileImage(getComponent().getWidth(),
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

//        getEffectPainter().paintWithoutEffects(g2);

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
//        return new FadeOutAnimation(this);
        fadeoutAnimation.reset();
        return fadeoutAnimation;
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
     * Returns this components effect painter which is a component.
     * @return this components effect painter
     */
    public Component getComponent() {
        return component;
    }
//    public void setComponent(Component component) {
//        this.component = component;
//    }

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
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
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
 * 
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class FadeOutAnimation extends Animation implements FrameListener {

    private LinearInterpolation alphaTarget;
    private FadeOutEffect effect;
    private float initialTransparency;

    public FadeOutAnimation(FadeOutEffect effect) {
        this.effect = effect;
        addFrameListener(this);


        initialTransparency = effect.getTransparency();

        // add targets     
        alphaTarget = new LinearInterpolation(initialTransparency, 0);


        getInterpolators().add(alphaTarget);
    }

    @Override
    public void frameStarted(double time) {
        if (getTime() == 0.0) {
            effect.setActive(true);
        }
        effect.setTransparency((float) alphaTarget.getValue());

        Component c = effect.getComponent();

        // TODO put that inside of another frame listener
        if (c instanceof CanvasWindow) {
            CanvasWindow obj = (CanvasWindow) c;
            List<Connection> connections =
                    obj.getMainCanvas().getDataConnections().getAllWith(obj);

            connections.addAll(
                    obj.getMainCanvas().
                    getControlFlowConnections().getAllWith(obj));

            for (Connection connection : connections) {
                connection.setTransparency((float) alphaTarget.getValue());
            }
        }

        // TODO put that inside of another frame listener
        if (c instanceof DefaultMethodRepresentation) {
            DefaultMethodRepresentation obj = (DefaultMethodRepresentation) c;
            ArrayList<Connection> connections = obj.getConnections();

//            ArrayList<Connection> controlFlowConnections =
//                    obj.getParentObject().getMainCanvas().
//                    getControlFlowConnections().
//                    getAllWith(obj.getControlflowInput());
//
//            controlFlowConnections.addAll(obj.getParentObject().getMainCanvas().
//                    getControlFlowConnections().
//                    getAllWith(obj.getControlflowOutput()));

//            connections.addAll(controlFlowConnections);

            for (Connection connection : connections) {
//                System.out.println("Connection: " + connection);

                boolean senderAndReceiverVisible =
                        connection.getSender().getValueObject().
                        getParent().isVisible()
                        && connection.getReceiver().getValueObject().
                        getParent().isVisible();

                boolean parentWindowVisible =
                        connection.getSender().
                        getValueObject().getParentWindow().isVisible();

                boolean connectionVisible =
                        senderAndReceiverVisible && parentWindowVisible;


                if (connectionVisible) {
                    float transparency = Math.max(
                            (float) alphaTarget.getValue(), 0.38f);
                    connection.setTransparency(transparency);
                }
            }
        }

        c.repaint();

        if (getTime() == 1.0) {
            effect.setActive(false);
            effect.setTransparency(initialTransparency);
            effect.getComponent().setVisible(false);

            if (effect.getComponent() instanceof CanvasWindow) {
                CanvasWindow obj = (CanvasWindow) effect.getComponent();
                obj.getMainCanvas().getWindows().
                        removeObjectWithoutEffect(obj.getID());

                List<Connection> connections =
                        obj.getMainCanvas().getDataConnections().getAllWith(obj);
                for (Connection connection : connections) {
                    connection.setVisible(false);
                }
            }

//            if (c instanceof DefaultMethodRepresentation) {
//                DefaultMethodRepresentation obj = (DefaultMethodRepresentation) c;
//                ArrayList<Connection> connections = obj.getConnections();
//
//                for (Connection connection : connections) {
//                    connection.setVisible(false);
//                }
//            }

//            // prevents memory leaks
//            if (getRepeats() >= getNumberOfRepeats()) {
//                effect = null;
//            }
        }
    }
}
