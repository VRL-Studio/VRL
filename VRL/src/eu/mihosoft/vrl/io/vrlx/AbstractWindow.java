/* 
 * AbstractWindow.java
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

import eu.mihosoft.vrl.system.VParamUtil;
import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.WindowContentProvider;
import eu.mihosoft.vrl.visual.CanvasWindow;
import java.awt.Dimension;
import java.awt.Point;

/**
 * Defines a canvas window. Abstract windows
 * are only used for XML serialization.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public final class AbstractWindow {

    private Point location;
    private Integer objID;
    private String title;
    private Dimension titleBarSize;
    private Boolean windowFullSizedState;
    private WindowContentProvider contentProvider;
    private Boolean visible;
    private int zindex;

    /**
     * Constructor.
     */
    public AbstractWindow() {
        //
    }

    /**
     * Constructor.
     * @param obj the canvas object to serialize
     */
    public AbstractWindow(CanvasWindow obj) {
        setObjID(obj.getID());
        setTitle(obj.getTitle());
        setLocation(obj.getLocation());
        setTitleBarSize(obj.getTitleBarSize());
        setWindowFullSizedState(!obj.isMinimized());
        setContentProvider(obj.getContentProvider());

        setVisible(obj.isVisible());
        
        // try-catch is a workaround for issue #1: https://github.com/miho/VRL/issues/1
        // todo: find a better solution (04.06.2013)
        try {        
            setZindex(obj.getMainCanvas().getComponentZOrder(obj));
        } catch (Exception ex) {
            System.err.println(
                    ">> Windows order cannot be fully restored (some windows are missing)");
        }
    }

    /**
     * Returns the window location.
     * @return the location
     */
    public Point getLocation() {
        return location;
    }

    /**
     * Defines the window location.
     * @param location the location to set
     */
    public void setLocation(Point location) {
        this.location = location;
    }

    /**
     * Returns the window id.
     * @return the window id
     */
    public Integer getObjID() {
        return objID;
    }

    /**
     * Defines the window id.
     * @param objID the id to set
     */
    public void setObjID(Integer objID) {
        this.objID = objID;
    }

    /**
     * Adds the window defined by this abstract window to the canvas.
     * @param mainCanvas the canvas this window shall be added to
     */
    public CanvasWindow addToCanvas(final Canvas mainCanvas) {
        
        if (contentProvider==null) {
            System.out.println(
                    ">> serialization error: ContentProvider missing!"
                    + " Window-Name: "
                    + getTitle() + ". Ignoring window.");
            return null;
        }
        
        CanvasWindow window = null;
        
        VParamUtil.throwIfNull(mainCanvas);

        window = contentProvider.newWindow(mainCanvas, this);

        if (!getWindowFullSizedState()) {
            window.minimize();
        }

        if (getTitle() != null && window!=null) {
            window.setTitle(getTitle());
        }
        
        return window;
    }

    /**
     * Indicates whether this window is maximized.
     * @return <code>true</code> if the window is maximized;
     *         <code>false</code> otherwise
     */
    public Boolean getWindowFullSizedState() {
        return windowFullSizedState;
    }

    /**
     * Defines whether this windiw is to be maximized.
     * @param windowFullSizedState the state to set
     */
    public void setWindowFullSizedState(Boolean windowFullSizedState) {
        this.windowFullSizedState = windowFullSizedState;
    }

    /**
     * Returns the size of the window title bar.
     * @return the titleSize the size of the window title bar
     */
    public Dimension getTitleBarSize() {
        return titleBarSize;
    }

    /**
     * Defines the size of the window title bar
     * @param titleBarSize the size to set
     */
    public void setTitleBarSize(Dimension titleBarSize) {
        this.titleBarSize = titleBarSize;
    }

    /**
     * Returns the window title.
     * @return the title the window title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Defines the window title.
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the content provider of this window.
     * @return the contentProvider the content provider of this window
     */
    public WindowContentProvider getContentProvider() {
        return contentProvider;
    }

    /**
     * Defines the content provider of this window.
     * @param contentProvider the content provider to set
     */
    public void setContentProvider(WindowContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    /**
     * Indicates whether this window is visible.
     * @return <code>true</code> if this window is visible;
     *         <code>false</code> otherwise
     */
    public Boolean getVisible() {
        return visible;
    }

    /**
     * Defines if this window shall be visible.
     * @param visible the state to set
     */
    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    /**
     * @return the zindex
     */
    public int getZindex() {
        return zindex;
    }

    /**
     * @param zindex the zindex to set
     */
    public void setZindex(int zindex) {
        this.zindex = zindex;
    }
}
