package eu.mihosoft.vrl.lang.command;

public interface Command {
	
	boolean canUndo();
	void execute();
	void rollback();

}
