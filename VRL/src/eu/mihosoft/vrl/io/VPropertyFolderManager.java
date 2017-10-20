/* 
 * VPropertyFolderManager.java
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
import eu.mihosoft.vrl.system.VSysUtil;
import eu.mihosoft.vrl.system.VTerminalUtil;
import eu.mihosoft.vrl.visual.VSwingUtil;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VPropertyFolderManager {

    private final String HOME_FOLDER =
            System.getProperty("user.home");
    private final File PROPERTY_FOLDER_BASE =
            new File(HOME_FOLDER + "/.vrl/" + Constants.VERSION_MAJOR);
    private  File PROPERTY_FOLDER;
    private  File PROPERTY_FOLDER_TEMPLATE;
    private  File TMP_BASE;
    private  File PLUGINS;
    private  File PLUGIN_UPDATES;
    private  File UPDATES;
    private  File ETC;
    private  File TMP;
    private  File RESOURCES;
    private  File PROJECT_TEMPLATES;
    private  int maximumNumberOfBackups = 5;
    private  boolean initialized = false;
    private  Runnable alreadyRunningTask;

    public void setAlreadyRunningTask(Runnable r) {
        alreadyRunningTask = r;
    }

    public void installBundledSoftwareAndResources() throws IOException {
        IOUtil.copyDirectory(PROPERTY_FOLDER_TEMPLATE, getPropertyFolder());
    }

    public void create() throws IOException {

        if (PROPERTY_FOLDER.exists() && !getPropertyFolder().isDirectory()) {
            throw new IOException("VRL property folder cannot be created: "
                    + getPropertyFolder().getAbsolutePath()
                    + " A file with this"
                    + " name already exists.");
        }

        if (PROPERTY_FOLDER_TEMPLATE != null) {

            if (!getPropertyFolder().exists()) {
                System.out.println(
                        " --> initializing property folder from template");

                if (PROPERTY_FOLDER_TEMPLATE.isDirectory()) {
                    IOUtil.copyDirectory(PROPERTY_FOLDER_TEMPLATE, getPropertyFolder());
                } else {
                    System.out.println(
                            "  --> requested template does not exist! Using empty folder.");
                }
            }
        }

        if (!getPropertyFolder().exists()) {
            if (!getPropertyFolder().mkdirs()) {
                throw new IOException("VRL property folder cannot be created: "
                        + getPropertyFolder().getAbsolutePath());
            }
        }

        // init plugin folder
        PLUGINS.mkdir();

        // init plugin update folder
        PLUGIN_UPDATES.mkdir();
        
        // init update folder
        UPDATES.mkdir();

        // init tmp folder
        TMP_BASE.mkdir();

        // init etc folder
        ETC.mkdir();

        // init resources folder
        RESOURCES.mkdir();

        // init templates folder
        PROJECT_TEMPLATES.mkdir();
    }

    public void remove() throws IOException {
        System.out.println(">> removing preference folder");
        if (VSysUtil.isWindows()) {
            IOUtil.deleteTmpFilesOnExitIgnoreFileLocks(getPropertyFolder());
        } else {
            IOUtil.deleteDirectory(getPropertyFolder());
        }
    }

    public void init() {
        init(null, false);
    }

    public void init(String folder, boolean asSuffix) {
        if (!isInitialized()) {

            if (asSuffix) {
                if (folder == null) {
                    setPropertyFolder( new File(PROPERTY_FOLDER_BASE, "default"));
                } else {
                    setPropertyFolder( new File(PROPERTY_FOLDER_BASE, folder));
                }
            } else {
                if (folder == null) {
                    setPropertyFolder( new File(PROPERTY_FOLDER_BASE, "default"));
                } else {
                    setPropertyFolder( new File(folder));
                }
            }
            System.out.println(
                    " --> initializing property folder: " + getPropertyFolder());

            TMP_BASE = new File(getPropertyFolder(), "tmp");
            TMP = new File(TMP_BASE, "0");
            PLUGINS = new File(getPropertyFolder(), "plugins");
            PLUGIN_UPDATES = new File(getPropertyFolder(), "plugin-updates");
            UPDATES = new File(getPropertyFolder(), "updates");
            ETC = new File(getPropertyFolder(), "etc");
            RESOURCES = new File(getPropertyFolder(), "resources");
            PROJECT_TEMPLATES = new File(RESOURCES, "project-templates");

            if (isLocked() && alreadyRunningTask != null) {
                VSwingUtil.invokeAndWait(alreadyRunningTask);
//                alreadyRunningTask.run();
            }

            lockFolder(true);

            try {
                create();
            } catch (IOException ex) {
                Logger.getLogger(VPropertyFolderManager.class.getName()).
                        log(Level.SEVERE, null, ex);
            }

            shiftTmpFolders();
            initialized = true;
        } else {
            throw new IllegalStateException("Already initialized.");
        }
    }

    private void shiftTmpFolders() {

        // delete all tmp files that don't match
        for (File f : TMP_BASE.listFiles()) {

            // if name is not a number delete it
            if (!f.getName().matches("\\d+")) {
                IOUtil.deleteDirectory(f);
            }//
            // if name interpreted as number is bigger than maxNumberBackup
            // delete it
            else {
                int value = new Integer(f.getName());
                if (value > maximumNumberOfBackups) {
                    IOUtil.deleteDirectory(f);
                }
            }
        }

        // delete latest backup
        IOUtil.deleteDirectory(new File(TMP_BASE, "" + maximumNumberOfBackups));

        for (int i = maximumNumberOfBackups - 1; i >= 0; i--) {
            new File(TMP_BASE, "" + i).renameTo(new File(TMP_BASE, "" + (i + 1)));
        }

        TMP.mkdir();
    }

    public File getTmpFolder() {
        return TMP;
    }

    public File getPluginFolder() {
        return PLUGINS;
    }

    public File getPluginUpdatesFolder() {
        return PLUGIN_UPDATES;
    }
    
    public File getUpdatesFolder() {
        return UPDATES;
    }

    public File getResourcesFolder() {
        return RESOURCES;
    }

    public File getProjectTemplatesFolder() {
        return PROJECT_TEMPLATES;
    }

    public File getPropertyFolder() {
        return PROPERTY_FOLDER;
    }
    
    public void setPropertyFolder(File propertyFolder) {
        PROPERTY_FOLDER = propertyFolder;
    }

    public File getPropertyFolderTemplate() {
        return PROPERTY_FOLDER_TEMPLATE;
    }

    public File getEtcFolder() {
        return ETC;
    }

//    public static void main(String[] args) {
//        VPropertyFolderManager.init();
//
//        System.out.println(VSysUtil.getPID());
//
//        File[] roots = File.listRoots();
//        for (int i = 0; i < roots.length; i++) {
//            System.out.println("Root[" + i + "]:" + roots[i].getAbsolutePath());
//        }
//    }
    public File toLocalPathInTmpFolder(File f) {

        File result = new File(getTmpFolder(), toLocalPath(f).getPath());

        return result;
    }

    public File toLocalPath(File f) {
        String path = f.getAbsolutePath();

        // if on windows, replace drive letter,
        // e.g., c:\test123 becomes drive_c\test123
        if (VSysUtil.isWindows()) {
            for (File drive : File.listRoots()) {
                String s = drive.getAbsolutePath();
                if (path.startsWith(s)) {
                    path = "Drive_" + s.substring(0, 1) + "\\"
                            + path.substring(2);
                }
            }
        }// 
        // on unix it's much easier. we just rempve the first /. Unix only has
        // one file system root
        else if (VSysUtil.isLinux() || VSysUtil.isMacOSX()) {
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
        }

        return new File(path);

    }

    public void evalueteArgs(String[] args) {
        String template = VArgUtil.getArg(args, "-property-folder-template");
        String folder = VArgUtil.getArg(args, "-property-folder");
        String subfolder = VArgUtil.getArg(args, "-property-folder-suffix");

        System.out.println(">> Property Folder Options:");

        if (template != null) {
            System.out.println(
                    " --> using property folder template, path: " + template);
            PROPERTY_FOLDER_TEMPLATE = new File(template);
        }

        boolean ignoreSuffix = false;

        if (folder != null && subfolder != null) {

            System.out.println(
                    "folder: " + folder + ", subfolder: " + subfolder);

            System.out.println(VTerminalUtil.red(
                    " --> cannot define both, custom folder and suffix."
                    + " Ignoring suffix!"));

            ignoreSuffix = true;
        }

        if (folder != null) {
            System.out.println(
                    " --> using custom property folder, path: " + folder);
            init(folder, false);

            return;
        }

        if (!ignoreSuffix) {
            if (subfolder != null) {
                System.out.println(
                        " --> using custom property folder suffix, suffix: "
                        + subfolder);
                init(subfolder, true);
                return;
            }
        }


        System.out.println(
                " --> no folder or suffix specified, using default.");
        init();


    }
//    public static String[] knownArgs() {
//        return new String[]{"-property-folder", "-property-folder-suffix"};
//    }

    public void lockFolder(boolean force) {
        File lockFile = new File(getPropertyFolder(), ".lock");

//        if (!force && isLocked()) {
//            return;
//        }

        try {
            lockFile.createNewFile();
        } catch (IOException ex) {
            //
            System.out.println(" --> cannot lock property folder (does not exist).");
        }

        SynchronizedFileAccess.lockFile(lockFile);
    }

    public boolean isLocked() {
        File lockFile = new File(getPropertyFolder(), ".lock");
//        return new File(getPropertyFolder(), ".locked").exists();

        return SynchronizedFileAccess.isLocked(lockFile);
    }

    public void unlockFolder() {
        System.out.println(" --> unlocking property folder"
                + " (action will be performed on shutdown).");
//        File lockFile = new File(getPropertyFolder(), ".locked");
//
//        IOUtil.deleteDirectory(lockFile);
//
//        if (lockFile.exists()) {
//            IOUtil.deleteTmpFilesOnExitIgnoreFileLocks(lockFile);
//        }
    }

    /**
     * @return the initialized
     */
    public boolean isInitialized() {
        return initialized;
    }
}
