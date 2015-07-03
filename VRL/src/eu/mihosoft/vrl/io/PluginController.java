///*
// * PluginController.java
// *
// * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
// *
// * Copyright (C) 2009 Michael Hoffer &lt;info@michaelhoffer.de&gt;
// *
// * Supported by the Goethe Center for Scientific Computing of Prof. Wittum
// * (http://gcsc.uni-frankfurt.de)
// *
// * This file is part of Visual Reflection Library (VRL).
// *
// * VRL is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License version 3
// * as published by the Free Software Foundation.
// *
// * VRL is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// *
// * Linking this library statically or dynamically with other modules is
// * making a combined work based on this library.  Thus, the terms and
// * conditions of the GNU General Public License cover the whole
// * combination.
// *
// * As a special exception, the copyright holders of this library give you
// * permission to link this library with independent modules to produce an
// * executable, regardless of the license terms of these independent
// * modules, and to copy and distribute the resulting executable under
// * terms of your choice, provided that you also meet, for each linked
// * independent module, the terms and conditions of the license of that
// * module.  An independent module is a module which is not derived from
// * or based on this library.  If you modify this library, you may extend
// * this exception to your version of the library, but you are not
// * obligated to do so.  If you do not wish to do so, delete this
// * exception statement from your version.
// */
//package eu.mihosoft.vrl.io;
//
//import eu.mihosoft.vrl.dialogs.LoadPluginDialog;
//import eu.mihosoft.vrl.reflection.VisualCanvas;
//import eu.mihosoft.vrl.visual.Message;
//import eu.mihosoft.vrl.visual.MessageType;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.beans.XMLDecoder;
//import java.beans.XMLEncoder;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.net.URL;
//import java.net.URLClassLoader;
//import java.util.ArrayList;
//import java.util.jar.JarInputStream;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.swing.JMenu;
//import javax.swing.JMenuItem;
//
///**
// * A Controller for VRL plugins.
// * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
// */
//public class PluginController {
//
//    private PluginGroup plugins = new PluginGroup();
//    private JMenu removeMenu;
////    private VisualCanvas mainCanvas;
//    private ClassLoader classLoader;
//
//    /**
//     * Adds a plugin entry to this plugin controller.
//     * @param entry the entry to add
//     */
//    public void addPlugin(PluginEntry entry) {
//        boolean exists = false;
//        for (PluginEntry e : getPlugins()) {
//            if (e.getChecksum().equals(entry.getChecksum())) {
//                exists = true;
//
////                mainCanvas.getMessageBox().
////                        addMessage("Can't add Plugin:",
////                        ">> plugin \"<b><tt>" + e.getName() +
////                        "</tt></b>\" has already been added (SHA-1: " +
////                        e.getChecksum() + " )", MessageType.ERROR);
//
//                break;
//            }
//        }
//
//        if (!exists) {
//            entry.setName(getUniqueName(entry.getName()));
//            getPlugins().add(entry);
//            addToMenu(entry);
//
//            try {
//                registerPlugin(entry);
////                Message m =
////                        mainCanvas.getMessageBox().addMessage("Plugin added:",
////                        ">> plugin \"<b><tt>" + entry.getName() +
////                        "</tt></b>\" has been successfully added!", null,
////                        MessageType.INFO, 5);
////                mainCanvas.getMessageBox().messageRead(m);
//            } catch (Exception ex) {
//                Logger.getLogger(PluginController.class.getName()).
//                        log(Level.SEVERE, null, ex);
//            }
//        }
//    }
//
//    private ArrayList<Class<?>> loadClasses(PluginEntry e) throws Exception {
//        URL tmpFile = IOUtil.base64ToTmpFile(e.getData()).toURI().toURL();
//
//        ArrayList<String> classNames =
//                VJarUtil.getClassNamesFromStream(
//                new JarInputStream(e.getStream()));
//
//        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
//
////        if (classLoader==null) {
////            classLoader = this.getClass().getClassLoader();
////        }
//
//        URLClassLoader loader =
//                new URLClassLoader(new URL[]{tmpFile});
//
////        classLoader = loader;
//
//        for (String n : classNames) {
//            try {
//                classes.add(loader.loadClass(n));
////                System.out.println(">> added: " + n);
//
////                Message m =
////                        mainCanvas.getMessageBox().addUniqueMessage(
////                        "Loading Plugin:",
////                        ">> added class: \"<b><tt>" +
////                        n +
////                        "</b></tt>\"", null,
////                        MessageType.INFO, 5);
////
////                mainCanvas.getMessageBox().messageRead(m);
//
//            } catch (NoClassDefFoundError ex) {
//                System.out.println(">> ERROR: cannot add \"" + n +
//                        "\"");
//                System.out.println(" > cause: " + ex.toString());
//
////                Message m =
////                        mainCanvas.getMessageBox().addUniqueMessage(
////                        "Loading Plugin:",
////                        ">> cannot add class: \"<b><tt>" +
////                        n +
////                        "</b></tt>\"<br>" +
////                        " > cause: " + ex.toString(), null,
////                        MessageType.INFO, 5);
////
////                mainCanvas.getMessageBox().messageRead(m);
//            } catch (Exception ex) {
//                System.out.println(">> ERROR: cannot add \"" + n +
//                        "\"");
//                System.out.println(" > cause: " + ex.toString());
//
////                Message m =
////                        mainCanvas.getMessageBox().addUniqueMessage(
////                        "Loading Plugin:",
////                        ">> cannot add class: \"<b><tt>" +
////                        n +
////                        "</b></tt>\"<br>" +
////                        " > cause: " + ex.toString(), null,
////                        MessageType.INFO, 5);
////
////                mainCanvas.getMessageBox().messageRead(m);
//            } catch (java.lang.IncompatibleClassChangeError ex) {
//                System.out.println(">> ERROR: cannot add \"" + n +
//                        "\"");
//                System.out.println(" > cause: " + ex.toString());
//            }
//        }
//
////        for (Class<?> c : classes) {
////            mainCanvas.getClassLoader().addClass(c);
////        }
//
//        return classes;
//    }
//
//    /**
//     * Returns instances of all classes of the specified class list that
//     * implement the * {@link PluginConfigurator} interface.
//     * @param classes the class list to check
//     * @return a list containing instances of all classes of the plugin entry
//     *         that implement the {@link PluginConfigurator} interface
//     * @throws InstantiationException
//     * @throws IllegalAccessException
//     */
//    public ArrayList<PluginConfigurator> getPluginConfigurators(
//            ArrayList<Class<?>> classes)
//            throws InstantiationException, IllegalAccessException {
//        ArrayList<PluginConfigurator> result =
//                new ArrayList<PluginConfigurator>();
//
//        for (Class<?> c : classes) {
//            if (PluginConfigurator.class.isAssignableFrom(c)) {
//
//                result.add((PluginConfigurator) c.newInstance());
//
//                System.out.println(">> PluginConfigurator found: \"" +
//                        c.getName() +
//                        "\"");
//
////                Message m =
////                        mainCanvas.getMessageBox().addUniqueMessage(
////                        "Loading Plugin:",
////                        ">> PluginConfigurator found: \"<b><tt>" +
////                        c.getName() +
////                        "</b></tt>\"", null,
////                        MessageType.INFO, 5);
////
////                mainCanvas.getMessageBox().messageRead(m);
//            }
//        }
//
//        return result;
//    }
//
//    /**
//     * Registers a plugin with the main canvas and adds all of its classes
//     * to the class loader of this controller.
//     * @param e the plugin to register
//     * @throws Exception
//     */
//    public void registerPlugin(PluginEntry e) throws Exception {
//
//        ArrayList<PluginConfigurator> configurators = null;
//        try {
//            configurators = getPluginConfigurators(loadClasses(e));
//        } catch (InstantiationException ex) {
////            mainCanvas.getMessageBox().addMessage(
////                    "Plugin couldn't be registered",
////                    ">> plugin \"<b><tt>" + e.getName() +
////                    "</tt></b>\" couldn't be registerd because the " +
////                    "plugin does not work correctly.",
////                    MessageType.ERROR);
//            removePlugin(e, false);
//        } catch (IllegalAccessException ex) {
////            mainCanvas.getMessageBox().addMessage(
////                    "Plugin couldn't be registered",
////                    ">> plugin \"<b><tt>" + e.getName() +
////                    "</tt></b>\" couldn't be registerd because the " +
////                    "plugin does not work correctly.",
////                    MessageType.ERROR);
//            removePlugin(e, false);
//        }
//
//        for (PluginConfigurator pluginConfigurator : configurators) {
//            try {
//                pluginConfigurator.register(mainCanvas);
//            } catch (java.lang.NoClassDefFoundError ex) {
////                mainCanvas.getMessageBox().addMessage(
////                        "Plugin couldn't be registered",
////                        ">> plugin \"<b><tt>" + e.getName() +
////                        "</tt></b>\" couldn't be registered because of " +
////                        "missing dependencies. Add them first and try adding " +
////                        "the plugin again.",
////                        MessageType.ERROR);
//                removePlugin(e, false);
//                throw ex;
//            } catch (Exception ex) {
////                mainCanvas.getMessageBox().addMessage(
////                        "Plugin couldn't be registered",
////                        ">> plugin \"<b><tt>" + e.getName() +
////                        "</tt></b>\" couldn't be registerd because the " +
////                        "plugin does not work correctly.",
////                        MessageType.ERROR);
//                removePlugin(e, false);
//                throw ex;
//            }
//        }
//    }
//
//    /**
//     * Unregisters a plugin with the main canvas.
//     * @param e the plugin to register
//     * @throws Exception
//     */
//    public void unregisterPlugin(PluginEntry e) throws Exception {
//
//        ArrayList<Class<?>> classes = loadClasses(e);
//
//        ArrayList<PluginConfigurator> configurators =
//                getPluginConfigurators(classes);
//        for (PluginConfigurator pluginConfigurator : configurators) {
////            pluginConfigurator.unregister(mainCanvas);
//        }
//
//        // removes classes from main canvas class
//        // loader that is used for groovy code
////        for (Class<?> c : classes) {
////            mainCanvas.getClassLoader().removeClass(c);
////        }
//    }
//
//    /**
//     * Initializes the controller. Initializing implies specification of menu
//     * to operate on and initializing the given menu to represent the plugins
//     * of the main canvas.
//     * @param removeMenu the menu that shall contain the remove menu items
//     */
//    public void initController(
//            JMenu removeMenu) {
//        this.removeMenu = removeMenu;
//        initMenus();
//    }
//
//    /**
//     * Initializes the menus. It maps the given plugin list to the
//     * controlled menus.
//     */
//    private void initMenus() {
//        removeMenu.removeAll();
//        for (PluginEntry e : getPlugins()) {
//            addToMenu(e);
//        }
//    }
//
//    /**
//     * Adds a plugin from file.
//     * @param file the file
//     * @throws IOException
//     */
//    public void addFromFile(File file) throws IOException {
//        addPlugin(new PluginEntry(file));
//    }
//
//    /**
//     * Adds a plugin from file which is chosen by file dialog.
//     */
//    public void addFromFileDialog() {
//        PluginEntry e = LoadPluginDialog.showDialog(null);
//        if (e != null) {
//            addPlugin(e);
//        }
//    }
//
//    /**
//     * Maps a plugin entry to a menu item.
//     * @param entry the entry to add
//     */
//    private void addToMenu(final PluginEntry entry) {
//
//        if (removeMenu != null) {
//
//            final JMenuItem removeItem = new JMenuItem(entry.getName());
//
//            removeItem.addActionListener(new ActionListener() {
//
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    if (e.getActionCommand().equals(entry.getName())) {
//                        removePlugin(entry);
//                    }
//                }
//            });
//
//            removeMenu.add(removeItem);
//            removeMenu.setVisible(true);
//        }
//    }
//
//    /**
//     * Removes a plugin from menu.
//     * @param e the plugin to remove
//     */
//    private void removeFromMenu(PluginEntry e) {
//        if (removeMenu != null) {
//            JMenuItem removeItem = getItemPerName(removeMenu, e.getName());
//            removeMenu.remove(removeItem);
//        }
//    }
//
//    /**
//     * Removes a plugin.
//     * @param e the plugin to remove.
//     * @param showMessages defines whether to show messages
//     */
//    public void removePlugin(PluginEntry e, boolean showMessages) {
//        getPlugins().remove(e);
//        removeFromMenu(e);
//        try {
//            unregisterPlugin(e);
//            if (showMessages) {
////                Message m =
////                        mainCanvas.getMessageBox().addMessage("Plugin removed:",
////                        ">> plugin \"<b><tt>" + e.getName() +
////                        "</tt></b>\" has been successfully removed!", null,
////                        MessageType.INFO, 5);
////                mainCanvas.getMessageBox().messageRead(m);
//            }
//        } catch (Exception ex) {
//            if (showMessages) {
////                mainCanvas.getMessageBox().addMessage(
////                        "Plugin couldn't be unregistered",
////                        ">> plugin \"<b><tt>" + e.getName() +
////                        "</tt></b>\" couldn't be unregisterd. " +
////                        "In most cases this is not critical.",
////                        MessageType.WARNING);
//            }
//        }
//    }
//
//    /**
//     * Removes a plugin.
//     * @param e the plugin tp remove
//     */
//    public void removePlugin(PluginEntry e) {
//        removePlugin(e, true);
//    }
//
////    /**
////     * @param mainCanvas the mainCanvas to set
////     */
////    public void setMainCanvas(VisualCanvas mainCanvas) {
////        this.mainCanvas = mainCanvas;
////    }
//
//    /**
//     * Returns a menu item by name.
//     * @param menu the menu to search
//     * @param name the name
//     * @return the item if an item with the specified name exists;
//     *         <code>null</code> otherwise
//     */
//    private JMenuItem getItemPerName(JMenu menu, String name) {
//        JMenuItem result = null;
//
//        for (int i = 0; i < menu.getItemCount(); i++) {
//            if (menu.getItem(i).getText().equals(name)) {
//                result = menu.getItem(i);
//            }
//        }
//
//        return result;
//    }
//
//    /**
//     * Computes a unique plugin name that can be used for automated entry
//     * naming. If the name <code>filename</code> already exists it uses the
//     * pattern <code>"filename (i)"</code> where <code>i</code>
//     * represents an index number. This method always tries to use the lowest
//     * index that is available.
//     * @return the unique plugin name
//     */
//    private String getUniqueName(String fileName) {
//        String result = null;
//        int count = 1;
//
//        String name = fileName;
//        while (getPlugins().getByName(name) != null) {
//            name = fileName + " (" + count + ")";
//            count++;
//        }
//        result = name;
//
//        return result;
//    }
//
//    /**
//     * Loads plugins via XML deserialisation.
//     * @param d the decoder to use
//     */
//    public void load(XMLDecoder d) {
//        setPlugins((PluginGroup) d.readObject());
//    }
//
//    /**
//     * Saves plugins via XML serialization
//     * @param e the encoder to use
//     */
//    public void save(XMLEncoder e) {
//        e.writeObject(getPlugins());
//    }
//
//    /**
//     * Registers all defined plugins.
//     * @throws Exception
//     */
//    public void registerPlugins() throws Exception {
//        for (PluginEntry e : getPlugins()) {
//            registerPlugin(e);
//        }
//    }
//
//    /**
//     * Unregisters all defined plugins.
//     * @throws Exception
//     */
//    public void unregisterPlugins() throws Exception {
//        for (PluginEntry e : getPlugins()) {
//            unregisterPlugin(e);
//        }
//    }
//
//    /**
//     * Returns the plugins of this controller.
//     * @return the plugins
//     */
//    public PluginGroup getPlugins() {
//        return plugins;
//    }
//
//    /**
//     * Defines the plugins of this controller.
//     * @param plugins the plugins to set
//     */
//    public void setPlugins(PluginGroup plugins) {
//        this.plugins = plugins;
//    }
//
//    /**
//     * Disposes all plugins and additional resources.
//     */
//    public void dispose() {
//        try {
//            PluginGroup delList = new PluginGroup();
//            delList.addAll(plugins);
//
//            for (PluginEntry e : delList) {
//                removePlugin(e);
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(PluginController.class.getName()).
//                    log(Level.SEVERE, null, ex);
//        }
//        removeMenu = null;
//        plugins = null;
//    }
////    public void loadGlobalPlugins(File f) throws IOException {
////        XMLDecoder d = new XMLDecoder(new FileInputStream(f));
////        PluginGroup globalPlugins = (PluginGroup) d.readObject();
////
////        JarStreamClassLoader globalLoader = new JarStreamClassLoader();
////
////        for (PluginEntry e : globalPlugins) {
////            globalLoader.addClassesFromStream((JarInputStream) e.getStream());
////        }
////    }
//}
