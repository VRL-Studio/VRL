/* 
 * WindowGroup.java
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

package eu.mihosoft.vrl.visual;

import eu.mihosoft.vrl.animation.Animation;
import eu.mihosoft.vrl.animation.PositionAnimation;
import java.awt.Point;
import java.util.ArrayList;

/**
 * Groups windows and allows to perform show/hide minimize/maximize commands
 * on all windows in the group. Windows are identified by their unique id.
 * In addition it is possible to define a group name.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class WindowGroup {

    private ArrayList<Integer> windowIDs;
    private ArrayList<Point> windowPositions;
    private String name;
    private transient Canvas mainCanvas;
    private boolean shown;
    private boolean maximized;

    /**
     * Constructor.
     */
    public WindowGroup() {
        windowIDs = new ArrayList<Integer>();
        windowPositions = new ArrayList<Point>();
    }

    /**
     * Defines the canvas object this group belongs to.
     * @param mainCanvas the main canvas object
     */
    public void setMainCanvas(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;
    }

    /**
     * Adds a window to this group.
     * @param w the window to add
     */
    public void addWindow(CanvasWindow w) {
        getWindowIDs().add(w.getID());
        getWindowPositions().add(w.getLocation());
    }

    /**
     * Removes a window from this group.
     * @param w the window to remove
     */
    public void removeWindow(CanvasWindow w) {
        for (int i = 0; i < windowIDs.size(); i++) {
            if (windowIDs.get(i).equals(w.getID())) {
                getWindowIDs().remove(i);
                windowPositions.remove(i);
            }

        }
    }

    /**
     * Removes a window from this group.
     * @param id the id of the window to remove
     */
    public void removeWindowByID(int id) {
        for (int i = 0; i < windowIDs.size(); i++) {
            if (windowIDs.get(i).equals(id)) {
                getWindowIDs().remove(i);
                windowPositions.remove(i);
            }
        }
    }

    /**
     * Hides all windows in this group.
     * @see CanvasWindow#hideWindow()
     */
    public void hide() {

        for (int windowId : getWindowIDs()) {

            mainCanvas.getWindows().getById(windowId).hideWindow();

        } // end for

        setShown(false);
    }

    /**
     * Shows all windows in this group.
     * @see CanvasWindow#showWindow()
     */
    public void show() {

        for (int i = 0; i < getWindowIDs().size(); i++) {
            int windowId = getWindowIDs().get(i);

            CanvasWindow window = null;

            window = mainCanvas.getWindows().getById(windowId);
            window.showWindow();

            Animation posAnimation =
                    new PositionAnimation(window,
                    window.getLocation(),
                    getWindowPositions().get(i));

//            Constant velocity
//            double xDiff =
//                    window.getLocation().x - getWindowPositions().get(i).x;
//            double yDiff =
//                    window.getLocation().y - getWindowPositions().get(i).y;
//            double length = Math.sqrt(xDiff*xDiff+yDiff*yDiff);
//            posAnimation.setDuration(length*0.001);


            posAnimation.setDuration(1);

            mainCanvas.getAnimationManager().addAnimation(posAnimation);

        } // end for

//        for (int i : getWindowIDs()) {
        //            mainCanvas.getWindows().getById(i).showWindow();
        //        }
        setShown(true);
    }

    /**
     * Minimizes all windows in this group.
     * @see CanvasWindow#minimize()
     */
    public void minimize() {
        for (int i : getWindowIDs()) {
            mainCanvas.getWindows().getById(i).minimize();
        }
        setMaximized(false);
    }

    /**
     * Maximizes all windows in this group.
     * @see CanvasWindow#maximize()
     */
    public void maximize() {
        for (int i : getWindowIDs()) {
            mainCanvas.getWindows().getById(i).maximize();
        }
        setMaximized(true);
    }

//    public void listIDs() {
//        for (int i : getWindowIDs()) {
//            System.out.println("ID: " + i);
//        }
//    }
    /**
     * Converts the list of internal internal saved id values to a list that
     * contains the corresponding canvas windows and returns it.
     * @return list of all windows that are element of this group
     */
    public ArrayList<CanvasWindow> getWindows() {
        ArrayList<CanvasWindow> result = new ArrayList<CanvasWindow>();

        for (int i : getWindowIDs()) {
            result.add(mainCanvas.getWindows().getById(i));
        }

        return result;
    }

    /**
     * Returns the group name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Defines the group name. The name has to be unique because it is also
     * used as identifier.
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a list that contains the id values of all windows that are
     * element of this group.
     * @return the id list
     */
    public ArrayList<Integer> getWindowIDs() {
        return windowIDs;
    }

    /**
     * Defines the window id list of this group.
     * @param windowIDs the list to set
     */
    public void setWindowIDs(ArrayList<Integer> windowIDs) {
        this.windowIDs = windowIDs;
    }

    /**
     * Indicates whether this group is currently shown.
     * @return <code>true</code> if this group is currently shown;
     *         <code>false</code> otherwise
     * @see WindowGroup#show()
     */
    public boolean isShown() {
        return shown;
    }

    /**
     * Defines whether this group is shown.
     * @param shown the value to set
     * @see WindowGroup#show()
     */
    public void setShown(boolean shown) {
        this.shown = shown;
    }

    /**
     * Indicates whether this group is currently maximized.
     * @return <code>true</code> if this group is currently maximized;
     *         <code>false</code> otherwise
     * @see WindowGroup#maximize()
     */
    public boolean isMaximized() {
        return maximized;
    }

    /**
     * Defines whether this group is maximized.
     * @param maximized the value to set
     * @see WindowGroup#maximize() 
     */
    public void setMaximized(boolean maximized) {
        this.maximized = maximized;
    }

    /**
     * Indicates whether this group contains elements of another group, i.e.,
     * whether they share elements.
     * @param g the group to check
     * @return <code>true</code> if this group contains elements of the
     *         specified group;
     *         <code>false</code> otherwise
     */
    public boolean containsElementsOfGroup(WindowGroup g) {
        boolean result = false;

        for (Integer i : windowIDs) {
            result = g.getWindowIDs().contains(i);

            if (result) {
                break;
            }
        }
        return true;
    }

    /**
     * Returns the windows positions.
     * @return the window positions
     */
    public ArrayList<Point> getWindowPositions() {
        return windowPositions;
    }

    /**
     * Defines the windows positions.
     * @param windowPositions the windowPositions to set
     */
    public void setWindowPositions(ArrayList<Point> windowPositions) {
        this.windowPositions = windowPositions;
    }

    public boolean isEmpty() {
        return windowIDs.isEmpty();
    }
}
