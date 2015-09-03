package eu.mihosoft.vrl.lang.command;

import java.util.Stack;

public class AggregateCommand implements Command {

	private Command[] cmds;

	public AggregateCommand(Command... cmds) {
		this.cmds = cmds;
	}

	@Override
	public boolean canUndo() {
		for (Command cmd : cmds)
			if (!cmd.canUndo())
				return false;
		return true;
	}

	@Override
	public void execute() {
		Stack<Command> done = new Stack<Command>();
		for (Command cmd : cmds) {
			try {
				cmd.execute();
				done.push(cmd);
			} catch (RuntimeException c) {
				if (canUndo()) {
					for (Command d : done) {
						d.rollback();
					}
				}
				throw new RuntimeException(
						"Exception occured during aggregate command execution.",
						c);
			}
		}
	}

	@Override
	public void rollback() {
		for (Command cmd : cmds) {
			cmd.rollback();
		}
	}

}
