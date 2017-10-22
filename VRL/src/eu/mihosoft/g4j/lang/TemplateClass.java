/* 
 * TemplateClass.java
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

package eu.mihosoft.g4j.lang;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */


public class TemplateClass {
    private String name;
    private String templateArguments;
    private String clsHeader;

    public TemplateClass(
            String name, String templateArguments, String clsHeader) {
        this.name = name;
        this.templateArguments = templateArguments;
        this.clsHeader = clsHeader;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the templateArguments
     */
    public String getTemplateArguments() {
        return templateArguments;
    }

    /**
     * @return the code
     */
    public String getClsHeader() {
        return clsHeader;
    }


    @Override
    public String toString() {
        return "Name: " + getName() + ", TemplateArguments: " + getTemplateArguments();
    }

    @Override
    public boolean equals(Object obj) {
        
        // null or instance of incompatible class
        if (!(obj instanceof TemplateClass)) {
            return false;
        }
        
        TemplateClass other  = (TemplateClass) obj;
        
        if (!getName().equals(other.getName())) {
            return false;
        }
        
        if (!getTemplateArguments().equals(other.getTemplateArguments())) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 23 * hash + (this.templateArguments != null ? this.templateArguments.hashCode() : 0);
//        hash = 23 * hash + (this.clsHeader != null ? this.clsHeader.hashCode() : 0);
        return hash;
    }
    
    
}
