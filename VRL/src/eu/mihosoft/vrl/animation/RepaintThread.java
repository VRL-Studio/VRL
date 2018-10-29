/* 
 * RepaintThread.java
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

package eu.mihosoft.vrl.animation;

import javax.swing.SwingUtilities;

/**
 * The prepaint thread is a seperate thread that triggers the animation manager.
 * 
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
//TODO think of replacing thread with timer?
public class RepaintThread implements Runnable {

    private AnimationManager animationManager;
    private Thread thread;
    private boolean running = false;
    private long sleepTime = 10;

    /**
     * Constructor.
     */
    public RepaintThread() {
        //
    }

    /**
     * Constructor.
     * @param animationManager the animation manager
     */
    public RepaintThread(AnimationManager animationManager) {
        this.animationManager = animationManager;
        animationManager.setRepaintThread(this);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(getSleepTime());
            } catch (InterruptedException e) {
                System.out.println(">> RepaintThread: warning,"
                        + " thread interrupted!");
                running = false;
            }

            SwingUtilities.invokeLater(
                    new Runnable() {

                        @Override
                        public void run() {
                            animationManager.run();
                        }
                    });
        }
    }

    /**
     * Starts this repaint thread.
     */
    void start() {
        if (!running) {
            running = true;
            thread = new Thread(this);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
//            System.out.println(">> RepaintThread: started");
        }
    }

    /**
     * Stops this repaint thread.
     */
    void stop() {
//        if (running) {
//            System.out.println(">> RepaintThread: stopped");
//        }

        running = false;
        thread = null;

    }

    /**
     * Returns the sleep time of the thread.
     * @return the sleep time (milliseconds)
     */
    public long getSleepTime() {
        return sleepTime;
    }

    /**
     * Defines the sleep time of the thread (per frame). This prevents 
     * unnecessarily high frame rates. The default value is 10 milliseconds
     * which does not allow framerates above 100 fps. Setting the value to 33
     * milliseconds does not allow frame rates above 30 fps and so forth.
     * 
     * @param sleepTime the time to set (milliseconds)
     */
    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }
//    @Override
//    protected void finalize() throws Throwable {
//
//        System.out.println(getClass().getName() + ": Finalize!!!");
//
//        stop();
//
//        super.finalize();
//    }
}
