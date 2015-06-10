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
public interface IArgument extends CodeEntity {
    public ArgumentType getArgType();
    public Optional<Variable> getVariable();
    public Optional<Invocation> getInvocation();
    public Optional<Object> getConstant();
    public IType getType();
}
