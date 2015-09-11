package eu.mihosoft.vrl.instrumentation.composites;

import static org.junit.Assert.*;

import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.junit.Test;

import eu.mihosoft.vrl.lang.model.ConstantValueFactory;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.lang.model.WhileDeclaration;

public class WhileLoopPartTest extends
		CompositeTestUtil<WhileStatement, WhileLoopPart> {

	@Test
	public void testWhileLoop() throws Exception {
		createHarness("while(true){ println(\"test\"); }",
				WhileStatement.class, WhileLoopPart.class);
		resolveAs("WhileLoop.condition",
				ConstantValueFactory.createConstantValue(Boolean.TRUE,
						Type.BOOLEAN));
		WhileDeclaration decl = part.transform(statement, scope, context);
		assertTrue(decl.getCheck().getConstant().get().getValue(Boolean.class));
	}

	@Test
	public void testNullWhileLoop() throws Exception {
		createHarness("while(null){ println(\"test\"); }",
				WhileStatement.class, WhileLoopPart.class);
		resolveAs("WhileLoop.condition",
				ConstantValueFactory.createConstantValue(null, Type.VOID));
		WhileDeclaration decl = part.transform(statement, scope, context);

		assertTrue(decl.getCheck().getType().equals(Type.VOID));

	}
}
