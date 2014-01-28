/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
        MethodCallExpressionTransformer transformer =
                new MethodCallExpressionTransformer(sourceUnit);

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

        // instrument nested method calls inside cloruse blocks
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
        if (!(exp instanceof MethodCallExpression)) {

            return exp.transformExpression(this);
        }

        // we have a method call
        MethodCallExpression methodCall = (MethodCallExpression) exp;

        if (methodCall.getMethod().getText().equals("__instrumentCode")) {
            System.out.println(" --> skipping already instrumented code");
            return exp;
        }

        // original method args
        ArgumentListExpression mArgs =
                (ArgumentListExpression) methodCall.getArguments();

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
        try {
            System.out.println(" --> RET " + methodCall.getMethodTarget().getReturnType());
        } catch (Exception ex) {
            System.out.println(" --> VOID");
        }

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
