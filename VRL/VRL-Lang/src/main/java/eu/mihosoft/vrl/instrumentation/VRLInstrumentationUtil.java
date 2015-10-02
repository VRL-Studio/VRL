/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

import eu.mihosoft.vrl.lang.model.ControlFlow;
import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.ObjectProvider;
import eu.mihosoft.vrl.lang.model.ScopeInvocation;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.lang.model.WhileDeclaration;

/**
 * Instrumentation utility class for model based instrumentation.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VRLInstrumentationUtil {
    
    private static final InstrumentationEventSender eventSender
            = new InstrumentationEventSender();

    /**
     * Generates a pre event invocation for the specified invocation.
     *
     * @param cf controlflow
     * @param inv invocation
     * @return the generated pre event invocation
     */
    public static Invocation generatePreEvent(ControlFlow cf, Invocation inv) {
        Argument[] args;
        // for while-loops we don't add the arguments to the event invocation
        // since they are unknown before we enter the loop body
        if (inv instanceof ScopeInvocation
                && ((ScopeInvocation) inv).getScope() instanceof WhileDeclaration) {
            args = new Argument[2];
            args[0] = Argument.constArg(Type.STRING, inv.getId());
            args[1] = Argument.constArg(Type.STRING, inv.getMethodName());
        } else {
            // ... for all other invocations we add all arguments since they
            // are known at event invocation time
            args = new Argument[inv.getArguments().size() + 2];
            
            args[0] = Argument.constArg(Type.STRING, inv.getId());
            args[1] = Argument.constArg(Type.STRING, inv.getMethodName());
            
            for (int i = 0; i < inv.getArguments().size(); i++) {
                args[i + 2] = inv.getArguments().get(i);
            }
        }
        
        return cf.callMethod(
                "",
                ObjectProvider.fromClassObject(
                        Type.fromClass(VRLInstrumentationUtil.class)),
                "__preEvent", Type.VOID, args);
    }

    /**
     * Generates a post event invocation for the specified invocation.
     *
     * @param cf controlflow
     * @param inv invocation
     * @param retValArg return value argument of the specified invocation
     * @return the generated post event invocation
     */
    public static Invocation generatePostEvent(ControlFlow cf,
            Invocation inv, Argument retValArg) {
        
        Argument[] args = new Argument[3];
        
        args[0] = Argument.constArg(Type.STRING, inv.getId());
        args[1] = Argument.constArg(Type.STRING, inv.getMethodName());
        args[2] = retValArg;
        
        return cf.callMethod(
                "",
                ObjectProvider.fromClassObject(
                        Type.fromClass(VRLInstrumentationUtil.class)),
                "__postEvent", Type.VOID, args);
    }

    /**
     * Generates a post event invocation for the specified invocation.
     *
     * @param cf controlflow
     * @param inv invocation
     * @return the generated post event invocation
     */
    public static Invocation generatePostEvent(ControlFlow cf,
            Invocation inv) {
        Argument[] args = new Argument[2];
        
        args[0] = Argument.constArg(Type.STRING, inv.getId());
        args[1] = Argument.constArg(Type.STRING, inv.getMethodName());
        
        return cf.callMethod(
                "",
                ObjectProvider.fromClassObject(Type.fromClass(
                        VRLInstrumentationUtil.class)),
                "__postEvent", Type.VOID, args);
    }

    /**
     * Don't call this method manually. It is designed for the automatic model
     * based instrumentation only! API is subject to change.
     *
     * @param id invocation id
     * @param invName invocation name
     * @param args invocation arguments
     * @deprecated
     */
    @Deprecated()
    public static void __preEvent(String id, String invName, Object... args) {
        
        InstrumentationEvent evt = new InstrumentationEventImpl(
                InstrumentationEventType.PRE_INVOCATION,
                new InstrumentationSourceImpl(id, invName, args, null, true));
        
        eventSender.fireEvent(evt);
    }

    /**
     * Don't call this method manually. It is designed for the automatic model
     * based instrumentation only! API is subject to change.
     *
     * @param id invocation id
     * @param invName invocation name
     * @param retVal return value
     * @deprecated
     */
    @Deprecated()
    public static void __postEvent(String id, String invName, Object retVal) {
        
        InstrumentationEvent evt = new InstrumentationEventImpl(
                InstrumentationEventType.POST_INVOCATION,
                new InstrumentationSourceImpl(id, invName, null, retVal, true));
        
        eventSender.fireEvent(evt);
    }

    /**
     * Don't call this method manually. It is designed for the automatic model
     * based instrumentation only! API is subject to change.
     *
     * @param id invocation id
     * @param invName invocation name
     * @deprecated
     */
    @Deprecated()
    public static void __postEvent(String id, String invName) {
        
        InstrumentationEvent evt = new InstrumentationEventImpl(
                InstrumentationEventType.POST_INVOCATION,
                new InstrumentationSourceImpl(id, invName, null, null, true));
        
        eventSender.fireEvent(evt);
    }
    
    /**
     * Adds the specified instrumentation event handler.
     * @param type event type
     * @param eventHandler event handler that shall be registered
     */
    public static void addEventHandler(InstrumentationEventType type,
            InstrumentationEventHandler eventHandler) {
        eventSender.addEventHandler(type, eventHandler);
    }
    
    /**
     * Removes the specified instrumentation event handler.
     * @param type event type
     * @param eventHandler event handler that shall be removed
     */
    public static void removeEventHandler(InstrumentationEventType type,
            InstrumentationEventHandler eventHandler) {
        eventSender.removeEventHandler(type, eventHandler);
    }
}
