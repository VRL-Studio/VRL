/* 
 * MessageLogger.java
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

package eu.mihosoft.vrl.logging;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */


import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.visual.MessageType;
import javax.swing.SwingUtilities;

/**
 * <p>
 * This class allows only one
 * instance which can be obtained via
 * {@link #getInstance(eu.mihosoft.vrl.reflection.VisualCanvas) }.
 * </p>
 * <p>
 * <b>Note:</b> this singleton must not be loaded by more than one
 * classloader per JVM! Although this is no problem for Java classes, it
 * does not work for native libraries. Unfortunately, we cannot provide an
 * acceptable workaround. Thus, it is recommended to use this class from
 * a valid VRL plugin only. The VRL plugin system is aware of this problem
 * and handles it correctly.
 * </p>
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class MessageLogger {
    /**
     * messages
     */
    private static StringBuffer messages = new StringBuffer();
    /**
     * message logger instance
     */
    private static MessageLogger instance;
    /**
     * VRL canvas used to visualize classes
     */
    private VisualCanvas mainCanvas;
    /**
     * Messaging thread which displays messages.
     */
    private MessageThread messagingThread;

    /**
     * instanciation only allowed in this class
     */
    private MessageLogger() {
        // we must set the singleton instance to prevent
        // calling multiple constructors.
        instance = this;
    }

    /**
     * <p>
     * Returns the instance of this singleton. If it does not exist it will be
     * created.
     * <p>
     * <p>
     * <b>Note:</b> this singleton must not be loaded by more than one
     * classloader per JVM! Although this is no problem for Java classes it
     * does not work for native libraries. Unfortunately we cannot provide an
     * acceptable workaround. Thus, it is recommended to use this method from
     * a valid VRL plugin only. The VRL plugin system is aware of this problem
     * and handles it correctly.
     * </p>
     * @param canvas VRL canvas that shall be used to visualize classes
     * @return the instance of this singleton
     */
    public static synchronized MessageLogger getInstance(VisualCanvas canvas) {
        if (instance == null) {

            instance = new MessageLogger();

            if (canvas != null) {
                instance.setMainCanvas(canvas);
            }

        } else if (canvas != null) {
            instance.setMainCanvas(canvas);
        }

        return instance;
    }

    /**
     * <p>
     * Returns the instance of this singleton.
     * </p>
     * <p>
     * <b>Note:</b>If message
     * logging shall be used, please assign a visible canvas. For this
     * {@link #getInstance(eu.mihosoft.vrl.reflection.VisualCanvas)  }
     * can be used.
     * </p>
     * <p>
     * <b>Note:</b> this singleton must not be loaded by more than one
     * classloader per JVM if native libs have been loaded! Although this is
     * no problem for Java classes, it
     * does not work for native libraries. Unfortunately, we cannot provide an
     * acceptable workaround. Thus, it is recommended to use this method from
     * a valid VRL plugin only. The VRL plugin system is aware of this problem
     * and handles it correctly.
     * </p>
     */
    public static MessageLogger getInstance() {
        return getInstance(null);
    }

    /**
     * Returns the VRL canvas that is used to visualize classes.
     * @return the VRL canvas that is used to visualize classes
     */
    public VisualCanvas getMainCanvas() {
        return mainCanvas;
    }

    /**
     * <p>
     * The VRL canvas that shall be used to visualize the classes
     * </p>
     * <p>
     * <b>Notice:</b> as a side effect this method starts message logging
     * </p>
     * @param mainCanvas the VRL canvas to set
     */
    public void setMainCanvas(VisualCanvas mainCanvas) {
        this.mainCanvas = mainCanvas;
        stopLogging();
        startLogging();
    }

    /**
     * Starts VRL message logging.
     */
    public void startLogging() {
        stopLogging();
        messagingThread = new MessageThread();
        messagingThread.start();
    }

    /**
     * Stops VRL message logging.
     */
    public void stopLogging() {
        if (messagingThread != null) {
            messagingThread.stopLogging();
        }
    }

    /**
     * @return the messages
     */
    private StringBuffer getMessages() {
        return messages;
    }

    public static void addMessage(String s) {
        messages.append(s);

        if (messages.length() > 1000000) {
            messages.delete(0, 1000000);
        }
    }

    public void clearMessages() {
        if (messages.length() > 0) {
            messages.delete(0, messages.length());
        }
    }

    class MessageThread extends Thread {

        private boolean logging = true;

        public MessageThread() {
            //
        }

        @Override
        public void run() {

            while (logging) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    //
                }

                if (getMessages().length() > 0) {
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            if (mainCanvas != null) {
                                mainCanvas.getMessageBox().addMessageAsLog(
                                        "Output:",
                                        "<pre>" + messages + "</pre>",
                                        MessageType.INFO);
                                clearMessages();
                            }
                        }
                    });
                }
            }
        }

        /**
         * @param logging the logging to set
         */
        public void stopLogging() {
            this.logging = false;
        }
    }
}

