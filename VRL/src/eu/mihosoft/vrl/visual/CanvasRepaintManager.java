/* 
 * CanvasRepaintManager.java
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

import eu.mihosoft.vrl.reflection.DefaultObjectRepresentation;
import eu.mihosoft.vrl.types.VCanvas3D;
import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

/**
 * Custom repaint manager. The problem without this customization was that Swing
 * was not able to correctly repaint the canvas and its components. It is a
 * partial replacement for the default repaint manager class.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class CanvasRepaintManager extends RepaintManager {

    private Canvas mainCanvas;

    public CanvasRepaintManager(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;
    }

    @Override
    public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {

        // first we perform the default task
        super.addDirtyRegion(c, x, y, w, h);

        // additionally we need to update the whole canvas at the specified
        // region because most objects are transparent and that is why also the
        // parent objects have to repaint this region
        if (!(c instanceof EffectPane)) {

            try {
                Point location
                        = mainCanvas.getAbsPos(c, false);

//                super.addDirtyRegion(
//                        mainCanvas, location.x, location.y,
//                        c.getWidth(), c.getHeight());
                Rectangle vRect = c.getVisibleRect();

                int vRectX = location.x + vRect.x;
                int vRectY = location.y + vRect.y;

                int vRectW = vRect.width;
                int vRectH = vRect.height;

                super.addDirtyRegion(
                        mainCanvas, vRectX, vRectY,
                        vRectW, vRectH);

//            System.out.println("REPAINT: c= " + c.getClass()
//                    + " x=" + location.x + " y=" + location.y
//                    + " w=" + w + " h=" + h + " :: timestamp: " + System.nanoTime());
            } catch (Exception ex) {
                // catch nullpointer exceptions etc.
            }

        }

    }

}
