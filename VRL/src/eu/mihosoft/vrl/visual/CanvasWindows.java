/* 
 * CanvasWindows.java
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

package eu.mihosoft.vrl.visual;

import eu.mihosoft.vrl.animation.FrameListener;
import eu.mihosoft.vrl.effects.EffectPainter;
import eu.mihosoft.vrl.effects.FadeEffect;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * Represents a list of all CanvasObject objects inside a Canvas object.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class CanvasWindows extends IDArrayList<CanvasWindow>
        implements CanvasChild, Disposable {

    private static final long serialVersionUID = -387940247347678451L;
    /**
     * The Canvas object the Connections object
     * belongs to.
     */
    private Canvas mainCanvas;
    /**
     * The Connections object containing data connections of all child objects.
     */
    private Connections dataConnections;
    /**
     * The Connections object containing control connections of all child objects.
     */
    private Connections controlFlowConnections;

    /**
     * Creates an empty CanvasWindows object.
     * @param mainCanvas the Canvas object the CanvasObject object
     * belongs to
     */
    public CanvasWindows(Canvas mainCanvas) {
        setMainCanvas(mainCanvas);
        dataConnections = mainCanvas.getDataConnections();
        controlFlowConnections = mainCanvas.getControlFlowConnections();
        getMainCanvas().getStyleManager().addStyleChangedListener(
                new StyleChangedListener() {

                    @Override
                    public void styleChanged(Style style) {
                        redrawRequest();
                    }
                });
    }

    /**
     * Adds a window to the canvas and initiates appearance effects.
     * @param obj the window that is to be added
     */
    private void initAddingEffects(CanvasWindow obj) {
        mainCanvas.add(obj);
        mainCanvas.validate();

        if (obj instanceof EffectPainter) {
            FadeEffect effect = (FadeEffect) obj.getEffectManager().
                    getEffectByName("FadeEffect");

            effect.setFadeIn(true);

            // we have to remove all animation tasks related to object deletion

            ArrayList<FrameListener> delList = new ArrayList<FrameListener>();

            for (FrameListener task :
                    effect.getAnimation().getFrameListeners()) {
                if (task instanceof RemoveObjectTask) {
                    delList.add(task);
                }
            }

            for (FrameListener f : delList) {
                effect.getAnimation().getFrameListeners().remove(f);
            }

            obj.getEffectManager().startEffect("FadeEffect", obj.getStyle().
                    getBaseValues().getDouble(
                    CanvasWindow.FADE_IN_DURATION_KEY));

        }
    }

    /**
     * Adds CanvasObject object to the list.
     * @param obj the object that is to be added
     * @return <code>true</code> (as specified by Collection.add(E))
     */
    @Override
    public boolean add(CanvasWindow obj) {
        
        if (obj.isDisposed()) {
            throw new IllegalStateException(
                    "Cannot add Window that has been disposed()!");
        }

        initAddingEffects(obj);
        boolean result = super.add(obj);
        setActive(obj);
        obj.added();

        return result;
    }

    /**
     * Adds CanvasObject object to the list.
     * @param obj the object that is to be added
     * @param ID the ID of the object (only used when loading from file,
     *                                 because then we need to restore the ID)
     * @return <code>true</code> (as specified by {@link IDArrayList#addWidthID})
     */
    @Override
    public boolean addWithID(CanvasWindow obj, int ID) {
        boolean result;
        initAddingEffects(obj);
        result = super.addWithID(obj, ID);
        setActive(obj);
        return result;
    }

    /**
     * Removes object from list.
     * @param ID the ID value of the object that is to be removed,
     *           valid range: [0,MAX_INT]
     */
    public void removeObject(final int ID) {
        CanvasWindow o = getObject(ID);

        if (o instanceof EffectPainter && !getMainCanvas().isDisableEffects()) {

            FadeEffect effect = (FadeEffect) o.getEffectManager().
                    getEffectByName("FadeEffect");

            effect.setFadeIn(false);
            effect.getAnimation().addFrameListener(
                    new RemoveObjectTask(mainCanvas, ID));

            o.getEffectManager().startEffect("FadeEffect",
                    o.getStyle().getBaseValues().getDouble(
                    CanvasWindow.FADE_OUT_DURATION_KEY));
        } else {
            removeObjectWithoutEffect(ID);
        }
    }

    /**
     * Removes a canvas object without animation effects.
     * @param ID the id of the object is to be removed
     */
    public void removeObjectWithoutEffect(int ID) {
        CanvasWindow o = getObject(ID);
        if (o != null) {
            dataConnections.removeAllWith(o);
            controlFlowConnections.removeAllWith(o);

            o.setVisible(false);
            
            getMainCanvas().remove(o);
            
            o.windowRemoved();
            o.dispose();
            this.remove(o);
            System.out.println(">> Window " + ID + " deleted!");
            o.fireAction(new ActionEvent(o, 0, CanvasWindow.CLOSED_ACTION));
        }
    }

    /**
     * Removes  object from list.
     * @param o the object that is to be removed
     */
    public void removeObject(CanvasWindow o) {
        removeObject(o.getID());
    }

    /**
     * Removes all objects from list.
     */
    public void removeAll() {
        ArrayList<Integer> delList = new ArrayList<Integer>();

        for (CanvasWindow o : this) {
            delList.add(o.getID());
        }

        for (Integer i : delList) {
            removeObject(i);
        }
    }

    /**
     * Removes all objects from list.
     */
    public void removeAllWithoutEffect() {
        ArrayList<Integer> delList = new ArrayList<Integer>();

        for (CanvasWindow o : this) {
            delList.add(o.getID());
        }

        for (Integer i : delList) {
            removeObjectWithoutEffect(i);
        }
    }

    /**
     * Returns object.
     * @param ID the ID value of the object that is to be returned,
     *           valid range: [0,MAX_INT]
     * @return the object that is to be returned
     */
    public CanvasWindow getObject(int ID) {
        return getById(ID);
    }

    @Override
    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    @Override
    public void setMainCanvas(Canvas mainCanvas) {
        assert mainCanvas != null : "";
        this.mainCanvas = mainCanvas;
    }

    /**
     * Returns the connections that are connected to type representations inside
     * all canvas window in this list.
     * @return the connections that are connected to type representations inside
     * all canvas window in this list
     */
    public Connections getDataConnections() {
        return dataConnections;
    }

    /**
     * Returns the connections that are connected to method representations inside
     * all canvas window in this list.
     * @return the connections that are connected to method representations inside
     * all canvas window in this list
     */
    public Connections getControlFlowConnections() {
        return controlFlowConnections;
    }

    /**
     * Defines a window as active. This method brings the window to front. The
     * window usually indicates its activity by changing its title color.
     * @param o the window that is to be defined as active
     */
    public void setActive(CanvasWindow o) {
        // we make sure the component is drawn on top of all others
        //
        // this behavior is similar to window management in most OS
        // where the window that has last been moved comes to front.
        mainCanvas.setObjectZOrder(o, 0);
        for (CanvasWindow i : this) {
            i.setActive(false);
        }
        o.setActive(true);
    }

    /**
     * Defines all windows as inactive.
     */
    public void setAllInactive() {
        for (CanvasWindow w : this) {
            w.setActive(false);
        }
    }

    /**
     * Sends an redraw request. All windows will be notified by calling their
     * <code>contentChanged()</code> method.
     */
    public void redrawRequest() {
        for (CanvasWindow i : this) {
            i.contentChanged();
        }
    }

    /**
     * Hides all windows.
     */
    public void hideWindows() {
        for (CanvasWindow w : this) {
            w.hideWindow();
        }
    }

    /**
     * Shows all windows.
     */
    public void showWindows() {
        for (CanvasWindow w : this) {
            w.showWindow();
        }
    }

    /**
     * Returns an array list containing all windows that are direct instances
     * of a specific class
     * @param type the class of the windows that are to be returned
     * @return an array list containing all windows that are direct instances
     *         of the specifed class
     */
    public ArrayList<CanvasWindow> getAllWindowsOfType(Class<?> type) {
        ArrayList<CanvasWindow> windows = new ArrayList<CanvasWindow>();

        for (CanvasWindow w : this) {
            if (w.getClass().equals(type)) {
                windows.add(w);
            }
        }

        return windows;
    }

    /**
     * Hides windows that are direct instances of a specific class.
     * @param type the class of the windows that are to be hidden
     */
    public void hideWindowsOfType(Class<?> type) {
        for (CanvasWindow w : getAllWindowsOfType(type)) {
            w.hideWindow();
        }
    }

    /**
     * Shows windows that are direct instances of a specific class.
     * @param type the class of the windows that are to be shown
     */
    public void showWindowsOfType(Class<?> type) {
        for (CanvasWindow w : getAllWindowsOfType(type)) {
            w.showWindow();
        }
    }

    /**
     * Hides selected windows.
     */
    public void hideSelectedWindows() {
        for (Selectable s : getMainCanvas().getClipBoard()) {
            if (s instanceof CanvasWindow) {
                CanvasWindow window = (CanvasWindow) s;
                window.hideWindow();
            }
        }
    }

    /**
     * Shows selected windows.
     */
    public void showSelectedWindows() {
        for (Selectable s : getMainCanvas().getClipBoard()) {
            if (s instanceof CanvasWindow) {
                CanvasWindow window = (CanvasWindow) s;
                window.showWindow();
            }
        }
    }

    @Override
    public void dispose() {
        ArrayList<CanvasWindow> windows = new ArrayList<CanvasWindow>(this);
        for (CanvasWindow canvasWindow : windows) {
            canvasWindow.close();
        }
    }
}
