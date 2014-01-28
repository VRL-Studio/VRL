/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.instrumentation;

import org.codehaus.groovy.transform.*;
import java.lang.annotation.*;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.control.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.ast.expr.*;

/**
 * This annotation triggers method call instrumentation.
 * 
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.TYPE])
@GroovyASTTransformationClass(["eu.mihosoft.vrl.instrumentation.VRLInstrumentationTransformation"])
public @interface VRLInstrumentation {
    
}
