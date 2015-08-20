/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility methods for analyzing scope hierarchies.
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
    public static Optional<ClassDeclaration> getEnclosingClassDeclaration(
            CodeEntity cE) {
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

        return i.getObjectProvider().getClassObject().
                map(cls -> cls.equals(enclosingClass.get().getClassType())).orElse(false);

    }

    /**
     * Returns a set containing all used types in the specified scope and all
     * of its subscopes.
     * 
     * @param s scope to analyze
     * @return set that contains all types that are used in the specified scope
     */
    public static Set<IType> getUsedTypes(Scope s) {

        Set<IType> usedTypes = new HashSet<>();

        // adding results to set to get distinct collection
        usedTypes.addAll(scopeTypes(s));
        
        return usedTypes;
    }

    /**
     * @see #getUsedTypes(eu.mihosoft.vrl.lang.model.Scope) 
     * @param cd
     * @return 
     */
    private static List<IType> classTypes(ClassDeclaration cd) {
        List<IType> cTypes = new ArrayList<>();

        cTypes.add(cd.getClassType());
        cTypes.addAll(cd.getExtends().getTypes());
        cTypes.addAll(cd.getImplements().getTypes());

        return cTypes;
    }

    /**
     * @see #getUsedTypes(eu.mihosoft.vrl.lang.model.Scope) 
     * @param md
     * @return 
     */
    private static List<IType> methodTypes(MethodDeclaration md) {
        List<IType> mTypes = new ArrayList<>();

        mTypes.add(md.getReturnType());
        List<IType> params
                = md.getParameters().getParamenters().stream().
                map(p -> p.getType()).collect(Collectors.toList());
        mTypes.addAll(params);

        return mTypes;
    }

    /**
     * @see #getUsedTypes(eu.mihosoft.vrl.lang.model.Scope) 
     * @param scope
     * @return 
     */
    private static List<IType> scopeTypes(Scope scope) {
        List<IType> result = new ArrayList<>();
        scope.getVariables().stream().map(v -> v.getType()).
                collect(Collectors.toCollection(() -> result));

        scope.getScopes().stream().flatMap(s -> scopeTypes(s).stream()).
                collect(Collectors.toCollection(() -> result));

        if (scope instanceof ControlFlowScope) {
            ControlFlowScope cfs = (ControlFlowScope) scope;
            cfs.getControlFlow().getInvocations().stream().
                    filter(inv -> inv.getObjectProvider().getClassObject().isPresent()).
                    map(inv -> inv.getObjectProvider().getClassObject().get()).
                    collect(Collectors.toCollection(() -> result));

            cfs.getControlFlow().getInvocations().stream().
                    filter(inv -> inv instanceof ScopeInvocation).
                    map(inv -> (ScopeInvocation) inv).
                    flatMap(sInv -> scopeTypes(sInv.getScope()).stream()).
                    collect(Collectors.toCollection(() -> result));
        }

        if (scope instanceof ClassDeclaration) {
            result.addAll(classTypes((ClassDeclaration) scope));
        } else if (scope instanceof MethodDeclaration) {
            result.addAll(methodTypes((MethodDeclaration) scope));
        }

        return result;
    }
}
