/* 
 * ProjectLibraryDialog.java
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

package eu.mihosoft.vrl.io;

import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.system.VParamUtil;
import eu.mihosoft.vrl.visual.*;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ProjectLibraryDialog {

    private static VDialogWindow dialog = null;
    private static boolean moved = false;
    private static JTextPane htmlCommit = new JTextPane();

    public static void showDialog(VisualCanvas canvas) {
        showDialog(canvas, null);
    }

    public static void showDialog(final VisualCanvas canvas, Point loc) {

        VParamUtil.throwIfNull(canvas);

        if (dialog != null) {
            dialog.close();
            dialog = null;
        }

        moved = false;

        final ProjectLibraryDialog.LibraryManagementPanel componentPanel =
                new ProjectLibraryDialog.LibraryManagementPanel(canvas);


        dialog = VDialog.showDialogWindow(canvas,
                "Manage Libraries",
                new VDialogContent(new ResizableContainer(componentPanel)),
                new String[0]);



        if (loc != null) {
            dialog.setLocation(loc);
        }

        dialog.addCloseIcon();
        dialog.setResizable(false);
        dialog.setMovable(true);
        dialog.setActivatable(true);

        dialog.getStyle().getBaseValues().set(
                CanvasWindow.FADE_IN_DURATION_KEY, 0.0);
        dialog.getStyle().getBaseValues().set(
                CanvasWindow.FADE_OUT_DURATION_KEY, 0.0);

        dialog.setLayoutController(new VLayoutController() {

            @Override
            public void layoutComponent(JComponent c) {
                try {
                    CanvasWindow w = (CanvasWindow) c;

                    Point loc = w.getLocation();

                    if (!moved) {
                        Dimension size = w.getSize();

                        loc.x = (int) (w.getMainCanvas().getVisibleRect().x
                                + w.getMainCanvas().getVisibleRect().
                                getWidth() / 2 - size.width / 2);
                        loc.y = 15 - w.getInsets().top;// - size.height / 2;

                        // check that windows are always inside canvas bounds
                        loc.x = Math.max(loc.x,
                                w.getMainCanvas().getVisibleRect().x);

                        loc.y = Math.max(loc.y,
                                w.getMainCanvas().getVisibleRect().y
                                - w.getInsets().top + 15);

                        loc.x = Math.min(loc.x,
                                w.getMainCanvas().getVisibleRect().x
                                + w.getMainCanvas().getVisibleRect().width
                                - w.getWidth());

                        loc.y = Math.min(loc.y,
                                w.getMainCanvas().getVisibleRect().y
                                + w.getMainCanvas().getVisibleRect().height
                                - w.getHeight());
                    }

                    w.setLocation(loc);
                    w.resetWindowLocation();

                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }
            }
        });

        dialog.addActionListener(new CanvasActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(CanvasWindow.CLOSED_ACTION)) {
                    dialog = null;
                }

                if (e.getActionCommand().equals(CanvasWindow.MOVE_ACTION)) {
                    moved = true;
                }

                if (e.getActionCommand().equals(CanvasWindow.VISIBLE_ACTION)) {
                    VSwingUtil.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            componentPanel.requestFocus();
                        }
                    });
                }
            }
        });
    }

    public static void closeDialog(VisualCanvas canvas) {
        if (dialog != null) {

            dialog.close();
        }
    }

    /**
     * @author Michael Hoffer <info@michaelhoffer.de>
     * @author Christian Poliwoda <christian.poliwoda@gcsc.uni-frankfurt.de>
     */
    static class LibraryManagementPanel extends VComponent {

        private final Object[] versionData;
        private final DefaultListModel resultModel;
        private final JTextArea searchField;
        private JList versionList;

        @SuppressWarnings("unchecked") // we must be compatible with 1.6
        public LibraryManagementPanel(final VisualCanvas canvas) {

            super(canvas);

            setLayout(new GridLayout());

            // TODO @Christian Poliwoda what does manual testing mean?
            // numbers tested manually
            Dimension prefScrollPaneDim = new Dimension(100, 30);
            Dimension visibleRectDim = canvas.getVisibleRect().getSize();


            final ProjectLibraryController controller = 
                    canvas.getProjectController().getLibraryController();

            final int numLibs = controller.getLibs().length;

            versionData = new Object[numLibs];

            String[] versions = new String[0];

//            try {
                versions = controller.getLibs();
//            } catch (IOException ex) {
//                Logger.getLogger(LibraryManagementPanel.class.getName()).
//                        log(Level.SEVERE, null, ex);
//            }

            int maxTextwidth = 0;
            String longestText = null;

            // the history with timestamp and a short commit message
            for (int i = 1; i < versions.length; i++) {
                String text = versions[i];


                // truncate texts that are too long
                int maxTextLength = 100;
                String dots = "...";

                int textLength = text.length() - dots.length();

                if (textLength > maxTextLength) {
                    text = text.substring(0, maxTextLength) + dots;
                }

                versionData[versions.length - i - 1] = versions[i];

                if (text.length() > maxTextwidth) {
                    maxTextwidth = text.length();
                    longestText = text;
                }
            }


            resultModel = new DefaultListModel();

            //first init to show all if search not started yet
            for (int i = 0; i < versionData.length; i++) {
                resultModel.addElement(versionData[i]);
            }

            versionList = new JList(resultModel);

            //set the width of version managment window 
            //dependent on largest git short message length
            double maxFontWidth = versionList.getFontMetrics(
                    versionList.getFont()).
                    getStringBounds(longestText, versionList.getGraphics()).
                    getWidth();

            if (maxFontWidth <= visibleRectDim.width) {

                prefScrollPaneDim.width = (int) maxFontWidth;
            } else {

                if (visibleRectDim.width < 400) {
                    prefScrollPaneDim.width = visibleRectDim.width;
                } else {
                    prefScrollPaneDim.width = 400;
                }

            }


            versionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            versionList.setOpaque(false);

            versionList.setBackground(VSwingUtil.TRANSPARENT_COLOR);
            versionList.setBorder(new EmptyBorder(3, 3, 3, 3));


            Box upperTopBox = Box.createVerticalBox();


            //press the commits to top with VerticalGlue
            //contains search area at top and
            //search results at the botton
            Box upperOuterBox = Box.createVerticalBox();

            JButton searchButton = new JButton("search");
            searchButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    searchAndAddToResultList();
                }
            });

            searchField = new JTextArea();

            // search area box
            Box upperBox1 = Box.createHorizontalBox();

            upperBox1.add(searchField);
            upperBox1.add(searchButton);


            Dimension fieldDim = new Dimension(Short.MAX_VALUE,
                    searchField.getPreferredSize().height);
            searchField.setMaximumSize(fieldDim);


            searchField.addKeyListener(new KeyAdapter() {

                String tmp = "";

                @Override
                public void keyReleased(KeyEvent ke) {

                    searchAndAddToResultList();
                }
            });

//            upperOuterBox.add(upperBox1);
            upperTopBox.add(upperBox1);
            upperTopBox.add(upperOuterBox);

            //result area box
            Box upperBox2 = Box.createHorizontalBox();

            upperBox2.add(Box.createHorizontalGlue());
            upperBox2.add(versionList);
            upperBox2.add(Box.createHorizontalGlue());

            upperOuterBox.add(upperBox2);
            upperOuterBox.add(Box.createVerticalGlue());


            //for optical reasons created (scrollbar right)
            Box upperInnerBorderPane = Box.createHorizontalBox();
            upperInnerBorderPane.add(upperOuterBox);
            upperInnerBorderPane.setBorder(new EmptyBorder(5, 15, 5, 15));
            upperInnerBorderPane.setBackground(VSwingUtil.TRANSPARENT_COLOR);

            VScrollPane upperScrollPane = new VScrollPane(upperInnerBorderPane);
            upperScrollPane.setHorizontalScrollBarPolicy(
                    VScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            upperScrollPane.setVerticalScrollBarPolicy(
                    VScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            upperScrollPane.setMinimumSize(prefScrollPaneDim);


            JSplitPane splitPane = new VSplitPane(JSplitPane.VERTICAL_SPLIT);
            splitPane.setEnabled(true);// true = transparent
            splitPane.setBackground(VSwingUtil.TRANSPARENT_COLOR);
            splitPane.setBorder(new EmptyBorder(5, 5, 5, 5));
            splitPane.setDividerLocation(0.5);

            upperTopBox.add(upperScrollPane);
            splitPane.add(upperTopBox); //add in the upper part


            htmlCommit.setBackground(VSwingUtil.TRANSPARENT_COLOR);
            htmlCommit.setContentType("text/html");
            htmlCommit.setOpaque(false);
            htmlCommit.setEditable(false);
            htmlCommit.setBorder(new EmptyBorder(0, 15, 0, 15));

            Box lowerBox = Box.createVerticalBox();
            lowerBox.setAlignmentX(Component.LEFT_ALIGNMENT);
            lowerBox.add(htmlCommit);
            lowerBox.add(Box.createVerticalGlue());


            VScrollPane lowerScrollPane = new VScrollPane(lowerBox);

            lowerScrollPane.setHorizontalScrollBarPolicy(
                    VScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            lowerScrollPane.setVerticalScrollBarPolicy(
                    VScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            lowerScrollPane.setMinimumSize(new Dimension(0, 0));

            // add in the lower part
            splitPane.setBottomComponent(lowerScrollPane);

            add(splitPane);

            versionList.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {

                    // show commit message in lower part if clicked on a row
                    // in upper part
                    if (e.getClickCount() == 1) {

                        final VersionController controller =
                                canvas.getProjectController().
                                getProject().getProjectFile();

                        final int numVersions =
                                controller.getNumberOfVersions() - 1;


                        ArrayList<RevCommit> versions =
                                new ArrayList<RevCommit>();

                        try {
                            versions = controller.getVersions();
                        } catch (IOException ex) {
                            Logger.getLogger(
                                    VersionManagement.VersionManagementPanel.class.getName()).
                                    log(Level.SEVERE, null, ex);
                        }

                        int versionIndex = ((eu.mihosoft.vrl.io.Version) versionList.getSelectedValue()).getVersion();

                        htmlCommit.setText("<html>"
                                + "<pre> <font color=white><br>"
                                + "<b>SHA-1:</b> "
                                + versions.get(versionIndex).getName()
                                + "<br><br>"
                                + "<b>Message:</b><br><br>"
                                + versions.get(versionIndex).getFullMessage()
                                + "</pre></p>"
                                + "</html>");
                        htmlCommit.setCaretPosition(0);

                    }

                    if (e.getClickCount() == 2
                            && SwingUtilities.isLeftMouseButton(e)) {

                        if (VDialog.showConfirmDialog(canvas,
                                "Checkout Version:",
                                "<html><div align=Center>"
                                + "<p>Do you want to checkout the selected"
                                + "version?<p>"
                                + "<p><b>Unsaved changes will be lost!</b></p>"
                                + "</div></html>",
                                VDialog.DialogType.YES_NO) != VDialog.YES) {
                            return;
                        }

                        try {

                            int versionIndex = ((eu.mihosoft.vrl.io.Version) versionList.getSelectedValue()).getVersion();

                            canvas.setActive(false);

                            String currentSessionName =
                                    canvas.getProjectController().
                                    getCurrentSession();

                            canvas.getProjectController().close(
                                    currentSessionName);

//                            controller.checkoutVersion(versionIndex);

                            if (dialog != null) {
                                dialog.close();
                                dialog = null;
                            }

                            if (canvas.getProjectController().getProject().
                                    getSessionFileByEntryName(
                                    currentSessionName).exists()) {
                                canvas.getProjectController().open(
                                        currentSessionName, false, true);
                            } else {
//                                VDialog.showMessageDialog(canvas,
//                                        "Cannot load \"" 
//                                        + currentSessionName
//                                        +"\":", "<html><div align=Center>"
//                                        + "<p>The Session " 
//                                        + Message.EMPHASIZE_BEGIN
//                                        + currentSessionName
//                                         +  Message.EMPHASIZE_END
//                                        + " does not exist in the current"
//                                        + " version."
//                                        + "<p>The <b>Main</b>-Session will"
//                                        + "be loaded instead</div></html>");
                                canvas.getProjectController().open(
                                        "Main", false, true);
                            }

                        } catch (IOException ex) {
                            Logger.getLogger(
                                    VersionManagement.VersionManagementPanel.class.getName()).
                                    log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });


//            setMinimumSize(visibleRectDim);
            setMaximumSize(visibleRectDim);

            int width = getPreferredSize().width;
            setPreferredSize(new Dimension(width,
                    (int) (visibleRectDim.height * 0.5)));

        }//end constructure

        @Override
        public void contentChanged() {
            super.contentChanged();

            htmlCommit.setForeground(getStyle().
                    getBaseValues().getColor(Canvas.TEXT_COLOR_KEY));

            versionList.setForeground(getStyle().
                    getBaseValues().getColor(Canvas.TEXT_COLOR_KEY));
        }

        /**
         * Search for all elements in the commit list which contains the search
         * words and them to the shown result list.
         *
         */
        @SuppressWarnings("unchecked") // we must be compatible with 1.6
        private void searchAndAddToResultList() {
            String tmp = null;

            resultModel.removeAllElements();

            for (Object element : versionData) {

                if (element instanceof eu.mihosoft.vrl.io.Version) {
                    tmp = element.toString().toLowerCase();

                    if (tmp.contains(searchField.getText().toLowerCase())) {

                        resultModel.addElement(element);
                    }
                }
            }
        }//end searchAndAddToResultList
    }
}

