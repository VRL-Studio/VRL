/* 
 * VDialog.java
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

import eu.mihosoft.vrl.system.VParamUtil;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VDialog {

    public static final DialogType YES_NO = DialogType.YES_NO;
    public static final DialogType YES_NO_CANCEL = DialogType.YES_NO_CANCEL;
    public static final DialogType OK = DialogType.OK;
    public static final AnswerType YES = AnswerType.YES;
    public static final AnswerType NO = AnswerType.NO;
    public static final AnswerType CANCEL = AnswerType.CANCEL;
    private static final Stack<VDialogWindow> unconfirmedDialogs =
            new Stack<VDialogWindow>();
    private static boolean cancelDialogs = false;
    
    public static final String ACTION_DIALOG_CREATE = "VDialog:create";
    
    private static final List<ActionListener> dialogActionListeners =
            new ArrayList<>();

    public static boolean showsDialog() {
        return !unconfirmedDialogs.isEmpty();
    }
    
    public static void addDialogActionListener(ActionListener l) {
        dialogActionListeners.add(l);
    }
    
    public static boolean removeDialogActionListener(ActionListener l) {
        return dialogActionListeners.remove(l);
    }

    private static final class DialogPanel extends TransparentPanel {

        public DialogPanel() {

            setBorder(new EmptyBorder(20, 20, 5, 20));

            BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
            setLayout(layout);

            setMinimumSize(new Dimension(300, 100));
        }

        public void init(VDialogWindow dialog,
                DialogAnswerListener answerListener,
                JComponent content, DialogType type) {

            content.setAlignmentX(0.5f);

            add(content);
            add(Box.createGlue());

            add(createButtonPanel(dialog, type, answerListener));
        }

        public void init(VDialogWindow dialog,
                DialogAnswerListener answerListener,
                JComponent content, String[] options) {

            content.setAlignmentX(0.5f);

            add(content);
            add(Box.createGlue());

            add(createButtonPanel(dialog,
                    DialogType.CUSTOM, answerListener, options));
        }
    }
    
    public static synchronized VDialogWindow showDialogWindow(
            Canvas canvas, String title, JComponent component,
            String buttonText, boolean modal) {
        
        notifyActionListeners(ACTION_DIALOG_CREATE);

        VParamUtil.throwIfNull(canvas, title, component, buttonText);

        final VDialogWindow dialog = new VDialogWindow(
                "<html><b>" + title + "</b></html>", canvas);

        DialogPanel dPanel = new DialogPanel();

        DialogContent content =
                new VDialogContent(dPanel);

        dPanel.init(dialog, content, component, OK);

        content.setDialog(dialog);
        dialog.add(content.getContent());

        showDialog(canvas, dialog, modal);
        
        return dialog;
    }

    public static synchronized VDialogWindow showDialogWindow(
            Canvas canvas, String title, DialogContent content,
            String[] options) {
        
        notifyActionListeners(ACTION_DIALOG_CREATE);

        VParamUtil.throwIfNull(canvas, title, content);

        VDialogWindow dialog = new VDialogWindow(
                "<html><b>" + title + "</b></html>", canvas);

        DialogPanel dPanel = new DialogPanel();

        dPanel.init(dialog, content, content.getContent(), options);

        content.setDialog(dialog);

        dialog.add(content.getContent());

        showDialog(canvas, dialog, false);

        return dialog;
    }
    
    private static void notifyActionListeners(String a) {
        for(ActionListener l : dialogActionListeners) {
            l.actionPerformed(new ActionEvent(VDialog.class, 0, a));
        }
    }

    public static synchronized int showConfirmDialog(
            Canvas canvas, String title, JComponent component,
            String... options) {
        return showConfirmDialog(canvas, title, component, null, options);
    }

    public static synchronized int showConfirmDialog(
            Canvas canvas, String title, JComponent component,
            DialogAnswerListener answerListener,
            String... options) {
        
        notifyActionListeners(ACTION_DIALOG_CREATE);

        VParamUtil.throwIfNull(canvas, title, component);

        VDialogWindow dialog = new VDialogWindow(
                "<html><b>" + title + "</b></html>", canvas);

        DialogPanel dPanel = new DialogPanel();

        DialogContent content =
                new VDialogContent(dPanel, answerListener);

        dPanel.init(dialog, content, component, options);

        content.setDialog(dialog);

        dialog.add(content.getContent());

        showDialog(canvas, dialog, true);

        int answer = content.getAnswerIndex();

        return (cancelDialogs) ? Integer.MAX_VALUE : answer;
    }

    public static synchronized int showConfirmDialog(
            Canvas canvas, String title, String message,
            String[] options) {
        
        notifyActionListeners(ACTION_DIALOG_CREATE);

        VParamUtil.throwIfNull(canvas, title, message);

        VDialogWindow dialog = new VDialogWindow(
                "<html><b>" + title + "</b></html>", canvas);

        CanvasLabel messageLabel = new CanvasLabel(dialog, message);

        DialogPanel dPanel = new DialogPanel();

        DialogContent content =
                new VDialogContent(dPanel);

        dPanel.init(dialog, content, messageLabel, options);

        content.setDialog(dialog);

        dialog.add(content.getContent());

        showDialog(canvas, dialog, true);

        int answer = content.getAnswerIndex();

        return (cancelDialogs) ? Integer.MAX_VALUE : answer;
    }

    public static synchronized void showMessageDialog(
            Canvas canvas, String title, String message, MessageType mType,
            boolean modal) {
        
        notifyActionListeners(ACTION_DIALOG_CREATE);

        VParamUtil.throwIfNull(canvas, title, message, mType);

        final VDialogWindow dialog = new VDialogWindow(
                "<html><b>" + title + "</b></html>", canvas);

        CanvasLabel messageLabel = new CanvasLabel(dialog, message);

        DialogPanel dPanel = new DialogPanel();

        DialogContent content =
                new VDialogContent(dPanel);

        dPanel.init(dialog, content, messageLabel, OK);

        content.setDialog(dialog);
        dialog.add(content.getContent());


        showDialog(canvas, dialog, modal);
    }

    public static synchronized void showMessageDialog(
            Canvas canvas, String title, String message) {
        showMessageDialog(canvas, title, message, MessageType.INFO, true);
    }

    public static synchronized AnswerType showConfirmDialog(
            Canvas canvas, String title, String message, DialogType dType) {

        notifyActionListeners(ACTION_DIALOG_CREATE);
        
        VParamUtil.throwIfNull(canvas, title, message, dType);

        if (dType == DialogType.CUSTOM) {
            throw new IllegalArgumentException(
                    "Custom type currently not supported."
                    + " Use showCustomConfirmDialog() instead.");
        }

        final VDialogWindow dialog = new VDialogWindow(
                "<html><b>" + title + "</b></html>", canvas);

        CanvasLabel messageLabel = new CanvasLabel(dialog, message);

        DialogPanel dPanel = new DialogPanel();

        DialogContent content =
                new VDialogContent(dPanel);

        content.setDialog(dialog);

        dPanel.init(dialog, content, messageLabel, dType);
        dialog.add(content.getContent());

        showDialog(canvas, dialog, true);
        
        AnswerType answer = content.getAnswer();

        return (cancelDialogs) ? AnswerType.CANCEL : answer;
    }

    private static synchronized void showDialog(
            final Canvas canvas, final VDialogWindow dialog, boolean modal) {

        notifyActionListeners(ACTION_DIALOG_CREATE);
        
        cancelDialogs = false;

        if (!VSwingUtil.isWindowChild(canvas)) {
            throw new IllegalStateException("Canvas is not child of a window!");
        }


        if (modal) {
            unconfirmedDialogs.add(dialog);
        }

        boolean ignoredInout = canvas.isIgnoreInput();

        canvas.setIgnoreInput(false);

        canvas.addWindow(dialog);

        if (!modal) {
            canvas.setIgnoreInput(ignoredInout);
            return;
        }

        boolean paintedSpot = canvas.getEffectPane().isPaintsSpot();

        if (!paintedSpot) {
            dialog.addActionListener(new CanvasActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getActionCommand().equals(
                            CanvasWindow.CLOSE_ACTION)) {
                        VSwingUtil.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                canvas.getEffectPane().stopSpot();
                            }
                        });
                    }
                }
            });
        }

//        canvas.getEffectPane().startSpot();

        VSwingUtil.deactivateEventFilter();
        VSwingUtil.activateEventFilter(
                canvas, dialog, canvas.getMessageBox(), canvas.getDock());

        VSwingUtil.newWaitController().requestConcurrentWait(
                new ProceedRequest() {

                    @Override
                    public boolean proceed() {
//                        System.out.println(
//                                "Dialog: " + dialog.getTitle()
//                                + "disposable=" + dialog.isDisposed());
                        return dialog.isDisposed()
                                || canvas.isDisposed()
                                || cancelDialogs;
                    }
                });

        VSwingUtil.deactivateEventFilter();

        unconfirmedDialogs.remove(dialog);

        if (!unconfirmedDialogs.isEmpty()) {
            VDialogWindow d = unconfirmedDialogs.lastElement();

            VSwingUtil.deactivateEventFilter();
            VSwingUtil.activateEventFilter(
                    canvas, d, canvas.getMessageBox(), canvas.getDock());

//            canvas.getEffectPane().startSpot();
        }

        canvas.setIgnoreInput(ignoredInout);
    }

    private static synchronized JPanel createButtonPanel(
            final VDialogWindow dialog,
            DialogType type, final DialogAnswerListener answerListener) {
        return createButtonPanel(dialog, type, answerListener, new String[0]);
    }

    /**
     * Cancels all opened dialogs.
     */
    public static void cancelDialogs() {
        cancelDialogs = true;
    }

    private static synchronized JPanel createButtonPanel(
            final VDialogWindow dialog,
            DialogType type, final DialogAnswerListener answerListener,
            String[] options) {

        JPanel result = new TransparentPanel();

        BoxLayout layout = new BoxLayout(result, BoxLayout.LINE_AXIS);

        result.setLayout(layout);

        JComponent inner = new TransparentPanel();//new Box(BoxLayout.LINE_AXIS);
        inner.setLayout(new GridLayout());

        result.add(Box.createGlue());
        result.add(inner);
        result.add(Box.createGlue());

        VButton btn = null;

        switch (type) {
            case OK:
                btn = new VButton("OK");
                inner.add(btn);
                btn.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
//                        dialog.addActionListener(new CanvasActionListener() {
//
//                            @Override
//                            public void actionPerformed(ActionEvent e) {
//                                if (e.getActionCommand().equals(
//                                        CanvasWindow.CLOSE_ACTION)) {
//                                    
//                                }
//                            }
//                        });


                        if (answerListener.answered(YES, 0)) {
                            dialog.close();
                        }
//                        controller.requestProceed();
                    }
                });

//                btn.setSelected(true);
//                btn.requestFocus(true);

                result.setMaximumSize(
                        new Dimension(Short.MAX_VALUE, btn.getHeight()));

                break;

            case YES_NO:
                btn = new VButton("Yes");
                inner.add(btn);
                btn.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

//                        dialog.addActionListener(new CanvasActionListener() {
//
//                            @Override
//                            public void actionPerformed(ActionEvent e) {
//                                if (e.getActionCommand().equals(
//                                        CanvasWindow.CLOSE_ACTION)) {
//                                    answerListener.answered(YES);
//                                    controller.requestProceed();
//                                }
//                            }
//                        });

                        System.out.println("Dialog:" + dialog.getTitle() + ": YES");

                        if (answerListener.answered(YES, 0)) {
                            dialog.close();
                        }
//                        controller.requestProceed();
                    }
                });

                btn = new VButton("No");
                inner.add(btn);
                btn.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {


                        System.out.println("Dialog:" + dialog.getTitle() + ": NO");

                        if (answerListener.answered(NO, 1)) {
                            dialog.close();
                        }
//                        controller.requestProceed();

                    }
                });

//                btn.setSelected(true);
//                btn.requestFocus(true);

                result.setMaximumSize(
                        new Dimension(Short.MAX_VALUE, btn.getHeight()));

                break;

            case YES_NO_CANCEL:
                btn = new VButton("Yes");
                inner.add(btn);
                btn.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {


//                        dialog.addActionListener(new CanvasActionListener() {
//
//                            @Override
//                            public void actionPerformed(ActionEvent e) {
//                                if (e.getActionCommand().equals(
//                                        CanvasWindow.CLOSE_ACTION)) {
//                                    answerListener.answered(YES);
//                                    controller.requestProceed();
//                                }
//                            }
//                        });

                        if (answerListener.answered(YES, 0)) {
                            dialog.close();
                        }
//                        controller.requestProceed();
                    }
                });

                btn = new VButton("No");
                inner.add(btn);
                btn.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

//                        dialog.addActionListener(new CanvasActionListener() {
//
//                            @Override
//                            public void actionPerformed(ActionEvent e) {
//                                if (e.getActionCommand().equals(
//                                        CanvasWindow.CLOSE_ACTION)) {
//                                    answerListener.answered(NO, 1);
////                                    controller.requestProceed();
//                                }
//                            }
//                        });
                        // dialog.close();

                        if (answerListener.answered(NO, 1)) {
                            dialog.close();
                        }

                    }
                });

//                btn.setSelected(true);
//                btn.requestFocus(true);

                btn = new VButton("Cancel");
                inner.add(btn);
                btn.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
////                        dialog.addActionListener(new CanvasActionListener() {
////
////                            @Override
////                            public void actionPerformed(ActionEvent e) {
////                                if (e.getActionCommand().equals(
////                                        CanvasWindow.CLOSE_ACTION)) {
////                                    answerListener.answered(CANCEL);
////                                    controller.requestProceed();
////                                }
////                            }
////                        });

//                        dialog.close();
//                        answerListener.answered(CANCEL, 2);
////                        controller.requestProceed();

                        if (answerListener.answered(CANCEL, 2)) {
                            dialog.close();
                        }
                    }
                });

                result.setMaximumSize(
                        new Dimension(Short.MAX_VALUE, btn.getHeight()));

                break;


            case CUSTOM:
                VParamUtil.throwIfNull((Object) options);

                for (int i = 0; i < options.length; i++) {
                    final int index = i;
                    btn = new VButton(options[i]);
                    inner.add(btn);
                    btn.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {

                            if (answerListener.answered(
                                    AnswerType.CUSTOM, index)) {
                                dialog.close();
                            }
                        }
                    });

                    if (i == 0) {
                        result.setMaximumSize(
                                new Dimension(Short.MAX_VALUE,
                                btn.getHeight()));
                    }
                }

                break;
        }

        result.setBorder(new EmptyBorder(20, 0, 0, 0));

        return result;
    }

    public static enum DialogType {

        YES_NO,
        YES_NO_CANCEL,
        OK,
        CUSTOM
    }

    public static enum AnswerType {

        YES,
        NO,
        CANCEL,
        CUSTOM
    }
}
