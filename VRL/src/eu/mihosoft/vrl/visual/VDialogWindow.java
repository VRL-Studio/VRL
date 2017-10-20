/* 
 * VDialogWindow.java
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

import eu.mihosoft.vrl.system.VParamUtil;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VDialogWindow extends CanvasWindow {

    public static final String DIALOG_STYLE_KEY = "VDialog:style";

    public VDialogWindow(String title, Canvas mainCanvas) {
        super(title, mainCanvas);

        setStyle(mainCanvas.getStyle().getBaseValues().
                getStyle(DIALOG_STYLE_KEY));

        removeCloseIcon();

        setResizable(false);
        setMovable(false);
        setSelectable(false);
        setMenuEnabled(false);
        setActivatable(false);

        setLayoutController(new VLayoutController() {

            @Override
            public void layoutComponent(JComponent c) {
                CanvasWindow w = VDialogWindow.this;

                Dimension size = w.getSize();

                Point loc = w.getLocation();
                loc.x = (int) (w.getMainCanvas().getVisibleRect().x
                        + w.getMainCanvas().getVisibleRect().
                        getWidth() / 2 - size.width / 2);
                loc.y = (int) (w.getMainCanvas().getVisibleRect().y
                        + w.getMainCanvas().getVisibleRect().
                        getHeight() / 2 - size.height / 2);

                // check that windows are always inside canvas bounds
                loc.x = Math.max(loc.x, 0 - w.getInsets().left);
                loc.y = Math.max(loc.y, 0 - w.getInsets().top);

                if (w.getMainCanvas().getVisibleRect().width < w.getWidth()) {
                    loc.x = w.getLocation().x;
                }

                if (w.getMainCanvas().getVisibleRect().height < w.getHeight()) {
                    loc.y = w.getLocation().y;
                }

                w.setLocation(loc);
                w.resetWindowLocation();
            }
        });
    }

    static Style createDialogStyle() {
        Style s = new Style("VDialogStyle", false);

        s.getBaseValues().set(CanvasGrid.GRID_COLOR_KEY, new Color(50, 50, 50));
        s.getBaseValues().set(Canvas.BACKGROUND_COLOR_KEY, new Color(30, 30, 30));
        s.getBaseValues().set(Canvas.TEXT_COLOR_KEY, new Color(220, 220, 220));
        s.getBaseValues().set(Canvas.SELECTED_TEXT_COLOR_KEY, new Color(160, 160, 160));
        s.getBaseValues().set(Canvas.CARET_COLOR_KEY, new Color(160, 160, 160));
        s.getBaseValues().set(Canvas.TEXT_SELECTION_COLOR_KEY, new Color(130, 145, 180, 60));

        s.getBaseValues().set(CanvasWindow.ICON_COLOR_KEY, new Color(180, 180, 180));
//        s.getBaseValues().set(CanvasWindow.ACTIVE_ICON_COLOR_KEY, new Color(180, 180, 180));
        s.getBaseValues().set(CanvasWindow.UPPER_BACKGROUND_COLOR_KEY, new Color(40, 40, 40));
        s.getBaseValues().set(CanvasWindow.LOWER_BACKGROUND_COLOR_KEY, new Color(10, 10, 10));
        s.getBaseValues().set(CanvasWindow.BORDER_COLOR_KEY, new Color(255, 255, 255));
        s.getBaseValues().set(CanvasWindow.BORDER_THICKNESS_KEY, 1.5f);

        s.getBaseValues().set(CanvasWindow.SHADOW_WIDTH_KEY, 50f);
        s.getBaseValues().set(ShadowBorder.SHADOW_WIDTH_KEY, 50f);
        s.getBaseValues().set(ShadowBorder.SHADOW_TRANSPARENCY_KEY, 1.f);

        s.getBaseValues().set(CanvasWindow.FADE_IN_DURATION_KEY, 0.0);
        s.getBaseValues().set(CanvasWindow.FADE_OUT_DURATION_KEY, 0.0);


//        s.getBaseValues().set(CanvasWindow.UPPER_TITLE_COLOR_KEY, VSwingUtil.TRANSPARENT_COLOR);
//        s.getBaseValues().set(CanvasWindow.LOWER_TITLE_COLOR_KEY, VSwingUtil.TRANSPARENT_COLOR);
//        s.getBaseValues().set(CanvasWindow.BORDER_COLOR_KEY, new Color(150, 150, 150));
        s.getBaseValues().set(CanvasWindow.TRANSPARENCY_KEY, 0.95f);

        s.getBaseValues().set(CanvasWindow.BACKGROUND_PAINTER_KEY,
                new DialogBackgroundPainter());
        s.getBaseValues().set(CanvasWindow.TITLEBAR_PAINTER_KEY,
                new DialogTitleBarPainter());

//        s.getBaseValues().set(DefaultMethodRepresentation.METHOD_TITLE_UPPER_COLOR_KEY, new Color(75, 75, 75));
//        s.getBaseValues().set(DefaultMethodRepresentation.METHOD_TITLE_LOWER_COLOR_KEY, new Color(40, 40, 40));

        s.getBaseValues().set(MessageBox.BOX_COLOR_KEY, new Color(40, 45, 60));

        s.getBaseValues().set(Connector.INACTIVE_COLOR_ERROR_KEY,
                new Color(85, 85, 85));
        s.getBaseValues().set(Connector.INACTIVE_COLOR_VALID_KEY,
                new Color(85, 85, 85));

        s.getBaseValues().set(Connector.BORDER_COLOR_KEY,
                new Color(85, 85, 85));

//        s.getBaseValues().set(Connection.CONNECTION_COLOR_KEY, new Color(70, 70, 70));
//        s.getBaseValues().set(Connection.CONNECTION_THICKNESS_KEY, 2f);
//        s.getBaseValues().set(Connection.ACTIVE_CONNECTION_COLOR_KEY, new Color(47, 110, 47));
//        s.getBaseValues().set(Connection.ACTIVE_CONNECTION_THICKNESS_KEY, 3f);


//        s.getBaseValues().set(ControlFlowConnection.CONTROLFLOW_CONNECTION_COLOR_KEY,
//                new Color(100,100,100));
//        s.getBaseValues().set(ControlFlowConnection.ACTIVE_CONTROLFLOW_CONNECTION_COLOR_KEY,
//                new Color(70, 70, 70));
//        s.getBaseValues().set(
//                ControlFlowConnection.CONTROLFLOW_CONNECTION_THICKNESS_KEY, 5.f);
//        s.getBaseValues().set(
//                ControlFlowConnection.ACTIVE_CONTROLFLOW_CONNECTION_THICKNESS_KEY, 2.f);


        s.getBaseValues().set(VTextField.TEXT_FIELD_COLOR_KEY, new Color(80, 80, 80));
        s.getBaseValues().set(VCodeEditor.LINE_NUMBER_COLOR_KEY, new Color(160, 160, 160, 160));
        s.getBaseValues().set(VCodeEditor.LINE_NUMBER_FIELD_COLOR_KEY, new Color(160, 160, 160, 20));
        s.getBaseValues().set(VCodeEditor.BACKGROUND_COLOR_KEY, new Color(85, 85, 85));
        s.getBaseValues().set(VCodeEditor.BORDER_COLOR_KEY, new Color(100, 100, 100));


        s.getBaseValues().set(
                SelectionRectangle.FILL_COLOR_KEY, new Color(80, 80, 80, 120));
        s.getBaseValues().set(
                SelectionRectangle.BORDER_COLOR_KEY, new Color(120, 120, 120, 160));

        // Nimbus related
        s.getLookAndFeelValues().set("nimbusBase", new Color(24, 24, 24));
        s.getLookAndFeelValues().set("nimbusBlueGrey", new Color(24, 24, 24));
        s.getLookAndFeelValues().set("control", new Color(40, 40, 40));
        s.getLookAndFeelValues().set("text", new Color(160, 160, 160));
        s.getLookAndFeelValues().set("menuText", new Color(160, 160, 160));
        s.getLookAndFeelValues().set("infoText", new Color(160, 160, 160));
        s.getLookAndFeelValues().set("controlText", new Color(160, 160, 160));
        s.getLookAndFeelValues().set("nimbusSelectedText",
                new Color(160, 160, 160));
        s.getLookAndFeelValues().set("nimbusLightBackground",
                new Color(40, 40, 40));



        // EDITOR STYLE
        SyntaxScheme scheme = new SyntaxScheme(true);

        // set default color to text color
        for (int i = 0; i < scheme.getStyleCount(); i++) {
            if (scheme.getStyle(i) != null) {
                scheme.getStyle(i).foreground = s.getBaseValues().getColor(
                        Canvas.TEXT_COLOR_KEY);
            }
        }

//        StyleContext sc = StyleContext.getDefaultStyleContext();
        Font baseFont = RSyntaxTextArea.getDefaultFont();
        Font boldFont = baseFont.deriveFont(Font.BOLD);
        Font italicFont = baseFont.deriveFont(Font.ITALIC);

        scheme.getStyle(Token.COMMENT_DOCUMENTATION).foreground = Color.gray;
        scheme.getStyle(Token.COMMENT_DOCUMENTATION).font = italicFont;
        scheme.getStyle(Token.COMMENT_MULTILINE).foreground = Color.gray;
        scheme.getStyle(Token.COMMENT_MULTILINE).font = italicFont;
        scheme.getStyle(Token.COMMENT_EOL).foreground = Color.gray;
        scheme.getStyle(Token.COMMENT_EOL).font = italicFont;

        scheme.getStyle(Token.RESERVED_WORD).font = baseFont;
        scheme.getStyle(Token.RESERVED_WORD).foreground = new Color(145, 145, 240);
        scheme.getStyle(Token.DATA_TYPE).foreground = new Color(145, 145, 240);

        scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = new Color(220, 180, 100);
        scheme.getStyle(Token.LITERAL_CHAR).foreground = new Color(220, 180, 100);

        scheme.getStyle(Token.LITERAL_NUMBER_DECIMAL_INT).foreground = new Color(160, 200, 180);
        scheme.getStyle(Token.LITERAL_NUMBER_FLOAT).foreground = new Color(160, 200, 180);
        scheme.getStyle(Token.LITERAL_NUMBER_HEXADECIMAL).foreground = new Color(160, 200, 180);

        scheme.getStyle(Token.ERROR_STRING_DOUBLE).foreground = new Color(230, 0, 30);
        scheme.getStyle(Token.ERROR_CHAR).foreground = new Color(230, 0, 30);
        scheme.getStyle(Token.ERROR_NUMBER_FORMAT).foreground = new Color(230, 0, 30);

        s.getBaseValues().setEditorStyle(VCodeEditor.EDITOR_STYLE_KEY, scheme);

        return s;
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}

class DialogBackgroundPainter implements
        Painter, ShadowPainter, BufferedPainter {

    transient private BufferedImage buffer;
    private VComponent parent;

    @Override
    public void paint(Graphics g, Style s, Dimension d) {
        //        System.out.println("PAINT: " + getTitle());
        if (buffer == null || buffer.getWidth() != d.width
                || buffer.getHeight() != d.height) {

//                int top = getInsets().top;
//                int bottom = getInsets().bottom;
//                int left = getInsets().left;
//                int right = getInsets().right;

            int top = 0;
            int bottom = 0;
            int left = 0;
            int right = 0;

//            if (d.width < 1 || d.height < 1) {
//                return;
//            }

            buffer =
                    ImageUtils.createCompatibleImage(d.width, d.height);

            Graphics2D g2 = buffer.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Composite original = g2.getComposite();

            AlphaComposite ac1 =
                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    s.getBaseValues().getFloat(
                    CanvasWindow.TRANSPARENCY_KEY));
            g2.setComposite(ac1);

            GradientPaint paint = new GradientPaint(0, 0,
                    s.getBaseValues().getColor(
                    CanvasWindow.UPPER_BACKGROUND_COLOR_KEY),
                    0, d.height,
                    s.getBaseValues().getColor(
                    CanvasWindow.LOWER_BACKGROUND_COLOR_KEY),
                    false); // true means to repeat pattern
            g2.setPaint(paint);

            //g2.setColor(getBackground());

            Shape sh = new RoundRectangle2D.Double(left + 0, top + 0,
                    d.width - right - left - 1,
                    d.height - bottom - top - 1, 20, 20);

            g2.fill(sh);

            Stroke oldStroke = g2.getStroke();

            BasicStroke stroke =
                    new BasicStroke(s.getBaseValues().
                    getFloat(CanvasWindow.BORDER_THICKNESS_KEY));

            g2.setStroke(stroke);
            g2.setColor(s.getBaseValues().getColor(
                    CanvasWindow.BORDER_COLOR_KEY));

            sh = new RoundRectangle2D.Double(left + 0, top + 0.5,
                    d.width - right - left - 1,
                    d.height - bottom - top - 2, 20, 20);

            g2.draw(sh);

            g2.dispose();

            buffer.flush();
        }
        g.drawImage(buffer, 0, 0, null);
    }

    @Override
    public Shape getShape() {

        int top = parent.getInsets().top;
        int bottom = parent.getInsets().bottom;
        int left = parent.getInsets().left;
        int right = parent.getInsets().right;

        return new RoundRectangle2D.Double(left + 0, top + 0,
                parent.getWidth() - right - left,//(*)
                parent.getHeight() - bottom - top - 1, 20, 20);
    }

    @Override
    public void contentChanged() {
        buffer = null;
    }

    private void setParent(VComponent parent) {
        if (parent == null) {
            throw new IllegalArgumentException(
                    "Argument \"null\" is not supported!");
        }

//        if (!CanvasWindow.class.isAssignableFrom(parent.getClass())) {
//            throw new IllegalArgumentException(
//                    "Only \"CanvasWindow\" is supported as parent!");
//        }

        this.parent = (VComponent) parent;
    }

    @Override
    public Painter newInstance(VComponent parent) {
        DialogBackgroundPainter result = new DialogBackgroundPainter();

        result.setParent(parent);

        return result;
    }
}

class DialogTitleBarPainter implements Painter, BufferedPainter {

    private CanvasWindowTitleBar parent;

    public DialogTitleBarPainter() {
    }

    public DialogTitleBarPainter(CanvasWindowTitleBar parent) {
        this.parent = parent;
    }

    @Override
    public void paint(Graphics g, Style s, Dimension d) {

//        if (parent.getParentWindow().isMinimized()) {
//            contentChanged();
//        }
//        
//        if (buffer == null
//                || buffer.getWidth() != d.width
//                || buffer.getHeight() != d.height) {
//
//            buffer = ImageUtils.createCompatibleImage(d.width, d.height);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Style style = s;

        Composite original = g2.getComposite();

        g2.setComposite(original);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setStroke(new BasicStroke(3.f));

        g2.setColor(style.getBaseValues().getColor(
                CanvasWindow.BORDER_COLOR_KEY));

        if (!parent.getParentWindow().isMinimized()) {
            g2.drawLine(1, d.height, d.width - 1, d.height);
        }


//            g2.dispose();
//        }

//        g.drawImage(buffer, 0, 0, null);
    }

    @Override
    public void contentChanged() {
//        buffer = null;
    }

    private void setParent(VComponent parent) {
        if (parent == null) {
            throw new IllegalArgumentException(
                    "Argument \"null\" is not supported!");
        }

        if (!CanvasWindowTitleBar.class.isAssignableFrom(parent.getClass())) {
            throw new IllegalArgumentException(
                    "Only \"CanvasWindowTitleBar\" is supported as parent!");
        }
        this.parent = (CanvasWindowTitleBar) parent;
    }

    @Override
    public Painter newInstance(VComponent parent) {

        VParamUtil.throwIfNotValid(VParamUtil.VALIDATOR_INSTANCEOF,
                CanvasWindowTitleBar.class, parent);

        DialogTitleBarPainter result =
                new DialogTitleBarPainter((CanvasWindowTitleBar) parent);
        result.setParent(parent);
        return result;
    }
}
