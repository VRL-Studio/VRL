/* 
 * AddImportReplacementRule.java
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

import eu.mihosoft.vrl.lang.CompilerProvider;
import eu.mihosoft.vrl.lang.Patterns;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.lang.groovy.GroovyCompiler;
import eu.mihosoft.vrl.system.VRL;
import java.util.ArrayList;
import java.util.List;
import org.fife.ui.autocomplete.Completion;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class AddImportReplacementRule implements ReplacementRule {

    @Override
    public String replace(String text, Completion c) {

        CompletionType type = CompletionType.UNDEFINED;

        if (c instanceof VCompletion) {
            type = ((VCompletion) c).getType();
        }

        List<String> imports = VLangUtils.importsFromCode(text);

        // imports from groovy compiler
        GroovyCompiler gC = new GroovyCompiler();

        for (String importCmd : gC.getImports()) {
            String s = importCmd.replace("import", "").
                    replace(";", "").trim();
            imports.add(s);
        }

        // we assume this is a class name
        String replacement = c.getReplacementText();

        String fullClassNameFromReplacement = replacement;

        boolean classImported = false;

        // we try to get the class name from the relacement text
        for (String imp : imports) {
            if (replacement.startsWith(imp)) {
                fullClassNameFromReplacement = imp;
                classImported = true;
                break;
            }
        }

        boolean isFullClassName = !VLangUtils.isShortName(
                fullClassNameFromReplacement);

        // we do not add an import if short class name is used
        if (!isFullClassName) {
            return text;
        }


        String shortClassName =
                VLangUtils.shortNameFromFullClassName(
                fullClassNameFromReplacement);


        // if we are not a class completion and cannot extract class name
        // from string we do not replace text
        if (!classImported && type != CompletionType.CLASS) {
            return text;
        }

        //line-by-line replacement of full classnames by short classnames
        String[] lines = text.split("\n");
        text = "";

        for (int i = 0; i < lines.length; i++) {
            String l = lines[i];

            if (i < lines.length - 1) {
                text += l + "\n";
            } else {
                // replace currently edited line with short name
                text += l.replaceAll(
                        "\\b"
                        + VLangUtils.addEscapeCharsToRegexMetaCharacters(
                        fullClassNameFromReplacement) + "",
                        shortClassName);
            }
        }

        // add import if not already defined
        if (!imports.contains(fullClassNameFromReplacement)
                && !fullClassNameFromReplacement.startsWith("java.lang.")) {
            if (VLangUtils.packageDefinedInCode(text)) {
                text = text.replaceAll(
                        "package\\s+"
                        + Patterns.PACKAGE_NAME_STRING + "\\s*;",
                        "$0\n" + "import "
                        + fullClassNameFromReplacement + ";");
            } else {
                text = "import " + fullClassNameFromReplacement + ";\n" + text;
            }
        }


        return text;
    }
}
