/* 
 * Connector.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007–2018 by Michael Hoffer,
 * Copyright (c) 2015–2018 G-CSC, Uni Frankfurt,
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn)
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

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseEvent;
import java.awt.geom.*;

/**
 * The Connector class represents a small drag/drop area where a Transferable
 * object can be dragged from or dropped to.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public abstract class Connector extends VComponent
        implements MouseMotionListener, MouseListener, StringIdObject {

    private static final long serialVersionUID = -6967588855062791189L;
    /**
     * connection list to operate on
     */
    private Connections connections;
    /**
     * Each Connector object has a dragging object that can be dragged on to
     * another Connector object to establish a connection between them.
     */
    private Transferable draggingObj;
    /**
     * defines whether the object is selected or not, i.e. whether it receives a
     * "mouse entered" event or not
     */
    private boolean selected;
    /**
     * Defines the objects active color. An Connector object is defined as
     * active whenever it receives a "mouse entered" event.
     */
    private Color activeColor;
    /**
     * Defines the objects inactive color. An Connector object is defined as
     * inactive if it receives a "mouse exited" event.
     */
    private Color inactiveColor;
    /**
     * the object's ID value, valid range: [0,MAX_INT]
     */
    private int ID;
    /**
     *
     */
    private String stringId;
    /**
     * defines if this connector is valid.
     */
    private boolean validConnector = true;
    /**
     * the connector size
     */
    private Integer connectorSize;
    /**
     * the value object the connector belongs to.
     */
    private ValueObject valueObject;

    /*
     * Connector type.
     */
    private ConnectorType type;
    /**
     *
     */
    public static String ACTIVE_COLOR_ERROR_KEY = "Connector[active]:errorColor";
    public static String ACTIVE_COLOR_VALID_KEY = "Connector[active]:validColor";
    public static String INACTIVE_COLOR_ERROR_KEY = "Connector[inactive]:errorColor";
    public static String INACTIVE_COLOR_VALID_KEY = "Connector[inactive]:validColor";
    public static String BORDER_THICKNESS_KEY = "Connector:thickness";
    public static String SIZE_KEY = "Connector:size";
    public static String TRANSPARENCY_KEY = "Connector:transparency";
    public static String BORDER_COLOR_KEY = "Connector:Border:color";
    public static int DEFAULT_SIZE = 9;
    /**
     *
     */
    /**
     * list of action listeners
     */
    private ArrayList<CanvasActionListener> actionListeners
            = new ArrayList<CanvasActionListener>();
    /**
     * Defines the identifier for connector receive action
     *
     */
    public static final String RECEIVE_ACTION = "receive-data";
    private final static Point ZERO = new Point(0, 0);

    /**
     * Creates a new instance of Connector.
     *
     * @param mainCanvas the main canvas object
     */
    public Connector(Canvas mainCanvas, Connections connections) {
        // INIT
        this.setMainCanvas(mainCanvas);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        draggingObj = new Transferable(mainCanvas);
        //draggingObj.setSourceObject (this);
        //this.add (draggingObj);
        this.setTransferable(draggingObj);
        this.setSelected(false);
        this.connections = connections;

        // DESIGN
        setConnectorSize(DEFAULT_SIZE);

        this.setActiveColor(Color.GREEN);
        this.setInactiveColor(getBackground());
        setLayout(null);
        setAlignmentY(0.5f);
        setAlignmentX(0.5f);

        setOpaque(false);

        setType(ConnectorType.INPUT);
    }

    /**
     * Fires an action.
     *
     * @param event the event
     */
    protected void fireAction(ActionEvent event) {
        for (CanvasActionListener l : actionListeners) {
            l.actionPerformed(event);
        }
    }

    /**
     * Adds a change listener to this window.
     *
     * @param l the listener to add
     * @return <code>true</code> (as specified by {@link Collection#add})
     */
    public boolean addActionListener(CanvasActionListener l) {
        return actionListeners.add(l);
    }

    /**
     * Removes a change listener from this window.
     *
     * @param l the listener to remove
     * @return <code>true</code> (as specified by {@link Collection#remove})
     */
    public boolean removeActionListener(CanvasActionListener l) {
        return actionListeners.remove(l);
    }

    /**
     * Defines the size of this connector and sets min/max size correct.
     *
     * @param size the conector size
     */
    public final void setConnectorSize(Integer size) {
        this.setSize(size, size);
        this.setPreferredSize(new Dimension(size, size));
        this.setMaximumSize(new Dimension(size, size));
        this.setMinimumSize(new Dimension(size, size));
        this.revalidate();
        VSwingUtil.repaintRequest(this);
        this.connectorSize = size;
    }

    /**
     * Returns the size of this connector.
     *
     * @return the connector size
     */
    public Integer getConnectorSize() {
        return connectorSize;
    }

    /**
     * Sets the size of the connector. It does not set min/max size.
     *
     * @param x the width, valid range: [0,MAX_INT]
     * @param y theheight, valid range: [0,MAX_INT]
     * @see #setConnectorSize(java.lang.Integer)
     */
    @Override
    public void setSize(int x, int y) {
        super.setSize(x, y);
        if (draggingObj != null) {
            draggingObj.setSize(x - 1, y - 1);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Style style = getStyle();

        // update style color
        // TODO: find a more efficient solution for updating style color
        if (isValidConnector()) {
            setActiveColor(
                    (Color) style.getBaseValues().get(ACTIVE_COLOR_VALID_KEY));
            setInactiveColor(
                    (Color) style.getBaseValues().get(INACTIVE_COLOR_VALID_KEY));
        } else {
            setActiveColor(
                    (Color) style.getBaseValues().get(ACTIVE_COLOR_ERROR_KEY));
            setInactiveColor(
                    (Color) style.getBaseValues().get(INACTIVE_COLOR_ERROR_KEY));
        }

        if (!getConnectorSize().equals(style.getBaseValues().get(SIZE_KEY))) {
            setConnectorSize((Integer) style.getBaseValues().get(SIZE_KEY));
        }

        // we want to instantly update background color
        updateBackgroundColor();

        Composite original = g2.getComposite();

        AlphaComposite ac1
                = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                        style.getBaseValues().getFloat(TRANSPARENCY_KEY));
        g2.setComposite(ac1);

        Stroke oldStroke = g2.getStroke();

        float thickness
                = (Float) style.getBaseValues().get(BORDER_THICKNESS_KEY);

        if (thickness > getConnectorSize() / 2.f) {
            thickness = getConnectorSize() / 2.f;
        }

        BasicStroke stroke = new BasicStroke(thickness);

        // offset based on line thickness
        int topLeftSpacing = (int) (thickness / 2.f);
        int bottomRightSpacing = topLeftSpacing * 2;

        // offset for fillOval, circle must be a bit smaller then the border circle
        float topLeftSpacingFill = topLeftSpacing / 8.f;
        float bottomRightSpacingFill = topLeftSpacingFill * 2;

        g2.setColor(this.getBackground());

//        g2.fillOval(0, 0, getWidth(), getHeight());
        g2.fill(new Ellipse2D.Float(
                0 + topLeftSpacingFill,
                0 + topLeftSpacingFill,
                getWidth() - bottomRightSpacingFill,
                getHeight() - bottomRightSpacingFill));

        g2.setStroke(stroke);

        g2.setColor(style.getBaseValues().getColor(BORDER_COLOR_KEY));

        g2.drawOval(topLeftSpacing, topLeftSpacing,
                getWidth() - bottomRightSpacing - 1,
                getHeight() - bottomRightSpacing - 1);

        g2.setStroke(oldStroke);
        g2.setComposite(original);
    }

    @Override
    public void mouseDragged(MouseEvent ev) {
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void mousePressed(MouseEvent ev) {
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        // sometimes swing has bugs with mouseover events
        // to prevent the case of having more than one selected
        // connector, we first unselect all
//            if (this.getParentObject() != null)
//            {
//                this.getParentObject().unselectConnectors();
//            }

        this.setSelected(true);
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        // sometimes swing has bugs with mouseover events
        // to prevent the case of having more than one selected
        // connector, we first unselect all
//        if (this.getParentObject() != null)
//        {
//            this.getParentObject().unselectConnectors();
//        }

        this.setSelected(false);
    }

    /**
     * Checks if the Connector object is selected (via MouseOver event)
     *
     * @return <code>true</code> if the object is selected; <code>false</code>
     * otherwise
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Defines whether the object is selected or not.
     *
     * @param selected the selection state; valid range [true,false]
     */
    public final void setSelected(boolean selected) {
        this.selected = selected;

        // set background color
        if (!selected) {

            // if not selected, define as valid
            setValidConnector(true);
        }

        updateBackgroundColor();
    }

    /**
     * Updates the background color of this connector depending on its selection
     * state.
     */
    private void updateBackgroundColor() {
        // set background color
        if (selected) {
            this.setBackground(getActiveColor());
        } else {
            if (this.getParent() != null) {
                this.setBackground(getInactiveColor());
            }
        }
    }

    /**
     * Returns the Transferable object.
     *
     * @return the Transferable object
     */
    public Transferable getTransferable() {
        return draggingObj;
    }

    /**
     * Sets the transferable object.
     *
     * @param dragginObj the Transferable object
     */
    public final void setTransferable(Transferable dragginObj) {
        this.draggingObj = dragginObj;
        draggingObj.setSourceObject(this);
        this.add(draggingObj);
//        draggingObj.setLocation(0, 0);

        draggingObj.setSize((int) this.getSize().getWidth() - 1,
                (int) this.getSize().getHeight() - 1);
    }

    /**
     * Returns position relative to main canvas's upper left corner.
     *
     * @return connector's position
     */
    public Point getAbsPos() {
        Component c = this.getParent();

        if (c == null) {
            return ZERO;
        }

        Point location = this.getLocation();

        while (!(c instanceof Canvas)) {

            location.x += c.getLocation().x;
            location.y += c.getLocation().y;

            c = c.getParent();

            if (c == null) {
                return null;
            }
        }

        // if the corresponding canvas window is minimized we have to change
        // the location because we then want the connection to start/stop at 
        // the title bar
        CanvasWindow object
                = (CanvasWindow) getValueObject().getParentWindow();

        if (object.isResizing()) {
            int diffY = object.getY() + object.getInsets().top + getHeight() - location.y;

            double y = location.y;

            if (object.isMinimized()) {
                // object is maximizing
                y += diffY * (1 - object.getMinimizeValue());

            } else {
                // object is minimizing
                y += diffY * object.getMinimizeValue();
            }

            location = new Point(location.x, (int) y);
        }

        if (object.isMinimized() && (!object.isResizing())) {
            int y = object.getY() + object.getInsets().top
                    + object.getTitleBarSize().height / 2 - getHeight() / 2;
            location = new Point(location.x, y);
        }

        return location;
    }

    /**
     * Returns the active color.
     *
     * @return the color
     */
    public Color getActiveColor() {
        return activeColor;
    }

    /**
     * Defines the active color.
     *
     * @param activeColor the color
     */
    public final void setActiveColor(Color activeColor) {
        this.activeColor = activeColor;
    }

    /**
     * Returns the ID value.
     *
     * @return the ID value, valid range [0,MAX_INT]
     */
    @Override
    public int getID() {
        return ID;
    }

    /**
     * Sets the ID value.
     *
     * @param ID the ID value, valid range [0,MAX_INT]
     */
    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * Returns the inactive color.
     *
     * @return the inactive color
     */
    public Color getInactiveColor() {
        return inactiveColor;
    }

    /**
     * Defines the inactive color.
     *
     * @param inactiveColor
     */
    public final void setInactiveColor(Color inactiveColor) {
        this.inactiveColor = inactiveColor;
    }

    /**
     * Indicates whether the connector is an input connector.
     *
     * @return <code>true</code> if the connector is an input connector;
     * <code>false</code> otherwise
     */
    public boolean isInput() {
        return getType() == ConnectorType.INPUT;
    }

    /**
     * Indicates whether the connector is an output connector.
     *
     * @return <code>true</code> if the connector is an output connector;
     * <code>false</code> otherwise
     */
    public boolean isOutput() {
        return getType() == ConnectorType.OUTPUT;
    }

    /**
     * Indicates whether this connector is valid.
     *
     * @return <code>true</code> if this connector is valid; <code>false</code>
     * otherwise
     */
    public boolean isValidConnector() {
        return validConnector;
    }

    /**
     * Defines
     *
     * @param validConnector the value to set
     */
    public void setValidConnector(boolean validConnector) {
        this.validConnector = validConnector;
        VSwingUtil.repaintRequest(this);
    }
//    @Override
//    protected void finalize() throws Throwable {
//
//        System.out.println(getClass().getName() + ": Finalize!!!");
//
//        super.finalize();
//    }

    public abstract void receiveData();

    public abstract void sendData();

    public void connectionRemoved(Connection connection) {
        //
    }

    public void connectionAdded(Connection connection) {
        //
    }

    /**
     * @return the valueObject
     */
    public ValueObject getValueObject() {
        return valueObject;
    }

    /**
     * @param valueObject the valueObject to set
     */
    public void setValueObject(ValueObject valueObject) {
        this.valueObject = valueObject;
    }

    /**
     * @return the connections
     */
    public Connections getConnections() {
        return connections;
    }

    /**
     * @param connections the connections to set
     */
    public void setConnections(Connections connections) {
        this.connections = connections;
    }

    /**
     * @param type the type to set
     */
    public final void setType(ConnectorType type) {
        this.type = type;
    }

    /**
     * @return the type
     */
    public ConnectorType getType() {
        return type;
    }

    /**
     * @return the id
     */
    @Override
    public String getId() {
        return stringId;
    }

    /**
     * @param id the id to set
     */
    @Override
    public void setId(String id) {
        this.stringId = id;
    }
}
