package eu.mihosoft.vrl.instrumentation.composites;

import java.util.Stack;

import org.codehaus.groovy.ast.ModuleNode;

import eu.mihosoft.vrl.instrumentation.CompositeTransformingVisitorSupport.Root;
import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.instrumentation.transform.TransformPart;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class ModuleNodePart implements
		TransformPart<ModuleNode, CompilationUnitDeclaration, Root> {

	VisualCodeBuilder builder;

	public ModuleNodePart(VisualCodeBuilder builder) {
		this.builder = builder;
	}

	@Override
	public CompilationUnitDeclaration transform(ModuleNode obj, Root parent,
			TransformContext context) {
		String packageName = "";

		if (obj.getPackage() != null) {
			packageName = obj.getPackage().getName();
		}

		if (packageName.endsWith(".")) {
			packageName = packageName.substring(0, packageName.length() - 1);
		}

		String unitName;

		if (obj.getMainClassName() != null) {
			unitName = obj.getMainClassName() + ".groovy";
		} else {
			unitName = "unknown.groovy";
		}

		CompilationUnitDeclaration decl = builder.declareCompilationUnit(
				unitName, packageName);
		parent.setRootObject(decl);
		return decl;
	}

	@Override
	public Class<ModuleNode> getAcceptedType() {
		return ModuleNode.class;
	}

	@Override
	public Class<Root> getParentType() {
		return Root.class;
	}

	@Override
	public final void postTransform(CompilationUnitDeclaration obj,
			ModuleNode in, Root parent, TransformContext context) {
	}

}
