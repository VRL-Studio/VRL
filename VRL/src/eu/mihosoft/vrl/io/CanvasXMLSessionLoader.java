/* 
 * CanvasXMLSessionLoader.java
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

package eu.mihosoft.vrl.io;

import eu.mihosoft.vrl.animation.AnimationTask;
import eu.mihosoft.vrl.io.vrlx.VRLXSessionController;
import eu.mihosoft.vrl.io.vrlx.VRLXReflection;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.visual.Style;
import eu.mihosoft.vrl.visual.Task;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * XML session loader. This is the recommended way to load VRL sessions. It
 * is well supported and replaces the binary session management.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class CanvasXMLSessionLoader  {

    /**
     * load event listeners
     */
    private ArrayList<LoadSessionListener> preLoadListeners =
            new ArrayList<LoadSessionListener>();
    private ArrayList<LoadSessionListener> postLoadListeners =
            new ArrayList<LoadSessionListener>();
    private FileNotFoundException fileException;
    private Style style;

//    @Override
//    public Object loadFile(final File file) throws FileNotFoundException {
//
//        final VisualCanvas canvas = new VisualCanvas();
//
//        canvas.setStyle(style);
//
//        for (LoadSessionListener l : preLoadListeners) {
//            l.loadEvent(canvas);
//        }
//
////        canvas.getEffectPane().startSpin();
////
////        // read from disk using FileInputStream
//////        FileInputStream fIn = null;
//////        fIn = new FileInputStream(file);
//////
//////        canvas.loadSession(fIn);
////
////        canvas.getEffectPane().stopSpin();
//
//
//        canvas.getEffectPane().colorize(new Color(0, 0, 0, 120), 0, 0.3,
//                //          canvas.getEffectPane().startSpin(0.3,
//                new AnimationTask() {
//
//            @Override
//            public void firstFrameStarted() {
//                //
//            }
//
//            @Override
//            public void frameStarted(double time) {
//                //
//            }
//
//            @Override
//            public void lastFrameStarted() {
//
//                SwingUtilities.invokeLater(
//                        new Runnable() {
//
//                            @Override
//                            public void run() {
//                                // read from disk
//                                try {
//                                    VRLXSessionController sessionController =
//                                            new VRLXSessionController(
//                                            VRLXReflection.getVRLXFormat());
//                                    sessionController.loadSession(canvas,file);
//                                    for (LoadSessionListener l : postLoadListeners) {
//                                        l.loadEvent(canvas);
//                                    }
//
//                                } catch (FileNotFoundException ex) {
//                                    fileException = ex;
//                                }
//                            }
//                        });
//
//                SwingUtilities.invokeLater(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        canvas.getEffectPane().
//                                colorize(VSwingUtil.TRANSPARENT_COLOR,
//                                1, 0.3, null);
////                                  canvas.getEffectPane().stopSpin();
//                    }
//                });
//            }
//        });
//
//        if (fileException != null) {
//            throw fileException;
//        }
//        return canvas;
//    }

    /**
     * Returns a list containing all pre <code>LoadSessionListener</code> objects
     * that are associated with this session loader.
     * @return a list containing all <code>LoadSessionListener</code> objects
     *         that are associated with this session loader
     */
    public ArrayList<LoadSessionListener> getPreLoadListeners() {
        return preLoadListeners;
    }

    /**
     * Returns a list containing all post <code>LoadSessionListener</code> objects
     * that are associated with this session loader.
     * @return a list containing all <code>LoadSessionListener</code> objects
     *         that are associated with this session loader
     */
    public ArrayList<LoadSessionListener> getPostLoadListeners() {
        return postLoadListeners;
    }

    /**
     * @param style the style to set
     */
    public void setStyle(Style style) {
        this.style = style;
    }
}
