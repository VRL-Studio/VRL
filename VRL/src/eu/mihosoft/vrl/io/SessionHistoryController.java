/* 
 * SessionHistoryController.java
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

package eu.mihosoft.vrl.io;

import java.io.IOException;
import java.util.List;
import javax.swing.JMenuItem;

/**
 * History controller to navigate through session history.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface SessionHistoryController {

    /**
     * Returns the previous session if such a session exists.
     * @return the previous session if such a session exists
     * @throws IllegalStateException if no previous session exists
     */
    public String getPreviousSession();

    /**
     * Returns the next session if such a session exists.
     * @return the session session if such a session exists
     * @throws IllegalStateException if no next session exists
     */
    public String getNextSession();

    /**
     * Loads the previous session if such a session exists.
     * @return true if the session has been loaded (user can cancel that via dialog)
     * @throws IOException if an IO related error occurs.
     * @throws IllegalStateException if no previous session exists
     */
    public boolean loadPreviousSession() throws IOException;

    /**
     * Loads the next session if such a session exists.
     * @return true if the session has been loaded (user can cancel that via dialog)
     * @throws IOException if an IO related error occurs.
     * @throws IllegalStateException if no next session exists
     */
    public boolean loadNextSession() throws IOException;

    /**
     * Indicates whether next session exists.
     * @return <code>true</code> if next session exists;
     *         <code>false</code> otherwise
     */
    public boolean hasNextSession();

    /**
     * Indicates whether previous session exists.
     * @return <code>true</code> if previous session exists;
     *         <code>false</code> otherwise
     */
    public boolean hasPreviousSession();

    /**
     * Resets history pointer to the end of this history.
     */
    public void reset();

    /**
     * Clears this history, i.e., removes all elements.
     */
    public void clear();

    /**
     * Returns the current position.
     * @return the current position
     */
    int getPosition();

    /**
     * Returns the session history.
     * @return history session history
     */
    public List<String> getHistory();
    
    /**
     * Defines the item that shall be used to trigger
     * "load next session" command.
     * @param item the item to use
     */
    public void setNextItem(JMenuItem item);
    
     /**
     * Defines the item that shall be used to trigger
     * "load previous session" command.
     * @param item the item to use
     */
    public void setPreviousItem(JMenuItem item);
}
