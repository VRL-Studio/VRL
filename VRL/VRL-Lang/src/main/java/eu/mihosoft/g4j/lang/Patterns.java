/* 
 * Patterns.java
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

import java.util.regex.Pattern;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Patterns {

    /**
     * Regular expression for a valid identifier (variable, class or method
     * names).
     */
    public static final String IDENTIFIER_STRING = "[a-zA-Z$_][a-zA-Z$_0-9]*";
    /**
     * Regular expression for a list of valid identifiers.
     */
    public static final String IDENTIFIER_LIST_STRING =
            "(" + IDENTIFIER_STRING + "\\s*,\\s*)*"
            + IDENTIFIER_STRING;
    /**
     * Regular expression for a valid template argument, e.g,
     * <code>&lt;&lt;T,V&gt;&gt;</code>.
     */
    public static final String TEMPLATE_ARGUMENT_STRING =
            "<<\\s*" + IDENTIFIER_LIST_STRING + "\\s*>>";
    /**
     * <p>Regular expression to match block-comments.</p> <p><b>Note:</b> does
     * also match block-comments inside strings! Thus, to work correctly strings
     * have to be removed before using this expression.</p>
     */
    public static final String BLOCK_COMMENT_STRING =
            "/\\*(?:.|[\\n\\r])*?\\*/";
    /**
     * Pattern to match template class headers. Example:
     * <code>
     * public class Sample01 &lt;&lt;Type, Type2&gt;&gt; extends Base01
     * </code>
     */
    public static final Pattern TEMPLATE_CLS_HEADER = Pattern.compile(
            "(\\s+|^|(\\s+|^)public\\s+|(\\s+|^)protected\\s+|(\\s+|^)"
            + "private\\s+)(static\\s+|abstract\\s+|final\\s+|)class\\s+"
            + IDENTIFIER_STRING
            + "\\s*" + TEMPLATE_ARGUMENT_STRING
            + "(\\s*extends\\s+" + IDENTIFIER_STRING + ")*"
            + "(\\s+implements\\s+" + IDENTIFIER_LIST_STRING + ")*",
            Pattern.MULTILINE);
    /**
     * Pattern to match template class. Example:
     * <code>
     * Sample01 &lt;&lt;Type, Type2&gt;&gt;
     * </code>
     */
    public static final Pattern TEMPLATE_CLS = Pattern.compile(
            "(class\\s+"+IDENTIFIER_STRING +"|" +IDENTIFIER_STRING
            + ")\\s*" + TEMPLATE_ARGUMENT_STRING,
            Pattern.MULTILINE);
    /**
     * Pattern to match template arguments, e.g.,
     * <code>&lt;T,V&gt;</code>.
     */
    public static final Pattern TEMPLATE_ARGUMENT = Pattern.compile(TEMPLATE_ARGUMENT_STRING);
    /**
     * Pattern to match an identifier.
     */
    public static final Pattern IDENTIFIER = Pattern.compile(IDENTIFIER_STRING);
    /**
     * Pattern to match an identifier list.
     */
    public static final Pattern IDENTIFIER_LIST = Pattern.compile(IDENTIFIER_LIST_STRING);
    /**
     * <p>Regular expression to match package names.</p> (default package, i.e.,
     * empty string is NOT supported)
     */
    public static final String PACKAGE_NAME_STRING =
            "(" + IDENTIFIER_STRING + ")" + "(\\." + IDENTIFIER_STRING + ")*";
    /**
     * Pattern to match package name (default package, i.e., empty string is NOT
     * supported)
     */
    public static final Pattern PACKAGE_NAME =
            Pattern.compile(PACKAGE_NAME_STRING,
            Pattern.DOTALL);
    /**
     * Regular expression to match import definition.
     */
//    // \\b is stands for word boundary
    public static final String IMPORT_DEFINITION_STRING =
            "\\bimport\\b\\s+" + Patterns.PACKAGE_NAME_STRING
            + "\\s*(;|\\.\\*\\s*;)";
    /**
     * Pattern to match import definition.
     */
    public static final Pattern IMPORT_DEFINITION =
            Pattern.compile(IMPORT_DEFINITION_STRING,
            Pattern.DOTALL);
    /**
     * Regular expression to match class definition (without class name).
     */
    public static final String CLASS_DEFINITION_WITHOUT_IDENTIFIER_STRING =
            "(\\s+|^|(\\s+|^)public\\s+|(\\s+|^)protected\\s+|(\\s+|^)"
            + "private\\s+)(static\\s+|abstract\\s+|final\\s+|)class\\s+";
    /**
     * Regular expression to match class definition.
     */
    public static final String CLASS_DEFINITION_STRING =
            CLASS_DEFINITION_WITHOUT_IDENTIFIER_STRING
            + IDENTIFIER_STRING;
    /**
     * Pattern to match class definition. Example:
     * <code>
     * public class Sample01
     * </code>
     */
    public static final Pattern CLASS_DEFINITION =
            Pattern.compile(CLASS_DEFINITION_STRING);
}
