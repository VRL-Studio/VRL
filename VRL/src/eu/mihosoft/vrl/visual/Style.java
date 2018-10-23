/* 
 * Style.java
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

import eu.mihosoft.vrl.reflection.ControlFlowConnection;
import eu.mihosoft.vrl.reflection.DefaultMethodRepresentation;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.types.PlotPane;
import eu.mihosoft.vrl.types.VCanvas3D;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.text.StyleContext;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public final class Style {

    private StyleValueController baseValues;
    private StyleValueController lookAndFeelValues;
    private StyleValueController customValues;
    private String name;

    public Style() {
        setBaseValues(new StyleValueController());
        setLookAndFeelValues(new StyleValueController());
        setCustomValues(new StyleValueController());
        setName(name);

        init(true);
    }

    public Style(String name) {
        setBaseValues(new StyleValueController());
        setLookAndFeelValues(new StyleValueController());
        setCustomValues(new StyleValueController());
        setName(name);

        init(true);
    }

    Style(String name, boolean initSubStyles) {
        setBaseValues(new StyleValueController());
        setLookAndFeelValues(new StyleValueController());
        setCustomValues(new StyleValueController());
        setName(name);

        init(initSubStyles);
    }

    private void init(boolean initSubStyles) {
        getBaseValues().set(
                Canvas.BACKGROUND_COLOR_KEY, new Color(98, 107, 129));
        getBaseValues().set(CanvasGrid.GRID_COLOR_KEY, new Color(154, 158, 166));
        getBaseValues().set(CanvasGrid.ENABLE_GRID_KEY, true);
        getBaseValues().set(Canvas.TEXT_COLOR_KEY, new Color(0, 0, 0));
        getBaseValues().set(Canvas.SELECTED_TEXT_COLOR_KEY, Color.BLACK);
        getBaseValues().set(Canvas.CARET_COLOR_KEY, Color.BLACK);
        getBaseValues().set(Canvas.TEXT_SELECTION_COLOR_KEY,
                new Color(130, 140, 165, 140));
        getBaseValues().set(BackgroundImage.IMAGE_KEY, null);
        getBaseValues().set(BackgroundImage.IMAGE_TRANSPARENCY_KEY, 0.f);

        getBaseValues().set(Dock.DOCK_HEIGHT_KEY, 40);

        getBaseValues().set(MessageBox.BOX_COLOR_KEY, Color.BLACK);
        getBaseValues().set(MessageBox.TOP_TRANSPARENCY_KEY, 0.8f);
        getBaseValues().set(MessageBox.BOTTOM_TRANSPARENCY_KEY, 0.4f);
        getBaseValues().set(MessageBox.TEXT_COLOR_KEY, Color.WHITE);
        getBaseValues().set(MessageBox.ICON_COLOR_KEY, new Color(200, 200, 200));
        getBaseValues().set(MessageBox.ACTIVE_ICON_COLOR_KEY, Color.WHITE);
        getBaseValues().set(MessageBox.MAX_HEIGHT_KEY, 400);
        getBaseValues().set(MessageBox.OPEN_DURATION_KEY, 15.0);
        getBaseValues().set(MessageBox.SHOW_ANIM_DURATION_KEY, 0.3);
        getBaseValues().set(MessageBox.HIDE_ANIM_DURATION_KEY, 0.3);

        getBaseValues().set(CanvasWindow.BACKGROUND_PAINTER_KEY,
                new WindowBackgroundPainter());
        getBaseValues().set(CanvasWindow.TITLEBAR_PAINTER_KEY,
                new TitleBarPainter());
        getBaseValues().set(CanvasWindow.ICON_COLOR_KEY, Color.BLACK);
        getBaseValues().set(CanvasWindow.ACTIVE_ICON_COLOR_KEY, Color.RED);
        getBaseValues().set(CanvasWindow.UPPER_BACKGROUND_COLOR_KEY,
                new Color(210, 227, 228));
        getBaseValues().set(CanvasWindow.LOWER_BACKGROUND_COLOR_KEY,
                new Color(130, 137, 159));
        getBaseValues().set(CanvasWindow.UPPER_TITLE_COLOR_KEY,
                new Color(84, 255, 200));
        getBaseValues().set(CanvasWindow.LOWER_TITLE_COLOR_KEY,
                new Color(102, 255, 0));
        getBaseValues().set(CanvasWindow.UPPER_ACTIVE_TITLE_COLOR_KEY,
                new Color(171, 227, 250));
        getBaseValues().set(CanvasWindow.LOWER_ACTIVE_TITLE_COLOR_KEY,
                new Color(115, 145, 220));
        getBaseValues().set(CanvasWindow.TRANSPARENCY_KEY, 0.65f);
        getBaseValues().set(CanvasWindow.TITLE_TRANSPARENCY_KEY, 0.65f);
        getBaseValues().set(CanvasWindow.BORDER_COLOR_KEY, Color.BLACK);
        getBaseValues().set(CanvasWindow.BORDER_THICKNESS_KEY, 1.f);
        getBaseValues().set(CanvasWindow.SHADOW_WIDTH_KEY, 15f);
        getBaseValues().set(ShadowBorder.SHADOW_COLOR_KEY, Color.BLACK);
        getBaseValues().set(ShadowBorder.SHADOW_TRANSPARENCY_KEY, 0.4f);
        getBaseValues().set(ShadowBorder.SHADOW_WIDTH_KEY, 15f);
        getBaseValues().set(CanvasWindow.FLIP_DURATION_KEY, 0.5);
        getBaseValues().set(CanvasWindow.FADE_IN_DURATION_KEY, 0.3);
        getBaseValues().set(CanvasWindow.FADE_OUT_DURATION_KEY, 0.3);

        getBaseValues().set(Connection.CONNECTION_COLOR_KEY,
                new Color(70, 70, 70));
        getBaseValues().set(Connection.ACTIVE_CONNECTION_COLOR_KEY,
                new Color(255, 249, 102));
        getBaseValues().set(Connection.CONNECTION_THICKNESS_KEY, 2.f);
        getBaseValues().set(Connection.ACTIVE_CONNECTION_THICKNESS_KEY, 3.f);


        getBaseValues().set(ControlFlowConnection.CONTROLFLOW_CONNECTION_COLOR_KEY,
                new Color(255, 255, 20));
        getBaseValues().set(ControlFlowConnection.ACTIVE_CONTROLFLOW_CONNECTION_COLOR_KEY,
                new Color(20, 20, 20));
        getBaseValues().set(
                ControlFlowConnection.CONTROLFLOW_CONNECTION_THICKNESS_KEY, 5.f);
        getBaseValues().set(
                ControlFlowConnection.ACTIVE_CONTROLFLOW_CONNECTION_THICKNESS_KEY, 2.f);


        getBaseValues().set(TypeRepresentationBase.INVALID_VALUE_COLOR_KEY, Color.RED);
        getBaseValues().set(TypeRepresentationBase.VALID_VALUE_COLOR_KEY, Color.GREEN);
        getBaseValues().set(TypeRepresentationBase.WARNING_VALUE_COLOR_KEY, Color.YELLOW);


        getBaseValues().set(
                DefaultMethodRepresentation.METHOD_TITLE_TRANSPARENCY_KEY, 0.2f);
        getBaseValues().set(
                DefaultMethodRepresentation.METHOD_TITLE_UPPER_COLOR_KEY,
                new Color(70, 75, 70));
        getBaseValues().set(
                DefaultMethodRepresentation.METHOD_TITLE_LOWER_COLOR_KEY,
                new Color(70, 75, 70));

        getBaseValues().set(VTextField.TEXT_FIELD_COLOR_KEY,
                new Color(240, 240, 240));
        getBaseValues().set(VCanvas3D.UPPER_COLOR_KEY,
                new Color(34, 30, 38, 180));
        getBaseValues().set(VCanvas3D.LOWER_COLOR_KEY,
                new Color(74, 68, 78, 80));
        getBaseValues().set(VCanvas3D.CONTENT_TRANSPARENCY_KEY, 1.f);

        getBaseValues().set(PlotPane.UPPER_COLOR_KEY, new Color(34, 30, 38, 180));
        getBaseValues().set(PlotPane.LOWER_COLOR_KEY, new Color(74, 68, 78, 80));


        getBaseValues().set(VCodeEditor.BACKGROUND_COLOR_KEY,
                new Color(230, 230, 230));
        getBaseValues().set(VCodeEditor.BACKGROUND_TRANSPARENCY_KEY, 0.8f);
        getBaseValues().set(VCodeEditor.LINE_NUMBER_COLOR_KEY,
                new Color(50, 50, 50, 200));
        getBaseValues().set(VCodeEditor.LINE_NUMBER_FIELD_COLOR_KEY,
                new Color(0, 0, 0, 80));
        getBaseValues().set(VCodeEditor.COMPILE_ERROR_COLOR_KEY,
                new Color(255, 0, 0, 120));
        getBaseValues().set(VCodeEditor.COMPILE_ERROR_BORDER_COLOR_KEY,
                new Color(100, 100, 100, 200));
        getBaseValues().set(VCodeEditor.BORDER_COLOR_KEY, Color.BLACK);
        getBaseValues().set(VCodeEditor.BORDER_THICKNESS_KEY, 1.f);
        getBaseValues().set(VCodeEditor.FONT_SIZE_KEY,12.f);

        getBaseValues().set("LookAndFeel", null);


        // DATA CONNECTOR
        getBaseValues().set(Connector.ACTIVE_COLOR_ERROR_KEY, Color.RED);
        getBaseValues().set(Connector.ACTIVE_COLOR_VALID_KEY, Color.GREEN);
        getBaseValues().set(Connector.INACTIVE_COLOR_ERROR_KEY,
                new Color(210, 227, 228));
        getBaseValues().set(Connector.INACTIVE_COLOR_VALID_KEY,
                new Color(210, 227, 228));
        getBaseValues().set(Connector.SIZE_KEY, new Integer(9));
        getBaseValues().set(Connector.BORDER_THICKNESS_KEY, 1.f);
        getBaseValues().set(Connector.BORDER_COLOR_KEY, Color.BLACK);
        getBaseValues().set(Connector.TRANSPARENCY_KEY, 0.65f);

        getBaseValues().set(SelectionRectangle.FILL_COLOR_KEY,
                new Color(0, 0, 0, 120));
        getBaseValues().set(SelectionRectangle.BORDER_COLOR_KEY,
                new Color(255, 255, 255, 160));
        getBaseValues().set(SelectionRectangle.CUSTOM_STROKE_KEY,
                false);
        getBaseValues().set(SelectionRectangle.STROKE_DASH_KEY,
                new float[]{5});
        getBaseValues().set(SelectionRectangle.STROKE_WIDTH_KEY, 1.0f);

        if (initSubStyles) {
            getBaseValues().set(VDialogWindow.DIALOG_STYLE_KEY,
                    VDialogWindow.createDialogStyle());
            getBaseValues().set(VWindowGroup.STYLE_KEY,
                    VWindowGroup.createGroupStyle());
        }

        // EDITOR STYLE
        SyntaxScheme scheme = new SyntaxScheme(true);

        // set default color to text color
        for (int i = 0; i < scheme.getStyleCount(); i++) {
            if (scheme.getStyle(i) != null) {
                scheme.getStyle(i).foreground = getBaseValues().getColor(
                        Canvas.TEXT_COLOR_KEY);
            }
        }
        
        Font baseFont = RSyntaxTextArea.getDefaultFont().deriveFont(getBaseValues().getFloat(VCodeEditor.FONT_SIZE_KEY));
        
        
        // set default font
        for (int i = 0; i < scheme.getStyleCount(); i++) {
            if (scheme.getStyle(i) != null) {
                scheme.getStyle(i).font = baseFont;
            }
        }

        Font boldFont = baseFont.deriveFont(Font.BOLD);
        Font italicFont = baseFont.deriveFont(Font.ITALIC);

        scheme.getStyle(Token.COMMENT_DOCUMENTATION).foreground = Color.gray;
        scheme.getStyle(Token.COMMENT_DOCUMENTATION).font = italicFont;
        scheme.getStyle(Token.COMMENT_MULTILINE).foreground = Color.gray;
        scheme.getStyle(Token.COMMENT_MULTILINE).font = italicFont;
        scheme.getStyle(Token.COMMENT_EOL).foreground = Color.gray;
        scheme.getStyle(Token.COMMENT_EOL).font = italicFont;

        scheme.getStyle(Token.RESERVED_WORD).font = baseFont;
        scheme.getStyle(Token.RESERVED_WORD).foreground = new Color(60, 60, 240);
        scheme.getStyle(Token.DATA_TYPE).foreground = new Color(60, 60, 240);

        scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = new Color(170, 110, 30);
        scheme.getStyle(Token.LITERAL_CHAR).foreground = new Color(170, 110, 30);

        scheme.getStyle(Token.LITERAL_NUMBER_DECIMAL_INT).foreground = new Color(40, 120, 220);
        scheme.getStyle(Token.LITERAL_NUMBER_FLOAT).foreground = new Color(40, 120, 220);
        scheme.getStyle(Token.LITERAL_NUMBER_HEXADECIMAL).foreground = new Color(40, 120, 220);

        scheme.getStyle(Token.ERROR_STRING_DOUBLE).foreground = new Color(230, 0, 30);
        scheme.getStyle(Token.ERROR_CHAR).foreground = new Color(230, 0, 30);
        scheme.getStyle(Token.ERROR_NUMBER_FORMAT).foreground = new Color(230, 0, 30);

        getBaseValues().setEditorStyle(VCodeEditor.EDITOR_STYLE_KEY, scheme);
        
        getBaseValues().set(VCodeEditor.EDITOR_HIGHLIGHTED_LINE_KEY, new Color(140,140,140,60));

    }

    /**
     * @return the baseValues
     */
    public StyleValueController getBaseValues() {
        return baseValues;
    }

    /**
     * @param baseValues the baseValues to set
     */
    public void setBaseValues(StyleValueController baseValues) {
        this.baseValues = baseValues;
    }

    /**
     * @return the lookAndFeelValues
     */
    public StyleValueController getLookAndFeelValues() {
        return lookAndFeelValues;
    }

    /**
     * @param lookAndFeelValues the lookAndFeelValues to set
     */
    public void setLookAndFeelValues(StyleValueController lookAndFeelValues) {
        this.lookAndFeelValues = lookAndFeelValues;
    }

    /**
     * @return the customValues
     */
    public StyleValueController getCustomValues() {
        return customValues;
    }

    /**
     * @param customValues the customValues to set
     */
    public void setCustomValues(StyleValueController customValues) {
        this.customValues = customValues;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Returns a new style with the specified font size.
     * 
     * @param styleName name of the style (as shown in the style menu)
     * @param editorFontSize font size 
     * @return style with the specified editor font size
     */
    public static Style newStyleWithFontSize(String styleName, float editorFontSize) {
        
        Style s = new Style(styleName);
        // EDITOR STYLE
        SyntaxScheme scheme = new SyntaxScheme(true);

        // set default color to text color
        for (int i = 0; i < scheme.getStyleCount(); i++) {
            if (scheme.getStyle(i) != null) {
                scheme.getStyle(i).foreground = s.getBaseValues().getColor(
                        Canvas.TEXT_COLOR_KEY);
            }
        }

        Font baseFont = RSyntaxTextArea.getDefaultFont().deriveFont(editorFontSize);
        
        
        // set default font
        for (int i = 0; i < scheme.getStyleCount(); i++) {
            if (scheme.getStyle(i) != null) {
                scheme.getStyle(i).font = baseFont;
            }
        }

        Font boldFont = baseFont.deriveFont(Font.BOLD);
        Font italicFont = baseFont.deriveFont(Font.ITALIC);


        scheme.getStyle(Token.COMMENT_DOCUMENTATION).foreground = Color.gray;
        scheme.getStyle(Token.COMMENT_DOCUMENTATION).font = italicFont;
        scheme.getStyle(Token.COMMENT_MULTILINE).foreground = Color.gray;
        scheme.getStyle(Token.COMMENT_MULTILINE).font = italicFont;
        scheme.getStyle(Token.COMMENT_EOL).foreground = Color.gray;
        scheme.getStyle(Token.COMMENT_EOL).font = italicFont;

        scheme.getStyle(Token.RESERVED_WORD).font = baseFont;
        scheme.getStyle(Token.RESERVED_WORD).foreground = new Color(60, 60, 240);
        scheme.getStyle(Token.DATA_TYPE).foreground = new Color(60, 60, 240);

        scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = new Color(170, 110, 30);
        scheme.getStyle(Token.LITERAL_CHAR).foreground = new Color(170, 110, 30);

        scheme.getStyle(Token.LITERAL_NUMBER_DECIMAL_INT).foreground = new Color(40, 120, 220);
        scheme.getStyle(Token.LITERAL_NUMBER_FLOAT).foreground = new Color(40, 120, 220);
        scheme.getStyle(Token.LITERAL_NUMBER_HEXADECIMAL).foreground = new Color(40, 120, 220);

        scheme.getStyle(Token.ERROR_STRING_DOUBLE).foreground = new Color(230, 0, 30);
        scheme.getStyle(Token.ERROR_CHAR).foreground = new Color(230, 0, 30);
        scheme.getStyle(Token.ERROR_NUMBER_FORMAT).foreground = new Color(230, 0, 30);

        s.getBaseValues().setEditorStyle(VCodeEditor.EDITOR_STYLE_KEY, scheme);
        s.getBaseValues().set(VCodeEditor.EDITOR_HIGHLIGHTED_LINE_KEY, new Color(140,140,140,60));
        
        return s;
    }
}
