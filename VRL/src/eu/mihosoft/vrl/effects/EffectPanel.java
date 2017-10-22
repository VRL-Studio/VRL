/* 
 * EffectPanel.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2007–2017 by Michael Hoffer
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

package eu.mihosoft.vrl.effects;

import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.CanvasChild;
import eu.mihosoft.vrl.visual.Disposable;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * Effect panels can be used as container for components that do not implement
 * the effect painter interface. Its effects will be applied to all child
 * components. One possible use case is fading out a group of Swing components.
 * 
 * 
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class EffectPanel extends JPanel implements CanvasChild, EffectPainter,
    Disposable{

    /**
     * the effect manager that is responsible for this component
     */
    private EffectManager effectManager;
    /**
     * the main canvas object
     */
    private Canvas mainCanvas;
    /**
     * the list of effects applied to this panel
     */
    private ArrayList<Effect> effects = new ArrayList<Effect>();

    /**
     * Constructor.
     *
     */
    public EffectPanel() {
        setOpaque(false);
    }

    /**
     * Costructor.
     * @param mainCanvas
     */
    public EffectPanel(Canvas mainCanvas) {
        setMainCanvas(mainCanvas);
        setOpaque(false);
    }

    /**
     * Returns the effect manager of this panel.
     * @return the effect manager of this panel
     */
    @Override
    public EffectManager getEffectManager() {
        return effectManager;
    }

    @Override
    protected void paintComponent(Graphics g) {
        // invisible
    }

    @Override
    public void paintWithoutEffects(Graphics g) {
        super.paint(g);
    }

    @Override
    public void paint(Graphics g) {
        if (effectManager != null) {
            effectManager.paint(this, g);
        }
    }

    @Override
    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    @Override
    public void setMainCanvas(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;
        this.effectManager = new EffectManager(mainCanvas);
        effectManager.getEffects().addAll(getEffects());
    }

    /**
     * Returns a list containing all effect of this panel.
     * @return a list containing all effect of this panel
     */
    public ArrayList<Effect> getEffects() {
        return effects;
    }

    /**
     * Defines the effects of this panel.
     * @param effects the effects to set
     */
    public void setEffects(ArrayList<Effect> effects) {
        this.effects = effects;
        if (effectManager != null) {
            effectManager.getEffects().removeAll(effectManager.getEffects());
            effectManager.getEffects().addAll(effects);
        }
    }

    
    @Override
    public void dispose() {
//        System.out.println("(D)EffectPanel.dispose()");
        try {
            setEffects(new ArrayList<Effect>());
            this.mainCanvas = null;
            this.effectManager.setMainCanvas(null);
            this.effectManager = null;
        } catch (Exception ex) {
        }
    }

    @Override
    public void setVisible(boolean value) {
        super.setVisible(value);
        this.contentChanged();
    }

    @Override
    public void contentChanged() {
        if (getEffectManager() != null) {
            getEffectManager().contentChanged();
        }
    }
}
