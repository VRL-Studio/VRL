/* 
 * Scope2Code.java
 *
 * Copyright (c) 2009–2014 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2014 by Michael Hoffer
 * 
 * This file is part of Visual Reflection Library (VRL).
 *
 * VRL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * see: http://opensource.org/licenses/LGPL-3.0
 *      file://path/to/VRL/src/eu/mihosoft/vrl/resources/license/lgplv3.txt
 *
 * VRL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * This version of VRL includes copyright notice and attribution requirements.
 * According to the LGPL this information must be displayed even if you modify
 * the source code of VRL. Neither the VRL Canvas attribution icon nor any
 * copyright statement/attribution may be removed.
 *
 * Attribution Requirements:
 *
 * If you create derived work you must do three things regarding copyright
 * notice and author attribution.
 *
 * First, the following text must be displayed on the Canvas or an equivalent location:
 * "based on VRL source code".
 * 
 * Second, the copyright notice must remain. It must be reproduced in any
 * program that uses VRL.
 *
 * Third, add an additional notice, stating that you modified VRL. In addition
 * you must cite the publications listed below. A suitable notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Publications:
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, in press.
 */
package eu.mihosoft.vrl.lang.model;

//import org.stringtemplate.v4.ST;
import eu.mihosoft.vrl.lang.CodeRenderer;
import eu.mihosoft.vrl.lang.VLangUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

//import org.stringtemplate.v4.STGroup;
//import org.stringtemplate.v4.STGroupString;
/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Scope2Code {

    static Set<Comment> renderedComments = new HashSet<>();

    public static String getCode(CompilationUnitDeclaration scope) {

        renderedComments.clear();

        CompilationUnitRenderer renderer
                = new CompilationUnitRenderer(
                        new ClassDeclarationRenderer(
                                new MethodDeclarationRenderer(
                                        new InvocationCodeRenderer())));

        return renderer.render(scope);
    }

    public static String getCode(ClassDeclaration scope) {

        renderedComments.clear();

        ClassDeclarationRenderer renderer
                = new ClassDeclarationRenderer(
                        new MethodDeclarationRenderer(
                                new InvocationCodeRenderer()));

        return renderer.render(scope);
    }

    public static String getCode(MethodDeclaration scope) {

        renderedComments.clear();

        MethodDeclarationRenderer renderer
                = new MethodDeclarationRenderer(
                        new InvocationCodeRenderer());

        return renderer.render(scope);
    }

    public static String getCode(Invocation invocation) {

        renderedComments.clear();

        return new InvocationCodeRenderer().render(invocation);
    }

}

final class Utils {

    private static Map<Modifier, String> modifierNames = new HashMap<>();

    private Utils() {
        throw new AssertionError();
    }

    static {
        modifierNames.put(Modifier.ABSTRACT, "abstract");
        modifierNames.put(Modifier.FINAL, "final");

        modifierNames.put(Modifier.PRIVATE, "private");
        modifierNames.put(Modifier.PROTECTED, "protected");

        modifierNames.put(Modifier.PUBLIC, "public");
        modifierNames.put(Modifier.STATIC, "static");
    }

    public static String modifierToName(Modifier m) {

        return modifierNames.get(m);
    }

    static void renderComments(Set<Comment> rendered,
            List<? extends CodeEntity> entities, Scope e,
            CodeBuilder cb, RenderEventListener rel) {
        CommentRenderer commentRenderer = new CommentRenderer();
        for (CodeEntity ce : entities) {

            for (Comment comment : e.getComments()) {

                if (rendered.contains(comment)) {
                    continue;
                }

                if (ce.getRange() == null) {
                    System.err.println("RANGE NULL for: " + ce);
                    continue;
                }

                if (ce.getRange().contains(comment.getRange())) {
                    continue;
                }

                if (comment.getRange().getEnd().getCharIndex()
                        < ce.getRange().getBegin().getCharIndex()) {

//                    String commentString = comment.getComment();
//                    System.out.println("commentstr: " + commentString);
//                    cb.append(commentString).newLine();
//                    System.out.println("code: " + cb.getCode());
                    commentRenderer.render(comment, cb);
                    rendered.add(comment);
                }

            }

            // custom render
            rel.render(ce);
        }

        // render comments after last code entity in scope
        for (Comment comment : e.getComments()) {
            if (!rendered.contains(comment)) {
                commentRenderer.render(comment, cb);
                rendered.add(comment);
            }
        }
    }
}

class InvocationCodeRenderer implements CodeRenderer<Invocation> {

    /**
     * chained method map. values are inv-object providers, keys are invocations
     * that are called on return values of other invocations.
     */
    private Map<Invocation, Invocation> chainedMethods = new HashMap<>();

    public InvocationCodeRenderer() {
    }

    @Override
    public String render(Invocation e) {
        CodeBuilder cb = new CodeBuilder();
        render(e, cb);
        return cb.getCode();
    }

    @Override
    public void render(Invocation i, CodeBuilder cb) {
        render(i, cb, false);
    }

    private void renderOperator(Operator operator, CodeBuilder cb) {
        switch (operator) {
            case PLUS:
                cb.append("+");
                break;
            case MINUS:
                cb.append("-");
                break;
            case TIMES:
                cb.append("*");
                break;
            case DIV:
                cb.append("/");
                break;
            case ASSIGN:
                cb.append("=");
                break;
            case INC_ONE:
                cb.append("++");
                break;
            case DEC_ONE:
                cb.append("--");
                break;
            case PLUS_ASSIGN:
                cb.append("+=");
                break;
            case MINUS_ASSIGN:
                cb.append("-=");
                break;
            case TIMES_ASSIGN:
                cb.append("*=");
                break;
            case DIV_ASSIGN:
                cb.append("/=");
                break;
            case EQUALS:
                cb.append("==");
                break;
            case NOT_EQUALS:
                cb.append("!=");
                break;
            case LESS:
                cb.append("<");
                break;
            case LESS_EQUALS:
                cb.append("<=");
                break;
            case GREATER:
                cb.append(">");
                break;
            case GREATER_EQUALS:
                cb.append(">=");
                break;
            case AND:
                cb.append("&&");
                break;
            case OR:
                cb.append("||");
                break;
            case ACCESS_ARRAY_ELEMENT:
                cb.append("[");
                break;
            default:
                cb.append("/*operator type not implemented*/");
        }
    }

    private void render(Invocation i, CodeBuilder cb, boolean inParam) {

        boolean isUsedAsInput = i.getParent().getControlFlow().isUsedAsInput(i);
        boolean retValOfInvIsUsedAsInvObj
                = getNextInvocationWithRetValOf(i).isPresent();
        boolean newLine = !inParam;

        if (!inParam && (isUsedAsInput || retValOfInvIsUsedAsInvObj)) {
            return;
        }

        if (inParam) {
            ObjectProvider objP = i.getObjectProvider();
 
            if(objP.getInvocation().isPresent()) {
                render(objP.getInvocation().get(),cb, true);
            }
        }

        if (i.isConstructor()) {
            cb.
                    append("new ").append(
                            renderObjProvider(i.getObjectProvider())).
                    append("(");
            renderArguments(i, cb);
            cb.append(")");

        } else if (i instanceof DeclarationInvocation) {
            DeclarationInvocation decl = (DeclarationInvocation) i;
            cb.append(decl.getDeclaredVariable().getType().getClassNameAsCode()).append(" ").
                    append(decl.getDeclaredVariable().getName());
        } else if (i instanceof BinaryOperatorInvocation) {
            BinaryOperatorInvocation operatorInvocation = (BinaryOperatorInvocation) i;

            boolean letArgNeedsParantheses
                    = operatorInvocation.getLeftArgument().getArgType()
                    == ArgumentType.INVOCATION;
            boolean rightArgNeedsParantheses
                    = operatorInvocation.getRightArgument().getArgType()
                    == ArgumentType.INVOCATION;

            // no parantheses around not operator
            if (letArgNeedsParantheses) {
                letArgNeedsParantheses = !(operatorInvocation.getLeftArgument()
                        .getInvocation().get() instanceof NotInvocation);
            }
            // no parantheses around not operator
            if (rightArgNeedsParantheses) {
                rightArgNeedsParantheses = !(operatorInvocation.getRightArgument()
                        .getInvocation().get() instanceof NotInvocation);
            }

            if (letArgNeedsParantheses) {
                cb.append("(");
            }
            renderArgument(operatorInvocation.getLeftArgument(), cb);
            if (letArgNeedsParantheses) {
                cb.append(")");
            }
            if (!operatorInvocation.isArrayAccessOperator()) {
                cb.append(" ");
            }
            renderOperator(operatorInvocation.getOperator(), cb);
            if (!operatorInvocation.isArrayAccessOperator()) {
                cb.append(" ");
            }
            if (rightArgNeedsParantheses) {
                cb.append("(");
            }
            renderArgument(operatorInvocation.getRightArgument(), cb);
            if (rightArgNeedsParantheses) {
                cb.append(")");
            }

            if (operatorInvocation.isArrayAccessOperator()) {
                cb.append("]");
            }

        } else if (i instanceof ReturnStatementInvocation) {
            ReturnStatementInvocation retInvocation = (ReturnStatementInvocation) i;
            cb.append("return ");
            renderArgument(retInvocation.getArgument(), cb);
        } else if (i instanceof BreakInvocation) {
            BreakInvocation breakInvocation = (BreakInvocation) i;
            cb.append("break");
        } else if (i instanceof ContinueInvocation) {
            ContinueInvocation continueInvocation = (ContinueInvocation) i;
            cb.append("continue");
        } else if (i instanceof NotInvocation) {
            NotInvocation notInvocation = (NotInvocation) i;
            boolean argNeedsParantheses
                    = notInvocation.getArgument().getArgType()
                    == ArgumentType.INVOCATION;
            cb.append("!");
            if (argNeedsParantheses) {
                cb.append("(");
            }
            renderArgument(notInvocation.getArgument(), cb);
            if (argNeedsParantheses) {
                cb.append(")");
            }
        } else if (!i.isScope()) {

            boolean isThis = i.getObjectProvider().getVariableName().
                    map(vName -> vName.equals("this")).orElse(false);

            if (!isThis
                    && !ScopeUtils.callingObjectIsEnclosingClass(i)) {
                cb.
                        append(renderObjProvider(i.getObjectProvider())).
                        append(".");
            }

            cb.append(i.getMethodName()).append("(");
            renderArguments(i, cb);
            cb.append(")");
        } else {

            ScopeInvocation si = (ScopeInvocation) i;
            Scope s = si.getScope();

            if (s instanceof SimpleForDeclaration) {
                SimpleForDeclaration forD = (SimpleForDeclaration) s;
                cb.append("for(").append("int ").append(forD.getVarName()).
                        append(" = " + forD.getFrom()).
                        append("; ").append(forD.getVarName());

                if (forD.getInc() > 0) {
                    cb.append(" <= " + forD.getTo());
                } else {
                    cb.append(" >= " + forD.getTo());
                }

                cb.append("; ");

                cb.append(forD.getVarName());

                if (forD.getInc() == 1) {
                    cb.append("++");
                } else if (forD.getInc() == -1) {
                    cb.append("--");
                } else if (forD.getInc() < 0) {
                    cb.append("-=" + Math.abs(forD.getInc()));
                } else {
                    cb.append("+=" + forD.getInc());
                }

                cb.append(") {");

                if (!s.getControlFlow().getInvocations().isEmpty()) {
                    cb.newLine();
                    cb.incIndentation();
                }

                Utils.renderComments(Scope2Code.renderedComments,
                        forD.getControlFlow().getInvocations(),
                        forD, cb, (CodeEntity ce) -> {

                            if (ce instanceof Invocation
                            && ce.isTextRenderingEnabled()) {

                                render((Invocation) ce, cb);
                            }
                        });

                if (!s.getControlFlow().getInvocations().isEmpty()) {
                    cb.decIndentation();
                }

                cb.append("}");

            } else if (s instanceof WhileDeclaration) {
                WhileDeclaration whileD = (WhileDeclaration) s;
                cb.append("while(");
                renderArgument(whileD.getCheck(), cb);
                cb.append(") {");

                if (!s.getControlFlow().getInvocations().isEmpty()) {
                    cb.newLine();
                    cb.incIndentation();
                }

                Utils.renderComments(Scope2Code.renderedComments,
                        whileD.getControlFlow().getInvocations(),
                        whileD, cb, (CodeEntity ce) -> {

                            if (ce instanceof Invocation) {
                                render((Invocation) ce, cb);
                            }
                        });

                if (!s.getControlFlow().getInvocations().isEmpty()) {
                    cb.decIndentation();
                }

                cb.append("}");
            } else if (s instanceof IfDeclaration) {
                IfDeclaration ifD = (IfDeclaration) s;

                if (ifD instanceof ElseIfDeclaration) {
                    cb.append("else if (");
                } else {
                    cb.append("if (");
                }

                renderArgument(ifD.getCheck(), cb);
                cb.append(") {");

                if (!s.getControlFlow().getInvocations().isEmpty()) {
                    cb.newLine();
                    cb.incIndentation();
                }

                Utils.renderComments(Scope2Code.renderedComments,
                        ifD.getControlFlow().getInvocations(),
                        ifD, cb, (CodeEntity ce) -> {

                            if (ce instanceof Invocation) {
                                render((Invocation) ce, cb);
                            }
                        });

                if (!s.getControlFlow().getInvocations().isEmpty()) {
                    cb.decIndentation();
                }

                cb.append("}");
            } else if (s instanceof ElseDeclaration) {
                ElseDeclaration elseD = (ElseDeclaration) s;
                cb.append("else {");

                if (!s.getControlFlow().getInvocations().isEmpty()) {
                    cb.newLine();
                    cb.incIndentation();
                }

                Utils.renderComments(Scope2Code.renderedComments,
                        elseD.getControlFlow().getInvocations(),
                        elseD, cb, (CodeEntity ce) -> {

                            if (ce instanceof Invocation) {
                                render((Invocation) ce, cb);
                            }
                        });

                if (!s.getControlFlow().getInvocations().isEmpty()) {
                    cb.decIndentation();
                }

                cb.append("}");
            } else {
                cb.append("/*unsupported invocation*/");
            }

        }

        // look ahead
        boolean currentInvIsChainedWithNext
                = getNextInvocationWithRetValOf(i).isPresent();

        if (!currentInvIsChainedWithNext) {
            if (!inParam && !i.isScope()) {
                cb.append(";");
            }

            if (newLine) {
                cb.newLine();
            }
        }
    }

    private static Optional<Invocation> getNextInvocationWithRetValOf(Invocation inv) {

        ControlFlow cf = inv.getParent().getControlFlow();

//        if (inv)
//
//        // look ahead
//        int indexOfCurrentInvocation = cf.getInvocations().indexOf(inv);
//
//        List<Invocation> invocations = cf.getInvocations();
//
//        for (int i = indexOfCurrentInvocation + 1; i < invocations.size(); i++) {
//
//            Invocation potentialInv = invocations.get(i);
//
//            // TODO 01.08.2015 improve check (objName/varName is not sufficient)
//            boolean currentInvIsChainedWithNext
//                    = potentialInv.getVariableName() != null
//                    && potentialInv.getVariableName().isEmpty();
//
//            if (currentInvIsChainedWithNext) {
//                return Optional.of(potentialInv);
//            }
//
//            if (!cf.isUsedAsInput(potentialInv)) {
//                break;
//            }
//        }
//
//        return Optional.empty();
        return cf.getInvocations().stream().
                filter(i -> i.getObjectProvider().getInvocation().isPresent()).
                filter(i -> Objects.equals(i.getObjectProvider().
                                getInvocation().orElse(null), inv)).
                findAny();
    }

    private void renderArguments(Invocation e, CodeBuilder cb) {
        boolean firstCall = true;
        for (Argument a : e.getArguments()) {

            if (firstCall) {
                firstCall = false;
            } else {
                cb.append(", ");
            }

            renderArgument(a, cb);
        }
    }

    private void renderArgument(Argument arg, CodeBuilder cb) {

        if (arg.getArgType() == ArgumentType.CONSTANT) {

            String constString = null;

            if (arg.getType().equals(Type.STRING)) {
                constString = "\""
                        + VLangUtils.addEscapeCharsToCode(arg.getConstant().get().
                                toString()) + "\"";
            } else {
                constString = arg.getConstant().get().toString();
            }

            cb.append(constString);

        } else if (arg.getArgType() == ArgumentType.INVOCATION) {
            render(arg.getInvocation().get(), cb, true);
        } else if (arg.getArgType() == ArgumentType.VARIABLE) {
            cb.append(arg.getVariable().get().getName());
        } else if (arg.getArgType() == ArgumentType.NULL) {
            cb.append("null");
        }
    }

    private String renderObjProvider(ObjectProvider objProvider) {
        if (objProvider.getVariableName().isPresent()) {
            return objProvider.getVariableName().get();
        } else if (objProvider.getClassObject().isPresent()) {
            return objProvider.getClassObject().get().getFullClassName();
        } else if (objProvider.getInvocation().isPresent()) {
            return "";
        }

        return "/*UNSUPPORTED OBJ PROVIDER*/";
    }
}

class MethodDeclarationRenderer implements CodeRenderer<MethodDeclaration> {

    private CodeRenderer<Invocation> invocationRenderer;

    public MethodDeclarationRenderer(CodeRenderer<Invocation> invocationRenderer) {
        this.invocationRenderer = invocationRenderer;
    }

    @Override
    public String render(MethodDeclaration e) {
        CodeBuilder cb = new CodeBuilder();

        render(e, cb);

        return cb.toString();
    }

    @Override
    public void render(MethodDeclaration e, CodeBuilder cb) {

        createModifiers(e, cb);
        cb.append(e.getReturnType().getClassNameAsCode());
        cb.append(" ").append(e.getName()).append("(");
        renderParams(e, cb);
        cb.append(") {").newLine();
        cb.incIndentation();

//        for (Invocation i : e.getControlFlow().getInvocations()) {
//            System.out.println(" --> inv: " + i);
//            invocationRenderer.render(i, cb);
//        }
//        
        Utils.renderComments(
                Scope2Code.renderedComments,
                e.getControlFlow().getInvocations(), e, cb, (CodeEntity ce) -> {

                    if (ce instanceof Invocation && ce.isTextRenderingEnabled()) {
                        invocationRenderer.render((Invocation) ce, cb);
                    }
                });

        cb.decIndentation().append("}").newLine();
    }

    private void createModifiers(MethodDeclaration md, CodeBuilder cb) {
        for (Modifier m : md.getModifiers().getModifiers()) {
            cb.append(Utils.modifierToName(m)).append(" ");
        }
    }

    private void renderParams(MethodDeclaration e, CodeBuilder cb) {
        boolean firstCall = true;
        for (IParameter v : e.getParameters().getParamenters()) {

            if (firstCall) {
                firstCall = false;
            } else {
                cb.append(", ");
            }

            cb.append(v.getType().getClassNameAsCode()).append(" ").
                    append(v.getName());

//            if (v.getType().getPackageName().equals("java.lang")) {
//                cb.append(v.getType().getShortName()).append(" ").
//                        append(v.getName());
//            } else {
//                cb.append(v.getType().getFullClassNameAsCode()).append(" ").
//                        append(v.getName());
//            }
        }
    }
}

class ClassDeclarationRenderer implements CodeRenderer<ClassDeclaration> {

    private CodeRenderer<MethodDeclaration> methodDeclarationRenderer;

    public ClassDeclarationRenderer(
            CodeRenderer<MethodDeclaration> methodDeclarationRenderer) {
        this.methodDeclarationRenderer = methodDeclarationRenderer;
    }

    @Override
    public String render(ClassDeclaration cd) {
        CodeBuilder cb = new CodeBuilder();

        render(cd, cb);

        return cb.getCode();
    }

    @Override
    public void render(ClassDeclaration cd, CodeBuilder cb) {

//        cb.append("@eu.mihosoft.vrl.instrumentation.VRLVisualization").
//                newLine();
        createModifiers(cd, cb);
        cb.append("class ");
        cb.append(new Type(cd.getName()).getShortName());
        createExtends(cd, cb);
        createImplements(cd, cb);
        cb.append(" {").newLine().newLine();
        cb.incIndentation();

        createDeclaredVariables(cd, cb);

        Utils.renderComments(Scope2Code.renderedComments,
                cd.getDeclaredMethods(), cd, cb, (CodeEntity ce) -> {

                    if (ce instanceof MethodDeclaration) {
                        methodDeclarationRenderer.render((MethodDeclaration) ce, cb);
                    }
                });

        cb.decIndentation();
        cb.append("}").newLine();
    }

    private void createDeclaredVariables(ClassDeclaration cd, CodeBuilder cb) {
        for (Variable v : cd.getVariables()) {
            if (!"this".equals(v.getName())) {
                createModifiers(v, cb);
                cb.append(v.getType().getClassNameAsCode()).
                        append(" ").append(v.getName()).append(";").newLine();
            }
        }
        cb.newLine();
    }

    private void createModifiers(ClassDeclaration cd, CodeBuilder cb) {
        for (Modifier m : cd.getClassModifiers().getModifiers()) {
            cb.append(Utils.modifierToName(m)).append(" ");
        }
    }

    private void createModifiers(Variable v, CodeBuilder cb) {

        if (!v.isField()) {
            return;
        }

        for (Modifier m : v.getModifiers().get().getModifiers()) {
            cb.append(Utils.modifierToName(m)).append(" ");
        }
    }

    private void createExtends(ClassDeclaration cd, CodeBuilder cb) {

        if (cd.getExtends().getTypes().isEmpty()) {
            return;
        }

        boolean first = true;

        for (IType type : cd.getExtends().getTypes()) {

            if (type.getClassNameAsCode().equals("Object")) {
                continue;
            }

            if (first) {
                first = false;
                cb.append(" extends ");
            } else {
                cb.append(", ");
            }
            cb.append(type.getClassNameAsCode());
        }
    }

    private void createImplements(ClassDeclaration cd, CodeBuilder cb) {

        if (cd.getImplements().getTypes().isEmpty()) {
            return;
        }

        cb.append(" implements ");

        boolean first = true;

        for (IType type : cd.getImplements().getTypes()) {
            if (first) {
                first = false;
            } else {
                cb.append(", ");
            }
            cb.append(type.getClassNameAsCode());
        }
    }
}

class CompilationUnitRenderer implements
        CodeRenderer<CompilationUnitDeclaration> {

    private CodeRenderer<ClassDeclaration> classDeclarationRenderer;

    public CompilationUnitRenderer() {
    }

    public CompilationUnitRenderer(
            CodeRenderer<ClassDeclaration> classDeclarationRenderer) {
        this.classDeclarationRenderer = classDeclarationRenderer;
    }

    @Override
    public String render(CompilationUnitDeclaration e) {
        CodeBuilder cb = new CodeBuilder();

        render(e, cb);

        return cb.getCode();
    }

    @Override
    public void render(CompilationUnitDeclaration e, CodeBuilder cb) {

        if (e.getPackageName() != null && !e.getPackageName().isEmpty()) {
            cb.append("package ").append(e.getPackageName()).append(";").
                    newLine();
        }

        List<? extends ImportDeclaration> imports = e.getImports();

        List<? extends CodeEntity> entities = e.getDeclaredClasses();

        Utils.renderComments(Scope2Code.renderedComments, entities, e, cb, (CodeEntity ce) -> {

            if (ce instanceof ClassDeclaration) {
                classDeclarationRenderer.render((ClassDeclaration) ce, cb);
            }
        });

        CommentRenderer cR = new CommentRenderer();

        for (Comment c : e.getComments()) {
            if (Scope2Code.renderedComments.contains(c)) {
                continue;
            }
            cR.render(c, cb);
            Scope2Code.renderedComments.add(c);
        }

    }

    /**
     * @return the classDeclarationRenderer
     */
    public CodeRenderer<ClassDeclaration> getClassDeclarationRenderer() {
        return classDeclarationRenderer;
    }

    /**
     * @param classDeclarationRenderer the classDeclarationRenderer to set
     */
    public void setClassDeclarationRenderer(
            CodeRenderer<ClassDeclaration> classDeclarationRenderer) {
        this.classDeclarationRenderer = classDeclarationRenderer;
    }

}

interface RenderEventListener {

    public void render(CodeEntity ce);
}

class CommentRenderer implements CodeRenderer<Comment> {

    @Override
    public String render(Comment e) {
        CodeBuilder cb = new CodeBuilder();
        render(e, cb);

        return cb.toString();
    }

    @Override
    public void render(Comment e, CodeBuilder cb) {

        String[] lines = e.getComment().split("\n");
        for (String l : lines) {
            cb.addLine(l.trim());
        }
    }

}

//class ScopeCodeRendererImpl implements CodeRenderer<Scope> {
//
//    private static final STGroup group = new STGroupString(
//            "class(accessModifier, name, superclass, methods)::=<<\n"
//            + "\n"
//            + "$accessModifier$ class $name$ extends $superclass$ {\n"
//            + "\n"
//            + "    $methods:method(); separator=\"\\n\"$\n"
//            + "\n"
//            + "}\n"
//            + ">>\n"
//            + "\n"
//            + "method(method)::=<<\n"
//            + "/**\n"
//            + " $method.comments$\n"
//            + "*/\n"
//            + "$method.accessModifier$ $method.returnType.name$ $name$ ($method.arguments:argument(); separator=\",\"$) {\n"
//            + "    $method.body$\n"
//            + "}\n"
//            + ">>\n"
//            + "\n"
//            + "argument(argument)::=<<\n"
//            + "$argument.type.name$ $argument.name$\n"
//            + ">>"
//    );
//
//    private static final ST invocation = group.getInstanceOf("invocation");
//
//    @Override
//    public String render(Scope e) {
//        return "";
//    }
//
//    public int var;
//
//}
