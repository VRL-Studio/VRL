package eu.mihosoft.vrl.lang.command;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AggregateCommandTest {

	static class Data {
		int value;
	}

	Data data;
	Command incCommand;
	Command crasher;

	@Before
	public void setUp() {
		data = new Data();
		incCommand = new Command() {
			@Override
			public boolean canUndo() {
				return true;
			}

			@Override
			public void execute() {
				data.value++;
			}

			@Override
			public void rollback() {
				data.value--;
			}
		};
		crasher = new Command() {

			@Override
			public boolean canUndo() {
				return true;
			}

			@Override
			public void execute() {
				throw new RuntimeException("Nooooesss!");
			}

			@Override
			public void rollback() {

			}

		};
	}

	@Test
	public void testDefaultExecuteRollback() {

		// test default execute/rollback
		AggregateCommand cmd = new AggregateCommand(incCommand, incCommand,
				incCommand);
		cmd.execute();
		assertEquals(3, data.value);

		cmd.rollback();
		assertEquals(0, data.value);

	}

	@Test
	public void testFailureDuringExecute() {

		// test failure during execution and rollback
		AggregateCommand cmd = new AggregateCommand(incCommand, crasher,
				incCommand);
		try {
			cmd.execute();
			fail();
		} catch (RuntimeException ex) {

		}
		assertEquals(0, data.value);
	}

	@Test
	public void testNotUndoableAggregate() {
		// test unundoable aggregate command
		crasher = new Command() {

			@Override
			public boolean canUndo() {
				return false;
			}

			@Override
			public void execute() {
				throw new RuntimeException("Oh noeesss!!");
			}

			@Override
			public void rollback() {

			}
		};
		AggregateCommand cmd = new AggregateCommand(incCommand, crasher);
		assertFalse(cmd.canUndo());
		try {
			cmd.execute();
			fail();
		} catch (RuntimeException e) {
		}
		assertEquals(1, data.value);
	}

}
