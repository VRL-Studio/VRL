/* 
 * LangUtils.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2012 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2012 by Michael Hoffer
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
 * First, the following text must be displayed on the Canvas:
 * "based on VRL source code". In this case the VRL canvas icon must be removed.
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
 * Computing and Visualization in Science, 2011, in press.
 */

package eu.mihosoft.g4j.lang;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Language utils provides several code related methods to analyze and verify
 * source code.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class LangUtils {

    // no instanciation allowed
    private LangUtils() {
        throw new AssertionError(); // not in this class either!
    }

    
    /**
     * Returns class name of the specified class file.
     * @param parent parent directory (not part of the classpath)
     * @param f class file, including full classpath
     * @return class name of the specified class file
     */
    public static String getClassNameFromFile(File parent, File f) {
        // remove .class ending
        String className = f.getAbsolutePath();

        if (className.endsWith(".class")) {
            className = className.substring(
                    0, f.getAbsolutePath().lastIndexOf(".class"));
        } else {
            throw new IllegalArgumentException(
                    "According to the file ending the specified file is no"
                    + "supported class file! File: " + f);
        }

        // remove absolute path + the / or \ after the path and ensure /
        // is used for classpath on windows
        className = className.substring(
               parent.getAbsolutePath().length() + 1,
                className.length()).replace('\\', '/');

        return className;
    }

    /**
     * Returns the class name of the first class defined in the given source
     * code.
     *
     * @param code code to analyze
     * @return class name of the first class defined in the given source code or
     * an empty string if no class has been defined
     */
    public static String classNameFromCode(String code) {
        String result = "";

        String[] lines = code.split("\\n");

        Matcher m = Patterns.CLASS_DEFINITION.matcher(code);

        for (String l : lines) {

            l = l.trim();

            if (m.find()) {

                l = m.group();

                result = l.replaceFirst(
                        Patterns.CLASS_DEFINITION_WITHOUT_IDENTIFIER_STRING,
                        "").split(" ")[0];
                break;
            }
        }

        return result;
    }
    
    /**
     * Returns the class name of the class defined in the given template class
     * header
     *
     * @param clsHeader string to analyze
     * @return class name of the class defined in the given template class
     * header
     */
    public static String classNameFromTemplateClsHeader(String clsHeader) {
        String result = "";

        String[] lines = clsHeader.split("\\n");

        Matcher m = Patterns.CLASS_DEFINITION.matcher(clsHeader);

        for (String l : lines) {

            l = l.trim();

            if (m.find()) {

                l = m.group();

                result = l.replaceFirst(
                        Patterns.CLASS_DEFINITION_WITHOUT_IDENTIFIER_STRING,
                        "").split(" ")[0];
                break;
            }
        }

        return result;
    }

    /**
     * Indicates whether in the specified code a class is defined.
     *
     * @param code
     * @return
     * <code>true</code> if the class is defined;
     * <code>false</code> otherwise
     */
    public static boolean classDefinedInCode(String code) {
        code = removeCommentsAndStringsFromCode(code);
        return !classNameFromCode(code).equals("");
    }

    /**
     * Indicates whether in the specified code a package name is defined.
     *
     * @param code
     * @return
     * <code>true</code> if the package name is defined;
     * <code>false</code> otherwise
     */
    public static boolean packageDefinedInCode(String code) {
        return !packageNameFromCode(code).isEmpty();
    }

    /**
     * Get package name defined in the given source code.
     *
     * @param code code to analyze
     * @return package name defined in the given source code or empty string if
     * no package could be found
     */
    public static String packageNameFromCode(String code) {
        String result = "";

        code = removeCommentsAndStringsFromCode(code);

        String[] lines = code.split("\\n");

        // match example: ^package eu.mihosoft.vrl;$
        Pattern p1 =
                Pattern.compile(
                "^\\s*package\\s+" + Patterns.PACKAGE_NAME_STRING + ";",
                Pattern.DOTALL);

        for (String l : lines) {

            l = l.trim();

            Matcher m1 = p1.matcher(l);

            if (m1.find()) {

                l = m1.group();

                result =
                        l.replaceFirst("^\\s*package\\s+", "").
                        split(" ")[0].replace(";", "");
                break;
            }
        }

        return result;
    }

    /**
     * Returns full classname from code, i.e., classname with package name.
     *
     * @param code code to analyze
     * @return full classname from code, i.e., classname with package name or //
     * * an empty string if no class has been defined
     */
    public static String fullClassNameFromCode(String code) {

        code = removeCommentsAndStringsFromCode(code);

        String packageName = packageNameFromCode(code);

        String result = "";

        String className = classNameFromCode(code);

        if (className.equals("")) {
            return result;
        }

        if (packageName.isEmpty()) {
            result = className;
        } else {
            result = packageNameFromCode(code) + "." + classNameFromCode(code);
        }

        return result;
    }

    /**
     * Indicates whether the specified class name is valid. Currently only
     * unqualified names are supported, i.e. names without package.
     *
     * @param className class name to check
     * @return
     * <code>true</code> if the class name is valid;
     * <code>false</code> otherwise
     */
    public static boolean isClassNameValid(String className) {

        if (className == null) {
            className = "";
        }

        return isIdentifierValid(className);
    }

    /**
     * Indicates whether the specified method name is valid. Currently only
     * unqualified names are supported.
     *
     * @param methodName method name to check
     * @return
     * <code>true</code> if the variable name is valid;
     * <code>false</code> otherwise
     */
    public static boolean isMethodNameValid(String methodName) {


        if (methodName == null) {
            methodName = "";
        }

        return isIdentifierValid(methodName);
    }

    /**
     * Indicates whether the specified variable name is valid. Currently only
     * unqualified names are supported.
     *
     * @param varName variable name to check
     * @return
     * <code>true</code> if the variable name is valid;
     * <code>false</code> otherwise
     */
    public static boolean isVariableNameValid(String varName) {

        if (varName == null) {
            varName = "";
        }

        return isIdentifierValid(varName);
    }

    /**
     * Indicates whether the specified identifier name is valid.
     *
     * @param varName identifier name to check
     * @return
     * <code>true</code> if the identifier name is valid;
     * <code>false</code> otherwise
     */
    private static boolean isIdentifierValid(String varName) {

        if (varName == null) {
            varName = "";
        }

        // same as class name (currently, this may change soon)
        Pattern p = Pattern.compile(getIdentifierRegex());

        boolean result = p.matcher(varName).matches();

        if (result) {
            // now check whether the identifier is a reserved keyword
            result = !Keywords.isKeyword(varName);
        }

        return result;
    }

    /**
     * Indicates whether the specified name is a valid component class
     * identifier.
     *
     * @param varName identifier name to check
     * @return
     * <code>true</code> if the identifier name is valid;
     * <code>false</code> otherwise
     */
    public static boolean isComponentClassNameValid(String varName) {

        if (varName == null) {
            varName = "";
        }

        // same as class name (currently, this may change soon)
        Pattern p = Pattern.compile(getComponentClassNameRegex());

        boolean result = p.matcher(varName).matches();

        if (result) {
            // now check whether the identifier is a reserved keyword
            result = !Keywords.isKeyword(varName);
        }

        return result;
    }

    /**
     * Returns the regular expression that is used to match a valid identifier.
     *
     * @return the regular expression that is used to match a valid identifier
     */
    private static String getIdentifierRegex() {
        return "[a-zA-Z\\p{L}$_][a-zA-Z\\p{L}$_0-9]*";
    }

    /**
     * Returns the regular expression that is used to match a valid project
     * class name.
     *
     * @return the regular expression that is used to match a valid project
     * class name
     */
    private static String getComponentClassNameRegex() {
        return "[A-Z\\p{Lu}][a-zA-Z\\p{L}_0-9]*";
    }

    /**
     * Indicates whether the specified package name is valid. Package names with
     * slashes are not accepted. Only packages using dot as seperator are
     * supported.
     *
     * @param varName package name to check
     * @return
     * <code>true</code> if the package name is valid;
     * <code>false</code> otherwise
     */
    public static boolean isPackageNameValid(String packageName) {

        // default package name
        if ("".equals(packageName)) {
            return true;
        }

        if (packageName == null) {
            packageName = "";
        }

        // check whether language keyword is used which is not allowed
        String[] identifiers = packageName.split("\\.");

        for (String id : identifiers) {
            if (Keywords.isKeyword(id)) {
                return false;
            }
        }

        // same as class name (currently, this may change soon)
//        Pattern p = Pattern.compile(
//                "(" + getIdentifierRegex() + ")"
//                + "(\\." + getIdentifierRegex() + ")*");

        return Patterns.PACKAGE_NAME.matcher(packageName).matches();
    }

    /**
     * Adds escape characters to all occurences of
     * <code>"</code>,
     * <code>\</code> and
     * <code>\n</code>.
     *
     * @param code code
     * @return code with escape characters
     */
    public static String addEscapesToCode(String code) {
        code = addEscapeCharsToBackSlash(code);
        code = addEscapeNewLinesToCode(code);
        return addEscapeCharsToCode(code);
    }

    /**
     * Adds escape characters to all occurences of
     * <code>\</code>.
     *
     * @param code code
     * @return code with escape characters
     */
    public static String addEscapeCharsToBackSlash(String code) {
        if (code == null) {
            code = "";
        }

        // windows backslashes
        return code.replace("\\", "\\\\");
    }

    /**
     * Adds escape charaters to all occurences of the following characters:
     * <code>^[.${*(\+)|?&#60;>&#62;/code>
     */
    public static String addEscapeCharsToRegexMetaCharacters(String text) {
        return text.replace("\\", "\\\\").
                replace("^", "\\^").
                replace("[", "\\[").
                replace(".", "\\.").
                replace("$", "\\$").
                replace("{", "\\{").
                replace("*", "\\*").
                replace("(", "\\(").
                replace("+", "\\+").
                replace(")", "\\)").
                replace("|", "\\|").
                replace("?", "\\?").
                replace("<", "\\<").replace(">", "\\>");
    }

    /**
     * Adds escape characters to all occurences of
     * <code>"</code>.
     *
     * @param code code
     * @return code with escape characters
     */
    public static String addEscapeCharsToCode(String code) {
        if (code == null) {
            code = "";
        }

        return code.replace("\"", "\\\"");
    }

    /**
     * Adds escape characters to all occurences of
     * <code>\n</code>.
     *
     * @param code code
     * @return code with escape characters
     */
    public static String addEscapeNewLinesToCode(String code) {
        if (code == null) {
            code = "";
        }

        return code.replace("\n", "\\n");
    }

    /**
     * Determines whether the specified character is a printable ASCII
     * character.
     *
     * @param ch character to check
     * @return
     * <code>true</code> if the character is printable;
     * <code>false</code> otherwise
     */
    public static boolean isPrintableASCIICharacter(char ch) {
        return ch >= 32 && ch < 127;
    }

    /**
     * Determines whether the specified String only contains printable ASCII
     * characters.
     *
     * @param s string to check
     * @return
     * <code>true</code> if the string is printable;
     * <code>false</code> otherwise
     */
    public static boolean isPrintableString(String s) {

        for (int i = 0; i < s.length(); i++) {
            if (!isPrintableASCIICharacter(s.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the short class name from the specified full class name, i.e.,
     * class name without package/classpath. Class names specified using dot
     * separation and slash separation are supported. <p>Example:</p>
     * <pre>
     * Short name: ClassOne
     * Full name : a.b.c.ClassOne
     * </pre>
     *
     * @param name full class name
     * @return the short class name from the specified full class name
     */
    public static String shortNameFromFullClassName(String name) {
        name = name.replace('.', '/');

        String[] path = name.split("/");

        if (path.length > 0) {
            return path[path.length - 1];
        }

        return name;
    }

    /**
     * Returns the package name from the specified full class name using slash
     * notation.
     *
     * @param name full class name
     * @return the package name from the specified full class name using slash
     * notation
     */
    public static String packageNameFromFullClassName(String name) {
        name = name.replace('.', '/');

        String[] path = name.split("/");

        String result = "";

        if (path.length > 0) {
            for (int i = 0; i < path.length - 1; i++) {
                if (i > 0) {
                    result += "/";
                }
                result += path[i];
            }
        }

        return result;
    }

    /**
     * Converts a package and/or class name.
     *
     * @param name
     * @return
     */
    public static String dotToSlash(String name) {
        return name.replace('.', '/');
    }

    /**
     * Converts a package and/or class name.
     *
     * @param name
     * @return
     */
    public static String slashToDot(String name) {
        return name.replace('/', '.');
    }

    /**
     * Indicates whether the specified name is a short class name without
     * classpath.
     *
     * @param name class name to check
     * @return
     * <code>true</code> if the specified class name is a short class name;
     * <code>false</code> otherwise
     */
    public static boolean isShortName(String name) {
        return dotToSlash(name).equals(shortNameFromFullClassName(name));
    }

    /**
     * Removes comments, strings and chars from code, i.e.
     *
     * <pre>
     * 1: // comment 1
     * 2: /* comment 2
     * 3:    still in comment2 *&#47;
     * 4:
     * 5: String s = "Classname";
     * 6:
     * 7: char c = 'A';
     * 8:
     * </pre>
     *
     * becomes
     *
     * <pre>
     * 1:
     * 2:
     * 3:
     * 4:
     * 5: String s = ;
     * 6:
     * 7: char c = ;
     * 8:
     * </pre>
     *
     * This is usefull for methods that search for class dependencies in code
     * where strings inside comments or string literals must not be matched.
     */
    public static String removeCommentsAndStringsFromCode(String code) {

        // filter comments (classname could occur in comment)
        code = new FilterComments().process(code);

        // filter strings (classname could occur in strings)
        code = new FilterStrings().process(code);

        // filter chars (classname could occur in chars for one-char names
        // like A,B,C etc.)
        code = new FilterChars().process(code);
        
        
        return code;
    }

    /**
     * Returns all import definitions from code.
     */
    public static List<String> importsFromCode(String code) {

        code = removeCommentsAndStringsFromCode(code);

        List<String> result = new ArrayList<String>();

        Pattern p = Patterns.IMPORT_DEFINITION;

        Matcher m = p.matcher(code);

        while (m.find()) {
            String match =
                    m.group().replace("import", "").replace(";", "").trim();
            result.add(match);
        }

        return result;
    }

    /**
     * <p> Checks whether the specified class is used by the given source code.
     * </p> <p><b>Note:</b> this method assumes the specified class exists. For
     * non existent classes it might return
     * <code>true</code>! Only for classes from the code's package it is safe to
     * specify non existent classes as they are explicitly specified.</p>
     *
     * @param code code
     * @param fullClassName full class name, e.g.,
     * <code>com.software.ClassOne</code>
     * @param classesInPackage collection containing short class names, i.e.,
     * names without package of all classes of the package defined in the given
     * code
     * @return
     * <code>true</code> if the specified class is used by the given code;
     * <code>false</code> otherwise
     */
    public static boolean isClassUsedInCode(String code, String fullClassName,
            Collection<String> classesInPackage) {

        // *****************
        // explaining terms:
        // *****************
        //
        // a) short name and full name
        //
        // full name:  a.b.c.ClassOne
        // short name: ClassOne
        //
        // b) explicit import and implicit import
        //
        // explicit import: import a.b.c.ClassOne;
        // implicit import: import a.b.c.*;
        //
        // *****************************************************************
        // import priority (which class is used if short name is specified):
        // *****************************************************************
        //
        // 1) explicit import is strongest, short name always refers to this
        //    even if the current package contains a class with the same 
        //    short name or if implicit imports contain a class with same
        //    short name
        // 2) if not explicitly specified short name refers to class in the
        //    current package even if the implicit imports contains such
        //    a class
        // 3) if neither 1) or 2) specifies the class, implicit imports are
        //    checked


        // check whether classesInPackage only contain short names
        for (String clsName : classesInPackage) {
            if (!isShortName(clsName)) {
                throw new IllegalArgumentException(
                        "Only short class names without package definition are"
                        + " supported.");
            }
        }

        String packageNameOfCode = slashToDot(packageNameFromCode(code));
        String packageNameFromClassName = slashToDot(
                packageNameFromFullClassName(fullClassName));
        String shortClassName =
                shortNameFromFullClassName(fullClassName);
        List<String> imports = importsFromCode(code);

        // if class name or package name is invalid we throw an exception
        if (!isClassNameValid(shortClassName)) {
            throw new IllegalArgumentException(
                    "Class name is invalid! Name: " + shortClassName);
        } else if (!isPackageNameValid(packageNameFromClassName)) {

            throw new IllegalArgumentException(
                    "Package name are invalid! cls: " + packageNameFromClassName);
        }

        // filter comments, strings and chars (classname could occur in comment)
        code = removeCommentsAndStringsFromCode(code);

        // tokenize
        String[] lines = code.split("\n");
        List<String> tokens = new ArrayList<String>();

        for (int i = 0; i < lines.length; i++) {
            String l = lines[i];
            // replace all non alphanumeric chars and chars that are not
            // equal to " or ' or . with space character
            l = l.replaceAll("[^A-Za-z0-9_\"\'\\. ]", " ");

            tokens.addAll(Arrays.asList(l.split("\\s")));
        }

        boolean shortNameUsedInCode = false;

        // ***********************************************
        //  1) check for full classname (explicit import)
        // ***********************************************
        for (String t : tokens) {

            // remove leading and trailing spaces
            t = t.trim();

            if (t.equals(fullClassName)) {
                return true;
            } else if (t.equals(shortClassName)) {
                shortNameUsedInCode = true;
            }
        }

        // now first check whether explicit imports contain
        // a class with same short name.
        // do we need to check 2) and 3) ?
        for (String importString : imports) {
            // explicit import
            if (!importString.endsWith("*")
                    && !importString.equals(packageNameFromClassName)) {
                String sn = shortNameFromFullClassName(importString);

                // if short names are equal we return false
                // as another class is used in the code
                if (sn.equals(shortClassName)) {
                    // no, we do not need 2 and 3 because we could proof they
                    // will fail
                    return false;
                }
            }
        }

        // yes, we do need 2 and/or 3 because we could not proof they will fail

        // ****************************************************************************
        // 2a) check for short name (if class is in same package as the specified code)
        // ****************************************************************************
        if (packageNameOfCode.equals(packageNameFromClassName)) {
            // search short name and return true if found
            if (classesInPackage.contains(shortClassName) && shortNameUsedInCode) {
                return true;
            }
        } else {
            // *****************************************************************
            // 2b) check whether short name class is part of the current package
            // *****************************************************************
            // if classname is used in package this class is used if referenced
            // as short name. thus, we return false
            if (classesInPackage.contains(shortClassName)) {
                return false;
            }
        }

        // *********************************************
        // 3) check for short name using implicit import
        // *********************************************
        for (String importString : imports) {
            // implicit import
            if (importString.endsWith("*")) {
                String packageNameFromImport = importString.replace(".*", "");
                // imports class and short name because we know that it is imported
                if (packageNameFromClassName.equals(packageNameFromImport) && shortNameUsedInCode) {
//                    // search short name because we know that it is imported
//                    for (String t : tokens) {
//                        if (t.equals(shortClassName)) {
//                            return true;
//                        }
//                    }

                    return true;
                }
            }
        }

        return false;
    }
}
