/* 
 * VThread.java
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Thread class for workflow related tasks such as visual method invocation.
 * VThreads may be stopped when closing sessions or when building projects. Do
 * not use it for massive parallel tasks.</p>
 * <p>
 * A possible advantage over Thread objects is that VThreads can be directly
 * accessed from VRL Shell. Distinguishing VThreas from other threads makes it
 * easy to identify and control workflow related tasks.
 * </p>
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VThread extends Thread {

    private static final Map<Long, VThread> threads
            = new HashMap<Long, VThread>();
    private static final Object threadsLock = new Object();
    private long id;

    /**
     * Constructor.
     *
     * @see Thread#Thread()
     */
    public VThread() {
        addToMap(this);
    }

    /**
     * Constructor.
     *
     * @param r Runnable object
     *
     * @see Thread#Thread(java.lang.Runnable)
     */
    public VThread(Runnable r) {
        super(r);
        addToMap(this);
    }

    /**
     * Adds a vthread to the map and assigns a unique id.
     *
     * @param t thread to add
     */
    private static synchronized void addToMap(VThread t) {
        synchronized (threadsLock) {

            Long id = 0L;

            while (threads.containsKey(id)) {
                id += 1;
            }

            threads.put(id, t);
            t.id = id;
        }
    }

    /**
     * Returns a copy of the ids of all running VThread instances.
     *
     * @return a copy of the ids of all running VThread instances
     */
    public static Set<Long> getIds() {

        updateThreadList();

        Set<Long> result
                = new HashSet<Long>();

        synchronized (threadsLock) {
            result.addAll(threads.keySet());
        }

        return result;
    }

    /**
     * Updates the thread list (removes threads that are not alive).
     */
    private static void updateThreadList() {

        Map<Long, VThread> threadCopy = getVThreads();

        synchronized (threadsLock) {
            for (VThread vt : threadCopy.values()) {
                if (!vt.isAlive()) {
                    threads.remove(vt.getVId());
                } else {
                    //
                }
            }
        }

    }

    /**
     * Returns a copy of the map that contains all VThread instances.
     *
     * @return a copy of the map that contains all VThread instances
     */
    private static Map<Long, VThread> getVThreads() {

        Map<Long, VThread> result
                = new HashMap<Long, VThread>();

        synchronized (threadsLock) {
            result.putAll(threads);
        }

        return result;
    }

    /**
     * Stops a vthread specified by id. Stopping a thread is a dangerous
     * operation and may lead to unpredictable application behavior. Use this
     * method with caution.
     *
     * @see Thread#stop()
     * @param id id of the vthread to stop
     * @return <code>true</code> if stopping was successful; <code>false</code>
     * otherwise
     */
    @SuppressWarnings("deprecation")
    public static boolean stopVThread(Long id) {

        updateThreadList();
        Map<Long, VThread> threadCopy = getVThreads();

        if (threadCopy.containsKey(id)) {
            threadCopy.get(id).stop();
        } else {
            return false;
        }

        return true;
    }

    /**
     * Interrupts a vthread specified by id. Threads may stop on interruption or
     * may continue to run. This depends on the concrete implementation of the
     * thread or its Runnable object.
     *
     * @param id id of the vthread to interrupt
     * @return <code>true</code> if interrupting was successful;
     * <code>false</code> otherwise
     */
    public static boolean interruptVThread(Long id) {

        updateThreadList();
        Map<Long, VThread> threadCopy = getVThreads();

        if (threadCopy.containsKey(id)) {
            threadCopy.get(id).interrupt();
        } else {
            return false;
        }

        return true;
    }

    /**
     * @return the id of this vthread.
     */
    public long getVId() {
        return id;
    }

    /**
     * Interrupts all running vthreads. Threads may stop on interruption or may
     * continue to run. This depends on the concrete implementation of the
     * thread or its Runnable object.
     *
     * @return <code>true</code> if interrupting all vthreads was successful;
     * <code>false</code> otherwise
     */
    public static boolean interruptAll() {
        updateThreadList();

        boolean result = true;

        for (VThread vt : getVThreads().values()) {
            if (!interruptVThread(vt.getVId())) {
                result = false;
            }
        }

        return result;
    }

    /**
     * Stops all running vthreads. Stopping a thread is a dangerous operation
     * and may lead to unpredictable application behavior. Use this method with
     * caution.
     *
     * @see Thread#stop()
     *
     * @return <code>true</code> if stopping all vthreads was successful;
     * <code>false</code> otherwise
     */
    public static boolean stopAll() {
        updateThreadList();

        boolean result = true;

        for (VThread vt : getVThreads().values()) {
            if (!stopVThread(vt.getVId())) {
                result = false;
            }
        }

        return result;
    }
}
