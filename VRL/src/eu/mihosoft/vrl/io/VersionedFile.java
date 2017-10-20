/* 
 * VersionedFile.java
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

import eu.mihosoft.vrl.io.vrlx.FileVersionInfo;
import eu.mihosoft.vrl.system.VParamUtil;
import eu.mihosoft.vrl.system.VRL;
import eu.mihosoft.vrl.system.VSysUtil;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.errors.UnmergedPathException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

/**
 * An archive file with internal version control support.<p><b>Purpose:</b> The
 * purpose of this class is to provide a simple and completely
 * platform-independent way to create archive files that can store different
 * versions of the contained data. Apart from that, this class allows to easily
 * store data in the versioned archive file without making it necessary to write
 * archive specific code.</p>
 *
 * <p><b>Note:</b> VersionedFile does currently not support incremental flushing
 * of changes. For large files this might be an issue as a complete copy of the
 * file is created temporarily for each call of
 * <code>flush()</code>.</p>
 *
 * <p><b>Warnings:</b> do not use multiple VersionedFile instances for
 * controlling the same file on the filesystem! </p>
 *
 * <p><b>Usage:</b> In the following example we create an archive file, add a
 * text file and create a few versions. After that we show how to checkout
 * different versions. Please note that the error handling in this example is
 * incorrect. For example, using try/catch/finally should be used (for closing
 * the writer and the versioned file). We didn't do it here to simplify the
 * example code. </p>
 *
 * <p><b>Example (Java code):</b></p>
 * <code>
 * <pre>
 * try {
 *     // create and open the file
 *     VersionedFile f =
 *             new VersionedFile(new File("project.vrlp")).create().open();
 *
 *     // prepare writing to a text file
 *     BufferedWriter writer = new BufferedWriter(
 *             new FileWriter(f.getContent().getPath() + "/file1.txt"));
 *
 *     // first version
 *     f.commit("empty file created");
 *
 *     // second version
 *     writer.write("NanoTime 1: " + System.nanoTime() + "\n");
 *     writer.flush();
 *     f.commit("timestamp added");
 *
 *     // third version
 *     writer.write("NanoTime 2: " + System.nanoTime() + "\n");
 *     writer.flush();
 *     f.commit("another timestamp added");
 *
 *     // finish writing
 *     writer.close();
 *
 *     // checkout latest/newest version
 *     f.checkoutLatestVersion();
 *
 *     // checkout previous versions one by one
 *     while (f.hasPreviousVersion()) {
 *         System.in.read(); // waiting for user input
 *         f.checkoutPreviousVersion();
 *     }
 *
 *     // finally, close the file
 *     f.close();
 *
 * } catch (IOException ex) {
 *     ex.printStackTrace(System.out);
 * }
 * </pre>
 * </code>
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public final class VersionedFile implements VersionController {

    /**
     * the archived file, e.g,
     * <code>file.zip</code>.
     */
    private File archiveFile;
    /**
     * temporary folder used to unpack and modify the archived file.
     */
    private File tmpFolder;
    /**
     * usually empty, except when using private constructor to load this file
     * again from archive for comparison reasons
     */
    private String tmpFolderPrefix = "";
    /**
     * available commits, i.e., versions.
     */
    private ArrayList<RevCommit> commits;
    /**
     * the version that is currently checked out
     */
    private int currentVersion = 0;
    /**
     * excluded paths (relative to tmpFolder)
     */
    private final Collection<String> excludedPaths =
            new ArrayList<String>();
    /**
     * endings of ignored file, e.g., .class
     */
    private String[] excludedEndings = new String[]{};
    /**
     * the name of the file-info file
     */
    private static final String FILE_INFO_NAME = ".versioned-file-info.xml";
    /**
     * version event listeners.
     */
    private Collection<VersionEventListener> versionEventListeners =
            new ArrayList<VersionEventListener>();
    private static Set<String> openedFiles = new HashSet<String>();
    // relevant for windows only
    private static Map<String, Integer> usedTmpFileIndices =
            new HashMap<String, Integer>();
    private boolean flushCommits = false;
    private ArchiveFormat archiveFormat;

    static {
        //
    }

    private void init() {
        getExcludedPaths().add(".git/");
        getExcludedPaths().add(FILE_INFO_NAME);
    }

    /**
     * Constructor.
     *
     * @param f file to open/create
     * @param af archive format
     */
    public VersionedFile(File f, ArchiveFormat af) {

        init();

        VParamUtil.throwIfNull(f, af);

        this.archiveFile = f;

        try {
            updateTmpFolder();
        } catch (IOException ex) {
            // should not happen as no previous tmp folder is present
            Logger.getLogger(VersionedFile.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        this.archiveFormat = af;
    }

    /**
     * Constructor. Uses default format (ZIP).
     *
     * @param f file to open/create
     */
    public VersionedFile(File f) {

        init();

        VParamUtil.throwIfNull(f);

        this.archiveFile = f;

        try {
            updateTmpFolder();
        } catch (IOException ex) {
            // should not happen as no previous tmp folder is present
            Logger.getLogger(VersionedFile.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        this.archiveFormat = new ZipFormat();
    }

    /**
     * Constructor.
     *
     * @param f file to open/create
     * @param af archive format
     */
    public VersionedFile(String fileName, ArchiveFormat af) {

        init();

        VParamUtil.throwIfNull(fileName, af);

        this.archiveFile = new File(fileName);
        try {
            updateTmpFolder();
        } catch (IOException ex) {
            // should not happen as no previous tmp folder is present
            Logger.getLogger(VersionedFile.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        this.archiveFormat = af;
    }

    /**
     * Updates the tmp folder. If the archiveFile has changed this method copies
     * the content of the original tmp folder to the new location.
     *
     * @throws IOException if copying failed
     */
    private void updateTmpFolder() throws IOException {
        File newTmpFolder = new File(getTmpFolderPath(this.getFile(),
                VRL.getPropertyFolderManager().
                toLocalPathInTmpFolder(this.getFile().getParentFile())));

        if (this.tmpFolder == null) {
            this.tmpFolder = newTmpFolder;
        } else if (!newTmpFolder.equals(this.tmpFolder)) {
            IOUtil.copyDirectory(tmpFolder, newTmpFolder);
        }

        this.tmpFolder = newTmpFolder;
    }

    /**
     * Constructor. Uses default format (ZIP).
     *
     * @param f file to open/create
     */
    public VersionedFile(String fileName) {

        init();

        this.archiveFile = new File(fileName);

        this.tmpFolder = new File(getTmpFolderPath(this.getFile(),
                VRL.getPropertyFolderManager().
                toLocalPathInTmpFolder(this.getFile().getParentFile())));

        archiveFormat = new ZipFormat();
    }

    /**
     * Constructor. There is only one valid use case in canClose()!
     *
     * @param f file to open/create
     * @param tmpFolderPrefix prefix
     * @param af archive format
     */
    private VersionedFile(File f, String tmpFolderPrefix, ArchiveFormat af) {

        init();

        VParamUtil.throwIfNull(f, tmpFolderPrefix, af);

        this.archiveFile = f;
        this.tmpFolderPrefix = tmpFolderPrefix;

        String parentPath = getFile().getParent();

        if (parentPath == null) {
            parentPath = "./";
        }

        this.tmpFolder = VRL.getPropertyFolderManager().toLocalPathInTmpFolder(
                new File(
                parentPath + "/"
                + tmpFolderPrefix
                + "/" + getTmpFolderName(f)));

        this.archiveFormat = af;
    }

    /**
     * Returns the content directory of this versioned file. Everything copied
     * to this location will be put under version control (except content that
     * matches one of the patterns in the .gitignore file).
     *
     * @return the content directory of this versioned file
     * @throws IllegalStateException if this file is currently not open
     */
    public File getContent() {

        if (!isOpened()) {
            throw new IllegalStateException(
                    "File \"" + getFile().getAbsolutePath()
                    + "\" not opened!");
        }

        return tmpFolder;
    }

    /**
     * Determines if the specified file exists.
     *
     * @param f the file to check
     * @return <code>true</code> if the specified file exists;
     * <code>false</code> otherwise
     *
     */
    public static boolean exists(File f) {

        VParamUtil.throwIfNotValid(
                VParamUtil.VALIDATOR_FILE, null, f);

        return f.exists();
    }

    /**
     * <p>Deletes the complete history of this file keeping only the latest
     * version, i.e., the version with the highest version number.</p>
     * <p><b>Warning:</b> Uncommited changes will be lost. This action cannot be
     * undone!</p>
     *
     * @throws IllegalStateException if this file is currently not open
     */
    @Override
    public void deleteHistory() throws IOException {

        System.out.println(">> delete history:");

        if (!isOpened()) {
            throw new IllegalStateException(
                    "File \"" + getFile().getAbsolutePath()
                    + "\" not opened!");
        }

        checkoutLatestVersion();

        initGit();

        commit("initial commit (cleared history)");
    }

    /**
     * Determines if this file is currently opened by checking whether the
     * content folder exists.
     *
     * @param f file to check
     * @return <code>true</code> if this file is currently opened;
     * <code>false</code> otherwise
     */
    public boolean isOpened() {
        return tmpFolder.isDirectory();
    }

    /**
     * Determines if this file is a valid versioned file. <p><b>Note:</b> if
     * this file is closed it will be opened temporarily to read the contained
     * file info. Thus, it should be used carefully to avoid unnecessary io
     * operations.</p>
     *
     * @return <code>true</code> if this file is valid; <code>false</code>
     * otherwise
     */
    public boolean isValid() {

        // if the file does not exist this file is clearly not valid
        if (!archiveFile.exists()) {
            return false;
        }

        // indicates current file state
        boolean wasOpened = isOpened();

        try {

            // open if was not opened before
            if (!wasOpened) {
                open();
            }

            boolean result = isValidWithoutOpen();

            // close file if we temporarily opened it
            if (!wasOpened) {
                close();
            }

            return result;

        } catch (IOException ex) {
            Logger.getLogger(VersionedFile.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        return false;
    }

    /**
     * Determines if this file is a valid versioned file. Does not open the
     * file.
     *
     * @return <code>true</code> if this file is valid; <code>false</code>
     * otherwise
     * @throws IllegalStateException if this file is currently not open
     */
    private boolean isValidWithoutOpen() {

        // file has to be opened
        if (!isOpened()) {
            throw new IllegalStateException("File not opened!");
        }

        try {

            // check whether file version info exists
            boolean result =
                    getFileInfo(tmpFolder) != null;

            return result;

        } catch (IOException ex) {
            Logger.getLogger(VersionedFile.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        return false;
    }

    /**
     * Creates this versioned file (creates an archive file on the file system).
     *
     * @return this versioned file
     * @throws IOException will be thrown if this file cannot be created. The
     * most likely cases for failure are: <ul> <li> this file already
     * exists</li> <li> the temporary content folder of this file already exists
     * (file not correctly closed last time?)</li> <li> the temporary content
     * folder of this file cannot be created</li> </ul>
     */
    public VersionedFile create() throws IOException {

        System.out.println(">> create file: " + getFile().getAbsolutePath());

        if (openedFiles.contains(getFile().getAbsolutePath())) {
            throw new IllegalStateException(
                    "File \"" + getFile().getPath() + "\" already opened!");
        }

        openedFiles.add(getFile().getAbsolutePath());

        if (getFile().exists()) {
            throw new IOException(
                    "File \""
                    + getFile().getAbsolutePath() + "\" already exists!");
        }

        if (tmpFolder.exists()) {
            throw new IOException("Folder \""
                    + tmpFolder.getAbsolutePath() + "\" already exists!");
        }

        if (!tmpFolder.mkdirs()) {
            throw new IOException("Folder \""
                    + tmpFolder.getAbsolutePath() + "\" cannot be created!");
        }

        // create version info to allow file identification
        // (used for validation)
        createFileInfo(tmpFolder);

        // create git repository
        initGit();

        try {
            // close the file after creation
            close();

            return this;
        } catch (Exception ex) {
            throw new IOException(
                    "File \"" + getFile().getPath()
                    + "\" cannot be created!", ex);
        }
    }

    /**
     * <p> Determines whether the history of the specified file is contained in
     * this file. </p> <p> <b>Note:</b> this method involves several io
     * intensive tasks and may be inefficient for large files. </p>
     *
     * @param f file to check
     * @return <code>true</code> if this file contains the history of the
     * specified file
     * @throws IllegalStateException if this file is currently not open
     */
    public boolean contains(VersionedFile f) {

        // file has to be opened
        if (!isOpened()) {
            throw new IllegalStateException("File not opened!");
        }

        if (f == null) {
            return false;
        }

        // if our history is shorter we can't contain the history
        // of f
        if (getNumberOfVersions() < f.getNumberOfVersions()) {
            return false;
        }

        // histories to compare
        ArrayList<RevCommit> ours = null;
        ArrayList<RevCommit> theirs = null;

        try {
            ours = getVersions();
            theirs = f.getVersions();
        } catch (IOException ex) {
            Logger.getLogger(VersionedFile.class.getName()).
                    log(Level.SEVERE, null, ex);
            return false;
        }

        // check whether we contain the full history of f
        for (int i = 0; i < theirs.size(); i++) {
            boolean found = false;
            for (int j = 0; j < ours.size(); j++) {
                if (theirs.get(i).getName().
                        equals(ours.get(i).getName())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                return false;
            }
        }

        return true;
    }

    /**
     * Creates a version info file in the specified directory.
     *
     * @param contentDir target location
     * @throws IOException
     */
    private static void createFileInfo(File contentDir)
            throws IOException {

        File versionInfo =
                new File(contentDir.getPath() + "/" + FILE_INFO_NAME);

        XMLEncoder e = null;

        try {
            e = new XMLEncoder(
                    new BufferedOutputStream(
                    new FileOutputStream(versionInfo)));

            e.writeObject(new VersionedFileInfo(
                    new FileVersionInfo("0.1", "versioned file")));
        } catch (IOException ex) {
            throw new IOException(ex);
        } finally {
            if (e != null) {
                e.close();
            }
        }
    }

    /**
     * Returns the version info of this file.
     *
     * @return the version info of this file or <code>null</code> if no version
     * info exists
     * @throws IllegalStateException if this file is currently not open
     */
    public VersionedFileInfo getFileInfo() {
        // file has to be opened
        if (!isOpened()) {
            throw new IllegalStateException("File not opened!");
        }

        try {
            return getFileInfo(tmpFolder);
        } catch (IOException ex) {
            Logger.getLogger(VersionedFile.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Returns the version info from the specified location.
     *
     * @param contentDir location
     * @return the version info from the specified location or <code>null</code>
     * if no version info exists at the specified location
     * @throws IOException
     * @throws IllegalStateException if this file is currently not open
     */
    private static VersionedFileInfo getFileInfo(File contentDir)
            throws IOException {

        // file has to be opened
        if (!contentDir.isDirectory()) {
            throw new IllegalStateException("File not opened!");
        }

        File versionInfo =
                new File(contentDir.getPath() + "/" + FILE_INFO_NAME);


        // stop here if the version info file does not exists
        if (!versionInfo.exists()) {
            return null;
        }

        // decode the version info
        XMLDecoder d = null;

        try {
            d = new XMLDecoder(
                    new BufferedInputStream(
                    new FileInputStream(versionInfo)));

            Object result = d.readObject();

            if (!(result instanceof VersionedFileInfo)) {
                throw new IOException("The file \""
                        + versionInfo.getPath()
                        + "\" does not contain a valid file info");
            }

            return (VersionedFileInfo) result;

        } catch (Exception ex) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            }
        } finally {
            if (d != null) {
                d.close();
            }
        }

        // no version info found
        return null;
    }

    /**
     * Deletes all files and folders contained in the content directory of this
     * file (excludes all paths specified in excludes).
     *
     * @throws IllegalStateException if this file is currently not open
     */
    private void deleteAllCheckedOutFiles() {

        // file has to be opened
        if (!isOpened()) {
            throw new IllegalStateException(
                    "File \"" + getFile().getPath() + "\" not opened!");
        }

        List<File> excludes = new ArrayList<File>();

        for (String p : getExcludedPaths()) {
            excludes.add(new File(tmpFolder.getPath() + "/" + p));
//            System.out.println(excludes.get(excludes.size()-1) + ": " + 
//                    excludes.get(excludes.size()-1).exists());
        }

        // add files ending with an ending from excludedEndings to excluded paths 
        Collection<File> clsFiles =
                IOUtil.listFiles(getContent(), getExcludedEndings());

        excludes.addAll(clsFiles);

        IOUtil.deleteContainedFilesAndDirs(tmpFolder, excludes);
    }

    /**
     * Checks out the specified commit from the git repository.
     *
     * @param commit commit to checkout
     * @return this file
     * @throws IOException
     * @throws IllegalStateException if this file is currently not open
     */
    private VersionedFile checkoutVersion(RevCommit commit) throws IOException {

        // pre event
        for (VersionEventListener l : versionEventListeners) {
            l.preCheckout(commit);
        }

        // file has to be opened
        if (!isOpened()) {
            throw new IllegalStateException(
                    "File \"" + getFile().getPath() + "\" not opened!");
        }

        // git repository has conflicts. should NEVER happen!
        if (hasConflicts()) {
            throw new IllegalStateException(
                    "File \"" + getFile().getPath() + "\" has conflicts!");
        }

        try {

            // delete files currently checked out
            deleteAllCheckedOutFiles();

            // checkout all files of the specified commit
            checkoutFilesInVersion(commit);

        } catch (JGitInternalException ex) {
            throw new IOException("Git exception", ex);
        }

        // post event
        for (VersionEventListener l : versionEventListeners) {
            l.postCheckout(commit);
        }

        return this;
    }

    /**
     * Opens this file and optionally checks out the latest version.
     *
     * @param checkoutLastest defines whether to check out the latest version
     * @return this file
     * @throws IOException
     * @throws IllegalStateException if this file is already open
     */
    public VersionedFile open(boolean checkoutLastest) throws IOException {

        System.out.println(">> open file: " + getFile().getAbsolutePath());

        if (openedFiles.contains(getFile().getAbsolutePath())) {
            throw new IllegalStateException(
                    "File \"" + getFile().getPath() + "\" already opened!");
        }

        openedFiles.add(getFile().getAbsolutePath());

        // check whether this file exists
        if (!archiveFile.exists()) {
            throw new FileNotFoundException(
                    "File \"" + getFile().getPath() + "\" does not exist!");
        }


        // file has to be opened
        if (isOpened()) {

            String msg =
                    ">> File \"" + getFile().getPath() + "\" already opened!";

            // os specific behavior is ugly. but we can't implement it
            // consistently because windows filelocks are mandatory. that means
            // that every plugin that does incorrectly implement io related
            // operations could potentially destroy project file consistency.
            // but we take care that each VersionedFile instance uses its own
            // tmp directory.
            if (VSysUtil.isWindows()) {
                msg += "\n --> Running on Windows. Maybe filelocking prevented"
                        + " file deletion. Temporary files will be removed on"
                        + " JVM shutdown.";
                System.err.println(msg);
            } else {
                // on os x and linux we use strict checking
                throw new IllegalStateException(msg);
            }

            return this;
        }

        // unzip the archive file to its parent directory
        File parent = getFile().getParentFile();


        if (parent == null) {
            parent = new File("./");
        }

        // we do everything relative to the vrl property folder
        parent = VRL.getPropertyFolderManager().toLocalPathInTmpFolder(parent);

        // we add this suffix in case we try to open this file a second time
        // for comparison reasons
        String parentPath =
                parent + "/" + tmpFolderPrefix + "/" + tmpFolder.getName();

        try {

            if (!archiveFormat.unpack(getFile(), new File(parentPath))) {
                throw new IOException("Could not unpack archive: " + getFile());
            }

            if (!isValidWithoutOpen()) {
                rmTmpFolder();
                throw new IOException(
                        "File \"" + getFile().getPath()
                        + "\" is no valid versioned file."
                        + " File info missing or damaged!");
            }

            // checkout latest version to ensure consistency
            if (checkoutLastest) {
                checkoutLatestVersion();
            }

            return this;

        } catch (Exception ex) {
            throw new IOException(
                    "File \"" + getFile().getPath()
                    + "\" cannot be opened!", ex);
        }
    }

    /**
     * Opens this file and checks out the latest version.
     *
     * @return this file
     * @throws IOException
     * @throws IllegalStateException if this file is already open
     */
    public VersionedFile open() throws IOException {
        return open(true);
    }

    /**
     * Checkout latest version, i.e., version with highest version number.
     *
     * @throws IOException
     */
    @Override
    public void checkoutLatestVersion() throws IOException {
        if (getNumberOfVersions() > 1) {
            checkoutVersion(Math.max(getNumberOfVersions() - 1, 1));
        }
    }

    /**
     * Checkout first version, i.e., version 1.
     *
     * @throws IOException
     */
    @Override
    public void checkoutFirstVersion() throws IOException {
        if (getNumberOfVersions() > 1) {
            checkoutVersion(1);
        }
    }

    /**
     * <p> Cleans up this file. That is, it closes this file to ensure that no
     * dirty content directory exists. This method should be used if this file
     * has not been closed after the last usage. </p> <p> <b>Note:</b> this
     * method checks whether the content directory contains the history of the
     * archive file to be overwritten to prevent data loss. It throws an
     * {@link IOException} if this is not the case. </p>
     *
     * @return this file
     */
    public VersionedFile cleanup() throws IOException {

        // if thid file is not opened we do nothing
        if (!isOpened()) {
            return this;
        }

        try {
            if (canClose()) {
                close();
            } else {
                throw new IOException(
                        "Cannot flush to \"" + getFile().getPath()
                        + "\" because the temporary content dir does not"
                        + " contain the history of the archive!");
            }
        } catch (IOException ex) {
            throw new IOException(
                    "File \"" + getFile().getPath()
                    + "\" cannot be closed!", ex);
        }

        return this;
    }

    /**
     * Indicates whether this file can be closed without loosing data. It checks
     * if the file does contain the full history of the archive file to be
     * overwritten.
     *
     * @return <code>true</code> if this file can be closed without loosing
     * information; <code>false</code> otherwise
     */
    private boolean canClose() {

        // relevant for windows only:
        // if this is true it means that we have opened the file before and it
        // can be safely reused under the assumption that the file was not
        // modified while running the current JVM session
        if (VSysUtil.isWindows() && usedTmpFileIndices.containsKey(
                getFile().getAbsolutePath())) {
            return true;
        }

        try {
            // if the tmp folder already exists ensure that the tmpfolder we
            // have to create for comparison does not already exist. we are
            // not very patient and only try 10 times and give up afterwards.
            String dirPrefix = "";
            boolean canCreateSecondTmpDir = false;
            for (int i = 0; i < 10; i++) {
                dirPrefix = "." + UUID.randomUUID().toString();
                String tmpFolderName = tmpFolder.getName() + dirPrefix;
                if (!new File(tmpFolderName).exists()) {
                    canCreateSecondTmpDir = true;
                    break; // we made it
                }
            }

            // comparison must fail. this case shouldn't ever happen.
            // but you never know...
            if (!canCreateSecondTmpDir) {
                return false;
            }

            // the unmodified version from the archive
            VersionedFile originalFromArchive = null;

            // dirty version from the pre existing content dir
            VersionedFile dirtyVersion = this;

            try {
                originalFromArchive =
                        new VersionedFile(
                        getFile(), dirPrefix, archiveFormat).open();
            } catch (IOException ex) {
                Logger.getLogger(VersionedFile.class.getName()).
                        log(Level.SEVERE, null, ex);

                if (originalFromArchive != null) {
                    originalFromArchive.close();
                }
            }

            // now we check whether our dirty version contains the history of 
            // the original file from the archive. If so, we can safely 
            // overwrite the archive
            if (isValidWithoutOpen()
                    && dirtyVersion.contains(originalFromArchive)) {
                originalFromArchive.close();
                return true;
            } else {
                originalFromArchive.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(VersionedFile.class.getName()).
                    log(Level.SEVERE, null, ex);
            return false;
        }

        return false;
    }

    /**
     * Returns names of all files that contain uncommitted changes that match
     * the specified endings.
     *
     * @param endings endings, e.g., ".java" or ".txt"
     * @return names of all files that contain uncommitted changes that match
     * the specified endings
     */
    public Set<String> getUncommittedChanges(String... endings) {
        Set<String> allChanges = getUncommittedChanges();

        Set<String> result = new HashSet<String>();

        for (String s : allChanges) {

            for (String ending : endings) {
                if (s.endsWith(ending)) {
                    result.add(s);
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Returns names of all files that contain uncommitted changes.
     *
     * @return names of all files that contain uncommitted changes
     */
    public Set<String> getUncommittedChanges() {
        // file has to be opened
        if (!isOpened()) {
            throw new IllegalStateException(
                    "File \"" + getFile().getPath() + "\" not opened!");
        }

        Set<String> result = new HashSet<String>();

        Git git = null;

        try {
            git = Git.open(tmpFolder);

            Status status = git.status().call();



            for (String s : status.getAdded()) {
                result.add(s);
            }

            for (String s : status.getChanged()) {
                result.add(s);
            }

            for (String s : status.getMissing()) {
                result.add(s);
            }

            for (String s : status.getModified()) {
                result.add(s);
            }

            for (String s : status.getRemoved()) {
                result.add(s);
            }

            for (String s : status.getUntracked()) {
                result.add(s);
            }

        } catch (UnmergedPathException ex) {
            ex.printStackTrace(System.err);
            closeGit(git);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            closeGit(git);
        }

        return result;
    }

    /**
     * Determines whether this file has been changed and needs a commit to store
     * these changes. <p> <b>Note:</b> if a version other than the latest
     * version has been checked out this method will treat this as content
     * change. </p>
     *
     * @return <code>true</code> if uncommited changes exist; <code>false</code>
     * otherwise
     */
    public boolean hasUncommittedChanges() {

        Set<String> changes = getUncommittedChanges();

//        for (String string : changes) {
//            System.out.println("M: " + string);
//        }

        return !changes.isEmpty();
    }

    /**
     * Closes the git repository.
     *
     * @param git git repository to close
     */
    private void closeGit(Git git) {
        if (git != null) {
            git.getRepository().close();
        }
    }

    /**
     * Determines if this file has conflicts.
     *
     * @return <code>true</code> if conflicts exist; <code>false</code>
     * otherwise
     * @throws IOException
     * @throws IllegalStateException if this file is currently not open
     */
    private boolean hasConflicts() throws IOException {

        // file has to be opened
        if (!isOpened()) {
            throw new IllegalStateException(
                    "File \"" + getFile().getPath() + "\" not opened!");
        }

        Git git = null;

        try {
            git = Git.open(tmpFolder);

            Status status = git.status().call();

            closeGit(git);

            return !status.getConflicting().isEmpty();

        } catch (UnmergedPathException ex) {
            closeGit(git);
            throw new IOException("Git exception", ex);
        } catch (IOException ex) {
            closeGit(git);
            throw new IOException("Git exception", ex);
        }
    }

    /**
     * Commit file changes. IF flushing for commits is enabled changes will be
     * flushed.
     *
     * @param message commit message
     * @return this file
     * @throws IOException
     * @throws IllegalStateException if this file is currently not open
     */
    public VersionedFile commit(String message)
            throws IOException {

        // file has to be opened
        if (!isOpened()) {
            throw new IllegalStateException(
                    "File\"" + getFile().getPath() + "\" not opened!");
        }

        Git git = null;

        try {

//             this should NEVER happen
            if (hasConflicts()) {
                throw new IllegalStateException(
                        "File \"" + getFile().getPath()
                        + "\" has conflicts!");
            }

            // ensures that message is not null
            if (message == null || message.isEmpty()) {
                message = "no message";
            }

            System.out.print(">> commit version ");

            // open the git repository
            git = Git.open(tmpFolder);

            // retrieve the current git status
            Status status = git.status().call();

            // rm command to tell git to remove files
            RmCommand rm = git.rm();

            boolean needsRM = false;

            // checks whether rm is necessary and adds relevant paths
            for (String removedFile : status.getMissing()) {
                rm.addFilepattern(removedFile);
                needsRM = true;
            }

            // calls the rm command if necessary
            if (needsRM) {
                rm.call();
            }

            // adds all remaining files
            git.add().addFilepattern(".").call();

            // perform the commit
            git.commit().setMessage(message).
                    setAuthor(System.getProperty("user.name"), "?").call();

            commits = null;

            // updates the current version number
            currentVersion = getNumberOfVersions() - 1;

            System.out.println(currentVersion + ": ");
            System.out.println(">>> commit-id (SHA-1): "
                    + getVersions().get(currentVersion).getName());

            if (isFlushCommits()) {
                flush();
            }

            closeGit(git);

            return this;

        } catch (NoFilepatternException ex) {
            closeGit(git);
            throw new IOException("Git exception", ex);
        } catch (NoHeadException ex) {
            closeGit(git);
            throw new IOException("Git exception", ex);
        } catch (NoMessageException ex) {
            closeGit(git);
            throw new IOException("Git exception", ex);
        } catch (UnmergedPathException ex) {
            closeGit(git);
            throw new IOException("Git exception", ex);
        } catch (ConcurrentRefUpdateException ex) {
            closeGit(git);
            throw new IOException("Git exception", ex);
        } catch (JGitInternalException ex) {
            closeGit(git);
            throw new IOException("Git exception", ex);
        } catch (WrongRepositoryStateException ex) {
            closeGit(git);
            throw new IOException("Git exception", ex);
        } catch (IOException ex) {
            closeGit(git);
            throw new IOException("Git exception", ex);
        }
    }

    /**
     * Returns the number of versions.
     *
     * @return the number of versions or <code>-1</code> if an error occured
     * @throws IllegalStateException if this file is currently not open
     */
    @Override
    public int getNumberOfVersions() {

        // file has to be opened
        if (!isOpened()) {
            throw new IllegalStateException(
                    "File\"" + getFile().getPath() + "\" not opened!");
        }
        try {
            return getVersions().size();
        } catch (IOException ex) {
            Logger.getLogger(VersionedFile.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        return -1;
    }

    /**
     * Returns the number of the current version.
     *
     * @return the number of the current version
     * @throws IllegalStateException if this file is currently not open
     */
    @Override
    public int getCurrentVersion() {
        // file has to be opened
        if (!isOpened()) {
            throw new IllegalStateException(
                    "File\"" + getFile().getPath() + "\" not opened!");
        }

        return currentVersion;
    }

    /**
     * Determines whether a version with version number
     * <code>currentVersion+1</code> exists.
     *
     * @return <code>true</code> if a next version exists
     * @throws IllegalStateException if this file is currently not open
     */
    @Override
    public boolean hasNextVersion() {
        // file has to be opened
        if (!isOpened()) {
            throw new IllegalStateException(
                    "File\"" + getFile().getPath() + "\" not opened!");
        }

        return currentVersion < getNumberOfVersions() - 1;
    }

    /**
     * Determines whether a version with version number
     * <code>currentVersion-1</code> exists. Version counting starts with
     * <code>1</code>. Version
     * <code>0</code> is for internal use only and cannot be accessed.
     *
     * @return <code>true</code> if a previous version exists
     * @throws IllegalStateException if this file is currently not open
     */
    @Override
    public boolean hasPreviousVersion() {
        // file has to be opened
        if (!isOpened()) {
            throw new IllegalStateException(
                    "File\"" + getFile().getPath() + "\" not opened!");
        }

        return currentVersion > 1;
    }

    /**
     * Checks out previous version. Throws an {@link IllegalStateException} if
     * if such a version does not exist.
     *
     * @throws IOException
     * @throws IllegalStateException if the specified version does not exist
     */
    @Override
    public void checkoutPreviousVersion() throws IOException {
        if (hasPreviousVersion()) {
            currentVersion--;
            checkoutVersion(currentVersion);
        } else {
            throw new IllegalStateException("No previous version available!");
        }
    }

    /**
     * Checks out next version. Throws an {@link IllegalStateException} if if
     * such a version does not exist.
     *
     * @throws IOException
     * @throws IllegalStateException if the specified version does not exist
     */
    @Override
    public void checkoutNextVersion() throws IOException {
        if (hasNextVersion()) {
            currentVersion++;
            checkoutVersion(currentVersion);
        } else {
            throw new IllegalStateException("No next version available!");
        }
    }

    /**
     * Returns a list containing the paths to all files in the specified
     * version.
     *
     * @param c version identifier (commit)
     * @return a list containing the paths to all files in the specified version
     * @throws IllegalStateException if this file is currently not open
     */
    private Collection<String> getFilesInVersion(RevCommit c) {

        Collection<String> result = new ArrayList<String>();

        // file has to be opened
        if (!isOpened()) {
            throw new IllegalStateException(
                    "File\"" + getFile().getPath() + "\" not opened!");
        }

        Git git = null;

        try {
            git = Git.open(tmpFolder);
            // create a tree walk to search for files
            TreeWalk walk = new TreeWalk(git.getRepository());
            if (walk != null) {

                // recursively search fo files
                walk.setRecursive(true);
                // add the tree the specified commit belongs to
                walk.addTree(c.getTree());

                // walk through the tree
                while (walk.next()) {

                    // TODO: is it a problem if mode is treemode?
                    final FileMode mode = walk.getFileMode(0);
                    if (mode == FileMode.TREE) {
                        System.out.print(
                                "VersionedFile."
                                + "getFilesInVersion(): FileMode unexpected!");
                    }

                    // retrieve the path name of the current element
                    String fileName = walk.getPathString();

                    // we do not want to commit/checkout this file
                    if (!fileName.equals(FILE_INFO_NAME)) {
                        result.add(walk.getPathString());
                    }
                }
            }

        } catch (IOException ex) {
            closeGit(git);
            Logger.getLogger(VersionedFile.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        closeGit(git);

        return result;

    }

    /**
     * Checks out all files in the specified version.
     *
     * @param c version identifier (commit)
     * @throws IllegalStateException if this file is currently not open
     */
    private void checkoutFilesInVersion(RevCommit c) {

        // file has to be opened
        if (!isOpened()) {
            throw new IllegalStateException(
                    "File\"" + getFile().getPath() + "\" not opened!");
        }

        System.out.println(">>> commit-id (SHA-1): " + c.getName());

        Git git = null;

        TreeWalk walk = null;

        try {

            git = Git.open(tmpFolder);

            // create a tree walk to search for files.
            walk = new TreeWalk(git.getRepository());
            if (walk != null) {
                // recursively search fo files
                walk.setRecursive(true);
                // add the tree the specified commit belongs to
                walk.addTree(c.getTree());

                while (walk.next()) {

                    // TODO: is it a problem if mode is treemode?
                    final FileMode mode = walk.getFileMode(0);
                    if (mode == FileMode.TREE) {
                        System.out.print('0');
                    }

                    String fileName = walk.getPathString();

                    if (!fileName.equals(FILE_INFO_NAME)) {
                        // checks out the current file
                        checkoutFile(fileName, walk.getObjectId(0));
                    }
                }
            }

        } catch (IOException ex) {
            closeGit(git);
            Logger.getLogger(VersionedFile.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        closeGit(git);
    }

    /**
     * Checks out the specified file (it is necessary to specifiy the file name
     * and the object id).
     *
     * @param fileName file name (path relative to content dir)
     * @param id object id of this file
     * @throws IOException
     * @throws IllegalStateException if this file is currently not open
     */
    private void checkoutFile(String fileName, ObjectId id) throws IOException {

        // file has to be opened
        if (!isOpened()) {
            throw new IllegalStateException(
                    "File\"" + getFile().getPath() + "\" not opened!");
        }

        System.out.println(">>> checkout file: " + fileName);

        // file that shall be checked out
        File checkoutFile = new File(tmpFolder.getAbsolutePath()
                + "/" + fileName);

        // the parent directory of the file to be checked out
        File parentDirectory = checkoutFile.getParentFile();

        // create the parent directory if it is not the content directory to
        // allow the output stream to save the file there
        if (parentDirectory != null && !parentDirectory.equals(tmpFolder)) {
            parentDirectory.mkdirs();
        }

        BufferedOutputStream out = null;

        Git git = null;

        try {

            git = Git.open(tmpFolder);

            // checkout the file via an object loader
            ObjectLoader loader = git.getRepository().open(id);
            out = new BufferedOutputStream(new FileOutputStream(checkoutFile));

            loader.copyTo(out);

            closeGit(git);

        } catch (IOException ex) {
            closeGit(git);
            Logger.getLogger(VersionedFile.class.getName()).
                    log(Level.SEVERE, null, ex);
        } finally {
            closeGit(git);
            // we are responsible to close the stream
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * Checks out the specified version.
     *
     * @param i version to checkout (version counting starts with
     * <code>1</code>)
     * @return this file
     * @throws IOException
     * @throws IllegalArgumentException if the specified version does not exist
     */
    @Override
    public void checkoutVersion(int i) throws IOException {

        if (i > getNumberOfVersions() - 1) {
            throw new IllegalArgumentException(
                    "Version " + i + " not available!");
        } else if (i < 1) {
            throw new IllegalArgumentException(
                    "Illegal version index:"
                    + " values less than 1 are not supported."
                    + " Version 0 is for internal usage only!");
        }

        System.out.println(">> checkout version " + i + ":");

        checkoutVersion(getVersions().get(i));
        currentVersion = i;
    }

    /**
     * Returns a list containing commit objects of all versions. This method can
     * be used to show the version messages, e.g., for creating a ui that does
     * allow the selection of the version that shall be checked out.
     *
     * @return a list containing commit objects of all versions
     * @throws IOException
     * @throws IllegalStateException if this file is currently not open
     */
    @Override
    public ArrayList<RevCommit> getVersions() throws IOException {

        // use cached results if possible
        if (commits != null) {
            return commits;
        }

        // file has to be opened
        if (!isOpened()) {
            throw new IllegalStateException(
                    "File\"" + getFile().getPath() + "\" not opened!");
        }

        RevWalk walk = null;

        Git git = null;

        try {

            // open the git repository
            git = Git.open(tmpFolder);
            walk = new RevWalk(git.getRepository());

            // retrieve the object id of the current HEAD version
            // (latest/youngest version)
            ObjectId headId = git.getRepository().resolve(Constants.HEAD);

            // tell the walk to start from HEAD
            walk.markStart(walk.parseCommit(headId));

            // change sorting order
            walk.sort(RevSort.TOPO, true);
            walk.sort(RevSort.REVERSE, true);

            commits = new ArrayList<RevCommit>();

            // walk through all versions and add them to the list
            for (RevCommit commit : walk) {
                commits.add(commit);
            }

            closeGit(git);

        } catch (IOException ex) {
            throw new IOException("Git exception", ex);
        } finally {
            closeGit(git);
            // we are responsible for disposing the walk object
            if (walk != null) {
                walk.dispose();
            }
        }

        return commits;
    }

    /**
     * Switches this versioned file to a new archive location. This method
     * implies copying of the tmp folder and one additional flushing to the new
     * archive.
     *
     * @param dest new archive destination
     * @throws IOException if switchin is not possible
     */
    public void switchToNewArchive(File dest) throws IOException {

        System.out.println(">> Switching archive:");
        System.out.println(" --> from: " + archiveFile);
        System.out.println(" --> to  : " + dest);

        // keep the old folder location
        File oldTmpFolder = tmpFolder;

        boolean canSwitch = false;
        IOException exception = null;

        File oldArchiveFile = archiveFile;

        try {
            archiveFile = dest;
            updateTmpFolder();
            canSwitch = true;
        } catch (IOException ex) {
            exception = ex;

            Logger.getLogger(VersionedFile.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        if (canSwitch) {
            openedFiles.remove(oldArchiveFile.getAbsolutePath());

            // delete old tmp folder
            boolean canDelete = IOUtil.deleteDirectory(oldTmpFolder);

            if (!canDelete) {
                System.out.println(
                        " --> cannot delete old tmp folder: " + oldTmpFolder);
            }

        } else {
            archiveFile = oldArchiveFile;
            throw new IOException(
                    "Cannot switch to new archive: " + dest, exception);
        }

        flush();
    }

    /**
     * <p> Flushes this file. This method saves the current state of the content
     * folder of this file in the archive file. The previous archive file will
     * be backed up to
     * <code>filename~</code> before it will be overwritten. This method shall
     * be used to ensure the content directory and the archive file are in sync.
     * </p> <p> <b>Note:</b> this method may cause performance problems when
     * working with large files. Incremental flushing is currently unsupported.
     * On the other hand, calling this method after content changes decreases
     * the chance of data loss. </p>
     *
     * @return this file
     * @throws IOException
     */
    public VersionedFile flush() throws IOException {

        // file has to be opened
        if (!isOpened()) {
            return this;
        }

        System.out.println(">> project:" + getFile());
        System.out.println(" --> flushing project...");

        try {
            // backup archive file if it already exists to prevent data loss
            if (getFile().exists()) {
                IOUtil.copyFile(
                        getFile(),
                        VRL.getPropertyFolderManager().toLocalPathInTmpFolder(
                        new File(getFile().getAbsolutePath() + "~")));
            }

            // determine parent directory of the archive file
            String parentPath = getFile().getParent();

            if (parentPath == null) {
                parentPath = "./";
            }

            Collection<String> endings = new ArrayList<String>();

            endings.add(".git");
            endings.add(".gitignore");
            endings.add(FILE_INFO_NAME);
            endings.add(".class");
            endings.add("MANIFEST.MF");
            endings.add("vproject-info.xml");
     
            endings.addAll(getExcludedPaths()); // check VProject.initGitIgnore()

            if (!archiveFormat.packContentsOfFolder(
                    tmpFolder,
                    new File(parentPath + "/" + getFile().getName()),
                    endings.toArray(new String[endings.size()]))) {
                throw new IOException("Could not pack archive: " + getFile());
            }

//            if (!archiveFormat.packContentsOfFolder(
//                    tmpFolder,
//                    new File(parentPath + "/" + getFile().getName()))) {
//                throw new IOException("Could not pack archive: " + getFile());
//            }

//            IOUtil.zipContentOfFolder(
//                    tmpFolder.getAbsolutePath(),
//                    parentPath + "/" + getFile().getName());

        } catch (IOException ex) {
            throw new IOException(
                    "File \"" + getFile().getAbsolutePath()
                    + "\" cannot be created!", ex);
        }

        System.out.println(" --> done.");

        return this;
    }

    /**
     * <b>EXPERIMENTAL!</b> <br> <p> Flushes this file to a custom destination.
     * This method saves the current state of the content folder of this file in
     * the specified custom archive file. Existing files will be silently
     * overwritten.
     *
     * This method shall be used to ensure the content directory and the archive
     * file are in sync. </p> <p> <b>Note:</b> this method may cause performance
     * problems when working with large files. Incremental flushing is currently
     * unsupported. On the other hand, calling this method after content changes
     * decreases the chance of data loss. </p>
     *
     * @return this file
     * @throws IOException
     */
    private VersionedFile flush(File dest) throws IOException {

        // file has to be opened
        if (!isOpened()) {
            return this;
        }

        try {
            if (!archiveFormat.packContentsOfFolder(
                    tmpFolder,
                    dest,
                    ".git", ".gitignore", FILE_INFO_NAME,
                    ".class", "MANIFEST.MF", "vproject-info.xml")) {
                throw new IOException("Could not pack archive: " + getFile());
            }

//            if (!archiveFormat.packContentsOfFolder(
//                    tmpFolder,
//                    new File(parentPath + "/" + getFile().getName()))) {
//                throw new IOException("Could not pack archive: " + getFile());
//            }

//            IOUtil.zipContentOfFolder(
//                    tmpFolder.getAbsolutePath(),
//                    parentPath + "/" + getFile().getName());

        } catch (IOException ex) {
            throw new IOException(
                    "File \"" + getFile().getAbsolutePath()
                    + "\" cannot be created!", ex);
        }

        return this;
    }

    /**
     * Closes this file. This method flushes all changes to the archive file and
     * removes the temporary content directory.
     *
     * @return this file
     * @throws IOException
     */
    public VersionedFile close() throws IOException {

        System.out.println(">> close file: " + getFile().getAbsolutePath());

        openedFiles.remove(getFile().getAbsolutePath());

        // if thid file is not opened we do nothing
        if (!isOpened()) {
            return this;
        }

        try {
            flush();
        } catch (IOException ex) {
            throw new IOException(
                    "File \"" + getFile().getAbsolutePath()
                    + "\" cannot be closed because flushing failed!", ex);
        }

        // we do not want to leave the temporary content folder opened.
        // thus, we delete it
        rmTmpFolder();

        return this;
    }

    /**
     * Returns the path of the temporary content folder of the specified file.
     * If the
     * <code>tmpFolderLocation</code> is
     * <code>null</code> the parent of the specified file
     * <code>f</code> will be used as location. <p><b>Note:</b> only use this
     * method if it is clear that no prefix is used.</p>
     *
     * @param f the file
     * @param tmpFolderLocation the location of the tmp folder (optional, may be
     * null)
     * @return the name of the temporary content folder of the specified file
     */
    private static String getTmpFolderPath(File f, File tmpFolderLocation) {

        String parentPath = f.getParent();

        if (parentPath == null) {
            parentPath = "./";
        }

        if (tmpFolderLocation != null) {
            parentPath = tmpFolderLocation.getAbsolutePath();
        }

        String path = parentPath + "/" + getTmpFolderName(f);

        int index = 0;

        // search for unused tmp folder
        while (new File(path + index).exists()) {
            index++;
        }

        System.out.println(
                ">> VersionedFile.getTmpFolderPath(..): "
                + new File(path + index));

        return path + index;
    }

    /**
     * Returns the name of the temporary content folder of the specified file.
     * <p><b>Note:</b> only use this method if it is clear that no prefix is
     * used.</p>
     *
     * @param f the file
     * @return the name of the temporary content folder of the specified file
     */
    private static String getTmpFolderName(File f) {
        return "" + f.getName() + ".vtmp";
    }

    /**
     * Removes the temporary content folder of the specified file.
     *
     * @param f file
     * @return <code>true</code> if this operation was successful;
     * <code>false</code> otherwise
     */
    private boolean rmTmpFolder() {

        if (tmpFolderPrefix.isEmpty()) {
            if (VSysUtil.isWindows()) {
                // windows filelocks and java don't mix well :(
                // we delete tmp files on exit
                IOUtil.deleteTmpFilesOnExitIgnoreFileLocks(tmpFolder);
                return true;
            } else {
                return IOUtil.deleteDirectory(tmpFolder);
            }
        } else {
            // here we need to do more because we have to delete the prefix
            // directory
            String parentPath = getFile().getParent();

            if (parentPath == null) {
                parentPath = "./";
            }

            if (VSysUtil.isWindows()) {
                // windows filelocks and java don't mix well :(
                // we delete tmp files on exit
                IOUtil.deleteTmpFilesOnExit(new File(
                        parentPath + "/" + tmpFolderPrefix));

                return true;
            } else {
                IOUtil.deleteDirectory(new File(
                        parentPath + "/" + tmpFolderPrefix));

                return true;
            }
        }
    }

    /**
     * Initializes git repository.
     *
     * <p><b>Warning:</b> Be careful when calling this method. It will destroy
     * any existing repository!</p>
     *
     * @throws IOException
     */
    private void initGit() throws IOException {

        File repoFile = new File(tmpFolder.getAbsolutePath() + "/.git");

        // delete existing repository
        if (repoFile.exists()) {
            IOUtil.deleteDirectory(repoFile);
        }

        Git git = null;

        try {
            // initialize git repository
            Git.init().setDirectory(tmpFolder).call();
            git = Git.open(tmpFolder);
            git.add().addFilepattern(".").call();
            // perform initial commit
            git.commit().setMessage("initial commit").
                    setAuthor("VRL-User", "").call();

            git.getRepository().close();

        } catch (NoHeadException ex) {
            throw new IOException("Git exception", ex);
        } catch (NoMessageException ex) {
            throw new IOException("Git exception", ex);
        } catch (UnmergedPathException ex) {
            throw new IOException("Git exception", ex);
        } catch (ConcurrentRefUpdateException ex) {
            throw new IOException("Git exception", ex);
        } catch (JGitInternalException ex) {
            throw new IOException("Git exception", ex);
        } catch (WrongRepositoryStateException ex) {
            throw new IOException("Git exception", ex);
        } catch (NoFilepatternException ex) {
            throw new IOException("Git exception", ex);
        } catch (IOException ex) {
            throw new IOException("Git exception", ex);
        } finally {
            if (git != null) {
                git.getRepository().close();
            }
        }
    }

    /**
     * @return the versioned file (archive)
     */
    public File getFile() {
        return archiveFile;
    }

    @Override
    public void addVersionEventListener(VersionEventListener l) {
        versionEventListeners.add(l);
    }

    @Override
    public void removeVersionEventListener(VersionEventListener l) {
        versionEventListeners.remove(l);
    }

    @Override
    public void removeAllVersionEventListeners() {
        versionEventListeners.clear();
    }

    @Override
    public Iterable<VersionEventListener> getVersionEventListeners() {
        return versionEventListeners;
    }

    /**
     * Clears the opened files record used to check that only one instance per
     * file exists. <p> <b>Note:</b> you should usually not use this method.
     * Only use it if you are sure that VersionedFile has a bug and that you
     * need this method as workaround.</p> <p><b>Report bugs!</b> </p>
     */
    public static void clearOpenedFilesRecord() {
        openedFiles.clear();
    }

    /**
     * @return the excludedEndings
     */
    public String[] getExcludedEndings() {
        return excludedEndings;
    }

    /**
     * @return the excludedPaths
     */
    public Collection<String> getExcludedPaths() {
        return excludedPaths;
    }

    /**
     * Defines the endings that shall be excluded from project cleanup.
     * <p><b>Note:</b> on project
     * <code>close()</code> the working directory is deleted except for excluded
     * files. Deleted working directory files managed by git are stored in the
     * archive in the
     * <code>.git</code> folder. </p>
     *
     * @param endings the endings to exclude
     */
    public VersionedFile setExcludeEndingsFromCleanup(String... endings) {
        this.excludedEndings = endings;
        return this;
    }

    /**
     * Excludes the specified paths from project cleanup. <p><b>Note:</b> on
     * project
     * <code>close()</code> the working directory is deleted except for excluded
     * files. Deleted working directory files managed by git are stored in the
     * archive in the
     * <code>.git</code> folder. Each call of this method adds paths that shall
     * be excluded. It does not overwrite previous definitions.</p>
     *
     * @param endings the endings to exclude
     */
    public VersionedFile excludePathsFromCleanup(String... paths) {
        this.excludedPaths.addAll(Arrays.asList(paths));
        return this;
    }

    /**
     * @return the flushCommits
     */
    public boolean isFlushCommits() {
        return flushCommits;
    }

    /**
     * @param flushCommits the flushCommits to set
     */
    public VersionedFile setFlushCommits(boolean flushCommits) {
        this.flushCommits = flushCommits;
        return this;
    }
// TODO: add to unittests (11.04.2012)
//    public static void main(String[] args) throws IOException {
//
//        VRL.initAll();
//
//        System.out.println(">> Delete old Files");
//
//        File f = new File("/Users/miho/tmp/f1");
//
//        f.delete();
//
//        System.out.println(">> Create File One 1");
//
//        VersionedFile f1 = new VersionedFile(f).create();
//
//        f1.close();
//
//        System.out.println(">> Delete File One");
//
//        f.delete();
//
//        System.out.println(">> Create File One 2");
//
//        VersionedFile f2 = new VersionedFile(f).create();
//
//        f2.close();
//    }
}
