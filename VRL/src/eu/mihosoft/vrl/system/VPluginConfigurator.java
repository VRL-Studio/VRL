/* 
 * VPluginConfigurator.java
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

import eu.mihosoft.vrl.lang.VLangUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 * Plugin classloader. This class should be used to create a VRL plugin. <p>
 * <b>Creating a Plugin:</b> almost every Java project can be converted to a VRL
 * plugin by providing a class that extends
 * <code>VPluginConfigurator</code>. The VRL run-time system can detect the
 * class and recognizes the Java library as plugins and performs the
 * registration as specified in the plugin configurator implementation. The code
 * below shows a possible implementation. </p>
 * <p><b>Sample Code:</b></p>
 * 
 * <pre><code>
 * import eu.mihosoft.vrl.system.*;
 *
 * public class SamplePluginConfigurator extends VPluginConfigurator {
 *
 *    public SamplePluginConfigurator() {
 *
 *       //specify the plugin name and version
 *       setIdentifier(new PluginIdentifier("Sample-Plugin", "0.1"));
 *
 *       // optionally allow other plugins to use the api of this plugin
 *       // you can specify packages that shall be
 *       // exported by using the exportPackage() method:
 *       // 
 *       // exportPackage("com.your.package");
 *
 *       // describe the plugin
 *       setDescription("Plugin Description");
 *
 *       // copyright info
 *       setCopyrightInfo("Sample-Plugin",
 *               "(c) Your Name",
 *               "www.you.com", "License Name", "License Text...");
 *       
 *       // specify dependencies
 *       // addDependency(new PluginDependency("VRL", "0.4.0", "0.4.0"));
 *
 *    }
 *
 *   public void register(PluginAPI api) {
 *
 *       // register plugin with canvas
 *       if (api instanceof VPluginAPI) {
 *           VPluginAPI vapi = (VPluginAPI) api;
 *
 *           // Register visual components:
 *           //
 *           // Here you can add additional components,
 *           // type representations, styles etc.
 *           //
 *           // ** NOTE **
 *           //
 *           // To ensure compatibility with future versions of VRL,
 *           // you should only use the vapi or api object for registration.
 *           // If you directly use the canvas or its properties, please make
 *           // sure that you specify the VRL versions you are compatible with
 *           // in the constructor of this plugin configurator because the
 *           // internal api is likely to change.
 *           //
 *           // examples:
 *           //
 *           // vapi.addComponent(MyComponent.class);
 *           // vapi.addTypeRepresentation(MyType.class);
 *       }
 *   }
 *
 *   public void unregister(PluginAPI api) {
 *       // nothing to unregister
 *   }
 *
 *    public void init(InitPluginAPI iApi) {
 *       // nothing to init
 *   }
 * }
 * </code></pre>
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public abstract class VPluginConfigurator implements PluginConfigurator {

    private ArrayList<PluginDependency> dependencies =
            new ArrayList<PluginDependency>();
    private String description;
    private BufferedImage icon;
    private PluginIdentifier identifier;
    private boolean loadNativeLibraries = true;
    private AccessPolicyImpl accessPolicy = new AccessPolicyImpl();
    private File nativeLibFolder;
    private CopyrightInfoImpl copyrightInfo = new CopyrightInfoImpl();
    private PreferencePane preferencePane;
    private InitPluginAPI initAPI;

    public VPluginConfigurator() {
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public final BufferedImage getIcon() {
        return icon;
    }

    @Override
    public final PluginIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public final PluginDependency[] getDependencies() {
        PluginDependency[] result = new PluginDependency[dependencies.size()];

        result = dependencies.toArray(result);

        return result;
    }

    /**
     * Defines the description of this plugin.
     *
     * @param description the description to set
     */
    public final void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the icon of this plugin.
     *
     * @param icon the icon to set
     */
    public final void setIcon(BufferedImage icon) {
        this.icon = icon;
    }

    /**
     * Adds a dependency to this configurator.
     *
     * @param dep dependency to add
     */
    public final void addDependency(PluginDependency dep) {
        dependencies.add(dep);
    }

    /**
     * Defines the identifier of this plugin.
     *
     * @param identifier the identifier to set
     */
    public final void setIdentifier(PluginIdentifier identifier) {
        this.identifier = identifier;
    }

    /**
     * Initializes native libraries via
     * <code>System.loadLibrary()</code>. This method must not be used manually!
     */
    final void nativeInit(PluginDataController dC) {

        nativeLibFolder =
                VRL.addNativesPath(this, dC, !isLoadNativeLibraries());
    }

    /**
     * @return the loadNativeLibraries
     */
    public final boolean isLoadNativeLibraries() {
        return loadNativeLibraries;
    }

    /**
     * @param loadNativeLibraries the loadNativeLibraries to set
     */
    public final void setLoadNativeLibraries(boolean loadNativeLibraries) {
        this.loadNativeLibraries = loadNativeLibraries;
    }

    @Override
    public final AccessPolicy getAccessPolicy() {
        return accessPolicy;
    }

    public final void exportPackage(String packageName) {

        if (!VLangUtils.isPackageNameValid(packageName)) {
            throw new IllegalArgumentException(
                    " The specified string is not a valid package name: "
                    + packageName);
        }

        accessPolicy.addPackage(packageName);
    }

    public final void exportClass(String className) {

        // if accepted nothing to do
        if (accessPolicy.accept(className)) {
            return;
        }

        if (VLangUtils.isShortName(className)) {
            if (!VLangUtils.isClassNameValid(className)) {
                throw new IllegalArgumentException(
                        " The specified string is not a valid class name: " + className);
            }
        }

        String packageName = VLangUtils.packageNameFromFullClassName(className);

        packageName = VLangUtils.slashToDot(packageName);

        if (!VLangUtils.isPackageNameValid(packageName) && packageName.length() > 0) {
            throw new IllegalArgumentException(
                    " The package name of the specified class name is invalid: " + packageName);
        }

        String shortName = VLangUtils.shortNameFromFullClassName(className);

        if (!VLangUtils.isClassNameValid(shortName)) {
            throw new IllegalArgumentException(
                    " The class name name of the specified class name is invalid: " + className);
        }

        accessPolicy.addClass(className);
    }

    public final void addAccessPolicy(AccessPolicy policy) {
        accessPolicy.addPolicy(policy);
    }

    public final void disableAccessControl(boolean value) {
        accessPolicy.setAllowAll(value);
    }

    /**
     * @return the nativeLibFolder
     */
    public File getNativeLibFolder() {
        return nativeLibFolder;
    }

    /**
     * @return the initAPI
     */
    public final InitPluginAPI getInitAPI() {

        if (initAPI == null) {
            throw new IllegalStateException("Cannot get InitAPI: InitAPI not set");
        }

        return initAPI;
    }

    /**
     * @param initAPI the initAPI to set
     */
    public final void setInitAPI(InitPluginAPI initAPI) {
        this.initAPI = initAPI;
    }

    @Override
    public final CopyrightInfo getCopyrightInfo() {

        return copyrightInfo;
    }

    public void setCopyrightInfoAsPlainText(String plainText) {
        this.copyrightInfo.setPlainText(plainText);
    }

    /**
     *
     * @param projectName
     * @param copyrightStatement
     * @param licenseName
     * @param licenseText
     */
    public void setCopyrightInfo(
            String projectName,
            String copyrightStatement,
            String projectPage,
            String licenseName,
            String licenseText) {
        this.copyrightInfo.setProjectName(projectName);
        this.copyrightInfo.setCopyrightStatement(copyrightStatement);
        this.copyrightInfo.setProjectPage(projectPage);
        this.copyrightInfo.setLicense(licenseName, licenseText);
    }

    public VPluginConfigurator addThirdPartyCopyrightInfo(
            String projectName,
            String copyrightStatement,
            String projectPage,
            String licenseName,
            String licenseText) {

        CopyrightInfoImpl thirdPartyInfo = new CopyrightInfoImpl(
                projectName, copyrightStatement, projectPage,
                licenseName, licenseText);

        this.copyrightInfo.addThirdPartyCopyrightInfo(thirdPartyInfo);

        return this;
    }

    /**
     * @return the preferencePane
     */
    @Override
    public PreferencePane getPreferencePane() {
        return preferencePane;
    }

    /**
     * @param preferencePane the preferencePane to set
     */
    public void setPreferencePane(PreferencePane preferencePane) {
        this.preferencePane = preferencePane;
    }

    @Override
    public void shutdown() {
        //
    }

    @Override
    public void unregister(PluginAPI api) {
        //
    }

    @Override
    public void install(InitPluginAPI iApi) {
        //
    }

    @Override
    public void uninstall(InitPluginAPI iApi) {
        //
    }
}

class AccessPolicyImpl implements AccessPolicy {

    private boolean allowAll = false;
    private ArrayList<String> accessPatterns = new ArrayList<String>();
    private ArrayList<String> classNames = new ArrayList<String>();
    private ArrayList<AccessPolicy> customPolicies =
            new ArrayList<AccessPolicy>();

    @Override
    public boolean accept(String className) {

        // main class of project cannot be used by other plugins
        if (className.equals("eu.mihosoft.vrl.user.Main")) {
            return false;
        }

        // main class of project cannot be used by other plugins
        if (className.equals("eu.mihosoft.vrl.user.VSessionMainClass")) {
            return false;
        }

        // if we allow all classes no further checks are necessary
        // however, default session package is excluded to prevent name clashes
        boolean defaultPackage =
                className.matches("eu\\.mihosoft\\.vrl\\.user\\..*");
        if (isAllowAll() && !defaultPackage) {
            return true;
        }

        // check whether custom policies allow us to load the specified class
        for (AccessPolicy p : customPolicies) {
            if (p.accept(className)) {
                return true;
            }
        }

        // check whether access patterns allow us to load the specified class
        for (String extPath : accessPatterns) {

            if (className.matches(extPath)) {
                return true;
            }
        }

        // check whether class name explicitly allowed
        for (String extPath : classNames) {

            if (className.equals(extPath)) {
                return true;
            }
        }


        return false;
    }

    public void addPolicy(AccessPolicy policy) {
        customPolicies.add(policy);
    }

    public void addPattern(String pattern) {
        accessPatterns.add(pattern);
    }

    public void addPackage(String packageName) {
        accessPatterns.add(packageName.replace(".", "\\.") + ".*");
    }

    /**
     * @return the allowAll
     */
    public boolean isAllowAll() {
        return allowAll;
    }

    /**
     * @param allowAll the allowAll to set
     */
    public void setAllowAll(boolean allowAll) {
        this.allowAll = allowAll;
    }

    public void addClass(String className) {
        classNames.add(className);
    }
}
