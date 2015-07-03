/* 
 * PluginDataController.java
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

import eu.mihosoft.vrl.io.ConfigurationFile;
import eu.mihosoft.vrl.io.IOUtil;
import java.io.File;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public final class PluginDataController {

    /**
     * Content name for resources folder.
     */
    public static final String RESOURCES = "resources";
    /**
     * Content name for help folder.
     */
    public static final String HELP = "help";
    /**
     * Content name for native libraries.
     */
    public static final String NATIVELIB = "natives";
    /**
     * Content name for config.
     */
    public static final String CONFIG = "config";
    /**
     * File name of the plugin config file.
     */
    public static final String CONFIG_FILENAME = "config.xml";
    private PluginConfigurator pC;
    private ConfigurationFile pConfig;

    public PluginDataController(PluginConfigurator pC) {
        this.pC = pC;

        VParamUtil.throwIfNull(pC);

        File configFile = new File(geConfigFolder(), CONFIG_FILENAME);


        pConfig = IOUtil.newConfigurationFile(configFile);

        // ensure that a configuration exists
        if (!configFile.exists()) {
            pConfig.save();
        }

//        try {
        pConfig.load();

//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(PluginDataController.class.getName()).
//                    log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(PluginDataController.class.getName()).
//                    log(Level.SEVERE, null, ex);
//        }
    }

//    private String getDataFolderName() {
//        return getDataFolderName(pC.getIdentifier().getName());
//    }

    private static String getDataFolderName(String pluginName) {
        return pluginName.replace(" ", "_");
    }

    /**
     * Returns the data folder of the plugin.
     * @return the data folder of the plugin
     */
    public File getDataFolder() {

        return getDataFolder(pC.getIdentifier().getName());
    }

    /**
     * Returns the data folder of the plugin
     * @param pluginName plugin name
     * @return the data folder of the plugin
     */
    private static File getDataFolder(String pluginName) {

        createFolders(pluginName);

        File dataFolder = new File(
                VRL.getPropertyFolderManager().getPluginFolder(),
                getDataFolderName(pluginName));

        return dataFolder;
    }

    /**
     * Returns the resource folder of the plugin.
     *
     * @return the resource folder of the plugin
     */
    public final File getResourceFolder() {
        return new File(getDataFolder(), RESOURCES);
    }

    /**
     * Returns the native library folder of the plugin.
     *
     * @return the native library folder of the plugin
     */
    public final File getNativeLibFolder() {
        return new File(getDataFolder(), NATIVELIB);
    }

    /**
     * Returns the help folder of the plugin.
     *
     * @return the help folder of the plugin
     */
    public final File getHelpFolder() {
        return new File(getDataFolder(), HELP);
    }

    /**
     * Returns the content folder by name.
     *
     * @param name name of the requested content
     * @return the content folder by name
     * @see #getResourceFolder()
     */
    public final File getContentFolderByName(String name) {

        return new File(getDataFolder(), name);
    }

    public final File geConfigFolder() {
        return new File(getDataFolder(), CONFIG);
    }

    private static void createFolders(String pluginName) {

        String dataFolderName = getDataFolderName(pluginName);

        File dataFolder = new File(
                VRL.getPropertyFolderManager().getPluginFolder(),
                dataFolderName);

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File resources = new File(dataFolder, RESOURCES);

        if (!resources.exists()) {
            resources.mkdirs();
        }

        File config = new File(dataFolder, CONFIG);

        if (!config.exists()) {
            config.mkdirs();
        }

        File natives = new File(dataFolder, NATIVELIB);

        if (!natives.exists()) {
            natives.mkdirs();
        }

        File help = new File(dataFolder, HELP);

        if (!help.exists()) {
            help.mkdirs();
        }
    }

    public final boolean removeFolder() {
        return removeFolder(pC.getIdentifier().getName());
    }

    public static boolean removeFolder(String pluginName) {
        return IOUtil.deleteDirectory(getDataFolder(pluginName));
    }

    public ConfigurationFile getConfiguration() {
        return pConfig;
    }
}
