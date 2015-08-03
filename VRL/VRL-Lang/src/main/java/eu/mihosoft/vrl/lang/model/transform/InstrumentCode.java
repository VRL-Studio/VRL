/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.transform;

import eu.mihosoft.vrl.instrumentation.VRLInstrumentationUtil;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.IArgument;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.Scope2Code;
import eu.mihosoft.vrl.lang.model.Type;
import java.util.ArrayList;
import java.util.List;

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
        
        for(ClassDeclaration cd : result.getDeclaredClasses()) {
            List<MethodDeclaration> methods = new ArrayList<>();
            for(MethodDeclaration md : cd.getDeclaredMethods()) {
                methods.add((MethodDeclaration)im.transform(md));
            }
            cd.getDeclaredMethods().clear();
            cd.getDeclaredMethods().addAll(methods);
        }
        
        System.out.println(Scope2Code.getCode(cu));
        
        return result;
    }
}

class InstrumentControlFlowScope implements CodeTransform<ControlFlowScope> {

    @Override
    public ControlFlowScope transform(ControlFlowScope ce) {
        // TODO 01.08.2015 add clone()

        
        ControlFlowScope result = ce;
        
        List<Invocation> invocations = new ArrayList<>();
        invocations.addAll(ce.getControlFlow().getInvocations());
        
        result.getControlFlow().getInvocations().clear();
        
        for(Invocation i : invocations) {
            System.out.println("i : " + i);
            result.getControlFlow().callMethod(
                    "", "this", "println", Type.VOID,
                    Argument.constArg(
                            Type.STRING, "pre-m-call: " + i.toString()));
            result.getControlFlow().getInvocations().add(i);
            result.getControlFlow().callMethod(
                    "", "this", "println", Type.VOID,
                    Argument.constArg(
                            Type.STRING, "post-m-call: " + i.toString()));
            if (i instanceof ScopeInvocation) {
                ScopeInvocation si = (ScopeInvocation) i;
                transform((ControlFlowScope) si.getScope());
            }
        }
        
//        for(Invocation i : invocations) {
//            System.out.println("i : " + i);
//            
//            result.getControlFlow().callStaticMethod("",
//                   Type.fromClass(VRLInstrumentationUtil.class),
//                    "__instrumentCode", i.getMethodName(),
//                    i.getArguments().toArray(new IArgument[i.getArguments().size()]));
//        }
        
        return result;
    }
    
}
