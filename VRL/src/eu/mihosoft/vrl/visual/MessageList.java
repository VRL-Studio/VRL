/* 
 * MessageList.java
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

package eu.mihosoft.vrl.visual;

import java.util.ArrayList;

/**
 * Message lists are used for MessageBoxViewer objects. This class gives an
 * easy way to define multi-line messages and to emulate a message buffer
 * (as used in console windows) .
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class MessageList {

    private ArrayList<String> messages = new ArrayList<String>();
    private int messageListSize = 5;

    public MessageList() {
    }

    public void addMessage(String message) {
        messages.add(message);
        removeOldMessages();
    }

    public String getMessages() {
        String result = "";
        for (int i = 0; i < getMessageListSize() && i < messages.size(); i++) {
            result += messages.get(i) + "<br>";
        }

        return result;
    }

    private void removeOldMessages() {
        int numberOfMessagesToDelete =
                Math.max(0, messages.size() - getMessageListSize());
        while (numberOfMessagesToDelete > 0) {
            messages.remove(0);
            numberOfMessagesToDelete--;
        }
    }

    /**
     * @return the messageListSize
     */
    public int getMessageListSize() {
        return messageListSize;
    }

    /**
     * @param messageListSize the messageListSize to set
     */
    public void setMessageListSize(int messageListSize) {
        this.messageListSize = messageListSize;
    }
}
