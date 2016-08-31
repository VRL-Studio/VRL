/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff.actions;

import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.DeclarationInvocation;
import eu.mihosoft.vrl.lang.model.IExtends;
import eu.mihosoft.vrl.lang.model.IModifiers;
import eu.mihosoft.vrl.lang.model.IParameter;
import eu.mihosoft.vrl.lang.model.IParameters;
import eu.mihosoft.vrl.lang.model.IType;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.Variable;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public interface IModelCommands {

    public static IModelCommands getInstance() {
        return ModelCommands.INSTANCE;
    }

    void setScopeName(String ceName, Scope codeEntity);

    void removeScope(Scope parentScope, Scope scope);

    void removeScope(Scope scope);

    void insertScope(Scope parentScope, Scope scope);

    void insertScope(Scope parentScope, int pos, Scope scope);

    void insertVariableToScope(Scope scope, Variable variable);

    void removeVariableFromScope(Scope scope, Variable variable);

    //COMPILATION UNIT DECLARATION
    void removeClassFromCUD(CompilationUnitDeclaration cud, ClassDeclaration cls);

    void insertClassToCUD(CompilationUnitDeclaration cud, ClassDeclaration cls);

    void insertClassToCUD(CompilationUnitDeclaration cud, int pos, ClassDeclaration cls);

    void setCUDeclPackageName(String packageName, CompilationUnitDeclaration cud);

    void setCUDeclFileName(String name, CompilationUnitDeclaration cud);

    // CLASS
    ClassDeclaration removeMethodFromClass(ClassDeclaration cls, MethodDeclaration methodDeclaration);

    void insertMethodToClass(ClassDeclaration cls, MethodDeclaration methodDeclaration);

    void insertMethodToClass(ClassDeclaration cls, int pos, MethodDeclaration methodDeclaration);

    void setClassType(IType type, ClassDeclaration classDeclaration);

    void setClassModifiers(IModifiers modifiers, ClassDeclaration classDeclaration);

    void setClassImplements(IExtends implementz, ClassDeclaration classDeclaration);

    void setClassExtends(IExtends extendz, ClassDeclaration classDeclaration);

    // METHOD
    void removeVariableFromMethod(MethodDeclaration methodDeclaration, Variable variable);

    void insertVariableToMethod(MethodDeclaration methodDeclaration, Variable variable);

    void setMethodName(String methName, MethodDeclaration methodDeclaration);

    void setMethodModifiers(IModifiers modifiers, MethodDeclaration methodDeclaration);

    void setMethodParameters(IParameters parameters, MethodDeclaration methodDeclaration);

    void setMethodReturnType(IType type, MethodDeclaration methodDeclaration);

    // VARIABLE
    void setVariableName(String varName, Variable variable);

    void setVariableType(IType type, Variable variable);

    void setVariableValue(Object value, Variable variable);

    void setVariableConstant(Boolean constant, Variable variable);

    void setVariableModifiers(IModifiers modifiers, Variable variable);

    void setVariableDeclarationInvocation(DeclarationInvocation invocation, Variable variable);

    void setVariableScope(Scope scope, Variable variable);

    void setVariables(Scope scope, Scope scopeWithVariables);

    // DECLARATION INVOCATION 
    void setVariableInDeclInvocation(Variable var, DeclarationInvocation declInv);

    //ARGUMENT
    void setConstTypeInArgument(IType type, Argument arg);

    // PARAMETER
    void setTypeInParameter(IType type, IParameter codeEntity);

    //INVOCATION
    void setReturnTypeInInvocation(IType type, Invocation invocation);
    
    void setMethodNameInInvocation(String methodName, Invocation invocation);

}
