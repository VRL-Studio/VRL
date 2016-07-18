/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.ui.codevisualization;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ImageUtils {

    public static BufferedImage createCompatibleImage(int w, int h) {
        GraphicsEnvironment ge = GraphicsEnvironment.
                getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gs.getDefaultConfiguration();

        // Create an image that does not support transparency
        BufferedImage bimage = gc.createCompatibleImage(w, h,
                Transparency.TRANSLUCENT);

        return bimage;
    }

    public static BufferedImage loadImageFromURL(String url) {

        try {
            URL path = new URL(url);
            return ImageIO.read(path);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ImageUtils.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ImageUtils.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
