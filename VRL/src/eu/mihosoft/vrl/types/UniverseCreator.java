/* 
 * UniverseCreator.java
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

package eu.mihosoft.vrl.types;


import com.sun.j3d.exp.swing.JCanvas3D;
import com.sun.j3d.utils.universe.SimpleUniverse;
import eu.mihosoft.vrl.visual.Disposable;
import javax.media.j3d.TransformGroup;



/**
 * <p>
 * Creates a Java 3D universe and the corresponding scene graph. All 3D based
 * type representations rely on it.
 * </p>
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface UniverseCreator extends Disposable{

    /**
     * Initializes the universe creator. This method must be called after
     * adding the canvas to a parent component.
     * @param canvas the 3D canvas
     */
    public void init(VCanvas3D canvas);

    /**
     * Returns the camera groop.
     * @return the camera group
     */
    public TransformGroup getCamGroup();

    /**
     * Returns the canvas.
     * @return the canvas
     */
    public VCanvas3D getCanvas();

    /**
     * Returns the offscreen canvas.
     * @return the offscreen canvas to set
     */
    public VOffscreenCanvas3D getOffscreenCanvas();

    /**
     * Returns the root group. Always use the root group to attach 3d shapes
     * etc.
     * @return the root group
     */
    public TransformGroup getRootGroup();

    /**
     * Returns the universe.
     * @return the universe
     */
    public SimpleUniverse getUniverse();
    
    /**
     * Defines the zoom factor (wheel zoom behavior).
     * @param d factor to set
     */
    public void setZoomFactor(double d);
    
    /**
     * Returns the zoom factor (wheel zoom behavior).
     * @return the zoom factor
     */
    public double getZoomFactor();

}
