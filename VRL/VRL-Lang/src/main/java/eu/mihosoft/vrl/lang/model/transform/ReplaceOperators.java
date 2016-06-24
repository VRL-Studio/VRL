/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.transform;

import eu.mihosoft.vrl.instrumentation.VRLVisualizationTransformation;
import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.BinaryOperatorInvocation;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.ControlFlow;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.Modifier;
import eu.mihosoft.vrl.lang.model.Modifiers;
import eu.mihosoft.vrl.lang.model.ObjectProvider;
import eu.mihosoft.vrl.lang.model.Operator;
import eu.mihosoft.vrl.lang.model.Parameter;
import eu.mihosoft.vrl.lang.model.Parameters;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.lang.model.UIBinding;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder_Impl;
import groovy.lang.GroovyClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ReplaceOperators implements CodeTransform<CompilationUnitDeclaration> {

    @Override
    public CompilationUnitDeclaration transform(CompilationUnitDeclaration cu) {
        // TODO 01.08.2015 add clone()
        CompilationUnitDeclaration result = cu;

        MethodDeclaration addMethod = createOperatorUtility(cu);

        ReplaceOp op = new ReplaceOp(addMethod, Operator.PLUS);
        ReplaceOp op2 = new ReplaceOp(addMethod, Operator.TIMES);

        for (ClassDeclaration cd : result.getDeclaredClasses()) {
            if (cd.getClassType().getShortName().equals("OpUtil")) {
                continue;
            }

            List<MethodDeclaration> methods = new ArrayList<>();
            for (MethodDeclaration md : cd.getDeclaredMethods()) {
                methods.add((MethodDeclaration) op.transform(md));
                methods.add((MethodDeclaration) op2.transform(md));
            }
            cd.getDeclaredMethods().clear();
            cd.getDeclaredMethods().addAll(methods);
        }

        return result;
    }

    private MethodDeclaration createOperatorUtility(CompilationUnitDeclaration cu) {
        VisualCodeBuilder codeBuilder = new VisualCodeBuilder_Impl();

        ClassDeclaration utilClass
                = codeBuilder.declareClass(cu, new Type("OpUtil"));

        MethodDeclaration addMethod = codeBuilder.declareMethod(
                utilClass,
                new Modifiers(Modifier.STATIC),
                Type.INT,
                "add",
                new Parameters(
                        new Parameter(Type.INT, "a"),
                        new Parameter(Type.INT, "b")
                )
        );

        codeBuilder.invokeMethod(addMethod,
                ObjectProvider.fromClassObject(new Type("System")),
                "out.println", Type.VOID,
                Argument.constArg(Type.STRING, "OP ADD"));

        Invocation addInv = codeBuilder.invokeOperator(addMethod,
                Argument.varArg(
                        addMethod.getVariable("a")),
                Argument.varArg(
                        addMethod.getVariable("b")),
                Operator.PLUS);

        codeBuilder.returnValue(addMethod, Argument.invArg(addInv));

        return addMethod;
    }

    public static void main(String[] args) {
        // clear model
        UIBinding.scopes.clear();

        // configure groovy compiler with model importer (groovy ast -> model)
        CompilerConfiguration ccfg = new CompilerConfiguration();
        ccfg.addCompilationCustomizers(new ASTTransformationCustomizer(
                new VRLVisualizationTransformation()));
        GroovyClassLoader gcl = new GroovyClassLoader(
                new GroovyClassLoader(), ccfg);

        // code to compile
        String code = ""
                + "package mypackage\n"
                + "\n"
                + "public class MyClass {\n"
                + "  public method() {\n"
                + "    int a = 2 + 3 * (4 + 2)\n"
                + "    int b = a + 7\n"
                + "  }\n"
                + "}\n";
        
        System.out.println(code);

        // compile the code and execute model importer
        try {
            gcl.parseClass(code, "Script");
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        // obtain compilation unit (e.g., .groovy file)
        CompilationUnitDeclaration cud
                = (CompilationUnitDeclaration) UIBinding.scopes.values().
                iterator().next().get(0);

        // apply transformation
        cud = new ReplaceOperators().transform(cud);

        // model -> code
        String newCode = Scope2Code.getCode(cud);
        System.out.println(newCode);
    }

}

class ReplaceOp implements CodeTransform<ControlFlowScope> {

    // method that shall replace the specified operator
    private final MethodDeclaration operatorMethod;
    // operator that shall be replaced
    private final Operator op;

    public ReplaceOp(MethodDeclaration operatorMethod, Operator op) {
        this.operatorMethod = operatorMethod;
        this.op = op;
    }

    @Override
    public ControlFlowScope transform(ControlFlowScope ce) {

        // obtain cotrol flow from specified scope
        ControlFlow cf = ce.getControlFlow();

        // create new code builder
        VisualCodeBuilder codeBuilder = new VisualCodeBuilder_Impl();

        List<Invocation> prevInvocations
                = new ArrayList<>(cf.getInvocations());
        List<Invocation> newInvocations = new ArrayList<>();

        // for each invocation check whether it has to be replaced with
        // invocations of the specified method declaration
        for (Invocation inv : prevInvocations) {

            // skip normal method calls 
            if (!(inv instanceof BinaryOperatorInvocation)) {
                newInvocations.add(inv);
                continue;
            }

            // skip operator calls that are not of the specified operator type
            BinaryOperatorInvocation boi = (BinaryOperatorInvocation) inv;
            if (boi.getOperator() != op) {
                newInvocations.add(inv);
                continue;
            }

            // for operators of the specified type create a replacement
            // invocation
            Invocation newInv = codeBuilder.invokeMethod(
                    ce, ObjectProvider.fromClassObject(
                            operatorMethod.getClassDeclaration().
                            getClassType()),
                    operatorMethod,
                    boi.getLeftArgument(),
                    boi.getRightArgument());

            newInvocations.add(newInv);

            // replace arguments of receiver methods, if present
            Optional<Invocation> receiver = cf.returnInvTargetIfPresent(inv);
            if (receiver.isPresent()) {
                // search argument indices
                int[] argumentsToReplace = receiver.get().
                        getArguments().stream().
                        filter(a -> Objects.equals(a.getInvocation().
                                orElse(null), inv)).
                        mapToInt(a -> receiver.get().
                                getArguments().indexOf(a)).toArray();
                // replace args
                for (int aIndex : argumentsToReplace) {
                    receiver.get().getArguments().set(aIndex,
                            Argument.invArg(newInv));
                }
            }
        }

        // replace the invocations in the control-flow with the modified
        // invocations
        cf.getInvocations().clear();
        cf.getInvocations().addAll(newInvocations);

        return ce;
    }
}

