package eu.mihosoft.vrl.instrumentation.composites;

import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.BreakInvocation;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class BreakPart
		extends
		AbstractCodeBuilderPart<BreakStatement, BreakInvocation, ControlFlowScope> {

	public BreakPart(SourceUnit sourceUnit, VisualCodeBuilder builder,
			CodeLineColumnMapper mapper) {
		super(sourceUnit, builder, mapper);
	}

	@Override
	public BreakInvocation transform(BreakStatement obj,
			ControlFlowScope parent, TransformContext context) {
		return builder.invokeBreak(parent);
	}

	@Override
	public Class<BreakStatement> getAcceptedType() {
		return BreakStatement.class;
	}

	@Override
	public Class<ControlFlowScope> getParentType() {
		return ControlFlowScope.class;
	}
}
