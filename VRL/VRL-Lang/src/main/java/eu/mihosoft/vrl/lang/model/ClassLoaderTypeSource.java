/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public final class ClassLoaderTypeSource implements ITypeSource{
    
    private final ClassLoader loader;
    private final ClassObjectTypeSource clsTypeSource;

    public ClassLoaderTypeSource(ClassLoader loader) {
        this.loader = loader;
        this.clsTypeSource = new ClassObjectTypeSource();
    }

    @Override
    public Optional<ClassDeclaration> requestModel(
            CompilationUnitDeclaration parent, IType type) {
        try {
            Class<?> cls = loader.loadClass(type.getFullClassName());  
            return clsTypeSource.requestModel(parent, cls);
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClassLoaderTypeSource.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        
        return Optional.empty();
    }
    
}
