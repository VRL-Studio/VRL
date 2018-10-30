/* 
 * DockApplet.java
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
package eu.mihosoft.vrl.visual;

import eu.mihosoft.vrl.animation.AnimationTask;
import eu.mihosoft.vrl.effects.EffectPanel;
import eu.mihosoft.vrl.effects.FadeEffect;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * <p> This class defines a Mac OS X like dock applet. An applet defines an icon
 * and can receive notifications. The number of notifications is displayed in
 * the upper right corner of the icon. </p> <p> Additionally an applet behaves
 * like a button and fires an action event if it receives a left mouse click.
 * </p>
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class DockApplet extends EffectPanel implements CanvasChild,
        MouseListener, MouseMotionListener {

    private static final long serialVersionUID = 4174428444718983394L;
    private transient BufferedImage buffer;
    private transient BufferedImage notifierBuffer;
    private CanvasActionListener actionListener;
    private Dock dock;
    private ImageIcon icon;
    private int numberOfNotifications = 0;
    private float transparency = 1.0f;
    private boolean isActive = false;
    private FadeEffect fadeEffect;
    private Dimension appletSize;
//    private boolean iconVisible = true;
    public static final String CLICKED_ACTION = "dock-applet:clicked";
    private int progress;

    /**
     * Constructor.
     *
     * @param mainCanvas the main canvas object
     */
    public DockApplet(Canvas mainCanvas) {
        super(mainCanvas);

        setIcon(generateIcon(new Dimension(60, 60)));

        init();
    }

    protected ImageIcon generateIcon(Dimension size) {
        return new ImageIcon(new PulseIcon(getMainCanvas(),
                MessageType.INFO).getStillImage(size.width, size.height));
    }

    /**
     * Constructor.
     *
     * @param mainCanvas the main canvas
     * @param icon the icon to use
     */
    public DockApplet(Canvas mainCanvas, ImageIcon icon) {
        super(mainCanvas);
        setIcon(icon);

        init();
    }

//    public boolean isIconVisible() {
//        return iconVisible;
//    }
//
//    public void setIconVisible(boolean value) {
//        this.iconVisible = value;
//    }
    private void init() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        fadeEffect = new FadeEffect(this);

        fadeEffect.getAnimation().addFrameListener(new AnimationTask() {
            @Override
            public void firstFrameStarted() {
                if (fadeEffect.isFadeIn()) {
//                    setIconVisible(true);
                    setVisible(true);
                }
            }

            @Override
            public void lastFrameStarted() {
                if (!fadeEffect.isFadeIn()) {
//                    setIconVisible(false);
                    setVisible(false);
                }
            }

            @Override
            public void frameStarted(double time) {
                //
            }
        });

        // a bit strange?
        // why define effects in seperate array list
        getEffects().add(fadeEffect);
        getEffectManager().getEffects().addAll(getEffects());

        setOpaque(false);

        setBorder(new EmptyBorder(5, 5, 10, 5));
    }

    /**
     * Adds a new notification to the applet, i.e., increases the notification
     * counter by 1.
     */
    public void newNotification() {
        numberOfNotifications++;
        contentChanged();
    }

    /**
     * Defines the number of notifications.
     *
     * @param notifications the number of notifications to set
     */
    public void setNumberOfNotification(int notifications) {
        numberOfNotifications = notifications;
        contentChanged();
    }

    /**
     * Returns the number of notifications.
     *
     * @return the number of notifications
     */
    public int getNumberOfNotifications() {
        return numberOfNotifications;
    }

    @Override
    public void paintComponent(Graphics g) {


//        if(!isIconVisible()) {
//            return;
//        }

        if (buffer == null
                || buffer.getWidth() != getWidth()
                || buffer.getHeight() != getHeight()) {

            buffer = new BufferedImage(getWidth(), getHeight(),
                    BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2 = buffer.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Style style = getMainCanvas().getStyle();

            getIcon().paintIcon(null, g2, 0 + getInsets().left,
                    0 + getInsets().top);

//            g2.drawRect(0, 0, getWidth()-1, getHeight()-1);


            if (isActive) {
                float value = 1.f / 6f;

                float[] BLUR = {
                    value, value, value,
                    value, value, value,
                    value, value, value
                };

                ConvolveOp vBlurOp = new ConvolveOp(new Kernel(3, 3, BLUR));

                g2.dispose();

                buffer = vBlurOp.filter(buffer, null);

                g2 = buffer.createGraphics();
            }

            paintProgressIndicator(g2);

            if (getNumberOfNotifications() > 0) {

                BufferedImage notifier = paintNotifier();

                g2.drawImage(notifier, null, getWidth() - notifier.getWidth(),
                        0 + getInsets().top);
            }

            g2.dispose();

        }

        Graphics2D g2 = (Graphics2D) g;

        AlphaComposite ac1 =
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                getTransparency());
        g2.setComposite(ac1);

        g2.drawImage(buffer, 0, 0, getWidth(), getHeight(), null);
    }

    private void paintProgressIndicator(Graphics2D g2) {

        if (progress < 1) {
            return;
        }

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        float strokeWidth = 1.5f;

        double width = getAppletSize().width - strokeWidth - 1;
        double height = getAppletSize().height / 6.0;

        double x = strokeWidth / 2 + 0.5;
        double y = getAppletSize().height / 2.0 - height;

        RoundRectangle2D bkgRect = new RoundRectangle2D.Double(
                x, y, width, height, 8, 8);

        g2.setColor(Color.BLACK);
        g2.fill(bkgRect);

        RoundRectangle2D rect = new RoundRectangle2D.Double(
                x + strokeWidth, y, (width - (strokeWidth) * 2f) / 100 * progress, height, 5, 5);

        g2.setColor(new Color(140, 130, 150));
        g2.fill(rect);

        g2.setColor(Color.BLACK);
        g2.draw(rect);

        float blackStrokeWidth = strokeWidth * 2f;

        g2.setStroke(new BasicStroke(blackStrokeWidth));

        g2.setColor(Color.BLACK);
        g2.draw(bkgRect);

        x = blackStrokeWidth / 2;
        width = getAppletSize().width - blackStrokeWidth - 0.5;

        RoundRectangle2D bkgRectWhite = new RoundRectangle2D.Double(
                x, y, width, height, 5, 5);

        g2.setStroke(new BasicStroke(strokeWidth));

        g2.setColor(Color.WHITE);
        g2.draw(bkgRectWhite);
    }

    @Override
    public void contentChanged() {
        buffer = null;
        super.contentChanged();
    }

    public void resizeApplet(Dimension d) {

        int v = d.height;//Math.max(d.height-getInsets().bottom,1);

        Dimension size = new Dimension(v, v);

        appletSize = new Dimension(
                size.width + getInsets().left + getInsets().right,
                size.height + getInsets().top + getInsets().bottom);

        setMinimumSize(size);
        setMaximumSize(size);
        setPreferredSize(size);
        setSize(size);

        setIcon(generateIcon(new Dimension(size.width, size.height)));
    }

    /**
     * Returnns the applet icon.
     *
     * @return the applet icon
     */
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * Defines the applet icon.
     *
     * @param icon the icon to set
     */
    public final void setIcon(ImageIcon icon) {
        this.icon = icon;

        int width = icon.getIconWidth() + getInsets().left + getInsets().right;
        int height =
                icon.getIconHeight() + getInsets().top + getInsets().bottom;

        setMinimumSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setPreferredSize(new Dimension(width, height));

        contentChanged();
    }

    /**
     * Paints the notification display.
     *
     * @return the image containing the notification display graphics
     */
    protected BufferedImage paintNotifier() {

        String notifierString =
                new Integer(getNumberOfNotifications()).toString();

        int width = 20;
        int height = 20;

        float fontSize = 9.f;

        if (notifierBuffer == null
                || notifierBuffer.getWidth() != width
                || notifierBuffer.getHeight() != height) {

            notifierBuffer = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB);
        }

        Graphics2D g2 = notifierBuffer.createGraphics();

        // clear buffer
        Composite original = g2.getComposite();
        g2.setComposite(AlphaComposite.Clear);
        g2.setPaint(new Color(0.f, 0.f, 0.f, 0.f));
        g2.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
        g2.setComposite(original);
        g2.setFont(this.getFont().deriveFont(fontSize));

        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D area = fm.getStringBounds(notifierString, g2);

        int leftRightBorder = 10;

        if (width < area.getWidth() + leftRightBorder) {
            width = (int) area.getWidth() + leftRightBorder;
            g2.dispose();
            notifierBuffer = new BufferedImage(width + 10, height,
                    BufferedImage.TYPE_INT_ARGB);
            g2 = notifierBuffer.createGraphics();

            Font font = this.getFont().
                    deriveFont(fontSize).deriveFont(Font.BOLD);

            g2.setFont(font);
        }

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        float circleThickness = 2.f;

        BasicStroke stroke = new BasicStroke(circleThickness,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        g2.setStroke(stroke);

        int topLeftSpacing = (int) circleThickness / 2;
        int bottomRightSpacing = (int) circleThickness / 2 + topLeftSpacing + 1;

        g2.setColor(new Color(255, 0, 0, 200));

        g2.fillRoundRect(1, 1, width - 2, height - 2, 15, 15);

        g2.setColor(new Color(255, 255, 255, 220));

        g2.drawRoundRect(topLeftSpacing,
                topLeftSpacing, width - bottomRightSpacing,
                height - bottomRightSpacing, 15, 15);


        // drawString() always referes to the baseline of a character
        // therefore we have to compute an offset
        // TODO why the -1?
        double yOffset = fm.getAscent() - area.getHeight() / 2 - 1;

        g2.drawString(notifierString, (float) (width / 2 - area.getWidth() / 2),
                (float) (height / 2 + yOffset));

        return notifierBuffer;
    }

    /**
     * Returns the applet transparency.
     *
     * @return the applet transparency
     */
    public float getTransparency() {
        return transparency;
    }

    /**
     * Defines the applet transparency.
     *
     * @param transparency the transparency to set
     */
    public void setTransparency(float transparency) {
        this.transparency = transparency;
        contentChanged();
    }

    /**
     * Returns the action lister associated with this applet.
     *
     * @return the action listener associated with this applet if such an action
     * listener is defined;<code>null</code> otherwise
     */
    public ActionListener getActionListener() {
        return actionListener;
    }

    /**
     * Defines the action listener that is to be used for firing click events.
     *
     * @param actionListener the action listener to set
     */
    public void setActionListener(CanvasActionListener actionListener) {
        this.actionListener = actionListener;
    }

    /**
     * Fires an action event.
     *
     * @param event the event to fire
     */
    private void fireAction(ActionEvent event) {
        if (actionListener != null) {
            actionListener.actionPerformed(event);
        }
    }

    @Override
    public void mouseDragged(MouseEvent ev) {
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent ev) {
        if(isActive) {
            fireAction(new ActionEvent(this, 0, CLICKED_ACTION));
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
//        PulseIcon pulse = new PulseIcon(getMainCanvas(),
//                MessageType.INFO);
//        pulse.setSize(120, 120);
//        ImageIcon icon = new ImageIcon(pulse.getStillImage());
//
//        setIcon(icon);

        isActive = true;
        contentChanged();
        VSwingUtil.repaintRequest(this);
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
//        PulseIcon pulse = new PulseIcon(getMainCanvas(),
//                MessageType.INFO);
//        pulse.setSize(60, 60);
//        ImageIcon icon = new ImageIcon(pulse.getStillImage());

//        setIcon(icon);


        isActive = false;
        contentChanged();
        VSwingUtil.repaintRequest(this);
    }

    /**
     * Returns the dock this applet belongs to.
     *
     * @return the dock this applet belongs to if such a dock exists;
     * <code>null</code> otherwise
     */
    public Dock getDock() {
        return dock;
    }

    /**
     * Defines the dock this applet belongs to.
     *
     * @param dock the dock to set
     */
    public void setDock(Dock dock) {
        this.dock = dock;
    }

    /**
     * Fades in this applet.
     *
     * @param duration the duration of the fade in effect
     */
    public void fadeIn(double duration) {
        fadeEffect.setFadeIn(true);
        if (getEffectManager() != null) {
            getEffectManager().startEffect(fadeEffect.getName(), duration);
        }
    }

    /**
     * Fades out this applet.
     *
     * @param duration the duration of the fade out effect
     */
    public void fadeOut(double duration) {
        fadeEffect.setFadeIn(false);
        if (getEffectManager() != null) {
            getEffectManager().startEffect(fadeEffect.getName(), duration);
        }
    }

    /**
     * Hides this applet using the fade out effect.
     */
    public void hideApplet() {
        fadeOut(0.5);

        if (getMainCanvas().isDisableEffects()) {
            fadeEffect.setTransparency(0.f);
//            setIconVisible(false);
            setVisible(false);
        }
    }

    /**
     * Shows this applet using the fade in effect.
     */
    public void showApplet() {
        fadeIn(0.5);
    }

    /**
     * Returns the applet size independently from swing getSize() methods.
     *
     * @return the applet size
     */
    public Dimension getAppletSize() {
        return appletSize;
    }

    /**
     * @return the progress
     */
    public int getProgress() {
        return progress;
    }

    /**
     * @param progress the progress to set
     */
    public void setProgress(int progress) {

        if (progress < 0) {
            progress = 0;
        }

        if (progress > 100) {
            progress = 100;
        }

        if (this.progress != progress) {
            contentChanged();
            VSwingUtil.repaintRequest(this);
        }

        this.progress = progress;
    }
}
