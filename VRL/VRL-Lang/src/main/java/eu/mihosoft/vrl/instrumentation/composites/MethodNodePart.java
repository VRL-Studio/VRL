package eu.mihosoft.vrl.instrumentation.composites;

import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.instrumentation.TransformContext;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.MethodDeclaration;
import eu.mihosoft.vrl.lang.model.Parameter;
import eu.mihosoft.vrl.lang.model.Parameters;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class MethodNodePart
		extends
		AbstractCodeBuilderPart<MethodNode, MethodDeclaration, ClassDeclaration> {

	public MethodNodePart(StateMachine stateMachine, SourceUnit unit,
			VisualCodeBuilder builder, CodeLineColumnMapper mapper) {
		super(stateMachine, unit, builder, mapper);
	}

	@Override
	public MethodDeclaration transform(MethodNode obj, ClassDeclaration parent,
			TransformContext context) {
		MethodDeclaration md = builder.declareMethod((ClassDeclaration) parent,
				convertModifiers(obj.getModifiers()), new Type(obj
						.getReturnType().getName(), true), obj.getName(),
				convertMethodParameters(obj.getParameters()));
		setCodeRange(md, obj);
		addCommentsToScope(md, comments);
		return md;
	}

	@Override
	public Class<MethodNode> getAcceptedType() {
		return MethodNode.class;
	}

	@Override
	public Class<ClassDeclaration> getParentType() {
		return ClassDeclaration.class;
	}

	private Parameters convertMethodParameters(
			org.codehaus.groovy.ast.Parameter... params) {
		Parameter[] result = new Parameter[params.length];

		for (int i = 0; i < params.length; i++) {
			org.codehaus.groovy.ast.Parameter p = params[i];

			String pType = p.getType().getName();

			// if (pType.startsWith("[L")) {
			// System.err.print("convertMethodParameters(): array param not supported! "
			// + pType);
			// pType = pType.replace("[L", "").replace(";", "");
			// }
			result[i] = new Parameter(new Type(pType, true), p.getName(),
					setCodeRange(p));
		}

		return new Parameters(result);
	}
}
