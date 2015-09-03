package eu.mihosoft.vrl.lang.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class CommandListTest {

	static class Data {
		int value;
	}

	Data data;
	Command incCommand;
	CommandList list;

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
		list = new CommandList();
	}

	@Test
	public void testInitialState() {

		assertFalse(list.canRedo());
		assertFalse(list.canUndo());
	}

	@Test
	public void testUndoRedo() {

		list.execute(incCommand);
		assertTrue(list.canUndo());
		assertEquals(1, data.value);

		list.undo();
		assertFalse(list.canUndo());
		assertTrue(list.canRedo());
		assertEquals(0, data.value);

		list.redo();
		assertTrue(list.canUndo());
		assertEquals(1, data.value);

	}
}
