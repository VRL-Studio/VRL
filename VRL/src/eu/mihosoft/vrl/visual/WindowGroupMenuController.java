/* 
 * WindowGroupMenuController.java
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

import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.visual.CanvasWindow;
import eu.mihosoft.vrl.visual.Selectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.acl.Group;
import java.util.ArrayList;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * A window group menu controller applies group commands to window groups and
 * maps the window group states to specified menus. It synchronises menu state
 * and window group states.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class WindowGroupMenuController {

    private Canvas mainCanvas;
    private JMenu showGroupMenu;
    private JMenu removeGroupMenu;
//    private int currentGroup; is now in windowgroups

    /**
     * Defines the main canvas object this controller belongs to.
     * @param canvas the canvas to set
     */
    public void setMainCanvas(Canvas canvas) {
        this.mainCanvas = canvas;
    }

    /**
     * Initializes the controller. Initializing implies specification of menus
     * to operate on and initializing the given menus to represent the window
     * groups of the main canvas.
     * @param showMenuGroup the menu that shall contain the show/hide menu items
     * @param removeGroupMenu the menu that shall contain the remove menu items
     */
    public void initController(
            JMenu showMenuGroup,
            JMenu removeGroupMenu) {
        this.showGroupMenu = showMenuGroup;
        this.removeGroupMenu = removeGroupMenu;

        removeInexistentWindowsFromGroups();

        initMenus(mainCanvas.getWindowGroups());

        removeEmptyGroups();
    }

    /**
     * Initializes the menus. It maps the given window group list to the
     * controlled menus.
     * @param groups the groups that are to be controlled
     */
    private void initMenus(WindowGroups groups) {
        showGroupMenu.removeAll();
        removeGroupMenu.removeAll();
        for (WindowGroup g : groups) {
            addGroupToMenu(g);
            if (g.isShown()) {
                showGroup(g);
            } else {
                hideGroup(g);
            }
        }
        mainCanvas.setPresentationMode(!groups.isEmpty());
    }

    /**
     * Adds a group to the window group list of the main canvas.
     * @param group the group to add
     */
    public void addGroup(final WindowGroup group) {
        boolean alreadyAdded = false;

        for (WindowGroup g : mainCanvas.getWindowGroups()) {
            if (g.getName().equals(group.getName())) {
                alreadyAdded = true;
                break;
            }
        }

        if (!alreadyAdded) {
            addGroupToMenu(group);
            mainCanvas.getWindowGroups().add(group);
        }

        mainCanvas.setPresentationMode(true);
    }

    /**
     * Maps a window group to a menu item.
     * @param group the group to add
     */
    private void addGroupToMenu(final WindowGroup group) {

        final JCheckBoxMenuItem showItem =
                new JCheckBoxMenuItem(group.getName());
        showItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(group.getName())) {
                    if (showItem.isSelected()) {
                        showGroup(group);
                    } else {
                        hideGroup(group);
                    }
                }
            }
        });

        final JMenuItem removeItem = new JMenuItem(group.getName());

        removeItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(group.getName())) {
                    removeGroup(group);
                }
            }
        });

        if (showGroupMenu != null) {
            showGroupMenu.add(showItem);
            showItem.setVisible(true);
            showItem.setSelected(true);
        }
        if (removeGroupMenu != null) {
            removeGroupMenu.add(removeItem);
            removeGroupMenu.setVisible(true);
        }
    }

    /**
     * Unselects a menu item specified by its window group
     * @param g the group
     */
    public void unselectShowGroupItem(WindowGroup g) {
        if (showGroupMenu != null) {
            final JCheckBoxMenuItem item =
                    (JCheckBoxMenuItem) getItemPerName(
                    showGroupMenu, g.getName());
            item.setSelected(false);
        }
    }

    /**
     * Selects a menu item specified by its window group
     * @param g the group
     */
    public void selectShowGroupItem(WindowGroup g) {
        if (showGroupMenu != null) {
            final JCheckBoxMenuItem item =
                    (JCheckBoxMenuItem) getItemPerName(
                    showGroupMenu, g.getName());
            item.setSelected(true);
        }
    }

    /**
     * Hides a window group.
     * @see WindowGroup#hide()
     * @param g the group to hide
     */
    public void hideGroup(WindowGroup g) {
        g.hide();

        for (WindowGroup i : mainCanvas.getWindowGroups()) {
            if (i.isShown() && g.containsElementsOfGroup(i)) {
                i.show();
            }
        }

        unselectShowGroupItem(g);
    }

    /**
     * Shows a window group.
     * @param g the group to show
     * @see WindowGroup#show()
     */
    public void showGroup(WindowGroup g) {
        g.show();
        selectShowGroupItem(g);
    }

    /**
     * Hides all window groups.
     * @see WindowGroupMenuController#hideGroup(eu.mihosoft.vrl.reflection.WindowGroup)
     */
    public void hideAllGroups() {
        for (WindowGroup g : mainCanvas.getWindowGroups()) {
            hideGroup(g);
        }
    }

    /**
     * Shows all window groups.
     * @see WindowGroupMenuController#showGroup(eu.mihosoft.vrl.reflection.WindowGroup)
     */
    public void showAllGroups() {
        for (WindowGroup g : mainCanvas.getWindowGroups()) {
            showGroup(g);
        }
    }

    /**
     * Computes a unique group name that can be used for automated group naming.
     * It uses the pattern <code>"Group: i"</code> where <code>i</code>
     * represents an index number. This method always tries to use the lowest
     * index that is available.
     * @return the unique group name
     */
    private String getUniqueGroupName() {
        String result = null;
        int count = 0;

        String groupName = null;
        while (groupName == null
                || mainCanvas.getWindowGroups().getByName(groupName) != null) {
            groupName = "Group " + count;
            count++;
        }

        result = groupName;

        return result;
    }

    /**
     * Creates a new window group from selected selected components in the
     * clipboard of the main canvas. The group will be added to the list
     * of window groups and menu entries will becreated.
     * @see eu.mihosoft.vrl.visual.Canvas#getClipBoard()
     * @param groupName the group name
     */
    public void createGroupFromSelectedWindows(String groupName) {
        WindowGroup group = new WindowGroup();
        group.setMainCanvas(mainCanvas);

        if (groupName == null) {
            groupName = getUniqueGroupName();
        }

        group.setName(groupName);

        ArrayList<Selectable> selectables = new ArrayList<Selectable>();

        for (Selectable s : mainCanvas.getClipBoard()) {
            CanvasWindow w = (CanvasWindow) s;
            group.addWindow(w);
            selectables.add(w);
        }

        for (Selectable s : selectables) {
            mainCanvas.getClipBoard().unselect(s);
        }

        addGroup(group);
    }

    /**
     * Creates a new window group from selected selected components in the
     * clipboard of the main canvas. The group will be added to the list
     * of window groups and menu entries will becreated. In addition the group
     * name will be generated automatically.
     * @see eu.mihosoft.vrl.visual.Canvas#getClipBoard()
     */
    public void createGroupFromSelectedWindows() {
        createGroupFromSelectedWindows(null);
    }

    /**
     * Removes a window group. The group will be removed from the canvas window
     * group list. Also the corresponding menu items will be removed. If the
     * window group is hidden all of its windows will be shown after removal.
     * @param group the group to remove
     */
    public void removeGroup(final WindowGroup group) {
        JMenuItem showItem = getItemPerName(showGroupMenu, group.getName());
        JMenuItem removeItem = getItemPerName(removeGroupMenu, group.getName());

        if (showGroupMenu != null) {
            showGroupMenu.remove(showItem);
        }
        if (removeGroupMenu != null) {
            removeGroupMenu.remove(removeItem);
        }

        group.show();

        mainCanvas.getWindowGroups().
                remove(mainCanvas.getWindowGroups().getByName(group.getName()));

        mainCanvas.setPresentationMode(!mainCanvas.getWindowGroups().isEmpty());
    }

    /**
     * Returns a menu item by name.
     * @param menu the menu to search
     * @param name the name
     * @return the item if an item with the specified name exists;
     *         <code>null</code> otherwise
     */
    private JMenuItem getItemPerName(JMenu menu, String name) {
        JMenuItem result = null;

        if (menu != null) {
            for (int i = 0; i < menu.getItemCount(); i++) {
                if (menu.getItem(i).getText().equals(name)) {
                    result = menu.getItem(i);
                }
            }
        }

        return result;
    }

    /**
     * Shows the current group and hides all other groups. The current group is
     * defined by an index variable that can be increased or decreased to
     * iterate through all window groups of the main canvas.
     */
    public void showOnlyCurrentGroup() {
        if (!mainCanvas.getWindowGroups().isEmpty()) {
            ArrayList<WindowGroup> groups = new ArrayList<WindowGroup>();

            for (int i = 0; i < mainCanvas.getWindowGroups().size(); i++) {
                if (i != getCurrentGroup()) {
                    groups.add(mainCanvas.getWindowGroups().get(i));
                }
            }

            for (WindowGroup g : groups) {
                hideGroup(g);
            }

            showGroup(mainCanvas.getWindowGroups().get(getCurrentGroup()));
        }
    }

    /**
     * Increases the group index variable (or sets it to zero if it exceeds its
     * maximum) and calls
     * {@link WindowGroupMenuController#showOnlyCurrentGroup() }.
     */
    public void showNextGroup() {
        if (getCurrentGroup() < mainCanvas.getWindowGroups().size() - 1) {
            setCurrentGroup(getCurrentGroup() + 1);
        } else {
            setCurrentGroup(0);
        }
        showOnlyCurrentGroup();
    }

    /**
     * Decreases the group index variable (or sets it to its maximum if it is
     * less than zero) and calls
     * {@link WindowGroupMenuController#showOnlyCurrentGroup() }.
     */
    public void showPreviousGroup() {
        if (getCurrentGroup() > 0) {
            setCurrentGroup(getCurrentGroup() - 1);
        } else {
            setCurrentGroup(mainCanvas.getWindowGroups().size() - 1);
        }
        showOnlyCurrentGroup();
    }

    /**
     * Removes a window from the group list. Empty groups will be removed.
     * @param w the window to remove
     */
    public void removeWindow(CanvasWindow w) {
        ArrayList<WindowGroup> delList = new ArrayList<WindowGroup>();
        for (WindowGroup g : mainCanvas.getWindowGroups()) {
            g.removeWindow(w);
            if (g.getWindowIDs().isEmpty()) {
                delList.add(g);
            }
        }

        for (WindowGroup g : delList) {
            removeGroup(g);
        }
    }

    public void removeEmptyGroups() {

        ArrayList<WindowGroup> delList = new ArrayList<WindowGroup>();

        for (WindowGroup g : mainCanvas.getWindowGroups()) {
            if (g.isEmpty()) {
                delList.add(g);
            }
        }

        for (WindowGroup g : delList) {
            removeGroup(g);
        }
    }

    public void removeInexistentWindowsFromGroups() {

//        ArrayList<WindowGroup> delList = new ArrayList<WindowGroup>();

        for (WindowGroup g : mainCanvas.getWindowGroups()) {

            // incorrect group entries (id values of non existent windows)
            ArrayList<Integer> delList = new ArrayList<Integer>();

            for (int windowId : g.getWindowIDs()) {
//            int windowId = getWindowIDs().get(i);

                if (mainCanvas.getWindows().getById(windowId) == null) {
                    System.out.println(
                            ">> eu.mihosoft.vrl.visual.WindowGroup WARNING: ("
                            + g.getName() + ")\n"
                            + ">>> window with id " + windowId
                            + " does not exist anymore and will be removed from"
                            + " this group.");
                    delList.add(windowId);
                }
            } // end for

            // remove incorrect group entires from list
            for (int id : delList) {
                g.removeWindowByID(id);
            }
        }

//        for (WindowGroup g : delList) {
//            removeGroup(g);
//        }
    }

    /**
     * @return the current group
     */
    public int getCurrentGroup() {
        return mainCanvas.getWindowGroups().getCurrentGroup();
    }

    /**
     * @param currentGroup the current group to set
     */
    public void setCurrentGroup(int currentGroup) {
        mainCanvas.getWindowGroups().setCurrentGroup(currentGroup);
    }

    /**
     * Celars menus.
     */
    public void clear() {
        removeGroupMenu.removeAll();
        showGroupMenu.removeAll();
    }
}
