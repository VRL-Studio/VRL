/* 
 * VRL.java
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

import eu.mihosoft.vrl.io.*;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.types.observe.FileAnalyzer;
import eu.mihosoft.vrl.visual.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.ref.WeakReference;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOException;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JFrame;

/**
 * VRL class for managing initialization of the VRL run-time. This includes
 * plugin management.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VRL {

    /**
     * Plugin configurators accessible via plugin name.
     */
    private static Map<String, PluginConfigurator> plugins =
            new HashMap<String, PluginConfigurator>();
    /**
     * Names of all invalid plugins. All plugins listed here will be excluded
     * from initialization.
     */
    private static List<String> invalidPlugins = new ArrayList<String>();
    /**
     * Plugin data controllers accessible through plugin name.
     */
    private static Map<String, PluginDataController> pluginsDataControllers =
            new HashMap<String, PluginDataController>();
    /**
     * Plugin groups. A group is a jar file. All plugin names in the file can be
     * accessed via this map
     */
    private static Map<String, List<String>> pluginGroups =
            new HashMap<String, List<String>>();
    /**
     * Cached plugin configurators used for uninstall (also contains plugins
     * that could not be loaded)
     */
    private static final List<PluginConfigurator> allPlugins =
            new ArrayList<PluginConfigurator>();
    /**
     * Canvas list (uses weak references).
     */
    private static HashSet<WeakReference<Canvas>> canvasList =
            new HashSet<WeakReference<Canvas>>();
    /**
     * Current/Active Project Controller.
     */
    private static VProjectController projectController;
    /**
     * Style list
     */
    private static HashMap<String, Style> styles = new HashMap<String, Style>();
    /**
     * Stores the provided entries of the registered plugins.
     */
    private static HashMap<String, String> providedEntry =
            new HashMap<String, String>();
    /**
     * Defines how often the weak reference list shall be cleaned, i.e., how
     * often
     * <code>null</code>-references shall be removed from the canvas list.
     */
    private static final int REFRESH_INTERVAL = 3;
    /**
     * Registration error message.
     */
    private static String initPluginError = null;
    /**
     * Initialization error message.
     */
    private static String registrationError = null;
    /**
     * Action delegator used to delegate menu actions.
     */
    private static VActionDelegator actionDelegator = new VActionDelegator();
    /**
     * Menucontroller for style menu.
     */
    private static final MenuController styleMenuController =
            new VMenuController();
    /**
     * Menucontroller for plugin menu.
     */
    private static final MenuController pluginMenuController =
            new VMenuController();
    /**
     * Menucontroller for uninstall menu.
     */
    private static final MenuController uninstallPluginMenuController =
            new VMenuController();
    /**
     * Menucontroller for file templates menu.
     */
    private static final MenuController fileTemplatesMenuController =
            new VMenuController();
    /**
     * Plugin classloader. This classloader has access to all classloaders of
     * the individual plugins. This classloader may be used for serialization/
     * deserialization.
     */
    private static ClassLoader pluginClassLoader;
    /**
     * Manager for property folder. This instance contains locations of all
     * official subfolders, such as tmp, plugins etc.
     */
    private static final VPropertyFolderManager propertyFolderManager =
            new VPropertyFolderManager();
    /**
     * Menus used to register plugin actions.
     */
    private static Map<String, MenuAdapter> menus =
            new HashMap<String, MenuAdapter>();
    /**
     * Commandline options that have been specified to the JVM.
     */
    private static String[] commandLineOptions;
    /**
     * A map with all external classloaders.
     */
    private static Map<String, ClassLoader> externalCLMap =
            new HashMap<String, ClassLoader>();

    static {

        System.out.println(">> VRL Version: " + Constants.VERSION);
        System.out.println(" --> running on Java Version: "
                + System.getProperty("java.version")
                + " (" + System.getProperty("java.vendor") + ")");
        System.out.println(" --> OS: " + VSysUtil.getPlatformInfo());
        System.out.println(" --> pid: " + VSysUtil.getPID());

        addPlugin(new VRLPlugin(), null);
    }
    /**
     * Plugin classloader. This classloader has access to all external
     * classloaders of the individual plugins. This classloader may be used to
     * access exported plugin functionality.
     */
    private static PluginClassLoader externalPluginClassLoader;

    /**
     * Initializes the VRL runtime system.
     *
     * @param args optional arguments such as plugin location. Example:
     * <code>-plugins /home/user/plugins</code>
     */
    public static void initAll(String[] args) {


        VRL.setCommandLineOptions(args);

//        List<String> invalidPlugins = new ArrayList<String>();

        getPropertyFolderManager().evalueteArgs(args);

        SplashScreenGenerator.setProgress(5);

        Constants.PLUGIN_DIR =
                getPropertyFolderManager().getPluginFolder().getAbsolutePath();


        // load libraries and plugins
        VRL.initLibs();
        if (args != null) {
            VRL.evaluatePluginArguments(args);
        }

        SplashScreenGenerator.setProgress(20);

        VRL.initPlugins();

        SplashScreenGenerator.setProgress(80);
    }

    /**
     * Initializes the VRL run-time system using default plugin/library location
     * (relative path:
     * <code>custom-lib</code>).
     */
    public static void initAll() {
        initAll(new String[0]);
    }

    /**
     * Verifies whether requested plugins are provided.
     *
     * @param dependencies requested plugins
     * @return dependencycheck object that contains information on whether
     * plugins are available and, if not, whicl plugins are missing
     */
    public static PluginDependencyCheck verify(
            Collection<AbstractPluginDependency> dependencies) {

        PluginDependencyCheckImpl result = new PluginDependencyCheckImpl();

        if (dependencies == null) {
            return result;
        }

        for (AbstractPluginDependency d : dependencies) {
            boolean found = false;
            PluginDependency dep = d.toPluginDependency();
            for (PluginConfigurator p : plugins.values()) {
                if (d.toPluginDependency().verify(p.getIdentifier())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.addMissingDependency(dep);
            }
        }

        return result;
    }

    public static String getVersionIdentifier() {
        return Constants.VERSION;
    }

    public static String getVersionBaseIdentifier() {
        return Constants.VERSION_BASE;
    }

    /**
     * Returns the available plugins as collection of plugin dependencies.
     *
     * @return the available plugins as collection of plugin dependencies
     */
    public static Collection<AbstractPluginDependency> getAvailablePlugins() {
        Collection<AbstractPluginDependency> dependencies =
                new ArrayList<AbstractPluginDependency>();

        for (PluginConfigurator p : plugins.values()) {
            dependencies.add(new AbstractPluginDependency(
                    p.getIdentifier().getName(),
                    p.getIdentifier().getVersion().toString(),
                    VersionInfo.UNDEFINED));
        }

        return dependencies;
    }

    /**
     * Initializes the plugins. Plugins usually perform canvas independent tasks
     * such as compiling additional classer or loading resources.
     */
    private static void initPlugins() {
        System.out.println(">> Init Plugins ( #" + plugins.size() + " )");

        // plugin validation
        for (PluginConfigurator pC : plugins.values()) {
            PluginIdentifier id = pC.getIdentifier();

            boolean error = false;

            String msg = "";

            if (!id.isNameValid()) {
                error = true;
                msg += "<br>"
                        + ">> Name "
                        + Message.EMPHASIZE_BEGIN + id.getName()
                        + Message.EMPHASIZE_END + " is invalid.";

            } else if (!id.isVersionValid()) {
                error = true;
                msg += "<br>"
                        + ">> Version "
                        + Message.EMPHASIZE_BEGIN + id.getVersion()
                        + Message.EMPHASIZE_END + " is invalid.";

            }

            if (error) {

                invalidPlugins.add(id.getName());

                if (initPluginError != null) {
                    initPluginError += "Error in " + id.toString() + ": " + msg;
                } else {
                    initPluginError = "Error in " + id.toString() + ": " + msg;
                }
            }
        } // end for pC


        // remove invalid plugins
        for (String name : invalidPlugins) {
            plugins.remove(name);
        }

        invalidPlugins.clear();


        PluginManager graph = new PluginManager();
        int counter = 0;

        BootOrder result = graph.computeBootOrder(plugins.values());

        if (result.hasErrors()) {
            if (initPluginError != null) {
                initPluginError += "<br>" + result.getErrorMessages();
            } else {
                initPluginError = result.getErrorMessages();
            }
        }

        List<PluginConfigurator> bootOrder = result.getOrder();


        // root classloader 
        ClassLoader root_parent = ClassLoader.getSystemClassLoader();

        // list containing root elements of plugin groups:
        // - a plugin group is a set of elements that are connected via graph
        //   edges, i.e., dependencies
        // - the root element of a group is the group element with the highest
        //   bootorder index.
        ArrayList<String> pluginGroupsRootElements = new ArrayList<String>();

        // classloader parentage map:
        // - this map contains for each element the parent element.
        //   the parentage relation is defined as classloader relation, where
        //   the group element with the lowest bootorder index is the root
        //   parent of the group
        Map<String, String> pluginParentageMap =
                graph.computeClassLoaderParentageMap(
                pluginGroupsRootElements, bootOrder);

        // plugin classloaders connected as defined by the graph
        Collection<ClassLoader> classLoaders = new ArrayList<ClassLoader>();

        // plugin classloaders connected as defined by the graph
        Collection<ClassLoader> externalLoaders = new ArrayList<ClassLoader>();


        double incProgress = 80.0 / plugins.size();
        double progressValue = SplashScreenGenerator.getProgress();

        boolean updatedOrInstalledPlugins = false;

        // initialize plugin
        for (PluginConfigurator plugin : bootOrder) {

            SplashScreenGenerator.setProgress((int) progressValue);

            progressValue += incProgress;

            try {
                counter++;
                System.out.println(
                        ">> Plugin (" + counter + "): "
                        + plugin.getIdentifier());

                SplashScreenGenerator.printBootMessage(
                        ">> plugin (" + counter + "): "
                        + plugin.getIdentifier());


                String pluginID = plugin.getIdentifier().getName();
                String parentPluginID = pluginParentageMap.get(pluginID);

                // set system classloader as parent classloader
                ClassLoader parentCL = root_parent;

                // if a parent is defined, change reference accordingly
                if (parentPluginID != null) {
                    parentCL = externalCLMap.get(parentPluginID);
                }

                // create new internal plugin classloader with the previously
                // defined parent
                ClassLoader internalCL = createInternalPluginClassLoader(
                        plugin.getClass(),
                        parentCL);

                // create a new instance of the plugin configurator after
                // reloading the configurator class with the new classloader
                plugin = instanceFromClassLoader(plugin.getClass(), internalCL);

                // create new external plugin classloader with the previously
                // defined parent
                ClassLoader externalCL = createExternalPluginClassLoader(
                        plugin,
                        parentCL, internalCL);

                externalCLMap.put(pluginID, externalCL);

                // add the classloader if this is the loader of a root element
                if (pluginGroupsRootElements.contains(pluginID)) {
                    classLoaders.add(plugin.getClass().getClassLoader());
                    externalLoaders.add(externalCL);
                }

                // update plugin map entry
                plugins.put(pluginID, plugin);

                PluginDataController dataController =
                        new PluginDataController(plugin);

                // update plugin data controller map entry
                pluginsDataControllers.put(
                        pluginID, dataController);

                InitPluginAPI initAPI = new InitPluginAPIImpl(dataController);

                // perform native library loading if configurator supports this
                if (plugin instanceof VPluginConfigurator) {

                    try {
                        ((VPluginConfigurator) plugin).setInitAPI(initAPI);
                    } catch (AbstractMethodError ex) {
                        System.err.println(
                                "--> Error: plugin does not provide"
                                + " setInitAPI() method. This is deprecated"
                                + " since VRL-0.4.0.");
                    }

                    ((VPluginConfigurator) plugin).nativeInit(dataController);
                }

                // check whether to install/update this plugin
                if (needsInstall(plugin, dataController)) {

                    updatedOrInstalledPlugins = true;

                    performConfiguratorInstall(plugin, dataController, initAPI);
                }


                try {
                    // initialize the plugin
                    plugin.init(initAPI);
                } catch (AbstractMethodError ex) {
                    System.err.println(
                            "--> Error: plugin does not provide"
                            + " init(InitPluginAPI) method. This is deprecated"
                            + " since VRL-0.4.0.");
                }


            } catch (Throwable tr) {

                invalidPlugins.add(plugin.getIdentifier().getName());

                Logger.getLogger(
                        VRL.class.getName()).log(Level.SEVERE, null, tr);

                String errorMsg =
                        " --> Error: cannot add plugin \""
                        + plugin.getIdentifier().toString()
                        + "\" because initialization failed. Cause: <br>"
                        + tr.toString();
                if (initPluginError != null) {
                    initPluginError += "<br>" + errorMsg;
                } else {
                    initPluginError = errorMsg;
                }
            }
        } // end for p in bootorder


        if (updatedOrInstalledPlugins) {
            updateHelpIndex();
        }

        // remove invalid plugins
        for (String name : invalidPlugins) {
            plugins.remove(name);
        }

        // update plugin cache to allow the GC to remove unused classloaders
        // and related classes, resources etc.
        for (int i = 0; i < allPlugins.size(); i++) {

            PluginConfigurator tmpPC = allPlugins.get(i);
            PluginConfigurator newPC =
                    plugins.get(tmpPC.getIdentifier().getName());

            if (newPC != null) {
                allPlugins.set(i, newPC);
            }
        }

        pluginClassLoader = new PluginClassLoader(classLoaders);

        externalPluginClassLoader = new PluginClassLoader(externalLoaders);
    }

    /**
     * Updates the plugin help index page.
     */
    static void updateHelpIndex() {
        File helpindex = new File(
                getPropertyFolderManager().getPluginFolder(),
                "VRL/help/plugin-index.html");

        TextSaver textSaver = new TextSaver();

        HTMLMenuGenerator menuGenerator =
                new HTMLMenuGenerator("VRL Plugin Help Index",
                "<p>The list below contains all installed VRL plugins."
                + " Click on an item to open the desired help page.</p>\n");

        for (PluginConfigurator pC : plugins.values()) {

            String pluginIdentifier = pC.getIdentifier().toString();
            String pluginName = pC.getIdentifier().getName();

            menuGenerator.addMenuEntry(pluginIdentifier,
                    "../../" + pluginName + "/" + PluginDataController.HELP + "/index.html");
        }

        String htmlFile = menuGenerator.render();

        try {
            textSaver.saveFile(htmlFile, helpindex, ".html");
        } catch (IOException ex) {
            Logger.getLogger(VRL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns the VRL plugin configurator.
     *
     * @return the VRL plugin configurator
     */
    public static PluginConfigurator getVRLPlugin() {
        return plugins.get("VRL");
    }

    /**
     * Returns the VRL plugin data controller.
     *
     * @return the VRL plugin data controller
     */
    public static PluginDataController getVRLPluginDataController() {
        return pluginsDataControllers.get("VRL");
    }

    /**
     * Determines whether installation should be performed for the specified
     * plugin.
     *
     * @param p plugin configurator
     * @param pD plugin data controller
     * @return <code>true</code> if initialization is necessary;
     * <code>false</code> otherwise
     */
    private static boolean needsInstall(
            PluginConfigurator p, PluginDataController pD) {

        ConfigurationFile config = pD.getConfiguration();

        boolean loaded = config.load();

        boolean notInstalled = !loaded || !config.containsProperty(
                PluginConfigurator.VERSION_KEY)
                || !config.containsProperty(
                PluginConfigurator.TIMESTAMP_KEY);

        if (notInstalled) {
            return true;
        }

        String installedVersion = config.getProperty(
                PluginConfigurator.VERSION_KEY);

        // version differs, call install
        boolean versionDiffers = p.getIdentifier().
                getVersion().compareTo(new VersionInfo(installedVersion)) != 0;

        String installedTimestamp = config.getProperty(
                PluginConfigurator.TIMESTAMP_KEY);

        File pluginLocation = VJarUtil.getClassLocation(p.getClass());

        String currentTimestamp = "" + pluginLocation.lastModified();

        // timestamp differs, call install
        boolean timestampDiffers = !installedTimestamp.equals(currentTimestamp);

        boolean needsInstall = versionDiffers || timestampDiffers;

        // decides whether to perform content update for VRL plugin
        // this simplifies development, see BuildProperties.java for details
        if (p.getIdentifier().getName().equals("VRL")) {
            needsInstall = needsInstall && BuildProperties.CONTENTUPDATE;
        }

        return needsInstall;
    }

    /**
     * Calls the
     * {@link PluginConfigurator#install(eu.mihosoft.vrl.system.InitPluginAPI) }
     * method of the specified plugin.
     *
     * @param p plugin configurator
     * @param pD data controller of the plugin
     * @param iApi init api that shall be accessed from the plugin
     * @return <code>true</code> if install could be performed;
     * <code>false</code> otherwise
     */
    private static boolean performConfiguratorInstall(
            PluginConfigurator p, PluginDataController pD, InitPluginAPI iApi) {

        String msg =
                " --> " + p.getIdentifier().getName()
                + ": installing/updating:";

        File pluginLocation = VJarUtil.getClassLocation(p.getClass());

        System.out.println(msg);

        SplashScreenGenerator.printBootMessage(msg);

        ConfigurationFile config = pD.getConfiguration();

        boolean loaded = config.load();

        boolean installed = false;

        // perform resource install
        performContentInstall(PluginDataController.NATIVELIB, true, p, pD, iApi);
        performContentInstall(PluginDataController.HELP, false, p, pD, iApi);

        // create initial index.html if help does not exist
        File index = new File(pD.getHelpFolder(), "index.html");

        if (!index.exists()) {
            createInitialIndexFile(p, pD);
        }

        try {
            System.out.println(
                    " --> " + p.getIdentifier().getName()
                    + ": calling install()");
            // install the plugin
            p.install(iApi);
            installed = true;
        } catch (Throwable tr) {
            System.err.println(
                    " --> Error: cannot call install() method!");
            tr.printStackTrace(System.err);
        }

        config.setProperty(PluginConfigurator.VERSION_KEY,
                p.getIdentifier().getVersion().toString());

        String currentTimestamp = "" + pluginLocation.lastModified();
        config.setProperty(PluginConfigurator.TIMESTAMP_KEY,
                currentTimestamp);

        config.save();

        return installed;
    }

    /**
     * Creates an initial index.html file in the help folder of the specified
     * plugin.
     *
     * @param p plugin configurator of the desired plugin
     * @param pD plugin data controller of the specified plugin
     */
    private static void createInitialIndexFile(
            PluginConfigurator p, PluginDataController pD) {
        InitialIndexPageGenerator indexGen =
                new InitialIndexPageGenerator(p, pD);
        String initialIndexPage = indexGen.render();

        File index = new File(pD.getHelpFolder(), "index.html");

        TextSaver saver = new TextSaver();
        try {
            saver.saveFile(initialIndexPage, index, ".html");
        } catch (IOException ex) {
            Logger.getLogger(VRL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Performs installation of plugin resources, such as native libraries, help
     * documents etc.
     *
     * @param contentName name of the resource to install (e.g.,
     * <code>natives</code> or <code>help</code>)
     * @param platformSpecific defines whether content is plugin specific
     * @param p plugin configurator
     * @param pD plugin data controller
     * @param iApi ini pluign api
     */
    private static void performContentInstall(String contentName,
            boolean platformSpecific,
            PluginConfigurator p, PluginDataController pD, InitPluginAPI iApi) {

        // native install
        String archiveName = contentName + ".zip";
        File contentFolder = pD.getContentFolderByName(contentName);

        File pluginLocation = VJarUtil.getClassLocation(p.getClass());

        // native install
        if (needsInstall(p, pD)
                && providesContent(contentName, pluginLocation)) {

            System.out.print(
                    " --> installing content \"" + contentName + "\"");

            String finalResourceName = "/eu/mihosoft/vrl/plugin/content/" + contentName + "/";

            // special case for VRL:
            //
            // (paths must be different, otherwise classloader delegation will
            //  always delegate to vrl content instead of plugins)
            if (p.getIdentifier().getName().equals(
                    VRL.getVRLPlugin().getIdentifier().getName())) {
                finalResourceName = "/eu/mihosoft/vrl/rootplugin/content/" + contentName + "/";
            }


            if (platformSpecific) {
                finalResourceName += VSysUtil.getPlatformSpecificPath() + "/" + archiveName;
            } else {
                finalResourceName += archiveName;
            }

            finalResourceName = finalResourceName.replace("//", "/");

            InputStream in = p.getClass().getResourceAsStream(finalResourceName);

            // Only for compatibility reasons (11.07.2012)
            // prior to VRL-0.4.2 natives and other content is located at /eu/mihosoft/vrl/
            if (in == null) {
                finalResourceName = "/eu/mihosoft/vrl/" + contentName + "/"
                        + VSysUtil.getPlatformSpecificPath() + "/" + archiveName;

                finalResourceName = finalResourceName.replace("//", "/");

                in = p.getClass().getResourceAsStream(finalResourceName);

                if (in != null) {
                    showContentFolderDeprecationWarning();
                }
            }

            File archiveDest = contentFolder;

            if (platformSpecific) {
                archiveDest = new File(
                        contentFolder,
                        VSysUtil.getPlatformSpecificPath());
            }

            boolean installSuccessful = false;

            try {

                // ensure that parent folders of archive exist 
                archiveDest.mkdirs();

                File archiveFile = new File(archiveDest, archiveName);

                IOUtil.saveStreamToFile(in, archiveFile);
                IOUtil.unzip(archiveFile, archiveDest);

                archiveFile.delete();

                if (archiveFile.exists()) {
                    IOUtil.deleteDirectory(archiveFile);
                }

                installSuccessful = true;

            } catch (FileNotFoundException ex) {
                Logger.getLogger(VRL.class.getName()).
                        log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(VRL.class.getName()).
                        log(Level.SEVERE, null, ex);
            }


            if (installSuccessful) {
                System.out.println(" [ok]");
            } else {
                System.out.println(" [failed]");
            }

        } else {
            System.out.println(
                    " --> not installing content \"" + contentName
                    + "\" (not available or still up to date)");
        }
    }

    public static void main(String[] args) {
        VRL.initAll();
    }

    /**
     * Returns an instance of the specified class, after reloading the class
     * with the specified classloader.
     *
     * @param cls class to instantiate
     * @param classLoader classloader
     * @return instance of the specified class, after reloading the class with
     * the specified classloader
     */
    public static PluginConfigurator instanceFromClassLoader(
            Class<?> cls, ClassLoader classLoader) {
        try {
            Class<?> newCls = classLoader.loadClass(cls.getName());

            return (PluginConfigurator) newCls.newInstance();

        } catch (InstantiationException ex) {
            Logger.getLogger(VRL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(VRL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(VRL.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Returns a plugin classloader for the specified configurator class, using
     * the specified classloader as parent.
     *
     * @param cls plugin configurator class
     * @param parent parent classloader
     * @return a plugin classloader for the specified configurator class
     */
    public static ClassLoader createInternalPluginClassLoader(
            Class<?> cls, ClassLoader parent) {
        URLClassLoader pluginClsLoader = null;
        try {
            pluginClsLoader =
                    new URLClassLoader(
                    new URL[]{VJarUtil.getClassLocation(cls).
                        toURI().toURL()}, parent);
        } catch (MalformedURLException ex) {
            Logger.getLogger(VRL.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        if (pluginClsLoader == null) {
            throw new IllegalStateException(
                    "cannot create internal plugin classloader");
        }

        return pluginClsLoader;
    }

    /**
     * Returns a plugin classloader for the specified configurator class, using
     * the specified classloader as parent.
     *
     * @param cls plugin configurator class
     * @param parentCL parent classloader
     * @return a plugin classloader for the specified configurator class
     */
    private static ClassLoader createExternalPluginClassLoader(
            PluginConfigurator plugin,
            ClassLoader parentCL, ClassLoader internalCL) {
        PolicyClassLoader pluginClsLoader = null;

        try {
            pluginClsLoader = new PolicyClassLoader(
                    parentCL, internalCL, plugin.getAccessPolicy());
        } catch (Throwable tr) {
            Logger.getLogger(VRL.class.getName()).
                    log(Level.SEVERE, null, tr);
        }

        if (pluginClsLoader == null) {
            throw new IllegalStateException(
                    "cannot create external plugin classloader");
        }

        return pluginClsLoader;
    }

    /**
     * Removes all plugins.
     */
    private static void removeAllPlugins() {
        plugins.clear();
        allPlugins.clear();

        throw new UnsupportedOperationException(
                "classloader unloading not working for plugins!");
    }

    /**
     * Registers a canvas. The canvas will be stored as weak reference. Thus, it
     * is not necessary to unregister the canvas to allow garbage collection.
     *
     * @param c canvas to add
     */
    public static void addCanvas(Canvas c, Collection<PluginDependency> usedPlugins) {


        registrationError = null; // we don't want to show old error messages

        clearMenus();

        canvasList.add(new WeakReference<Canvas>(c));

        // clean the reference list from time to time
        if (canvasList.size() % REFRESH_INTERVAL == 0) {
            cleanupCanvasList();
        }

        PluginAPI api = null;

        // if the canvas supports reflection create an api instance that
        // provides access to the type factory etc.
        if (c instanceof VisualCanvas) {
            api = new VPluginAPIImpl((VisualCanvas) c);
        } else {
            api = new PluginAPIImpl(c);
        }

        System.out.println(">> register plugins with canvas: ");

        Collection<ClassLoader> usedClassLoaders = new ArrayList<ClassLoader>();

        // add all external classloaders of used plugins to canvas
        if (c instanceof VisualCanvas) {
            ((VisualCanvas) c).setClassLoader(
                    new VClassLoader(new PluginClassLoader(usedClassLoaders)));
        }

        // register plugins with this canvas via the corresponding api
        // object
        for (PluginConfigurator plugin : plugins.values()) {

            // if not vrl plugin check whether we need to register with canvas
            if (!plugin.getIdentifier().getName().equals("VRL")) {
                boolean used = false;

                for (PluginDependency pDep : usedPlugins) {
                    if (pDep.verify(plugin.getIdentifier())) {
                        used = true;
                        break;
                    }
                }

                if (!used) {
                    continue;
                }
            }

            ClassLoader extLoader = externalCLMap.get(
                    plugin.getIdentifier().getName());

            if (extLoader != null) {
                usedClassLoaders.add(extLoader);
            }

            System.out.print(
                    " --> " + plugin.getIdentifier().toString());
            try {
                // set the configurator to optinally add export rules for 
                // components and type representations
                if (api instanceof VPluginAPIImpl) {
                    ((VPluginAPIImpl) api).setConfigurator(plugin);
                }

                plugin.register(api);

            } catch (Throwable tr) {
                if (registrationError == null) {
                    registrationError = "";
                }
                System.out.println(" (failed)");
                tr.printStackTrace(System.err);
                registrationError += "<br><br><b>"
                        + plugin.getIdentifier().getName()
                        + ":</b><br><br>" + tr.toString();
            }

            System.out.println(" (ok)");
        }

        String msg = "";

        String messageType = "";

        if (initPluginError != null) {
            msg += initPluginError;
            messageType = "initialize";
        }

        if (registrationError != null) {
            msg += "<br>" + registrationError;

            if (!messageType.trim().isEmpty()) {
                messageType += "/";
            }

            messageType += "register";
        }

        if (!msg.trim().isEmpty()) {
            c.getMessageBox().addMessage("Cannot " + messageType + " plugins:",
                    msg, MessageType.ERROR);
        }

        initPluginError = null; // we don't want to show old error messages

        // create the canvas menu. the canvas menu controller has been already
        // configured by the plugins
        c.getCanvasMenuController().buildMenu(
                new MenuAdapter(((VisualCanvas) c).getPopupMenu()), c);

        // update menus
        updateMenus();
    }

    /**
     * <p> Returns all Canvas instances that are currently in use. </p> <p>
     * <b>Note:</b> for performance critical use cases cache the result of this
     * method because it must convert weak references to a normal list. On the
     * other hand, to be sure you only get Canvas instances that are really in
     * use, you should not keep the results too long. This prevents the garbage
     * collector from releasing the references. </p>
     *
     * @return all Canvas instances that are currently in use
     */
    public static Collection<Canvas> getCanvases() {
        ArrayList<Canvas> result = new ArrayList<Canvas>();

        for (WeakReference<Canvas> wR : canvasList) {
            Canvas c = wR.get();
            if (c != null) {
                result.add(wR.get());
            }
        }

        return result;
    }

    /**
     * Cleans up the weak ref canvas list.
     */
    private static void cleanupCanvasList() {
        ArrayList<WeakReference<Canvas>> delList =
                new ArrayList<WeakReference<Canvas>>();

        for (WeakReference<Canvas> c : canvasList) {
            if (c.get() == null) {
                delList.add(c);
            }
        }

        for (WeakReference<Canvas> c : delList) {
            canvasList.remove(c);
        }
    }

    /**
     * Adds all plugins in the given file or folder.
     *
     * @param file file or folder that contains the plugins
     */
    public static void addPlugins() {

        ArrayList<File> jarFiles = IOUtil.listFiles(
                new File(Constants.PLUGIN_DIR), new String[]{".jar"});

        for (File f : jarFiles) {

            URL[] urls = new URL[1];

            try {
                urls[0] = f.toURI().toURL();
            } catch (MalformedURLException ex) {
                Logger.getLogger(VRL.class.getName()).
                        log(Level.SEVERE, null, ex);
            }

            URLClassLoader classLoader = new URLClassLoader(urls);

            if (PluginCacheController.needsUpdate(f)) {
                loadPlugin(f, classLoader);
                PluginCacheController.saveCache(f);
            } else {
                PluginCacheController.addPluginsFromCache(f);
            }
        }


//        if (!file.exists()) {
//            System.err.println(" --> Error: the file \"" + file.getName()
//                    + "\" does not exist!");
//            return;
//        }



//        if (file.isDirectory()) {
//            String message = ">> searching for plugins in \""
//                    + file.getAbsolutePath() + "\".";
//            System.out.println(message);
//
//            SplashScreenGenerator.printBootMessage(message);
//
//            for (File f : file.listFiles()) {
//
//                if (f.isFile() && f.getName().toLowerCase().endsWith(".jar")) {
//                    ClassLoader classLoader = loaders.get(f);
//                    VParamUtil.throwIfNull(classLoader);
//                    loadPlugin(f, classLoader);
//                } else if (f.isDirectory()) {
//                    addPlugins(f);
//                }
//            }
//        } else if (file.getName().toLowerCase().endsWith(".jar")) {
//
//            ClassLoader classLoader = loaders.get(file);
//            VParamUtil.throwIfNull(classLoader);
//            loadPlugin(file, classLoader);
//        }
    }

    /**
     * Adds a plugin to the plugin set.
     *
     * @param plugin the plugin to add
     */
    static void addPlugin(final PluginConfigurator plugin, File f) {
        PluginConfigurator registeredPlugin =
                plugins.get(plugin.getIdentifier().getName());

        allPlugins.add(plugin);

        if (registeredPlugin != null) {
            String errorMsg = " --> Error: cannot add plugin \""
                    + plugin.getIdentifier().toString()
                    + "\" because, according to its name, it is a duplicate of"
                    + " the already exiting plugin \""
                    + registeredPlugin.getIdentifier()
                    + "\".";

            // TODO enable this again, this was just done to prevent ugly errors
            // when running from netbeans
//            if (registrationError != null) {
//                registrationError += "<br>" + errorMsg;
//            } else {
//                registrationError = errorMsg;
//            }

            System.err.println(VTerminalUtil.red(errorMsg));
        } else {

            if (!plugin.getIdentifier().getName().equals("VRL")) {
                String msg = " --> plugin \""
                        + plugin.getIdentifier().getName()
                        + "\" found";

                System.out.println(VTerminalUtil.green(msg));
            }

            plugins.put(plugin.getIdentifier().getName(), plugin);
        }

        if (f != null) {
            addPluginToGroup(f, plugin);
        }
    }

    /**
     * Initializes Java libraries VRL depends on.
     */
    public static void initLibs() {
        ClassPathUpdater.addAllJarsInDirectory(
                new File(Constants.LIB_DIR));
        ClassPathUpdater.addAllJarsInDirectory(
                new File(Constants.CUSTOM_LIB_DIR));
    }

    /**
     * Evaluates plugin arguments. Allows the definition of custom plugin
     * locations.
     */
    public static void evaluatePluginArguments(String[] args) {
        String enablePlugins = VArgUtil.getArg(args, "-plugins");
        String checksumTest = VArgUtil.getArg(args, "-plugin-checksum-test");
        System.out.println(">> Plugin Options:");

        if (checksumTest != null) {
            if (checksumTest.equals("yes")) {
                System.out.println(
                        " --> enabling checksum test");
                PluginCacheController.enableChecksums(true);
            } else if (checksumTest.equals("no")) {
                System.out.println(
                        " --> disabling checksum test");
                PluginCacheController.enableChecksums(false);
            } else {
                System.out.println(
                        " --> -plugin-checksum-test: "
                        + "wrong value specified! Valid values:"
                        + " [yes/no]. Using default.");
            }
        }

        if (enablePlugins != null) {
            System.out.println(" --> enabling plugins, path: " + enablePlugins);
            Constants.PLUGIN_DIR = enablePlugins;

            if (!new File(Constants.PLUGIN_DIR).isDirectory()) {
                System.err.println(
                        " --> specified plugin path does not exist "
                        + "or is no directory!");
            }

            UninstallPluginController.performPendingRequests();
            VRL.installUpdates(null);

            VRL.addPlugins();

        } else {
            System.out.println(
                    " --> no plugin path specified, using default.");

            UninstallPluginController.performPendingRequests();
            VRL.installUpdates(null);

            VRL.addPlugins();
        }
    }

    /**
     * Loads the plugins in the specified file.
     *
     * @param f file that contains the plugins to load
     */
    private static void loadPlugin(final File f,
            ClassLoader searchClassLoader) {
        loadPlugin(f, new PluginLoadAction() {
            @Override
            public void loaded(PluginConfigurator pC) {
                addPlugin(pC, f);
            }
        }, searchClassLoader);
    }

    /**
     * Loads the plugins in the specified file.
     *
     * @param f file that contains the plugins to load
     */
    private static void loadPlugin(File f, PluginLoadAction pA,
            ClassLoader searchClassLoader) {
        if (f.isFile() && f.getName().toLowerCase().endsWith(".jar")) {

            String message = ">> searching for plugins in \""
                    + f.getAbsolutePath() + "\".";
            System.out.println(message);

            SplashScreenGenerator.printBootMessage(message);

            // retrieve all classes in this jar file via urlclassloader
            Collection<Class<?>> classes =
                    VJarUtil.loadClasses(f, searchClassLoader);

            boolean platformSupported = isPlatformSupported(f);

            boolean isPlugin = false;

            // search for configurators
            for (Class<?> cls : classes) {

                if (PluginConfigurator.class.isAssignableFrom(cls)) {
                    try {

                        boolean interfaceOrAbstract = cls.isInterface()
                                || Modifier.isAbstract(cls.getModifiers());
                        boolean isVrlPluginClass =
                                cls.getClass().equals(VRLPlugin.class);

                        if (!interfaceOrAbstract && !isVrlPluginClass) {
                            isPlugin = true;

                            PluginConfigurator pC =
                                    (PluginConfigurator) cls.newInstance();

                            if (platformSupported) {
                                pA.loaded(pC);
                            } else {
                                invalidPlugins.add(pC.getIdentifier().getName());
                                String errorMsg =
                                        " --> Error: cannot add plugin \""
                                        + pC.getIdentifier().toString()
                                        + "\" because it depends on native"
                                        + " libraries that are not"
                                        + " available for your platform (<b>"
                                        + VSysUtil.getPlatformInfo() + "</b>).";
                                if (registrationError != null) {
                                    initPluginError += "<br>" + errorMsg;
                                } else {
                                    initPluginError = errorMsg;
                                }

                                System.err.println(VTerminalUtil.red(errorMsg));
                            }
                        }
                    } catch (InstantiationException ex) {
                        Logger.getLogger(VRL.class.getName()).
                                log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(VRL.class.getName()).
                                log(Level.SEVERE, null, ex);
                    }
                }
            } // end for class

            if (isPlugin) {
                try {
                    Collection<String> entries =
                            VJarUtil.getEntryNamesFromStream(
                            new JarInputStream(new FileInputStream(f)));
                    addProvidedEntries(f.getName(), entries);
                } catch (IOException ex) {
                    Logger.getLogger(VRL.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }
        } else {
            System.err.println(VTerminalUtil.red(" --> Error: the file \""
                    + f.getName()
                    + "\" is no valid .jar-Archive!"));
        }
    }

    /**
     * Indicates whether the specified plugin supports the current os/platform.
     *
     * @param f file that contains the native libraries
     * @see VSysUtil#SUPPORTED_OPERATING_SYSTEMS
     */
    private static boolean isPlatformSupported(File f) {

        String nativeFolderPath = "eu/mihosoft/vrl/plugin/content/"
                + PluginDataController.NATIVELIB;

        String archiveName = PluginDataController.NATIVELIB + ".zip";

        String platformLocation =
                nativeFolderPath + "/"
                + VSysUtil.getPlatformSpecificPath() + "/" + archiveName;

        System.out.println(">> platform location: " + platformLocation);

        platformLocation = platformLocation.replace("//", "/");

        if (providesContent(PluginDataController.NATIVELIB, f)
                && !VJarUtil.containsEntry(f, platformLocation)) {

            return _isPlatformSupported_Deprecated(f);
        }

        return true;
    }

    /**
     * Indicates whether the specified plugin supports the current os/platform.
     *
     * Only for compatibility reasons (11.07.2012) prior to VRL-0.4.2 natives
     * and other content is located at /eu/mihosoft/vrl/
     *
     * @param f file that contains the native libraries
     * @see VSysUtil#SUPPORTED_OPERATING_SYSTEMS
     */
    private static boolean _isPlatformSupported_Deprecated(File f) {

        String nativeFolderPath = "eu/mihosoft/vrl/"
                + PluginDataController.NATIVELIB + "/";

        String archiveName = PluginDataController.NATIVELIB + ".zip";

        String platformLocation =
                nativeFolderPath
                + VSysUtil.getPlatformSpecificPath() + "/" + archiveName;

        platformLocation = platformLocation.replace("//", "/");

        if (providesContent(PluginDataController.NATIVELIB, f)
                && !VJarUtil.containsEntry(f, platformLocation)) {
            return false;
        }

        showContentFolderDeprecationWarning();

        return true;
    }

    /**
     * Only for compatibility reasons (11.07.2012) prior to VRL-0.4.2 natives
     * and other content is located at /eu/mihosoft/vrl/
     */
    private static void showContentFolderDeprecationWarning() {
        System.out.println(
                " --> Warning: content location deprecated since VRL-0.4.2");
        System.out.println(
                "              use new location for natives and other content");
        System.out.println(
                "               - old location: /eu/mihosoft/vrl/");
        System.out.println(
                "               - new location: /eu/mihosoft/vrl/plugin/content/");
    }

    /**
     * Indicates whether the specified file contains the specified content.
     *
     * @param contentName the content to check
     * @param f file to check
     *
     * @return <code>true</code> if the specified file contains the specified
     * resource; <code>false</code> otherwise
     */
    public static boolean providesContent(String contentName, File f) {

        String contentFolderPath = "eu/mihosoft/vrl/plugin/content/" + contentName + "/";
        String rootPluginFolderPath = "eu/mihosoft/vrl/rootplugin/content/" + contentName + "/";

        boolean contains = VJarUtil.containsEntry(f, contentFolderPath);

        // maybe we are a root plugin (usually only VRL)
        // this is due to classloader delegation
        // (root content is available for all plugins)
        if (!contains) {
            contains = VJarUtil.containsEntry(f, rootPluginFolderPath);
        }

        if (!contains) {
            // Only for compatibility reasons (11.07.2012)
            // prior to VRL-0.4.2 natives and other content is located at /eu/mihosoft/vrl/
            contentFolderPath = "eu/mihosoft/vrl/" + contentName + "/";
            contains = VJarUtil.containsEntry(f, contentFolderPath);
            if (contains) {
                showContentFolderDeprecationWarning();
            }
        }

        return contains;
    }

    /**
     * Adds native libraries to the library path of the JVM that are located in
     * <code>eu/mihosoft/vrl/plugin/content/natives/</code>. Does nothing
     * otherwise.
     *
     * @param f file that contains the native libraries
     * @param disableLibLoad disables library loading, lib location only defined
     * (is used by plugin configurators)
     * @return the folder containing the native libraries or <code>null</code>
     * if no such folder exists
     */
    static File addNativesPath(
            PluginConfigurator p,
            PluginDataController dC,
            boolean disableLibLoad) {

        File f = VJarUtil.getClassLocation(p.getClass());

        if (!providesContent(PluginDataController.NATIVELIB, f)) {
            return null;
        }

        File nativeFolder = dC.getNativeLibFolder();
        File nativeLibs = new File(nativeFolder,
                VSysUtil.getPlatformSpecificPath());

        if (!disableLibLoad) {
            VSysUtil.loadNativeLibrariesInFolder(nativeLibs);
        }

        return nativeLibs;
    }

    /**
     * Adds provided entries. If an entry is provided by two files an error
     * message will be generated.
     *
     * @param pluginFile name of the plugin file that provides the specified
     * entries
     * @param entries entries provided by the specified plugin
     */
    private static void addProvidedEntries(String pluginFile,
            Collection<String> entries) {
        for (String entry : entries) {

            boolean isDirectory = entry.endsWith("/");

            if (isDirectory) {
                continue;
            }

            if (providedEntry.get(entry) != null) {
//                String errorMsg = " --> Error: entry \"" + entry
//                        + "\" already provided by \""
//                        + providedEntry.get(entry) + "\"!";
//
//                System.err.println(VTerminalUtil.red(errorMsg));
//
//                if (registrationError != null) {
//                    registrationError += "<br>" + errorMsg;
//                } else {
//                    registrationError = errorMsg;
//                }
            } else {
                providedEntry.put(entry, pluginFile);
            }

        } // end for each entry
    }

    public static void clearMenus() {
        getActionDelegator().getFileMenuController().
                clear(menus.get(VActionDelegator.FILE_MENU), false);

        getActionDelegator().getViewMenuController().
                clear(menus.get(VActionDelegator.VIEW_MENU), false);

        getActionDelegator().getEditMenuController().
                clear(menus.get(VActionDelegator.EDIT_MENU), false);
        getActionDelegator().getToolMenuController().
                clear(menus.get(VActionDelegator.TOOL_MENU), false);

        getActionDelegator().getDebugMenuController().
                clear(menus.get(VActionDelegator.DEBUG_MENU), false);

        getActionDelegator().getInfoMenuController().
                clear(menus.get(VActionDelegator.INFO_MENU), false);

        updateHelpIndex();
    }

    public static void updateMenus() {

        getActionDelegator().getFileMenuController().
                buildMenu(menus.get(VActionDelegator.FILE_MENU), null);

        getActionDelegator().getViewMenuController().
                buildMenu(menus.get(VActionDelegator.VIEW_MENU), null);

        getActionDelegator().getEditMenuController().
                buildMenu(menus.get(VActionDelegator.EDIT_MENU), null);
        getActionDelegator().getToolMenuController().
                buildMenu(menus.get(VActionDelegator.TOOL_MENU), null);

        getActionDelegator().getDebugMenuController().
                buildMenu(menus.get(VActionDelegator.DEBUG_MENU), null);

        getActionDelegator().getInfoMenuController().
                buildMenu(menus.get(VActionDelegator.INFO_MENU), null);
    }

    /**
     * @param aEditMenu the editMenu to set
     */
    public static void registerFileTemplatesMenu(MenuAdapter fileMenu) {
        getFileTemplatesMenuController().buildMenu(fileMenu, null);
    }

    /**
     * @param aFileMenu the fileMenu to set
     */
    public static void registerFileMenu(MenuAdapter fileMenu) {
        menus.put(VActionDelegator.FILE_MENU, fileMenu);
        getActionDelegator().getFileMenuController().buildMenu(fileMenu, null);
    }

    /**
     * @param aViewMenu the viewMenu to set
     */
    public static void registerViewMenu(MenuAdapter viewMenu) {
        menus.put(VActionDelegator.VIEW_MENU, viewMenu);
        getActionDelegator().getViewMenuController().buildMenu(viewMenu, null);
    }

    /**
     * @param aEditMenu the editMenu to set
     */
    public static void registerEditMenu(MenuAdapter editMenu) {
        menus.put(VActionDelegator.EDIT_MENU, editMenu);
        getActionDelegator().getEditMenuController().buildMenu(editMenu, null);
    }

    /**
     * @param aViewMenu the viewMenu to set
     */
    public static void registerToolMenu(MenuAdapter toolMenu) {
        menus.put(VActionDelegator.TOOL_MENU, toolMenu);
        getActionDelegator().getToolMenuController().buildMenu(toolMenu, null);
    }

    /**
     * @param aEditMenu the editMenu to set
     */
    public static void registerDebugMenu(MenuAdapter debugMenu) {
        menus.put(VActionDelegator.DEBUG_MENU, debugMenu);
        getActionDelegator().getDebugMenuController().buildMenu(debugMenu, null);
    }

    /**
     * @param aEditMenu the editMenu to set
     */
    public static void registerInfoMenu(MenuAdapter infoMenu) {
        menus.put(VActionDelegator.INFO_MENU, infoMenu);
        getActionDelegator().getInfoMenuController().buildMenu(infoMenu, null);
    }

    /**
     * @param aEditMenu the editMenu to set
     */
    public static void registerStyleMenu(MenuAdapter infoMenu) {
        styleMenuController.buildMenu(infoMenu, null);
    }

    /**
     * @param infoMenu the info menu to set
     */
    public static void registerPluginMenu(MenuAdapter infoMenu,
            MenuAdapter uninstallMenu) {

        for (final PluginConfigurator p : plugins.values()) {
            pluginMenuController.addAction(
                    new VAction(p.getIdentifier().toString(), null) {
                        @Override
                        public void actionPerformed(ActionEvent e, Object owner) {
                            System.out.println(
                                    "Plugin: " + p.getIdentifier().toString());

                            Canvas canvas = null;

                            if (!getCanvases().isEmpty()) {

                                for (Canvas c : getCanvases()) {
                                    if (VSwingUtil.isWindowChild(c)) {
                                        canvas = c;
                                        break;
                                    }
                                }

                                String copyrightStatementText =
                                        p.getCopyrightInfo().
                                        getCopyrightStatement();

                                if (copyrightStatementText == null
                                        || copyrightStatementText.isEmpty()) {
                                    copyrightStatementText = "info missing";
                                }

                                String description = p.getDescription();

                                if (description == null
                                        || description.isEmpty()) {
                                    description = "description missing";
                                }

                                Box container = Box.createVerticalBox();

                                HTMLLabel label = new HTMLLabel(
                                        "<html><body color=white><div align=Center>"
                                        + "<p><b><font size=10>&nbsp;&nbsp;&nbsp;&nbsp;"
                                        + p.getIdentifier().getName()
                                        + "&nbsp;&nbsp;&nbsp;&nbsp;</b></p><br>"
                                        + "<p><font size=4>"
                                        + p.getIdentifier().getVersion().toString()
                                        + "</p><br><br>"
                                        + "<p><b>Description:</b></p><br>"
                                        + description + "<br><br>"
                                        + "<p><b>Copyright:</b></p><br>"
                                        + copyrightStatementText
                                        + "<br>"
                                        + "</div></body></html>");

                                label.setAlignmentX(0.5f);

                                container.add(label);

                                VButton copyrightBtn =
                                        new VButton("Copyright Information");

                                copyrightBtn.setAlignmentX(0.5f);

                                copyrightBtn.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        Canvas currentCanvas = null;

                                        for (Canvas c : getCanvases()) {
                                            if (VSwingUtil.isWindowChild(c)) {
                                                currentCanvas = c;
                                                break;
                                            }
                                        }
                                        CopyrightDialog.showCopyrightDialog(
                                                currentCanvas, p.getCopyrightInfo());
                                    }
                                });

                                if (!p.getCopyrightInfo().isPlainText()
                                        && (p.getCopyrightInfo().getCopyrightStatement() == null
                                        || p.getCopyrightInfo().getLicense() == null
                                        || p.getCopyrightInfo().getProjectName() == null
                                        || p.getCopyrightInfo().getProjectPage() == null)) {
                                    copyrightBtn.setEnabled(false);
                                }

                                VButton preferencesBtn =
                                        new VButton("Preferences");

                                preferencesBtn.setAlignmentX(0.5f);

                                container.add(Box.createVerticalStrut(5));
                                container.add(copyrightBtn);
                                container.add(Box.createVerticalStrut(20));
                                container.add(preferencesBtn);
                                container.add(Box.createVerticalStrut(20));

                                if (p.getPreferencePane() == null
                                        || p.getPreferencePane().getInterface() == null) {
                                    preferencesBtn.setEnabled(false);
                                }

                                preferencesBtn.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        final JFrame f = new JFrame(
                                                "Preferences: "
                                                + p.getIdentifier().getName());
                                        f.setDefaultCloseOperation(
                                                JFrame.DISPOSE_ON_CLOSE);

                                        PreferencePane pane = p.getPreferencePane();
                                        pane.setControl(new PreferencePaneControl() {
                                            @Override
                                            public void close() {
                                                f.setVisible(false);
                                                f.dispose();
                                            }
                                        });

                                        f.add(pane.getInterface());
                                        f.setMinimumSize(new Dimension(400, 300));
                                        f.pack();

                                        f.setVisible(true);

                                        Canvas currentCanvas = null;

                                        for (Canvas c : getCanvases()) {
                                            if (VSwingUtil.isWindowChild(c)) {
                                                currentCanvas = c;
                                                break;
                                            }
                                        }

                                        VGraphicsUtil.centerOnWindow(
                                                VSwingUtil.getTopmostParent(
                                                currentCanvas), f);
                                    }
                                });

                                VButton helpBtn =
                                        new VButton("Help");
                                helpBtn.setMinimumSize(
                                        new Dimension(100,
                                        helpBtn.getMinimumSize().height));
                                helpBtn.setAlignmentX(0.5f);
                                container.add(helpBtn);

                                // help
                                final File helpIndex = new File(
                                        pluginsDataControllers.get(
                                        p.getIdentifier().getName()).
                                        getHelpFolder(), "index.html");

                                if (!helpIndex.exists()) {
                                    helpBtn.setEnabled(false);
                                }

                                helpBtn.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent ae) {
                                        VSysUtil.openURI(helpIndex.toURI());
                                    }
                                });

//                                new CanvasLabel(new CopyrightInfo2HTML(p.getCopyrightInfo()).toString())

                                VDialog.showDialogWindow(
                                        canvas, "Plugin Information",
                                        container, "Close", false);

//                                VDialog.showMessageDialog(canvas, "Plugin Information",
//                                        container);
                            }
                        }
                    });
        }

        pluginMenuController.buildMenu(infoMenu, null);
        pluginMenuController.clear(null, false);

        for (final PluginConfigurator p : allPlugins) {

            if (p.getClass().getName().equals(VRLPlugin.class.getName())) {
                continue;
            }

            uninstallPluginMenuController.addAction(
                    new VAction(p.getIdentifier().toString(), null) {
                        @Override
                        public void actionPerformed(ActionEvent e, Object owner) {
                            System.out.println(
                                    "Uninstall Plugin?: "
                                    + p.getIdentifier().toString());

                            Canvas canvas = null;

                            if (!getCanvases().isEmpty()) {

                                for (Canvas c : getCanvases()) {
                                    if (VSwingUtil.isWindowChild(c)) {
                                        canvas = c;
                                        break;
                                    }
                                }

                                if (VDialog.showConfirmDialog(
                                        canvas, "Uninstall Plugin?",
                                        "<html><div align=Center>"
                                        + "<p>Shall the plugin "
                                        + Message.EMPHASIZE_BEGIN
                                        + p.getIdentifier()
                                        + Message.EMPHASIZE_END
                                        + " be uninstalled?</p>"
                                        + "</div></html>",
                                        VDialog.DialogType.YES_NO)
                                        == VDialog.AnswerType.YES) {

                                    if (e.getSource() instanceof AbstractButton) {
                                        AbstractButton item =
                                                (AbstractButton) e.getSource();
                                        item.setEnabled(false);
                                    }

                                    // call uninstall method
                                    // check whether to install/update this plugin
                                    System.out.println(
                                            " --> " + p.getIdentifier().getName()
                                            + ": calling uninstall()");
                                    try {

                                        InitPluginAPI initAPI =
                                                new InitPluginAPIImpl(
                                                pluginsDataControllers.get(
                                                p.getIdentifier().getName()));

                                        // uninstall the plugin
                                        p.uninstall(initAPI);
                                    } catch (Throwable tr) {
                                        System.err.println(
                                                "--> Error: cannot call uninstall() method!");
                                        tr.printStackTrace(System.err);
                                    }


                                    UninstallPluginController.addRequest(p);

                                    canvas.getMessageBox().addMessage(
                                            "Uninstalled Plugin:",
                                            ">> the plugin "
                                            + p.getIdentifier()
                                            + " has been uninstalled. "
                                            + "Restart VRL-Studio to complete "
                                            + "this operation.",
                                            MessageType.INFO);

                                }
                            }
                        }
                    });
        }

        uninstallPluginMenuController.buildMenu(uninstallMenu, null);
        uninstallPluginMenuController.clear(null, false);
    }

    /**
     * @return the actionDelegator
     */
    static VActionDelegator getActionDelegator() {
        return actionDelegator;
    }

    public static void installPlugin(File f, InstallPluginAction installAction) {

        if (installAction != null) {
            installAction.analyzeStart(f);
        }

        File src = f;
        File finalDest = new File(Constants.PLUGIN_DIR + "/" + f.getName());

        boolean install = true;

        if (installAction != null && finalDest.isFile()) {
            installAction.analyzeStop(f);
            install = installAction.overwrite(src, finalDest);
            installAction.analyzeStart(f);
        }

        if (!install) {
            installAction.analyzeStop(f);
            return;
        }

        File dest = new File(
                getPropertyFolderManager().getPluginUpdatesFolder().
                getAbsoluteFile(),
                f.getName());

        if (!checkIfIsPluginAndCreateCacheFile(f)) {
            if (installAction == null) {
                throw new IllegalArgumentException(
                        "The given file is no VRL plugin!");
            } else {
                installAction.isNoPlugin(f);
                installAction.analyzeStop(f);

                return;
            }
        }

        if (installAction != null) {
            installAction.analyzeStop(f);
        }

        try {
            IOUtil.copyFile(src, dest);

            if (installAction != null) {
                installAction.installed(src);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(VRL.class.getName()).
                    log(Level.SEVERE, null, ex);
            if (installAction != null) {
                installAction.cannotInstall(ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(VRL.class.getName()).
                    log(Level.SEVERE, null, ex);
            if (installAction != null) {
                installAction.cannotInstall(ex);
            }
        }
    }

    /**
     * Installs plugin updates.
     *
     * @param action action that reacts on errors etc.
     */
    private static void installUpdates(UpdatePluginAction action) {

        ArrayList<File> pluginFiles = IOUtil.listFiles(
                getPropertyFolderManager().getPluginUpdatesFolder(),
                new String[]{".jar"});

        if (!pluginFiles.isEmpty()) {
            SplashScreenGenerator.printBootMessage(">> updating plugins...");
        }

        for (File f : pluginFiles) {

            String msg = "installing \"" + f.getName() + "\"";

            SplashScreenGenerator.printBootMessage(">> " + msg);

            System.out.println(" --> " + msg);

            File destination = new File(
                    getPropertyFolderManager().getPluginFolder(),
                    f.getName());

            if (!IOUtil.move(f, destination)) {

                IOException ex = new IIOException(
                        "Cannot move file: " + f + " to plugin folder!");

                if (action != null) {
                    action.updateFailed(ex);
                } else {
                    Logger.getLogger(VRL.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Returns plugins that are marked for update (this is just the content of
     * the plugin-updates folder)
     *
     * @return plugins that are marked for update
     */
    public static Collection<PluginConfigurator> getUpdates() {
        ArrayList<File> jarFiles = IOUtil.listFiles(
                getPropertyFolderManager().getPluginUpdatesFolder(),
                new String[]{".jar"});

        final ArrayList<PluginConfigurator> result =
                new ArrayList<PluginConfigurator>();

        for (File f : jarFiles) {
            try {
                final VURLClassLoader clsLoader =
                        new VURLClassLoader(new URL[]{f.toURI().toURL()});

                loadPlugin(
                        f, new PluginLoadAction() {
                    @Override
                    public void loaded(PluginConfigurator pC) {
                        result.add(pC);
                        clsLoader.close();
                    }
                },
                        clsLoader);
            } catch (MalformedURLException ex) {
                Logger.getLogger(VRL.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }

    /**
     * Loads the specified plugin.
     *
     * @param f plugin file
     * @return plugin configurators in the plugin file
     */
    public static Collection<PluginConfigurator> loadPlugins(File f) {
        final ArrayList<PluginConfigurator> result =
                new ArrayList<PluginConfigurator>();

        try {
            loadPlugin(
                    f, new PluginLoadAction() {
                @Override
                public void loaded(PluginConfigurator pC) {
                    result.add(pC);
                }
            },
                    new URLClassLoader(new URL[]{f.toURI().toURL()}));
        } catch (MalformedURLException ex) {
            Logger.getLogger(VRL.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        return result;
    }

    /**
     * Checks whether the specified file is a plugin and create a plugin cache
     * file in the plugin updates folder. Plugins are marked as installed.
     * Therefore, they are added to the {@link #installed} list.
     */
    public static boolean checkIfIsPluginAndCreateCacheFile(File f) {
        Collection<PluginConfigurator> pluginConfigs = loadPlugins(f);

        System.out.println(">> Plugins in " + f);

        for (PluginConfigurator pluginConfigurator : pluginConfigs) {
            System.out.println(" --> plugin: " + pluginConfigurator.getIdentifier().getName());
        }

        if (pluginConfigs.isEmpty()) {
            return false;
        }

        PluginCacheController.createCache(f,
                getPropertyFolderManager().getPluginFolder(),
                pluginConfigs);

        return true;
    }

    /**
     * Adds a canvas style to the style menu.
     *
     * @param s style to add
     */
    public static void addStyle(final Style s) {

        if (s == null) {
            throw new IllegalArgumentException(
                    "Argument \"null\" is not supported!");
        } else if (s.getName() == null) {
            throw new IllegalArgumentException(
                    "Style name \"null\" is not supported!");
        }

        styleMenuController.addAction(new Action() {
            @Override
            public String getText() {
                return s.getName();
            }

            @Override
            public Icon getIcon() {
                return null;
            }

            @Override
            public boolean isSeparator() {
                return false;
            }

            @Override
            public void actionPerformed(ActionEvent e, Object owner) {
                for (Canvas c : VRL.getCanvases()) {
                    c.setStyle(s);
                }
            }
        });

        styles.put(s.getName(), s);
    }

    /**
     * Returns a style by name.
     *
     * @param name name of the style to be returned
     * @return a style by name or <code>null</code> if no such style exists
     */
    public static Style getStyle(String name) {
        return styles.get(name);
    }

    /**
     * @return the fileTemplatesMenuController
     */
    static MenuController getFileTemplatesMenuController() {
        return fileTemplatesMenuController;
    }

    /**
     * Returns the plugin classloader. It contains all internal plugin
     * classloaders of the plugins that are root nodes in the dependency graph.
     * <p><b>Note:</b> Only use this classloader for serialization. Using it in
     * other situations violates the plugin visibility rule. Only exported api
     * may be used from outside a plugins. In most cases
     * {@link VRL#getExternalPluginClassLoader()} sould be used.</p>
     *
     * @return the pluginClassLoader
     */
    public static ClassLoader getInternalPluginClassLoader() {
        return pluginClassLoader;
    }

    /**
     * Returns all plugins.
     *
     * @return all plugins
     */
    static Map<String, PluginConfigurator> getPlugins() {
        return plugins;
    }

    /**
     * Returns plugin configurator by name.
     *
     * @param pluginName plugin name (without version)
     * @return plugin configurator or <code>null</code> if no such configurator
     * can be found
     */
    public static PluginConfigurator getPluginConfiguratorByName(String pluginName) {
        return getPlugins().get(pluginName);
    }

    /**
     * Unlocks the property folder and calls
     * <code>System.exit()</code>.
     *
     * @param retval exit value
     */
    public static void exit(int retval) {
        getPropertyFolderManager().unlockFolder();

        for (PluginConfigurator pConf : getPlugins().values()) {
            try {
                pConf.shutdown();
            } catch (Throwable tr) {
                tr.printStackTrace(System.err);
            }
        }

        System.exit(retval);
    }

    /**
     * @return the pluginGroups
     */
    static List<String> getPluginsInGroup(String fileName) {
        List<String> result = pluginGroups.get(fileName);

        if (pluginGroups.get(fileName) == null) {
            result = new ArrayList<String>();
        }

        return result;
    }

    /**
     * @return the pluginGroups
     */
    static List<String> getPluginsInGroup(File f) {
        List<String> result = pluginGroups.get(f.getName());

        if (pluginGroups.get(f.getName()) == null) {
            result = new ArrayList<String>();
        }

        return result;
    }

    /**
     * Adds a plugin to a plugin group. If a group for the specified file name
     * already exists the plugin configurator will be added to this group. If
     * not, a new group will be created.
     *
     * @param f plugin file
     * @param pC plugin configurator
     */
    private static void addPluginToGroup(File f, PluginConfigurator pC) {

        if (pluginGroups.get(f.getName()) == null) {
            pluginGroups.put(f.getName(), new ArrayList<String>());
        }

        if (!pluginGroups.get(f.getName()).contains(pC.getIdentifier().getName())) {
            pluginGroups.get(f.getName()).add(pC.getIdentifier().getName());
        }
    }

    /**
     * Returns the external plugin classloader. It cann access all external
     * plugin classloaders of the plugins that are root nodes in the dependency
     * graph. <p><b>Note:</b> This classloader has only access to exported api.
     * If you need access to the plugin classloader for serialization you should
     * use {@link VRL#getInternalPluginClassLoader()} sould be used.</p>
     *
     * @return the external plugin classLoader
     */
    public static ClassLoader getExternalPluginClassLoader() {
        return externalPluginClassLoader;
    }

    /**
     * @return the commandline options given to the JVM
     */
    public static String[] getCommandLineOptions() {
        return VRL.commandLineOptions;
    }

    /**
     * Defines the commandline options given to the JVM.
     *
     * @param options
     */
    private static void setCommandLineOptions(String[] options) {
        VRL.commandLineOptions = options;
    }

    /**
     * @return the propertyFolderManager
     */
    public static VPropertyFolderManager getPropertyFolderManager() {
        return propertyFolderManager;
    }

    /**
     * Returns the current project controller. <p><b>Note: </b>Do not cache the
     * result as the reference may be replaced. </p>
     *
     * @return the projectController
     */
    public static VProjectController getCurrentProjectController() {
        return projectController;
    }

    /**
     * Defines the current project controller. If more than one
     * projectcontroller is used, the active controller has to be specified via
     * this method for each activity change.
     *
     * @param aProjectController the project controller to set
     */
    public static void setCurrentProjectController(
            VProjectController aProjectController) {
        projectController = aProjectController;

    }
}

/**
 * Classloader that has access to a collection of classloaders. This is used to
 * create a classloader that has access to all individual classloaders of
 * selected plugins.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class PluginClassLoader extends ClassLoader {

    private Collection<ClassLoader> classLoaders;

    public PluginClassLoader(Collection<ClassLoader> classLoaders) {
        super(ClassLoader.getSystemClassLoader());
        this.classLoaders = classLoaders;
    }

    public void addClassLoader(ClassLoader loader) {
        classLoaders.add(loader);
    }

    public void addClassLoaders(Collection<ClassLoader> loaders) {
        classLoaders.addAll(loaders);
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        Class<?> result = null;

        // main class of project cannot be used by other plugins
        if (className.equals("eu.mihosoft.vrl.user.Main")) {
            throw new ClassNotFoundException(className);
        }

        // main class of project cannot be used by other plugins
        if (className.equals("eu.mihosoft.vrl.user.VSessionMainClass")) {
            throw new ClassNotFoundException(className);
        }

        for (ClassLoader cl : classLoaders) {
            try {
                result = cl.loadClass(className);
            } catch (Throwable tr) {
            }

            if (result != null) {
                return result;
            }
        }

        throw new ClassNotFoundException(className);
    }
}

/**
 * A classloader that allows to specifiy access policies. This is used to
 * prevent plugins from accessing plugin classes that have not be exported.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class PolicyClassLoader extends ClassLoader {

    private ClassLoader internalCL;
    private AccessPolicy accessPolicy;

    public PolicyClassLoader(
            ClassLoader parentCL,
            ClassLoader internalCL,
            AccessPolicy accessPolicy) {
        super(parentCL);
        this.internalCL = internalCL;
        this.accessPolicy = accessPolicy;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {

        if (!accessPolicy.accept(className)) {
            throw new ClassNotFoundException(className);
        }

        return internalCL.loadClass(className);
    }
}

/**
 * Action that allows to react if plugin has been loaded.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
interface PluginLoadAction {

    /**
     * Called if plugin has been loaded.
     *
     * @param pC plugin that has been loaded
     */
    void loaded(PluginConfigurator pC);
}
