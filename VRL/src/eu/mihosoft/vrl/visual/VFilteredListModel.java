/* 
 * VFilteredListModel.java
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

import java.util.*;
import javax.swing.AbstractListModel;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VFilteredListModel extends AbstractListModel {

    private List<Object> elements = new ArrayList<Object>();
    private Set<Object> hidden = new HashSet<Object>();
    private List<Object> matched = new ArrayList<Object>();

    public void addElement(Object o) {
        elements.add(o);
    }

    public boolean removeElement(Object o) {
        return elements.remove(o);
    }

    @Override
    public Object getElementAt(int index) {
        return matched.get(index);
    }

    @Override
    public int getSize() {
        return matched.size();
    }

    private void updateFilterInfo() {
        hidden.clear();
    }

    /**
     * Sets the filter and updates the list structure.
     *
     * @param filter
     */
    public void setFilter(Iterable<VFilter> filter) {

        Collection<VFilter> priorityFilter = new ArrayList<VFilter>();
        Collection<VStringFilter> stringFilter = new ArrayList<VStringFilter>();

        for (VFilter f : filter) {
            if (f instanceof VStringFilter) {
                stringFilter.add((VStringFilter) f);
            } else {
                priorityFilter.add(f);
            }
        }

        // apply all filters that do not depend on user input
        for (Object e : elements) {

            updateFilterInfo();

            boolean matches = false;

            for (VFilter f : priorityFilter) {

                if (f.matches(e) && !f.hideWhenMatching()) {
                    matched.add(e);
                    matches = true;
                }

                if (f.matches(e) && f.hideWhenMatching()) {
                    matched.remove(e);
                    matches = false;
                    hidden.add(e);
                    break;
                }
            }

            // we remove the element if not matched or if hidden
            if (!matches) {
                matched.remove(e);
            }
        }


        // apply all filters that may hide elements and do depend on user input
        for (Object e : elements) {

            boolean matches = false;

            for (VFilter f : stringFilter) {

                // if already hidden no check necessary
                if (hidden.contains(e)) {
                    break;
                }

                if (f.matches(e) && f.hideWhenMatching()) {
                    matched.remove(e);
                    matches = true;
                    hidden.add(e);
                    break;
                }
            }

            // we remove the element if matched because that means it shall
            // be hidden
            if (matches) {
                matched.remove(e);
            }
        }

        // apply all filters that may show elements and do depend on user input
        for (Object e : elements) {

            boolean matches = false;

            for (VFilter f : stringFilter) {

                // if already hidden no check necessary
                // (hidden is stronger than show)
                if (hidden.contains(e)) {
                    break;
                }

                if (f.matches(e) && !f.hideWhenMatching()) {
                    matched.add(e);
                    matches = true;
                    break;
                }
            }

            // we remove the element if not matched
            if (!matches) {
                matched.remove(e);
            }
        }
    }
}
