package eu.mihosoft.vrl.instrumentation.composites;

import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.IArgument;
import eu.mihosoft.vrl.lang.model.IfDeclaration;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class IfStatementPart extends
		AbstractCodeBuilderPart<IfStatement, IfDeclaration, ControlFlowScope> {

	public IfStatementPart(SourceUnit sourceUnit, VisualCodeBuilder builder,
			CodeLineColumnMapper mapper) {
		super(sourceUnit, builder, mapper);
	}

	@Override
	public IfDeclaration transform(IfStatement s,
			ControlFlowScope currentScope, TransformContext context) {

		IArgument condition = convertToArgument("IfStatement.condition",
				s.getBooleanExpression(), context);

		IfDeclaration decl = null;

		if (currentScope instanceof IfDeclaration) {
			decl = builder.invokeElseIf(
					(ControlFlowScope) currentScope.getParent(), condition);
		} else {
			decl = builder.invokeIf(currentScope, condition);
		}

		setCodeRange(decl, s);
		addCommentsToScope(decl, comments);
		return decl;
	}

	@Override
	public Class<IfStatement> getAcceptedType() {
		return IfStatement.class;
	}

	@Override
	public Class<ControlFlowScope> getParentType() {
		return ControlFlowScope.class;
	}
}
