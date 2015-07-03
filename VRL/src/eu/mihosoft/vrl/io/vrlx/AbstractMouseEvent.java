/* 
 * AbstractMouseEvent.java
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

package eu.mihosoft.vrl.io.vrlx;

import eu.mihosoft.vrl.visual.VMouseEvent;
import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/**
 * An abstract mouse event, used for XML serialization
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class AbstractMouseEvent extends AbstractEvent {

//    private Component source;
//    private int ID;
//    private long when;
//    private int modifiers;
    private int x;
    private int y;
    private int clickCount;
    private boolean popupTrigger;

    /**
     * Constructor.
     */
    public AbstractMouseEvent() {
    }

    /**
     * Constructor.
     * @param m the mouse event
     */
    public AbstractMouseEvent(MouseEvent m) {
//        setSource((Component) m.getSource());
        setID(m.getID());
        setWhen(m.getWhen());
        setModifiers(m.getModifiers());
        setX(m.getX());
        setY(m.getY());
        setClickCount(m.getClickCount());
        setPopupTrigger(m.isPopupTrigger());

//        if (m instanceof VMouseEvent){
//            VMouseEvent vm = (VMouseEvent) m;
//            dockAppletParent = vm.isDockAppletParent();
//        }
    }

//    public InputEvent getEvent(Component source) {
//        VMouseEvent vm = new VMouseEvent(source, getID(), getWhen(),
//                getModifiers(), getX(), getY(),
//                getClickCount(), isPopupTrigger());
////        vm.setDockAppletParent(dockAppletParent);
//        return vm;
//    }

    @Override
    public InputEvent getEvent(Component source) {
        return new VMouseEvent(source, getID(), getWhen(),
                getModifiers(), getX(), getY(),
                getClickCount(), isPopupTrigger());
    }

    /**
     * @return the source
     */
//    public Component getSource() {
//        return source;
//    }
    /**
     * @param source the source to set
     */
//    public void setSource(Component source) {
//        this.source = source;
//    }

//    /**
//     * @return the ID
//     */
//    public int getID() {
//        return ID;
//    }
//
//    /**
//     * @param ID the ID to set
//     */
//    public void setID(int ID) {
//        this.ID = ID;
//    }
//
//    /**
//     * @return the when
//     */
//    public long getWhen() {
//        return when;
//    }
//
//    /**
//     * @param when the when to set
//     */
//    public void setWhen(long when) {
//        this.when = when;
//    }
//
//    /**
//     * @return the modifiers
//     */
//    public int getModifiers() {
//        return modifiers;
//    }
//
//    /**
//     * @param modifiers the modifiers to set
//     */
//    public void setModifiers(int modifiers) {
//        this.modifiers = modifiers;
//    }

    /**
     * @return the x position
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x position to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the y position
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y position to set
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * @return the number of clicks
     */
    public int getClickCount() {
        return clickCount;
    }

    /**
     * @param clickCount the number of clicks to set
     */
    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    /**
     * @return the popup trigger state
     */
    public boolean isPopupTrigger() {
        return popupTrigger;
    }

    /**
     * @param popupTrigger the popup trigger state to set
     */
    public void setPopupTrigger(boolean popupTrigger) {
        this.popupTrigger = popupTrigger;
    }
}
