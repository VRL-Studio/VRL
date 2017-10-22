/* 
 * annotations.java
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

package eu.mihosoft.vrl.doc;

/**
 * \page annotation_page GUI Customization Tutorial
 * \author Michael Hoffer (info@michaelhoffer.de)
 * \date 2009
 *
 * \section annotation_intro_sec Introduction
 *
 * <p>
 * Although the completely automatic visualization of Java objects is
 * sufficient for simple scenarios it is sometimes necessary to customize
 * the generated interface. A first use case for GUI customization is shown
 * in the \ref tr_page. For GUI customization several solutions exist. One
 * possibility would be to provide this information in a seperate style object
 * or file. But this seperates the information from the code this
 * information is applied to. Thus it is difficult to see the relation between
 * the code and its style information. Another argument against such a solution
 * is that some information must be specified twice as it is necessary to define
 * the link between style information and the code element it affects.
 * </p>
 *
 * <p>
 * Fortunately Java provides a technique to add additional information to the
 * code without changing its functionality: <b>annotations</b>
 * (see http://java.sun.com/docs/books/tutorial/java/javaOO/annotations.html).
 * VRL provides special annotations for methods and parameters. There it is
 * possible to specify a different name or in case of parameters modify there
 * visualization style. Other style information that affects the whole VRL
 * interface are stored inside of a special style object.
 * </p>
 * 
 * \section annotation_param_sec Parameter Customization
 * <p>
 * All examples that follow will use the <code>VRLExample</code> project of
 * \ref use_sec. In the following we will change the <code>SimpleClass</code>
 * of our project. Recall the source code:
 * \code
 * class SimpleClass {
 * 	public Integer add(Integer a, Integer b) {
 * 		return a + b;
 * 	}
 * }
 * \endcode
 * </p>
 * <p>
 * The <code>add()</code> method has two <code>Integer</code> parameters. VRL
 * also provides a slider representation for <code>Integer</code> values. We 
 * will try to change the input parameter representation and select the slider
 * representation:
 * \code
 * import eu.mihosoft.vrl.reflection.ParamInfo;
 *
 * class SimpleClass {
 * 	public Integer add(
 *          @ParamInfo(name="Value A", style="slider") Integer a,
 *          @ParamInfo(name="Value B", style="slider") Integer b) {
 * 		return a + b;
 * 	}
 * }
 * \endcode
 * </p>
 *
 * <p>
 * After dragging a <code>SimpleClass</code> object to the canvas we should
 * get the following result:
 * @image html custom_type_sample02.png
 * @image latex custom_type_sample02.eps "Customized Parameters" width=4cm
 * </p>
 *
 * <p>
 * In this example we have used two customizations, firstly the name
 * of the value and secondly the value style. For further information see
 * eu.mihosoft.vrl.reflection.ParamInfo.
 * </p>
 *
 * \section annotation_method_sec Method Customization
 *
 * <p>
 * In the following example we will customize the method representation:
 * \code
 * import eu.mihosoft.vrl.reflection.MethodInfo;
 *
 * class SimpleClass {
 *  @MethodInfo(name="custom name",interactive=false)
 * 	public Integer add(
 *          Integer a,
 *          Integer b) {
 * 		return a + b;
 * 	}
 * }
 * \endcode
 * </p>
 *
 * <p>
 * The resulting interface has a different method name and does not provide
 * an invoke button:
 * @image html custom_method_sample01.png
 * @image latex custom_method_sample01.eps "Customized Method" width=4cm
 * </p>
 *
 * <p>
 * Methods can be customized using the eu.mihosoft.vrl.reflection.MethodInfo
 * annotation.
 * </p>
 *
 * \section annotation_object_sec Object Customization
 *
 * <p>
 * Object customization currently supports changing the name of an object and
 * specifying whether inherited methods shall be visualized. The following
 * example changes the name of the object representation:
 * \code
 * import eu.mihosoft.vrl.reflection.ObjectInfo;
 *
 * @ObjectInfo(name="Custom Name")
 * class SimpleClass {
 * 	public Integer add(
 *          Integer a,
 *          Integer b) {
 * 		return a + b;
 * 	}
 * }
 * \endcode
 * </p>
 * The result:
 * <p>
 * @image html custom_object_sample01.png
 * @image latex custom_object_sample01.eps "Customized Object" width=3.5cm
 * </p>
 */


