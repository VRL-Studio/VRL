///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package eu.mihosoft.vrl.visual;
//
//import java.awt.Graphics2D;
//import java.awt.RenderingHints;
//import java.awt.geom.AffineTransform;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.apache.batik.bridge.BridgeContext;
//import org.apache.batik.bridge.DocumentLoader;
//import org.apache.batik.bridge.GVTBuilder;
//import org.apache.batik.bridge.UserAgent;
//import org.apache.batik.bridge.UserAgentAdapter;
//import org.apache.batik.bridge.ViewBox;
//import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
//import org.apache.batik.ext.awt.image.GraphicsUtil;
//import org.apache.batik.gvt.GraphicsNode;
//import org.apache.batik.util.XMLResourceDescriptor;
//import org.w3c.dom.Element;
//import org.w3c.dom.svg.SVGDocument;
//
///**
// * Renders SVG Documents.
// * @author Michael Hoffer <info@michaelhoffer.de>
// */
//public class SVGRenderer {
//
//    /**
//     * Loads an svg document from file.
//     * @param f file 
//     * @return svg document or <code>null</code> if no such document exists.
//     */
//    private static SVGDocument loadDocument(File f) {
//        return loadDocument(f.toURI().toString());
//    }
//
//    /**
//     * Loads an svg document from the specified URI.
//     * @param uri uri
//     * @return an svg document from the specified URI or <code>null</code>
//     * if no such document exists.
//     */
//    private static SVGDocument loadDocument(String uri) {
//        String className =
//                XMLResourceDescriptor.getXMLParserClassName();
//        SAXSVGDocumentFactory factory =
//                new SAXSVGDocumentFactory(className);
//        try {
//            return factory.createSVGDocument(uri);
//        } catch (IOException ex) {
//            Logger.getLogger(SVGRenderer.class.getName()).
//                    log(Level.SEVERE, null, ex);
//        }
//
//        return null;
//    }
//
//    /**
//     * Loads an svg and renders it.
//     * @param f svg file
//     * @param width width of the image to render
//     * @param height height of the image to render
//     * @return a buffered image containing the rendered svg document
//     */
//    public static BufferedImage svgToImage(File f, int width, int height) {
//        return svgToImage(loadDocument(f), width, height);
//    }
//
//    /**
//     * Loads an svg and renders it.
//     * @param uri URI
//     * @param width width of the image to render
//     * @param height height of the image to render
//     * @return a buffered image containing the rendered svg document
//     */
//    public static BufferedImage svgToImage(String uri, int width, int height) {
//        return svgToImage(loadDocument(uri), width, height);
//    }
//
//    /**
//     * Loads an svg and renders it.
//     * @param svgDocument svg document
//     * @param width width of the image to render
//     * @param height height of the image to render
//     * @return a buffered image containing the rendered svg document
//     */
//    public static BufferedImage svgToImage(
//            SVGDocument svgDocument, int width, int height) {
//        // Paint svg into image buffer
//        BufferedImage img = ImageUtils.createCompatibleImage(width, height);
//
//        Graphics2D g2d = GraphicsUtil.createGraphics(img);
//
//        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
//                RenderingHints.VALUE_RENDER_QUALITY);
//
//        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
//                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);
//
//        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//
//        // Scale image to desired size
//        AffineTransform usr2dev = getScaledTransform(svgDocument, width, height);
//        g2d.transform(usr2dev);
//
//        GraphicsNode rootSvgNode = getRootNode(svgDocument);
//
//        rootSvgNode.paint(g2d);
//
//        // Cleanup and return image
//        g2d.dispose();
//
//        return img;
//    }
//
//    /**
//     * <p>
//     * Returns an affine transform that scales the root element of the specified
//     * svg document to fit in the specified view box.
//     * </p>
//     * <p>
//     * <b>Note:</b> If this method does not work as expected check the
//     * <code>viewbox</code> property of the corresponding svg document. If the
//     * <code>width</code> and <code>height</code> properties are not the same
//     * as <code>viewbox</code> this may result in incorrectly scaled renderings.
//     * </p>
//     * @param svgDocument svg document
//     * @param width width of the viewbox
//     * @param height height of the viewbox
//     * @return an affine transform that scales the root element of the specified
//     * svg document to fit in the specified view box
//     */
//    public static AffineTransform getScaledTransform(SVGDocument svgDocument,
//            int width, int height) {
//        Element elt = svgDocument.getRootElement();
//
//        return ViewBox.getViewTransform(null, elt, width,
//                height, null);
//    }
//
//    /**
//     * Returns the root node of the specified svg document.
//     * @param svgDocument svg document
//     * @return the root node of the specified svg document
//     */
//    public static GraphicsNode getRootNode(SVGDocument svgDocument) {
//        UserAgent userAgent = new UserAgentAdapter();
//        DocumentLoader loader = new DocumentLoader(userAgent);
//        GVTBuilder builder = new GVTBuilder();
//        BridgeContext ctx = new BridgeContext(userAgent, loader);
//        ctx.setDynamicState(BridgeContext.DYNAMIC);
//
//        return builder.build(ctx, svgDocument);
//    }
//
//    /**
//     * Returns the root node of the specified svg file.
//     * @param svgDocument svg file
//     * @return the root node of the specified svg document
//     */
//    public static GraphicsNode getRootNode(File f) {
//        return getRootNode(loadDocument(f));
//    }
//
//        /**
//     * Returns the root node of the specified svg file.
//     * @param uri URI
//     * @return the root node of the specified svg document
//     */
//    public static GraphicsNode getRootNode(String uri) {
//        return getRootNode(loadDocument(uri));
//    }
//}
