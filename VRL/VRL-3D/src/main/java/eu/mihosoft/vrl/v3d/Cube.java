/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.v3d;

import java.util.ArrayList;
import java.util.List;


// Construct an axis-aligned solid cuboid. Optional parameters are `center` and
    // `radius`, which default to `[0, 0, 0]` and `[1, 1, 1]`. The radius can be
    // specified using a single number or a list of three numbers, one for each axis.

/**
 * An axis-aligned solid cuboid defined by {@code center} and {@code dimensions}.
 * 
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Cube implements Primitive{
    
    private Vector center;
    private Vector dimensions;

    public Cube() {
        center = new Vector(0, 0, 0);
        dimensions = new Vector(1, 1, 1);
    }

    public Cube(Vector center, Vector dimensions) {
        this.center = center;
        this.dimensions = dimensions;
    }
    

    @Override
    public List<Polygon> toPolygons() {
        Vector c = getCenter();
        Vector d = getDimensions();

        int[][][] a = {
            {{0, 4, 6, 2}, {-1, 0, 0}},
            {{1, 3, 7, 5}, {+1, 0, 0}},
            {{0, 1, 5, 4}, {0, -1, 0}},
            {{2, 6, 7, 3}, {0, +1, 0}},
            {{0, 2, 3, 1}, {0, 0, -1}},
            {{4, 5, 7, 6}, {0, 0, +1}}
        };
        List<Polygon> polygons = new ArrayList<>();
        for (int[][] info : a) {
            List<Vertex> vertexes = new ArrayList<>();
            for (int i : info[0]) {
                Vector pos = new Vector(
                        // TODO 
                        c.x + d.x * (2 * Utils.doubleNegate(i & 1) - 1),
                        c.y + d.y * (2 * Utils.doubleNegate(i & 2) - 1),
                        c.z + d.z * (2 * Utils.doubleNegate(i & 4) - 1)
                );
                vertexes.add(new Vertex(pos, new Vector(
                        (double) info[1][0],
                        (double) info[1][1],
                        (double) info[1][2]
                )));
            }
            polygons.add(new Polygon(vertexes, false));
        }
        return polygons;
    }

    /**
     * @return the center
     */
    public Vector getCenter() {
        return center;
    }

    /**
     * @param center the center to set
     */
    public void setCenter(Vector center) {
        this.center = center;
    }

    /**
     * @return the dimensions
     */
    public Vector getDimensions() {
        return dimensions;
    }

    /**
     * @param dimensions the dimensions to set
     */
    public void setDimensions(Vector dimensions) {
        this.dimensions = dimensions;
    }

     
}
