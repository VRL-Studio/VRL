/* 
 * VFileFilter.java
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
package eu.mihosoft.vrl.io;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.filechooser.*;

/**
 * File extension filter for files.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VFileFilter extends FileFilter implements Serializable, VExtensionFileFilter {

    private static final long serialVersionUID = 1L;
    private ArrayList<String> acceptedEndings = new ArrayList<>();
    private String description = "Files *.*";

    @Override
    public boolean accept(File f) {
        boolean endingAcccepted = false;

        if (f.isDirectory() || acceptedEndings.isEmpty()) {
            endingAcccepted = true;
        } else {
            for (String e : getAcceptedEndings()) {
                if (f.getName().toLowerCase().endsWith(e)) {
                    endingAcccepted = true;
                    break;
                }
            }
        }

        return endingAcccepted;
    }

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Defines the description for this file filter.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the endings accepted by this file filter.
     *
     * @return the accepted endings
     */
    public ArrayList<String> getAcceptedEndings() {
        return acceptedEndings;
    }

    /**
     * Returns the endings accepted by this file filter.
     *
     * @return the accepted endings
     */
    @Override
    public ArrayList<String> getExtensions() {
        return acceptedEndings;
    }

    /**
     * Defines the endings accepted by this file filter.
     *
     * @param acceptedEndings the endings to set
     */
    public void setAcceptedEndings(ArrayList<String> acceptedEndings) {
        this.acceptedEndings = acceptedEndings;
    }

    /**
     * Adds an accepted ending to this file filter, e.g., <code>".jpg"</code>.
     *
     * @param e the ending to add
     */
    public void addEnding(String e) {
        acceptedEndings.add(e);
    }
}
