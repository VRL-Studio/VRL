/* 
 * VFilteredTreeModel.java
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

import eu.mihosoft.vrl.system.VParamUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VFilteredTreeModel extends DefaultTreeModel {

//    private TreeModel nestedModel;
    private Map<DefaultMutableTreeNode, FilterNodeInfo> filterInfo =
            new HashMap<DefaultMutableTreeNode, FilterNodeInfo>();

//    public VFilteredTreeModel(
//            TreeModel nestedModel,
//            DefaultMutableTreeNode root) {
//
//        super(root);
//
//        init(root, );
//    }
    public VFilteredTreeModel(DefaultMutableTreeNode root) {
        super(root);
        init(root);
    }

    private void init(DefaultMutableTreeNode root) {
        VParamUtil.throwIfNull(root);
//        this.nestedModel = model;
        updateFilterNodeInfo();
    }

    private void updateFilterNodeInfo() {

        filterInfo.clear();

        Enumeration treeEnum =
                ((DefaultMutableTreeNode) getRoot()).breadthFirstEnumeration();

        DefaultMutableTreeNode node = null;

        while (treeEnum.hasMoreElements()) {
            node = (DefaultMutableTreeNode) treeEnum.nextElement();
            filterInfo.put(node, new FilterNodeInfo());
        }
    }

    private Collection<DefaultMutableTreeNode> getSubTreeNodes(
            DefaultMutableTreeNode root) {
        Collection<DefaultMutableTreeNode> result =
                new ArrayList<DefaultMutableTreeNode>();

        Enumeration treeEnum = root.breadthFirstEnumeration();

        DefaultMutableTreeNode node = null;

        while (treeEnum.hasMoreElements()) {
            node = (DefaultMutableTreeNode) treeEnum.nextElement();
            result.add(node);
        }

        return result;
    }

//    private void setHidden(DefaultMutableTreeNode n,
//            boolean hidden) {
//        setHidden(filterInfo, n, hidden);
//    }
//
//    private void setHidden(Iterable<DefaultMutableTreeNode> nodes,
//            boolean hidden) {
//        setHidden(filterInfo, nodes, hidden);
//    }
//
//    private void setMatches(DefaultMutableTreeNode n,
//            boolean matches) {
//        setMatches(filterInfo, n, matches);
//    }
//
//    private void setMatches(Iterable<DefaultMutableTreeNode> nodes,
//            boolean matches) {
//        setMatches(filterInfo, nodes, matches);
//    }
    private void setHidden(Map<DefaultMutableTreeNode, FilterNodeInfo> map,
            DefaultMutableTreeNode n,
            boolean hidden) {
        map.get(n).setHidden(hidden);
    }

    private void setHidden(Map<DefaultMutableTreeNode, FilterNodeInfo> map,
            Iterable<DefaultMutableTreeNode> nodes,
            boolean hidden) {
        for (DefaultMutableTreeNode n : nodes) {
            setHidden(map, n, hidden);
        }
    }

    private void setMatches(Map<DefaultMutableTreeNode, FilterNodeInfo> map,
            DefaultMutableTreeNode n,
            boolean matches) {
        map.get(n).setMatches(matches);
    }

    private void setMatches(Map<DefaultMutableTreeNode, FilterNodeInfo> map,
            Iterable<DefaultMutableTreeNode> nodes,
            boolean matches) {
        for (DefaultMutableTreeNode n : nodes) {
            setMatches(map, n, matches);
        }
    }

//    @Override
//    public Object getRoot() {
//        nestedModel.getRoot();
//    }
    @Override
    public Object getChild(Object parent, int index) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent;

        int pos = 0;
        for (int i = 0, cnt = 0; i < node.getChildCount(); i++) {

            FilterNodeInfo info =
                    filterInfo.get((DefaultMutableTreeNode) node.getChildAt(i));

            if (info == null || info.isMatches()) {
                if (cnt++ == index) {
                    pos = i;
                    break;
                }
            }
        }
        return node.getChildAt(pos);
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

        int childCount = 0;
        Enumeration children = node.children();
        while (children.hasMoreElements()) {

            FilterNodeInfo info =
                    filterInfo.get(
                    (DefaultMutableTreeNode) children.nextElement());

            if (info == null || info.isMatches()) {
                childCount++;
            }
        }
        return childCount;
    }

    /**
     * Sets the filter and does update the tree structure.
     * @param filter 
     */
    public void setFilter(Iterable<VFilter> filter) {
        updateFilterNodeInfo();

        Collection<VFilter> priorityFilter = new ArrayList<VFilter>();
        Collection<VStringFilter> stringFilter = new ArrayList<VStringFilter>();

        for (VFilter f : filter) {
            if (f instanceof VStringFilter) {
                stringFilter.add((VStringFilter) f);
            } else {
                priorityFilter.add(f);
            }
        }

        Map<DefaultMutableTreeNode, FilterNodeInfo> localFilterInfo =
                new HashMap<DefaultMutableTreeNode, FilterNodeInfo>(filterInfo);

        Iterable<DefaultMutableTreeNode> allTreeNodes =
                getSubTreeNodes((DefaultMutableTreeNode) getRoot());

        // apply all filters that do not depend on user input
        for (DefaultMutableTreeNode n : allTreeNodes) {

            setMatches(localFilterInfo, n, false);

            for (VFilter f : priorityFilter) {

                if (f.matches(n) && !f.hideWhenMatching()) {
                    setMatches(localFilterInfo, n, true);

                    // match the path from us to the root node
                    Collection<DefaultMutableTreeNode> pathToGlobalRoot =
                            new ArrayList<DefaultMutableTreeNode>();
                    for (TreeNode m : n.getPath()) {
                        pathToGlobalRoot.add((DefaultMutableTreeNode) m);
                    }
                    setMatches(localFilterInfo, pathToGlobalRoot, true);
                }

                if (f.matches(n) && f.hideWhenMatching()) {

                    setMatches(localFilterInfo, n, false);
                    setMatches(localFilterInfo, getSubTreeNodes(n), false);
                    setHidden(localFilterInfo, n, true);
                    setHidden(localFilterInfo, getSubTreeNodes(n), true);
                    break;
                }
            }
        }


        // apply all filters that may hide components and do depend on user input
        for (DefaultMutableTreeNode n : allTreeNodes) {

            for (VFilter f : stringFilter) {

                // if already hidden no check necessary
                if (localFilterInfo.get(n).isHidden()) {
                    break;
                }
                
                // don't apply filters that show nodes
                if (!f.hideWhenMatching()) {
                    continue;
                }

                if (f.matches(n)) {
                    setMatches(localFilterInfo, n, false);
                    setMatches(localFilterInfo, getSubTreeNodes(n), false);
                    setHidden(localFilterInfo, n, true);
                    setHidden(localFilterInfo, getSubTreeNodes(n), true);

                    break;
                }
            }
        }

        // apply all filters that may show components and do depend on user input
        for (DefaultMutableTreeNode n : allTreeNodes) {

            for (VFilter f : stringFilter) {

                // if already hidden no check necessary
                // (hidden is stronger than show)
                if (localFilterInfo.get(n).isHidden()) {
                    break;
                }
                
                // don't apply filters that hide nodes
                if (f.hideWhenMatching()) {
                    continue;
                }

                if (f.matches(n)) {

                    setMatches(localFilterInfo, n, true);

                    // match the path from us to the root node
                    Collection<DefaultMutableTreeNode> pathToGlobalRoot =
                            new ArrayList<DefaultMutableTreeNode>();
                    for (TreeNode m : n.getPath()) {
                        pathToGlobalRoot.add((DefaultMutableTreeNode) m);
                    }

                    setMatches(localFilterInfo, pathToGlobalRoot, true);

                    Collection<DefaultMutableTreeNode> notHiddenSubTreeNodes =
                            new ArrayList<DefaultMutableTreeNode>();

                    for (DefaultMutableTreeNode m : getSubTreeNodes(n)) {
                        if (!localFilterInfo.get(m).isHidden()) {
                            notHiddenSubTreeNodes.add(m);
                        }
                    }

                    setMatches(localFilterInfo, notHiddenSubTreeNodes, true);
                    
                    break;
                }
            }
        }

        filterInfo = localFilterInfo;

        nodeStructureChanged(root);

        reload();
    }

//    private void filterNodes(DefaultMutableTreeNode root, VFilter filter) {
//        if (filter.matches(root)) {
//
//            setMatches(getSubTreeNodes(root), true);
//
//            Collection<DefaultMutableTreeNode> pathToGlobalRoot =
//                    new ArrayList<DefaultMutableTreeNode>();
//
//            for (TreeNode n : root.getPath()) {
//                pathToGlobalRoot.add((DefaultMutableTreeNode) n);
//            }
//
//            setMatches(pathToGlobalRoot, true);
//
//        } else {
//            setMatches(Arrays.asList(root), false);
//            for (int i = 0; i < root.getChildCount(); i++) {
//                filterNodes((DefaultMutableTreeNode) root.getChildAt(i), filter);
//            }
//        }
//    }
    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        updateFilterNodeInfo();
        super.valueForPathChanged(path, newValue);
//        System.err.println("valueForPathChanged");
    }

    @Override
    protected void fireTreeStructureChanged(Object source, Object[] path,
            int[] childIndices,
            Object[] children) {
//        updateFilterNodeInfo();
        super.fireTreeStructureChanged(source, path, childIndices, children);
//        System.err.println("fireTreeStructureChanged");
    }

    @Override
    protected void fireTreeNodesChanged(Object source, Object[] path,
            int[] childIndices,
            Object[] children) {

        super.fireTreeNodesChanged(source, path, childIndices, children);
//        System.err.println("fireTreeNodesChanged");
        updateFilterNodeInfo();
    }

    @Override
    protected void fireTreeNodesInserted(Object source, Object[] path,
            int[] childIndices,
            Object[] children) {
        super.fireTreeNodesInserted(source, path, childIndices, children);
//        System.err.println("fireTreeNodesInserted");
        updateFilterNodeInfo();
    }

    @Override
    protected void fireTreeNodesRemoved(Object source, Object[] path,
            int[] childIndices,
            Object[] children) {
        super.fireTreeNodesRemoved(source, path, childIndices, children);
//        System.err.println("fireTreeNodesRemoved");
        updateFilterNodeInfo();
    }
}

class FilterNodeInfo {

    private boolean matches = true;
    private boolean hidden = false;

    public FilterNodeInfo() {
        //
    }

    /**
     * @return the matches
     */
    public boolean isMatches() {
        return matches;
    }

    /**
     * @param matches the matches to set
     */
    public void setMatches(boolean matches) {
        this.matches = matches;
    }

    /**
     * @return the hidden
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * @param hidden the hidden to set
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
