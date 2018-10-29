/* 
 * LoadCanvasXMLSessionDialog.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007–2018 by Michael Hoffer,
 * Copyright (c) 2015–2018 G-CSC, Uni Frankfurt,
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn)
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

package eu.mihosoft.vrl.dialogs;

import eu.mihosoft.vrl.io.CanvasXMLSessionFilter;
import eu.mihosoft.vrl.io.CanvasXMLSessionLoader;
import eu.mihosoft.vrl.io.LoadSessionListener;
import eu.mihosoft.vrl.reflection.TypeRepresentation;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.visual.Canvas;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;

/**
 * A dialog for loading XML canvas sessions. This is the recommended way
 * to load VRL sessions. It is well supported and replaces the binary session
 * management.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class LoadCanvasXMLSessionDialog {

    /**
     * Opens a load file dialog and loads a XML canvas session which
     * is returned as visual canvas object.
     * @param parent the parent component of the dialog
     * @param mainCanvas the canvas that was in use before loading
     * @return the loaded session as visual canvas object or
     *         <code>null</code> if the session couldn't be loaded
     */
//    public static Object showDialog(VisualCanvas parent) {
//        return showDialog(parent,
//                new ArrayList<LoadSessionListener>(),
//                new ArrayList<LoadSessionListener>(), null);
//    }

//    /**
//     * Opens a load file dialog and loads a XML canvas session which
//     * is returned as visual canvas object.
//     * @param parent the parent component of the dialog
//     * @param mainCanvas the canvas that was in use before loading
//     * @param prelisteners pre load session listeners
//     * @param postlisteners post load session listeners
//     * @param directory the current directory to use
//     * @return the loaded session as visual canvas object or
//     *         <code>null</code> if the session couldn't be loaded
//     */
//    public static Object showDialog(Component parent, VisualCanvas mainCanvas,
//            ArrayList<LoadSessionListener> prelisteners,
//            ArrayList<LoadSessionListener> postlisteners, File directory) {
//        FileDialogManager dialogManager = new FileDialogManager();
//        CanvasXMLSessionLoader sessionLoader = new CanvasXMLSessionLoader();
//
//        sessionLoader.getPreLoadListeners().addAll(prelisteners);
//        sessionLoader.getPostLoadListeners().addAll(postlisteners);
//
//        sessionLoader.setStyle(mainCanvas.getStyle());
//
//
//        return dialogManager.loadFile(parent, sessionLoader,
//                new CanvasXMLSessionFilter());
//    }
    /**
     * Opens a load file dialog and loads a XML canvas session which
     * is returned as visual canvas object.
     * @param parent the parent component of the dialog
     * @param parent the canvas that was in use before loading
     * @param prelisteners pre load session listeners
     * @param postlisteners post load session listeners
     * @param directory the current directory to use
     * @return the loaded session as visual canvas object or
     *         <code>null</code> if the session couldn't be loaded
     */
//    public static Object showDialog(VisualCanvas parent,
//            ArrayList<LoadSessionListener> prelisteners,
//            ArrayList<LoadSessionListener> postlisteners, File directory) {
//        FileDialogManager dialogManager = new FileDialogManager();
//        CanvasXMLSessionLoader sessionLoader = new CanvasXMLSessionLoader();
//
//        if (prelisteners != null) {
//            sessionLoader.getPreLoadListeners().addAll(prelisteners);
//        }
//        
//        if (postlisteners != null) {
//            sessionLoader.getPostLoadListeners().addAll(postlisteners);
//        }
//
//        sessionLoader.setStyle(parent.getStyle());
//
//
//        return dialogManager.loadFile(parent, sessionLoader, directory,
//                new CanvasXMLSessionFilter());
//    }
//    /**
//     * Opens a load file dialog and loads a XML canvas session which
//     * is returned as visual canvas object.
//     * @param parent the parent component of the dialog
//     * @param mainCanvas the canvas that was in use before loading
//     * @param typeRepresentations a list containing all type representations
//     *                            that are to be added to the loaded session
//     * @return the loaded session as visual canvas object or
//     *         <code>null</code> if the session couldn't be loaded
//     */
//    public static Object showDialogWithTypes(Component parent,
//            VisualCanvas mainCanvas,
//            final ArrayList<TypeRepresentationBase> typeRepresentations) {
//
//        CanvasXMLSessionLoader sessionLoader = new CanvasXMLSessionLoader();
//
//        if (mainCanvas != null) {
//            sessionLoader.setStyle(mainCanvas.getStyle());
//        }
//
//        if (typeRepresentations != null) {
//            sessionLoader.getPreLoadListeners().add(new LoadSessionListener() {
//
//                @Override
//                public void loadEvent(VisualCanvas canvas) {
//                    for (TypeRepresentationBase t : typeRepresentations) {
//                        canvas.getTypeFactory().addType(t);
//                    }
//                }
//            });
//        }
//
//        FileDialogManager dialogManager = new FileDialogManager();
//
//        return dialogManager.loadFile(parent, sessionLoader,
//                new CanvasXMLSessionFilter());
//    }
}
