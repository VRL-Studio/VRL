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
public class ElseIfDeclarationImpl extends IfDeclarationImpl implements ElseIfDeclaration{

    public ElseIfDeclarationImpl(String id, Scope parent, IArgument arg) {
        super(id, parent, ScopeType.IF, ScopeType.ELSE.name()+" "+ScopeType.IF.name(), new IfDeclarationMetaData(arg));
    }
}
