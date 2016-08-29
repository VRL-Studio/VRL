/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.diff.actions;

import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.IParameter;
import eu.mihosoft.vrl.lang.model.IType;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.Variable;

/**
 *
 * @author Joanna Pieper <joanna.pieper@gcsc.uni-frankfurt.de>
 */
public class RefactoringUtils {

    public static void renameClassRefactoring(IType from, IType to, CompilationUnitDeclaration cuDecl) {
        cuDecl.visitScopeAndAllSubElements((e) -> {

            if (e instanceof Scope) {
                Scope s = (Scope) e;
                for (Variable v : s.getVariables()) {
                    if (v.getType().equals(from)) {
                        IModelCommands.getInstance().setVariableType(to, v);
                    }
                }
            }

            if (e instanceof ClassDeclaration) {
                ClassDeclaration classDecl = (ClassDeclaration) e;
                if (classDecl.getClassType().equals(from)) {
                    IModelCommands.getInstance().setClassType(to, classDecl);
                    IModelCommands.getInstance().setScopeName(to.getFullClassName(), classDecl);
                    if (cuDecl.getDeclaredClasses().get(0).equals(classDecl)) {
                        String ending = VLangUtils.shortNameFromFullClassName(cuDecl.getFileName());
                        IModelCommands.getInstance().setCUDeclFileName(to.getFullClassName() + "." + ending, cuDecl);
                        IModelCommands.getInstance().setCUDeclPackageName(to.getPackageName(), cuDecl);
                    }
                }
            } else if (e instanceof MethodDeclaration) {
                MethodDeclaration methDecl = (MethodDeclaration) e;
                if (methDecl.getReturnType().equals(from)) {
                    IModelCommands.getInstance().setMethodReturnType(to, methDecl);
                }
                for (IParameter p : methDecl.getParameters().getParamenters()) {
                    if (p.getType().equals(from)) {
                        IModelCommands.getInstance().setTypeInParameter(to, p);
                    }
                }
//            } else if (e instanceof ConstantValue) {
//                ConstantValue cv = (ConstantValue) e;
//                if (cv.getType().equals(from)) {
//                    IModelCommands.getInstance().setTypeInConstValue(to, cv);
//                }
            } else if (e instanceof Invocation) {
                Invocation inv = (Invocation) e;
                if (inv.getReturnType().equals(from)) {
                    IModelCommands.getInstance().setReturnTypeInInvocation(to, inv);
                }
                for (Argument a : inv.getArguments()) {
                    if (a.getType().equals(from)) {
                        IModelCommands.getInstance().setConstTypeInArgument(to, a);
                    }
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
