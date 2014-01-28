/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

import eu.mihosoft.vrl.lang.VLangUtils;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Parameter implements IParameter {

    private final Type type;
    private final String name;

    public Parameter(Type type, String name) {
        this.type = type;
        this.name = name;

        validate();
    }

    private void validate() {
        if (!VLangUtils.isVariableNameValid(name)) {
            throw new IllegalArgumentException("Specified name is not a valid parameter name: " + name);
        }
    }

    /**
     * @return the type
     */
    @Override
    public Type getType() {
        return type;
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

}
