/* 
 * MessageBoxApplet.java
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;

/**
 * Dock applet used to display VRL messages (notifications).
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class MessageBoxApplet extends DockApplet
        implements MessageListener {

    private static final long serialVersionUID = -3844995611877748446L;
    private MessageBox messageBox;

    /**
     * Constructor.
     * @param messageBox the message box
     */
    public MessageBoxApplet(final MessageBox messageBox) {
        super(messageBox.getMainCanvas());
        this.messageBox = messageBox;
        messageBox.setMessageListener(this);

        this.setActionListener(new CanvasActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(CLICKED_ACTION)) {
                    if (messageBox.getNumberOfMessages() > 0) {
                        messageBox.showMessage();
                    } else if (messageBox.hasLog()) {
                        messageBox.showLog();
                    }
                    
                    messageBox.setHideLog(false);
                }
            }
        });

        setBorder(new EmptyBorder(0, 5, 10, 5));


        final JPopupMenu menu = new JPopupMenu("Options");
        JMenuItem item;

        item = new JMenuItem("Copy Messages to Clipboard");

        menu.add(item);
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                messageBox.copyMessagesToClipboard();
            }
        });

        item = new JMenuItem("Delete all Messages");
        menu.add(item);
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                messageBox.markAllMessagesAsRead();
            }
        });

        item = new JMenuItem("Clear Message Log");
        menu.add(item);
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                messageBox.markAllMessagesAsRead();
                messageBox.clearLog();
            }
        });



        /**
         * open popup with button 3.
         */
        class PopupListener extends MouseAdapter {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 3) {
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }

        addMouseListener(new PopupListener());

    }

    @Override
    public void messageEvent(MessageBox messageBox) {
        setNumberOfNotification(messageBox.getNumberOfMessages());
    }
}
