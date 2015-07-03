/* 
 * EffectPane.java
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
package eu.mihosoft.vrl.visual;

import eu.mihosoft.vrl.animation.Animation;
import eu.mihosoft.vrl.animation.AnimationGroup;
import eu.mihosoft.vrl.animation.AnimationTask;
import eu.mihosoft.vrl.animation.BackgroundAnimation;
import eu.mihosoft.vrl.animation.ForegroundAnimation;
import eu.mihosoft.vrl.animation.FrameListener;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * <p>
 * An effect pane is used to draw effects on top of all canvas children. Drawing
 * on the effect pane ensures that everythis is drawn after canvas drawing
 * requests. </p>
 * <p>
 * Elements and effects that are includded by default are: <ul> <li>dock</li>
 * <li>message box</li> <li>pulse effects</li>
 * <li>colorize effect</li> <li>spinning wheel</li> </ul> </p>
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class EffectPane extends JComponent
        implements CanvasChild, ComponentListener {

    private static final long serialVersionUID = 989793208936845716L;
    private Canvas mainCanvas;
    private MessageBox messageBox;
    private Dock dock;
    private Wheel wheel;
    private boolean pulseEffect = true;
    private Map<MessageType, Boolean> pulseEffects
            = new EnumMap<MessageType, Boolean>(MessageType.class);
    private JComponent fullScreenComponent;
    private Container fullScreenParent;
    private FullScreenCloseIcon fullScreenCloseIcon;
    private MouseAdapter consumeEventsAdapter = new MouseAdapter() {
    };
    private boolean paintSpot;
    private LocationIndicator locationIndicator;

//    private Color foregroundColor = VSwingUtil.TRANSPARENT_COLOR;
    /**
     * Constructor.
     *
     * @param mainCanvas the main canvas object
     */
    public EffectPane(Canvas mainCanvas) {

        wheel = new Wheel(12, mainCanvas.getAnimationManager());
        wheel.setSize(200, 200);

        fullScreenCloseIcon = new FullScreenCloseIcon(mainCanvas);
        fullScreenCloseIcon.setActionListener(new CanvasActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeCurrentFullScreenComponent();
            }
        });

        fullScreenCloseIcon.setPreferredSize(new Dimension(50, 50));
        fullScreenCloseIcon.setIconInset(20);
        fullScreenCloseIcon.setVisible(false);
        add(fullScreenCloseIcon);

        setMainCanvas(mainCanvas);
        setOpaque(false);
        setLayout(null);
        setBackground(VSwingUtil.TRANSPARENT_COLOR);

        add(wheel);

        this.addComponentListener(this);

        locationIndicator = new LocationIndicator(mainCanvas);
    }

    /**
     * Checks whether pulse effect for the specified message type is enabled. To
     * check whether pulse efect is enabled in general, invoke the method
     * {@link #isPulseEffect() }
     *
     * @param type message type
     * @return <code>true</code> if enabled; <code>false</code> otherwise
     */
    public boolean isPulseEffectEnabledFor(MessageType type) {
        if (pulseEffects.containsKey(type)) {
            Boolean value = pulseEffects.get(type);
            if (value == null) {
                return true;
            } else {
                return value;
            }
        }

        return true;
    }

    /**
     * Defines whether pulse effect for the specified message type is enabled.
     * To define whether pulse efect is enabled in general, invoke the method
     * {@link #enablePulseEffect() }
     *
     * @param type message type
     * @param state state to set
     */
    public void setPulseEffectFor(MessageType type, boolean state) {
        pulseEffects.put(type, state);
    }

    /**
     * Displays a spinning wheel.
     *
     * @param duration the duration of the animation
     * @param t an optional animation task
     */
    public void startSpin(final double duration, final AnimationTask t) {
        VSwingUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                wheel.startSpin(duration);
                if (!isPaintsSpot()) {
                    colorize(new Color(0, 0, 0, 120), 0, duration, t);
                }
            }
        });
    }

    /**
     * Displays a spot (used for dialogs).
     *
     * @param duration the duration of the animation
     * @param t an optional animation task
     */
    public void startSpot(final double duration, final AnimationTask t) {
        VSwingUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                paintSpot = true;

                colorize(new Color(0, 0, 0, 120), 0, duration, t);
            }
        });
    }

    /**
     * Displays a spot (used for dialogs) with default duration.
     */
    public void startSpot() {
        startSpot(0.1, null);
    }

    /**
     * Stops displays a spot (used for dialogs).
     */
    public void stopSpot() {
        VSwingUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                colorize(VSwingUtil.TRANSPARENT_COLOR, 0, 0.1, new AnimationTask() {
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
                        paintSpot = false;
                    }
                });
            }
        });
    }

    /**
     * Displays a spinning wheel.
     *
     * @param t an optional animation task
     */
    public void startSpin(AnimationTask t) {
        startSpin(0.5, t);
    }

    /**
     * Displays a spinning wheel with default duration.
     */
    public void startSpin() {
        startSpin(0.5, null);
    }

    /**
     * Stops the wheel animation effect.
     */
    public void stopSpin() {
        wheel.stopSpin();
        if (!isPaintsSpot()) {
            colorize(VSwingUtil.TRANSPARENT_COLOR, 0.5);
        }
    }

    /**
     * Displays a pulse animation effect.
     *
     * @param c the canvas child used to define the location of the effect
     * @param type the message type
     * @param duration the duration of the animation
     * @param repeat defines the number of pulse animations
     */
    public void pulse(final CanvasChild c,
            final MessageType type,
            final double duration,
            final int repeat) {
        if (isPulseEffect() && isPulseEffectEnabledFor(type)) {
            VSwingUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    PulseIcon icon = new PulseIcon(mainCanvas, type);
//        icon.setSize(80, 80);
                    add(icon);
                    icon.pulse(c, 1.0, duration, repeat);
                }
            });
        }
    }

    /**
     * Displays a pulse animation effect.
     *
     * @param c the canvas child used to define the location of the effect
     * @param type the message type
     * @param transparency the transparency of the pulse effect
     * @param duration the duration of the animation
     * @param repeat defines the number of pulse animations
     */
    public void pulse(final CanvasChild c, final MessageType type, final double transparency,
            final double duration, final int repeat) {
        if (isPulseEffect() && isPulseEffectEnabledFor(type)) {
            VSwingUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    PulseIcon icon = new PulseIcon(mainCanvas, type);
//        icon.setSize(80, 80);
                    add(icon);
                    icon.pulse(c, transparency, duration, repeat);
                }
            });
        }
    }

    /**
     * Displays a pulse animation effect.
     *
     * @param c the canvas child used to define the location of the effect
     * @param type the message type
     */
    public void pulse(final CanvasChild c, final MessageType type) {
        if (isPulseEffect() && isPulseEffectEnabledFor(type)) {
            VSwingUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    PulseIcon icon = new PulseIcon(mainCanvas, type);
//        icon.setSize(80, 80);
                    add(icon);
                    icon.pulse(c);
                }
            });
        }
    }

    /**
     * Displays a pulse animation effect.
     *
     * @param c the canvas child used to define the location of the effect
     * @param type message type
     * @param transparency the transparency of the pulse effect
     */
    public void pulse(final CanvasChild c, final MessageType type, final double transparency) {
        if (isPulseEffect() && isPulseEffectEnabledFor(type)) {
            VSwingUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    PulseIcon icon = new PulseIcon(mainCanvas, type);
//        icon.setSize(80, 80);
                    add(icon);
                    icon.pulse(c, transparency);
                }
            });
        }
    }

    /**
     * Displays a pulse animation effect.
     *
     * @param p the location of the effect
     * @param type the messagge type
     * @param repeat defines the number of pulse animations
     */
    public void pulse(final Point p, final MessageType type, final int repeat) {
        if (isPulseEffect() && isPulseEffectEnabledFor(type)) {
            VSwingUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    PulseIcon icon = new PulseIcon(mainCanvas, type);
//        icon.setSize(80, 80);
                    add(icon);
                    icon.setLocation(p);
                    icon.pulse(repeat);
                }
            });
        }
    }

    /**
     * Displays a pulse animation effect.
     *
     * @param p the location of the effect
     * @param type the message type
     * @param transparancy the transparency of the pulse effect
     * @param duration the duration of the animation
     * @param repeat defines the number of pulse animations
     */
    public void pulse(final Point p, final MessageType type, final double transparancy,
            final double duration, final int repeat) {
        if (isPulseEffect() && isPulseEffectEnabledFor(type)) {
            VSwingUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    PulseIcon icon = new PulseIcon(mainCanvas, type);
//        icon.setSize(80, 80);
                    add(icon);
                    icon.setLocation(p);
                    icon.pulse(transparancy, duration, repeat);
                }
            });
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        if (!isPaintsSpot()) {
            g2.setPaint(getBackground());
            g2.fillRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
        } else {
            int x = getVisibleRect().x;
            int y = getVisibleRect().y;

            int w = getVisibleRect().width;
            int h = getVisibleRect().height;

            int radius = Math.max(w, h) / 2;

            float[] dist = {0.0f, 1.0f};

            Color[] colors = {
                VSwingUtil.TRANSPARENT_COLOR, getBackground()};

            RadialGradientPaint paint
                    = new RadialGradientPaint(
                            new Point2D.Double(x + w / 2, y + h / 2),
                            radius, dist, colors);

            g2.setPaint(paint);

            g2.fillRect(x, y, w, h);
        }

//        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }

    @Override
    public void paint(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        VScale scale = TransformingParent.getScale(mainCanvas);
        
        if (!scale.isIdentity()) {
            g2.setTransform(AffineTransform.getScaleInstance(scale.getScaleX(), scale.getScaleY()));
        }

        super.paint(g);

        locationIndicator.paint(g, getMainCanvas().getStyle(), getSize());
    }

    /**
     * Displays a colorize effect on the entire effect pane. This effect draws a
     * rectangle on the whole effect pane. Its color fades from the currently
     * defined color (usually black with 100% transparency) to the defined
     * target color.
     *
     * @param c the target color of the effect
     * @param offset the animation offset
     * @param duration the duration of the animation
     * @param t an optional animation task
     */
    public void colorize(Color c, double offset, double duration,
            AnimationTask t) {

        BackgroundAnimation a
                = new BackgroundAnimation(this, getBackground(), c);
        a.setDuration(duration);
        a.setOffset(offset);

        a.addFrameListener(new FrameListener() {
            @Override
            public void frameStarted(double time) {
                getMainCanvas().repaint();
            }
        });

        a.addFrameListener(t);

        getMainCanvas().getAnimationManager().addAnimation(a);
    }

    /**
     * Displays a colorize effect on the entire effect pane. This effect draws a
     * rectangle on the whole effect pane. Its color fades from the currently
     * defined color (usually black with 100% transparency) to the defined
     * target color.
     *
     * @param c the target color of the effect
     * @param duration the duration of the animation
     */
    public void colorize(Color c, double duration) {
        colorize(c, 0, duration, null);
    }

    /**
     * Displays a colorize effect on the entire effect pane. This effect draws a
     * rectangle on the whole effect pane. Its color fades from the currently
     * defined color (usually black with 100% transparency) to the defined
     * target color.
     *
     * @param c the target color of the effect
     * @param t an optional animation task
     */
    public void colorize(Color c, AnimationTask t) {
        colorize(c, 0, 0.5, t);
    }

    /**
     * Displays a colorize effect on the entire effect pane. This effect draws a
     * rectangle on the whole effect pane. Its color fades from the currently
     * defined color (usually black with 100% transparency) to the defined
     * target color.
     *
     * @param c the target color of the effect
     */
    public void colorize(Color c) {
        colorize(c, 0.5);
    }

    /**
     * Displays a flash effect. It can be used to simulate a snap shot etc. This
     * effect uses the colorize effect to temporarily colorize the whole effect
     * pane with the defined color. It uses linear interpolation for the color
     * transitions.
     *
     * @param color the color to use for this effect
     * @param offset the animation offset
     * @param duration the duration of the animation
     */
    public void flash(Color color, double offset, double duration) {
        BackgroundAnimation a1
                = new BackgroundAnimation(this,
                        mainCanvas.getEffectPane().getBackground(), color);
        BackgroundAnimation a2
                = new BackgroundAnimation(this,
                        color, mainCanvas.getEffectPane().getBackground());

        a1.setDuration(1);
        a2.setDuration(1);

        AnimationGroup group
                = new AnimationGroup(mainCanvas.getAnimationManager());
        group.add(a1);
        group.append(a2);
        group.setDuration(1);
        group.setOffset(offset);
        mainCanvas.getAnimationManager().addAnimation(group, 1);
    }

    /**
     * Displays a flash effect. It can be used to simulate a snap shot etc. This
     * effect uses the colorize effect to temporarily colorize the whole effect
     * pane with the defined color. It uses linear interpolation for the color
     * transitions.
     *
     * @param c the color to use for this effect
     */
    public void flash(Color c) {
        flash(c, 0, 1.8);
    }

    @Override
    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    @Override
    public void setMainCanvas(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;
        if (messageBox != null) {
            this.remove(messageBox);
        }
        this.messageBox = mainCanvas.getMessageBox();
        this.add(messageBox);

        // make messageBox invisible 
        // because we get some ugly drawing effects
        messageBox.setVisible(false);

        if (dock != null) {
            this.remove(dock);
        }

        this.dock = mainCanvas.getDock();
        this.add(dock);

        wheel.setAnimationManager(mainCanvas.getAnimationManager());

        componentResized(null);
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        //
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        //
    }

    @Override
    public void componentResized(ComponentEvent e) {

        // TODO don't use static min size values
        int width = Math.max(this.getVisibleRect().width, 440);

        Dimension d = new Dimension(width,
                messageBox.getMaxMessageHeight());
        int x = this.getVisibleRect().x;
        int y = this.getVisibleRect().y
                + this.getVisibleRect().height - messageBox.getMessageHeight();
        messageBox.setBounds(x, y, d.width, d.height);

        d = new Dimension(Math.min(d.width, dock.getPreferredSize().width),
                dock.getMaxDockHeight());
        x = this.getVisibleRect().x + getVisibleRect().width / 2 - d.width / 2;
        y = this.getVisibleRect().y + this.getVisibleRect().height
                - dock.getDockHeight() + dock.getInsets().bottom;
        dock.setBounds(x, y, d.width, d.height);

        int centerX = getVisibleRect().x + getVisibleRect().width / 2;
        int centerY = getVisibleRect().y + getVisibleRect().height / 2;

        int wheelX = centerX - wheel.getSize().width / 2;
        int wheelY = centerY - wheel.getSize().height / 2;

        wheel.setLocation(wheelX, wheelY);

        if (fullScreenComponent != null) {
            fullScreenComponent.setSize(getVisibleRect().width,
                    getVisibleRect().height);
            fullScreenComponent.setLocation(getVisibleRect().x, getVisibleRect().y);
        }

        getFullScreenCloseIcon().setSize(getFullScreenCloseIcon().getPreferredSize());
        getFullScreenCloseIcon().setLocation(getVisibleRect().x, getVisibleRect().y);
    }

    @Override
    public void componentShown(ComponentEvent e) {
        //
    }

    /**
     * Returns the wheel used for "spinning wheel" effects.
     *
     * @return the wheel
     */
    public Wheel getWheel() {
        return wheel;
    }

    /**
     * Indicates whether the pulse effect is enabled.
     *
     * @return <code>true</code> if pulse effect is enabled; <code>false</code>
     * otherwise
     */
    public boolean isPulseEffect() {
        return pulseEffect;
    }

    /**
     * Defines whether to enable the pulse effect.
     *
     * @param pulseEffect the state to set
     */
    public void enablePulseEffect(boolean pulseEffect) {
        this.pulseEffect = pulseEffect;
    }

    /**
     * @return the fullScreenComponent
     */
    public JComponent getFullScreenComponent() {
        return fullScreenComponent;
    }

    /**
     * @param component the fullScreenComponent to set
     */
    public void setFullScreenComponent(JComponent component) {
        removeCurrentFullScreenComponent();

        mainCanvas.setFullScreenMode(true);

        this.fullScreenComponent = component;

        FullScreenComponent fullComp = null;

        // check whether jcomponent implements fullscreen interface
        if (this.fullScreenComponent instanceof FullScreenComponent) {
            fullComp = (FullScreenComponent) this.fullScreenComponent;

            // check whether to use custom component for fullscreen
            if (fullComp.customViewComponent() != null) {
                this.fullScreenComponent = fullComp.customViewComponent();

                fullComp = null;

                // if custom component defined check whether it implements
                // the fullscreen interface
                if (this.fullScreenComponent instanceof FullScreenComponent) {
                    fullComp = (FullScreenComponent) this.fullScreenComponent;
                }
            }
        }

        if (this.fullScreenComponent != null) {
            fullScreenParent = this.fullScreenComponent.getParent();
            add(this.fullScreenComponent);

            // if fullscreen interface implemented notify the component
            if (fullComp != null) {
                fullComp.enterFullScreenMode(
                        new Dimension(getVisibleRect().width,
                                getVisibleRect().height));
            }

            consumeMouseEvents(true);

            setOpaque(false);

            getFullScreenCloseIcon().setVisible(true);
        } else {
            getFullScreenCloseIcon().setVisible(false);
        }

        mainCanvas.revalidate();
        mainCanvas.setPresentationMode(false);
    }

    public void removeCurrentFullScreenComponent() {

        mainCanvas.setFullScreenMode(false);

        if (fullScreenComponent != null) {
            this.remove(fullScreenComponent);
            if (fullScreenParent != null) {
                fullScreenParent.add(fullScreenComponent);
            }
            if (fullScreenComponent instanceof FullScreenComponent) {
                FullScreenComponent comp = (FullScreenComponent) fullScreenComponent;
                comp.leaveFullScreenMode();
            }
        }

        getFullScreenCloseIcon().setVisible(false);

        mainCanvas.revalidate();
        this.fullScreenComponent = null;
        consumeMouseEvents(false);
        setOpaque(false);
        mainCanvas.setPresentationMode(!mainCanvas.getWindowGroups().isEmpty());
    }

    public void consumeMouseEvents(boolean value) {
        if (value) {
            this.addMouseListener(consumeEventsAdapter);
            this.addMouseMotionListener(consumeEventsAdapter);
            this.addMouseWheelListener(consumeEventsAdapter);
        } else {
            removeMouseListener(consumeEventsAdapter);
            removeMouseMotionListener(consumeEventsAdapter);
            removeMouseWheelListener(consumeEventsAdapter);
        }
    }

    @Override
    public Dimension getPreferredSize() {

        return getMainCanvas().getSize();
    }

    /**
     * @return the fullScreenCloseIcon
     */
    public FullScreenCloseIcon getFullScreenCloseIcon() {
        return fullScreenCloseIcon;
    }
//    @Override
//    public boolean contains(int x, int y) {
//        // We need to return always false to prevent the EffectPane being
//        // noticed by methods like getComponentAt() and findComponentAt()
//        return false;
//    }
//
//    @Override
//    public boolean contains(Point p) {
//        // We need to return always false to prevent the EffectPane being
//        // noticed by methods like getComponentAt() and findComponentAt()
//        return false;
//    }

    /**
     * @return the paintSpot
     */
    public boolean isPaintsSpot() {
        return paintSpot;
    }

    public void processLocationInputEvent(InputEvent i) {
        locationIndicator.processInputEvent(i);
    }

    public void enableMouseLocationIndicator(boolean v) {
        locationIndicator.setEnabled(v);
    }

    public void toggleMouseLocationIndicatorEnableState() {
        locationIndicator.setEnabled(!locationIndicator.isEnabled());
    }

    public void toggleMouseLocationIndicatorVisibilityIfEnabled() {
        if (locationIndicator.isEnabled()) {
            locationIndicator.setVisible(!locationIndicator.isVisible());
            VSwingUtil.repaintRequest(mainCanvas);
        }
    }
}

class LocationIndicator implements Painter {

    private Point location = null;
    private Point previousLocation = null;
    private int radius = 200;
    private boolean enabled = false;
    private boolean visible = false;
    private Canvas mainCanvas;
    private int mouseBtn = MouseEvent.NOBUTTON;
    private boolean shiftKeyDown;
    private boolean cursorOutOfRange = true;

    public LocationIndicator(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;
    }

    @Override
    public void paint(Graphics g, Style s, Dimension d) {

        if (!isVisible() || !isEnabled() || location == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Composite original = g2.getComposite();

        AlphaComposite ac1
                = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
        g2.setComposite(ac1);

        Area a1 = new Area(new Rectangle2D.Double(
                0.0, 0.0, d.getWidth(), d.getHeight()));

        Area a2 = new Area(new Ellipse2D.Double(
                location.x - radius / 2,
                location.y - radius / 2,
                radius,
                radius));

        a1.subtract(a2);

        g2.setColor(Color.black);
        g2.fill(a1);
        g2.setComposite(original);
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(5f));
        g2.draw(a2);
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(2f));
        g2.draw(a2);

        float width = 60.f;
        float height = 80.f;

        float mouseX = location.x + radius / 2 + width / 2;
        float mouseY = location.y + radius / 2 - height / 2;

        // keep mouse coordinates inside visible rect 
        mouseX = Math.min(
                mainCanvas.getVisibleRect().x
                + mainCanvas.getVisibleRect().width - width - 30,
                mouseX);
        mouseY = Math.min(
                mainCanvas.getVisibleRect().y
                + mainCanvas.getVisibleRect().height - height - 30,
                mouseY);

        mouseX = Math.max(30, mouseX);
        mouseY = Math.max(30, mouseY);

        Shape mouse = new RoundRectangle2D.Double(
                mouseX, mouseY, width, height, 20, 20);

        ac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g2.setComposite(ac1);

        g2.setColor(Color.black);

        g2.fill(mouse);

        g2.setComposite(original);
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(5f));
        g2.draw(mouse);

        g2.setComposite(original);
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(Color.white);
        g2.draw(mouse);

        Shape btn = new RoundRectangle2D.Double(
                0, 0, width / 4,
                height / 3, 5, 5);

        double btnOffsetX = btn.getBounds().getWidth() + 0.5;

        double btnX = mouseX + width / 2 - btnOffsetX * 3 / 2 + 1f;

        g2.translate(btnX, mouseY + 6);

        for (int i = 0; i < 3; i++) {

            if (i > 0) {
                g2.translate(
                        btnOffsetX,
                        0);
            }

            g2.setColor(Color.white);

            boolean btnPressed = (i == 0 && mouseBtn == MouseEvent.BUTTON1)
                    || (i == 1 && mouseBtn == MouseEvent.BUTTON2)
                    || (i == 2 && mouseBtn == MouseEvent.BUTTON3);

            if (btnPressed) {
                g2.setColor(Color.white);
                g2.fill(btn);

            }

            g2.setColor(Color.black);
            g2.setStroke(new BasicStroke(2.f));
            g2.draw(btn);

            g2.setColor(Color.white);
            g2.setStroke(new BasicStroke(1.f));
            g2.draw(btn);

        }

        // revert translation
        g2.translate(-btnX - btnOffsetX * 2, -mouseY - 6);

        g2.setComposite(original);
    }

    @Override
    public Painter newInstance(VComponent parent) {
        return new LocationIndicator(parent.getMainCanvas());
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Point location) {
        this.location = location;

        int xBefore = location.x;
        int yBefore = location.y;

        // keep location coordinates near visible rect 
        location.x = Math.min(
                mainCanvas.getVisibleRect().x
                + mainCanvas.getVisibleRect().width,
                location.x);
        location.y = Math.min(
                mainCanvas.getVisibleRect().y
                + mainCanvas.getVisibleRect().height,
                location.y);
        location.x = Math.max(0, location.x);
        location.y = Math.max(0, location.y);

        boolean outOfRangeNew = xBefore != location.x || yBefore != location.y;

        boolean enteredOrLeftRange = outOfRangeNew != cursorOutOfRange;

        cursorOutOfRange = outOfRangeNew;

        if (enteredOrLeftRange
                && shiftKeyDown && mouseBtn == MouseEvent.NOBUTTON) {
            VSwingUtil.repaintRequest(mainCanvas);
            return;
        }

        if (previousLocation != null) {
            VSwingUtil.repaintRequest(mainCanvas,
                    previousLocation.x - (int) (radius * 1.5),
                    previousLocation.y - (int) (radius * 1.5),
                    radius * 3,
                    radius * 3);
        }

        VSwingUtil.repaintRequest(mainCanvas,
                location.x - (int) (radius * 1.5),
                location.y - (int) (radius * 1.5),
                radius * 3,
                radius * 3);

        previousLocation = location;
    }

    public void setRadius(int r) {
        this.radius = r;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param enabled the visible to set
     */
    public void setVisible(boolean visible) {

        if (this.visible != visible) {
            this.visible = visible;

            if (location == null) {
                setLocation(new Point(0, 0));
            }

            VSwingUtil.repaintRequest(mainCanvas);
        }
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private void processMouseEvent(MouseEvent m) {

        if (m.getID() == MouseEvent.MOUSE_PRESSED) {
            setVisible(true);
            mouseBtn = m.getButton();
        } else if (m.getID() == MouseEvent.MOUSE_RELEASED) {
            setVisible(false || shiftKeyDown);
            mouseBtn = MouseEvent.NOBUTTON;
        }

        if (m.getSource() instanceof Component) {
            if (isEnabled()) {
                setLocation(
                        SwingUtilities.convertPoint((Component) m.getSource(),
                                m.getPoint(), mainCanvas));
            } else {
                // set location but don't update graphics
                location = SwingUtilities.convertPoint((Component) m.getSource(),
                        m.getPoint(), mainCanvas);
            }
        }
    }

    public void processInputEvent(InputEvent i) {
        if (i instanceof MouseEvent) {
            processMouseEvent((MouseEvent) i);
        } else if (i instanceof KeyEvent) {
            processKeyEvent((KeyEvent) i);
        }
    }

    private void processKeyEvent(KeyEvent keyEvent) {
        if (isEnabled() && keyEvent.getKeyCode() == KeyEvent.VK_SHIFT) {
            shiftKeyDown = keyEvent.getID() == KeyEvent.KEY_PRESSED;
            setVisible(shiftKeyDown);
//            if (!shiftKeyDown) {
//                location = null;
//            }
        }
    }
}
