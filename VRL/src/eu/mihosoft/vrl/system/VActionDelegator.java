/* 
 * VActionDelegator.java
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

package eu.mihosoft.vrl.system;

import eu.mihosoft.vrl.visual.Action;
import eu.mihosoft.vrl.visual.ActionDelegator;
import eu.mihosoft.vrl.visual.ActionGroup;
import eu.mihosoft.vrl.visual.MenuController;
import eu.mihosoft.vrl.visual.VMenuController;
import eu.mihosoft.vrl.visual.VSeparator;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class VActionDelegator implements ActionDelegator {

    private MenuController fileMenuController = new VMenuController();
    private MenuController editMenuController = new VMenuController();
    private MenuController viewMenuController = new VMenuController();
    private MenuController toolMenuController = new VMenuController();
    private MenuController debugMenuController = new VMenuController();
    private MenuController infoMenuController = new VMenuController();

    @Override
    public boolean addAction(Action a, String destination) {
        if (destination.equals(FILE_MENU)) {
            getFileMenuController().addAction(a);
            return true;
        } else if (destination.equals(EDIT_MENU)) {
            getEditMenuController().addAction(a);
            return true;
        } else if (destination.equals(VIEW_MENU)) {
            getViewMenuController().addAction(a);
            return true;
        } else if (destination.equals(TOOL_MENU)) {
            getToolMenuController().addAction(a);
            return true;
        } else if (destination.equals(DEBUG_MENU)) {
            getDebugMenuController().addAction(a);
            return true;
        } else if (destination.equals(INFO_MENU)) {
            getInfoMenuController().addAction(a);
            return true;
        }
        return false;
    }

    @Override
    public boolean addSeparator(String destination) {
        if (destination.equals(FILE_MENU)) {
            getFileMenuController().addAction(new VSeparator());
            return true;
        } else if (destination.equals(EDIT_MENU)) {
            getEditMenuController().addAction(new VSeparator());
            return true;
        } else if (destination.equals(VIEW_MENU)) {
            getViewMenuController().addAction(new VSeparator());
            return true;
        } else if (destination.equals(TOOL_MENU)) {
            getToolMenuController().addAction(new VSeparator());
            return true;
        } else if (destination.equals(DEBUG_MENU)) {
            getDebugMenuController().addAction(new VSeparator());
            return true;
        } else if (destination.equals(INFO_MENU)) {
            getInfoMenuController().addAction(new VSeparator());
            return true;
        }
        return false;
    }

    /**
     * @return the fileMenuController
     */
    public MenuController getFileMenuController() {
        return fileMenuController;
    }

    /**
     * @return the editMenuController
     */
    public MenuController getEditMenuController() {
        return editMenuController;
    }

    /**
     * @return the viewMenuController
     */
    public MenuController getViewMenuController() {
        return viewMenuController;
    }

    /**
     * @return the debugMenuController
     */
    public MenuController getDebugMenuController() {
        return debugMenuController;
    }

    /**
     * @return the infoMenuController
     */
    public MenuController getInfoMenuController() {
        return infoMenuController;
    }

    /**
     * @return the toolMenuController
     */
    public MenuController getToolMenuController() {
        return toolMenuController;
    }
}
