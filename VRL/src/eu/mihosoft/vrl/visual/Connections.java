/* 
 * Connections.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2007–2017 by Michael Hoffer
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
 * Third, add an additional notice, stating that you modified VRL. A suitable
 * notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Please cite the publication(s) listed below.
 *
 * Publications:
 *
 * M. Hoffer, C. Poliwoda, & G. Wittum. (2013). Visual reflection library:
 * a framework for declarative GUI programming on the Java platform.
 * Computing and Visualization in Science, 2013, 16(4),
 * 181–192. http://doi.org/10.1007/s00791-014-0230-y
 */

package eu.mihosoft.vrl.visual;

import eu.mihosoft.vrl.animation.Animation;
import eu.mihosoft.vrl.animation.ColorTransitionAnimation;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

// XML

/**
 * Represents a list of all connections inside a Canvas object.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Connections extends ArrayList<Connection>
        implements GlobalBackgroundPainter {

    private static final long serialVersionUID = -5555228202214634468L;
    /**
     * The Canvas object the Connections object
     * belongs to.
     */
    private Canvas mainCanvas;
    private Connection prototype = new Connection();

    /**
     * Creates an empty Connections object.
     * @param mainCanvas the Canvas object the Connections object
     * belongs to
     */
    public Connections(Canvas mainCanvas) {
        super();
        setMainCanvas(mainCanvas);
    }

//    /**
//     * Saves the connection list via XML encoder.
//     * @param e the XML encoder to use for serialization
//     */
//    public void save(XMLEncoder e) {
//        AbstractConnections connections = new AbstractConnections();
//        for (Connection c : this) {
//            connections.add(new AbstractConnection(c));
//        }
//        e.writeObject(connections);
//    }

//    /**
//     * Loads a connection list via XML decoder.
//     * @param d the XML decoder to use for deserialization
//     */
//    // TODO maybe move this method to VisualCanvas or VisualObjectInspector
//    public void load(XMLDecoder d) {
//
//        Object result = d.readObject();
//
//        AbstractConnections connections = (AbstractConnections) result;
//        VisualCanvas canvas = (VisualCanvas) mainCanvas;
//
//        connections.addToCanvas(canvas,this);
//    }

    @Override
    public boolean add(Connection c) {
        c.setMainCanvas(mainCanvas);

        Animation a = new ColorTransitionAnimation(c);
        a.setDuration(0.3);
        mainCanvas.getAnimationManager().addAnimation(a);

        return super.add(c);
    }

    /**
     * Creates and adds a connection to the Connections object.
     * @param s sender object
     * @param r receiver object
     */
    public Connection add(Connector s, Connector r) {
        Connection c = prototype.newInstance(s, r);
        c.setMainCanvas(mainCanvas);

        Animation a = new ColorTransitionAnimation(c);
        a.setDuration(0.3);
        mainCanvas.getAnimationManager().addAnimation(a);


        super.add(c);

        return c;
    }
    /**
     * Removes a connection from the Connections object.
     * @param c Connection object that is to be removed
     */
    public void remove(Connection c) {

        Connector r = c.getReceiver();

        super.remove(c);
    }

//    /**
//     * Removes a connection from this list. The Connection object
//     * that is to be removed is described by its sender and receiver.
//     * @param s sender
//     * @param r receiver
//     */
//    public void remove(V s, V r) {
//
//        T tmpC = new T(s, r);
//
//        for (int i = this.size() - 1; i >= 0; i--) {
//            Connection c = super.get(i);
//
//            if (c.equals(tmpC)) {
//                remove(c);
//            }
//        }
//    }
    /**
     * Returns a reference to a connection.
     * @param i index of the Connection object that is to be returned
     * @return Connection object.
     */
    public Connection getConnection(int i) {
        return super.get(i);
    }

    @Override
    public void paintGlobal(Graphics g) {
        paintConnections(g);
    }

    /**
     * Paints representation of all Connection objects (depending on their
     * paint() - method.
     * @param g the Graphics context in which to paint 
     */
    public void paintConnections(Graphics g) {
        for (Connection i : this) {
            i.paint(g);
        }
    }

    /**
     * Removes all Connection objects from the Connections object.
     */
    public void removeAll() {
        ArrayList<Connection> delList = new ArrayList<Connection>();

        for (Connection o : this) {
            delList.add(o);
        }

        for (Connection i : delList) {
            remove(i);
        }
    }

    /**
     * Removes all Connection objects from the Connections object
     * that are connected to a specific connector.
     * @param o Connector object
     */
    public void removeAllWith(Connector o) {

        List<Connection> list = getAllWith(o);

        for (Connection c : list) {
            remove(c);
        }
    }

    /**
     * Removes all Connection objects from the Connections object
     * that are connected to a specific canvas window.
     * @param o the canvas window
     */
    public void removeAllWith(CanvasWindow o) {

        for (Connector c : o.getConnectors()) {
            removeAllWith(c);
        }
    }

    /**
     * Returns all Connection objects that are connected with type
     * representations inside the specified canvas window.
     * @param o the canvas window
     * @return a list containing all connections that are connected to
     *         type representations inside the specified canvas window
     */
    public List<Connection> getAllWith(CanvasWindow o) {
        ArrayList<Connection> connections = new ArrayList<Connection>();

        for (Connector c : o.getConnectors()) {
            connections.addAll(getAllWith(c));
        }

        return connections;
    }

    /**
     * Returns an ArrayList containing references to all Connection objects
     * that are connected to a specific Connector object.
     * @param o Connector object
     * @return ArrayList containing references to all Connection objects
     * that are conneced with Connector o
     */
    public List<Connection> getAllWith(Connector o) {
        ArrayList<Connection> list = new ArrayList<Connection>();
        for (int i = this.size() - 1; i >= 0; i--) {
            Connection c = this.get(i);

            if ((c.getSender().equals(o)) || (c.getReceiver().equals(o))) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Returns a reference to Connection object which is connected to sender
     * s and receiver r.
     * @param s sender
     * @param r receiver
     * @return Connection object which is connected to sender s and
     * receiver r; returns <code>null</code> if connection does not exist
     */
    public Connection get(Connector s, Connector r) {
        Connection tmpC = prototype.newInstance(s, r);
        for (int i = this.size() - 1; i >= 0; i--) {
            Connection c = this.get(i);
            if (c.equals(tmpC)) {
                return c;
            }
        }
        return null;
    }
    /**
     * Checks if Connector c is already connected to another connector.
     * @param c Connector that is to be checked
     * @return <code>true</code> if the Connector is already connected;
     *         <code>false</code> otherwise
     */
    public boolean alreadyConnected(Connector c) {
        return (!getAllWith(c).isEmpty());
    }

//    public Element getXMLDescription() {
//        Element connectionXML = new Element("connections");
//
//        for (Connection c : this) {
//            connectionXML.addContent(c.getXMLDescription());
//        }
//
//        return connectionXML;
//    }
//
//    public void loadXML(Element o) throws DataConversionException {
//        List<?> connectionsXML = o.getChildren("connection");
//
//        Iterator<?> iterator = connectionsXML.iterator();
//
//        while (iterator.hasNext()) {
//            Element cXML = (Element) iterator.next();
//            Connection c = new Connection(cXML, getMainCanvas());
//            this.add(c);
//        }
//    }
//    public void loadXML(String filename) throws JDOMException, IOException {
//        Document doc = new SAXBuilder().build(filename);
//
//        loadXML(doc.getRootElement());
//    }
//
//    public void writeXML() {
//        Document doc = new Document(getXMLDescription());
//
//        XMLOutputter out = new XMLOutputter();
//        try {
//            out.output(doc, System.out);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    public void writeXML(String filename) {
//        Document doc = new Document(getXMLDescription());
//        try {
//            FileOutputStream out = new FileOutputStream(filename);
//            XMLOutputter serializer =
//                    new XMLOutputter(Format.getPrettyFormat());
//            serializer.output(doc, out);
//            out.flush();
//            out.close();
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }
    /**
     * Returns the main canvas.
     * @return the main canvas
     */
    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    /**
     * Defines the main canvas.
     * @param mainCanvas the canvas to set
     */
    public final void setMainCanvas(Canvas mainCanvas) {
        assert mainCanvas != null;
        this.mainCanvas = mainCanvas;
    }

    /**
     * @return the prototype
     */
    public Connection getPrototype() {
        return prototype;
    }

    /**
     * @param prototype the prototype to set
     */
    public void setPrototype(Connection prototype) {
        this.prototype = prototype;
    }
    
}
