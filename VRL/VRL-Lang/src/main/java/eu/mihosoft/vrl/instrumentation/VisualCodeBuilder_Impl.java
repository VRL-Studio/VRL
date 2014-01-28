/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.IdGenerator;
import java.util.Stack;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VisualCodeBuilder_Impl implements VisualCodeBuilder {

    private final Stack<String> variables = new Stack<>();
    private IdRequest idRequest = new IdRequest() {
        
        private IdGenerator generator = FlowFactory.newIdGenerator();

        @Override
        public String request() {
            return generator.newId();
        }
    };

    String popVariable() {
        return variables.pop();
    }

    Scope createScope(Scope parent, ScopeType type, String name, Object... args) {
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
    public Variable createVariable(Scope scope, IType type, String varName) {
        Variable result = scope.createVariable(type, varName);

        variables.push(varName);

        return result;
    }

    public Variable createVariable(Scope scope, IType type) {
        
        Variable result = scope.createVariable(type);

        variables.push(result.getName());

        return result;
    }

    @Override
    public MethodDeclaration declareMethod(ClassDeclaration scope,
            IModifiers modifiers, Type returnType, String methodName, IParameters params) {
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
    public void createInstance(Scope scope, IType type, String varName, Variable... args) {

        String id = idRequest.request();

        scope.getControlFlow().createInstance(id, type, varName, args);

        variables.push(varName);
    }

    @Override
    public Invocation invokeMethod(Scope scope, String varName, String mName, boolean isVoid, String retValName, Variable... args) {
        String id = idRequest.request();

        Invocation result = scope.getControlFlow().callMethod(id, varName, mName, isVoid, retValName, args);

        return result;
    }
    
    @Override
    public Invocation invokeStaticMethod(Scope scope, IType type, String mName, boolean isVoid, String retValName, Variable... args) {
        String id = idRequest.request();

        Invocation result = scope.getControlFlow().callStaticMethod(id, type, mName, isVoid, retValName, args);

        return result;
    }

    @Override
    public void assignVariable(Scope scope, String varNameDest, String varNameSrc) {
        scope.assignVariable(varNameDest, varNameSrc);
    }

    @Override
    public void assignConstant(Scope scope, String varName, Object constant) {
        scope.assignConstant(varName, constant);
    }

    void setIdRequest(IdRequest idRequest) {
        this.idRequest = idRequest;
    }

    @Override
    public ClassDeclaration declareClass(CompilationUnitDeclaration scope, IType type, IModifiers modifiers, IExtends extendz, IExtends implementz) {
        String id = idRequest.request();
        
        ClassDeclaration result = new ClassDeclaration_Impl(id, scope, type, modifiers, extendz, implementz);
        
        return result;
    }

}
