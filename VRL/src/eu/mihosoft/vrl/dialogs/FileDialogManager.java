/* 
 * FileDialogManager.java
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
package eu.mihosoft.vrl.dialogs;

import eu.mihosoft.vrl.io.FileSaver;
import eu.mihosoft.vrl.io.FileLoader;
import eu.mihosoft.vrl.io.RestrictedFileSystemView;
import eu.mihosoft.vrl.system.VSysUtil;
import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.VSwingUtil;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

/**
 * A file dialog manager is responsible for showing a dialog for loading/saving
 * files and calling the corresponding file loader/saver. It also takes care of
 * already existing files and file extensions.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class FileDialogManager {

    private static File CACHED_DIR;

    /**
     * @return the default dir
     */
    public static File getDefaultDir() {
        return CACHED_DIR;
    }

    /**
     * @param dir the default dir to set
     */
    public static void setDefaultDir(File dir) {
        CACHED_DIR = dir;
    }

//    public static final String FILE_OR_FOLDER_LOADED_ACTION = "file-or-folder-selected";
//    //
//    protected File latestFileOrFolder;
//    
//    /**
//     * list of action listeners
//     */
//    private Collection<ActionListener> actionListeners =
//            new ArrayList<ActionListener>();
//
//    /**
//     * @return the changeListeners
//     */
//    public Collection<ActionListener> getActionListeners() {
//        return actionListeners;
//    }
//    
//   
//    /**
//     * Fires an action.
//     *
//     * @param event the event
//     */
//    protected void fireAction(ActionEvent event) {
//        for (ActionListener l : actionListeners) {
//            l.actionPerformed(event);
//        }
//    }
    /**
     * Opens a load file dialog.
     *
     * @param parent the parent component of the dialog
     * @param loader the file loader to use for loading
     * @param filter the file extension filter
     * @return the loaded object or <code>null</code> if the file could not be
     * loaded
     */
    public Object loadFile(Canvas parent, FileLoader loader,
            FileFilter filter) {
        return loadFile(parent, loader, null, filter, false);
    }

    /**
     * Opens a load file dialog.
     *
     * @param parent the parent component of the dialog
     * @param loader the file loader to use for loading
     * @param directory the current directory to use or <code>null</code> if the
     * current directory shall not be defined (if the file is no directoy the
     * parent directory is used)
     * @param filter the file extension filter
     * @param restrict defines whether to restrict the dialog to the specified
     * directory and its subdirectories
     * @return the loaded object or <code>null</code> if the file could not be
     * loaded
     */
    public Object loadFile(Component parent, FileLoader loader, File directory,
            FileFilter filter, boolean restrict) {

        Object result = null;

        if (VSysUtil.isMacOSX()) {
            VSwingUtil.forceAppleLAF(null);
        }

        final JFileChooser fc = new JFileChooser();

        directory = chooseDefaultDir(directory);

        if (restrict) {
            fc.setFileSystemView(new RestrictedFileSystemView(directory));
        }

        fc.setFileHidingEnabled(true);

        if (directory != null && directory.exists()) {
            if (!directory.isDirectory()) {
                directory = directory.getParentFile();
            }
            if (directory != null && directory.canRead()) {
                fc.setCurrentDirectory(directory);
            }
        }

        fc.setFileFilter(filter);
        int returnVal = fc.showOpenDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            boolean fileNotFound = false;

            if (file.exists()) {

                updateDefaultDir(file);

                try {
                    result = loader.loadFile(file);
                } catch (IOException ex) {

                    Logger.getLogger(FileDialogManager.class.getName()).
                            log(Level.SEVERE, null, ex);

                    fileNotFound = true;
                }
            } else {
                fileNotFound = true;
            }

            if (fileNotFound) {

                FileNotFoundDialog.show(parent);
            }


        } else {
            System.out.println(
                    ">> Operation \"load file\" cancelled by user." + "\n");
        }

        if (VSysUtil.isMacOSX()) {
            VSwingUtil.forceNimbusLAF(null);
        }

        return result;
    }

    /**
     * Opens a load file dialog and returns the selected file.
     *
     * @param parent the parent component of the dialog
     * @param filter the file extension filter
     * @return the selected file or <code>null</code> if no file has been
     * selected
     */
    public File getLoadFile(Component parent, FileFilter filter) {
        return getLoadFile(parent, null, filter, false);
    }

    private static File chooseDefaultDir(File directory) {
        if (directory == null) {
            directory = getDefaultDir();
        }

        return directory;
    }

    private static void updateDefaultDir(File fileOrDir) {

        if (fileOrDir == null) {
            return;
        }

        if (fileOrDir.isFile()) {
            fileOrDir = fileOrDir.getAbsoluteFile().getParentFile();
        }

        setDefaultDir(fileOrDir);
    }

    /**
     * Opens a load file dialog and returns the selected file.
     *
     * @param parent the parent component of the dialog
     * @param directory the current directory to use
     * @param filter the file extension filter
     * @param restrict defines whether to restrict the dialog to the specified
     * directory and its subdirectories
     * @return the selected file or <code>null</code> if no file has been
     * selected
     */
    public File getLoadFile(Component parent,
            File directory, FileFilter filter, boolean restrict) {

        directory = chooseDefaultDir(directory);

        File result = null;

        if (VSysUtil.isMacOSX()) {
            VSwingUtil.forceAppleLAF(null);
        }
        final JFileChooser fc = new JFileChooser();

        if (restrict) {
            fc.setFileSystemView(new RestrictedFileSystemView(directory));
            restrictNavigation(fc, directory);
        }

        fc.setCurrentDirectory(directory);


        fc.setFileFilter(filter);
        int returnVal = fc.showOpenDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            updateDefaultDir(file);

            result = file;

        } else {
            System.out.println(
                    ">> Operation \"open file\" cancelled by user." + "\n");
        }

        if (VSysUtil.isMacOSX()) {
            VSwingUtil.forceNimbusLAF(null);
        }

        return result;
    }

    /**
     * Opens a load file dialog and returns the selected files.
     *
     * @param parent the parent component of the dialog
     * @param directory the current directory to use
     * @param filter the file extension filter
     * @param restrict defines whether to restrict the dialog to the specified
     * directory and its subdirectories
     * @return the selected file or <code>null</code> if no file has been
     * selected
     */
    public File[] getLoadFiles(Component parent,
            File directory, FileFilter filter, boolean restrict) {
        File[] result = null;

        if (VSysUtil.isMacOSX()) {
            VSwingUtil.forceAppleLAF(null);
        }

        final JFileChooser fc = new JFileChooser();

        directory = chooseDefaultDir(directory);

        if (restrict) {
            fc.setFileSystemView(new RestrictedFileSystemView(directory));
        }

        fc.setCurrentDirectory(directory);
        fc.setFileFilter(filter);
        fc.setMultiSelectionEnabled(true);
        int returnVal = fc.showOpenDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File[] file = fc.getSelectedFiles();

            if (file.length > 0) {
                updateDefaultDir(file[0]);
            }

            result = file;

        } else {
            System.out.println(
                    ">> Operation \"open file\" cancelled by user." + "\n");
        }

        if (VSysUtil.isMacOSX()) {
            VSwingUtil.forceNimbusLAF(null);
        }

        return result;
    }

    /**
     * Opens a load folder dialog and returns the selected folder.
     *
     * @param parent the parent component of the dialog
     * @param directory the current directory to use
     * @param restrict defines whether to restrict the dialog to the specified
     * directory and its subdirectories
     * @return the selected folder or <code>null</code> if no folder has been
     * selected
     */
    public File getLoadFolder(Component parent,
            File directory, boolean restrict) {
        return getLoadFileOrFolder(parent, directory, restrict, JFileChooser.DIRECTORIES_ONLY, null);
    }

    /**
     * Opens a load file/folder dialog and returns the selected folder.
     *
     * @param parent the parent component of the dialog
     * @param directory the current directory to use
     * @param restrict defines whether to restrict the dialog to the specified
     * @param loadType specifies whether to load files, floders both , see
     * {@link javax.swing.JFileChooser#DIRECTORIES_ONLY} etc. directory and its
     * subdirectories
     * @return the selected folder or <code>null</code> if no folder has been
     * selected
     */
    public File getLoadFileOrFolder(Component parent,
            File directory, boolean restrict, int loadType, FileFilter filter) {
        File result = null;

        if (VSysUtil.isMacOSX()) {
            VSwingUtil.forceAppleLAF(null);
        }

        final JFileChooser fc = new JFileChooser();

        directory = chooseDefaultDir(directory);

        fc.setFileFilter(filter);

        if (restrict) {
            fc.setFileSystemView(new RestrictedFileSystemView(directory));
        }

        fc.setFileHidingEnabled(true);
        fc.setFileSelectionMode(loadType);
        fc.setCurrentDirectory(directory);
        int returnVal = fc.showOpenDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            updateDefaultDir(file);

////            if (file.exists()) {
////                result = file;
////            } else {
////                FileNotFoundDialog.show(parent);
////            }
            result = file;

        } else {
            System.out.println(
                    ">> Operation \"load folder\" cancelled by user." + "\n");
        }

        if (VSysUtil.isMacOSX()) {
            VSwingUtil.forceNimbusLAF(null);
        }

//
//        fireAction(new ActionEvent(this, 0, FILE_OR_FOLDER_LOADED_ACTION));

        return result;
    }

    /**
     * Opens a save file/folder dialog and returns the selected folder.
     *
     * @param parent the parent component of the dialog
     * @param directory the current directory to use
     * @param restrict defines whether to restrict the dialog to the specified
     * @param saveType specifies whether to load files, folders both , see
     * {@link javax.swing.JFileChooser#DIRECTORIES_ONLY} etc. directory and its
     * subdirectories
     * @return the selected folder or <code>null</code> if no folder has been
     * selected
     */
    public File getSaveFileOrFolder(Component parent,
            File directory, boolean restrict, int saveType, FileFilter filter) {
        File result = null;

        if (VSysUtil.isMacOSX()) {
            VSwingUtil.forceAppleLAF(null);
        }

        final JFileChooser fc = new JFileChooser();

        directory = chooseDefaultDir(directory);

        fc.setFileFilter(filter);

        if (restrict) {
            fc.setFileSystemView(new RestrictedFileSystemView(directory));
        }

        fc.setFileHidingEnabled(true);
        fc.setFileSelectionMode(saveType);
        fc.setCurrentDirectory(directory);
        int returnVal = fc.showSaveDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            updateDefaultDir(file);

////            if (file.exists()) {
////                result = file;
////            } else {
////                FileNotFoundDialog.show(parent);
////            }
            result = file;

        } else {
            System.out.println(
                    ">> Operation \"load folder\" cancelled by user." + "\n");
        }

        if (VSysUtil.isMacOSX()) {
            VSwingUtil.forceNimbusLAF(null);
        }

//
//        fireAction(new ActionEvent(this, 0, FILE_OR_FOLDER_LOADED_ACTION));

        return result;
    }

    /**
     * Opens a save file dialog.
     *
     * @param parent the parent component of the dialog
     * @param o the object to save
     * @param fileSaver the file saver to use for saving
     * @param filter the file extension filter
     *
     */
    public void saveFile(Component parent, Object o,
            FileSaver fileSaver,
            FileFilter filter) {
        saveFile(parent, o, fileSaver, null, filter, false);
    }

    /**
     * Opens a save file dialog.
     *
     * @param parent the parent component of the dialog
     * @param o the object to save
     * @param fileSaver the file saver to use for saving
     * @param directory the current directory to use or <code>null</code> if the
     * current directory shall not be defined (if the file is no directoy the
     * parent directory is used)
     * @param restrict defines whether to restrict the dialog to the specified
     * directory and its subdirectories
     * @param filter the file extension filter
     *
     */
    public void saveFile(Component parent, Object o,
            FileSaver fileSaver, File directory,
            FileFilter filter, boolean restrict) {

        if (VSysUtil.isMacOSX()) {
            VSwingUtil.forceAppleLAF(null);
        }

        final JFileChooser fc = new JFileChooser();

        directory = chooseDefaultDir(directory);

        if (restrict) {
            fc.setFileSystemView(new RestrictedFileSystemView(directory));
        }

        fc.setFileHidingEnabled(true);

        if (directory != null && directory.exists()) {
            if (!directory.isDirectory()) {
                directory = directory.getParentFile();
            }
            if (directory != null && directory.canRead()) {
                fc.setCurrentDirectory(directory);
            }
        }

        fc.setFileFilter(filter);

        int returnVal = fc.showSaveDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            updateDefaultDir(file);

            String ext = getFileExtension(file);

            boolean writeFile = true;
            boolean writeOrOverwrite = true;

            ext = chooseDefaultOrCustomExtension(
                    parent, file, filter, fileSaver);

            // if we don't have a supported extension we add one
            if (!hasSupportedExtension(file, filter)) {
                file = addFileExtension(file, ext);
            }

            // if we still don't have a supported extension we
            // can't write the file
            if (!hasSupportedExtension(file, filter)) {
                writeFile = false;
            } else {
                writeOrOverwrite = writeEvenIfFileExists(parent, file);
            }

            // we write the file if we have a supported extension and if we
            // accept to overwrite the file if it already exists
            writeFile = writeOrOverwrite && writeFile;

            if (writeFile) {
                try {
                    System.out.println("Saving: " + file.getName() + "\n");
                    fileSaver.saveFile(o, file, ext);
                } catch (IOException ex) {
                    Logger.getLogger(this.getClass().
                            getName()).log(Level.SEVERE, null, ex);
//                    System.err.println( ex );
                    CantSaveFileDialog.show(parent);
                }
            }
        } else {
            System.out.println(
                    ">> Operation \"save file\" cancelled by user." + "\n");
        }

        if (VSysUtil.isMacOSX()) {
            VSwingUtil.forceNimbusLAF(null);
        }
    }

    /**
     * Opens a save file dialog and returns the selected file.
     *
     * @param parent the parent component of the dialog
     * @param directory the current directory to use
     * @param filter the file extension filter
     * @param restrict defines whether to restrict the dialog to the specified
     * directory and its subdirectories
     * @return the selected file or <code>null</code> if no file has been
     * selected
     */
    public File getSaveFile(Component parent,
            File directory, FileFilter filter, boolean restrict) {
        File result = null;

        if (VSysUtil.isMacOSX()) {
            VSwingUtil.forceAppleLAF(null);
        }

        final JFileChooser fc = new JFileChooser();

        directory = chooseDefaultDir(directory);

        if (restrict) {
            fc.setFileSystemView(new RestrictedFileSystemView(directory));
        }

        fc.setFileHidingEnabled(true);
        fc.setCurrentDirectory(directory);
        fc.setFileFilter(filter);
        int returnVal = fc.showSaveDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            updateDefaultDir(file);

//            if (file.exists()) {
//                result = file;
//            } else {
//                FileNotFoundDialog.show(parent);
//            }
            result = file;

        } else {
            System.out.println(
                    ">> Operation \"save file\" cancelled by user." + "\n");
        }

        if (VSysUtil.isMacOSX()) {
            VSwingUtil.forceNimbusLAF(null);
        }

        return result;
    }

    /**
     * Opens a save file dialog and returns the selected files.
     *
     * @param parent the parent component of the dialog
     * @param directory the current directory to use
     * @param filter the file extension filter
     * @param restrict defines whether to restrict the dialog to the specified
     * directory and its subdirectories
     * @return the selected files or <code>null</code> if no file has been
     * selected
     */
    public File[] getSaveFiles(Component parent,
            File directory, FileFilter filter, boolean restrict) {
        File[] result = null;

        if (VSysUtil.isMacOSX()) {
            VSwingUtil.forceAppleLAF(null);
        }

        final JFileChooser fc = new JFileChooser();

        directory = chooseDefaultDir(directory);

        if (restrict) {
            fc.setFileSystemView(new RestrictedFileSystemView(directory));
        }

        // allow mut liple selections
        fc.setMultiSelectionEnabled(true);
        fc.setFileHidingEnabled(true);
        fc.setCurrentDirectory(directory);
        fc.setFileFilter(filter);
        int returnVal = fc.showSaveDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file[] = fc.getSelectedFiles();

            if (file.length > 0) {
                updateDefaultDir(file[0]);
            }

//            if (file.exists()) {
//                result = file;
//            } else {
//                FileNotFoundDialog.show(parent);
//            }
            result = file;

        } else {
            System.out.println(
                    ">> Operation \"save file\" cancelled by user." + "\n");
        }

        if (VSysUtil.isMacOSX()) {
            VSwingUtil.forceNimbusLAF(null);
        }

        return result;
    }

    /**
     * Opens a save folder dialog and returns the selected folder.
     *
     * @param parent the parent component of the dialog
     * @param directory the current directory to use
     * @param restrict defines whether to restrict the dialog to the specified
     * directory and its subdirectories
     * @return the selected folder or <code>null</code> if no folder has been
     * selected
     */
    public File getSaveFolder(Component parent,
            File directory, boolean restrict) {
        File result = null;

        if (VSysUtil.isMacOSX()) {
            VSwingUtil.forceAppleLAF(null);
        }

        final JFileChooser fc = new JFileChooser();

        directory = chooseDefaultDir(directory);

        if (restrict) {
            fc.setFileSystemView(new RestrictedFileSystemView(directory));
        }

        fc.setFileHidingEnabled(true);
        fc.setCurrentDirectory(directory);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showSaveDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            
            updateDefaultDir(file);

//            if (file.exists()) {
//                result = file;
//            } else {
//                FileNotFoundDialog.show(parent);
//            }
            result = file;

        } else {
            System.out.println("Save dialog cancelled by user." + "\n");
        }

        if (VSysUtil.isMacOSX()) {
            VSwingUtil.forceNimbusLAF(null);
        }

        return result;
    }

    /**
     * Opens a save file dialog and returns the selected file.
     *
     * @param parent the parent component of the dialog
     * @param filter the file extension filter
     * @return the selected file or <code>null</code> if no file has been
     * selected
     */
    public File getSaveFile(Component parent, FileFilter filter) {
        return getSaveFile(parent, null, filter, false);
    }

    /**
     * Returns the extension of the given file.
     *
     * @param file the file
     * @return the extension of the file
     */
    private String getFileExtension(File file) {
        String ext = "";
        String s = file.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }

        return ext;
    }

    /**
     * Indicates whether the given file has an extension.
     *
     * @param file the file to check
     * @return <code>true</code> if the file has an extension;
     * <code>false</code> ozherwise
     */
    private boolean hasExtension(File file) {
        return getFileExtension(file).length() > 0;
    }

    /**
     * Indicates whether the given file has a supported file extension, i.e., if
     * the file filter contains the extension.
     *
     * @param file the file to check
     * @param filter the file extension filter
     * @return <code>true</code> if the extension is supported;
     * <code>false</code> ozherwise
     */
    private boolean hasSupportedExtension(File file, FileFilter filter) {
        return filter.accept(file) && !file.isDirectory();
    }

    /**
     * Returns either the default extension that is defined for the file or a
     * manually chosen one, depending on whether the manually extension is given
     * or supported. If the manually chosen extension is not supported a dialog
     * will open and ask if the default extension shall be used.
     *
     * @param parent the parent component of the dialog
     * @param file the file
     * @param filter the file extension filter
     * @param fileSaver the file saver
     * @return the chosen file extension
     */
    private String chooseDefaultOrCustomExtension(Component parent,
            File file, FileFilter filter,
            FileSaver fileSaver) {

        String ext = getFileExtension(file);

        String defaultExt = fileSaver.getDefaultExtension();

        if (!hasExtension(file)) {
            ext = defaultExt;
            file = addFileExtension(file, ext);
        }

        if (!filter.accept(file)) {
            if (UseDefaultFormatDialog.show(parent, defaultExt, ext)) {
//                String filePath = file.getPath();
                ext = defaultExt;
            } else {
                ext = "";
            }
        }

        return ext;
    }

    /**
     * Adds a file extension to the file name.
     *
     * @param file the file that is to be extended
     * @param ext the extension
     * @return the file with extension
     */
    private File addFileExtension(File file, String ext) {
        return new File(file.getPath() + "." + ext);
    }

    /**
     * Opens the dialog and asks if already exting files shall be overwritten.
     *
     * @param parent the parent of the dialog
     * @param file the file to save
     * @return <code>true</code> if the file shall be overwritten;
     * <code>false</code> otherwise
     */
    private boolean writeEvenIfFileExists(Component parent, File file) {
        boolean result = true;

        if (file.isFile()) {
            // shall i overwrite the file
            result = OverwriteFileDialog.show(parent);
        }
        return result;
    }

    @SuppressWarnings("unchecked") // we must be compatible with 1.6
    public static void restrictNavigation(JFileChooser fc, File dir) {

        // delete combobox items
        for (Component c : VSwingUtil.getAllChildren(fc, JComboBox.class)) {
            JComboBox box = (JComboBox) c;


            if (box.getItemCount() == 0) {
                break;
            }

            if (box.getItemAt(0) instanceof File) {
                box.setModel(new DefaultComboBoxModel(new Object[]{dir}));
            }
        }

        // delete combobox items
        for (Component c : VSwingUtil.getAllChildren(fc, JTextField.class)) {
            JTextField tf = (JTextField) c;

            tf.setEditable(false);
        }
    }
//    /**
//     * @return the latestFileOrFolder
//     */
//    public File getLatestFileOrFolder() {
//        return latestFileOrFolder;
//    }
}
