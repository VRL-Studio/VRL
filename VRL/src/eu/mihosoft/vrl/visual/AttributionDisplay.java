/* 
 * AttributionDisplay.java
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
package eu.mihosoft.vrl.visual;

import eu.mihosoft.vrl.reflection.ComponentTree;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class AttributionDisplay implements GlobalForegroundPainter {

    private Canvas canvas;
    private Image vrlImage;

    private boolean positionChanged = true;

    public AttributionDisplay(Canvas canvas) {
        this.canvas = canvas;

        try {
            vrlImage = new ImageIcon(
                    ComponentTree.class.getResource(
                            "/eu/mihosoft/vrl/resources/images/vrl-logo-dark-bg.png")).getImage();

            vrlImage = ImageUtils.convert(vrlImage, BufferedImage.TYPE_INT_ARGB, 140, 36, true);

        } catch (Exception ex) {
            System.out.println(">> AttributionDisplay: cannot load vrl logo!");
        }
    }

    @Override
    public void paintGlobal(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        VScale scale = TransformingParent.getScale(canvas);

        int visibleRectX = (int) (canvas.getVisibleRect().getX() * scale.getScaleX());
        int visibleRectY = (int) (canvas.getVisibleRect().getY() * scale.getScaleY());

        int visibleRectWidth = (int) (canvas.getVisibleRect().getWidth() * scale.getScaleX());
        int visibleRectHeight = (int) (canvas.getVisibleRect().getHeight() * scale.getScaleY());

        Composite original = g2.getComposite();

        AlphaComposite ac1 = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 0.45f);

        g2.setComposite(ac1);

        g2.setColor(canvas.getStyle().getBaseValues().
                getColor(MessageBox.BOX_COLOR_KEY));

        int rectX = visibleRectX + visibleRectWidth - 160 - 10;
        int rectY = visibleRectY + visibleRectHeight - 70;

        int imgX = visibleRectX + visibleRectWidth - 160;
        int imgY = visibleRectY + visibleRectHeight - 60 + 2;

        if (canvas.getMessageBox().isOpened()
                || canvas.getMessageBox().isOpening()) {
            rectY = 10;
            imgY = 20 + 2;
            positionChanged = true;
        }

        g2.fillRoundRect(rectX, rectY, 160, 60, 18, 18);

        g2.setStroke(new BasicStroke(4f));
        g2.setColor(canvas.getStyle().getBaseValues().
                getColor(MessageBox.BOX_COLOR_KEY));
        g2.drawRoundRect(rectX, rectY, 160, 60, 18, 18);

        ac1 = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 0.75f);

        g2.setComposite(ac1);

        g2.drawImage(vrlImage, imgX, imgY, null);

        ac1 = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 0.85f);

        g2.setComposite(ac1);

        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(canvas.getStyle().getBaseValues().
                getColor(MessageBox.TEXT_COLOR_KEY));
        g2.drawRoundRect(rectX, rectY, 160, 60, 18, 18);

        g2.setComposite(original);

        if (positionChanged) {
            canvas.repaint();
            positionChanged = false;
        }

    }
}
