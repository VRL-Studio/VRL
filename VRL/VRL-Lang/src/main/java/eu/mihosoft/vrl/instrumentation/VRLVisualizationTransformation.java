/* 
 * VRLVisualizationTransformation.java
 *
 * Copyright (c) 2009–2014 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2014 by Michael Hoffer
 * 
 * This file is part of Visual Reflection Library (VRL).
 *
 * VRL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * see: http://opensource.org/licenses/LGPL-3.0
 *      file://path/to/VRL/src/eu/mihosoft/vrl/resources/license/lgplv3.txt
 *
 * VRL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * This version of VRL includes copyright notice and attribution requirements.
 * According to the LGPL this information must be displayed even if you modify
 * the source code of VRL. Neither the VRL Canvas attribution icon nor any
 * copyright statement/attribution may be removed.
 *
 * Attribution Requirements:
 *
 * If you create derived work you must do three things regarding copyright
 * notice and author attribution.
 *
 * First, the following text must be displayed on the Canvas or an equivalent location:
 * "based on VRL source code".
 * 
 * Second, the copyright notice must remain. It must be reproduced in any
 * program that uses VRL.
 *
 * Third, add an additional notice, stating that you modified VRL. In addition
 * you must cite the publications listed below. A suitable notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Publications:
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, in press.
 */
package eu.mihosoft.vrl.instrumentation;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.LocatedMessage;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

import eu.mihosoft.transverse.Validatelet;
import eu.mihosoft.transverse.ValidationHook;
import eu.mihosoft.transverse.ValidationMessage;
import eu.mihosoft.vrl.instrumentation.composites.BinaryExpressionPart;
import eu.mihosoft.vrl.instrumentation.composites.BreakPart;
import eu.mihosoft.vrl.instrumentation.composites.ClassNodePart;
import eu.mihosoft.vrl.instrumentation.composites.ConstantExpressionPart;
import eu.mihosoft.vrl.instrumentation.composites.ContinuePart;
import eu.mihosoft.vrl.instrumentation.composites.DeclarationExpressionPart;
import eu.mihosoft.vrl.instrumentation.composites.ElsePart;
import eu.mihosoft.vrl.instrumentation.composites.FieldPart;
import eu.mihosoft.vrl.instrumentation.composites.ForLoopPart;
import eu.mihosoft.vrl.instrumentation.composites.IfStatementPart;
import eu.mihosoft.vrl.instrumentation.composites.MethodCallExpressionPart;
import eu.mihosoft.vrl.instrumentation.composites.MethodNodePart;
import eu.mihosoft.vrl.instrumentation.composites.ModuleNodePart;
import eu.mihosoft.vrl.instrumentation.composites.PostFixExpressionPart;
import eu.mihosoft.vrl.instrumentation.composites.PropertyExpressionPart;
import eu.mihosoft.vrl.instrumentation.composites.ReturnStatementPart;
import eu.mihosoft.vrl.instrumentation.composites.ValidationUtil;
import eu.mihosoft.vrl.instrumentation.composites.VariableExpressionPart;
import eu.mihosoft.vrl.instrumentation.composites.WhileLoopPart;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.CompilationUnitDeclaration;
import eu.mihosoft.vrl.lang.model.IdRequest;
import eu.mihosoft.vrl.lang.model.IfDeclaration;
import eu.mihosoft.vrl.lang.model.ModelTraverse;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.UIBinding;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder_Impl;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.IdGenerator;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class VRLVisualizationTransformation implements ASTTransformation {

	@Override
	public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {

		if (UIBinding.scopes.containsKey(sourceUnit.getName())) {
			return;
		}

		TypeCheckingTransform transformation = new TypeCheckingTransform();

		if (astNodes == null) {
			System.err.println("ASTNodes = NULL: skipping initial type check");
		} else {
			transformation.visit(astNodes, sourceUnit);
		}

		VisualCodeBuilder_Impl codeBuilder = new VisualCodeBuilder_Impl();

		Map<String, List<Scope>> scopes = new HashMap<>();

		CompositeTransformingVisitorSupport visitor = init(sourceUnit);

		// VGroovyCodeVisitor visitor = new VGroovyCodeVisitor(sourceUnit,
		// codeBuilder);

		List<Scope> clsScopes = new ArrayList<>();
		UIBinding.scopes.put(sourceUnit.getName(), clsScopes);

		// Create VRL model
		visitor.visitModuleNode(sourceUnit.getAST());
		CompilationUnitDeclaration decl = (CompilationUnitDeclaration) visitor
				.getRoot().getRootObject();
		
		// Apply validation code
		ValidationHook hook = new ValidationHook(new ValidationUtil().validations());

		ModelTraverse traverse = new ModelTraverse(hook);
		traverse.traverse(decl);

		for (ValidationMessage msg : hook.getMessages())
		{
			throwErrorMessage(msg.getMessage(), sourceUnit.getAST() ,sourceUnit);
		}

		clsScopes.add(decl);
	}
	
	protected static void throwErrorMessage(String text, ASTNode node, SourceUnit sourceUnit) {

		// thanks to
		// http://grails.io/post/15965611310/lessons-learnt-developing-groovy-ast-transformations
		Token token = Token.newString(node.getText(), node.getLineNumber(),
				node.getColumnNumber());
		LocatedMessage message = new LocatedMessage(text, token, sourceUnit);
		sourceUnit.getErrorCollector().addError(message);
	}

	public static CompositeTransformingVisitorSupport init(SourceUnit sourceUnit) {
		VisualCodeBuilder_Impl builder = new VisualCodeBuilder_Impl();
		StateMachine stateMachine = new StateMachine();

		builder.setIdRequest(new IdRequest() {

			private IdGenerator generator = FlowFactory.newIdGenerator();

			@Override
			public String request() {
				String result = generator
						.newId(this.getClass().getSimpleName());
				return result;
			}
		});

		try {

			Reader in = sourceUnit.getSource().getReader();
			CodeLineColumnMapper mapper = new CodeLineColumnMapper();
			mapper.init(in);

			return new CompositeTransformingVisitorSupport(sourceUnit,
					new BinaryExpressionPart(sourceUnit, builder, mapper),
					new BreakPart(sourceUnit, builder, mapper),
					new ClassNodePart(sourceUnit, builder, mapper),
					new ContinuePart(sourceUnit, builder, mapper),
					new DeclarationExpressionPart(sourceUnit, builder, mapper),
					new FieldPart(sourceUnit, builder, mapper),
					new ForLoopPart(sourceUnit, builder, mapper),
					new IfStatementPart(sourceUnit, builder, mapper),
					new MethodNodePart(sourceUnit, builder, mapper),
					new ModuleNodePart(builder, mapper),
					new PostFixExpressionPart(sourceUnit, builder, mapper),
					new ReturnStatementPart(sourceUnit, builder, mapper),
					new WhileLoopPart(sourceUnit, builder, mapper),
					new ConstantExpressionPart(sourceUnit, builder, mapper),
					new VariableExpressionPart(sourceUnit, builder, mapper),
					new MethodCallExpressionPart(sourceUnit, builder, mapper),
					new PropertyExpressionPart(sourceUnit, builder, mapper),
					new ElsePart(sourceUnit, builder, mapper));

		} catch (IOException ex) {
			Logger.getLogger(VGroovyCodeVisitor.class.getName()).log(
					Level.SEVERE, null, ex);
			throw new RuntimeException(ex);
		}
	}
}
