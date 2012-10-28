/* 
 * CanvasStyleManager.java
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

package eu.mihosoft.vrl.visual;

import java.awt.Component;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * This class manages Canvas styles. Depending on the style it also changes
 * the LookAndFeel.
 * @see javax.swing.LookAndFeel
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class CanvasStyleManager implements CanvasChild {

    private Canvas mainCanvas;
    private HashSet<WeakReference<StyleChangedListener>> styleChangedListeners =
            new HashSet<WeakReference<StyleChangedListener>>();
    public static final String DEFAULT_LOOK_AND_FEEL;
    private static final int REFRESH_COUNTER = 3;

    /**
     * Static constructor.
     */
    static {
        if (System.getProperty("os.name").contains("Mac OS X")) {
//            DEFAULT_LOOK_AND_FEEL =
//                    "com.apple.laf.AquaLookAndFeel";
            DEFAULT_LOOK_AND_FEEL =
                    "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
        } else {
            DEFAULT_LOOK_AND_FEEL =
                    "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
        }
    }

    /**
     * Constructor.
     */
    public CanvasStyleManager() {
    }

    /**
     * Constructor.
     * @param mainCanvas the main canvas
     */
    public CanvasStyleManager(Canvas mainCanvas) {
        setMainCanvas(mainCanvas);
    }

    /**
     * Removes old weak references from the listener set.
     */
    private void cleanListenerSet() {

        if (styleChangedListeners.size() % REFRESH_COUNTER == 0) {
            ArrayList<WeakReference<StyleChangedListener>> delList =
                    new ArrayList<WeakReference<StyleChangedListener>>();

            for (WeakReference<StyleChangedListener> weakReference : delList) {
                if (weakReference.get() == null) {
                    delList.add(weakReference);
                }
            }

            for (WeakReference<StyleChangedListener> weakReference : delList) {
                styleChangedListeners.remove(weakReference);
            }
        }
    }

    /**
     * Adds a style changed listener to this style manager.
     * <p><b>Note: </b></p> weak references are used! Thus, the user is responsible
     * for keeping a strong reference to the added listener.
     * @param l the listener to add
     */
    public void addStyleChangedListener(StyleChangedListener l) {
        styleChangedListeners.add(
                new WeakReference<StyleChangedListener>(l));

        cleanListenerSet();
    }

    /**
     * Removes a style changed listener to this style manager.
     * @param l the listener to add
     * @return <code>true</code> (as specified by {@link java.util.Collection#add})
     */
    public boolean removeStyleChangedListener(StyleChangedListener l) {
        if (l == null) {
            throw new IllegalArgumentException(
                    "Argument \"null\" not supported!");
        }
        WeakReference<StyleChangedListener> delRef = null;
        for (WeakReference<StyleChangedListener> wR : styleChangedListeners) {
            StyleChangedListener listener = wR.get();
            if (listener != null && listener.equals(l)) {
                delRef = wR;
            }
        }

        if (delRef != null) {
            return styleChangedListeners.remove(delRef);
        }

        cleanListenerSet();

        return false;
    }

    /**
     * Evaluates a canvas style. That is, applies color values to the canvas
     * and the LookAndFeel. After evaluating the style all canvas changed
     * listeners will be notified.
     * @param style the canvas style to evaluate
     */
    void evaluateStyle(Style style) {
        Style previousStyle = getMainCanvas().getStyle();

        getMainCanvas().setBackground(style.getBaseValues().getColor(Canvas.BACKGROUND_COLOR_KEY));


        getMainCanvas().getBackgroundImage().backgroundImageFromStyle(style);

        if (getMainCanvas().getWindows() != null) {
            getMainCanvas().getWindows().redrawRequest();
        }

        // TODO remove this hack and replace it with observer pattern
        if (getMainCanvas().getMessageBox() != null) {
            getMainCanvas().getMessageBox().contentChanged();
        }
        if (getMainCanvas().getDock() != null) {
            getMainCanvas().getDock().contentChanged();
        }

        // Look and Feel related code

        // undo changes to laf specific ui defaults
        if (previousStyle != null) {
            for (String key : previousStyle.getLookAndFeelValues().getValues().keySet()) {
                UIManager.getDefaults().remove(key);
            }
        }

        String lookAndFeel = null;

        try {
            lookAndFeel = (String) style.getBaseValues().get("LookAndFeel");
        } catch (Exception ex) {
            //
        }

        if (lookAndFeel == null) {
            lookAndFeel = DEFAULT_LOOK_AND_FEEL;
        }


        boolean foundLAF = true;
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Canvas.class.getName()).
                    log(Level.SEVERE, null, ex);
            foundLAF = false;
        } catch (InstantiationException ex) {
            Logger.getLogger(Canvas.class.getName()).
                    log(Level.SEVERE, null, ex);
            foundLAF = false;
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Canvas.class.getName()).
                    log(Level.SEVERE, null, ex);
            foundLAF = false;
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Canvas.class.getName()).
                    log(Level.SEVERE, null, ex);
            foundLAF = false;
        }

        if (!foundLAF) {
            try {
                mainCanvas.getMessageBox().addMessage("Style Error:",
                        ">> the look and feel: \"<i><u>" + lookAndFeel
                        + "</u></i>\" " + "could not be loaded!",
                        MessageType.WARNING);
            } catch (Exception ex) {
                //
            }
        }

        // set laf specific ui defaults
        for (String key : style.getLookAndFeelValues().getValues().keySet()) {
            UIManager.put(key, style.getLookAndFeelValues().get(key));
        }

        SwingUtilities.updateComponentTreeUI(mainCanvas);

        notifyListeners(style);
    }

    private void notifyListeners(Style style) {
        if (styleChangedListeners != null) {
            for (WeakReference<StyleChangedListener> l : styleChangedListeners) {
                StyleChangedListener listener = l.get();
                if (listener != null) {
                    listener.styleChanged(style);
                }
            }
        }
    }

    /**
     * Puts a base value to the canvas style and notifies all style changed listeners.
     * @param k the key
     * @param v the value
     */
    public void putBaseValue(String k, Object v) {
        getMainCanvas().getStyle().getBaseValues().set(k, v);

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                mainCanvas.repaint();
            }
        });

        notifyListeners(getMainCanvas().getStyle());
    }

    /**
     * Puts a nimbus value to the canvas style and notifies all style changed
     * listeners.
     * @param k the key
     * @param v the value
     */
    public void putNimbusValue(String k, Object v) {
        getMainCanvas().getStyle().getLookAndFeelValues().set(k, v);

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                mainCanvas.repaint();
            }
        });

        notifyListeners(getMainCanvas().getStyle());
    }

    /**
     * Puts a custom value to the canvas style and notifies all style changed
     * listeners.
     * @param k the key
     * @param v the value
     */
    public void putCustomValue(String k, Object v) {
        getMainCanvas().getStyle().getCustomValues().set(k, v);

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                mainCanvas.repaint();
            }
        });

        notifyListeners(getMainCanvas().getStyle());
    }

    @Override
    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    @Override
    public final void setMainCanvas(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;
    }

//    /**
//     * Returns the style changed listeners.
//     * @return the style changed listeners
//     */
//    public Collection<CanvasStyleChangedListener> getStyleChangedListeners() {
//        return styleChangedListeners;
//    }
    /**
     * Disposes additional resources, e.g., listeners, etc.
     */
    public void dispose() {
        if (styleChangedListeners != null) {
            styleChangedListeners.clear();
            styleChangedListeners = null;
        }
    }
}
