/* 
 * VClassUtils.java
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

package eu.mihosoft.vrl.lang;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Utilitie methods allowing inheritance analysis.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VClassUtils {
    /**
     * Returns all common interfaces of the two given class objects.
     * @param cls1 first class object
     * @param cls2 second class object
     * @return all common interfaces of the two given class objects
     */
    public static Collection<Class<?>> commonInterfaces(
            Class<?> cls1, Class<?> cls2) {
        Collection<Class<?>> result = new ArrayList<Class<?>>();
        
        for (Class<?> i1 : cls1.getInterfaces()) {
            for (Class<?> i2 : cls2.getInterfaces()) {
                if (i1.equals(i2)) {
                    result.add(i2);
                }
            }
        }
        return result;
    }
    
    /**
     * Determines whether the two given class objects have common interfaces.
     * @param cls1 first class object
     * @param cls2 second class object
     * @return <code>true</code> if the two given class objects have common
     *         interfaces; <code>false</code> otherwise
     */
    public static boolean haveCommonInterfaces(Class<?> cls1, Class<?> cls2) {
        return !commonInterfaces(cls1, cls2).isEmpty();
    }
    
    
}
