package eu.mihosoft.vrl.instrumentation.composites;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.ElseDeclaration;
import eu.mihosoft.vrl.lang.model.IfDeclaration;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class ElsePart extends
		AbstractCodeBuilderPart<BlockStatement, ElseDeclaration, IfDeclaration> {

	public ElsePart(StateMachine stateMachine, SourceUnit sourceUnit,
			VisualCodeBuilder builder, CodeLineColumnMapper mapper) {
		super(stateMachine, sourceUnit, builder, mapper);
	}

	@Override
	public ElseDeclaration transform(BlockStatement obj, IfDeclaration parent,
			TransformContext ctx) {
		if (parent.getControlFlow().getInvocations().size() == 1) {
			return builder.invokeElse((ControlFlowScope) parent.getParent());
		} else {
			return null;
		}
	}

	@Override
	public Class<BlockStatement> getAcceptedType() {
		return BlockStatement.class;
	}

	@Override
	public Class<IfDeclaration> getParentType() {
		return IfDeclaration.class;
	}

}
