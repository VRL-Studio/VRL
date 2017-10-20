/* 
 * EventTask.java
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

import eu.mihosoft.vrl.animation.AnimationTask;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class EventTask implements AnimationTask, CanvasChild {

    private Canvas mainCanvas;
    private InputEvent event;
    private Component previousFocusOwner;
    private Component currentFocusOwner;
    private InputEvent lastEventThatChangedFocusOwner;

    public EventTask(Canvas mainCanvas, InputEvent event) {
        setMainCanvas(mainCanvas);
        setEvent(event);

//        if (event instanceof KeyEvent) {
//            System.out.println("KeyEvent");
//        }
    }

    @Override
    public void firstFrameStarted() {
//        System.out.println("firstFrame");
        InputEvent ev = event;

        Component focusOwnerBeforeEvent =
                KeyboardFocusManager.getCurrentKeyboardFocusManager().
                getFocusOwner();

        if (ev instanceof MouseEvent) {
            MouseEvent m = (MouseEvent) ev;

            // find out top level parent of mainCanvas
            Component c = mainCanvas;
            Component parent = mainCanvas.getParent();

            Point cPos = c.getLocation();
            while (parent != null) {
                parent = c.getParent();
                if (parent != null) {
                    c = parent;
                    cPos.x += c.getX();
                    cPos.y += c.getY();
                }
            }

//            m.setSource(mainCanvas);

//            System.out.println("Pos: " + (m.getX() - cPos.x));

            VMouseEvent newEvent = new VMouseEvent(c, m.getID(), m.getWhen(),
                    m.getModifiers(), m.getX(), m.getY(),
                    m.getClickCount(), m.isPopupTrigger());


            Toolkit.getDefaultToolkit().getSystemEventQueue().
                    postEvent(newEvent);

        }

        if (ev instanceof KeyEvent) {
            KeyEvent k = (KeyEvent) ev;

            currentFocusOwner =
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().
                    getFocusOwner();

            if (getCurrentFocusOwner() != null) {

                VKeyEvent newEvent = new VKeyEvent(
                        getCurrentFocusOwner(), k.getID(),
                        System.currentTimeMillis(),
                        k.getModifiers(), k.getKeyCode(), k.getKeyChar());

                Toolkit.getDefaultToolkit().getSystemEventQueue().
                        postEvent(newEvent);

//                if (ev.getID() == KeyEvent.KEY_PRESSED) {
//                    System.out.println("KeyPressed");
//                }
//                if (ev.getID() == KeyEvent.KEY_RELEASED) {
//                    System.out.println("KeyReleased");
//                }
            } else {
//                    System.out.println("LOST FOCUS");
                }
        } else {
//            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(ev);
        }

        currentFocusOwner =
                KeyboardFocusManager.getCurrentKeyboardFocusManager().
                getFocusOwner();

        if (focusOwnerBeforeEvent!=null&&
                !focusOwnerBeforeEvent.equals(currentFocusOwner)) {
            getMainCanvas().getEventRecorder().
                    setLastEventChangedFocusOwner(true);
            setLastEventThatChangedFocusOwner(event);
            getMainCanvas().getEventRecorder().
                    setLastEventThatChangedFocusOwner(event);
        } else {
            getMainCanvas().getEventRecorder().
                    setLastEventChangedFocusOwner(false);
        }

        getMainCanvas().getEventRecorder().
                setLastEvent(event);

        if (getCurrentFocusOwner() != null) {
            previousFocusOwner = getCurrentFocusOwner();

            if (event instanceof MouseEvent) {
                MouseEvent m = (MouseEvent) event;
                if (m.getID() == MouseEvent.MOUSE_PRESSED) {
                }
            }
            getMainCanvas().getEventRecorder().setFocusOwner(currentFocusOwner);
        }
    }

    @Override
    public void lastFrameStarted() {
//        System.out.println("lastFrame");
    }

    @Override
    public void frameStarted(double time) {
        //
    }

    @Override
    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    @Override
    public void setMainCanvas(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;
    }

    /**
     * @return the event
     */
    public InputEvent getEvent() {
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(InputEvent event) {
        this.event = event;
    }

    /**
     * @return the previous focus owner
     */
    public Component getPreviousFocusOwner() {
        return previousFocusOwner;
    }

    /**
     * @return the current focus owner
     */
    public Component getCurrentFocusOwner() {
        return currentFocusOwner;
    }

    /**
     * @return the lastEventThatChangedFocusOwner
     */
    public InputEvent getLastEventThatChangedFocusOwner() {
        return lastEventThatChangedFocusOwner;
    }

    /**
     * @param lastEventThatChangedFocusOwner the lastEventThatChangedFocusOwner to set
     */
    public void setLastEventThatChangedFocusOwner(InputEvent lastEventThatChangedFocusOwner) {
        this.lastEventThatChangedFocusOwner = lastEventThatChangedFocusOwner;
    }
}
