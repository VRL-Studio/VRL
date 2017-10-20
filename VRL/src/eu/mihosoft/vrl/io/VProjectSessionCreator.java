/* 
 * VProjectSessionCreator.java
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

import eu.mihosoft.vrl.system.VParamUtil;
import eu.mihosoft.vrl.system.VRL;
import eu.mihosoft.vrl.visual.Message;
import eu.mihosoft.vrl.visual.VDialog;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VProjectSessionCreator implements FileSaver {
//    private VProject project;

    private boolean createdProject;
    private String defaultProject;
    private boolean askBeforeOverwrite;

    public VProjectSessionCreator(String defaultSessionName, boolean askBeforeOverwrite) {
        this.defaultProject = defaultSessionName;
        this.askBeforeOverwrite = askBeforeOverwrite;
    }
    
        public VProjectSessionCreator(String defaultSessionName) {
        this.defaultProject = defaultSessionName;
        this.askBeforeOverwrite = true;
    }

    @Override
    public void saveFile(Object o, File file, String ext) throws IOException {

        VParamUtil.throwIfNull(file);

        // ensure that o is an instance of VProjectController
        VParamUtil.throwIfNotValid(
                VParamUtil.VALIDATOR_INSTANCEOF, VProjectController.class, o);

        VProjectController controller = (VProjectController) o;


        // name of backup file
        String fileName = VRL.getPropertyFolderManager().toLocalPathInTmpFolder(
                new File(file.getAbsolutePath() + "~")).getAbsolutePath();

        // defines whether new and current are the same project file
        // in this case we don't have to ask the user whether we want to 
        // save the current project before closing it because it will be
        // overwritten anyway
        boolean sameProject = false;

        if (controller.getProject() != null) {
            sameProject =
                    controller.getProject().getFile().equals(file);
        }

        VDialog.AnswerType answer = VDialog.YES;

        if (sameProject && askBeforeOverwrite) {

            answer = VDialog.showConfirmDialog(controller.getCurrentCanvas(),
                    "OVerwrite current Project?",
                    "<html><div align=Center>"
                    + "<p>Do you really want to overwrite the current project?</p><br>"
                    + "<p><b>The current project will be lost!</b></p>"
                    + "</div></html>",
                    VDialog.DialogType.YES_NO);
        } else {
            //
        }

        // stop if current project shall not be overwritten
        if (answer == VDialog.NO) {
            return;
        }

        // if a default project has been specified and if it exists
        // copy it to the location were the new project shall be ctreated
        // and open it
        if (defaultProject != null && new File(defaultProject).isFile()) {

            controller.closeProject(!sameProject, null);

            IOUtil.copyFile(new File(defaultProject), file);

            createdProject = controller.loadProject(file, false);
            createdProject = false;
        } else {

            // if a previous version of the file already exists make a backup
            // note: existing backup files will be overwritten silently
            if (file.exists() && file.isFile()) {

                // necessary for windows
                // on unix paths are created automatically)
                new File(fileName).getParentFile().mkdirs();

                file.renameTo(new File(fileName));

//              IOUtil.deleteDirectory(new File(fileName));
//              IOUtil.copyFile(file, new File(fileName));
//              IOUtil.deleteDirectory(file);
            }

            controller.closeProject(!sameProject, null);

            createdProject = controller.newProject(file, false, false);

            if (!createdProject) {
                // revert backup
                new File(fileName).renameTo(file);
            }
        }

    }

    public boolean createdProject() {
        return createdProject;
    }

//    /**
//     * 
//     * @return the project
//     */
//    public VProject getProject() {
//        if (project == null) {
//            throw new IllegalStateException(
//                    "Project does not exist. Please call saveFile() first!");
//        }
//        
//        return project;
//    }
    @Override
    public String getDefaultExtension() {
        return "vrlp";
    }
}
