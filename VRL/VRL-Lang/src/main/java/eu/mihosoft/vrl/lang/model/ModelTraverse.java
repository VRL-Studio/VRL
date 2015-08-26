package eu.mihosoft.vrl.lang.model;

import eu.mihosoft.transverse.Buildlet;
import eu.mihosoft.transverse.CompositeTraverse;
import eu.mihosoft.transverse.TraverseHook;
import eu.mihosoft.transverse.Traverselet;

public class ModelTraverse extends CompositeTraverse {

	static Traverselet<CompilationUnitDeclaration> COMPILATION_UNIT = (decl,
			ctx) -> {
		for (ClassDeclaration cdec : decl.getDeclaredClasses())
			ctx.traverse(cdec);
	};
	static Traverselet<ClassDeclaration> CLASS_DECLARATION = (cdec, ctx) -> {
		for (MethodDeclaration mdec : cdec.getDeclaredMethods())
			ctx.traverse(mdec);
	};
	static Traverselet<MethodDeclaration> METHOD_DECLARATION = (mdec, ctx) -> {
		for (Scope scope : mdec.getScopes()) {
			ctx.traverse(scope);
		}
		for (Invocation inv : mdec.getControlFlow().getInvocations()) {
			ctx.traverse(inv);
		}
	};
	static Traverselet<IfDeclaration> IF_DECLARATION = (ifdec, ctx) -> {
        ctx.traverse(ifdec.getCheck());
        ctx.traverse(ifdec.getControlFlow());
	};

	public ModelTraverse(TraverseHook hook) {

		super(hook, COMPILATION_UNIT, CLASS_DECLARATION, METHOD_DECLARATION, IF_DECLARATION);
		
		// TODO Auto-generated constructor stub
	}

}
