///*
// * CanvasStyle.java
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
//import eu.mihosoft.vrl.reflection.ControlFlowConnector;
//import eu.mihosoft.vrl.visual.CanvasGrid;
//import eu.mihosoft.vrl.visual.StyleValues;
//import eu.mihosoft.vrl.visual.CanvasWindow;
//import eu.mihosoft.vrl.visual.Connector;
//import eu.mihosoft.vrl.visual.Dock;
//import eu.mihosoft.vrl.visual.FullScreenIconShadowBorder;
//import eu.mihosoft.vrl.visual.MessageBox;
//import eu.mihosoft.vrl.visual.SelectionRectangle;
//import eu.mihosoft.vrl.visual.ShadowBorder;
//import java.awt.Color;
//
//import java.io.Serializable;
//
///**
// * Defines a canvas style, e.g. color, thickness of visual components.
// *
// * @author Michael Hoffer <info@michaelhoffer.de>
// */
//public class CanvasStyle implements Serializable {
//
//    private Color canvasBackground;
//    private Color gridColor;
//    private Color objectUpperBackground;
//    private Color objectLowerBackground;
//    private Color objectBorderColor;
//    private Color objectUpperTitleColor;
//    private Color objectLowerTitleColor;
//    private Color objectUpperActiveTitleColor;
//    private Color objectLowerActiveTitleColor;
//    private int objectShadowWidth;
//    private float objectShadowTransparency;
//    private Color textColor;
//    private Color activeTextColor;
//    private Color windowIconColor;
//    private Color activeWindowIconColor;
//    private int objectBorderThickness;
//    private Float objectTransparency;
//    private Color inputColor;
//    private Color outputColor;
//    private Color activeInputColor;
//    private Color activeOutputColor;
//    private Color activeConnectionColor;
//    private Color connectionColor;
//    private int connectionThickness;
//    private int activeConnectionThickness;
//    private Color messageBoxColor;
//    private Color messageBoxTextColor;
//    private Color messageBoxIconColor;
//    private Color activeMessageBoxIconColor;
//    private Color methodUpperTitleColor;
//    private Color methodLowerTitleColor;
//    private Color methodUpperActiveTitleColor;
//    private Color methodLowerActiveTitleColor;
//    private Float methodTitleTransparency;
//    private Color InputFieldColor;
//    private Color vCanvas3DUpperColor;
//    private Color vCanvas3DLowerColor;
//    private Color imageViewUpperColor;
//    private Color imageViewLowerColor;
//    private Float vCanvas3DContentTransparency;
//    private Color selectedTextColor;
//    private Color textSelectionColor;
//    private Color caretColor;
//    private Color lineNumberColor;
//    private Color codeKeyWordColor;
//    private Color lineNumberFieldColor;
//    private Color compileErrorColor;
//    private Color compileErrorBorderColor;
//    private String backgroundImageData;
//    private float backgroundImageTransparency;
//    // these maps are to replace all other values in the future
//    // (now is pre 0.3.8.11)
//    private StyleValues baseValues;
//    private StyleValues lookAndFeelValues;
//    private StyleValues customValues;
//    public static final String NAME_KEY = "canvas-style";
//
//    /**
//     * Constructor.
//     */
//    public CanvasStyle() {
//        //standard style
//        setCanvasBackground(new Color(98, 107, 129));
//        setGridColor(new Color(154, 158, 166));
//        this.setTextColor(new Color(0, 0, 0));
//        this.setActiveTextColor(Color.GRAY);
//        this.setWindowIconColor(Color.BLACK);
//        this.setActiveWindowIconColor(Color.RED);
//        this.setMessageBoxColor(new Color(0, 0, 0));
//        this.setMessageBoxTextColor(new Color(255, 255, 255));
//        this.setMessageBoxIconColor(new Color(200, 200, 200));
//        this.setActiveMessageBoxIconColor(new Color(255, 255, 255));
//
//        setObjectUpperBackground(new Color(210, 227, 228));
//        setObjectLowerBackground(new Color(130, 137, 159));
//        this.setObjectUpperTitleColor(new Color(84, 255, 200));
//        this.setObjectLowerTitleColor(new Color(102, 255, 0));
//
//        setObjectTransparency(0.65f);
//        setObjectBorderColor(Color.BLACK);
//        setObjectBorderThickness(1);
//        this.setObjectShadowWidth(15);
//        this.setObjectShadowTransparency(0.4f);
//
////        setInputColor(getObjectUpperBackground());
////        setOutputColor(getObjectUpperBackground());
////        setActiveInputColor(Color.GREEN);
////        setActiveOutputColor(Color.GREEN);
//
//        setConnectionColor(new Color(70, 70, 70));
//        setConnectionThickness(2);
//        setActiveConnectionColor(new Color(255, 249, 102));
//        setActiveConnectionThickness(3);
//
//        this.setObjectUpperTitleColor(new Color(94, 245, 220));
//        this.setObjectLowerTitleColor(new Color(102, 255, 0));
//
//        this.setObjectUpperActiveTitleColor(new Color(171, 227, 250));
//        this.setObjectLowerActiveTitleColor(new Color(115, 145, 220));
//
//        setMethodTitleTransparency(0.2f);
//
//        this.setMethodUpperTitleColor(new Color(70, 75, 70));
//        this.setMethodLowerTitleColor(new Color(70, 75, 70));
//
//        this.setMethodUpperActiveTitleColor(new Color(171, 227, 250));
//        this.setMethodLowerActiveTitleColor(new Color(115, 145, 220));
//
//        this.setInputFieldColor(new Color(240, 240, 240));
//
//        this.setVCanvas3DUpperColor(new Color(34, 30, 38, 180));
//        this.setVCanvas3DLowerColor(new Color(74, 68, 78, 80));
//        this.setVCanvas3DContentTransparency(1.f);
//
//        this.setImageViewUpperColor(new Color(34, 30, 38, 180));
//        this.setImageViewLowerColor(new Color(74, 68, 78, 80));
//
//        setCaretColor(new Color(51, 51, 51, 51));
//        setLineNumberColor(new Color(50, 50, 50, 200));
//        setLineNumberFieldColor(new Color(0, 0, 0, 80));
//        setSelectedTextColor(getTextColor());
//        setTextSelectionColor(new Color(130, 140, 165, 140));
//
//        setCompileErrorColor(new Color(255, 0, 0, 120));
//        setCompileErrorBorderColor(new Color(100, 100, 100, 200));
//
//        setBackgroundImageTransparency(0.f);
//        setBackgroundImageData(null);
//
//        // ********* new style options (now is pre 0.3.8.11) *********
//
//        setBaseValues(new StyleValues());
//        setLookAndFeelValues(new StyleValues());
//        setCustomValues(new StyleValues());
//
//        getBaseValues().put("LookAndFeel", null);
//
//        // WINDOWS
//        getBaseValues().put(CanvasWindow.FLIP_DURATION_KEY, 0.5);
//
//        getBaseValues().put(CanvasGrid.ENABLE_GRID_KEY, true);
//
//        // DATA CONNECTOR
//        getBaseValues().put(Connector.ACTIVE_COLOR_ERROR_KEY, Color.RED);
//        getBaseValues().put(Connector.ACTIVE_COLOR_VALID_KEY, Color.GREEN);
//        getBaseValues().put(Connector.INACTIVE_COLOR_ERROR_KEY,
//                getObjectUpperBackground());
//        getBaseValues().put(Connector.INACTIVE_COLOR_VALID_KEY,
//                getObjectUpperBackground());
//        getBaseValues().put(Connector.CONNECTOR_SIZE_KEY, new Integer(9));
//        getBaseValues().put(Connector.CONNECTOR_BORDER_THICKNESS_KEY, new Float(1));
//
//        getBaseValues().put(ShadowBorder.SHADOW_COLOR_KEY, Color.BLACK);
//
//        getBaseValues().put(FullScreenIconShadowBorder.getCustomShadowTransparencyKey(), 0.8f);
//        getBaseValues().put(FullScreenIconShadowBorder.getCustomShadowWidthKey(), 8);
//        //getBaseValues().put(FullScreenIconShadowBorder.getCustomShadowColorKey(), Color.B);
//
//        getBaseValues().put(Dock.DOCK_HEIGHT_KEY, 40);
//
//        getBaseValues().put(MessageBox.MAX_HEIGHT_KEY, 250);
//        getBaseValues().put(MessageBox.OPEN_DURATION_KEY, 15.0);
//        getBaseValues().put(MessageBox.SHOW_ANIM_DURATION_KEY, 0.3);
//        getBaseValues().put(MessageBox.HIDE_ANIM_DURATION_KEY, 0.3);
//
//        getBaseValues().put(SelectionRectangle.FILL_COLOR_KEY, new Color(0, 0, 0, 120));
//        getBaseValues().put(SelectionRectangle.BORDER_COLOR_KEY, new Color(255, 255, 255, 160));
//        getBaseValues().put(SelectionRectangle.CUSTOM_STROKE_KEY, false);
//        getBaseValues().put(SelectionRectangle.STROKE_DASH_KEY, new float[]{5});
//        getBaseValues().put(SelectionRectangle.STROKE_WIDTH_KEY, 1.0f);
//
//
////        getBaseValues().put("LookAndFeel",
////                "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
//
////        getBaseValues().put("LookAndFeel",
////                "javax.swing.plaf.metal.MetalLookAndFeel");
//
////                getBaseValues().put("LookAndFeel",
////                "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
//
////                        getBaseValues().put("LookAndFeel",
////                "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//    }
//
//    /**
//     * Returns the canvas background color.
//     * @return the canvas background color
//     */
//    public Color getCanvasBackground() {
//        return canvasBackground;
//    }
//
//    /**
//     * Defines the canvas background color.
//     * @param canvasBackground the canvas background color
//     */
//    public void setCanvasBackground(Color canvasBackground) {
//        this.canvasBackground = canvasBackground;
//    }
//
//    /**
//     * Returns the upper background color of canvas windows.
//     * @return the upper background color of canvas objects
//     */
//    public Color getObjectUpperBackground() {
//        return objectUpperBackground;
//    }
//
//    /**
//     * Defines the upper background color of canvas windows.
//     * @param objectUpperBackground the upper background color of canvas objects
//     */
//    public void setObjectUpperBackground(Color objectUpperBackground) {
//        this.objectUpperBackground = objectUpperBackground;
//    }
//
//    /**
//     * Returns the lower background of canvas windows.
//     * @return the lower background of canvas objects
//     */
//    public Color getObjectLowerBackground() {
//        return objectLowerBackground;
//    }
//
//    /**
//     * Defines the lower background color of canvas windows.
//     *
//     * @param objectLowerBackground the lower background color of canvas objects
//     */
//    public void setObjectLowerBackground(Color objectLowerBackground) {
//        this.objectLowerBackground = objectLowerBackground;
//    }
//
//    /**
//     * Returns the canvas window border color.
//     * @return the canvas window border color
//     */
//    public Color getObjectBorderColor() {
//        return objectBorderColor;
//    }
//
//    /**
//     * Defines the canvas window border color.
//     * @param objectBorderColor the canvas window border color
//     */
//    public void setObjectBorderColor(Color objectBorderColor) {
//        this.objectBorderColor = objectBorderColor;
//    }
//
////    public Color getActiveInputColor() {
////        return (Color) getBaseValues().get("Connector[active]:validColor");
////    }
////    public void setActiveInputColor(Color activeInputColor) {
////        this.activeInputColor = activeInputColor;
////    }
////    public Color getActiveOutputColor() {
////        return (Color) getBaseValues().get("Connector[active]:validColor");
////    }
////    public void setActiveOutputColor(Color activeOutputColor) {
////        this.activeOutputColor = activeOutputColor;
////    }
//    public int getObjectBorderThickness() {
//        return objectBorderThickness;
//    }
//
//    public void setObjectBorderThickness(int objectBorderThickness) {
//        this.objectBorderThickness = objectBorderThickness;
//    }
//
//    public Color getActiveConnectionColor() {
//        return activeConnectionColor;
//    }
//
//    public void setActiveConnectionColor(Color activeConnectionColor) {
//        this.activeConnectionColor = activeConnectionColor;
//    }
//
//    public Color getConnectionColor() {
//        return connectionColor;
//    }
//
//    public void setConnectionColor(Color connectionColor) {
//        this.connectionColor = connectionColor;
//    }
//
//    public int getConnectionThickness() {
//        return connectionThickness;
//    }
//
//    public void setConnectionThickness(int connectionThickness) {
//        this.connectionThickness = connectionThickness;
//    }
//
//    public int getActiveConnectionThickness() {
//        return activeConnectionThickness;
//    }
//
//    public void setActiveConnectionThickness(int activeConnectionThickness) {
//        this.activeConnectionThickness = activeConnectionThickness;
//    }
//
////    public Color getInputColor() {
////        return inputColor;
////    }
////
////    public void setInputColor(Color inputColor) {
////        this.inputColor = inputColor;
////    }
////
////    public Color getOutputColor() {
////        return outputColor;
////    }
////
////    public void setOutputColor(Color outputColor) {
////        this.outputColor = outputColor;
////    }
//    public Float getObjectTransparency() {
//        return objectTransparency;
//    }
//
//    public void setObjectTransparency(Float objectTransparency) {
//        this.objectTransparency = objectTransparency;
//    }
//
//    public Color getObjectUpperTitleColor() {
//        return objectUpperTitleColor;
//    }
//
//    public void setObjectUpperTitleColor(Color objectUpperTitleColor) {
//        this.objectUpperTitleColor = objectUpperTitleColor;
//    }
//
//    public Color getObjectLowerTitleColor() {
//        return objectLowerTitleColor;
//    }
//
//    public void setObjectLowerTitleColor(Color objectLowerTitleColor) {
//        this.objectLowerTitleColor = objectLowerTitleColor;
//    }
//
//    public Color getGridColor() {
//        return gridColor;
//    }
//
//    public void setGridColor(Color gridColor) {
//        this.gridColor = gridColor;
//    }
//
//    public Color getTextColor() {
//        return textColor;
//    }
//
//    public void setTextColor(Color textColor) {
//        this.textColor = textColor;
//    }
//
//    public int getObjectShadowWidth() {
//        return objectShadowWidth;
//    }
//
//    public void setObjectShadowWidth(int objectShadowWidth) {
//        this.objectShadowWidth = objectShadowWidth;
//    }
//
//    public float getObjectShadowTransparency() {
//        return objectShadowTransparency;
//    }
//
//    public void setObjectShadowTransparency(float objectShadowTransparency) {
//        this.objectShadowTransparency = objectShadowTransparency;
//    }
//
//    public Color getObjectUpperActiveTitleColor() {
//        return objectUpperActiveTitleColor;
//    }
//
//    public void setObjectUpperActiveTitleColor(Color objectUpperActiveTitleColor) {
//        this.objectUpperActiveTitleColor = objectUpperActiveTitleColor;
//    }
//
//    public Color getObjectLowerActiveTitleColor() {
//        return objectLowerActiveTitleColor;
//    }
//
//    public void setObjectLowerActiveTitleColor(Color objectLowerActiveTitleColor) {
//        this.objectLowerActiveTitleColor = objectLowerActiveTitleColor;
//    }
//
//    public Color getMessageBoxColor() {
//        return messageBoxColor;
//    }
//
//    public void setMessageBoxColor(Color messageBoxColor) {
//        this.messageBoxColor = messageBoxColor;
//    }
//
//    public Color getMessageBoxTextColor() {
//        return messageBoxTextColor;
//    }
//
//    public void setMessageBoxTextColor(Color messageBoxTextColor) {
//        this.messageBoxTextColor = messageBoxTextColor;
//    }
//
//    public Color getActiveTextColor() {
//        return activeTextColor;
//    }
//
//    public void setActiveTextColor(Color activeTextColor) {
//        this.activeTextColor = activeTextColor;
//    }
//
//    public Color getWindowIconColor() {
//        return windowIconColor;
//    }
//
//    public void setWindowIconColor(Color windowIconColor) {
//        this.windowIconColor = windowIconColor;
//    }
//
//    public Color getActiveWindowIconColor() {
//        return activeWindowIconColor;
//    }
//
//    public void setActiveWindowIconColor(Color activeWindowIconColor) {
//        this.activeWindowIconColor = activeWindowIconColor;
//    }
//
//    public Color getMessageBoxIconColor() {
//        return messageBoxIconColor;
//    }
//
//    public void setMessageBoxIconColor(Color messageBoxIconColor) {
//        this.messageBoxIconColor = messageBoxIconColor;
//    }
//
//    public Color getActiveMessageBoxIconColor() {
//        return activeMessageBoxIconColor;
//    }
//
//    public void setActiveMessageBoxIconColor(Color activeMessageBoxIconColor) {
//        this.activeMessageBoxIconColor = activeMessageBoxIconColor;
//    }
//
//    public Color getMethodUpperTitleColor() {
//        return methodUpperTitleColor;
//    }
//
//    public void setMethodUpperTitleColor(Color methodUpperTitleColor) {
//        this.methodUpperTitleColor = methodUpperTitleColor;
//    }
//
//    public Color getMethodLowerTitleColor() {
//        return methodLowerTitleColor;
//    }
//
//    public void setMethodLowerTitleColor(Color methodLowerTitleColor) {
//        this.methodLowerTitleColor = methodLowerTitleColor;
//    }
//
//    public Color getMethodUpperActiveTitleColor() {
//        return methodUpperActiveTitleColor;
//    }
//
//    public void setMethodUpperActiveTitleColor(Color methodUpperActiveTitleColor) {
//        this.methodUpperActiveTitleColor = methodUpperActiveTitleColor;
//    }
//
//    public Color getMethodLowerActiveTitleColor() {
//        return methodLowerActiveTitleColor;
//    }
//
//    public void setMethodLowerActiveTitleColor(Color methodLowerActiveTitleColor) {
//        this.methodLowerActiveTitleColor = methodLowerActiveTitleColor;
//    }
//
//    public Float getMethodTitleTransparency() {
//        return methodTitleTransparency;
//    }
//
//    public void setMethodTitleTransparency(Float methodTitleTransparency) {
//        this.methodTitleTransparency = methodTitleTransparency;
//    }
//
//    public Color getInputFieldColor() {
//        return InputFieldColor;
//    }
//
//    public void setInputFieldColor(Color InputFieldColor) {
//        this.InputFieldColor = InputFieldColor;
//    }
//
//    /**
//     * @return the vCanvas3DUpperColor
//     */
//    public Color getVCanvas3DUpperColor() {
//        return vCanvas3DUpperColor;
//    }
//
//    /**
//     * @param vCanvas3DUpperColor the vCanvas3DUpperColor to set
//     */
//    public void setVCanvas3DUpperColor(Color vCanvas3DUpperColor) {
//        this.vCanvas3DUpperColor = vCanvas3DUpperColor;
//    }
//
//    /**
//     * @return the vCanvas3DLowerColor
//     */
//    public Color getVCanvas3DLowerColor() {
//        return vCanvas3DLowerColor;
//    }
//
//    /**
//     * @param vCanvas3DLowerColor the vCanvas3DLowerColor to set
//     */
//    public void setVCanvas3DLowerColor(Color vCanvas3DLowerColor) {
//        this.vCanvas3DLowerColor = vCanvas3DLowerColor;
//    }
//
//    /**
//     * @return the vCanvas3DContentTransparency
//     */
//    public Float getVCanvas3DContentTransparency() {
//        return vCanvas3DContentTransparency;
//    }
//
//    /**
//     * @param vCanvas3DContentTransparency the vCanvas3DContentTransparency to set
//     */
//    public void setVCanvas3DContentTransparency(Float vCanvas3DContentTransparency) {
//        this.vCanvas3DContentTransparency = vCanvas3DContentTransparency;
//    }
//
//    /**
//     * @return the caretColor
//     */
//    public Color getCaretColor() {
//        return caretColor;
//    }
//
//    /**
//     * @param caretColor the caretColor to set
//     */
//    public void setCaretColor(Color caretColor) {
//        this.caretColor = caretColor;
//    }
//
//    /**
//     * @return the lineNumberColor
//     */
//    public Color getLineNumberColor() {
//        return lineNumberColor;
//    }
//
//    /**
//     * @param lineNumberColor the lineNumberColor to set
//     */
//    public void setLineNumberColor(Color lineNumberColor) {
//        this.lineNumberColor = lineNumberColor;
//    }
//
//    /**
//     * @return the codeKeyWordColor
//     */
//    public Color getCodeKeyWordColor() {
//        return codeKeyWordColor;
//    }
//
//    /**
//     * @param codeKeyWordColor the codeKeyWordColor to set
//     */
//    public void setCodeKeyWordColor(Color codeKeyWordColor) {
//        this.codeKeyWordColor = codeKeyWordColor;
//    }
//
//    /**
//     * @return the selectedTextColor
//     */
//    public Color getSelectedTextColor() {
//        return selectedTextColor;
//    }
//
//    /**
//     * @param selectedTextColor the selectedTextColor to set
//     */
//    public void setSelectedTextColor(Color selectedTextColor) {
//        this.selectedTextColor = selectedTextColor;
//    }
//
//    /**
//     * @return the textSelectionColor
//     */
//    public Color getTextSelectionColor() {
//        return textSelectionColor;
//    }
//
//    /**
//     * @param textSelectionColor the textSelectionColor to set
//     */
//    public void setTextSelectionColor(Color textSelectionColor) {
//        this.textSelectionColor = textSelectionColor;
//    }
//
//    /**
//     * @return the lineNumberFieldColor
//     */
//    public Color getLineNumberFieldColor() {
//        return lineNumberFieldColor;
//    }
//
//    /**
//     * @param lineNumberFieldColor the lineNumberFieldColor to set
//     */
//    public void setLineNumberFieldColor(Color lineNumberFieldColor) {
//        this.lineNumberFieldColor = lineNumberFieldColor;
//    }
//
//    /**
//     * @return the compileErrorColor
//     */
//    public Color getCompileErrorColor() {
//        return compileErrorColor;
//    }
//
//    /**
//     * @param compileErrorColor the compileErrorColor to set
//     */
//    public void setCompileErrorColor(Color compileErrorColor) {
//        this.compileErrorColor = compileErrorColor;
//    }
//
//    /**
//     * @return the compileErrorBorderColor
//     */
//    public Color getCompileErrorBorderColor() {
//        return compileErrorBorderColor;
//    }
//
//    /**
//     * @param compileErrorBorderColor the compileErrorBorderColor to set
//     */
//    public void setCompileErrorBorderColor(Color compileErrorBorderColor) {
//        this.compileErrorBorderColor = compileErrorBorderColor;
//    }
//
//    /**
//     * @return the imageViewUpperColor
//     */
//    public Color getImageViewUpperColor() {
//        return imageViewUpperColor;
//    }
//
//    /**
//     * @param imageViewUpperColor the imageViewUpperColor to set
//     */
//    public void setImageViewUpperColor(Color imageViewUpperColor) {
//        this.imageViewUpperColor = imageViewUpperColor;
//    }
//
//    /**
//     * @return the imageViewLowerColor
//     */
//    public Color getImageViewLowerColor() {
//        return imageViewLowerColor;
//    }
//
//    /**
//     * @param imageViewLowerColor the imageViewLowerColor to set
//     */
//    public void setImageViewLowerColor(Color imageViewLowerColor) {
//        this.imageViewLowerColor = imageViewLowerColor;
//    }
//
//    /**
//     * @return the backgroundImageData
//     */
//    public String getBackgroundImageData() {
//        return backgroundImageData;
//    }
//
//    /**
//     * @param backgroundImageData the backgroundImageData to set
//     */
//    public void setBackgroundImageData(String backgroundImageData) {
//        this.backgroundImageData = backgroundImageData;
//    }
//
//    /**
//     * @return the backgroundImageTransparency
//     */
//    public float getBackgroundImageTransparency() {
//        return backgroundImageTransparency;
//    }
//
//    /**
//     * @param backgroundImageTransparency the backgroundImageTransparency to set
//     */
//    public void setBackgroundImageTransparency(
//            float backgroundImageTransparency) {
//        this.backgroundImageTransparency = backgroundImageTransparency;
//    }
//
//    /**
//     * Returns the VRL base style values, used by VRL specific components.
//     * @return the VRL base style values, used by VRL specific components
//     */
//    public StyleValues getBaseValues() {
//        return baseValues;
//    }
//
//    /**
//     * Defines the VRL base style values, used by VRL specific components.
//     * @param baseValues the values to set
//     */
//    public void setBaseValues(StyleValues baseValues) {
//        this.baseValues = baseValues;
//    }
//
//    /**
//     * Returns the custom style values.
//     * @return the custom style values
//     */
//    public StyleValues getCustomValues() {
//        return customValues;
//    }
//
//    /**
//     * Defines the custom style values.
//     * @param customValues the values to set
//     */
//    public void setCustomValues(StyleValues customValues) {
//        this.customValues = customValues;
//    }
//
//    /**
//     * Returns the LookAndFeel values.
//     * @see javax.swing.LookAndFeel
//     * @return the LookAndFeel values
//     */
//    public StyleValues getLookAndFeelValues() {
//        return lookAndFeelValues;
//    }
//
//    /**
//     * Defines the LookAndFeel values.
//     * @param lookAndFeelValues the values to set
//     * @see javax.swing.LookAndFeel
//     */
//    public void setLookAndFeelValues(
//            StyleValues lookAndFeelValues) {
//        this.lookAndFeelValues = lookAndFeelValues;
//    }
//}
