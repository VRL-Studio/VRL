/* 
 * Message.java
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

import java.awt.Dimension;
import java.io.Serializable;
import javax.swing.ImageIcon;

/**
 * This class defines a VRL message. It can be used to display messages via the
 * message box of the Canvas class. A message contains of <ul> <li>title</li>
 * <li>message text (simple HTML)</li> <li>pulse icon used to define the message
 * type</li> </ul>
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Message implements Serializable {

    private static final long serialVersionUID = -3699847340364148014L;
    private ImageIcon icon;
    private PulseIcon pulseIcon;
    private String text;
    private String title;
    //
    private MessageType messageType;
    private double showMessageDuration = 15;
    private boolean scrollToMessageEnd = false;
    private Dimension stillIconSize = new Dimension(80, 80);
    public static final String EMPHASIZE_BEGIN = "\"<tt><b>";
    public static final String EMPHASIZE_END = "</tt></b>\"";
    private boolean log;

    /**
     * Constructor.
     *
     * @param pulseIcon the pulse icon used to indicate the message type
     * @param title the message title
     * @param text the message text (simple HTML)
     */
    public Message(PulseIcon pulseIcon, String title, String text) {
        setPulseIcon(pulseIcon);
        setText(text);
        setTitle(title);
    }

    /**
     * Constructor.
     *
     * @param title the message title
     * @param text the message text (simple HTML)
     * @param messageType the message type
     */
    public Message(String title, String text, MessageType messageType) {
        setText(text);
        setTitle(title);
        setMessageType(messageType);
    }

    /**
     * Constructor.
     *
     * @param title the message title
     * @param text the message text (simple HTML)
     * @param messageType the message type
     * @param showMessageDuration the time this message is shown
     */
    public Message(
            String title, String text,
            MessageType messageType, double showMessageDuration) {
        this.text = text;
        this.title = title;
        this.messageType = messageType;
        this.showMessageDuration = showMessageDuration;
    }

    /**
     * Constructor.
     *
     * @param pulseIcon the pulse icon used to indicate the message type
     * @param title the message title
     * @param text the message text (simple HTML)
     * @param messageType the message type
     * @param showMessageDuration the time this message is shown
     */
    public Message(PulseIcon pulseIcon,
            String title, String text,
            MessageType messageType, double showMessageDuration) {
        setPulseIcon(pulseIcon);
        this.text = text;
        this.title = title;
        this.messageType = messageType;
        this.showMessageDuration = showMessageDuration;
    }

    /**
     * Defines the message icon.
     *
     * @param icon the icon to set
     */
    private void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    /**
     * Returns the message text.
     *
     * @return the message text (simple HTML)
     */
    public String getText() {
        return text;
    }

    /**
     * Defines the message text.
     *
     * @param text the message text to set (simple HTML)
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns the message pulse icon.
     *
     * @return the message pulse icon
     */
    public PulseIcon getPulseIcon() {
        return pulseIcon;
    }

    /**
     * Defines the message pulse icon.
     *
     * @param pulseIcon the pulse icon to set
     */
    public void setPulseIcon(PulseIcon pulseIcon) {
        this.pulseIcon = pulseIcon;

        if (pulseIcon != null) {
            Dimension size = pulseIcon.getSize();
            pulseIcon.setSize(stillIconSize);
            setIcon(new ImageIcon(pulseIcon.getStillImage()));
            pulseIcon.setSize(size);
        }
    }

    /**
     * Returns the message title.
     *
     * @return the message title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Defines the message title.
     *
     * @param title the title to sets
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the messageType
     */
    public MessageType getType() {
        return messageType;
    }

    /**
     * @param messageType the messageType to set
     */
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    /**
     * @return the showMessageDuration
     */
    public double getShowMessageDuration() {
        return showMessageDuration;
    }

    /**
     * @param showMessageDuration the showMessageDuration to set
     */
    public void setShowMessageDuration(double showMessageDuration) {
        this.showMessageDuration = showMessageDuration;
    }

    /**
     * @return the icon
     */
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * Indicates whether the end of the message shall be shown (automatic
     * scrolling).
     *
     * @return
     * <code>true</code> if the end of the message shall be shown;
     * <code>false</code> otherwise
     */
    public boolean isScrollToMessageEnd() {
        return scrollToMessageEnd;
    }

    /**
     * Defines whether to show the end of the message (automatic scrolling).
     *
     * @param scrollToMessageEnd the value to set
     */
    public void setScrollToMessageEnd(boolean scrollToMessageEnd) {
        this.scrollToMessageEnd = scrollToMessageEnd;
    }

    /**
     * Appends text to this message.
     *
     * @param s text to append
     */
    public void append(String t) {
        this.text += t;
    }

    /**
     * Removes all HTML tags from specified string. <p><b>Note:</b> This method
     * does not support "dirty" HTML such as &lt;bsometext&gt;&lt;b/&gt;. For
     * this purpose a HTML parser library should be used. </p>
     *
     * @param text
     * @return the specified string without HTML tags
     * @see http://jtidy.sourceforge.net/
     */
    public static String removeHTML(String text) {
        return text.replaceAll("\\<[^>]*>", "");
    }

    public static String generateHTMLSpace(int n) {
        String blank = "&nbsp;";

        String result = "";

        for (int i = 0; i < n; i++) {
            result += blank;
        }

        return result;
    }

    /**
     * Removes the specified number of characters from the front and returns the
     * new message.
     *
     * @param len number of characters to remove from front
     */
    public Message remove(int len) {
        String result = text;
        if (result.length() > len) {
            result = result.substring(len + 1, result.length());
        }

        Message m = new Message(pulseIcon, title, result, messageType, showMessageDuration);
        m.setScrollToMessageEnd(scrollToMessageEnd);

        return m;
    }
//    @Override
//    protected void finalize() throws Throwable {
//
//        System.out.println(getClass().getName() + ": Finalize!!!");
//
//        super.finalize();
//    }

    /**
     * @return the log
     */
    public boolean isLog() {
        return log;
    }

    /**
     * @param log the log to set
     */
    public void setLog(boolean log) {
        this.log = log;
    }
}
