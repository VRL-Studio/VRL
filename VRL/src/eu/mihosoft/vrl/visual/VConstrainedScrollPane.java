/* 
 * VConstrainedScrollPane.java
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

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VConstrainedScrollPane extends VScrollPane {

    private int maxWidth;
    private int maxHeight;

    public VConstrainedScrollPane() {
        super();
    }

    public VConstrainedScrollPane(Component view) {
        super(view);

    }

    public VConstrainedScrollPane(int vsbPolicy, int hsbPolicy) {
        super(vsbPolicy, hsbPolicy);
    }

    public VConstrainedScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
        super(view, vsbPolicy, hsbPolicy);
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {

        super.setBounds(x, y, Math.min(w, maxWidth), Math.min(h, maxHeight));

        if (w < maxWidth) {
            setHorizontalScrollBarPolicy(
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        } else {
            setHorizontalScrollBarPolicy(
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }

        if (h < maxHeight) {
            setVerticalScrollBarPolicy(
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        } else {
            setVerticalScrollBarPolicy(
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        }
    }

    @Override
    public Dimension getPreferredSize() {

        int wPlus = verticalScrollBar.getWidth();
        int hPlus = horizontalScrollBar.getHeight();

        int w = super.getPreferredSize().width + wPlus;
        int h = super.getPreferredSize().height + hPlus;

        if (w < maxWidth && h < maxHeight) {
            return new Dimension(w, h);
        } else {
            return new Dimension(Math.min(maxWidth, w), Math.min(maxHeight, h));
        }
    }

    /**
     * @return the maxWidth
     */
    public int getMaxWidth() {
        return maxWidth;
    }

    /**
     * @param maxWidth the maxWidth to set
     */
    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    /**
     * @return the maxHeight
     */
    public int getMaxHeight() {
        return maxHeight;
    }

    /**
     * @param maxHeight the maxHeight to set
     */
    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }
}
