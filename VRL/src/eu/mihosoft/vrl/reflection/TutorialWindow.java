///*
// * TutorialWindow.java
// *
// * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
// *
// * Copyright (C) 2009 Michael Hoffer &lt;info@michaelhoffer.de&gt;
// *
// * Supported by the Goethe Center for Scientific Computing of Prof. Wittum
// * (http://gcsc.uni-frankfurt.de)
// *
// * This file is part of Visual Reflection Library (VRL).
// *
// * VRL is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License version 3
// * as published by the Free Software Foundation.
// *
// * VRL is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// *
// * Linking this library statically or dynamically with other modules is
// * making a combined work based on this library.  Thus, the terms and
// * conditions of the GNU General Public License cover the whole
// * combination.
// *
// * As a special exception, the copyright holders of this library give you
// * permission to link this library with independent modules to produce an
// * executable, regardless of the license terms of these independent
// * modules, and to copy and distribute the resulting executable under
// * terms of your choice, provided that you also meet, for each linked
// * independent module, the terms and conditions of the license of that
// * module.  An independent module is a module which is not derived from
// * or based on this library.  If you modify this library, you may extend
// * this exception to your version of the library, but you are not
// * obligated to do so.  If you do not wish to do so, delete this
// * exception statement from your version.
// */
//
//package eu.mihosoft.vrl.reflection;
//
//import eu.mihosoft.vrl.visual.EventTask;
//import eu.mihosoft.vrl.visual.VMouseEvent;
//import java.awt.Component;
//import java.awt.Container;
//import java.awt.KeyboardFocusManager;
//import java.awt.Point;
//import java.awt.event.FocusEvent;
//import java.awt.event.FocusListener;
//import java.awt.event.InputEvent;
//import java.awt.event.MouseEvent;
//import java.awt.event.WindowEvent;
//import java.awt.event.WindowFocusListener;
//import java.awt.event.WindowListener;
//import java.awt.event.WindowStateListener;
//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyVetoException;
//import java.beans.VetoableChangeListener;
//import java.util.ArrayList;
//import javax.swing.JFrame;
//
///**
// * A frame for tutorial sessions.
// * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
// */
//public class TutorialWindow extends JFrame {
//    private static final long serialVersionUID = 7962639621189219483L;
//
//    private TutorialSession session;
//
//    /**
//     * Constructor.
//     * @param name the session frame name
//     * @param session the session to show
//     */
//    TutorialWindow(String name, TutorialSession session) {
//        super(name);
//        this.toFront();
//
//        initialize();
//        this.session = session;
//    }
//
//    /**
//     * Returns the toplevel parent of a component.
//     * @param c the component
//     * @return the toplevel parent or <code>null</code> if the component
//     *         doesn't have a parent
//     */
//    public Component getTopLevelParent(Component c) {
//        // find out top level parent of mainCanvas
//        Container parent = c.getParent();
//
//        Point cPos = c.getLocation();
//
//        while (parent != null) {
//            parent = c.getParent();
//            if (parent != null) {
//                c = parent;
//                cPos.x += c.getX();
//                cPos.y += c.getY();
//            }
//        }
//
//        return c;
//    }
//
//    /**
//     * Initializes this frame.
//     */
//    private void initialize() {
//
//
//
//        // TODO when the loose focus event occures after a button press
//        //      but before a button release the button won't fire an
//        //      action event. What can we do about that?
//        // TODO check focus behavior on all plattforms.
//        KeyboardFocusManager.getCurrentKeyboardFocusManager().
//                addVetoableChangeListener(new VetoableChangeListener() {
//
//            @Override
//            public void vetoableChange(PropertyChangeEvent evt)
//                    throws PropertyVetoException {
//                Component oldComp = (Component) evt.getOldValue();
//                Component newComp = (Component) evt.getNewValue();
//
//                if ("focusOwner".equals(evt.getPropertyName())) {
//                    if (oldComp == null) {
//                        System.out.println(">> Gained focus: " +
//                                newComp.hashCode());
//
//                        Component parent = getTopLevelParent(newComp);
//
//                        boolean parentIsNotTutorialWindow =
//                                !(parent instanceof TutorialWindow);
//
//                        if (parentIsNotTutorialWindow && session.isPlaying()) {
//                            throw new PropertyVetoException("message", evt);
//                        }
//
//                    } else {
//                        System.out.println(">> Lost focus: " +
//                                oldComp.hashCode());
//                    }
//                } else if ("focusedWindow".equals(evt.getPropertyName())) {
//                    if (oldComp == null) {
//                        // the newComp window will gain the focus
//                    } else {
//                        // the oldComp window will lose the focus
//
////                        if (session.isPlaying() &&
////                                oldComp == TutorialWindow.this) {
//////                            System.out.println(">> Rejected focus change!");
//////                            w.toFront();
////                        }
//
//
//                    }
//                }
//            }
//        });
//    }
//}
