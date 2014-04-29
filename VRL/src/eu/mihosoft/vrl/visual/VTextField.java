/* 
 * VTextField.java
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

import eu.mihosoft.vrl.animation.Animation;
import eu.mihosoft.vrl.system.VSysUtil;
import eu.mihosoft.vrl.visual.Canvas;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import eu.mihosoft.vrl.visual.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;

/**
 * VRL version of JTextField. It provides the same functionality as JTextField.
 * The only difference is that it has a custom visual appearance.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VTextField extends JTextField implements CanvasChild {

    private static final long serialVersionUID = 1377692648356732302L;
    private VComponent vParent;
    transient private BufferedImage buffer;
    private Animation animation1;
    private Animation animation2;
    private boolean invalidState = false;
    private Color invalidStateColor = Color.RED;
    private Style style;
    public static final String TEXT_FIELD_COLOR_KEY = "TextField:Color";

    public VTextField() {
        setBorder(new EmptyBorder(3, 3, 3, 3));

        setOpaque(false);
        
        initialize();
    }

    
    
    /**
     * Constructor.
     *
     * @param typeRepresentation the type representation parent
     */
    public VTextField(VComponent vParent) {
        this.vParent = vParent;

        setBorder(new EmptyBorder(3, 3, 3, 3));

        setOpaque(false);
        
        initialize();
    }

    /**
     * Constructor.
     *
     * @param typeRepresentation the type representation parent
     * @param s the text to show
     */
    public VTextField(VComponent vParent, String s) {
        super(s);
        this.vParent = vParent;

        setBorder(new EmptyBorder(3, 3, 3, 3));

        setOpaque(false);
        
        initialize();
    }

    public VTextField(String s) {
        super(s);

        setBorder(new EmptyBorder(3, 3, 3, 3));

        setOpaque(false);
        
        initialize();
    }

    public VTextField(int i) {
        super(i);

        setBorder(new EmptyBorder(3, 3, 3, 3));

        setOpaque(false);
        
        initialize();
    }

    public VTextField(String string, int i) {
        super(string, i);

        setBorder(new EmptyBorder(3, 3, 3, 3));

        setOpaque(false);
        
        initialize();
    }

    private void initialize() {
        // add shortcuts
        if (VSysUtil.isMacOSX()) {

            InputMap keyMap = getInputMap();

            KeyStroke keyC = KeyStroke.getKeyStroke(KeyEvent.VK_C,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
            KeyStroke keyV = KeyStroke.getKeyStroke(KeyEvent.VK_V,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
            KeyStroke keyX = KeyStroke.getKeyStroke(KeyEvent.VK_X,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
            KeyStroke keyA = KeyStroke.getKeyStroke(KeyEvent.VK_A,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());


            keyMap.put(keyC, DefaultEditorKit.copyAction);
            keyMap.put(keyV, DefaultEditorKit.pasteAction);
            keyMap.put(keyX, DefaultEditorKit.cutAction);
            keyMap.put(keyA, DefaultEditorKit.selectAllAction);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Style newStyle = null;

        if (vParent == null) {
            vParent = (VComponent) VSwingUtil.getParent(this, VComponent.class);
        }

        if (vParent != null) {

            newStyle = vParent.getStyle();
        }

        if (newStyle != null) {

            if (style == null || !style.equals(newStyle)) {
                style = newStyle;
                setCaretColor(style.getBaseValues().getColor(
                        Canvas.TEXT_COLOR_KEY));
                setForeground(style.getBaseValues().getColor(
                        Canvas.TEXT_COLOR_KEY));
                setSelectedTextColor(style.getBaseValues().getColor(
                        Canvas.SELECTED_TEXT_COLOR_KEY));
                setSelectionColor(style.getBaseValues().getColor(
                        Canvas.TEXT_SELECTION_COLOR_KEY));
            }
        }

//        Color originalBackground = getBackground();



        if (isInvalidState()) {
            g2.setPaint(getInvalidStateColor());
        } else {
            g2.setPaint(style.getBaseValues().getColor(TEXT_FIELD_COLOR_KEY));
        }

        Composite original = g2.getComposite();

        AlphaComposite ac1 =
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                0.6f);
        g2.setComposite(ac1);

        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

        g2.setComposite(original);

        Color border =
                style.getBaseValues().getColor(CanvasWindow.BORDER_COLOR_KEY);

        g2.setColor(border);

        BasicStroke stroke =
                new BasicStroke(style.getBaseValues().getFloat(
                CanvasWindow.BORDER_THICKNESS_KEY));

        g2.setStroke(stroke);

        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
        setBackground(VSwingUtil.TRANSPARENT_COLOR);

        g2.setComposite(original);

        super.paintComponent(g);
    }

//    /**
//     * Returns the parent type representation.
//     * @return the parent type representation if such a type representation
//     *         exists;<code>false</code> otherwise
//     */
//    public final TypeRepresentationBase getTypeRepresentation() {
//        return typeRepresentation;
//    }
//    /**
//     * DEfines the type representation parent.
//     *
//     * @param typeRepresentation the type representation to set
//     */
//    public final void setTypeRepresentation(
//            TypeRepresentationBase typeRepresentation) {
//        this.typeRepresentation = typeRepresentation;
//    }
    /**
     * Returns the color used to indicate an invalid state.
     *
     * @return the color used to indicate an invalid state
     */
    public Color getInvalidStateColor() {
        return invalidStateColor;
    }

    /**
     * Defines the color used to indicate an invalid state.
     *
     * @param invalidStateColor the color to set
     */
    public void setInvalidStateColor(Color invalidStateColor) {
        this.invalidStateColor = invalidStateColor;
    }

    /**
     * Indicates if the state of this component is invalid.
     *
     * @return
     * <code>true</code> if the state of this component is invalid;
     * <code>false</code> otherwise
     */
    public boolean isInvalidState() {
        return invalidState;
    }

    /**
     * Defines whether this component is in an invalid state.
     *
     * @param invalidState the state to set
     */
    public void setInvalidState(boolean invalidState) {
        this.invalidState = invalidState;
    }

    @Override
    public Canvas getMainCanvas() {
        return vParent.getMainCanvas();
    }

    /**
     * <b>Warning: </b> this method does nothing. The maincanvas of the
     * specified vcomponent is used instead.
     *
     * @param mainCanvas canvas to set
     */
    @Override
    public void setMainCanvas(Canvas mainCanvas) {
        //
    }
}
