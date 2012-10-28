/* 
 * UserFunction.java
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

package eu.mihosoft.vrl.math;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.Serializable;


/**
 * A Function that can be evaluated ( e.g. with 3d plotter).
 * <p>
 * <b>Note:</b> Groovy is used to evaluate the expressions. Thus, expression 
 * evaluation is flexible. If performance is important another expression
 * evaluator should be used instead
 * (the java compiler api is an interesting alternative).
 * </p>
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@ComponentInfo(name = "User Function", category = "VRL/Math",
        description="Evaluates expressions (e.g. x*x+y*y)")
@ObjectInfo(name = "User Function")
public class UserFunction implements Serializable {

    private static final long serialVersionUID = 1L;

    public Function1D getFunction1D(
            @ParamInfo(name = "f(x) = ",
            options = "xVarName=\"x\"") GroovyFunction1D f) {


        // we return the user defined function
        return f;
    }
    
    public Function2D getFunction2D(
            @ParamInfo(name = "f(x,y) = ",
            options = "xVarName=\"x\";yVarName=\"y\"") GroovyFunction2D f) {


        // we return the user defined function
        return f;
    }
    
    public Function3D getFunction3D(
            @ParamInfo(name = "f(x,y,z) = ",
            options = "xVarName=\"x\";yVarName=\"y\";zVarName=\"z\"") GroovyFunction3D f) {


        // we return the user defined function
        return f;
    }
}
