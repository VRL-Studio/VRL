/* 
 * VCanvasPopupMenu.java
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

package eu.mihosoft.vrl.reflection;

import eu.mihosoft.vrl.io.SessionHistoryController;
import eu.mihosoft.vrl.lang.groovy.GroovyCodeEditorComponent;
import eu.mihosoft.vrl.visual.CanvasCapabilities;
import eu.mihosoft.vrl.visual.StyleChangedListener;
import eu.mihosoft.vrl.visual.CanvasWindow;
import eu.mihosoft.vrl.visual.CapabilityChangedListener;
import eu.mihosoft.vrl.visual.CapabilityManager;
import eu.mihosoft.vrl.visual.Style;
import eu.mihosoft.vrl.visual.TransformingParent;
import eu.mihosoft.vrl.visual.VScale;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * Popup menu for visual canvas. It contains items for adding code, code samples
 * and for component management.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public final class VCanvasPopupMenu extends JPopupMenu/* implements ComponentController*/ {

    private static final long serialVersionUID = -2684877771681576730L;
    private Point location;
    private JMenu components = new JMenu("Components");
    private JMenuItem addGroovyCode = new JMenuItem("New Groovy Code");
    private JMenu addComponents = new JMenu("add Component");
    private JMenu removeComponents = new JMenu("remove Component");
    private JMenu codeTemplates = new JMenu("Code Templates");
    private JMenuItem loadPrevious = new JMenuItem("Load Previous Session");
    private JMenuItem loadNext = new JMenuItem("Load Next Session");
    private VisualCanvas mainCanvas;
    private MouseListener mouseListener;
    private StyleChangedListener styleListener;
    private ComponentControllerImpl controller = new ComponentControllerImpl();

    /**
     * Constructor.
     * @param canvas the parent canvas
     */
    public VCanvasPopupMenu(final VisualCanvas canvas) {
        styleListener =
                new StyleChangedListener() {

                    @Override
                    public void styleChanged(Style style) {
                        SwingUtilities.updateComponentTreeUI(
                                VCanvasPopupMenu.this);
                    }
                };

        canvas.getStyleManager().addStyleChangedListener(styleListener);

        this.mainCanvas = canvas;

        mouseListener = new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    
                    VScale scale = TransformingParent.getScale(canvas);
                    
                    VCanvasPopupMenu.this.show(e.getComponent(),
                            (int)(e.getX()/scale.getScaleX()),
                            (int)(e.getY()/scale.getScaleY()));
                    setCurrentLocation(e.getPoint());
                }
            }
        };

//        add(components);
//        components.add(addComponents);
//        components.add(removeComponents);
//        
//        components.setEnabled(false);
//
//        addSeparator();


//        add(components);
        components.add(addComponents);
        components.add(removeComponents);

//        addSeparator();


        JMenuItem manageComponents = new JMenuItem("Manage Components");

        manageComponents.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ComponentManagement.toggleSearchDialog(canvas);
            }
        });

        add(manageComponents);

        JMenuItem newComponent = new JMenuItem("New Component");

        newComponent.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {


                    getMainCanvas().getProjectController().createComponent();
                } catch (IOException ex) {
                    Logger.getLogger(VCanvasPopupMenu.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }
        });

        add(newComponent);

        addSeparator();

        add(loadPrevious);
        add(loadNext);

        addSeparator();

        addGroovyCode.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (e.getActionCommand().equals("New Groovy Code")) {

                    addWindow(null, canvas);

                }
            }
        });

        add(addGroovyCode);

        add(codeTemplates);

        JMenuItem item = null;

        //***************************BEGIN***************************
        item = new JMenuItem("Hello, World! - Sample");

        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Hello, World! - Sample")) {
                    addWindow(
                            "@ComponentInfo(name=\"Hello, World! - Sample\", category=\"Custom\")\n"
                            + "class SampleClass implements "
                            + "Serializable {\n\n"
                            + "  private static final long serialVersionUID=1;\n\n"
                            + "  public String hello(){\n"
                            + //                "    Color c = new Color();\n" +
                            "      return \"Hello, World!\";\n"
                            + "  }\n"
                            + "}", canvas);

                }
            }
        });

        codeTemplates.add(item);
        //*************************** END ***************************


        //***************************BEGIN***************************
        item = new JMenuItem("Add Integers");

        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Add Integers")) {
                    addWindow(
                            "@ComponentInfo(name=\"Add Integers\", category=\"Custom\")\n"
                            + "class AddIntegers implements "
                            + "Serializable {\n\n"
                            + "  private static final long serialVersionUID=1;\n\n"
                            + "  public Integer add(Integer a, Integer b){\n"
                            + "    return a+b;\n"
                            + "  }\n"
                            + "}", canvas);

                }
            }
        });

        codeTemplates.add(item);
        //*************************** END ***************************

        //***************************BEGIN***************************
        item = new JMenuItem("Add Integers (Array Version)");

        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Add Integers (Array Version)")) {
                    addWindow(
                            "@ComponentInfo(name=\"Add Integers (Array Version)\", category=\"Custom\")\n"
                            + "class AddIntegers2 implements "
                            + "Serializable {\n\n"
                            + "  private static final long serialVersionUID=1;\n\n"
                            + "  public Integer add(\n"
                            + "  @ParamInfo(style=\"array\", options=\"minArraySize=2\") Integer... values) {\n"
                            + "    def result = 0\n"
                            + "    values.each{it->result+=it}\n"
                            + "    return result\n"
                            + "  }\n"
                            + "}", canvas);

                }
            }
        });

        codeTemplates.add(item);
        //*************************** END ***************************

        //***************************BEGIN***************************
        item = new JMenuItem("Counter");

        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Counter")) {
                    addWindow(
                            "@ComponentInfo(name=\"Counter\", category=\"Custom\")\n"
                            + "class Counter implements "
                            + "Serializable {\n\n"
                            + "  private static final long serialVersionUID=1;\n\n"
                            + "  int v;\n\n"
                            + "  public void inc() {\n"
                            + "    v++;\n"
                            + "  }\n"
                            + "  public void dec() {\n"
                            + "    v--;\n"
                            + "  }\n"
                            + "  public void reset() {\n"
                            + "    v = 0;\n"
                            + "  }\n"
                            + "}", canvas);

                }
            }
        });

        codeTemplates.add(item);
        //*************************** END ***************************

        //***************************BEGIN***************************
        item = new JMenuItem("Color Chooser");

        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Color Chooser")) {

                    addWindow(
                            "@ComponentInfo(name=\"Color Chooser\", category=\"Custom\")\n"
                            + "class ColorChooser implements Serializable {\n\n"
                            + "  private static final long serialVersionUID=1;\n\n"
                            + "  public Color choose(\n"
                            + "    @ParamInfo(name=\"R\", style=\"slider\") Integer r,\n"
                            + "    @ParamInfo(name=\"G\", style=\"slider\") Integer g,\n"
                            + "    @ParamInfo(name=\"B\", style=\"slider\") Integer b) {\n\n"
                            + "    return new Color(r,g,b);\n"
                            + "  }\n"
                            + "}", canvas);
                }
            }
        });

        codeTemplates.add(item);
        //*************************** END ***************************

        //***************************BEGIN***************************
        item = new JMenuItem("Color Sample");

        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Color Sample")) {

                    addWindow(
                            "@ComponentInfo(name=\"Color Sample\", category=\"Custom\")\n"
                            + "class ColorSample implements Serializable {\n\n"
                            + "  private static final long serialVersionUID=1;\n\n"
                            + "  public Color green(){\n"
                            + "    return Color.green;\n"
                            + "  }\n"
                            + "}", canvas);
                }
            }
        });

        codeTemplates.add(item);
        //*************************** END ***************************

        //***************************BEGIN***************************
        item = new JMenuItem("3D Geometry Sample");

        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("3D Geometry Sample")) {
                    addWindow(
                            "@ComponentInfo(name=\"3D Geometry Sample\", category=\"Custom\")\n"
                            + "class GeometrySample implements Serializable {\n"
                            + "  private static final long serialVersionUID=1\n\n"
                            + "  @MethodInfo(callOptions=\"autoinvoke\")\n"
                            + "  public VGeometry3D getGeometry() {\n"
                            + "    VTriangleArray result = new VTriangleArray()\n\n"
                            + "    Node n1 = new Node(new Point3f(0f,0f,0f))\n"
                            + "    Node n2 = new Node(new Point3f(10f,0f,0f))\n"
                            + "    Node n3 = new Node(new Point3f(0f,10f,0f))\n"
                            + "    Node n4 = new Node(new Point3f(0f,0f,10f))\n\n"
                            + "    result.addTriangle(new Triangle(1,n1,n2,n3))\n"
                            + "    result.addTriangle(new Triangle(2,n1,n2,n4))\n"
                            + "    result.addTriangle(new Triangle(3,n1,n3,n4))\n"
                            + "    result.addTriangle(new Triangle(4,n2,n3,n4))\n\n"
                            + "    return new VGeometry3D(result,Color.black,Color.green,1F,false)\n"
                            + "  }\n"
                            + "}", canvas);
                }
            }
        });


        codeTemplates.add(item);
        //*************************** END ***************************

        //***************************BEGIN***************************
        item = new JMenuItem("Sample Image Painter");

        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Sample Image Painter")) {
                    addWindow(
                            "@ComponentInfo(name=\"Sample Image Painter\", category=\"Custom\")\n"
                            + "class SampleImagePainter implements Serializable {\n"
                            + "  private static final long serialVersionUID=1;\n\n"
                            + "  public BufferedImage paint(Color c,\n"
                            + "    @ParamInfo(name=\"thickness\", style=\"slider\",\n"
                            + "    options=\"min=1;max=30;invokeOnChange=true\") Integer thickness){\n\n"
                            + "    BufferedImage image =\n"
                            + "                  new BufferedImage(120,120,BufferedImage.TYPE_INT_ARGB);\n\n"
                            + "    Graphics2D g2 = image.createGraphics();\n"
                            + "    g2.setColor(c);\n"
                            + "    g2.setStroke(new BasicStroke(thickness));\n"
                            + "    g2.drawLine(0,0,image.getWidth(),image.getHeight());\n"
                            + "    g2.drawLine(0,image.getHeight(),image.getWidth(),0);\n"
                            + "    g2.dispose();\n"
                            + "    return image;\n"
                            + "  }\n"
                            + "}", canvas);
                }
            }
        });

        codeTemplates.add(item);
        //*************************** END ***************************

        //***************************BEGIN***************************
        item = new JMenuItem("Colorize Effect");

        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Colorize Effect")) {
                    addWindow(
                            "@ComponentInfo(name=\"Colorize Effect\", category=\"Custom\")\n"
                            + "class ColorEffect implements Serializable {\n"
                            + "  private static final long serialVersionUID=1;\n\n"
                            + "  public BufferedImage colorize(BufferedImage input, Color c){\n"
                            + "    BufferedImage out = new BufferedImage(input.getWidth(),\n"
                            + "	            input.getHeight(),BufferedImage.TYPE_INT_ARGB);\n"
                            + "    Graphics2D g2 = out.createGraphics();\n"
                            + "    g2.drawImage(input,0,0,null);\n"
                            + "    g2.setColor(c);\n"
                            + "    g2.fillRect(0,0,out.getWidth(),out.getHeight());\n"
                            + "    g2.dispose();\n"
                            + "    return out;\n"
                            + "  }\n"
                            + "}", canvas);
                }
            }
        });

        codeTemplates.add(item);
        //*************************** END ***************************

        //***************************BEGIN***************************
        item = new JMenuItem("Blur Effect");

        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Blur Effect")) {
                    addWindow(
                            "@ComponentInfo(name=\"Blur Effect\", category=\"Custom\")\n"
                            + "class BlurEffect implements Serializable {\n"
                            + "  private static final long serialVersionUID=1;\n\n"
                            + "  public BufferedImage blurImage(BufferedImage image, Float blurValue) {\n\n"
                            + "    BufferedImage out =\n"
                            + "    new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);\n\n"
                            + "    Graphics2D g2 = out.createGraphics();\n"
                            + "    g2.drawImage(image,0,0,null);\n\n"
                            + "    float middle = blurValue;\n"
                            + "    float value = (1.0 - middle) / 8.0;\n\n"
                            + "    float[] BLUR = [\n"
                            + "      value, value, value,\n"
                            + "      value, middle, value,\n"
                            + "      value, value, value\n"
                            + "    ];\n\n"
                            + "    ConvolveOp vBlurOp = new ConvolveOp(new Kernel(3, 3, BLUR));\n\n"
                            + "    return vBlurOp.filter(out, null);"
                            + "  }\n"
                            + "}", canvas);
                }
            }
        });

        codeTemplates.add(item);
        //*************************** END ***************************

        //***************************BEGIN***************************
        item = new JMenuItem("Canvas Template");

        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Canvas Template")) {
                    addWindow(
                            "@ComponentInfo(name=\"Canvas Template\", category=\"Custom\")\n"
                            + "class CanvasTemplate implements Serializable {\n"
                            + "  private static final long serialVersionUID=1;\n\n"
                            + "  private transient VisualCanvas mainCanvas;\n\n"
                            + "  @MethodInfo(noGUI = true, callOptions = \"assign-canvas\")\n"
                            + "  public void setMainCanvas(VisualCanvas mainCanvas){\n"
                            + "    this.mainCanvas = mainCanvas;\n"
                            + "  }\n"
                            + "}", canvas);

                }
            }
        });

        codeTemplates.add(item);
        //*************************** END ***************************

        //***************************BEGIN***************************
        item = new JMenuItem("Process Sample");

        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Process Sample")) {
                    addWindow(
                            "@ComponentInfo(name=\"Process Sample\", category=\"Custom\")\n"
                            + "@ObjectInfo(name=\"Command: ls\")\n"
                            + "class ProcessSample extends ProcessTemplate implements Serializable {\n\n"
                            + "  private static final long serialVersionUID=1;\n\n"
                            + "  public ProcessSample(){\n"
                            + "    setPath(\"/bin\");\n"
                            + "    ArrayList<String> command = new ArrayList<String>();\n"
                            + "    command.add(\"./ls\");\n"
                            + "    command.add(\"/\");\n"
                            + "    setCommand(command);\n"
                            + "    setProcessTitle(\"/bin/ls /\");\n"
                            + "    setNumberOfMessageLines(15);\n"
                            + "    setViewer(new MessageBoxViewer());\n"
                            + "  }\n\n"
                            + "  @MethodInfo(noGUI = true, callOptions = \"assign-to-canvas\")\n"
                            + "  public void setMainCanvas(VisualCanvas mainCanvas){\n"
                            + "    super.setMainCanvas(mainCanvas);\n"
                            + "  }\n\n"
                            + "  public void run(){\n"
                            + "    super.run();\n"
                            + "  }\n"
                            + "}", canvas);
                }
            }
        });

        codeTemplates.add(item);
        //*************************** END ***************************

        //***************************BEGIN***************************
        item = new JMenuItem("TypeRepresentation Sample");

        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("TypeRepresentation Sample")) {
                    addWindow(
                            "@ComponentInfo(name=\"TypeRepresentation Sample\", category=\"Custom\")\n"
                            + "class TypeRepresentationSample extends TypeTemplate {\n"
                            + "  private static final long serialVersionUID=1;\n\n"
                            + "  public TypeRepresentationSample(){\n"
                            + "    setType(Integer.class);\n"
                            + "    setValueName(\"Sample Representation\");\n"
                            + "    setStyleName(\"sample\");\n"
                            + "    setBorder(VSwingUtil.createDebugBorder());\n"
                            + "  }\n"
                            + "}", canvas);
                }
            }
        });

        codeTemplates.add(item);
        //*************************** END ***************************

        //***************************BEGIN***************************
        item = new JMenuItem("EffectPane Sample");

        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("EffectPane Sample")) {
                    addWindow(
                            "@ComponentInfo(name=\"EffectPane Sample\", category=\"Custom\")\n"
                            + "class EffectPaneSample implements Serializable {\n"
                            + "  private static final long serialVersionUID=1;\n\n"
                            + "  private transient VisualCanvas mainCanvas;\n\n"
                            + "  @MethodInfo(noGUI = true, callOptions = \"assign-canvas\")\n"
                            + "  public void setMainCanvas(VisualCanvas mainCanvas){\n"
                            + "    this.mainCanvas = mainCanvas;\n"
                            + "  }\n\n"
                            + "  public void blink(@ParamInfo(nullIsValid=true) Color c){\n"
                            + "    Color color = Color.white;\n"
                            + "    if (c !=null) {\n"
                            + "      color= c;\n"
                            + "    }\n\n"
                            + "    BackgroundAnimation a1 =\n"
                            + "        new BackgroundAnimation(mainCanvas.getEffectPane(),\n"
                            + "    mainCanvas.getEffectPane().getBackground(), color);\n"
                            + "    BackgroundAnimation a2 =\n"
                            + "        new BackgroundAnimation(mainCanvas.getEffectPane(),\n"
                            + "    color, new Color(0,0,0,0));\n\n"
                            + "    a1.setDuration(1);\n"
                            + "    a2.setDuration(1);\n\n"
                            + "    AnimationGroup group =\n"
                            + "        new AnimationGroup(mainCanvas.getAnimationManager());\n"
                            + "    group.add(a1);\n"
                            + "    group.append(a2);\n"
                            + "    group.setDuration(1);\n"
                            + "    mainCanvas.getAnimationManager().addAnimation(group,1);\n"
                            + "  }\n\n"
                            + "  public void startSpin() {\n"
                            + "    mainCanvas.getEffectPane().startSpin();\n"
                            + "  }\n\n"
                            + "  public void stopSpin() {\n"
                            + "    mainCanvas.getEffectPane().stopSpin();\n"
                            + "  }\n"
                            + "}", canvas);
                }
            }
        });

        codeTemplates.add(item);
        //*************************** END ***************************

        // add capability listener
        mainCanvas.getCapabilityManager().addCapabilityChangedListener(
                new CapabilityChangedListener() {

                    @Override
                    public void capabilityChanged(
                            CapabilityManager manager, Integer bit) {
                        defineCapabilities();
                    }
                });
    }

    /**
     * Initializes this menu (defines menu states etc.). This method shall be
     * called from from canvas when project controller is defined.
     */
    void init() {

        SessionHistoryController history =
                mainCanvas.getProjectController().
                getSessionHistoryController();

        history.setNextItem(loadNext);
        history.setPreviousItem(loadPrevious);

    }

    /**
     * Defines the menu capabilities depending on canvas capabilities.
     */
    private void defineCapabilities() {

        if (mainCanvas.getCapabilityManager().
                isCapable(CanvasCapabilities.ALLOW_REMOVE_COMPONENTS)) {
            removeComponents.setVisible(true);
        } else {
            removeComponents.setVisible(false);
        }

        if (mainCanvas.getCapabilityManager().
                isCapable(CanvasCapabilities.ALLOW_EDIT)) {
            codeTemplates.setVisible(true);
            addGroovyCode.setVisible(true);
        } else {
            codeTemplates.setVisible(false);
            addGroovyCode.setVisible(false);
        }

        if (mainCanvas.getCapabilityManager().
                isCapable(CanvasCapabilities.ALLOW_INSTANCIATION)) {
            components.setVisible(true);
            addComponents.setVisible(true);
        } else {
            components.setVisible(false);
            addComponents.setVisible(false);
        }
    }

//    /**
//     * Returns the category menu as defined in the component info of the
//     * specified class. If the requested menus don't exist they will be created.
//     * @param root root menu
//     * @param c class to add as component
//     * @return category menu as defined in the component info of the
//     * specified class
//     */
//    private JMenu getCategoryMenu(JMenu root, Class<?> c) {
//        ArrayList<String> categories = new ArrayList<String>();
//        @SuppressWarnings("unchecked")
//        ComponentInfo cInfo = c.getAnnotation(ComponentInfo.class);
//
//        String[] entries = new String[]{"undefined"};
//
//        if (cInfo != null) {
//            entries = cInfo.category().split("/");
//        }
//
//        for (String s : entries) {
//            if (s.length() > 0) {
//                categories.add(s);
//            }
//        }
//
//        JMenu parent = root;
//
//        for (String category : categories) {
//            if (categories != null) {
//                boolean found = false;
//                for (Component comp : parent.getMenuComponents()) {
//                    if (comp instanceof JMenu) {
//                        JMenu categoryMenu = (JMenu) comp;
//                        if (categoryMenu.getText().equals(category)) {
//                            parent = categoryMenu;
//                            found = true;
//                            break;
//                        }
//                    }
//                }
//                if (!found) {
//                    JMenu categoryMenu = new JMenu(category);
//                    parent.add(categoryMenu);
//                    parent = categoryMenu;
//                }
//            }
//        }
//
//        return parent;
//    }
//
//    /**
//     * Adds a component to this menu.
//     * @param c the component to add
//     */
//    @Override
//    public void addComponent(Class<?> c) {
//
//        controller.addComponent(c, this);
//
//        if (!ComponentUtil.requestsIgnore(c)) {
//
//            ComponentMenuItem cAddItem = new ComponentMenuItem(c, this);
//            cAddItem.createAddComponentAction();
//            ComponentMenuItem cRemoveItem = new ComponentMenuItem(c, this);
//            cRemoveItem.createRemoveComponentAction();
//
//            ArrayList<ComponentMenuItem> delList =
//                    getComponentItemsByClass(addComponents, c);
//
//            for (ComponentMenuItem cmi : delList) {
//                getCategoryMenu(addComponents, c).remove(cmi);
//            }
//
//            delList = getComponentItemsByClass(removeComponents, c);
//
//            for (ComponentMenuItem cmi : delList) {
//                getCategoryMenu(removeComponents, c).remove(cmi);
//            }
//
////            if (cAddItem.isComponent()) {
//            getCategoryMenu(addComponents, c).add(cAddItem);
////            }
//
//            if (/*cRemoveItem.isComponent() &&*/cRemoveItem.isAllowRemoval()) {
//                getCategoryMenu(removeComponents, c).add(cRemoveItem);
//            }
//        }
//    }
//
//    /**
//     * Returns a list containing all add-component-menu-items that are
//     * associated with a given class object
//     * @param c the class object
//     * @return a list containing all add-component-menu-items that are
//     *         associated with a given class object
//     */
//    public ArrayList<ComponentMenuItem> getComponentItemsByClass(JMenu root, Class c) {
//        ArrayList<ComponentMenuItem> result =
//                new ArrayList<ComponentMenuItem>();
//
//        JMenu parent = getCategoryMenu(root, c);
//
//        for (int i = 0; i < parent.getItemCount(); i++) {
//            JMenuItem item = parent.getItem(i);
//
//            if (item instanceof ComponentMenuItem) {
//                ComponentMenuItem cTempItem = (ComponentMenuItem) item;
//                if (cTempItem.getComponentClass().getName().equals(c.getName())) {
//                    result.add(cTempItem);
//                }
//            }
//        }
//
//        return result;
//    }
//
    /**CodeTemplate
     * Returns the current location.
     * @return the location
     */
    protected Point getCurrentLocation() {
        return location;
    }

    /**
     * Defines the current location of this menu.
     * @param location the location to set
     */
    protected void setCurrentLocation(Point location) {
        this.location = location;
    }
//
//    /**
//     * Removes all component items that are associated with a given class
//     * object.
//     * @param componentClass the class object
//     */
//    @Override
//    public void removeComponent(Class<?> componentClass) {
//
//        controller.removeComponent(componentClass, this);
//
//        ArrayList<ComponentMenuItem> addItems =
//                getComponentItemsByClass(addComponents, componentClass);
//        ArrayList<ComponentMenuItem> removeItems =
//                getComponentItemsByClass(removeComponents, componentClass);
//        for (ComponentMenuItem i : addItems) {
//            getCategoryMenu(addComponents, componentClass).remove(i);
//        }
//        for (ComponentMenuItem i : removeItems) {
//            getCategoryMenu(removeComponents, componentClass).remove(i);
//        }
//    }
//
////    /**
////     * Adds an empty code window to the canvas.
////     * @param canvas the canvas the code window shall be added to
////     */
////    private void addWindow(VisualCanvas canvas) {
////        GroovyCodeWindow window = new GroovyCodeWindow(canvas);
////
////        canvas.getWindows().
////                add(window);
////        int x = getCurrentLocation().x - window.getWidth() / 2;
////        int y = (int) (getCurrentLocation().y
////                - canvas.getStyle().getBaseValues().getFloat(
////                CanvasWindow.SHADOW_WIDTH_KEY));
////
////        window.setLocation(new Point(x, y));
////    }
//
    /**
     * Adds a code window to the canvas.
     * @param code the code that is to be displayed
     * @param canvas the canvas the code window shall be added to
     */
    private void addWindow(String code, VisualCanvas canvas) {
//        GroovyCodeWindow window = new GroovyCodeWindow(canvas);
//        window.setCode(code);
//
//        canvas.getWindows().
//                add(window);
//        int x = getCurrentLocation().x - window.getWidth() / 2;
//        int y = (int) (getCurrentLocation().y
//                - canvas.getStyle().getBaseValues().getFloat(
//                CanvasWindow.SHADOW_WIDTH_KEY));
//
//        window.setLocation(new Point(x, y));

        GroovyCodeEditorComponent editor = new GroovyCodeEditorComponent(code);

        VisualObject window = canvas.addObject(editor);
        int x = getCurrentLocation().x - window.getWidth() / 2;
        int y = (int) (getCurrentLocation().y
                - canvas.getStyle().getBaseValues().getFloat(
                CanvasWindow.SHADOW_WIDTH_KEY));

        window.setLocation(new Point(x, y));
    }

    /**
     * Returns the main canvas.
     * @return the main canvas
     */
    public VisualCanvas getMainCanvas() {
        return mainCanvas;
    }

    /**
     * Defines the main canvas of this menu.
     * @param mainCanvas the canvas to set
     */
    public void setMainCanvas(VisualCanvas mainCanvas) {
        this.mainCanvas = mainCanvas;
    }

    /**
     * Returns the mouse listener of this menu.
     * @return the mouse listener of this menu
     */
    public MouseListener getMouseListener() {
        return mouseListener;
    }
//
//    /**
//     * @param parent the parent to set
//     */
//    @Override
//    public void setParent(ComponentController parent) {
//        controller.setParent(parent);
//    }
//
//    @Override
//    public void addComponent(Class<?> component, ComponentController sender) {
//        controller.addComponent(component, sender);
//        addComponent(component);
//    }
//
//    @Override
//    public void removeComponent(Class<?> component, ComponentController sender) {
//        controller.removeComponent(component, sender);
//        removeComponent(component);
//    }
}
