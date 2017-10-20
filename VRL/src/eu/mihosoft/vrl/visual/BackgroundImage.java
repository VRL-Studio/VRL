/* 
 * BackgroundImage.java
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

import eu.mihosoft.vrl.visual.ImageUtils;
import eu.mihosoft.vrl.io.Base64;
import eu.mihosoft.vrl.io.IOUtil;
import eu.mihosoft.vrl.io.ImageLoader;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * BackgroundImage for Canvas objects.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class BackgroundImage implements GlobalBackgroundPainter {

    /**
     * the background image
     */
    private BufferedImage image;
    /**
     * scaled version of the background image
     */
    private BufferedImage scaledImage;
    /**
     * transparency of the image
     */
    private float transparency = 0.f;
    /**
     * the Canvas object
     */
    private Canvas mainCanvas;
    private AffineTransform transform = new AffineTransform();
    /**
     *
     */
    public static final String IMAGE_KEY = "BackgroundImage:data";
    public static final String IMAGE_TRANSPARENCY_KEY = "BackgroundImage:transparency";

    /**
     * Constructor.
     * @param mainCanvas the main canvas object
     */
    public BackgroundImage(Canvas mainCanvas) {
        setMainCanvas(mainCanvas);
    }

    @Override
    public void paintGlobal(Graphics g) {
        if (image != null) {

            Graphics2D g2 = (Graphics2D) g;

            Composite original = g2.getComposite();

            AlphaComposite ac1 =
                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    1.f - getTransparency());
            g2.setComposite(ac1);

//            g2.drawImage(getScaledImage(), transform, null);

            g2.drawImage(getImage(), transform, null);

            g2.setComposite(original);
        }
    }

//    /**
//     * Generates a scaled image depending on the canvas dimensions.
//     */
//    void generateScaledImage() {
//        Dimension s = getMainCanvas().getSize();
//
//        boolean sizeChanged = getScaledImage() == null
//                || getScaledImage().getWidth() != s.getWidth()
//                || getScaledImage().getHeight() != s.getHeight();
//
//        if (getImage() != null && sizeChanged) {
//            setScaledImage(new BufferedImage(s.width, s.height,
//                    BufferedImage.TYPE_4BYTE_ABGR));
//            Graphics2D g2 = getScaledImage().createGraphics();
//            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//            g2.drawImage(getImage(), 0, 0,
//                    getScaledImage().getWidth(), getScaledImage().getHeight(),
//                    null);
//            g2.dispose();
//        }
//    }
    /**
     * Returns the image.
     * @return the image
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Defines the image.
     * @param image the image to set
     */
    public void setImage(BufferedImage image) {

        if (image != null) {
            this.image = ImageUtils.convertToCompatibleImage(image);
        }

        backgroundImageToStyle(getMainCanvas().getStyle());
//        setScaledImage(null);
//        generateScaledImage();
        getMainCanvas().revalidate();
    }

//    /**
//     * Returns the scaled image.
//     * @return the scaled image
//     */
//    public BufferedImage getScaledImage() {
//        return scaledImage;
//    }
//
//    /**
//     * Defines the scaled image.
//     * @param scaledImage the scaled image to set
//     */
//    private void setScaledImage(BufferedImage scaledImage) {
//        this.scaledImage = scaledImage;
//    }
    /**
     * Returns the transparency.
     * @return the transparency
     */
    public float getTransparency() {
        return transparency;
    }

    /**
     * Defines the transparency.
     * @param transparency the transparency to set
     */
    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }

    /**
     * Returns the main canvas.
     * @return the main Canvas
     */
    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    /**
     * Defines the main canvas.
     * @param mainCanvas the canvas to set
     */
    public void setMainCanvas(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;
    }

    /**
     * Sets the background image. Takes the base64 representation of the styles
     * background image, converts it to BufferedImage.
     * @param style the style
     */
    public void backgroundImageFromStyle(Style style) {

        if (style.getBaseValues().getString(IMAGE_KEY) != null) {
            byte[] bytes =
                    IOUtil.base64ToByteArray(style.getBaseValues().getString(IMAGE_KEY));

            ByteArrayInputStream input = new ByteArrayInputStream(bytes);
            try {
                image = ImageUtils.convertToCompatibleImage(
                        ImageIO.read(input));
            } catch (IOException ex) {
                Logger.getLogger(BackgroundImage.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        } else {
            image = null;
        }

//        setScaledImage(null);
//        generateScaledImage();
        getMainCanvas().revalidate();
    }

    /**
     * Loads an image from file.
     * @param file the file
     * @throws java.io.IOException
     */
    public void loadImage(File file) throws IOException {
        ImageLoader loader = new ImageLoader();

        setImage((BufferedImage) loader.loadFile(file));
    }

    /**
     * Saves the background image as base64 string in a style object.
     * @param style the style
     */
    public void backgroundImageToStyle(Style style) {

        if (image != null) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                ImageIO.write(image, "png", output);
            } catch (IOException ex) {
                Logger.getLogger(BackgroundImage.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
            byte[] bytes = output.toByteArray();

            style.getBaseValues().set(IMAGE_KEY, Base64.encodeBytes(bytes));
        } else {
            style.getBaseValues().set(IMAGE_KEY, null);
        }
    }

    void sizeChanged() {
        if (image != null) {
            Dimension s = getMainCanvas().getSize();
            float scaleX = (float) (s.getWidth() / image.getWidth());
            float scaleY = (float) (s.getHeight() / image.getHeight());

            transform = new AffineTransform();
            transform.scale(scaleX, scaleY);

//            generateScaledImage();
        }
    }
}
