/* 
 * ObjectIdentifier.java
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

package eu.mihosoft.vrl.reflection;

import java.io.Serializable;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ObjectIdentifier implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer objectID;
    private Integer visualID;

    public ObjectIdentifier() {
    }

    public ObjectIdentifier(DefaultObjectRepresentation oRep) {
        this.objectID = oRep.getObjectID();
        this.visualID = oRep.getID();
    }

    /**
     * @return the objectID
     */
    public Integer getObjectID() {
        return objectID;
    }

    /**
     * @param objectID the objectID to set
     */
    public void setObjectID(Integer objectID) {
        this.objectID = objectID;
    }

    /**
     * @return the visualID
     */
    public Integer getVisualID() {
        return visualID;
    }

    /**
     * @param visualID the visualID to set
     */
    public void setVisualID(Integer visualID) {
        this.visualID = visualID;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof ObjectIdentifier)) {
            return false;
        }

        ObjectIdentifier otherID = (ObjectIdentifier) o;

        return getVisualID().equals(otherID.getVisualID())
                && getObjectID().equals(otherID.getObjectID());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.objectID != null ? this.objectID.hashCode() : 0);
        hash = 83 * hash + (this.visualID != null ? this.visualID.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return "ObjectIdentifier=[objID: " + getObjectID() + 
                ", visualID: " + getVisualID() + "]";
    }
}
