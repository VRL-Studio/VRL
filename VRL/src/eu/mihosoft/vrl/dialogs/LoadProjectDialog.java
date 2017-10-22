/* 
 * LoadProjectDialog.java
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

package eu.mihosoft.vrl.dialogs;

import eu.mihosoft.vrl.io.CanvasConfigurator;
import eu.mihosoft.vrl.io.ProjectFileFilter;
import eu.mihosoft.vrl.io.VProjectController;
import eu.mihosoft.vrl.io.VProjectSessionLoader;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import java.awt.Component;
import java.io.File;
import javax.swing.JComponent;

/**
 * A dialog for loading project sessions. This is the recommended way
 * to load VRL projects.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class LoadProjectDialog {

//    /**
//     * Opens a load file dialog and loads a project which
//     * is returned as project object.
//     * @param parent the parent component of the dialog
//     * @param mainCanvas the canvas that was in use before loading
//     * @return the loaded project object or
//     *         <code>null</code> if the project couldn't be loaded
//     */
//    public static Object showDialog(VisualCanvas parent) {
//        return showDialog(parent, null);
//    }

    /**
     * Opens a load file dialog and loads a project which
     * is returned as project object.
     * @param parent the parent component of the dialog
     * @param parent the canvas that was in use before loading
     * @param directory the current directory to use
     * @return the loaded project object or
     *         <code>null</code> if the project couldn't be loaded
     */
    public static Object showDialog(Component parent,
            VProjectController controller, File directory) {
        FileDialogManager dialogManager = new FileDialogManager();
        VProjectSessionLoader sessionLoader = 
                new VProjectSessionLoader(controller);

        return dialogManager.loadFile(parent, sessionLoader, directory,
                new ProjectFileFilter(), false);
    }
}
