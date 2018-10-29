/* 
 * VComboBox.java
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 * Advanced combobox with custom renderer that supports tooltips and improved
 * layout management (uses code/ideas from the links below).
 * @see http://tutiez.com/?p=171
 * @see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4743225
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VComboBox extends JComboBox {

    private boolean layingOut = false;
    private int widestLengh = 0;
    private boolean wide = true;
    private TooltipComboBoxRenderer boxRenderer;

    public VComboBox() {
        init();
    }

    @SuppressWarnings("unchecked") // we must be compatible with 1.6
    public VComboBox(final Object items[]) {
        super(items);
        init();
    }

    @SuppressWarnings("unchecked") // we must be compatible with 1.6
    public VComboBox(Vector items) {
        super(items);
        init();
    }

    @SuppressWarnings("unchecked") // we must be compatible with 1.6
    public VComboBox(ComboBoxModel aModel) {
        super(aModel);
        init();
    }

    @SuppressWarnings("unchecked") // we must be compatible with 1.6
    private void init() {
        boxRenderer = new TooltipComboBoxRenderer(this);
        setRenderer(boxRenderer);
    }

    public void setToolTip(Object o, String s) {
        boxRenderer.addToolTip(o, s);
    }

    public void removeToolTip(Object o) {
        boxRenderer.removeToolTip(o);
    }

    public void removeToolTips(Collection<?> all) {
        boxRenderer.removeToolTips(all);
    }

    @Override
    @SuppressWarnings("unchecked") // we must be compatible with 1.6
    public void addItem(Object o) {
        super.addItem(o);
        setWide(wide); // update width
    }

    @SuppressWarnings("unchecked") // we must be compatible with 1.6
    public void addItem(Object o, String tooltip) {
        super.addItem(o);
        setToolTip(o, tooltip);
        setWide(wide); // update width
    }

    @Override
    public void removeItem(Object o) {
        super.removeItem(o);
        removeToolTip(o);
        setWide(wide); // update width
    }

    @Override
    public void removeItemAt(int i) {
        Object o = getItemAt(i);
        removeToolTip(o);
        super.removeItemAt(i);
        setWide(wide); // update width
    }

    private boolean isWide() {
        return wide;
    }

    private void setWide(boolean wide) {
        this.wide = wide;
        widestLengh = getWidestItemWidth();
    }

    @Override
    public Dimension getSize() {
        Dimension dim = super.getSize();
        if (!layingOut && isWide()) {
            dim.width = Math.max(widestLengh, dim.width);
        }
        return dim;
    }

    private int getWidestItemWidth() {

        int numOfItems = this.getItemCount();
        Font font = this.getFont();
        FontMetrics metrics = this.getFontMetrics(font);
        int widest = 0;
        for (int i = 0; i < numOfItems; i++) {
            Object item = this.getItemAt(i);
            int lineWidth = metrics.stringWidth(
                    Message.removeHTML(item.toString()));
            widest = Math.max(widest, lineWidth);
        }

        // +60 because we use html and may need some extra space for bold etc.
        return widest + 60;
    }

    @Override
    public void doLayout() {
        try {
            layingOut = true;
            super.doLayout();
        } finally {
            layingOut = false;
        }
    }
}

class TooltipComboBoxRenderer extends BasicComboBoxRenderer {

    private Map<Object, String> tooltips =
            new HashMap<Object, String>();
    private VComboBox parent;

    public TooltipComboBoxRenderer(VComboBox parent) {
        this.parent = parent;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {

        String text = (value == null) ? "" : value.toString();

        if (value == parent.getSelectedItem()) {
            text = Message.removeHTML(text);
        }

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
            if (-1 < index) {
                list.setToolTipText(tooltips.get(value));
            }

        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setFont(list.getFont());
        setText(text);
        return this;
    }

    public void addToolTip(Object o, String s) {
        tooltips.put(o, s);
    }

    public void removeToolTip(Object o) {
        tooltips.remove(o);
    }

    public void removeToolTips(Collection<?> all) {
        for (Object o : all) {
            tooltips.remove(o);
        }
    }
}
