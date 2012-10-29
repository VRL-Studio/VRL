/* 
 * VRLXImport.java
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

package eu.mihosoft.vrl.io.vrlx;

import eu.mihosoft.vrl.dialogs.RecompileClassDialog;
import eu.mihosoft.vrl.lang.InstanceCreator;
import eu.mihosoft.vrl.lang.groovy.GroovyCompiler;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.CanvasWindow;
import java.util.Collection;

/**
 * Format definition for the VRL reflection package.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public final class VRLXImport {

    private static final DynamicFileFormat format;
    public static final String VRLX_REFLECTION =
            VRLXVisual.VRLX_BASE + "/reflection";

    static {

        format = new FileFormatTemplate(
                new DefaultIOModel(new XMLEntryFactory()));

        getVRLXFormat().addTask(
                VRLXVisual.VRLX_VISUAL + "/session-code",
                new ImportSessionTask(TaskType.LOAD));

    }

    // no instanciation allowed
    private VRLXImport() {
        throw new AssertionError(); // not in this class either!
    }

    /**
     * Returns the file format.
     * @return the file format
     */
    public static DynamicFileFormat getVRLXFormat() {
        return format;
    }

    public static class ImportSessionTask implements SessionTask {

        private TaskType type;

        public ImportSessionTask(TaskType type) {
            this.type = type;
        }

        @Override
        public TaskType getType() {
            return type;
        }

        @Override
        public boolean load(Canvas canvas, SessionEntry entry) {
            boolean result = false;
            AbstractSession session = null;

            try {
                session = (AbstractSession) format.getModel().getFileContent(
                        entry,
                        AbstractSession.class);
            } catch (Exception ex) {
                //
            }
            if (session == null || session.getCode() == null
                    || session.getCode().getCode() == null) {
                VRLXSessionController.showErrorMessage(
                        canvas,
                        "Error while importing Session:",
                        ">> possibly session component definition is missing"
                        + " in the specified file!");

                result = false;
            } else {
                VisualCanvas vCanvas =
                        VisualCanvas.asVisualCanvas(canvas);

                final GroovyCompiler compiler =
                        new GroovyCompiler(vCanvas);

                Class<?> checkIfAlreadyExistentClass =
                        compiler.compile(session.getCode().getCode(), null);
                //
                // check whether the previous version of this class is already
                // in use, i.e., if instances of it exist
                @SuppressWarnings("unchecked")
                Collection<Object> objects =
                        vCanvas.getInspector().
                        getObjectsByClassName(
                        checkIfAlreadyExistentClass.getName());

                boolean compile = checkIfAlreadyExistentClass != null;

                // if class already in use ask whether to recompile
                if (objects.size() > 0) {
                    compile =
                            RecompileClassDialog.show(vCanvas,
                            checkIfAlreadyExistentClass.getName());
                }

                if (compile) {

                    session.getCode().setRecompile(true);
                    
                    vCanvas.getCodes().add(session.getCode());
                    Class<?> cls = session.getCode().addToClassPath(vCanvas);

                    // try to replace instances
                    try {
                        vCanvas.getInspector().replaceAllObjects(
                                cls,
                                new InstanceCreator(vCanvas));

                    } catch (Exception ex) {
                        // interface changed or instanciation failed

                        // delete old instances
                        for (Object obj : objects) {
                            for (CanvasWindow vObj :
                                    vCanvas.getInspector().getCanvasWindows(obj)) {
                                vObj.close();
                            }
                        }
                    }

                    result = true;
                }
            }
            return result;
        }

        @Override
        public boolean save(Canvas canvas, SessionEntry entry) {
            VisualCanvas vCanvas = VisualCanvas.asVisualCanvas(canvas);

            SessionEntryFile file = format.getModel().asFile(entry);

            if (file == null) {
                return false;
            }
            


            file.setContent(vCanvas.getSession());

            return true;
        }
    }
}
