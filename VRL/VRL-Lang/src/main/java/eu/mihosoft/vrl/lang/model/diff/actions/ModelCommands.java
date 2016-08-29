/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff.actions;

import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.ConstantValue;
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
    public void removeScope(CodeEntity parentScope, CodeEntity scope) {
        Scope parent = (Scope) parentScope;
        Scope child = (Scope) scope;
        parent.getScopes().remove(child);
        //parent.removeScope(child);
    }

    @Override
    public void removeScope(CodeEntity scope) {
        Scope cud = (Scope) scope;
        Scope parent = (Scope) cud.getParent();
        if (parent != null) {
            parent.getScopes().remove(cud);
        }
    }

    @Override
    public void insertScope(CodeEntity parentScope, CodeEntity scope) {
        Scope parent = (Scope) parentScope;
        Scope child = (Scope) scope;
        parent.getScopes().add(child);
        // parent.addScope(child);
    }

    @Override
    public void insertScope(CodeEntity parentScope, int pos, CodeEntity scope) {
        Scope parent = (Scope) parentScope;
        Scope child = (Scope) scope;
        parent.getScopes().add(pos, child);
    }

    @Override
    public void setScopeName(String ceName, CodeEntity codeEntity) {
        Scope scope = (Scope) codeEntity;
        scope.setName(ceName);
    }

    @Override
    public void insertVariableToScope(CodeEntity scope, CodeEntity variable) {
        Scope scope1 = (Scope) scope;
        Variable variable1 = (Variable) variable;
        scope1.getVariables().add(variable1);
    }

    @Override
    public void removeVariableFromScope(CodeEntity scope, CodeEntity variable) {
        Scope scope1 = (Scope) scope;
        Variable variable1 = (Variable) variable;
        if (scope1.getVariables().contains(variable1)) {
            scope1.getVariables().remove(variable1);
        }
    }

    //COMPILATION UNIT DECLARATION
    @Override
    public void removeClassFromCUD(CodeEntity cud, CodeEntity cls) {
        CompilationUnitDeclaration cuDecl = (CompilationUnitDeclaration) cud;
        ClassDeclaration classDeclaration = (ClassDeclaration) cls;
        cuDecl.getDeclaredClasses().remove(classDeclaration);
    }

    @Override
    public void insertClassToCUD(CodeEntity cud, CodeEntity cls) {
        CompilationUnitDeclaration cuDecl = (CompilationUnitDeclaration) cud;
        ClassDeclaration classDeclaration = (ClassDeclaration) cls;
        cuDecl.getDeclaredClasses().add(classDeclaration);
    }

    @Override
    public void insertClassToCUD(CodeEntity cud, int pos, CodeEntity cls) {
        CompilationUnitDeclaration cuDecl = (CompilationUnitDeclaration) cud;
        ClassDeclaration classDeclaration = (ClassDeclaration) cls;
        cuDecl.getDeclaredClasses().add(pos, classDeclaration);
    }

    @Override
    public void setCUDeclPackageName(String packageName, CodeEntity codeEntity) {
        CompilationUnitDeclaration cuDecl = (CompilationUnitDeclaration) codeEntity;
        cuDecl.setPackageName(packageName);
    }

    @Override
    public void setCUDeclFileName(String fileName, CodeEntity codeEntity) {
        CompilationUnitDeclaration cuDecl = (CompilationUnitDeclaration) codeEntity;
        cuDecl.setFileName(fileName);
    }

    // CLASS
    @Override
    public ClassDeclaration removeMethodFromClass(ClassDeclaration cls, MethodDeclaration method) {
        cls.getDeclaredMethods().remove(method);
        return cls;
    }

    @Override
    public void insertMethodToClass(CodeEntity cls, CodeEntity method) {
        ClassDeclaration classDeclaration = (ClassDeclaration) cls;
        MethodDeclaration methodDeclaration = (MethodDeclaration) method;
        classDeclaration.getDeclaredMethods().add(methodDeclaration);
    }

    @Override
    public void insertMethodToClass(CodeEntity cls, int pos, CodeEntity method) {
        ClassDeclaration classDeclaration = (ClassDeclaration) cls;
        MethodDeclaration methodDeclaration = (MethodDeclaration) method;
        classDeclaration.getDeclaredMethods().add(pos, methodDeclaration);
    }

    @Override
    public void setClassType(IType type, CodeEntity codeEntity) {
        ClassDeclaration classDeclaration = (ClassDeclaration) codeEntity;
        classDeclaration.setClassType(type);
        
    }

    @Override
    public void setClassModifiers(IModifiers modifiers, CodeEntity codeEntity) {
        ClassDeclaration classDeclaration = (ClassDeclaration) codeEntity;
        classDeclaration.setClassModifiers(modifiers);
    }

    @Override
    public void setClassImplements(IExtends implementz, CodeEntity codeEntity) {
        ClassDeclaration classDeclaration = (ClassDeclaration) codeEntity;
        classDeclaration.setImplements(implementz);
    }

    @Override
    public void setClassExtends(IExtends extendz, CodeEntity codeEntity) {
        ClassDeclaration classDeclaration = (ClassDeclaration) codeEntity;
        classDeclaration.setExtends(extendz);
    }

    // METHOD
    @Override
    public void removeVariableFromMethod(CodeEntity method, CodeEntity variable) {
        MethodDeclaration methodDeclaration = (MethodDeclaration) method;
        Variable var = (Variable) variable;
        if (methodDeclaration.getVariables().contains(var)) {
            methodDeclaration.getVariables().remove(var);
        }
    }

    @Override
    public void insertVariableToMethod(CodeEntity method, CodeEntity variable) {
        MethodDeclaration methodDeclaration = (MethodDeclaration) method;
        Variable var = (Variable) variable;
        methodDeclaration.getVariables().add(var);
    }

    @Override
    public void setMethodName(String methName, CodeEntity codeEntity) {
        MethodDeclaration methodDeclaration = (MethodDeclaration) codeEntity;
        methodDeclaration.setName(methName);

    }

    @Override
    public void setMethodModifiers(IModifiers modifiers, CodeEntity codeEntity) {
        MethodDeclaration methodDeclaration = (MethodDeclaration) codeEntity;
        methodDeclaration.setModifiers(modifiers);
    }

    @Override
    public void setMethodParameters(IParameters parameters, CodeEntity codeEntity) {
        MethodDeclaration methodDeclaration = (MethodDeclaration) codeEntity;
        methodDeclaration.setParameters(parameters);
    }

    @Override
    public void setMethodReturnType(IType type, CodeEntity codeEntity) {
        MethodDeclaration methodDeclaration = (MethodDeclaration) codeEntity;
        methodDeclaration.setReturnType(type);
    }

    //VARIABLE
    @Override
    public void setVariableName(String varName, CodeEntity codeEntity) {
        Variable variable = (Variable) codeEntity;
        variable.setName(varName);
    }

    @Override
    public void setVariableType(IType type, CodeEntity codeEntity) {
        Variable variable = (Variable) codeEntity;
        variable.setType(type);
    }

    @Override
    public void setVariableValue(Object value, CodeEntity codeEntity) {
        Variable variable = (Variable) codeEntity;
        variable.setValue(value);
    }

    @Override
    public void setVariableConstant(Boolean constant, CodeEntity codeEntity) {
        Variable variable = (Variable) codeEntity;
        variable.setConstant(constant);
    }

    @Override
    public void setVariableModifiers(IModifiers modifiers, CodeEntity codeEntity) {
        Variable variable = (Variable) codeEntity;
        variable.setModifiers(modifiers);
    }

    @Override
    public void setVariableDeclarationInvocation(DeclarationInvocation invocation, CodeEntity codeEntity) {
        Variable variable = (Variable) codeEntity;
        variable.setDeclaration(invocation);
    }

    @Override
    public void setVariableScope(Scope scope, CodeEntity codeEntity) {
        Variable variable = (Variable) codeEntity;
        variable.setScope(scope);
    }

    // DECLARATION INVOCATION
    @Override
    public void setVariableInDeclInvocation(Variable var, DeclarationInvocation declInv) {
        declInv.setDeclaredVariable(var);
    }

    // Argument
    @Override
    public void setConstTypeInArgument(IType type, CodeEntity codeEntity) {
        Argument arg = (Argument) codeEntity;
        arg.setConstType(type);
    }

    // Constant Value
    @Override
    public void setTypeInConstValue(IType type, CodeEntity codeEntity) {
        ConstantValue cv = (ConstantValue) codeEntity;
        cv.setType(type);
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
    public void setVariables(Scope scope, Scope scopeVar) {
        scope.setVariables(scopeVar.getVariablesInMap());
    }
}
