/* 
 * PluginDependency.java
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

import eu.mihosoft.vrl.io.VersionInfo;

/**
 * This class represents a plugin dependency.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class PluginDependency {

    private final VersionInfo min;
    private final VersionInfo max;
    private final String name;
    private final boolean optional;

    /**
     * Constructor.
     *
     * @param name plugin name
     * @param version plugin version
     */
    public PluginDependency(String name, VersionInfo version) {
        this.min = version;
        this.max = version;
        this.name = name;
        this.optional = false;
    }

    /**
     * Constructor.
     *
     * @param name plugin name
     * @param version plugin version
     * @param optional defines whether this dependency is optional
     */
    public PluginDependency(String name, VersionInfo version, boolean optional) {
        this.min = version;
        this.max = version;
        this.name = name;
        this.optional = optional;
    }

    /**
     * Constructor.
     *
     * @param name
     * @param min
     * @param max
     */
    public PluginDependency(String name, String min, String max) {
        this.min = new VersionInfo(min);
        this.max = new VersionInfo(max);
        this.name = name;
        this.optional = false;
    }

    /**
     * Constructor.
     *
     * @param name
     * @param min
     * @param max
     * @param optional defines whether this dependency is optional
     */
    public PluginDependency(String name, String min, String max, boolean optional) {
        this.min = new VersionInfo(min);
        this.max = new VersionInfo(max);
        this.name = name;
        this.optional = optional;
    }

    /**
     * Constructor.
     *
     * @param name plugin name
     * @param min minimum version
     * @param max maximum version
     */
    public PluginDependency(String name, VersionInfo min, VersionInfo max) {
        this.min = min;
        this.max = max;
        this.name = name;
        this.optional = false;
    }

    /**
     * Constructor.
     *
     * @param name plugin name
     * @param min minimum version
     * @param max maximum version
     * @param optional defines whether this dependency is optional
     */
    public PluginDependency(String name, VersionInfo min, VersionInfo max, boolean optional) {
        this.min = min;
        this.max = max;
        this.name = name;
        this.optional = optional;
    }

    /**
     * Returns the minimum version.
     *
     * @return the minimum version
     */
    public VersionInfo getMin() {
        return min;
    }

    /**
     * Returns the maximum version.
     *
     * @return the maximum version
     */
    public VersionInfo getMax() {
        return max;
    }

    /**
     * Returns the name of the plugin referenced by this dependency.
     *
     * @return the name of the plugin referenced by this dependency
     */
    public String getName() {
        return name;
    }

    /**
     * Verifies the specified plugin, i.e., determines whether the specified
     * plugin meets the version conditions defined by this dependency.
     *
     * @param plugin plugin to verify
     * @return <code>true</code> if the specified plugin meets the version
     * conditions defined by this dependency; <code>false</code> otherwise
     */
    boolean verify(PluginIdentifier plugin) {
        boolean nameEqual = name.equals(plugin.getName());

        boolean greaterOrEqual
                = min.compareTo(plugin.getVersion()) <= 0;

        boolean lessOrEqual
                = max.compareTo(plugin.getVersion()) >= 0;

        if (nameEqual && greaterOrEqual && lessOrEqual) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return getName()
                + "-[" + getMin().getVersion()
                + ", " + getMax().getVersion() + "]-optional:" + optional;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PluginDependency) {
            PluginDependency p = (PluginDependency) o;
            if (p.getMax().equals(getMax())
                    && p.getMin().equals(getMin())
                    && p.getName().equals(getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + (this.min != null ? this.min.hashCode() : 0);
        hash = 13 * hash + (this.max != null ? this.max.hashCode() : 0);
        hash = 13 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    /**
     * @return the optional
     */
    public boolean isOptional() {
        return optional;
    }
}
