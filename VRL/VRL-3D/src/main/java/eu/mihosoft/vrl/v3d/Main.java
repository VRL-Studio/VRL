/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.v3d;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Matrix4d;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Main {

    public static void main(String[] args) {
        Polygon p = Polygon.createFromPoints(
                Arrays.asList(new Vector(0, 0, 0),
                        new Vector(0, 1.0, 0),
                        new Vector(0.5, 1, 0),
                        new Vector(1, 0.5, 0),
                        new Vector(1, 0, 0)), true);

        Transform transform = Transform.unity().
                rotZ(25).
                rotY(25).
                rotX(25).
                translate(new Vector(0, 0, 3)).
                scale(new Vector(0.5, 1.5, 1.5));

        CSG testObject = new Sphere(1.5).toCSG().
                intersect(
                        new Cube(new Vector(0, 0, 0), new Vector(3, 0.5, 0.5)).toCSG()
//                        p.extrude(new Vector(0, 0, 3)).
//                                transformed(transform)
                );
//                .intersect(
//                        new Cylinder().toCSG().
//                        transformed(
//                                Transform.unity().
//                                translate(new Vector(0, 0, 0)).
//                                scale(new Vector(1, 3, 1))
//                        )
//                );

        String stlString;

        stlString = testObject.toStlString();

        BufferedWriter writer;

        try {
            writer = Files.newBufferedWriter(Paths.get("obj.stl"), Charset.defaultCharset(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            writer.write(stlString, 0, stlString.length());

            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
