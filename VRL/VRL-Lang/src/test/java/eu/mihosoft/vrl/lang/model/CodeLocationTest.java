/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
        //2:class B {}\n|
        //               ^ <- note: newline (\n) is a single char!
        //               |
        //              (23)->(2,-1)
        parameterizedLocationTestCharIndexToLineAndColumn(code, 23, -1, -1);
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
        // (3,0) -> (-1)
        parameterizedLocationTestLineAndColumnToCharIndex(code, 3, 0, -1);
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
        CodeLocation location = new CodeLocation(charPos, new StringReader(s));

        int line = location.getLine();
        int column = location.getColumn();

        Assert.assertTrue("expected line " + expectedLine + ", column " + expectedColumn + ", got line: " + line + ", column: " + column, line == expectedLine && column == expectedColumn);
    }

    private void parameterizedLocationTestLineAndColumnToCharIndex(String s, int line, int column, int expectedCharIndex) {
        CodeLocation location = new CodeLocation(line, column, new StringReader(s));

        int charIndex = location.getCharIndex();

        Assert.assertTrue("expected char index: " + expectedCharIndex + ", got char index: " + charIndex, charIndex == expectedCharIndex);
    }
}
