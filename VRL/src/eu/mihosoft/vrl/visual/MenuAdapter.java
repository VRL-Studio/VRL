/* 
 * MenuAdapter.java
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

/**
 * A menu adapter that allows basic operations on JMenu, JPopupMenu and
 * DefaultMutableTreeNode objects.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class MenuAdapter implements ActionViewAdapter {

    private JMenu menu;
    private JPopupMenu popup;
    private Map<Action, Object> viewObjects = new HashMap<Action, Object>();

    /**
     * Constructor.
     *
     * @param menu manu
     */
    public MenuAdapter(JMenu menu) {
        this.menu = menu;
    }

    /**
     * Constructor.
     *
     * @param popup menu
     */
    public MenuAdapter(JPopupMenu popup) {
        this.popup = popup;
    }

    /**
     * Adds an action to this menu adapter.
     *
     * @param a action to add
     * @param owner menu owner
     */
    @Override
    public void add(final Action a, final Object owner) {
        if (a.isSeparator()) {
            addSeparator(a);
        } else if (a instanceof ActionGroup) {
            JMenu m = new JMenu(a.getText());
            add(m);
            viewObjects.put(a, m);

            MenuAdapter menuAdapter = new MenuAdapter(m);

            for (Action e : ((ActionGroup) a).getElements()) {
                menuAdapter.add(e, owner);
                viewObjects.putAll(menuAdapter.viewObjects);
            }

        } else {
            JMenuItem item = new JMenuItem(a.getText(), a.getIcon());
            item.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    a.actionPerformed(e, owner);
                }
            });

            add(item);
            viewObjects.put(a, item);
        }
    }

    @Override
    public void remove(final Action a) {
        remove(a, true);
    }

    private void remove(final Action a, boolean removeViewObj) {

        // find the view object
        Object o = findActionView(a);

        // remove the view object
        if (o != null) {
            // remove all view objects
            viewObjects.remove(o);

            if (!removeViewObj) {
                return;
            }

            if (o instanceof JMenu) {
                JMenu m = (JMenu) o;

                // remove from menu
                m.removeAll();

                m.getParent().remove(m);

            } else if (o instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) o;

                // remove from menu
                item.getParent().remove(item);
            } else if (o instanceof JSeparator) {
                JSeparator s = (JSeparator) o;
                s.getParent().remove(s);
            }
        }

        // if a is a group we also have to remove sub-view objects from the map
        // to prevent memory leaks
        if (a instanceof ActionGroup) {
            for (Action e : ((ActionGroup) a).getElements()) {
                remove(e, false);
            }
        }
    }

    private Object findActionView(Action a) {
        return viewObjects.get(a);
    }

    /**
     * Adds an item to this menu.
     *
     * @param item the item to add
     */
    private void add(JMenuItem item) {
        if (menu != null) {
            menu.add(item);
        } else if (popup != null) {
            popup.add(item);
        }
    }

    /**
     * Adds an item to this menu.
     *
     * @param item the item to add
     */
    private void add(JMenu item) {
        if (menu != null) {
            menu.add(item);
        } else if (popup != null) {
            popup.add(item);
        }
    }

    /**
     * Adds a separator to this menu.
     */
    private void addSeparator(Action a) {
        JSeparator separator = null;
        if (menu != null) {
            separator = new JSeparator();
            menu.add(separator);
        } else if (popup != null) {
//            popup.addSeparator();
            separator = new JSeparator();
            popup.add(separator);
        }

        viewObjects.put(a, separator);
    }

    @Override
    public boolean isEmpty() {
        if (menu != null) {
            return menu.getItemCount()==0;
        } else if (popup != null) {
            return popup.getSubElements().length==0;
        }
        
        return true;
    }

    @Override
    public void setEnabled(boolean v) {
        if (menu != null) {
            menu.setEnabled(v);
        } else if (popup != null) {
            popup.setEnabled(v);
        }
    }
    
    
}
