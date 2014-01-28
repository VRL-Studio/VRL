/* 
 * OutputInfo.java
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

package eu.mihosoft.vrl.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to customize type representations. The \ref
 * annotation_page shows how to use this annotation.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 * @see MethodInfo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OutputInfo {

    /**
     * Defines the group name of the parameter.
     */
    //String group() default "default";
    /**
     * Defines the name of the parameter.
     */
    String name() default "";

    /**
     * Defines the preferred style for the parameter (cooses a type
     * representation)
     */
    String style() default "default";

    /**
     * <p> Defines the value options for the parameter. Value options are
     * defined as Groovy script. To define a mininum value of 1 and a maximum
     * value of 15 as used by {@link eu.mihosoft.vrl.types.IntSliderType} the
     * options have to be defined as follows:
     * <code>"min=1;max=15"</code>. </p> <p> <b>Example param info:</b>
     * <code>&#64;ParamInfo(style="slider", options="min=1;max=15")</code> </p>
     * <p> Note that each type representation may request its own option values.
     * The example shown above is known to work for
     * {@link eu.mihosoft.vrl.types.IntSliderType} and
     * {@link eu.mihosoft.vrl.types.NumberBasedTypeRepresentation} including its
     * subclasses. Consult the documentation of the corresponding type
     * representation for details. </p>
     */
    String options() default "";

    /**
     * <p> Specifies a custom type name used for tooltip info. If an empty
     * string is specified, the class name will be used (default). </p>
     */
    String typeName() default "";

    /**
     * Defines the name of the parameter.
     */
    String[] elemNames() default {};

    /**
     * Defines the preferred style for the parameter (cooses a type
     * representation)
     */
    String[] elemStyles() default {};

    /**
     * <p> Defines the value options for the parameter. Value options are
     * defined as Groovy script. To define a mininum value of 1 and a maximum
     * value of 15 as used by {@link eu.mihosoft.vrl.types.IntSliderType} the
     * options have to be defined as follows:
     * <code>"min=1;max=15"</code>. </p> <p> <b>Example param info:</b>
     * <code>&#64;ParamInfo(style="slider", options="min=1;max=15")</code> </p>
     * <p> Note that each type representation may request its own option values.
     * The example shown above is known to work for
     * {@link eu.mihosoft.vrl.types.IntSliderType} and
     * {@link eu.mihosoft.vrl.types.NumberBasedTypeRepresentation} including its
     * subclasses. Consult the documentation of the corresponding type
     * representation for details. </p>
     */
    String[] elemOptions() default {};

    /**
     * <p> Specifies a custom type name used for tooltip info. If an empty
     * string is specified, the class name will be used (default). </p>
     */
    String[] elemTypeNames() default {};
    
    /**
     * Specifies the element types (class objects).
     */
    Class<?>[] elemTypes() default {};
}
