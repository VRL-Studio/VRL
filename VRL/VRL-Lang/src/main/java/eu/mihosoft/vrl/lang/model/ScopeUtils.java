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
public class ScopeUtils {

    private ScopeUtils() {
        throw new AssertionError("Instantiation not allowd!", null);
    }

    /**
     * Returns the first enclosing class of the specified code entity.
     *
     * @param cE code entity to analyze
     * @return the first enclosing class of the specified code entity
     */
    public static Optional<ClassDeclaration> getEnclosingClassDeclaration(CodeEntity cE) {
        Scope parent = cE.getParent();

        while (parent != null) {
            if (parent instanceof ClassDeclaration) {
                return Optional.of((ClassDeclaration) parent);
            }
            parent = parent.getParent();
        }

        return Optional.empty();
    }

    /**
     * Determines whether the calling object of the specified invocation is the
     * enclosing class of the corresponding method definition.
     *
     * @param i the invocation to analyze
     * @return <code>true</code> if the calling object of the specified
     * invocation is the enclosing class of the corresponding method definition;
     * <code>false</code> otherwise
     */
    public static boolean callingObjectIsEnclosingClass(Invocation i) {

        if (!i.isStatic()) {
            return false;
        }

        Optional<ClassDeclaration> enclosingClass
                = getEnclosingClassDeclaration(i);

        if (!enclosingClass.isPresent()) {
            return false;
        }

        return i.getVariableName().equals(enclosingClass.get().getName());

    }
}
