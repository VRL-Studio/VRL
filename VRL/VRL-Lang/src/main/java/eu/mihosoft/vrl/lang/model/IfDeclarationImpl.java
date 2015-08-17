/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

import javafx.collections.ListChangeListener;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class IfDeclarationImpl extends ScopeImpl implements IfDeclaration {

    private IfDeclarationMetaData metadata;

    public IfDeclarationImpl(String id, Scope parent, Argument arg) {

        super(id, parent, ScopeType.IF, ScopeType.IF.name(), new IfDeclarationMetaData(arg));
    }
    
    IfDeclarationImpl(String id, Scope parent, ScopeType scopeType, String name, Object... scopeArgs) {
         super(id, parent, scopeType, name, scopeArgs);
    }

    @Override
    public Argument getCheck() {

        return getIfMetaData().getCheck();
    }

    private IfDeclarationMetaData getIfMetaData() {
        if (metadata == null) {
            metadata = (IfDeclarationMetaData) getScopeArgs()[0];
        }

        return metadata;
    }

    @Override
    public void defineParameters(Invocation i) {
        i.getArguments().setAll(getCheck());

        i.getArguments().addListener((ListChangeListener.Change<? extends Argument> c) -> {
            while (c.next()) {
                if (i.getArguments().isEmpty()) {
                    getIfMetaData().setCheck(Argument_Impl.NULL);
                } else {
                    getIfMetaData().setCheck(i.getArguments().get(0));
                }
            }
        });
    }

}

class IfDeclarationMetaData {

    private Argument check;

    public IfDeclarationMetaData(Argument check) {
        this.check = check;
    }

    /**
     * @return the check
     */
    public Argument getCheck() {
        return check;
    }

    public void setCheck(Argument check) {
        this.check = check;
    }

}
