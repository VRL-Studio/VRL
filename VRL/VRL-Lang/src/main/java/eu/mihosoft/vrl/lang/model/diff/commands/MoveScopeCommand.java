/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff.commands;

import eu.mihosoft.vrl.lang.command.Command;
import eu.mihosoft.vrl.lang.model.Scope;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class MoveScopeCommand implements Command {

    private Scope scope;
    private Scope targetScopeParent;
    private int targetScopePos;

    private Scope sourceScopeParent;
    private int sourceScopePos;

    public MoveScopeCommand(Scope scope, Scope sourceScopeParent, int sourceScopePos) {
        this.scope = scope;
        this.targetScopeParent = scope.getParent();
        this.targetScopePos =  scope.getParent().getScopes().indexOf(scope);
        this.sourceScopeParent = sourceScopeParent;
        this.sourceScopePos = sourceScopePos;
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public void execute() {
        sourceScopeParent.getScopes().add(sourceScopePos, scope);
        targetScopeParent.removeScope(scope);
    }

    @Override
    public void rollback() {
        targetScopeParent.getScopes().add(targetScopePos, scope);
        sourceScopeParent.removeScope(scope);
    }

}
