/* 
 * OBJ2Geometry.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2012 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2012 by Michael Hoffer
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
 * First, the following text must be displayed on the Canvas:
 * "based on VRL source code". In this case the VRL canvas icon must be removed.
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
 * Computing and Visualization in Science, 2011, in press.
 */

package eu.mihosoft.vrl.v3d;

import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import javax.media.j3d.Geometry;
import javax.vecmath.Point3f;

/**
 * <p>Converts simple obj files to java 3d geometries.</p>
 * <p>
 * The format is relatively simple:
 * </p>
 * <code>
 * <pre>
 * v node_x node_y node_z
 * .
 * .
 * .
 * f node_index_1 node_index_2 node_index_3
 * </pre>
 * </code>
 * <p>
 * Example (tetrahedron):
 * </p>
 * <code>
 * <pre>
 * v  1  1 -1
 * v -1 -1 -1
 * v -1  1  1
 * v  1 -1  1
 * f 1 2 3
 * f 1 2 4
 * f 1 3 4
 * f 2 3 4
 * </pre>
 * </code>
 * <p>
 * All other information in the obj file is ignored. Currently only triangles
 * are supported.
 * <p>
 * @author Michael Hoffer <info@michaelhoffer.de>
 * @see VTriangleArray
 */
public class OBJ2Geometry {

    /**
     * Loads text file to a geometry array.
     * @param in the input stream
     * @return the geometry
     * @throws java.io.IOException
     */
    public Geometry loadObj(
            @ParamInfo(name = "Input File:",
            style = "default") InputStream in) throws IOException {
        return loadAsVTriangleArray(in).getTriangleArray();
    }

    /**
     * Loads text file to a geometry array.
     * @param file the file
     * @return the geometry
     * @throws java.io.IOException
     */
    public Geometry loadObj(
            @ParamInfo(name = "Input File:",
            style = "load-dialog") File file) throws IOException {
        return loadAsVTriangleArray(file).getTriangleArray();
    }

    /**
     * Loads text file to a VTriangleArray.
     * @param file the file
     * @return the VTriangleArray
     * @throws java.io.IOException
     */
    public VTriangleArray loadAsVTriangleArray(
            @ParamInfo(name = "Input File:",
            style = "load-dialog") File file) throws IOException {
        return loadAsVTriangleArray(new FileInputStream(file));
    }

    /**
     * Loads text file to a VTriangleArray.
     * @param in the stream
     * @return the VTriangleArray
     * @throws java.io.IOException
     */
    public VTriangleArray loadAsVTriangleArray(
            @ParamInfo(name = "Input File:",
            style = "default") InputStream in) throws IOException {

        VTriangleArray triangleArray = null;
        BufferedReader reader = null;

        IOException exception = null;

        try {

            reader = new BufferedReader(new InputStreamReader(in));

            StringTokenizer stringTokenizer = null;

            String line = reader.readLine();

            Nodes nodes = new Nodes();

            triangleArray = new VTriangleArray();

            int i = 1;
            // read nodes
            while (!line.trim().startsWith("f")) {

                if (line.trim().startsWith("v") && !line.trim().startsWith("vn")) {
                    line = line.replace("v", "");

                    stringTokenizer = new StringTokenizer(line);

                    Node n = readNode(stringTokenizer);
                    n.setIndex(i);

                    nodes.addNode(i, n);
                    i++;
                }

                line = reader.readLine();
            }

            System.out.println(">> # Nodes: " + (i));

            nodes.centerNodes();

            i = 1;

            // read nodes
            while (line != null) {

                if (line.trim().startsWith("f")) {
                    line = line.replace("f", "");

                    stringTokenizer = new StringTokenizer(line);

                    Triangle t = readTriangle(stringTokenizer);

                    // collect node coordinates
                    Node n = nodes.getNode(t.getNodeOne().getIndex());
                    t.setNodeOne(n);
                    n = nodes.getNode(t.getNodeTwo().getIndex());
                    t.setNodeTwo(n);
                    n = nodes.getNode(t.getNodeThree().getIndex());
                    t.setNodeThree(n);

                    // add triangle to triangle array
                    triangleArray.addTriangle(t);

                    t.setIndex(i);

                    i++;
                }

                line = reader.readLine();
            }

            System.out.println(">> # Triangles: " + i);

        } catch (IOException ex) {
            exception = ex;
        } finally {

            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ex) {
                    //
                }
            }
        }

        if (exception != null) {
            throw exception;
        }

        return triangleArray;
    }

    /**
     * Reads a node.
     * @param stringTokenizer the string tokenizer to use for reading
     * @return the node
     * @throws java.io.IOException
     */
    private Node readNode(StringTokenizer stringTokenizer) throws IOException {

        Node node = new Node();

        float x = Float.parseFloat(stringTokenizer.nextToken());
        float y = Float.parseFloat(stringTokenizer.nextToken());
        float z = Float.parseFloat(stringTokenizer.nextToken());

        node.setLocation(new Point3f(x, y, z));

        return node;
    }

    /**
     * Reads a triangle.
     * @param stringTokenizer the string tokenizer to use for reading
     * @return the read triangle
     * @throws java.io.IOException
     */
    private Triangle readTriangle(
            StringTokenizer stringTokenizer) throws IOException {

        Triangle t = new Triangle();

        t.setNodeOne(
                new Node(Integer.parseInt(stringTokenizer.nextToken()), null));
        t.setNodeTwo(
                new Node(Integer.parseInt(stringTokenizer.nextToken()), null));
        t.setNodeThree(
                new Node(Integer.parseInt(stringTokenizer.nextToken()), null));

        return t;
    }
}
