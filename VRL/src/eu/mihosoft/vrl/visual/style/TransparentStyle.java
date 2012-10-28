///*
// * TransparentStyle.java
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
//import eu.mihosoft.vrl.visual.CanvasGrid;
//import eu.mihosoft.vrl.visual.Connector;
//import java.awt.Color;
//
//
///**
// * Defines a canvas style, e.g. color, thickness of visual components.
// *
// * @author Michael Hoffer <info@michaelhoffer.de>
// */
//public class TransparentStyle extends CanvasStyle {
//
//    private static Color TRANSPARENT = VSwingUtil.TRANSPARENT_COLOR;
//
//    /**
//     * Constructor.
//     */
//    public TransparentStyle() {
////	transparent style
//
////        setGridColor(TRANSPARENT); // inefficient
//
//        this.setCanvasBackground(new Color(50,50,50));
//        this.setTextColor(Color.white);
//        this.setGridColor(new Color(60,60,60));
//
//        this.setWindowIconColor(TRANSPARENT);
//
//        setObjectUpperBackground(TRANSPARENT);
//        setObjectLowerBackground(TRANSPARENT);
//        this.setObjectUpperTitleColor(TRANSPARENT);
//        this.setObjectLowerTitleColor(TRANSPARENT);
//
//        setObjectBorderColor(TRANSPARENT);
//
////	setInputColor(TRANSPARENT);
////	setOutputColor(TRANSPARENT);
//
//        setConnectionColor(Color.white);
//
//        this.setObjectUpperTitleColor(TRANSPARENT);
//        this.setObjectLowerTitleColor(TRANSPARENT);
//
//        this.setObjectUpperActiveTitleColor(TRANSPARENT);
//        this.setObjectLowerActiveTitleColor(TRANSPARENT);
//
//        this.setMethodUpperTitleColor(TRANSPARENT);
//        this.setMethodLowerTitleColor(TRANSPARENT);
//
//        this.setMethodUpperActiveTitleColor(TRANSPARENT);
//        this.setMethodLowerActiveTitleColor(TRANSPARENT);
//
//        this.setInputFieldColor(TRANSPARENT);
//
//        this.setVCanvas3DUpperColor(TRANSPARENT);
//        this.setVCanvas3DLowerColor(TRANSPARENT);
//
//
//        this.setImageViewUpperColor(TRANSPARENT);
//        this.setImageViewLowerColor(TRANSPARENT);
//
//        setLineNumberFieldColor(TRANSPARENT);
//
//        // ********* new style options (now is pre 0.3.8.11) *********
//
//        getBaseValues().put(Connector.INACTIVE_COLOR_ERROR_KEY, TRANSPARENT);
//        getBaseValues().put(Connector.INACTIVE_COLOR_VALID_KEY, TRANSPARENT);
//
//        getBaseValues().put(CanvasGrid.ENABLE_GRID_KEY, false);
//    }
//}
