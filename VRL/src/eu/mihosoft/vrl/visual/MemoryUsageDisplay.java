/* 
 * MemoryUsageDisplay.java
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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayDeque;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class MemoryUsageDisplay implements GlobalBackgroundPainter, Disposable {

    private ArrayDeque<Double> totalMemGraph = new ArrayDeque<Double>();
    private ArrayDeque<Double> usedMemGraph = new ArrayDeque<Double>();
    private Canvas canvas;
    // AffineTransform transform = new AffineTransform();
    private int maxSteps = 100;
    private double maxUsage;
    private double totalUsage;
    private double memUsage;
    private Timer timer = new Timer();
    private boolean requestUpdate = true;

    public MemoryUsageDisplay(Canvas canvas) {
        this.canvas = canvas;
    }

    private synchronized void setRequestUpdate(boolean state) {
        requestUpdate = state;
    }

    @Override
    public void paintGlobal(Graphics g) {

        if (requestUpdate) {
            updateGraph();
            setRequestUpdate(false);
        }

        paintGraph(g, Color.green, totalMemGraph);
        paintGraph(g, Color.red, usedMemGraph);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int x = (int) canvas.getVisibleRect().getX();
        int y = (int) canvas.getVisibleRect().getY();

        int width = (int) canvas.getVisibleRect().getWidth();
        int height = 80 + 10; //(int) canvas.getVisibleRect().getHeight();

        Composite original = g2.getComposite();

        AlphaComposite ac1 = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 0.6f);

        g2.setComposite(ac1);

        g2.setColor(canvas.getStyle().getBaseValues().
                getColor(MessageBox.BOX_COLOR_KEY));
        
        g2.fillRoundRect(x + 10, y + height - 80, 200, 70, 18, 18);

        g2.setColor(Color.green);
        g2.drawString("Total Memory: " + totalUsage + " MB", x + 25, y + height - 30);
        g2.setColor(Color.red);
        g2.drawString("Used Memory: " + memUsage + " MB", x + 25, y + height - 50);

        g2.setComposite(original);
    }

    public void paintGraph(Graphics g, Color c, ArrayDeque<Double> graph) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int x = (int) canvas.getVisibleRect().getX();
        int y = (int) canvas.getVisibleRect().getY();

        int width = (int) canvas.getVisibleRect().getWidth();
        int height = (int) canvas.getVisibleRect().getHeight();

        double scaleX = width / (double) (maxSteps - 1);
        double scaleY = height / maxUsage;

        Double[] graphArray = new Double[maxSteps];

        graph.toArray(graphArray);

        g.setColor(c);

        BasicStroke stroke = new BasicStroke(2,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        g2.setStroke(stroke);

        for (int i = 1; i < graphArray.length; i++) {

            if (graphArray[i] != null & graphArray[i - 1] != null) {

                int x0 = (int) ((i - 1) * scaleX);
                int y0 = (int) (graphArray[i - 1] * scaleY);
                int x1 = (int) (i * scaleX);
                int y1 = (int) (graphArray[i] * scaleY);

                g2.drawLine(x + x0, y + height - y0, x + x1, y + height - y1);
            }
        }
    }

    private void updateGraph() {
        Runtime r = Runtime.getRuntime();

        maxUsage = r.maxMemory() / 1024 / 1024;
        totalUsage = r.totalMemory() / 1024 / 1024;
        memUsage = totalUsage - r.freeMemory() / 1024 / 1024;

        totalMemGraph.addLast(totalUsage);
        usedMemGraph.addLast(memUsage);

        while (totalMemGraph.size() > maxSteps) {
            totalMemGraph.removeFirst();
            usedMemGraph.removeFirst();
        }
    }

    public void run(double duration) {

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                canvas.repaint();
                setRequestUpdate(true);
            }
        }, 0, (long) (duration * 1000));
    }

    public void stop() {
        timer.cancel();
        timer = new Timer();
        canvas.repaint();
    }

    @Override
    public void dispose() {
        if (timer!=null) {
            timer.cancel();
        }
    }
    
}
