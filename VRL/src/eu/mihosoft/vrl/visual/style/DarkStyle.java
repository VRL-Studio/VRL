///*
// * DarkStyle.java
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
//import eu.mihosoft.vrl.visual.SelectionRectangle;
//import java.awt.Color;
//
///**
// * Canvas style with dark color set.
// * @author Michael Hoffer <info@michaelhoffer.de>
// */
//public class DarkStyle extends CanvasStyle {
//
//    private static final long serialVersionUID = 7831760847824097355L;
//
//    /**
//     * Constructor.
//     */
//    public DarkStyle() {
//        //dark style
//        setCanvasBackground(new Color(30, 30, 30));
//        setGridColor(new Color(50, 50, 50));
//        this.setTextColor(new Color(160, 160, 160));
//        this.setWindowIconColor(new Color(180, 180, 180));
//
//        this.setMessageBoxColor(new Color(40, 45, 60));
//
//        setObjectUpperBackground(new Color(85, 85, 85));
//        setObjectLowerBackground(new Color(35, 35, 35));
//
//        this.setObjectUpperTitleColor(new Color(55, 55, 55));
//        this.setObjectLowerTitleColor(new Color(35, 35, 35));
//
//        this.setObjectUpperActiveTitleColor(new Color(35, 55, 75));
//        this.setObjectLowerActiveTitleColor(new Color(45, 35, 45));
//
//        this.setMethodUpperTitleColor(new Color(75, 75, 75));
//        this.setMethodLowerTitleColor(new Color(40, 40, 40));
//
//        setObjectTransparency(0.65f);
//        setObjectBorderColor(new Color(100, 100, 100));
//        setObjectBorderThickness(1);
//
////        setInputColor(getObjectUpperBackground());
////        setOutputColor(getObjectUpperBackground());
////        setActiveInputColor(Color.GREEN);
////        setActiveOutputColor(Color.RED);
//
//        setConnectionColor(new Color(70, 70, 70));
//        setConnectionThickness(2);
//        setActiveConnectionColor(new Color(47, 110, 47));
//        setActiveConnectionThickness(3);
//
//        setInputFieldColor(new Color(80, 80, 80));
//
//        setTextSelectionColor(new Color(130, 145, 180, 60));
//
//        setLineNumberColor(new Color(160, 160, 160, 160));
//        setLineNumberFieldColor(new Color(160, 160, 160, 20));
//
//        setCompileErrorColor(new Color(255, 0, 0, 60));
//        setCompileErrorBorderColor(new Color(100, 100, 100, 160));
//
//        // ********* new style options (now is pre 0.3.8.11) *********
//
////        getBaseValues().put("LookAndFeel",
////                "javax.swing.plaf.nimbus.NimbusLookAndFeel");
//
//        // Nimbus related
//        getLookAndFeelValues().put("nimbusBase", new Color(24, 24, 24));
//        getLookAndFeelValues().put("nimbusBlueGrey", new Color(24, 24, 24));
//        getLookAndFeelValues().put("control", new Color(40, 40, 40));
//        getLookAndFeelValues().put("text", getTextColor());
//        getLookAndFeelValues().put("menuText", getTextColor());
//        getLookAndFeelValues().put("infoText", getTextColor());
//        getLookAndFeelValues().put("controlText", getTextColor());
//        getLookAndFeelValues().put("nimbusSelectedText",
//                getSelectedTextColor());
//        getLookAndFeelValues().put("nimbusLightBackground",
//                new Color(40, 40, 40));
//
//
//        getBaseValues().put(Connector.INACTIVE_COLOR_ERROR_KEY,
//                getObjectUpperBackground());
//        getBaseValues().put(Connector.INACTIVE_COLOR_VALID_KEY,
//                getObjectUpperBackground());
//
//
//        getBaseValues().put(
//                SelectionRectangle.FILL_COLOR_KEY, new Color(80,80,80,120));
//        getBaseValues().put(
//                SelectionRectangle.BORDER_COLOR_KEY, new Color(120,120,120,160));
//    }
//}
