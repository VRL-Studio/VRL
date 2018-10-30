/* 
 * VRTextScrollPane.java
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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VRTextScrollPane extends RTextScrollPane implements StyleChangedListener{
    
    private int maxWidth = 680;
    private int maxHeight = 800;

//    MouseWheelListener[] mouseWheelListeners;
    public VRTextScrollPane() {
        init();
    }

    public VRTextScrollPane(RTextArea textArea) {
        super(textArea);
        init();
    }

    public VRTextScrollPane(RTextArea textArea, boolean lineNumbers) {
        super(textArea, lineNumbers);
        init();
    }

    public VRTextScrollPane(
            RTextArea area, boolean lineNumbers, Color lineNumberColor) {
        super(area, lineNumbers, lineNumberColor);
        init();
    }
    
//    /**
//     * Computes max default width (120 characters) depending on current font.
//     */
//    public void computeMaxDefaultWidth() {
//        
//        try {
//        
//        // compute width (120 characters)
//        StringBuilder builder = new StringBuilder();
//   
//        for(int i = 0; i < 120;i++) {
//            builder.append("A");
//        }
//        
//        // compute width
//        maxWidth = getTextArea().getGraphics().getFontMetrics().stringWidth(
//                builder.toString());
//        } catch (NullPointerException ex) {
//            // not initialized
//        } 
//    }

    private void init() {
        
        // component init
        setOpaque(false);
        getViewport().setBackground(VSwingUtil.TRANSPARENT_COLOR);
        setBackground(VSwingUtil.TRANSPARENT_COLOR);
        getViewport().setOpaque(false);
        setViewportBorder(new EmptyBorder(0, 0, 0, 0));
        setBorder(new EmptyBorder(0, 0, 0, 0));
        
        getGutter().setOpaque(true);
        getGutter().setBackground(new Color(0, 0, 0, 0));

        addMouseWheelListener(new PDMouseWheelListener());
        
        getTextArea().addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {

                getTextArea().setPreferredSize(null);
                getTextArea().revalidate();
                VRTextScrollPane.this.setPreferredSize(null);
                VRTextScrollPane.this.revalidate();
                
                Container parentWindow =
                        VSwingUtil.getParent(
                        VRTextScrollPane.this, CanvasWindow.class);
                
                if (parentWindow!=null) {
                    CanvasWindow w = (CanvasWindow) parentWindow;
                    w.setPreferredSize(null);
                    w.revalidate();
                }
            }
        });
        
        getTextArea().getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                getTextArea().setPreferredSize(null);
                getTextArea().revalidate();
                VRTextScrollPane.this.setPreferredSize(null);
                VRTextScrollPane.this.revalidate();
                
                Container parentWindow =
                        VSwingUtil.getParent(
                        VRTextScrollPane.this, CanvasWindow.class);
                
                if (parentWindow!=null) {
                    CanvasWindow w = (CanvasWindow) parentWindow;
                    w.setPreferredSize(null);
                    w.revalidate();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                getTextArea().setPreferredSize(null);
                getTextArea().revalidate();
                VRTextScrollPane.this.setPreferredSize(null);
                VRTextScrollPane.this.revalidate();
                
                Container parentWindow =
                        VSwingUtil.getParent(
                        VRTextScrollPane.this, CanvasWindow.class);
                
                if (parentWindow!=null) {
                    CanvasWindow w = (CanvasWindow) parentWindow;
                    w.setPreferredSize(null);
                    w.revalidate();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                getTextArea().setPreferredSize(null);
                getTextArea().revalidate();
                VRTextScrollPane.this.setPreferredSize(null);
                VRTextScrollPane.this.revalidate();
                
                Container parentWindow =
                        VSwingUtil.getParent(
                        VRTextScrollPane.this, CanvasWindow.class);
                
                if (parentWindow!=null) {
                    CanvasWindow w = (CanvasWindow) parentWindow;
                    w.setPreferredSize(null);
                    w.revalidate();
                }
            }
        });
        
        getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {

            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                VSwingUtil.repaintRequest((JComponent)getParent());
            }
        });
        getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                VSwingUtil.repaintRequest((JComponent)getParent());
            }
        });
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
        
        int w = super.getPreferredSize().width+wPlus;
        int h = super.getPreferredSize().height+hPlus;

        if (w < maxWidth && h < maxHeight) {
            return new Dimension(w,h);
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

    @Override
    public void styleChanged(Style style) {
        
        //getGutter().setBackground(style.getBaseValues().getColor(VCodeEditor.LINE_NUMBER_FIELD_COLOR_KEY));
        getGutter().setLineNumberColor(style.getBaseValues().getColor(VCodeEditor.LINE_NUMBER_COLOR_KEY));
        getGutter().setBorderColor(style.getBaseValues().getColor(VCodeEditor.BORDER_COLOR_KEY));
    }

    // idea for custom listener from:
    // http://stackoverflow.com/questions/1377887/jtextpane-prevents-scrolling-in-the-parent-jscrollpane
    class PDMouseWheelListener implements MouseWheelListener {

        private JScrollBar bar;
        private int previousValue = 0;
        private JScrollPane parentScrollPane;

        private JScrollPane getParentScrollPane() {
            if (parentScrollPane == null) {
                Component parent = getParent();
                while (!(parent instanceof JScrollPane) && parent != null) {
                    parent = parent.getParent();
                }
                parentScrollPane = (JScrollPane) parent;
            }
            return parentScrollPane;
        }

        public PDMouseWheelListener() {
            bar = VRTextScrollPane.this.getVerticalScrollBar();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            JScrollPane parent = getParentScrollPane();
            if (parent != null) {
                /*
                 * Only dispatch if we have reached top/bottom on previous
                 * scroll
                 */
                if (e.getWheelRotation() < 0) {
                    if (bar.getValue() == 0 && previousValue == 0) {
                        parent.dispatchEvent(cloneEvent(e));
                    }
                } else {
                    if (bar.getValue() == getMax() && previousValue == getMax()) {
                        parent.dispatchEvent(cloneEvent(e));
                    }
                }
                previousValue = bar.getValue();
            } /*
             * If parent scrollpane doesn't exist, remove this as a listener. We
             * have to defer this till now (vs doing it in constructor) because
             * in the constructor this item has no parent yet.
             */ else {
                VRTextScrollPane.this.removeMouseWheelListener(this);
            }
        }

        private int getMax() {
            return bar.getMaximum() - bar.getVisibleAmount();
        }

        private MouseWheelEvent cloneEvent(MouseWheelEvent e) {
            return new MouseWheelEvent(getParentScrollPane(),
                    e.getID(),
                    e.getWhen(),
                    e.getModifiers(), 1, 1,
                    e.getClickCount(), false, e.getScrollType(),
                    e.getScrollAmount(), e.getWheelRotation());
        }
    }
}
