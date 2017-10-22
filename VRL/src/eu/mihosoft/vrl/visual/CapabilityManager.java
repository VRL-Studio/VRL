/* 
 * CapabilityManager.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2007–2017 by Michael Hoffer
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
 * Third, add an additional notice, stating that you modified VRL. A suitable
 * notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Please cite the publication(s) listed below.
 *
 * Publications:
 *
 * M. Hoffer, C. Poliwoda, & G. Wittum. (2013). Visual reflection library:
 * a framework for declarative GUI programming on the Java platform.
 * Computing and Visualization in Science, 2013, 16(4),
 * 181–192. http://doi.org/10.1007/s00791-014-0230-y
 */

package eu.mihosoft.vrl.visual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * <p>
 * A capability manager allows to store and manage capability bits. It supports
 * capability listeners ({@link CapabilityChangedListener}) and is used to
 * manage VRL GUI capabilities. VRL uses capabilites to enable/disable certain
 * functinality of the GUI. That implies code editing, dragging canvas windows
 * or changing connections.
 * </p>
 * <p>
 * It is possible to define capability dependencies. Dependencies are very
 * important because for many GUI operations it
 * is necessary to have some other functionality working because the operation
 * cannot be performed otherwise. Also other non GUI related capability systems
 * often have dependencies ({@link CapabilityDependency}).
 * </p>
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class CapabilityManager {

    private long bits = 0L;
    private ArrayList<CapabilityChangedListener> listeners =
            new ArrayList<CapabilityChangedListener>();

    private ArrayList<CapabilityDependency> dependencies =
            new ArrayList<CapabilityDependency>();

    /**
     * Enables a capability bit.
     * @param bit the bit to enable
     */
    public void enableCapability(int bit) {
        enableCapabilityDependencies(bit);
        
        bits |= (1L << bit);
        notifyListeners(bit);
    }

    /**
     * Disables a capability bit.
     * @param bit the bit to disable
     */
    public void disableCapability(int bit) {
        bits &=~(1L << bit);
        notifyListeners(bit);
        disableCapabilityWithMissingDependencies(bit);
    }

    /**
     * Indicates whether the specified capability bit is enabled.
     * @param bit the bit to check
     * @return <code>true</code> if the capability is enabled;
     *         <code>false</code> otherwise
     */
    public boolean isCapable(int bit) {
        return (bits & (1L << bit)) != 0L;
    }

    /**
     * Enables capability bits.
     * @param capabilities the capability bits to enable
     */
    public void enableCapabilities(int[] capabilities) {
        for (Integer bit : capabilities) {
            enableCapability(bit);
        }
    }

    /**
     * Defines the capability bit variable (contains all bits).
     * @param bits the bits to set
     */
    public void setCapabilityBits(long bits) {
        this.bits = bits;
        notifyListeners(null);
    }

    /**
     * Returns the capabilit bit variable (contains all bits).
     * @return the bits
     */
    public long getCapabilityBits() {
        return bits;
    }

    /**
     * Disbales capability bits.
     * @param capabilities the capability to disable
     */
    public void disableCapabilities(int[] capabilities) {
        for (Integer bit : capabilities) {
            disableCapability(bit);
        }
    }

    /**
     * Adds a capability changed listener.
     * @param l the listener to add
     */
    public void addCapabilityChangedListener(CapabilityChangedListener l) {
        listeners.add(l);
    }

    /**
     * Removes a capability changed listener.
     * @param l the listener to remove
     */
    public void removeCapabilityChangedListener(CapabilityChangedListener l) {
        boolean b = listeners.remove(l);

//        System.out.println(">> Remove CapabilityListener: " + b);
    }

    /**
     * Notifies all capability changed listeners.
     * @param bit the bit that has changed or <code>null</code> if an undefined
     * number of bits have been changed. Usually this means that almost
     * everything has changed and should therefore be reconfigured.
     */
    private void notifyListeners(Integer bit) {
        for (CapabilityChangedListener capabilityChangedListener : listeners) {
            capabilityChangedListener.capabilityChanged(this, bit);
        }
    }

    /**
     * Disables all capability bits.
     */
    public void disableAllCapabilities() {
        bits = 0L;
        notifyListeners(null);
    }

    /**
     * Adds a capability dependency.
     * @param d the dependency to add
     */
    public void addDependency(CapabilityDependency d) {
        dependencies.add(d);
    }

    /**
     * Enables all dependencies of a specified capability bit.
     * @param bit the bit
     */
    private void enableCapabilityDependencies(int bit) {
        for (CapabilityDependency d : dependencies) {
            if (d.getCapability() == bit) {
                enableCapability(d.getDependency());
            }
        }
    }


    /**
     * Disables all dependencies of a specified capability bit.
     * @param bit the bit
     */
    private void disableCapabilityWithMissingDependencies(int bit) {
        for (CapabilityDependency d : dependencies) {
            if (d.getDependency() == bit) {
                disableCapability(d.getCapability());
            }
        }
    }
}
