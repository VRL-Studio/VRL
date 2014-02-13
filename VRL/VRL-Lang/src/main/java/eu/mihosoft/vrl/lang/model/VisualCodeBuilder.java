/* 
 * VisualCodeBuilder.java
 *
 * Copyright (c) 2009–2014 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2014 by Michael Hoffer
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
 * First, the following text must be displayed on the Canvas or an equivalent location:
 * "based on VRL source code".
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
 * Computing and Visualization in Science, in press.
 */

package eu.mihosoft.vrl.lang.model;

import eu.mihosoft.vrl.instrumentation.Type;
import eu.mihosoft.vrl.instrumentation.Variable;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.IType;
import eu.mihosoft.vrl.lang.model.WhileDeclaration;
import eu.mihosoft.vrl.lang.model.IModifiers;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.IParameters;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.IExtends;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.ForDeclaration;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface VisualCodeBuilder {
    
    CompilationUnitDeclaration declareCompilationUnit(String name, String packageName);

    void assignConstant(Scope scope, String varName, Object constant);

    void assignVariable(Scope scope, String varNameDest, String varNameSrc);

    void createInstance(Scope scope, IType type, String varName, Variable... args);

    Variable createVariable(Scope scope, IType type, String varName);

    ForDeclaration declareFor(Scope scope, String varName, int from, int to, int inc);
    
    ClassDeclaration declareClass(CompilationUnitDeclaration scope, IType type, IModifiers modifiers, IExtends extendz, IExtends implementz);

    MethodDeclaration declareMethod(ClassDeclaration scope, IModifiers modifiers, Type returnType, String methodName, IParameters params);

    WhileDeclaration declareWhile(Scope scope, Invocation check);

    Invocation invokeMethod(Scope scope, String varName, String mName, boolean isVoid, String retValName, Variable... args);
    
    Invocation invokeStaticMethod(Scope scope, IType type, String mName, boolean isVoid, String retValName, Variable... args);
}
