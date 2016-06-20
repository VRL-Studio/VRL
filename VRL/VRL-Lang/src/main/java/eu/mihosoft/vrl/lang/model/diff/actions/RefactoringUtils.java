/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff.actions;

import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.ConstantValue;
import eu.mihosoft.vrl.lang.model.IType;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.Parameter;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.Variable;

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
                    IModelCommands.getInstance().setScopeName(to.getShortName(), classDecl);
                    System.out.println("Class Declaration set Type");
                }
            } else if (e instanceof MethodDeclaration) {
                MethodDeclaration methDecl = (MethodDeclaration) e;
                if (methDecl.getReturnType().equals(from)) {
                    IModelCommands.getInstance().setMethodReturnType(to, methDecl);
                    System.out.println("MethodDeclaration set Type");
                }
            } else if (e instanceof Argument) {
                Argument arg = (Argument) e;
                if (arg.getType().equals(from)) {
                    IModelCommands.getInstance().setConstTypeInArgument(to, arg);
                    System.out.println("Argument set Type");
                }
            } else if (e instanceof ConstantValue) {
                ConstantValue cv = (ConstantValue) e;
                if (cv.getType().equals(from)) {
                    IModelCommands.getInstance().setTypeInConstValue(to, cv);
                    System.out.println("ConstantValue set Type");
                }
            } else if (e instanceof Parameter) {
                Parameter param = (Parameter) e;
                if (param.getType().equals(from)) {
                    IModelCommands.getInstance().setTypeInParameter(to, e);
                    System.out.println("Parameter set Type");
                }
            } else if (e instanceof Variable) {
                Variable var = (Variable) e;
                if (var.getType().equals(from)) {
                    IModelCommands.getInstance().setVariableType(to, var);
                    System.out.println("Variable set Type");
                }

            }
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
