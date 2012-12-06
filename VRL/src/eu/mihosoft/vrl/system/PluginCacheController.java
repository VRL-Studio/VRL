/* 
 * PluginCacheController.java
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

import eu.mihosoft.vrl.io.IOUtil;
import eu.mihosoft.vrl.io.VJarUtil;
import eu.mihosoft.vrl.visual.SplashScreenGenerator;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class PluginCacheController {

    private static boolean useChecksums = true;

    private static boolean _isCacheAvailable(File f) {
        return getCacheFile(f).exists();
    }

    public static boolean isCacheAvailable(File f) {
        return getCacheFile(f.getParentFile(), f).exists();
    }

    public static File getCacheFile(File f) {
        return new File(Constants.PLUGIN_DIR
                + "/" + f.getName() + ".xml");
    }

    private static File getCacheFile(File parent, File f) {
        return new File(parent, f.getName() + ".xml");
    }

    public static boolean isChecksumsEnabled() {
        return useChecksums;
    }

    public static void enableChecksums(boolean v) {
        useChecksums = v;
    }

//    public static void addPluginsFromCache() {
//        ArrayList<File> jarFiles = IOUtil.listFiles(
//                new File(Constants.PLUGIN_DIR), new String[]{".jar"});
//        
//        for (File f : jarFiles) {
//            addPluginsFromCache(f);
//        }
//    }
    public static void addPluginsFromCache(File f) {
        PluginCache cache = loadCache(f);

        if (cache == null) {
            return;
        }

        SplashScreenGenerator.printBootMessage(
                ">> loading plugin from cache");
        System.out.println(">> loading plugin from cache");

        for (PluginCacheEntry pEntry : cache.getEntries()) {
            try {

                URL[] urls = new URL[]{
                    new File(Constants.PLUGIN_DIR,
                    pEntry.getJarFile()).toURI().toURL()};

                ClassLoader classLoader = new URLClassLoader(urls);

                PluginConfigurator pC = (PluginConfigurator) classLoader.loadClass(
                        pEntry.getConfiguratorClass()).
                        newInstance();

                VRL.addPlugin(pC, f);

            } catch (MalformedURLException ex) {
                Logger.getLogger(PluginCacheController.class.getName()).
                        log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(PluginCacheController.class.getName()).
                        log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(PluginCacheController.class.getName()).
                        log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PluginCacheController.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Loads plugins from the specified cache file.
     *
     * @param f cache file
     * @return plugins loaded from cache file
     */
    public static Collection<PluginConfigurator> loadPluginsFromCache(File f) {

        Collection<PluginConfigurator> result =
                new ArrayList<PluginConfigurator>();

        File parentDir = f.getParentFile();

        PluginCache cache = loadCache(f);

        if (cache == null) {
            System.err.println(">> Error: cannot load cache file");
            return result;
        }

        SplashScreenGenerator.printBootMessage(
                ">> loading plugin from cache");
        System.out.println(">> loading plugin from cache");

        for (PluginCacheEntry pEntry : cache.getEntries()) {
            try {

                URL[] urls = new URL[]{
                    new File(parentDir,
                    pEntry.getJarFile()).toURI().toURL()};

                ClassLoader classLoader = new URLClassLoader(urls);

                PluginConfigurator pC = (PluginConfigurator) classLoader.loadClass(
                        pEntry.getConfiguratorClass()).
                        newInstance();

                result.add(pC);

            } catch (MalformedURLException ex) {
                Logger.getLogger(PluginCacheController.class.getName()).
                        log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(PluginCacheController.class.getName()).
                        log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(PluginCacheController.class.getName()).
                        log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PluginCacheController.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }

//    private static void loadCache() {
//        ArrayList<File> jarFiles = IOUtil.listFiles(
//                new File(Constants.PLUGIN_DIR), new String[]{".jar"});
//        
//        for (File f : jarFiles) {
//            loadCache(f);
//        }
//    }
    /**
     * Loads cache file of the specified plugin file.
     *
     * @param f plugin file
     * @return plugin cache of the specified plugin file if the cache exists;
     * <code>null</code> otherwise
     */
    private static PluginCache loadCache(File f) {

        File cacheFile = f;

        // if f does not end with .xml check whether cache file exists in plugin
        // folder
        if (!f.getName().toLowerCase().endsWith(".xml")) {
            if (!_isCacheAvailable(f)) {
                return null;
            }

            cacheFile = getCacheFile(f);
        }

        XMLDecoder d = null;
        try {
            d = new XMLDecoder(new FileInputStream(cacheFile));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PluginCacheController.class.getName()).
                    log(Level.SEVERE, null, ex);
        } finally {
            if (d != null) {
                try {
                    d.close();
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }

        PluginCache cache = null;

        try {
            cache = (PluginCache) d.readObject();
        } catch (Exception ex) {
            SplashScreenGenerator.printBootMessage(
                    ">> cannot load plugin cache");
            System.out.println(">> cannot load plugin cache");
        }

        return cache;
    }

    private static Map<String, String> generateChecksums() {

        Map<String, String> result = new HashMap<String, String>();

        ArrayList<File> jarFiles = IOUtil.listFiles(
                new File(Constants.PLUGIN_DIR), new String[]{".jar"});

        for (File f : jarFiles) {
            try {
                String jarFileChecksum =
                        IOUtil.generateSHASum(IOUtil.fileToByteArray(f));
                result.put(f.getAbsolutePath(), jarFileChecksum);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PluginCacheEntry.class.getName()).
                        log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PluginCacheEntry.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }

    public static void deleteCache(File f) {
        File cacheFile = getCacheFile(f);

        cacheFile.delete();

        if (cacheFile.exists()) {
            cacheFile.deleteOnExit();
        }
    }

    public static void deleteAllCaches() {
        ArrayList<File> jarFiles = IOUtil.listFiles(
                new File(Constants.PLUGIN_DIR), new String[]{".jar"});

        for (File f : jarFiles) {
            deleteCache(f);
        }
    }

    public static boolean needsUpdate(File f) {

        if (!isChecksumsEnabled()) {
            return !_isCacheAvailable(f);
        }

        Map<String, String> checksums = generateChecksums();

        PluginCache cache = loadCache(f);

        if (cache == null) {
            SplashScreenGenerator.printBootMessage(
                    ">> plugin cache needs update (not available)");
            System.out.println(">> plugin cache needs update (not available)");
            return true;
        }

        if (cache.getEntries().size() != checksums.size()) {
            SplashScreenGenerator.printBootMessage(
                    ">> plugin cache needs update (wrong number of files)");
            System.out.println(
                    ">> plugin cache needs update (wrong number of files)");
            return true;
        }

        for (PluginCacheEntry e : cache.getEntries()) {

            String checkSum = checksums.get(e.getJarFile());

            if (checkSum == null || !checkSum.equals(e.getJarFileChecksum())) {
                SplashScreenGenerator.printBootMessage(
                        ">> plugin cache needs update (wrong checksum)");
                System.out.println(
                        ">> plugin cache needs update (wrong checksum)");
                return true;
            }
        }

        SplashScreenGenerator.printBootMessage(
                ">> plugin cache up to date");
        System.out.println(">> plugin cache up to date");

        return false;
    }

//    public static void saveCache() {
//        ArrayList<File> jarFiles = IOUtil.listFiles(
//                new File(Constants.PLUGIN_DIR), new String[]{".jar"});
//        
//        for (File f : jarFiles) {
//            saveCache(f);
//        }
//    }
    public static void saveCache(File f) {
        File cacheFile = getCacheFile(f);

        PluginCache cache = new PluginCache();

        for (PluginConfigurator pConf : VRL.getPlugins().values()) {

            // ignore vrl plugin
            boolean ignore = pConf.getClass().getName().
                    equals(VRLPlugin.class.getName());

            // ignore plugins from other jars
            ignore = ignore || !VJarUtil.getClassLocation(
                    pConf.getClass()).equals(f);

            if (!ignore) {
                cache.add(pConf.getClass().getName(),
                        new PluginCacheEntry(pConf));
            }
        }

        SplashScreenGenerator.printBootMessage(">> saving plugin cache");
        System.out.println(">> saving plugin cache");

        XMLEncoder e = null;
        try {
            e = new XMLEncoder(new FileOutputStream(cacheFile));
            e.writeObject(cache);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PluginCacheController.class.getName()).
                    log(Level.SEVERE, null, ex);
        } finally {
            if (e != null) {
                try {
                    e.flush();
                    e.close();
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
    }

    public static void createCache(File f,
            File destFolder, Collection<PluginConfigurator> pluginConfigs) {

        VParamUtil.validate(VParamUtil.VALIDATOR_EXISTING_FOLDER, destFolder);

        File cacheFile = getCacheFile(destFolder, f);

        PluginCache cache = new PluginCache();


        for (PluginConfigurator pConf : pluginConfigs) {

            // ignore vrl plugin if accidently added multiple times
            boolean ignore = pConf.getClass().getName().
                    equals(VRLPlugin.class.getName());
            if (!ignore) {
                cache.add(pConf.getClass().getName(),
                        new PluginCacheEntry(pConf));
            }
        }

        SplashScreenGenerator.printBootMessage(">> saving plugin cache");
        System.out.println(">> saving plugin cache");

        XMLEncoder e = null;
        try {
            e = new XMLEncoder(new FileOutputStream(cacheFile));
            e.writeObject(cache);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PluginCacheController.class.getName()).
                    log(Level.SEVERE, null, ex);
        } finally {
            if (e != null) {
                try {
                    e.flush();
                    e.close();
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }

    }
}
