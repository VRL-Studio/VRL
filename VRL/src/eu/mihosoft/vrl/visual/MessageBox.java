/* 
 * MessageBox.java
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

package eu.mihosoft.vrl.visual;

import eu.mihosoft.vrl.animation.Animation;
import eu.mihosoft.vrl.animation.AnimationBase;
import eu.mihosoft.vrl.animation.AnimationTask;
import eu.mihosoft.vrl.animation.FrameListener;
import eu.mihosoft.vrl.animation.LinearInterpolation;
import eu.mihosoft.vrl.reflection.DefaultObjectRepresentation;
import eu.mihosoft.vrl.system.VSysUtil;
import eu.mihosoft.vrl.visual.ImageUtils;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Message array.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class MessageArrayList extends ArrayList<Message> implements Serializable {

    private static final long serialVersionUID = 2493499688932075663L;
    //
}

/**
 * Message box used to display VRL messages.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class MessageBox extends VComponent implements
        ShadowPainter, ComponentListener, Serializable {

    private static final long serialVersionUID = 5078186563510959741L;
    transient private BufferedImage buffer = null;
    private boolean opened = false;
    private MessageArrayList messages = new MessageArrayList();
    private MessageArrayList readMessages = new MessageArrayList();
    private Message log;
    private JPanel iconPane = new TransparentPanel();
    private int messagePointer;
    private Message currentMessage;
    private TransparentLabel numberOfMessagesLabel =
            new TransparentLabel("1/1");
    private JPanel messagePanel = new TransparentPanel() {

        @Override
        public Insets getInsets() {
            return new Insets(10, 10, 10, 10);
        }
    };
    private JTextPane messageField = new JTextPane();
//    private TransparentLabel messageField = new TransparentLabel("");
    private int messageHeight = 0;
    private int maxMessageHeight = 180;
    private boolean opening = false;
    private Shape shape;
    private MessageListener messageListener;
    private int numberOfMessages;
    private AnimationBase currentHideAnim;
    private AnimationBase currentShowAnim;
    private JScrollPane messageFieldScrollPane;
    private PrintStream messageStream;
    private String messageStreamTitle = "Output:";
    private PrintStream errorStream;
    private String errorStreamTitle = "Error Output:";
    public static final String MAX_HEIGHT_KEY = "MessageBox:maxHeight";
    public static final String SHOW_ANIM_DURATION_KEY = "MessageBox:showAnimDuration";
    public static final String HIDE_ANIM_DURATION_KEY = "MessageBox:hideAnimDuration";
    public static final String OPEN_DURATION_KEY = "MessageBox:openDuration";
    public static final String TEXT_COLOR_KEY = "MessageBox:Text:Color";
    public static final String BOX_COLOR_KEY = "MessageBox:Color";
    public static final String TOP_TRANSPARENCY_KEY = "MessageBox:topTransparency";
    public static final String BOTTOM_TRANSPARENCY_KEY = "MessageBox:bottomTransparency";
    public static final String ICON_COLOR_KEY = "MessageBox:Icon:Color";
    public static final String ACTIVE_ICON_COLOR_KEY = "MessageBox:Icon[active]:Color";
    /**
     * defines whether Message.scrollToMessageEnd has an effect (disable this if
     * manual scrolling shall be possible)
     */
    private boolean autoAdjustScrollbar = false;
    private boolean hideLog = false;
    private Style cachedMessageStyle = null;
    /**
     * Defines whether messagebox uses custom image buffer.
     */
    private boolean bufferingEnabled = true;
    private int maxLogSize = Integer.MAX_VALUE;

    /**
     * Constructor.
     *
     * @param mainCanvas the main canvas
     */
    public MessageBox(Canvas mainCanvas) {
        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.X_AXIS);
        setLayout(layout);
        setOpaque(false);
        setMainCanvas(mainCanvas);

        messagePanel.setLayout(new VBoxLayout(messagePanel, VBoxLayout.X_AXIS));
        messagePanel.setAlignmentX(LEFT_ALIGNMENT);
        messagePanel.setMaximumSize(new Dimension(Short.MAX_VALUE,
                Short.MAX_VALUE));

        messagePanel.setOpaque(false);
        this.add(messagePanel);

        messageField.setAlignmentX(TOP_ALIGNMENT);

        messageFieldScrollPane = new JScrollPane(messageField);
        messageFieldScrollPane.getViewport().
                setBackground(VSwingUtil.TRANSPARENT_COLOR);
        messageFieldScrollPane.getViewport().setOpaque(false);
        messageFieldScrollPane.setBackground(VSwingUtil.TRANSPARENT_COLOR);
        messageFieldScrollPane.setOpaque(false);
        messageFieldScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        messageFieldScrollPane.setViewportBorder(new EmptyBorder(0, 0, 0, 0));

        messageFieldScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        messageFieldScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        messageFieldScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        messageFieldScrollPane.getVerticalScrollBar().addAdjustmentListener(
                new AdjustmentListener() {

                    @Override
                    public void adjustmentValueChanged(AdjustmentEvent e) {
                        if (unreadMessages()) {
                            autoAdjustScrollbar = false;
                            if (MessageBox.this.isOpened()) {
//                                MessageBox.this.showMessage();
                                increaseMessageShowDuration();
                            }

                        }
                    }
                });

        messagePanel.add(messageFieldScrollPane);

        messageField.setBackground(VSwingUtil.TRANSPARENT_COLOR);
        messageField.setContentType("text/html");
        messageField.setOpaque(false);
        messageField.setEditable(false);
        messageField.setEditorKit(new VHTMLEditorKit());

//        messageField.setHorizontalTextPosition(JLabel.RIGHT);
//        messageField.setVerticalTextPosition(JLabel.TOP);
        messagePanel.add(numberOfMessagesLabel);

        numberOfMessagesLabel.setForeground(Color.WHITE);

        NextIcon next = new NextIcon(mainCanvas) {

            @Override
            public void stateChanged() {
                if (unreadMessages()) {
                    autoAdjustScrollbar = false;
//                    MessageBox.this.showMessage();
                    increaseMessageShowDuration();
                }
            }
        };

        messagePanel.add(next);

        next.setActionListener(new CanvasActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (currentShowAnim != null) {
                    currentShowAnim.requestDeletion();
                }
                if (currentHideAnim != null) {
                    currentHideAnim.requestDeletion();
                }

                nextMessage();
            }
        });

        iconPane.setMinimumSize(
                new Dimension(50, Short.MAX_VALUE));
        iconPane.setMaximumSize(
                new Dimension(50, Short.MAX_VALUE));
        iconPane.setAlignmentX(RIGHT_ALIGNMENT);
        iconPane.setOpaque(
                false);


        this.add(iconPane);

        MessageCloseIcon close = new MessageCloseIcon(mainCanvas) {

            @Override
            public void stateChanged() {
                if (unreadMessages()) {
                    autoAdjustScrollbar = false;

                    increaseMessageShowDuration();
                }
            }
        };

        getIconPane().add(close);

        close.setActionListener(
                new CanvasActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

                        if (currentShowAnim != null) {
                            currentShowAnim.requestDeletion();
                        }
                        if (currentHideAnim != null) {
                            currentHideAnim.requestDeletion();
                        }

                        MessageBox.this.hideMessage();
                        MessageBox.this.setHideLog(true);
                    }
                });

        setPreferredSize(
                new Dimension(0, 0));

        class FieldAdapter extends MouseAdapter implements Serializable {

            @Override
            public void mouseClicked(MouseEvent e) {
                //

                PulseIcon pulse = getCurrentMessage().getPulseIcon();

                if (pulse != null) {

                    CanvasChild c = pulse.getCanvasChild();

                    // TODO find a better way to get transparency
                    if (c != null && (c instanceof Connector)) {
                        Connector connector = (Connector) c;
                        CanvasWindow w =
                                connector.getValueObject().getParentWindow();

                        double transparency = w.getTransparency();

                        pulse.pulse(c, transparency);

                    } else {
                        pulse.pulse(c);
                    }
                }

                autoAdjustScrollbar = false;
//                MessageBox.this.showMessage();
                increaseMessageShowDuration();
            }
        }

        messageField.addMouseListener(
                new FieldAdapter());
        
        messageField.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent me) {
                increaseMessageShowDuration();
            }

            @Override
            public void mouseMoved(MouseEvent me) {
                increaseMessageShowDuration();
            }
        });

        setBorder(
                new ShadowBorder(this));

        addComponentListener(
                this);
        messageStream = new MessageStream(
                this, getMessageStreamTitle(), MessageType.INFO, System.out);


        errorStream = new MessageStream(
                this, getErrorStreamTitle(), MessageType.ERROR, System.err);
    }

    private void enableHTML() {
        messageField.setContentType("text/html");
        messageField.setOpaque(false);
        messageField.setEditable(false);
        messageField.setEditorKit(new VHTMLEditorKit());
    }

    private void disableHTML() {
        messageField.setContentType("text/plain");
        final Color color = getStyle().
                getBaseValues().getColor(TEXT_COLOR_KEY);
        messageField.setForeground(color);
        messageField.setOpaque(false);
        messageField.setEditable(false);
        messageField.setEditorKit(new StyledEditorKit());
    }

    void copyMessagesToClipboard() {
        StringBuilder builder = new StringBuilder();

//        for (Message m : messages) {
//            builder.append("<h1>").append(m.getTitle()).append("</h1><br>\n").
//                    append("<p>\n").append(m.getText()).append("\n</p>\n\n");
//        }


        for (Message m : messages) {
            builder.append("Title: ").append(m.getTitle()).append("\n").
                    append("Message: ").append(m.getText()).append("\n\n");
        }

        VSysUtil.copyToClipboard(builder.toString());
    }

    /**
     * If the messagebox is currently opened this method increases the time the
     * messagebox is kept opened.
     */
    private void increaseMessageShowDuration() {
        if (currentHideAnim != null && (isOpened() || isOpening())) {
            currentHideAnim.reset();
            currentHideAnim.restoreOffset();
        }
    }

    /**
     * @return the hideLog
     */
    public boolean isHideLog() {
        return hideLog;
    }

    /**
     * @param hideLog the hideLog to set
     */
    public void setHideLog(boolean hideLog) {
        this.hideLog = hideLog;
    }

    /**
     * @return the bufferingEnabled
     */
    public boolean isBufferingEnabled() {
        return bufferingEnabled;
    }

    /**
     * @param bufferingEnabled the bufferingEnabled to set
     */
    public void setBufferingEnabled(boolean bufferingEnabled) {
        this.bufferingEnabled = bufferingEnabled;
    }

    class MessageStream extends PrintStream {

        MessageBox mBox;
        MessageType mType;
        String messageTitle;
        ArrayDeque<String> buffer = new ArrayDeque<String>();
        long timeStart = 0;
        long timeStop = 0;
        int maxLines = 300;

        public MessageStream(MessageBox mBox,
                String messageTitle, MessageType mType, PrintStream stream) {
            super(stream);
            this.mBox = mBox;
            this.messageTitle = messageTitle;
            this.mType = mType;
        }

        @Override
        public void println(String message) {

            buffer.addLast(message);

            // remove old entries
            while (buffer.size() > maxLines) {
                buffer.removeFirst();
            }

            // don't update the message buffer more than
            // 5 times per second
            if (timeStop - timeStart > 200
                    || (timeStart == 0 && timeStop == 0)) {
                timeStart = System.currentTimeMillis();

                String msg = "";
                int counter = 0;

                for (Iterator<String> i = buffer.descendingIterator(); i.hasNext() || counter < maxLines;/*
                         *
                         */) {
                    if (i.hasNext()) {
                        msg += i.next();
                    }
                    msg += "<br>";

                    counter++;
                }

                mBox.addUniqueMessage(
                        messageTitle, msg,
                        null, mType);
            }
            timeStop = System.currentTimeMillis();
        }
    }

// this should remind me of the fact that if the whole container is covered with
// child components they have to be non opaque, i.e. setOpaque(false);
// otherwise the container won't be painted. Painting can be forced using
// the following overloaded version of paint(Graphics g).
//    @Override
//    public void paint(Graphics g){
//        paintComponent(g);
//        super.paint(g);
//    }
//
    /**
     * Defines all messages as read.
     */
    public void markAllMessagesAsRead() {

        while (messagePointer > 0) {
            currentMessageRead();
            messagePointer--;

        }

        currentMessageRead();

        hideMessage();
        updateNumberOfMessageLabel();
    }

    @Override
    protected void paintComponent(Graphics g) {

        if (cachedMessageStyle != getStyle()) {
            cachedMessageStyle = getMainCanvas().getStyle();
            numberOfMessagesLabel.setForeground(
                    cachedMessageStyle.getBaseValues().getColor(TEXT_COLOR_KEY));
            setMessageWithoutAnimation(getCurrentMessage());
        }

        if (buffer == null || buffer.getWidth() != getWidth()
                || buffer.getHeight() != getHeight()) {

            Graphics2D g2;

            if (isBufferingEnabled()) {
                buffer = ImageUtils.createCompatibleImage(getWidth(), getHeight());

                g2 = buffer.createGraphics();
            } else {
                g2 = (Graphics2D) g;
            }

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setStroke(new BasicStroke(1));
            g2.setColor(cachedMessageStyle.getBaseValues().getColor(BOX_COLOR_KEY));

            int top = getInsets().top;
            int left = getInsets().left;
            int bottom = getInsets().bottom;
            int right = getInsets().right;

            shape =
                    new RoundRectangle2D.Double(left, top,
                    getWidth() - left - right,
                    getHeight() - top - bottom, 20, 20);

            g2.fill(getShape());

            g2.setStroke(new BasicStroke(2));
            g2.setColor(cachedMessageStyle.getBaseValues().getColor(TEXT_COLOR_KEY));

            g2.draw(getShape());

            // TODO find another way than redefining shape
            // currently this has to be done because of stroke
            shape =
                    new RoundRectangle2D.Double(left, top,
                    getWidth() - left - right + 1,
                    getHeight() - top - bottom + 1, 20, 20);

            if (isBufferingEnabled()) {
                g2.dispose();

                buffer =
                        ImageUtils.gradientMask(buffer,
                        cachedMessageStyle.getBaseValues().getFloat(BOTTOM_TRANSPARENCY_KEY),
                        cachedMessageStyle.getBaseValues().getFloat(TOP_TRANSPARENCY_KEY));
            }
        }
        if (isBufferingEnabled()) {
            Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(buffer, 0, 0, getWidth(), getHeight(), null);
        }
    }

    @Override
    public void contentChanged() {
        buffer = null;

        super.contentChanged();
    }

    /**
     * Defines the message height.
     *
     * @param messageHeight the height to set
     */
    void setMessageHeight(int messageHeight) {
        this.messageHeight = messageHeight;
    }

    /**
     * Hides the message box.
     *
     * @param offset the animation offset
     * @param duration the animation duration
     */
    void hideBox(double offset, double duration) {

        Animation a = new HideMessageBoxAnimation(this);

        currentHideAnim = a;

        a.setOffset(offset);
        a.setDuration(duration);

        a.addFrameListener(new AnimationTask() {

            @Override
            public void firstFrameStarted() {
//                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void frameStarted(double time) {
//                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void lastFrameStarted() {
                if (unreadMessages() == false) {
                    currentMessage = null;
                }
            }
        });

//        AnimationManager manager = mainCanvas.getAnimationManager();

        getMainCanvas().getAnimationManager().addUniqueAnimation(a, 1);

        getMainCanvas().getDock().fadeIn(offset, duration);

    }

    /**
     * Shows the message box.
     *
     * @param offset the animation offset
     * @param duration the animation duration
     */
    private void showBox(double offset, double duration) {

        updateNumberOfMessageLabel();

        Animation a = new ShowMessageBoxAnimation(this);
        currentShowAnim = a;
        a.setOffset(offset);
        a.setDuration(duration);

        // This framelistener adds another show messagebox animation to update
        // the messagebox layout (height). It prevents the messagebox from being
        // to small for the message to show. This happened if the messagebox
        // got big message while it displayed a short one. It didn't occure if
        // the messagebox was hidden before the second message was displayed.
        //
        // The workaround is to add a second show messagebox animation directly
        // after the original animation (at its last frame).
        a.addFrameListener(new AnimationTask() {

            @Override
            public void firstFrameStarted() {
                //
            }

            @Override
            public void frameStarted(double time) {
                //
            }

            @Override
            public void lastFrameStarted() {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        Animation updateAnimation =
                                new ShowMessageBoxAnimation(MessageBox.this);
                        updateAnimation.setDuration(0.1);
                        getMainCanvas().getAnimationManager().
                                addAnimation(updateAnimation);


                    }
                });


            }
        });

        getMainCanvas().getAnimationManager().addUniqueAnimation(a, 1);
        getMainCanvas().getDock().fadeOut(offset, duration);
    }

    /**
     * Switches to the next message in the message list and defines the
     * previously shown message as read.
     */
    public void nextMessage() {
        if (unreadMessages()) {
            currentMessageRead();
        }

        if (messagePointer > 0) {
            messagePointer--;
            updateNumberOfMessageLabel();

            setCurrentMessage();
            setMessage(
                    currentMessage);

        } else {
            hideMessage();
        }
    }

    /**
     * Indicates if unread messages are in the message list.
     *
     * @return
     * <code>true</code> if unread messages are in the list;
     * <code>false</code> otherwise
     */
    public boolean unreadMessages() {
        boolean result = false;

        for (Message m : messages) {
            boolean mIsUnread = true;

            for (Message n : readMessages) {
                if (m.equals(n)) {
                    mIsUnread = false;
                }
            }
            if (mIsUnread) {
                result = mIsUnread;

                break;
            }
        }

        return result;
    }

    /**
     * updates the label that indicates the number of messages and the currently
     * displayed message.
     */
    public void updateNumberOfMessageLabel() {
        numberOfMessagesLabel.setText(
                messages.size() - messagePointer + "/" + messages.size());

        numberOfMessages =
                messages.size();
        //notify message listener

        if (messageListener != null) {
            messageListener.messageEvent(this);
        }
    }

    /**
     * Deletes all read messages in the message list.
     */
    public void deleteReadMessages() {
        for (Message m : readMessages) {
            messages.remove(m);
        }

        if (messagePointer > 0) {
            messagePointer = messages.size() - 1;
        }

        numberOfMessages = messages.size();
        //notify message listener


        if (messageListener != null) {
            messageListener.messageEvent(this);
        }

        // prevents memory leak
        readMessages = new MessageArrayList();

        setCurrentMessage();

    }

    /**
     * Defines message with specific id as read.
     *
     * @param msgID the id of the message that is to be defined as read
     */
    public void messageRead(int msgID) {
        messageRead(messages.get(msgID));
    }

    /**
     * Defines a message as read.
     *
     * @param message the message that is to be defined as read
     */
    public void messageRead(Message message) {
        readMessages.add(message);
    }

    /**
     * Defines the currrent message as read.
     */
    public void currentMessageRead() {
        if (unreadMessages()) {
            messageRead(messagePointer);
        }
    }

    /**
     * Defines the message to show.
     *
     * @param message the message to show
     */
    public void setMessage(Message message) {

        double duration = (Double) getStyle().getBaseValues().get(
                OPEN_DURATION_KEY);

        setMessage(
                message, duration);
    }

    /**
     * Defines the message to show.
     *
     * @param message the message to show
     * @param showMessageDuration defines how long the message is to be shown
     */
    public void setMessage(Message message, double showMessageDuration) {

        setMessageWithoutAnimation(message);
        showMessage(
                showMessageDuration);
    }

    /**
     * Inserts a string to the current html document of the specified editor.
     * Main usage is to add strings to the current log message.
     *
     * @param editor message field
     * @param html html string to add
     * @param location document location where the specified string shall be
     * added
     */
    private void insertHTML(JEditorPane editor, String html, int location) {
        //assumes editor is already set to "text/html" type
        HTMLEditorKit kit =
                (HTMLEditorKit) editor.getEditorKit();
        Document doc = editor.getDocument();
        StringReader reader = new StringReader(html);
        try {
            kit.read(reader, doc, location);
        } catch (BadLocationException ex) {
            Logger.getLogger(MessageBox.class.getName()).log(
                    Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MessageBox.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    private String messageHeader(Message m) {

        final Color color = getStyle().
                getBaseValues().getColor(TEXT_COLOR_KEY);
        final String textColor = Integer.toHexString(color.getRGB()).
                substring(2);

        String text = "<html><body text=\"" + textColor + "\">"
                + "<" + VHTML.messageType2Tag(m.getType())
                + " width=50 height=50>"
                + "<font size=+2>"
                + m.getTitle()
                + "</font><p></p>";

        return text;
    }

    private String messageBody(Message m) {
        return m.getText();
    }

    private static String messageFooter(Message m) {
        return "</body></html>";
    }

    /**
     * Defines the message to show and updates the view. It does not involve
     * animation management.
     *
     * @param message the message to set
     */
    protected void setMessageWithoutAnimation(final Message message) {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (message != null && message.getPulseIcon() != null
                        && message.getPulseIcon().getMessageType()
                        == MessageType.SILENT) {

                    String text = "";

                    if (message.isLog()) {
                        disableHTML();
                    } else {
                        enableHTML();
                    }
                    messageField.setText(text);
                } else {

                    String text = "";

                    if (message.isLog()) {
                        disableHTML();
                        text = ">> " + message.getTitle() + "\n\n"
                                + message.getText();
                    } else {
                        enableHTML();
                        text =
                                messageHeader(message)
                                + messageBody(message)
                                + messageFooter(message);
                    }

                    messageField.setText(text);
                    messageField.setCaretPosition(0);
                }

                autoAdjustScrollbar = true;
                updateLayout();
            }
        });

    }

    /**
     * Defines the current message depending on the message pointer.
     */
    public void setCurrentMessage() {
        if (messagePointer >= 0 && messages.size() > 0 && unreadMessages()) {
            currentMessage = messages.get(messagePointer);
            setMessageWithoutAnimation(
                    currentMessage);
        }
    }

    /**
     * Defines the message that is to be shown by id.
     *
     * @param msgID the id of the message to show
     */
    public void setMessage(int msgID) {
        Message message = messages.get(msgID);
        setMessage(
                message);
    }

    /**
     * Hides the message box.
     */
    public void hideMessage() {
        int distance = Math.abs(0 - getMessageHeight());

        double hideSpeedValue = (Double) getStyle().getBaseValues().get(
                SHOW_ANIM_DURATION_KEY);
        double hideSpeed = hideSpeedValue / getMaxMessageHeight();

        double hideDuration = hideSpeed * distance;

        hideBox(0, hideDuration);
    }

    /**
     * Shows the message box.
     */
    public void showMessage() {

        double duration = (Double) getStyle().getBaseValues().get(
                OPEN_DURATION_KEY);

        showMessage(duration);
    }

    /**
     * Shows the message box.
     *
     * @param showMessageDuration the animation duration
     */
    public void showMessage(double showMessageDuration) {
        int distance = Math.abs(getMaxMessageHeight() - getMessageHeight());

        // SHOW

        double showSpeedValue = (Double) getStyle().getBaseValues().get(
                SHOW_ANIM_DURATION_KEY);

        double showSpeed = showSpeedValue / getMaxMessageHeight();

        double showDuration = showSpeed * distance;

        if (!isOpening()) {
            showBox(0, showDuration);


        } // HIDE

        double hideSpeedValue = (Double) getStyle().getBaseValues().get(
                SHOW_ANIM_DURATION_KEY);

        double hideSpeed = hideSpeedValue / getMaxMessageHeight();

        double hideDuration = hideSpeed * distance;

        hideBox(
                showDuration + showMessageDuration, hideDuration);
    }

    /**
     * Returns the icon pane.
     *
     * @return the icon pane
     */
    private JPanel getIconPane() {
        return iconPane;
    }

    /**
     * @return
     * <code>true</code> if the message box is opened (shown);
     * <code>false</code> otherwise
     */
    public boolean isOpened() {
        return opened;

    }

    /**
     * Defines the message box state.
     *
     * @param opened the message box state to set
     */
    void setOpened(boolean opened) {
        this.opened = opened;

        if (!opened) {
            deleteReadMessages();

        }
    }

    /**
     * Adds a message to the message box.
     *
     * @param title the message title
     * @param message the message
     * @param type the message type
     * @return the message
     */
    public Message addMessage(String title, String message, MessageType type) {
        return addMessage(title, message, null, type);
    }

    /**
     * Adds a message to the message box.
     *
     * @param title the message title
     * @param message the message
     * @param c the canvas child that is to be associated with this message
     * @param type the message type
     * @return the message
     */
    public Message addMessage(String title, String message,
            CanvasChild c, MessageType type) {
        return addMessage(title, message, c, type, 15);
    }

    /**
     * Adds a message to the message box.
     *
     * @param title the message title
     * @param message the message
     * @param c the canvas child that is to be associated with this message
     * @param type the message type
     * @param showMessageDuration defines how long the message is to be shown
     * @return the message
     */
    public Message addMessage(String title, String message,
            CanvasChild c, MessageType type, double showMessageDuration) {

        return addMessage(new Message(title, message, type, showMessageDuration), c);
    }

    /**
     * Adds a message to the message box.
     *
     * @param message the message to show
     * @param c the canvas child that is to be associated with this message
     * @return the message
     */
    public Message addMessage(final Message message, CanvasChild c) {

        // do not show message if canvas is inactive
        if (getMainCanvas().isIgnoreMessages()) {
            return message;
        }

        PulseIcon icon = new PulseIcon(getMainCanvas(), c, message.getType());

//        pulse.setSize(80, 80);

        // TODO find a better way for the transparency
        // only has an effect if c != null


        if (c instanceof Connector) {

            Connector connector = (Connector) c;
            CanvasWindow w =
                    connector.getValueObject().getParentWindow();

            double transparency = w.getTransparency();

//            icon.pulse(c, transparency);
            getMainCanvas().getEffectPane().pulse(c,
                    message.getType(), transparency);

        } else {
//            icon.pulse(c);
            if (c != null) {
                getMainCanvas().getEffectPane().pulse(c,
                        message.getType());
            }
        }

        if (message.getPulseIcon() == null) {
            message.setPulseIcon(icon);
        }

        messages.add(message);
        messagePointer =
                messages.size() - 1;

        // TODO: why do these two lines produce crashes (gui freezes)
        // if JTextPane is used? There occur no problems with TransparentLabel.
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                setCurrentMessage();
                setMessage(
                        currentMessage, message.getShowMessageDuration());
            }
        });

        setOpened(true);

        deleteReadMessages();
        updateNumberOfMessageLabel();

        return message;
    }

    /**
     * Adds a message to the message box. If an equal message has already been
     * added it will be deleted. Messages are defined as equal if the title,
     * message type and canvas child are equal.
     *
     * @param title the message title
     * @param message the message
     * @param c the canvas child that is to be associated with this message
     * @param type the message type
     * @return the message
     */
    public Message addUniqueMessage(String title, String message,
            CanvasChild c, MessageType type) {
        return addUniqueMessage(title, message, c, type, 15);
    }

    /**
     * Adds a message to the message box. If an equal message has already been
     * added it will be deleted. Messages are defined as equal if the title,
     * message type and canvas child are equal.
     *
     * @param title the message title
     * @param message the message
     * @param c the canvas child that is to be associated with this message
     * @param type the message type
     * @param showMessageDuration defines how long the message is to be shown
     * @return the message
     */
    public Message addUniqueMessage(String title, String message,
            CanvasChild c, MessageType type, double showMessageDuration) {
        return addUniqueMessage(
                new Message(title, message, type, showMessageDuration), c);
    }

    /**
     * Adds a message to the message box. If an equal message has already been
     * added it will be deleted. Messages are defined as equal if the title and
     * message type are equal. This method alsways shows the end of the message
     * (like a log window)
     *
     * @param title the message title
     * @param message the message
     * @param c the canvas child that is to be associated with this message
     * @param type the message type
     * @return the message
     */
    public Message addMessageAsLog(String title, String message,
            MessageType type) {
        return addMessageAsLog(title, message, type, 15);
    }

    /**
     * Adds a message to the message box. If an equal message has already been
     * added it will be deleted. Messages are defined as equal if the title and
     * message type are equal. This method alsways shows the end of the message
     * (like a log window)
     *
     * @param title the message title
     * @param message the message
     * @param c the canvas child that is to be associated with this message
     * @param type the message type
     * @param showMessageDuration defines how long the message is to be shown
     * @return the message
     */
    public Message addMessageAsLog(String title, final String message,
            MessageType type, double showMessageDuration) {

        if ((getCurrentMessage() != log || log == null || !isOpened())
                && !isHideLog()) {

            String msg = "";

            if (log != null) {
                msg = log.getText() + message;
            } else {
                msg = message;
            }

            log = new Message(title, msg, type);
            log.setScrollToMessageEnd(true);
            log.setLog(true);

            Message displayMsg = log;

            if (log.getText().length() > maxLogSize) {

//                String messageHeader = messageHeader(log);
//                
//                int removeLength = Math.max(
//                        log.getText().length() - maxLogSize,
//                        messageHeader.length());

                displayMsg = log.remove(log.getText().length() - maxLogSize);
                displayMsg.setScrollToMessageEnd(true);
                displayMsg.setLog(true);
            }

            addUniqueMessage(displayMsg, null);
        } else {

            int notifications =
                    getMainCanvas().
                    getMessageBoxApplet().getNumberOfNotifications();

            if (notifications == 0) {
                getMainCanvas().getMessageBoxApplet().newNotification();
            }

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    log.append(message);

                    //                    if (messageField.getText().length() > maxLogSize) {
                    //
                    //                        try {
                    //
                    //                            String messageHeader = messageHeader(log);
                    //
                    //                            int removeLength = Math.max(
                    //                                    messageField.getText().length() - maxLogSize,
                    //                                    messageHeader(log).length());
                    //
                    ////                            int value = messageFieldScrollPane.getVerticalScrollBar().getValue();
                    //                            messageField.getDocument().remove(0, removeLength);
                    //                            messageField.setCaretPosition(messageField.getDocument().getLength() - 1);
                    //                            insertHTML(messageField, messageHeader, 0);
                    ////                            messageFieldScrollPane.getVerticalScrollBar().setValue(value);
                    //                            messageField.setCaretPosition(messageField.getDocument().getLength() - 1);
                    ////
                    ////                            messageField.scrollRectToVisible(
                    ////                                    new Rectangle(0, messageField.getHeight() - 2, 1, 1));
                    //
                    //                        } catch (BadLocationException ex) {
                    //                            Logger.getLogger(MessageBox.class.getName()).
                    //                                    log(Level.SEVERE, null, ex);
                    //                        }
                    //                    }
                    //                    insertHTML(messageField, message,
                    //                            messageField.getDocument().getLength());

                    if (messageField.getDocument().getLength() > maxLogSize) {
                        int removeLength = messageField.getDocument().getLength() - maxLogSize;
                        messageFieldScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                        try {
                            messageField.getDocument().remove(0, removeLength);
                        } catch (BadLocationException ex) {
                            Logger.getLogger(MessageBox.class.getName()).
                                    log(Level.SEVERE, null, ex);
                        }
                        messageFieldScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                    }

                    try {
                        messageField.getDocument().insertString(
                                messageField.getDocument().getLength(), message, null);
                    } catch (BadLocationException ex) {
                        Logger.getLogger(MessageBox.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            });
        }

        updateLayout(
                (Integer) getStyle().getBaseValues().get(
                MAX_HEIGHT_KEY));

        messageField.revalidate();

        // sometimes multithreading messes up some values. here we correct that.
        if (isOpened() && getMessageHeight() != getMaxMessageHeight()) {
            setOpening(false);
            setVisible(true);
            setMessageHeight(getMaxMessageHeight());
            updateLayout();
        }

        return log;
    }

    /**
     * Clears the message log.
     */
    public void clearLog() {
        if (log != null) {
            log.setText("");
        }
    }

    /**
     * Indicates whether this message box has a non empty log.
     */
    public boolean hasLog() {
        return log != null && !log.getText().isEmpty();
    }

    /**
     * Shows the log of this message box if the log exists and is not empty.
     */
    public void showLog() {
        if (hasLog()) {
            addUniqueMessage(log, null);
        }
    }

    /**
     * Adds a message to the message box. If an equal message has already been
     * added it will be deleted. Messages are defined as equal if the title,
     * message type and canvas child are equal.
     *
     * @param message the message
     * @param c the canvas child that is to be associated with this message
     * @return the message
     */
    public Message addUniqueMessage(Message message, CanvasChild c) {
        ArrayList<Message> delList = new ArrayList<Message>();

        for (Message i : messages) {
            CanvasChild cObj = i.getPulseIcon().getCanvasChild();

            boolean equalCanvasChild = true;

            if (cObj != null) {
                equalCanvasChild = cObj.equals(c);
            }

            boolean equalMessageType =
                    i.getPulseIcon().getMessageType().equals(
                    message.getType());

            boolean equalTitle =
                    i.getTitle().equals(message.getTitle());

            if (equalCanvasChild && equalMessageType && equalTitle) {
                delList.add(i);
            }
        }

        for (Message i : delList) {
            messages.remove(i);
        }

        return addMessage(message, c);
    }

    public void updateLayout() {
        updateLayout(null);
    }

    /**
     * Updates the message box layout.
     *
     * @param height custom messagebox height (
     * <code>null</code> causes computation of the necessary height)
     */
    public void updateLayout(Integer height) {
        setMinimumSize(null);

        Integer newMessageHeight = null;

        // define maximum MessageBox height as specified in canvas style
        Integer maxHeight =
                (Integer) getStyle().getBaseValues().get(
                MAX_HEIGHT_KEY);

        if (maxHeight == null) {
            maxHeight = 0;
        }

        if (height == null) {

            if (maxHeight == null) {
                maxHeight = 0;
            }

            int visibleWidth = getMainCanvas().getVisibleRect().width;
//        int visibleHeight = getMainCanvas().getVisibleRect().height;

            Dimension maximumSize = new Dimension(visibleWidth,
                    Math.max(maxHeight, 120)); // at least 120 heigh

            setMaximumSize(
                    maximumSize);
            setPreferredSize(
                    null);
//            revalidate();


            int preferredHeight =
                    Math.min(getPreferredSize().height, maximumSize.height);

            newMessageHeight =
                    Math.max(preferredHeight, getMaxMessageHeight());


        } else {
            newMessageHeight = height;

            if (maxHeight == null) {
                maxHeight = height;
            }
        }

        setMaxMessageHeight(
                newMessageHeight);

        messageField.revalidate();

        // -15 to prevent text is hidden under right scrollbar

        int messageFieldWidth =
                messageFieldScrollPane.getSize().width - 15;

        Dimension maxMessageFieldSize =
                new Dimension(messageFieldWidth,
                messageField.getMinimumSize().height);

        messageField.setMaximumSize(maxMessageFieldSize);
        messageField.setPreferredSize(maxMessageFieldSize);


        if (newMessageHeight >= maxHeight) {
            messageFieldScrollPane.setVerticalScrollBarPolicy(
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);


        } else {
            messageFieldScrollPane.setVerticalScrollBarPolicy(
                    JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        }

        if (autoAdjustScrollbar
                && currentMessage != null
                && currentMessage.isScrollToMessageEnd()) {
            // automatically scroll down to the last line
            messageField.scrollRectToVisible(
                    new Rectangle(0, messageField.getHeight() - 2, 1, 1));
        }
    }

    /**
     * Returns the message box height.
     *
     * @return the message box height
     */
    public int getMessageHeight() {
        return messageHeight;
    }

    /**
     * Returns the maximum message box height.
     *
     * @return the maximum message box height
     */
    public int getMaxMessageHeight() {
        return maxMessageHeight;
    }

    /**
     * Defines the maximum height of the message box.
     *
     * @param maxMessageHeight the maximum height to set
     */
    public void setMaxMessageHeight(int maxMessageHeight) {
        this.maxMessageHeight = maxMessageHeight;
    }

    /**
     * Returns the current message, i.e., the message that is currently shown.
     *
     * @return the message that is currently shown
     */
    public Message getCurrentMessage() {
        return currentMessage;
    }

    /**
     * Indicates whether the message box is currently appearing (opening).
     *
     * @return
     * <code>true</code> if the message box is currently appearing;
     * <code>false</code> otherwises
     */
    public boolean isOpening() {
        return opening;
    }

    /**
     * Defines the opening state of the message box.
     *
     * @param opening the opening state of the message box
     */
    protected void setOpening(boolean opening) {
        this.opening = opening;
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    /**
     * Returns the message listener.
     *
     * @return the message listener
     */
    public MessageListener getMessageListener() {
        return messageListener;
    }

    /**
     * Defines the message listener.
     *
     * @param messageListener the message listener to set
     */
    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    /**
     * Returns the number of messages.
     *
     * @return the number of messages
     */
    public int getNumberOfMessages() {
        return numberOfMessages;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        if (isOpened() || isOpening()) {
            showMessage();
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        //
    }

    @Override
    public void componentShown(ComponentEvent e) {
        //
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        //
    }

    /**
     * Removes implicit object references from messages to prevent memory leaks.
     *
     * @param objRep the object representation
     */
    public void removeObjectReferences(DefaultObjectRepresentation objRep) {
        for (Connector c : objRep.getConnectors()) {
            for (Message m : messages) {
                CanvasChild child = m.getPulseIcon().getCanvasChild();

                if (child != null && child.equals(c)) {
                    m.getPulseIcon().removeCanvasChild();
                }
            }
        }
    }

    /**
     * @return the messageStream
     */
    public PrintStream getMessageStream() {
        return messageStream;
    }

    /**
     * @return the errorStream
     */
    public PrintStream getErrorStream() {
        return errorStream;
    }

    /**
     * @return the messageStreamTitle
     */
    public final String getMessageStreamTitle() {
        return messageStreamTitle;
    }

    /**
     * @param messageStreamTitle the messageStreamTitle to set
     */
    public void setMessageStreamTitle(String messageStreamTitle) {
        this.messageStreamTitle = messageStreamTitle;
    }

    /**
     * @return the errorStreamTitle
     */
    public final String getErrorStreamTitle() {
        return errorStreamTitle;
    }

    /**
     * @param errorStreamTitle the errorStreamTitle to set
     */
    public void setErrorStreamTitle(String errorStreamTitle) {
        this.errorStreamTitle = errorStreamTitle;
    }
}

/**
 * Defines the animation used to show messages.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class ShowMessageBoxAnimation extends Animation implements FrameListener {

    private MessageBox messageBox;
    private LinearInterpolation heightValue;

    /**
     * Constructor.
     *
     * @param messageBox the message box
     */
    public ShowMessageBoxAnimation(MessageBox messageBox) {
        addFrameListener(this);
        heightValue = new LinearInterpolation(messageBox.getMessageHeight(),
                messageBox.getMaxMessageHeight());
        getInterpolators().add(heightValue);
        this.messageBox = messageBox;
    }

    @Override
    public void frameStarted(double time) {
        if (getTime() == 0.0) {
            messageBox.setVisible(true);
            messageBox.setOpened(true);
            messageBox.setOpening(true);
        }

        int value = (int) getInterpolators().get(0).getValue();

        messageBox.setMessageHeight(value);

        messageBox.updateLayout();
        heightValue.setStopValue(messageBox.getMaxMessageHeight());
        messageBox.revalidate();

        // causes repaint and layout update
        messageBox.getMainCanvas().getEffectPane().componentResized(null);

        if (getTime() == 1.0) {
            messageBox.setOpening(false);
        }
    }
}

/**
 * Animation used to hide the message box.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class HideMessageBoxAnimation extends Animation implements FrameListener {

    private MessageBox messageBox;

    /**
     * Constructor.
     *
     * @param messageBox the message box
     */
    public HideMessageBoxAnimation(MessageBox messageBox) {
        addFrameListener(this);

        getInterpolators().add(
                new LinearInterpolation(messageBox.getMessageHeight(), 0));
        this.messageBox = messageBox;
    }

    @Override
    public void frameStarted(double time) {

        if (getTime() == 0.0) {

            // update messagebox height because if animation has offset it is
            // likely that it initialized the size wrong
            getInterpolators().get(0).setStartValue(
                    messageBox.getMessageHeight());
            messageBox.setOpened(false);
            messageBox.setOpening(false);
        }

        int value = (int) getInterpolators().get(0).getValue();

        messageBox.setMessageHeight(value);
        messageBox.revalidate();

        // causes repaint and layout update
        messageBox.getMainCanvas().getEffectPane().componentResized(null);

        if (getTime() == 1.0) {
//            messageBox.setOpened(false);
            messageBox.setVisible(false);
            messageBox.setMaxMessageHeight(1);
        }
    }
}
