/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.transform;

import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.ControlFlow;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.IArgument;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.Operator;
import eu.mihosoft.vrl.lang.model.ScopeInvocation;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.lang.model.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class InstrumentCode implements CodeTransform<CompilationUnitDeclaration> {

    @Override
    public CompilationUnitDeclaration transform(CompilationUnitDeclaration cu) {
        // TODO 01.08.2015 add clone()

        CompilationUnitDeclaration result = cu;

        InstrumentControlFlowScope im = new InstrumentControlFlowScope();

        for (ClassDeclaration cd : result.getDeclaredClasses()) {
            List<MethodDeclaration> methods = new ArrayList<>();
            for (MethodDeclaration md : cd.getDeclaredMethods()) {
                methods.add((MethodDeclaration) im.transform(md));
            }
            cd.getDeclaredMethods().clear();
            cd.getDeclaredMethods().addAll(methods);
        }

        return result;
    }
}

class InstrumentControlFlowScope implements CodeTransform<ControlFlowScope> {

    int varNameCounter = 0;

    private String newVarName() {
        return "__vrl_reserved_intermediate_var_" + ++varNameCounter;
    }

    @Override
    public ControlFlowScope transform(ControlFlowScope ce) {
        // TODO 01.08.2015 add clone()

        ControlFlowScope result = ce;

        List<Invocation> invocations = new ArrayList<>();
        invocations.addAll(ce.getControlFlow().getInvocations());

        result.getControlFlow().getInvocations().clear();

        ControlFlow cf = result.getControlFlow();

        for (int i = 0; i < invocations.size(); i++) {

            Invocation inv = invocations.get(i);

            System.out.println("i : " + inv);
            
            String varName = newVarName();
            
//            cf.declareVariable("", Type.STRING, varName+"_pre_arg");
//                    Variable rightArgVariable = ce.getVariable(varName+"_pre_arg");
//                    Invocation preArgInv = cf.invokeOperator("",
//                            Argument.constArg(Type.STRING, "pre-m-call: " + inv.getMethodName() + ", args: "),
//                            Argument.varArg(result.getVariable(varName)), Operator.PLUS);
//                    cf.assignVariable("", rightArgVariable.getName(), Argument.invArg(preArgInv));
            

            Invocation preEventInv
                    = cf.callMethod("", "this", "println", Type.VOID, Argument.constArg(
                                    Type.STRING, "pre-m-call: " + inv.getMethodName()));

            boolean lastInvocation = i == invocations.size() - 1;

            IArgument retValArg = Argument.NULL;

            if (!lastInvocation) {
                Invocation nextI = invocations.get(i + 1);

                boolean iIsArgOfNextI = nextI.getArguments().stream().
                        filter(a -> Objects.equals(a.getInvocation().orElse(null), inv)).
                        findAny().isPresent();

                if (iIsArgOfNextI) {
                    
                    cf.declareVariable("", inv.getReturnType(), varName);
                    cf.assignVariable("", varName, Argument.invArg(inv));

                    int[] argumentsToReplace = nextI.getArguments().stream().
                            filter(a -> Objects.equals(a.getInvocation().orElse(null), inv)).
                            mapToInt(a -> nextI.getArguments().indexOf(a)).toArray();

                    for (int aIndex : argumentsToReplace) {
                        nextI.getArguments().set(aIndex,
                                Argument.varArg(result.getVariable(varName)));
                    }
                    
                    cf.declareVariable("", Type.STRING, varName+"_post_arg");
                    Variable rightArgVariable = ce.getVariable(varName+"_post_arg");
                    Invocation retValPostArgInv = cf.invokeOperator("",
                            Argument.constArg(Type.STRING, "post-m-call: " + inv.getMethodName() + ", retVal: "),
                            Argument.varArg(result.getVariable(varName)), Operator.PLUS);
                    cf.assignVariable("", rightArgVariable.getName(), Argument.invArg(retValPostArgInv));
                    
                    retValArg = Argument.varArg(rightArgVariable);
                }
            }

            cf.getInvocations().add(inv);
            if (Objects.equals(retValArg, Argument.NULL)) {
                cf.callMethod("", "this", "println", Type.VOID, Argument.constArg(
                        Type.STRING, "post-m-call: " + inv.getMethodName()));
            } else {
                cf.callMethod("", "this", "println", Type.VOID, retValArg);
            }

            if (inv instanceof ScopeInvocation) {
                ScopeInvocation si = (ScopeInvocation) inv;
                transform((ControlFlowScope) si.getScope());
            }

        }

        return result;
    }

}
