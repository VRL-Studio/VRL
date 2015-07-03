/* 
 * CompatibleFileVersionInfo.java
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

import eu.mihosoft.vrl.io.vrlx.FileVersionInfo;
import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.MessageBox;
import eu.mihosoft.vrl.visual.MessageType;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class can be used to define a range of compatible file versions. It
 * provides functionality for version verification etc.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class CompatibleFileVersionInfo {

    private FileVersionInfo minimum;
    private FileVersionInfo maximum;
    private FileVersionInfo currentVersion;

    /**
     * Constructor.
     */
    public CompatibleFileVersionInfo() {
    }

    /**
     * Constructor.
     * @param currentVersion the current version
     * @param minimum the minimum supported version
     * @param maximum the maximum supported version
     */
    public CompatibleFileVersionInfo(FileVersionInfo currentVersion,
            FileVersionInfo minimum,
            FileVersionInfo maximum) {
        this.currentVersion = currentVersion;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    /**
     * Returns the minimum version.
     * @return the minimum version
     */
    public FileVersionInfo getMinimum() {
        return minimum;
    }

    /**
     * Defines the munimum supported version.
     * @param minimum the version to set
     */
    public void setMinimum(FileVersionInfo minimum) {
        this.minimum = minimum;
    }

    /**
     * Returns the maximum supported version.
     * @return the maximum version
     */
    public FileVersionInfo getMaximum() {
        return maximum;
    }

    /**
     * Defines the maximum supported version.
     * @param maximum the version to set
     */
    public void setMaximum(FileVersionInfo maximum) {
        this.maximum = maximum;
    }

    /**
     * Verifies whether the specified version is compatible.
     * @param info the file version info
     * @param mainCanvas the main canvas
     * @return <code>true</coce> if the specified version is compatible;
     *         <code>false</code> otherwise
     */
    public boolean verify(FileVersionInfo info, Canvas mainCanvas) {
        boolean result = false;

        try {
            boolean greaterOrEqual =
                    minimum.compareTo(info) <= 0;

            boolean lessOrEqual =
                    maximum.compareTo(info) >= 0;

            if (greaterOrEqual && lessOrEqual) {
                result = true;
            } else {
                if (mainCanvas != null) {
                    MessageBox mBox = mainCanvas.getMessageBox();
                    mBox.addMessage("Can't load file", ">> File version "
                            + info.getVersion() + " is not supported!<br>"
                            + ">> Suported versions: min="
                            + getMinimum().getVersion()
                            + ", max=" + getMaximum().getVersion(),
                            MessageType.ERROR);
                }
            }
        } catch (Exception ex) {
            System.out.println("EXCEPITON:");

            Logger.getLogger(CompatibleFileVersionInfo.class.getName()).
                            log(Level.SEVERE, null, ex);

            return false;
        }

        return result;
    }

    /**
     * Returns the compatibility range as string.
     * @return the compatibility range as string
     */
    public String getCompatibilityRange() {
        return "[" + getMinimum().getVersion()
                + ", " + getMaximum().getVersion() + "]";
    }

    /**
     * Returns the current version.
     * @return the current version
     */
    public FileVersionInfo getCurrentVersion() {
        return currentVersion;
    }

    /**
     * Defines the current version.
     * @param currentVersion the version to set
     */
    public void setCurrentVersion(FileVersionInfo currentVersion) {
        this.currentVersion = currentVersion;
    }
}
