/* 
 * ColLayout.java
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
 * Third, add an additional notice, stating that you modified VRL. In addition
 * you must cite the publications listed below. A suitable notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Publications:
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, 2011, in press.
 */

package eu.mihosoft.vrl.visual;

import java.awt.*;

/**
 * This LayoutManager adds each component in it's own row.
 * 
 * This LayoutManager is based on the DiagonalLayout that can be found here:
 * http://java.sun.com/docs/books/tutorial/uiswing/layout/custom.html
 * (but DiagonalLayout produces division by zero exceptions and is an example
 * only)
 * 
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ColLayout implements LayoutManager {

    private int vgap;
    private int minWidth = 0,  minHeight = 0;
    private int preferredWidth = 0,  preferredHeight = 0;
    private boolean sizeUnknown = true;
    private int leftMargin = 0;
    private int rightMargin = 0;
    private int topMargin = 0;
    private int bottomMargin = 0;
    private boolean equalWidth = false;
    private boolean debug = false;

    /**
     * Constructor.
     */
    public ColLayout() {
        this(10);
    }

    /**
     * Constructor.
     * @param v the vetrical gap
     */
    public ColLayout(int v) {
        vgap = v;

        setMargins(0, 0, 0, 0);
    }

    /* Required by LayoutManager. */
    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    /* Required by LayoutManager. */
    @Override
    public void removeLayoutComponent(Component comp) {
    }

    /**
     * Defines the sizes of the child components managed by this layout.
     * @param parent the parent container
     */
    private void setSizes(Container parent) {
        int nComps = parent.getComponentCount();
        Dimension d = null;

        //Reset preferred/minimum width and height.

        minWidth = parent.getWidth();
        minHeight = 0;

        for (int i = 0; i < nComps; i++) {
            Component c = parent.getComponent(i);
            if (c.isVisible()) {
                d = c.getPreferredSize();

                if (i >= 0) {
                    minHeight += vgap;
                }
                minHeight += d.height;

                minWidth = Math.max(c.getMinimumSize().width,
                        minWidth);
            }
        }

        minWidth += getLeftMargin() + getRightMargin() +
                parent.getInsets().left + parent.getInsets().right;
        minHeight += getTopMargin() + getBottomMargin() +
                parent.getInsets().top + parent.getInsets().bottom;

        preferredWidth = minWidth;
        preferredHeight = minHeight;

        if (isDebug()) {
            System.out.println("ParentComponent Size   X: " + minWidth);
            System.out.println("ParentComponent Size   Y: " + minHeight);
        }

        parent.setMaximumSize(new Dimension(minWidth, minHeight));
        parent.setMinimumSize(new Dimension(minWidth, minHeight));
        parent.setPreferredSize(new Dimension(minWidth, minHeight));
    }


    /* Required by LayoutManager. */
    @Override
    public Dimension preferredLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);
        int nComps = parent.getComponentCount();

        setSizes(parent);

        //Always add the container's insets!
        Insets insets = parent.getInsets();
        dim.width = preferredWidth + insets.left + insets.right;
        dim.height = preferredHeight + insets.top + insets.bottom;

        sizeUnknown = false;

        return dim;
    }

    /* Required by LayoutManager. */
    @Override
    public Dimension minimumLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);
        int nComps = parent.getComponentCount();

        //Always add the container's insets!
        Insets insets = parent.getInsets();
        dim.width = minWidth + insets.left + insets.right;
        dim.height = minHeight + insets.top + insets.bottom;

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
        Insets insets = parent.getInsets();
        int maxWidth = parent.getWidth() - (insets.left + insets.right) - (getLeftMargin() + getRightMargin());
        int maxHeight = parent.getHeight() - (insets.top + insets.bottom);
        int nComps = parent.getComponentCount();
        int previousWidth = 0, previousHeight = 0;
        int x = insets.left + getLeftMargin(), y = insets.top + getTopMargin();
        int rowh = 0, start = 0;
        int xFudge = 0, yFudge = 0;
        boolean oneColumn = false;

        // Go through the components' sizes, if neither
        // preferredLayoutSize nor minimumLayoutSize has
        // been called.
        if (sizeUnknown) {
            setSizes(parent);
        }

        if (maxWidth <= minWidth) {
            oneColumn = true;
        }

        if (maxWidth != preferredWidth) {
            // xFudge = (maxWidth - preferredWidth)/(nComps - 1);

            // as we don't want to modify x there is no need to set xFudge
            // to values other than zero
            xFudge = 0;
        }

        if (maxHeight > preferredHeight) {
            if (nComps - 1 > 0) {
                yFudge = (maxHeight - preferredHeight) / (nComps - 1);
            } else {
                yFudge = 0;
            }
        }

        for (int i = 0; i < nComps; i++) {
            Component c = parent.getComponent(i);
            if (c.isVisible()) {
                Dimension d = c.getPreferredSize();

                // increase x and y, if appropriate
                if (i > 0) {
                    if (!oneColumn) {
                        // here one may change x but we don't want that
                        //x += previousWidth/2 + xFudge;
                    }
                    y += previousHeight + vgap + yFudge;
                }

                // If x is too large,
                if ((!oneColumn) &&
                        (x + d.width) >
                        (parent.getWidth() - insets.right - getLeftMargin() - -getRightMargin())) {
                    // reduce x to a reasonable number.
                    x = parent.getWidth() - insets.bottom - d.width - getLeftMargin() - -getRightMargin();
                }

                // If y is too large,
                if ((y + d.height) > (parent.getHeight() - insets.bottom)) {
                    // do nothing.
                    // Another choice would be to do what we do to x.
                }

                // Set the component's size and position.
                c.setBounds(x, y, d.width, d.height);

                previousWidth = d.width;
                previousHeight = d.height;

            }
        }

        if (isEqualWidth()) {
            for (Component c : parent.getComponents()) {
                c.setSize(maxWidth, c.getPreferredSize().height);

                if (isDebug()) {
                    System.out.println("ChildComponent Size   X: " +
                            maxWidth);
                    System.out.println("ChildComponent Size   Y: " +
                            c.getPreferredSize().height);
                }
            }
        }
    }

    /**
     * Defines the margins.
     * @param top top margin
     * @param left left margin
     * @param bottom bottom margin
     * @param right right margin
     */
    public void setMargins(int top, int left, int bottom, int right) {
        setLeftMargin(left);
        setRightMargin(right);
        setTopMargin(top);
        setBottomMargin(bottom);
    }

    /**
     * Returns the left margin.
     * @return the left margin
     */
    public int getLeftMargin() {
        return leftMargin;
    }

    /**
     * Defines the left margin.
     * @param leftMargin the margin to set
     */
    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }

    /**
     * Returns the right margin.
     * @return the right margin
     */
    public int getRightMargin() {
        return rightMargin;
    }

    /**
     * Defines the right margin.
     * @param rightMargin the margin to set
     */
    public void setRightMargin(int rightMargin) {
        this.rightMargin = rightMargin;
    }

    /**
     * Returns the top margin
     * @return the top margin
     */
    public int getTopMargin() {
        return topMargin;
    }

    /**
     * Defines the top margin.
     * @param topMargin the margin to set
     */
    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }

    /**
     * Returns the bottom margin.
     * @return the bottom margin
     */
    public int getBottomMargin() {
        return bottomMargin;
    }

    /**
     * Defines the bottom margin
     * @param bottomMargin the margin to set
     */
    public void setBottomMargin(int bottomMargin) {
        this.bottomMargin = bottomMargin;
    }

    @Override
    public String toString() {
        String str = "";
        return getClass().getName() + "[vgap=" + vgap + str + "]";
    }

    /**
     * Indicates whether the layout sets equal child widths.
     * @return <code>true</code> if this layout sets equal child widths;
     *         <code>false</code> otherwise
     */
    public boolean isEqualWidth() {
        return equalWidth;
    }

    /**
     * Defines whether to set equal child widths.
     * @param equalWidth the state to set
     */
    public void setEqualWidth(boolean equalWidth) {
        this.equalWidth = equalWidth;
    }

    /**
     * Defines if this layout is in debug state.
     * @return <code>true</code> if this layout is in debug mode;
     *         <code>false</code> otherwise
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Defines whether this layout is in debug mode.
     * @param debug the debug state to set
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
