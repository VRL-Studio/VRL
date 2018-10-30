/* 
 * NoLineWrapParagraphView.java
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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.text.Element;
import javax.swing.text.ParagraphView;

/**
 * Does not wrap lines and has support for error notification.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class NoLineWrapParagraphView extends ParagraphView {

    private VCodePane codePane;
    private boolean errorView;

    /**
     * Constructor.
     * @param e the element
     * @param codePane the code pane
     */
    public NoLineWrapParagraphView(Element e, VCodePane codePane) {
        super(e);
        this.codePane = codePane;
    }

    @Override
    public void paintChild(Graphics g, Rectangle r, int n) {
        if (isErrorView()) {

            // use these coordinates for round rectangles
//            int x = codePane.getInsets().left / 2;
//            int y = r.y;
//            int w = codePane.getWidth() - x - codePane.getInsets().right;
//            int h = r.height;

            // use these coordinates for rectangles and lines
            int x = codePane.getInsets().left / 2 - 1;
            int y = r.y;
            int w = codePane.getWidth() + codePane.getInsets().left
		    - codePane.getInsets().right + 1;
            int h = r.height;

            Style style = codePane.getVParent().getStyle();

            g.setColor(style.getBaseValues().getColor(
                    VCodeEditor.COMPILE_ERROR_COLOR_KEY));
//            g.fillRoundRect(x, y, w, h, 9, 9);

            g.fillRect(x, y, w, h);

            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(style.getBaseValues().getColor(
                    VCodeEditor.COMPILE_ERROR_BORDER_COLOR_KEY));
//            g2.drawRoundRect(x, y, w, h, 10, 10);

            g2.drawLine(x, y, w - 1, y);
            g2.drawLine(x, y + h, w - 1, y + h);
        }

        super.paintChild(g, r, n);
    }

    /**
     * Overriden to disable line wrap.
     */
    @Override
    public void layout(int width, int height) {
        super.layout(Short.MAX_VALUE, height);
    }

    /**
     * Overriden to disable line wrap.
     */
    @Override
    public float getMinimumSpan(int axis) {
        return super.getPreferredSpan(axis);
    }

    /**
     * Indicates whether this paragraph view is defined as error view.
     * @return <code>true</code> if this paragraph view is adefined as error
     *         view;<code>false</code> otherwise
     */
    public boolean isErrorView() {
        return errorView;
    }

    /**
     * Defines whether this paragraph view is to be an error view.
     * @param errorView the error view state to set
     */
    public void setErrorView(boolean errorView) {
        this.errorView = errorView;
    }
}
