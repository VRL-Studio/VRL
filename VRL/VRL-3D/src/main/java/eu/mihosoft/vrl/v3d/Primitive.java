/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.v3d;

import java.util.List;

/**
 * A Primitive.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface Primitive {

    /**
     * Returns the polygons that define this primitive.
     *
     * <b>Note:</b> this method computes the polygons each time this method is
     * called. The polygons can be cached inside a {@link CSG} object.
     *
     * @return the polygons that define this primitive
     */
    public List<Polygon> toPolygons();

    /**
     * Returns this primitive as {@link CSG}.
     *
     * @return this primitive as {@link CSG}
     */
    public default CSG toCSG() {
        return CSG.fromPolygons(toPolygons());
    }
}
