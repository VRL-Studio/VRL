/* 
 * VRLInstrumentationUtil.java
 *
 * Copyright (c) 2009–2014 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2014 by Michael Hoffer
 * 
 * This file is part of Visual Reflection Library (VRL).
 *
 * VRL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * see: http://opensource.org/licenses/LGPL-3.0
 *      file://path/to/VRL/src/eu/mihosoft/vrl/resources/license/lgplv3.txt
 *
 * VRL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * This version of VRL includes copyright notice and attribution requirements.
 * According to the LGPL this information must be displayed even if you modify
 * the source code of VRL. Neither the VRL Canvas attribution icon nor any
 * copyright statement/attribution may be removed.
 *
 * Attribution Requirements:
 *
 * If you create derived work you must do three things regarding copyright
 * notice and author attribution.
 *
 * First, the following text must be displayed on the Canvas or an equivalent location:
 * "based on VRL source code".
 * 
 * Second, the copyright notice must remain. It must be reproduced in any
 * program that uses VRL.
 *
 * Third, add an additional notice, stating that you modified VRL. In addition
 * you must cite the publications listed below. A suitable notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Publications:
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, in press.
 */

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
