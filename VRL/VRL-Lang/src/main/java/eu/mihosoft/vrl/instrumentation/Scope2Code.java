/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

//import org.stringtemplate.v4.ST;
import eu.mihosoft.vrl.lang.VLangUtils;
import groovy.lang.GroovyClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

//import org.stringtemplate.v4.STGroup;
//import org.stringtemplate.v4.STGroupString;
/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Scope2Code {

    public static String getCode(CompilationUnitDeclaration scope) {

        CompilationUnitRenderer renderer
                = new CompilationUnitRenderer(
                        new ClassDeclarationRenderer(
                                new MethodDeclarationRenderer(
                                        new InvocationCodeRenderer())));

        return renderer.render(scope);
    }

    public static String getCode(ClassDeclaration scope) {

        ClassDeclarationRenderer renderer
                = new ClassDeclarationRenderer(
                        new MethodDeclarationRenderer(
                                new InvocationCodeRenderer()));

        return renderer.render(scope);
    }

    public static String getCode(MethodDeclaration scope) {

        MethodDeclarationRenderer renderer
                = new MethodDeclarationRenderer(
                        new InvocationCodeRenderer());

        return renderer.render(scope);
    }

    public static String getCode(Invocation invocation) {

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

        GroovyClassLoader gcl = new GroovyClassLoader();
        gcl.parseClass(renderer.render(scope));

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

        System.out.println("code from compiler:\n" + code);

        gcl = new GroovyClassLoader();
        gcl.parseClass(renderer.render(scope));

        for (Collection<Scope> scopeList : UIBinding.scopes.values()) {
            for (Scope s : scopeList) {
                if (s instanceof CompilationUnitDeclaration) {
                    code = renderer.render((CompilationUnitDeclaration) s);
                }
            }
        }

        System.out.println("code from compiler 2:\n" + code);

    }

    public static CompilationUnitDeclaration demoScope() {
        VisualCodeBuilder builder = new VisualCodeBuilder_Impl();

        CompilationUnitDeclaration myFile = builder.declareCompilationUnit(
                "MyFile.java", "my.testpackage");
        ClassDeclaration myFileClass = builder.declareClass(myFile,
                new Type("my.testpackage.MyFileClass"),
                new Modifiers(Modifier.PUBLIC), new Extends(), new Extends());

        builder.createVariable(myFileClass, new Type("int"), "value1");

        MethodDeclaration m1 = builder.declareMethod(myFileClass,
                new Modifiers(Modifier.PUBLIC), new Type("int"), "m1",
                new Parameters(new Parameter(new Type("int"), "v1")));

        builder.invokeMethod(m1, "this", m1.getName(), true, "retM1a", m1.getVariable("v1"));
        builder.invokeMethod(m1, "this", m1.getName(), true, "retM1b", m1.getVariable("v1"));

        MethodDeclaration m2 = builder.declareMethod(myFileClass,
                new Modifiers(Modifier.PUBLIC), new Type("int"), "m2",
                new Parameters(new Parameter(new Type("double"), "v1"),
                        new Parameter(new Type("my.testpackage.MyFileClass"), "v2")));

        builder.invokeMethod(m2, "this", m2.getName(), true,
                "retM2", m2.getVariable("v1"), m2.getVariable("v2"));

        ForDeclaration forD1 = builder.declareFor(m2, "i", 1, 3, 1);
        ForDeclaration forD2 = builder.declareFor(forD1, "j", 10, 9, -1);

        builder.invokeMethod(forD2, "this", m1.getName(), true, "retM1c", forD2.getVariable("j"));
        
        Variable var = forD2.createVariable(new Type("java.lang.String"));
        forD2.assignConstant(var.getName(), "Hello!\"");
        
        builder.invokeStaticMethod(forD2, new Type("System"), "out.println", true, "", var);

//        builder.invokeMethod(forD2, "this", m2.getName(), true,
//                "retM2", forD2.getVariable("v1"), m2.getVariable("v2"));
//        builder.invokeMethod(forD2, "this", m1.getName(), true, "retM1b", m1.getVariable("v1"));
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

        if (i.isConstructor()) {
            cb.append("new ").append(i.getReturnValueName()).
                    append("= new").append(i.getVariableName()).
                    append("(");
            renderParams(i, cb);
            cb.append(");");

        } else if (!i.isScope()) {
            cb.
                    append(i.getVariableName()).
                    append(".").
                    append(i.getMethodName()).append("(");
            renderParams(i, cb);

            cb.append(");");
        } else {

            ScopeInvocation si = (ScopeInvocation) i;
            Scope s = si.getScope();

            if (s instanceof ForDeclaration) {
                ForDeclaration forD = (ForDeclaration) s;
                cb.append("for(").append("int ").append(forD.getVarName()).
                        append(" = " + forD.getFrom()).append("; ").append(forD.getVarName());

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

                for (Invocation j : forD.getControlFlow().getInvocations()) {
                    render(j, cb);
                }

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

        cb.newLine();
    }

    private void renderParams(Invocation e, CodeBuilder cb) {
        boolean firstCall = true;
        for (Variable v : e.getArguments()) {

            if (firstCall) {
                firstCall = false;
            } else {
                cb.append(", ");
            }

            if (v.isConstant()) {
                
                String constString = null;
                
                if (v.getType().equals(new Type("java.lang.String"))) {
                    constString = "\""+VLangUtils.addEscapeCharsToCode(v.getValue().toString())+"\"";
                } else {
                    constString = v.getValue().toString();
                }
                
                cb.append(constString);
                
            } else {
                cb.append(v.getName());
            }
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

        for (Invocation i : e.getControlFlow().getInvocations()) {
            System.out.println(" --> inv: " + i);
            invocationRenderer.render(i, cb);
        }

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

            cb.append(v.getType().getFullClassName()).append(" ").append(v.getName());
        }
    }
}

class ClassDeclarationRenderer implements CodeRenderer<ClassDeclaration> {

    private CodeRenderer<MethodDeclaration> methodDeclarationRenderer;

    public ClassDeclarationRenderer(CodeRenderer<MethodDeclaration> methodDeclarationRenderer) {
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

        cb.append("@eu.mihosoft.vrl.instrumentation.VRLVisualization").newLine();
        createModifiers(cd, cb);
        cb.append("class ");
        cb.append(new Type(cd.getName()).getShortName());
        createExtends(cd, cb);
        createImplements(cd, cb);
        cb.append(" {").newLine();
        cb.incIndentation();

        createDeclaredVariables(cd, cb);

        for (MethodDeclaration md : cd.getDeclaredMethods()) {
            methodDeclarationRenderer.render(md, cb);
        }

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

class CompilationUnitRenderer implements CodeRenderer<CompilationUnitDeclaration> {

    private CodeRenderer<ClassDeclaration> classDeclarationRenderer;

    public CompilationUnitRenderer() {
    }

    public CompilationUnitRenderer(CodeRenderer<ClassDeclaration> classDeclarationRenderer) {
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

        if (e.getPackageName() != null || e.getPackageName().isEmpty()) {
            cb.append("package ").append(e.getPackageName()).append(";").
                    newLine().newLine();
        }

        for (ClassDeclaration cd : e.getDeclaredClasses()) {
            classDeclarationRenderer.render(cd, cb);
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
    public void setClassDeclarationRenderer(CodeRenderer<ClassDeclaration> classDeclarationRenderer) {
        this.classDeclarationRenderer = classDeclarationRenderer;
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
