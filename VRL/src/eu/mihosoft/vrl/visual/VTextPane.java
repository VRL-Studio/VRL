/* 
 * VTextPane.java
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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Position;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import javax.swing.undo.UndoManager;

/**
 * VText pane has the same functionality as JTextPane. The only difference is
 * the visual appearance.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VTextPane extends JTextPane {

    private static final long serialVersionUID = -322820662027906541L;
    private Color background;

    /**
     * Constructor.
     */
    public VTextPane() {
        setBackground(VSwingUtil.TRANSPARENT_COLOR);
        setBorder(new EmptyBorder(3, 3, 3, 5));
        super.setOpaque(false);
//        setTabs(4);
        
        setFont(new Font("SansSerif", Font.PLAIN, 11));
    }

    /**
     * Clears the document of this text pane.
     */
    public void clear() {
        try {
            getDocument().remove(0, getDocument().getLength());
        } catch (BadLocationException ex) {
            Logger.getLogger(VTextPane.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
//    public void setTabs(int charactersPerTab) {
//        FontMetrics fm = this.getFontMetrics(this.getFont());
//        int charWidth = fm.charWidth('w');
//        int tabWidth = charWidth * charactersPerTab;
//
//        TabStop[] tabs = new TabStop[10];
//
//        for (int j = 0; j < tabs.length; j++) {
//            int tab = j + 1;
//            tabs[j] = new TabStop(tab * tabWidth);
//        }
//
//        TabSet tabSet = new TabSet(tabs);
//        SimpleAttributeSet attributes = new SimpleAttributeSet();
//        StyleConstants.setFontFamily(attributes, this.getFont().getFamily());
//        StyleConstants.setTabSet(attributes, tabSet);
//        int length = this.getDocument().getLength();
//        this.getStyledDocument().setParagraphAttributes(
//                0, length, attributes, true);
//    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getBackgroundColor() != null) {
            g.setColor(getBackgroundColor());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        super.paintComponent(g);
    }

    /**
     * This component must never be opaque. This method is completely ignored.
     * Te general problem is that many L&F implementations ignore custom
     * background.
     * @param v state to set
     */
    @Override
    public final void setOpaque(boolean v) {
        //
    }

    /**
     * 
     * @return the background
     */
    public Color getBackgroundColor() {
        return background;
    }

    /**
     * @param background the background to set
     */
    public void setBackgroundColor(Color background) {
        this.background = background;
    }
    
     /**
     *  At time of writing this method does not work as expected (Java 1.6.x).
     * Therefore use {@link #getBackgroundColor()} instead
     * @return the background
     */
    @Override
    public final Color getBackground() {
        return super.getBackground();
    }

    /**
     * At time of writing this method does not work as expected (Java 1.6.x).
     * Therefore use {@link #setBackgroundColor()} instead
     * @param background the background to set
     */
    @Override
    public final void setBackground(Color background) {
        super.setBackground(background);
    }
    
        /**
     * Sets the default text color of this text pane. Its functionality is
     * partly equivalent to the <code>setForeGeound()</code> method.
     * @param c the color to set
     */
    public void setDefaultTextColor(Color c) {
//        setForeground(c);
        Font font = this.getFont();
        // Start with the current input attributes for the JTextPane. This
        // should ensure that we do not wipe out any existing attributes
        // (such as alignment or other paragraph attributes) currently
        // set on the text area.
        MutableAttributeSet attrs = this.getInputAttributes();

        // Set the font family, size, and style, based on properties of
        // the Font object. Note that JTextPane supports a number of
        // character attributes beyond those supported by the Font class.
        // For example, underline, strike-through, super- and sub-script.
        StyleConstants.setFontFamily(attrs, font.getFamily());
        StyleConstants.setFontSize(attrs, font.getSize());
        StyleConstants.setItalic(attrs, (font.getStyle() & Font.ITALIC) != 0);
        StyleConstants.setBold(attrs, (font.getStyle() & Font.BOLD) != 0);

        // Set the font color
        StyleConstants.setForeground(attrs, c);

        // Retrieve the pane's document object
        StyledDocument doc =  getStyledDocument();

        // Replace the style for the entire document. We exceed the length
        // of the document by 1 so that text entered at the end of the
        // document uses the attributes.
        doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
    }
}
