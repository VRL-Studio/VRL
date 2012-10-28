/* 
 * ConfigurationFileImpl.java
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

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class ConfigurationFileImpl implements ConfigurationFile {

    private Map<String, String> properties = new HashMap<String, String>();
//    Properties properties = new Properties();
    private File file;

    public ConfigurationFileImpl(File f) {
        file = f;
    }

    @SuppressWarnings("unchecked")
    public boolean load() {
        
        boolean result = false;
        
        if (!file.isFile()) {
            return false;
        }

        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfigurationFileImpl.class.getName()).
                    log(Level.SEVERE, null, ex);

            try {
                in.close();
            } catch (Exception ex1) {
                Logger.getLogger(ConfigurationFileImpl.class.getName()).
                        log(Level.SEVERE, null, ex1);
            }

            // Throw exception
            //throw ex;

        }
//        try {
//            properties.load(in);
//        } catch (IOException ex) {
//            Logger.getLogger(PluginConfigurationImpl.class.getName()).
//                    log(Level.SEVERE, null, ex);
//            try {
//                in.close();
//            } catch (IOException ex1) {
//                throw ex;
//            }
//        }

        XMLDecoder d = new XMLDecoder(in);

        Object o = d.readObject();

        if (!(o instanceof Map<?, ?>)) {
            try {
                throw new IOException("Data format not recognized!");
            } catch (IOException ex) {
                Logger.getLogger(ConfigurationFileImpl.class.getName()).
                        log(Level.SEVERE, null, ex);

                try {
                    d.close();
                } catch (Exception ex1) {
                    Logger.getLogger(ConfigurationFileImpl.class.getName()).
                            log(Level.SEVERE, null, ex1);
                }

                // Throw exception
                //throw ex;
            }
        } else {
            properties = (Map<String, String>) o;
            result = true;
        }

        d.close();
        
        return result;
    }

    @Override
    public ConfigurationFile setProperty(String key, String value) {
        properties.put(key, value);

        return this;
    }

    @Override
    public boolean save() {

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfigurationFileImpl.class.getName()).
                    log(Level.SEVERE, null, ex);

            try {
                out.close();
            } catch (Exception ex1) {
                Logger.getLogger(ConfigurationFileImpl.class.getName()).
                        log(Level.SEVERE, null, ex1);
            }

            return false;
        }

        XMLEncoder e = new XMLEncoder(out);

        e.writeObject(properties);

        e.flush();
        e.close();

        return true;
    }

    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }

    @Override
    public Iterable<String> getKeys() {
        return properties.keySet();
    }

    @Override
    public Iterable<String> getValues() {
        return properties.values();
    }

    @Override
    public ConfigurationFile removeProperty(String key) {
        properties.remove(key);
        return this;
    }

    @Override
    public boolean containsProperty(String key) {
        return properties.containsKey(key);
    }
}
