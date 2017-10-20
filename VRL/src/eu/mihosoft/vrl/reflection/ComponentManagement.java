/* 
 * ComponentManagement.java
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

package eu.mihosoft.vrl.reflection;

import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.CanvasActionListener;
import eu.mihosoft.vrl.visual.CanvasWindow;
import eu.mihosoft.vrl.visual.ResizableContainer;
import eu.mihosoft.vrl.visual.VComponent;
import eu.mihosoft.vrl.visual.VDialog;
import eu.mihosoft.vrl.visual.VDialogContent;
import eu.mihosoft.vrl.visual.VDialogWindow;
import eu.mihosoft.vrl.visual.VLayoutController;
import eu.mihosoft.vrl.visual.VSwingUtil;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ComponentManagement {

    private static VDialogWindow dialog;
    private static ComponentManagementPanel componentPanel;
    private static boolean moved;

    private static void showSearchDialog(VisualCanvas canvas) {

        // if canvas is inactive don't show dialog
        if (!canvas.isActive()) {
            return;
        }

        if (dialog != null) {
            return;
        }

        moved = false;

        componentPanel =
                new ComponentManagementPanel(canvas);

        ResizableContainer resizableContainer =
                new ResizableContainer(componentPanel);

        dialog = VDialog.showDialogWindow(canvas,
                "Manage Components",
                new VDialogContent(resizableContainer), new String[0]);

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
                            dialog.getMainCanvas().getVisibleRect().x);

                    loc.y = Math.max(loc.y,
                            dialog.getMainCanvas().getVisibleRect().y
                            - w.getInsets().top + 15);

                    loc.x = Math.min(loc.x,
                            dialog.getMainCanvas().getVisibleRect().x
                            + dialog.getMainCanvas().getVisibleRect().width
                            - dialog.getWidth());

                    loc.y = Math.min(loc.y,
                            dialog.getMainCanvas().getVisibleRect().y
                            + dialog.getMainCanvas().getVisibleRect().height
                            - dialog.getHeight());
                }

                w.setLocation(loc);
                w.resetWindowLocation();
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
                            componentPanel.executeFilter();
                            componentPanel.requestFocus();
                        }
                    });
                }
            }
        });
    }

    /**
     * Updates the content in the component panel by executing the current 
     * filter. This method should be called whenever components have been
     * added and a filter is currently used, e.g., if the user typed
     * something in the search field.
     */
    public static void updateContent() {
        if (componentPanel != null) {
            componentPanel.executeFilter();
        }
    }

    public static void toggleSearchDialog(VisualCanvas canvas) {

        // if canvas is inactive don't show dialog
        if (!canvas.isActive()) {
            return;
        }

        if (dialog != null && dialog.isVisible()) {


            if (dialog.getMainCanvas() != canvas) {
                dialog = null;
                showSearchDialog(canvas);
            } else {
                dialog.hideWindow();
//                System.out.println("ShowDialog: hide");
            }

        } else {

            if (dialog == null || dialog.getMainCanvas() != canvas) {
                dialog = null;
                componentPanel = null;
                showSearchDialog(canvas);
//                System.out.println("ShowDialog: new ");
            } else {

//                System.out.println("ShowDialog: reuse");

                dialog.showWindow();

                Point loc = dialog.getLocation();

                loc.x = Math.max(loc.x,
                        dialog.getMainCanvas().getVisibleRect().x);

                loc.y = Math.max(loc.y,
                        dialog.getMainCanvas().getVisibleRect().y);

                loc.x = Math.min(loc.x,
                        dialog.getMainCanvas().getVisibleRect().x
                        + dialog.getMainCanvas().getVisibleRect().width
                        - dialog.getWidth());

                loc.y = Math.min(loc.y,
                        dialog.getMainCanvas().getVisibleRect().y
                        + dialog.getMainCanvas().getVisibleRect().height
                        - dialog.getHeight());

                dialog.setLocation(loc);
            }
        }
    }
}
