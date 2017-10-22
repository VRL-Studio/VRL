/* 
 * AnimatedShape3DArrayType.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2007–2017 by Michael Hoffer
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

package eu.mihosoft.vrl.types;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.reflection.CustomParamData;
import eu.mihosoft.vrl.reflection.RepresentationType;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.v3d.Shape3DArray;
import eu.mihosoft.vrl.visual.*;
import groovy.lang.Script;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * TypeRepresentation for <code>eu.mihosoft.vrl.v3d.Shape3DArray</code>.
 * 
 * Style name: "animation"
 * 
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
@TypeInfo(type=Shape3D.class, input = false, style = "animation")
public class AnimatedShape3DArrayType extends TypeRepresentationBase {

    private static final long serialVersionUID = -4516600302355830671L;
    private BranchGroup shapeGroup;
    private Switch switchGroup;
    private UniverseCreator universeCreator;
    private VCanvas3D canvas;
    private int fps = 30;
    private int currentFrame = 0;
    private int previousFrame = 0;
    private int numberOfFrames = 0;
    private JSlider frameSlider;
    private java.util.BitSet visibleFrames;
    private Thread thread;
    private boolean interruptionRequest = true;
    private VButton playButton;
    private VButton stopButton;
    private javax.swing.Box viewGroup;
    private ArrayList<V3DRenderListener> renderListeners =
            new ArrayList<V3DRenderListener>();
    private VContainer container;

    public static String ORIENTATION_KEY = "orientation";
    private Dimension previousVCanvas3DSize;
    protected Dimension minimumVCanvas3DSize;

    /**
     * Constructor.
     */
    public AnimatedShape3DArrayType() {
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
     * Constructor.
     * @param canvas the 3D canvas
     * @param universeCreator the universe creator
     */
    public AnimatedShape3DArrayType(VCanvas3D canvas,
            UniverseCreator universeCreator) {
        init();
        init3DView(canvas, universeCreator);
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
     * @param canvas the 3D canvas
     * @param universeCreator the universe creator
     */
    protected void init3DView(VCanvas3D canvas, UniverseCreator universeCreator) {
        dispose3D(); // very important to prevent memory leaks of derived classes!

        if (viewGroup != null) {
            this.remove(viewGroup);
        }

        this.canvas = canvas;
        this.universeCreator = universeCreator;

        viewGroup = javax.swing.Box.createVerticalBox();

        this.add(viewGroup);


        //canvas = new VCanvas3D(this);

        canvas.setOpaque(false);
        canvas.setMinimumSize(new Dimension(160, 120));
        canvas.setPreferredSize(new Dimension(160, 120));
        canvas.setSize(new Dimension(160, 120));
        setValueOptions("width=160;height=120;blurValue=0.5F;" +
                "renderOptimization=true;realtimeOptimization=true;fps=30");

        canvas.setRenderOptimizationEnabled(true);
        canvas.setRealTimeRenderOptimization(true);
        canvas.setBlurValue(0.01f);

        minimumVCanvas3DSize = new Dimension(160, 120);

        container = new VContainer();

        container.add(canvas);

        viewGroup.add(container);

        this.setInputComponent(container);

        // universeCreator = new VUniverseCreator(getCanvas());
        universeCreator.init(canvas);

        frameSlider = new JSlider();
        frameSlider.setBackground(VSwingUtil.TRANSPARENT_COLOR);
        frameSlider.setEnabled(false);
        frameSlider.setValue(0);
        frameSlider.setMinimum(0);
        frameSlider.setMaximum(0);

        frameSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                currentFrame = frameSlider.getValue();
                viewCurrentFrame();
            }
        });

        viewGroup.add(frameSlider);

        javax.swing.Box playerGroup = javax.swing.Box.createHorizontalBox();

        viewGroup.add(playerGroup);

        playerGroup.add(javax.swing.Box.createHorizontalGlue());

        playButton = new VButton("Play");

        playButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPlaying() && getViewValueWithoutValidation() != null) {
                    playButton.setText("Pause");
                    startPlayback();
                } else {
                    playButton.setText("Play");
                    pausePlayBack();
                }
            }
        });

        playerGroup.add(playButton);

        stopButton = new VButton("Stop");

        stopButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stopPlayback();
            }
        });

        playerGroup.add(stopButton);

        playerGroup.add(javax.swing.Box.createHorizontalGlue());

    }

    /**
     * Indicates whether this type representation is playing.
     * @return <code>true</code> if this type representation is playing;
     *         <code>false</code> otherwise
     */
    public boolean isPlaying() {
        return thread != null && thread.isAlive();
    }

    /**
     * Views the current frame.
     */
    private void viewCurrentFrame() {
        if (visibleFrames != null && switchGroup != null) {
            frameSlider.setValue(currentFrame);

            visibleFrames.set(previousFrame, false);
            visibleFrames.set(currentFrame, true);

            switchGroup.setChildMask(visibleFrames);
        }
        previousFrame = currentFrame;

        if (isPlaying()) {
            notifyListeners(canvas);
        }
    }

    /**
     * Notifies listeners.
     * @param canvas the canvas
     */
    private void notifyListenersStarted() {
        for (V3DRenderListener l : renderListeners) {
            l.renderingStarted();
        }
    }

    /**
     * Notifies listeners.
     * @param canvas the canvas
     */
    private void notifyListenersStopped() {
        for (V3DRenderListener l : renderListeners) {
            l.renderingStopped();
        }
    }

    /**
     * Notifies listeners.
     * @param canvas the canvas
     */
    private void notifyListeners(VCanvas3D canvas) {
        for (V3DRenderListener l : renderListeners) {
            l.frameRendered(canvas);
        }
    }

    /**
     * Adds a render listener to this type represention.
     * @param listener the listener to add
     */
    public void addListener(V3DRenderListener listener) {
        renderListeners.add(listener);
    }

    /**
     * Removes a render listener from this type represention.
     * @param listener the listener to remove
     */
    public void removeListener(V3DRenderListener listener) {
        renderListeners.remove(listener);
    }

    /**
     * Starts playback.
     */
    public void startPlayback() {
        if (thread == null || !thread.isAlive()) {
            if (!isInterruptionRequest()) {
                frameSlider.setValue(0);
            }
            setInterruptionRequest(false);
            thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    while (currentFrame < numberOfFrames) {
                        if (isInterruptionRequest()) {
                            break;
                        }
                        try {
                            viewCurrentFrame();
                            Thread.sleep(1000 / fps);
                            currentFrame++;
                        } catch (InterruptedException ex) {
                            //
                        }
                    }
                    playButton.setText("Play");
                    notifyListenersStopped();
                }
            });

            notifyListenersStarted();
            thread.start();
        }
    }

    /**
     * Stops playback.
     */
    public void stopPlayback() {
        if (thread != null) {
            setInterruptionRequest(true);
            frameSlider.setValue(0);
        }
    }

    /**
     * Pauses playback.
     */
    public void pausePlayBack() {
        if (thread != null) {
            setInterruptionRequest(true);
        }
    }

    @Override
    synchronized public void setViewValue(
            Object o) {
        emptyView();

        value = o;

        if (!VGraphicsUtil.NO_3D) {

            final Shape3DArray shapes = (Shape3DArray) o;

            shapeGroup = new BranchGroup();

            shapeGroup.setCapability(BranchGroup.ENABLE_PICK_REPORTING);
            shapeGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
            shapeGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            shapeGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
            shapeGroup.setCapability(BranchGroup.ALLOW_DETACH);

            switchGroup = new Switch(Switch.CHILD_MASK);
            switchGroup.setCapability(Switch.ALLOW_SWITCH_WRITE);
            switchGroup.setCapability(Switch.ENABLE_PICK_REPORTING);
            switchGroup.setCapability(Switch.ALLOW_CHILDREN_EXTEND);
            switchGroup.setCapability(Switch.ALLOW_CHILDREN_READ);
            switchGroup.setCapability(Switch.ALLOW_CHILDREN_WRITE);
            switchGroup.setCapability(BranchGroup.ALLOW_DETACH);

            shapeGroup.addChild(switchGroup);

            universeCreator.getRootGroup().addChild(shapeGroup);

            for (final Shape3D shape : shapes) {
                BranchGroup g = new BranchGroup();

                g.setCapability(BranchGroup.ENABLE_PICK_REPORTING);
                g.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
                g.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
                g.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
                g.setCapability(BranchGroup.ALLOW_DETACH);

                g.addChild(shape);
                switchGroup.addChild(g);
            }

            visibleFrames =
                    new java.util.BitSet(switchGroup.numChildren());

            frameSlider.setMaximum(switchGroup.numChildren() - 1);
            frameSlider.setEnabled(true);

            numberOfFrames = shapes.size();

            currentFrame = 0;

//        startPlayback();

//        getCanvas().postRenderTask();
        }
    }

    @Override
    public void emptyView() {
        if (!VGraphicsUtil.NO_3D) {
            if (shapeGroup != null) {
                shapeGroup.detach();
                shapeGroup = null;
                visibleFrames = null;
            }
            if (frameSlider != null) {
                frameSlider.setEnabled(false);
            }
            if (canvas != null) {
                getCanvas().contentChanged();
                getCanvas().postRenderTask();
            }
        }
    }

    /**
     * Defines the Vcanvas3D size by evaluating a groovy script.
     * @param script the script to evaluate
     */
    private void setVCanvas3DSizeFromValueOptions(Script script) {
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
        }

        if (w != null && h != null) {
            // TODO find out why offset is 5
            getCanvas().setPreferredSize(new Dimension(w-5, h));
            getCanvas().setSize(new Dimension(w-5, h));
        }

        System.out.println(getValueOptions());
    }

    /**
     * Defines render options by evaluating a groovy script.
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

            if (getValueOptions().contains("fps")) {
                property = script.getProperty("fps");
            }

            if (property != null) {
                fps = (Integer) property;
            }
        }
    }

    @Override
    protected void evaluationRequest(Script script) {
        setVCanvas3DSizeFromValueOptions(script);
        setRenderOptionsFromValueOptions(script);
    }

    @Override
    public CustomParamData getCustomData() {
        CustomParamData result = super.getCustomData();

        Transform3D t3d = new Transform3D();
        getUniverseCreator().getRootGroup().getTransform(t3d);
        double[] values = new double[16];
        t3d.get(values);

        result.put(ORIENTATION_KEY, values);

        return result;
    }

    @Override
    public void evaluateCustomParamData() {
        Transform3D t3d = new Transform3D();
        double[] values = (double[]) super.getCustomData().get(ORIENTATION_KEY);
        if (values != null) {
            t3d.set(values);
            getUniverseCreator().getRootGroup().setTransform(t3d);
        }
    }

    /**
     * Returns the canvas used for rendering
     * @return the canvas used for rendering
     */
    public VCanvas3D getCanvas() {
        return canvas;
    }

    /**
     * Returns the canvas used for offscreen rendering
     * @return the canvas used for offscreen rendering
     */
    public VOffscreenCanvas3D getOffscreenCanvas() {
        return universeCreator.getOffscreenCanvas();
    }

    /**
     * Returns the number of frames per second.
     * @return the fps the number of frames per second
     */
    public int getFps() {
        return fps;
    }

    /**
     * Defines the number of frames per second.
     * @param fps the value to set
     */
    public void setFps(int fps) {
        this.fps = fps;
    }

    /**
     * Indicates whether interuption is requested.
     * @return <code>true</code> if interruption is requested;
     *         <code>false</code> otherwise
     */
    public boolean isInterruptionRequest() {
        return interruptionRequest;
    }

    /**
     * Defines whether to request interruption.
     * @param interruptionRequest the state to set
     */
    public void setInterruptionRequest(boolean interruptionRequest) {
        this.interruptionRequest = interruptionRequest;
    }

    /**
     * Returns the universe creator.
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
            emptyView();
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
        container.setPreferredSize(new Dimension(Short.MAX_VALUE,Short.MAX_VALUE));
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
}
