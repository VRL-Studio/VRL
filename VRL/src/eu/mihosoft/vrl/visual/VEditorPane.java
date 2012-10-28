/*
 * VEditorPane.java
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2009 Michael Hoffer <info@michaelhoffer.de>
 *
 * Supported by the Goethe Center for Scientific Computing of Prof. Wittum
 * (http://gcsc.uni-frankfurt.de)
 *
 * This file is part of Visual Reflection Library (VRL).
 *
 * VRL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * VRL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
//
//package eu.mihosoft.vrl.visual;
//
//import java.awt.AlphaComposite;
//import java.awt.BasicStroke;
//import java.awt.Color;
//import java.awt.Composite;
//import java.awt.FontMetrics;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.Rectangle;
//import java.awt.RenderingHints;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.geom.Rectangle2D;
//import javax.swing.JEditorPane;
//import javax.swing.border.EmptyBorder;
//import javax.swing.text.AbstractDocument;
//import javax.swing.text.BoxView;
//import javax.swing.text.ComponentView;
//import javax.swing.text.DefaultEditorKit;
//import javax.swing.text.Element;
//import javax.swing.text.IconView;
//import javax.swing.text.LabelView;
//import javax.swing.text.MutableAttributeSet;
//import javax.swing.text.ParagraphView;
//import javax.swing.text.SimpleAttributeSet;
//import javax.swing.text.StyleConstants;
//import javax.swing.text.StyledEditorKit;
//import javax.swing.text.StyledEditorKit.BoldAction;
//import javax.swing.text.View;
//import javax.swing.text.ViewFactory;
//
///**
// *
// * @author Michael Hoffer <info@michaelhoffer.de>
// */
//public class VEditorPane extends JEditorPane {
//    private static final long serialVersionUID = 4979257751840292588L;
//
//    private Canvas mainCanvas;
//
//    public VEditorPane(Canvas mainCanvas) {
//        this.mainCanvas = mainCanvas;
//
//        this.mainCanvas = mainCanvas;
//        setBorder(new EmptyBorder(3, 3, 3, 3));
////        this.setEditorKit(new VCodeEditorKit());
//
////        ActionListener boldAction = new BoldAction();
//////        boldAction.actionPerformed(new ActionEvent(this, 0, ""));
////
////        ActionListener fontAction = new StyledEditorKit.FontFamilyAction("", "SansSerif");
////        fontAction.actionPerformed(new ActionEvent(this, 0, ""));
////
////        ActionListener fontSizeAction = new StyledEditorKit.FontSizeAction("", 12);
////        fontSizeAction.actionPerformed(new ActionEvent(this, 0, ""));
//    }
//
//    @Override
//    public void setText(String text){
//        super.setText(text);
//    }
//
//    @Override
//    protected void paintComponent(Graphics g) {
//        Graphics2D g2 = (Graphics2D) g;
//
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);
//
//
//        CanvasStyle style = mainCanvas.getStyle();
//
////        Color originalBackground = getBackground();
//
//        setForeground(style.getTextColor());
//
//        g2.setPaint(style.getInputFieldColor());
//
//        Composite original = g2.getComposite();
//
//        AlphaComposite ac1 =
//                AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
//                0.4f);
//        g2.setComposite(ac1);
//
//        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
//
//        g2.setComposite(original);
//
//        Color border =
//                style.getObjectBorderColor();
//
//        g2.setColor(border);
//
//        BasicStroke stroke =
//                new BasicStroke(style.getObjectBorderThickness());
//
//        g2.setStroke(stroke);
//
//        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
//
//        setBackground(VSwingUtil.TRANSPARENT_COLOR);
//
//        g2.setComposite(original);
//
//        super.paintComponent(g);
//    }
//}
//
////class VCodeEditorKit extends StyledEditorKit {
////
////    public VCodeEditorKit() {
////    }
////
////    @Override
////    public ViewFactory getViewFactory() {
////        return new CodeViewFactory();
////    }
////}
////
////class CodeViewFactory implements ViewFactory {
////
////    public View create(Element elem) {
////        String kind = elem.getName();
////        if (kind != null) {
////            if (kind.equals(AbstractDocument.ContentElementName)) {
////                return new LabelView(elem);
////            } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
////              return new ParagraphView(elem);
//////                return new CodeParagraphView(elem);
////            } else if (kind.equals(AbstractDocument.SectionElementName)) {
////                return new BoxView(elem, View.Y_AXIS);
////            } else if (kind.equals(StyleConstants.ComponentElementName)) {
////                return new ComponentView(elem);
////            } else if (kind.equals(StyleConstants.IconElementName)) {
////                return new IconView(elem);
////            }
////        }
////        // default to text display
////        return new LabelView(elem);
////    }
////}
////
////class CodeParagraphView extends ParagraphView {
////
////    public static short numberWidth;
////    private short previousWidth;
////
////    public CodeParagraphView(Element e) {
////        super(e);
//////        short top = 0;
//////        short left = 0;
//////        short bottom = 0;
//////        short right = 0;
//////        this.setInsets(top, left, bottom, right);
////        previousWidth = numberWidth;
////    }
////
//////    @Override
//////    protected void setInsets(short top, short left, short bottom,
//////            short right) {
//////        super.setInsets(top, (short) (left + numberWidth),
//////                bottom, right);
//////    }
////    /**
////     * disable line wrap
////     * @param width
////     * @param height
////     */
////    @Override
////    public void layout(int width, int height) {
////        super.layout(Short.MAX_VALUE, height);
////    }
////
////    /**
////     *  disable line wrap
////     * @param axis
////     * @return
////     */
////    @Override
////    public float getMinimumSpan(int axis) {
////        return super.getPreferredSpan(axis);
////    }
////
////    @Override
////    public void paintChild(Graphics g, Rectangle r, int n) {
////        super.paintChild(g, r, n);
////        int previousLineCount = getPreviousLineCount();
////
////        String numberString = Integer.toString(previousLineCount + n + 1) + ":";
////
////        FontMetrics fm = g.getFontMetrics();
////        Rectangle2D area = fm.getStringBounds(numberString, g);
////
////        numberWidth = (short) Math.max(area.getWidth() + 2, numberWidth);
////
////        setInsets((short) 0, numberWidth, (short) 0, (short) 0);
////
////        int xPos = r.x - getLeftInset();
////        // TODO why the correction value?
////        int yPos = r.y + r.height - 3;
////
////        g.setColor(Color.gray);
////
////        g.drawString(numberString, xPos, yPos);
////
////        g.drawRect(r.x, r.y, r.width, r.height);
////    }
////
////
////    @Override
////    public void setParent(View parent){
////        super.setParent(parent);
////
////        // Notifies other views about new size
////        if (parent==null){
////            numberWidth = (short) Math.min(numberWidth, previousWidth);
////        }
////    }
////
////    public int getPreviousLineCount() {
////        int lineCount = 0;
////        View parent = this.getParent();
////        int count = parent.getViewCount();
////        for (int i = 0; i < count; i++) {
////            if (parent.getView(i) == this) {
////                break;
////            } else {
////                lineCount += parent.getView(i).getViewCount();
////            }
////        }
////
////        return lineCount;
////    }
////}
