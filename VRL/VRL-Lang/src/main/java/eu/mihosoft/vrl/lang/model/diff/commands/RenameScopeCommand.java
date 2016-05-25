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
public class RenameScopeCommand implements Command{

    private Scope scope;
    private String oldName;
    private String newName;

    public RenameScopeCommand(Scope scope, String newName) {
        this.scope = scope;
        this.oldName = scope.getName();
        this.newName = newName;
    }
    
    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public void execute() {
        
    }

    @Override
    public void rollback() {
        
    }
    
}
