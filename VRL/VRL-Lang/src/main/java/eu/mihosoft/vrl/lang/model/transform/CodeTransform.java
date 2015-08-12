/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.transform;

import eu.mihosoft.vrl.lang.model.CodeEntity;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 * @param <T>
 */
@FunctionalInterface
public interface CodeTransform<T extends CodeEntity> {
    /**
     * 
     * @param ce conde entity to transform
     * @return transformed copy of the cpecified code entity
     */
    public T transform(T ce);
}
