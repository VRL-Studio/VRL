/* 
 * CallOptionsEvaluator.java
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

import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.CanvasWindow;
import eu.mihosoft.vrl.visual.MessageBox;
import eu.mihosoft.vrl.visual.MessageType;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Evaluates call options and runs the specified invoke tasks.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class CallOptionsEvaluator {

    private ArrayList<CallOptionTask> options = new ArrayList<CallOptionTask>();
    private VisualObjectInspector inspector;

    /**
     * Constructor.
     * @param inspector the inspector to use for invoking methods
     */
    public CallOptionsEvaluator(final VisualObjectInspector inspector) {
        this.inspector = inspector;
        options.add(new CallOptionTask("assign-canvas",
                new AssignCanvasTask()));

        options.add(new CallOptionTask("autoinvoke",
                new AutoInvokeTask()));

        options.add(new CallOptionTask("assign-window",
                new AssignWindowTask()));

        options.add(new CallOptionTask("assign-visual-id",
                new AssignVisualIDTask()));

    }

    /**
     * Adds a call option task.
     * @param cmd the task
     */
    public void addCallOptionTask(CallOptionTask cmd) {
        getOptions().add(cmd);
    }

    /**
     * Returns the call option task by name.
     * @param cmdName the command name
     * @return the command or <code>null</code> if the requestet option task
     * could not been found
     */
    private CallOptionTask getCallOptionTask(String cmdName) {

        CallOptionTask result = null;

        for (CallOptionTask cmd : options) {
            if (cmd.getName().equals(cmdName)) {
                result = cmd;
            }
        }

        return result;
    }

    /**
     * Evaluates all objects visualized by the specified canvas.
     * @param canvas canvas to evaluate
     */
    public void evaluateAll(VisualCanvas canvas) {
        for (ObjectDescription desc : canvas.getInspector().getObjectDescriptions()) {
            // we invoke the task on the first visualization
            // warning: only tasks that are independent from
            // the visualization will work. example: "assign-canvas"
            //
            // tasks related to visualID or type representation will
            // probably not work
            ArrayList<DefaultObjectRepresentation> oReps =
                    canvas.getInspector().
                    getObjectRepresentations(desc.getID());

            if (!oReps.isEmpty()) {
                evaluate(desc, oReps.get(0).getID());
            }
        }
    }

    /**
     * Evaluates object descriptions and runs the specified invoke tasks.
     * @param oDesc the object description to evaluate
     */
    public void evaluate(ObjectDescription oDesc, int visualID) {
        for (MethodDescription m : oDesc.getMethods()) {
            MethodInfo mInfo = m.getMethodInfo();
//            System.out.println(">> Evaluating call options for: "
//                    + m.getMethodName());
            if (mInfo != null) {

                // split string into call commands
                String callOptions = m.getMethodInfo().callOptions();

                StringTokenizer tokenizer =
                        new StringTokenizer(callOptions, ";");

                // iterate through all found call commands
                while (tokenizer.hasMoreTokens()) {
                    String cmdName = tokenizer.nextToken();

                    CallOptionTask cmd = getCallOptionTask(cmdName);

                    if (cmd != null) {
                        cmd.getTask().invoke(inspector, visualID, m);
                    } else if (cmdName.length() != 0) {
                        MessageBox mBox =
                                inspector.getMainCanvas().getMessageBox();
                        mBox.addMessage("Unsupported Call Option:",
                                "the call option \"<i><u>" + cmdName
                                + "</u></i>\" is " + "not supported and will be"
                                + " ignored!",
                                MessageType.WARNING);
                    }

                } // while (tokenizer.hasMoreTokens())
            } // end if (mInfo != null)
        } // for (MethodDescription m : oDesc.getMethods())
    }

    /**
     * Returns a list containing the available call option tasks.
     * @return a list containing the available call option tasks
     */
    public ArrayList<CallOptionTask> getOptions() {
        return options;
    }
}
