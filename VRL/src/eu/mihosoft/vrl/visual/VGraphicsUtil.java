/* 
 * VGraphicsUtil.java
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

package eu.mihosoft.vrl.visual;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.Border;

/**
 * Simplifies the process of specifying correct system values for the JVM.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VGraphicsUtil {

    // no instanciation allowed
    private VGraphicsUtil() {
        throw new AssertionError(); // not in this class either!
    }
    /**
     * Indicates whether to use 3d rendering.
     */
    public static boolean NO_3D;

    /**
     * Enabling 2D acceleration has to be enabled before the first graphical
     * element has been created. It does not work on all plattforms and graphics
     * configurations.
     */
    public static void enable2DAcceleration() {
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.translaccel", "true");
        System.setProperty("sun.java2d.ddforcevram", "true");
    }

    /**
     * 3D variables have to be set before the first graphical element has been
     * created. It is recommended on all plattforms because it will allow
     * transparent VCanvas3D objects and scene antialiasing.
     *
     * @param transparency defines whether to enable 3d transparency
     */
    public static void set3DEnvironment(Boolean transparency) {
        System.setProperty("j3d.transparentOffScreen", transparency.toString());
        System.setProperty("j3d.implicitAntialiasing", "true");
    }

    /**
     * 3D variables have to be set before the first graphical element has been
     * created. It is recommended on all plattforms because it will allow
     * transparent VCanvas3D objects and scene antialiasing.
     */
    public static void set3DEnvironment() {
        set3DEnvironment(true);
    }

    /**
     * Disbales Java 3D based rendering. This is necessary on some small devices
     * such as Netbooks etc.
     */
    public static void disable3DGraphics() {
        NO_3D = true;
    }

    /**
     * Returns the dimensions of a screen.
     *
     * @param screenID the id of the screen
     * @return the screen dimension
     */
    public static Dimension getScreenDimension(int screenID) {

        // TODO check http://nopaste.linux-dev.org/?22450

        Dimension size = new Dimension(0, 0);

        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        DisplayMode mode =
                ge.getScreenDevices()[screenID].getDisplayMode();
        size.setSize(mode.getWidth(), mode.getHeight());

        return size;
    }

    /**
     * Returns the bounds of the screen in device coordinates.
     *
     * @param screenID the screen id
     * @return the screen bounds
     */
    public static Rectangle getScreenBounds(int screenID) {
        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gds = ge.getScreenDevices();

        GraphicsDevice gd = gds[screenID];

        return gd.getDefaultConfiguration().getBounds();
    }

    /**
     * Returns the number of screens.
     *
     * @return the number of screens
     */
    public static int getNumberOfScreens() {
        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ge.getScreenDevices();
        return gd.length;
    }

    /**
     * Centers a frame on screen.
     *
     * @param frame the frame to center
     * @param screenID the screen id
     */
    public static void centerOnScreen(JFrame frame, int screenID) {
        Rectangle screenBounds = getScreenBounds(screenID);

        int x = screenBounds.x
                + screenBounds.width / 2 - frame.getWidth() / 2;
        int y = screenBounds.y
                + screenBounds.height / 2 - frame.getHeight() / 2;

        frame.setLocation(x, y);
    }

    /**
     * Centers a frame on parent frame.
     *
     * @param parent parent frame
     * @param frame the frame to center
     */
    public static void centerOnWindow(Window parent, Window frame) {
        Rectangle screenBounds = parent.getBounds();

        int x = screenBounds.x
                + screenBounds.width / 2 - frame.getWidth() / 2;
        int y = screenBounds.y
                + screenBounds.height / 2 - frame.getHeight() / 2;

        frame.setLocation(x, y);
    }

    /**
     * Returns a red line border to simplify layout debugging.
     *
     * @return a red line border
     * @deprecated since 0.4
     */
    public static Border createDebugBorder() {
        return VSwingUtil.createDebugBorder();
    }

    /**
     * Returns a graphics device.
     *
     * @param deviceID the device id
     * @return the device
     */
    public static GraphicsDevice getGraphicsDevice(int deviceID) {
        GraphicsEnvironment graphicsEnvironment =
                GraphicsEnvironment.getLocalGraphicsEnvironment();

        GraphicsDevice[] devices =
                graphicsEnvironment.getScreenDevices();

        return devices[deviceID];
    }

    /**
     * Returns the screen ID of the specified window. This method can be used to
     * find out on which screen the specified window is displayed.
     *
     * @return the screen id or
     * <code>-1</code> if no screen can be found
     */
    public static int getScreenId(Window w) {
        int result = -1;

        GraphicsDevice d = w.getGraphicsConfiguration().getDevice();

        GraphicsEnvironment graphicsEnvironment =
                GraphicsEnvironment.getLocalGraphicsEnvironment();

        GraphicsDevice[] devices =
                graphicsEnvironment.getScreenDevices();

        for (int i = 0; i < devices.length; i++) {
            if (d.equals(devices[i])) {
                result = i;
                break;
            }
        }

        return result;
    }

    /**
     * Enters fullscreen mode. <p><b>Warning:</b> using native support may cause
     * problems with multi screen setups on OS X.</p>
     *
     * @param frame
     * @param screenID id of the target screen
     * @param nativeSupport defines whether to use tative fullscreen support
     */
    public static void enterFullscreenMode(final Window frame, int screenID,
            boolean nativeSupport) {
        GraphicsDevice graphicsDevice = getGraphicsDevice(screenID);

        if (graphicsDevice.isFullScreenSupported() && nativeSupport) {

            if (frame instanceof JFrame) {
                ((JFrame) frame).setResizable(false);
            }

//            frame.setVisible(true);

            // enter fullscreen mode
            graphicsDevice.setFullScreenWindow(frame);
            frame.validate();

            if (frame instanceof JFrame) {
                frame.addComponentListener(new FullscreenComponentListener((JFrame) frame));
            }

        } else {
            Rectangle bounds =
                    graphicsDevice.getDefaultConfiguration().getBounds();
            frame.setVisible(false);
            frame.setBounds(bounds);
            frame.setVisible(true);
//            frame.setBounds(bounds);
            if (nativeSupport) {
                System.err.println("Full-screen mode not supported");
            }
        }//end else
    }

    /**
     * Enters fullscreen mode.
     *
     * @param frame
     */
    public static void enterFullscreenMode(final Window frame) {
        enterFullscreenMode(frame, 0, true);
    }

    /**
     * Leaves fullscreen mode. This method should only be used if native support
     * requested and/or supported.
     *
     * @param frame
     */
    public static void leaveFullscreenMode(Window frame) {
        GraphicsDevice graphicsDevice = getGraphicsDevice(getScreenId(frame));

        if (graphicsDevice.isFullScreenSupported()) {
            // Enter full-screen mode with an undecorated,
            // non-resizable JFrame object.
//            frame.setVisible(false);
//            frame.setUndecorated(false);


            if (frame instanceof JFrame) {
                ((JFrame) frame).setResizable(true);
            }
//            frame.setVisible(true);
            //Make it happen!
            graphicsDevice.setFullScreenWindow(null);
            frame.validate();

            for (ComponentListener l : frame.getComponentListeners()) {
                if (l instanceof FullscreenComponentListener) {
                    frame.removeComponentListener(l);
                    break;
                }
            }

        } else {
            System.out.println("Full-screen mode not supported");
        }//end else
    }
}

class FullscreenComponentListener extends ComponentAdapter {

    private JFrame frame;

    public FullscreenComponentListener(JFrame frame) {
        this.frame = frame;
    }

//                public void componentResized(ComponentEvent e) {
//                    setSize(1024, 768);	// or whatever your full size is
//                }
    @Override
    public void componentMoved(ComponentEvent e) {
        frame.setLocation(0, 0);
    }
}
