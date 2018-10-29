/* 
 * AssignCanvasTask.java
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

package eu.mihosoft.vrl.reflection;

import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.MessageBox;
import eu.mihosoft.vrl.visual.MessageType;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * This invoke task can be used to get a reference to the main canvas. The
 * method must have exactly one parameter of type <code>Canvas</code> or
 * <code>VisualCanvas</code>.
 * </p>
 * <p>
 * The following example shows a groovy class with one setter method. This
 * method is called when adding an object of this class to the main canvas.
 * </p>
 * <p>
 * Example:
 * </p>
 * <p>
 * <pre>
 * class Sample implements Serializable {
 *     private static final long serialVersionUID=1;
 *
 *     private transient VisualCanvas mainCanvas;
 *
 *     &#64;MethodInfo(hide = true, callOptions = "assign-canvas")
 *     public void setMainCanvas(Canvas mainCanvas){
 *         this.mainCanvas = mainCanvas;
 *     }
 *}
 * </pre>
 * </p>
 *
 * <p>
 * <b>Note:</b> An alternative to canvas tasks is the usage of
 * @{@link eu.mihosoft.vrl.types.CanvasRequest}. In this case the method can
 * have multiple parameters. Method invocation is not automatic and fully
 * user controlled.
 * </p>
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class AssignCanvasTask implements InvokeTask {

    @Override
    public void invoke(VisualObjectInspector inspector, int visualID,
            MethodDescription mDesc) {
        boolean paramFound = false;

        MessageBox mBox = inspector.getMainCanvas().getMessageBox();

        ArrayList<Object> paramList = new ArrayList<Object>();

        // check for correct parameters
        for (Class c : mDesc.getParameterTypes()) {
            // if paramFound is true we have at lest two
            // parameters of type VisualCanvas which does not meet
            // the demand
            if (paramFound) {
                mBox.addMessage("Can't perform call request:",
                        "the call option <i>\"assign-canvas\"</i>" +
                        " needs " +
                        "only" + " one parameter of type" +
                        " <code>" +
                        "eu.mihosoft.vrl.reflection.VisualCanvas" +
                        "</code>",
                        MessageType.ERROR);
            }

            // we add the maincanvas if we found the correct parameter
            if (c.equals(VisualCanvas.class) ||
                    c.equals(Canvas.class)) {
                paramFound = true;
                paramList.add(inspector.getMainCanvas());
                break;
            } else {
                paramList.add(null);
            }
        }

        if (paramFound) {
            mDesc.setParameters(paramList.toArray());
            try {
                inspector.invoke(mDesc);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(AssignCanvasTask.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        } else {

            mBox.addMessage("Can't perform call request:",
                    "the call option \"assign-canvas\" needs" +
                    " exactly" +
                    " one parameter of type" +
                    " <code>" +
                    "eu.mihosoft.vrl.reflection.VisualCanvas" +
                    "</code>",
                    MessageType.ERROR);
        }
    }
}
