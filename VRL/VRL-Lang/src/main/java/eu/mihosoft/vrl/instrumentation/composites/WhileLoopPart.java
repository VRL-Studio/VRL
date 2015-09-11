package eu.mihosoft.vrl.instrumentation.composites;

import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.IArgument;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;
import eu.mihosoft.vrl.lang.model.WhileDeclaration;

public class WhileLoopPart
		extends
		AbstractCodeBuilderPart<WhileStatement, WhileDeclaration, ControlFlowScope> {

	public WhileLoopPart(SourceUnit sourceUnit, VisualCodeBuilder builder,
			CodeLineColumnMapper mapper) {
		super(sourceUnit, builder, mapper);
	}

	@Override
	public WhileDeclaration transform(WhileStatement s,
			ControlFlowScope parent, TransformContext context) {

		IArgument arg = convertToArgument("WhileLoop.condition", s
				.getBooleanExpression().getExpression(), context);

		WhileDeclaration inv = builder.invokeWhileLoop(parent, arg);

		setCodeRange(inv, s);
		addCommentsToScope(inv, comments);

		return inv;
	}

	@Override
	public Class<WhileStatement> getAcceptedType() {
		return WhileStatement.class;
	}

	@Override
	public Class<ControlFlowScope> getParentType() {
		return ControlFlowScope.class;
	}
}
