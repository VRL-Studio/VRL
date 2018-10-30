/* 
 * ImageUtils.java
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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.VolatileImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
//import javax.media.jai.Interpolation;
//import javax.media.jai.JAI;
//import javax.media.jai.PerspectiveTransform;
//import javax.media.jai.RenderedOp;
//import javax.media.jai.Warp;
//import javax.media.jai.WarpPerspective;

/**
 * ImageUtils is a collection of useful methods related to image creation and
 * manipulation (alpha mask, image format etc.).
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ImageUtils {

    private float topMargin;

    /**
     * Draws linear alpha mask on source image.
     *
     * @param img source image
     * @param start the start transparency
     * @param stop the stop transparency
     * @return source image with new alpha mask
     */
    public static BufferedImage gradientMask(
            BufferedImage img, float start, float stop) {
        BufferedImage result =
                new BufferedImage(img.getWidth(),
                img.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = result.createGraphics();

        g2.drawImage(img, 0, 0, null);

        GradientPaint paint = new GradientPaint(0, img.getHeight(),
                new Color(1.0f, 1.0f, 1.0f, start),
                0, 0,
                new Color(1.0f, 1.0f, 1.0f, stop),
                false); // true means to repeat pattern

        g2.setComposite(AlphaComposite.DstIn);

        g2.setPaint(paint);

        g2.fillRect(0, 0, img.getWidth(), img.getHeight());
        g2.dispose();
        return result;
    }

//    /**
//     * Converts a buffered image to different image type and scales it.
//     * A possible use case
//     * is the convertion of an image with alpha channel
//     * (BufferedImage.TYPE_INT_ARGB) to an image with only Rgb color channels
//     * (BufferedImage.TYPE_INT_RGB).
//     * @param img the image to convert
//     * @param imageType the image type to use for convertion
//     * @param w the width of the converted image
//     * @param h the height of the converted image
//     * @return the converted image
//     */
//    public static BufferedImage convert(BufferedImage img,
//            int imageType, int w, int h) {
//        BufferedImage result = new BufferedImage(
//                w,
//                h,
//                imageType);
//        Graphics2D g2 = result.createGraphics();
//        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//
//        g2.drawImage(img, 0, 0, w, h, null);
//        g2.dispose();
//        return result;
//    }
    /**
     * Converts a buffered image to different image type and scales it. A
     * possible use case is the convertion of an image with alpha channel
     * (BufferedImage.TYPE_INT_ARGB) to an image with only Rgb color channels
     * (BufferedImage.TYPE_INT_RGB).
     *
     * @param img the image to convert
     * @param imageType the image type to use for convertion
     * @param w the width of the converted image
     * @param h the height of the converted image
     * @return the converted image
     */
    public static BufferedImage convert(BufferedImage img,
            int imageType, int w, int h) {
        return convert(img, imageType, w, h, false);
    }

    /**
     * Converts a buffered image to different image type and scales it. A
     * possible use case is the convertion of an image with alpha channel
     * (BufferedImage.TYPE_INT_ARGB) to an image with only Rgb color channels
     * (BufferedImage.TYPE_INT_RGB).
     *
     * @param img the image to convert
     * @param imageType the image type to use for convertion
     * @param w the width of the converted image
     * @param h the height of the converted image
     * @param fixedRatio defines whether ratio is fixed
     * @return the converted image
     */
    public static BufferedImage convert(Image image,
            int imageType, int w, int h, boolean fixedRatio) {

        BufferedImage previewImage = new BufferedImage(w,
                h,
                imageType);
        Graphics2D g2 = previewImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);


        // *******************************
        // fixed aspect ratio
        // *******************************

        if (fixedRatio) {
            double scalex = 1;
            double scaley = 1;

            double sx = (double) w / image.getWidth(null);
            double sy = (double) h / image.getHeight(null);
            scalex = Math.min(sx, sy);
            scaley = scalex;
            // center the image
            g2.translate((w - (image.getWidth(null) * scalex)) / 2,
                    (h - (image.getHeight(null) * scaley)) / 2);

            g2.scale(scalex, scaley);

            g2.drawImage(image, 0, 0, null);

        } else {
            g2.drawImage(image, 0, 0, w, h, null);
        }

        g2.dispose();
        
        return previewImage;

    }

    /**
     * Converts a buffered image to different image type. A possible use case is
     * the convertion of an image with alpha channel
     * (BufferedImage.TYPE_INT_ARGB) to an image with only Rgb color channels
     * (BufferedImage.TYPE_INT_RGB).
     *
     * @param img the image to convert
     * @param imageType the image type to use for convertion
     * @return the converted image
     */
    public static BufferedImage convert(BufferedImage img, int imageType) {
        return convert(img, imageType, img.getWidth(), img.getHeight());
    }

    /**
     * Draws radial alpha mask on source image.
     *
     * @param img source image
     * @param start the start transparency
     * @param stop the stop transparency
     * @return source image with new alpha mask
     */
    public static BufferedImage radialGradientMask(
            BufferedImage img, float start, float stop) {
        BufferedImage result =
                new BufferedImage(img.getWidth(), img.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = result.createGraphics();

        g2.drawImage(img, 0, 0, null);

        Point2D center =
                new Point2D.Double(img.getWidth() / 2.0, img.getHeight() / 2.0);

        float[] dist = {0.0f, 1.0f};
        Color[] colors = {new Color(1.0f, 1.0f, 1.0f, start),
            new Color(1.0f, 1.0f, 1.0f, stop)};
        RadialGradientPaint paint =
                new RadialGradientPaint(
                center, img.getWidth() / 2.0f, dist, colors);



//        GradientPaint paint = new GradientPaint(0, img.getHeight(),
//                new Color(1.0f, 1.0f, 1.0f, start),
//                0,0 ,
//                new Color(1.0f, 1.0f, 1.0f, stop),
//                false); // true means to repeat pattern

        g2.setComposite(AlphaComposite.DstIn);

        g2.setPaint(paint);

        g2.fillRect(0, 0, img.getWidth(), img.getHeight());
        g2.dispose();
        return result;
    }

    /**
     * Returns a new volatile image.
     *
     * @param width the image width
     * @param height the image height
     * @return the volatile image
     */
    public static VolatileImage createVolatileImage(
            int width, int height) {
        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsConfiguration gc =
                ge.getDefaultScreenDevice().getDefaultConfiguration();
        VolatileImage image = null;

        image = gc.createCompatibleVolatileImage(width, height, Transparency.TRANSLUCENT);

        int valid = image.validate(gc);

//        if (valid == VolatileImage.IMAGE_INCOMPATIBLE) {
//            image = createVolatileImage(width, height, Transparency.TRANSLUCENT);
//            return image;
//        }

        return image;
    }

    /**
     * Converts an image to a volatile image.
     *
     * @param image the image to convert
     * @return the converted image
     */
    public static VolatileImage convertToVolatileImage(BufferedImage image) {
        VolatileImage result =
                ImageUtils.createVolatileImage(image.getWidth(),
                image.getHeight());
        Graphics2D g2 = result.createGraphics();
        g2.drawImage(image, null, 0, 0);
        return result;
    }

    /**
     * Returns a new compatible image.
     *
     * @param width the image width
     * @param height the image height
     * @return the compatible image
     */
    public static BufferedImage createCompatibleImage(int width, int height) {
        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsConfiguration gc =
                ge.getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage out = gc.createCompatibleImage(width, height,
                Transparency.TRANSLUCENT);

        return out;
    }

    /**
     * Converts an image to a compatible buffered image (increases performance).
     *
     * @param image the image to convert
     * @return the converted image
     */
    public static BufferedImage convertToCompatibleImage(BufferedImage image) {
        BufferedImage result =
                ImageUtils.createCompatibleImage(image.getWidth(),
                image.getHeight());
        Graphics2D g2 = result.createGraphics();
        g2.drawImage(image, null, 0, 0);
        return result;
    }

    /**
     * Converts an image to a buffered image with specified color model/format.
     *
     * @param image the image to convert
     * @param imageType image color format/model
     * @return the converted image
     */
    public static BufferedImage convertToBufferedImage(
            BufferedImage image, int imageType) {
        BufferedImage result = new BufferedImage(image.getWidth(),
                image.getHeight(), imageType);
        Graphics2D g2 = result.createGraphics();
        g2.drawImage(image, null, 0, 0);
        return result;
    }

    /**
     * Clears an imager.
     *
     * @param image the image to clear
     */
    public static void clearImage(BufferedImage image) {
        Graphics2D g2 = image.createGraphics();

        g2.setComposite(AlphaComposite.Clear);
        g2.setPaint(new Color(0.f, 0.f, 0.f, 0.f));
        g2.fillRect(0, 0, image.getWidth(), image.getHeight());
    }

    /**
     * Blurs an image.
     *
     * @param image the image to blur
     * @param blurValue the blur value
     * @return the blurred image
     */
    public static BufferedImage blurImage(
            BufferedImage image, float blurValue) {
        float middle = blurValue;
        float value = (1.f - middle) / 8f;

        float[] BLUR = new float[]{
            value, value, value,
            value, middle, value,
            value, value, value
        };

        ConvolveOp vBlurOp = new ConvolveOp(new Kernel(3, 3, BLUR));

        return vBlurOp.filter(image, null);
    }

    /**
     * Creates a new BufferedImage instance and copies the content of the source
     * image to it.
     *
     * @param source the image that is to be cloned
     * @return the cloned image
     */
    public static BufferedImage cloneImage(BufferedImage source) {
        BufferedImage result =
                createCompatibleImage(source.getWidth(), source.getHeight());

        result.getGraphics().drawImage(source, 0, 0, null);

        return result;
    }
//    /**
//     * PErforms a perspective transformation.
//     * @param source
//     * @param p1 the bottom-left point
//     * @param p2 the bottom-right point
//     * @param p3 the top-right point
//     * @param p4 the top-left point
//     * @return the transformed image
//     */
//    public static BufferedImage perspectiveTransform(
//            BufferedImage source,
//            Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
//        BufferedImage img = cloneImage(source);
//
//        ParameterBlock params = new ParameterBlock();
//
//        // define the input image
//        params.addSource(img);
//
//        int w = source.getWidth();
//        int h = source.getHeight();
//
//        // compute the perspective transform
//        int tlX = (int) (p4.getX() * w);
//        int tlY = (int) ((1 - p4.getY()) * h);
//        int blX = (int) (p1.getX() * w);
//        int blY = (int) ((1 - p1.getY()) * h);
//        int brX = (int) (p2.getX() * w);
//        int brY = (int) ((1 - p2.getY()) * h);
//        int trX = (int) (p3.getX() * w);
//        int trY = (int) ((1 - p3.getY()) * h);
//
//        PerspectiveTransform pt = PerspectiveTransform.getQuadToQuad(
//                0, 0,
//                0, h,
//                w, h,
//                w, 0,
//                tlX, tlY,
//                blX, blY,
//                brX, brY,
//                trX, trY);
//        try {
//            pt = pt.createInverse();
//        } catch (NoninvertibleTransformException ex) {
//            Logger.getLogger(ImageUtils.class.getName()).
//                    log(Level.SEVERE, null, ex);
//        } catch (CloneNotSupportedException ex) {
//            Logger.getLogger(ImageUtils.class.getName()).
//                    log(Level.SEVERE, null, ex);
//        }
//
//        Warp warp = new WarpPerspective(pt);
//        params.add(warp);
//        params.add(Interpolation.getInstance(Interpolation.INTERP_BILINEAR));
//        RenderedOp dest = JAI.create("warp", params);
//        source = dest.getAsBufferedImage();
//        return source;
//    }

    /**
     * Returns a scaling transform to fit the source object exactly in the
     * destination object. This can be used to draw an image inside another one
     * without creating a scaled image.
     *
     * @param dimSrc the dimension of the source object
     * @param dimDest the dimension of the destination object
     * @return the transform
     */
    public static AffineTransform createMatchingTransform(
            Dimension dimSrc, Dimension dimDest) {
        AffineTransform result = new AffineTransform();
        float scaleX = (float) (dimDest.getWidth() / dimSrc.getWidth());
        float scaleY = (float) (dimDest.getHeight() / dimSrc.getHeight());

        result.scale(scaleX, scaleY);

        return result;
    }

    /**
     * Converts image to buffered image.
     *
     * @param image image to convert
     * @return image as buffered image
     */
    public static BufferedImage convertToBufferedImage(Image image) {

        // based on http://www.exampledepot.com/egs/java.awt.image/image2buf.html


        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see Determining If an Image Has Transparent Pixels
        boolean hasAlpha = false; //hasAlpha(image);

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                    image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(
                    image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }
}
