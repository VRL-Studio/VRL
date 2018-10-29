/* 
 * Clipboard.java
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

import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;

/**
 * Clipboard for Canvas objects.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Clipboard extends ArrayList<Selectable> {

    private static final long serialVersionUID = 888162547224909894L;

    /**
     * Selcts an item.
     * @param s the item that is to be selected
     */
    public void select(Selectable s) {
        if (!this.contains(s)) {
            s.setSelected(true);

            // check if s accepted selection
            if (s.isSelected()) {
                this.add(s);
            }
        }
    }

    /**
     * Unselects an item.
     * @param s the item that is to be unselected
     */
    public void unselect(Selectable s) {
        if (this.contains(s)) {
            s.setSelected(false);
            this.remove(s);
        }
    }

    /**
     * Indicates whether all items in the clipboard are instances of a
     * given class.
     * @param clazz the class object
     * @return <code>true</code> if all items are instances of the given class;
     *         <code>false</code> otherwise
     */
    public boolean containsOnlyItemsOfClass(Class clazz) {
        boolean result = !this.isEmpty();

        for (Selectable s : this) {
            if (!s.getClass().equals(clazz)) {
                result = false;
                break;
            }
        }

        return result;
    }

    /**
     * Indicates whether all items in the clipboard are instances of a
     * class that inherits a given super class.
     * @param clazz the class object
     * @return <code>true</code> if all items are instances of the given 
     *         super class; <code>false</code> otherwise
     */
    public boolean containsOnlyItemsWithSuperClass(Class clazz) {
        boolean result = !this.isEmpty();


        for (Selectable s : this) {
            if (!clazz.isInstance(s)) {
                result = false;
                break;
            }
        }

        return result;
    }

    /**
     * Indicates whether at least one item in the clipboard is an instance of a
     * class that inherits a given super class.
     * @param clazz the class object
     * @return <code>true</code> if at least one item is instance of the given
     *         super class; <code>false</code> otherwise
     */
    public boolean containsItemsWithSuperClass(Class clazz) {
        boolean result = !this.isEmpty();

        for (Selectable s : this) {
            if (clazz.isInstance(s)) {
                result = true;
                break;
            }
        }

        return result;
    }

    /**
     * Indicates whether at least one item in the clipboard is an instance of a
     * given class.
     * @param clazz the class object
     * @return <code>true</code> if at least one item in the clipboard is an
     *         instance of the given class; <code>false</code> otherwise
     */
    public boolean containsItemsOfClass(Class clazz) {
        boolean result = !this.isEmpty();

        for (Selectable s : this) {
            if (s.getClass().equals(clazz)) {
                result = true;
                break;
            }
        }

        return result;
    }

    /**
     * Returns an array that ontains all class objects of the instances in the
     * clipboard.
     * @return a list that contains all class objects of the instances in the
     * clipboard.
     */
    public ArrayList<Class> getClassObjects() {
        ArrayList<Class> result = new ArrayList<Class>();

        for (Selectable s : this) {
            if (!result.contains(s.getClass())) {
                result.add(s.getClass());
            }
        }

        return result;
    }
}
