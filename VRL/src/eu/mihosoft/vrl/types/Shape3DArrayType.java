/* 
 * Shape3DArrayType.java
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
package eu.mihosoft.vrl.types;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.dialogs.SaveImageDialog;
import eu.mihosoft.vrl.reflection.CustomParamData;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import eu.mihosoft.vrl.visual.VBoxLayout;
import eu.mihosoft.vrl.visual.VContainer;
import eu.mihosoft.vrl.visual.VGraphicsUtil;
import groovy.lang.Script;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.OrderedGroup;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

/**
 * TypeRepresentation for
 * <code>eu.mihosoft.vrl.v3d.Shape3DArray</code>. The easiest way to create 3D
 * shapes is to use {@link eu.mihosoft.vrl.v3d.VTriangleArray} or
 * {@link eu.mihosoft.vrl.v3d.TxT2Geometry}. This type representation works just
 * like {@link Shape3DType} but can visualize several Shape3D objects at once.
 *
 * For simple scenarios, Instead of directly using Java3D objects (Shape3D) it
 * is suggested to use {@link eu.mihosoft.vrl.v3d.VGeometry3D} objects and the
 * corresponding type representation.
 *
 * <p>Sample:</p> <br/> <img src="doc-files/shape3d-default-01.png"/> <br/>
 *
 * @see eu.mihosoft.vrl.v3d.VTriangleArray
 * @see eu.mihosoft.vrl.v3d.Shape3DArray
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@TypeInfo(type = Shape3DArray.class, input = false, output = true, style = "default")
public class Shape3DArrayType extends TypeRepresentationBase {

    private static final long serialVersionUID = -4516600302355830671L;
    private BranchGroup shapeGroups[] = new BranchGroup[2];
    private BranchGroup shapeParents[] = new BranchGroup[2];
    private Switch switchGroup;
    private java.util.BitSet visibleNodes;
    private UniverseCreator universeCreator;
    private VCanvas3D canvas;
    private boolean doEmpty = true;
    private VContainer container;
    private Dimension previousVCanvas3DSize;
    protected Dimension minimumVCanvas3DSize;
    public static String ORIENTATION_KEY = "orientation";

    // ThomasL
    // remember Shape3DArray for rendering
    private Shape3DArray shapes = null;    
    
    /**
     * Defines whether to force branch group in favour of ordered group.
     */
    private boolean forceBranchGroup = false;

    /**
     * Constructor.
     *
     * @param canvas the 3D canvas
     * @param universeCreator the universe creator
     */
    public Shape3DArrayType(VCanvas3D canvas, UniverseCreator universeCreator) {
        init();
        init3DView(canvas, universeCreator);
    }

    /**
     * Constructor.
     */
    public Shape3DArrayType() {
        init();
        if (!VGraphicsUtil.NO_3D) {
            VCanvas3D c = null;
            c = new VCanvas3D(this);
            init3DView(new VCanvas3D(this), new VUniverseCreator());
        } else {
            add(new JLabel("Java3D support disabled!"));
        }
    }

    /**
     * Initializes this type representation.
     */
    protected void init() {
        setUpdateLayoutOnValueChange(false);
        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.X_AXIS);
        setLayout(layout);

        nameLabel.setText("Shape3D Array:");
        nameLabel.setAlignmentY(0.5f);
        this.add(nameLabel);

        setHideConnector(true);
    }

    /**
     * Initializes the 3D view of this type representation.
     *
     * @param canvas the 3D canvas
     * @param universeCreator the universe creator
     */
    protected void init3DView(final VCanvas3D canvas, UniverseCreator universeCreator) {
        dispose3D(); // very important to prevent memory leaks of derived classes!

        switchGroup = new Switch(Switch.CHILD_MASK);

        if (container != null) {
            this.remove(container);
        }

        this.canvas = canvas;
        this.universeCreator = universeCreator;


        //canvas = new VCanvas3D(this);

        canvas.setOpaque(false);
        canvas.setMinimumSize(new Dimension(160, 120));
        canvas.setPreferredSize(new Dimension(160, 120));
        canvas.setSize(new Dimension(160, 120));
        setValueOptions("width=160;height=120;blurValue=0.7F;"
                + "renderOptimization=false;realtimeOptimization=false;"
                + "doEmpty=true");

        minimumVCanvas3DSize = canvas.getMinimumSize();

//            canvas.setRenderOptimizationEnabled(true);
//            canvas.setRealTimeRenderOptimization(true);
//            canvas.setBlurValue(0.8f);

        switchGroup.setCapability(Switch.ALLOW_SWITCH_WRITE);
        switchGroup.setCapability(Switch.ENABLE_PICK_REPORTING);
        switchGroup.setCapability(Switch.ALLOW_CHILDREN_EXTEND);
        switchGroup.setCapability(Switch.ALLOW_CHILDREN_READ);
        switchGroup.setCapability(Switch.ALLOW_CHILDREN_WRITE);
        switchGroup.setCapability(BranchGroup.ALLOW_DETACH);

        for (int i = 0; i < shapeGroups.length; i++) {
            shapeGroups[i] = new BranchGroup();

            shapeGroups[i].setCapability(BranchGroup.ENABLE_PICK_REPORTING);
            shapeGroups[i].setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
            shapeGroups[i].setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            shapeGroups[i].setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
            shapeGroups[i].setCapability(BranchGroup.ALLOW_DETACH);

            switchGroup.addChild(shapeGroups[i]);
        }

        visibleNodes =
                new java.util.BitSet(switchGroup.numChildren());

        container = new VContainer();

        container.add(canvas);

        this.add(container);

        this.setInputComponent(container);

        universeCreator.init(canvas);

        BranchGroup switchParentGroup = new BranchGroup();

        switchParentGroup.setCapability(BranchGroup.ENABLE_PICK_REPORTING);
        switchParentGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        switchParentGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        switchParentGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        switchParentGroup.setCapability(BranchGroup.ALLOW_DETACH);

        switchParentGroup.addChild(switchGroup);

        universeCreator.getRootGroup().addChild(switchParentGroup);

        JMenuItem item = new JMenuItem("Reset View");

        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Transform3D t3d = new Transform3D();
                getUniverseCreator().getRootGroup().setTransform(t3d);
                getCanvas().contentChanged();
            }
        });

        canvas.getMenu().add(item);

        item = new JMenuItem("Save as Image");

        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int w = 4096;
                int h = (int) (((double) canvas.getHeight() / (double) canvas.getWidth()) * w);

                BufferedImage img = getOffscreenCanvas().doRender(w, h);

                SaveImageDialog.showDialog(getMainCanvas(), img);
            }
        });

        canvas.getMenu().add(item);

        canvas.getMenu().addSeparator();

        item = new JMenuItem("Increase Zoom Speed");

        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                getUniverseCreator().setZoomFactor(
                        getUniverseCreator().getZoomFactor() + 0.5);
            }
        });

        canvas.getMenu().add(item);

        item = new JMenuItem("Decrease Zoom Speed");

        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                getUniverseCreator().setZoomFactor(
                        getUniverseCreator().getZoomFactor() - 0.5);
            }
        });

        canvas.getMenu().add(item);
    }

    @Override
    synchronized public void setViewValue(Object o) {

        clearView();

        if (!VGraphicsUtil.NO_3D) {

            final Shape3DArray shapes = (Shape3DArray) o;

            // ThomasL
            // remember Shape3DArray for rendering with sunflow
            this.shapes = (Shape3DArray) o;
            
            
            if (shapeParents[0] != null) {
                shapeParents[1] = new BranchGroup();
                shapeParents[1].setCapability(BranchGroup.ENABLE_PICK_REPORTING);
                shapeParents[1].setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
                shapeParents[1].setCapability(BranchGroup.ALLOW_CHILDREN_READ);
                shapeParents[1].setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
                shapeParents[1].setCapability(BranchGroup.ALLOW_DETACH);

                Group childGroup = null;

                if (!shapes.isEmpty() && !isForceBranchGroup()) {
                    Appearance app = shapes.get(0).getAppearance();
                    if (app != null && app.getTransparencyAttributes()!=null) {
                        TransparencyAttributes tA = app.getTransparencyAttributes();
                        if (tA.getTransparency() > 0) {
                            childGroup = new OrderedGroup();
                        }
                    }
                }
                if (childGroup == null) {
                    childGroup = new BranchGroup();
                }
                for (Shape3D s : shapes) {
                    childGroup.addChild(s);
                }

                shapeParents[1].addChild(childGroup);

                shapeGroups[1].addChild(shapeParents[1]);
                visibleNodes.set(1, true);
                visibleNodes.set(0, false);
                switchGroup.setChildMask(visibleNodes);

                shapeParents[0].detach();
                shapeParents[0] = null;
            } else {
                shapeParents[0] = new BranchGroup();
                shapeParents[0].setCapability(BranchGroup.ENABLE_PICK_REPORTING);
                shapeParents[0].setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
                shapeParents[0].setCapability(BranchGroup.ALLOW_CHILDREN_READ);
                shapeParents[0].setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
                shapeParents[0].setCapability(BranchGroup.ALLOW_DETACH);

                Group childGroup = null;

                if (!shapes.isEmpty() && !isForceBranchGroup()) {
                    Appearance app = shapes.get(0).getAppearance();
                    if (app != null && app.getTransparencyAttributes()!=null) {
                        TransparencyAttributes tA = app.getTransparencyAttributes();
                        if (tA.getTransparency() > 0) {
                            childGroup = new OrderedGroup();
                        }
                    }
                }
                if (childGroup == null) {
                    childGroup = new BranchGroup();
                }
                for (Shape3D s : shapes) {
                    childGroup.addChild(s);
                }

                shapeParents[0].addChild(childGroup);

                shapeGroups[0].addChild(shapeParents[0]);
                visibleNodes.set(0, true);
                visibleNodes.set(1, false);
                switchGroup.setChildMask(visibleNodes);

                if (shapeParents[1] != null) {
                    shapeParents[1].detach();
                    shapeParents[1] = null;
                }
            }
        }
    }

    
    // Thomas Licht
//    @Override
//    public void setValue(Object o)
//    {
//        // here comes an Shape3DArray object
//        if (o != null)
//        {
//            // remember Shape3DArray for rendering with sunflow
//            this.shapes = (Shape3DArray) o;
//        }
//        super.setValue(o);
//    }    
    
    
    @Override
    public void emptyView() {
        clearView();
    }

    private void clearView() {

        if (!VGraphicsUtil.NO_3D) {
            if (isDoEmpty()) {
                if (visibleNodes != null) {
                    visibleNodes.set(0, false);
                    visibleNodes.set(1, false);
                    switchGroup.setChildMask(visibleNodes);

                    for (int i = 0; i < shapeParents.length; i++) {
                        if (shapeParents[i] != null) {
                            shapeParents[i].detach();
                            shapeParents[i] = null;
                        }
                    }
                    if (getCanvas() != null) {
                        getCanvas().contentChanged();
                        getCanvas().postRenderTask();
                    }
                }
            }
        }
    }

    /**
     * Defines the Vcanvas3D size by evaluating a groovy script.
     *
     * @param script the script to evaluate
     */
    private void setVCanvas3DSizeFromValueOptions(Script script) {

        if (VGraphicsUtil.NO_3D) {
            return;
        }

        Integer w = null;
        Integer h = null;
        Object property = null;

        if (getValueOptions() != null) {

            if (getValueOptions().contains("width")) {
                property = script.getProperty("width");
            }

            if (property != null) {
                w = (Integer) property;
            }

            property = null;

            if (getValueOptions().contains("height")) {
                property = script.getProperty("height");
            }

            if (property != null) {
                h = (Integer) property;
            }
            
            property = null;

            if (getValueOptions().contains("forceBranchGroup")) {
                property = script.getProperty("forceBranchGroup");
            }

            if (property != null) {
                setForceBranchGroup((Boolean) property);
            }
        }

        if (w != null && h != null && getCanvas() != null) {
            // TODO find out why offset is 5
            getCanvas().setPreferredSize(new Dimension(w - 5, h));
            getCanvas().setMinimumSize(minimumVCanvas3DSize);
            getCanvas().setSize(new Dimension(w - 5, h));
        }

        System.out.println(getValueOptions());
    }

    /**
     * Defines render options by evaluating a groovy script.
     *
     * @param script the script to evaluate
     */
    private void setRenderOptionsFromValueOptions(Script script) {

        Object property = null;
        Boolean enableRenderOptimization = true;
        Boolean enableRealtimeOptimization = false;
        Float blurValue = 0.7f;

        if (getValueOptions() != null) {

            if (getValueOptions().contains("renderOptimization")) {
                property = script.getProperty("renderOptimization");
            }

            if (property != null) {
                enableRenderOptimization = (Boolean) property;
                canvas.setRenderOptimizationEnabled(
                        enableRenderOptimization);
            }

            property = null;

            if (getValueOptions().contains("realtimeOptimization")) {
                property = script.getProperty("realtimeOptimization");
            }

            if (property != null) {
                enableRealtimeOptimization = (Boolean) property;
                canvas.setRealTimeRenderOptimization(
                        enableRealtimeOptimization);
            }

            property = null;

            if (getValueOptions().contains("blurValue")) {
                property = script.getProperty("blurValue");
            }

            if (property != null) {
                blurValue = (Float) property;
                canvas.setBlurValue(blurValue);
            }

            property = null;

            if (getValueOptions().contains("doEmpty")) {
                property = script.getProperty("doEmpty");
            }

            if (property != null) {
                doEmpty = (Boolean) property;
            }
        }
    }

    @Override
    protected void evaluationRequest(Script script) {

        if (VGraphicsUtil.NO_3D) {
            return;
        }

        setVCanvas3DSizeFromValueOptions(script);
        setRenderOptionsFromValueOptions(script);
    }

    @Override
    public CustomParamData getCustomData() {

        if (VGraphicsUtil.NO_3D) {
            return new CustomParamData();
        }

        CustomParamData result = super.getCustomData();

        Transform3D t3d = new Transform3D();
        getUniverseCreator().getRootGroup().getTransform(t3d);
        double[] values = new double[16];
        t3d.get(values);

        result.put(ORIENTATION_KEY, values);

        return result;
    }

    public double[] getOrientationFromCustomData() {

        if (VGraphicsUtil.NO_3D) {
            return null;
        }

        double[] values = (double[]) super.getCustomData().get(ORIENTATION_KEY);
        return values;
    }

    public double[] getOrientationFromUniverse() {
        if (VGraphicsUtil.NO_3D) {
            return new double[0];
        }
        Transform3D t3d = new Transform3D();
        getUniverseCreator().getRootGroup().getTransform(t3d);
        double[] values = new double[16];
        t3d.get(values);
        return values;
    }

    // ThomasL
    // get float array
    public float[] getOrientationFromUniverseF() {
        if (VGraphicsUtil.NO_3D) {
            return new float[0];
        }
        Transform3D t3d = new Transform3D();
        getUniverseCreator().getRootGroup().getTransform(t3d);
        float[] values = new float[16];
        t3d.get(values);
        return values;    
    }
    
    @Override
    public void evaluateCustomParamData() {

        if (VGraphicsUtil.NO_3D) {
            return;
        }

        Transform3D t3d = new Transform3D();
        double[] values = getOrientationFromCustomData();
        if (values != null) {
            t3d.set(values);
            getUniverseCreator().getRootGroup().setTransform(t3d);
        }
    }

    public void setOrientationFromValues(double[] values) {

        if (VGraphicsUtil.NO_3D) {
            return;
        }

        Transform3D t3d = new Transform3D();
        if (values != null) {
            t3d.set(values);
            getUniverseCreator().getRootGroup().setTransform(t3d);
        }
    }

    /**
     * Returns the canvas used for rendering
     *
     * @return the canvas used for rendering
     */
    public VCanvas3D getCanvas() {
        return canvas;
    }

    /**
     * Returns the canvas used for offscreen rendering
     *
     * @return the canvas used for offscreen rendering
     */
    public VOffscreenCanvas3D getOffscreenCanvas() {
        return universeCreator.getOffscreenCanvas();
    }

    /**
     * Indicates whether to empty view of the type representation.
     *
     * @return <code>true</code> if the view will be emptied; <code>false</code>
     * otherwise
     */
    public boolean isDoEmpty() {
        return doEmpty;
    }

    /**
     * Returns the universe creator.
     *
     * @return the universe creator
     */
    public UniverseCreator getUniverseCreator() {
        return universeCreator;
    }

    /**
     * Disposes 3D resources.
     */
    private void dispose3D() {
        if (!VGraphicsUtil.NO_3D) {
            clearView();
            value = null;

            if (getCanvas() != null) {
                getCanvas().getOffscreenCanvas3D().stopRenderer();
                getUniverseCreator().getUniverse().cleanup();
                universeCreator.dispose();
            }

            canvas = null;
            universeCreator = null;
        }
    }

    @Override
    public void dispose() {
        dispose3D();
        super.dispose();
    }

    @Override
    public void enterFullScreenMode(Dimension size) {
        super.enterFullScreenMode(size);
        previousVCanvas3DSize = canvas.getSize();
        container.setPreferredSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        container.setMinimumSize(null);
        container.setMaximumSize(null);

        canvas.setPreferredSize(null);
        canvas.setMinimumSize(minimumVCanvas3DSize);
        canvas.setMaximumSize(null);

        revalidate();
    }

    @Override
    public void leaveFullScreenMode() {
        super.leaveFullScreenMode();
        container.setPreferredSize(null);
        container.setMinimumSize(null);
        container.setMaximumSize(null);

        canvas.setSize(previousVCanvas3DSize);
        canvas.setPreferredSize(previousVCanvas3DSize);
        canvas.setMinimumSize(minimumVCanvas3DSize);

        canvas.contentChanged();

        revalidate();
    }

    @Override
    public JComponent customViewComponent() {

//        final BufferedImage img = plotPane.getImage();
//
//        JPanel panel = new JPanel() {
//            @Override
//            public void paintComponent(Graphics g) {
//                g.drawImage(img, 0, 0, 640, 480,  null);
//            }
//        };
//
//        return panel;
        return null;
    }
//    protected void setMinimumVCanvas3DSize(Dimension canvas3DSize) {
//
//        canvas.setPreferredSize(canvas3DSize);
//        canvas.setMinimumSize(canvas3DSize);
//        minimumVCanvas3DSize = canvas3DSize;
//
//        setValueOptions("width=" + canvas3DSize.width + ";"
//                + "height=" + canvas3DSize.height);
//    }

    @Override
    public boolean noSerialization() {
        // we cannot serialize shape3d objects
        return true;
    }

    /**
     * Indicates whether to force branch group in favour of ordered group.
     *
     * @return the state
     */
    public boolean isForceBranchGroup() {
        return forceBranchGroup;
    }

    /**
     * Defines whether to force branch group in favour of ordered group.
     *
     * @param forceBranchGroup the state to set
     */
    public void setForceBranchGroup(boolean forceBranchGroup) {
        this.forceBranchGroup = forceBranchGroup;
    }
}
