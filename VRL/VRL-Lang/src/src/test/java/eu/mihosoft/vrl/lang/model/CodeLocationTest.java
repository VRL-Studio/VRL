/* 
 * CodeLocationTest.java
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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public class CodeLocationTest {

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
    public void testValidLocationConversionCharIndexToLineAndColumn() {

        //0:class A {}\n
        //  ^
        //  |
        // (0)->(0,0)
        String code = String.join("\n",
                "class A {}",
                "",
                "class B {}", "");

        parameterizedLocationTestCharIndexToLineAndColumn(code, 0, 0, 0);

        //0:class A {}\n
        //   ^
        //   |
        //  (1)->(0,1)
        parameterizedLocationTestCharIndexToLineAndColumn(code, 1, 0, 1);

        //0:class A {}\n
        //            ^ <- note: newline (\n) is a single char!
        //            |
        //           (10)->(0,10)
        parameterizedLocationTestCharIndexToLineAndColumn(code, 10, 0, 10);

        //0:class A {}\n
        //1:\n 
        //  ^  <- note: newline (\n) is a single char!
        //  |
        // (11)->(1,0)
        parameterizedLocationTestCharIndexToLineAndColumn(code, 11, 1, 0);

        //0:class A {}\n
        //1:\n
        //2:class B {}\n
        //  ^
        //  |
        // (12)->(2,0)
        parameterizedLocationTestCharIndexToLineAndColumn(code, 12, 2, 0);

        //0:class A {}\n
        //1:\n
        //2:class B {}\n
        //           ^
        //           |
        //          (21)->(2,9)
        parameterizedLocationTestCharIndexToLineAndColumn(code, 21, 2, 9);

        //0:class A {}\n
        //1:\n
        //2:class B {}\n
        //            ^ <- note: newline (\n) is a single char!
        //            |
        //           (22)->(2,10)
        parameterizedLocationTestCharIndexToLineAndColumn(code, 22, 2, 10);
    }

    @Test
    public void testInvalidLocationConversionCharIndexToLineAndColumn() {

        // note: the symbol | denotes the beginning/ending of the line
        //0: |class A {}\n
        //  ^
        //  |
        // (-1)->(0,-1)
        String code = String.join("\n",
                "class A {}",
                "",
                "class B {}", "");

        parameterizedLocationTestCharIndexToLineAndColumn(code, -1, -1, -1);

        //0:class A {}\n
        //1:\n
        //2:class B {}\n
        //3:
        //  ^ <- note: newline (\n) is a single char!
        //  |
        // (23)->(3,0)
        parameterizedLocationTestCharIndexToLineAndColumn(code, 23, 3, 0);
        
      //0:class A {}\n
        //1:\n
        //2:class B {}\n|
        //3:$          
        //   ^ 
        //   |
        //  (24)->(-1,-1)
        parameterizedLocationTestCharIndexToLineAndColumn(code, 24, -1, -1);
    }

    @Test
    public void testValidLocationConversionLineAndColumnToCharIndex() {

        //0:class A {}\n
        //  ^
        //  |
        // (0,0) -> (0)
        String code = String.join("\n",
                "class A {}",
                "",
                "class B {}", "");

        parameterizedLocationTestLineAndColumnToCharIndex(code, 0, 0, 0);

        //0:class A {}\n
        //   ^
        //   |
        // (0,1)->(1)
        parameterizedLocationTestLineAndColumnToCharIndex(code, 0, 1, 1);

        //0:class A {}\n
        //            ^ <- note: newline (\n) is a single char!
        //            |
        //          (0,10) -> (10)
        parameterizedLocationTestLineAndColumnToCharIndex(code, 0, 10, 10);

        //0:class A {}\n
        //1:\n 
        //  ^  <- note: newline (\n) is a single char!
        //  |
        // (1,0) -> (11)
        parameterizedLocationTestLineAndColumnToCharIndex(code, 1, 0, 11);

        //0:class A {}\n
        //1:\n
        //2:class B {}\n
        //  ^
        //  |
        // (2,0) -> (12)
        parameterizedLocationTestLineAndColumnToCharIndex(code, 2, 0, 12);

        //0:class A {}\n
        //1:\n
        //2:class B {}\n
        //           ^
        //           |
        //         (2,9) -> (21)
        parameterizedLocationTestLineAndColumnToCharIndex(code, 2, 9, 21);

        //0:class A {}\n
        //1:\n
        //2:class B {}\n
        //            ^ <- note: newline (\n) is a single char!
        //            |
        //          (2,10) -> (22)
        parameterizedLocationTestLineAndColumnToCharIndex(code, 2, 10, 22);
    }

    @Test
    public void testInvalidLocationConversionLineAndColumnToCharIndex() {

        // note: the symbol | denotes the beginning/ending of the line
        //-1:
        //   ^
        //   |
        // (-1,-1) -> (-1)
        String code = String.join("\n",
                "class A {}",
                "",
                "class B {}", "");

        parameterizedLocationTestLineAndColumnToCharIndex(code, -1, -1, -1);

        //0:class A {}\n| 
        //               ^
        //               |
        //             (0,11) -> (-1)
        parameterizedLocationTestLineAndColumnToCharIndex(code, 0, 11, -1);

        //0: |class A {}\n
        //1: |\n 
        //  ^
        //  |
        // (1,-1) -> (-1)
        parameterizedLocationTestLineAndColumnToCharIndex(code, 1, -1, -1);

        //0:class A {}\n|
        //1:\n|
        //     ^
        //     |
        //   (1,1) -> (-1)
        parameterizedLocationTestLineAndColumnToCharIndex(code, 1, 1, -1);

        //0: |class A {}\n
        //1: |\n
        //2: |class B {}\n
        //  ^
        //  |
        // (2,-1) -> (-1)
        parameterizedLocationTestLineAndColumnToCharIndex(code, 2, -1, -1);

        //0:class A {}\n
        //1:\n
        //2:class B {}\n|
        //              ^
        //              |
        //            (2,11) -> (-1)
        parameterizedLocationTestLineAndColumnToCharIndex(code, 2, 11, -1);

        // -1:
        //    ^
        //    |
        //  (-1,0) -> (-1)
        parameterizedLocationTestLineAndColumnToCharIndex(code, -1, 0, -1);

        //0:class A {}\n
        //1:\n
        //2:class B {}\n|
        //3: 
        //  ^
        //  |
        // (3,0) -> (23)
        parameterizedLocationTestLineAndColumnToCharIndex(code, 3, 0, 23);
        
        //0:class A {}\n
        //1:\n
        //2:class B {}\n|
        //3:$ 
        //   ^
        //   |
        //  (3,1) -> (-1)
        parameterizedLocationTestLineAndColumnToCharIndex(code, 3, 1, -1);
    }

    @Test
    public void locationOrderTest() {

        List<ICodeLocation> ranges = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            ranges.add(new CodeLocation(i));
        }

        Collections.shuffle(ranges);

        boolean shuffled = false;

        for (int i = 0; i < 100; i++) {
            if (ranges.get(i).getCharIndex() != i) {
                shuffled = true;
                break;
            }
        }

        Assert.assertTrue("Location list must be shuffled", shuffled);

        Collections.sort(ranges);

        boolean sorted = true;

        for (int i = 0; i < 100; i++) {
            if (ranges.get(i).getCharIndex() != i) {
                sorted = false;
                break;
            }
        }

        Assert.assertTrue("Location list must be sorted", sorted);
    }

    private void parameterizedLocationTestCharIndexToLineAndColumn(String s, int charPos, int expectedLine, int expectedColumn) {
    	CodeLineColumnMapper locator = new CodeLineColumnMapper();
    	locator.init(new StringReader(s));
    	CodeLocation location = new CodeLocation(charPos, locator);

        int line = location.getLine();
        int column = location.getColumn();

        Assert.assertTrue("expected line " + expectedLine + ", column " + expectedColumn + ", got line: " + line + ", column: " + column, line == expectedLine && column == expectedColumn);
    }

    private void parameterizedLocationTestLineAndColumnToCharIndex(String s, int line, int column, int expectedCharIndex) {
        
    	CodeLineColumnMapper locator = new CodeLineColumnMapper();
    	locator.init(new StringReader(s));
    	CodeLocation location = new CodeLocation(line, column, locator);

        int charIndex = location.getCharIndex();

        Assert.assertTrue("expected char index: " + expectedCharIndex + ", got char index: " + charIndex, charIndex == expectedCharIndex);
    }
}
