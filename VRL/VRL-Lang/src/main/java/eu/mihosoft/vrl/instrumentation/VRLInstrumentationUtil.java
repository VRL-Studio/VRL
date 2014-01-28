package eu.mihosoft.vrl.instrumentation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VRLInstrumentationUtil {
    // http://groovy.codehaus.org/Compile-time+Metaprogramming+-+AST+Transformations
    // http://hamletdarcy.blogspot.de/2009/01/groovy-compile-time-meta-magic.html
    // methodcall transformations:
    // http://groovy.329449.n5.nabble.com/AST-Transforming-a-MethodCallExpression-td5711841.html
    // type transformations:
    // http://groovy.329449.n5.nabble.com/is-possible-an-AST-transformation-to-convert-all-BigDecimals-to-doubles-in-GroovyLab-td5711461.html

    /**
     * Do not call manually! This method will be used by AST transformations to
     * instrument method calls.
     *
     * @param staticCall defines whether method call is a static method call
     * @param o instance the method belongs to
     * @param mName method name
     * @param args method arguments
     * @return return value of the method that shall be instrumented
     * @throws Throwable
     */
    public static Object __instrumentCode(boolean staticCall, Object o, String mName, Object[] args) throws Throwable {

        System.out.println(" --> calling " + mName + "(...)");

        Object result = null;

        if (staticCall) {
            result = org.codehaus.groovy.runtime.InvokerHelper.invokeStaticMethod((Class<?>) o, mName, args);
        } else {
            result = org.codehaus.groovy.runtime.InvokerHelper.invokeMethod(o, mName, args);
        }
        
        System.out.println(" --> returning " + result);

        return result;
    }
    
    /**
     * Do not call manually! This method will be used by AST transformations to
     * instrument method calls.
     *
     * @param staticCall defines whether method call is a static method call
     * @param scopeId position in scope
     * @param o instance the method belongs to
     * @param mName method name
     * @param args method arguments
     * @return return value of the method that shall be instrumented
     * @throws Throwable
     */
    public static Object __instrumentCode(int scopeId, boolean staticCall, Object o, String mName, Object[] args) throws Throwable {

        System.out.println(" --> calling " + mName + "(...): scope=" + scopeId);

        Object result = null;

        if (staticCall) {
            result = org.codehaus.groovy.runtime.InvokerHelper.invokeStaticMethod((Class<?>) o, mName, args);
        } else {
            result = org.codehaus.groovy.runtime.InvokerHelper.invokeMethod(o, mName, args);
        }
        
        System.out.println(" --> returning " + result);

        return result;
    }
}
