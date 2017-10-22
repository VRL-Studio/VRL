/* 
 * VWindowGroup.java
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

import eu.mihosoft.vrl.system.VParamUtil;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.Box;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VWindowGroup extends CanvasWindow {

    public static final String STYLE_KEY = "VWindowGroup:style";
    public static final String BORDER_TRANSPARENCY_KEY =
            "VWindowGroup:border:transparency";
    public static final String OUTER_TRANSPARENCY_KEY =
            "VWindowGroup:outer:transparency";
//    public static final String INNER_TRANSPARENCY_KEY =
//            "VWindowGroup:inner:transparency";
//    public static final String INNER_COLOR_KEY =
//            "VWindowGroup:inner:color";
    public static final String OUTER_UPPER_COLOR_KEY =
            "VWindowGroup:outer:upper:color";
    public static final String OUTER_LOWER_COLOR_KEY =
            "VWindowGroup:outer:lower:color";

    /**
     * Constructor.
     *
     * @param title the window title
     * @param mainCanvas the main canvas object
     */
    public VWindowGroup(String title, Canvas mainCanvas) {
        super(title, mainCanvas);
        initialize();
    }

    private void initialize() {

        setStyle(createGroupStyle());

        Box box = Box.createVerticalBox();
        add(box);
        box.setMinimumSize(new Dimension(200, 120));
        box.setPreferredSize(new Dimension(200, 120));

        Component c = Box.createGlue();
        c.setSize(160, 100);
        c.setMinimumSize(new Dimension(160, 100));
        final ResizableContainer rc = new ResizableContainer(c, this);
        box.add(rc);
    }

    static Style createGroupStyle() {
        Style s = new Style("VWindowGroupStyle", false);

        s.getBaseValues().set(CanvasGrid.GRID_COLOR_KEY, new Color(50, 50, 50));
        s.getBaseValues().set(Canvas.BACKGROUND_COLOR_KEY, new Color(30, 30, 30));
        s.getBaseValues().set(Canvas.TEXT_COLOR_KEY, new Color(220, 220, 220));
        s.getBaseValues().set(Canvas.SELECTED_TEXT_COLOR_KEY, new Color(160, 160, 160));
        s.getBaseValues().set(Canvas.CARET_COLOR_KEY, new Color(160, 160, 160));
        s.getBaseValues().set(Canvas.TEXT_SELECTION_COLOR_KEY, new Color(130, 145, 180, 60));

        s.getBaseValues().set(CanvasWindow.ICON_COLOR_KEY, new Color(220, 220, 220));
//        s.getBaseValues().set(CanvasWindow.ACTIVE_ICON_COLOR_KEY, new Color(180, 180, 180));
        
        
//        s.getBaseValues().set(CanvasWindow.UPPER_BACKGROUND_COLOR_KEY, new Color(40, 40, 40));
//        s.getBaseValues().set(CanvasWindow.LOWER_BACKGROUND_COLOR_KEY, new Color(10, 10, 10));
        
        s.getBaseValues().set(CanvasWindow.UPPER_BACKGROUND_COLOR_KEY, new Color(40, 40, 40));
        s.getBaseValues().set(CanvasWindow.LOWER_BACKGROUND_COLOR_KEY, new Color(10, 10, 10));
        
//        s.getBaseValues().set(VWindowGroup.OUTER_UPPER_COLOR_KEY, new Color(20, 30, 40));
//        s.getBaseValues().set(VWindowGroup.OUTER_LOWER_COLOR_KEY, new Color(23, 16, 2));
        
//        s.getBaseValues().set(VWindowGroup.OUTER_LOWER_COLOR_KEY, new Color(238, 160, 28));
        
        s.getBaseValues().set(VWindowGroup.OUTER_UPPER_COLOR_KEY, new Color(40,40,40));
        s.getBaseValues().set(VWindowGroup.OUTER_LOWER_COLOR_KEY, new Color(10, 10, 10));
        
        
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
        s.getBaseValues().set(CanvasWindow.TRANSPARENCY_KEY, 0.5f);
        s.getBaseValues().set(VWindowGroup.BORDER_TRANSPARENCY_KEY, 0.98f);
        s.getBaseValues().set(VWindowGroup.OUTER_TRANSPARENCY_KEY, 0.98f);

        s.getBaseValues().set(CanvasWindow.BACKGROUND_PAINTER_KEY,
                new VWindowGroupBackgroundPainter());
        s.getBaseValues().set(CanvasWindow.TITLEBAR_PAINTER_KEY,
                new VWindowGroupTitleBarPainter());

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
//        s.getBaseValues().set(VTextField.TEXT_FIELD_COLOR_KEY, new Color(80, 80, 80));

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

        return s;
    }
}

class VWindowGroupBackgroundPainter implements
        Painter, ShadowPainter, BufferedPainter {

    transient private BufferedImage buffer;
    private CanvasWindow parent;
     

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
            
            float innerOffset = 12;
            
            float titleBarOffset = parent.getTitleBarSize().height;
            
            
            Shape sh2 = new RoundRectangle2D.Double(left + innerOffset,
                    top + titleBarOffset - 1,
                    d.width - right - left - 1 - innerOffset*2,
                    d.height - bottom - top - 1 - innerOffset - titleBarOffset,
                    20, 20);
            
            Shape sh2InnerLine
                    = new RoundRectangle2D.Double(left + innerOffset,
                    top + titleBarOffset - 1,
                    d.width - right - left - 1 - innerOffset*2,
                    d.height - bottom - top - 1 - innerOffset - titleBarOffset,
                    18, 18);
            
            Area a1 = new Area(sh);
            Area a2 = new Area(sh2);
            
            a1.subtract(a2);
            
            g2.fill(sh);
            
            paint = new GradientPaint(0, 0,
                    s.getBaseValues().getColor(
                    VWindowGroup.OUTER_UPPER_COLOR_KEY),
                    0, d.height,
                    s.getBaseValues().getColor(
                    VWindowGroup.OUTER_LOWER_COLOR_KEY),
                    false); // true means to repeat pattern
            
            g2.setPaint(paint);
            
            ac1 =
                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    s.getBaseValues().getFloat(
                    VWindowGroup.OUTER_TRANSPARENCY_KEY));
            
            g2.setComposite(ac1);

            g2.fill(a1);

            ac1 =
                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    s.getBaseValues().getFloat(
                    VWindowGroup.BORDER_TRANSPARENCY_KEY));
            
            g2.setComposite(ac1);

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
            
            g2.draw(sh2InnerLine);

            g2.dispose();

            g2.setStroke(oldStroke);

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

        if (!CanvasWindow.class.isAssignableFrom(parent.getClass())) {
            throw new IllegalArgumentException(
                    "Only \"CanvasWindow\" is supported as parent!");
        }
        this.parent = (CanvasWindow) parent;
    }

    @Override
    public Painter newInstance(VComponent parent) {
        VWindowGroupBackgroundPainter result =
                new VWindowGroupBackgroundPainter();

        result.setParent(parent);

        return result;
    }
}

class VWindowGroupTitleBarPainter implements Painter, BufferedPainter {

    private CanvasWindowTitleBar parent;

    public VWindowGroupTitleBarPainter() {
    }

    public VWindowGroupTitleBarPainter(CanvasWindowTitleBar parent) {
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

//        if (!parent.getParentWindow().isMinimized()) {
//            g2.drawLine(1, d.height, d.width - 1, d.height);
//        }


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

        VWindowGroupTitleBarPainter result =
                new VWindowGroupTitleBarPainter((CanvasWindowTitleBar) parent);
        result.setParent(parent);
        return result;
    }
}
