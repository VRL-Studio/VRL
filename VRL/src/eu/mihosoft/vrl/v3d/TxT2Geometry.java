/* 
 * TxT2Geometry.java
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
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.StringTokenizer;
import javax.media.j3d.Geometry;
import javax.vecmath.Point3f;

/**
 * <p>Converts simple text files to java 3d geometries.</p>
 * <p>
 * The format is relatively simple:
 * </p>
 * <code>
 * <pre>
 * &#35;nodes &#35;triangles
 * node_index node_x node_y node_z
 * .
 * .
 * triangle_index node_index_1 node_index_2 node_index_3
 * .
 * .
 * </pre>
 * </code>
 * <p>
 * Example (tetrahedron):
 * </p>
 * <code>
 * <pre>
 * 4 4
 * 0  1  1 -1
 * 1 -1 -1 -1
 * 2 -1  1  1
 * 3  1 -1  1
 * 0 0 1 2
 * 1 0 1 3
 * 2 0 2 3
 * 3 1 2 3
 * </pre>
 * </code>
 * @author Michael Hoffer <info@michaelhoffer.de>
 * @see VTriangleArray
 */
public class TxT2Geometry implements Serializable {

    private static final long serialVersionUID = 3445929541515695754L;

    /**
     * Loads text file to a geometry array.
     * @param file the file
     * @return the geometry
     * @throws java.io.IOException
     */
    public Geometry loadTxt(
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

        BufferedReader reader = new BufferedReader(new FileReader(file));

        StringTokenizer stringTokenizer =
                new StringTokenizer(reader.readLine());

        int numberOfNodes = Integer.parseInt(stringTokenizer.nextToken());
        int numberOfTriangles = Integer.parseInt(stringTokenizer.nextToken());

        System.out.println("#Nodes: " + numberOfNodes);
        System.out.println("#Triangles: " + numberOfTriangles);

        Nodes nodes = new Nodes();

        VTriangleArray triangleArray = new VTriangleArray();

        for (int i = 0; i < numberOfNodes; i++) {
            stringTokenizer = new StringTokenizer(reader.readLine());
            Node n = readNode(stringTokenizer);
            nodes.addNode(n.getIndex(), n);
        }

//        nodes.centerNodes();
        
        for (int i = 0; i < numberOfTriangles; i++) {
            stringTokenizer = new StringTokenizer(reader.readLine());
            Triangle t = readTriangle(stringTokenizer);

            // collect node coordinates
            Node n = nodes.getNode(t.getNodeOne().getIndex());
            t.setNodeOne(n);
            n = nodes.getNode(t.getNodeTwo().getIndex());
            t.setNodeTwo(n);
            n = nodes.getNode(t.getNodeThree().getIndex());
            t.setNodeThree(n);

//            System.out.println("Triangle: " + t);

            // add triangle to triangle array
            triangleArray.addTriangle(t);
        }
        
        triangleArray.centerNodes();

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

        node.setIndex(Integer.parseInt(stringTokenizer.nextToken()));

        float x = Float.parseFloat(stringTokenizer.nextToken());
        float y = Float.parseFloat(stringTokenizer.nextToken());
        float z = Float.parseFloat(stringTokenizer.nextToken());

        node.setLocation(new Point3f(x, y, z));

//        System.out.println("Node: " + node);

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

        t.setIndex(Integer.parseInt(stringTokenizer.nextToken()));

        t.setNodeOne(
                new Node(Integer.parseInt(stringTokenizer.nextToken()), null));
        t.setNodeTwo(
                new Node(Integer.parseInt(stringTokenizer.nextToken()), null));
        t.setNodeThree(
                new Node(Integer.parseInt(stringTokenizer.nextToken()), null));

//        System.out.println("INDEX:" + t.getIndex());

        return t;
    }
}
