///*
// * ComposerStyle.java
// *
// * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
// *
// * Copyright (C) 2009 Michael Hoffer <info@michaelhoffer.de>
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
//package eu.mihosoft.vrl.visual.style;
//
//import eu.mihosoft.vrl.visual.Connector;
//import java.awt.Color;
//
///**
// * Canvas style inspired by Apple's Quartz composer.
// * @author Michael Hoffer <info@michaelhoffer.de>
// */
//public class ComposerStyle extends CanvasStyle {
//
//    private static final long serialVersionUID = -2258346570263970738L;
//
//    /**
//     * Constructor.
//     */
//    public ComposerStyle() {
//        //Composer like style
//        setCanvasBackground(new Color(98, 107, 129));
//        setGridColor(new Color(154, 158, 166));
//        this.setTextColor(new Color(0, 0, 0));
//
//        setObjectUpperBackground(new Color(210, 227, 228));
//        setObjectLowerBackground(new Color(130, 137, 159));
//        this.setObjectUpperTitleColor(new Color(84, 255, 200));
//        this.setObjectLowerTitleColor(new Color(102, 255, 0));
//        setObjectTransparency(0.65f);
//        setObjectBorderColor(Color.BLACK);
//        setObjectBorderThickness(1);
//
////        setInputColor(getObjectUpperBackground());
////        setOutputColor(getObjectUpperBackground());
//
////        setActiveInputColor(Color.GREEN);
////        setActiveOutputColor(Color.RED);
//
//        setConnectionColor(new Color(70, 70, 70));
//        setConnectionThickness(2);
//        setActiveConnectionColor(new Color(255, 249, 102));
//        setActiveConnectionThickness(2);
//
//        // ********* new style options (now is pre 0.3.8.11) *********
//
//        getBaseValues().put(Connector.INACTIVE_COLOR_ERROR_KEY,
//                getObjectUpperBackground());
//        getBaseValues().put(Connector.INACTIVE_COLOR_VALID_KEY,
//                getObjectUpperBackground());
//    }
//}
