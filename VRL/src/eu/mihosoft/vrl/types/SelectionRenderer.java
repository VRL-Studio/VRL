/* 
 * SelectionRenderer.java
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

package eu.mihosoft.vrl.types;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.v3d.AppearanceGenerator;
import eu.mihosoft.vrl.v3d.Node;
import eu.mihosoft.vrl.v3d.Triangle;
import eu.mihosoft.vrl.v3d.VTriangleArray;
import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.ImageUtils;
import eu.mihosoft.vrl.visual.VBoxLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.media.j3d.Shape3D;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.vecmath.Point3f;

/**
 * HIGHLY EXPERIMENTAL! DON'T USE THIS!
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@ComponentInfo(name = "SelectionRenderer", instantiate = false)
public class SelectionRenderer extends JPanel
        implements ListCellRenderer {

    private BufferedImageType imageType;

    public SelectionRenderer(Canvas mainCanvas) {
        setOpaque(false);
        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.LINE_AXIS);

        setLayout(layout);

        imageType = new BufferedImageType();
        imageType.setMainCanvas(mainCanvas);
        imageType.setValueOptions("width=320;height=240");
        imageType.setValueName("");

        add(imageType);

        setImage();
    }

    public void setImage() {
      
        BufferedImage image = ImageUtils.createCompatibleImage(300,300);
        Graphics2D g2 = image.createGraphics();
        
        g2.setColor(Color.green);
        
        float thickness = 5;
        
        g2.setStroke(new BasicStroke(thickness));
        
        int centerX=image.getWidth()/2;
        int centerY=image.getHeight()/2;
        
        int x = (int)(centerX + thickness/2 - 30);
        int y = (int)(centerY + thickness/2 - 30);
        
        int width = (int)(2 * 30-thickness/2 - 1);
        int height = (int)(2 * 30-thickness/2 - 1);
        
        g2.drawOval(x,y,width,height) ;
        g2.dispose();

        imageType.setValue(image);
    }

    /*
     * This method finds the image and text corresponding
     * to the selected value and returns the label, set up
     * to display the text and image.
     */
    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }
}
