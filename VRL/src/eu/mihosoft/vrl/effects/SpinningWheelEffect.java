/* 
 * SpinningWheelEffect.java
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

import eu.mihosoft.vrl.animation.FrameListener;
import eu.mihosoft.vrl.visual.Wheel;
import javax.swing.SwingUtilities;

/**
 * Spinning wheel effect visualizes GUI component wait state.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class SpinningWheelEffect extends ColorizeEffect {

    private Wheel wheel;
    // TODO use this for styles
//    public static String COLOR_KEY = "SpinningWheelEffect.Color";

    /**
     * Constructor.
     * @param e the effect painter
     */
    public SpinningWheelEffect(EffectPainter e) {
        super(e);
        setName("SpinningWheelEffect");

        wheel = new Wheel(12, null);
        wheel.setDotSize(10);

        setChildComponent(wheel);

        addFrameListener(new FrameListener() {

            @Override
            public void frameStarted(double time) {
                if (time == 0.0) {
                    setActive(true);
                    getComponent().setVisible(true);

                    if (isUncolorize()) {
                        wheel.stopSpin();
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                final SpinningWheelEffect e =
                                        SpinningWheelEffect.this;
                                wheel.setAnimationManager(
                                        getCurrentAnimation().
                                        getAnimationManager());
                                wheel.startSpin(0.1, 1, new FrameListener() {

                                    @Override
                                    public void frameStarted(double time) {
                                        e.getComponent().repaint();
                                    }
                                });
                            }
                        });
                    }
                }

//        if (time > 0.3 && effect!=null) {
//            SwingUtilities.invokeLater(new Runnable() {
//
//                @Override
//                public void run() {
//                    final SpinningWheelEffect e = effect;
//                    e.getWheel().
//                            setAnimationManager(getAnimationManager());
//                    e.getWheel().startSpin(1, new FrameListener() {
//
//                        @Override
//                        public void frameStarted(double time) {
//                            e.getComponent().repaint();
//                        }
//                    });
//                }
//            });
//        }

                if (time == 1.0) {
                    if (isUncolorize()) {
                        setActive(false);
                    }
                }
            }
        });
    }
}
