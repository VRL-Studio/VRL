/* 
 * MethodDeclaration_Impl.java
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
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class MethodDeclaration_Impl extends ScopeImpl implements MethodDeclaration {

    private final MethodDeclarationMetaData metadata;

    public MethodDeclaration_Impl(String id, String methodName, ClassDeclaration parent, IType returnType, IModifiers modifiers, IParameters params) {
        super(id, parent, ScopeType.METHOD, methodName, new MethodDeclarationMetaData(returnType, modifiers, params));
        metadata = (MethodDeclarationMetaData) getScopeArgs()[0];

        getNode().setTitle("method " + methodName + "()");

        createParamVariables();
    }

    private void createParamVariables() {
        for (IParameter p : metadata.getParams().getParamenters()) {

            createParamVariable(p.getType(), p.getName(), p.getRange());
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

    @Override
    public ClassDeclaration getClassDeclaration() {
        return (ClassDeclaration) getParent();
    }

    @Override
    public void setModifiers(IModifiers modifiers) {
        metadata.setModifiers(modifiers);
    }

    @Override
    public void setParameters(IParameters parameters) {
        metadata.setParams(parameters);
    }

    @Override
    public void setReturnType(IType type) {
        metadata.setType(type);
    }

}

final class MethodDeclarationMetaData {

    private IType type; //final
    private IModifiers modifiers;//final
    private IParameters params;//final

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

    public void setType(IType type) {
        this.type = type;
    }

    public void setModifiers(IModifiers modifiers) {
        this.modifiers = modifiers;
    }

    public void setParams(IParameters params) {
        this.params = params;
    }

}
