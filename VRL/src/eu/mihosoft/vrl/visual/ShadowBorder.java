/* 
 * ShadowBorder.java
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

import java.awt.AlphaComposite;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.AbstractBorder;

/**
 * This border can be used to add a shadow around a swing component. If the
 * component implements the ShadowPainter interface it is also possible to
 * use non rectangular shapes, i.e., round edges. Otherwise rectangle shape
 * will be used.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ShadowBorder extends AbstractBorder implements VComponentChild,
        BufferedPainter {

    private static final long serialVersionUID = 2419639645750979147L;
    private VComponent parent;
    private int leftMargin = 0;
    private int rightMargin = 0;
    private int topMargin = 0;
    private int bottomMargin = 0;
    private int x = 0;
    private int y = 0;
    private int width = 0;
    private int height = 0;
    private int shadowWidth = 30;
    private boolean redraw = true;
    private float shadowTransparency = 0.6f;
    transient private BufferedImage buffer;
    public static final String SHADOW_COLOR_KEY = "Shadow:color";
    public static final String SHADOW_WIDTH_KEY = "Shadow:width";
    public static final String SHADOW_TRANSPARENCY_KEY = "Shadow:transparency";

    /**
     * Constructor.
     * @param parent the parent component
     */
    public ShadowBorder(VComponent parent) {
        this.parent = parent;

        // we need to set shadow width etc. here
        // because ugly redrawings happen if style changes at runtime
        shadowTransparency = getShadowTransparency();
        shadowWidth = (int) getShadowWidth();

        setMargins(shadowWidth, shadowWidth, shadowWidth, shadowWidth);

//        setMainCanvas(mainCanvas);


    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    private float getShadowWidth() {
        return parent.getStyle().getBaseValues().
                getFloat(SHADOW_WIDTH_KEY);
    }

    private float getShadowTransparency() {
        return parent.getStyle().getBaseValues().
                getFloat(SHADOW_TRANSPARENCY_KEY);
    }

    protected Color getShadowColor() {
        return parent.getStyle().getBaseValues().
                getColor(SHADOW_COLOR_KEY);
    }

    @Override
    public void paintBorder(Component c, Graphics g,
            int x, int y, int width, int height) {

        boolean xEqual = (this.x == x);
        boolean yEqual = (this.y == y);
        boolean widthEqual = (this.width == width);
        boolean heightEqual = (this.height == height);



        boolean shadowWidthEqual =
                (this.shadowWidth
                == getShadowWidth());
        boolean shadowTransparencyEqual =
                (this.shadowTransparency
                == getShadowTransparency());

        // if layout relevant stuff changes we need to redraw the buffer image
        if (!(xEqual
                && yEqual
                && widthEqual
                && heightEqual
                && shadowTransparencyEqual
                && shadowWidthEqual)
                || redraw) {

            redraw = false;

            int shadowWidthDiff =
                    (int) (shadowWidth - getShadowWidth());

            shadowTransparency = getShadowTransparency();
            shadowWidth = (int) getShadowWidth();

            setMargins(shadowWidth, shadowWidth, shadowWidth, shadowWidth);

            // position of the parent component changes if shadow width changes
            // because insets also change
            // therefore we have to move the component
            // TODO: changing style at runtime results in ugly resizing
            int xPosTmp = c.getBounds().x;
            int yPosTmp = c.getBounds().y;
            int widthTmp = c.getBounds().width;
            int heightTmp = c.getBounds().height;

            c.setBounds(xPosTmp + shadowWidthDiff,
                    yPosTmp + shadowWidthDiff, widthTmp, heightTmp);

            c.doLayout();

            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;

            buffer = new BufferedImage(c.getWidth(),
                    c.getHeight(), BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2 = buffer.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

//            g2.setColor(getMainCanvas().getStyle().getObjectBorderColor());

            // try to get the shape of the base object
            // if it does not provide a custom shape we assume a rectangluar
            // shape
            Shape shape = null;
            if (c instanceof ShadowPainter) {
                ShadowPainter cObj = (ShadowPainter) c;
                shape = cObj.getShape();
            } else {
                shape = new RoundRectangle2D.Double(leftMargin, topMargin,
                        c.getWidth() - leftMargin - rightMargin,
                        c.getHeight() - topMargin - bottomMargin, 3, 3);
            }

            Composite original = g2.getComposite();

            g2.setColor(getShadowColor());

            for (int i = shadowWidth; i > 0; i--) {

                BasicStroke stroke = new BasicStroke(i * 2);
                g2.setStroke(stroke);

                float alpha = (float) (1.0f / (i * Math.sqrt(i)));

                AlphaComposite ac1 =
                        AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, alpha);

                g2.setComposite(ac1);
                g2.draw(shape);
            }

            g2.setStroke(new BasicStroke(1));

            // clear the inner part of the shadow image, otherwise
            // we would get dirty drawings on the parent component 
            g2.setComposite(AlphaComposite.Clear);
            g2.fill(shape);

            g2.dispose();

            // draw alpha gradient on shadow image
            buffer = gradientMask(buffer);
        }

        // draw buffer image‚
        Graphics2D g2 = (Graphics2D) g;
        Composite original = g2.getComposite();

        AlphaComposite ac1 =
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                shadowTransparency);
        g2.setComposite(ac1);

        g2.drawImage(buffer, x, y, null);

        g2.setComposite(original);
    }

    /**
     * Draw alpha mask on source image.
     * @param img source image
     * @return source image with new alpha mask
     */
    public BufferedImage gradientMask(BufferedImage img) {
        BufferedImage result =
                new BufferedImage(img.getWidth(), img.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = result.createGraphics();

        g2.drawImage(img, 0, 0, null);

        GradientPaint paint = new GradientPaint(0, 0,
                new Color(1.0f, 1.0f, 1.0f, 0.0f),
                0, topMargin * 1.5f, // defines the value with that
                new Color(1.0f, 1.0f, 1.0f, 1.0f),
                false); // false means we don't want to repeat the pattern

        g2.setComposite(AlphaComposite.DstIn);

        g2.setPaint(paint);

        g2.fillRect(0, 0, img.getWidth(), img.getHeight());
        g2.dispose();
        return result;
    }

    @Override
    public Insets getBorderInsets(Component arg0) {
        return new Insets(getTopMargin(), getLeftMargin(),
                getBottomMargin(), getRightMargin());
    }

    /**
     * Defines the margins of the border.
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
     * @param leftMargin the left margin
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
     * @param rightMargin the right margin
     */
    public void setRightMargin(int rightMargin) {
        this.rightMargin = rightMargin;
    }

    /**
     * Returns the top margin.
     * @return the top margin
     */
    public int getTopMargin() {
        return topMargin;
    }

    /**
     * Defines the top margin.
     * @param topMargin the top margin
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
     * Defines the bottom margin.
     * @param bottomMargin the bottom margin
     */
    public void setBottomMargin(int bottomMargin) {
        this.bottomMargin = bottomMargin;
    }

//    @Override
//    public Canvas getMainCanvas() {
//        return mainCanvas;
//    }
//
//    @Override
//    public void setMainCanvas(Canvas mainCanvas) {
//        this.mainCanvas = mainCanvas;
//    }
    @Override
    public void contentChanged() {
        redraw = true;
    }

    /**
     * This method is used to customize binary serialization.
     * @param oos the object output stream
     * @throws java.io.IOException
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        redraw = true;
        oos.defaultWriteObject();
    }

//    protected Object getStyleValue(String key) {
//        return parent.getStyle().
//                getBaseValues().get(key);
//    }
    @Override
    public VComponent getVParent() {
        return parent;
    }

    @Override
    public void setVParent(VComponent parent) {
        this.parent = parent;
    }
}
