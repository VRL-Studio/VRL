/* 
 * Main.java
 *
 * Copyright (c) 2009–2014 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2014 by Michael Hoffer
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
 * First, the following text must be displayed on the Canvas or an equivalent location:
 * "based on VRL source code".
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
 * Computing and Visualization in Science, in press.
 */
package eu.mihosoft.vrl.v3d;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Main {

    public static void main(String[] args) {
        
        Polygon p = Polygon.fromPoints(
                new Vector3d(0, 0),
                new Vector3d(1, 0),
                new Vector3d(1, 1),
                new Vector3d(0.5, 1),
                new Vector3d(0.5, 0.5),
                new Vector3d(0, 0.5)
        );

        CSG extruded = p.extrude(new Vector3d(0, 0, 1));
        Transform transform = Transform.unity().
                rotZ(25).
                rotY(15).
                rotX(25).
                translate(0, 0, -1).
                scale(0.5, 1.5, 1.5);

        CSG cube = new Cube(2).toCSG();
        CSG sphere = new Sphere(1.25).toCSG();
        CSG union = cube.union(sphere);

//                .difference(
////                        new Cube(new Vector3d(0, 0, 0), new Vector3d(2, 2, 2)).toCSG()
//                        p.extrude(new Vector3d(0, 0, 3)).
//                                transformed(transform)
//                )
//                ;
//                .intersect(
//                        new Cylinder().toCSG().
//                        transformed(
//                                Transform.unity().
//                                translate(new Vector3d(0, 0, 0)).
//                                scale(new Vector3d(1, 3, 1))
//                        )
//                );
        try {
            FileUtil.write(
                    Paths.get("obj.stl"),
                    boardAndPegs().toStlString());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static CSG peg() {
        //      ol
        //     | |
        //   __    _
        //  |  \   ptoph
        //  |   \  _
        //  |   /  pth 
        //  |  /   _
        //  | |    bt
        //  | |__  _
        //  |    | bh
        //  ------ -
        //  |pw |

        // pw    = peg width
        // bh    = board mounting height
        // bt    = board thickness
        // pth   = peg tooth hight
        // ptoph = peg top height
        // ol    = overlap between board and peg
        double outer_offset = 4;
        double inner_offset = 4;
        double board_mounting_height = 4;
        double board_thickness = 2;
        double overlap = 1;
        double peg_depth = 3;
        double peg_tooth_height = 1;
        double peg_top_height = 2;
        double board_spacing = 0.2;

        // inner offset
        double oi = inner_offset;
        //outer offset
        double oo = outer_offset;
        double bh = board_mounting_height;
        double bt = board_thickness;
        double ol = overlap;

        double pth = peg_tooth_height;
        double ptoph = peg_top_height;

        // board spacing (small spacing between peg and board, should be < 0.5mm)
        double bs = board_spacing;

        double pd = peg_depth;

        double pw = oo + oi;

        Polygon peg_points = Polygon.fromPoints(
                new Vector3d(0, 0),
                new Vector3d(pw, 0),
                new Vector3d(pw, bh / 5),
                new Vector3d(pw - oi / 2, bh),
                new Vector3d(oo - bs, bh),
                new Vector3d(oo - bs, bh + bt),
                new Vector3d(oo + ol, bh + bt + pth),
                new Vector3d(oo, bh + bt + pth + ptoph),
                new Vector3d(0, bh + bt + pth + ptoph)
        );

        return peg_points.
                transformed(Transform.unity().translateX(-oo)).
                extrude(new Vector3d(0, 0, pd)).
                transformed(Transform.unity().rotX(90).translateZ(-pd/2));
    }

    public static CSG board() {
        double board_thickness = 2;
        double bottom_thickness = 2;

        double board_mounting_height = 4;

        double outer_offset = 4;
        double inner_offset = 4;

        double board_width = 85.6;
        double board_height = 56;
        double bw = board_width;
        double bh = board_height;

        double sd1 = 14;
        double sd2 = 11;
        double sd3 = 18;

        Polygon board_points_exact = Polygon.fromPoints(
                new Vector3d(0, 0),
                new Vector3d(0, bh),
                new Vector3d(bw, bh),
                new Vector3d(bw, bh - sd1),
                new Vector3d(bw - sd3, bh - sd1),
                new Vector3d(bw - sd3, sd2),
                new Vector3d(bw, sd2),
                new Vector3d(bw, 0)
        );

// outer offset 
        double ox1 = outer_offset;
        double oy1 = outer_offset;

// inner offset
        double ox2 = inner_offset;
        double oy2 = inner_offset;
        
        Polygon board_points_outer = Polygon.fromPoints(
                new Vector3d(0-ox1,0-oy1),
                new Vector3d(0-ox1,bh+oy1),
                new Vector3d(bw+ox1,bh+oy1),
                new Vector3d(bw+ox1,bh-sd1),
                new Vector3d(bw-sd3,bh-sd1),
                new Vector3d(bw-sd3,sd2),
                new Vector3d(bw+ox1,sd2),
                new Vector3d(bw+ox1,0-oy1)
        );
        
        Polygon board_points_inner = Polygon.fromPoints(
                new Vector3d(0+ox2,0+oy2),
                new Vector3d(0+ox2,bh-oy2),
                new Vector3d(bw-ox2,bh-oy2),
                new Vector3d(bw-ox2,bh-sd1+oy2),
                new Vector3d(bw-sd3-ox2,bh-sd1+oy2),
                new Vector3d(bw-sd3-ox2,sd2-oy2),
                new Vector3d(bw-ox2,sd2-oy2),
                new Vector3d(bw-ox2,0+oy2)
        );

        CSG outer = board_points_outer.
                extrude(new Vector3d(0, 0, bottom_thickness));
        CSG inner = board_points_inner.
                extrude(new Vector3d(0, 0, bottom_thickness));
        
        return outer.difference(inner).transformed(Transform.unity().rotX(180).translateY(-bh));
    }
    
    public static CSG boardAndPegs() {
        
        double board_width = 85.6;
        double board_height = 56;
        double bw = board_width;
        double bh = board_height;
        
        double outer_offset = 4;
        
        double bottom_thickness = 2;
        
        CSG board = board();
        
        CSG peg1 = peg().transformed(Transform.unity().translate(0,bh-8,-bottom_thickness));

        CSG peg2 = peg().transformed(Transform.unity().translate(8,bh,-bottom_thickness).rotZ(90));
        
        
        CSG peg3 = peg().transformed(Transform.unity().translate(bw/2,bh,-bottom_thickness).rotZ(90));
        
//        translate([bw,outer_offset,0])
//rotate([0,0,180])
        CSG peg4 = peg().transformed(Transform.unity().translate(bw,bh-outer_offset,-bottom_thickness).rotZ(180));
        
//        translate([bw-12,bh,0])
//rotate([0,0,270])
        CSG peg5 = peg().transformed(Transform.unity().translate(bw-12,0,-bottom_thickness).rotZ(270));
        
//        translate([30,bh,0])
//rotate([0,0,270])
        CSG peg6 = peg().transformed(Transform.unity().translate(30,0,-bottom_thickness).rotZ(270));
        
        CSG union = board.union(peg1).union(peg2).union(peg3).union(peg4).union(peg5).union(peg6);
        
        return union;
        
//        return peg1;
    }

}
