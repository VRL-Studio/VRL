package eu.mihosoft.vrl.instrumentation.composites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.EmptyExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
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
import eu.mihosoft.vrl.lang.model.BinaryOperatorInvocation;
import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.CodeRange;
import eu.mihosoft.vrl.lang.model.Comment;
import eu.mihosoft.vrl.lang.model.CommentImpl;
import eu.mihosoft.vrl.lang.model.ConstantValue;
import eu.mihosoft.vrl.lang.model.ConstantValueFactory;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
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

	protected static Operator convertPostfixOperator(String op) {
		switch (op) {
		case "++":
			return Operator.INC_ONE;
		case "--":
			return Operator.DEC_ONE;
		default:
			throw new UnsupportedOperationException("Unknown operator " + op);
		}
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

	protected static IArgument[] convertArguments(String key,
			ArgumentListExpression args, TransformContext context) {
		IArgument[] arguments = new IArgument[args.getExpressions().size()];
		for (int i = 0; i < args.getExpressions().size(); i++) {
			arguments[i] = convertToArgument(key + "[" + i + "]",
					args.getExpression(i), context);
		}
		return arguments;
	}

	protected static IArgument convertToArgument(String key, Expression expr,
			TransformContext context) {
		IArgument arg;
		if (expr instanceof ConstantExpression) {
			ConstantValue constVal = context.resolve(key, expr,
					ConstantValue.class);
			arg = Argument.constArg(constVal);
		} else if (expr instanceof BinaryExpression) {
			Invocation inv = context.resolve(key, expr,
					BinaryOperatorInvocation.class);
			arg = Argument.invArg(inv);
		} else if (expr instanceof BooleanExpression) {
			Invocation inv = context.resolve(key, expr,
					BinaryOperatorInvocation.class);
			arg = Argument.invArg(inv);
		} else if (expr instanceof MethodCallExpression) {
			Invocation inv = context.resolve(key, expr, Invocation.class);
			arg = Argument.invArg(inv);
		} else if (expr instanceof PropertyExpression) {
			Variable inv = context.resolve(key, expr, Variable.class);
			arg = Argument.varArg(inv);
		} else if (expr instanceof VariableExpression) {
			Variable v = context.resolve(key, expr, Variable.class);
			arg = Argument.varArg(v);
		} else if (expr instanceof SpreadExpression) {
			// TODO add support for SpreadExpression
			Variable v = context.resolve(key,
					((SpreadExpression) expr).getExpression(), Variable.class);
			arg = Argument.varArg(v);
		} else if (expr instanceof EmptyExpression) {
			return Argument.NULL;
		} else {
			throw new UnsupportedOperationException(
					"Unsupported expression type for argument encountered (key '"
							+ key + "') " + expr.getClass().getSimpleName());
		}
		return arg;
	}

	protected static <T extends CodeEntity> T getParentScope(CodeEntity entity,
			Class<T> cls) {
		if (cls.isInstance(entity))
			return cls.cast(entity);
		return getParentScope(entity.getParent(), cls);
	}
}
