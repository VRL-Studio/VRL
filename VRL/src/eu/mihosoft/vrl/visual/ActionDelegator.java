/* 
 * ActionDelelator.java
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

/**
 * Delegates actions to the correct menu controllers.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface ActionDelegator {

    /**
     * Adds an action to this delegator.
     *
     * @param a action to add
     * @param destination destination
     * @return
     * <code>true</code> if the action could be added at the specified location;
     * <code>false</code> otherwise
     */
    public boolean addAction(Action a, String destination);

//    /**
//     * Adds an action to this delegator.
//     *
//     * @param a action to add
//     * @param destination destination
//     * @return
//     * <code>true</code> if the action could be added at the specified location;
//     * <code>false</code> otherwise
//     */
//    public boolean addAction(ActionGroup a, String destination);

    /**
     * Adds a separator to this delegator.
     *
     * @param destination destination
     * @return
     * <code>true</code> if the separator could be added at the specified
     * location;
     * <code>false</code> otherwise
     */
    public boolean addSeparator(String destination);
    /**
     * Popup menu of the canvas.
     */
    public final String CANVAS_MENU = "Action:Destination=CanvasMenu";
    /**
     * Window popup menu.
     */
    public final String WINDOW_MENU = "Action:Destination=WindowMenu";
    /**
     * File menu.
     */
    public final String FILE_MENU = "Action:Destination=FileMenu";
    /**
     * Edit menu.
     */
    public final String EDIT_MENU = "Action:Destination=EditMenu";
    /**
     * View menu.
     */
    public final String VIEW_MENU = "Action:Destination=ViewMenu";
    /**
     * Tool menu.
     */
    public final String TOOL_MENU = "Action:Destination=ToolMenu";
    /**
     * Debug menu.
     */
    public final String DEBUG_MENU = "Action:Destination=DebugMenu";
    /**
     * Info menu.
     */
    public final String INFO_MENU = "Action:Destination=InfoMenu";
}
