/* 
 * AutoInvokeTask.java
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

package eu.mihosoft.vrl.reflection;

import eu.mihosoft.vrl.types.CanvasRequest;
import eu.mihosoft.vrl.types.MethodRequest;
import eu.mihosoft.vrl.types.ObjectRequest;
import eu.mihosoft.vrl.types.VisualIDRequest;
import eu.mihosoft.vrl.types.WindowRequest;
import eu.mihosoft.vrl.visual.MessageBox;
import eu.mihosoft.vrl.visual.MessageType;
import java.util.ArrayList;

/**
 * <p>
 * This invoke task can be used to automatically invoke methods when adding an
 * object to the canvas.
 * The following example shows a groovy class with one getter method. This
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
 *     &#64;MethodInfo(callOptions = "autoinvoke")
 *     public Color getColor(){
 *         return Color.green;
 *     }
 *}
 * </pre>
 * </p>
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class AutoInvokeTask implements InvokeTask {

    @Override
    public void invoke(VisualObjectInspector inspector, int visualID,
            MethodDescription mDesc) {

        MessageBox mBox = inspector.getMainCanvas().getMessageBox();

        boolean wrongParameter = false;

        ArrayList<Class<?>> allowedTypes = new ArrayList<Class<?>>();

        allowedTypes.add(CanvasRequest.class);
        allowedTypes.add(ObjectRequest.class);
        allowedTypes.add(MethodRequest.class);
        allowedTypes.add(VisualIDRequest.class);
        allowedTypes.add(WindowRequest.class);

        for(Class<?> t : mDesc.getParameterTypes()) {
            if (!allowedTypes.contains(t)) {
                wrongParameter=true;
                break;
            }
        }

        if (wrongParameter) {
            mBox.addMessage("Can't perform call request:",
                    "the call option <i>\"autoinvoke\"</i>"
                    + " does "
                    + "only" 
                    + " support methods without parameters "
                    + "(exceptions are request parameters such as CanvasRequest)!",
                    MessageType.WARNING);
        } else {
            inspector.invokeFromInvokeButton(mDesc, visualID);
        }
    }
}
