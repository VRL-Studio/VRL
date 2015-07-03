/* 
 * PluginConfigurator.java
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

import eu.mihosoft.vrl.reflection.VisualCanvas;
import java.awt.image.BufferedImage;
import java.util.function.BooleanSupplier;

/**
 * Configures VRL plugin packages and is responsible for register type
 * representations and components with the type factory of a given canvas.
 *
 * @see VisualCanvas
 * @see eu.mihosoft.vrl.reflection.TypeRepresentationFactory
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface PluginConfigurator {

    /**
     * Key for accessing the "installed" property of the plugin config.
     */
    public static final String VERSION_KEY = "PluginConfigurator:version";
    /**
     * Key for accessing the "timestamp" property of the plugin config.
     */
    public static final String TIMESTAMP_KEY = "PluginConfigurator:timestamp";

    /**
     * Registers VRL plugin type representations with the type representation
     * factory of a given canvas and optionally performs other tasks such as
     * configuring additional components.
     *
     * @param api the api to register with
     */
    public void register(PluginAPI api);

    /**
     * Reverts VRL plugin registration. That is, it removes the type
     * representations of the VRL plugin and reverts all configurations.
     *
     * @param api the api to unregister from
     */
    public void unregister(PluginAPI api);

    /**
     * Returns a plugin description. Descriptions should use basic HTML
     * formatting.
     *
     * @return a plugin description
     */
    public String getDescription();

    /**
     * Returns a plugin icon.
     *
     * @return a plugin icon
     */
    public BufferedImage getIcon();

    /**
     * Returns the plugin version info.
     *
     * @return the plugin version info
     */
    public PluginIdentifier getIdentifier();

    /**
     * Initializes this plugin configurator. This can be used to prepare
     * resources such as loading or compiling classes.
     *
     * @param iApi plugin api for initialization (contains configuration etc.)
     */
    public void init(InitPluginAPI iApi);

    /**
     * Returns the plugins this plugin depends on.
     *
     * @return the plugins this plugin depends on
     */
    public PluginDependency[] getDependencies();

    /**
     * Returns the access policy which defines the access rules for plugins that
     * depend on this plugin.
     */
    public AccessPolicy getAccessPolicy();

    /**
     * Returns the copyright info of this plugin.
     */
    public CopyrightInfo getCopyrightInfo();

    /*
     * Returns the preference pane of this plugin.
     */
    public PreferencePane getPreferencePane();

    /**
     * Shuts down this plugin configurator. This can be used to release
     * resources or terminate associated processes.
     */
    public void shutdown();

    /**
     * Installs this plugin.
     *
     * @param iApi plugin api for initialization (contains configuration etc.)
     */
    public void install(InitPluginAPI iApi);

    /**
     * Uninstalls this plugin.
     *
     * @param iApi plugin api for uninitialization (contains configuration etc.)
     */
    public void uninstall(InitPluginAPI iApi);

    /**
     * Indicates whether this plugin is relevant for project persistence.
     *
     * @return <code>true</code> if this plugin is relevant for project
     * persistance; <code>false</code> otherwise
     */
    public boolean isRelevantForPersistence();

    /**
     * Indicates whether this plugin shall be automatically selected.
     *
     * @return <code>true</code> if this plugin shall be automatically selected;
     * <code>false</code> otherwise
     */
    public boolean isAutomaticallySelected();

    /**
     * Indicates whether the plugin initialization fails. This allows plugin
     * developers to skip plugin initialization if additional requirements that
     * are not specified via dependencies are not met.
     *
     * @param iApi
     * @return failure object
     */
    public default InitFailure checkFailure(InitPluginAPI iApi) {
        return InitFailure.success();
    }

    /**
     * Fails Initialization if the specified condition is met. This allows
     * plugin developers to skip plugin initialization if additional
     * requirements that are not specified via dependencies are not met.
     *
     * @param condition fail condition
     * @param message message/reason
     */
    public void failsInitIf(BooleanSupplier condition, String message);

}
