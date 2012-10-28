/* 
 * PathUtil.java
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

package eu.mihosoft.vrl.io.vrlx;

/**
 * Provides utility methods for path handling.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class PathUtil {

    /**
     * Formats a given path: 
     * <ul>
     * <li>removes leading and trailing /</li>
     * <li>replaces // with /</li>
     * </ul>
     * @param path path to format
     * @return  formatted path
     */
    public static String formatPath(String path) {
        String result = path;

        result.replaceAll("//", "/");

        if (result.startsWith("/")) {
            result = result.substring(1);
        }

        if (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    /**
     * Splits a path using / as delimiter.
     * @param path path to split
     * @return splitted path
     */
    public static String[] splitPath(String path) {
        return formatPath(path).split("/");
    }

    /**
     * Returns the parent path, e.g., <code>"/folder1/folder2"</code> becomes
     * <code>"/folder1/"</code>
     * @param path path
     * @return parent path
     */
    public static String getParentPath(String path) {
        String[] pathEntries = splitPath(path);

        String result = "";

        for (int i = 0; i < pathEntries.length - 1; i++) {
            result += pathEntries[i] + "/";
        }

        return result;
    }

    /**
     * Returns the filename of the given absolute or relative path, i.e.,
     * <code>"/folder1/folder2/file.txt"<code> becomes <code>"file.txt"<code>.
     * <p><b>Note:</b> this method does not determine if the given string
     * denotes a path to a file or a folder as the given path does not 
     * necessarily represent existing folders and files on a physical harddisk.
     * Thus, <code>"/folder1/folder2/"<code> becomes <code>"folder2"<code></p>
     * @param path path
     * @return filename
     */
    public static String getFileName(String path) {
        String[] pathEntries = splitPath(path);

        String fileName = "";

        if (pathEntries.length > 0) {
            fileName = pathEntries[pathEntries.length - 1];
        }

        if (fileName.length() == 0) {
            throw new IllegalArgumentException("empty filename!");
        }

        return fileName;
    }
}
