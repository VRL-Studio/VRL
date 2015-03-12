package eu.mihosoft.vrl.lang.model;


import static org.junit.Assert.*;

import org.junit.Test;

public class CodeLineColumnMapperTest {

	@Test
	public void test() {

		CodeLineColumnMapper loc = new CodeLineColumnMapper();
		loc.init("test\ntesttest\ntesttesttest\n");
		// get line numbers
		assertEquals(0, loc.getLocation(0, 0).getOffset());
		assertEquals(5, loc.getLocation(1, 0).getOffset());
		assertEquals(14, loc.getLocation(2, 0).getOffset());
		
		// increase col
		assertEquals(2, loc.getLocation(0, 2).getOffset());
		assertEquals(7, loc.getLocation(1, 2).getOffset());
		assertEquals(16, loc.getLocation(2, 2).getOffset());
		
		// get line, col by offset
		assertEquals(0, loc.getLocation(2).getLine());
		assertEquals(2, loc.getLocation(2).getColumn());
		
		assertEquals(1, loc.getLocation(6).getLine());
		assertEquals(1, loc.getLocation(6).getColumn());
		
		assertEquals(2, loc.getLocation(16).getLine());
		assertEquals(2, loc.getLocation(16).getColumn());
		
		loc.init("test\n\n");
		assertEquals(0, loc.getLocation(4).getLine());
		assertEquals(1, loc.getLocation(5).getLine());
		assertEquals(2, loc.getLocation(6).getLine());
		
		loc.init("test\n\ntest");
		assertEquals(2, loc.getLocation(10).getLine());
		assertEquals(4, loc.getLocation(10).getColumn());
		assertEquals(3, loc.getLocation(11).getLine());
		assertEquals(0, loc.getLocation(11).getColumn());
		assertEquals(-1, loc.getLocation(12).getLine());
		assertEquals(-1, loc.getLocation(12).getColumn());
		
		loc.init("test\n\ntest\n"); // TODO is this an issue??
		assertEquals(2, loc.getLocation(10).getLine());
		assertEquals(4, loc.getLocation(10).getColumn());
		assertEquals(3, loc.getLocation(11).getLine());
		assertEquals(0, loc.getLocation(11).getColumn());
		assertEquals(-1, loc.getLocation(12).getLine());
		assertEquals(-1, loc.getLocation(12).getColumn());
		
	}

}
