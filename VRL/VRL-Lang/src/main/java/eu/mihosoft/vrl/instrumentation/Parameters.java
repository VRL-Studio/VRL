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
public final class Parameters implements IParameters {

    private final List<IParameter> arguments = new ArrayList<>();
    private List<IParameter> readOnlyParams;

    public Parameters(IParameter... params) {
        this.arguments.addAll(Arrays.asList(params));
    }

    @Override
    public List<IParameter> getParamenters() {
        if (readOnlyParams == null) {
            readOnlyParams = Collections.unmodifiableList(arguments);
        }
        
        return readOnlyParams;
    }

}
