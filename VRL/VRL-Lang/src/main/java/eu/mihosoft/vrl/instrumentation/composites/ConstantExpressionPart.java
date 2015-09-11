package eu.mihosoft.vrl.instrumentation.composites;

import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ConstantValue;
import eu.mihosoft.vrl.lang.model.ConstantValueFactory;
import eu.mihosoft.vrl.lang.model.IType;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class ConstantExpressionPart extends
		AbstractCodeBuilderPart<ConstantExpression, ConstantValue, CodeEntity> {

	public ConstantExpressionPart(SourceUnit sourceUnit,
			VisualCodeBuilder builder, CodeLineColumnMapper mapper) {
		super(sourceUnit, builder, mapper);
	}

	@Override
	public ConstantValue transform(ConstantExpression obj, CodeEntity parent,
			TransformContext ctx) {

		IType type;
		if (obj.getValue() == null) {
			type = Type.VOID;
		} else {
			type = new Type(obj.getType().getName());
		}
		ConstantValue value = ConstantValueFactory.createConstantValue(
				obj.getValue(), type);
		return value;
	}

	@Override
	public Class<ConstantExpression> getAcceptedType() {
		return ConstantExpression.class;
	}

	@Override
	public Class<CodeEntity> getParentType() {
		return CodeEntity.class;
	}

}
