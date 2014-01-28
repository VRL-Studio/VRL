/* 
 * Keywords.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * mIndicates whether the specified word is a reserved language keyword
   (currently only java and groovy are supported).
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Keywords {

    private static Set<String> keywords = new HashSet<String>();

    static {
        
        // Java Keywords
        String keywordString = readKeyWords(
                "/eu/mihosoft/vrl/lang/keywords_java6");
        keywords.addAll(Arrays.asList(keywordString.split("\n")));
        
        // Groovy Keywords
        keywordString = readKeyWords(
                "/eu/mihosoft/vrl/lang/keywords_groovy");
        
        keywords.addAll(Arrays.asList(keywordString.split("\n")));
    }

    /**
     * Indicates whether the specified word is a reserved language keyword
     * (currently only java and groovy are supported).
     * @param word word to check
     * @return <code>true</code> if the specified word is a reserved
     *         language keyword; <code>false</code> otherwise
     */
    public static boolean isKeyword(String word) {

        for (String s : keywords) {
            if (s.equals(word)) {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Returns all reserved language keywords
     * (currently only java and groovy are supported).
     * @return all reserved language keywords
     */
    public static Iterable<String> getKeywords() {
        return keywords;
    }

    private static String readKeyWords(String codeName) {
        // load Sample Code
        InputStream iStream = Keywords.class.getResourceAsStream(
                codeName);

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(iStream));

        String code = "";

        try {
            while (reader.ready()) {
                String line = reader.readLine();
                // lines starting with # and empty lines are ignored
                if (line.trim().startsWith("#") || line.trim().isEmpty()) {
                    continue;
                }
                code += line + "\n";
            }
        } catch (IOException ex) {
            Logger.getLogger(Keywords.class.getName()).
                    log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(Keywords.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }
        return code;
    }
}
