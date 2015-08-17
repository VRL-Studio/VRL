package eu.mihosoft.vrl.instrumentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

import org.codehaus.groovy.ast.expr.Expression;

import eu.mihosoft.vrl.lang.model.CodeEntity;
import eu.mihosoft.vrl.lang.model.Invocation;

@Deprecated
public class StateMachine {

    private final Stack<Map<String, Object>> stateStack = new Stack<>();
    private Map<Expression, Invocation> returnVariables = new HashMap<>();

    public StateMachine() {
        push("root", true);
    }

    public void setBoolean(String name, boolean state) {
        stateStack.peek().put(name, state);
    }

    public void setString(String name, String state) {
        stateStack.peek().put(name, state);
    }

    public boolean getBoolean(String name) {
        Boolean result = (Boolean) stateStack.peek().get(name);

        if (result == null) {
            return false;
        }

        return result;
    }

    public String getString(String name) {
        String result = (String) stateStack.peek().get(name);

        if (result == null) {
            return "";
        }

        return result;
    }

    public void setEntity(String name, CodeEntity entity) {
        stateStack.peek().put(name, entity);
    }

    public Optional<CodeEntity> getEntity(String name) {
        CodeEntity result = (CodeEntity) stateStack.peek().get(name);

        return Optional.ofNullable(result);
    }

    public <T> List<T> addToList(String name, T element) {

//        System.out.println("add-to-list: " + name + ", " + element);

        Object obj = stateStack.peek().get(name);

        if (obj == null) {
            obj = new ArrayList<T>();
        }

        List<T> result = (List<T>) obj;

        stateStack.peek().put(name, result);

        result.add(element);

        return result;

    }

    public <T> List<T> getList(String name) {
        Object obj = stateStack.peek().get(name);

        if (obj == null) {
//            System.err.println("WARNING: list " + name + " was not available (will be created now)");
            obj = new ArrayList<T>();
        }

        return (List<T>) obj;
    }

    public void push(String name, boolean state) {
        stateStack.push(new HashMap<>());
        stateStack.peek().put(name, state);
    }

    public void pop() {
        stateStack.pop();
    }

	public Map<Expression, Invocation> getReturnVariables() {
		return returnVariables;
	}

	public void setReturnVariables(Map<Expression, Invocation> returnVariables) {
		this.returnVariables = returnVariables;
	}

}