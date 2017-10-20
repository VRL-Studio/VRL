/* 
 * VRLXVisual.java
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

package eu.mihosoft.vrl.io.vrlx;

import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.CanvasWindow;
import eu.mihosoft.vrl.visual.Style;
import eu.mihosoft.vrl.visual.VDialogWindow;
import eu.mihosoft.vrl.visual.WindowGroups;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Format definition for the VRL visual package.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public final class VRLXVisual {

    private static final DynamicFileFormat format;
    public static final String VRLX_BASE = "vrlx/base";
    public static final String VRLX_VISUAL = VRLX_BASE + "/visual";

    static {
        format = new FileFormatTemplate(
                new DefaultIOModel(new XMLEntryFactory()));
        getVRLXFormat().addTask(VRLX_VISUAL + "/session-initializer",
                new SessionInitializerTask());
        getVRLXFormat().addTask(VRLX_VISUAL + "/style",
                new StyleTask());
        getVRLXFormat().addTask(VRLX_VISUAL + "/windows",
                new WindowTask());
        getVRLXFormat().addTask(VRLX_VISUAL + "/window-groups",
                new WindowGroupTask());
    }

    // no instanciation allowed
    private VRLXVisual() {
        throw new AssertionError(); // not in this class either!
    }

    /**
     * Returns the file format.
     * @return the file format
     */
    public static DynamicFileFormat getVRLXFormat() {
        return format;
    }

    public static class SessionInitializerTask implements SessionTask {

        @Override
        public TaskType getType() {
            return TaskType.LOAD_AND_SAVE;
        }

        @Override
        public boolean load(Canvas canvas, SessionEntry entry) {
            boolean result = false;
            SessionInitializer sInit = null;
            sInit = (SessionInitializer) format.getModel().getFileContent(
                    entry,
                    SessionInitializer.class);
            if (sInit == null) {
                VRLXSessionController.showErrorMessage(
                        canvas,
                        "Error while loading Session Initializer:",
                        ">> cannot find a valid session initializer in the"
                        + " session file! ");
            } else {
                canvas.setSessionInitializer(sInit);
                try {
                    canvas.getSessionInitializers().preInit(canvas);
                } catch (Exception ex) {
                    Logger.getLogger(VRLXVisual.class.getName()).
                            log(Level.SEVERE, null, ex);

                    VRLXSessionController.showErrorMessage(
                            canvas,
                            "Error while loading Session Initializer:", ex);

                    result = false;
                }
                result = true;
            }
            return result;
        }

        @Override
        public boolean save(Canvas canvas, SessionEntry entry) {

            SessionEntryFile file =
                    format.getModel().asFile(entry);

            if (file == null) {
                return false;
            }

            file.setContent(canvas.getSessionInitializer());

            return true;
        }
    }

    public static class StyleTask implements SessionTask {

        @Override
        public TaskType getType() {
            return TaskType.LOAD_AND_SAVE;
        }

        @Override
        public boolean load(Canvas canvas, SessionEntry entry) {
            boolean result = false;
            Style style = null;
            style = (Style) format.getModel().getFileContent(
                    entry,
                    Style.class);
            if (style == null) {
                VRLXSessionController.showErrorMessage(
                        canvas, null,
                        "Error while loading Style:");
            } else {
                canvas.setStyle(style);
                result = true;
            }
            return result;
        }

        @Override
        public boolean save(Canvas canvas, SessionEntry entry) {
            SessionEntryFile file =
                    format.getModel().asFile(entry);

            if (file == null) {
                return false;
            }

            file.setContent(canvas.getStyle());

            return true;
        }
    }

    public static class WindowTask implements SessionTask {

        @Override
        public TaskType getType() {
            return TaskType.LOAD_AND_SAVE;
        }

        @Override
        public boolean load(Canvas canvas, SessionEntry entry) {
            boolean result = false;
            AbstractWindows objects = null;
            objects = (AbstractWindows) format.getModel().getFileContent(
                    entry,
                    AbstractWindows.class);
            if (objects == null) {
                VRLXSessionController.showErrorMessage(
                        canvas, null,
                        "Error while loading Windows:");
            } else {
                objects.addToCanvas(canvas);
                result = true;
            }
            return result;
        }

        @Override
        public boolean save(Canvas canvas, SessionEntry entry) {

            SessionEntryFile file =
                    format.getModel().asFile(entry);

            if (file == null) {
                return false;
            }

            AbstractWindows objects = new AbstractWindows();

            // why did we need this sorting?
//            Collections.sort(canvas.getWindows());

            for (CanvasWindow o : canvas.getWindows()) {

                // we ignore dialog windows
                if (!(o instanceof VDialogWindow)) {
                    objects.add(new AbstractWindow(o));
                }
            }

            file.setContent(objects);

            return true;
        }
    }

    public static class WindowGroupTask implements SessionTask {

        @Override
        public TaskType getType() {
            return TaskType.LOAD_AND_SAVE;
        }

        @Override
        public boolean load(Canvas canvas, SessionEntry entry) {
            boolean result = false;
            WindowGroups windowGroups = null;
            windowGroups = (WindowGroups) format.getModel().getFileContent(
                    entry,
                    WindowGroups.class);
            if (windowGroups == null) {
                VRLXSessionController.showErrorMessage(
                        canvas, null,
                        "Error while loading Window Groups:");
            } else {

                canvas.setWindowGroups(windowGroups);
                canvas.getWindowGroups().setMainCanvas(canvas);

                result = true;
            }
            return result;
        }

        @Override
        public boolean save(Canvas canvas, SessionEntry entry) {

            SessionEntryFile file =
                    format.getModel().asFile(entry);

            if (file == null) {
                return false;
            }

            file.setContent(canvas.getWindowGroups());

            return true;
        }
    }
}
