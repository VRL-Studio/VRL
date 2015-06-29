package eu.mihosoft.vrl.instrumentation.composites;

import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.Variable;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class PropertyExpressionPart extends
		AbstractCodeBuilderPart<PropertyExpression, Variable, CodeEntity> {

	public PropertyExpressionPart(StateMachine stateMachine,
			SourceUnit sourceUnit, VisualCodeBuilder builder,
			CodeLineColumnMapper mapper) {
		super(stateMachine, sourceUnit, builder, mapper);
	}

	@Override
	public Variable transform(PropertyExpression obj, CodeEntity ce,
			TransformContext ctx) {
		Scope parent = getParentScope(ce, Scope.class);
		// TODO generate proper model structure for properties
		Variable v = parent.getVariable(
				obj.getObjectExpression().getText());
		setCodeRange(v, obj);
		return v;
	}

	@Override
	public Class<PropertyExpression> getAcceptedType() {
		return PropertyExpression.class;
	}

	@Override
	public Class<CodeEntity> getParentType() {
		return CodeEntity.class;
	}

}
