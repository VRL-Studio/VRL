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

    public IfDeclarationImpl(String id, Scope parent, IArgument arg) {

        super(id, parent, ScopeType.IF, ScopeType.IF.name(), new IfDeclarationMetaData(arg));
    }

    @Override
    public IArgument getCheck() {

        return getMetaData().getCheck();
    }

    private IfDeclarationMetaData getMetaData() {
        if (metadata == null) {
            metadata = (IfDeclarationMetaData) getScopeArgs()[0];
        }

        return metadata;
    }

    @Override
    public void defineParameters(Invocation i) {
        i.getArguments().setAll(getCheck());

        i.getArguments().addListener((ListChangeListener.Change<? extends IArgument> c) -> {
            while (c.next()) {
                if (i.getArguments().isEmpty()) {
                    getMetaData().setCheck(Argument.NULL);
                } else {
                    getMetaData().setCheck(i.getArguments().get(0));
                }
            }
        });
    }

}

class IfDeclarationMetaData {

    private IArgument check;

    public IfDeclarationMetaData(IArgument check) {
        this.check = check;
    }

    /**
     * @return the check
     */
    public IArgument getCheck() {
        return check;
    }

    public void setCheck(IArgument check) {
        this.check = check;
    }

}
