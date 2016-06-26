/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.transform;

import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.ArgumentType;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.ControlFlow;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.IType;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.ObjectProvider;
import eu.mihosoft.vrl.lang.model.ScopeInvocation;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.lang.model.Variable;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;
import static eu.mihosoft.vrl.lang.model.transform.OptUtils.isArgOfNext;
import static eu.mihosoft.vrl.lang.model.transform.OptUtils.isObjProviderOfNext;
import static eu.mihosoft.vrl.lang.model.transform.OptUtils.objNameEqArgName;
import static eu.mihosoft.vrl.lang.model.transform.OptUtils.ofName;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import static eu.mihosoft.vrl.lang.model.transform.OptUtils.csgAPIMethod;
import java.util.stream.Collectors;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class BooleanJCSGOptimizer implements CodeTransform<CompilationUnitDeclaration> {

    private final ExpressionOptimizer exprOptimizer
            = new ExpressionOptimizer();
    private final TransformationOptimizer transformOptimizer
            = new TransformationOptimizer();

    @Override
    public CompilationUnitDeclaration transform(CompilationUnitDeclaration ce) {
        for (ClassDeclaration cD : ce.getDeclaredClasses()) {
            for (MethodDeclaration mD : cD.getDeclaredMethods()) {
                exprOptimizer.transform(mD);
                transformOptimizer.transform(mD);
            }
        }

        return ce;
    }

}

class TransformationOptimizer implements CodeTransform<ControlFlowScope> {

    @Override
    public ControlFlowScope transform(ControlFlowScope cfs) {
        // TODO 01.08.2015 add clone()
        ControlFlowScope result = cfs;
        ControlFlow cf = cfs.getControlFlow();

        Invocation prevInv = null;
        Invocation nextInv = null;

        List<Invocation> invocationsToDelete = new ArrayList<>();

        // search for transformed() invocation chains and simplify them
        for (int i = 0; i < cf.getInvocations().size(); i++) {

            Invocation inv = cf.getInvocations().get(i);

            if (i - 1 >= 0) {
                prevInv = cf.getInvocations().get(i - 1);
            }

            if (i + 1 < cf.getInvocations().size()) {
                nextInv = cf.getInvocations().get(i + 1);
            }

            // apply transformation to sub-scopes
            if (inv instanceof ScopeInvocation) {
                ScopeInvocation sInv = (ScopeInvocation) inv;
                if (sInv.getScope() instanceof ControlFlowScope) {
                    transform((ControlFlowScope) sInv.getScope());
                }
                continue;
            }

            // if the current method is no csg method, continue
            if (!csgAPIMethod().test(inv)) {
                continue;
            }

            // optimizing boolean expressions is only possible with 
            // union() and intersect()
            if (!"transformed".equals(inv.getMethodName())) {
                continue;
            }

            // find transformed() method execution chain
            List<Invocation> transformedChain = new ArrayList<>();
            Invocation invI1 = inv;
            while (cf.returnInvocationObjectReceiverIfPresent(invI1).isPresent()) {
                transformedChain.add(invI1);
                invI1 = cf.returnInvocationObjectReceiverIfPresent(invI1).get();
            }
            transformedChain.add(invI1);

            // only optimize real chains (#elements >=2)
            if (transformedChain.size() < 2) {
                continue;
            }

            // transformed() invocation chain found
            System.out.println(" -> opt: transformed() -> transformed()");

            Invocation firstTransformedChainInv
                    = transformedChain.get(0);
            Invocation lastTransformedChainInv
                    = transformedChain.get(transformedChain.size() - 1);

            // find arguments
            List<Argument> args
                    = transformedChain.stream().flatMap(
                            m -> m.getArguments().stream()).
                    collect(Collectors.toList());

            // add transform.apply() calls before first invocation in
            // the transformedChain
            int firstTransformPos = cf.getInvocations().
                    indexOf(firstTransformedChainInv);
            int lastTransformPos = cf.getInvocations().
                    indexOf(lastTransformedChainInv);

            // move current invocations, clear cf and add them again,
            // including modifications
            List<Invocation> origInvs = new ArrayList<>(cf.getInvocations());
            cf.getInvocations().clear();

            // apply() invocations are temporarily created in current cf
            List<Invocation> applyInvocations = new ArrayList<>();
            VisualCodeBuilder builder = VisualCodeBuilder.newInstance();

            // check whether arg is a valid variable
//            Argument firstArg = args.get(0);
//            if (!firstArg.getVariable().isPresent()) {
//                System.out.println("   -> aborting opt: arg0 is no variable!");
//                continue;
//            }
//
//            // create first object provider of apply() invocation chain
//            ObjectProvider iop = ObjectProvider.fromVariable(
//                    firstArg.getVariable().get().getName(),
//                    firstArg.getVariable().get().getType());
            applyInvocations.add(
                    builder.invokeMethod(cfs,
                            ObjectProvider.fromClassObject(
                                    OptUtils.TRANSFORM_TYPE),
                            "unity", OptUtils.TRANSFORM_TYPE));

            ObjectProvider iop = ObjectProvider.
                    fromInvocation(applyInvocations.get(0));

            // finally, build apply() invocation chain
            boolean argsAreValid = true;
            for (int argI = 0; argI < args.size(); argI++) {
                Argument a = args.get(argI);

                if (!a.getVariable().isPresent()) {
                    argsAreValid = false;
                    System.out.println(
                            "   -> aborting opt: arg" + argI
                            + " is no variable!");
                    break;
                }

                // create next object provider of apply() invocation chain
                Variable v = a.getVariable().get();
                Invocation iopInv = builder.invokeMethod(cfs,
                        iop,
                        "apply",
                        OptUtils.TRANSFORM_TYPE,
                        a);

                iop = ObjectProvider.fromInvocation(iopInv);
                applyInvocations.add(iopInv);
            }

            if (!argsAreValid) {
                continue;
            }

            // remove temporary invocations from cf and move
            // the original invocations to their previous location
            cf.getInvocations().clear();
            cf.getInvocations().addAll(origInvs);
            cf.getInvocations().addAll(firstTransformPos, applyInvocations);

            // use apply() invocation chain as argument
            lastTransformedChainInv.getArguments().set(0,
                    Argument.invArg(applyInvocations.
                            get(applyInvocations.size() - 1)));

            // delete unused transformed() invocations
            System.out.println(" -> opt: deleting original invocations");
            for (int transI = 0; transI < transformedChain.size() - 1; transI++) {
                cf.getInvocations().remove(transformedChain.get(transI));
            }

            // set the object provider to the previous one
            lastTransformedChainInv.setObjectProvider(
                    firstTransformedChainInv.getObjectProvider());

            break; // indices are messed up, will be continued in next pass
        } // end for

        return result;
    }
}

class ExpressionOptimizer implements CodeTransform<ControlFlowScope> {

    @Override
    public ControlFlowScope transform(ControlFlowScope cfs) {

        // TODO 01.08.2015 add clone()
        ControlFlowScope result = cfs;
        ControlFlow cf = cfs.getControlFlow();

        Invocation prevInv = null;
        Invocation nextInv = null;

        List<Invocation> invocationsToDelete = new ArrayList<>();

        for (int i = 0; i < cf.getInvocations().size(); i++) {

            Invocation inv = cf.getInvocations().get(i);

            if (i - 1 >= 0) {
                prevInv = cf.getInvocations().get(i - 1);
            }

            if (i + 1 < cf.getInvocations().size()) {
                nextInv = cf.getInvocations().get(i + 1);
            }

            if (inv instanceof ScopeInvocation) {
                ScopeInvocation sInv = (ScopeInvocation) inv;
                if (sInv.getScope() instanceof ControlFlowScope) {
                    transform((ControlFlowScope) sInv.getScope());
                }
                continue;
            }

            if (!csgAPIMethod().test(inv)) {
                continue;
            }

            // optimizing boolean expressions is only possible with 
            // union() and intersect()
            if (!"union".equals(inv.getMethodName())
                    && !"intersect".equals(inv.getMethodName())) {
                continue;
            }

            boolean isNoArgAndNoObjProvider
                    = !isArgOfNext(inv, nextInv)
                    && !isObjProviderOfNext(inv, nextInv);

            // eliminate if inv has no effect
            if (isNoArgAndNoObjProvider || nextInv == null) {
                System.out.println(" -> opt: inv            -> no-opt");
                invocationsToDelete.add(inv);
                continue;
            }

            if (transformSelfUnionAndIntersection(inv, nextInv)) {
                System.out.print(" -> opt: ");
                if ("union".equals(inv.getMethodName())) {
                    System.out.println("a or  a        -> a");
                } else {
                    System.out.println("a and a        -> a");
                }

                invocationsToDelete.add(inv);
                continue;
            }

            if (transformCombinedAndOr(inv, nextInv)) {
                System.out.println(" -> opt: a and (a or b) -> a");
                invocationsToDelete.add(inv);
                invocationsToDelete.add(nextInv);
                continue;
            }

        } // end for each invocation
        if (!invocationsToDelete.isEmpty()) {
            System.out.println(" -> opt: deleting original invocations");
        }
        for (Invocation invToDel : invocationsToDelete) {
//            System.out.println("   -> del: " + invToDel);
            cf.getInvocations().remove(invToDel);
        }

        return result;
    }

    private boolean transformSelfUnionAndIntersection(Invocation inv, Invocation nextInv) {
        if (isArgOfNext(inv, nextInv) && objNameEqArgName().test(inv)) {
            final Invocation nextInvF = nextInv;
            // search argument indices
            int[] argumentsToReplace = nextInv.
                    getArguments().stream().
                    filter(a -> Objects.equals(a.getInvocation().
                            orElse(null), inv)).
                    mapToInt(a -> nextInvF.
                            getArguments().indexOf(a)).toArray();

            // replace args
            for (int aIndex : argumentsToReplace) {
                nextInv.getArguments().set(aIndex,
                        inv.getArguments().get(0));
            }

            return true;

        } else if (isObjProviderOfNext(inv, nextInv) && objNameEqArgName().test(inv)) {
            Variable v = inv.getArguments().get(0).getVariable().get();
            nextInv.setObjectProvider(ObjectProvider.fromVariable(v.getName(), v.getType()));
            return true;
        }

        return false;
    }

    private boolean transformCombinedAndOr(Invocation inv, Invocation nextInv) {

        if (!isCombinedAndOr(inv, nextInv)) {
            return false;
        }

        ControlFlow cf = nextInv.getParent().getControlFlow();

        if (cf.isUsedAsInput(inv)) {
            final Invocation receiver = cf.
                    returnInvTargetIfPresent(inv).get();
            // search argument indices
            int[] argumentsToReplace = receiver.
                    getArguments().stream().
                    filter(a -> Objects.equals(a.getInvocation().
                            orElse(null), inv)).
                    mapToInt(a -> receiver.
                            getArguments().indexOf(a)).toArray();

            // replace args
            for (int aIndex : argumentsToReplace) {
                receiver.getArguments().set(aIndex, Argument.varArg(
                        cf.getParent().getVariable(inv.getObjectProvider().
                                getVariableName().get())));
            }

            return true;

        } else if (cf.returnInvocationObjectReceiverIfPresent(nextInv).isPresent()) {
            Variable v = nextInv.getArguments().get(0).getVariable().get();
            cf.returnInvocationObjectReceiverIfPresent(nextInv).get().
                    setObjectProvider(ObjectProvider.fromVariable(v.getName(),
                            v.getType()));
            return true;
        }
        return false;
    }

    private boolean isCombinedAndOr(Invocation inv, Invocation nextInv) {
        if (!"union".equals(inv.getMethodName())) {
            return false;
        }
        if (!"intersect".equals(nextInv.getMethodName())) {
            return false;
        }
        if (!isArgOfNext(inv, nextInv)) {
            return false;
        }

        if (!inv.getObjectProvider().getVariableName().isPresent()) {
            return false;
        }

        if (!nextInv.getObjectProvider().getVariableName().isPresent()) {
            return false;
        }

        if (Objects.equals(inv.getObjectProvider().getVariableName().get(),
                nextInv.getObjectProvider().getVariableName().get())) {

        }

        return true;
    }

}

class OptUtils {

    static IType TRANSFORM_TYPE = new Type("eu.mihosoft.vrl.v3d.jcsg.Transform");
    static IType CSG_TYPE = new Type("eu.mihosoft.vrl.v3d.jcsg.CSG");

    static Predicate<Invocation> selfUnion() {
        return csgAPIMethod().and(ofName("union").and(objNameEqArgName()));
    }

    static Predicate<Invocation> selfIntersect() {
        return csgAPIMethod().and(ofName("intersection").and(objNameEqArgName()));
    }

    static Predicate<Invocation> objNameEqArgName() {
        return (Invocation i) -> {
            // check for arg 0
            if (i.getArguments().isEmpty()) {
                return false;
            }
            Argument arg = i.getArguments().get(0);
            if (arg.getArgType() != ArgumentType.VARIABLE) {
                return false;
            }
            String argName = arg.getVariable().get().getName();

            // check for caller obj
            if (!i.getObjectProvider().getVariableName().isPresent()) {
                return false;
            }

            String objName = i.getObjectProvider().getVariableName().get();

            return Objects.equals(objName, argName);
        };
    }

    static Predicate<Invocation> withObjName(String objName) {
        return (Invocation i) -> {

            if (!i.getObjectProvider().getVariableName().isPresent()) {
                return false;
            }

            return Objects.equals(i.getObjectProvider().getVariableName(),
                    objName);

        };
    }

    static Predicate<Invocation> withArgName(String argName) {
        return (Invocation i) -> {

            if (i.getArguments().isEmpty()) {
                return false;
            }

            Argument arg = i.getArguments().get(0);

            if (arg.getArgType() != ArgumentType.VARIABLE) {
                return false;
            }

            return Objects.equals(arg.getVariable().get().getName(), argName);

        };
    }

    static Predicate<Invocation> csgAPIMethod() {
        return ofType(CSG_TYPE);
    }

    static Predicate<Invocation> transformAPIMethod() {
        return ofType(TRANSFORM_TYPE);
    }

    static Predicate<Invocation> objectMethod() {
        return (Invocation i) -> i.getObjectProvider().
                getVariableName().isPresent();
    }

    static Predicate<Invocation> ofName(String mName) {
        return (Invocation i) -> Objects.equals(i.getMethodName(), mName);
    }

    static Predicate<Invocation> ofType(IType t) {
        return (Invocation i) -> {
            if (!i.getObjectProvider().getType().isPresent()) {
                return false;
            } else {
                return Objects.equals(i.getObjectProvider().
                        getType().get(), t);
            }
        };
    }

    static boolean isObjProviderOfNext(Invocation inv, Invocation nextInv) {
        return InstrumentCode.isRetValObjectOfNextInv(inv, nextInv);
    }

    static boolean isArgOfNext(Invocation inv, Invocation nextInv) {
        return nextInv.
                getArguments().stream().
                filter(a -> Objects.equals(a.getInvocation().
                        orElse(null), inv)).count() > 0;
    }
}
