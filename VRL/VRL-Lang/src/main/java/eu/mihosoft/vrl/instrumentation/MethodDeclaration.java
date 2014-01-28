/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.instrumentation;

import java.util.List;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface MethodDeclaration extends Scope{

    IModifiers getModifiers();

    IParameters getParameters();
    Variable getParameterAsVariable(IParameter p);

    IType getReturnType();
    
}
