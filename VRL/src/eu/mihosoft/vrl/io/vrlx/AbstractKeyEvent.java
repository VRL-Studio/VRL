/* 
 * AbstractKeyEvent.java
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

package eu.mihosoft.vrl.io.vrlx;

import eu.mihosoft.vrl.visual.VKeyEvent;
import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * An abstract key event, used for XML serialization.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class AbstractKeyEvent extends AbstractEvent{
//    private Component source;
//    private int ID;
//    private long when;
//    private int modifiers;
    private int keyCode;
    private char keyChar;

    /**
     * Constructor.
     */
    public AbstractKeyEvent(){

    }

    /**
     * Constructor.
     * @param k the key event
     */
    public AbstractKeyEvent(KeyEvent k){
//        setSource((Component) k.getSource());
        setID(k.getID());
        setWhen(k.getWhen());
        setModifiers(k.getModifiers());
        setKeyCode(k.getKeyCode());
        setKeyChar(k.getKeyChar());
    }

    @Override
    public InputEvent getEvent(Component source){
        return new VKeyEvent(source, getID(), getWhen(), getModifiers(),
                getKeyCode(), getKeyChar());
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
     * @return the key code
     */
    public int getKeyCode() {
        return keyCode;
    }

    /**
     * @param keyCode the key code to set
     */
    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    /**
     * @return the key char
     */
    public char getKeyChar() {
        return keyChar;
    }

    /**
     * @param keyChar the key char to set
     */
    public void setKeyChar(char keyChar) {
        this.keyChar = keyChar;
    }

}
