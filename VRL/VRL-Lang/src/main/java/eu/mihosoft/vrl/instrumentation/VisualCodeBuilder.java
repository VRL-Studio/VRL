/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.instrumentation;

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
