/* 
 * CodeAnalyzerImpl.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2007–2017 by Michael Hoffer
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
 * Third, add an additional notice, stating that you modified VRL. A suitable
 * notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Please cite the publication(s) listed below.
 *
 * Publications:
 *
 * M. Hoffer, C. Poliwoda, & G. Wittum. (2013). Visual reflection library:
 * a framework for declarative GUI programming on the Java platform.
 * Computing and Visualization in Science, 2013, 16(4),
 * 181–192. http://doi.org/10.1007/s00791-014-0230-y
 */

package eu.mihosoft.g4j.lang;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class CodeAnalyzerImpl implements CodeAnalyzer {
    private static final String id = "ClassExtractor";

    public String getID() {
        return id;
    }

    public Collection<ClassEntry> analyze(CodeEntry code) {

        int depth = 0;

        Collection<ClassEntry> result = new ArrayList<ClassEntry>();

        String text = code.getCode();
        String header;
        
        Matcher classHeaderMatcher = Patterns.TEMPLATE_CLS_HEADER.matcher(text);

        if (classHeaderMatcher.find()) {

            int posBegin = classHeaderMatcher.start();

            System.out.println(">> class found!");

            int posEnd = classHeaderMatcher.end();

            header = text.substring(posBegin, posEnd);

            text = text.substring(posEnd);

            for (int i = 0; i < text.length();i++) {
                char c = text.charAt(i);

                boolean braceFound = false;

                if (c == '{') {
                    depth++;
                    braceFound = true;
                } else if (c == '}') {
                    depth--;
                    braceFound = true;
                }

                if (depth == 0 && braceFound) {
                    posEnd = i+1;
                    break;
                }
            }

            if (depth>0) {
                System.err.println(">> too many \"{\"!");
            } else if (depth<0) {
                System.err.println(">> too many \"}\"!");
            }

            text = header+text.substring(0,posEnd);

            result.add(new ClassEntryImpl("class",text));
        }

        return result;
    }

}

class ClassEntryImpl implements ClassEntry {
    private String code;
    private String name;
    private Collection<MethodEntry> methods = new ArrayList<MethodEntry>();
    private Collection<ClassEntry> classes = new ArrayList<ClassEntry>();
    private Collection<String> templateArguments = new ArrayList<String>();

    public ClassEntryImpl(String name, String code ) {
        this.code = code;
        this.name = name;
    }

    

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Collection<MethodEntry> getMethods() {
        return methods;
    }

    public Collection<String> getTemplateArguments() {
        return templateArguments;
    }

    public Collection<ClassEntry> getClasses() {
       return classes;
    }

    /**
     * @param methods the methods to set
     */
    public void setMethods(Collection<MethodEntry> methods) {
        this.methods = methods;
    }

    /**
     * @param classes the classes to set
     */
    public void setClasses(Collection<ClassEntry> classes) {
        this.classes = classes;
    }

    /**
     * @param templateArguments the templateArguments to set
     */
    public void setTemplateArguments(Collection<String> templateArguments) {
        this.templateArguments = templateArguments;
    }
    
}
