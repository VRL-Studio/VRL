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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Main {

    public static void main(String[] args) {

//        CubeOptions smallCube = new CubeOptions();
//        smallCube.setCenter(new Vector(2, 2, 2));
        SphereOptions sphereOptions = new SphereOptions();
        sphereOptions.setRadius(1.8);
//        sphereOptions.setCenter(new Vector(1, 0, 0));

        CylinderOptions cylinderOptions = new CylinderOptions();
        cylinderOptions.setRadius(0.8);
        cylinderOptions.setStart(new Vector(0, -3, 0));
        cylinderOptions.setEnd(new Vector(0, 3, 0));

        CSG testObject = CSG.sphere(sphereOptions).
                subtract(CSG.cylinder(cylinderOptions));
        

        testObject = CSG.fromPolygons(testObject.clone().toPolygons());

        testObject = testObject.subtract(CSG.cube(new CubeOptions()));
        
        testObject.translate(new Vector(5, 0, 0));

        String stlString;

        stlString = testObject.toStlString();

//        stlString = CSG.cylinder(cylinderOptions).inverse().toStlString();
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
