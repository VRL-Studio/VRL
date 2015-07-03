/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.visual;

import java.awt.Component;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface TransformingParent {
    public double getScaleX();
    public double getScaleY();
    
    public static VScale getScale(Component c) {
        double scaleX = 1.0;
        double scaleY = 1.0;

        TransformingParent tp
                = (TransformingParent) VSwingUtil.
                getParent(c, TransformingParent.class);

        if (tp != null && scaleX > 1e-6 && scaleY > 1e-6) {
            scaleX = 1.0 / tp.getScaleX();
            scaleY = 1.0 / tp.getScaleY();
        }
        
        return new VScale(scaleX, scaleY);
    }
}
