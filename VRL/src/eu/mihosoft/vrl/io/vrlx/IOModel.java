/* 
 * IOModel.java
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * IO model interface.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface IOModel {
    
    /**
     * Loads a session file from the specified location.
     * @param f location
     * @return session file
     */
    public SessionFile loadFile(File f) throws IOException;
    
    /**
     * Saves a session file to the specified location.
     * @param f location
     * @param file file to save
     */
    public void saveFile(SessionFile file, File f) throws IOException;

    /**
     * Returns a new session file.
     * @return a new session file
     */
    public SessionFile newFile(String path);

    /**
     * Returns a new session file.
     * @param version file version info
     * @return a new session file
     */
    public SessionFile newFile(FileVersionInfo version, String path);

    /**
     * Returns the content of a file that is content of the specified folder.
     * @param folder folder
     * @param name fila name
     * @param clazz class of the content object
     * @return the content of a file or <code>null</code> if no file or
     *         content object with the specified class object exists
     */
    public Object getFileContent(
            SessionEntryFolder folder, String name, Class<?> clazz);

    /**
     * Returns the content of a file.
     * @param entry file entry
     * @param clazz the class object of the content object
     * @return the content of a file or <code>null</code> if no content object
     *         with the specified class object exists
     */
    public Object getFileContent(
            SessionEntry entry, Class<?> clazz);

    /**
     * Returns a subfolder of a given folder.
     * @param folder folder
     * @param name subfolder name
     * @return a subfolder of a given folder or <code>null</code> if no folder
     *         with the specified name exists
     */
    public SessionEntryFolder getSubFolder(
            SessionEntryFolder folder, String name);

    /**
     * Returns a subfile of a given folder.
     * @param folder folder
     * @param name name of the file to return
     * @return a subfile of a given folder or <code>null</code> if no file
     *         with the specified name exists
     */
    public SessionEntryFile getSubFile(
            SessionEntryFolder folder, String name);

    /**
     * Returns a session entry as file.
     * @param entry session entry
     * @return the session entry as file or <code>null</code> if the entry is no
     *         file
     */
    public SessionEntryFile asFile(SessionEntry entry);

    /**
     * Creates a new entry file and returns it.
     * @param parent parent folder
     * @param path file path (relative to parent folder,
     *             folders will be created if necessary)
     * @return a new entry file
     * @throws FileNotFoundException if the file already exists this exception
     *                               will be thrown
     */
    public SessionEntryFile createFile(
            SessionEntryFolder parent, String path)
            throws FileNotFoundException;

    /**
     * Creates a new entry folder and returns it.
     * @param parent parent folder
     * @param path folder path (relative to parent folder,
     *             folders will be created if necessary)
     * @return a new entry folder
     */
    public SessionEntryFolder createFolder(
            SessionEntryFolder parent, String path)
            throws FileNotFoundException;

    /**
     * Returns a folder specifed by path (relative to parent folder).
     * @param parent parent folder
     * @param path folder path
     * @return the specified folder or <code>null</code>
     *         if the specified folder does not exist
     */
    public SessionEntryFolder getFolder(
            SessionEntryFolder parent, String path);
//            throws FileNotFoundException;

    /**
     * Returns a file specifed by path (relative to parent folder).
     * @param parent parent folder
     * @param path file path
     * @return the specified file or <code>null</code>
     *         if the specified file does not exist
     */
    public SessionEntryFile getFile(
            SessionEntryFolder parent, String path);
//            throws FileNotFoundException;

    /**
     * Indicates whether the specified entry exists.
     * @param parent parent folder
     * @param path entry path
     * @return <code>true</code> if the specified entry exists;
     *         <code>false</code> otherwise
     */
    public boolean exists(SessionEntryFolder parent, String path);

    /**
     * Returns a session entry specified by path (relative to parent folder).
     * @param parent parent folder
     * @param path entry path
     * @return the specified session entry
     * @throws FileNotFoundException
     */
    public SessionEntry getEntry(SessionEntryFolder parent, String path)
            throws FileNotFoundException;
}
