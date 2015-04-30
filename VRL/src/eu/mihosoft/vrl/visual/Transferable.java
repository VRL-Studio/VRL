/* 
 * Transferable.java
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
package eu.mihosoft.vrl.visual;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import eu.mihosoft.vrl.reflection.VisualObject;
import java.util.List;

/**
 * <P>
 * Transferable is partly equivalent to the Java D&D technique. The difference
 * is that this transferable class is completely independent from OS specific
 * D&D gestures. This ensures that D&D inside of the canvas is always the same,
 * no matter which OS is used for running applications.
 * </p>
 * <p>
 * The transferable is always connected to it's parent and a wire is drawn from
 * the parent to it's current position.
 * </p>
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public final class Transferable extends JPanel implements
        MouseMotionListener, MouseListener, GlobalForegroundPainter,
        CanvasChild {

    private static final long serialVersionUID = 5256709881196340612L;
    /**
     * indicates whether the transferable is currently dragged or not
     */
    private boolean dragged;
    /**
     * the source object
     */
    private Connector sourceObject;
    /**
     * the main canvas object
     */
    private Canvas mainCanvas;
    /**
     * the transparency of the temporarily transferable connection
     */
    private float transparency = 1.0f;
    /**
     * the connection status
     *
     */
    private ConnectionStatus connectionStatus;
    /*
     * 
     */
    private Message statusMessage;
    /**
     *
     */
    private Connector currentTargetConnector;
    /**
     *
     */
    private boolean draggingAllowed = true;
    private Point previousSenderLocation;
    private Point previousReceiverLocation;

    /**
     * Creates a new instance of Transferable
     *
     * @param mainCanvas the Canvas object the Transferable object belongs to
     */
    public Transferable(Canvas mainCanvas) {
        // INIT
        setMainCanvas(mainCanvas);
        this.setVisible(true);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        setSourceObject(null);
        setDragged(false);

        setOpaque(false);

        setBorder(VSwingUtil.createDebugBorder());
    }

    @Override
    public void paintGlobal(Graphics g) {
        paintConnections(g);
        Point absPos = getAbsPos();
        g.drawRect((int) absPos.getX(), (int) absPos.getY(), getWidth(), getHeight());
    }

    /**
     * Paints the connections.
     *
     * @param g the Graphics context in which to paint
     */
    public void paintConnections(Graphics g) {
        if (isDragged()) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Composite originalComposite = g2.getComposite();

            AlphaComposite ac1
                    = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                            getTransparency());
            g2.setComposite(ac1);

            Style style = getMainCanvas().getStyle();

            float thickness = style.getBaseValues().getFloat(
                    Connection.ACTIVE_CONNECTION_THICKNESS_KEY);

            BasicStroke stroke
                    = new BasicStroke(thickness,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND);

            g2.setStroke(stroke);

            g2.setColor(style.getBaseValues().
                    getColor(Connection.ACTIVE_CONNECTION_COLOR_KEY));

            Point startLocation = sourceObject.getAbsPos();

            int startLocationX = startLocation.x
                    + sourceObject.getWidth() / 2;
            int startLocationY = startLocation.y
                    + sourceObject.getHeight() / 2;

            Point targetLocation = this.getAbsPos();

            int targetLocationX = targetLocation.x + this.getWidth() / 2;
            int targetLocationY = targetLocation.y + this.getHeight() / 2;

            GeneralPath p = new GeneralPath();

            p.moveTo(startLocationX, startLocationY);

            int x1 = startLocationX + (targetLocationX - startLocationX) / 2;
            int y1 = startLocationY; //+ (targetLocationY - startLocationY)/2+30;

            int xMid = startLocationX + (targetLocationX - startLocationX) / 2;
            int yMid = startLocationY + (targetLocationY - startLocationY) / 2;

            int x2 = targetLocationX - (targetLocationX - startLocationX) / 2;
            int y2 = targetLocationY; //+ (targetLocationY - startLocationY)/2+30;

//            int xDist = Math.abs(targetLocationX - startLocationX);
//            int yDist = Math.abs(targetLocationY - startLocationY);
//            
//            double dist = Math.sqrt(xDist*xDist+yDist*yDist);
//            x1+=35*dist/100;
//            x2-=35*dist/100;
            p.curveTo(x1, y1, x2, y2, targetLocationX, targetLocationY);

            g2.draw(p);

            g2.setComposite(originalComposite);

            // set dirty rectangle to ensure our custom repaint manager repaints
            // the corresponding canvas area correctly
            if (previousReceiverLocation != null
                    && previousSenderLocation != null) {

                addDirtyRegion(getPreviousSenderLocation().x,
                        getPreviousSenderLocation().y,
                        getPreviousReceiverLocation().x,
                        getPreviousReceiverLocation().y,
                        thickness);
            }

            setPreviousSenderLocation(getSourceObject().getAbsPos());
            setPreviousReceiverLocation(
                    new Point(targetLocationX, targetLocationY));
        }
    }

    /**
     * Returns position relative to main canvas's upper left corner.
     *
     * @return transferable's position
     */
    public Point getAbsPos() {
        Component c = this.getParent();
        Point location = this.getLocation();

        while (!(c instanceof Canvas)) {
            location.x += c.getLocation().x;
            location.y += c.getLocation().y;

            c = c.getParent();

            if (c == null) {
                return null;
            }
        }
        return location;
    }

    @Override
    public void paintComponent(Graphics g) {
        // we want an invisible object
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
     *
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
     * <p>
     * If the transferable is currently dragged this method will check whether
     * the user
     * <ul>
     * <li> wants to create a connection by dragging the transferable object of
     * one connector to another.</li>
     * <li>wants to remove an existing connection by dragging the transferable
     * to a clear position of the canvas and releasing it (can only be performed
     * on an input connector)</li>
     * <li> wants to drag the transferable of an existing connection to another
     * connector (can only be performed on an input connector) </li>
     * </ul>
     * </p>
     *
     * @param ev the mouse event
     */
    @Override
    public void mouseDragged(MouseEvent ev) {
        if (getMainCanvas().getCapabilityManager().
                isCapable(CanvasCapabilities.ALLOW_CONNECT)) {
            Point newLocation
                    = new Point(this.getX() + ev.getX() - this.getWidth() / 2,
                            this.getY() + ev.getY() - this.getHeight() / 2);

            draggingAllowed = true;

            if (!isDragged()) {

                boolean weAreInput = getSourceObject().isInput();
                boolean weAreConnected = getSourceObject().getConnections().
                        alreadyConnected(this.getSourceObject());

                if (weAreInput && weAreConnected) {
                    Connection connection
                            = getSourceObject().
                            getConnections().getAllWith(sourceObject).get(0);

                    draggingAllowed = connection.isVisible();
                }

                if (draggingAllowed) {
                    getMainCanvas().setActualDraggable(this);
                    setDragged(true);
                    System.out.println(">> Transferable: Dragging!");
                } else {
                    System.out.println(">> Transferable: Dragging not allowed!");
                }

                if (draggingAllowed && weAreConnected && weAreInput) {

                    System.out.println(">> Transferable: "
                            + "Connection remove gesture performed!");

                    Connection connection
                            = getSourceObject().
                            getConnections().getAllWith(sourceObject).get(0);

                    Connector sourceConnector = this.getSourceObject();
                    Connector targetConnector = connection.getSender();

                    setConnectionStatus(ConnectionStatus.CONNECTION_REMOVED);

                    sourceConnector.connectionRemoved(connection);
                    targetConnector.connectionRemoved(connection);

                    List<Connection> connections;

                    connections = getSourceObject().getConnections().
                            getAllWith(sourceConnector);

                    // Inputs occure only once. Thus only one connection
                    // may exist.
                    //
                    // retrieve corresponding output
                    Connector c = connections.get(0).getSender();

                    // remove the connection
                    getSourceObject().getConnections().
                            removeAllWith(sourceConnector);
                    
                    setSourceObject(c);
                }
            }

//            // if either the method representation or object representation this
//            // transferable belongs to are not visible we manually trigger
//            // repaint of the canvas to prevent drawing bugs
//            if (!this.getSourceObject().getValueObject().getMethod().
//                    isVisible()
//                    || !this.getSourceObject().getValueObject().getMethod().
//                    getParentObject().isVisible()) {
//                mainCanvas.repaint();
//            }
            if (draggingAllowed) {
                this.setLocation(newLocation);
                checkConnection();
            }

        }
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
     * If the transferable is not dragged anymore it will be placed at the
     * position of it's parent.
     *
     * @param mouseEvent the mouse event
     */
    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        if (isDragged() == false) {
//            this.setLocation(0, 0);
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void mousePressed(MouseEvent ev) {
    }

    /**
     * If transferable has been released doConnect() will be called.
     *
     * @param ev the event
     */
    @Override
    public void mouseReleased(MouseEvent ev) {

        // indicates whether we were performing a dragging gesture before
        // releasing event occured
        boolean wasDragging = isDragged();

        setDragged(false);
        doConnect();

        // repaint canvas, otherwise the connection won't vanish
        // we tried to aviod this call before
        // this is new because we try to get rid of the custom repaint
        // manager
        // mainCanvas.repaint();
        //
        // the previous call seems to be needless. we will still use our custom
        // repaint manager in the near future (30.05.2011).
        // unselect all connectors. this is a fix for a very old and annoying
        // bug
        if (wasDragging) {
            for (Component comp : VSwingUtil.getAllChildren(getMainCanvas(),
                    Connector.class)) {
                Connector c = (Connector) comp;
                c.setSelected(false);
            }
        }
        
        setSourceObject(getParent());
    }

    /**
     * Checks a connection between two connectors. Connections are valid if
     * <ul>
     * <li>connectors are of different type, i.e. one is an input connector and
     * the other is an output connector</li>
     * <li>the target connector, i.e. the input connector is not already
     * connected, as we only allow one connection per input</li>
     * <li>types of both typerepresentation objects are equal</li>
     * </ul>
     *
     * @return the connection to add/remove or <code>null</code> if no correct
     * connection mouse gesture detected
     * @see #doConnect()
     */
    public Connection checkConnection() {
        Connection result = null;

        // default status
        setConnectionStatus(ConnectionStatus.NO_CONNECTION);
        setStatusMessage(null);

        // we don't do any further checks as dragging and thus connecting
        // is not allowed
        if (!draggingAllowed) {
            return null;
        }

        Point position = getAbsPos();

        int posX = position.x + getWidth() / 2;
        int posY = position.y + getHeight() / 2;

        // we can't use getComponentAt as this would return the effectPane
        // thats why we iterate through the objects and return the lowest
        // child under the effectPane
        Component c = null;

        for (Component i : mainCanvas.getComponents()) {

            // check if the transferable is dropped on the component
            boolean xRange
                    = (posX > i.getX()) && (posX < i.getX() + i.getWidth());
            boolean yRange
                    = (posY > i.getY()) && (posY < i.getY() + i.getHeight());

            boolean contains = xRange && yRange;

            if (contains && (!(i instanceof EffectPane)) && i.isVisible()) {
                c = i;
                break;
            }
        }

        if (c instanceof CanvasWindow) {

            VisualObject obj;

            if (c instanceof VisualObject) {
                obj = (VisualObject) c;
                setCurrentTargetConnector(obj.getSelected());
            }

            if (getCurrentTargetConnector() != null) {
                boolean weAreOutput = getSourceObject().isOutput();
                boolean targetIsConnected = getSourceObject().getConnections().
                        alreadyConnected(getCurrentTargetConnector());
                boolean targetIsOutput
                        = getCurrentTargetConnector().isOutput();
                boolean differentTargetType = weAreOutput != targetIsOutput;

//                Class sourceType =
//                        getSourceObject().getValueObject().getType();
//                Class targetType =
//                        getCurrentTargetConnector().getValueObject().getType();
                // only establish connection if connectors type are not equal,
                // i.e. not both are outputs and if the targetConnector is not
                // already connected (we only allow one connection per input)
                if (differentTargetType
                        && (!targetIsConnected || targetIsOutput)) {

                    ConnectionResult connectionResult
                            = getSourceObject().getValueObject().
                            compatible(getCurrentTargetConnector().
                                    getValueObject());

                    boolean compatibleValueType
                            = connectionResult.getStatus()
                            == ConnectionStatus.VALID;

//                    System.out.println("ConnectionResult: " + connectionResult.getStatus());
//                    System.out.println(">> check-msg: " + connectionResult.getMessage());
                    setStatusMessage(connectionResult.getMessage());

                    // only continue if types of both typerepresentation objects
                    // are equal
                    if (compatibleValueType) {

                        // connection valid, create it
                        result = getSourceObject().
                                getConnections().getPrototype().newInstance(
                                        getSourceObject(), getCurrentTargetConnector());

                        setConnectionStatus(ConnectionStatus.VALID);
                    } else {

                        // error: value type missmatch
                        getCurrentTargetConnector().setActiveColor(
                                (Color) mainCanvas.getStyle().getBaseValues().
                                get("Connector:errorColor"));

                        setConnectionStatus(
                                ConnectionStatus.ERROR_VALUE_TYPE_MISSMATCH);
                    }

                } else {
                    if (!differentTargetType && weAreOutput
                            && !sourceObject.equals(getCurrentTargetConnector())) {

                        // error: two outputs
                        setConnectionStatus(
                                ConnectionStatus.ERROR_BOTH_ARE_OUTPUTS);
                    }
                    if (!differentTargetType && (!weAreOutput
                            && !sourceObject.equals(getCurrentTargetConnector()))) {

                        // error: two inputs
                        setConnectionStatus(
                                ConnectionStatus.ERROR_BOTH_ARE_INPUTS);
                    }
                    if (differentTargetType && (targetIsConnected)) {

                        // error: more than one connection per input
                        setConnectionStatus(
                                ConnectionStatus.ERROR_INPUT_ALREADY_CONNECTED);
                    }
                }
            }

        } else {
            boolean weAreConnected = getSourceObject().getConnections().
                    alreadyConnected(this.getSourceObject());
            boolean weAreInput = getSourceObject().isInput();

            // only delete connection if we are performing gesture
            // on an input connector
            if (weAreConnected && weAreInput) {
                // remove gesture can only affect one connection, thus
                // we can safely get element 0
//                result = getMainCanvas().getConnections().
//                        getAllWith(getSourceObject()).get(0);

                // this case might not be used because it is handled inside
                // mouseDragged() method
                setConnectionStatus(
                        ConnectionStatus.CONNECTION_REMOVED);
            }
        }

        switch (getConnectionStatus()) {
            case VALID:
                getCurrentTargetConnector().setValidConnector(true);
                break;
            case NO_CONNECTION:
                // nothing to do
                break;

            default: // error case
                getCurrentTargetConnector().setValidConnector(false);

                getMainCanvas().getEffectPane().pulse(
                        getCurrentTargetConnector(), MessageType.ERROR_SINGLE);
                break;
        }

        return result;
    }

    /**
     * Creates a connection between two connectors if
     * <ul>
     * <li>connectors are of different type, i.e. one is an input connector and
     * the other is an output connector</li>
     * <li>the target connector, i.e. the input connector is not already
     * connected, as we only allow one connection per input</li>
     * <li>types of both typerepresentation objects are equal</li>
     * </ul>
     *
     * @see #checkConnection()
     */
    public void doConnect() {
        MessageBox mBox = getMainCanvas().getMessageBox();

        System.out.println(">> connection mouse gesture:");

        Connection connection = checkConnection();

        this.setLocation(0, 0);

        System.out.println(">>> status: " + getConnectionStatus());

        Message m = getStatusMessage();

        switch (getConnectionStatus()) {
            case VALID:
                getSourceObject().getConnections().add(connection);

                try {
                    // set ParamValues
                    connection.getReceiver().receiveData();
                } catch (Exception ex) {
                    mBox.addMessage("Can't receive data:",
                            ex.toString(), MessageType.ERROR);
                }
                break;
            case ERROR_VALUE_TYPE_MISSMATCH:
                System.out.println(">> Transferable/Connection: "
                        + "Source/Target type not equal!");

                if (m == null) {
                    m = new Message("Cannot establish connection:",
                            "Connection: "
                            + "Source/Target value type not equal!",
                            MessageType.ERROR_SINGLE);
                }

                mBox.addUniqueMessage(m,
                        getCurrentTargetConnector());

                break;
            case ERROR_BOTH_ARE_INPUTS:

                if (m == null) {
                    m = new Message("Cannot establish connection:",
                            "Connection: cannot establish connection"
                            + " between two inputs!",
                            MessageType.ERROR_SINGLE);
                }

                mBox.addUniqueMessage(m,
                        getCurrentTargetConnector());
                break;
            case ERROR_BOTH_ARE_OUTPUTS:
                if (m == null) {
                    m = new Message("Cannot establish connection:",
                            "Connection: cannot establish connection"
                            + " between two outputs!",
                            MessageType.ERROR_SINGLE);
                }

                mBox.addUniqueMessage(m,
                        getCurrentTargetConnector());
                break;
            case ERROR_INPUT_ALREADY_CONNECTED:
                if (m == null) {
                    m = new Message("Cannot establish connection:",
                            "Connection: cannot establish more than one "
                            + "connection per input!",
                            MessageType.ERROR_SINGLE);
                    mBox.addUniqueMessage(m,
                            getCurrentTargetConnector());
                }
                break;
            case ERROR_INVALID:
                if (m == null) {
                    m = new Message("Cannot establish connection:",
                            "Connection: the connection is invalid"
                            + " (no detailed description available)!",
                            MessageType.ERROR_SINGLE);
                    mBox.addUniqueMessage(m,
                            getCurrentTargetConnector());
                }
                break;

            case CONNECTION_REMOVED:
                // this case might not be used because it is handled inside
                // mouseDragged() method
                //
                // that is, nothing has to be done here
//                getMainCanvas().getConnections().
//                        removeAllWith(getSourceObject());
                break;
            default:
                break;
        } // end switch

//        // if either the method representation or object representation this
//        // transferable belongs to are not visible we manually trigger repaint
//        // of the canvas to prevent drawing bugs
//        if (!this.getSourceObject().getValueObject().getMethod().isVisible()
//                || !this.getSourceObject().getValueObject().getMethod().
//                getParentObject().isVisible()) {
//            mainCanvas.repaint();
//        }
        // release the reference to prevent memory leaks
        getMainCanvas().setActualDraggable(null);
    }

    /**
     * If mouse enters the transferable set it's parent color mode to active,
     * i.e., display it with the color set with <code>setActiveColor()</code>
     *
     * @param mouseEvent the mouse event
     */
    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        Connector i = (Connector) getParent();
        i.setBackground(i.getActiveColor());
        i.setSelected(true);

        try {

            List<Connection> connections
                    = getSourceObject().getConnections().
                    getAllWith(getCurrentTargetConnector());

        } catch (Exception ex) {
            //
        }
    }

    /**
     * If mouse leaves the transferable set its parent color mode to inactive,
     * i.e., display it with the color as defined by
     * <code>setInactiveColor()</code>
     *
     * @param mouseEvent the mouse event
     */
    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        Connector i = (Connector) getParent();
        i.setBackground(this.getParent().getParent().getBackground());
        i.setSelected(false);
    }

    /**
     * Returns the source object of the transferable.
     *
     * @return the source object of the transferable
     */
    public Connector getSourceObject() {
        return sourceObject;
    }

    /**
     * Defines the source object of the transferable.
     *
     * @param sourceObject the source object of the transferable
     */
    public void setSourceObject(Connector sourceObject) {
        this.sourceObject = sourceObject;
    }

    /**
     * Defines the source object of the transferable.
     *
     * @param sourceObject the source object of the transferable
     */
    public void setSourceObject(Object sourceObject) {
        this.sourceObject = (Connector) sourceObject;
    }

    /**
     * Checks if the transferable is cuurently be dragged.
     *
     * @return <code>true</code>, if the transferable is currently dragged;
     * <code>false</code> otherwise
     */
    public boolean isDragged() {
        return dragged;
    }

    /**
     * Defines whether the transferable is dragged or not.
     *
     * @param dragged <code>true</code>, if the transferable is currently
     * dragged;<code>false</code> otherwise
     */
    public void setDragged(boolean dragged) {
        this.dragged = dragged;
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
     * Returns the transparency of the transferable.
     *
     * @return the transparency of the transferable
     */
    public float getTransparency() {
        return transparency;
    }

    /**
     * Defines the transparency of the transferable.
     *
     * @param transparency the transparency of the transferable
     */
    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }

    /**
     * Returns the connection status.
     *
     * @return the connection status
     */
    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    /**
     * Defines the connection status.
     *
     * @param connectionStatus the connection status to set
     */
    private void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    /**
     * Returns the current target connector or <code>null</code> if no such
     * connector has ben defined.
     *
     * @return the current target connector
     */
    public Connector getCurrentTargetConnector() {
        return currentTargetConnector;
    }

    /**
     * Defines the current target connector.
     *
     * @param currentTargetConnector the connector to set
     */
    private void setCurrentTargetConnector(Connector currentTargetConnector) {
        this.currentTargetConnector = currentTargetConnector;
    }

    /**
     * @param statusMessage the statusMessage to set
     */
    public void setStatusMessage(Message statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * @return the statusMessage
     */
    public Message getStatusMessage() {
        return statusMessage;
    }
}
