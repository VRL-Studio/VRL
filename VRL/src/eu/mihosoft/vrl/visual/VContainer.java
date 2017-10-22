/* 
 * VContainer.java
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

import eu.mihosoft.vrl.visual.TransparentPanel;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * VRL conainer is a transparent Swing container that uses horizontal box
 * layout.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VContainer extends TransparentPanel {

    private static final long serialVersionUID = 6158340280993920880L;
    private Integer minPreferredWidth;
    private Integer minPreferredHeight;
    private Integer maxPreferredWidth;
    private Integer maxPreferredHeight;
    private Integer maxWidth;
    private Integer maxHeight;

    /**
     * Constructor.
     */
    public VContainer() {
        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.X_AXIS);
        setLayout(layout);
    }

    /**
     * Constructor.
     *
     * @param c the component that is to be added
     */
    public VContainer(Component c) {
        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.X_AXIS);
        setLayout(layout);
        add(c);
    }

    /**
     * Returns the minimum preferred width of this container.
     *
     * @return the minimum preferred width of this container
     */
    public Integer getMinPreferredWidth() {
        return minPreferredWidth;
    }

    /**
     * Defines the minimum preferred width of this container.
     *
     * @param minPreferredWidth the value to set
     */
    public void setMinPreferredWidth(Integer minPreferredWidth) {
        this.minPreferredWidth = minPreferredWidth;
    }

    /**
     * <p> Computes the preferred size as defined in the super class except if
     * custom preferred width or height is defined. </p> <p> The definition of
     * minimum preferred values is necessary if minimum values shall be defined
     * without breaking automatic layout revalidation for layout manager that
     * ignore minimum and maximum size definitions. </p>
     *
     * @return the preferred size of this container
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension result = null;

        boolean customValuesDefined =
                minPreferredWidth != null || minPreferredHeight != null
                || maxPreferredWidth != null || maxPreferredHeight != null;

        if (customValuesDefined) {
            if (super.getPreferredSize() != null) {
                int w = super.getPreferredSize().width;
                int h = super.getPreferredSize().height;

                if (minPreferredWidth != null) {
                    w = Math.max(w, minPreferredWidth);
                }

                if (minPreferredHeight != null) {
                    h = Math.max(h, minPreferredHeight);
                }

                if (maxPreferredWidth != null) {
                    w = Math.min(w, maxPreferredWidth);
                }

                if (maxPreferredHeight != null) {
                    h = Math.min(h, maxPreferredHeight);
                }

                result = new Dimension(w, h);
            }
        } else {
            result = super.getPreferredSize();
        }

        return result;
    }

    /**
     * <p> Computes the maximum size as defined in the super class except if
     * custom max width or height is defined. </p> <p> The definition of maximum
     * values is necessary if maximum values shall be defined without breaking
     * automatic layout revalidation for layout manager that ignore minimum and
     * maximum size definitions. </p>
     *
     * @return the preferred size of this container
     */
    @Override
    public Dimension getMaximumSize() {
        Dimension result = null;

        boolean customValuesDefined =
                maxWidth != null || maxHeight != null;

        if (customValuesDefined) {
            if (super.getMaximumSize() != null) {
                int w = super.getMaximumSize().width;
                int h = super.getMaximumSize().height;

                if (w == 0 && maxWidth != null) {
                    w = maxWidth;
                }

                if (h == 0 && maxHeight != null) {
                    h = maxHeight;
                }

                if (maxWidth != null) {
                    w = Math.min(w, maxWidth);
                }

                if (maxHeight != null) {
                    h = Math.min(h, maxHeight);
                }
                
                if (w == 0 ) {
                    w = Short.MAX_VALUE;
                }
                
                if (h == 0 ) {
                    h = Short.MAX_VALUE;
                }

                result = new Dimension(w, h);
                System.out.println("RESULT: " + w + "x" + h);
            }
        } else {
            result = super.getMaximumSize();
        }

        return result;
    }

    /**
     * Returns the minimum preferred height of this container.
     *
     * @return the the minimum preferred height of this container
     */
    public Integer getMinPreferredHeight() {
        return minPreferredHeight;
    }

    /**
     * Defines the minimum preferred height of this container.
     *
     * @param minPreferredHeight the value to set
     */
    public void setMinPreferredHeight(Integer minPreferredHeight) {
        this.minPreferredHeight = minPreferredHeight;
    }

    /**
     * Returns the maximum preferred width of this container. the the maximum
     * preferred width of this container
     */
    public Integer getMaxPreferredWidth() {
        return maxPreferredWidth;
    }

    /**
     * Defines the maximum preferred width of this container.
     *
     * @param maxPreferredWidth the value to set
     */
    public void setMaxPreferredWidth(Integer maxPreferredWidth) {
        this.maxPreferredWidth = maxPreferredWidth;
    }

    /**
     * Returns the maximum preferred height of this container.
     *
     * @return the the maximum preferred height of this container
     */
    public Integer getMaxPreferredHeight() {
        return maxPreferredHeight;
    }

    /**
     * Defines the maximum preferred height of this container.
     *
     * @param maxPreferredHeight the value to set
     */
    public void setMaxPreferredHeight(Integer maxPreferredHeight) {
        this.maxPreferredHeight = maxPreferredHeight;
    }

    /**
     * @return the maxWidth
     */
    public Integer getMaxWidth() {
        return maxWidth;
    }

    /**
     * @param maxWidth the maxWidth to set
     */
    public void setMaxWidth(Integer maxWidth) {
        this.maxWidth = maxWidth;
    }

    /**
     * @return the maxHeight
     */
    public Integer getMaxHeight() {
        return maxHeight;
    }

    /**
     * @param maxHeight the maxHeight to set
     */
    public void setMaxHeight(Integer maxHeight) {
        this.maxHeight = maxHeight;
    }
}
