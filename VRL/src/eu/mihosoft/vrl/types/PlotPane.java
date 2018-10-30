/* 
 * PlotPane.java
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

package eu.mihosoft.vrl.types;

import eu.mihosoft.vrl.visual.*;
import eu.mihosoft.vrl.visual.ImageUtils;
import eu.mihosoft.vrl.reflection.*;
import eu.mihosoft.vrl.visual.BufferedPainter;
import eu.mihosoft.vrl.visual.Canvas;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.Box;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * A plot pane can be used to display buffered imgages. It is designed for
 * type representations and shouldn't be used for other purposes.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class PlotPane extends JPanel
        implements BufferedPainter, ComponentListener {

    private static final long serialVersionUID = 4795229540234871712L;
    private TypeRepresentationBase typeRepresentation;
    transient private Image image;
    transient private BufferedImage previewImage;
    transient private BufferedImage buffer;
    private Color borderColor;
    private boolean imageChanged = true;
    private Shape shape;
    private boolean initialized = false;
    private boolean fixedAspectRatio = false;
    private boolean requestNewBuffer = false;

    public static final String UPPER_COLOR_KEY = "PlotPane:upperColor";
    public static final String LOWER_COLOR_KEY = "PlotPane:lowerColor";

    /**
     * Constructor.
     * @param parent the type representation that uses the plot pane
     */
    public PlotPane(TypeRepresentationBase parent) {
        this.typeRepresentation = parent;

        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.X_AXIS);
        setLayout(layout);

        this.add(Box.createGlue());
        Box box = new Box(VBoxLayout.Y_AXIS);
        box.add(Box.createVerticalGlue());

        box.add(new Ruler(this));

        this.add(box);

        addComponentListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();

//        Canvas mainCanvas = getTypeRepresentation().getMainCanvas();
        Color newColor = getTypeRepresentation().getStyle().
                getBaseValues().getColor(CanvasWindow.BORDER_COLOR_KEY);

        if (buffer == null
                || buffer.getWidth() != getWidth()
                || buffer.getHeight() != getHeight()
                || !newColor.equals(borderColor)
                || requestNewBuffer) {

            // this is necessary to restore size when loaded from file
            if (!initialized) {
                revalidate();
                typeRepresentation.updateLayout();
                initialized = true;
            }

            borderColor = newColor;

            if (image != null) {

                double aspectRatio = image.getWidth(null) / image.getHeight(null);

                double newAspectRatio = getWidth() / getHeight();

                if (newAspectRatio > aspectRatio) {
                    height = getHeight();
                    width = image.getWidth(null) * (height / image.getHeight(null));
                }

            }

            buffer =
                    ImageUtils.createCompatibleImage(getWidth(), getHeight());

            requestNewBuffer = false;

            BufferedImage buffer2 =
                    ImageUtils.createCompatibleImage(getWidth(), getHeight());

            setShape(new RoundRectangle2D.Double(0, 0,
                    getWidth(), getHeight(), 20, 20));

            Graphics2D g2 = buffer2.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getTypeRepresentation().getStyle().getBaseValues().
                    getColor(CanvasWindow.BORDER_COLOR_KEY));

            g2.setStroke(new BasicStroke(1));

            Color upperColor = getTypeRepresentation().getStyle().
                    getBaseValues().getColor(UPPER_COLOR_KEY);
            Color lowerColor = getTypeRepresentation().getStyle().
                    getBaseValues().getColor(LOWER_COLOR_KEY);

            GradientPaint paint = new GradientPaint(0, 0,
                    upperColor,
                    0, this.getHeight(),
                    lowerColor,
                    false);

            g2.setPaint(paint);

            g2.fill(getShape());

            g2.dispose();

            g2 = buffer.createGraphics();

            g2.drawImage(buffer2, 0, 0,
                    buffer.getWidth(), buffer.getHeight(), this);

            Shape originalClip = g2.getClip();
            Shape roundClip = new RoundRectangle2D.Double(0, 0,
                    getWidth() - 1, getHeight() - 1, 20, 20);

            g2.setClip(roundClip);

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            if (image != null) {
                generatePreviewImage();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);

                if (previewImage != null) {
                    g2.drawImage(previewImage, 0, 0,
                            buffer.getWidth(), buffer.getHeight(), this);
                }
            }

            Composite originalComposite = g2.getComposite();

            g2.setClip(originalClip);

            g2.setColor(getTypeRepresentation().getStyle().getBaseValues().
                    getColor(CanvasWindow.BORDER_COLOR_KEY));

            g2.setStroke(new BasicStroke(1));

            g2.draw(new RoundRectangle2D.Double(0, 0,
                    getWidth() - 1, getHeight() - 1, 20, 20));

            g2.dispose();
        }
        g.drawImage(buffer, 0, 0,
                this.getWidth(), this.getHeight(), this);

    }

    /**
     * Generates a scaled preview image (depending on the plot pane size).
     */
    public void generatePreviewImage() {
        if (image != null && buffer != null /*&& imageChanged == true*/) {
            previewImage = new BufferedImage(buffer.getWidth(),
                    buffer.getHeight(),
                    BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g2 = previewImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);


            // *******************************
            // fixed aspect ratio
            // *******************************

            if (isFixedAspectRatio()) {
                double scalex = 1;
                double scaley = 1;

                double sx = (double) getWidth() / image.getWidth(null);
                double sy = (double) getHeight() / image.getHeight(null);
                scalex = Math.min(sx, sy);
                scaley = scalex;
                // center the image
                g2.translate((getWidth() - (image.getWidth(null) * scalex)) / 2,
                        (getHeight() - (image.getHeight(null) * scaley)) / 2);

                g2.scale(scalex, scaley);

                g2.drawImage(image, 0, 0, null);

            } else {
                g2.drawImage(image, 0, 0,
                        previewImage.getWidth(), previewImage.getHeight(),
                        this);
            }

            imageChanged = false;
            g2.dispose();
        }
    }

    /**
     * Defines the image of the plot pane.
     * @param image the image to set
     */
    public void setImage(Image image) {
        this.image = image;
        this.contentChanged();
//        System.out.println("PlotPane::setImage()");

        imageChanged = true;
        if (image != null) {
            revalidate();
        }
    }

    /**
     * Returns the image of the plot pane.
     * @return the image of the plot pane
     */
    public Image getImage() {
        return this.image;
    }

    @Override
    public void contentChanged() {
        requestNewBuffer = true;

        // check whether we are visible or not
        // if we are invisible it is safe to delete the current buffer

        Container parent = getParent();
        boolean visible = isVisible();

        while (parent!=null && visible) {
            visible = parent.isVisible();
            parent = parent.getParent();
        }

        // it is safe to delete the buffer
        if (!visible) {
            buffer = null;
            //System.out.println("DELETE BUFFER!");
        }
    }

    /**
     * Returns the type representation that uses this plot pane.
     * @return the type representation that uses this plot pane
     */
    public TypeRepresentationBase getTypeRepresentation() {
        return typeRepresentation;
    }

    /**
     * Returns the shape of the plot pane.
     * @return the shape of the plot pane
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * Defines the shape of the plot pane.
     * @param shape the shape to set
     */
    public void setShape(Shape shape) {
        this.shape = shape;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        typeRepresentation.setValueOptions("width=" + getWidth() + ";"
                + "height=" + getHeight());
    }

    @Override
    public void componentMoved(ComponentEvent e) {
//	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void componentShown(ComponentEvent e) {
//	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void componentHidden(ComponentEvent e) {
//	throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the fixed aspect ratio
     */
    public boolean isFixedAspectRatio() {
        return fixedAspectRatio;
    }

    /**
     * @param fixedAspectRatio the value to set
     */
    public void setFixedAspectRatio(boolean fixedAspectRatio) {
        this.fixedAspectRatio = fixedAspectRatio;
    }

    @Override
    public void setVisible(boolean value) {
        super.setVisible(value);
        this.contentChanged();
        System.out.println("VISIBLE");
    }
}
