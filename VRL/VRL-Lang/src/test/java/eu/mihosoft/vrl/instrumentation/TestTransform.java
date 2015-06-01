package eu.mihosoft.vrl.instrumentation;

import java.lang.reflect.Modifier;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation
public class TestTransform extends AbstractASTTransformation {
	public int counter = 0;

	@Override
	public void visit(ASTNode[] nodes, SourceUnit source) {
		counter++;

		if (nodes == null)
			return;
		if (nodes.length < 2)
			return;
		if (nodes[0] == null || nodes[1] == null)
			return;
		if (!(nodes[0] instanceof AnnotationNode))
			return;
		if (!((AnnotationNode) nodes[0]).getClassNode().getNameWithoutPackage()
				.equals("Main"))
			return;
		if (!(nodes[1] instanceof MethodNode))
			return;
		MethodNode annotatedMethod = (MethodNode) nodes[1];
		ClassNode declaringClass = annotatedMethod.getDeclaringClass();
		MethodNode mainMethod = makeMainMethod(annotatedMethod);
		mainMethod.setCode(annotatedMethod.getCode());
		declaringClass.addMethod(mainMethod);
		
		// only way i found to make this object instance visible via the API
		source.getConfiguration().getJointCompilationOptions().put("save", this);
	}

	private MethodNode makeMainMethod(MethodNode annotatedMethod) {
		List<ASTNode> nodes = new AstBuilder()
				.buildFromString("System.out.println(\"test\");");
		MethodNode node = new MethodNode("main", Modifier.PUBLIC | Modifier.STATIC, ClassHelper.VOID_TYPE,
				new Parameter[] { new Parameter(new ClassNode(String.class).makeArray(),
						"args") }, ClassNode.EMPTY_ARRAY, annotatedMethod.getCode());
		return node;
	}
}