/*
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.PackageNode;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.MethodPointerExpression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.codehaus.groovy.control.SourceUnit;

public class CompositeTransformingVisitorSupport extends
		ClassCodeVisitorSupport {

	public static class Root {
		private Object rootObject;

		public Object getRootObject() {
			return rootObject;
		}

		public void setRootObject(Object rootObject) {
			this.rootObject = rootObject;
		}

	}

	private final Root root = new Root();

	private SourceUnit sourceUnit;
	private Stack<Object> stackIn = new Stack<Object>();
	private Stack<Object> stackOut = new Stack<Object>();
	@SuppressWarnings("rawtypes")
	private List<TransformPart> parts = new ArrayList<TransformPart>();

	public CompositeTransformingVisitorSupport(SourceUnit sourceUnit,
			@SuppressWarnings("rawtypes") TransformPart... parts) {
		this.sourceUnit = sourceUnit;
		this.parts.addAll(Arrays.asList(parts));
		stackOut.push(root);
	}

	@Override
	protected SourceUnit getSourceUnit() {
		return sourceUnit;
	}

	protected void dispatch(Object o) {
		stackIn.push(o);
		dispatch(stackIn, o);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void dispatch(Stack<Object> stackIn, Object in) {
		if (in == null)
			return; // AST visitor might dump null values for visiting, TODO
					// ignore or fail?
		Object parent = stackOut.peek();
		boolean transformed = false;
		all: for (TransformPart part : parts) {
			if (part.getAcceptedType().equals(in.getClass())
					&& part.getParentType().equals(parent.getClass())
					&& part.accepts(stackIn, in, stackOut, parent)) {
				Object out = part.transform(stackIn, in, stackOut, parent);
				stackOut.push(out);
				transformed = true;
				break all;
			}
		}
		if (!transformed)
		{
			stackOut.push(new Object());
		}

	}

	void pop() {
		stackIn.pop();
		stackOut.pop();
	}

	@Override
	public void visitAnnotations(AnnotatedNode node) {
		dispatch(node);
		super.visitAnnotations(node);
		pop();
	}

	@Override
	public void visitArgumentlistExpression(ArgumentListExpression ale) {
		dispatch(ale);
		super.visitArgumentlistExpression(ale);
		pop();
	}

	@Override
	public void visitArrayExpression(ArrayExpression expression) {
		dispatch(expression);
		super.visitArrayExpression(expression);
		pop();
	}

	@Override
	public void visitAssertStatement(AssertStatement statement) {
		dispatch(statement);
		super.visitAssertStatement(statement);
		pop();
	}

	@Override
	public void visitAttributeExpression(AttributeExpression expression) {
		dispatch(expression);
		super.visitAttributeExpression(expression);
		pop();
	}

	@Override
	public void visitBinaryExpression(BinaryExpression expression) {
		dispatch(expression);
		super.visitBinaryExpression(expression);
		pop();
	}

	@Override
	public void visitBitwiseNegationExpression(
			BitwiseNegationExpression expression) {
		dispatch(expression);
		super.visitBitwiseNegationExpression(expression);
		pop();
	}

	@Override
	public void visitBlockStatement(BlockStatement block) {
		dispatch(block);
		super.visitBlockStatement(block);
		pop();
	}

	@Override
	public void visitBooleanExpression(BooleanExpression expression) {
		dispatch(expression);
		super.visitBooleanExpression(expression);
		pop();
	}

	@Override
	public void visitBreakStatement(BreakStatement statement) {
		dispatch(statement);
		super.visitBreakStatement(statement);
		pop();
	}

	@Override
	public void visitBytecodeExpression(BytecodeExpression cle) {
		dispatch(cle);
		super.visitBytecodeExpression(cle);
		pop();
	}

	@Override
	public void visitCaseStatement(CaseStatement statement) {
		dispatch(statement);
		super.visitCaseStatement(statement);
		pop();
	}

	@Override
	public void visitCastExpression(CastExpression expression) {
		dispatch(expression);
		super.visitCastExpression(expression);
		pop();
	}

	@Override
	public void visitCatchStatement(CatchStatement statement) {
		dispatch(statement);
		super.visitCatchStatement(statement);
		pop();
	}

	@Override
	public void visitClass(ClassNode node) {
		dispatch(node);
		super.visitClass(node);
		pop();
	}

	@Override
	protected void visitClassCodeContainer(Statement code) {
		dispatch(code);
		super.visitClassCodeContainer(code);
		pop();
	}

	@Override
	public void visitClassExpression(ClassExpression expression) {
		dispatch(expression);
		super.visitClassExpression(expression);
		pop();
	}

	@Override
	public void visitClosureExpression(ClosureExpression expression) {
		dispatch(expression);
		super.visitClosureExpression(expression);
		pop();
	}

	@Override
	public void visitClosureListExpression(ClosureListExpression cle) {
		dispatch(cle);
		super.visitClosureListExpression(cle);
		pop();
	}

	@Override
	public void visitConstantExpression(ConstantExpression expression) {
		dispatch(expression);
		super.visitConstantExpression(expression);
		pop();
	}

	@Override
	public void visitConstructor(ConstructorNode node) {
		dispatch(node);
		super.visitConstructor(node);
		pop();
	}

	@Override
	public void visitConstructorCallExpression(ConstructorCallExpression call) {
		dispatch(call);
		super.visitConstructorCallExpression(call);
		pop();
	}

	@Override
	protected void visitConstructorOrMethod(MethodNode node,
			boolean isConstructor) {
		// TODO: differentiate between method/constructor...
		dispatch(node);
		super.visitConstructorOrMethod(node, isConstructor);
		pop();
	}

	@Override
	public void visitContinueStatement(ContinueStatement statement) {
		dispatch(statement);
		super.visitContinueStatement(statement);
		pop();
	}

	@Override
	public void visitDeclarationExpression(DeclarationExpression expression) {
		dispatch(expression);
		super.visitDeclarationExpression(expression);
		pop();
	}

	@Override
	public void visitDoWhileLoop(DoWhileStatement loop) {
		dispatch(loop);
		super.visitDoWhileLoop(loop);
		pop();
	}

	@Override
	protected void visitEmptyStatement(EmptyStatement statement) {
		dispatch(statement);
		super.visitEmptyStatement(statement);
		pop();
	}

	@Override
	public void visitExpressionStatement(ExpressionStatement statement) {
		dispatch(statement);
		super.visitExpressionStatement(statement);
		pop();
	}

	@Override
	public void visitField(FieldNode node) {
		dispatch(node);
		super.visitField(node);
		pop();
	}

	@Override
	public void visitFieldExpression(FieldExpression expression) {
		dispatch(expression);
		super.visitFieldExpression(expression);
		pop();
	}

	@Override
	public void visitForLoop(ForStatement forLoop) {
		dispatch(forLoop);
		super.visitForLoop(forLoop);
		pop();
	}

	@Override
	public void visitGStringExpression(GStringExpression expression) {
		dispatch(expression);
		super.visitGStringExpression(expression);
		pop();
	}

	@Override
	public void visitIfElse(IfStatement ifElse) {
		dispatch(ifElse);
		super.visitIfElse(ifElse);
		pop();
	}

	@Override
	public void visitImports(ModuleNode node) {
		dispatch(node);
		super.visitImports(node);
		pop();
	}

	@Override
	public void visitListExpression(ListExpression expression) {
		dispatch(expression);
		super.visitListExpression(expression);
		pop();
	}

	@Override
	protected void visitListOfExpressions(List<? extends Expression> list) {
		// TODO hide this or is the List important
		dispatch(list);
		super.visitListOfExpressions(list);
		pop();
	}

	@Override
	public void visitMapEntryExpression(MapEntryExpression expression) {
		dispatch(expression);
		super.visitMapEntryExpression(expression);
		pop();
	}

	@Override
	public void visitMapExpression(MapExpression expression) {
		dispatch(expression);
		super.visitMapExpression(expression);
		pop();
	}

	@Override
	public void visitMethod(MethodNode node) {
		dispatch(node);
		super.visitMethod(node);
		pop();
	}

	@Override
	public void visitMethodCallExpression(MethodCallExpression call) {
		dispatch(call);
		super.visitMethodCallExpression(call);
		pop();
	}

	@Override
	public void visitMethodPointerExpression(MethodPointerExpression expression) {
		dispatch(expression);
		super.visitMethodPointerExpression(expression);
		pop();
	}

	@Override
	public void visitNotExpression(NotExpression expression) {
		dispatch(expression);
		super.visitNotExpression(expression);
		pop();
	}

	@Override
	protected void visitObjectInitializerStatements(ClassNode node) {
		dispatch(node);
		super.visitObjectInitializerStatements(node);
		pop();
	}

	@Override
	public void visitPackage(PackageNode node) {
		dispatch(node);
		super.visitPackage(node);
		pop();
	}

	@Override
	public void visitProperty(PropertyNode node) {
		dispatch(node);
		super.visitProperty(node);
		pop();
	}

	@Override
	protected void visitStatement(Statement statement) {
		dispatch(statement);
		super.visitStatement(statement);
		pop();
	}

	@Override
	public void visitReturnStatement(ReturnStatement statement) {
		dispatch(statement);
		super.visitReturnStatement(statement);
		pop();
	}

	@Override
	public void visitSwitch(SwitchStatement statement) {
		dispatch(statement);
		super.visitSwitch(statement);
		pop();
	}

	@Override
	public void visitSynchronizedStatement(SynchronizedStatement statement) {
		dispatch(statement);
		super.visitSynchronizedStatement(statement);
		pop();
	}

	@Override
	public void visitThrowStatement(ThrowStatement statement) {
		dispatch(statement);
		super.visitThrowStatement(statement);
		pop();
	}

	@Override
	public void visitTryCatchFinally(TryCatchStatement statement) {
		dispatch(statement);
		super.visitTryCatchFinally(statement);
		pop();
	}

	@Override
	public void visitWhileLoop(WhileStatement loop) {
		dispatch(loop);
		super.visitWhileLoop(loop);
		pop();
	}

	public Root getRoot() {
		return root;
	}

	/**
	 * Groovy code visitor pattern doesn't visit the groovy model root node.
	 * @param module
	 */
	public void visitModuleNode(ModuleNode module) {
		dispatch(module);
		for (ClassNode cls : module.getClasses()) {
			visitClass(cls);
		}
		for (MethodNode meth : module.getMethods()) {
			visitMethod(meth);
		}
		pop();
	}
}
