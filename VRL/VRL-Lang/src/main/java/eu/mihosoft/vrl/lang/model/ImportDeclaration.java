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
public interface ImportDeclaration {

    Optional<String> getPackageName();

    ImportType getType();
    
    Optional<IType> getImportedType();

    static enum ImportType {
        CLASS,
        STAR,
        STATIC_CLASS,
        STATIC_STAR
    }

    default ImportDeclaration newImport(IType type) {
        return new ImportDeclaration_Impl(
                type.getFullClassName(),
                ImportDeclaration.ImportType.CLASS);
    }

    default ImportDeclaration newStaticImport(IType type) {
        return new ImportDeclaration_Impl(
                type.getFullClassName(),
                ImportDeclaration.ImportType.STATIC_CLASS);
    }

    default ImportDeclaration newStarImport(String packageName) {
        return new ImportDeclaration_Impl(
                packageName,
                ImportDeclaration.ImportType.STAR);
    }

    default ImportDeclaration newStaticStarImport(String packageName) {
        return new ImportDeclaration_Impl(
                packageName,
                ImportDeclaration.ImportType.STATIC_STAR);
    }
}
