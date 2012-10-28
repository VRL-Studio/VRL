/* 
 * OrderedBoxLayout.java
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

package eu.mihosoft.vrl.visual;

import eu.mihosoft.vrl.reflection.Pair;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.BoxLayout;
import javax.swing.JComponent;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class OrderedBoxLayout implements LayoutManager2 {

    private BoxLayout layout;
    private JComponent target;
    private boolean sorting;
    private ArrayList<ArrayList<Component>> positions =
            new ArrayList<ArrayList<Component>>();

    public OrderedBoxLayout(JComponent target, int axis) {
        this.target = target;
        this.layout = new BoxLayout(target, axis);
    }

    public void moveTo(Component c, int pos) {
        // only allow move if we don't change position of sorted components
        if (pos < positions.size()
                && !(positions.get(pos) instanceof OrderedLayoutConstraint)) {
            target.remove(c);
            target.add(c, pos);

            target.doLayout();
        }
    }

    private void sort() {
        sorting = true;

        positions.clear();

        for (int i = 0; i < target.getComponentCount(); i++) {
            positions.add(new ArrayList<Component>());
        }

        for (Component comp : target.getComponents()) {
            if (comp instanceof OrderedLayoutConstraint) {
                Integer pos =
                        ((OrderedLayoutConstraint) comp).getLayoutPosition();
                if (pos != null) {

                    pos = Math.min(pos, target.getComponentCount());
                    pos = Math.max(pos, 0);

                    positions.get(pos).add(comp);
                }
            }
        }

        for (int i = 0; i < positions.size(); i++) {
            for (Component comp : positions.get(i)) {
                target.remove(comp);
                target.add(comp, i);
            }
        }

        sorting = false;
    }

    public void swap(Component c1, Component c2) {
        Integer indexC1 = null;
        Integer indexC2 = null;

        for (int i = 0; i < target.getComponentCount(); i++) {
            if (target.getComponent(i) == c1) {
                indexC1 = i;
            }

            if (target.getComponent(i) == c2) {
                indexC2 = i;
            }

            if (indexC1 != null && indexC2 != null) {
                break;
            }
        }

        moveTo(c1, indexC2);
        moveTo(c2, indexC1);

    }

    public void setOrder(ArrayList<Component> order) {
        for (int i = 0; i < order.size(); i++) {
            moveTo(order.get(i), i);
        }
    }

    public ArrayList<Component> getOrder() {
        ArrayList<Component> result = new ArrayList<Component>();

        result.addAll(Arrays.asList(target.getComponents()));

        return result;
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        if (!sorting) {
            sort();
        }
        layout.addLayoutComponent(comp, constraints);
    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        return layout.maximumLayoutSize(target);
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return layout.getLayoutAlignmentX(target);
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return layout.getLayoutAlignmentY(target);
    }

    @Override
    public void invalidateLayout(Container target) {
        layout.invalidateLayout(target);
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        if (!sorting) {
            sort();
        }
        layout.addLayoutComponent(comp, this);
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        layout.removeLayoutComponent(comp);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layout.preferredLayoutSize(target);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        return layout.minimumLayoutSize(target);
    }

    @Override
    public void layoutContainer(Container target) {

        layout.layoutContainer(target);
    }
}
