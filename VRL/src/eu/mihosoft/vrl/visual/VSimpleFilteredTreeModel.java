/* 
 * VSimpleFilteredTreeModel.java
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

import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VSimpleFilteredTreeModel extends DefaultTreeModel {
// The TreeModel is based on ideas from some forum post:
// http://www.velocityreviews.com/forums/t131224-filtered-jtree.html

    private TreeModel internalModel;
    private VFilter filter;

    /**
     *
     * @param root
     * @param filter
     */
    public VSimpleFilteredTreeModel(DefaultMutableTreeNode root) {
        super(root);
    }

    /**
     *
     * @param root
     * @param filter
     */
    public VSimpleFilteredTreeModel(DefaultMutableTreeNode root, VFilter filter) {
        super(root);
        this.filter = filter;
    }

    @Override
    public Object getChild(Object parent, int index) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent;
        if (filter == null) {
            return node.getChildAt(index);
        } else {

            int pos = 0;
            for (int i = 0, cnt = 0; i < node.getChildCount(); i++) {
                if (filter.matches(((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject())) {
                    if (cnt++ == index) {
                        pos = i;
                        break;
                    }
                }
            }
            return node.getChildAt(pos);
        }
    }

    @Override
    public boolean isLeaf(Object node) {
        DefaultMutableTreeNode n = (DefaultMutableTreeNode) node;

        if (n.getUserObject() instanceof Class<?>) {
            return true;
        }
        return false;
    }

    @Override
    public int getChildCount(Object parent) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent;
        if (filter == null) {
            return node.getChildCount();
        } else {

            int childCount = 0;
            Enumeration children = node.children();
            while (children.hasMoreElements()) {

                if (filter.matches(((DefaultMutableTreeNode) children.nextElement()).getUserObject())) {
                    childCount++;
                }

            }
            return childCount;
        }
    }

    /**
     * Sets the filter and does update the tree structure.
     *
     * @param filter
     */
    public void setFilter(VFilter filter) {

        if (this.filter == null || !this.filter.equals(filter)) {

            this.filter = filter;
            Object[] path = {root};
            int[] childIndices = new int[root.getChildCount()];
            Object[] children = new Object[root.getChildCount()];

            for (int i = 0; i < root.getChildCount(); i++) {
                childIndices[i] = i;
                children[i] = root.getChildAt(i);
            }

            nodeChanged(root);

            fireTreeStructureChanged(this, path, childIndices, children);
        }
    }
}
