/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.lang.model;

import static eu.mihosoft.vrl.lang.model.Argument_Impl.NULL;
import java.util.Optional;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface Argument {
    public ArgumentType getArgType();
    public Optional<Variable> getVariable();
    public Optional<Invocation> getInvocation();
    public Optional<Object> getConstant();
    public IType getType();
    
    public static Argument constArg(IType type, Object constant) {
        Argument result = new Argument_Impl(ArgumentType.CONSTANT, null, constant, type, null);

        return result;
    }

    public static Argument varArg(Variable v) {
        Argument result = new Argument_Impl(ArgumentType.VARIABLE, v, null, null, null);

        return result;
    }

    public static Argument invArg(Invocation i) {
        Argument result = new Argument_Impl(ArgumentType.INVOCATION, null, null, null, i);

        return result;
    }

    public static Argument nullArg() {
        return NULL;
    }
}
