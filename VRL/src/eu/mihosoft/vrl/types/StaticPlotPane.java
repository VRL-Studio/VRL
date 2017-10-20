/* 
 * StaticPlotPane.java
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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.Box;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * A static plot pane can be used to display buffered imgages. It is designed
 * for type representations and shouldn't be used for other purposes. The
 * difference between the usual plot pane and static plot pane is that the
 * static plot pane does never change its size.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class StaticPlotPane extends JPanel implements BufferedPainter {
    private static final long serialVersionUID = 1136671151984609044L;

    private TypeRepresentationBase typeRepresentation;
    transient private BufferedImage image;
    transient private BufferedImage previewImage;
    transient private BufferedImage buffer;
    private Color borderColor;
    private boolean imageChanged = true;
    private Shape shape;

     /**
     * Constructor.
     * @param parent the type representation that uses the plot pane
     */
    public StaticPlotPane(TypeRepresentationBase parent) {
        this.typeRepresentation = parent;
    }

    @Override
    public void paintComponent(Graphics g) {
        Canvas mainCanvas = getTypeRepresentation().getMainCanvas();
        Color newColor = mainCanvas.getStyle().getBaseValues().getColor(
                CanvasWindow.BORDER_COLOR_KEY);

        if (buffer == null ||
                buffer.getWidth() != getWidth() ||
                buffer.getHeight() != getHeight() ||
                !newColor.equals(borderColor)) {
            borderColor = newColor;
//            buffer = new BufferedImage(getWidth(),
//                    getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
//
//            BufferedImage buffer2 = new BufferedImage(getWidth(),
//                    getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

            buffer =
                    ImageUtils.createCompatibleImage(getWidth(), getHeight());

            BufferedImage buffer2 =
                    ImageUtils.createCompatibleImage(getWidth(), getHeight());

            setShape(new RoundRectangle2D.Double(0, 0,
                    getWidth(), getHeight(), 20, 20));

            Graphics2D g2 = null;

            g2 = buffer.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.drawImage(buffer2, 0, 0,null);

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
                g2.drawImage(previewImage, 0, 0,
                        getWidth(), getHeight(), null);
            }

            Composite originalComposite = g2.getComposite();

            g2.setClip(originalClip);

            g2.setColor(mainCanvas.getStyle().getBaseValues().getColor(
                    CanvasWindow.BORDER_COLOR_KEY));

            g2.setStroke(new BasicStroke(1));

            g2.draw(new RoundRectangle2D.Double(0, 0,
                    getWidth() - 1, getHeight() - 1, 20, 20));

            g2.dispose();
        }
        g.drawImage(buffer, 0, 0,
                this.getWidth(), this.getHeight(), null);
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
            g2.drawImage(image, 0, 0,
                    previewImage.getWidth(), previewImage.getHeight(),
                    this);
            imageChanged = false;
            g2.dispose();
        }
    }

     /**
     * Defines the image of the plot pane.
     * @param image the image to set
     */
    public void setImage(BufferedImage image) {
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
    public BufferedImage getImage() {
        return this.image;
    }

    @Override
    public void contentChanged() {
        buffer = null;
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
}
