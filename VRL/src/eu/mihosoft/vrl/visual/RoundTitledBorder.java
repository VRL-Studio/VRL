/* 
 * RoundTitledBorder.java
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

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.border.AbstractBorder;

/**
 * This border has rounded edges and provides a title field.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class RoundTitledBorder extends AbstractBorder implements CanvasChild {
    private static final long serialVersionUID = -7802741909633947849L;

    private Canvas mainCanvas;
    private JLabel titleLabel;
//    private int titleHeight = 0;
    private int leftMargin = 0;
    private int rightMargin = 0;
    private int topMargin = 0;
    private int bottomMargin = 0;
//    private CanvasStyle style;

    public RoundTitledBorder(Canvas mainCanvas, String title) {
        this.titleLabel = new JLabel(title);
//        this.style = style;
//        this.titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

//        titleHeight = titleLabel.getHeight();

        setMargins(5, 7, 5, 7);

        setMainCanvas(mainCanvas);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x,
            int y, int width, int height) {

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        BasicStroke stroke = new BasicStroke(1);

        g2.setStroke(stroke);

        g2.setColor(getMainCanvas().getStyle().getBaseValues().getColor(
                CanvasWindow.BORDER_COLOR_KEY));



//        Composite original = g2.getComposite();
//
//        AlphaComposite ac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
//                style.getObjectTransparency());
//        g2.setComposite(ac1);
//
////        g2.setColor(getBackground());
////        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
//
//        BasicStroke stroke = new BasicStroke(style.getObjectBorderThickness());
//
//        g2.setStroke(stroke);
//        g2.setColor(style.getObjectBorderColor());



        // unfortunately we need a complicated paint method as
        // there does not seem to be a TitledBorder with round edges



        // temporary coordinates
        int xPos = 0;
        int yPos = 0;

        // start angle
        int startAngle = 0;


        int arcWidth = 20;
        int arcHeight = 20;

        // compute position of arc 1:
        xPos = x + width - getRightMargin() - arcWidth;
        yPos = y + getTopMargin();

        // arc 4
        g2.drawArc(xPos, yPos, arcWidth, arcHeight, startAngle, 90);

        // compute position of arc 2:
        xPos = x + getLeftMargin() - 1;
        yPos = y + getTopMargin();

        startAngle += 90;

        // arc 2
        g2.drawArc(xPos, yPos, arcWidth, arcHeight, startAngle, 90);

        // compute position of arc 3:
        xPos = x + getLeftMargin() - 1;
        yPos = y + height - arcHeight - getBottomMargin();

        startAngle += 90;

        // arc 3
        g2.drawArc(xPos, yPos, arcWidth, arcHeight, startAngle, 90);

        // compute position of arc 4:
        xPos = x + width - getRightMargin() - arcWidth;
        yPos = y + height - arcHeight - getBottomMargin();

        startAngle += 90;

        // arc 4
        g2.drawArc(xPos, yPos, arcWidth, arcHeight, startAngle, 90);


        // connecting the arcs with lines

        int xPos2 = 0;
        int yPos2 = 0;

        // connecting arc 2 and arc 3

        xPos = x + getLeftMargin() - 1;
        yPos = y + arcHeight / 2 + getTopMargin() + 1;

        xPos2 = xPos;
        yPos2 = y + height - arcHeight / 2 - getBottomMargin() - 1;

        g2.drawLine(xPos, yPos, xPos2, yPos2);

        // connecting arc 3 and arc 4

        xPos = x + getLeftMargin() - 1 + arcWidth / 2 + 1;
        yPos = y + height - getBottomMargin();

        xPos2 = x + width - getRightMargin() - arcWidth / 2 - 1;
        yPos2 = y + height - getBottomMargin();

        g2.drawLine(xPos, yPos, xPos2, yPos2);

        // connecting arc 4 and arc 1

        xPos = x + width - getRightMargin();
        yPos = y + height - getBottomMargin() - arcHeight / 2 - 1;

        xPos2 = xPos;
        yPos2 = y + getTopMargin() + arcHeight / 2 + 1;

        g2.drawLine(xPos, yPos, xPos2, yPos2);

        // and now the complicated part with the title

        g2.setFont(titleLabel.getFont());

        //compute title dimensions

        int titleWidth = (int) titleLabel.getMinimumSize().getWidth();
        int titleHeight = (int) titleLabel.getMinimumSize().getHeight();

        int titlePosX = x + width / 2 - titleWidth / 2;
        int titlePosY = y + getTopMargin() + titleHeight / 2;

        boolean titleIsToBig = titleWidth >
                width - (getLeftMargin() + getRightMargin() + arcWidth);

        g2.setColor(getMainCanvas().getStyle().getBaseValues().getColor(Canvas.TEXT_COLOR_KEY));

        g2.drawString(titleLabel.getText(), titlePosX, titlePosY - titleHeight / 4 + 1);

        g2.setColor(getMainCanvas().getStyle().getBaseValues().getColor(CanvasWindow.BORDER_COLOR_KEY));

        // draw line from arc 1 to title

        int marginFromTextToLine = 5;

        xPos = x + width - getRightMargin() - arcWidth / 2 - 1;
        yPos = y + getTopMargin();

        xPos2 = x + marginFromTextToLine + width / 2 + titleWidth / 2;
        yPos2 = y + getTopMargin();

        if (!titleIsToBig) {
            g2.drawLine(xPos, yPos, xPos2, yPos2);
        }
        // draw line from arc 2 to title

        xPos = x + getLeftMargin() - 1 + arcWidth / 2 + 1;
        yPos = y + getTopMargin();

        xPos2 = x + width / 2 - marginFromTextToLine - titleWidth / 2;
        yPos2 = y + getTopMargin();

        if (!titleIsToBig) {
            g2.drawLine(xPos, yPos, xPos2, yPos2);
        }

        // try to change size of component and its parents
        // this method works perfectly for CanvasObject but unfortunately
        // is no general solution:(
        if (titleIsToBig) {

            int minWidth =
                    titleWidth + arcWidth + getLeftMargin() + getRightMargin();

            Dimension d1 = c.getSize();
            c.setMinimumSize(new Dimension(minWidth, d1.height));
            c.setPreferredSize(new Dimension(minWidth, d1.height));
            c.doLayout();
            c.validate();



            if (c.getParent() != null) {

                Dimension d2 = c.getParent().getSize();
                c.getParent().setMinimumSize(
                        new Dimension(minWidth, d2.height));
                c.getParent().setPreferredSize(
                        new Dimension(minWidth, d2.height));

                c.getParent().doLayout();
                c.getParent().validate();

                if (c.getParent().getParent() != null) {
                    c.getParent().getParent().doLayout();
                    c.getParent().getParent().validate();
                }
            }
        }
    }

    @Override
    public Insets getBorderInsets(Component arg0) {
        int titleHeight = (int) titleLabel.getMinimumSize().getHeight();

        return new Insets(getTopMargin() + titleHeight / 2, getLeftMargin(),
                getBottomMargin(), getRightMargin());
    }

    public String getTitle() {
        return titleLabel.getText();
    }

    public void setTitle(String title) {
        this.titleLabel.setText(title);
    }

    public void setMargins(int top, int left, int bottom, int right) {
        setLeftMargin(left);
        setRightMargin(right);
        setTopMargin(top);
        setBottomMargin(bottom);
    }

    public int getLeftMargin() {
        return leftMargin;
    }

    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }

    public int getRightMargin() {
        return rightMargin;
    }

    public void setRightMargin(int rightMargin) {
        this.rightMargin = rightMargin;
    }

    public int getTopMargin() {
        return topMargin;
    }

    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }

    public int getBottomMargin() {
        return bottomMargin;
    }

    public void setBottomMargin(int bottomMargin) {
        this.bottomMargin = bottomMargin;
    }

    @Override
    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    @Override
    public void setMainCanvas(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;
    }
}
