/* 
 * ObjectTree.java
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

package eu.mihosoft.vrl.reflection;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * JTree based object tree.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ObjectTree extends JTree {

    private static final long serialVersionUID = -8026802668258764060L;
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Objects");

    /**
     * Constructor.
     */
    public ObjectTree() {
        treeModel = new DefaultTreeModel(root);
        setRootVisible(true);
        this.updateUI();

        // we only want to allow selecting one node per selection
        getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);

        // not possible on Mac OS X (Mac & Java is like Windows & security )
        //        setDropMode(DropMode.USE_SELECTION);

        setTransferHandler(new ObjectTransferHandler());
        setDragEnabled(true);

    }

    /**
     * Returns the tree model.
     * @return the tree model
     */
    public DefaultTreeModel getTreeModel() {
        return (DefaultTreeModel) this.treeModel;
    }

    /**
     * Adds an object to the tree.
     * @param o the object that is to be added.
     */
    public void addObject(Object o) {

        if (o != null) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(o);

            boolean alreadyContainsObject = false;

            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode n =
                        (DefaultMutableTreeNode) root.getChildAt(i);

                if (n.getUserObject() == o) {
                    alreadyContainsObject = true;
                    break;
                }
            }

            getTreeModel().insertNodeInto(node,
                    root, root.getChildCount());
            getTreeModel().nodeChanged(node);

        }

//        this.updateUI();
    }

    /**
     * Removes an object from the tree.
     * @param o the object that is to be removed from the tree
     */
    public void removeObject(Object o) {
        MutableTreeNode node = null;

        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode n =
                    (DefaultMutableTreeNode) root.getChildAt(i);

            if (n.getUserObject() == o) {
                node = n;
                break;
            }
        }

        this.getTreeModel().removeNodeFromParent(node);
    }    // neccessary for Java 1.4 (workaround for NullPointerException) 
    //    public void setUI(TreeUI newUI) {
    //        super.setUI(newUI);
    //        TransferHandler handler = getTransferHandler();
    //        setTransferHandler(null);
    //        setTransferHandler(handler);
    //    }
}
