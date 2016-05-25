/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff.actions;

import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.IType;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.Scope;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class RefactoringUtils {

    public static void renameClassRefactoring(IType from, IType to, CompilationUnitDeclaration cDecl) {
        cDecl.visitScopeAndAllSubElements((e) -> {

            if (e instanceof ClassDeclaration) {
                ClassDeclaration classDecl = (ClassDeclaration) e;
                if (classDecl.getClassType().equals(from)) {
                    IModelCommands.getInstance().setClassType(to, classDecl);
                }
            } else if (e instanceof MethodDeclaration) {
                // ...
            }

            // ...
        });
    }
    
    public static void renameMethodRefactoring(MethodDeclaration mDecl, String newName, Scope cDecl) {
        cDecl.visitScopeAndAllSubElements((e) -> {
            
            System.out.println("E: " + e.getId());

            if (e instanceof ClassDeclaration) {
                ClassDeclaration classDecl = (ClassDeclaration) e;
                if (classDecl.getDeclaredMethods().contains(mDecl)) {
                    // REMANE
                }
            } else if (e instanceof MethodDeclaration) {
                MethodDeclaration mD = (MethodDeclaration) e;
                      
            }

            // ...
        });
    }
    
}
