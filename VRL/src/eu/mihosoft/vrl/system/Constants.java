/* 
 * Constants.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007–2018 by Michael Hoffer,
 * Copyright (c) 2015–2018 G-CSC, Uni Frankfurt,
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn)
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
 * 181?192. http://doi.org/10.1007/s00791-014-0230-y
 */
package eu.mihosoft.vrl.system;

import eu.mihosoft.vrl.io.VPropertyFolderManager;
import eu.mihosoft.vrl.io.vrlx.CompatibleFileVersionInfo;
import eu.mihosoft.vrl.io.vrlx.FileVersionInfo;
import eu.mihosoft.vrl.visual.Message;

/**
 * System and Version Constants.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Constants {

    /**
     * Major version specifies the major version of VRL. Changes of this
     * variable indicate incompatible versions. Thus, for each major version, a
     * custom property folder will be generated.
     */
    public static final String VERSION_MAJOR = "0.4.5";
    /**
     * VRL version number that indicates major and minor changes.
     */
    public static final String VERSION_BASE = VERSION_MAJOR + ".0.0";
    // "-HEAD" or "-unstable" or "" for release etc.
    public static final String VERSION_SUFFIX = "";
    // full version (base+suffix+date)
    public static final String VERSION =
            VERSION_BASE + VERSION_SUFFIX
            + ", build: " +/*<VRL_COMPILE_DATE>*/""/*</VRL_COMPILE_DATE>*/;
    /**
     * Copyright statement.
     */
    public static final String COPYRIGHT =
            "2006-" + /*<VRL_COMPILE_DATE_YEAR>*/"2020"/*</VRL_COMPILE_DATE_YEAR>*/ + " by Michael Hoffer &lt;info@michaelhoffer.de&gt;";
    /**
     * Simple copyright statement (usefull for windows titles etc.)
     */
    public static final String COPYRIGHT_SIMPLE =
            "2006-" + /*<VRL_COMPILE_DATE_YEAR>*/"2020"/*</VRL_COMPILE_DATE_YEAR>*/ + " by Michael Hoffer";
    /**
     * Defines which projects can be processed by this version of VRL.
     */
    public static CompatibleFileVersionInfo fileVersionCompatibility =
            new CompatibleFileVersionInfo(
            new FileVersionInfo(VERSION_BASE,
            "created by VRL version " + VERSION), // current version
            new FileVersionInfo("0.4.0", ""), // min version
            new FileVersionInfo(VERSION_MAJOR, ""));   // max version
    /**
     * VRL developers.
     */
    public static final String DEVELOPERS =
            "Michael Hoffer &lt;info@michaelhoffer.de&gt;";
    /**
     * String that contains
     * <code>"write a VRL bug report to $DEVELOPERS"</code> (HTML)
     */
    public static final String WRITE_VRL_BUG_REPORT = "write a VRL bug report to "
            + Message.EMPHASIZE_BEGIN + DEVELOPERS + Message.EMPHASIZE_END;
    /**
     * String that contains
     * <code>"write a VRL bug report to $DEVELOPERS"</code> (plain text)
     */
    public static final String WRITE_VRL_BUG_REPORT_PLAIN =
            "write a VRL bug report to " + DEVELOPERS;
    /**
     * Library folder name.
     */
    public static final String LIB_DIR = "lib/";
    /**
     * Custom library folder name.
     */
    public static final String CUSTOM_LIB_DIR = "custom-lib/";
    /**
     * Plugin folder (changed at run-time). <b>Note:</b> please use
     * {@link VPropertyFolderManager} to access the plugin folder.
     */
    public static String PLUGIN_DIR;
}
