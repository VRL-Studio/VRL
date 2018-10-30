/* 
 * VSwingUtil.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007–2018 by Michael Hoffer,
 * Copyright (c) 2015–2018 G-CSC, Uni Frankfurt,
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn)
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

import eu.mihosoft.vrl.system.VSysUtil;
import foxtrot.ConcurrentWorker;
import foxtrot.Worker;
import foxtrot.Task;
import foxtrot.WorkerThread;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.*;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultEditorKit;
import org.omg.CORBA.INTERNAL;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VSwingUtil {

    // singleton instance
//    private static final WaitController waitController =
//            new WaitControllerImpl();
    private static final AWTEventFilter eventFilter
            = new AWTEventFilter();
    private static final AWTShortCutListener shortCutListener
            = new AWTShortCutListener();
    private static WindowListener[] windowListeners;
    private static Window window;
    private static ArrayList<ActionListener> enableDisableAWTEventListeners
            = new ArrayList<ActionListener>();
    public static final String EVENT_FILTER_ENABLED_ACTION_CMD = "eventFilter.enable";
    public static final String EVENT_FILTER_DISABLED_ACTION_CMD = "eventFilter.disable";
    /**
     * Transparent Color (0,0,0,0)
     */
    public static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);
    private static boolean DEBUG = false;

    static {
        Toolkit.getDefaultToolkit().addAWTEventListener(eventFilter,
                AWTEvent.MOUSE_EVENT_MASK);
        Toolkit.getDefaultToolkit().addAWTEventListener(eventFilter,
                AWTEvent.MOUSE_MOTION_EVENT_MASK);
        Toolkit.getDefaultToolkit().addAWTEventListener(eventFilter,
                AWTEvent.MOUSE_WHEEL_EVENT_MASK);
        Toolkit.getDefaultToolkit().addAWTEventListener(eventFilter,
                AWTEvent.KEY_EVENT_MASK);

        Toolkit.getDefaultToolkit().addAWTEventListener(eventFilter,
                AWTEvent.WINDOW_EVENT_MASK);

        Toolkit.getDefaultToolkit().addAWTEventListener(shortCutListener,
                AWTEvent.KEY_EVENT_MASK);
    }

    /**
     * @return the DEBUG
     */
    public static boolean isDebug() {
        return DEBUG;
    }

    /**
     * @param aDEBUG the DEBUG to set
     */
    public static void setDebug(boolean v) {
        DEBUG = v;
    }

    // no instanciation allowed
    private VSwingUtil() {
        throw new AssertionError(); // not in this class either!
    }

    /**
     * Returns the parent of a component that is instance of a given class
     * object or one of its subclasses. This method iterates through all parents
     * until it either found a parent that meets the condition or it reached the
     * topmost parent of the component.
     *
     * @param c the component
     * @param cl the class object
     * @return the parent of the component that is instance of a given class
     * object or <code>null</code> if no such parent exists
     */
    public static Container getParent(Component c, Class<?> cl) {

        Container parent = c.getParent();

        while (parent != null) {
            parent = c.getParent();
            if (parent != null) {
                c = parent;
            }
            if (cl.isAssignableFrom(c.getClass())) {
                break;
            }
        }

        // if we iterated through all parents (reached the toplevel parent) and
        // didn't find an object of class cl we return null instead of the
        // toplevel parent
        if (parent == null) {
            c = null;
        }
        return (Container) c;
    }

    /**
     * Returns a red line border to simplify layout debugging.
     *
     * @return a red line border
     */
    public static Border createDebugBorder() {
        return BorderFactory.createLineBorder(Color.RED);
    }

    /**
     * Adds the specified container to the current event filter.
     */
    public static void addContainerToEventFilter(Container c) {
        if (eventFilter != null) {
            eventFilter.addContainer(c);
        }
    }

    /**
     * Removes the specified container from the current event filter.
     */
    public static void removeContainerFromEventFilter(Container c) {
        if (eventFilter != null) {
            eventFilter.removeContainer(c);
        }
    }

    /**
     * Indicates whether the specified component is a child of the given
     * container or one of its subcontainers.
     *
     * @param c component
     * @param p container
     * @return <code>true</code> if the specified component is a child of the
     * given container; <code>false</code> otherwise
     */
    public static boolean isChildOf(Component c, Container p) {

        Container parent = c.getParent();

        while (parent != null) {
            parent = c.getParent();
            if (parent != null) {
                c = parent;
            }
            if (p == parent) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns all parents of a component that are instance of a given class
     * object. This method iterates through all parents until it reached the
     * topmost parent of the component.
     *
     * @param c the component
     * @param classes the class objects
     * @return the parent of the component that is instance of a given class
     * object or <code>null</code> if no such parent exists
     */
    public static ArrayList<Container> getAllParents(
            Component c, Class<?>... classes) {

        ArrayList<Container> result = new ArrayList<Container>();

        Container parent = c.getParent();

        while (parent != null) {
            parent = c.getParent();
            if (parent != null) {
                c = parent;
            }
            for (Class<?> cl : classes) {
                if (cl.isAssignableFrom(c.getClass())) {
                    result.add((Container) c);
                }
            }

        }

        return result;
    }

    /**
     * Indicates whether the specified component is visible. This is different
     * from {@link Component#isVisible() } as it not only checks this property
     * but also considers visibility of the parent components in the component
     * hierarchy.
     *
     * @param c component to check
     * @return <code>true</code> if the specified omponent is visible;
     * <code>false</code> otherwise
     */
    public static boolean isVisible(Component c) {

        // first check wehther the component itself is visible and return
        // if this is not the case (parents have no influence on visibility)
        if (!c.isVisible()) {
            return false;
        }

        // c is visible and the visibility of 
        ArrayList<Container> allParents = getAllParents(c, Container.class);

        for (Container container : allParents) {
            if (!container.isVisible()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the topmost parent of a component.
     *
     * @param c the component
     * @return the topmost parent of the component or the component itself if no
     * parent exists
     */
    public static Window getTopmostParent(Component c) {

        Container parent = c.getParent();

        while (parent != null) {
            parent = c.getParent();
            if (parent != null) {
                c = parent;
            }
        }

        if (c == null) {
            return null;
        }

        return (Window) c;
    }

    /**
     * Indicates whether the specified component is child of a window.
     *
     * @param c component
     * @return <code>true</code> if the specified component is child of a
     * window; <code>false</code> otherwise
     */
    public static boolean isWindowChild(Component c) {
        try {
            Window w = getTopmostParent(c);
            return true;
        } catch (ClassCastException ex) {
        }

        return false;
    }

    /**
     * Returns a list of all child components that are instance of one of the
     * specified classes or subclasses of them. This Method searches until it
     * reached the specified layer or it found a child that meets the specified
     * condition. Possible subcomponents of it have to be searched seperately.
     *
     * @param c the container to search in
     * @param layer the number of layers to search (if <code>null</code> is
     * specified all layers will be searched)
     * @param classes... the classes
     * @return a list of all child components that implement a specific
     * interface, are instance of a specific class or subclass
     */
    public static ArrayList<Component> getAllChildren(
            Container c, Integer layer, Class... classes) {
        ArrayList<Component> result = new ArrayList<Component>();

        for (Component child : c.getComponents()) {

            if (child == null) {
                continue;
            }

            boolean found = false;
            for (Class<?> cl : classes) {
                if (cl.isAssignableFrom(child.getClass())) {
                    found = true;
                    break;
                }
            }
            if (found) {
                result.add(child);
            } else if ((child instanceof Container)
                    && ((layer == null) || layer > 0)) {

                Container container = (Container) child;

                if (layer != null) {
                    layer--;
                }

                ArrayList<Component> components
                        = getAllChildren(container, layer, classes);

                result.addAll(components);
            }
        }

        return result;
    }

    /**
     * Returns a list of all child components that are instance of one of the
     * specified classes or subclasses of them. Search is done recursively.
     * However, if one component meets the specified condition no further search
     * is done. Possible subcomponents of it have to be searched seperately.
     *
     * @param c the container to search in
     * @param classes... the class
     * @return a list of all child components that are instance of a specific
     * class or subclass
     */
    public static ArrayList<Component> getAllChildren(
            Container c, Class... classes) {
        return getAllChildren(c, null, classes);
    }

    public static Component getDeepestReceivingChildAt(
            Container parent, Point location) {
        for (Component c : parent.getComponents()) {

//            System.out.println("0-location= " + location
//                    + ", C: " + c.getClass().getCanonicalName()
//                    + ": x=" + c.getX() + ", y=" + c.getY()
//                    + ", w=" + c.getWidth() + ", h=" + c.getHeight());
            if (!EffectPane.class.isAssignableFrom(c.getClass())
                    && c.getBounds().contains(location)) {
//                System.out.println("1-location= " + location
//                        + ", C: " + c.getClass().getCanonicalName()
//                        + ": x=" + c.getX() + ", y=" + c.getY()
//                        + ", w=" + c.getWidth() + ", h=" + c.getHeight());
                if (c instanceof Container
                        && (c.getMouseListeners().length == 0
                        || c.getMouseMotionListeners().length == 0
                        || c.getMouseWheelListeners().length == 0)) {
                    Component tmpC = getDeepestReceivingChildAt(
                            (Container) c, SwingUtilities.convertPoint(
                                    parent, location, c));

                    if (tmpC != null) {
                        return tmpC;
                    } else {
                        return c;
                    }
                } else {
                    return c;
                }
            }
        }

        return parent;
    }

    /**
     * Returns position relative to main canvas's upper left corner.
     *
     * @param c the canvas child
     *
     * @return position relative to main canvas's upper left corner
     */
    public static Point getAbsPos(CanvasChild c) {

        Component p = null;

        if (c instanceof Component) {
            p = (Component) c;
        } else {
            return null;
        }

        Point location = p.getLocation();

        p = p.getParent();

        while (p != null && !(p instanceof Canvas)) {
            location.x += p.getLocation().x;
            location.y += p.getLocation().y;

            p = p.getParent();

            if (p == null) {
                return null;
            }
        }

        return location;
    }

    public static void repaintRequest(JComponent c) {
        RepaintManager.currentManager(c).addDirtyRegion(
                c, c.getX(), c.getY(), c.getWidth(), c.getHeight());
    }

    public static void repaintRequest(JComponent c,
            int x, int y, int w, int h) {
        RepaintManager.currentManager(c).addDirtyRegion(
                c, x, y, w, h);
    }

    public static void repaintRequestOnComponent(JComponent c,
            int x, int y, int w, int h) {
        c.repaint(x, y, w, h);
    }

    /**
     * Same as {@link SwingUtilities#invokeAndWait(java.lang.Runnable) } but
     * checks whether already running in EDT. In this case no
     * <code>invokeAndWait()</code> call will be performed, i.e., the {@link Runnable#run()
     * } method will be called directly.
     * <p>
     * <b>Note:</b> interrupted exceptions and {@link InvocationTargetException}
     * will not be thrown to ensure that Swing and the EDT cannot not crash in
     * case of unexpected interruption etc. Use {@link SwingUtilities#invokeAndWait(java.lang.Runnable)
     * }
     * directly if this is a problem.</p>
     *
     * @param r runnable
     */
    public static void invokeAndWait(final Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        r.run();
                    }
                });
            } catch (InterruptedException ex) {
                Logger.getLogger(VSwingUtil.class.getName()).
                        log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(VSwingUtil.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Same as {@link SwingUtilities#invokeLater(java.lang.Runnable) } but
     * checks whether already running in EDT. In this case no
     * <code>invokeLater()</code> call will be performed, i.e., the {@link Runnable#run()
     * } method will be called directly.
     *
     * @param r runnable
     */
    public static void invokeLater(final Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    r.run();
                }
            });
        }
    }

    /**
     * Returns all sub menu elements of the specified menu element.
     *
     * @param root a menu element, i.e., JMenu object
     * @return all sub menu elements of the specified menu element
     */
    public static Collection<MenuElement> getAllMenuSubElements(
            MenuElement root) {
        Collection<MenuElement> result = new ArrayList<MenuElement>();

        for (MenuElement e : root.getSubElements()) {
            result.add(e);
            result.addAll(getAllMenuSubElements(e));
        }

        return result;
    }

    public static void activateEventFilter(Canvas canvas,
            Container... containers) {

        notifyEnableDisableAWTEventListeners(true);

        if (window != null) {
            throw new IllegalStateException(
                    "Please deactivate previous filter first!");
        }

        synchronized (eventFilter) {
            try {
                window = (Window) canvas.getTopLevelParent();

                eventFilter.addContainers(containers);

                windowListeners = window.getWindowListeners();

                for (WindowListener l : window.getWindowListeners()) {
                    window.removeWindowListener(l);
                }
            } catch (Exception ex) {
                throw new IllegalStateException(
                        "Please attach the canvas to a window first!");
            }
        }
    }

    // does not work for os x menu bar
    public static void deactivateEventFilter() {

        notifyEnableDisableAWTEventListeners(false);

        if (window == null) {
            return;
        }

        synchronized (eventFilter) {

            eventFilter.removeAllContainers();

            for (WindowListener l : windowListeners) {
                window.addWindowListener(l);
            }

            window = null;
            windowListeners = null;
        }
    }

    private static void notifyEnableDisableAWTEventListeners(boolean enable) {

        String command = EVENT_FILTER_ENABLED_ACTION_CMD;

        if (!enable) {
            command = EVENT_FILTER_DISABLED_ACTION_CMD;
        }

        ActionEvent event = new ActionEvent(eventFilter, 0, command);
        for (ActionListener l : enableDisableAWTEventListeners) {
            l.actionPerformed(event);
        }
    }

    /**
     * @return the enableDisableAWTEventListeners
     */
    public static ArrayList<ActionListener> getEnableDisableAWTEventListeners() {
        return enableDisableAWTEventListeners;
    }

    public static void addEnableDisableAWTEventListener(ActionListener l) {
        getEnableDisableAWTEventListeners().add(l);
    }

    public static void removeEnableDisableAWTEventListener(ActionListener l) {
        getEnableDisableAWTEventListeners().remove(l);
    }

    /**
     * Waits until proceed requested by the specified wait controller.
     *
     * @return new Waitcontroller instance
     */
    public static WaitController newWaitController() {
        return new WaitControllerImpl();
    }

    /**
     *
     * @author Michael Hoffer <info@michaelhoffer.de>
     */
    private static final class WaitControllerImpl implements WaitController {

        private boolean waiting = false;

        public WaitControllerImpl() {
            //
        }

        @Override
        public synchronized void requestConcurrentWait(final ProceedRequest p) {

//            System.out.println("Wait: start = " 
//                    + Thread.currentThread().getId());
            invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    try {
                        ConcurrentWorker.post(new Task() {
                            @Override
                            public Object run() throws Exception {
                                if (waiting) {
                                    return null;
                                }
                                waiting = true;

                                while (!p.proceed()) {
                                    Thread.sleep(10);
                                }

                                waiting = false;

                                return null;
                            }
                        });

                    } catch (Exception ex) {
                        Logger.getLogger(VSwingUtil.class.getName()).
                                log(Level.SEVERE, null, ex);
                    }
                }
            });
//
//            System.out.println("Wait: stop = " 
//                    + Thread.currentThread().getId());
        }

        @Override
        public synchronized void requestWait(final ProceedRequest p) {

//            System.out.println("Wait: start = "
//                    + Thread.currentThread().getId());
            invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    try {
                        Worker.post(new Task() {
                            @Override
                            public Object run() throws Exception {
                                if (waiting) {
                                    return null;
                                }
                                waiting = true;

                                while (!p.proceed()) {
                                    Thread.sleep(50);
                                }

                                waiting = false;

                                return null;
                            }
                        });

                    } catch (Exception ex) {
                        Logger.getLogger(VSwingUtil.class.getName()).
                                log(Level.SEVERE, null, ex);
                    }
                }
            });

//            System.out.println("Wait: stop = " 
//                    + Thread.currentThread().getId());
        }
    }

    public static void registerShortCutAction(VShortCutAction a) {
        shortCutListener.addAction(a);
    }

    public static void unregisterShortCutAction(VShortCutAction a) {
        shortCutListener.removeAction(a);
    }

    public static Collection<VShortCutAction> getShortCutActions() {
        return shortCutListener.getActions();
    }

    public static void forceAppleLAF(Component c) {

        String lookAndFeel = "com.apple.laf.AquaLookAndFeel";

        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(VSwingUtil.class.getName()).
                    log(Level.SEVERE, null, ex);
//            foundLAF = false;
        } catch (InstantiationException ex) {
            Logger.getLogger(VSwingUtil.class.getName()).
                    log(Level.SEVERE, null, ex);
//            foundLAF = false;
        } catch (IllegalAccessException ex) {
            Logger.getLogger(VSwingUtil.class.getName()).
                    log(Level.SEVERE, null, ex);
//            foundLAF = false;
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(VSwingUtil.class.getName()).
                    log(Level.SEVERE, null, ex);
//            foundLAF = false;
        }
        if (c != null) {
            SwingUtilities.updateComponentTreeUI(c);
        }
    }

    public static void forceNimbusLAF(Component c) {
        // force nimbus style for bottom pane as osx style looks very bad
        // we have to get rid of theese crappy apple "enhancements"

        String lookAndFeel = "javax.swing.plaf.nimbus.NimbusLookAndFeel";

        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(VSwingUtil.class.getName()).
                    log(Level.SEVERE, null, ex);
//            foundLAF = false;
        } catch (InstantiationException ex) {
            Logger.getLogger(VSwingUtil.class.getName()).
                    log(Level.SEVERE, null, ex);
//            foundLAF = false;
        } catch (IllegalAccessException ex) {
            Logger.getLogger(VSwingUtil.class.getName()).
                    log(Level.SEVERE, null, ex);
//            foundLAF = false;
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(VSwingUtil.class.getName()).
                    log(Level.SEVERE, null, ex);
//            foundLAF = false;
        }

        if (c != null) {
            SwingUtilities.updateComponentTreeUI(c);
        }

        if (VSysUtil.isMacOSX()) {
            InputMap im = (InputMap) UIManager.get("TextField.focusInputMap");
            im.put(KeyStroke.getKeyStroke(
                    KeyEvent.VK_C, KeyEvent.META_DOWN_MASK),
                    DefaultEditorKit.copyAction);
            im.put(KeyStroke.getKeyStroke(
                    KeyEvent.VK_V, KeyEvent.META_DOWN_MASK),
                    DefaultEditorKit.pasteAction);
            im.put(KeyStroke.getKeyStroke(
                    KeyEvent.VK_X, KeyEvent.META_DOWN_MASK),
                    DefaultEditorKit.cutAction);
        }
    }

    /**
     * This method should be called directly when starting the application to
     * prevent a JDK bug. Calling this method on Java version != 7 does nothing.
     *
     * @see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6923200
     * @see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7075600
     * @see http://forums.xkcd.com/viewtopic.php?f=11&t=76841
     */
    public static void fixSwingBugsInJDK7() {
        String javaVersion = System.getProperty("java.version");

        if (javaVersion.startsWith("1.7")) {
            System.out.println(">> Running on JDK 7");
            System.out.println(" --> switching to legacy mergesort due to Swing/AWT bug in Java 7");
            System.out.println("     see: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6923200");
            System.out.println("     see: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7075600");
            System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        }
    }
} // end class VSwingUtil

/**
 * An awt event listener responsible for controlling selection gesture for
 * grouping type representations.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class AWTEventFilter implements AWTEventListener {

    private List<Container> containers = new ArrayList<Container>();
    private List<Container> containersToRemove = new ArrayList<Container>();
    public static AWTEventFilter previousInstance;

    /**
     * Constructor.
     *
     * @param mainCanvas
     */
    public AWTEventFilter() {

        // very important: always remove previous instance because there will
        // be multiple instances in the event queue
        Toolkit.getDefaultToolkit().removeAWTEventListener(previousInstance);

        // define this instance as the one that will be removed from the
        // event queue if another instance is created
        previousInstance = this;
    }

    @Override
    public void eventDispatched(AWTEvent event) {

        boolean noContainersToBeExcluded
                = containers == null || containers.isEmpty();

        if (noContainersToBeExcluded) {
            return;
        }

        if (!(event instanceof InputEvent)) {
            return;
        }

        if (event instanceof MouseWheelEvent) {
            return;
        }

        boolean isComponent = event.getSource() instanceof Component;

        // remove containers
        for (Container c : containersToRemove) {
            containers.remove(c);
        }

        containersToRemove.clear();

        Window parentContainer = null;

        if (isComponent) {
            parentContainer
                    = (Window) VSwingUtil.getParent(
                            (Component) event.getSource(), Window.class);
        }

        boolean parentNotFiltered = true;

        for (Container c : containers) {

            boolean eventFromExcludedContainer = c == event.getSource()
                    || (isComponent && VSwingUtil.isChildOf(
                            (Component) event.getSource(), c));

            if (eventFromExcludedContainer) {
                return;
            }

            Window parentOfC = null;

            try {
                parentOfC = VSwingUtil.getTopmostParent(c);
            } catch (Exception ex) {
                // might not work due to wrong class
                return;
            }

            if (parentContainer == parentOfC) {
                parentNotFiltered = false;
            }

        }

        if (parentNotFiltered) {
            return;
        }

        ((InputEvent) event).consume();
    }

    /**
     * @return the container
     */
    public Iterable<Container> getContainers() {
        return containers;
    }

    public void addContainer(Container c) {
        containers.add(c);
    }

    public void addContainers(Container... containers) {
        this.containers.addAll(Arrays.asList(containers));
    }

    public void removeContainer(Container c) {
        containersToRemove.add(c);
    }

    public void removeAllContainers() {
        containersToRemove.addAll(containers);
    }
}

/**
 * An awt event listener responsible for receiving global key events
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class AWTShortCutListener implements AWTEventListener {

    private Container container;
    public static AWTShortCutListener previousInstance;
    private Collection<VShortCutAction> actions
            = new ArrayList<VShortCutAction>();
    private Deque<VKey> keyBuffer = new ArrayDeque<VKey>();
    private int maxShortCutLength;

    /**
     * Constructor.
     *
     * @param mainCanvas
     */
    public AWTShortCutListener() {

        // very important: always remove previous instance because there will
        // be multiple instances in the event queue
        Toolkit.getDefaultToolkit().removeAWTEventListener(previousInstance);

        // define this instance as the one that will be removed from the
        // event queue if another instance is created
        previousInstance = this;
    }

    public void addAction(VShortCutAction action) {
        actions.add(action);

        maxShortCutLength = 0;

        for (VShortCutAction a : actions) {
            maxShortCutLength = Math.max(
                    maxShortCutLength, a.getShortCut().getKeys().length);
        }
    }

    public boolean removeAction(VShortCutAction action) {
        return actions.remove(this);
    }

    public Collection<VShortCutAction> getActions() {
        return actions;
    }

    @Override
    public void eventDispatched(AWTEvent event) {

        if (!(event instanceof KeyEvent)) {
            return;
        }

        if (VSwingUtil.isDebug()) {
            System.out.println("KeyEvent: " + event);
        }

        KeyEvent keyEvent = (KeyEvent) event;

        VKey vKey = new VKey(keyEvent);

        if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {

            while (keyBuffer.size() > 0
                    && keyBuffer.size() >= maxShortCutLength) {
                keyBuffer.removeFirst();
            }

            // removes possible key event clones
            // (may happen if ui thread cannot handle release event)
            if (keyBuffer.contains(vKey)) {
                keyBuffer.remove(vKey);
            }

            keyBuffer.addLast(vKey);
        }

        if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {

            VShortCut shortCut = new VShortCut("",
                    keyBuffer.toArray(new VKey[keyBuffer.size()]));

            for (VShortCutAction a : actions) {

                if (a.getShortCut().equals(shortCut)) {
                    a.performAction();
                }
            }

            keyBuffer.remove(vKey);
        }

        if (VSwingUtil.isDebug()) {
            System.out.println("Keys: ");

            for (VKey vK : keyBuffer) {
                System.out.println(" --> "
                        + KeyEvent.getKeyText(vK.getKeyCode()));
            }

            System.out.println("------");
        }
    }

    /**
     * @return the container
     */
    public Container getContainer() {
        return container;
    }

    /**
     * @param container the container to set
     */
    public void setContainer(Container container) {
        this.container = container;
    }
}
