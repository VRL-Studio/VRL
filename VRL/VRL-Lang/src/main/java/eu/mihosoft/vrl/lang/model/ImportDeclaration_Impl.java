/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

import java.util.Optional;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class ImportDeclaration_Impl implements ImportDeclaration{
    private final String packageName;
    private final IType importedType;
    private final ImportType type;

    ImportDeclaration_Impl(String packageName, ImportType type) {
        
        this.type = type;
        
        if(type == ImportType.CLASS || type == ImportType.STATIC_CLASS) {
            importedType = new Type(packageName);
            this.packageName = null;
        } else {
            importedType = null;
            this.packageName = packageName;
        }
    }


    /**
     * @return the package name (only present for *-imports) 
     */
    @Override
    public Optional<String> getPackageName() {
        return Optional.ofNullable(packageName);
    }

    /**
     * @return the type (only present for class-imports) 
     */
    @Override
    public ImportType getType() {
        return type;
    }

    @Override
    public Optional<IType> getImportedType() {
        return Optional.ofNullable(importedType);
    }

    
    
}
