/* 
 * ClassDeclaration_Impl.java
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

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class ClassDeclaration_Impl extends ScopeImpl implements ClassDeclaration {

    private final ClassDeclarationMetaData metadata;

    public ClassDeclaration_Impl(String id, Scope parent, IType type, IModifiers modifiers, IExtends extendz, IExtends implementz) {
        super(id, parent, ScopeType.CLASS, type.getFullClassName(), new ClassDeclarationMetaData(type, modifiers, extendz, implementz));
        metadata = (ClassDeclarationMetaData) getScopeArgs()[0];

        createVariable(getClassType(), "this");

        getNode().setTitle("class " + type.getShortName());
    }

    @Override
    public final IType getClassType() {
        return metadata.getType();
    }

    @Override
    public IModifiers getClassModifiers() {
        return metadata.getModifiers();
    }

    @Override
    public IExtends getExtends() {
        return metadata.getExtendz();
    }

    @Override
    public IExtends getImplements() {
        return metadata.getImplementz();
    }

    @Override
    public List<MethodDeclaration> getDeclaredMethods() {
        return metadata.getDeclaredMethods();
    }

    @Override
    public MethodDeclaration declareMethod(String id, IModifiers modifiers, IType returnType, String methodName, IParameters params) {
        if (this.getType() != ScopeType.CLASS && this.getType() != ScopeType.NONE && this.getType() != ScopeType.COMPILATION_UNIT) {
            throw new IllegalArgumentException("Specified scopetype does not support method declaration: " + this.getType());
        }

        MethodDeclaration methodScope = new MethodDeclaration_Impl(id, methodName, this, returnType, modifiers, params);

        metadata.getDeclaredMethods().add(methodScope);
        
        addScope(methodScope);

        return methodScope;
    }

    @Override
    public boolean removeScope(Scope s) {

        boolean mResult = false;
        
        if (s instanceof MethodDeclaration) {
            mResult =  getDeclaredMethods().remove((MethodDeclaration) s);
        }
        
        boolean sResult = super.removeScope(s);
        

        return mResult || sResult;
    }
}

final class ClassDeclarationMetaData {

    private final IType type;
    private final IModifiers modifiers;
    private final IExtends extendz;
    private final IExtends implementz;
    private final ObservableList<MethodDeclaration> declaredMethods =FXCollections.observableArrayList();

    public ClassDeclarationMetaData(IType type, IModifiers modifiers, IExtends extendz, IExtends implementz) {
        this.type = type;
        this.modifiers = modifiers;
        this.extendz = extendz;
        this.implementz = implementz;
    }

    /**
     * @return the extendz
     */
    public IExtends getExtendz() {
        return extendz;
    }

    /**
     * @return the implementz
     */
    public IExtends getImplementz() {
        return implementz;
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
     * @return the declaredMethods
     */
    public List<MethodDeclaration> getDeclaredMethods() {
        return declaredMethods;
    }

}
