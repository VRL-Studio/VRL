/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model.transform;

import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
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
        
        return result;
    }
}

class InstrumentControlFlowScope implements CodeTransform<ControlFlowScope> {

    @Override
    public ControlFlowScope transform(ControlFlowScope ce) {
        // TODO 01.08.2015 add clone()
        ControlFlowScope result = ce;
        
        
        
        return result;
    }
    
}
