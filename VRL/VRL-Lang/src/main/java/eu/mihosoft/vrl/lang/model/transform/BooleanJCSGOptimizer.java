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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author miho
 */
public class BooleanJCSGOptimizer implements CodeTransform<CompilationUnitDeclaration> {

    private final ExpressionOptimizer optimizer = new ExpressionOptimizer();

    @Override
    public CompilationUnitDeclaration transform(CompilationUnitDeclaration ce) {
        for (ClassDeclaration cD : ce.getDeclaredClasses()) {
            for (MethodDeclaration mD : cD.getDeclaredMethods()) {
                optimizer.transform(mD);
            }
        }

        return ce;
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

            if (!csgMethod().test(inv)) {
                continue;
            }

            if (!"union".equals(inv.getMethodName())
                    && !"intersect".equals(inv.getMethodName())) {
                continue;
            }

            boolean isNoArgAndNoObjProvider
                    = !isArgOfNext(inv, nextInv)
                    && !isObjProviderOfNext(inv, nextInv);

            // eliminate if inv has no effect
            if (isNoArgAndNoObjProvider || nextInv == null) {
                invocationsToDelete.add(inv);
                continue;
            }

            if (transformSelfUnionAndIntersection(inv, nextInv)) {
                invocationsToDelete.add(inv);
                continue;
            }

            if (transformCombinedAndOr(inv, nextInv)) {
                invocationsToDelete.add(inv);
                invocationsToDelete.add(nextInv);
                continue;
            }

        } // end for each invocation

        for (Invocation invToDel : invocationsToDelete) {
            System.out.println("-> rem: " + invToDel);
            cf.getInvocations().remove(invToDel);
        }

        return result;
    }

    private boolean transformSelfUnionAndIntersection(Invocation inv, Invocation nextInv) {
        if (isArgOfNext(inv, nextInv) && objNameEqArgName().test(inv)) {
            System.out.println("-> csg is arg");
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
                System.out.println("-> replace arg " + aIndex);
                nextInv.getArguments().set(aIndex,
                        inv.getArguments().get(0));
            }

            return true;

        } else if (isObjProviderOfNext(inv, nextInv) && objNameEqArgName().test(inv)) {
            System.out.println("-> csg is objProvider");
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
        
        System.out.println("-> combined and or");

        ControlFlow cf = nextInv.getParent().getControlFlow();

        if (cf.isUsedAsInput(inv)) {
            System.out.println("-> csg is arg");
            final Invocation receiver = cf.
                    returnInvTargetIfPresent(inv).get();
            System.out.println(" -> in " + receiver);
            // search argument indices
            int[] argumentsToReplace = receiver.
                    getArguments().stream().
                    filter(a -> Objects.equals(a.getInvocation().
                            orElse(null), inv)).
                    mapToInt(a -> receiver.
                            getArguments().indexOf(a)).toArray();

            // replace args
            for (int aIndex : argumentsToReplace) {
                System.out.println("-> replace arg " + aIndex);
                receiver.getArguments().set(aIndex,Argument.varArg(
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

    static Predicate<Invocation> selfUnion() {
        return csgMethod().and(ofName("union").and(objNameEqArgName()));
    }

    static Predicate<Invocation> selfIntersect() {
        return csgMethod().and(ofName("intersection").and(objNameEqArgName()));
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

    static Predicate<Invocation> csgMethod() {
        return objectMethod().and(ofType(new Type("eu.mihosoft.vrl.v3d.jcsg.CSG")));
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

    private boolean isObjProviderOfNext(Invocation inv, Invocation nextInv) {
        return InstrumentCode.isRetValObjectOfNextInv(inv, nextInv);
    }

    private boolean isArgOfNext(Invocation inv, Invocation nextInv) {
        return nextInv.
                getArguments().stream().
                filter(a -> Objects.equals(a.getInvocation().
                        orElse(null), inv)).count() > 0;
    }

}
