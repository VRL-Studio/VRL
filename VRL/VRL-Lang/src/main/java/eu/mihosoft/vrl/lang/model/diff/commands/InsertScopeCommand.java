///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package eu.mihosoft.vrl.lang.model.diff.commands;
//
//import eu.mihosoft.vrl.lang.command.Command;
//import eu.mihosoft.vrl.lang.model.Scope;
//
///**
// *
// * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
// */
//public class InsertScopeCommand implements Command{
//
//    private Scope scope;
//    private Scope parent;
//    private int scopePos;
//
//    public InsertScopeCommand(Scope scope, Scope parent, int scopePos) {
//        this.scope = scope;
//        this.parent = parent;
//        this.scopePos = scopePos;
//    }
//    
//    
//    @Override
//    public boolean canUndo() {
//       return true; // ?
//    }
//
//    @Override
//    public void execute() {
//       parent.getScopes().add(scopePos, scope); // ? parent.addScope(scope);
//    }
//
//    @Override
//    public void rollback() {
//        parent.removeScope(scope);
//    }
//    
//}
