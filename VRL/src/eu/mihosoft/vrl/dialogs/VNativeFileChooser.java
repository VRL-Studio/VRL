/* 
 * VNativeFilechooser.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2016 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2016 by Michael Hoffer
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

/*
 * Original Header:
 * 
 * Copyright (c) 2015, Veluria
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
import eu.mihosoft.vrl.io.VExtensionFileFilter;
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javax.swing.JFileChooser;
import static javax.swing.JFileChooser.DIRECTORIES_ONLY;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

/**
 * This is a drop-in replacement for Swing's file chooser. Instead of displaying
 * the Swing file chooser, this method makes use of the JavaFX file chooser.
 * JavaFX uses the OS's native file chooser, which is much better than the Java
 * one. Technically, this class is a waste of memory, but its use is convenient.
 * Furthermore, if JavaFX is not available, the default file chooser will be
 * displayed instead. Of course, this class will not compile if you don't have
 * an JDK 8 or higher that has JavaFX support. Since this class will have to
 * call the constructor of JFileChooser, it won't increase the performance of
 * the file chooser, though; if anything, it might further decrease it. Please
 * note that some methods have not been overwritten and may not have any impact
 * on the file chooser. Sometimes, the new JavaFX file chooser does not provide
 * a certain functionality, or the native file chooser provides them itself, but
 * there are no methods to access them. For example, one feature that is not
 * supported is the selection of files AND directories. If trying to set this
 * using setFileSelectionMode(), still only files will be selectable.
 *
 * @author Veluria
 * @author @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 * @version 1.6.2-HEAD
 */
public class VNativeFileChooser extends JFileChooser {

    public static final boolean FX_AVAILABLE;
    private List<File> currentFiles;
    private FileChooser fileChooser;
    private File currentFile;
    private DirectoryChooser directoryChooser;

    static {
        boolean isFx;
        try {
            Class.forName("javafx.stage.FileChooser");
            isFx = true;
            JFXPanel jfxPanel = new JFXPanel(); // initializes JavaFX environment
        } catch (ClassNotFoundException e) {
            isFx = false;
        }

        FX_AVAILABLE = isFx;
    }

    public VNativeFileChooser() {
        initFxFileChooser(null);
    }

    public VNativeFileChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
        initFxFileChooser(new File(currentDirectoryPath));
    }

    public VNativeFileChooser(File currentDirectory) {
        super(currentDirectory);
        initFxFileChooser(currentDirectory);
    }

    public VNativeFileChooser(FileSystemView fsv) {
        super(fsv);
        initFxFileChooser(fsv.getDefaultDirectory());
    }

    public VNativeFileChooser(File currentDirectory, FileSystemView fsv) {
        super(currentDirectory, fsv);
        initFxFileChooser(currentDirectory);
    }

    public VNativeFileChooser(String currentDirectoryPath, FileSystemView fsv) {
        super(currentDirectoryPath, fsv);
        initFxFileChooser(new File(currentDirectoryPath));
    }

    @Override
    public int showOpenDialog(Component parent) throws HeadlessException {
        if (!FX_AVAILABLE) {
            return super.showOpenDialog(parent);
        }
        runAndWait(() -> {
            //                parent.setEnabled(false);
            if (isDirectorySelectionEnabled()) {
                directoryChooser.setTitle("Open Directory");
                currentFile = directoryChooser.showDialog(null);
            } else if (isMultiSelectionEnabled()) {
                fileChooser.setTitle("Open Files");
                currentFiles = fileChooser.showOpenMultipleDialog(null);
            } else {
                fileChooser.setTitle("Open File");
                currentFile = fileChooser.showOpenDialog(null);
            }
        });

        if (isMultiSelectionEnabled()) {
            if (currentFiles != null) {
                return JFileChooser.APPROVE_OPTION;
            } else {
                return JFileChooser.CANCEL_OPTION;
            }
        } else if (currentFile != null) {
            return JFileChooser.APPROVE_OPTION;
        } else {
            return JFileChooser.CANCEL_OPTION;
        }

    }

    // taken from here: http://www.guigarage.com/2013/01/invokeandwait-for-javafx/
    static void runAndWait(Runnable runnable) {
        FutureTask future = new FutureTask(runnable, null);
        Platform.runLater(future);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(VNativeFileChooser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int showSaveDialog(Component parent) throws HeadlessException {
        if (!FX_AVAILABLE) {
            return super.showSaveDialog(parent);
        }

        runAndWait(() -> {
            //                parent.setEnabled(false);
            if (isDirectorySelectionEnabled()) {
                directoryChooser.setTitle("Save Directory");
                currentFile = directoryChooser.showDialog(null);
            } else {
                fileChooser.setTitle("Save File");
                currentFile = fileChooser.showSaveDialog(null);
            }

        });

        if (currentFile != null) {
            return JFileChooser.APPROVE_OPTION;
        } else {
            return JFileChooser.CANCEL_OPTION;
        }
    }

    @Override
    public int showDialog(Component parent, String approveButtonText) throws HeadlessException {
        if (!FX_AVAILABLE) {
            return super.showDialog(parent, approveButtonText);
        }
        return showOpenDialog(parent);
    }

    @Override
    public File[] getSelectedFiles() {
        if (!FX_AVAILABLE) {
            return super.getSelectedFiles();
        }
        if (currentFiles == null) {
            return null;
        }
        return currentFiles.toArray(new File[currentFiles.size()]);
    }

    @Override
    public File getSelectedFile() {
        if (!FX_AVAILABLE) {
            return super.getSelectedFile();
        }
        return currentFile;
    }

    @Override
    public void setSelectedFiles(File[] selectedFiles) {
        if (!FX_AVAILABLE) {
            super.setSelectedFiles(selectedFiles);
            return;
        }
        if (selectedFiles == null || selectedFiles.length == 0) {
            currentFiles = null;
        } else {
            setSelectedFile(selectedFiles[0]);
            currentFiles = new ArrayList<>(Arrays.asList(selectedFiles));
        }
    }

    @Override
    public void setSelectedFile(File file) {
        if (!FX_AVAILABLE) {
            super.setSelectedFile(file);
            return;
        }
        currentFile = file;
        if (file != null) {
            if (file.isDirectory()) {
                fileChooser.setInitialDirectory(file.getAbsoluteFile());

                if (directoryChooser != null) {
                    directoryChooser.setInitialDirectory(file.getAbsoluteFile());
                }
            } else if (file.isFile()) {
                fileChooser.setInitialDirectory(file.getParentFile());
                fileChooser.setInitialFileName(file.getName());

                if (directoryChooser != null) {
                    directoryChooser.setInitialDirectory(file.getParentFile());
                }
            }

        }
    }

    @Override
    public void setFileSelectionMode(int mode) {
        super.setFileSelectionMode(mode);
        if (!FX_AVAILABLE) {
            return;
        }
        if (mode == DIRECTORIES_ONLY) {
            if (directoryChooser == null) {
                directoryChooser = new DirectoryChooser();
            }
            setSelectedFile(currentFile); // Set file again, so directory chooser will be affected by it
            setDialogTitle(getDialogTitle());
        }
    }

    @Override
    public void setDialogTitle(String dialogTitle) {
        if (!FX_AVAILABLE) {
            super.setDialogTitle(dialogTitle);
            return;
        }
        fileChooser.setTitle(dialogTitle);
        if (directoryChooser != null) {
            directoryChooser.setTitle(dialogTitle);
        }
    }

    @Override
    public String getDialogTitle() {
        if (!FX_AVAILABLE) {
            return super.getDialogTitle();
        }
        return fileChooser.getTitle();
    }

    @Override
    public void changeToParentDirectory() {
        if (!FX_AVAILABLE) {
            super.changeToParentDirectory();
            return;
        }
        File parentDir = fileChooser.getInitialDirectory().getParentFile();
        if (parentDir.isDirectory()) {
            fileChooser.setInitialDirectory(parentDir);
            if (directoryChooser != null) {
                directoryChooser.setInitialDirectory(parentDir);
            }
        }
    }

    @Override
    public void addChoosableFileFilter(FileFilter filter) {
        super.addChoosableFileFilter(filter);
        if (!FX_AVAILABLE || filter == null) {
            return;
        }
        if (filter instanceof FileNameExtensionFilter) {
            FileNameExtensionFilter f = (FileNameExtensionFilter) filter;

            List<String> ext = new ArrayList<>();
            for (String extension : f.getExtensions()) {
                ext.add(extension.replaceAll("^\\*?\\.?(.*)$", "*.$1"));
            }
            if (!ext.isEmpty()) {
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(f.getDescription(), ext));
            }
        } else if (filter instanceof VExtensionFileFilter) {
            VExtensionFileFilter f = (VExtensionFileFilter) filter;

            List<String> ext = new ArrayList<>();
            for (String extension : f.getExtensions()) {
                ext.add(extension.replaceAll("^\\*?\\.?(.*)$", "*.$1"));
            }
            if (!ext.isEmpty()) {
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(f.getDescription(), ext));
            }
        }
    }

    @Override
    public void setAcceptAllFileFilterUsed(boolean bool) {
        boolean differs = isAcceptAllFileFilterUsed() ^ bool;
        super.setAcceptAllFileFilterUsed(bool);
        if (!FX_AVAILABLE) {
            return;
        }
        if (differs) {
            if (bool) {
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files", "*.*"));
            } else {
                for (Iterator<FileChooser.ExtensionFilter> it = fileChooser.getExtensionFilters().iterator(); it.hasNext();) {
                    FileChooser.ExtensionFilter filter = it.next();
                    if (filter.getExtensions().contains("*.*")) {
                        it.remove();
                    }
                }
            }
        }
    }

    private void initFxFileChooser(File currentFile) {
        if (FX_AVAILABLE) {
            fileChooser = new FileChooser();
            this.currentFile = currentFile;
            setSelectedFile(currentFile);
        }
    }

}
