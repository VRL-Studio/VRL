/* 
 * RestrictedFileSystemView.java
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

package eu.mihosoft.vrl.io;

import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.filechooser.FileSystemView;

/**
 * Filesystem view that restricts the filesystem to the specified root
 * directory. It is designed to be used in conjuction with {@link javax.swing.JFileChooser}.
 * Its main purpose to restrict file dialogs to vrl project files.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class RestrictedFileSystemView extends FileSystemView {

    File root;
    File[] roots = new File[1];

    /**
     * Constructor.
     *
     * @param root root folder
     */
    public RestrictedFileSystemView(File root) {
        super();
        this.root = root;
        roots[0] = root;
    }

    @Override
    public File createNewFolder(File containingDir) {
        File folder = new File(containingDir, "New Folder");
        folder.mkdir();
        return folder;
    }

    @Override
    public File getDefaultDirectory() {
        return root;
    }

    @Override
    public File getHomeDirectory() {
        return root;
    }

    @Override
    public File[] getRoots() {
        return roots;
    }

    @Override
    public boolean isRoot(File file) {
        for (File r : roots) {
            if (r.equals(file)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean isTraversable(File f) {
        return f.getAbsolutePath().contains(root.getAbsolutePath());
    }

//    @Override
//    public boolean isHiddenFile(File f) {
//        return !isTraversable(f);
//    }

//    /**
//     * Gets the list of shown (i.e. not hidden) files.
//     */
//    public File[] getFiles(File dir, boolean useFileHiding) {
//        File[] files = super.getFiles(dir, useFileHiding);
//        
//        ArrayList<File> shownFiles = 
//    }
//    @Override
//    public String getSystemDisplayName(File f) {
//        
//        
//        
//        return f.getAbsolutePath().substring(root.getAbsolutePath().length());
//    }
//    @Override
//    public boolean isFileSystemRoot(File dir) {
//        return isRoot(dir);
//    }
//    
//     /**
//     * Returns the parent directory of <code>dir</code>.
//     * @param dir the <code>File</code> being queried
//     * @return the parent directory of <code>dir</code>, or
//     *   <code>null</code> if <code>dir</code> is <code>null</code>
//     */
//    @Override
//    public File getParentDirectory(File dir) {
//        if (dir == null || !dir.exists()) {
//            return null;
//        }
//        
//        File parent = dir.getParentFile();
//        
//        if (parent == null) {
//            return null;
//        }
//        
//        if (parent.getAbsolutePath().contains(root.getAbsolutePath())) {
//            return parent;
//        }
//        
//        return null;
//    }
}
