/* 
 * UpdateNotifierApplet.java
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
package eu.mihosoft.vrl.visual;

import eu.mihosoft.vrl.reflection.ComponentTree;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;

/**
 * Dock applet used to display VRL messages (notifications).
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class UpdateNotifierApplet extends DockApplet {

    private static final long serialVersionUID = -3844995611877748446L;
    private boolean active;
    private String basePath =
            "/eu/mihosoft/vrl/resources/images/update-icon-01-";
    private static final String INACTIVE_PATH = "inactive.png";
    private static final String ACTIVE_PATH = "active.png";

    /**
     * Constructor.
     *
     * @param messageBox the message box
     */
    public UpdateNotifierApplet(VisualCanvas canvas) {
        super(canvas);

        setIcon(new ImageIcon(
                UpdateNotifierApplet.class.getResource(basePath + INACTIVE_PATH)));

        contentChanged();

        setBorder(new EmptyBorder(0, 5, 10, 5));


//        setActionListener(new CanvasActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                
//            }
//        });


        final JPopupMenu menu = new JPopupMenu("Options");
        JMenuItem item;

        item = new JMenuItem("Search for Updates");

        menu.add(item);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                //
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
    protected ImageIcon generateIcon(Dimension size) {

        String finalPath = basePath + INACTIVE_PATH;

        if (isActive()) {
            finalPath = basePath + ACTIVE_PATH;
        }

        ImageIcon tmpIcon = null;

        if (basePath == null) {
            // TODO this is a hack to prevent nullpointer exceptions
            tmpIcon = new ImageIcon(
                    new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
        } else {
            tmpIcon = new ImageIcon(
                    UpdateNotifierApplet.class.getResource(finalPath));
        }

        BufferedImage resizedImage =
                ImageUtils.convert(tmpIcon.getImage(),
                BufferedImage.TYPE_INT_ARGB, size.width, size.height, true);

        return new ImageIcon(resizedImage);

    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
        
        Dimension d = new Dimension(
                getAppletSize().width- getInsets().left - getInsets().right,
                getAppletSize().height-getInsets().top - getInsets().bottom);

        setIcon(generateIcon(d));
        contentChanged();
        VSwingUtil.repaintRequest(this);
    }
}
