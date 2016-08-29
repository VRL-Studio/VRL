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
enum ModelCommands implements IModelCommands {

    INSTANCE;

    //SCOPE
    @Override 
    public void removeScope(Scope parent, Scope scope) {
        parent.getScopes().remove(scope);
        //parent.removeScope(scope);
    }

    @Override
    public void removeScope(Scope scope) {
        Scope parent = scope.getParent();
        if (parent != null) {
            parent.getScopes().remove(scope);
        }
    }

    @Override
    public void insertScope(Scope parent, Scope scope) {
        parent.getScopes().add(scope);
        // parent.addScope(scope);
    }

    @Override
    public void insertScope(Scope parent, int pos, Scope scope) {
        parent.getScopes().add(pos, scope);
    }

    @Override
    public void setScopeName(String ceName, Scope scope) {
        scope.setName(ceName);
    }

    @Override
    public void insertVariableToScope(Scope scope, Variable variable) {
        scope.getVariables().add(variable);
    }

    @Override
    public void removeVariableFromScope(Scope scope, Variable variable) {
        if (scope.getVariables().contains(variable)) {
            scope.getVariables().remove(variable);
        }
    }

    //COMPILATION UNIT DECLARATION
    @Override
    public void removeClassFromCUD(CompilationUnitDeclaration cud, ClassDeclaration cls) {
        cud.getDeclaredClasses().remove(cls);
    }

    @Override
    public void insertClassToCUD(CompilationUnitDeclaration cud, ClassDeclaration cls) {
        cud.getDeclaredClasses().add(cls);
    }

    @Override
    public void insertClassToCUD(CompilationUnitDeclaration cud, int pos, ClassDeclaration cls) {
        cud.getDeclaredClasses().add(pos, cls);
    }

    @Override
    public void setCUDeclPackageName(String packageName, CompilationUnitDeclaration cud) {
        cud.setPackageName(packageName);
    }

    @Override
    public void setCUDeclFileName(String fileName, CompilationUnitDeclaration cud) {
        cud.setFileName(fileName);
    }

    // CLASS
    @Override
    public ClassDeclaration removeMethodFromClass(ClassDeclaration cls, MethodDeclaration method) {
        cls.getDeclaredMethods().remove(method);
        return cls;
    }

    @Override
    public void insertMethodToClass(ClassDeclaration cls, MethodDeclaration method) {
        ClassDeclaration classDeclaration = (ClassDeclaration) cls;
        MethodDeclaration methodDeclaration = (MethodDeclaration) method;
        classDeclaration.getDeclaredMethods().add(methodDeclaration);
    }

    @Override
    public void insertMethodToClass(ClassDeclaration cls, int pos, MethodDeclaration method) {
        cls.getDeclaredMethods().add(pos, method);
    }

    @Override
    public void setClassType(IType type, ClassDeclaration classDeclaration) {
        classDeclaration.setClassType(type);
        
    }

    @Override
    public void setClassModifiers(IModifiers modifiers, ClassDeclaration classDeclaration) {
        classDeclaration.setClassModifiers(modifiers);
    }

    @Override
    public void setClassImplements(IExtends implementz, ClassDeclaration classDeclaration) {
        classDeclaration.setImplements(implementz);
    }

    @Override
    public void setClassExtends(IExtends extendz, ClassDeclaration classDeclaration) {
        classDeclaration.setExtends(extendz);
    }

    // METHOD
    @Override
    public void removeVariableFromMethod(MethodDeclaration methodDeclaration, Variable variable) {
        if (methodDeclaration.getVariables().contains(variable)) {
            methodDeclaration.getVariables().remove(variable);
        }
    }

    @Override
    public void insertVariableToMethod(MethodDeclaration methodDeclaration, Variable variable) {
        methodDeclaration.getVariables().add(variable);
    }

    @Override
    public void setMethodName(String methName, MethodDeclaration methodDeclaration) {
        methodDeclaration.setName(methName);
    }

    @Override
    public void setMethodModifiers(IModifiers modifiers, MethodDeclaration methodDeclaration) {
        methodDeclaration.setModifiers(modifiers);
    }

    @Override
    public void setMethodParameters(IParameters parameters, MethodDeclaration methodDeclaration) {
        methodDeclaration.setParameters(parameters);
    }

    @Override
    public void setMethodReturnType(IType type, MethodDeclaration methodDeclaration) {
        methodDeclaration.setReturnType(type);
    }

    //VARIABLE
    @Override
    public void setVariableName(String varName, Variable variable) {
        variable.setName(varName);
    }

    @Override
    public void setVariableType(IType type, Variable variable) {
        variable.setType(type);
    }

    @Override
    public void setVariableValue(Object value, Variable variable) {
        variable.setValue(value);
    }

    @Override
    public void setVariableConstant(Boolean constant, Variable variable) {
        variable.setConstant(constant);
    }

    @Override
    public void setVariableModifiers(IModifiers modifiers, Variable variable) {
        variable.setModifiers(modifiers);
    }

    @Override
    public void setVariableDeclarationInvocation(DeclarationInvocation invocation, Variable variable) {
        variable.setDeclaration(invocation);
    }

    @Override
    public void setVariableScope(Scope scope, Variable variable) {
        variable.setScope(scope);
    }

    // DECLARATION INVOCATION
    @Override
    public void setVariableInDeclInvocation(Variable var, DeclarationInvocation declInv) {
        declInv.setDeclaredVariable(var);
    }

    // Argument
    @Override
    public void setConstTypeInArgument(IType type, Argument arg) {
        arg.setConstType(type);
    }


    // PARAMETER
    @Override
    public void setTypeInParameter(IType type, IParameter param) {
        param.setType(type);
    }
    
    // INVOCATION

    @Override
    public void setReturnTypeInInvocation(IType type, Invocation invocation) {
        invocation.setReturnType1(type);
    }

    @Override
    public void setVariables(Scope scope, Scope scopeWithVariables) {
        scope.setVariables(scopeWithVariables.getVariablesInMap());
    }
}
