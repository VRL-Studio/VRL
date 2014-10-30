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
public interface ITypeSource extends IModelSource<CompilationUnitDeclaration,IType, ClassDeclaration>{
    @Override
    Optional<ClassDeclaration> requestModel(CompilationUnitDeclaration parent, IType type);
}
