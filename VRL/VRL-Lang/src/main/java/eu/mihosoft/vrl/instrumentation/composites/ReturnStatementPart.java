package eu.mihosoft.vrl.instrumentation.composites;

import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.IArgument;
import eu.mihosoft.vrl.lang.model.ReturnStatementInvocation;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class ReturnStatementPart
		extends
		AbstractCodeBuilderPart<ReturnStatement, ReturnStatementInvocation, ControlFlowScope> {

	public ReturnStatementPart(SourceUnit unit, VisualCodeBuilder builder,
			CodeLineColumnMapper mapper) {
		super(unit, builder, mapper);
	}

	@Override
	public ReturnStatementInvocation transform(ReturnStatement s,
			ControlFlowScope parent, TransformContext context) {

		IArgument arg = convertToArgument("ReturnStatement.returnValue",
				s.getExpression(), context);
		ReturnStatementInvocation inv = builder.returnValue(parent, arg);
		return inv;
	}

	@Override
	public void postTransform(ReturnStatementInvocation obj,
			ReturnStatement in, ControlFlowScope parent,
			TransformContext context) {

	}

	@Override
	public Class<ReturnStatement> getAcceptedType() {
		return ReturnStatement.class;
	}

	@Override
	public Class<ControlFlowScope> getParentType() {
		return ControlFlowScope.class;
	}
}
