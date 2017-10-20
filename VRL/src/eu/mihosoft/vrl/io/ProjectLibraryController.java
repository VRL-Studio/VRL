/* 
 * ProjectLibraryController.java
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

package eu.mihosoft.vrl.io;

import eu.mihosoft.vrl.system.Constants;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ProjectLibraryController {

    private VProjectController projectController;
    public static final String LIB_FOLDER_NAME = "META-INF/VRL/lib";
    private File libFolderLocation;

    public ProjectLibraryController(VProjectController projectController) {
        this.projectController = projectController;
    }

    void projectDefined() {
        this.libFolderLocation = new File(
                projectController.getProject().
                getContentLocation() + "/" + LIB_FOLDER_NAME);

        if (!libFolderLocation.exists()) {
            libFolderLocation.mkdirs();
        }
    }

    public String[] getLibs() {
        ArrayList<File> files = IOUtil.listFiles(
                getLibFolderLocation(),
                new String[]{".jar"});

        String[] result = new String[files.size()];

        for (int i = 0; i < files.size(); i++) {
            result[i] = files.get(i).getName();
        }

        return result;
    }

    public boolean addLib(File f) {

        if (!f.getName().endsWith(".jar")) {
            throw new IllegalArgumentException(
                    "Only .jar-Files are supported.");
        }

        boolean result = false;

        try {
            IOUtil.copyFile(f, new File(getLibFolderLocation() + "/" + f.getName()));
            result = true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ProjectLibraryController.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ProjectLibraryController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        return result;

    }

    public boolean deleteLib(String name) {

        File f = new File(getLibFolderLocation(), name);

        boolean state = f.delete();

        return state;
    }

    /**
     * @return the libFolderLocation
     */
    public File getLibFolderLocation() {
        return libFolderLocation;
    }
}
