/* 
 * GroovyFunction1D.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2007–2017 by Michael Hoffer
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
 * First, the following text must be displayed on the Canvas:
 * "based on VRL source code". In this case the VRL canvas icon must be removed.
 * 
 * Second, the copyright notice must remain. It must be reproduced in any
 * program that uses VRL.
 *
 * Third, add an additional notice, stating that you modified VRL. A suitable
 * notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Please cite the publication(s) listed below.
 *
 * Publications:
 *
 * M. Hoffer, C. Poliwoda, & G. Wittum. (2013). Visual reflection library:
 * a framework for declarative GUI programming on the Java platform.
 * Computing and Visualization in Science, 2013, 16(4),
 * 181–192. http://doi.org/10.1007/s00791-014-0230-y
 */

package eu.mihosoft.vrl.math;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.io.Serializable;

/**
 * <p>
 * A function defined by a groovy string. It can be used to define functions
 * at runtime. The performance will be significantly worse than native
 * java code. But it is much more flexible.
 * </p>
 * <p>
 * The function must consist of an expression that does not use other variables
 * than <code>x</code>. All functions from
 * <code>java.lang.Math</code> can be used.
 * </p>
 * <p>
 * Example:
 * <pre>
 * <code>
 * </code>
 * sin(sqrt(x*x))
 * </code>
 * </p>
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class GroovyFunction1D implements Serializable, Function1D {

    private static final long serialVersionUID = -9203551757468880846L;
    private String expression;
    private transient Script script;
    private String xVarName = "x";

    /**
     * Constructor.
     */
    public GroovyFunction1D() {
        //
    }

    /**
     * Constructor.
     * @param expression the expression that defines the function.
     */
    public GroovyFunction1D(String expression) {
        setExpression(expression);
    }
    
    /**
     * Constructor.
     * @param expression the expression that defines the function.
     * @param xVarName custom name of the x variable
     */
    public GroovyFunction1D(String expression, String xVarName) {
        setExpression(expression);
        setXVarName(xVarName);
    }

    /**
     * Returns the expression of the function
     * @return the expression of the function
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Defines the expression of the function.
     * @param expression the expression to set
     */
    public void setExpression(String expression) {
        this.expression = expression;
        GroovyShell shell = new GroovyShell();
        script = shell.parse("import static java.lang.Math.*; result = (" +
                expression + ") as Double");
    }

    @Override
    public Double run(Double x) {
        getScript().setProperty(getXVarName(), x);

        getScript().run();

        return (Double) getScript().getProperty("result");
    }

    /**
     * Returns the groovy script that is used to evaluate the expression.
     * @return the groovy script that is used to evaluate the expression
     */
    public Script getScript() {
        return script;
    }

    /**
     * @return the xValueName
     */
    public String getXVarName() {
        return xVarName;
    }

    /**
     * @param xValueName the xValueName to set
     */
    public void setXVarName(String xValueName) {
        this.xVarName = xValueName;
    }
}
