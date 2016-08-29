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
//public class SwapScopesCommand implements Command{
//    
//    private Scope scope1;
//    private Scope scopeParent1;
//    private int scopePos1;
//    
//    private Scope scope2;
//    private Scope scopeParent2;
//    private int scopePos2;
//
//    public SwapScopesCommand(Scope scope1, Scope scope2) {
//        this.scope1 = scope1;
//        this.scopeParent1 = scope1.getParent();
//        this.scopePos1 = scope1.getParent().getScopes().indexOf(scope1);
//        this.scope2 = scope2;
//        this.scopeParent2 = scope2.getParent();
//        this.scopePos2 = scope2.getParent().getScopes().indexOf(scope2);
//    }
//    
//    @Override
//    public boolean canUndo() {
//       return true;
//    }
//
//    @Override
//    public void execute() {
//        scopeParent1.removeScope(scope1);
//        scopeParent2.removeScope(scope2);
//        
//        scopeParent1.getScopes().add(scopePos1, scope2);
//        scopeParent2.getScopes().add(scopePos2, scope1);
//    }
//
//    @Override
//    public void rollback() {
//        scopeParent1.removeScope(scope2);
//        scopeParent2.removeScope(scope1);
//        
//        scopeParent1.getScopes().add(scopePos1, scope1);
//        scopeParent2.getScopes().add(scopePos2, scope2);
//    }
//    
//}
