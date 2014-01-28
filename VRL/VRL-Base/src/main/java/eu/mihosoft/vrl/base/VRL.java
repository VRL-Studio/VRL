/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.base;

import java.nio.file.Path;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VRL {

    private static VPropertyFolderManager propertyFolderManager;

    public static void init() {
        if (propertyFolderManager == null) {
            propertyFolderManager = new VPropertyFolderManager();
            propertyFolderManager.evalueteArgs(new String[0]);
        }
    }

    public static void init(Path baseLocation) {
        if (propertyFolderManager == null) {
            propertyFolderManager = new VPropertyFolderManager(baseLocation);
            propertyFolderManager.evalueteArgs(new String[0]);
        }
    }

    public static VPropertyFolderManager getPropertyFolderManager() {

        return propertyFolderManager;
    }
}
