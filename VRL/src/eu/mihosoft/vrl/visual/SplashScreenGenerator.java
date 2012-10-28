/* 
 * SplashScreenGenerator.java
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

import eu.mihosoft.vrl.system.Constants;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.Deque;

// splash screen image dimensions: 600x380
/**
 * Generates splash screens used to show application startup progress.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class SplashScreenGenerator {

    private static final Object lock = new Object();
    private String copyrightText = Constants.COPYRIGHT_SIMPLE;

    /**
     * @param aBootMessage the bootMessages to set
     */
    public static synchronized void printBootMessage(String aBootMessage) {
        synchronized (lock) {
            bootMessages.addFirst(aBootMessage);

            while (bootMessages.size() > 3) {
                bootMessages.removeLast();
            }
        }
    }

    /**
     * @param aCopyrightText the copyrightText to set
     */
    public void setCopyrightText(String aCopyrightText) {
        copyrightText = aCopyrightText;
    }
    
    private SplashScreen splashScreen;
    private int maxFrame = 100;
    private static Integer frame = 0;
    private static Deque<String> bootMessages = new ArrayDeque<String>();

    private void renderBootMessages(Graphics2D g2) {
        Font originalFont = g2.getFont();
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 9);

        g2.setFont(font);

        Rectangle2D fontBounds =
                font.getStringBounds(
                copyrightText, g2.getFontRenderContext());

        double fontHeight = fontBounds.getHeight();

        double posY = 330;
        double lineSpace = 5;
        int alpha = 128;
        int alphaStep = 32;

        synchronized (lock) {
            for (String m : bootMessages) {
                g2.setColor(new Color(185, 186, 230, alpha));
                g2.drawString(m, 10, (int) posY);
                posY -= fontHeight + lineSpace;
                alpha -= alphaStep;
            }
        }

        g2.setFont(originalFont);
    }

    /**
     * Renders the splash screen.
     *
     * @param g the graphics context to use
     * @param frame the frame number (used to visualize progress par)
     */
    private void renderSplashFrame(Graphics2D g, double frame) {
        g.setComposite(AlphaComposite.Clear);

        g.setComposite(AlphaComposite.Clear);
//        g.fillRect(1, 269, 600 - 2, 11);

        g.fillRect(0, 0,
                splashScreen.getBounds().width,
                splashScreen.getBounds().height);

        g.setPaintMode();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(185, 186, 230, 128));
//        g.drawString("Loading "+comps[(frame/5)%3]+"...", 130, 260);
//        g.drawString("Loading ...", 130, 260);      

//        Font originalFont = g.getFont();
//        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 9);
//
//        g.setFont(font);
//
//        g.drawString(bootMessage, 10, 300);

        renderBootMessages(g);

        drawVersionAndCopyrightText(g);
        
        int color = 80+(int) ((1+Math.sin(20*frame))*40);

        g.setColor(new Color(145, 156, 220, color));

        g.fillRect(1, 269,
                (int)((splashScreen.getBounds().width / maxFrame) * frame - 2),
                11);

//        g.setFont(originalFont);
    }

    public SplashScreen initGlobalSplashScreen() {

        printBootMessage(">> initializing VRL");

        splashScreen = SplashScreen.getSplashScreen();
        if (splashScreen == null) {
            System.out.println("SplashScreen.getSplashScreen() returned null");
        }
        return splashScreen;
    }

    /**
     * Shows the splash screen.
     *
     * @param durationPerFrame the duration per frame
     */
    public void showSplashScreen(int durationPerFrame) {

        if (splashScreen == null) {
            System.out.println("SplashScreen.getSplashScreen() returned null");
            return;
        }

        Graphics2D g2 = splashScreen.createGraphics();
        if (g2 == null) {
            System.out.println("g is null");
            return;
        }

        double value = 0;

        while (value < maxFrame || splashScreen.isVisible()) {

            value = Math.max(getProgress(), value);

            try {
                renderSplashFrame(g2, value);
                splashScreen.update();
            } catch (IllegalStateException ex) {
                //
            }
            try {
                Thread.sleep(durationPerFrame);
            } catch (InterruptedException e) {
            }

            value += 0.01;

            setProgress((int) value);
        }

    }

    public static void setProgress(int percentage) {
        synchronized (frame) {
            frame = Math.max(frame, percentage);
        }
    }

    public static int getProgress() {
        int result = 0;
        synchronized (frame) {
            result = frame;
        }

        return result;
    }

    private void drawVersionAndCopyrightText(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(202, 212, 222, 128));

        Font originalFont = g2.getFont();
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 12);
        
        // draw copyright string (used for studio)
        g2.setFont(font);
        Rectangle2D fontBounds =
                font.getStringBounds(
                copyrightText, g2.getFontRenderContext());

        Rectangle2D splashBounds = splashScreen.getBounds();

        int x = (int) (splashBounds.getWidth() - fontBounds.getWidth()) - 8;
        int y = (int) (splashBounds.getHeight() - fontBounds.getHeight() + 5);

        g2.drawString(copyrightText, x, y);
        
        
        // draw vrl version string
        font = new Font(Font.SANS_SERIF, Font.BOLD, 8);
        g2.setFont(font);
        
        String vrlText = "VRL "+Constants.VERSION_BASE;
        
        fontBounds =
                font.getStringBounds(
                vrlText, g2.getFontRenderContext());

        x = 10;
        y = (int) (splashBounds.getHeight() - fontBounds.getHeight() + 2);

        g2.drawString(vrlText, x, y);

        g2.setFont(originalFont);
    }

    /**
     * Shows the splash screen.
     */
    public void showSplashScreen() {
        showSplashScreen(50);
    }
}
