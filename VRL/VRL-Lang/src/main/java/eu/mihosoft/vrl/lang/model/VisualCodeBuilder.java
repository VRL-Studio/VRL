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

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface VisualCodeBuilder {

    /**
     * Declares a compilation unit with the specified name and package name.
     *
     * @param name name of the compilation unit (e.g. file name)
     * @param packageName package name
     * @return compilation unit
     */
    CompilationUnitDeclaration declareCompilationUnit(String name, String packageName);
    
    /**
     * Declares a class in the specified compilation unit.
     *
     * @param scope compilation unit that shall contain the class declaration.
     * @param type class name
     * @param modifiers modifiers
     * @param extendz class that shall be extended (caution: currently only one
     * class is supported)
     * @param implementz interfaces that shall be implemented
     * @return class declaration
     */
    ClassDeclaration declareClass(CompilationUnitDeclaration scope, IType type, IModifiers modifiers, IExtends extendz, IExtends implementz);

    /**
     * Declares a class in the specified compilation unit.
     *
     * @param scope compilation unit that shall contain the class declaration
     * @param type class name
     * @return class declaration
     */
    ClassDeclaration declareClass(CompilationUnitDeclaration scope, IType type);

    /**
     * Declares a method in the specified class.
     *
     * @param scope class declaration that shall contain the method declaration
     * @param modifiers method modifiers
     * @param returnType return type of the method
     * @param methodName method name
     * @param params method parameters
     * @return method celaration
     */
    MethodDeclaration declareMethod(ClassDeclaration scope, IModifiers modifiers, IType returnType, String methodName, IParameters params);

    /**
     * Declares a method in the specified class.
     *
     * @param scope class declaration that shall contain the method declaration
     * @param returnType return type of the method
     * @param methodName method name
     * @param params method parameters
     * @return method celaration
     */
    MethodDeclaration declareMethod(ClassDeclaration scope, IType returnType, String methodName, IParameters params);

    /**
     * Declares a method in the specified class.
     *
     * @param scope class declaration that shall contain the method declaration
     * @param returnType return type of the method
     * @param methodName method name
     * @return method celaration
     */
    MethodDeclaration declareMethod(ClassDeclaration scope, IType returnType, String methodName);

    /**
     * Assigns the specified argument to the given variable.
     *
     * @param scope parent scope that shall contain the assignment invocation
     * @param varName name of the variable
     * @param arg argument to assign
     * @return assignment invocation
     */
    BinaryOperatorInvocation assign(Scope scope, String varName, Argument arg);

    /**
     * Assigns a given constant to the specified variable.
     *
     * @param scope parent scope that shall contain the assignment invocation
     * @param varName name of the variable
     * @param constant constant to assign
     * @return assignment invocation
     */
    BinaryOperatorInvocation assignConstant(Scope scope, String varName, Object constant);

    /**
     * Assigns a given variable to the specified variable.
     *
     * @param scope parent scope that shall contain the assignment invocation
     * @param varNameDest name of the destination variable
     * @param varNameSrc variable to assign
     * @return assignment invocation
     */
    BinaryOperatorInvocation assignVariable(Scope scope, String varNameDest, String varNameSrc);

    /**
     * Creates an instance of the specified type.
     *
     * @param scope parent scope that shall contain the constructor invocation
     * @param type type to instantiate
     * @param args constructor arguments
     * @return constructor invocation
     */
    Invocation createInstance(Scope scope, IType type, Argument... args);

    /**
     * Declares a variable in the specified scope.
     *
     * @param scope parent scope that shall contain the declaration invocation.
     * @param type type of the variable
     * @param varName name of the variable
     * @return declaration invocation
     */
    DeclarationInvocation declareVariable(Scope scope, IType type, String varName);

    @Deprecated
    DeclareAndAssignInvocation declareAndAssignVariable(Scope scope, IType type, String varName, Argument assignmentArg);

    /**
     * Invokes a for-loop in the specified controlflow scope.
     *
     * @param scope parent scope that shall contain the loop invocation
     * @param varName name of the loop variable
     * @param from initial value
     * @param to max value
     * @param inc increment
     * @return for-loop invocation
     */
    SimpleForDeclaration invokeForLoop(ControlFlowScope scope, String varName, int from, int to, int inc);

    /**
     * Invokes a while-loop in the specified controlflow scope
     *
     * @param scope controlflow scope that shall contain the loop invocation
     * @param check loop-check (argument of type boolean)
     * @return while-loop invocation
     */
    WhileDeclaration invokeWhileLoop(ControlFlowScope scope, Argument check);

    /**
     * Invokes a break command in the specified controlflow scope.
     *
     * @param scope controlflow scope that shall contain the break invocation
     * @return break invocation
     */
    BreakInvocation invokeBreak(ControlFlowScope scope);

    /**
     * Invokes a continue command in the specifried controlflow scope.
     *
     * @param scope controlflow scope that shall contain the continue invocation
     * @return continue invocation
     */
    ContinueInvocation invokeContinue(ControlFlowScope scope);

    /**
     * Invokes a method in the specified controlflow scope.
     *
     * @param scope parent scope that shall contain the method invocation
     * @param objProvider object provider (method will be invoked on the
     * specified object)
     * @param mName name of the method that shall be invoked
     * @param returnType return type of the method that shall be invoked (return
     * type does not influence method selection)
     * @param args invocation aerguments
     * @return method invocation
     */
    Invocation invokeMethod(ControlFlowScope scope, ObjectProvider objProvider, String mName, IType returnType, Argument... args);

    /**
     * Invokes a method in the specified controlflow scope.
     *
     * @param scope parent scope that shall contain the method invocation
     * @param objProvider object provider (method will be invoked on the
     * specified object)
     * @param mDec declaration of the method that shall be invoked
     * @param args invocation aerguments
     * @return method invocation
     */
    Invocation invokeMethod(ControlFlowScope scope, ObjectProvider objProvider, MethodDeclaration mDec, Argument... args);

    /**
     * Invokes a return statement in the specified controlflow scope.
     *
     * @param scope controlflow scope that shall contain the return statement
     * invocation
     * @param arg value that shall be returned
     * @return return statement invocation
     */
    ReturnStatementInvocation returnValue(ControlFlowScope scope, Argument arg);

    @Deprecated
    BinaryOperatorInvocation assignInvocationResult(Scope scope, String varName, Invocation invocation);

    /**
     * Invokes an operator in the specified controlflow scope.
     *
     * @param scope controlflow scope that shall contain the operator invocation
     * @param leftArg left operator argument
     * @param rightArg right operator argument
     * @param operator operator that shall be invoked
     * @return operator invocation
     */
    BinaryOperatorInvocation invokeOperator(ControlFlowScope scope, Argument leftArg, Argument rightArg, Operator operator);

    /**
     * Invokes a not-operator in the specified controlflow scope.
     *
     * @param scope controlflow scope that shall contain the operator invocation
     * @param arg operator argument that shall be negated (boolean)
     * @return not-operator invocation
     */
    NotInvocation invokeNot(ControlFlowScope scope, Argument arg);

    /**
     * Invokes an if-statement in the specified controlflow scope
     *
     * @param scope controlflow scope that shall contain the if-statement
     * invocation
     * @param check check for the if-statement (boolean)
     * @return if-statement invocation
     */
    IfDeclaration invokeIf(ControlFlowScope scope, Argument check);

    /**
     * Invokes an elseif statement in the specified controlflow scope.
     * @param scope controlflow scope that shall contain the elseif statement
     * @param check check for the elseif-statement (boolean)
     * @return elseif-statement invocation
     */
    ElseIfDeclaration invokeElseIf(ControlFlowScope scope, Argument check);
}
