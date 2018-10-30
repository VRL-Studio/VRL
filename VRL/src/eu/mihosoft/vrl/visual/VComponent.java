/* 
 * VComponent.java
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

import eu.mihosoft.vrl.effects.EffectPanel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JToolTip;

/**
 * VComponent is a Swing container that supports custom styles, painters for
 * rendering and visual effects.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 * @see eu.mihosoft.vrl.visual.style.CanvasStyle
 * @see eu.mihosoft.vrl.effects.Effect
 */
public class VComponent extends EffectPanel
        implements CanvasChild, VLayoutControllerProvider, IDObject {

    /**
     * custom style specifically defined for this component
     */
    private Style customStyle;
    /**
     * cached style, usually a style defined for a parent component or the style
     * of the maincanvas
     */
    private Style cachedStyle;
    /**
     * the id
     */
    private int id;
    /**
     * a swing container representing the back of this component
     */
    private TransparentPanel backSide = new TransparentPanel();
    /**
     * custom painter
     */
    private Painter painter;
    boolean customPainter;
    private boolean frontVisible = true;
    private String painterKey;
    private boolean disposed = false;
    private VLayoutController layoutController;
    private boolean boundsLocked = false;
    private boolean ingoredByLayout = false;

    /**
     * Constructor.
     */
    public VComponent() {
        init();
    }

    /**
     * Initializes this component.
     */
    private void init() {
        getBackSide().setVisible(false);
    }

    /**
     * Constructor.
     *
     * @param mainCanvas canvas
     */
    public VComponent(Canvas mainCanvas) {
        setMainCanvas(mainCanvas);
        init();
    }

    /**
     * Constructor.
     *
     * @param mainCanvas canvas
     * @param customStyle style
     */
    public VComponent(Canvas mainCanvas, Style customStyle) {
        this.customStyle = customStyle;
        setMainCanvas(mainCanvas);
    }

    /**
     * <p> Returns the style of this component. If this component does not
     * define a style this method will search for a parent that defines a style
     * until it reaches the toplevel parent. If no parent defines a style the
     * current canvas style is returned. <p> <p> <b>Note:</b> Although this
     * method involves a lot of searching it is relatively efficient. Style
     * definitions from parent components are cached to reduce possible
     * performance overhead. </p>
     *
     * @return the style of this component
     */
    public final Style getStyle() {
        Style result = customStyle;

        if (result == null) {
            if (cachedStyle != null) {
                result = cachedStyle;
            } else {

                VComponent parent = (VComponent) VSwingUtil.getParent(
                        this, VComponent.class);

                if (parent != null) {
                    result = parent.getStyle();
                    cachedStyle = result;
                } else if (getMainCanvas() != null) {
                    result = getMainCanvas().getStyle();
                    cachedStyle = result;
                }
            }
        }

        return result;
    }

    @Override
    public JToolTip createToolTip() {
        return super.createToolTip();
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        if (!isBoundsLocked()) {
            super.setBounds(x, y, w, h);
        }
    }

//    @Override
//    public void setBounds(int x, int y, int w, int h) {
//
//        super.setBounds(x, y, w, h);
//
//        if (getPainter() != null) {
//            getPainter().setSize(new Dimension(w, h));
//        }
//    }
//
//    @Override
//    public Dimension getPreferredSize() {
//        if (getPainter() != null) {
//            Dimension d = getPainter().getPreferredSize();
//            if (d != null) {
//                return d;
//            }
//        }
//
//        return super.getPreferredSize();
//    }
//
//    @Override
//    public Dimension getMinimumSize() {
//        if (getPainter() != null) {
//            Dimension d = getPainter().getPreferredSize();
//            if (d != null) {
//                return d;
//            }
//        }
//
//        return super.getPreferredSize();
//    }
//
//    @Override
//    public Dimension getMaximumSize() {
//        if (getPainter() != null) {
//            Dimension d = getPainter().getPreferredSize();
//            if (d != null) {
//                return d;
//            }
//        }
//
//        return super.getPreferredSize();
//    }
//    @Override
//    protected  final void addImpl(Component comp, Object constraints, int index) {
//        super.addImpl(comp, constraints, index);
//
//        if (comp instanceof VComponent) {
//            ((VComponent) comp).setMainCanvas(getMainCanvas());
//        }
//    }
//    /**
//     * @param customStyle the customStyle to set
//     */
//    public final void setStyleForAll(CanvasStyle customStyle) {
//        this.customStyle = customStyle;
//
//        ArrayList<Component> children =
//                VSwingUtil.getAllChildren(this, VComponent.class);
//
//        for (Component c : children) {
//            ((VComponent) c).setStyle(customStyle);
//        }
//    }
    /**
     * Defines a style for this component.
     *
     * <b>Note:</b> Calling this method will notify every child component that
     * implements the {@link StyleChangedListener} interface about style changes
     * by calling
     * {@link StyleChangedListener#styleChanged(eu.mihosoft.vrl.visual.Style)}.
     * Also child components implementing the {@link BufferedPainter} interface
     * will be notified about changes by calling
     * {@link BufferedPainter#contentChanged()}.
     *
     * @param customStyle the style to set
     */
    public final void setStyle(Style customStyle) {
        this.customStyle = customStyle;
        contentChanged();
    }

    @Override
    public final void setMainCanvas(Canvas mainCanvas) {
        Canvas oldCanvas = getMainCanvas();
//        System.out.println("MAINCANVAS CALLED: " + mainCanvas);
        super.setMainCanvas(mainCanvas);

        ArrayList<Component> children =
                VSwingUtil.getAllChildren(this, VComponent.class);

        for (Component c : children) {
            ((VComponent) c).setMainCanvas(mainCanvas);
        }

        newCanvasAssigned(oldCanvas, mainCanvas);
    }

    @Override
    public final Canvas getMainCanvas() {
        Canvas result = super.getMainCanvas();

        if (result == null) {
            VComponent parent = (VComponent) VSwingUtil.getParent(
                    this, VComponent.class);

            if (parent != null) {
                result = parent.getMainCanvas();
            }

            // caching result
            if (result != null) {
                super.setMainCanvas(result);
            }
        }

        return result;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public void contentChanged() {

        clearStyleCache();

        if (!customPainter) {
            painter = null;
        }

        ArrayList<Component> children =
                VSwingUtil.getAllChildren(this, BufferedPainter.class);

        children.addAll(
                VSwingUtil.getAllChildren(this, StyleChangedListener.class));

        try {
            for (Component c : children) {

                if (c instanceof StyleChangedListener) {
                    ((StyleChangedListener) c).styleChanged(getStyle());
                }

                if (c instanceof BufferedPainter) {
                    if (c instanceof VComponent) {
                        ((VComponent) c).clearStyleCache();
                    }
                    ((BufferedPainter) c).contentChanged();
                }
            }
        } catch (Exception ex) {
            //
        }

        if (getBorder() != null && getBorder() instanceof BufferedPainter) {
            BufferedPainter p = (BufferedPainter) getBorder();
            p.contentChanged();
        }

        if (getStyle() != null && getPainter() instanceof BufferedPainter) {
            ((BufferedPainter) getPainter()).contentChanged();
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getPainter() != null) {
            // create graphics context (component dimensions - insets)
            int x = getInsets().left;
            int y = getInsets().top;
            int w = getSize().width - getInsets().left - getInsets().right;
            int h = getSize().height - getInsets().top - getInsets().bottom;

            // paint custom content
            getPainter().paint(
                    g.create(x, y, w, h), getStyle(), new Dimension(w, h));

        } else {
            super.paintComponent(g);
        }
    }

    /**
     * Clears the cached style.
     */
    private void clearStyleCache() {
        cachedStyle = null;
    }

    /**
     * This method will be called whenever a new canvas is assigned. It provides
     * refererences to the old and new canvas instance. A possible use case of
     * this method is adding/removing listeners to/from the canvas.
     *
     * @param oldCanvas old canvas object
     * @param newCanvas new canvas object
     */
    protected void newCanvasAssigned(Canvas oldCanvas, Canvas newCanvas) {
        //
    }

    @Override
    public void dispose() {

        try {
            ArrayList<Component> children =
                    VSwingUtil.getAllChildren(this, VComponent.class);
            for (Component c : children) {
                VComponent vC = (VComponent) c;
                vC.dispose();
            }

            if (painter != null && painter instanceof Disposable) {
                Disposable d = (Disposable) painter;

                d.dispose();
            }

        } catch (Exception ex) {
            //
        } finally {
            disposed = true;
            super.dispose();
        }
    }

    /**
     * Shows the front side of this component.
     */
    public void showFront() {
        for (Component c : getComponents()) {
            c.setVisible(true);
        }
        getBackSide().setVisible(false);
        remove(getBackSide());
        frontVisible = true;
    }

    /**
     * Shows the back side of this component.
     */
    public void showBack() {
        Dimension size = new Dimension(
                getSize().width - getInsets().left - getInsets().right,
                getSize().height - getInsets().top - getInsets().bottom);
        for (Component c : getComponents()) {
            c.setVisible(false);
        }
        add(getBackSide());
        getBackSide().setVisible(true);
        getBackSide().setPreferredSize(size);
        getBackSide().setSize(size);
        frontVisible = false;
    }

    /**
     * Returns the container that represents the back side of this component.
     *
     * @return the container that represents the back side of this component
     */
    public TransparentPanel getBackSide() {
        return backSide;
    }

    /**
     * Indicates whether the front is visible.
     *
     * @return <code>true</code> if front is visible; <code>false</code>
     * otherwise
     */
    public boolean isFrontVisible() {
        return frontVisible;
    }

    /**
     * Flips this component, i.e., performs a 180 degree rotation around the
     * vertical axis.
     */
    public void flip() {
        if (isFrontVisible()) {
            showBack();
        } else {
            showFront();
        }
    }

    /**
     * Returns the painter that currently renders this component.
     *
     * @return the painter that currently renders this component
     */
    public Painter getPainter() {

        if (painter == null && painterKey != null) {
            painter = getStyle().getBaseValues().getPainter(painterKey, this);
        }

        return painter;
    }

    /**
     * Defines the style key of the painter that shall be used to render this
     * component. The Painter that will be used depends on the current style of
     * this component.
     *
     * @param key key to set
     */
    public void setPainterKey(String key) {
        this.painterKey = key;
    }

    /**
     * Defines the painter that shall be used to render this component.
     *
     * @param painter the painter to set
     */
    public void setPainter(Painter painter) {
        this.painter = painter;
        customPainter = painter != null;
    }

    /**
     * @return <code>true</code> if this component has been disposed;
     * <code>false</code> otherwise
     */
    public boolean isDisposed() {
        return disposed;
    }

    /**
     * @return the layoutController
     */
    @Override
    public VLayoutController getLayoutController() {
        return layoutController;
    }

    /**
     * @param layoutController the layoutController to set
     */
    public void setLayoutController(VLayoutController layoutController) {
        this.layoutController = layoutController;
    }

    /**
     * @return the positionLocked
     */
    public boolean isBoundsLocked() {
        return boundsLocked;
    }

    /**
     * @param positionLocked the positionLocked to set
     */
    @Override
    public void setBoundsLocked(boolean boundsLocked) {
        this.boundsLocked = boundsLocked;
    }

    /**
     * @return the ingoredByLayout
     */
    @Override
    public boolean isIngoredByLayout() {
        return ingoredByLayout;
    }

    /**
     * @param ingoredByLayout the ingoredByLayout to set
     */
    public void setIngoredByLayout(boolean ingoredByLayout) {
        this.ingoredByLayout = ingoredByLayout;
    }
}
