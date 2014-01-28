/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public final class Modifiers implements IModifiers {

    private final List<Modifier> modifiers = new ArrayList<>();
    private List<Modifier> readOnlyModifiers;

    public Modifiers(Modifier... modifiers) {
        this.modifiers.addAll(Arrays.asList(modifiers));
    }

    @Override
    public List<Modifier> getModifiers() {
        if (readOnlyModifiers == null) {
            readOnlyModifiers = Collections.unmodifiableList(modifiers);
        }
        
        return readOnlyModifiers;
    }

}
