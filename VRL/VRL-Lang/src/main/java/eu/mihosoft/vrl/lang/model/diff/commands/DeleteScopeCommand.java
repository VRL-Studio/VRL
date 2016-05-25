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
public class DeleteScopeCommand implements Command{
    
    private Scope scope;
    private Scope parent;
    private int scopePos;

    public DeleteScopeCommand(Scope scope) {
        this.scope = scope;
        this.parent = scope.getParent();
        this.scopePos = parent.getScopes().indexOf(scope);
    }
   
    @Override
    public boolean canUndo() {
        return true; // ?
    }

    @Override
    public void execute() {
        parent.removeScope(scope);
    }

    @Override
    public void rollback() {
        parent.getScopes().add(scopePos, scope);
    }
    
}
