package eu.mihosoft.vrl.instrumentation.composites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.LocatedMessage;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;

import com.google.common.base.Objects;

import eu.mihosoft.vrl.instrumentation.StateMachine;
import eu.mihosoft.vrl.instrumentation.VSource;
import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.instrumentation.transform.TransformPart;
import eu.mihosoft.vrl.lang.model.Argument;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.CodeRange;
import eu.mihosoft.vrl.lang.model.Comment;
import eu.mihosoft.vrl.lang.model.CommentImpl;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.DeclarationInvocation;
import eu.mihosoft.vrl.lang.model.Extends;
import eu.mihosoft.vrl.lang.model.IArgument;
import eu.mihosoft.vrl.lang.model.ICodeRange;
import eu.mihosoft.vrl.lang.model.IModifiers;
import eu.mihosoft.vrl.lang.model.IType;
import eu.mihosoft.vrl.lang.model.Invocation;
import eu.mihosoft.vrl.lang.model.Modifier;
import eu.mihosoft.vrl.lang.model.Modifiers;
import eu.mihosoft.vrl.lang.model.Operator;
import eu.mihosoft.vrl.lang.model.Scope;
import eu.mihosoft.vrl.lang.model.SimpleForDeclaration_Impl;
import eu.mihosoft.vrl.lang.model.Type;
import eu.mihosoft.vrl.lang.model.Variable;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public abstract class AbstractCodeBuilderPart<In extends ASTNode, Out extends CodeEntity, OutParent extends CodeEntity>
		implements TransformPart<In, Out, OutParent> {

	SourceUnit sourceUnit;
	VisualCodeBuilder builder;
	CodeLineColumnMapper mapper;
	List<Comment> comments = new ArrayList<>();
	StateMachine stateMachine;

	public AbstractCodeBuilderPart(StateMachine stateMachine,
			SourceUnit sourceUnit, VisualCodeBuilder builder,
			CodeLineColumnMapper mapper) {
		this.builder = builder;
		this.mapper = mapper;
		this.stateMachine = stateMachine;
		this.sourceUnit = sourceUnit;
	}

	protected void setCodeRange(CodeEntity codeEntity, ASTNode astNode) {

		codeEntity.setRange(new CodeRange(astNode.getLineNumber() - 1, astNode
				.getColumnNumber() - 1, astNode.getLastLineNumber() - 1,
				astNode.getLastColumnNumber() - 1, mapper));
	}

	protected static IModifiers convertModifiers(int modifiers) {

		List<Modifier> modifierList = new ArrayList<>();

		// TODO rethink modifiers design (21.10.2013)
		if (java.lang.reflect.Modifier.isPublic(modifiers)) {
			modifierList.add(Modifier.PUBLIC);
		}
		if (java.lang.reflect.Modifier.isPrivate(modifiers)) {
			modifierList.add(Modifier.PRIVATE);
		}
		if (java.lang.reflect.Modifier.isProtected(modifiers)) {
			modifierList.add(Modifier.PROTECTED);
		}
		if (java.lang.reflect.Modifier.isAbstract(modifiers)) {
			modifierList.add(Modifier.ABSTRACT);
		}
		if (java.lang.reflect.Modifier.isFinal(modifiers)) {
			modifierList.add(Modifier.FINAL);
		}
		if (java.lang.reflect.Modifier.isStatic(modifiers)) {
			modifierList.add(Modifier.STATIC);
		}

		return new Modifiers(modifierList.toArray(new Modifier[modifierList
				.size()]));
	}

	@Override
	public void postTransform(Out obj, In in, OutParent parent,
			TransformContext context) {

	}

	protected static Extends convertExtends(ClassNode n) {

		ClassNode superType = n.getSuperClass();

		Type type = new Type(superType.getName(), false);

		Extends result = new Extends(type);

		return result;
	}

	protected static Extends convertImplements(ClassNode n) {

		Collection<ClassNode> interfaces = n.getAllInterfaces();

		Type[] types = new Type[interfaces.size()];

		int i = 0;
		for (ClassNode classNode : interfaces) {
			types[i] = new Type(classNode.getName(), false);
			i++;
		}

		Extends result = new Extends(types);

		return result;
	}

	protected static void addCommentsToScope(Scope scope, List<Comment> comments) {
		for (Comment comment : comments) {
			if (scope.getRange().contains(comment.getRange())) {
				((CommentImpl) comment).setParent(scope);
				scope.getComments().add(comment);
			}
		}
	}

	protected ICodeRange setCodeRange(ASTNode astNode) {

		return new CodeRange(astNode.getLineNumber() - 1,
				astNode.getColumnNumber() - 1, astNode.getLastLineNumber() - 1,
				astNode.getLastColumnNumber() - 1, mapper);
	}

	protected static Operator convertOperator(BinaryExpression be) {
		switch (be.getOperation().getType()) {
		case org.codehaus.groovy.syntax.Types.PLUS:
			return Operator.PLUS;
		case org.codehaus.groovy.syntax.Types.MINUS:
			return Operator.MINUS;
		case org.codehaus.groovy.syntax.Types.MULTIPLY:
			return Operator.TIMES;
		case org.codehaus.groovy.syntax.Types.DIVIDE:
			return Operator.DIV;
		case org.codehaus.groovy.syntax.Types.ASSIGN:
			return Operator.ASSIGN;
		case org.codehaus.groovy.syntax.Types.PLUS_EQUAL:
			return Operator.PLUS_ASSIGN;
		case org.codehaus.groovy.syntax.Types.MINUS_EQUAL:
			return Operator.MINUS_ASSIGN;
		case org.codehaus.groovy.syntax.Types.MULTIPLY_EQUAL:
			return Operator.TIMES_ASSIGN;
		case org.codehaus.groovy.syntax.Types.DIVIDE_EQUAL:
			return Operator.DIV_ASSIGN;
		case org.codehaus.groovy.syntax.Types.COMPARE_EQUAL:
			return Operator.EQUALS;
		case org.codehaus.groovy.syntax.Types.COMPARE_NOT_EQUAL:
			return Operator.NOT_EQUALS;
		case org.codehaus.groovy.syntax.Types.COMPARE_GREATER_THAN:
			return Operator.GREATER;
		case org.codehaus.groovy.syntax.Types.COMPARE_GREATER_THAN_EQUAL:
			return Operator.GREATER_EQUALS;
		case org.codehaus.groovy.syntax.Types.COMPARE_LESS_THAN:
			return Operator.LESS;
		case org.codehaus.groovy.syntax.Types.COMPARE_LESS_THAN_EQUAL:
			return Operator.LESS_EQUALS;
		case org.codehaus.groovy.syntax.Types.LOGICAL_OR:
			return Operator.OR;
		case org.codehaus.groovy.syntax.Types.LOGICAL_AND:
			return Operator.AND;
		case org.codehaus.groovy.syntax.Types.LEFT_SQUARE_BRACKET:
			return Operator.ACCESS_ARRAY_ELEMENT;

		default:

			String leftStr = be.getLeftExpression().getText();
			String opStr = be.getOperation().getText();
			String rightStr = be.getRightExpression().getText();

			throw new UnsupportedOperationException("Operation " + opStr
					+ " not supported! Left: " + leftStr + ", right: "
					+ rightStr);

		}
	}

	protected static IType convertMethodReturnType(StaticMethodCallExpression s) {
		boolean isVoid = true;
		MethodNode mTarget = (MethodNode) s
				.getNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET);
		if (mTarget != null && mTarget.getReturnType() != null) {
			isVoid = mTarget.getReturnType().getName().toLowerCase()
					.equals("void");
			// System.out.println("TYPECHECKED!!!");
		} else {
			System.out.println("NO TYPECHECKING!!!");
		}
		IType returnType;
		if (!isVoid) {
			returnType = new Type(mTarget.getReturnType().getName());
		} else {
			returnType = Type.VOID;
		}
		return returnType;
	}

	protected static IType convertMethodReturnType(MethodCallExpression s) {
		boolean isVoid = true;
		MethodNode mTarget = (MethodNode) s
				.getNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET);
		if (mTarget != null && mTarget.getReturnType() != null) {
			isVoid = mTarget.getReturnType().getName().toLowerCase()
					.equals("void");
			// System.out.println("TYPECHECKED!!!");
		} else {
			System.out.println("NO TYPECHECKING!!!");
		}
		IType returnType;
		if (!isVoid) {
			returnType = new Type(mTarget.getReturnType().getName());
		} else {
			returnType = Type.VOID;
		}
		return returnType;
	}

	protected static IType convertStaticMethodOwnerType(
			StaticMethodCallExpression s) {
		return convertType(s.getOwnerType());
	}

	protected static IType convertType(ClassNode type) {
		return new Type(type.getName());
	}

	protected void throwErrorMessage(String text, ASTNode node) {

		// thanks to
		// http://grails.io/post/15965611310/lessons-learnt-developing-groovy-ast-transformations
		Token token = Token.newString(node.getText(), node.getLineNumber(),
				node.getColumnNumber());
		LocatedMessage message = new LocatedMessage(text, token, sourceUnit);
		sourceUnit.getErrorCollector().addError(message);
	}

	@Deprecated
	IArgument convertExpressionToArgument(Expression e,
			ControlFlowScope currentScope) {

		stateMachine.push("convert-argument", true);

		IArgument result = null;

		if (e instanceof ConstantExpression) {
			ConstantExpression ce = (ConstantExpression) e;

			if (ce.isNullExpression()) {
				result = Argument.NULL;
			} else {
				result = Argument.constArg(new Type(ce.getType().getName(),
						true), ce.getValue());
			}
		} else if (e instanceof VariableExpression) {
			VariableExpression ve = (VariableExpression) e;

			Variable v = currentScope.getVariable(ve.getName());
			result = Argument.varArg(v);

		} else if (e instanceof PropertyExpression) {
			PropertyExpression pe = (PropertyExpression) e;
		
			Variable v = currentScope.getVariable(pe.getObjectExpression().getText());
			
			result = Argument.varArg(v);

			//throw new UnsupportedOperationException(
			//		"vrl.internal.PROPERTYEXPR not supported");

		} else if (e instanceof MethodCallExpression) {
			System.out.println("TYPE: " + e);
			visitMethodCallExpression((MethodCallExpression) e, currentScope);
			result = Argument.invArg(stateMachine.getReturnVariables().get(
					(MethodCallExpression) e));
		} else if (e instanceof StaticMethodCallExpression) {
			System.out.println("TYPE: " + e);
			visitStaticMethodCallExpression((StaticMethodCallExpression) e,
					currentScope);
			result = Argument.invArg(stateMachine.getReturnVariables().get(
					(StaticMethodCallExpression) e));
		} else if (e instanceof ConstructorCallExpression) {
			System.out.println("TYPE: " + e);
			System.out.println("CONSTRUCTOR: "
					+ stateMachine.getReturnVariables().get(
							(ConstructorCallExpression) e));
			visitConstructorCallExpression((ConstructorCallExpression) e,
					currentScope);
			result = Argument.invArg(stateMachine.getReturnVariables().get(
					(ConstructorCallExpression) e));
		} else if (e instanceof BinaryExpression) {
			System.out.println("TYPE: " + e);
			System.out.println("BINARY-EXPR: "
					+ stateMachine.getReturnVariables().get(
							(BinaryExpression) e));
			System.out.println("ARG: "
					+ stateMachine.getBoolean("convert-argument"));
			visitBinaryExpression((BinaryExpression) e, currentScope);
			result = Argument.invArg(stateMachine.getReturnVariables().get(
					(BinaryExpression) e));
		} else if (e instanceof NotExpression) {
			System.out.println("TYPE: " + e);
			System.out.println("NOT-EXPR: "
					+ stateMachine.getReturnVariables().get((NotExpression) e));
			System.out.println("ARG: "
					+ stateMachine.getBoolean("convert-argument"));
			visitNotExpression((NotExpression) e, currentScope);
			result = Argument.invArg(stateMachine.getReturnVariables().get(
					(NotExpression) e));
		} else // if nothing worked so far, we assumen null arg
		if (result == null) {
			System.err.println(" -> UNSUPPORTED-ARG: " + e);
			result = Argument.NULL;
		}
		
		setCodeRange(result, e);

		stateMachine.pop();

		return result;
	}

	void visitNotExpression(NotExpression n, ControlFlowScope currentScope) {

		if (stateMachine.getReturnVariables().containsKey(n)) {
			return;
		}

		if (currentScope instanceof ControlFlowScope) {
			ControlFlowScope cfS = (ControlFlowScope) currentScope;
			IArgument arg = convertExpressionToArgument(n.getExpression(),
					currentScope);
			Invocation notInvocation = builder.invokeNot(cfS, arg);
			setCodeRange(notInvocation, n);
			stateMachine.getReturnVariables().put(n, notInvocation);
		}
	}

	void visitStaticMethodCallExpression(StaticMethodCallExpression s,
			ControlFlowScope currentScope) {

		if (stateMachine.getReturnVariables().containsKey(s)) {
			return;
		}

		ArgumentListExpression args = (ArgumentListExpression) s.getArguments();
		IArgument[] arguments = convertArguments(args, currentScope);

		IType returnType = convertMethodReturnType(s);

		if (!(currentScope instanceof ControlFlowScope)) {
			throwErrorMessage(
					"Method can only be invoked inside ControlFlowScopes!", s);
		}

		Invocation invocation = builder.invokeStaticMethod(
				(ControlFlowScope) currentScope,
				convertStaticMethodOwnerType(s), s.getMethod(), returnType,
				arguments);

		if (stateMachine.getBoolean("variable-declaration")) {

			stateMachine.addToList(
					"variable-declaration:assignment-invocations", invocation);

		}

		setCodeRange(invocation, s);
		addCommentsToScope(currentScope, comments);

		stateMachine.getReturnVariables().put(s, invocation);

	}

	void visitMethodCallExpression(MethodCallExpression s,
			ControlFlowScope currentScope) {

		if (stateMachine.getReturnVariables().containsKey(s)) {
			return;
		}

		ArgumentListExpression args = (ArgumentListExpression) s.getArguments();
		IArgument[] arguments = convertArguments(args, currentScope);

		String objectName = null;

		boolean isIdCall = false;

		if (s.getObjectExpression() instanceof VariableExpression) {
			VariableExpression ve = (VariableExpression) s
					.getObjectExpression();
			objectName = ve.getName();
		} else if (s.getObjectExpression() instanceof ClassExpression) {
			ClassExpression ce = (ClassExpression) s.getObjectExpression();
			objectName = ce.getType().getName();

			if (ce.getType().getName().equals(VSource.class.getName())) {
				isIdCall = true;

				for (IArgument arg : arguments) {

					// TODO is this still in use? 18.02.2014
					// vIdStack.push(arg.toString());
				}
			}
		}

		IType returnType = convertMethodReturnType(s);

		if (!(currentScope instanceof ControlFlowScope)) {
			throwErrorMessage(
					"Method can only be invoked inside ControlFlowScopes!", s);
		}

		if (!isIdCall) {
			if (objectName != null) {

				Invocation invocation = builder.invokeMethod(
						(ControlFlowScope) currentScope, objectName, s
								.getMethod().getText(), returnType, arguments);

				if (stateMachine.getBoolean("variable-declaration")) {

					stateMachine.addToList(
							"variable-declaration:assignment-invocations",
							invocation);

				}

				setCodeRange(invocation, s);
				addCommentsToScope(currentScope, comments);

				stateMachine.getReturnVariables().put(s, invocation);

			} else if (s.getMethod().getText().equals("println")) {
				// codeBuilder.invokeStaticMethod(currentScope, new
				// Type("System.out"), s.getMethod().getText(), isVoid,
				// returnValueName, arguments).setCode(getCode(s));
				Invocation invocation = builder.invokeStaticMethod(
						(ControlFlowScope) currentScope,
						new Type("System.out"), s.getMethod().getText(),
						Type.VOID, arguments);
				setCodeRange(invocation, s);
				addCommentsToScope(currentScope, comments);
				// if (invocation.getReturnValue().isPresent()) {
				stateMachine.getReturnVariables().put(s, invocation);
				// }
			}
		}

	}

	void visitConstructorCallExpression(ConstructorCallExpression s,
			ControlFlowScope currentScope) {

		if (stateMachine.getReturnVariables().containsKey(s)) {
			return;
		}

		ArgumentListExpression args = (ArgumentListExpression) s.getArguments();

		IArgument[] arguments = convertArguments(args, currentScope);

		Invocation invocation = builder.createInstance(currentScope, new Type(s
				.getType().getName(), false), arguments);

		setCodeRange(invocation, s);

		if (stateMachine.getBoolean("variable-declaration")) {
			stateMachine.addToList(
					"variable-declaration:assignment-invocations", invocation);
			System.out.println("DECL-add-inv: " + invocation);
		}

		stateMachine.getReturnVariables().put(s, invocation);

		// TODO range
	}

	void visitBinaryExpression(BinaryExpression s, ControlFlowScope currentScope) {

		if (stateMachine.getBoolean("for-loop")
				&& !stateMachine.getBoolean("for-loop:compareExpression")
				&& !stateMachine.getBoolean("for-loop:incExpression")) {

			SimpleForDeclaration_Impl forD = (SimpleForDeclaration_Impl) currentScope;

			if (stateMachine.getBoolean("for-loop:declaration")
					&& !stateMachine.getBoolean("for-loop:compareExpression")) {

				if (!(s.getLeftExpression() instanceof VariableExpression)) {
					throwErrorMessage(
							"In for-loop: only binary"
									+ " expressions of the form 'a <= b'/'a >= b' with a, b being"
									+ " constant integers are supported!", s);
				}

				if (!"<=".equals(s.getOperation().getText())
						&& !">=".equals(s.getOperation().getText())) {
					throwErrorMessage(
							"In for-loop: only binary"
									+ " expressions of the form 'a <= b' or 'a >= b' with a, b being"
									+ " constant integers are supported!", s);
				}

				stateMachine.setString("for-loop:compareOperation", s
						.getOperation().getText());

				if (!(s.getRightExpression() instanceof ConstantExpression)) {
					throwErrorMessage(
							"In for-loop: only binary"
									+ " expressions of the form 'a <= b' or 'a >= b' with a, b being"
									+ " constant integers are supported!", s);
				}

				ConstantExpression ce = (ConstantExpression) s
						.getRightExpression();

				if (!(ce.getValue() instanceof Integer)) {
					// throw new IllegalStateException("In for-loop: value '" +
					// ce.getValue()
					// + "' is not an integer constant! ");

					throwErrorMessage(
							"In for-loop: only binary"
									+ " expressions of the form 'a <= b' or 'a >= b' with a, b being"
									+ " constant integers are supported!", s);
				}

				forD.setTo((int) ce.getValue());

				stateMachine.setBoolean("for-loop:compareExpression", true);
			}
		} else if (stateMachine.getBoolean("for-loop")
				&& stateMachine.getBoolean("for-loop:declaration")
				&& stateMachine.getBoolean("for-loop:compareExpression")
				&& !stateMachine.getBoolean("for-loop:incExpression")) {

			SimpleForDeclaration_Impl forD = (SimpleForDeclaration_Impl) currentScope;

			if (!"+=".equals(s.getOperation().getText())
					&& !"-=".equals(s.getOperation().getText())) {
				throw new IllegalStateException(
						"In for-loop: inc/dec '"
								+ s.getOperation().getText()
								+ "' not spupported! Must be '+=' or '-=' or '++' or '--'!");
			}

			if (!(s.getRightExpression() instanceof ConstantExpression)) {
				throwErrorMessage("In for-loop: variable '" + forD.getVarName()
						+ "' must be initialized with an integer constant!", s);
			}

			ConstantExpression ce = (ConstantExpression) s.getRightExpression();

			if (!(ce.getValue() instanceof Integer)) {
				throwErrorMessage(
						"In for-loop: inc/dec must be an integer constant!", s);
			}

			if ("+=".equals(s.getOperation().getText())) {
				forD.setInc((int) ce.getValue());
			} else if ("-=".equals(s.getOperation().getText())) {
				forD.setInc(-(int) ce.getValue());
			}

			if (forD.getInc() > 0
					&& ">=".equals(stateMachine
							.getString("for-loop:compareOperation"))) {
				throwErrorMessage(
						"In for-loop: infinite loops"
								+ " are not supported! Change '>=' to '<=' to prevent that.",
						s);
			}

			if (forD.getInc() < 0
					&& "<=".equals(stateMachine
							.getString("for-loop:compareOperation"))) {
				// throw new IllegalStateException("In for-loop: infinite loops"
				// + " are not supported! Change '<=' to '>=' to prevent that."
				// );
				throwErrorMessage(
						"In for-loop: infinite loops"
								+ " are not supported! Change '<=' to '>=' to prevent that.",
						s);
			}

			// System.out.println("s: " + s.getOperation().getText() + ", " +
			// forD.getInc());
			// System.exit(0);
			// if (forD.getInc() < 0 && "<=".equals(s.getOperation().getText()))
			// {
			// throw new IllegalStateException("In for-loop: infinite loops"
			// + " are not supported! Change '<=' to '>=' to prevent that."
			// );
			// }
			stateMachine.setBoolean("for-loop:incExpression", true);

			//
		} else {

			if (!stateMachine.getReturnVariables().containsKey(s)) {

				Operator operator = convertOperator(s);
				IArgument leftArg = convertExpressionToArgument(
						s.getLeftExpression(), currentScope);
				IArgument rightArg = convertExpressionToArgument(
						s.getRightExpression(), currentScope);

				boolean emptyAssignment = (Objects.equal(Argument.NULL,
						rightArg) && operator == Operator.ASSIGN);

				if (!emptyAssignment) {

					Invocation invocation = builder.invokeOperator(
							currentScope, leftArg, rightArg, operator);

					setCodeRange(invocation, s);

					stateMachine.getReturnVariables().put(s, invocation);
				}
			}
		}
	}

	@Deprecated
	IArgument[] convertArguments(ArgumentListExpression args,
			ControlFlowScope currentScope) {
		IArgument[] arguments = new IArgument[args.getExpressions().size()];
		for (int i = 0; i < args.getExpressions().size(); i++) {
			arguments[i] = convertExpressionToArgument(args.getExpression(i),
					currentScope);
		}
		return arguments;
	}
}
