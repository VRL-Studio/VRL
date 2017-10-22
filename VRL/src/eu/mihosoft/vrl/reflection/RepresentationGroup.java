/* 
 * RepresentationGroup.java
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

import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.visual.CanvasLabel;
import eu.mihosoft.vrl.visual.CanvasWindow;
import eu.mihosoft.vrl.visual.Style;
import eu.mihosoft.vrl.visual.VComponent;
import eu.mihosoft.vrl.visual.VSwingUtil;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.PopupMenu;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Visually groups parameters or methods
 *
 * (TypeRepresentationContainer/DefaultMethodRepresentation).
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class RepresentationGroup extends JPanel {

    private String title = "";
    private String identifier = "default";
    private boolean minimized;
    private String description;
    private JLabel nameLabel = new CanvasLabel();
    private Map<String, RepresentationGroup> groups =
            new HashMap<String, RepresentationGroup>();
    public static int DEFAULT_BORDER_CORNER_ARC_WIDTH = 16;
//    public static int DEFAULT_TITLEBAR_CORNER_ARC_WIDTH = 18;
    private JComponent titleContainer;
    private Triangle triangle;
    private boolean hover;

    /**
     * Constructor.
     */
    public RepresentationGroup() {
        initialize();
    }

    /**
     * Constructor.
     *
     * @param title group title (shown via label)
     * @param identifier group identifier
     * @param minimized determines whether group shall be minimize
     * @param desc group description (shown via tooltip)
     */
    public RepresentationGroup(String title, String identifier, boolean minimized, String desc) {
        this.title = title;
        this.identifier = identifier;
        this.minimized = minimized;
        this.description = desc;

        initialize();
//        setBorder(BorderFactory.createLineBorder(Color.black));    

    }

    /**
     * @return the titleContainer
     */
    protected JComponent getTitleContainer() {
        return titleContainer;
    }

    private void initPopupMenu(final JPopupMenu popupMenu) {
        JMenuItem copyItem = new JMenuItem("Copy Parameters");
        copyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ClipboardUtil.paramsToClipboard(getParams());
            }
        });

        popupMenu.add(copyItem);

        JMenuItem pasteItem = new JMenuItem("Paste Parameters");
        pasteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ClipboardUtil.clipboardToParams(getParams());
            }
        });

        popupMenu.add(pasteItem);
    }

    private static class Triangle extends JPanel {

        private boolean expanded = true;

        public Triangle() {
            Dimension size = new Dimension(14, 14);
            super.setBounds(getX(), getY(), size.width, size.height);
            setMinimumSize(size);
            setPreferredSize(size);
            setMaximumSize(size);
        }

        @Override
        public void setBounds(int x, int y, int width, int height) {
            super.setBounds(x, y, getWidth(), getHeight());
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int padding = 3;

            Point p1;
            Point p2;
            Point p3;

            if (expanded) {
                p1 = new Point(padding, padding);
                p2 = new Point(getWidth() - padding, padding);
                p3 = new Point(getWidth() / 2, getHeight() - padding);
            } else {
                p1 = new Point(padding, padding);
                p2 = new Point(padding, getHeight() - padding);
                p3 = new Point(getWidth() - padding, getHeight() / 2);
            }

            int[] xs = {p1.x, p2.x, p3.x};
            int[] ys = {p1.y, p2.y, p3.y};

            Polygon triangle = new Polygon(xs, ys, xs.length);

            Style style = getStyle();

            Composite original = g2.getComposite();

            if (style != null) {
                g2.setColor(style.getBaseValues().getColor(
                        CanvasWindow.BORDER_COLOR_KEY));

                AlphaComposite ac1 =
                        AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                        style.getBaseValues().getFloat(
                        CanvasWindow.TRANSPARENCY_KEY));
                g2.setComposite(ac1);
            }

            g2.fillPolygon(triangle);

            g2.setComposite(original);
        }

        private Style getStyle() {
            Component c = VSwingUtil.getParent(this, VComponent.class);

            if (c == null || !(c instanceof VComponent)) {
                return null;
            } else {
                return ((VComponent) c).getStyle();
            }
        }

        /**
         * @return the expanded
         */
        public boolean isExpanded() {
            return expanded;
        }

        /**
         * @param expanded the expanded to set
         */
        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }

        public void toggleExpand() {
            expanded = !expanded;
        }
    }

    /**
     * initializes this group.
     */
    private void initialize() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        titleContainer = Box.createHorizontalBox();
        getTitleContainer().setAlignmentX(0);

        triangle = new Triangle();

        getTitleContainer().add(triangle);

        nameLabel.setText(title);
        getTitleContainer().add(nameLabel);
        getTitleContainer().add(Box.createHorizontalGlue());

        add(getTitleContainer());

        nameLabel.setToolTipText(description);

        if (identifier.equals("default")
                || identifier.startsWith("default-")) {
            getTitleContainer().setVisible(false);
        } else {
            getTitleContainer().setVisible(true);
        }



        final JPopupMenu popupMenu = new JPopupMenu("Group");

        initPopupMenu(popupMenu);

        nameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2 && e.getButton() == 1) {
                    toggleMinMax();
                }

                if (e.getButton() == 3) {
                    popupMenu.show(nameLabel, e.getX(), e.getY());

                    for (MenuElement mE : popupMenu.getSubElements()) {
                        if (mE instanceof JMenuItem) {
                            if (((JMenuItem) mE).getText().contains("Paste")) {
                                ((JMenuItem) mE).setEnabled(
                                        ClipboardUtil.
                                        isClipboardContentCompatible(getParams()));
                            }
                        }
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hoverOn();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverOff();
            }
        });

        triangle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && e.getButton() == 1) {
                    toggleMinMax();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hoverOn();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverOff();
            }
        });

        updateMinimzedState();

        setBorder(new EmptyBorder(2, 3, 3, 3));

    }

    private ArrayList<TypeRepresentationBase> getParams() {
        ArrayList<Component> components =
                VSwingUtil.getAllChildren(RepresentationGroup.this,
                TypeRepresentationBase.class);
        ArrayList<TypeRepresentationBase> params =
                new ArrayList<TypeRepresentationBase>();
        // typecheck has been done before
        for (Component comp : components) {
            params.add((TypeRepresentationBase) comp);
        }
        return params;
    }

    private void hoverOn() {
        hover = true;
        repaint();
    }

    private void hoverOff() {
        hover = false;
        repaint();
    }

    /**
     * Adds a component to this group. Depending on the groupInfo string
     * subgroups will be created.
     *
     * @param groupInfo group info, see
     * {@link eu.mihosoft.vrl.annotation.ParamGroupInfo}
     * @param index parameter index
     * @param c component to add
     * @return topmost group that has been added to this group
     */
    public RepresentationGroup addComponentToGroup(String groupInfo, int index, JComponent c) {

        String levelString = groupInfo.split(";")[0].trim();

        String[] levelInfo = levelString.split("\\|");

        String levelId = levelInfo[0].trim();

        // each parameter gets its own group
        if (levelId.equals("default")) {
            levelId = "default-" + index;
        }

        boolean levelExpand = true;
        String levelDesc = "no description";

        if (levelInfo.length > 1) {
            levelExpand = Boolean.parseBoolean(levelInfo[1].trim());
        }

        if (levelInfo.length > 2) {
            levelDesc = levelInfo[2].trim();
        }

        RepresentationGroup result = groups.get(levelId);

        if (result == null) {

            addGroup(createInstance(levelId, levelId, !levelExpand, levelDesc));
            result = groups.get(levelId);
        }

        if (groupInfo.contains(";")) {
            String subId = groupInfo.substring(groupInfo.indexOf(";") + 1);

            if (!subId.trim().isEmpty()) {
                result = result.addComponentToGroup(subId, index, c);
            }
        }
        result.add(Box.createVerticalGlue());
        result.add(c);

        return result;
    }

    public RepresentationGroup createInstance(
            String title,
            String levelId,
            boolean minimize,
            String levelDesc) {

        try {
            return getClass().getConstructor(
                    String.class, String.class, boolean.class, String.class).
                    newInstance(title, levelId, minimize, levelDesc);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(RepresentationGroup.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(RepresentationGroup.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(RepresentationGroup.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(RepresentationGroup.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(RepresentationGroup.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(RepresentationGroup.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        return new RepresentationGroup(title, identifier, minimized, title);
    }

    public RepresentationGroup getDirectChildById(String levelId) {
        return groups.get(levelId);
    }

    /**
     * Returns all subgroups.
     *
     * @return all subgroups
     */
    public Collection<RepresentationGroup> getGroups() {

        ArrayList<RepresentationGroup> result = new ArrayList<RepresentationGroup>();

        result.addAll(groups.values());

        for (RepresentationGroup g : groups.values()) {
            result.addAll(g.getGroups());
        }

        return result;
    }

    public void addGroup(RepresentationGroup group) {
        groups.put(group.getIdentifier(), group);
        add(group);
    }

    void setTitleVisible(boolean v) {
        if (!identifier.equals("default")) {
            getTitleContainer().setVisible(v);
        }
    }

    private Style getStyle() {
        Component c = VSwingUtil.getParent(this, VComponent.class);

        if (c == null || !(c instanceof VComponent)) {
            return null;
        } else {
            return ((VComponent) c).getStyle();
        }
    }

    @Override
    public void paintComponent(Graphics g) {


        Style style = getStyle();

        if (style == null || !titleContainer.isVisible() || !hover) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Composite original = g2.getComposite();

        if (style != null) {
            g2.setColor(style.getBaseValues().getColor(
                    CanvasWindow.BORDER_COLOR_KEY));

            AlphaComposite ac1 =
                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    style.getBaseValues().getFloat(
                    CanvasWindow.TRANSPARENCY_KEY));
            g2.setComposite(ac1);
        }

        BasicStroke stroke =
                new BasicStroke(1.f);

        g2.setStroke(stroke);

        g2.drawLine(0, 0 + getTitleContainer().getHeight() / 2, 3, 0 + getTitleContainer().getHeight() / 2);
        g2.drawLine(0, 0 + getTitleContainer().getHeight() / 2, 0, getHeight());
        g2.drawLine(0, getHeight() - 1, 3, getHeight() - 1);

        g2.drawLine(getWidth() - 1, 0 + getTitleContainer().getHeight() / 2, getWidth() - 1 - 3, 0 + getTitleContainer().getHeight() / 2);
        g2.drawLine(getWidth() - 1, 0 + getTitleContainer().getHeight() / 2, getWidth() - 1, getHeight());
        g2.drawLine(getWidth() - 1, getHeight() - 1, getWidth() - 1 - 3, getHeight() - 1);

        g2.setComposite(original);

    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
        nameLabel.setText(title);
    }

    /**
     * @return the minimized
     */
    public boolean isMinimized() {
        return minimized;
    }

    /**
     * @param minimized the minimized to set
     */
    public void setMinimized(boolean minimized) {
        this.minimized = minimized;

        triangle.setExpanded(!minimized);

        for (RepresentationGroup g : groups.values()) {
            g.setVisible(!minimized);
        }

        for (Component c : getComponents()) {

            if (c instanceof TypeRepresentationContainer) {

                c.setVisible(!minimized);
            }
        }
    }

    public void toggleMinMax() {
        setMinimized(!minimized);
    }

    public void updateMinimzedState() {

        setMinimized(minimized);

        for (RepresentationGroup g : groups.values()) {
            g.updateMinimzedState();
        }
    }

    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;

        nameLabel.setToolTipText(description);
    }
}
