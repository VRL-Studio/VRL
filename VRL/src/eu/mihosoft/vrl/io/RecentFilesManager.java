/* 
 * RecentFilesManager.java
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

import eu.mihosoft.vrl.dialogs.FileDialogManager;
import eu.mihosoft.vrl.visual.VDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * A Controller for recently opened VRL Sessions and other files. This class can
 * be used to create "Recently opened Files" like menus.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class RecentFilesManager {

    private JMenu sessionsMenu;
    private ArrayList<RecentFilesEntry> recentSessions =
            new ArrayList<RecentFilesEntry>();
    private LoadSessionRequest loadRequest;
    private int maxNumberOfSessionEntries = 10;
    private File cacheLocation;

    /**
     * Constructor. Saving disabled.
     */
    public RecentFilesManager() {
        //
    }

    /**
     *
     * Constructor. Saving enabled.
     *
     * @param cacheLocation
     */
    public RecentFilesManager(File cacheLocation) {
        this.cacheLocation = cacheLocation;
    }

    /**
     * Adds a recent project to this manager.
     *
     * @param project project to add.
     */
    public void addRecentSession(VProject project) {
        addRecentSession(project.getFile().getAbsolutePath(), null);
    }

    /**
     * Adds a recent session/file to this manager.
     *
     * @param entry entry to add
     */
    public void addRecentSession(RecentFilesEntry entry) {

        BufferedImage image = null;

        if (entry.getImage() != null) {
            image = entry.getImage().toImage();
        }

        addRecentSession(entry.getFileName(), image);
    }

    /**
     * Adds a session entry to this session controller.
     *
     * @param fileName the entry to add
     */
    public void addRecentSession(String fileName, BufferedImage image) {

        _addRecentSession(fileName, image, false);
    }

    /**
     * Adds a session entry to this session controller.
     *
     * @param fileName the entry to add
     */
    private void _addRecentSession(
            String fileName, BufferedImage image, boolean fromFile) {

        RecentFilesEntry entry = new RecentFilesEntry(fileName, image);

        recentSessions.remove(entry);

        recentSessions.add(entry);

        if (!recentSessions.contains(entry)) {
            addToMenu(entry);
        } else {
            removeFromMenu(entry);
            addToMenu(entry);
        }

        if (getRecentSessions().size() > maxNumberOfSessionEntries) {
            removeRecentSession(recentSessions.get(0));
        }

        if (!fromFile) {
            save();
        }
    }

    /**
     * Adds a recent session from file.
     *
     * @param entry entry to add
     */
    private void addRecentSessionFromFile(RecentFilesEntry entry) {

        BufferedImage image = null;

        if (entry.getImage() != null) {
            image = entry.getImage().toImage();
        }

        _addRecentSession(entry.getFileName(), image, true);
    }

    /**
     * Clears this session manager, i.e., removes all sessions.
     */
    public void clear() {
        ArrayList<RecentFilesEntry> delList =
                new ArrayList<RecentFilesEntry>(getRecentSessions());

        for (RecentFilesEntry session : delList) {

            removeRecentSession(session);
        }
    }

    /**
     * Initializes the controller. Initializing implies specification of menu to
     * operate on and initializing the given menu to represent the recent
     * sessions of the main canvas.
     *
     * @param sessionMenu the menu that shall contain the menu items
     */
    public void initController(
            JMenu sessionMenu, LoadSessionRequest loadRequest) {
        this.sessionsMenu = sessionMenu;
        this.loadRequest = loadRequest;

        load();

        initMenus();
    }

    /**
     * Initializes the menus. It maps the given session list to the controlled
     * menus.
     */
    private void initMenus() {
        sessionsMenu.removeAll();
        for (RecentFilesEntry e : getRecentSessions()) {
            addToMenu(e);
        }
        if (getRecentSessions().isEmpty()) {
            sessionsMenu.setEnabled(false);
        } else {
            sessionsMenu.setEnabled(true);
        }

        // add delete item
        sessionsMenu.addSeparator();
        JMenuItem clearItem = new JMenuItem("Delete all Entries");
        clearItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
                save();
            }
        });
        sessionsMenu.add(clearItem);
    }

    /**
     * Maps a session entry to a menu item.
     *
     * @param fileName the entry to add
     */
    private void addToMenu(final RecentFilesEntry entry) {

        if (sessionsMenu != null) {

            final JMenuItem loadSessionItem = new JMenuItem(entry.getFileName());

            loadSessionItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getActionCommand().equals(entry.getFileName())) {
                        loadRequest.request(entry.getFileName());
                        // update default directory
                        FileDialogManager.setDefaultDir(new File(entry.getFileName()));
                    }
                }
            });

            sessionsMenu.add(loadSessionItem, 0);
            sessionsMenu.setVisible(true);

            if (getRecentSessions().isEmpty()) {
                sessionsMenu.setEnabled(false);
            } else {
                sessionsMenu.setEnabled(true);
            }
        }
    }

    /**
     * Removes a session from menu.
     *
     * @param fileName the session to remove
     */
    private void removeFromMenu(RecentFilesEntry entry) {
        if (sessionsMenu != null) {
            JMenuItem removeItem = getItemPerName(sessionsMenu,
                    entry.getFileName());

            // only remove if available
            if (removeItem != null) {
                sessionsMenu.remove(removeItem);
            }

            if (getRecentSessions().isEmpty()) {
                sessionsMenu.setEnabled(false);
            } else {
                sessionsMenu.setEnabled(true);
            }
        }
    }

    /**
     * Removes a session.
     *
     * @param fileName the session to remove.
     */
    public void removeRecentSession(RecentFilesEntry entry) {
        getRecentSessions().remove(entry);
        removeFromMenu(entry);
    }

    /**
     * Removes a session.
     *
     * @param fileName the session to remove.
     */
    public void removeRecentSession(String name) {

        RecentFilesEntry entry = new RecentFilesEntry(name, null);

        getRecentSessions().remove(entry);
        removeFromMenu(entry);
    }

    /**
     * Returns a menu item by name.
     *
     * @param menu the menu to search
     * @param name the name
     * @return the item if an item with the specified name exists;
     * <code>null</code> otherwise
     */
    private JMenuItem getItemPerName(JMenu menu, String name) {
        JMenuItem result = null;

        for (int i = 0; i < menu.getItemCount(); i++) {

            // we skip this item if it is no menu item
            // see documentation of JMenu.getItem(i)
            if (menu.getItem(i) == null) {
                continue;
            }

            if (menu.getItem(i).getText().equals(name)) {
                result = menu.getItem(i);
            }
        }

        return result;
    }

    /**
     * Returns the maximum number of session entries.
     *
     * @return the maximum number of session entries
     */
    public int getMaxNumberOfSessionEntries() {
        return maxNumberOfSessionEntries;
    }

    /**
     * Returns the maximum number of session entries that are shown. If the
     * number of entries is too high the oldest entries will be deleted. The
     * number of deleted entries is
     * <code>max(#entries - max, 0)</code>.
     *
     * @param maxNumberOfSessionEntries the maximum number of session entries to
     * set
     */
    public void setMaxNumberOfSessionEntries(int maxNumberOfSessionEntries) {
        this.maxNumberOfSessionEntries = maxNumberOfSessionEntries;

        if (maxNumberOfSessionEntries < 0) {
            maxNumberOfSessionEntries = 0;
        }

        if (getRecentSessions().size() > maxNumberOfSessionEntries) {
            while (getRecentSessions().size() > maxNumberOfSessionEntries) {
                removeRecentSession(recentSessions.get(0));
            }
        }
    }
//    public void dispose() {
////        try {
////            PluginGroup delList = new PluginGroup();
////            delList.addAll(plugins);
////
////            for (PluginEntry e : delList) {
////                removeRecentSession(e);
////            }
////        } catch (Exception ex) {
////            Logger.getLogger(PluginController.class.getName()).
////                    log(Level.SEVERE, null, ex);
////        }
//        sessionsMenu = null;
//        loadRequest = null;
////        plugins = null;
//    }

    /**
     * @return the recentSessions
     */
    public Collection<RecentFilesEntry> getRecentSessions() {
        return recentSessions;
    }

    /**
     * @param recentSessions the recentSessions to set
     */
    private void setEntriesFromFile(
            Collection<RecentFilesEntry> recentSessions) {

        clear();

        for (RecentFilesEntry recentFilesEntry : recentSessions) {
            addRecentSessionFromFile(recentFilesEntry);
        }

//        // reverse order
//        ArrayList<RecentFilesEntry> copy =
//                new ArrayList<RecentFilesEntry>();
//        copy.addAll(recentSessions);
//
//        for (int i = copy.size() - 1; i >= 0; i--) {
//            addRecentSessionFromFile(copy.get(i));
//        }
    }

    private void save() {

        // no file specified, saving/loading disabled
        if (cacheLocation == null) {
            return;
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(cacheLocation);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RecentFilesManager.class.getName()).
                    log(Level.SEVERE, null, ex);

            try {
                out.close();
            } catch (Exception ex1) {
                Logger.getLogger(RecentFilesManager.class.getName()).
                        log(Level.SEVERE, null, ex1);
            }

            return;
        }

        XMLEncoder e = new XMLEncoder(out);

        e.writeObject(recentSessions);

        e.flush();
        e.close();
    }

    @SuppressWarnings("unchecked")
    private void load() {

        // no file specified, saving/loading disabled
        if (cacheLocation == null) {
            return;
        }

        if (!cacheLocation.exists()) {
            return;
        }

        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(cacheLocation));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RecentFilesManager.class.getName()).
                    log(Level.SEVERE, null, ex);

            try {
                in.close();
            } catch (Exception ex1) {
                Logger.getLogger(RecentFilesManager.class.getName()).
                        log(Level.SEVERE, null, ex1);
            }
            return;
        }

        XMLDecoder d = new XMLDecoder(in);

        Object o = d.readObject();

        if (!(o instanceof Collection<?>)) {
            try {
                throw new IOException("Data format not recognized!");
            } catch (IOException ex) {
                Logger.getLogger(RecentFilesManager.class.getName()).
                        log(Level.SEVERE, null, ex);

                try {
                    d.close();
                } catch (Exception ex1) {
                    Logger.getLogger(RecentFilesManager.class.getName()).
                            log(Level.SEVERE, null, ex1);
                }

                return;
            }
        } else {
            setEntriesFromFile((Collection<RecentFilesEntry>) o);
        }

        d.close();

        initDefaultDir();
    }

    /**
     * Initializes the default dir that is used by all methods of the
     * {@link FileDialogManager} class that create file dialogs. Therefore it
     * uses the recentSessions list.
     */
    private void initDefaultDir() {
        if (!getRecentSessions().isEmpty()) {
            FileDialogManager.setDefaultDir(
                    new File(recentSessions.get(recentSessions.size() - 1).
                    getFileName()));
        }
    }

    /**
     * Returns the last recently used dir. It returns the directory from the
     * recentSessions collection.
     *
     * @return the last recently used dir
     */
    public File getLastUsedDir() {
        if (!getRecentSessions().isEmpty()) {
            File fileOrDir = new File(recentSessions.get(
                    recentSessions.size() - 1).getFileName());

            if (fileOrDir.isFile()) {
                return fileOrDir.getAbsoluteFile().getParentFile();
            }

        }
        return null;
    }
}
