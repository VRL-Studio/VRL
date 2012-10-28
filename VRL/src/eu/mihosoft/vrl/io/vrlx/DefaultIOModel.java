/* 
 * DefaultIOModel.java
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

/**
 * Default IO Model.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
final class DefaultIOModel implements IOModel {

    private SessionEntryFactory entryFactory;

    /**
     * Constructor.
     *
     * @param entryFactory entry factory
     */
    public DefaultIOModel(SessionEntryFactory entryFactory) {

        if (entryFactory == null) {
            throw new IllegalArgumentException(
                    "Argument \"null\" not supported.");
        }

        this.entryFactory = entryFactory;
    }

    @Override
    public SessionFile newFile(String path) {
        return entryFactory.newFile(path);
    }

    @Override
    public SessionFile newFile(FileVersionInfo version, String path) {
        return entryFactory.newFile(version, path);
    }

    @Override
    public SessionFile loadFile(File f) throws IOException {
        return entryFactory.loadFile(f);
    }

    @Override
    public void saveFile(SessionFile file, File f)
            throws IOException {
        entryFactory.saveFile(file, f);
    }

    @Override
    public Object getFileContent(SessionEntryFolder file,
            String name, Class<?> clazz) {
        SessionEntry entry = file.getEntry(name);

        return getFileContent(entry, clazz);
    }

    @Override
    public Object getFileContent(SessionEntry entry, Class<?> clazz) {
        Object result = null;

        try {
            if (!entry.isFolder()) {
                SessionEntryFile entryFile = (SessionEntryFile) entry;
                if (clazz.isAssignableFrom(entryFile.getContent().getClass())) {
                    result = entryFile.getContent();
                }
            }
        } catch (Exception ex) {
            System.err.println(
                    ">> FileContent cannot be loaded!\n"
                    + " --> Returning null.\n"
                    + "  --> We proceed with loading...");
            ex.printStackTrace();
        }

        return result;
    }

    @Override
    public SessionEntryFolder getSubFolder(SessionEntryFolder folder,
            String name) {
        SessionEntryFolder result = null;

        SessionEntry entry = folder.getEntry(name);

        if (entry != null && entry.isFolder()) {
            result = (SessionEntryFolder) entry;
        }

        return result;
    }

    @Override
    public SessionEntryFile getSubFile(SessionEntryFolder folder,
            String name) {
        SessionEntryFile result = null;

        SessionEntry entry = folder.getEntry(name);

        if (entry != null && !entry.isFolder()) {
            result = (SessionEntryFile) entry;
        }

        return result;
    }

    @Override
    public SessionEntryFile asFile(SessionEntry entry) {
        SessionEntryFile result = null;

        if (!entry.isFolder()) {
            result = (SessionEntryFile) entry;
        }

        return result;
    }

    @Override
    public SessionEntryFile createFile(SessionEntryFolder parent,
            String path) throws FileNotFoundException {
        SessionEntryFile result = null;

        String folderNames = PathUtil.getParentPath(path);
        String fileName = PathUtil.getFileName(path);


        if (fileName.length() == 0) {
            throw new IllegalArgumentException("empty filename!");
        }

        SessionEntryFolder folder = getFolder(parent, folderNames);

        if (folder == null) {
            folder = createFolder(parent, folderNames);
        }

        if (folder.getEntry(fileName) != null) {
            throw new FileNotFoundException("cannot overwrite existing file!");
        } else {
            result = entryFactory.newEntryFile(fileName);
            folder.addEntry(result);
        }

        return result;
    }

    @Override
    public SessionEntryFolder getFolder(SessionEntryFolder parent,
            String path) {
        String[] pathEntries = PathUtil.splitPath(path);

        SessionEntryFolder folder = parent;

        // create folders if not existent
        for (int i = 0; i < pathEntries.length && parent != null; i++) {
            String folderName = pathEntries[i];

            folder =
                    getSubFolder(parent, folderName);

            parent = folder;
        }

        return folder;
    }

    @Override
    public SessionEntryFile getFile(SessionEntryFolder parent, String path) {
        SessionEntryFile result = null;

        SessionEntryFolder folder =
                getFolder(parent, PathUtil.getParentPath(path));

        if (folder != null) {
            result = getSubFile(folder, PathUtil.getFileName(path));
        }

        return result;
    }

    @Override
    public boolean exists(SessionEntryFolder parent, String path) {
        boolean result = false;

        result = getFolder(parent, path) != null
                || getFile(parent, path) != null;


        return result;
    }

    @Override
    public SessionEntryFolder createFolder(
            SessionEntryFolder parent, String path) {
        String[] pathEntries = PathUtil.splitPath(path);

        SessionEntryFolder folder = parent;

        for (int i = 0; i < pathEntries.length; i++) {
            String folderName = pathEntries[i];

            folder =
                    getSubFolder(parent, folderName);

            // create folder if not existent
            if (folder == null) {
                folder = entryFactory.newEntryFolder(folderName);
                parent.addEntry(folder);
            }

            parent = folder;
        }

        return folder;
    }

    @Override
    public SessionEntry getEntry(SessionEntryFolder parent, String path)
            throws FileNotFoundException {
        SessionEntry result = null;

        try {
            result = getFile(parent, path);
        } catch (Exception ex) {
            //
        } finally {
            if (result == null) {
                result = getFolder(parent, path);
            }
        }

        return result;
    }
}
