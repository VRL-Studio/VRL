/* 
 * ClassLoaderCompletionProvider.java
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
package eu.mihosoft.vrl.lang.visual;

import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.lang.groovy.GroovyCompiler;
import eu.mihosoft.vrl.system.VClassLoader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.text.JTextComponent;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ClassLoaderCompletionProvider extends DefaultCompletionProvider {

    private VClassLoader loader;

    public ClassLoaderCompletionProvider(VClassLoader loader) {
        this.loader = loader;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List getCompletions(JTextComponent tc) {

        String fullText = tc.getText();

        List<Completion> result = new ArrayList<Completion>();

        String text = getAlreadyEnteredText(tc).replace("(", "");

        // classname auto-complete
        result.addAll(getClassNameCompletions(text));
        result.addAll(super.getCompletions(tc));

        //class member auto-complete
        Class<?> cls = getMatchingClass(getIdentifierName(text), fullText);

        if (cls != null) {
            result.addAll(getClassMemberCompletions(
                    cls,
                    getMemberName(text), null, fullText, true));
        } else {

            // search instance type
            Class<?> instanceType =
                    getInstanceClass(getIdentifierName(text), fullText);

            if (instanceType != null) {
                result.addAll(getClassMemberCompletions(
                        instanceType, getMemberName(text),
                        getIdentifierName(text), fullText, false));
            }
        }


//        // lsort in alphabetical order
//        Collections.sort(result, new Comparator<Completion>() {
//
//            @Override
//            public int compare(Completion o1, Completion o2) {
//                String name1 = o1.getReplacementText();
//                String name2 = o2.getReplacementText();
//                return name2.compareTo(name1);
//            }
//        });
//
//        // length is more important than alphabetical order
//        Collections.sort(result, new Comparator<Completion>() {
//
//            @Override
//            public int compare(Completion o1, Completion o2) {
//                Integer name1 = o1.getReplacementText().length();
//                Integer name2 = o2.getReplacementText().length();
//                return name1.compareTo(name2);
//            }
//        });

        return result;
    }

    private Class<?> getInstanceClass(String instance, String fullText) {

        String filteredFullText = VLangUtils.removeCommentsAndStringsFromCode(fullText);

        filteredFullText = filteredFullText.replace('(', ' ');
        filteredFullText = filteredFullText.replace(')', ' ');
        filteredFullText = filteredFullText.replace(',', ' ');
        filteredFullText = filteredFullText.replace(';', ' ');
        filteredFullText = filteredFullText.replace('=', ' ');

        String[] text = filteredFullText.split("(\\s+|\\n)");

        for (int i = 0; i < text.length; i++) {
            String s = text[i];
            // System.out.println(" --> s: " + s);
            if (s.equals(instance) && i > 0) {
                // System.out.println("  -> found possible type of " + s + ": " + text[i - 1].trim());
                return getMatchingClass(text[i - 1].trim(), fullText);

            }
        }

        return null;
    }

    private String getIdentifierName(String text) {
        int idx0 = text.lastIndexOf(".");
        String identifier = text;

        if (!identifier.isEmpty() && idx0 > 0) {
            identifier = text.substring(0, idx0);
        }

//        System.out.println("ID: " + identifier);

        return identifier;
    }

    private String getMemberName(String text) {
        int idx0 = text.lastIndexOf(".");
        String identifier = getIdentifierName(text);
        String memberText = "";

//        if (!identifier.isEmpty() && idx0 > 0) {
//            identifier = text.substring(0, idx0);
//        }

        if (idx0 < text.length() && idx0 > 0) {
            memberText = text.substring(idx0 + 1);

            // we allow starting ( for methods and ignore it for
            // name comparison
            memberText = memberText.replace("(", "");
        }

//        System.out.println("MEMBER: " + memberText);

        return memberText;
    }

    private Collection<Completion> getClassNameCompletions(String text) {
        ArrayList<Completion> result = new ArrayList<Completion>();

        for (String clsName : loader.getClasses().keySet()) {

            if (clsName.toLowerCase().contains(text.toLowerCase())) {

                result.add(
                        new VBasicCompletion(
                        this, clsName,
                        clsName, CompletionType.CLASS));
            }
        }

        for (String clsName : CompletionUtil.getCompletionList().getClassNames()) {

            if (clsName.toLowerCase().contains(text.toLowerCase())) {

                result.add(
                        new VBasicCompletion(
                        this, clsName,
                        clsName, CompletionType.CLASS));
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private Collection<Completion> getClassMemberCompletions(
            Class<?> cls, String member,
            String replacementIdentifier,
            String fullText,
            boolean onlyStatic) {
        ArrayList<Completion> result = new ArrayList<Completion>();

        List<String> imports = VLangUtils.importsFromCode(fullText);


//        System.out.println("ID: " + identifier + ", member: " + member);
//        System.out.println("CLS: " + cls.getName());

        // methods
        for (Method m : cls.getMethods()) {

            // filter methods (never used)
            if (m.getName().equals("__$swapInit")
                    || m.getName().contains("super$1$")) {
                continue;
            }

            boolean isStatic = Modifier.isStatic(m.getModifiers());
            boolean isPublic = Modifier.isPublic(m.getModifiers());

            boolean acceptModifiers = (isStatic || !onlyStatic) && isPublic;

//            System.out.println("m: " + m.getName());

            if (acceptModifiers
                    && m.getName().toLowerCase().startsWith(
                    member.toLowerCase())
                    && !VAutoCompletion.isHideFromAutoCompleteEnabled(m)) {

                String replacement = "";

                if (replacementIdentifier == null
                        || replacementIdentifier.isEmpty()) {
                    replacement = cls.getName() + "." + m.getName() + "(";
                } else {
                    replacement =
                            replacementIdentifier + "." + m.getName() + "(";
                }

                String returnTypeName = m.getReturnType().getName().replaceFirst(
                        "java\\.lang\\.", "");

                String shortReturnTypeName =
                        VLangUtils.shortNameFromFullClassName(
                        returnTypeName);

                Class<?> types[] = m.getParameterTypes();
                String typeString = "";

                for (int i = 0; i < types.length; i++) {
                    if (i > 0) {
                        typeString += ", ";
                    }

                    String typeName = types[i].getName().replaceFirst(
                            "java\\.lang\\.", "");

                    String shortName =
                            VLangUtils.shortNameFromFullClassName(
                            typeName);

                    typeName = shortName;

                    typeString += typeName + " p" + i;
                }

                String suffix = "";

                if (typeString.isEmpty()) {
                    suffix = ")";
                }

                result.add(
                        new VBasicCompletion(
                        this, replacement + suffix,
                        replacement + "<b>" + typeString + "</b>) : <b>"
                        + shortReturnTypeName + "</b>",
                        CompletionType.METHOD));
            }

        } // end for m

        // member variables
        for (Field f : cls.getFields()) {

            // filter methods (never used)
            if (f.getName().startsWith("__")) {
                continue;
            }

            boolean isStatic = Modifier.isStatic(f.getModifiers());
            boolean isPublic = Modifier.isPublic(f.getModifiers());

            boolean acceptModifiers = (isStatic || !onlyStatic) && isPublic;

            if (acceptModifiers && f.getName().toLowerCase().startsWith(
                    member.toLowerCase())) {

                String replacement = "";
                String replacementDesc = "";

                if (replacementIdentifier == null
                        || replacementIdentifier.isEmpty()) {
                    replacement = cls.getName() + "." + f.getName();
                    replacementDesc = cls.getName() + ".<b>" + f.getName() + "</b>";
                } else {
                    replacement =
                            replacementIdentifier + "." + f.getName();
                    replacementDesc =
                            replacementIdentifier + ".<b>" + f.getName() + "</b>";
                }

                result.add(
                        new VBasicCompletion(
                        this, replacement, replacementDesc,
                        CompletionType.FIELD));
            }
        }

        return result;
    }

    /**
     * Returns class by name. If the classname is specified as short classname
     * this method checks for explicit imports and replaces the short class name
     * with the full classname.
     *
     * @param name class name (short or full name)
     * @param fullText complete code that is currently edited
     * @return class by name
     */
    private Class<?> getMatchingClass(String name, String fullText) {

        String packageName = VLangUtils.packageNameFromCode(fullText);
        packageName = VLangUtils.slashToDot(packageName);

        List<String> imports = VLangUtils.importsFromCode(fullText);

        // add java.lang as implicit import
        imports.add("java.lang.*");

        GroovyCompiler compiler = new GroovyCompiler();

        // add implicit imports from compiler
        for (String imp : compiler.getImports()) {
            String importString = imp.replace("import", "").replace(";", "");
            imports.add(importString.trim());
        }


        // check whether explicit name specified
        Class<?> cls = loadClassByName(name);

        if (cls != null) {
            return cls;
        }

        // if name is no full class name search for the class via explicit
        // imports
        if (VLangUtils.isShortName(name)) {
            for (String imp : imports) {
                if (imp.endsWith("." + name)) {
                    return loadClassByName(imp);
                }
            }
        }

        // current package support
        cls = loadClassByName(packageName + "." + name);
        if (cls != null) {
            return cls;
        }


        // check whether implicit import is possible
        for (String imp : imports) {

            if (imp.endsWith("*")) {

                String fullName =
                        imp.substring(0, imp.length() - 1) + name;

                Class<?> result =
                        loadClassByName(fullName.trim());

                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    private Class<?> loadClassByName(String fullName) {

        Class<?> result = null;
        try {
            result = loader.loadClass(fullName);
        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(ClassLoaderCompletionProvider.class.getName()).
//                    log(Level.SEVERE, null, ex);
        }

        return result;
    }

    /**
     * Returns whether the specified character is valid in an auto-completion.
     * The default implementation is equivalent to
     *
     * <code>Character.isLetterOrDigit(ch) || ch=='_' || ch =='.' || ch =='.'
     * </code> ".
     *
     * Subclasses can override this method to change which characters are
     * matched.
     *
     * @param ch The character.
     * @return Whether the character is valid.
     */
    @Override
    protected boolean isValidChar(char ch) {
        return Character.isLetterOrDigit(ch)
                || ch == '_' || ch == '.' || ch == '(';
    }
}
