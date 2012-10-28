//package eu.mihosoft.vrl.visual;
//
//import eu.mihosoft.vrl.reflection.ComponentTree;
//import eu.mihosoft.vrl.system.PluginConfigurator;
//import eu.mihosoft.vrl.system.VRL;
//import java.awt.AlphaComposite;
//import java.awt.BasicStroke;
//import java.awt.Color;
//import java.awt.Composite;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.Image;
//import java.awt.RenderingHints;
//import java.awt.image.BufferedImage;
//import javax.swing.ImageIcon;
//
///**
// *
// * @author Michael Hoffer <info@michaelhoffer.de>
// */
//public class UGAttributionDisplay implements GlobalForegroundPainter {
//    
//    public static final String UG_PLUGIN_NAME="VRL-UG"; // must match ug plugin name (without version)
//
//    private Canvas canvas;
//    private Image image;
//    // AffineTransform transform = new AffineTransform();
//    
//    private boolean positionChanged = true;
//
//    public UGAttributionDisplay(Canvas canvas) {
//        this.canvas = canvas;
//
//        try {
//
//            PluginConfigurator pC = VRL.getPluginConfiguratorByName(UG_PLUGIN_NAME);
//            
//            image = new ImageIcon(pC.getIcon()).getImage();
//            image = ImageUtils.convert(image, BufferedImage.TYPE_INT_ARGB, 140, 36, true);
//
//        } catch (Exception ex) {
//            System.out.println(">> UGAttributionDisplay: cannot load ug logo!");
//        }
//    }
//
//    @Override
//    public void paintGlobal(Graphics g) {
//
//        Graphics2D g2 = (Graphics2D) g;
//
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);
//
//        int visibleRectX = (int) canvas.getVisibleRect().getX();
//        int visibleRectY = (int) canvas.getVisibleRect().getY();
//
//        int visibleRectWidth = (int) canvas.getVisibleRect().getWidth();
//        int visibleRectHeight = (int) canvas.getVisibleRect().getHeight();
//
//        Composite original = g2.getComposite();
//
//        AlphaComposite ac1 = AlphaComposite.getInstance(
//                AlphaComposite.SRC_OVER, 0.45f);
//
//        g2.setComposite(ac1);
//
//        g2.setColor(canvas.getStyle().getBaseValues().
//                getColor(MessageBox.BOX_COLOR_KEY));
//        
//        int rectX = visibleRectX + 10;
//        int rectY = visibleRectY + visibleRectHeight - 70;
//        
//        int imgX = visibleRectX + 20;
//        int imgY = visibleRectY + visibleRectHeight - 60 + 2;
//        
//        if (canvas.getMessageBox().isOpened() 
//                || canvas.getMessageBox().isOpening()) {
//            rectY = 10;
//            imgY = 20 + 2;
//            positionChanged = true;
//        }
//
//        g2.fillRoundRect(rectX, rectY, 160, 60, 18, 18);
//
//        g2.setStroke(new BasicStroke(4f));
//        g2.setColor(canvas.getStyle().getBaseValues().
//                getColor(MessageBox.BOX_COLOR_KEY));
//        g2.drawRoundRect(rectX, rectY, 160, 60, 18, 18);
//        
//        ac1 = AlphaComposite.getInstance(
//                AlphaComposite.SRC_OVER, 0.75f);
//
//        g2.setComposite(ac1);
//        
//        g2.drawImage(image, imgX, imgY, null);
//
//         ac1 = AlphaComposite.getInstance(
//                AlphaComposite.SRC_OVER, 0.85f);
//
//        g2.setComposite(ac1);
//
//        g2.setStroke(new BasicStroke(1.5f));
//        g2.setColor(canvas.getStyle().getBaseValues().
//                getColor(MessageBox.TEXT_COLOR_KEY));
//        g2.drawRoundRect(rectX, rectY, 160, 60, 18, 18);
//
//        g2.setComposite(original);
//        
//        if (positionChanged) {
//            canvas.repaint();
//            positionChanged = false;
//        }
//
//    }
//}
