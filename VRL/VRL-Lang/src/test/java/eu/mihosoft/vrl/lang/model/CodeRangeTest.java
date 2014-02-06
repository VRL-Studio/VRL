/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;
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
        String code = String.join("\n",
                "class A {}",
                "",
                "// comment 1",
                "// comment 2",
                "",
                "class B {}", "");

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
        parameterizedValidReadRangeTestLineAndColumn(code, 2, 1, 5, 7, "/ comment 1\n// comment 2\n\nclass B");
        
        parameterizedValidReadRangeTestLineAndColumn(code, 0, 0, 0, 0, "");
    }
    
    @Test
    public void rangeIntersectionTest() {
        // note:
        // - newline (\n, \r\n, \r) is treatet as single char!
        // - [ and ] are used to define text ranges
        //
        String code = String.join("\n",
                "class A {}",
                "",
                "// comment 1",
                "// comment 2",
                "",
                "class B {}", "");

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
        Reader codeReader = new StringReader(code);
        
        ICodeRange a = new CodeRange(6, 44, codeReader);
        ICodeRange b = new CodeRange(2, 5, 5, 10, codeReader);
        
        String expectedCode = "mment 1\n// comment 2\n\nclass";
        
        ICodeRange intersection = a.intersection(b);
        
        ICodeReader intersectionReader = new CodeReader(codeReader);
        
        boolean readSuccess = false;
        String intersectionCode = "";
        try {
            intersectionCode = intersectionReader.read(intersection);
            readSuccess = true;
        } catch (IOException ex) {
            Logger.getLogger(CodeRangeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Assert.assertTrue("Reader must not throw an exception!", readSuccess);
        
        Assert.assertTrue("Intersection [" + expectedCode.replace("\n", "\\n") + "] expected, got [" + intersectionCode.replace("\n", "\\n") + "]", expectedCode.equals(intersectionCode));
    }
    
    private void parameterizedValidReadRangeTestCharIndex(String code, int beginCharIdx, int endCharIdx, String expectedSubString) {
        Reader codeReader = new StringReader(code);
        ICodeRange range = new CodeRange(beginCharIdx, endCharIdx, codeReader);
        
        ICodeReader reader = new CodeReader(codeReader);
        
        boolean successRead = false;
        
        String rangeCode = "";
        
        try {
            rangeCode = reader.read(range);
            successRead = true;
        } catch (IOException ex) {
            Logger.getLogger(CodeRangeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Assert.assertTrue("Reader must not throw an exception", successRead);
        Assert.assertEquals("[" + expectedSubString + "] expected, got [" + rangeCode + "]", expectedSubString, rangeCode);
    }
    
    private void parameterizedValidReadRangeTestLineAndColumn(String code, int beginL, int beginC, int endL, int endC, String expectedSubString) {
        Reader codeReader = new StringReader(code);
        ICodeRange range = new CodeRange(beginL, beginC, endL, endC, codeReader);
        
        ICodeReader reader = new CodeReader(codeReader);
        
        boolean successRead = false;
        
        String rangeCode = "";
        
        try {
            rangeCode = reader.read(range);
            successRead = true;
        } catch (IOException ex) {
            Logger.getLogger(CodeRangeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Assert.assertTrue("Reader must not throw an exception", successRead);
        Assert.assertTrue("[" + rangeCode.replace("\n", "\\n") + "] expected, got [" + expectedSubString.replace("\n", "\\n") + "]", expectedSubString.equals(rangeCode));
    }
    
}
