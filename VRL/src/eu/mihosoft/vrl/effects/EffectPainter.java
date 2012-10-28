/* 
 * EffectPainter.java
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

package eu.mihosoft.vrl.effects;

import eu.mihosoft.vrl.visual.BufferedPainter;
import java.awt.Graphics;

/**
 * Components that want to use an effect manager for post processing their
 * <code>paint()</code> method have to implement this interface.
 * 
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface EffectPainter extends BufferedPainter{
    /**
     * Returns the effect manager that is responsible for this effect painter.
     * @return the effect manager that is responsible for this effect painter
     */
    public EffectManager getEffectManager();
    /**
     * In case of Swing components this method should contain the functionality
     * of the <code>paint</code> method. Usually it looks like this:
     *
     * <pre>
     * public void paintWithoutEffects(Graphics g) {
     *     super.paint(g);
     * }
     * </pre>
     * @param g the Graphics context in which to paint 
     */
    public void paintWithoutEffects(Graphics g);

    /**
     * This is an overloaded version of the <code>paint</code> method.
     * A possible implementation:
     * <pre>
     * public void paint(Graphics g) {
     *   if (effectManager != null) {
     *       effectManager.paint(this, g);
     *   }
     * }
     * </pre>
     * @param g the Graphics context in which to paint 
     */
    public void paint(Graphics g);


}
