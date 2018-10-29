/* 
 * SynchronizedFileAccess.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007–2018 by Michael Hoffer,
 * Copyright (c) 2015–2018 G-CSC, Uni Frankfurt,
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn)
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

package eu.mihosoft.vrl.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to simplify synchronized file access via file locks.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class SynchronizedFileAccess {

    /**
     * <p>Accesses a file, i.e., performs a file task and ensures that only one
     * task can access this file at once.</p> <p> <b>Note:</b> this method
     * may invoke
     * <code>Thread.sleep</code> on the current thread. </p>
     *
     * @param fTask the task to perform
     * @param f the file to access
     * @param maxRetries number of retries
     * @param retryDelay the retry delay (in milliseconds)
     * @throws FileNotFoundException if the file could not be found
     * @throws IOException if access failed
     */
    public static synchronized void access(
            SynchronizedFileTask fTask, File f, int maxRetries, long retryDelay)
            throws FileNotFoundException, IOException {

        access(fTask, f, maxRetries, retryDelay, true);
    }

    /**
     * <p>Accesses a file, i.e., performs a file task and ensures that only one
     * task can access this file at once.</p> <p> <b>Note:</b> this method
     * may invoke
     * <code>Thread.sleep</code> on the current thread.</p>
     *
     * <p> <b>Warning:</b> files locked with this method using
     * <code>unlock=false</code> cannot be unlocked from within the process that
     * calls this method. Only use this method to lock preference files to
     * assure that no other process accidently changes its content. </p>
     *
     * @param fTask the task to perform
     * @param f the file to access
     * @param maxRetries number of retries
     * @param retryDelay the retry delay (in milliseconds)
     * @param unlock defines whether to unlock the file after usage
     * @throws FileNotFoundException if the file could not be found
     * @throws IOException if access failed
     */
    private static synchronized void access(
            SynchronizedFileTask fTask, File f, int maxRetries, long retryDelay,
            boolean unlock)
            throws FileNotFoundException, IOException {

        if (f.exists()) {
            // Try to get the lock
            FileChannel channel = new RandomAccessFile(f, "rw").getChannel();
            FileLock lock;

            int counter = 0;

            while ((lock = channel.tryLock()) == null && counter < maxRetries) {
                // File is locked by other application
                System.out.println(
                        ">> SFA: Resource unavailable. Trying again in "
                        + (retryDelay / 1000.f) + " sec.");
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException e) {
                    //
                }

                counter++;
            }

            if (lock != null) {
                fTask.performTask(f);
            }

            // release file lock
            if (unlock) {
                try {
                    if (lock != null) {
                        lock.release();
                    }
                    channel.close();
                } catch (IOException e) {
                    //
                }
            }
        } else {
            fTask.performTask(f);
        }
    }

    /**
     * Locks the specified file. <p> <b>Warning:</b> files locked with this
     * method cannot be unlocked from within the process that calls this method.
     * Apossible use case for this method is to lock files to assure that no
     * other process accidently changes its content. </p>
     *
     * @param f the file to lock
     * @return
     * <code>true</code> if the file could be successfully locked;
     * <code>false</code> otherwise
     */
    public static synchronized boolean lockFile(File f) {
        LockedTask task = new LockedTask();

        try {
            access(task, f, false);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SynchronizedFileAccess.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SynchronizedFileAccess.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        return task.locked;
    }

    public static synchronized boolean isLocked(File f) {

        LockedTask task = new LockedTask();

        try {
            access(task, f);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SynchronizedFileAccess.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SynchronizedFileAccess.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        return task.locked;
    }

    /**
     * <p>Accesses a file, i.e., performs a file task and ensures that only one
     * task can access this file at once. It tries 10 times to get exclusive
     * access to the file with a retry delay of 0.3 seconds.</p> <p>
     * <b>Note:</b> this method may invoke
     * <code>Thread.sleep</code> on the current thread. </p>
     *
     * @param fTask the task to perform
     * @param f the file to access
     * @throws FileNotFoundException if the file could not be found
     * @throws IOException if access failed
     */
    public static synchronized void access(SynchronizedFileTask fTask, File f)
            throws FileNotFoundException, IOException {
        access(fTask, f, 10, 300);
    }

    /**
     * <p>Accesses a file, i.e., performs a file task and ensures that only one
     * task can access this file at once. It tries 10 times to get exclusive
     * access to the file with a retry delay of 0.3 seconds.</p> <p>
     * <b>Note:</b> this method may invoke
     * <code>Thread.sleep</code> on the current thread. </p>
     *
     * @param fTask the task to perform
     * @param f the file to access
     * @param unlock defines whether to unlock the file after usage
     * @throws FileNotFoundException if the file could not be found
     * @throws IOException if access failed
     */
    private static synchronized void access(SynchronizedFileTask fTask, File f,
            boolean unlock)
            throws FileNotFoundException, IOException {
        access(fTask, f, 10, 300, unlock);
    }
}

class LockedTask implements SynchronizedFileTask {

    boolean locked = true;

    @Override
    public void performTask(File f) {
        locked = false;
    }
}
