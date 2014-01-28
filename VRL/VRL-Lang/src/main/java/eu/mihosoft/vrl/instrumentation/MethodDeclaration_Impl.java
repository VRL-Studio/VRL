/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class MethodDeclaration_Impl extends ScopeImpl implements MethodDeclaration {

    private final MethodDeclarationMetaData metadata;

    public MethodDeclaration_Impl(String id, String methodName, Scope parent, IType returnType, IModifiers modifiers, IParameters params) {
        super(id, parent, ScopeType.METHOD, methodName, new MethodDeclarationMetaData(returnType, modifiers, params));
        metadata = (MethodDeclarationMetaData) getScopeArgs()[0];

        createParamVariables();
    }

    private void createParamVariables() {
        for (IParameter p : metadata.getParams().getParamenters()) {

            createVariable(p.getType(), p.getName());
        }
        
    }

    @Override
    public IType getReturnType() {
        return metadata.getType();
    }

    @Override
    public IModifiers getModifiers() {
        return metadata.getModifiers();
    }

    @Override
    public IParameters getParameters() {
        return metadata.getParams();
    }

    @Override
    public Variable getParameterAsVariable(IParameter p) {
        return getVariable(p.getName());
    }
}

final class MethodDeclarationMetaData {

    private final IType type;
    private final IModifiers modifiers;
    private final IParameters params;

    public MethodDeclarationMetaData(IType type, IModifiers modifiers, IParameters params) {
        this.type = type;
        this.modifiers = modifiers;
        this.params = params;
    }

    /**
     * @return the type
     */
    public IType getType() {
        return type;
    }

    /**
     * @return the modifiers
     */
    public IModifiers getModifiers() {
        return modifiers;
    }

    /**
     * @return the params
     */
    public IParameters getParams() {
        return params;
    }

}
