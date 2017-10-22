/* 
 * ResizableContainer.java
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

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseMotionListener;
import javax.swing.BoxLayout;
import javax.swing.JComponent;

/**
 * A container that can be resized via mouse dragging gesture.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ResizableContainer extends TransparentPanel implements Disposable {

    Component parent;
    ComponentListener l;
    RulerContainer rulerContainer;

    /**
     * Constructor.
     *
     * @param component child component
     * @param parent parent component
     */
    public ResizableContainer(Component component) {
        this(component, null);
    }

    public void addExternalListener(MouseMotionListener l) {
        rulerContainer.getRuler().getExternalListeners().add(l);
    }

    public boolean removeExternalListener(MouseMotionListener l) {
        return  rulerContainer.getRuler().getExternalListeners().remove(l);
    }

    /**
     * Constructor.
     *
     * @param component child component
     * @param parent parent component
     */
    public ResizableContainer(Component component, Component parent) {

        VLayout layout = new VLayout(
                new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.setLayout(layout);

        rulerContainer = new RulerContainer(this);

        this.add(rulerContainer);
        this.add(new ComponentContainer(this, component));

        setOpaque(false);

        if (parent != null) {
            this.parent = parent;
            l = new ComponentListener() {
                @Override
                public void componentResized(ComponentEvent ce) {
                    //
                    ResizableContainer.this.doLayout();
                }

                @Override
                public void componentMoved(ComponentEvent ce) {
                    //
                }

                @Override
                public void componentShown(ComponentEvent ce) {
                    //
                }

                @Override
                public void componentHidden(ComponentEvent ce) {
                    //
                }
            };

            parent.addComponentListener(l);
        }
    }

    @Override
    public void dispose() {
        if (parent != null) {
            parent.removeComponentListener(l);
            parent = null;
        }
    }
}

class ComponentContainer extends TransparentPanel implements
        VLayoutControllerProvider {

    private ResizableContainer parent;

    public ComponentContainer(ResizableContainer parent, Component component) {
        this.parent = parent;
        add(component);
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
    }

    @Override
    public VLayoutController getLayoutController() {
        return new VLayoutController() {
            @Override
            public void layoutComponent(JComponent c) {
                c.setBounds(0, 0, parent.getWidth(), parent.getHeight());
            }
        };
    }

    @Override
    public void setBoundsLocked(boolean value) {
        // we ignore this
    }

    @Override
    public boolean isIngoredByLayout() {
        // we ignore this
        return false;
    }
}

class RulerContainer extends TransparentPanel implements
        VLayoutControllerProvider {

    private ResizableContainer parent;
    private Ruler ruler;

    public RulerContainer(ResizableContainer parent) {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.parent = parent;
        this.ruler = new Ruler(parent);
        add(ruler);
    }

    @Override
    public VLayoutController getLayoutController() {
        return new VLayoutController() {
            @Override
            public void layoutComponent(JComponent c) {

                int w = parent.getWidth();
                int h = parent.getHeight();

                int x = w - getRuler().getWidth();
                int y = h - getRuler().getHeight();

                c.setLocation(x, y);
            }
        };
    }

    @Override
    public void setBoundsLocked(boolean value) {
        // we ignore this
    }

    @Override
    public boolean isIngoredByLayout() {
        // we ignore this
        return false;
    }

    /**
     * @return the ruler
     */
    public Ruler getRuler() {
        return ruler;
    }
}
