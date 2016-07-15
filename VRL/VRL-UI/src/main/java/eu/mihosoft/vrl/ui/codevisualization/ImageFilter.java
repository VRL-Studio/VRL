/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.ui.codevisualization;

import eu.mihosoft.vrl.ext.com.jhlabs.image.BoxBlurFilter;
import java.awt.image.BufferedImage;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ImageFilter {

    public static BufferedImage blur(BufferedImage img, int size) {
        BoxBlurFilter filter = new BoxBlurFilter();

        filter.setRadius(size);
        
        return filter.filter(img, null);
    }
}
