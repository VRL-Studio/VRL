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

        TransformingParent tp = CachingParentForCanvas.
                getParent(c);

        if (tp != null && scaleX > 1e-6 && scaleY > 1e-6) {
            scaleX = 1.0 / tp.getScaleX();
            scaleY = 1.0 / tp.getScaleY();
        }

        if (Double.compare(1.0, scaleX) == 0
                && Double.compare(1.0, scaleY) == 0) {
            return VScale.UNITY;
        }

        return CachingVScaleForCanvas.newScale(scaleX, scaleY);
    }
}

class CachingVScaleForCanvas {

    private static VScale lastScale;

    public static VScale newScale(double sx, double sy) {

        if (lastScale == null) {
            lastScale = new VScale(sx, sy);
        } else if (Double.compare(lastScale.getScaleX(), sx) == 0
                && Double.compare(lastScale.getScaleY(), sy) == 0) {
        } else {
            lastScale = new VScale(sx, sy);
        }

        return lastScale;
    }
}

class CachingParentForCanvas {

    private static TransformingParent lastParent;

    public static TransformingParent getParent(Component c) {

        lastParent = (TransformingParent) VSwingUtil.
                getParent(c, TransformingParent.class);

        return lastParent;
    }
}
