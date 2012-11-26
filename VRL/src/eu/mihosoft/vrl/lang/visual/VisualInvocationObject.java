/* 
 * VisualInvocationObject.java
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

package eu.mihosoft.vrl.lang.visual;

import eu.mihosoft.vrl.reflection.DefaultMethodRepresentation;
import eu.mihosoft.vrl.reflection.DefaultObjectRepresentation;
import eu.mihosoft.vrl.system.VThread;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.visual.MessageBox;
import eu.mihosoft.vrl.visual.MessageType;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VisualInvocationObject {

    private VisualCanvas canvas;
    private Collection<DefaultMethodRepresentation> methods;
    private Thread thread;
    private boolean running;
    private StartObject startObject;
    private StopObject stopObject;

    public synchronized boolean init(VisualCanvas canvas) {

        this.canvas = canvas;

        startObject = ControlFlowUtils.getStartObject(canvas);
        stopObject = ControlFlowUtils.getStopObject(canvas);

        if (!ControlFlowUtils.validateSession(canvas)) {
            methods = null;
        } else {
            methods = ControlFlowUtils.getInvocationList(canvas);
        }

        // methods object is null if errors occured (see MessageBox output)
        return methods != null;
    }

    public synchronized void invoke() {
        final MessageBox mBox = canvas.getMessageBox();
        
        running = true;

        if (thread == null) {
            thread = new VThread() {

                @Override
                public void run() {
                    running = true;
                    try {
                        if (canvas.isInvokeWaitEffect()) {
                            for (DefaultMethodRepresentation mRep : methods) {
                                mRep.startWaitEffect();
                            }
                        }

                        for (DefaultMethodRepresentation mRep : methods) {
                            if (!isRunning()) {
                                break;
                            } else {

                                if (canvas.isInvokeWaitEffect()) {
                                    try {
                                        Thread.sleep(
                                                canvas.getInvocationDelay());
                                    } catch (InterruptedException ex) {
                                        //
                                    }
                                }

                                mRep.automaticInvocation(canvas.getInspector());

                                if (canvas.isInvokeWaitEffect()) {
                                    mRep.stopWaitEffect();
                                }
                                
                                if (!mRep.areParamsValid()) {
                                    break;
                                }
                            }
                        }
                    } catch (Throwable ex) {
                        
                        // error output is now handled by objectinspector
                        // TODO cleanup

//                        String message = ex.toString();
//
//                        if (ex.getCause() != null) {
//                            message += "<br>" + ex.getCause().toString();
//                        }
//
//                        mBox.addMessage("Cannot invoke Method:",
//                                message, MessageType.ERROR);

//                        Logger.getLogger(
//                                VisualInvocationObject.class.getName()).
//                                log(Level.SEVERE, null, ex);
                    }
                    
                    thread = null;
                    running = false;
                }
            };
            startObject.thread = thread;
            stopObject.invocationObj = this;

            thread.start();
        } else {

            String text = "Method still running! Please wait!";

            mBox.addUniqueMessage("Cannot invoke method:", text,
                    null, MessageType.ERROR);

            running = false;
        }
    }

    /**
     * @return the running
     */
    public synchronized boolean isRunning() {
        return running;
    }

    /**
     * Stops invokation.
     */
    public synchronized void stop() {
        running = false;

        for (DefaultMethodRepresentation mRep : methods) {
            if (canvas.isInvokeWaitEffect()) {
                mRep.stopWaitEffect();
            }
        }
    }
}
