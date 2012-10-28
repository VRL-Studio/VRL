/* 
 * ComponentTree.java
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

import eu.mihosoft.vrl.io.TextLoader;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.lang.groovy.GroovyCodeEditorComponent;
import eu.mihosoft.vrl.visual.CanvasWindow;
import eu.mihosoft.vrl.visual.TransparentLabel;
import eu.mihosoft.vrl.visual.VSwingUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ComponentTree extends JTree {

    private VisualCanvas canvas;

    public ComponentTree(TreeModel model, VisualCanvas canvas) {
        super(model);
        this.canvas = canvas;
        // we only want to allow selecting one node per selection
        getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);

        setTransferHandler(new ObjectTransferHandler());
        setDragEnabled(true);

        setCellRenderer(new ComponentCellRenderer(canvas));

        ToolTipManager.sharedInstance().registerComponent(this);

        // 26.07.2011: fix for OS X (otherwise custom row height is ignored)
        setRowHeight(0);

        setOpaque(false);


        initMenu();
    }

    private void initMenu() {

        addMouseListener(new CellPopupMenuListener(this));
    }

    static class CellPopupMenuListener extends MouseAdapter {

        private JPopupMenu sessionComponentMenu;
        private JPopupMenu sessionCodeComponentMenu;
        private JPopupMenu groovyComponentMenu;
        private ComponentTree tree;
        private Class<?> component;
        private Point menuLocation = new Point();

        public CellPopupMenuListener(final ComponentTree tree) {
            initSessionComponentMenu();
            initGroovyComponentMenu();
            initSessionCodeComponentMenu();

            this.tree = tree;
        }

        private void initGroovyComponentMenu() {
            this.groovyComponentMenu = new JPopupMenu("Component");


            JMenuItem deleteComponent = new JMenuItem("Delete");

            deleteComponent.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    ComponentUtil.requestRemoval(
                            component, tree.canvas,
                            tree.canvas.getComponentController());
                }
            });

            groovyComponentMenu.add(deleteComponent);
        }

        private void initSessionComponentMenu() {
            this.sessionComponentMenu = new JPopupMenu("Component");

            JMenuItem openComponent = new JMenuItem("Open");

            openComponent.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        tree.canvas.getProjectController().open(
                                component.getName());
                    } catch (IOException ex) {
                        Logger.getLogger(
                                ComponentCellRenderer.class.getName()).
                                log(Level.SEVERE, null, ex);
                    }
                }
            });

            sessionComponentMenu.add(openComponent);

            JMenuItem copyComponent = new JMenuItem("Copy");

            copyComponent.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        tree.canvas.getProjectController().copy(
                                component.getName());
                    } catch (IOException ex) {
                        Logger.getLogger(
                                ComponentCellRenderer.class.getName()).
                                log(Level.SEVERE, null, ex);
                    }
                }
            });

            sessionComponentMenu.add(copyComponent);

            JMenuItem deleteComponent = new JMenuItem("Delete");

            deleteComponent.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    ComponentUtil.requestRemoval(
                            component, tree.canvas,
                            tree.canvas.getComponentController());
                }
            });

            sessionComponentMenu.add(deleteComponent);
        }

        private void initSessionCodeComponentMenu() {
            this.sessionCodeComponentMenu = new JPopupMenu("Component");

            JMenuItem openComponent = new JMenuItem("Open");

            openComponent.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    String componentName = component.getName();

                    try {
                        File codeFile =
                                tree.canvas.getProjectController().getProject().
                                getSourceFileByEntryName(componentName);

                        TextLoader loader = new TextLoader();
                        String code = (String) loader.loadFile(codeFile);

                        GroovyCodeEditorComponent editWindow =
                                new GroovyCodeEditorComponent(code);

                        CanvasWindow w =
                                tree.canvas.addObject(editWindow, menuLocation);
                        
//                        String title = "Groovy Code: " 
//                                +tree.canvas.getProjectController().
//                                getProject().
//                                getEntryNameWithoutDefaultPackage(componentName);
//                        
//                        w.setTitle(title);

                    } catch (IOException ex) {
                        Logger.getLogger(
                                ComponentCellRenderer.class.getName()).
                                log(Level.SEVERE, null, ex);
                    }
                }
            });

            sessionCodeComponentMenu.add(openComponent);

            JMenuItem copyComponent = new JMenuItem("Copy");

            copyComponent.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
//                    try {
//                        tree.canvas.getProjectController().copy(
//                                component.getName());
//                    } catch (IOException ex) {
//                        Logger.getLogger(
//                                ComponentCellRenderer.class.getName()).
//                                log(Level.SEVERE, null, ex);
//                    }
                }
            });

            copyComponent.setEnabled(false);

            sessionCodeComponentMenu.add(copyComponent);

            JMenuItem deleteComponent = new JMenuItem("Delete");

            deleteComponent.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    ComponentUtil.requestRemoval(
                            component, tree.canvas,
                            tree.canvas.getComponentController());
                }
            });

            sessionCodeComponentMenu.add(deleteComponent);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            component = null;

            Point p = e.getPoint();
            TreePath path = tree.getClosestPathForLocation(p.x, p.y);

            boolean pathNotNull = path != null;
            boolean isTreeNode =
                    path.getLastPathComponent() instanceof DefaultMutableTreeNode;

            if (pathNotNull && isTreeNode) {

                DefaultMutableTreeNode n =
                        (DefaultMutableTreeNode) path.getLastPathComponent();

                if (n.getUserObject() instanceof Class) {

                    tree.addSelectionPath(path);
                    component = (Class<?>) n.getUserObject();
                }
            }

            if (component == null) {
                return;
            }

            if (SwingUtilities.isRightMouseButton(e)
                    && e.getClickCount() == 1) {
                menuLocation = e.getPoint();
                if (ComponentUtil.isVisualSessionComponent(component)) {
                    sessionComponentMenu.show(e.getComponent(), p.x, p.y);
                } else if (ComponentUtil.isCodeSessionComponent(component)) {
                    sessionCodeComponentMenu.show(e.getComponent(), p.x, p.y);

                } else if (ComponentUtil.isComponent(component)
                        && ComponentUtil.allowsRemoval(component)
                        && tree.canvas.getCodes().
                        getByName(VLangUtils.shortNameFromFullClassName(
                        component.getName())) != null) {
                    groovyComponentMenu.show(e.getComponent(), p.x, p.y);
                }
            } else if (SwingUtilities.isLeftMouseButton(e)
                    && e.getClickCount() == 2) {

                if (ComponentUtil.isVisualSessionComponent(component)) {
                    try {
                        tree.canvas.getProjectController().open(
                                component.getName());
                    } catch (IOException ex) {
                        Logger.getLogger(
                                CellPopupMenuListener.class.getName()).
                                log(Level.SEVERE, null, ex);
                    }
                } else if (ComponentUtil.isCodeSessionComponent(component)) {
                    String componentName = component.getName();

                    try {
                        File codeFile =
                                tree.canvas.getProjectController().getProject().
                                getSourceFileByEntryName(componentName);

                        TextLoader loader = new TextLoader();
                        String code = (String) loader.loadFile(codeFile);

                        GroovyCodeEditorComponent editWindow =
                                new GroovyCodeEditorComponent(code);


                        CanvasWindow w =
                                tree.canvas.addObject(editWindow, menuLocation);
                        
//                        String title = "Groovy Code: " 
//                                +tree.canvas.getProjectController().
//                                getProject().
//                                getEntryNameWithoutDefaultPackage(componentName);
//                        
//                        w.setTitle(title);


                    } catch (IOException ex) {
                        Logger.getLogger(
                                ComponentCellRenderer.class.getName()).
                                log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}

class ComponentCellRenderer implements TreeCellRenderer {

    private static final ImageIcon leafVSessionIcon = new ImageIcon(
            ComponentTree.class.getResource(
            "/eu/mihosoft/vrl/resources/images/tree_leaf-vsession01.png"));
    private static final ImageIcon leafCSessionIcon = new ImageIcon(
            ComponentTree.class.getResource(
            "/eu/mihosoft/vrl/resources/images/tree_leaf-csession01.png"));
    private static final ImageIcon leafComponentIcon = new ImageIcon(
            ComponentTree.class.getResource(
            "/eu/mihosoft/vrl/resources/images/tree_leaf-component01.png"));
    private static final ImageIcon closedIcon = new ImageIcon(
            ComponentTree.class.getResource(
            "/eu/mihosoft/vrl/resources/images/treenode01_closed.png"));
    private static final ImageIcon openedIcon = new ImageIcon(
            ComponentTree.class.getResource(
            "/eu/mihosoft/vrl/resources/images/treenode01_opened.png"));
    JLabel titleLabel;
    JPanel renderer;
    DefaultTreeCellRenderer defaultRenderer;
    Color backgroundNonSelectionColor = VSwingUtil.TRANSPARENT_COLOR;
    Color backgroundSelectionColor;
    Color textColor = Color.white;
    private int iconHeight = 20;
    private int insetLeftRight = 0;
    private int insetTopBottom = 0;
    private VisualCanvas canvas;

    public ComponentCellRenderer(VisualCanvas canvas) {
        this.canvas = canvas;

        renderer = new JPanel() {

            @Override
            public Dimension getPreferredSize() {
                // prevents too large/small cell height
                return new Dimension(
                        super.getPreferredSize().width
                        + insetLeftRight, iconHeight + insetTopBottom);
            }

            @Override
            public Dimension getMinimumSize() {
                // prevents too large/small cell height
                return new Dimension(
                        super.getMinimumSize().width
                        + insetLeftRight, iconHeight + insetTopBottom);
            }

            @Override
            public Dimension getSize() {
                // prevents too large/small cell height
                return new Dimension(
                        super.getSize().width
                        + insetLeftRight, iconHeight + insetTopBottom);
            }
        };



        if (!UIManager.getLookAndFeel().getName().contains("Nimbus")) {
            renderer.setBackground(backgroundNonSelectionColor);
            renderer.setBorder(new EmptyBorder(1, 1, 1, 1));

            insetLeftRight = 2;
            insetTopBottom = 1;
        } else {
            renderer.setBackground(VSwingUtil.TRANSPARENT_COLOR);
        }

        titleLabel = new TransparentLabel(" ");
        titleLabel.setForeground(Color.white);
        titleLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        renderer.add(titleLabel);
        renderer.setLayout(new GridLayout());
        initDefaultRenderer();
    }

    private void initDefaultRenderer() {
        // ui setup
        // init JTree
        // Set the icon for leaf nodes.
        defaultRenderer = new DefaultTreeCellRenderer();

        if (leafComponentIcon != null) {
            defaultRenderer.setMinimumSize(
                    new Dimension(leafComponentIcon.getIconWidth(),
                    leafComponentIcon.getIconHeight()));
            defaultRenderer.setLeafIcon(leafComponentIcon);
            titleLabel.setIcon(leafComponentIcon);
            iconHeight = leafComponentIcon.getIconHeight();

            defaultRenderer.setClosedIcon(closedIcon);
            defaultRenderer.setOpenIcon(openedIcon);
            // sets tree background
            defaultRenderer.setBackgroundNonSelectionColor(
                    backgroundNonSelectionColor);
            defaultRenderer.setTextNonSelectionColor(textColor);

            defaultRenderer.setBackground(VSwingUtil.TRANSPARENT_COLOR);

            backgroundSelectionColor =
                    defaultRenderer.getBackgroundSelectionColor();

        } else {
            System.err.println("Leaf icon missing; using default.");
        }
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        Component returnValue = null;

        if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
            Object userObject =
                    ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof Class) {

                final Class<?> cls = (Class<?>) userObject;

                String prefix = "";

                String packageName = "edu.gcsc.vrl.ug";

                String clsPackage =
                        VLangUtils.packageNameFromFullClassName(cls.getName());

                if (packageName.equals(clsPackage)) {
                    prefix = cls.getName().substring(
                            packageName.length() + 1, packageName.length() + 2);

                    prefix = "(" + prefix + ") ";
                }

                String name = prefix
                        + ComponentUtil.getComponentName(cls);

                titleLabel.setText(name);

                String toolTipText =
                        ComponentUtil.getComponentDescription(
                        cls);

                renderer.setToolTipText(toolTipText);


                if (selected && !UIManager.getLookAndFeel().getName().
                        contains("Nimbus")) {
                    renderer.setBackground(backgroundSelectionColor);
                    renderer.setBorder(new LineBorder(Color.gray, 1));

                } else {
                    renderer.setBackground(backgroundNonSelectionColor);
                    renderer.setBorder(new EmptyBorder(1, 1, 1, 1));
                }

                renderer.setEnabled(tree.isEnabled());
                returnValue = renderer;

                if (ComponentUtil.isCodeSessionComponent(cls)) {
                    titleLabel.setIcon(leafCSessionIcon);
                } else if (ComponentUtil.isVisualSessionComponent(cls)) {
                    titleLabel.setIcon(leafVSessionIcon);
                } else {
                    titleLabel.setIcon(leafComponentIcon);
                }

            } // end if (userObject instanceof Class)
        }
        if (returnValue == null) {
            returnValue = defaultRenderer.getTreeCellRendererComponent(tree,
                    value, selected, expanded, leaf, row, hasFocus);
        }
        return returnValue;
    }
}
