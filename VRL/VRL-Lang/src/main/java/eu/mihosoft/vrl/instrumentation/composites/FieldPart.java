package eu.mihosoft.vrl.instrumentation.composites;

import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.DeclarationInvocation;
import eu.mihosoft.vrl.lang.model.IModifiers;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class FieldPart
		extends
		AbstractCodeBuilderPart<FieldNode, DeclarationInvocation, ClassDeclaration> {

	public FieldPart(SourceUnit sourceUnit, VisualCodeBuilder builder,
			CodeLineColumnMapper mapper) {
		super(sourceUnit, builder, mapper);
	}

	@Override
	public DeclarationInvocation transform(FieldNode field,
			ClassDeclaration currentScope, TransformContext context) {		

		String varType = field.getType().getName();
		String varName = field.getName();

		DeclarationInvocation declInv = builder.declareVariable(currentScope,
				new Type(varType, true), varName);

		IModifiers fieldModifiers = convertModifiers(field.getModifiers());

		declInv.getDeclaredVariable().setModifiers(fieldModifiers);

		Expression initialValueExpression = field.getInitialExpression();

		if (initialValueExpression != null) {

			throwErrorMessage(
					"Direct field initialization currently not supported. Field '"
							+ field.getName()
							+ "' cannot be initialized. Please move initialization to a constructor.",
					initialValueExpression);

			return null;
			// TODO 30.07.2014 : fix this!
			// codeBuilder.assign(currentScope, varName,
			// convertExpressionToArgument(initialValueExpression)
			// );
		}

		setCodeRange(declInv, field);

		return declInv;
	}

	@Override
	public Class<FieldNode> getAcceptedType() {
		return FieldNode.class;
	}

	@Override
	public Class<ClassDeclaration> getParentType() {
		return ClassDeclaration.class;
	}

}
