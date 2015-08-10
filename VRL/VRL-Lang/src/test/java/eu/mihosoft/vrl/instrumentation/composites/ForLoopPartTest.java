package eu.mihosoft.vrl.instrumentation.composites;

import static org.junit.Assert.assertEquals;

import java.io.Reader;

import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.control.SourceUnit;
import org.junit.Test;

import eu.mihosoft.vrl.instrumentation.CompositeTransformingVisitorSupportTest;
import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.instrumentation.composites.CompositeTestUtil.DynaProxy;
import eu.mihosoft.vrl.instrumentation.transform.DefaultProxy;
import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.DeclarationInvocationImpl;
import eu.mihosoft.vrl.lang.model.IdRequest;
import eu.mihosoft.vrl.lang.model.SimpleForDeclaration;
import eu.mihosoft.vrl.lang.model.VariableFactory;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder_Impl;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.IdGenerator;

public class ForLoopPartTest extends
		CompositeTestUtil<ForStatement, ForLoopPart> {

	@Test
	public void testSimpleForLoop() throws Exception {
		createHarness("for (int i=0; i<10; i++){System.out.println(i);}",
				ForStatement.class, ForLoopPart.class);
		resolveAs("SimpleForDeclaration.declaration", new DeclarationInvocationImpl());
		
		SimpleForDeclaration decl = part.transform(statement, scope, context);
		part.postTransform(decl, statement, scope, context);
		assertEquals(0, decl.getFrom());
		assertEquals(9, decl.getTo());
		assertEquals(1, decl.getInc());
	}

	@Test
	public void testSimpleForLoopWithGE() throws Exception {
		createHarness("for (int i=0; i<=10; i++){System.out.println(i);}",
				ForStatement.class, ForLoopPart.class);
		resolveAs("SimpleForDeclaration.declaration", new DeclarationInvocationImpl());
		
		SimpleForDeclaration decl = part.transform(statement, scope, context);
		part.postTransform(decl, statement, scope, context);
		assertEquals(0, decl.getFrom());
		assertEquals(10, decl.getTo());
		assertEquals(1, decl.getInc());
	}
	
	@Test
	public void testProxy() throws Exception {
		ControlFlowScope scope = createProxy(new DynaProxy() {
			public String _getName() {
				return "Test";
			}
		}, ControlFlowScope.class);
		assertEquals("Test", scope.getName());
	}
}
