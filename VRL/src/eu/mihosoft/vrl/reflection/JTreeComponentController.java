/* 
 * JTreeComponentController.java
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

import eu.mihosoft.vrl.annotation.ComponentInfo;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class JTreeComponentController implements ComponentController {

    private ComponentControllerImpl controller = new ComponentControllerImpl();
    private DefaultTreeModel model;
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Components");

    public JTreeComponentController(DefaultTreeModel model) {
        this.model = model;
        model.setRoot(root);
    }

    /**
     * Adds a node in alphabetical order
     *
     * @param parent parent node
     * @param child child node to add
     */
    private void addNodeInAlphabeticalOrder(DefaultMutableTreeNode parent,
            DefaultMutableTreeNode child) {
        int n = parent.getChildCount();
        if (n == 0) {
            parent.add(child);
            return;
        }
        DefaultMutableTreeNode node = null;
        for (int i = 0; i < n; i++) {
            node = (DefaultMutableTreeNode) parent.getChildAt(i);
            if (node.toString().toLowerCase().compareTo(
                    child.toString().toLowerCase()) > 0) {
                parent.insert(child, i);
                return;
            }
        }
        parent.add(child);
        return;
    }

    @Override
    public void setParent(ComponentController c) {
        controller.setParent(c);
    }

    @Override
    public void addComponent(Class<?> c) {
        controller.addComponent(c, this);

        if (!ComponentUtil.requestsIgnore(c)) {

            DefaultMutableTreeNode cAddItem = new DefaultMutableTreeNode(c);

            ArrayList<DefaultMutableTreeNode> delList =
                    getComponentNodesByClass(root, c);

            for (DefaultMutableTreeNode cmi : delList) {
                getCategoryNode(root, c).remove(cmi);
            }

//            if (ComponentUtil.isComponent((Class<?>) cAddItem.getUserObject())) {
            DefaultMutableTreeNode node = getCategoryNode(root, c);
            addNodeInAlphabeticalOrder(node, cAddItem);
            model.nodeStructureChanged(node);
//            }
        }
    }

    @Override
    public void removeComponent(Class<?> component) {
        controller.removeComponent(component, this);

        ArrayList<DefaultMutableTreeNode> addItems =
                getComponentNodesByClass(root, component);

        DefaultMutableTreeNode node = getCategoryNode(root, component);

        for (DefaultMutableTreeNode i : addItems) {
            i.removeFromParent();
        }

        model.nodeStructureChanged(node);

//        // remove empty groups
//        if (node.getChildCount()==0) {
//            TreeNode parent = node.getParent();
//            node.removeFromParent();
//            model.nodeStructureChanged(parent);
//        }
    }

    @Override
    public void addComponent(Class<?> component, ComponentController sender) {
        addComponent(component);
    }

    @Override
    public void removeComponent(Class<?> component, ComponentController sender) {
        removeComponent(component);
    }

    /**
     * Returns a list containing all component nodes that are associated with a
     * given class object
     *
     * @param c the class object
     * @return a list containing all component nodes that are associated with a
     * given class object
     */
    public ArrayList<DefaultMutableTreeNode> getComponentNodesByClass(
            DefaultMutableTreeNode root, Class c) {
        ArrayList<DefaultMutableTreeNode> result =
                new ArrayList<DefaultMutableTreeNode>();

        DefaultMutableTreeNode parent = getCategoryNode(root, c);

        for (int i = 0; i < parent.getChildCount(); i++) {
            TreeNode item = parent.getChildAt(i);

            if (item instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode cTempItem = (DefaultMutableTreeNode) item;

                // TODO do we need this?
//                if (cTempItem.getUserObject() instanceof Class<?>
//                        && ComponentUtil.getComponentName(
//                        (Class<?>) cTempItem.getUserObject()).
//                        equals(ComponentUtil.getComponentName(c))) {
//
//                    result.add(cTempItem);
//                }

                if (cTempItem.getUserObject() instanceof Class<?>
                        && ((Class<?>) cTempItem.getUserObject()).getName().
                        equals(c.getName())) {

                    result.add(cTempItem);
                }
            }
        }

        return result;
    }

    /**
     * Returns the category menu as defined in the component info of the
     * specified class. If the requested menus don't exist they will be created.
     *
     * @param root root menu
     * @param c class to add as component
     * @return category menu as defined in the component info of the specified
     * class
     */
    private DefaultMutableTreeNode getCategoryNode(
            final DefaultMutableTreeNode root, final Class<?> c) {
        Collection<String> categories = new ArrayList<String>();
        @SuppressWarnings("unchecked")
        ComponentInfo cInfo = c.getAnnotation(ComponentInfo.class);

        String[] entries = new String[]{"undefined"};

        if (cInfo != null) {
            entries = cInfo.category().split("/");
        }

        for (String s : entries) {
            if (s.length() > 0) {
                categories.add(s);
            }
        }

        DefaultMutableTreeNode parent = root;

        for (String category : categories) {

            if (categories != null) {
                boolean found = false;
                for (int i = 0; i < parent.getChildCount(); i++) {
                    TreeNode comp = parent.getChildAt(i);
                    if (comp instanceof MutableTreeNode) {
                        DefaultMutableTreeNode categoryNode =
                                (DefaultMutableTreeNode) comp;

                        if (categoryNode.getUserObject().equals(category)) {
                            parent = categoryNode;
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    DefaultMutableTreeNode categoryNode =
                            new DefaultMutableTreeNode(category);
//                    parent.add(categoryNode);
                    addNodeInAlphabeticalOrder(parent, categoryNode);
                    parent = categoryNode;
                }
            }
        }

        model.nodeStructureChanged(root);

        return parent;
    }
}
