/* 
 * VDebugTextField.java
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

import eu.mihosoft.vrl.animation.Animation;
import eu.mihosoft.vrl.animation.FrameListener;
import eu.mihosoft.vrl.animation.LinearInterpolation;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;


/**
 * This is an experimental class and should not be used!
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VDebugTextField extends JTextField implements BufferedPainter {
    private static final long serialVersionUID = 8076233892893101339L;

    private TypeRepresentationBase typeRepresentation;
    transient private BufferedImage buffer;
    private Animation animation1;
    private Animation animation2;

    public VDebugTextField(TypeRepresentationBase typeRepresentation) {
        setTypeRepresentation(typeRepresentation);
//        setBackground( VSwingUtil.TRANSPARENT_COLOR);
        
        setBorder(new EmptyBorder(3, 3, 3, 3));

        setOpaque(false);
    }

    public VDebugTextField(TypeRepresentationBase typeRepresentation,
            String s) {
        super(s);
        setTypeRepresentation(typeRepresentation);
//        setBackground( VSwingUtil.TRANSPARENT_COLOR);
        
         setBorder(new EmptyBorder(3, 3, 3, 3));

         setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        
         Color originalBackground = getBackground();
        
        g2.setPaint(originalBackground);
        
        Composite original = g2.getComposite();

                AlphaComposite ac1 =
                        AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                        0.8f);
                g2.setComposite(ac1);
        
        g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
        
        
        Color border = 
                typeRepresentation.getMainCanvas().getStyle().
                getBaseValues().getColor(CanvasWindow.BORDER_COLOR_KEY);
        
        g2.setColor(border);
        
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
        
        setBackground(VSwingUtil.TRANSPARENT_COLOR);
        
        g2.setComposite(original);
        
        super.paintComponent(g);
        
       setBackground(originalBackground);
    }

//    @Override
//    public void setBounds(int x, int y, int w, int h) {
//        Canvas mainCanvas = typeRepresentation.getMainCanvas();
//
//        if (mainCanvas != null) {
//            super.setBounds(x, y, getSize().width, getSize().height);
//            mainCanvas.getAnimationManager().removeAnimation(animation);
//            animation = new ResizeAnimation(this, getSize(),
//                    new Dimension(w, h));
//            animation.setDuration(0.5);
//            mainCanvas.getAnimationManager().addAnimation(animation);
//        } else {
//            super.setBounds(x, y, w, h);
//        }
//    }
    
    public void setBoundsAsSuper(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
    }

    @Override
    public void setPreferredSize(Dimension d) {
        Canvas mainCanvas = typeRepresentation.getMainCanvas();

        if (mainCanvas != null && d != null) {
            mainCanvas.getAnimationManager().removeAnimation(animation1);
            animation1 = new ResizeAnimation2(this, getPreferredSize(), d);
            animation1.setDuration(3.5);
            mainCanvas.getAnimationManager().addAnimation(animation1);
        } else {
            setPreferredSizeAsSuper(d);
        }
    }

    @Override
    public void setMaximumSize(Dimension d) {
        Canvas mainCanvas = typeRepresentation.getMainCanvas();

        if (mainCanvas != null && d != null) {
            mainCanvas.getAnimationManager().removeAnimation(animation2);
            animation2 = new ResizeAnimation3(this, getMaximumSize(), d);
            animation2.setDuration(3.5);
            mainCanvas.getAnimationManager().addAnimation(animation2);
        } else {
            setMaximumSizeAsSuper(d);
        }
    }

    public void setPreferredSizeAsSuper(Dimension d) {
        super.setPreferredSize(d);
        if (typeRepresentation != null &&
                typeRepresentation.getMainCanvas() != null) {
            revalidate();
//            typeRepresentation.getMainCanvas().revalidate();
                   
        }
    }

    public void setMaximumSizeAsSuper(Dimension d) {
        super.setMaximumSize(d);
        if (typeRepresentation != null &&
                typeRepresentation.getMainCanvas() != null) {
             revalidate();
//            typeRepresentation.getMainCanvas().revalidate();
        }
    }
//    @Override
//    public void setSize(int w, int h){
//        super.setSize(w,h);
//        setBackground(Color.BLUE);
//        setForeground(Color.BLUE);
//    }
    public void contentChanged() {
        buffer = null;
    }

    public TypeRepresentationBase getTypeRepresentation() {
        return typeRepresentation;
    }

    public void setTypeRepresentation(
            TypeRepresentationBase typeRepresentation) {
        this.typeRepresentation = typeRepresentation;
    }
}
/**
 *
 * @author miho
 */
class ResizeAnimation2 extends Animation implements FrameListener {

    private VDebugTextField component;
    private LinearInterpolation resizeX;
    private LinearInterpolation resizeY;

    public ResizeAnimation2(VDebugTextField component,
            Dimension start, Dimension stop) {
        this.component = component;
         addFrameListener(this);

        // add targets     
        resizeX = new LinearInterpolation(start.width, stop.width);
        resizeY = new LinearInterpolation(start.height, stop.height);

        getInterpolators().add(resizeX);
        getInterpolators().add(resizeY);

    }

    public void frameStarted(double time) {
        int w = (int) resizeX.getValue();
        int h = (int) resizeY.getValue();
        component.setPreferredSizeAsSuper(new Dimension(w, h));
    }
}

/**
 *
 * @author miho
 */
class ResizeAnimation3 extends Animation implements FrameListener {

    private VDebugTextField component;
    private LinearInterpolation resizeX;
    private LinearInterpolation resizeY;

    public ResizeAnimation3(VDebugTextField component,
            Dimension start, Dimension stop) {
        this.component = component;
         addFrameListener(this);

        // add targets     
        resizeX = new LinearInterpolation(start.width, stop.width);
        resizeY = new LinearInterpolation(start.height, stop.height);

        getInterpolators().add(resizeX);
        getInterpolators().add(resizeY);

    }

    public void frameStarted(double time) {
        int w = (int) resizeX.getValue();
        int h = (int) resizeY.getValue();
        component.setMaximumSizeAsSuper(new Dimension(w, h));
    }
}

/**
 *
 * @author miho
 */
class ResizeAnimation extends Animation implements FrameListener {

    private VDebugTextField component;
    private LinearInterpolation resizeX;
    private LinearInterpolation resizeY;

    public ResizeAnimation(VDebugTextField component,
            Dimension start, Dimension stop) {
        this.component = component;
         addFrameListener(this);

        // add targets     
        resizeX = new LinearInterpolation(start.width, stop.width);
        resizeY = new LinearInterpolation(start.height, stop.height);

        getInterpolators().add(resizeX);
        getInterpolators().add(resizeY);

    }

    public void frameStarted(double time) {
        int w = (int) resizeX.getValue();
        int h = (int) resizeY.getValue();
        component.setBoundsAsSuper(component.getLocation().x,
                component.getLocation().y, w, h);
        System.out.println("W: " + w + " H: " + h);
    }
}
