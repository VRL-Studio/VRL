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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.PrefixExpression;
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
	public static final Object NULL = new Object();
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

	protected TransformPart dispatch(Object o) {
		if (!stackIn.empty() && o == stackIn.peek()) {
			// Groovy visitor dumped input object twice
			// like in visitWhileStatement followed by visitStatement
			// we do nothing
			stackIn.push(NULL);
			stackOut.push(NULL);
			return null;
		}
		Object in = o == null ? NULL : o;
		stackIn.push(in);
		return dispatch(stackIn, in);
	}

	private static Object getParent(Stack<Object> stack) {
		Object parent = null;
		int index = stack.size();
		do {
			parent = stack.get(--index);
		} while (parent == NULL || parent == null);
		return parent;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected TransformPart dispatch(Stack<Object> stackIn, Object in) {
		Object parent;
		TransformPart usedPart = null;

		// stackOut contains NULL placeholders for input graph objects that
		// were not transformed, these are not relevant as parent objects
		// in the output graph.
		parent = getParent(stackOut);
		all: for (TransformPart part : parts) {
			if (part.getAcceptedType().equals(in.getClass())
					&& part.getParentType().isAssignableFrom(parent.getClass())) {
				usedPart = part;
				support.setCurrent(in);
				Object out = part.transform(in, parent, support);
				support.update(in, out);
				stackOut.push(out);
				break all;
			}
		}
		if (usedPart == null) {
			stackOut.push(NULL);
		}
		return usedPart;
	}

	void pop(TransformPart used) {
		Object in = stackIn.pop();
		Object out = stackOut.pop();
		if (used != null) {
			support.setCurrent(in);
			used.postTransform(out, in, getParent(stackOut), support);
		}
	}

	@Override
	public void visitAnnotations(AnnotatedNode node) {
		TransformPart used = dispatch(node);
		super.visitAnnotations(node);
		pop(used);
	}

	@Override
	public void visitArgumentlistExpression(ArgumentListExpression ale) {
		TransformPart used = dispatch(ale);
		super.visitArgumentlistExpression(ale);
		pop(used);
	}

	@Override
	public void visitArrayExpression(ArrayExpression expression) {
		TransformPart used = dispatch(expression);
		super.visitArrayExpression(expression);
		pop(used);
	}

	@Override
	public void visitAssertStatement(AssertStatement statement) {
		TransformPart used = dispatch(statement);
		super.visitAssertStatement(statement);
		pop(used);
	}

	@Override
	public void visitAttributeExpression(AttributeExpression expression) {
		TransformPart used = dispatch(expression);
		super.visitAttributeExpression(expression);
		pop(used);
	}

	@Override
	public void visitBinaryExpression(BinaryExpression expression) {
		TransformPart used = dispatch(expression);
		super.visitBinaryExpression(expression);
		pop(used);
	}

	@Override
	public void visitBitwiseNegationExpression(
			BitwiseNegationExpression expression) {
		TransformPart used = dispatch(expression);
		super.visitBitwiseNegationExpression(expression);
		pop(used);
	}

	@Override
	public void visitBlockStatement(BlockStatement block) {
		TransformPart used = dispatch(block);
		super.visitBlockStatement(block);
		pop(used);
	}

	@Override
	public void visitBooleanExpression(BooleanExpression expression) {
		TransformPart used = dispatch(expression);
		super.visitBooleanExpression(expression);
		pop(used);
	}

	@Override
	public void visitBreakStatement(BreakStatement statement) {
		TransformPart used = dispatch(statement);
		super.visitBreakStatement(statement);
		pop(used);
	}

	@Override
	public void visitBytecodeExpression(BytecodeExpression cle) {
		TransformPart used = dispatch(cle);
		super.visitBytecodeExpression(cle);
		pop(used);
	}

	@Override
	public void visitCaseStatement(CaseStatement statement) {
		TransformPart used = dispatch(statement);
		super.visitCaseStatement(statement);
		pop(used);
	}

	@Override
	public void visitCastExpression(CastExpression expression) {
		TransformPart used = dispatch(expression);
		super.visitCastExpression(expression);
		pop(used);
	}

	@Override
	public void visitCatchStatement(CatchStatement statement) {
		TransformPart used = dispatch(statement);
		super.visitCatchStatement(statement);
		pop(used);
	}

	@Override
	public void visitClass(ClassNode node) {
		TransformPart used = dispatch(node);
		super.visitClass(node);
		pop(used);
	}

	@Override
	protected void visitClassCodeContainer(Statement code) {
		TransformPart used = dispatch(code);
		super.visitClassCodeContainer(code);
		pop(used);
	}

	@Override
	public void visitClassExpression(ClassExpression expression) {
		TransformPart used = dispatch(expression);
		super.visitClassExpression(expression);
		pop(used);
	}

	@Override
	public void visitClosureExpression(ClosureExpression expression) {
		TransformPart used = dispatch(expression);
		super.visitClosureExpression(expression);
		pop(used);
	}

	@Override
	public void visitClosureListExpression(ClosureListExpression cle) {
		TransformPart used = dispatch(cle);
		super.visitClosureListExpression(cle);
		pop(used);
	}

	@Override
	public void visitConstantExpression(ConstantExpression expression) {
		TransformPart used = dispatch(expression);
		super.visitConstantExpression(expression);
		pop(used);
	}

	@Override
	public void visitConstructor(ConstructorNode node) {
		TransformPart used = dispatch(node);
		super.visitConstructor(node);
		pop(used);
	}

	@Override
	public void visitConstructorCallExpression(ConstructorCallExpression call) {
		TransformPart used = dispatch(call);
		super.visitConstructorCallExpression(call);
		pop(used);
	}

	@Override
	protected void visitConstructorOrMethod(MethodNode node,
			boolean isConstructor) {
		// TODO: differentiate between method/constructor...
		TransformPart used = dispatch(node);
		super.visitConstructorOrMethod(node, isConstructor);
		pop(used);
	}

	@Override
	public void visitContinueStatement(ContinueStatement statement) {
		TransformPart used = dispatch(statement);
		super.visitContinueStatement(statement);
		pop(used);
	}

	@Override
	public void visitDeclarationExpression(DeclarationExpression expression) {
		TransformPart used = dispatch(expression);
		super.visitDeclarationExpression(expression);
		pop(used);
	}

	@Override
	public void visitDoWhileLoop(DoWhileStatement loop) {
		TransformPart used = dispatch(loop);
		super.visitDoWhileLoop(loop);
		pop(used);
	}

	@Override
	protected void visitEmptyStatement(EmptyStatement statement) {
		TransformPart used = dispatch(statement);
		super.visitEmptyStatement(statement);
		pop(used);
	}

	@Override
	public void visitExpressionStatement(ExpressionStatement statement) {
		TransformPart used = dispatch(statement);
		super.visitExpressionStatement(statement);
		pop(used);
	}

	@Override
	public void visitField(FieldNode node) {
		TransformPart used = dispatch(node);
		super.visitField(node);
		pop(used);
	}

	@Override
	public void visitFieldExpression(FieldExpression expression) {
		TransformPart used = dispatch(expression);
		super.visitFieldExpression(expression);
		pop(used);
	}

	@Override
	public void visitForLoop(ForStatement forLoop) {
		TransformPart used = dispatch(forLoop);
		super.visitForLoop(forLoop);
		pop(used);
	}

	@Override
	public void visitGStringExpression(GStringExpression expression) {
		TransformPart used = dispatch(expression);
		super.visitGStringExpression(expression);
		pop(used);
	}

	@Override
	public void visitIfElse(IfStatement ifElse) {
		TransformPart used = dispatch(ifElse);
		super.visitIfElse(ifElse);
		pop(used);
	}

	@Override
	public void visitImports(ModuleNode node) {
		TransformPart used = dispatch(node);
		super.visitImports(node);
		pop(used);
	}

	@Override
	public void visitListExpression(ListExpression expression) {
		TransformPart used = dispatch(expression);
		super.visitListExpression(expression);
		pop(used);
	}

	@Override
	protected void visitListOfExpressions(List<? extends Expression> list) {
		// TODO hide this or is the List important
		TransformPart used = dispatch(list);
		super.visitListOfExpressions(list);
		pop(used);
	}

	@Override
	public void visitMapEntryExpression(MapEntryExpression expression) {
		TransformPart used = dispatch(expression);
		super.visitMapEntryExpression(expression);
		pop(used);
	}

	@Override
	public void visitMapExpression(MapExpression expression) {
		TransformPart used = dispatch(expression);
		super.visitMapExpression(expression);
		pop(used);
	}

	@Override
	public void visitMethod(MethodNode node) {
		TransformPart used = dispatch(node);
		super.visitMethod(node);
		pop(used);
	}

	@Override
	public void visitMethodCallExpression(MethodCallExpression call) {
		TransformPart used = dispatch(call);
		super.visitMethodCallExpression(call);
		pop(used);
	}

	@Override
	public void visitMethodPointerExpression(MethodPointerExpression expression) {
		TransformPart used = dispatch(expression);
		super.visitMethodPointerExpression(expression);
		pop(used);
	}

	@Override
	public void visitNotExpression(NotExpression expression) {
		TransformPart used = dispatch(expression);
		super.visitNotExpression(expression);
		pop(used);
	}

	@Override
	protected void visitObjectInitializerStatements(ClassNode node) {
		TransformPart used = dispatch(node);
		super.visitObjectInitializerStatements(node);
		pop(used);
	}

	@Override
	public void visitPackage(PackageNode node) {
		TransformPart used = dispatch(node);
		super.visitPackage(node);
		pop(used);
	}

	@Override
	public void visitPostfixExpression(PostfixExpression expression) {
		TransformPart used = dispatch(expression);
		super.visitPostfixExpression(expression);
		pop(used);
	}

	@Override
	public void visitPrefixExpression(PrefixExpression expression) {
		TransformPart used = dispatch(expression);
		super.visitPrefixExpression(expression);
		pop(used);
	}

	@Override
	public void visitProperty(PropertyNode node) {
		TransformPart used = dispatch(node);
		super.visitProperty(node);
		pop(used);
	}

	@Override
	protected void visitStatement(Statement statement) {
		/*
		 * TransformPart used = dispatch(statement);
		 * super.visitStatement(statement); pop(used);
		 */
	}

	@Override
	public void visitReturnStatement(ReturnStatement statement) {
		TransformPart used = dispatch(statement);
		super.visitReturnStatement(statement);
		pop(used);
	}

	@Override
	public void visitSwitch(SwitchStatement statement) {
		TransformPart used = dispatch(statement);
		super.visitSwitch(statement);
		pop(used);
	}

	@Override
	public void visitSynchronizedStatement(SynchronizedStatement statement) {
		TransformPart used = dispatch(statement);
		super.visitSynchronizedStatement(statement);
		pop(used);
	}

	@Override
	public void visitThrowStatement(ThrowStatement statement) {
		TransformPart used = dispatch(statement);
		super.visitThrowStatement(statement);
		pop(used);
	}

	@Override
	public void visitTryCatchFinally(TryCatchStatement statement) {
		TransformPart used = dispatch(statement);
		super.visitTryCatchFinally(statement);
		pop(used);
	}

	@Override
	public void visitWhileLoop(WhileStatement loop) {
		TransformPart used = dispatch(loop);
		super.visitWhileLoop(loop);
		pop(used);
	}

	public Root getRoot() {
		return root;
	}

	/**
	 * Groovy code visitor pattern doesn't visit the groovy model root node.
	 * 
	 * @param module
	 */
	public void visitModuleNode(ModuleNode module) {
		TransformPart used = dispatch(module);
		for (ClassNode cls : module.getClasses()) {
			visitClass(cls);
		}
		for (MethodNode meth : module.getMethods()) {
			visitMethod(meth);
		}
		pop(used);
	}

	TransformingSupportImpl support = new TransformingSupportImpl();

	class TransformingSupportImpl implements TransformContext {
		Map<String, Object> resolvables = new HashMap<String, Object>();
		Set<Object> unresolved = new HashSet<Object>();
		Object current;

		public void setCurrent(Object current) {
			this.current = current;
		}

		@Override
		public void bind(String key, Object input) {
			String resolutionKey = current.hashCode() + key;
			resolvables.put(resolutionKey, input);
			unresolved.add(input);
		}

		@Override
		public <T> T resolve(String key, Class<T> outputType) {
			String resolutionKey = current.hashCode() + key;
			if (!resolvables.containsKey(resolutionKey))
				return null;
			Object r = resolvables.get(resolutionKey);
			if (unresolved.contains(r))
				return null;
			return outputType.cast(r);
		}

		void update(Object input, Object result) {
			if (unresolved.contains(input)) {
				entries: for (Map.Entry<String, Object> entry : resolvables
						.entrySet()) {
					if (input.equals(entry.getValue())) {
						resolvables.put(entry.getKey(), result);
						unresolved.remove(entry.getValue());
						break entries;
					}
				}
			}
		}

		@Override
		public Stack<Object> getInputPath() {
			return stackIn;
		}

		@Override
		public Stack<Object> getOutputPath() {
			return stackOut;
		}
	}
}
