/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

import eu.mihosoft.vrl.lang.VLangUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class CompilationUnitDeclaration_Impl extends ScopeImpl implements CompilationUnitDeclaration{
    
    private CompilationUnitMetaData metadata;

    public CompilationUnitDeclaration_Impl(String id, Scope parent, String name, String packageName) {
        super(id, parent, ScopeType.COMPILATION_UNIT, name, new Object[0]);
        
        if (!VLangUtils.isPackageNameValid(packageName)) {
            throw new IllegalArgumentException("Specified package name is invalid: ' " + packageName + "'");
        }
        
        metadata = new CompilationUnitMetaData(packageName);
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
    
    
}

final class CompilationUnitMetaData {
    private final String packageName;

    public CompilationUnitMetaData(String packageName) {
        this.packageName = packageName;
    }

    /**
     * @return the packageName
     */
    public String getPackageName() {
        return packageName;
    }
    
    
}
