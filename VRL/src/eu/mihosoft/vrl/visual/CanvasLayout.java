/* 
 * CanvasLayout.java
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;
import java.io.Serializable;
import org.apache.tools.ant.listener.MailLogger;

/**
 * Layoutmanager for <code>Canvas</code>. It does not change the position
 * of its child objects. It is only concerned with setting appropriate container
 * sizes.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class CanvasLayout implements LayoutManager, Serializable {

    private int minWidth = 0, minHeight = 0;
    private int preferredWidth = 0, preferredHeight = 0;
    private boolean sizeUnknown = true;

    public CanvasLayout() {
    }

    /* Required by LayoutManager. */
    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    /* Required by LayoutManager. */
    @Override
    public void removeLayoutComponent(Component comp) {
    }

    private void setSizes(Container parent) {
        int nComps = parent.getComponentCount();
        Dimension d = null;

        //Reset preferred/minimum width and height.

        minWidth = 0;
        minHeight = 0;

        int tmpMaxWidth = 0;
        int tmpMaxHeight = 0;

        //System.out.println("SetSizes:");

        for (int i = 0; i < nComps; i++) {
            Component c = parent.getComponent(i);

            if (c instanceof CanvasWindow) {
                CanvasWindow w = (CanvasWindow) c;

                Point loc = w.getLocation();
                loc.x = Math.max(loc.x, 0 - w.getInsets().left);
                loc.y = Math.max(loc.y, 0 - w.getInsets().top);

                w.setLocation(loc);
            }

            d = c.getPreferredSize();
            c.setSize(d);

            // effect pane size is not considered for canvas size calculations
            if (c instanceof EffectPane) {
//                c.setSize(300, 300);
                continue;
            }

            Point location = c.getLocation();

            int tmpWidth = d.width + location.x;
            int tmpHeight = d.height + location.y;

            // components only influence canvas size if they are visible
            if (c.isVisible()) {
                tmpMaxWidth = Math.max(tmpMaxWidth, tmpWidth);
                tmpMaxHeight = Math.max(tmpMaxHeight, tmpHeight);
            }
        }

        minWidth = tmpMaxWidth;
        minHeight = tmpMaxHeight;

        preferredWidth = Math.max(minWidth, parent.getSize().width);
        preferredHeight = Math.max(minHeight, parent.getSize().height);

        parent.setMaximumSize(new Dimension(minWidth, minHeight));
        parent.setMinimumSize(new Dimension(minWidth, minHeight));
        parent.setPreferredSize(new Dimension(minWidth, minHeight));

        parent.setSize(new Dimension(preferredWidth, preferredHeight));
//        System.out.println(
//        ">> FINAL: X: " + preferredWidth + " Y: " + preferredHeight);

//        System.out.println("LM-SIZE: " + parent.getPreferredSize());
    }


    /* Required by LayoutManager. */
    @Override
    public Dimension preferredLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);
        int nComps = parent.getComponentCount();

        setSizes(parent);

//        Always add the container's insets!
//        Insets insets = parent.getInsets();
//        dim.width = preferredWidth + insets.left + insets.right;
//        dim.height = preferredHeight + insets.top + insets.bottom;

        dim.width = preferredWidth;
        dim.height = preferredHeight;

        sizeUnknown = false;

        return dim;
    }

    /* Required by LayoutManager. */
    @Override
    public Dimension minimumLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);
        int nComps = parent.getComponentCount();

        //Always add the container's insets!
//        Insets insets = parent.getInsets();
//        dim.width = minWidth + insets.left + insets.right;
//        dim.height = minHeight + insets.top + insets.bottom;

        dim.width = minWidth;
        dim.height = minHeight;

        sizeUnknown = false;

        return dim;
    }

    /* Required by LayoutManager. */
    /*
     * This is called when the panel is first displayed,
     * and every time its size changes.
     * Note: You CAN'T assume preferredLayoutSize or
     * minimumLayoutSize will be called -- in the case
     * of applets, at least, they probably won't be.
     */
    @Override
    public void layoutContainer(Container parent) {

        // Go through the components' sizes, if neither
        // preferredLayoutSize nor minimumLayoutSize has
        // been called.
//        if (sizeUnknown) {
//            setSizes(parent);
//        }
        setSizes(parent);


        // set window position (don't allow negative positions)
        int nComps = parent.getComponentCount();

        for (int i = 0; i < nComps; i++) {
            Component c = parent.getComponent(i);

            // check that windows are always inside canvas bounds
            if (c instanceof CanvasWindow) {
                CanvasWindow w = (CanvasWindow) c;

                Point loc = w.getLocation();
                loc.x = Math.max(loc.x, 0 - w.getInsets().left);
                loc.y = Math.max(loc.y, 0 - w.getInsets().top);

                w.setLocation(loc);
            }
        }
    }

    @Override
    public String toString() {
        String str = "";
        return getClass().getName() + "[vgap=" + 0 + str + "]";
    }
}
