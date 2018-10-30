/* 
 * Effect.java
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

package eu.mihosoft.vrl.effects;

import eu.mihosoft.vrl.animation.Animation;
import eu.mihosoft.vrl.visual.BufferedPainter;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * <p>
 * An effect can be used to dynamically apply effects to the
 * <code>paint()</code> method of effect painters. Effect painters usually are
 * Swing components. Examples for effects are blurring, fade in and fade out
 * effects.
 * </p>
 * <p>
 * The purpose of this class is to provide an easy to use way to post process
 * the visual appearance of Swing components.
 * </p>
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface Effect extends BufferedPainter{
    /**
     * Applies the effect to an image.
     * @param img the image that is to be used to apply the effect
     */
    public void paint(BufferedImage img);

    /**
     * Returns the animation of this effect.
     * @return the animation of this effect
     */
    public Animation getAnimation();

    /**
     * Returns the effect painter on which the effect will be applied to.
     * @return the effect painter on which the effect will be applied to
     */
    public EffectPainter getEffectPainter();

    /**
     * Indicates whether the effect is currently active.
     * @return <code>true</code> if the effect is currently active;
     * <code>false</code> otherwise
     */
    public boolean isActive();
    /**
     * Defines if the effect is active.
     * @param state the state defining if this effect is active
     */
    public void setActive(boolean state);
    /**
     * Returns the name of the effect.
     * @return the name of the effect

     */
    public String getName();
    /**
     * Defines the name of this effect.
     * @param name the name
     */
    public void setName(String name);
}
