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
import com.google.common.io.Files;
import eu.mihosoft.vrl.lang.VLangUtils;
import groovy.lang.GroovyClassLoader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public static void main(String[] args) {
        CompilationUnitDeclaration scope = demoScope();

        CompilationUnitRenderer renderer
                = new CompilationUnitRenderer(
                        new ClassDeclarationRenderer(
                                new MethodDeclarationRenderer(
                                        new InvocationCodeRenderer())));

        System.out.println(renderer.render(scope));

        UIBinding.scopes.clear();

        String theCode = renderer.render(scope);

        try {
            Files.write(theCode,
                    new File("theCode.groovy"), Charset.forName("UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(Scope2Code.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        System.out.println("processing code: ");

        GroovyClassLoader gcl = new GroovyClassLoader();
        gcl.parseClass(theCode);

        if (UIBinding.scopes == null) {
            System.err.println("NO SCOPES");
            return;
        }

        String code = "";

        for (Collection<Scope> scopeList : UIBinding.scopes.values()) {
            for (Scope s : scopeList) {
                if (s instanceof CompilationUnitDeclaration) {
                    code = renderer.render((CompilationUnitDeclaration) s);
                }
            }
        }

        System.out.println("code from compiler 1:\n" + code);

        UIBinding.scopes.clear();

        gcl = new GroovyClassLoader();
        gcl.parseClass(code);

        for (Collection<Scope> scopeList : UIBinding.scopes.values()) {
            for (Scope s : scopeList) {
                if (s instanceof CompilationUnitDeclaration) {
                    code = renderer.render((CompilationUnitDeclaration) s);
                }
            }
        }

        System.out.println("code from compiler 2:\n" + code);

        scope.generateDataFlow();
    }

    public static CompilationUnitDeclaration demoScope() {
        VisualCodeBuilder builder = new VisualCodeBuilder_Impl();

        CompilationUnitDeclaration myFile = builder.declareCompilationUnit(
                "MyFile.java", "my.testpackage");
        ClassDeclaration myFileClass = builder.declareClass(myFile,
                new Type("my.testpackage.MyFileClass"),
                new Modifiers(Modifier.PUBLIC), new Extends(), new Extends());

        builder.declareVariable(myFileClass, new Type("int"), "value1");

        MethodDeclaration m1 = builder.declareMethod(myFileClass,
                new Modifiers(Modifier.PUBLIC), new Type("int"), "m1",
                new Parameters(new Parameter(new Type("int"), "v1")));

        builder.invokeMethod(m1, "this", m1, Argument.varArg(m1.getVariable("v1")));
        builder.invokeMethod(m1, "this", m1, Argument.varArg(m1.getVariable("v1")));

        MethodDeclaration m2 = builder.declareMethod(myFileClass,
                new Modifiers(Modifier.PUBLIC), new Type("int"), "m2",
                new Parameters(new Parameter(new Type("int"), "v1"),
                        new Parameter(
                                new Type("my.testpackage.MyFileClass"), "v2")));

        builder.invokeMethod(
                m2, "this", m2, Argument.varArg(m2.getVariable("v1")),
                Argument.varArg(m2.getVariable("v2")));

        builder.invokeMethod(
                m2, "this", m2,
                Argument.invArg(builder.invokeMethod(
                                m2, "this", m1,
                                Argument.varArg(m2.getVariable("v1")))),
                Argument.varArg(m2.getVariable("v2")));

        ForDeclaration forD1 = builder.declareFor(m2, "i", 1, 3, 1);
        ForDeclaration forD2 = builder.declareFor(forD1, "j", 10, 9, -1);

        builder.invokeMethod(forD2, "this", m1, Argument.varArg(forD2.getVariable("j")));

//        Variable var = forD2.createVariable(new Type("java.lang.String"));
//        forD2.assignConstant(var.getName(), "Hello!\"");
        builder.invokeStaticMethod(
                forD2, new Type("System"), "out.println",
                Type.VOID, true, Argument.constArg(Type.STRING, "Hello"));

//        builder.callMethod(forD2, "this", m2.getName(), true,
//                "retM2", forD2.getVariable("v1"), m2.getVariable("v2"));
//        builder.callMethod(forD2, "this", m1.getName(), true, "retM1b", m1.getVariable("v1"));
        return myFile;
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
            case AND:
                cb.append("&&");
                break;
            case OR:
                cb.append("||");
                break;
            default:
                cb.append("/*operator type not implemented*/");
        }
    }

    private void render(Invocation i, CodeBuilder cb, boolean inParam) {

        boolean isUsedAsInput = i.getParent().getControlFlow().isUsedAsInput(i);
        boolean newLine = !inParam;

        if (!inParam && isUsedAsInput) {
            return;
        }

        if (i.isConstructor()) {
            cb.
                    append("new ").append(i.getVariableName()).
                    append("(");
            renderArguments(i, cb);
            cb.append(");");

        } else if (i instanceof DeclarationInvocation) {
            DeclarationInvocation decl = (DeclarationInvocation) i;
            cb.append(decl.getDeclaredVariable().getType().getFullClassName().
                    replace("java.lang.", "")).append(" ").
                    append(decl.getDeclaredVariable().getName()).append(";");
        } else if (i instanceof BinaryOperatorInvocation) {
            BinaryOperatorInvocation operatorInvocation = (BinaryOperatorInvocation) i;

            renderArgument(operatorInvocation.getLeftArgument(), cb);
            renderOperator(operatorInvocation.getOperator(), cb);
            renderArgument(operatorInvocation.getRightArgument(), cb);

            if (!inParam) {
                if (!inParam) {
                    cb.append(";");
                }
            }

        } else if (!i.isScope()) {

            if (!i.getVariableName().equals("this")) {
                cb.
                        append(i.getVariableName()).
                        append(".");
            }
            cb.append(i.getMethodName()).append("(");
            renderArguments(i, cb);
            cb.append(")");
            if (!inParam) {
                cb.append(";");
            }
        } else {

            ScopeInvocation si = (ScopeInvocation) i;
            Scope s = si.getScope();

            if (s instanceof ForDeclaration) {
                ForDeclaration forD = (ForDeclaration) s;
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

                            if (ce instanceof Invocation) {
                                render((Invocation) ce, cb);
                            }
                        });

                if (!s.getControlFlow().getInvocations().isEmpty()) {
                    cb.decIndentation();
                }

                cb.append("}");

            }
//            else if (s instanceof WhileDeclaration) {
//                WhileDeclaration whileD = (WhileDeclaration) s;
//            }
//            else if (s instanceof If) {
//                ForDeclaration forD = (ForDeclaration) s;
//            }
        }

        if (newLine) {
            cb.newLine();
        }
    }

    private void renderArguments(Invocation e, CodeBuilder cb) {
        boolean firstCall = true;
        for (IArgument a : e.getArguments()) {

            if (firstCall) {
                firstCall = false;
            } else {
                cb.append(", ");
            }

            renderArgument(a, cb);
        }
    }

    private void renderArgument(IArgument arg, CodeBuilder cb) {

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
        cb.append(e.getReturnType().getFullClassName());
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

                    if (ce instanceof Invocation) {
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
            if (v.getType().getPackageName().equals("java.lang")) {
                cb.append(v.getType().getShortName()).append(" ").
                        append(v.getName());
            } else {
                cb.append(v.getType().getFullClassName()).append(" ").
                        append(v.getName());
            }
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

        cb.append("@eu.mihosoft.vrl.instrumentation.VRLVisualization").
                newLine();

        createModifiers(cd, cb);
        cb.append("class ");
        cb.append(new Type(cd.getName()).getShortName());
        createExtends(cd, cb);
        createImplements(cd, cb);
        cb.append(" {").newLine();
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
                cb.newLine().append(v.getType().getFullClassName()).
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

    private void createExtends(ClassDeclaration cd, CodeBuilder cb) {

        if (cd.getExtends().getTypes().isEmpty()) {
            return;
        }

        boolean first = true;

        for (IType type : cd.getExtends().getTypes()) {

            if (type.getFullClassName().equals("java.lang.Object")) {
                continue;
            }

            if (first) {
                first = false;
                cb.append(" extends ");
            } else {
                cb.append(", ");
            }
            cb.append(type.getFullClassName());
        }
    }

    private void createImplements(ClassDeclaration cd, CodeBuilder cb) {

        if (cd.getImplements().getTypes().isEmpty()) {
            return;
        }

        cb.append(" implements ");

        boolean first = true;

        for (IType type : cd.getExtends().getTypes()) {
            if (first) {
                first = false;
            } else {
                cb.append(", ");
            }
            cb.append(type.getFullClassName());
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
