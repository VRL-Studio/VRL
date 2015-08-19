/* 
 * ControlFlow.java
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

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface ControlFlow {

    public Invocation createInstance(String id, IType type, Argument... args);

//    public Invocation callMethod(String id, String varName, String mName, IType returnType, Argument... args);
//    
//    public Invocation callStaticMethod(String id, IType type, String mName, IType returnType, Argument... args);
//    
//    public Invocation callMethod(String id, String varName, MethodDeclaration mDec, Argument... args);
    
    public Invocation callMethod(String id, ObjectProvider objProvider, String mName, IType returnType, Argument... args);
    public Invocation callMethod(String id, ObjectProvider objProvider, MethodDeclaration mDec, Argument... args);
    
    public BinaryOperatorInvocation assignConstant(String id, String varName, Argument arg);
    public BinaryOperatorInvocation assignVariable(String id, String varName, Argument arg);
    public BinaryOperatorInvocation assignInvocationResult(String id, String varName, Invocation invocation);
    
    public DeclarationInvocation declareVariable(String id, IType type, String varName);

    public ScopeInvocation callScope(Scope scope);

    public List<Invocation> getInvocations();
    
    public boolean isUsedAsInput(Invocation invocation);
    
    public Optional<Invocation> returnInvTargetIfPresent(Invocation invocation);

    public BinaryOperatorInvocation invokeOperator(String id, Argument leftArg, Argument rightArg, Operator operator);
    
    // TODO 02.06.2014 switch to ControlFlowScope
    public Scope getParent();

    public ReturnStatementInvocation returnValue(String id, Argument arg);

    public BreakInvocation invokeBreak(String id);

    public ContinueInvocation invokeContinue(String id);

    public NotInvocation invokeNot(String id, Argument arg);



    
}

