/* 
 * VMessage.java
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

import eu.mihosoft.vrl.lang.VWorkflowException;
import eu.mihosoft.vrl.visual.Message;
import eu.mihosoft.vrl.visual.MessageBox;
import eu.mihosoft.vrl.visual.MessageType;

/**
 * Util class that simplifies message handling. 
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VMessage {

    
    private VMessage() {
        // no instantiation allowed. not in this class either!
        throw new AssertionError();

    }

    /**
     * Shows a message in the message box of the current canvas.
     * @param title message title
     * @param msg message
     * @param msgType message type (info,warning, error etc.)
     * 
     * @return message the message
     */
    public static Message msg(String title, String msg, MessageType msgType) {
        return getMsgBox().addMessage(title, msg, msgType);
    }

    /**
     * Shows an info message in the message box of the current canvas.
     * 
     * @param title message title
     * @param msg  message
     * 
     * @return message the message
     */
    public static Message info(String title, String msg) {
        return msg(title, msg, MessageType.INFO);
    }

    /**
     * Shows a warning message in the message box of the current canvas.
     * 
     * @param title message title
     * @param msg  message
     * 
     * @return message the message
     */
    public static Message warning(String title, String msg) {
        return msg(title, msg, MessageType.WARNING);
    }

    /**
     * Shows an error message in the message box of the current canvas.
     * 
     * @param title message title
     * @param msg  message
     * 
     * @return message the message
     */
    public static Message error(String title, String msg) {
        return msg(title, msg, MessageType.ERROR);
    }

    /**
     * Throws an exception (stops workflow execution) and shows an error
     * message in the message box of the current canvas.
     * 
     * @param title message title
     * @param msg  message
     */
    public static void exception(String title, String msg) {
        throw new VWorkflowException(title, msg);
    }
    
    /**
     * Dewfines the message as read.
     * @param m message
     */
    public static void defineMessageAsRead(Message m) {
        VMessage.getMsgBox().messageRead(m);
    }

    /**
     * Returns the message box of the current canvas.
     * @return message box of the current canvas
     */
    public static MessageBox getMsgBox() {
        return VRL.getCurrentProjectController().
                getCurrentCanvas().getMessageBox();
    }
    
    /**
     * Shows a critical error message that gives instructions on how to send a
     * bug report including logs and the current project.
     * 
     * @return message the message
     */
    public static Message criticalErrorDetected() {
        
        System.err.println("Critical Error: a critical error has been detected! Please "
                    + Constants.WRITE_VRL_BUG_REPORT_PLAIN
                    + " and attach your complete log and the current project (if possible).");
        
        return VMessage.error("Critical Error", "A critical error has been detected! Please "
                    + Constants.WRITE_VRL_BUG_REPORT
                    + " and attach your complete log and the current project (if possible).");
    }
}
