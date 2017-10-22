/* 
 * IconBox.java
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

import java.awt.Component;
import java.awt.ComponentOrientation;
import javax.swing.Box;
import javax.swing.border.EmptyBorder;

/**
 * Defines an icon box used as container for window icons.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class IconBox extends TransparentPanel {
    private static final long serialVersionUID = 6884474261263081716L;

    private TransparentPanel iconPane;
//    private int iconWidth = 25;
//    private int iconHeight = 25;
    private int iconBoxWidth;
    private int rightMargin = 3;
    private int leftMargin = 5;
    private IconBoxChangedListener iconBoxListener;

    /**
     * Constructor.
     */
    public IconBox() {
        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.X_AXIS);
        setLayout(layout);

        // TODO why top inset2 and bottom 3?
        setBorder(new EmptyBorder(2,0,3,0));

        iconPane = new TransparentPanel();
        VBoxLayout paneLayout = new VBoxLayout(iconPane, VBoxLayout.LINE_AXIS);
        iconPane.setLayout(paneLayout);
        iconPane.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        iconPane.setAlignmentX(Component.RIGHT_ALIGNMENT);

        add(Box.createHorizontalStrut(leftMargin));

        add(iconPane);

        add(Box.createHorizontalStrut(rightMargin));
    }

    /**
     * Adds an icon to this box.
     * @param c the icon to add
     */
    public void addIcon(Component c) {
        iconPane.add(c);
        iconBoxWidth += c.getPreferredSize().width;
        revalidate();
        if (iconBoxListener != null) {
            iconBoxListener.iconBoxChanged(this);
        }
    }

    /**
     * Removes an icon from the box.
     * @param c the icon to remove
     */
    public void removeIcon(Component c) {
        iconPane.remove(c);
        iconBoxWidth -= c.getPreferredSize().width;
        revalidate();
        if (iconBoxListener != null) {
            iconBoxListener.iconBoxChanged(this);
        }
    }

    /**
     * Returns the icon box width.
     * @return the icon box width
     */
    public int getIconBoxWidth() {
        return iconBoxWidth + rightMargin + leftMargin;
    }

    /**
     * Returns the icon box changed listener.
     * @return the icon box listener if such a listener is defined;
     * <code>null</code> otherwise
     */
    public IconBoxChangedListener getIconBoxListener() {
        return iconBoxListener;
    }

    /**
     * Defines thhe icon box listener for this box.
     * @param iconBoxListener the icon box listener to set
     */
    public void setIconBoxListener(IconBoxChangedListener iconBoxListener) {
        this.iconBoxListener = iconBoxListener;
    }
}
