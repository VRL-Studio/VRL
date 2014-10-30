/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ClassObjectTypeSource implements IModelSource<CompilationUnitDeclaration,Class<?>, ClassDeclaration> {

    @Override
    public Optional<ClassDeclaration> requestModel(CompilationUnitDeclaration parent, Class<?> type) {
        
        // class (n ame, modifiers, inheritance)
        ClassDeclaration_Impl classDecl = new ClassDeclaration_Impl(
                "NONE", parent, Type.fromClass(type), Modifiers.fromClass(type),
                Extends.extendsFromClass(type), Extends.implementsFromClass(type));
        
        // methods
        for(Method m : type.getMethods()) {
            classDecl.declareMethod("NONE", Modifiers.fromMember(m),
                    Type.fromClass(m.getReturnType()), m.getName(), Parameters.fromMethod(m));
        }
        
        // fields
        for(Field field : type.getFields()) {
            classDecl.createVariable(Type.fromClass(field.getType()), field.getName());
        }
        
        
        return Optional.of(classDecl);
        
    }

}
