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
 * Model transformation that adds instrumentations to the model that shall be
 * transformed.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class InstrumentCode implements CodeTransform<CompilationUnitDeclaration> {

    @Override
    public CompilationUnitDeclaration transform(
            CompilationUnitDeclaration cu, String indent) {

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

/**
 * Instruments control flows.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class InstrumentControlFlowScope implements CodeTransform<ControlFlowScope> {

    int varNameCounter = 0;

    /**
     * Returns a unique variable name.
     *
     * @return unique variable name
     */
    private String newVarName() {
        String varName = "__vrl_reserved_intermediate_var_" + varNameCounter++;
        return varName;
    }

    /**
     * Indicates whether the specified invocation is a while-loop.
     *
     * @param inv invocation to check
     * @return {@code true} if the specified invocation is a while-loop;
     * {@code false} otherwise
     */
    private boolean isWhileLoop(Invocation inv) {

        if (inv.isScope()) {
            ScopeInvocation si = (ScopeInvocation) inv;
            return si.getScope() instanceof WhileDeclaration;
        }

        return false;
    }

    /**
     * Returns the specified invocation and all connected invocations. Connected
     * invocations are defined as invocations that are connected via *dataflow*:
     *
     * - invocations that are inputs of other invocations - invocations that are
     * called on objects returned by invocations
     *
     * @param cf
     * @param invocations
     * @param inv
     * @return
     */
    private List<Invocation> getInvAndConnectedInvs(ControlFlow cf,
            List<Invocation> invocations, Invocation inv) {
        List<Invocation> result = new ArrayList<>();

        // current invocation index in the specified invocation list
        int invIndex = invocations.indexOf(inv);

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

        return result;
    }

    /**
     * While-Loop lookahead.
     */
    final static class WLLookahead {

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

    /**
     * Looks up the next while-loop.
     *
     * @param cf controlfow to analyze
     * @param invocations list of invocations to check
     * @param inv start invocation. it must be contained in the specified
     * invocation list and be a child of the specified controlflow
     * @return WLLookahead optional, which is not empty if the specified
     * invocations contain a while-loop invocation and if all invocations til
     * the specified invocation and the while-loop invocation (including the
     * loop) are connected (indexof(inv)&lt;indexof(while)).
     */
    private Optional<WLLookahead> lookupNextWhileLoop(ControlFlow cf,
            List<Invocation> invocations, Invocation inv) {
        int i = invocations.indexOf(inv);

        List<Invocation> potentialInvocations
                = getInvAndConnectedInvs(cf, invocations, inv);

        ScopeInvocation whileInv = null;

        if (potentialInvocations.size() > 1) {
            Invocation potentialWL = potentialInvocations.
                    get(potentialInvocations.size() - 1);
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

        // instrument all incovations of the specified scope
        List<Invocation> invocations
                = new ArrayList<>(ce.getControlFlow().getInvocations());
        ControlFlow cf = result.getControlFlow();
        addInstrumentation(cf, invocations, indent);

        return result;
    }

    /**
     * Instruments the specified invocations. The instrumented invocations will
     * be added to the specified controlflow. All previous invocations of the
     * controlflow are replaced.
     *
     * @param cf controlflow
     * @param invocations incovations to instrument
     * @param indent TODO remove me!!!
     */
    private void addInstrumentation(
            ControlFlow cf, List<Invocation> invocations, String indent) {

        // instrumented invocations: original invocations with additional
        // pre-/post event invocations
        List<Invocation> instrumentedInvocations = new ArrayList<>();

        // instrument each specified invocations
        for (int i = 0; i < invocations.size(); i++) {

            Invocation inv = invocations.get(i);
            System.out.println(indent + " inv: " + inv);

            // while loop instrumented separately
            if (isWhileLoop(inv)) {
                continue;
            }

            // check whether connected invocations are directly connected to
            // a while-loop, i.e., if the current invocations and all following
            // invocations are connected til the next while-loop. the connected
            // invocations before the while-loop are the *loop-condition*:
            // 
            // example: 
            //
            // ```java
            // while(i < n) {
            //  ... 
            // }
            // ```
            //
            // the meaning of *connected* is explained in the `
            // `getInv AndConnectedInvs(...)` method.
            //
            Optional<WLLookahead> wlLookAheadRes
                    = lookupNextWhileLoop(cf, invocations, inv);

            // instrument while-loop if present
            if (wlLookAheadRes.isPresent()) {
                WLLookahead wLLookAhead = wlLookAheadRes.get();

                instrumentWhileLoop(cf, wLLookAhead.getCondInvocations(),
                        wLLookAhead.getWhileInv(),
                        instrumentedInvocations, indent);

                // move forward to next invocation after the loop
                i += wLLookAhead.getCondInvocations().size();

            } else {
                instrumentNonLoopInvocation(cf, i, invocations,
                        instrumentedInvocations, indent);
            }
        } // end for i

        // remove all previous invocations from the controlflow and replace 
        // them with the instrumented version
        cf.getInvocations().clear();
        cf.getInvocations().addAll(instrumentedInvocations);
    }

    /**
     * Instruments the specified invocation (loops are not supported).
     *
     * @param cf controlflow to instrument
     * @param i invocation index
     * @param invocations list of all invocations
     * @param resultInvs list of instrumented invocations
     * @param indent TODO remove me!!
     */
    private void instrumentNonLoopInvocation(ControlFlow cf, int i,
            List<Invocation> invocations, List<Invocation> resultInvs,
            String indent) {
        Invocation inv = invocations.get(i);

        Scope result = cf.getParent();

        Invocation preEventInv
                = cf.callMethod("", "this", "println", Type.VOID,
                        Argument.constArg(Type.STRING,
                                "pre-m-call: " + inv.getMethodName()
                                + ", id: " + inv.getId()));

        resultInvs.add(preEventInv);

        boolean lastInvocation = i == invocations.size() - 1;

        IArgument retValArg = Argument.NULL;
        Invocation nextI = null;
        boolean retValIsObjectOfNextI = false;
        if (!lastInvocation) {
            nextI = invocations.get(i + 1);
            retValIsObjectOfNextI = isRetValObjectOfNextInv(nextI);
        }

        Optional<Invocation> invocationTarget = isInvArg(inv, cf);

        if (invocationTarget.isPresent() || retValIsObjectOfNextI) {
            
            String varName = newVarName();

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
            } else if (nextI != null) {
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
        } // end if 

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

    /**
     * Indicates whether the return value of the current invocation is the 
     * invocation object of the next invocation. 
     * @param nextI next invocation
     * @return {@code true} if the return value if this invocation is the 
     * invocation object of the next invocation; {@code false} otherwise
     */
    private boolean isRetValObjectOfNextInv(Invocation nextI) {
        // TODO 04.08.2015 chained method check buggy, needs to be modeled too
        boolean retValIsObjectOfNextI
                = nextI.getVariableName() != null
                && nextI.getVariableName().isEmpty();
        return retValIsObjectOfNextI;
    }

    /**
     * Indicates whether the specified invocation is used as argument inside the
     * specified controflow. Returns the target invocation if the target exists.
     *
     * @param inv invocation to check
     * @param cf controlflow
     * @return target invocation or an empty optional if the target does not
     * exist
     */
    private Optional<Invocation> isInvArg(Invocation inv, ControlFlow cf) {
        return cf.returnInvTargetIfPresent(inv);
    }

    /**
     * Instruments the specified while-loop invocation.
     *
     * @param cf controlfow
     * @param condInvs condition invocations
     * @param whileLoopInv while-loop invocation to instrument
     * @param resultInvs instrumented invocations
     * @param indent TODO remove me!!!
     */
    private void instrumentWhileLoop(
            ControlFlow cf,
            List<Invocation> condInvs,
            ScopeInvocation whileLoopInv,
            List<Invocation> resultInvs, String indent) {

        // pre-event call for the while-loop invocation
        Invocation preWhileEventInv
                = cf.callMethod("", "this", "println", Type.VOID,
                        Argument.constArg(
                                Type.STRING,
                                "pre-m-call: "
                                + whileLoopInv.getMethodName()
                                + ", id: " + whileLoopInv.getId()));
        resultInvs.add(preWhileEventInv);

        // introduce condition variable that is used to simulate the original
        // while-loop behavior
        String varName = newVarName();
        resultInvs.add(cf.declareVariable("", Type.BOOLEAN, varName));

        // while-loop check/condition has to be always true: 'while(true){...}'
        // we escape via break (see if-statement below) 
        whileLoopInv.getArguments().
                set(0, Argument.constArg(Type.BOOLEAN, true));

        // add the modified while-loop to the parent controlflow
        cf.getInvocations().add(whileLoopInv);
        resultInvs.add(whileLoopInv);

        // obtain the while-loop body scope and corresponding controlflow
        ControlFlowScope whileScope = (ControlFlowScope) whileLoopInv.getScope();
        ControlFlow whileCf = whileLoopInv.getScope().getControlFlow();

        // save all original invocations inside the while-loop body scope
        // and clear the while-loop controlflow
        List<Invocation> whileInvocations
                = new ArrayList<>(whileCf.getInvocations());
        whileCf.getInvocations().clear();

        // move the condition statements from the parent scope to the while-loop
        // body and instrument them. the while-loop body controlflow will be
        // cleared afterwards
        whileCf.getInvocations().addAll(condInvs);
        addInstrumentation(whileCf, condInvs, indent + "while-cond--|");
        List<Invocation> instrumentedCondInvs
                = new ArrayList<>(whileCf.getInvocations());
        whileCf.getInvocations().clear();

        // negate the condition/check and assign it to the previously declared
        // condition variable
        Invocation notInv = whileCf.invokeNot("",
                Argument.invArg(condInvs.get(condInvs.size() - 1)));
        instrumentedCondInvs.add(notInv);
        instrumentedCondInvs.add(whileCf.assignVariable("",
                varName, Argument.invArg(notInv)));

        // add an if-statement to the while-loop body controlflow that simulates
        // the original while-loop behavior
        VisualCodeBuilder builder = new VisualCodeBuilder_Impl();
        IfDeclaration ifDecl = builder.invokeIf(
                whileScope, Argument.varArg(cf.getParent().getVariable(varName)));
        Invocation conditionIfInv = whileScope.getControlFlow().getInvocations().get(
                whileScope.getControlFlow().getInvocations().size() - 1);
        ifDecl.getControlFlow().invokeBreak("");

        // instrument original while invocations, i.e. the content
        // of the while-loop body
        whileCf.getInvocations().clear();
        whileCf.getInvocations().addAll(whileInvocations);
        transform(whileScope, indent + "while--|");
        List<Invocation> instrumentedWhileInvocations
                = new ArrayList<>(whileCf.getInvocations());
        whileCf.getInvocations().clear();

        // add all instrumented invocations to the inal while-loop
        // controlflow:
        // 
        // - conditions (e.g. 'i < n' or 'i < 2*3 +myMethod(1)' )
        // - additional if(cond){break;} statement to simulate the original
        //   loop behavior
        // - instrumented version of the original while-loop body
        whileCf.getInvocations().clear();
        whileCf.getInvocations().addAll(instrumentedCondInvs);
        whileCf.getInvocations().add(conditionIfInv);
        whileCf.getInvocations().addAll(instrumentedWhileInvocations);

        // add a postEvent call to the parent controlflow of this while-loop
        resultInvs.add(cf.callMethod("", "this", "println", Type.VOID, Argument.constArg(
                Type.STRING, "post-m-call: " + whileLoopInv.getMethodName())));
    }

}
