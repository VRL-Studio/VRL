/* 
 * Connection.java
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

import eu.mihosoft.vrl.animation.LinearInterpolation;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents the connection between two connectors. Each
 * connection has the visual appearance of a flexible wire. 
 * @author Michael Hoffer <info@michaelhoffer.de>
 * @see Connector
 */
public class Connection implements CanvasChild,
        BufferedPainter, Serializable {

    private static final long serialVersionUID = -7205140024782323764L;
    /**
     * The sending Connector object.
     */
    private Connector sender;
    /**
     * The receiving Connector object.
     */
    private Connector receiver;
    /**
     * Defines whether the connection is to be visible or not.
     */
    private boolean visible;
    /**
     * The Canvas object the Connections object
     * belongs to.
     */
    private Canvas mainCanvas;
    private float transparency = 1.f;
    private double colorValue = 0.0;
    private LinearInterpolation redTarget = new LinearInterpolation(0, 0);
    private LinearInterpolation greenTarget = new LinearInterpolation(0, 0);
    private LinearInterpolation blueTarget = new LinearInterpolation(0, 0);
    private LinearInterpolation thicknessTarget = new LinearInterpolation(0, 0);
    private boolean animating = true;
    private Style styleBuffer;
    private Point previousSenderLocation;
    private Point previousReceiverLocation;
    public static final String ACTIVE_CONNECTION_COLOR_KEY =
            "Connection[active]:color";
    public static final String CONNECTION_COLOR_KEY = "Connection[inactive]:color";
    public static final String ACTIVE_CONNECTION_THICKNESS_KEY =
            "Connection[active]:thickness";
    public static final String CONNECTION_THICKNESS_KEY =
            "Connection[inactive]:thickness";

    /**
     * Constructor.
     */
    public Connection() {
    }

//    /**
//     * Constructor.
//     * @param mainCanvas the main canvas object
//     */
//    public Connection(Canvas mainCanvas) {
//        setMainCanvas(mainCanvas);
////        setVisible(true);
//    }
//    /**
//     * Creates a Connection object which represents a connection
//     * between two connectors.
//     *
//     * @param s sender
//     * @param r receiver
//     */
//    public Connection(Connector s, Connector r) {
//        setSender(s);
//        setReceiver(r);
//        sortConnectors();
//        setMainCanvas(s.getMainCanvas());
//        setVisible(true);
//    }
    /**
     * Creates a Connection object which represents a connection
     * between two connectors.
     *
     * @param s sender
     * @param r receiver
     */
    public Connection newInstance(Connector s, Connector r) {
        Connection c = null;
        try {
            c = getClass().newInstance();

            c.setSender(s);
            c.setReceiver(r);
            c.sortConnectors();
            c.setMainCanvas(s.getMainCanvas());
            c.setVisible(true);
        } catch (InstantiationException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return c;
    }

    /**
     * This method has to deal with the following problem:
     * from within the canvas a connection can be established by dragging the
     * sender connector to the reciever and vice versa. Therefore the Connection
     * class needs a methods which sorts the connectors, i.e. distinguish the 
     * sender and receiver.
     */
    private void sortConnectors() {
        Connector c1 = sender;
        Connector c2 = receiver;

        boolean c1IsOutput = c1.isOutput();
        boolean c1IsInput = c1.isInput();

        boolean c2IsOutput = c2.isOutput();
        boolean c2IsInput = c2.isInput();

        boolean bothAreOutput = c1IsOutput && c2IsOutput;
        boolean bothAreInput = c1IsInput && c2IsInput;

        boolean differentConnectorType = !bothAreOutput && !bothAreInput;

        assert differentConnectorType :
                "connector types are equal, cannot sort!";

        if (c1IsInput) {
            sender = c2;
            receiver = c1;
        }
    }

    /**
     * Returns the sender connector.
     * @return the sender connector
     */
    public Connector getSender() {
        return sender;
    }

    /**
     * Sets the sender.
     * @param sender sender
     */
    public void setSender(Connector sender) {
        this.sender = sender;
    }

    /**
     * Returns the receiver connector.
     * @return the receiver connector
     */
    public Connector getReceiver() {
        return receiver;
    }

    /**
     * Sets the receiver.
     * @param receiver the receiver to set
     */
    public void setReceiver(Connector receiver) {
        this.receiver = receiver;
    }

    /**
     * Paints the connection. This method uses the well known Bezier curves
     * to draw the connection.
     * @param g the Graphics context in which to paint
     */
    public void paint(Graphics g) {
        if (isVisible()) {

            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Composite originalComposite = g2.getComposite();

            AlphaComposite ac1 =
                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    getTransparency());
            g2.setComposite(ac1);

            Point startLocation = sender.getAbsPos();

            int startLocationX = startLocation.x + sender.getWidth() / 2;
            int startLocationY = startLocation.y + sender.getHeight() / 2;

            Point targetLocation = receiver.getAbsPos();

            int targetLocationX = targetLocation.x + receiver.getWidth() / 2;
            int targetLocationY = targetLocation.y + receiver.getHeight() / 2;

            Style style = getMainCanvas().getStyle();

            // watch style changings and trigger value update
            if (getStyleBuffer() == null || !styleBuffer.equals(style)) {
                setStyleBuffer(style);
                contentChanged();
            }

            // color animation
            if (isAnimating()) {
                setAnimating(false);

                getRedTarget().setStartValue(
                        style.getBaseValues().
                        getColor(ACTIVE_CONNECTION_COLOR_KEY).getRed());
                getRedTarget().setStopValue(
                        style.getBaseValues().
                        getColor(CONNECTION_COLOR_KEY).getRed());
                getGreenTarget().setStartValue(
                        style.getBaseValues().
                        getColor(ACTIVE_CONNECTION_COLOR_KEY).getGreen());
                getGreenTarget().setStopValue(
                        style.getBaseValues().
                        getColor(CONNECTION_COLOR_KEY).getGreen());
                getBlueTarget().setStartValue(
                        style.getBaseValues().
                        getColor(ACTIVE_CONNECTION_COLOR_KEY).getBlue());
                getBlueTarget().setStopValue(
                        style.getBaseValues().
                        getColor(CONNECTION_COLOR_KEY).getBlue());
                getThicknessTarget().setStartValue(
                        style.getBaseValues().
                        getFloat(ACTIVE_CONNECTION_THICKNESS_KEY));
                getThicknessTarget().setStopValue(
                        style.getBaseValues().
                        getFloat(CONNECTION_THICKNESS_KEY));

                getRedTarget().step(getColorValue());
                getGreenTarget().step(getColorValue());
                getBlueTarget().step(getColorValue());
                getThicknessTarget().step(getColorValue());
            }

            g2.setColor(new Color((int) getRedTarget().getValue(),
                    (int) getGreenTarget().getValue(),
                    (int) getBlueTarget().getValue()));

            BasicStroke stroke =
                    new BasicStroke((float) getThicknessTarget().getValue(),
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND);

            g2.setStroke(stroke);


//            g2.draw(new Line2D.Double(startLocationX, startLocationY,
//                                      targetLocationX, targetLocationY));
            GeneralPath p = new GeneralPath();

            p.moveTo(startLocationX, startLocationY);

            int x1 = startLocationX + (targetLocationX - startLocationX) / 2;
            int y1 = startLocationY;//+(targetLocationY - startLocationY)/2+30;

            int xMid = startLocationX + (targetLocationX - startLocationX) / 2;
            int yMid = startLocationY + (targetLocationY - startLocationY) / 2;

            int x2 = targetLocationX - (targetLocationX - startLocationX) / 2;
            int y2 = targetLocationY;//+(targetLocationY - startLocationY)/2+30;

//            int xDist = Math.abs(targetLocationX - startLocationX);
//            int yDist = Math.abs(targetLocationY - startLocationY);
//            
//            double dist = Math.sqrt(xDist*xDist+yDist*yDist);
//            
//            x1+=35*dist/100;
//            x2-=35*dist/100;

            p.curveTo(x1, y1, x2, y2, targetLocationX, targetLocationY);

            g2.draw(p);

            g2.setComposite(originalComposite);

            // if this connection changes location
            // (currently indicated by start and target position change)
            // repaint the canvas because the new and hopefully more
            // efficient repaint manager does not repaint the canvas
            // automatically anymore
            //
            // this caused drawing bugs when drawing two minimized windows
            // that were connected
            boolean receiverMoved =
                    getPreviousSenderLocation() == null
                    || !previousSenderLocation.equals(startLocation);

            boolean senderMoved =
                    getPreviousReceiverLocation() == null
                    || !previousReceiverLocation.equals(targetLocation);

            if (senderMoved || receiverMoved || isAnimating()) {
                contentChanged();

                setPreviousSenderLocation(startLocation);
                setPreviousReceiverLocation(targetLocation);
            }
        }
    }

    /**
     * <p>
     * Adds a dirty region to the current repaint manager. That is, it requests
     * repainting of the specified area of the canvas. All child components
     * inside this region will also be repainted.
     * </p>
     * <p>
     * <b>Note:</b> Values with <code>x2 < x1</code> or <code>y2 < y1</code>
     * </p>
     * are allowed.
     * @param x1 first x coordinate
     * @param y1 first y coordinate
     * @param x2 second x coordinate
     * @param y2 second y coordinate
     * @param thickness
     */
    private void addDirtyRegion(int x1, int y1, int x2, int y2, double thickness) {
        int dirtyThickness = (int) thickness;

        int dirtyX = Math.min(x1, x2) - dirtyThickness - 50;
        int dirtyY = Math.min(y1, y2) - dirtyThickness - 50;

        int dirtyW = Math.abs(x1 - x2) + dirtyThickness * 2 + 100;
        int dirtyH = Math.abs(y1 - y2) + dirtyThickness * 2 + 100;

        VSwingUtil.repaintRequestOnComponent(
                mainCanvas,
                dirtyX, dirtyY,
                dirtyW, dirtyH);
    }

    /**
     * Returns true if sender and receiver of compared Connection objects are
     * equal. Returns false otherwise.
     * @param o the Connection object that is to be comared
     * @return returns true if objects are equal, returns false otherwise
     */
    @Override
    // TODO implement corresponding hashCode() - Method!
    public boolean equals(Object o) {
        Connection c = (Connection) o;
        if ((this.getSender() == c.getSender())
                && (this.getReceiver() == c.getReceiver())) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the Connection object is visible or not.
     * @return returns true if the Connection object is visible, returns false
     * otherwise.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Defines whether this Connection is visible or not.
     * @param visible value range: [true, false]
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
        this.visible = visible && getTransparency() > 0;
    }

    @Override
    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    @Override
    public void setMainCanvas(Canvas mainCanvas) {
        assert mainCanvas != null;
        this.mainCanvas = mainCanvas;
    }

    /**
     * Returns the transparency of this connection.
     * @return the transparency of this connection
     */
    public float getTransparency() {
        return transparency;
    }

    /**
     * Defines the transparency of this connection.
     * @param transparency the transparency to set
     */
    public void setTransparency(float transparency) {
        this.transparency = transparency;
        
        // TODO there is a nasty error in the animation and/or multithreading
        // code responsible for the connection visibility.
        // this is a workaround.
        setVisible(getTransparency() > 0);

        contentChanged();
    }

    /**
     * Defines the color value of this connection.
     * @param value the color value to set
     */
    public void setColorValue(double value) {
        this.colorValue = value;
        contentChanged();
    }

    /**
     * Indicates whether this connection is currently animation.
     * @return <code>true</code> if this connection is currently animating;
     *         <code>false</code> otherwise
     */
    public boolean isAnimating() {
        return animating;
    }

    /**
     * Defines whether this connection is currently animating.
     * @param animating the animation state to set
     */
    public void setAnimating(boolean animating) {
        this.animating = animating;
    }

    /**
     * TODO logical inconsistency: usually this method requests new buffer
     * //   eg for style changes etc. but here it is used as repaint()
     */
    @Override
    public void contentChanged() {
        if (isVisible()) {
            setAnimating(true);

            if (getPreviousSenderLocation() != null
                    && getPreviousReceiverLocation() != null) {
                addDirtyRegion(getPreviousSenderLocation().x,
                        getPreviousSenderLocation().y,
                        getPreviousReceiverLocation().x,
                        getPreviousReceiverLocation().y,
                        getThicknessTarget().getValue());
            }
        }
    }

    /**
     * @return the redTarget
     */
    public LinearInterpolation getRedTarget() {
        return redTarget;
    }

    /**
     * @return the greenTarget
     */
    public LinearInterpolation getGreenTarget() {
        return greenTarget;
    }

    /**
     * @return the blueTarget
     */
    public LinearInterpolation getBlueTarget() {
        return blueTarget;
    }

    /**
     * @return the thicknessTarget
     */
    public LinearInterpolation getThicknessTarget() {
        return thicknessTarget;
    }

    /**
     * @return the styleBuffer
     */
    public Style getStyleBuffer() {
        return styleBuffer;
    }

    /**
     * @param styleBuffer the styleBuffer to set
     */
    public void setStyleBuffer(Style styleBuffer) {
        this.styleBuffer = styleBuffer;
    }

    /**
     * @return the colorValue
     */
    public double getColorValue() {
        return colorValue;
    }

    /**
     * @return the previousSenderLocation
     */
    protected Point getPreviousSenderLocation() {
        return previousSenderLocation;
    }

    /**
     * @return the previousReceiverLocation
     */
    protected Point getPreviousReceiverLocation() {
        return previousReceiverLocation;
    }

    /**
     * @param previousSenderLocation the previousSenderLocation to set
     */
    protected void setPreviousSenderLocation(Point previousSenderLocation) {
        this.previousSenderLocation = previousSenderLocation;
    }

    /**
     * @param previousReceiverLocation the previousReceiverLocation to set
     */
    protected void setPreviousReceiverLocation(Point previousReceiverLocation) {
        this.previousReceiverLocation = previousReceiverLocation;
    }

    /**
     * Indicates whether this connection contains the given connector.
     * @param c connector
     * @return <code>true</code> if this connection contains the given
     *         connector; <code>false</code> otherwise
     */
    public boolean contains(Connector c) {
        return getSender().equals(c) || getReceiver().equals(c);
    }
//
//    @Override
//    protected void finalize() throws Throwable {
//
//        System.out.println(getClass().getName() + ": Finalize!!!");
//
//        super.finalize();
//    }
}
