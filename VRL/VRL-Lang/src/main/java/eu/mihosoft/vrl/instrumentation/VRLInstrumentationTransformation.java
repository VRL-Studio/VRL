/* 
 * VRLInstrumentationTransformation.java
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

import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.transform.StaticTypesTransformation;

/**
 * Adds instrumentation to each method call. Use {@link VRLInstrumentation} to
 * request this transformation.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class VRLInstrumentationTransformation implements ASTTransformation {

    @Override
    public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {

        StaticTypesTransformation transformation = new StaticTypesTransformation();
        transformation.visit(astNodes, sourceUnit);

        // create transformer instance
        MethodCallExpressionTransformer transformer
                = new MethodCallExpressionTransformer(sourceUnit);

        // apply transformation for each class in the specified source unit
        for (ClassNode clsNode : sourceUnit.getAST().getClasses()) {

            transformer.visitClass(clsNode);
        }
    }
}

/**
 * Concrete transformer class.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class MethodCallExpressionTransformer extends ClassCodeExpressionTransformer {

    private final SourceUnit unit;
    private int blockDepth;

    public MethodCallExpressionTransformer(final SourceUnit unit) {
        this.unit = unit;
    }

    @Override
    public void visitBlockStatement(BlockStatement bs) {
        blockDepth++;
        super.visitBlockStatement(bs);
        blockDepth--;
    }

    @Override
    public Expression transform(Expression exp) {

//        System.out.println(" --> transform: " + exp);
        // don't transform if expression is null
        if (exp == null) {
            System.out.println(" --> NULL");
            return exp;
        }

        // instrument nested method calls inside closure blocks
        if (exp instanceof ClosureExpression) {
            ClosureExpression cExp = (ClosureExpression) exp;
            if (cExp.getCode() instanceof BlockStatement) {
                BlockStatement bs = (BlockStatement) cExp.getCode();

                for (Statement s : bs.getStatements()) {
                    if (s instanceof ExpressionStatement) {

                        ExpressionStatement es = (ExpressionStatement) s;

                        es.setExpression(es.getExpression().transformExpression(this));
                    }
                }
            } // end if is blockStatement
        }

        // we ignore other expressions as we only want to transform/instrument
        // method calls

        Expression result = null;
        if (exp instanceof MethodCallExpression) {
            result = instrumentNonStaticMethodCall(exp);
        } else if (exp instanceof StaticMethodCallExpression) {
            result = instrumentStaticMethodCall(exp);
        }

        if (result != null) {
            return result;
        }

        return exp.transformExpression(this);
    }

    private Expression instrumentNonStaticMethodCall(Expression exp) {
        // we have a method call
        MethodCallExpression methodCall = (MethodCallExpression) exp;

        if (methodCall.getMethod().getText().equals("__instrumentCode")) {
            System.out.println(" --> skipping already instrumented code");
            return exp;
        }

        // original method args
        ArgumentListExpression mArgs
                = (ArgumentListExpression) methodCall.getArguments();

        // instrument argument list (possible method calls as arguments )
        mArgs = (ArgumentListExpression) mArgs.transformExpression(this);

        // instrument method args (possible method calls inside)
        ArgumentListExpression instrumentedMargs = new ArgumentListExpression();
        for (Expression mArg : mArgs.getExpressions()) {
            Expression instrumentedArg = mArg.transformExpression(this);
            instrumentedMargs.addExpression(instrumentedArg);
            System.out.println(" -> arg: " + instrumentedArg.getText());
        }

        mArgs = instrumentedMargs;

        System.out.println(">> instrumenting method "
                + methodCall.getMethod().getText() + " : " + methodCall.getClass());

        // name of the object
        String objName = methodCall.getObjectExpression().getText();
        // name of the type of the object
        String typeName = methodCall.getObjectExpression().getType().getName();

        // if both are equal, static method call detected
        boolean staticCall = typeName.equals(objName);

        // create a new argument list with object the method belongs to
        // as first parameter and the method name as second parameter
        List<Expression> finalArgExpressions = new ArrayList<>();

        finalArgExpressions.add(new ConstantExpression(blockDepth));
        finalArgExpressions.add(new ConstantExpression(staticCall));
        finalArgExpressions.add(methodCall.getObjectExpression());
        finalArgExpressions.add(new ConstantExpression(methodCall.getMethod().getText()));

        ArgumentListExpression finalArgs = new ArgumentListExpression(finalArgExpressions);

        // add original method args to argument list of the instrumentation
        // method
        for (Expression e : mArgs.getExpressions()) {

            finalArgs.addExpression(e);
        }
//        try {
//            System.out.println(" --> RET " + methodCall.getMethodTarget().getReturnType());
//        } catch (Exception ex) {
//            System.out.println(" --> VOID");
//        }

        // create a static method call to the instrumentation method which
        // calls the original method via reflection
        return new StaticMethodCallExpression(
                new ClassNode(VRLInstrumentationUtil.class),
                "__instrumentCode", finalArgs);
    }

    private Expression instrumentStaticMethodCall(Expression exp) {
        // we have a method call
        StaticMethodCallExpression methodCall = (StaticMethodCallExpression) exp;

        if (methodCall.getMethod().equals("__instrumentCode")) {
            System.out.println(" --> skipping already instrumented code");
            return exp;
        }

        // original method args
        ArgumentListExpression mArgs
                = (ArgumentListExpression) methodCall.getArguments();

        // instrument argument list (possible method calls as arguments )
        mArgs = (ArgumentListExpression) mArgs.transformExpression(this);

        // instrument method args (possible method calls inside)
        ArgumentListExpression instrumentedMargs = new ArgumentListExpression();
        for (Expression mArg : mArgs.getExpressions()) {

            Expression instrumentedArg = mArg.transformExpression(this);

            instrumentedMargs.addExpression(instrumentedArg);
            System.out.println(" -> arg: " + instrumentedArg);
        }

        mArgs = instrumentedMargs;

        System.out.println(">> instrumenting method "
                + methodCall.getMethod() + " : " + methodCall.getClass());

        // name of the type of the object
//        String typeName = methodCall.getType().getName();
        // create a new argument list with object the method belongs to
        // as first parameter and the method name as second parameter
        List<Expression> finalArgExpressions = new ArrayList<>();

        finalArgExpressions.add(new ConstantExpression(blockDepth));
        finalArgExpressions.add(new ConstantExpression(true));
        finalArgExpressions.add(new ClassExpression(methodCall.getOwnerType()));
        finalArgExpressions.add(new ConstantExpression(methodCall.getMethod()));

        ArgumentListExpression finalArgs = new ArgumentListExpression(finalArgExpressions);

        // add original method args to argument list of the instrumentation
        // method
        for (Expression e : mArgs.getExpressions()) {

            finalArgs.addExpression(e);
        }

//        try {
//            System.out.println(" --> RET " + methodCall.);
//        } catch (Exception ex) {
//            System.out.println(" --> VOID");
//        }
        // create a static method call to the instrumentation method which
        // calls the original method via reflection
        return new StaticMethodCallExpression(
                new ClassNode(VRLInstrumentationUtil.class),
                "__instrumentCode", finalArgs);
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return unit;
    }
}
