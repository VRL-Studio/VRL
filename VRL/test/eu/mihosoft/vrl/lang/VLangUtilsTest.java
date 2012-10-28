package eu.mihosoft.vrl.lang;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;
import java.io.InputStream;
import eu.mihosoft.vrl.io.vrlx.AbstractCode;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VLangUtilsTest {

    public VLangUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private String readSampleCode(String codeName) {

        BufferedReader reader = null;
        String code = "";

        try {
            // load Sample Code
            InputStream iStream = getClass().getResourceAsStream(
                    codeName);

            reader = new BufferedReader(new InputStreamReader(iStream));

            while (reader.ready()) {
                code += reader.readLine() + "\n";
            }
        } catch (Exception ex) {
            Logger.getLogger(VLangUtilsTest.class.getName()).
                    log(Level.SEVERE, null, ex);
            fail("Cannot read test code: " + codeName);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(VLangUtilsTest.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }
        return code;
    }

    /**
     * Test of classNameFromCode method, of class VLangUtils.
     */
    @Test
    public void testClassNameFromCode_String() {
        System.out.println("classNameFromCode");
        String code = readSampleCode(
                "/eu/mihosoft/vrl/lang/source_class_name_Source01");
        String expResult = "Source01";
        String result = VLangUtils.classNameFromCode(code);
        assertEquals(expResult, result);
    }

//    /**
//     * Test of classNameFromCode method, of class VLangUtils.
//     */
//    @Test
//    public void testClassNameFromCode_AbstractCode() {
//        System.out.println("classNameFromCode");
//        AbstractCode aCode = null;
//        String expResult = "";
//        String result = VLangUtils.classNameFromCode(aCode);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of classDefinedInCode method, of class VLangUtils.
     */
    @Test
    public void testClassDefinedInCode() {
        System.out.println("classDefinedInCode");
        String code = readSampleCode(
                "/eu/mihosoft/vrl/lang/source_class_defined");
        boolean expResult = true;
        boolean result = VLangUtils.classDefinedInCode(code);
        assertEquals(expResult, result);

        String code2 = readSampleCode(
                "/eu/mihosoft/vrl/lang/source_no_class_defined");
        boolean expResult2 = false;
        boolean result2 = VLangUtils.classDefinedInCode(code2);
        assertEquals(expResult2, result2);

    }

    /**
     * Test of packageDefinedInCode method, of class VLangUtils.
     */
    @Test
    public void testPackageDefinedInCode() {
        System.out.println("packageDefinedInCode");
        String code = readSampleCode(
                "/eu/mihosoft/vrl/lang/source_package_defined");
        boolean expResult = true;
        boolean result = VLangUtils.packageDefinedInCode(code);
        assertEquals(expResult, result);

        String code2 = readSampleCode(
                "/eu/mihosoft/vrl/lang/source_package_not_defined");
        boolean expResult2 = false;
        boolean result2 = VLangUtils.packageDefinedInCode(code2);
        assertEquals(expResult2, result2);

    }

    /**
     * Test of packageNameFromCode method, of class VLangUtils.
     */
    @Test
    public void testPackageNameFromCode() {
        System.out.println("packageNameFromCode");
        String code = readSampleCode(
                "/eu/mihosoft/vrl/lang/source_package_defined");
        String expResult = "test.a.b.c";
        String result = VLangUtils.packageNameFromCode(code);
        assertEquals(expResult, result);

        String code2 = readSampleCode(
                "/eu/mihosoft/vrl/lang/source_package_not_defined");
        String expResult2 = "";
        String result2 = VLangUtils.packageNameFromCode(code2);
        assertEquals(expResult2, result2);
    }

    /**
     * Test of fullClassNameFromCode method, of class VLangUtils.
     */
    @Test
    public void testFullClassNameFromCode() {
        System.out.println("fullClassNameFromCode");
        String code = readSampleCode(
                "/eu/mihosoft/vrl/lang/source_class_name_a.b.c.Source01");
        String expResult = "a.b.c.Source01";
        String result = VLangUtils.fullClassNameFromCode(code);
        assertEquals(expResult, result);

        String code2 = readSampleCode(
                "/eu/mihosoft/vrl/lang/source_no_class_defined");
        String expResult2 = "";
        String result2 = VLangUtils.fullClassNameFromCode(code2);
        assertEquals(expResult2, result2);
    }

//    /**
//     * Test of interfaceNameFromCode method, of class VLangUtils.
//     */
//    @Test
//    public void testInterfaceNameFromCode() {
//        System.out.println("interfaceNameFromCode");
//        AbstractCode aCode = null;
//        String expResult = "";
//        String result = VLangUtils.interfaceNameFromCode(aCode);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of isClassNameValid method, of class VLangUtils.
     */
    @Test
    public void testIsClassNameValid() {
        System.out.println("isClassNameValid");
        String className = "ClassOne";
        boolean expResult = true;
        boolean result = VLangUtils.isClassNameValid(className);
        assertEquals(expResult, result);

        String className2 = "1ClassOne";
        boolean expResult2 = false;
        boolean result2 = VLangUtils.isClassNameValid(className2);
        assertEquals(expResult2, result2);

        String className3 = "ClassOne^";
        boolean expResult3 = false;
        boolean result3 = VLangUtils.isClassNameValid(className3);
        assertEquals(expResult3, result3);

        String className4 = "if";
        boolean expResult4 = false;
        boolean result4 = VLangUtils.isClassNameValid(className4);
        assertEquals(expResult4, result4);

        String className5 = "";
        boolean expResult5 = false;
        boolean result5 = VLangUtils.isClassNameValid(className5);
        assertEquals(expResult5, result5);
    }

    /**
     * Test of isMethodNameValid method, of class VLangUtils.
     */
    @Test
    public void testIsMethodNameValid() {

        System.out.println("isMethodNameValid");

        String methodName = "methodOne";
        boolean expResult = true;
        boolean result = VLangUtils.isMethodNameValid(methodName);
        assertEquals(expResult, result);

        String methodName2 = "1method";
        boolean expResult2 = false;
        boolean result2 = VLangUtils.isMethodNameValid(methodName2);
        assertEquals(expResult2, result2);

        String methodName3 = "method^";
        boolean expResult3 = false;
        boolean result3 = VLangUtils.isMethodNameValid(methodName3);
        assertEquals(expResult3, result3);

        String methodName4 = "const";
        boolean expResult4 = false;
        boolean result4 = VLangUtils.isMethodNameValid(methodName4);
        assertEquals(expResult4, result4);

        String methodName5 = "";
        boolean expResult5 = false;
        boolean result5 = VLangUtils.isMethodNameValid(methodName5);
        assertEquals(expResult5, result5);

    }

    /**
     * Test of isVariableNameValid method, of class VLangUtils.
     */
    @Test
    public void testIsVariableNameValid() {

        System.out.println("isVariableNameValid");
        String varName = "varOne";
        boolean expResult = true;
        boolean result = VLangUtils.isVariableNameValid(varName);
        assertEquals(expResult, result);

        String varName2 = "1var2";
        boolean expResult2 = false;
        boolean result2 = VLangUtils.isVariableNameValid(varName2);
        assertEquals(expResult2, result2);

        String varName3 = "variable^";
        boolean expResult3 = false;
        boolean result3 = VLangUtils.isVariableNameValid(varName3);
        assertEquals(expResult3, result3);

        String varName4 = "var";
        boolean expResult4 = false;
        boolean result4 = VLangUtils.isVariableNameValid(varName4);
        assertEquals(expResult4, result4);

        String varName5 = "";
        boolean expResult5 = false;
        boolean result5 = VLangUtils.isVariableNameValid(varName5);
        assertEquals(expResult5, result5);
    }

    /**
     * Test of isPackageNameValid method, of class VLangUtils.
     */
    @Test
    public void testIsPackageNameValid() {

        System.out.println("isPackageNameValid");
        String packageName = "a.test123.b";
        boolean expResult = true;
        boolean result = VLangUtils.isPackageNameValid(packageName);
        assertEquals(expResult, result);

        String packageName2 = "com.company";
        boolean expResult2 = true;
        boolean result2 = VLangUtils.isPackageNameValid(packageName2);
        assertEquals(expResult2, result2);

        String packageName3 = "a.if.b.c";
        boolean expResult3 = false;
        boolean result3 = VLangUtils.isPackageNameValid(packageName3);
        assertEquals(expResult3, result3);

        String packageName4 = "a.2name.b";
        boolean expResult4 = false;
        boolean result4 = VLangUtils.isPackageNameValid(packageName4);
        assertEquals(expResult4, result4);

        String packageName5 = "a..b";
        boolean expResult5 = false;
        boolean result5 = VLangUtils.isPackageNameValid(packageName5);
        assertEquals(expResult5, result5);

        String packageName6 = "";
        boolean expResult6 = true;
        boolean result6 = VLangUtils.isPackageNameValid(packageName6);
        assertEquals(expResult6, result6);
    }

    /**
     * Test of addEscapeCharsToBackSlash method, of class VLangUtils.
     */
    @Test
    public void testAddEscapeCharsToBackSlash() {
        System.out.println("addEscapeCharsToBackSlash");
        String code = "\\text\\";
        String expResult = "\\\\text\\\\";
        String result = VLangUtils.addEscapeCharsToBackSlash(code);
        assertEquals(expResult, result);
    }

    /**
     * Test of addEscapeCharsToCode method, of class VLangUtils.
     */
    @Test
    public void testAddEscapeCharsToCode() {
        System.out.println("addEscapeCharsToCode");
        String code = "\"text\"";
        String expResult = "\\\"text\\\"";
        String result = VLangUtils.addEscapeCharsToCode(code);
        assertEquals(expResult, result);
    }

    /**
     * Test of addEscapeNewLinesToCode method, of class VLangUtils.
     */
    @Test
    public void testAddEscapeNewLinesToCode() {
        System.out.println("addEscapeNewLinesToCode");
        String code = "line1\nline2";
        String expResult = "line1\\nline2";
        String result = VLangUtils.addEscapeNewLinesToCode(code);
        assertEquals(expResult, result);
    }

    /**
     * Test of addEscapesToCode method, of class VLangUtils.
     */
    @Test
    public void testAddEscapesToCode() {
        System.out.println("addEscapesToCode");
        String code = "line1\nline2\nprintln(\"a backslash: \\ \")";
        String expResult = "line1\\nline2\\nprintln(\\\"a backslash: \\\\ \\\")";
        String result = VLangUtils.addEscapesToCode(code);
        assertEquals(expResult, result);
    }

    /**
     * Test of isPrintableASCIICharacter method, of class VLangUtils.
     */
    @Test
    public void testIsPrintableASCIICharacter() {
        System.out.println("isPrintableASCIICharacter");
        char ch = 'a';
        boolean expResult = true;
        boolean result = VLangUtils.isPrintableASCIICharacter(ch);
        assertEquals(expResult, result);

        char ch2 = '\n';
        boolean expResult2 = false;
        boolean result2 = VLangUtils.isPrintableASCIICharacter(ch2);
        assertEquals(expResult2, result2);
    }

    /**
     * Test of isPrintableString method, of class VLangUtils.
     */
    @Test
    public void testIsPrintableString() {
        System.out.println("isPrintableString");
        String s = "text";
        boolean expResult = true;
        boolean result = VLangUtils.isPrintableString(s);
        assertEquals(expResult, result);

        String s2 = "line1\nline2";
        boolean expResult2 = false;
        boolean result2 = VLangUtils.isPrintableString(s2);
        assertEquals(expResult2, result2);
    }

    /**
     * Test of canonicalNameFromFullClassName method, of class VLangUtils.
     */
    @Test
    public void testCanonicalNameFromFullClassName() {
        System.out.println("canonicalNameFromFullClassName");
        String name = "a.b.c.ClassOne";
        String expResult = "ClassOne";
        String result = VLangUtils.shortNameFromFullClassName(name);
        assertEquals(expResult, result);

        String name2 = "ClassOne";
        String expResult2 = "ClassOne";
        String result2 = VLangUtils.shortNameFromFullClassName(name2);
        assertEquals(expResult2, result2);

        String name3 = "";
        String expResult3 = "";
        String result3 = VLangUtils.shortNameFromFullClassName(name3);
        assertEquals(expResult3, result3);
    }

    /**
     * Test of packageNameFromFullClassName method, of class VLangUtils.
     */
    @Test
    public void testPackageNameFromFullClassName() {
        System.out.println("packageNameFromFullClassName");
        String name = "com.software.ClassOne";
        String expResult = "com/software";
        String result = VLangUtils.packageNameFromFullClassName(name);
        assertEquals(expResult, result);

        String name2 = "ClassOne";
        String expResult2 = "";
        String result2 = VLangUtils.packageNameFromFullClassName(name2);
        assertEquals(expResult2, result2);
    }

    /**
     * Test of dotToSlash method, of class VLangUtils.
     */
    @Test
    public void testDotToSlash() {
        System.out.println("dotToSlash");
        String name = "com.software";
        String expResult = "com/software";
        String result = VLangUtils.dotToSlash(name);
        assertEquals(expResult, result);

        String name2 = "com";
        String expResult2 = "com";
        String result2 = VLangUtils.dotToSlash(name2);
        assertEquals(expResult2, result2);
    }

    /**
     * Test of slashToDot method, of class VLangUtils.
     */
    @Test
    public void testSlashToDot() {
        System.out.println("slashToDot");
        String name = "com/software";
        String expResult = "com.software";
        String result = VLangUtils.slashToDot(name);
        assertEquals(expResult, result);

        String name2 = "com";
        String expResult2 = "com";
        String result2 = VLangUtils.slashToDot(name2);
        assertEquals(expResult2, result2);
    }

    /**
     * Test of isCanonicalName method, of class VLangUtils.
     */
    @Test
    public void testIsCanonicalName() {
        System.out.println("isCanonicalName");
        String name = "com.software.ClassName";
        boolean expResult = false;
        boolean result = VLangUtils.isShortName(name);
        assertEquals(expResult, result);

        String name2 = "ClassName";
        boolean expResult2 = true;
        boolean result2 = VLangUtils.isShortName(name2);
        assertEquals(expResult2, result2);
    }

    /**
     * Test of removeCommentsAndStringsFromCode method, of class VLangUtils.
     */
    @Test
    public void testRemoveCommentsAndStringsFromCode() {
        System.out.println("removeCommentsAndStringsFromCode");
        String code = readSampleCode(
                "/eu/mihosoft/vrl/lang/source_with_comments_and_strings");

        String expResult = readSampleCode(
                "/eu/mihosoft/vrl/lang/source_without_comments_and_strings");

        System.out.println("Code 1:\n" + expResult);

        String result = VLangUtils.removeCommentsAndStringsFromCode(code);

        System.out.println("Code 2:\n" + result);

        assertEquals(expResult, result);
    }

    /**
     * Test of importsFromCode method, of class VLangUtils.
     */
    @Test
    public void testImportsFromCode() {
        System.out.println("importsFromCode");
        String code = readSampleCode(
                "/eu/mihosoft/vrl/lang/source_with_imports");
        List<String> expResult = new ArrayList<String>();
        expResult.add("a.b.c.ClassOne");
        expResult.add("a.b.c.*");
        expResult.add("a.b.c.d.ClassTwo");
        expResult.add("a.b.c.d.*");
        List<String> result = VLangUtils.importsFromCode(code);
        assertEquals(expResult, result);
    }

    /**
     * Test of isClassUsedInCode method, of class VLangUtils.
     */
    @Test
    public void testIsClassUsedInCode() {
        System.out.println("isClassUsedInCode");
        String code = readSampleCode(
                "/eu/mihosoft/vrl/lang/source_with_used_classes");

        List<String> classesInPackage = new ArrayList<String>();
        classesInPackage.add("ClassOne");
        classesInPackage.add("ClassTwo");

        // we check if the class from current package is used.
        String fullClassName = "test.a.b.c.ClassOne";
        // this is not the case as an explicit import of another class with the same
        // short name is defined in the code
        boolean expResult = false;
        boolean result = VLangUtils.isClassUsedInCode(code, fullClassName, classesInPackage);
        assertEquals(expResult, result);

        // we check if the class from explicit import is used.
        String fullClassName2 = "a.b.c.ClassOne";
        // this is the case (see explicit import)
        boolean expResult2 = true;
        boolean result2 = VLangUtils.isClassUsedInCode(code, fullClassName2, classesInPackage);
        assertEquals(expResult2, result2);

        // we check whether the class from implicit import is used.
        String fullClassName2b = "a.b.c.d.ClassOne";
        // this is not the case as a class with the same short name is
        // explicitly imported. thus, this class is used instead.
        boolean expResult2b = false;
        boolean result2b = VLangUtils.isClassUsedInCode(code, fullClassName2b, classesInPackage);
        assertEquals(expResult2b, result2b);

        // we check whether the specified class from explicit import is used.
        String fullClassName3 = "a.b.c.d.ClassTwo";
        // this is the case because explicit import is defined and this is stronger
        // than classes from same package or implicit import
        boolean expResult3 = true;
        boolean result3 = VLangUtils.isClassUsedInCode(code, fullClassName3, classesInPackage);
        assertEquals(expResult3, result3);

        boolean expResult4 = false;
        boolean result4 = true;

        try {
            // check whether specified class with invalid name is used.
            String fullClassName4 = "a.b.c.*";
            // invalid classes cannot be used and we assume valid code
            VLangUtils.isClassUsedInCode(code, fullClassName4, classesInPackage);
        } catch (IllegalArgumentException ex) {
            // thus, we expect an exception
            result4 = false;
        }
        assertEquals(expResult4, result4);

        // check whether specified class from default package is used in code
        String fullClassName5 = "test.a.b.c.ClassThree";
        // this is not the case as the class is not part of the current package
        boolean expResult5 = false;
        boolean result5 = VLangUtils.isClassUsedInCode(code, fullClassName5, classesInPackage);
        assertEquals(expResult5, result5);

        // check whether specified class is used in code
        String fullClassName6 = "a.b.c.ClassThree";
        // this is the case as implicit import of the class's package is defined
        boolean expResult6 = true;
        boolean result6 = VLangUtils.isClassUsedInCode(code, fullClassName6, classesInPackage);
        assertEquals(expResult6, result6);

        // check whether specified class is used
        String fullClassName7 = "ClassFour";
        // this is not the case as this class is not part of the code. the name
        // only occurs in a string.
        boolean expResult7 = false;
        boolean result7 = VLangUtils.isClassUsedInCode(code, fullClassName7, classesInPackage);
        assertEquals(expResult7, result7);

        // check whether specified class is used
        String fullClassName8 = "a.b.c.d.ClassFour";
        // this is not the case as this class is not part of the code. the name
        // only occurs in a string.
        boolean expResult8 = false;
        boolean result8 = VLangUtils.isClassUsedInCode(code, fullClassName8, classesInPackage);
        assertEquals(expResult8, result8);

        // check whether specified class is used
        String fullClassName9 = "ClassFive";
        boolean expResult9 = false;
        boolean result9 = VLangUtils.isClassUsedInCode(code, fullClassName9, classesInPackage);
        assertEquals(expResult9, result9);

        String fullClassName10 = "a.b.c.d.ClassFive";
        // this is not the case as this class is not part of the code. the name
        // only occurs in a comment.
        boolean expResult10 = false;
        boolean result10 = VLangUtils.isClassUsedInCode(code, fullClassName10, classesInPackage);
        assertEquals(expResult10, result10);

        // check whether specified class is used
        String fullClassName11 = "ClassSix";
        // this is not the case as this class is not part of the code. the name
        // only occurs in a comment.
        boolean expResult11 = false;
        boolean result11 = VLangUtils.isClassUsedInCode(code, fullClassName11, classesInPackage);
        assertEquals(expResult11, result11);

        // check whether specified class is used
        String fullClassName12 = "a.b.c.d.ClassSix";
        // this is not the case as this class is not part of the code. the name
        // only occurs in a comment.
        boolean expResult12 = false;
        boolean result12 = VLangUtils.isClassUsedInCode(code, fullClassName12, classesInPackage);
        assertEquals(expResult12, result12);

        // check whether specified class is used
        String fullClassName13 = "a.b.ClassSeven";
        // this is the case as the class is explicitly specified in the source
        // code
        boolean expResult13 = true;
        boolean result13 = VLangUtils.isClassUsedInCode(code, fullClassName13, classesInPackage);
        assertEquals(expResult13, result13);
    }

    /**
     * Test of getVRLTagAttribute method, of class VLangUtils.
     */
    @Test
    public void testGetVRLTagAttribute() {
        System.out.println("getVRLTagAttribute");
        String tag = "<vrl-tag-autogen name=myname id=abc>";
        String attribute = "id";
        String expResult = "abc";
        String result = VLangUtils.getVRLTagAttribute(tag, attribute);
        assertEquals(expResult, result);

        tag = "<vrl-tag-autogen name=myname id=abc>";
        attribute = "name";
        expResult = "myname";
        result = VLangUtils.getVRLTagAttribute(tag, attribute);
        assertEquals(expResult, result);

        tag = "<vrl-tag-autogen name  =myname id=  abc>";
        attribute = "name";
        expResult = "myname";
        result = VLangUtils.getVRLTagAttribute(tag, attribute);
        assertEquals(expResult, result);

        tag = "<vrl-tag-autogen name  =myname id=  abc>";
        attribute = "id";
        expResult = "abc";
        result = VLangUtils.getVRLTagAttribute(tag, attribute);
        assertEquals(expResult, result);
    }

    /**
     * Test of filterAutomaticImports method, of class VLangUtils.
     */
    @Test
    public void testFilterAutomaticImports() {
        System.out.println("filterAutomaticImports");
        String code = readSampleCode(
                "/eu/mihosoft/vrl/lang/source_with_autogen_code_and_imports");
        String expResult = readSampleCode(
                "/eu/mihosoft/vrl/lang/source_without_autogen_imports");
        String result = VLangUtils.filterAutoGenCode(code, "imports");
        assertEquals(expResult, result);

        expResult = readSampleCode(
                "/eu/mihosoft/vrl/lang/source_without_autogen_code");
        result = VLangUtils.filterAutoGenCode(code, "code");
        assertEquals(expResult, result);
    }
}
