/* 
 * TypeRepresentationFactory.java
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

package eu.mihosoft.vrl.reflection;

import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.visual.Canvas;
import java.util.Collection;



/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface TypeRepresentationFactory {

    /**
     * Adds a new type representation to the factory. If the factory already
     * contains a completely equal type representation this one will be
     * replaced. In this context equal means if their class object names and
     * their styles are the same.
     * @param t an instance of the type representation that is to be added
     */
    void addType(Class<? extends TypeRepresentationBase> tClass);

    /**
     * Dispose additional resources, e.g., Java 3D render threads.
     */
    void dispose();

    /**
     * Returns a type representation that provides an input interface.
     * @param type      the class object of the parameter that is
     * to be visualized
     * @param paramInfo the parameter annotation that is to be used to customize
     * the type representation
     * @return If a type representations  with the requested properties exists
     * it will be returned; returns <code>null</code> otherwise.
     */
    TypeRepresentationBase getInputInstance(Class<?> type, ParamInfo paramInfo);

    Canvas getMainCanvas();

    /**
     * Returns a type representation that provides an output interface.
     * @param type the class object of the parameter that is to be visualized
     * @param methodInfo the method annotation that is to be used to customize
     * the type representation
     * @param outputInfoInfo the outputInfo annotation that is to be used to 
     * customize the type representation
     * @return If a type representations  with the requested properties exists
     * it will be returned; returns <code>null</code> otherwise.
     */
    TypeRepresentationBase getOutputInstance(
            Class<?> type, MethodInfo methodInfo, OutputInfo outputInfo);

    /**
     * Returns a list containing all supported type representations.
     * @return the supportedTypes a list containing all supported type
     * representations
     */
    Collection<Class<? extends TypeRepresentationBase>> getSupportedTypes();

    /**
     * Removes all type representations with specified class name.
     * @param className class name of the type representations that are to
     * be removed
     */
    boolean removeTypeByClassName(String className);

//    /**
//     * Removes all type representations with specified style name.
//     * @param style style name of the type representations that are to
//     * be removed
//     */
//    boolean removeTypeByStyle(String style);

    void setMainCanvas(Canvas mainCanvas);

    /**
     * Defines the list of supported type representations.
     * @param supportedTypes the type representations that are to be supported
     */
    void setSupportedTypes(Collection<Class<? extends TypeRepresentationBase>> supportedTypes);

}
