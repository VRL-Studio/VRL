/* 
 * VisualDropTargetListener.java
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

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.Serializable;

/**
 * Drop target listener for visual canvas.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VisualDropTargetListener implements DropTargetListener,
        Serializable {

    private static final long serialVersionUID = -9213221581632500378L;
    VisualCanvas visualParent;

    /**
     * Constructor.
     * @param visualParent the visual canvas
     */
    public VisualDropTargetListener(VisualCanvas visualParent) {
        this.visualParent = visualParent;
    }

    // the mouse curser enters the target component with an object
    @Override
    public void dragEnter(DropTargetDragEvent e) {
    }

    // the mouse cursor leaves the target component 
    @Override
    public void dragExit(DropTargetEvent e) {
    }

    // the mouse cursor is over the component
    @Override
    public void dragOver(DropTargetDragEvent e) {
    }

    // the object is dropped over the component
    @Override
    public void drop(DropTargetDropEvent e) {
        
        if (visualParent.isIgnoreInput()) {
            return;
        }
        
        try {
            // get the transferable and all provided data flavors 
            Transferable tr = e.getTransferable();
            DataFlavor[] flavors = tr.getTransferDataFlavors();

            // try to find our data flavor
            for (int i = 0; i < flavors.length; i++) {
                if (flavors[i].getMimeType().equals(
                        ObjectTransferable.FLAVOR.getMimeType())) {

                    // accept drag and see if its going to work
                    e.acceptDrop(e.getDropAction());
                    Object object = tr.getTransferData(flavors[i]);

                    if (object instanceof Class<?>) {
                        ComponentUtil.addObject(
                                (Class<?>)object, visualParent,
                                e.getLocation(), true);
                    }
                    
                    // was only used by objecttree
//                    else {
//
//                        // get drop location
//                        Point location = e.getLocation();
//
//                        // modify y value:
//                        // mouse cursor has to be inside of the object's titlebar
//                        // and not on the border
//                        location.y -= 10;
//
//                        // we add the object to our main canvas
//                        VisualObject vObj = visualParent.addObject(
//                                object, location);
//
//                        // evaluate call options
//                        CallOptionsEvaluator evaluator =
//                                new CallOptionsEvaluator(
//                                visualParent.getInspector());
//
//                        evaluator.evaluate(
//                                visualParent.getInspector().
//                                getObjectDescription(object),
//                                vObj.getObjectRepresentation().getID());
//                    }

                    e.dropComplete(true);
                    return;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        // problem occured
        e.rejectDrop();
    }

    // drop type  (Move, Copy, Link) changed
    @Override
    public void dropActionChanged(
            DropTargetDragEvent e) {
    }
}
