/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff.actions;

import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CodeEntity;
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

    void setScopeName(String ceName, CodeEntity codeEntity);

    void removeScope(CodeEntity parentScope, CodeEntity scope);

    void removeScope(CodeEntity scope);

    void insertScope(CodeEntity parentScope, CodeEntity scope);

    void insertScope(CodeEntity parentScope, int pos, CodeEntity scope);

    void insertVariableToScope(CodeEntity scope, CodeEntity variable);

    void removeVariableFromScope(CodeEntity scope, CodeEntity variable);

    //COMPILATION UNIT DECLARATION
    void removeClassFromCUD(CodeEntity cud, CodeEntity cls);

    void insertClassToCUD(CodeEntity cud, CodeEntity cls);

    void insertClassToCUD(CodeEntity cud, int pos, CodeEntity cls);

    void setCUDeclPackageName(String packageName, CodeEntity codeEntity);

    void setCUDeclFileName(String name, CodeEntity codeEntity);

    // CLASS
    ClassDeclaration removeMethodFromClass(ClassDeclaration cls, MethodDeclaration method);

    void insertMethodToClass(CodeEntity cls, CodeEntity method);

    void insertMethodToClass(CodeEntity cls, int pos, CodeEntity method);

    void setClassType(IType type, CodeEntity codeEntity);

    void setClassModifiers(IModifiers modifiers, CodeEntity codeEntity);

    void setClassImplements(IExtends implementz, CodeEntity codeEntity);

    void setClassExtends(IExtends extendz, CodeEntity codeEntity);

    // METHOD
    void removeVariableFromMethod(CodeEntity method, CodeEntity variable);

    void insertVariableToMethod(CodeEntity method, CodeEntity variable);

    void setMethodName(String methName, CodeEntity codeEntity);

    void setMethodModifiers(IModifiers modifiers, CodeEntity codeEntity);

    void setMethodParameters(IParameters parameters, CodeEntity codeEntity);

    void setMethodReturnType(IType type, CodeEntity codeEntity);

    // VARIABLE
    void setVariableName(String varName, CodeEntity codeEntity);

    void setVariableType(IType type, CodeEntity codeEntity);

    void setVariableValue(Object value, CodeEntity codeEntity);

    void setVariableConstant(Boolean constant, CodeEntity codeEntity);

    void setVariableModifiers(IModifiers modifiers, CodeEntity codeEntity);

    void setVariableDeclarationInvocation(DeclarationInvocation invocation, CodeEntity codeEntity);

    void setVariableScope(Scope scope, CodeEntity codeEntity);
    
    void setVariables(Scope scope, Scope scopeVar);

    // DECLARATION INVOCATION 
    void setVariableInDeclInvocation(Variable var, DeclarationInvocation declInv);

    //ARGUMENT
    void setConstTypeInArgument(IType type, CodeEntity codeEntity);

    //CONSTANT VALUE
    void setTypeInConstValue(IType type, CodeEntity codeEntity);

    // PARAMETER
    void setTypeInParameter(IType type, IParameter codeEntity);
    
    //INVOCATION
    void setReturnTypeInInvocation(IType type, Invocation invocation);

}
