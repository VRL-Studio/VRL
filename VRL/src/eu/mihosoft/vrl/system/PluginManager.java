/* 
 * PluginManager.java
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

import eu.mihosoft.vrl.reflection.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages plugins and their dependencies.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class PluginManager {

    /**
     * Computes a bootorder of the specified plugins based on the dependencies
     * defined in each plugin. The boot order is determined via topological
     * sorting of an acyclic directed graph. Cyclic dependencies are detected.
     * In this case the computation of the boot order is impossible.
     *
     * @param plugins plugins
     * @return a bootorder of the specified plugins based on the dependencies
     * defined in each plugin
     * @throws IllegalStateException this exception will be thrown if either
     * plugin dependencies are missing or if cyclic dependencies exist
     */
    public BootOrder computeBootOrder(
            Collection<PluginConfigurator> plugins)
            throws IllegalStateException {

        List<PluginNode> resolvedNodes = new ArrayList<PluginNode>();
        List<PluginNode> unresolvedNodes = new ArrayList<PluginNode>();

        List<PluginConfigurator> result
                = new ArrayList<PluginConfigurator>();
        List<PluginConfigurator> deactivated
                = new ArrayList<PluginConfigurator>();
        String errorMessages = null;

        // define graph nodes
        HashMap<PluginIdentifier, PluginNode> nodes
                = new HashMap<PluginIdentifier, PluginNode>();

        for (PluginConfigurator p : plugins) {
            nodes.put(p.getIdentifier(), new PluginNode(p));
        }

        // check dependencies and create corresponding node list
        HashMap<PluginDependency, PluginNode> deps
                = new HashMap<PluginDependency, PluginNode>();

        HashMap<PluginIdentifier, PluginDependency> missingDependencies
                = new HashMap<PluginIdentifier, PluginDependency>();

        for (PluginConfigurator p : plugins) {
            for (PluginDependency d : p.getDependencies()) {
                boolean found = false;
                for (PluginConfigurator q : plugins) {
                    found = deps.containsKey(d);
                    if (d.verify(q.getIdentifier()) && !deps.containsKey(d)) {
                        deps.put(d, nodes.get(q.getIdentifier()));
                        found = true;
                        break;
                    }
                }

                // we don not break here even if we know that it does not work
                // because we collect all missing dependencies for showing an
                // error message
                if (!found && !d.isOptional()) {
                    missingDependencies.put(p.getIdentifier(), d);
                }
            }
        }

        // remove plugins with missing dependencies
        for (PluginIdentifier p : missingDependencies.keySet()) {
            nodes.remove(p);

            ArrayList<PluginConfigurator> delList
                    = new ArrayList<PluginConfigurator>();
            for (PluginConfigurator pC : plugins) {
                if (pC.getIdentifier().equals(p)) {
                    delList.add(pC);
                }
            }

            for (PluginConfigurator pC : delList) {
                plugins.remove(pC);
                System.err.println(">> Deactivating: " + pC.getIdentifier());
                deactivated.add(pC);
            }
        }

        // if dependencies are missing create an error message that lists
        // all missing dependencies
        if (!missingDependencies.isEmpty()) {
            String depString = ">> Error: missing dependencies:<br>";

            depString += "<ul>";

            for (PluginIdentifier p : missingDependencies.keySet()) {
                depString += "<li>" + p.toString() + " => "
                        + missingDependencies.get(p).toString() + "</li>";
            }

            depString += "</ul>";

            errorMessages = depString;

            System.err.println(depString);
        }

        // define graph edges
        for (PluginConfigurator p : plugins) {
            PluginNode node = nodes.get(p.getIdentifier());
            for (PluginDependency dep : p.getDependencies()) {
                PluginNode n = deps.get(dep);
                
                // n can be null if n was specified as optional dependency
                // and if it is not available
                if (n != null) {
                    node.addEdge(n);
                }
            }
        }

        // find root nodes
        ArrayList<PluginNode> rootNodes = new ArrayList<PluginNode>();
        for (PluginNode n : nodes.values()) {
            boolean isRoot = true;
            for (PluginNode m : nodes.values()) {
                if (m.getEdges().contains(n)) {
                    isRoot = false;
                    break;
                }
            }
            if (isRoot) {
                rootNodes.add(n);
            }
        }

        // resolve dependencies
        for (PluginNode root : rootNodes) {
            resolve(root, resolvedNodes, unresolvedNodes);
        }

        // convert nodes to plugins
        for (PluginNode node : resolvedNodes) {
            result.add(node.getPlugin());
        }

        return new BootOrder(result, deactivated, errorMessages);
    }

    /**
     * Computes a map containing the classloader parentage information that is
     * necessary to create plugin classloaders.
     *
     * @param pluginGroupRoots plugin root groups (will be filled with root
     * elements)
     * @param bootorder plugins in topological order
     * @return a map containing the classloader parentage information
     */
    public Map<String, String> computeClassLoaderParentageMap(
            List<String> pluginGroupRoots,
            List<PluginConfigurator> bootorder) {
        Map<String, String> result = new HashMap<String, String>();

        // for each plugin in reverse order
        for (int i = bootorder.size() - 1; i >= 0; i--) {

            PluginConfigurator p = bootorder.get(i);

            String pID = p.getIdentifier().getName();

            // if the plugin has a parent already, ignore it
            if (result.get(pID) != null) {
                continue;
            }

            // if the plugin has no parent we know that we are at the beginning
            // of a new plugin group (set of plugins that are connected via
            // graph edges). thus, we add this plugin as root element.
            pluginGroupRoots.add(pID);

            // create deps
            Collection<String> deps_p = new ArrayList<String>();
            for (PluginDependency d : p.getDependencies()) {
                deps_p.add(d.getName());
            }

            // the nearest elemenent to p in the bootorder that is a dependency
            // of p.
            String parentOfP
                    = getNearestElementInBootorder(pID, deps_p, bootorder);

            // if this is null, ignore it
            if (parentOfP == null) {
                continue;
            }

            // define the previously computed nearest dependency of p as parent
            // of p
            result.put(pID, parentOfP);

            // for each dependency of p compute the parent element
            for (String d : deps_p) {

                // remove d from deps_p set
                Collection<String> deps_p_without_d
                        = new ArrayList<String>(deps_p);
                deps_p_without_d.remove(d);

                // the nearest elemenent to d in the bootorder that is a
                // dependency of p and !=d (that is, why we removed d from
                // deps_p in the previous step).
                String parentOfD
                        = getNearestElementInBootorder(
                                d, deps_p_without_d, bootorder);

                // if we did not found a parent of d, ignore d
                if (parentOfD == null) {
                    continue;
                }

                // define the previously computed nearest dependency of d as
                // parent of d
                result.put(d, parentOfD);
            }

        }

        return result;
    }

    /**
     * Returns the nearest element of p that is in the specified element list
     * (dependencies of p)
     *
     * @param p element to find the nearest element for
     * @param deps_p element list (dependencies of p)
     * @param bootorder
     * @return the nearest element of p or <code>null</code> no such element
     * exists
     */
    private String getNearestElementInBootorder(String p,
            Collection<String> deps_p,
            List<PluginConfigurator> bootorder) {

        // find index of p in bootorder
        int p_index = 0;
        for (int i = 0; i < bootorder.size(); i++) {
            if (bootorder.get(i).getIdentifier().getName().equals(p)) {
                p_index = i;
                break;
            }
        }

        // find nearest dep
        // we go backward for efficiency reasons
        for (int i = p_index - 1; i >= 0; i--) {
            String e = bootorder.get(i).getIdentifier().getName();

            if (deps_p.contains(e)) {
                return e;
            }
        }

        return null;
    }

    /**
     * Resolution algorithm (topological sorting).
     *
     * @param node root node
     * @param resolvedNodes a list used to store resoved nodes (result)
     * @param unresolvedNodes a list used to store unresoved nodes (used
     * temporarily)
     */
    private void resolve(PluginNode node,
            List<PluginNode> resolvedNodes,
            List<PluginNode> unresolvedNodes) {
        unresolvedNodes.add(node);
        for (PluginNode n : node.getEdges()) {
            if (!resolvedNodes.contains(n)) {
                if (unresolvedNodes.contains(n)) {
                    throw new IllegalStateException(
                            ">> Error: circular plugin dependencies!");
                }
                resolve(n, resolvedNodes, unresolvedNodes);
            }
        }
        resolvedNodes.add(node);
        unresolvedNodes.remove(node);
    }
}
