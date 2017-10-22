/* 
 * VUniverseCreator.java
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

import com.sun.j3d.exp.swing.JCanvas3D;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.GraphicsConfigTemplate;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

/**
 * Creates a Java 3D universe and the corresponding scene graph. It is the
 * default universe creator for 3D based type representations.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class VUniverseCreator implements UniverseCreator {

    private SimpleUniverse universe;
    private TransformGroup rootGroup = new TransformGroup();
    private TransformGroup camGroup;
    private VCanvas3D canvas;
    private VOffscreenCanvas3D offscreenCanvas;
    private BoundingSphere bounds
            = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0);
    private MouseWheelZoom zoomBehavior;

    /**
     * Constructor.
     */
    public VUniverseCreator() {
        //
    }

    @Override
    public void init(VCanvas3D canvas) {

        GraphicsConfigTemplate3D gct = new GraphicsConfigTemplate3D();
        gct.setSceneAntialiasing(GraphicsConfigTemplate.PREFERRED);

        this.canvas = canvas;
        canvas.setDoubleBuffered(true);
        
        // 12.05.2015 define default size since offscreencanvas will be null
        // otherwise
        // see: https://java.net/jira/browse/JAVA3D-386
        canvas.setSize(100, 100);

        canvas.getOffscreenCanvas3D().setDoubleBufferEnable(true);
        canvas.setResizeMode(JCanvas3D.RESIZE_IMMEDIATELY);

        offscreenCanvas = new VOffscreenCanvas3D(
                SimpleUniverse.getPreferredConfiguration());

        universe = new SimpleUniverse(getCanvas().getOffscreenCanvas3D());

        universe.getViewer().getView().addCanvas3D(offscreenCanvas);

        // offscreen rendering does not work with sceneantialiasing enabled!
        // universe.getViewer().getView().setSceneAntialiasingEnable(true);
        universe.getViewingPlatform().setNominalViewingTransform();

        universe.getViewer().getView().setMinimumFrameCycleTime(0);

        universe.getViewer().getView().setFrontClipDistance(0.1);
        universe.getViewer().getView().setBackClipDistance(100);

        BranchGroup scene = createSceneGraph();
        scene.compile();
        universe.addBranchGraph(scene);
    }

    @Override
    public void dispose() {
        if (universe != null) {
            universe.getViewer().getView().removeAllCanvas3Ds();
            universe.getViewer().setViewingPlatform(null);
            universe.removeAllLocales();
        }
    }

    /**
     * Creates and returns the scene graph.
     *
     * @return the scene graph
     */
    protected BranchGroup createSceneGraph() {
        BranchGroup objRoot = new BranchGroup();

        objRoot.addChild(rootGroup);

        rootGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        rootGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        rootGroup.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        rootGroup.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        rootGroup.setCapability(BranchGroup.ALLOW_DETACH);

        // initialize camera
        camGroup
                = universe.getViewingPlatform().getViewPlatformTransform();
        Transform3D camTranslate = new Transform3D();
        camTranslate.set(new Vector3f(0.0f, 0.0f, 20.0f));
        camGroup.setTransform(camTranslate);

        // define light source
        Color3f lColor1 = new Color3f(0.6f, 0.6f, 0.6f);
        Vector3f lDir1 = new Vector3f(0f, -1.0f, -1.0f);
        DirectionalLight lgt1 = new DirectionalLight(lColor1, lDir1);
        lgt1.setInfluencingBounds(bounds);
        objRoot.addChild(lgt1);

        // define light source
        Color3f lColor2 = new Color3f(0.6f, 0.6f, 0.6f);
        Vector3f lDir2 = new Vector3f(-1f, 1.0f, -1.0f);
        DirectionalLight lgt2 = new DirectionalLight(lColor2, lDir2);
        lgt2.setInfluencingBounds(bounds);
        objRoot.addChild(lgt2);

//        // define light source
//        Color3f lColor3 = new Color3f(0.6f, 0.6f, 0.6f);
//        Vector3f lDir3 = new Vector3f(1f, 1.0f, -1.0f);
//        DirectionalLight lgt3 = new DirectionalLight(lColor3, lDir3);
//        lgt3.setInfluencingBounds(bounds);
//        objRoot.addChild(lgt3);
//        // define light source
//        Color3f lColor4 = new Color3f(0.6f, 0.6f, 0.6f);
//        Vector3f lDir4 = new Vector3f(-1f, -1.0f, -1.0f);
//        DirectionalLight lgt4 = new DirectionalLight(lColor4, lDir4);
//        lgt4.setInfluencingBounds(bounds);
//        objRoot.addChild(lgt4);
        // standard mouse rotation
        MouseRotate rotateBehavior = new MouseRotate(canvas, rootGroup);
        objRoot.addChild(rotateBehavior);
        rotateBehavior.setSchedulingBounds(bounds);
        rotateBehavior.setFactor(0.01);

        // standard zoom behavior
        zoomBehavior = new MouseWheelZoom(canvas);
        zoomBehavior.setTransformGroup(rootGroup);
        zoomBehavior.setSchedulingBounds(bounds);
        objRoot.addChild(zoomBehavior);
        zoomBehavior.setFactor(0.05);

        // standard translate behavior
        MouseTranslate translateBehavior = new MouseTranslate(canvas);
        translateBehavior.setFactor(0.05);
        translateBehavior.setTransformGroup(rootGroup);
        translateBehavior.setSchedulingBounds(bounds);
        objRoot.addChild(translateBehavior);

//        rootGroup.addChild(new ColorCube(1));
        Background background = new Background();
        background.setColor(0.285f, 0.285f, 0.3f);
        background.setApplicationBounds(bounds);
        objRoot.addChild(background);

        return objRoot;
    }

    @Override
    public void setZoomFactor(double d) {
        zoomBehavior.setFactor(d);
    }

    @Override
    public double getZoomFactor() {
        return zoomBehavior.getFactor();
    }

    /**
     * Returns the universe.
     *
     * @return the universe
     */
    @Override
    public SimpleUniverse getUniverse() {
        return universe;
    }

    /**
     * Returns the canvas.
     *
     * @return the canvas
     */
    @Override
    public VCanvas3D getCanvas() {
        return canvas;
    }

    /**
     * Returns the root group. Always use the root group to attach 3d shapes
     * etc.
     *
     * @return the root group
     */
    @Override
    public TransformGroup getRootGroup() {
        return rootGroup;
    }

    /**
     * Returns the camera groop.
     *
     * @return the camera group
     */
    @Override
    public TransformGroup getCamGroup() {
        return camGroup;
    }

    /**
     * Returns the offscreen canvas.
     *
     * @return the offscreen canvas to set
     */
    @Override
    public VOffscreenCanvas3D getOffscreenCanvas() {
        return offscreenCanvas;
    }
}
