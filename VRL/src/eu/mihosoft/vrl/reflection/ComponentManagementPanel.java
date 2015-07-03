/* 
 * ComponentManagementPanel.java
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

import eu.mihosoft.vrl.system.VRL;
import eu.mihosoft.vrl.visual.TransparentPanel;
import eu.mihosoft.vrl.visual.VButton;
import eu.mihosoft.vrl.visual.VFilter;
import eu.mihosoft.vrl.visual.VFilteredTreeModel;
import eu.mihosoft.vrl.visual.VScrollPane;
import eu.mihosoft.vrl.visual.VStringFilter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ComponentManagementPanel extends TransparentPanel {

    private ComponentTree componentTree;
    private VisualCanvas canvas;
    private VFilteredTreeModel filteredTreeModel;
    private JTextField searchField;

    public ComponentManagementPanel(VisualCanvas canvas) {
        this.canvas = canvas;

        componentTree = canvas.getComponentTree();
        this.filteredTreeModel = canvas.getComponentTreeModel();

        setMinimumSize(new Dimension(600, 400));
//        setMaximumSize(new Dimension(600, 400));
        setPreferredSize(new Dimension(600, 400));

        BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
        setLayout(layout);

        Box searchBox = new Box(BoxLayout.LINE_AXIS);

        add(searchBox);

        searchField = new JTextField();

        searchField.setMaximumSize(
                new Dimension(Short.MAX_VALUE,
                searchField.getPreferredSize().height));

        VButton btn = new VButton("Search");

        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                searchButtonActionPerformed(e);
            }
        });

        searchBox.add(searchField);
        searchBox.add(btn);
        searchBox.setBorder(new EmptyBorder(5, 5, 5, 5));

        searchField.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                //
            }

            @Override
            public void keyPressed(KeyEvent e) {
                //
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (!e.isAltDown() && !e.isAltGraphDown() && !e.isControlDown()
                        && !e.isMetaDown() && !e.isShiftDown()) {
                    searchFieldKeyReleased(e);
                }
            }
        });

        Box treeBox = new Box(BoxLayout.LINE_AXIS);

        add(treeBox);

        JScrollPane treeScrollPane = new VScrollPane(componentTree);
        treeScrollPane.setBorder(new EmptyBorder(5, 10, 10, 10));

        treeBox.add(treeScrollPane);
    }

    @Override
    public void requestFocus() {
        searchField.requestFocus();
        searchField.selectAll();
    }

    /**
     * @return the componentTree
     */
    public ComponentTree getComponentTree() {
        return componentTree;
    }

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {
        executeFilter();
    }

    private void searchFieldKeyReleased(java.awt.event.KeyEvent evt) {
        executeFilter();
    }

    void executeFilter() {
        //      
        if (filteredTreeModel != null) {

            setSearchString(searchField.getText().trim());

            filteredTreeModel.setFilter(canvas.getComponentSearchFilters());

            if (searchField.getText().trim().isEmpty()) {
                // expand tree
                for (int i = 1; i < componentTree.getRowCount(); i++) {
                    componentTree.collapseRow(i);
                }
            } else {
                // expand tree
                for (int i = 1; i < componentTree.getRowCount(); i++) {
                    componentTree.expandRow(i);
                }
            }
        }
    }

    public void setSearchString(String string) {
        for (VFilter f : canvas.getComponentSearchFilters()) {
            if (f instanceof VStringFilter) {
                ((VStringFilter) f).setSearchString(string);
            }
        }
    }

    public static class StringFilter implements VStringFilter {

        private String string = null;

        public StringFilter() {
        }

        @Override
        public void setSearchString(String s) {
            this.string = s;
        }

        @Override
        public boolean matches(Object o) {

            if (string == null || (o != null
                    && o.toString().toLowerCase().
                    contains(string.toLowerCase()))) {

                return true;
            }

            return false;
        }

        @Override
        public String getName() {
            return "String Filter";
        }

        @Override
        public boolean hideWhenMatching() {
            return false;
        }
    }

    public static class ComponentFilter implements VStringFilter {

        private String string = null;

        public ComponentFilter() {
        }

        @Override
        public void setSearchString(String s) {
            this.string = s;
        }

        @Override
        public boolean matches(Object o) {

            if (string == null) {
                return true;
            }

            if (o == null) {
                return false;
            }
            
            String str = string.toLowerCase();
            
            // if we are a treenode, take user object
            if (o instanceof DefaultMutableTreeNode) {
                o = ((DefaultMutableTreeNode)o).getUserObject();
            }
            
            Class<?> cls;
            
            if (o instanceof Class) {
                    cls = (Class<?>) o;
            } else {
                cls = o.getClass();
            }
            
            String componentName =
                    ComponentUtil.getComponentName(cls).toLowerCase();

            // component name (as defined by @ComponentInfo)
            if (componentName.
                    contains(str)) {

                return true;
            }

            // node name (can be any object)
            if (o.toString().toLowerCase().
                    contains(str)) {

                return true;
            }

            // component description (as defined by @ComponentInfo)
            String description =
                    ComponentUtil.getComponentDescription(cls).toLowerCase();

            if (description.
                    contains(str)) {

                return true;
            }
            
            
            return false;
        }

        @Override
        public String getName() {
            return "String Filter";
        }

        @Override
        public boolean hideWhenMatching() {
            return false;
        }
    }
}
