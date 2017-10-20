/* 
 * CreateLibraryPluginDialog.java
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

package eu.mihosoft.vrl.dialogs;

import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.lang.visual.ClassInfoObject;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.visual.VSwingUtil;
import java.io.File;
import java.util.ArrayList;

/**
 * Shows a dialog that allows to specify component information.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class CreateLibraryPluginDialog {

    /**
     * Shows the dialog.
     *
     * @param parent the parent component of the dialog
     * @return
     * <code>true</code> if the "yes" button has been clicked;
     * <code>false</code> otherwise
     */
    public static CreateLibraryPluginInfo show(VisualCanvas parent, String title) {

        final CreateLibraryPluginInfo dialogObject = new CreateLibraryPluginInfo();

        boolean result = RDialog.showConfirmDialog(
                parent, title, dialogObject, "Create Library Plugin").isValid();

        return dialogObject;
    }

    /**
     * Shows the dialog.
     *
     * @param parent the parent component of the dialog
     * @return
     * <code>true</code> if the "yes" button has been clicked;
     * <code>false</code> otherwise
     */
    public static CreateLibraryPluginInfo show(VisualCanvas parent) {

        return show(parent, "Create Library Plugin");
    }

    @ObjectInfo(serialize = false, controlFlowIn = false, controlFlowOut = false)
    public static class CreateLibraryPluginInfo extends DialogUIClass {

        private String pluginName;
        private String pluginVersion;
        private String description;
        private File destination;
        private File[] libraries;

        public CreateLibraryPluginInfo() {
            super(1);
        }

        @MethodInfo(name = "Plugin Properties:",
        interactive = false, hide = false, num = 1)
        public void setValues(
                @ParamInfo(name = "Name:",
                style = "plugin-name") String name,
                @ParamInfo(name = "Version:") String version,
                @ParamInfo(name = "Destination", style = "save-folder-dialog") File dest,
                @ParamInfo(name = " -- Libraries: --", style = "load-dialog",
                options = "minArraySize=1;endings=[\".jar\"];description=\"Java Library - *.jar\"") File[][] libraries) {
            this.pluginName = name;
            this.pluginVersion = version;
            this.destination = dest;

            ArrayList<File> libraryFilesList = new ArrayList<File>();



            for (int i = 0; i < libraries.length; i++) {

                if (libraries[i] == null) {
                    continue;
                }

                for (int j = 0; j < libraries[i].length; j++) {

                    if (libraries[i][j] == null) {
                        continue;
                    }

                    libraryFilesList.add(libraries[i][j]);
                }
            }
            
            this.libraries = new File[libraryFilesList.size()];
            this.libraries = libraryFilesList.toArray(this.libraries);

            validCall();
        }

        /**
         * @return the pluginName
         */
        @MethodInfo(noGUI = true)
        public String getPluginName() {
            return pluginName;
        }

        /**
         * @return the pluginVersion
         */
        @MethodInfo(noGUI = true)
        public String getPluginVersion() {
            return pluginVersion;
        }

        /**
         * @return the description
         */
        @MethodInfo(noGUI = true)
        public String getDescription() {
            return description;
        }

        /**
         * @return the destination
         */
        @MethodInfo(noGUI = true)
        public File getDestination() {
            return destination;
        }

        /**
         * @return the libraries
         */
        @MethodInfo(noGUI = true)
        public File[] getLibraries() {
            return libraries;
        }
        
    }
}
