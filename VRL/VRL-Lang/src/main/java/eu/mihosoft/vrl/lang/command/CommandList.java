package eu.mihosoft.vrl.lang.command;

import java.util.Stack;

public class CommandList {

	Stack<Command> history = new Stack<Command>();
	Stack<Command> stack = new Stack<Command>();

	public void execute(Command cmd) {
		cmd.execute();
		history.push(cmd);
	}

	public boolean canUndo() {
		return history.size() > 0 && history.peek().canUndo();
	}

	public boolean canRedo() {
		return stack.size() > 0;
	}

	public void undo() {
		Command cmd = history.pop();
		stack.push(cmd);
		cmd.rollback();
	}

	public void redo() {
		Command cmd = stack.pop();
		execute(cmd);
	}

}
