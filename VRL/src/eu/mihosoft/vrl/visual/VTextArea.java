/* 
 * VTextArea.java
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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 * VRL text area. It has the same functionality as JTextArea. The only
 * difference is that it uses custom component design.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VTextArea extends JTextArea {

    private static final long serialVersionUID = -8459071468349563025L;
    private VComponent parent;
    private Style style;

    /**
     *
     */
    public static final String BACKGROUND_COLOR_KEY="VTextArea:Background:Color";
    /**
     *
     */
    public static final String BORDER_COLOR_KEY="VTextArea:Border:Color";
    /**
     *
     */
    public static final String BORDER_THICKNESS_KEY="VTextArea:Border:Thickness";

    /**
     * Constructor.
     * @param parent
     */
    public VTextArea(VComponent parent) {
//		this.setOpaque(false);
        this.parent = parent;
        setBorder(new EmptyBorder(3, 3, 3, 3));
        setOpaque(false);
    }

    /**
     * Constructor.
     * @param parent the main canvas
     * @param string the text
     */
    public VTextArea(VComponent parent, String string) {
        super(string);
//        this.setOpaque(false);
        this.parent = parent;
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);


        Style newStyle = parent.getStyle();

        if (style == null || !style.equals(newStyle)) {
            style = newStyle;
            setForeground(style.getBaseValues().getColor(Canvas.TEXT_COLOR_KEY));
            setCaretColor(style.getBaseValues().getColor(Canvas.CARET_COLOR_KEY));
            setSelectionColor(style.getBaseValues().getColor(Canvas.TEXT_SELECTION_COLOR_KEY));
            setSelectedTextColor(style.getBaseValues().getColor(Canvas.SELECTED_TEXT_COLOR_KEY));
        }

//        Color originalBackground = getBackground();

        setForeground(style.getBaseValues().getColor(Canvas.TEXT_COLOR_KEY));

        g2.setPaint(style.getBaseValues().getColor(BACKGROUND_COLOR_KEY));

        Composite original = g2.getComposite();

        AlphaComposite ac1 =
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                0.4f);
        g2.setComposite(ac1);

        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

        g2.setComposite(original);

        Color border =
                style.getBaseValues().getColor(BORDER_COLOR_KEY);

        g2.setColor(border);

        BasicStroke stroke =
                new BasicStroke(style.getBaseValues().getFloat(BORDER_THICKNESS_KEY));

        g2.setStroke(stroke);

        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

        setBackground(VSwingUtil.TRANSPARENT_COLOR);

        g2.setComposite(original);

        super.paintComponent(g);
    }
}
