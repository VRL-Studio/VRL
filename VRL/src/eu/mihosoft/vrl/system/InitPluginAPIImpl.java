/* 
 * InitPluginAPIImpl.java
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

package eu.mihosoft.vrl.system;

import eu.mihosoft.vrl.dialogs.FileDialogManager;
import eu.mihosoft.vrl.io.ConfigurationFile;
import eu.mihosoft.vrl.io.FileSaver;
import eu.mihosoft.vrl.io.IOUtil;
import eu.mihosoft.vrl.io.ProjectFileFilter;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.visual.Action;
import eu.mihosoft.vrl.visual.MessageType;
import eu.mihosoft.vrl.visual.VSwingUtil;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class InitPluginAPIImpl implements InitPluginAPI {

    private PluginDataController dataController;

    public InitPluginAPIImpl(PluginDataController pdC) {
        this.dataController = pdC;
    }

    @Override
    public File getResourceFolder() {
        return dataController.getResourceFolder();
    }

    @Override
    public ConfigurationFile getConfiguration() {
        return dataController.getConfiguration();
    }

    @Override
    public void addProjectTemplate(final ProjectTemplate template) {

        Action a = new Action() {

            @Override
            public String getText() {
                return template.getName();
            }

            @Override
            public Icon getIcon() {

                // TODO ICON
//                IconCreator ...
//
//                return template.getIcon();

                return null;
            }

            @Override
            public boolean isSeparator() {
                return false;
            }

            @Override
            public void actionPerformed(ActionEvent e, Object owner) {

                // we can cast, checks are already made
                VisualCanvas vCanvas = VRL.getCurrentProjectController().
                        getCurrentCanvas();
                try {
                    if (!VRL.getCurrentProjectController().closeProject(
                            true, null)) {
                        return;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(InitPluginAPIImpl.class.getName()).
                            log(Level.SEVERE, null, ex);
                }

                File src = template.getSource();

                // dummy saver, only retrieves selected file
                class TemplateFileSaver implements FileSaver {

                    public File file;

                    @Override
                    public void saveFile(
                            Object o, File file, String ext) throws IOException {
                        this.file = file;
                    }

                    @Override
                    public String getDefaultExtension() {
                        return "vrlp";
                    }
                };

                TemplateFileSaver saver = new TemplateFileSaver();

                FileDialogManager manager = new FileDialogManager();
                manager.saveFile(
                        vCanvas, null, saver, new ProjectFileFilter());

                File dest = saver.file;

                if (dest != null) {
                    boolean success = false;
                    String msg = ">> error: ";
                    try {
                        IOUtil.copyFile(src, dest);
                        success = true;
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(PluginAPIImpl.class.getName()).
                                log(Level.SEVERE, null, ex);
                        msg += ex.toString();
                    } catch (IOException ex) {
                        Logger.getLogger(PluginAPIImpl.class.getName()).
                                log(Level.SEVERE, null, ex);
                        msg += ex.toString();
                    }

                    if (!success) {
                        vCanvas.getMessageBox().addMessage(
                                "Cannot Create Project:",
                                msg, MessageType.ERROR);
                    } else {
                        try {
                            VRL.getCurrentProjectController().
                                    loadProject(dest, false);
                        } catch (IOException ex) {
                            Logger.getLogger(VPluginAPIImpl.class.getName()).
                                    log(Level.SEVERE, null, ex);

                            VRL.getCurrentProjectController().
                                    getCurrentCanvas().getMessageBox().
                                    addMessage(
                                    "Cannot Create Project:",
                                    ">> error: " + ex.toString(),
                                    MessageType.ERROR);
                        }
                    }
                }
            }
        };

        VRL.getFileTemplatesMenuController().addAction(a);
    }
}
