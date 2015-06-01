package eu.mihosoft.vrl.instrumentation.composites;

import java.util.Stack;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.lang.model.ClassDeclaration;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class ClassNodePart extends AbstractCodeBuilderPart<ClassNode, ClassDeclaration, CompilationUnitDeclaration> {

	public ClassNodePart(StateMachine stateMachine,SourceUnit unit, VisualCodeBuilder builder, CodeLineColumnMapper mapper) {
		super(stateMachine, unit, builder, mapper);
	}

	@Override
	public ClassDeclaration transform(Stack<Object> stackIn, ClassNode obj,
			Stack<Object> stackOut, CompilationUnitDeclaration parent) {
		ClassDeclaration cd = builder.declareClass(parent,
                new Type(obj.getName(), false),
                convertModifiers(obj.getModifiers()),
                convertExtends(obj),
                convertImplements(obj));

        setCodeRange(cd, obj);
        addCommentsToScope(cd, comments);
        return cd;
	}

	@Override
	public Class<ClassNode> getAcceptedType() {
		return ClassNode.class;
	}

	@Override
	public Class<CompilationUnitDeclaration> getParentType() {
		return CompilationUnitDeclaration.class;
	}

	@Override
	public boolean accepts(Stack<Object> stackIn, ClassNode obj,
			Stack<Object> stackOut, CompilationUnitDeclaration parent) {
		return true;
	}

}
