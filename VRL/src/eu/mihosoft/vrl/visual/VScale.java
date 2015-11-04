/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.visual;

/**
 * 2-D Scale transform.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public final class VScale {

    private final double scaleX;
    private final double scaleY;

    public static final VScale UNITY = new VScale(1, 1);

    public VScale(double scaleX, double scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    /**
     * @return the scale X
     */
    public double getScaleX() {
        return scaleX;
    }

    /**
     * @return the scale Y
     */
    public double getScaleY() {
        return scaleY;
    }

    /**
     * Determines whether this scale transform is the identity transform, e.g.,
     * {@code getScaleX() == 1.0 && getScaleY() == 1.0}
     *
     * @return {@code true} if this scale transform is the identity transform;
     * {@code false} otherwise
     */
    public boolean isIdentity() {
        return Double.compare(getScaleX(), 1.0) == 0
                && Double.compare(getScaleY(), 1.0) == 0;
    }
}
