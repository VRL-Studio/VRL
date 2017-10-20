/* 
 * VHTMLEditorKit.java
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

package eu.mihosoft.vrl.visual;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Position.Bias;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLEditorKit.HTMLFactory;
import javax.swing.text.html.ImageView;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VHTMLEditorKit extends HTMLEditorKit {

    final ViewFactory defaultFactory = new VHTMLFactory();

    @Override
    public ViewFactory getViewFactory() {
        return defaultFactory;
    }
}

class VHTMLFactory extends HTMLFactory {

    @Override
    public View create(Element elem) {
        Object o =
                elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
        if (o instanceof HTML.Tag) {
            HTML.Tag kind = (HTML.Tag) o;
            if (kind.toString().equals(VHTML.ICON_INFO)) {
                return new VImageView(elem, MessageType.INFO);
            }
            if (kind.toString().equals(VHTML.ICON_WARNING)) {
                return new VImageView(elem, MessageType.WARNING);
            }
            if (kind.toString().equals(VHTML.ICON_ERROR)) {
                return new VImageView(elem, MessageType.ERROR);
            }
        }
        return super.create(elem);
    }
}

class VImageView extends ImageView {

    private int width;
    private int height;
    private Image img;

    public VImageView(Element elem, MessageType type) {
        super(elem);
        setSize(getIntAttr(HTML.Attribute.WIDTH, -1),
                getIntAttr(HTML.Attribute.HEIGHT, -1));
        width = getIntAttr(HTML.Attribute.WIDTH, -1);
        height = getIntAttr(HTML.Attribute.HEIGHT, -1);

        img = new PulseIcon(null, type).
                getStillImage(60, 60).getScaledInstance(
                width, height, BufferedImage.SCALE_SMOOTH);
    }

    @Override
    public void paint(Graphics g, Shape shape) {
        Graphics2D g2 = (Graphics2D) g;

        g2.drawImage(img, 0, 0, width, height, null);
    }

    /**
     * Convenience method for getting an integer attribute from the elements
     * AttributeSet.
     */
    private int getIntAttr(HTML.Attribute name, int deflt) {
        AttributeSet attr = getElement().getAttributes();
        if (attr.isDefined(name)) {		// does not check parents!
            int i;
            String val = (String) attr.getAttribute(name);
            if (val == null) {
                i = deflt;
            } else {
                try {
                    i = Math.max(0, Integer.parseInt(val));
                } catch (NumberFormatException x) {
                    i = deflt;
                }
            }
            return i;
        } else {
            return deflt;
        }
    }

    @Override
    public float getPreferredSpan(int axis) {
        switch (axis) {
            case View.X_AXIS:
                return width + width / 4; // THIS IS A HACK!!!
            case View.Y_AXIS:
                return height - height / 3; // THIS IS A HACK!!!
            default:
                throw new IllegalArgumentException("Invalid axis: " + axis);
        }
    }
}
