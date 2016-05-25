/* 
 * CompilationUnitDeclaration_Impl.java
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

import eu.mihosoft.vrl.lang.VLangUtilsNew;
import eu.mihosoft.vrl.workflow.VFlow;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class CompilationUnitDeclaration_Impl extends ScopeImpl implements CompilationUnitDeclaration{
    
    private CompilationUnitMetaData metadata;

    public CompilationUnitDeclaration_Impl(String id, Scope parent, String name, String packageName, VFlow rootFlow) {
        super(id, parent, ScopeType.COMPILATION_UNIT, name, rootFlow, new Object[0]);
        
        if (!VLangUtilsNew.isPackageNameValid(packageName)) {
            throw new IllegalArgumentException("Specified package name is invalid: ' " + packageName + "'");
        }
        
        metadata = new CompilationUnitMetaData(packageName);
        
        
        if (name.toLowerCase().endsWith(".java")) {
            name = name.substring(0, name.length()-5);
        }
        
        if (!name.toLowerCase().endsWith(".groovy")) {
            name = name + ".groovy";
        }
        
        getNode().setTitle("file " + name);
    }

    @Override
    public String getFileName() {
        return super.getName();
    }
    
    @Override
    public String getPackageName() {
        return metadata.getPackageName();
    }

    @Override
    public List<ClassDeclaration> getDeclaredClasses() {
//        List<ClassDeclaration> result = new ArrayList<>();
//        for (Scope cls : getScopes()) {
//            if (cls instanceof ClassDeclaration) {
//                result.add((ClassDeclaration)cls);
//            } 
//        }
//        
//        return result;
        
        return getScopes().stream().
                filter(it -> it instanceof ClassDeclaration).
                map(it->(ClassDeclaration)it).
                collect(Collectors.toList());
    }

    @Override
    public void setFileName(String fileName) {
        super.setName(fileName);
    }

    @Override
    public void setPackageName(String packageName) {
        metadata.setPackageName(packageName);
    }
    
    
}

final class CompilationUnitMetaData {
    private String packageName;

    public CompilationUnitMetaData(String packageName) {
        this.packageName = packageName;
    }

    /**
     * @return the packageName
     */
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    
}
