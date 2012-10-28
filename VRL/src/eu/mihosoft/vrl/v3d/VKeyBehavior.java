/* 
 * VKeyBehavior.java
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

package eu.mihosoft.vrl.v3d;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;


/**
 * Abstract behavior class to simplify key event handling. To make use of it
 * it is necessary to extend this class and implement a custom version of
 * the <code>keyPressed()<\code> method.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public abstract class VKeyBehavior extends Behavior {

    private WakeupOnAWTEvent wakeupOne = null;
    private WakeupCriterion[] wakeupArray = new WakeupCriterion[1];
    private WakeupCondition wakeupCondition = null;

    /**
     * Constructor.
     */
    public VKeyBehavior() {

        wakeupOne = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
        wakeupArray[0] = wakeupOne;
        wakeupCondition = new WakeupOr(wakeupArray);
    }

    //Override Behavior's initialize method to set up wakeup criteria
    @Override
    public void initialize() {
        //Establish initial wakeup criteria
        wakeupOn(wakeupCondition);
    }

    //Override Behavior's stimulus method to handle the event.
    @Override
    public void processStimulus(Enumeration criteria) {
        WakeupOnAWTEvent ev;
        WakeupCriterion genericEvt;
        AWTEvent[] events;

        while (criteria.hasMoreElements()) {
            genericEvt = (WakeupCriterion) criteria.nextElement();

            if (genericEvt instanceof WakeupOnAWTEvent) {
                ev = (WakeupOnAWTEvent) genericEvt;
                events = ev.getAWTEvent();
                processAWTEvent(events);
            }
        }

        //Set wakeup criteria for next time
        wakeupOn(wakeupCondition);
    }

    //Process a keyboard event
    private void processAWTEvent(AWTEvent[] events) {
        for (int n = 0; n < events.length; n++) {
            if (events[n] instanceof KeyEvent) {
                KeyEvent eventKey = (KeyEvent) events[n];

                processKeyEvent(eventKey);

                if (eventKey.getID() == KeyEvent.KEY_PRESSED) {
                    keyPressed(eventKey);
                }
            }
        }
    }

    /**
     * This method is called once for each key pressed event.
     * @param e the key event
     */
    public abstract void keyPressed(KeyEvent e);

    /**
     * This method is called once for each key event.
     * @param e the key event
     */
    public abstract void processKeyEvent(KeyEvent e);
}
