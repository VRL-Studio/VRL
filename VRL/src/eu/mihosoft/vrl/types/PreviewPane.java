/* 
 * PreviewPane.java
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

package eu.mihosoft.vrl.types;

import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import javax.swing.JPanel;
import eu.mihosoft.vrl.visual.BufferedPainter;
import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.Ruler;
import java.awt.Image;
import javax.swing.Box;


/**
 * A preview pane displays a buffered image.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class PreviewPane extends JPanel implements Serializable,
        BufferedPainter {
    private static final long serialVersionUID = -780359293653708169L;
    private Image image;
    private BufferedImage buffer;
    private Color borderColor;

//    public PreviewPane(){
//
//    }

    
    @Override
    public void paintComponent(Graphics g) {
        if (buffer == null ||
                buffer.getWidth() != getWidth() ||
                buffer.getHeight() != getHeight())  {
            buffer = new BufferedImage(getWidth(),
                    getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

            Graphics2D g2 = buffer.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            if (image != null) {
                g2.drawImage(image, 0, 0,
                        buffer.getWidth() - 1, buffer.getHeight() - 1, this);
            }

            g2.dispose();

        }
        g.drawImage(buffer, 0, 0,
                this.getWidth() - 1, this.getHeight() - 1, this);
    }

    /**
     * Defines the image of the preview pane.
     * @param image the image to set
     */
    public void setImage(Image image) {
        this.image = image;
        this.contentChanged();
        System.out.println("PreviewPane::setImage()");

        if (image != null) {
            this.setPreferredSize(new Dimension(image.getWidth(null),
                    image.getHeight(null)));

            invalidate();
        }
    }

    /**
     * Returns the image of the preview pane.
     * @return the image of the preview pane
     */
    public Image getImage() {
        return this.image;
    }

    @Override
    public void contentChanged() {
        buffer = null;
    }
}
