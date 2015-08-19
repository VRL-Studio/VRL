/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.transform;

import eu.mihosoft.vrl.instrumentation.VRLInstrumentationUtil;
import eu.mihosoft.vrl.lang.model.BinaryOperatorInvocationImpl;
import eu.mihosoft.vrl.lang.model.BreakInvocation;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.ContinueInvocation;
import eu.mihosoft.vrl.lang.model.ControlFlow;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.DeclarationInvocation;
import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.IfDeclaration;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.ObjectProvider;
import eu.mihosoft.vrl.lang.model.Operator;
import eu.mihosoft.vrl.lang.model.ReturnStatementInvocation;
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
            CompilationUnitDeclaration cu) {

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

/**
 * Instruments control flows.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class InstrumentControlFlowScope implements CodeTransform<ControlFlowScope> {

    private int varNameCounter = 0;
    private String currentVarName = "";
    private String prevVarName = "";

    /**
     * Returns a unique variable name.
     *
     * @return unique variable name
     */
    private String newVarName() {
        prevVarName = currentVarName;
        currentVarName = "__vrl_reserved_intermediate_var_" + varNameCounter++;
        return currentVarName;
    }

    /**
     * Returns the previous intermediate variable name.
     *
     * @return previous intermediate variable name
     */
    private String previousVarName() {
        return prevVarName;
    }

    /**
     * Returns the current intermediate variable name.
     *
     * @return current intermediate variable name
     */
    private String currentVarName() {
        return currentVarName;
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
     * <pre>
     *  a) invocations that are inputs of
     *     other invocations
     *  b) invocations that are called on
     *     objects returned by invocations
     * </pre>
     *
     * @param cf control flow that contains the invocations to check
     * @param invocations invocations that shall be considered
     * @param inv invocation
     * @return list that contains the specifried invocation and all connected
     * invocations
     */
    private List<Invocation> getInvAndConnectedInvs(ControlFlow cf,
            List<Invocation> invocations, Invocation inv) {
        List<Invocation> result = new ArrayList<>();

        // current invocation index in the specified invocation list
        int invIndex = invocations.indexOf(inv);

        for (int i = invIndex + 1; i < invocations.size(); i++) {

            Invocation nextInv = invocations.get(i);

            if (cf.isUsedAsInput(inv)
                    || isRetValObjectOfNextInv(inv, nextInv)) {
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
     * Indicates whether the specified invocation needs a tmp variable. This
     * method should be used instead of {@link Invocation#getReturnType()} since
     * some invocations have a non-void return type (e.g. assignments and
     * declarations) but they must not be used as argument of other invocations.
     *
     * @param inv invocation
     * @param invocations list of invocations
     * @return {@code true} if the invocation needs a tmp variable;
     * {@code false} otherwise
     */
    private boolean invNeedsTmpVar(Invocation inv, List<Invocation> invocations) {

        if (inv.getReturnType() == Type.VOID) {
            return false;
        }

        if (inv instanceof DeclarationInvocation) {
            return false;
        }

        if (inv instanceof ReturnStatementInvocation) {
            return false;
        }

        if (inv instanceof BreakInvocation) {
            return false;
        }

        if (inv instanceof ContinueInvocation) {
            return false;
        }

        // normal non-void method
        if (!(inv instanceof BinaryOperatorInvocationImpl)) {

            return true;
        }

        BinaryOperatorInvocationImpl boi = (BinaryOperatorInvocationImpl) inv;

        return !BinaryOperatorInvocationImpl.
                assignmentOperator(boi.getOperator())
                && !BinaryOperatorInvocationImpl.
                pureAssignmentOperator(boi.getOperator());
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
    public ControlFlowScope transform(ControlFlowScope ce) {
        // TODO 01.08.2015 add clone()
        ControlFlowScope result = ce;

        // instrument all incovations of the specified scope
        List<Invocation> invocations
                = new ArrayList<>(ce.getControlFlow().getInvocations());
        ControlFlow cf = result.getControlFlow();
        
        if(ce.getName().contains("createSphere")) {
            System.out.println("");
        }
        
        addInstrumentation(cf, invocations);

        return result;
    }

    /**
     * Instruments the specified invocations. The instrumented invocations will
     * be added to the specified controlflow. All previous invocations of the
     * controlflow are replaced.
     *
     * @param cf controlflow
     * @param invocations incovations to instrument
     */
    private void addInstrumentation(
            ControlFlow cf, List<Invocation> invocations) {

        // instrumented invocations: original invocations with additional
        // pre-/post event invocations
        List<Invocation> instrumentedInvocations = new ArrayList<>();

        // instrument each specified invocations
        for (int i = 0; i < invocations.size(); i++) {

            Invocation inv = invocations.get(i);

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
                        instrumentedInvocations);

                // move forward to next invocation after the loop
                i += wLLookAhead.getCondInvocations().size();

            } else {
                instrumentNonLoopInvocation(cf, i, invocations,
                        instrumentedInvocations);
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
     */
    private void instrumentNonLoopInvocation(ControlFlow cf, int i,
            List<Invocation> invocations, List<Invocation> resultInvs) {
        Invocation inv = invocations.get(i);

        Scope result = cf.getParent();

        // add the pre-event invocation
        Invocation preEventInv
                = VRLInstrumentationUtil.generatePreEvent(cf, inv);

        resultInvs.add(preEventInv);

        // if the current invocation is not the last invocation then
        // nextInvocation will be defined (for lookups) and it will be checked
        // whether the return value of the current invocation is the invocation
        // object of the next invocation
        boolean lastInvocation = i == invocations.size() - 1;
        Argument retValArg = Argument.nullArg();
        Invocation nextI = null;
        boolean retValIsObjectOfNextI = false;

        if (!lastInvocation) {
            nextI = invocations.get(i + 1);
            retValIsObjectOfNextI = isRetValObjectOfNextInv(inv, nextI);
        }

        // searches invocation target (other invocation that uses the current
        // invocation as input) 
        Optional<Invocation> invocationTarget = isInvArg(inv, cf);

        // if the current method is non-void we need to introduce tmp variables
        if (invNeedsTmpVar(inv, invocations)) {

            // creates and assigns tmp variable
            String varName = newVarName();
            resultInvs.add(cf.declareVariable("", inv.getReturnType(), varName));
            resultInvs.add(cf.assignVariable("", varName, Argument.invArg(inv)));

            // if this invocation is an input of another invocation then we
            // need to replace the argument with the previously defined tmp
            // variable
            if (invocationTarget.isPresent()) {

                if (invocationTarget.get().getMethodName().contains("return")) {
                    System.out.println("");
                }

                // search argument indices
                int[] argumentsToReplace = invocationTarget.get().
                        getArguments().stream().
                        filter(a -> Objects.equals(a.getInvocation().
                                        orElse(null), inv)).
                        mapToInt(a -> invocationTarget.get().
                                getArguments().indexOf(a)).toArray();
                // replace args
                for (int aIndex : argumentsToReplace) {
                    invocationTarget.get().getArguments().set(aIndex,
                            Argument.varArg(cf.getParent().
                                    getVariable(varName)));
                }
            }

            if (retValIsObjectOfNextI && nextI != null) {
                // if the result of the current invocation is used as
                // invocation object then we need to call the next invocation
                // on the previously defined tmp variable that stores the return
                // value of the current invocation
                nextI.setObjectProvider(ObjectProvider.fromVariable(varName));
                System.out.println("VARNAME: " + varName);
            }

            retValArg = Argument.varArg(result.getVariable(varName));
        } // end if 

        cf.getInvocations().add(inv);
        resultInvs.add(inv);
        if (Objects.equals(retValArg, Argument.nullArg())) {
            resultInvs.add(VRLInstrumentationUtil.generatePostEvent(cf, inv));
        } else {
            resultInvs.add(VRLInstrumentationUtil.generatePostEvent(
                    cf, inv, retValArg));
        }

        if (inv instanceof ScopeInvocation) {
            ScopeInvocation si = (ScopeInvocation) inv;
            transform((ControlFlowScope) si.getScope());
        }
    }

    /**
     * Indicates whether the return value of the current invocation is the
     * invocation object of the next invocation.
     *
     * @param nextI next invocation
     * @return {@code true} if the return value if this invocation is the
     * invocation object of the next invocation; {@code false} otherwise
     */
    private boolean isRetValObjectOfNextInv(Invocation inv, Invocation nextI) {

        boolean retValIsObjectOfNextI
                = nextI.getObjectProvider().getInvocation().isPresent()
                && nextI.getObjectProvider().getInvocation().get().equals(inv);
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
     */
    private void instrumentWhileLoop(
            ControlFlow cf,
            List<Invocation> condInvs,
            ScopeInvocation whileLoopInv,
            List<Invocation> resultInvs) {

        // pre-event call for the while-loop invocation
        Invocation preWhileEventInv
                = VRLInstrumentationUtil.generatePreEvent(cf, whileLoopInv);
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
        addInstrumentation(whileCf, condInvs);
        List<Invocation> instrumentedCondInvs
                = new ArrayList<>(whileCf.getInvocations());
        whileCf.getInvocations().clear();

        // negate the condition/check and assign it to the previously declared
        // condition variable
        Invocation notInv = whileCf.invokeNot("",
                Argument.varArg(whileCf.getParent().getVariable(
                                currentVarName())));
        instrumentedCondInvs.add(notInv);
        instrumentedCondInvs.add(whileCf.assignVariable("",
                varName, Argument.invArg(notInv)));

        // add an if-statement to the while-loop body controlflow that simulates
        // the original while-loop behavior
        VisualCodeBuilder builder = new VisualCodeBuilder_Impl();
        IfDeclaration ifDecl = builder.invokeIf(whileScope, Argument.varArg(cf.getParent().
                getVariable(varName)));
        Invocation conditionIfInv = whileScope.getControlFlow().
                getInvocations().get(
                        whileScope.getControlFlow().getInvocations().
                        size() - 1);
        ifDecl.getControlFlow().invokeBreak("");

        // instrument original while invocations, i.e. the content
        // of the while-loop body
        whileCf.getInvocations().clear();
        whileCf.getInvocations().addAll(whileInvocations);
        transform(whileScope);
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
        resultInvs.add(VRLInstrumentationUtil.generatePostEvent(cf, whileLoopInv));
    }

}
