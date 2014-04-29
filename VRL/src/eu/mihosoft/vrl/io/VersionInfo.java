/* 
 * VersionInfo.java
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

import java.util.ArrayList;

/**
 * <p> Defines a VRL version information. It also specifies specifies how to compare
 * versions. It is used to check file format compatibility and plugin versions
 * etc. Valid version numbers are strings that consist of decimal numbers, each pair
 * separated by a full stop (dot). Additionally the character
 * <code>x</code> can be used to denote an undefined number. This is useful for
 * defining plugin dependencies. </p> <p> Examples: 0.3.8.10, 1.3, 4.30.100,
 * 3.x, 1.2, x </p>
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public final class VersionInfo implements Comparable, AbstractVersionInfo {

    private String version;
    private String description;
    public static final String UNDEFINED = "x";

    /**
     * Constructor.
     */
    public VersionInfo() {
    }

    /**
     * Constructor.
     *
     * @param version the version number
     */
    public VersionInfo(String version) {
        setVersion(version);
    }

    /**
     * Constructor.
     *
     * @param version the version number
     * @param description the description
     */
    public VersionInfo(String version, String description) {
        setVersion(version);
        this.description = description;
    }

    @Override
    public boolean isVersionValid() {
        boolean result = true;
        try {
            parseVersionString(version);
        } catch (IllegalArgumentException ex) {
            result = false;
        }
        return result;
    }

    /**
     * <p> Parses a string and checks whether it has the right format. Version
     * strings have to be integer numbers splitted with dots. </p> <p> Examples:
     * 0.3.8.10, 1.3, 4.30.100 </p>
     *
     * @param s the string to parse
     * @return an array list that contains the integers
     * @throws IllegalArgumentException
     */
    private ArrayList<Integer> parseVersionString(String s)
            throws IllegalArgumentException {
        ArrayList<Integer> result = new ArrayList<Integer>();

        String[] numbers = s.split("\\.");

        for (String n : numbers) {
            if (n.matches("\\d+")) {
                result.add(new Integer(n));

            } else if (n.equals(UNDEFINED)) {
                break;
            } else {
                throw new IllegalArgumentException(
                        "Version string has wrong format: \"" + s + "\"");
            }
        }

        return result;
    }

    /**
     * Returns the version as integer array.
     *
     * @return the version as integer array
     */
    private ArrayList<Integer> getVersionArray() {
        return parseVersionString(version);
    }

    /**
     * Returns the version number.
     *
     * @return the version
     */
    @Override
    public String getVersion() {
        return version;
    }

    /**
     * Defines the version number.
     *
     * @param version the version number to set
     */
    public void setVersion(String version) {
        try {
            parseVersionString(version);
        } catch (IllegalArgumentException ex) {
            //
        }
        this.version = version;
    }

    /**
     * Returns the version description.
     *
     * @return the version description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Defines the version description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Compares file versions. <p> Versions are compared as follows: </p> The
     * number of elements to compare is defined by the minimum version array
     * size. Iterate through all elements to compare. If elements differ then
     * break and return </p> <p> <ul> <li><code>-1</code> if the first element
     * is smaller then the second element</li> <li><code>1</code> if the first
     * element is greater then the second element</li> </ul> </p> <p>
     * <b>Note:</b> if the version string of either this version info or the
     * specified version info contains the charactisVersionValider
     * <code>x</code> comparison will stop. Thus, specifying numbers or
     * characters after the first occurence of
     * <code>x</code> has no effect. </p>
     *
     * @param o the version to compare to
     * @return
     * <code>-1</code> if this version info is smaller,
     * <code>1</code> if this version info is bigger,
     * <code>0</code> if both version infos are equal
     */
    @Override
    public int compareTo(Object o) {
        int result = 0;

        if (o instanceof AbstractVersionInfo) {
            AbstractVersionInfo aV = (AbstractVersionInfo) o;
            VersionInfo comp = new VersionInfo(aV.getVersion(),
                    aV.getDescription());
            ArrayList<Integer> compVersion = comp.getVersionArray();
            ArrayList<Integer> ourVersion = getVersionArray();

            int size = Math.min(ourVersion.size(), compVersion.size());

            for (int i = 0; i < size; i++) {

                if (ourVersion.get(i) == null || compVersion.get(i) == null) {
                    break;
                }

                if (ourVersion.get(i) < compVersion.get(i)) {
                    result = -1;
                    break;
                } else if (ourVersion.get(i) == compVersion.get(i)) {
                    //
                } else if (ourVersion.get(i) > compVersion.get(i)) {
                    result = 1;
                    break;
                }
            }

        } else {
            throw new IllegalArgumentException(
                    "Argument is no VersionInfo object!");
        }

        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + (this.version != null ? this.version.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        return compareTo(o) == 0;
    }

    @Override
    public String toString() {
        return getVersion();
    }
}
