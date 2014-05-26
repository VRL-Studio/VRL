/* 
 * VisualCodeBuilder_Impl.java
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

import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.IType;
import eu.mihosoft.vrl.lang.model.WhileDeclaration;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.IModifiers;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.IParameters;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.IExtends;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.ForDeclaration;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.IdGenerator;
import java.util.Stack;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VisualCodeBuilder_Impl implements VisualCodeBuilder {

//    private final Stack<String> variables = new Stack<>();
    private IdRequest idRequest = new IdRequest() {
        
        private IdGenerator generator = FlowFactory.newIdGenerator();

        @Override
        public String request() {
            return generator.newId();
        }
    };

//    String popVariable() {
//        return variables.pop();
//    }

    @Deprecated
    public Scope createScope(Scope parent, ScopeType type, String name, Object... args) {
        if (parent != null) {
            return parent.createScope(idRequest.request(), type, name, args);
        } else {
            return new ScopeImpl(idRequest.request(), null, type, name, args);
        }
    }
    
    
    @Override
    public CompilationUnitDeclaration declareCompilationUnit(String name, String packageName) {
//        IType type = new Type(name); // TODO validation
        
        return new CompilationUnitDeclaration_Impl(idRequest.request(), null, name, packageName);
        
//        return createScope(null, ScopeType.COMPILATION_UNIT, name, new Object[0]);
    }

    @Override
    public DeclarationInvocation declareVariable(Scope scope, IType type, String varName) {
        return scope.declareVariable(idRequest.request(),type, varName);
    }

    @Deprecated
    public Variable createVariable(Scope scope, IType type) {
        
        Variable result = scope.createVariable(type);

//        variables.push(result.getName());

        return result;
    }

    @Override
    public MethodDeclaration declareMethod(ClassDeclaration scope,
            IModifiers modifiers, IType returnType, String methodName, IParameters params) {
        return scope.declareMethod(idRequest.request(), modifiers, returnType, methodName, params);
    }

    @Override
    public ForDeclaration declareFor(Scope scope, String varName, int from, int to, int inc) {

        if (scope.getType() == ScopeType.CLASS || scope.getType() == ScopeType.INTERFACE) {
            throw new UnsupportedOperationException("Unsupported parent scope specified."
                    + " Class " + ScopeType.CLASS + " or " + ScopeType.INTERFACE
                    + " based implementations are not supported!");
        }

        ForDeclaration result = new ForDeclaration_Impl(
                idRequest.request(), scope, varName, from, to, inc);

        return result;
    }

    @Override
    public WhileDeclaration declareWhile(Scope scope, Invocation check) {
        
        if (scope.getType() == ScopeType.CLASS || scope.getType() == ScopeType.INTERFACE) {
            throw new UnsupportedOperationException("Unsupported parent scope specified."
                    + " Class " + ScopeType.CLASS + " or " + ScopeType.INTERFACE
                    + " based implementations are not supported!");
        }

        WhileDeclaration_Impl result = new WhileDeclaration_Impl(
                idRequest.request(), scope, check);

        scope.getControlFlow().callScope(result);

        return result;
    }

    @Override
    public void createInstance(Scope scope, IType type, String varName, IArgument... args) {

        String id = idRequest.request();

        scope.getControlFlow().createInstance(id, type, varName, args);

//        variables.push(varName);
    }
    

    @Override
    public Invocation invokeMethod(Scope scope, String varName, String mName, IType returnType, boolean isVoid, IArgument... args) {
        String id = idRequest.request();

        Invocation result = scope.getControlFlow().callMethod(id, varName, mName, returnType, isVoid, args);

        return result;
    }
    
    @Override
    public Invocation invokeStaticMethod(Scope scope, IType type, String mName, IType returnType, boolean isVoid, IArgument... args) {
        String id = idRequest.request();

        Invocation result = scope.getControlFlow().callStaticMethod(id, type, mName, returnType, isVoid, args);

        return result;
    }

    @Override
    public BinaryOperatorInvocation assignVariable(Scope scope, String varNameDest, String varNameSrc) {
        return scope.assignVariable(varNameDest, varNameSrc);
    }

    @Override
    public BinaryOperatorInvocation assignConstant(Scope scope, String varName, Object constant) {
        return scope.assignConstant(varName, constant);
    }
    
    @Override
    public BinaryOperatorInvocation assignInvocationResult(Scope scope, String varName, Invocation invocation) {
        return scope.assignInvocationResult(varName, invocation);
    }

    public void setIdRequest(IdRequest idRequest) {
        this.idRequest = idRequest;
    }

    @Override
    public ClassDeclaration declareClass(CompilationUnitDeclaration scope, IType type, IModifiers modifiers, IExtends extendz, IExtends implementz) {
        String id = idRequest.request();
        
        ClassDeclaration result = new ClassDeclaration_Impl(id, scope, type, modifiers, extendz, implementz);
        
        return result;
    }

    @Override
    public Invocation invokeMethod(Scope scope, String varName, MethodDeclaration mDec, IArgument... args) {
        String id = idRequest.request();

        Invocation result = scope.getControlFlow().callMethod(id, varName, mDec, args);
        
        return result;
    }

    @Override
    public BinaryOperatorInvocation invokeOperator(Scope scope, IArgument leftArg, IArgument rightArg, Operator operator) {
        String id = idRequest.request();
        
        BinaryOperatorInvocation result = scope.getControlFlow().invokeOperator(id, leftArg, rightArg, operator);
        
        return result;
    }



}
