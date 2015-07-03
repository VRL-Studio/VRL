///*
// * RecordSessionApplet.java
// *
// * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
// *
// * Copyright (C) 2009 Michael Hoffer &lt;info@michaelhoffer.de&gt;
// *
// * Supported by the Goethe Center for Scientific Computing of Prof. Wittum
// * (http://gcsc.uni-frankfurt.de)
// *
// * This file is part of Visual Reflection Library (VRL).
// *
// * VRL is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License version 3
// * as published by the Free Software Foundation.
// *
// * VRL is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// *
// * Linking this library statically or dynamically with other modules is
// * making a combined work based on this library.  Thus, the terms and
// * conditions of the GNU General Public License cover the whole
// * combination.
// *
// * As a special exception, the copyright holders of this library give you
// * permission to link this library with independent modules to produce an
// * executable, regardless of the license terms of these independent
// * modules, and to copy and distribute the resulting executable under
// * terms of your choice, provided that you also meet, for each linked
// * independent module, the terms and conditions of the license of that
// * module.  An independent module is a module which is not derived from
// * or based on this library.  If you modify this library, you may extend
// * this exception to your version of the library, but you are not
// * obligated to do so.  If you do not wish to do so, delete this
// * exception statement from your version.
// */
//
//package eu.mihosoft.vrl.reflection;
//
//import eu.mihosoft.vrl.visual.CanvasActionListener;
//import eu.mihosoft.vrl.visual.style.CanvasStyle;
//import eu.mihosoft.vrl.visual.DockApplet;
//import eu.mihosoft.vrl.visual.MessageType;
//import eu.mihosoft.vrl.visual.PulseIcon;
//import java.awt.AlphaComposite;
//import java.awt.BasicStroke;
//import java.awt.Color;
//import java.awt.Composite;
//import java.awt.Dimension;
//import java.awt.Graphics2D;
//import java.awt.RenderingHints;
//import java.awt.event.ActionEvent;
//import java.awt.image.BufferedImage;
//import javax.swing.ImageIcon;
//
///**
// * Dock applet with recording functionality for tutorial sessions.
// * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
// */
//public class RecordSessionApplet extends DockApplet {
//    private static final long serialVersionUID = -6926135630282618538L;
//
//    /**
//     * Constructor.
//     * @param session the session to record
//     */
//    public RecordSessionApplet(final TutorialSession session) {
//        super(session);
////        ImageIcon icon =
////                new ImageIcon(new PulseIcon(getMainCanvas(),
////                MessageType.WARNING).getStillImage());
////        setIcon(icon);
//
//        setIcon(generateRecordIcon(new Dimension(40, 40)));
//
//        setActionListener(new CanvasActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (!session.isRecording() && !session.isPlaying()) {
//                    session.startRecording();
//                    setIcon(generateStopIcon(new Dimension(40, 40)));
//                    repaint();
//                } else {
//                    session.stopRecording();
//                    setIcon(generateRecordIcon(new Dimension(40, 40)));
//                    repaint();
//                }
//
//            }
//        });
//
//    }
//
//    /**
//     * Generates the record icon.
//     * @param size the icon size
//     * @return the icon
//     */
//    private ImageIcon generateRecordIcon(Dimension size) {
//
//        BufferedImage buffer =
//                new BufferedImage(size.width, size.height,
//                BufferedImage.TYPE_INT_ARGB);
//
//        Color color = new Color(1.f, 0.f, 0.f, 0.8f);
//
//        Graphics2D g2 = buffer.createGraphics();
//
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);
//
//        Composite original = g2.getComposite();
//
//        CanvasStyle style = getMainCanvas().getStyle();
//
//        float alpha = style.getObjectTransparency();
//        AlphaComposite ac1 = AlphaComposite.getInstance(
//                AlphaComposite.SRC_OVER, alpha);
//
//        g2.setComposite(ac1);
//
//
//        g2.setColor(style.getMessageBoxIconColor());
//
//
//        float lineThickness = 1;
//
//        BasicStroke stroke = new BasicStroke(lineThickness,
//                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
//
//        g2.setStroke(stroke);
//
//        int topLeftSpacing = (int) lineThickness;
//        int bottomRightSpacing = (int) lineThickness + topLeftSpacing;
//
////        Polygon triangle = new Polygon();
//
//        g2.setPaint(color);
//
//        g2.fillOval(0+topLeftSpacing, 0+topLeftSpacing,
//                size.width - bottomRightSpacing,
//                size.height- bottomRightSpacing);
//
//        g2.dispose();
//
//        return new ImageIcon(buffer);
//    }
//
//    /**
//     * Generates the stop icon.
//     * @param size the icon size
//     * @return the icon
//     */
//    private ImageIcon generateStopIcon(Dimension size) {
//
//        BufferedImage buffer =
//                new BufferedImage(size.width, size.height,
//                BufferedImage.TYPE_INT_ARGB);
//
//        Color color = getMainCanvas().getStyle().getMessageBoxTextColor();
//
//        Graphics2D g2 = buffer.createGraphics();
//
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);
//
//        Composite original = g2.getComposite();
//
//        CanvasStyle style = getMainCanvas().getStyle();
//
//        float alpha = style.getObjectTransparency();
//        AlphaComposite ac1 = AlphaComposite.getInstance(
//                AlphaComposite.SRC_OVER, alpha);
//
//        g2.setComposite(ac1);
//
//
//        g2.setColor(style.getMessageBoxIconColor());
//
//
//        float lineThickness = 1;
//
//        BasicStroke stroke = new BasicStroke(lineThickness,
//                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
//
//        g2.setStroke(stroke);
//
//        int topLeftSpacing = (int) lineThickness;
//        int bottomRightSpacing = (int) lineThickness + topLeftSpacing;
//
////        Polygon triangle = new Polygon();
//
//        g2.fillRect(0+topLeftSpacing, 0+topLeftSpacing,
//                size.width - bottomRightSpacing,
//                size.height- bottomRightSpacing);
//
//        g2.dispose();
//
//        return new ImageIcon(buffer);
//    }
//}
