/* 
 * ControlFlowConnector.java
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

package eu.mihosoft.vrl.reflection;

import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.CanvasWindow;
import eu.mihosoft.vrl.visual.Connections;
import eu.mihosoft.vrl.visual.Connector;
import eu.mihosoft.vrl.visual.ConnectorType;
import java.awt.Color;
import java.awt.Point;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ControlFlowConnector extends Connector {

    public ControlFlowConnector(
            Canvas mainCanvas,
            Connections connections) {
        super(mainCanvas, connections);
    }

    @Override
    public void receiveData() {
        //
    }

    @Override
    public void sendData() {
        //
    }

    public static ControlFlowConnector newInput(VisualCanvas canvas,
            DefaultObjectRepresentation oRep) {
        ControlFlowConnector result = new ControlFlowConnector(canvas,
                canvas.getControlFlowConnections());
        result.setType(ConnectorType.INPUT);
        result.setValueObject(new ControlFlowValueObject(
                canvas.getControlFlowConnections(), oRep, result));
        return result;
    }

    public static ControlFlowConnector newOutput(VisualCanvas canvas,
            DefaultObjectRepresentation oRep) {
        ControlFlowConnector result = new ControlFlowConnector(canvas,
                canvas.getControlFlowConnections());
        result.setType(ConnectorType.OUTPUT);
        result.setValueObject(new ControlFlowValueObject(
                canvas.getControlFlowConnections(), oRep, result));
        return result;
    }

    @Override
    public Point getAbsPos() {
        Point location = super.getAbsPos();

        ControlFlowValueObject valueObject = (ControlFlowValueObject) getValueObject();
        CanvasWindow object = valueObject.getParentWindow();

        // if object representation is not visible it is necessary to compute
        // different x location
        if (!valueObject.getObject().isVisible()) {
            int x = location.x;

            if (isInput()) {
                x = object.getX() + object.getTitleBar().getX() + 5;
            } else {
                x = object.getX() + object.getTitleBar().getX()
                        + object.getTitleBar().getWidth() - object.getInsets().right;
            }
            location = new Point(x, location.y);
        }

        // if methods are not shown but only listed in the methodList inside
        // ObjectRepresentation it is necessary to compute different locations
        if (!valueObject.getObject().isVisible()) {
            int y = object.getY() + object.getInsets().top
                    + object.getTitleBar().getHeight() / 2 - getHeight() / 2;
            int x = location.x;

            if (isInput()) {
                x = object.getX() + object.getTitleBar().getX() + 5;
            } else {
                x = object.getX() + object.getTitleBar().getX()
                        + object.getTitleBar().getWidth() - object.getInsets().right;
            }
            location = new Point(x, y);
        }

        return location;
    }
}
