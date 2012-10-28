/* 
 * MethodInfo.java
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
 * Use this annotation to customize method representations. The \ref
 * annotation_page shows how to use this annotation.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodInfo {

    /**
     * Defines the name of the method.
     */
    String name() default "";

    /**
     * Defines whether the method can be directly invoked by the user or not.
     * This will affect the invoke button. If the method is defined to be not
     * interactive. No invoke button will be displayed. In this case it is only
     * possible to call this method implicitly.
     */
    boolean interactive() default true;

    /**
     * Defines whether this method is to be hidden. If so it is accessible via
     * combobox of the object representation.
     */
    boolean hide() default true;

    /**
     * Defines whether this method is to be ignored by the object inspector
     * (like private and protected methods).
     */
    boolean ignore() default false;

    /**
     * Defines whether a GUI representation of this method is to be created.
     * This is like ignore(), except from being executable by the obpject
     * inspector (necessary for
     * <code>auto-invoke</code> and
     * <code>assign-to-canvas</code>).
     */
    boolean noGUI() default false;

    /**
     * Defines the name of the return value.
     */
    String valueName() default "";

    /**
     * Defines the preferred style for the method.
     */
    String valueStyle() default "default";

    /**
     * <p> Defines whether to show this method in object visualizations of
     * subclass instances </b> <p> <b>Note:</b> this even works if
     * <code>noGUI=true</code> is set. In this case the method will be
     * evalaluated by the object inspector but not visualized (callOptions are
     * evaluated too). </p>
     */
    boolean inheritGUI() default true;

    /**
     * <p> Defines special call options that are evaluated by the object
     * inspector. It is possible to request automatic method calling etc. </p>
     * <p> <b>Supported options</b> <ul> <li>assign-canvas (the method must take
     * exactly one argument of type eu.mihosoft.vrl.reflection.VisualCanvas)
     * </li> <li>autoinvoke (only method without parameters are supported) </li>
     * </ul> </p> <p> <b>Example method info:</b>
     * <code>&#64;MethodInfo(callOptions="assign-canvas")</code> </p>
     */
    String callOptions() default "";

    /**
     * Defines the value options for the return value. For option examples see
     * {@link ParamInfo#options() }.
     */
    String valueOptions() default "";

    /**
     * Defines the thext of the invoke button.
     */
    String buttonText() default "invoke";

    /**
     * Defines whether this method shall be an initializer method. Always
     * invoked as first method inside of the controlflow, only one initializer
     * method per object visualization is possible.
     */
    boolean initializer() default false;

    /**
     * <p> Specifies a custom return type name used for tooltip info. If an
     * empty string is specified, the class name will be used (default) </p>
     */
    String valueTypeName() default "";

    /**
     * Defines whether to ask the user before closing this method via close
     * icon.
     */
    boolean askIfClose() default false;

    /**
     * Defines whether to hide the close icon to hide the method. If
     * <code>true</code> this method won't be hidden (see {@link #hide() }).
     */
    boolean hideCloseIcon() default false;

    /**
     * If not empty or
     * <code>null</code> this property defines the method as property method of
     * another method (parent). That is, the output of this method will be shown
     * inside the parent method.<p><b>Note:</b> A method can only be a property
     * of a non-property method and it must not take input parameters or rely on
     * special call-options.</p>
     */
    String propertyOf() default "";
    
    String parentName() default "";
}
