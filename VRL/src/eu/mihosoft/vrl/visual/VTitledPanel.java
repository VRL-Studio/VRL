//// Error reading included file Templates/Classes/../Licenses/license-gplv3classpath.txt
//package eu.mihosoft.vrl.visual;
//
//import eu.mihosoft.vrl.reflection.DefaultMethodRepresentation;
//import eu.mihosoft.vrl.reflection.MethodTitleBar;
////import eu.mihosoft.vrl.reflection.MethodMouseControl;
//import java.awt.AlphaComposite;
//import java.awt.BasicStroke;
//import java.awt.Color;
//import java.awt.Component;
//import java.awt.Composite;
//import java.awt.Dimension;
//import java.awt.GradientPaint;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.Point;
//import java.awt.RenderingHints;
//import java.awt.Shape;
//import java.awt.event.ActionEvent;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.awt.event.MouseMotionListener;
//import java.awt.geom.RoundRectangle2D;
//import java.awt.image.BufferedImage;
//import javax.swing.Box;
//import javax.swing.JComponent;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.SwingConstants;
//import javax.swing.SwingUtilities;
//
///**
// *
// * @author Michael Hoffer <info@michaelhoffer.de>
// */
//
//
//public class VTitledPanel extends JPanel {
//
//    private CanvasLabel title;
//    transient private BufferedImage buffer;
////        private int width = 0;
////        private int height = 0;
//    private Style style;
//    private Shape shape;
//    private DefaultMethodRepresentation method;
//    private CloseIcon closeIcon;
//
//    VTitledPanel(final DefaultMethodRepresentation method) {
//
//
//        this.method = method;
//
//        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.LINE_AXIS);
//
//        setLayout(layout);
//
//        title = new CanvasLabel(method, "Title");
//        title.setHorizontalAlignment(SwingConstants.CENTER);
//        title.setForeground(method.getParentObject().
//                getMainCanvas().getStyle().getBaseValues().getColor(
//                Canvas.TEXT_COLOR_KEY));
//
//        closeIcon =
//                new CloseIcon(method.getParentObject().getMainCanvas());
//        closeIcon.setActionListener(new CanvasActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//                boolean close = true;
//
//                if (method.askIfClose()) {
//                    close = VDialog.showConfirmDialog(method.getMainCanvas(),
//                            "Close Method?",
//                            "<html><div align=center>"
//                            + "Shall the method be closed?<br><br>"
//                            + "<b>"
//                            + "All unsaved user input of this method will be lost!"
//                            + "<b>"
//                            + "</div></html>", VDialog.DialogType.YES_NO)
//                            == VDialog.YES;
//                }
//
//                closeIcon.deactivate();
//
//                if (close) {
//
//                    method.getEffectManager().
//                            startDisappearanceEffect(
//                            method, 0.5);
//
//                    method.getParentObject().removeMethodFromView(
//                            method);
//                }
//            }
//        });
//
//        int iconSize = 16;
//        int iconBoxSize = 21;
//
//        this.add(Box.createGlue());
//        Component glue = Box.createHorizontalStrut(iconBoxSize);
//        this.add(glue);
//        this.add(title);
//        this.add(Box.createGlue());
//        Box iconBox = Box.createVerticalBox();
//        iconBox.add(Box.createVerticalGlue());
//        closeIcon.setMinimumSize(new Dimension(iconSize, iconSize));
//        closeIcon.setPreferredSize(new Dimension(iconSize, iconSize));
//        closeIcon.setMaximumSize(new Dimension(iconSize, iconSize));
//        iconBox.add(closeIcon);
//        iconBox.add(Box.createVerticalGlue());
//        iconBox.setPreferredSize(new Dimension(iconBoxSize, iconBoxSize));
//        iconBox.setMinimumSize(iconBox.getPreferredSize());
//        this.add(iconBox);
//        this.add(Box.createHorizontalStrut(0));
//
//        this.setMinimumSize(null);
//        this.setPreferredSize(null);
//        this.setMaximumSize(new Dimension(title.getWidth()
//                + iconSize + 5, iconSize + 5));
//        revalidate();
//
//        shape = new RoundRectangle2D.Double(0, 0, getWidth(),
//                getHeight(),
//                DefaultMethodRepresentation.DEFAULT_TITLEBAR_CORNER_ARC_WIDTH,
//                DefaultMethodRepresentation.DEFAULT_TITLEBAR_CORNER_ARC_WIDTH);
//
//        setOpaque(false);
//
//        MethodMouseControl control = new MethodMouseControl(this);
//
//        addMouseListener(control);
//        addMouseMotionListener(control);
//    }
//
//    JLabel getTitle() {
//        return this.title;
//    }
//
//    @Override
//    protected void paintComponent(Graphics g) {
//
//        Style newStyle = getMethod().getStyle();
//
//        if (buffer == null
//                || buffer.getWidth() != getWidth()
//                || buffer.getHeight() != getHeight()
//                || style == null
//                || style != newStyle) {
//
//            style = newStyle;
//
//            buffer = new BufferedImage(getWidth(), getHeight(),
//                    BufferedImage.TYPE_INT_ARGB);
//
//            Graphics2D g2 = buffer.createGraphics();
//
//            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                    RenderingHints.VALUE_ANTIALIAS_ON);
//
//            Composite original = g2.getComposite();
//
//            g2.setComposite(AlphaComposite.Clear);
//
//            shape = new RoundRectangle2D.Double(0, 0, getWidth(),
//                    getHeight(),
//                    DefaultMethodRepresentation.DEFAULT_TITLEBAR_CORNER_ARC_WIDTH,
//                    DefaultMethodRepresentation.DEFAULT_TITLEBAR_CORNER_ARC_WIDTH);
//
//            g2.fill(getShape());
//
//            AlphaComposite ac1 =
//                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
//                    style.getBaseValues().getFloat(
//                    DefaultMethodRepresentation.METHOD_TITLE_TRANSPARENCY_KEY));
//            g2.setComposite(ac1);
//
//            Color upperColor = style.getBaseValues().getColor(
//                    DefaultMethodRepresentation.METHOD_TITLE_UPPER_COLOR_KEY);
//            Color lowerColor = style.getBaseValues().getColor(
//                    DefaultMethodRepresentation.METHOD_TITLE_LOWER_COLOR_KEY);
//
//            // TODO implement active state for DefaultMethodRepresentation
////                if (CanvasObject.this.isActive()) {
////                    upperColor = style.getObjectUpperActiveTitleColor();
////                    lowerColor = style.getObjectLowerActiveTitleColor();
////                }
//
//            GradientPaint paint = new GradientPaint(0, 0,
//                    upperColor,
//                    0, this.getHeight(),
//                    lowerColor,
//                    false);
//
//            g2.setPaint(paint);
//
//            g2.fill(getShape());
//
//            // aqua like button test
////                Shape originalClip = g2.getClip();
//
//            g2.setComposite(original);
//
//            g2.dispose();
//
////                float value = 1.0f / 9;
//
////                float[] BLUR = {
////                    value, value, value,
////                    value, value, value,
////                    value, value, value
////                };
////
////                float[] BLUR = {
////                    0.1f, 0.1f, 0.1f,
////                    0.1f, 0.3f, 0.1f,
////                    0.1f, 0.1f, 0.1f
////                };
//
////                ConvolveOp vBlurOp = new ConvolveOp(new Kernel(3, 3, BLUR));
////                buffer = vBlurOp.filter(buffer, null);
//
//            g2 = buffer.createGraphics();
//
//            //***button-end***
//
//            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                    RenderingHints.VALUE_ANTIALIAS_ON);
//
//            float thickness = 1.f;
//
//            g2.setStroke(new BasicStroke(1));
//            g2.setColor(style.getBaseValues().getColor(
//                    CanvasWindow.BORDER_COLOR_KEY));
//
//            float topLeftOffset = thickness / 2.f;
//            float bottomRightOffset = topLeftOffset * 2;
//
//            g2.draw(new RoundRectangle2D.Double(
//                    topLeftOffset, topLeftOffset,
//                    getWidth() - bottomRightOffset,
//                    getHeight() - bottomRightOffset,
//                    DefaultMethodRepresentation.DEFAULT_TITLEBAR_CORNER_ARC_WIDTH,
//                    DefaultMethodRepresentation.DEFAULT_TITLEBAR_CORNER_ARC_WIDTH));
//
//            g2.dispose();
//        }
//
//        g.drawImage(buffer, 0, 0, null);
//    }
//
//    public void contentChanged() {
//        buffer = null;
//    }
//
//    public Shape getShape() {
//        return shape;
//    }
////        public Insets getInsets(){
////            return new Insets(3,20,3,20);
////        }
//
//    /**
//     * @return the method
//     */
//    public DefaultMethodRepresentation getMethod() {
//        return method;
//    }
//
//    void hideCloseIcon(boolean hideCloseIcon) {
//        closeIcon.setVisible(!hideCloseIcon);
//    }
//} // end class
//
//
//class VTitledPanelMouseControl implements MouseListener, MouseMotionListener {
//
//    private VTitledPanel titleBar;
//    private DefaultMethodRepresentation method;
//    private JComponent methodView;
//    private OrderedBoxLayout layout;
//    private Point initMousePos;
//    private Integer componentIndex = null;
//
//    public MethodMouseControl(MethodTitleBar titleBar) {
//        this.titleBar = titleBar;
//        this.method = titleBar.getMethod();
//        this.methodView = method.getParentObject().getMethodView();
//        this.layout = method.getParentObject().getMethodLayout();
//    }
//
//    @Override
//    public void mouseClicked(MouseEvent e) {
//        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
//            if (!method.isMinimized()) {
//                method.minimize();
//            } else {
//                method.maximize();
//            }
//        }
//    }
//
//    @Override
//    public void mousePressed(MouseEvent e) {
//        initMousePos = e.getPoint();
//
//    }
//
//    @Override
//    public void mouseReleased(MouseEvent e) {
//
//        if (method.isInitializer()) {
//            return;
//        }
//
//        method.setIngoredByLayout(false);
//        method.revalidate();
//
//        method.setSelected(false);
//    }
//
//    @Override
//    public void mouseEntered(MouseEvent e) {
//        //
//    }
//
//    @Override
//    public void mouseExited(MouseEvent e) {
//        //
//    }
//
//    @Override
//    public void mouseDragged(MouseEvent e) {
//
//        if (method.isInitializer()) {
//            return;
//        }
//
//        if (!method.isSelected()) {
//            method.setSelected(true);
//        }
//
//        method.setIngoredByLayout(true);
//
//        final int moveY = (int) (e.getY() - initMousePos.getY());
//        int y = method.getY() + moveY;
//
//        y = Math.max(y, 0);
//        y = Math.min(y, methodView.getHeight());
//
//        method.setLocation(titleBar.getMethod().getX(), y);
//
//        componentIndex = null;
//
//        for (int i = 0; i < methodView.getComponentCount(); i++) {
//            Component c = methodView.getComponent(i);
//
//            int intersectionHeight = 0;
//
//            if (method.getHeight() < c.getHeight()) {
//                intersectionHeight = (method.getHeight() - c.getHeight());
//            }
//
//            if (c.getY() < method.getY()) {
//                intersectionHeight *= -1;
//            }
//
//            if (c != method && c.contains(1,
//                    intersectionHeight
//                    + method.getY() - c.getY())) {
//                componentIndex = i;
//                break;
//            }
//        }
//
//        if (componentIndex != null) {
//            layout.moveTo(method, componentIndex);
//        }
//    }
//
//    @Override
//    public void mouseMoved(MouseEvent e) {
//        //
//    }
//}
