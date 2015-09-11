package eu.mihosoft.vrl.instrumentation.composites;

import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.VSource;
import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.IArgument;
import eu.mihosoft.vrl.lang.model.IType;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class MethodCallExpressionPart extends
		AbstractCodeBuilderPart<MethodCallExpression, Invocation, CodeEntity> {

	public MethodCallExpressionPart(SourceUnit sourceUnit,
			VisualCodeBuilder builder, CodeLineColumnMapper mapper) {
		super(sourceUnit, builder, mapper);
	}

	@Override
	public Invocation transform(MethodCallExpression s, CodeEntity centity,
			TransformContext ctx) {
		ControlFlowScope parent = getParentScope(centity,
				ControlFlowScope.class);
		ArgumentListExpression args = (ArgumentListExpression) s.getArguments();
		IArgument[] arguments = convertArguments(
				"MethodCallExpression.methodArguments", args, ctx);

		String objectName = null;

		boolean isIdCall = false;

		if (s.getObjectExpression() instanceof VariableExpression) {
			VariableExpression ve = (VariableExpression) s
					.getObjectExpression();
			objectName = ve.getName();
		} else if (s.getObjectExpression() instanceof ClassExpression) {
			ClassExpression ce = (ClassExpression) s.getObjectExpression();
			objectName = ce.getType().getName();

			if (ce.getType().getName().equals(VSource.class.getName())) {
				isIdCall = true;
			}
		}

		IType returnType = convertMethodReturnType(s);

		if (!isIdCall) {
			if (objectName != null) {

				Invocation invocation = builder.invokeMethod(parent,
						objectName, s.getMethod().getText(), returnType,
						arguments);

				setCodeRange(invocation, s);
				addCommentsToScope(parent, comments);
				return invocation;

			} else if (s.getMethod().getText().equals("println")) {
				// codeBuilder.invokeStaticMethod(currentScope, new
				// Type("System.out"), s.getMethod().getText(), isVoid,
				// returnValueName, arguments).setCode(getCode(s));
				Invocation invocation = builder.invokeStaticMethod(parent,
						new Type("System.out"), s.getMethod().getText(),
						Type.VOID, arguments);
				setCodeRange(invocation, s);
				addCommentsToScope(parent, comments);
				return invocation;
			}
		}
		throw new IllegalArgumentException(
				"Unsupported type of MethodCallExpression: " + s);
	}

	@Override
	public Class<MethodCallExpression> getAcceptedType() {
		return MethodCallExpression.class;
	}

	@Override
	public Class<CodeEntity> getParentType() {
		return CodeEntity.class;
	}
}
