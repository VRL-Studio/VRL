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
import eu.mihosoft.vrl.lang.model.IfDeclaration;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.Operator;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.ScopeInvocation;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.lang.model.Variable;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder_Impl;
import eu.mihosoft.vrl.lang.model.WhileDeclaration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class InstrumentCode implements CodeTransform<CompilationUnitDeclaration> {

    @Override
    public CompilationUnitDeclaration transform(CompilationUnitDeclaration cu, String indent) {
        // TODO 01.08.2015 add clone()

        CompilationUnitDeclaration result = cu;

        InstrumentControlFlowScope im = new InstrumentControlFlowScope();

        for (ClassDeclaration cd : result.getDeclaredClasses()) {
            List<MethodDeclaration> methods = new ArrayList<>();
            for (MethodDeclaration md : cd.getDeclaredMethods()) {
                methods.add((MethodDeclaration) im.transform(md, indent));
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

    private boolean isWhileLoop(Invocation inv) {

        if (inv.isScope()) {
            ScopeInvocation si = (ScopeInvocation) inv;
            return si.getScope() instanceof WhileDeclaration;
        }

        return false;
    }

    private List<Invocation> getInvAndConnectedInvs(ControlFlow cf,
            List<Invocation> invocations, Invocation inv) {
        List<Invocation> result = new ArrayList<>();

        int invIndex = invocations.indexOf(inv);

//        System.out.println("---->");
//        System.out.println("-> inv: " + inv);
//        System.out.println(" -> #inv: " + invocations.size());
        for (Invocation pInv : invocations) {
//            System.out.println(" -> sw: " + pInv);
        }

        for (int i = invIndex + 1; i < invocations.size(); i++) {

            Invocation nextInv = invocations.get(i);

            if (cf.isUsedAsInput(inv)
                    || isRetValObjectOfNextInv(nextInv)) {
                result.add(inv);
                if (i == invocations.size() - 1) {
                    result.add(nextInv);
                }
            } else {
                result.add(inv);
                break;
            }
            inv = nextInv;
        }

//        for (Invocation pInv : result) {
//            System.out.println(" -> ci: " + pInv);
//        }
//        System.out.println("<----");
        return result;
    }

    class WLLookahead {

        private final int startIndex;
        private final List<Invocation> condInvocations;
        private final ScopeInvocation whileInv;

        public WLLookahead(int startIndex,
                List<Invocation> condInvocations,
                ScopeInvocation whileInv) {
            this.startIndex = startIndex;
            this.condInvocations = condInvocations;
            this.whileInv = whileInv;
        }

        public List<Invocation> getCondInvocations() {
            return condInvocations;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public ScopeInvocation getWhileInv() {
            return whileInv;
        }
    }

    private boolean isNextWhileLoop(List<Invocation> invocations, Invocation inv) {
        int i = invocations.indexOf(inv);

        boolean lastInvocation = i == invocations.size() - 1;

        if (lastInvocation) {
            return false;
        } else {
            Invocation nextI = invocations.get(i + 1);
            return isWhileLoop(nextI);
        }
    }

    private Optional<WLLookahead> lookupNextWhileLoop(ControlFlow cf,
            List<Invocation> invocations, Invocation inv) {
        int i = invocations.indexOf(inv);

        List<Invocation> potentialInvocations
                = getInvAndConnectedInvs(cf, invocations, inv);

        ScopeInvocation whileInv = null;

        if (potentialInvocations.size() > 1) {
            Invocation potentialWL = potentialInvocations.
                    get(potentialInvocations.size() - 1);
//            System.out.println("POT: " + potentialWL);
            if (isWhileLoop(potentialWL)) {
                whileInv = (ScopeInvocation) potentialWL;
            }
        }

        if (whileInv == null) {
            return Optional.empty();
        }

        List<Invocation> condInvs = new ArrayList<>();

        condInvs.addAll(potentialInvocations.subList(
                0, potentialInvocations.size() - 1));

        return Optional.of(new WLLookahead(i, condInvs, whileInv));
    }

    @Override
    public ControlFlowScope transform(ControlFlowScope ce, String indent) {
        // TODO 01.08.2015 add clone()

        ControlFlowScope result = ce;

        List<Invocation> invocations = new ArrayList<>();
        invocations.addAll(ce.getControlFlow().getInvocations());

        ControlFlow cf = result.getControlFlow();
        addInstrumentation(cf, invocations, indent);

        return result;
    }

    private void addInstrumentation(
            ControlFlow cf, List<Invocation> invocations, String indent) {

        List<Invocation> instrumentedInvocations = new ArrayList<>();

        for (int i = 0; i < invocations.size(); i++) {

            Invocation inv = invocations.get(i);
            System.out.println(indent + " inv: " + inv);

            // while loop instrumented separately
            if (isWhileLoop(inv)) {
                continue;
            }

            Optional<WLLookahead> wlLookAheadRes
                    = lookupNextWhileLoop(cf, invocations, inv);

            if (wlLookAheadRes.isPresent()) {
                WLLookahead wLLookAhead = wlLookAheadRes.get();

//                System.out.println(indent + "->");
//                List<Invocation> instrumentedCondInvocations = new ArrayList<>();
//                for (int j = 0; j < wLLookAhead.getCondInvocations().size(); j++) {
//                    Invocation cInv = wLLookAhead.getCondInvocations().get(j);
////                    System.out.println(indent + " cond: " + cf.isUsedAsInput(cInv));
//                    instrumentNonLoopInvocation(cf, j, wLLookAhead.getCondInvocations(), instrumentedCondInvocations, indent);
//                }
//                System.out.println(indent + "<-");
                instrumentWhileLoop(cf, wLLookAhead.getCondInvocations(),
                        wLLookAhead.getWhileInv(), instrumentedInvocations, indent);

                // move forward to next invocation after the loop
                i += wLLookAhead.getCondInvocations().size();

            } else {
                instrumentNonLoopInvocation(cf, i, invocations, instrumentedInvocations, indent);
            }
        } // end for i

        cf.getInvocations().clear();
        cf.getInvocations().addAll(instrumentedInvocations);
    }

    private void instrumentNonLoopInvocation(ControlFlow cf, int i, List<Invocation> invocations, List<Invocation> resultInvs, String indent) {
        Invocation inv = invocations.get(i);

        Scope result = cf.getParent();

        String varName = newVarName();
        Invocation preEventInv
                = cf.callMethod("", "this", "println", Type.VOID, Argument.constArg(
                                Type.STRING, "pre-m-call: " + inv.getMethodName() + ", id: " + inv.getId()));

        resultInvs.add(preEventInv);

        boolean lastInvocation = i == invocations.size() - 1;

        IArgument retValArg = Argument.NULL;

        if (!lastInvocation) {
            Invocation nextI = invocations.get(i + 1);

//            boolean iIsArgOfNextI = isInvArgOfNextInv(nextI, inv);
            boolean retValIsObjectOfNextI = isRetValObjectOfNextInv(nextI);
            
            Optional<Invocation> invocationTarget = isInvArg(inv, cf);

            if (invocationTarget.isPresent() || retValIsObjectOfNextI) {

                resultInvs.add(cf.declareVariable("", inv.getReturnType(), varName));
                resultInvs.add(cf.assignVariable("", varName, Argument.invArg(inv)));

                if (invocationTarget.isPresent()) {
                    int[] argumentsToReplace = invocationTarget.get().getArguments().stream().
                            filter(a -> Objects.equals(a.getInvocation().orElse(null), inv)).
                            mapToInt(a -> invocationTarget.get().getArguments().indexOf(a)).toArray();

                    for (int aIndex : argumentsToReplace) {
                        invocationTarget.get().getArguments().set(aIndex,
                                Argument.varArg(cf.getParent().getVariable(varName)));
                    }
                } else {
                    // retValIsObjectOfNextI
                    nextI.setVariableName(varName);
                }

                resultInvs.add(cf.declareVariable("", Type.STRING, varName + "_post_arg"));
                Variable rightArgVariable = result.getVariable(varName + "_post_arg");
                Invocation retValPostArgInv = cf.invokeOperator("",
                        Argument.constArg(Type.STRING, "post-m-call: " + inv.getMethodName() + ", retVal: "),
                        Argument.varArg(result.getVariable(varName)), Operator.PLUS);
                resultInvs.add(retValPostArgInv);
                resultInvs.add(cf.assignVariable("", rightArgVariable.getName(),
                        Argument.invArg(retValPostArgInv)));

                retValArg = Argument.varArg(rightArgVariable);
            }
        }

        cf.getInvocations().add(inv);
        resultInvs.add(inv);
        if (Objects.equals(retValArg, Argument.NULL)) {
            resultInvs.add(cf.callMethod("", "this", "println", Type.VOID, Argument.constArg(
                    Type.STRING, "post-m-call: " + inv.getMethodName())));
        } else {
            resultInvs.add(cf.callMethod("", "this", "println", Type.VOID, retValArg));
        }

        if (inv instanceof ScopeInvocation) {
            ScopeInvocation si = (ScopeInvocation) inv;
            transform((ControlFlowScope) si.getScope(), indent + "--|");
        }
    }

    private boolean isRetValObjectOfNextInv(Invocation nextI) {
        // TODO 04.08.2015 chained method check buggy, needs to be modeled too
        boolean retValIsObjectOfNextI
                = nextI.getVariableName() != null
                && nextI.getVariableName().isEmpty();
        return retValIsObjectOfNextI;
    }

//    private boolean isInvArgOfNextInv(Invocation nextI, Invocation inv) {
//        boolean iIsArgOfNextI = nextI.getArguments().stream().
//                filter(a -> Objects.equals(a.getInvocation().orElse(null), inv)).
//                findAny().isPresent();
//        return iIsArgOfNextI;
//    }

    private Optional<Invocation> isInvArg(Invocation inv, ControlFlow cf) {
        return cf.returnInvTargetIfPresent(inv);
    }

    private void instrumentWhileLoop(
            ControlFlow cf,
            List<Invocation> condInvs,
            ScopeInvocation whileLoopInv,
            List<Invocation> resultInvs, String indent) {

        Invocation preWhileEventInv
                = cf.callMethod("", "this", "println", Type.VOID,
                        Argument.constArg(
                                Type.STRING,
                                "pre-m-call: "
                                + whileLoopInv.getMethodName()
                                + ", id: " + whileLoopInv.getId()));

        resultInvs.add(preWhileEventInv);

        String varName = newVarName();
        resultInvs.add(cf.declareVariable("", Type.BOOLEAN, varName));

        whileLoopInv.getArguments().set(0, Argument.constArg(Type.BOOLEAN, true));

        cf.getInvocations().add(whileLoopInv);
        resultInvs.add(whileLoopInv);

        ControlFlowScope whileScope = (ControlFlowScope) whileLoopInv.getScope();

        transform(whileScope, indent + "while--|");

        ControlFlow whileCf = whileLoopInv.getScope().getControlFlow();

        List<Invocation> instrumentedWhileInvocations = new ArrayList<>();
        instrumentedWhileInvocations.addAll(whileCf.getInvocations());
        whileCf.getInvocations().clear();

        VisualCodeBuilder builder = new VisualCodeBuilder_Impl();

        List<Invocation> instrumentedCondInvs = new ArrayList<>();
        whileCf.getInvocations().addAll(condInvs);
        addInstrumentation(whileCf, condInvs, indent + "while-cond--|");
        instrumentedCondInvs.addAll(whileCf.getInvocations());

        whileCf.getInvocations().clear();

        Invocation notInv = whileCf.invokeNot("",
                Argument.invArg(condInvs.get(condInvs.size() - 1)));
        instrumentedCondInvs.add(notInv);
        instrumentedCondInvs.add(whileCf.assignVariable("",
                varName, Argument.invArg(notInv)));

        IfDeclaration ifDecl = builder.invokeIf(
                whileScope, Argument.varArg(cf.getParent().getVariable(varName)));
        Invocation conditionIfInv = whileScope.getControlFlow().getInvocations().get(
                whileScope.getControlFlow().getInvocations().size() - 1);

        ifDecl.getControlFlow().invokeBreak("");

        whileCf.getInvocations().clear();
        whileCf.getInvocations().addAll(instrumentedCondInvs);
        whileCf.getInvocations().add(conditionIfInv);
        whileCf.getInvocations().addAll(instrumentedWhileInvocations);

        resultInvs.add(cf.callMethod("", "this", "println", Type.VOID, Argument.constArg(
                Type.STRING, "post-m-call: " + whileLoopInv.getMethodName())));
    }

}
