/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.lang.model;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ElseDeclarationImpl  extends ScopeImpl implements ElseDeclaration {

    public ElseDeclarationImpl(String id, ControlFlowScope parent) {
        super(id, parent, ScopeType.ELSE, ScopeType.ELSE.name());
    }

    @Override
    public void defineParameters(Invocation i) {
        //
    }
    
}
