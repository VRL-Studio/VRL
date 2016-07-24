/* 
 * RangeCheckProvider.java
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

package eu.mihosoft.vrl.types;

import java.util.HashMap;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class RangeCheckProvider {

    private static final HashMap<Class<?>, RangeCheckBase<?>> supported;

    static {
        supported = new HashMap<Class<?>, RangeCheckBase<?>>();

        RangeCheckBase<?> rC = null;

        rC =
                new RangeCheckBase<Byte>(Byte.MIN_VALUE,
                Byte.MAX_VALUE, Byte.class);

        supported.put(Byte.class, rC);

        rC =
                new RangeCheckBase<Short>(Short.MIN_VALUE,
                Short.MAX_VALUE, Short.class);

        supported.put(Short.class, rC);


        rC =
                new RangeCheckBase<Integer>(Integer.MIN_VALUE,
                Integer.MAX_VALUE, Integer.class);

        supported.put(Integer.class, rC);

        rC =
                new RangeCheckBase<Long>(Long.MIN_VALUE,
                Long.MAX_VALUE, Long.class);

        supported.put(Long.class, rC);

        rC =
                new RangeCheckBase<Float>(-Float.MAX_VALUE,
                Float.MAX_VALUE, Float.class);

        supported.put(Float.class, rC);

        rC =
                new RangeCheckBase<Double>(-Double.MAX_VALUE,
                Double.MAX_VALUE, Double.class);

        supported.put(Double.class, rC);
    }

    public static RangeCheck<?> newInstance(Class<?> type) {
        RangeCheckBase<?> prototype = supported.get(type);

        if (prototype ==null) {
            throw new IllegalArgumentException(
                    ">> class \"" + type + "\" is not supported!");
        }

        return prototype.copy();
    }
}
