/* 
 * CodeRangeTest.java
 *
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

package eu.mihosoft.vrl.lang.model;

import eu.mihosoft.vrl.lang.ICodeReader;
import eu.mihosoft.vrl.lang.CodeReader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class CodeRangeTest {

	@BeforeClass
	public static void setUpClass() {
		//
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testValidReadRangeTest() {

		// note:
		// - newline (\n, \r\n, \r) is treatet as single char!
		// - [ and ] are used to define text ranges
		//
		String code = String.join("\n", "class A {}", "", "// comment 1",
				"// comment 2", "", "class B {}", "");

		// 0:c[las]s A {}\n
		parameterizedValidReadRangeTestCharIndex(code, 1, 4, "las");

		// note:
		// - newline (\n, \r\n, \r) is treatet as single char!
		// - [ and ] are used to define text ranges
		//
		// 0:class A {}\n
		// 1:\n
		// 2:/[/ comment 1\n
		// 3:// comment 2\n
		// 4:\n
		// 5:class B] {}\n
		parameterizedValidReadRangeTestLineAndColumn(code, 2, 1, 5, 7,
				"/ comment 1\n// comment 2\n\nclass B");

		parameterizedValidReadRangeTestLineAndColumn(code, 0, 0, 0, 0, "");
	}

	@Test
	public void rangeIntersectionTest() {
		// note:
		// - newline (\n, \r\n, \r) is treatet as single char!
		// - [ and ] are used to define text ranges
		//
		String code = String.join("\n", "class A {}", "", "// comment 1",
				"// comment 2", "", "class B {}", "");

		// intersection of
		// a):
		//
		// 0:[A {}\n
		// 1:\n
		// 2:// comment 1\n
		// 3:// comment 2\n
		// 4:\n
		// 5:class]
		//
		// and b):
		// 2:// co[mment 1\n
		// 3:// comment 2\n
		// 4:\n
		// 5:class B {}]
		CodeLineColumnMapper locator = new CodeLineColumnMapper();
		Reader codeReader = new StringReader(code);
		locator.init(codeReader);

		ICodeRange a = new CodeRange(6, 44, locator);
		ICodeRange b = new CodeRange(2, 5, 5, 10, locator);

		String expectedCode = "mment 1\n// comment 2\n\nclass";

		ICodeRange intersection = a.intersection(b);

		ICodeReader intersectionReader = new CodeReader(codeReader);

		boolean readSuccess = false;
		String intersectionCode = "";
		try {
			intersectionCode = intersectionReader.read(intersection);
			readSuccess = true;
		} catch (IOException ex) {
			Logger.getLogger(CodeRangeTest.class.getName()).log(Level.SEVERE,
					null, ex);
		}

		Assert.assertTrue("Reader must not throw an exception!", readSuccess);

		Assert.assertTrue("Intersection [" + expectedCode.replace("\n", "\\n")
				+ "] expected, got [" + intersectionCode.replace("\n", "\\n")
				+ "]", expectedCode.equals(intersectionCode));
	}

	@Test
	public void resetReaderTest() {

		// note:
		// - newline (\n, \r\n, \r) is treatet as single char!
		// - [ and ] are used to define text ranges
		//
		String code = String.join("\n", "class A {}", "", "// comment 1",
				"// comment 2", "", "class B {}", "");
		CodeLineColumnMapper locator = new CodeLineColumnMapper();
		Reader reader = new StringReader(code);
		locator.init(reader);

		ICodeRange range = new CodeRange(1, 4, locator);

		ICodeReader codeReader = new CodeReader(reader);

		boolean readSuccess = false;

		String firstResult = "";
		String secondResult = "";

		try {
			firstResult = codeReader.read(range);
			secondResult = codeReader.read(range);
			readSuccess = true;
		} catch (IOException ex) {
			Logger.getLogger(CodeRangeTest.class.getName()).log(Level.SEVERE,
					null, ex);
		}

		Assert.assertTrue("Reader must not throw an exception", readSuccess);
		Assert.assertTrue("First result must not be empty",
				!firstResult.isEmpty());
		Assert.assertTrue("Second result must not be empty",
				!secondResult.isEmpty());
		Assert.assertTrue("First result must be equal to second result: ["
				+ firstResult + "] == [" + secondResult + "]",
				firstResult.equals(secondResult));
	}

	@Test
	public void rangeOrderTest() {

		List<ICodeRange> ranges = new ArrayList<>();

		for (int i = 0; i < 100; i++) {
			ranges.add(new CodeRange(i, i * (int) (10 * Math.random())));
		}

		Collections.shuffle(ranges);

		boolean shuffled = false;

		for (int i = 0; i < 100; i++) {
			if (ranges.get(i).getBegin().getCharIndex() != i) {
				shuffled = true;
				break;
			}
		}

		Assert.assertTrue("Range list must be shuffled", shuffled);

		Collections.sort(ranges);

		boolean sorted = true;

		for (int i = 0; i < 100; i++) {
			if (ranges.get(i).getBegin().getCharIndex() != i) {
				sorted = false;
				break;
			}
		}

		Assert.assertTrue("Range list must be sorted", sorted);
	}

	private void parameterizedValidReadRangeTestCharIndex(String code,
			int beginCharIdx, int endCharIdx, String expectedSubString) {
		Reader codeReader = new StringReader(code);
		CodeLineColumnMapper locator = new CodeLineColumnMapper();
		locator.init(codeReader);
		ICodeRange range = new CodeRange(beginCharIdx, endCharIdx, locator);

		ICodeReader reader = new CodeReader(codeReader);

		boolean successRead = false;

		String rangeCode = "";

		try {
			rangeCode = reader.read(range);
			successRead = true;
		} catch (IOException ex) {
			Logger.getLogger(CodeRangeTest.class.getName()).log(Level.SEVERE,
					null, ex);
		}

		Assert.assertTrue("Reader must not throw an exception", successRead);
		Assert.assertEquals("[" + expectedSubString + "] expected, got ["
				+ rangeCode + "]", expectedSubString, rangeCode);
	}

	private void parameterizedValidReadRangeTestLineAndColumn(String code,
			int beginL, int beginC, int endL, int endC, String expectedSubString) {
		Reader codeReader = new StringReader(code);
		CodeLineColumnMapper locator = new CodeLineColumnMapper();
		locator.init(codeReader);
		ICodeRange range = new CodeRange(beginL, beginC, endL, endC, locator);

		ICodeReader reader = new CodeReader(codeReader);

		boolean successRead = false;

		String rangeCode = "";

		try {
			rangeCode = reader.read(range);
			successRead = true;
		} catch (IOException ex) {
			Logger.getLogger(CodeRangeTest.class.getName()).log(Level.SEVERE,
					null, ex);
		}

		Assert.assertTrue("Reader must not throw an exception", successRead);
		Assert.assertTrue("[" + rangeCode.replace("\n", "\\n")
				+ "] expected, got [" + expectedSubString.replace("\n", "\\n")
				+ "]", expectedSubString.equals(rangeCode));
	}

}
