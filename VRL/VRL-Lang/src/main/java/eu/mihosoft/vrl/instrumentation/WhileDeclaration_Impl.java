/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.instrumentation;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class WhileDeclaration_Impl extends ScopeImpl implements WhileDeclaration{
    private final WhileDeclarationMetaData metadata;

    public WhileDeclaration_Impl(String id, Scope parent, Invocation invocation) {
        super(id, parent, ScopeType.WHILE, ScopeType.WHILE.name(), new WhileDeclarationMetaData(invocation));
        metadata = (WhileDeclarationMetaData) getScopeArgs()[0];
    }

    @Override
    public Invocation getCheck() {
        return metadata.getCheck();
    } 
}

class WhileDeclarationMetaData {
    private final Invocation check;

    public WhileDeclarationMetaData(Invocation check) {
        this.check = check;
    }

    /**
     * @return the check
     */
    public Invocation getCheck() {
        return check;
    }
    
}
