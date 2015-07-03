/* 
 * ControlFlowConnection.java
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

package eu.mihosoft.vrl.reflection;

import eu.mihosoft.vrl.visual.Connection;
import eu.mihosoft.vrl.visual.Connector;
import eu.mihosoft.vrl.visual.Style;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ControlFlowConnection extends Connection {

    public static final String ACTIVE_CONTROLFLOW_CONNECTION_COLOR_KEY =
            "ControlFlowConnection[active]:color";
    public static final String CONTROLFLOW_CONNECTION_COLOR_KEY = 
            "ControlFlowConnection[inactive]:color";
    public static final String ACTIVE_CONTROLFLOW_CONNECTION_THICKNESS_KEY =
            "ControlFlowConnection[active]:thickness";
    public static final String CONTROLFLOW_CONNECTION_THICKNESS_KEY =
            "ControlFlowConnection[inactive]:thickness";

    /**
     * Paints the connection. This method uses the well known Bezier curves
     * to draw the connection.
     * @param g the Graphics context in which to paint
     */
    @Override
    public void paint(Graphics g) {
        Connector sender = getSender();
        Connector receiver = getReceiver();
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
            if (getStyleBuffer() == null || !getStyleBuffer().equals(style)) {
                setStyleBuffer(style);
                contentChanged();
            }

            // color animation
            if (isAnimating()) {
                setAnimating(false);
                
                Color startColor = 
                        style.getBaseValues().getColor(
                        ACTIVE_CONTROLFLOW_CONNECTION_COLOR_KEY);
                Color stopColor = 
                        style.getBaseValues().getColor(
                        CONTROLFLOW_CONNECTION_COLOR_KEY);
                
                float startThickness = 
                        style.getBaseValues().getFloat(
                        ACTIVE_CONTROLFLOW_CONNECTION_THICKNESS_KEY);
                
                float stopThickness = 
                        style.getBaseValues().getFloat(
                        CONTROLFLOW_CONNECTION_THICKNESS_KEY);
                        

                getRedTarget().setStartValue(
                        //                        style.getActiveConnectionColor().getRed()
                        startColor.getRed());
                getRedTarget().setStopValue(
                        //                        style.getConnectionColor().getRed()
                        stopColor.getRed());
                getGreenTarget().setStartValue(
                        //                        style.getActiveConnectionColor().getGreen()
                        startColor.getGreen());
                getGreenTarget().setStopValue(
                        //                        style.getConnectionColor().getGreen()
                        stopColor.getGreen());
                getBlueTarget().setStartValue(
                        //                        style.getActiveConnectionColor().getBlue()
                        startColor.getBlue());
                getBlueTarget().setStopValue(
                        //                        style.getConnectionColor().getBlue()
                        stopColor.getBlue());
                getThicknessTarget().setStartValue(
                        //                        style.getActiveConnectionThickness()
                        startThickness);
                getThicknessTarget().setStopValue(
                        //                        style.getConnectionThickness()
                        stopThickness);

                getRedTarget().step(getColorValue());
                getGreenTarget().step(getColorValue());
                getBlueTarget().step(getColorValue());
                getThicknessTarget().step(getColorValue());
            }

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



            g2.setColor(new Color((int) getRedTarget().getValue() / 4,
                    (int) getGreenTarget().getValue() / 4, (int) getBlueTarget().getValue() / 4));

            BasicStroke stroke =
                    new BasicStroke((float) (getThicknessTarget().getValue() / 0.7),
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND);


            g2.setStroke(stroke);

            g2.draw(p);

            stroke =
                    new BasicStroke((float) getThicknessTarget().getValue(),
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND);

            g2.setColor(new Color((int) getRedTarget().getValue(),
                    (int) getGreenTarget().getValue(), (int) getBlueTarget().getValue()));

            g2.setStroke(stroke);

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
                    || !getPreviousSenderLocation().equals(startLocation);

            boolean senderMoved =
                    getPreviousReceiverLocation() == null
                    || !getPreviousReceiverLocation().equals(targetLocation);

            if (senderMoved || receiverMoved || isAnimating()) {
                contentChanged();

                setPreviousSenderLocation(startLocation);
                setPreviousReceiverLocation(targetLocation);
            }
        }
    }
}
