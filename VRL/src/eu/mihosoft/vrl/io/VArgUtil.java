/* 
 * VArgUtil.java
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

package eu.mihosoft.vrl.io;

/**
 * Evaluates command line arguments.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VArgUtil {

    // no instanciation allowed
    private VArgUtil() {
        throw new AssertionError(); // not in this class either!
    }

    /**
     * Returns the value of a given command line argument.
     * @param args the command line arguments
     * @param argument the argument to return
     * @return the value or <code>null</code> if no such argument can be found
     */
    public static String getArg(String[] args, String argument) {
        String result = null;
        
        if (!argument.startsWith("-")) {
            throw new IllegalArgumentException(
                    "Argument has wrong format. Must start with -!");
        }

        for (int i = 0; i < args.length; i++) {
            String s = args[i];
            if (s.equals(argument) && args.length > i + 1) {
                if (args[i + 1].startsWith("-")) {
                    result = "";
                } else {
                    result = args[i + 1];
                }
                break;
            }
        }

        return result;
    }

    /**
     * Checks whether the specified argument array contains unknown arguments.
     * @param args arguments to check
     * @param knownArgs known arguments
     * @return <code>true</code> if the specified argument array contains
     *         unknown arguments; <code>false</code> otherwise
     */
    public static boolean hasUnknownArgs(
            String[] args, String... knownArgs) {
        
        boolean result = false;
        
        for(String a : args) {
            if (a.startsWith("-")) {
                boolean found = false;
                for(String ka : knownArgs) {
                    if (ka.equals(a)) {
                        found = true;
                        result = true;
                        break;
                    }
                }
                
                if (!found) {
                    System.out.println(" --> unknown argument: " + a);
                }
            }
        }
        
        return result;
    }

    /**
     * Returns the filename argument.
     * @param args the command line arguments
     * @return the filename argument or <code>null</code> if no such argument
     * can be found
     */
    public static String getFilename(String args[]) {
        String result = null;

        for (String s : args) {
            if (s.matches(".*\\.vrlp")) {
                result = s;
                break;
            }
        }

        return result;
    }
}
