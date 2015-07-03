/* 
 * DefaultObjectRepresentation.java
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
package eu.mihosoft.vrl.reflection;

import eu.mihosoft.vrl.animation.AnimationTask;
import eu.mihosoft.vrl.animation.FrameListener;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.effects.EffectPanel;
import eu.mihosoft.vrl.effects.FadeInEffect;
import eu.mihosoft.vrl.effects.FadeOutEffect;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.lang.visual.StartObject;
import eu.mihosoft.vrl.lang.visual.StopObject;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import javax.swing.JPanel;
import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.CanvasActionListener;
import eu.mihosoft.vrl.visual.CanvasChild;
import eu.mihosoft.vrl.visual.CanvasWindow;
import eu.mihosoft.vrl.visual.Connections;
import eu.mihosoft.vrl.visual.Connector;
import eu.mihosoft.vrl.visual.IDArrayList;
import eu.mihosoft.vrl.visual.IDObject;
import eu.mihosoft.vrl.visual.IDTable;
import eu.mihosoft.vrl.visual.OrderedBoxLayout;
import eu.mihosoft.vrl.visual.TransparentPanel;
import eu.mihosoft.vrl.visual.VBoxLayout;
import eu.mihosoft.vrl.visual.VButton;
import eu.mihosoft.vrl.visual.VComboBox;
import eu.mihosoft.vrl.visual.VLayout;
import eu.mihosoft.vrl.visual.VSwingUtil;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

/**
 * An object representation is a Swing based visualization of a Java object.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class DefaultObjectRepresentation extends JPanel
        implements CanvasChild, IDObject {

    private static final long serialVersionUID = 6754769138031705583L;
    private List<DefaultMethodRepresentation> methods
            = new IDArrayList<DefaultMethodRepresentation>();
    private IDArrayList<IDTable> connectorIDTables;
    private ObjectDescription description;
    private VisualObjectInspector inspector;
    private TypeRepresentationFactory typeFactory;
    private EffectPanel selectionView;
    private VComboBox methodList;
    private JPanel methodView = new TransparentPanel();
    private Canvas mainCanvas;
    private int visualID;
    private String name;
    private OrderedBoxLayout methodLayout;
    private ControlFlowConnector controlFlowInput;
    private ControlFlowConnector controlFlowOutput;
    private Connector referenceInput;
    private Connector referenceOutput;
    private JComponent inputPanel = new TransparentPanel();
    private JComponent outputPanel = new TransparentPanel();
    private DefaultMethodRepresentation referenceMethod = null;

    private Map<MethodIdentifier, Integer> visualMethodIds
            = new HashMap<MethodIdentifier, Integer>();

    /**
     * Constructor.
     *
     * @param inspector the associated object inspector
     * @param typeFactory the type factory that is to be used for generating
     * type representations
     * @param oDesc the object description
     * @param mainCanvas the main canvas object
     */
    DefaultObjectRepresentation(VisualObjectInspector inspector,
            TypeRepresentationFactory typeFactory,
            ObjectDescription oDesc,
            Canvas mainCanvas) {
        init(inspector, typeFactory, oDesc, mainCanvas, null);
    }

    /**
     * Constructor.
     *
     * @param inspector the associated object inspector
     * @param typeFactory the type factory that is to be used for generating
     * type representations
     * @param oDesc the object description
     * @param mainCanvas the main canvas object
     */
    DefaultObjectRepresentation(VisualObjectInspector inspector,
            TypeRepresentationFactory typeFactory,
            ObjectDescription oDesc,
            Canvas mainCanvas, IDArrayList<IDTable> connectorIDTables) {

        init(inspector, typeFactory, oDesc, mainCanvas, connectorIDTables);
    }

    /**
     * Init method.
     *
     * @param inspector the associated object inspector
     * @param typeFactory the type factory that is to be used for generating
     * type representations
     * @param oDesc the object description
     * @param mainCanvas the main canvas object
     */
    private void init(VisualObjectInspector inspector,
            TypeRepresentationFactory typeFactory,
            ObjectDescription oDesc,
            Canvas mainCanvas, IDArrayList<IDTable> connectorIDTables) {

        if (connectorIDTables == null) {
            this.connectorIDTables = new IDArrayList<IDTable>();

            for (int i = 0; i < oDesc.getMethods().size(); i++) {
                this.connectorIDTables.add(new IDTable());
            }

        } else {
            this.connectorIDTables = connectorIDTables;
        }

        this.setInspector(inspector);
        this.setTypeFactory(typeFactory);

        this.setMainCanvas(mainCanvas);

        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);

        this.setLayout(layout);

        methodLayout = new OrderedBoxLayout(methodView, VBoxLayout.Y_AXIS) {

            @Override
            public void layoutContainer(Container target) {
                super.layoutContainer(target);
                getMethodOrder(); // updates visual method id
            }
        };

        methodView.setLayout(new VLayout(methodLayout));

        selectionView = new EffectPanel(mainCanvas);
        selectionView.getEffectManager().
                getEffects().add(new FadeInEffect(selectionView));
        selectionView.getEffectManager().
                getEffects().add(new FadeOutEffect(selectionView));

        this.add(selectionView);

        controlFlowInput = ControlFlowConnector.newInput(
                (VisualCanvas) mainCanvas, this);
        controlFlowOutput = ControlFlowConnector.newOutput(
                (VisualCanvas) mainCanvas, this);

        inputPanel.setBorder(new EmptyBorder(3, 3, 0, 0));
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        inputPanel.add(Box.createHorizontalStrut(3));
        inputPanel.add(getControlFlowInput());
        inputPanel.add(Box.createHorizontalGlue());

        add(inputPanel);

        this.add(methodView);

        outputPanel.setBorder(new EmptyBorder(0, 0, 3, 3));
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.X_AXIS));
        outputPanel.add(Box.createHorizontalGlue());
        outputPanel.add(getControlFlowOutput());
        outputPanel.add(Box.createHorizontalStrut(3));

        add(outputPanel);

        createSelectionView();
        setDescription(oDesc);
        initRepresentation();
        initSelectionView();

//        referenceInput = new VConnector((VisualCanvas) mainCanvas);
//        referenceInput.setValueObject(new InputObject(referenceInput, this));
//        referenceInput.setType(ConnectorType.INPUT);
//        referenceOutput = new VConnector((VisualCanvas) mainCanvas);
//        referenceOutput.setValueObject(new OutputObject(referenceInput, this));
//        referenceOutput.setType(ConnectorType.OUTPUT);
//        // set preferred size is necessary, because minimum size is ignored by
//        // the layout manager
//        this.setMinimumSize(new Dimension(Short.MIN_VALUE, 40));
//        this.setPreferredSize(new Dimension(Short.MIN_VALUE, 40));
//        setMinimumSize(null);
//        setMaximumSize(null);
//        setPreferredSize(null);
        this.setOpaque(false);
    }

    private void updateSelectionViewVisibility() {

        // if no items are left selectionView is no longer needed
        if (methodList.getItemCount() == 0
                || getInspector().getObject(getObjectID()) instanceof StartObject
                || getInspector().getObject(getObjectID()) instanceof StopObject) {
            hideSelectionView();
        }
    }

    /**
     * Adds a given method representation to the main view.
     *
     * @param mDesc the method representation to add
     * @param visualMethodId the visual method id
     * @return the method representation that visualizes the specified method
     */
    public DefaultMethodRepresentation addMethodToView(
            final MethodDescription mDesc, int visualMethodId) {

        DefaultMethodRepresentation mRep
                = getMethodByMethodDescription(mDesc, visualMethodId);

        if (isNotallowedOnView(mDesc) || mRep != null) {

            updateSelectionViewVisibility();

            return mRep;
        }

        mRep
                = new DefaultMethodRepresentation(this);
        mRep.initRepresentation(
                mDesc, getConnectorIDTables().getById(
                        mDesc.getMethodID()));
        mRep.setVisualMethodID(visualMethodId);

        getMethods().add(mRep);

        if (mRep.isReferenceMethod() || mRep.isCustomReferenceMethod()) {
            updateSelectionViewVisibility();
            return null;
        }

        if (mRep.isInitializer()) {
            // check whether constructor method added to view
            for (Component c : methodView.getComponents()) {
                if (c instanceof DefaultMethodRepresentation) {
                    DefaultMethodRepresentation m
                            = (DefaultMethodRepresentation) c;

                    if (m.isInitializer()) {
                        updateSelectionViewVisibility();
                        // a constructor has been already added
                        // we don't allow a second one
                        return null;
                    }
                }
            }
        }

        // TODO start appearing with fade in
        // currently this is done inside of FadeIn effect
        //                method.setVisible(true);
        
        MethodInfo mInfo = mDesc.getMethodInfo();
        
        if (mInfo!=null && mInfo.num()==1) {
            methodList.removeItem(mDesc);
        }
        
        methodView.add(mRep);

        // prevents drawing bugs (setVisible(true) is called later)
        mRep.setVisible(false);

        if (mRep.isMinimized()) {
            mRep.minimize();
        }

        if (!mainCanvas.isDisableEffects()
                && !mRep.getEffectManager().isDisabled()) {
            mRep.getEffectManager().
                    startAppearanceEffect(mRep, mRep.getStyle().
                            getBaseValues().getDouble(
                                    CanvasWindow.FADE_IN_DURATION_KEY));
        } else {
            mRep.getEffectManager().
                    startAppearanceEffect(mRep, 0.0);
            mRep.setVisible(true);
        }

        updateSelectionViewVisibility();

        // this is done to prevent layout bugs regarding object minimization
        // via double click on title bar
        if (getParentWindow() != null) {

            setMinimumSize(null);
            setMaximumSize(null);
            setPreferredSize(null);

            getParentWindow().setMinimumSize(null);
            getParentWindow().setMaximumSize(null);
            getParentWindow().setPreferredSize(null);
            getParentWindow().revalidate();
        }

        return mRep;
    }

    /**
     * Removes a given method representation from the view.
     *
     * @param method the method representation to remove from view
     */
    public void removeMethodFromView(final DefaultMethodRepresentation method) {

        if (method.isVisible()) {

            // delete data connections when hiding method
            for (Connector c : method.getConnectors()) {
                mainCanvas.getDataConnections().removeAllWith(c);
            }

            FrameListener frameListener = new AnimationTask() {
                @Override
                public void firstFrameStarted() {
                    //
                }

                @Override
                public void frameStarted(double time) {
                    //
                }

                @Override
                public void lastFrameStarted() {
                    getMethodView().remove(method);
                    method.dispose();
                }
            };

            if (!mainCanvas.isDisableEffects()) {
                method.getEffectManager().
                        startDisappearanceEffect(method, method.getStyle().
                                getBaseValues().getDouble(
                                        CanvasWindow.FADE_OUT_DURATION_KEY), frameListener);
            } else {
                method.getEffectManager().
                        startDisappearanceEffect(method, 0.0,
                                frameListener);
                method.setVisible(false);
                getMethodView().remove(method);
            }

            // we ignore reference methods
            if (method.getDescription().getMethodType()
                    != MethodType.REFERENCE
                    && method.getDescription().getMethodType()
                    != MethodType.CUSTOM_REFERENCE) {
                
                MethodInfo mInfo = method.getDescription().getMethodInfo();

                if (mInfo!=null && !mInfo.hide() && mInfo.num()==1) {
                    methodList.addItem(method.getDescription(),
                            method.getDescription().getSignature());
                }

                getMethods().remove(method);

                if (!selectionView.isVisible()) {
                    showSelectionView();
                }
            }

            // this is done to prevent layout bugs regarding object minimization
            // via double click on title bar
            if (getParentWindow() != null) {
                setMinimumSize(null);
                setMaximumSize(null);
                setPreferredSize(null);

                getParentWindow().setMinimumSize(null);
                getParentWindow().setMaximumSize(null);
                getParentWindow().setPreferredSize(null);
                getParentWindow().revalidate();
            }

        }
    }

    /**
     * Creates the selection view. The selction view contains a list of all
     * methods that are not shown and provides the possibility to add hidden
     * methods to the main view.
     */
    public void createSelectionView() {

        methodList = new VComboBox(methods.toArray());

        Dimension preferredSize = methodList.getPreferredSize();
        Dimension size = new Dimension(80, preferredSize.height);

        methodList.setPreferredSize(size);

        selectionView.add(methodList);

        JButton button = new VButton("add");

        selectionView.add(button);

        button.addActionListener(new CanvasActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get the selected list item make it visible and remove
                // it from the list
                MethodDescription selected
                        = (MethodDescription) methodList.getSelectedItem();

                int visualMethodID = -1;

                if (selected != null) {
//                    boolean removeMethodAnimationRunning =
//                            selected.getEffectManager().
//                            getEffectByName("FadeOutEffect").isActive();

                    // This is necessary to prevent that methods that are
                    // currently removing from view will be added.
                    // It would result in a method that is added to the view
                    // but will be made invisible when the remove method effect
                    // stops.
//                    if (!removeMethodAnimationRunning) {
                    addMethodToView(selected, visualMethodID);
//                    }
                }
            }

        });
    }

    public int computeNextVisualMethodID(MethodDescription selected) {
        int visualMethodID = 0;
        // (0,0) since we are only interested in the method signature
        // and not in the visualization
        MethodIdentifier mId = selected.toMethodIdentifier(0, 0);
        if (visualMethodIds.containsKey(mId)) {
            visualMethodID = visualMethodIds.get(mId) + 1;
        }
        visualMethodIds.put(mId, visualMethodID);

        return visualMethodID;
    }

    /**
     * Call this only after createSeleectionView() and after object description
     * convertion, i.e., setObjectDescription()
     */
    private void initSelectionView() {
        selectionView.getEffectManager().setDisabled(true);

//        selectionView.addMouseListener(new MouseAdapter() {
//
//            @Override
//            public void mouseEntered(MouseEvent e) {
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//            }
//        });
        setMinimumSize(null);
        setMaximumSize(null);
        setPreferredSize(null);

        revalidate();

        // this feature has been disabled due to usability problem:
        // if an object is initialized from a reference, constructors must not
        // be used!
//        // if we only have one non-default initializer (with parameters)
//        // then add it to the view
//        ArrayList<MethodDescription> initializers =
//                new ArrayList<MethodDescription>();
//        for (MethodDescription m : getDescription().getMethods()) {
//            if (m.getMethodInfo() != null
//                    && m.getMethodInfo().initializer()
//                    && !m.getMethodInfo().noGUI() 
//                    && m.getParameterTypes().length > 1) {
//                initializers.add(m);
//            }
//        }
//        if (initializers.size() == 1) {
//            addMethodToView(initializers.get(0));
//        }
        for (MethodDescription m : getDescription().getMethods()) {

            MethodInfo mInfo = m.getMethodInfo();

            boolean isReferenceMethod
                    = m.getMethodType() == MethodType.REFERENCE
                    || m.getMethodType() == MethodType.CUSTOM_REFERENCE;

            if (!isReferenceMethod) {

                if (methodNeedsListEntry(mInfo)) {
                    methodList.addItem(m, m.getSignature());
                }

                if (!getMainCanvas().isLoadingSession()) {
                    if (mInfo != null) {

                        // TODO duplicated check (see DefaultMethodRepresentation.initRepresentation())
                        boolean hideMethod
                                = mInfo.hide() && !mInfo.hideCloseIcon();

                        if (!hideMethod) {
                            int visualMethodID = computeNextVisualMethodID(m);
                            addMethodToView(m, visualMethodID);
                        }
                    } else if (!isReferenceMethod) {
                        // if this method does not have a method info and
                        // only this method and a reference method are available
                        // then show this method as default
                        if (getDescription().getMethods().size() == 2) {
                            int visualMethodID = computeNextVisualMethodID(m);
                            addMethodToView(m, visualMethodID);
                        }
                    }
                }
            }
        }

        boolean isStartOrStopObject
                = getInspector().getObject(getObjectID()) instanceof StartObject
                || getInspector().getObject(getObjectID()) instanceof StopObject;

        if (methodList.getItemCount() > 0) {
            showSelectionView();
        }

        if (isStartOrStopObject) {
            hideSelectionView();
        }

        selectionView.getEffectManager().setDisabled(false);
    }

    private boolean methodNeedsListEntry(MethodInfo mInfo) {
        boolean methodNeedsListEntry = mInfo == null;
        if (mInfo != null) {
            methodNeedsListEntry = methodNeedsListEntry || !mInfo.noGUI();
            methodNeedsListEntry = methodNeedsListEntry && (mInfo.num() > 1);
        }
        return methodNeedsListEntry;
    }

    public boolean isNotallowedOnView(MethodDescription mDesc) {

        int numberOfInstances = getMethodsByMethodDescription(mDesc).size();

        int numberOfallowedInstances;

        if (mDesc.getMethodInfo() != null) {
            numberOfallowedInstances = mDesc.getMethodInfo().num();
        } else {
            numberOfallowedInstances = Integer.MAX_VALUE;
        }

        return numberOfInstances >= numberOfallowedInstances;
    }

    /**
     * Shows the selection view.
     */
    public void showSelectionView() {

        if (!mainCanvas.isDisableEffects()
                && !selectionView.getEffectManager().isDisabled()) {
            selectionView.getEffectManager().
                    startAppearanceEffect(selectionView, getParentWindow().
                            getStyle().
                            getBaseValues().getDouble(
                                    CanvasWindow.FADE_IN_DURATION_KEY));
        } else {
            selectionView.getEffectManager().
                    startAppearanceEffect(selectionView, 0.0);
            selectionView.setVisible(true);
        }
    }

    /**
     * Hides the selection view.
     */
    public void hideSelectionView() {
        if (!mainCanvas.isDisableEffects()
                && !selectionView.getEffectManager().isDisabled()) {
            selectionView.getEffectManager().
                    startDisappearanceEffect(selectionView, getParentWindow().
                            getStyle().
                            getBaseValues().getDouble(
                                    CanvasWindow.FADE_OUT_DURATION_KEY));
        } else {
            selectionView.getEffectManager().
                    startDisappearanceEffect(selectionView, 0.0);

            boolean isStartOrStopObject
                    = getInspector().getObject(getObjectID()) instanceof StartObject
                    || getInspector().getObject(getObjectID()) instanceof StopObject;

            if (methodList.getItemCount() == 0 || isStartOrStopObject) {
                selectionView.setVisible(false);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        //
    }

    void setDescription(ObjectDescription description) {
        this.description = description;

        if (methods != null) {
            for (DefaultMethodRepresentation mRep : methods) {

                for (MethodDescription mDesc : description.getMethods()) {

                    // we ignore visual id here (0) because we want to 
                    // reassign description
                    if (mDesc.toMethodIdentifier(0, mRep.getVisualMethodID()).
                            equals(mRep.getDescription().
                                    toMethodIdentifier(0, mRep.getVisualMethodID()))) {
                        mRep.setDescription(mDesc);
                        break;
                    }
                }
            }

            // Everything below is just for updating method desc instances
            // in methodList
            // copy items in methodList to array
            MethodDescription[] methodListEntries
                    = new MethodDescription[methodList.getItemCount()];

            for (int i = 0; i < methodListEntries.length; i++) {
                methodListEntries[i]
                        = (MethodDescription) methodList.getItemAt(i);
            }

            // delete old method desc objects
            methodList.removeAllItems();

            // replace method descriptions 
            // (otherwise methods that are not shown won't operate on the
            // correct object id!)
            for (int i = 0; i < methodListEntries.length; i++) {
                MethodDescription mDescOld = methodListEntries[i];

                for (MethodDescription mDescNew : description.getMethods()) {

                    // we ignore visual id's here (0,0) because we want to 
                    // reassign description and only care about the signature
                    if (mDescNew.toMethodIdentifier(0, 0).
                            equals(mDescOld.toMethodIdentifier(0, 0))) {

                        // add method
                        methodList.addItem(mDescNew);

                        break;
                    }
                }
            }
        }
    }

    /**
     * Defines the object description that is to be visulaized.
     */
    protected void initRepresentation() {
        name = this.getDescription().getName();

        name = VLangUtils.slashToDot(
                VLangUtils.shortNameFromFullClassName(name));

        if (getDescription().getInfo() != null
                && !getDescription().getInfo().controlFlowIn()) {
            inputPanel.setBorder(null);
            inputPanel.removeAll();
        }
        if (getDescription().getInfo() != null
                && !getDescription().getInfo().controlFlowOut()) {
            outputPanel.setBorder(new EmptyBorder(4, 0, 0, 0));
            outputPanel.removeAll();
        }

        // init parameter types
        for (MethodDescription m : this.getDescription().getMethods()) {

            boolean isReferenceMethod = m.getMethodInfo() != null
                    && (m.getMethodType() == MethodType.REFERENCE
                    || m.getMethodType() == MethodType.CUSTOM_REFERENCE);

            if (isReferenceMethod) {

                DefaultMethodRepresentation mRep
                        = new DefaultMethodRepresentation(this);
                mRep.initRepresentation(
                        m, getConnectorIDTables().getById(m.getMethodID()));

                getMethods().add(mRep);

                referenceMethod = mRep;
                referenceInput = mRep.getParameter(0).getConnector();
                referenceOutput = mRep.getReturnValue().getConnector();
            }
        }
    }

    /**
     * Invokes a method specified by name. Calls the first method with the
     * specified name.
     *
     * @param inspector the inspector that ist to invoke the method
     * @param methodName the name of the method that is to be invoked
     */
    void invoke(VisualObjectInspector inspector, String methodName)
            throws InvocationTargetException {
        DefaultMethodRepresentation method = null;
        for (DefaultMethodRepresentation m : getMethods()) {
            if (m.getName().equals(methodName)) {
                method = m;
                break;
            }
        }
        if (method != null) {
            method.invoke(inspector);
        }
    }

    /**
     * Returns the inspector associated with the object representation.
     *
     * @return the inspector associated with the object representation
     */
    public VisualObjectInspector getInspector() {
        return inspector;
    }

    /**
     * Defines the inspector that is to be associated with this object
     * representation
     *
     * @param inspector the inspector that is to be associated with this object
     * representation
     */
    public void setInspector(VisualObjectInspector inspector) {
        this.inspector = inspector;
    }

    /**
     * Returns the type factory that creates type representations.
     *
     * @return the type factory that creates type representations
     */
    public TypeRepresentationFactory getTypeFactory() {
        return typeFactory;
    }

    /**
     * Defines the type factory that is to be used for creating type
     * representations
     *
     * @param typeFactory the type factory that is to be used for creating type
     * representations
     */
    public void setTypeFactory(TypeRepresentationFactory typeFactory) {
        this.typeFactory = typeFactory;
    }

    @Override
    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    @Override
    public void setMainCanvas(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;
    }

    /**
     * Returns all methods of the visualized object.
     *
     * @return all methods of the visualized object
     */
    public List<DefaultMethodRepresentation> getMethods() {
        return methods;
    }

    /**
     * Returns method representation by signature (name and parameter types) and
     * not by id.
     *
     * @param mDesc the method description
     * @param visualMethodID the visual method id of the requested method
     * representation
     * @return returns method representation by signature (name and parameter
     * types) if method representation exists; returns <code>null</code>
     * otherwise
     */
    public DefaultMethodRepresentation getMethodByMethodDescription(
            MethodDescription mDesc, int visualMethodID) {
        DefaultMethodRepresentation result = null;

        for (DefaultMethodRepresentation m : getMethods()) {

            if (m.getVisualMethodID() != visualMethodID) {
                System.out.println("mismatch: " + m.getVisualMethodID() + " == " + visualMethodID);
                continue;
            }

            boolean equalName
                    = mDesc.getMethodName().equals(m.getDescription().
                            getMethodName());

            boolean equalPamareters = true;

            int paramLength1 = mDesc.getParameterTypes().length;
            int paramLength2 = m.getDescription().getParameterTypes().length;

            if (paramLength1 != paramLength2) {
                equalPamareters = false;
            }

            for (int i = 0; i < mDesc.getParameterTypes().length && equalPamareters; i++) {

                if (mDesc.getParameterTypes()[i]
                        != m.getDescription().getParameterTypes()[i]) {
                    equalPamareters = false;
                    System.out.println("mismatch: " + m.getName() + " == " + mDesc.getMethodName());
                    break;
                }
            }

            boolean equalSignature = equalName && equalPamareters;

            if (equalSignature) {
                result = m;
            }
        }

        return result;
    }

    /**
     * Returns method representation by signature (name and parameter types) and
     * not by id.
     *
     * @param mDesc the method description
     * @return returns method representation by signature (name and parameter
     * types) if method representation exists; returns <code>null</code>
     * otherwise
     */
    public List<DefaultMethodRepresentation> getMethodsByMethodDescription(
            MethodDescription mDesc) {
        List<DefaultMethodRepresentation> result
                = new ArrayList<DefaultMethodRepresentation>();

        for (DefaultMethodRepresentation m : getMethods()) {

            boolean equalName
                    = mDesc.getMethodName().equals(m.getDescription().
                            getMethodName());

            boolean equalPamareters = true;

            int paramLength1 = mDesc.getParameterTypes().length;
            int paramLength2 = m.getDescription().getParameterTypes().length;

            if (paramLength1 != paramLength2) {
                equalPamareters = false;
            }

            for (int i = 0; i < mDesc.getParameterTypes().length && equalPamareters; i++) {

                if (mDesc.getParameterTypes()[i]
                        != m.getDescription().getParameterTypes()[i]) {
                    equalPamareters = false;
                    break;
                }
            }

            boolean equalSignature = equalName && equalPamareters;

            if (equalSignature) {
                result.add(m);
            }
        }

        return result;
    }

    /**
     * Returns method representation by signature (name and parameter types).
     *
     * @param name the method name
     * @param parameterTypeNames the parameter type names
     * @return returns method representation by signature (name and parameter
     * types) if method representation exists; returns <code>null</code>
     * otherwise
     */
    public DefaultMethodRepresentation getMethodBySignature(
            String name, List<String> parameterTypeNames) {
        DefaultMethodRepresentation result = null;

        for (DefaultMethodRepresentation m : getMethods()) {

            boolean equalName
                    = name.equals(m.getDescription().
                            getMethodName());

            boolean equalParameters = true;

            if (m.getDescription().getParameterTypes().length
                    != parameterTypeNames.size()) {
                equalParameters = false;
            } else {

                for (int i = 0; i < parameterTypeNames.size(); i++) {
                    if (!parameterTypeNames.get(i).equals(
                            m.getDescription().
                            getParameterTypes()[i].getName())) {
//                        System.out.println(" >> equal false! " 
//                        + parameterTypeNames.get(i) + " != " 
//                        +m.getDescription().getParameterTypes()[i].getName());
                        equalParameters = false;
                        break;
                    }
                }
            }

            boolean equalSignature = equalName && equalParameters;

            if (equalSignature) {
                result = m;
            }
        }

        return result;
    }

    /**
     * Returns method representation by signature (name and parameter types).
     *
     * @param name the method name
     * @param parameterTypeNames the parameter type names
     * @return returns method description by signature (name and parameter
     * types) if method representation exists; returns <code>null</code>
     * otherwise
     */
    public MethodDescription getMethodDescriptionBySignature(
            String name, List<String> parameterTypeNames) {
        MethodDescription result = null;

        for (MethodDescription m : getDescription().getMethods()) {
//            System.out.println(">> Method: " + m.getName());
            boolean equalName
                    = name.equals(m.getMethodName());

            boolean equalPamareters = true;

            if (m.getParameterTypes().length
                    != parameterTypeNames.size()) {
                equalPamareters = false;
            } else {

                for (int i = 0; i < parameterTypeNames.size(); i++) {
                    if (!parameterTypeNames.get(i).equals(
                            m.getParameterTypes()[i].getName())) {
//                        System.out.println(" >> equal false!");
                        equalPamareters = false;
                        break;
                    }
                }
            }

            boolean equalSignature = equalName && equalPamareters;

            if (equalSignature) {
                result = m;
            }
        }

        return result;
    }

    public DefaultMethodRepresentation getMethodByIdentifier(
            MethodIdentifier id) {

        if (id.getParameterTypeNames() != null) {
            return getMethodBySignature(id.getMethodName(),
                    Arrays.asList(id.getParameterTypeNames()));
        } else {
            System.err.println(">> DefaultObjectRepresentation."
                    + "getMethodByIdentifier(): "
                    + "parameterTypeNames == null!");
            return getMethodBySignature(
                    id.getMethodName(), new ArrayList<String>());
        }
    }

    /**
     * Returns method representation by signature (name and parameter types).
     *
     * @param name the method name
     * @param parameters the parameter types
     * @return returns method representation by signature (name and parameter
     * types) if method representation exists; returns <code>null</code>
     * otherwise
     */
    public DefaultMethodRepresentation getMethodBySignature(
            String name, Class<?>... parameters) {
        DefaultMethodRepresentation result = null;

        for (DefaultMethodRepresentation m : getMethods()) {
//            System.out.println(">> Method: " + m.getName());
            boolean equalName
                    = name.equals(m.getDescription().
                            getMethodName());

            boolean equalPamareters = true;

            if (m.getDescription().getParameterTypes().length
                    != parameters.length) {
                equalPamareters = false;
            } else {

                for (int i = 0; i < parameters.length; i++) {
//                    System.out.println(">> Param: " + parameters[i] + "=="
//                            + m.getDescription().getParameterTypes()[i]);
                    if (!parameters[i].getName().equals(
                            m.getDescription().getParameterTypes()[i].getName())) {
//                        System.out.println(" >> equal false!");
                        equalPamareters = false;
                        break;
                    }
                }
            }

            boolean equalSignature = equalName && equalPamareters;

            if (equalSignature) {
                result = m;
            }
        }

        return result;
    }

    /**
     * Returns the ID of the object description.
     *
     * @return the ID of the object description
     */
    public int getObjectID() {
        return getDescription().getID();
    }

    /**
     * Returns all connectors of the object representation.
     *
     * @return all connectors of the object representation
     */
    public ArrayList<Connector> getConnectors() {
        ArrayList<Connector> connectors = new ArrayList<Connector>();
        for (DefaultMethodRepresentation m : methods) {
            for (Connector c : m.getConnectors()) {
                connectors.add(c);
            }
        }
        return connectors;
    }

    /**
     * Returns the canvas window that contains this object representation.
     *
     * @return the canvas window that contains this object representation
     */
    public CanvasWindow getParentWindow() {
        return (CanvasWindow) VSwingUtil.getParent(this, CanvasWindow.class);
    }

//    private void writeObject(ObjectOutputStream oos) throws IOException {
//        
//
//        oos.defaultWriteObject();
//    }
    //
    /**
     * Disposes the method representations of this object representation.
     */
    public void dispose() {
        for (DefaultMethodRepresentation m : getMethods()) {
            try {
                m.dispose();
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return the connectorIDTables
     */
    public IDArrayList<IDTable> getConnectorIDTables() {
        return connectorIDTables;
    }

    /**
     * Returns the visual id of this representation.
     *
     * @return the visual id of this representation
     */
    @Override
    public int getID() {
        return visualID;
    }

    /**
     * Defines the visual id of this representation.
     *
     * @param ID the id to set
     */
    @Override
    public void setID(int ID) {
        this.visualID = ID;
    }

    /**
     * @return the description
     */
    public ObjectDescription getDescription() {
        return description;
    }

    public void setMethodOrder(ArrayList<MethodIdentifier> order) {

        // we ignore empty order 
        // (may happen when loading from files before 26.09.2011)
        if (order == null) {
            return;
        }

        ArrayList<Component> methodOrder = new ArrayList<Component>();

        for (MethodIdentifier id : order) {

            DefaultMethodRepresentation mRep = getMethodByIdentifier(id);
            if (mRep == null) {
                System.err.println(">> Error: setMethodOrder(): Method "
                        + id.getMethodName() + "() does not exist.");

            } else if (!mRep.isReferenceMethod() && !mRep.isCustomReferenceMethod()) {
                methodOrder.add(mRep);
            }
        }

        getMethodLayout().setOrder(methodOrder);
    }

    /**
     * Used for source code generation.
     *
     * @return
     */
    public ArrayList<MethodIdentifier> getMethodOrder() {
        ArrayList<MethodIdentifier> result = new ArrayList<MethodIdentifier>();

        // add reference method if in use
        Connections connections = getMainCanvas().getDataConnections();
        boolean inputConnected = connections.alreadyConnected(
                getReferenceInput());
        boolean outputConnected = connections.alreadyConnected(
                getReferenceOutput());
        if (inputConnected || outputConnected) {
            result.add(new MethodIdentifier(getReferenceMethod()));
        }

        visualMethodIds.clear();

        // methods are an order if they have been added to the methodView
        // container
        for (Component comp : getMethodLayout().getOrder()) {

            if (comp instanceof DefaultMethodRepresentation) {

                if (comp.isVisible()) {

                    DefaultMethodRepresentation mRep
                            = (DefaultMethodRepresentation) comp;

                    int visualMethodID = computeNextVisualMethodID(
                            mRep.getDescription());

                    mRep.setVisualMethodID(visualMethodID);

                    result.add(
                            new MethodIdentifier(mRep.
                                    getDescription(),
                                    visualID, mRep.getVisualMethodID()));

                    for (DefaultMethodRepresentation m : mRep.getProperties()) {
                        result.add(
                                new MethodIdentifier(m.
                                        getDescription(),
                                        visualID, m.getVisualMethodID()));
                    }
                }
//            }
//            
//            else if (comp instanceof RepresentationGroup) {
//                Collection<Component> comps =
//                        VSwingUtil.getAllChildren((Container) comp,
//                        DefaultMethodRepresentation.class);
//
//                for (Component m : comps) {
//                    result.add(
//                            new MethodIdentifier(
//                            ((DefaultMethodRepresentation) m).getDescription(),
//                            visualID));
//                }

            } else {
                throw new IllegalStateException(
                        "Unsupported components in method container!");
            }
        }

        return result;
    }

    /**
     * Used for visual invocation.
     *
     * @return
     */
    public ArrayList<DefaultMethodRepresentation> getInvocationList() {
        ArrayList<DefaultMethodRepresentation> result
                = new ArrayList<DefaultMethodRepresentation>();

        // add reference method if in use and not already added
        Connections connections = getMainCanvas().getDataConnections();
        boolean inputConnected = connections.alreadyConnected(
                getReferenceInput());
        boolean outputConnected = connections.alreadyConnected(
                getReferenceOutput());
        if ((inputConnected || outputConnected)
                && !result.contains(getReferenceMethod())) {
            result.add(0, getReferenceMethod());
        }

        for (Component comp : getMethodLayout().getOrder()) {
            if (comp instanceof DefaultMethodRepresentation) {

                if (comp.isVisible()) {

                    DefaultMethodRepresentation mRep
                            = (DefaultMethodRepresentation) comp;

                    result.add(mRep);

                    result.addAll(mRep.getProperties());
                }
//            }
//            else if (comp instanceof RepresentationGroup) {
//                Collection<Component> comps =
//                        VSwingUtil.getAllChildren((Container) comp,
//                        DefaultMethodRepresentation.class);
//
//                for (Component m : comps) {
//                    result.add((DefaultMethodRepresentation) m);
//                }

            } else {
                throw new IllegalStateException(
                        "Unsupported components in method container!");
            }
        }

        return result;
    }

//    @Override
//    protected void finalize() throws Throwable {
//
//        System.out.println(getClass().getName() + ": Finalize!!!");
//
//        super.finalize();
//    }
    /**
     * @return the methodLayout
     */
    OrderedBoxLayout getMethodLayout() {
        return methodLayout;
    }

    /**
     * @return the methodView
     */
    JPanel getMethodView() {
        return methodView;
    }

    /**
     * @return the controlFlowInput
     */
    public ControlFlowConnector getControlFlowInput() {
        return controlFlowInput;
    }

    /**
     * @return the controlFlowOutput
     */
    public ControlFlowConnector getControlFlowOutput() {
        return controlFlowOutput;
    }

    /**
     * @return the referenceInput
     */
    public Connector getReferenceInput() {
        return referenceInput;
    }

    /**
     * @return the referenceOutput
     */
    public Connector getReferenceOutput() {
        return referenceOutput;
    }

    DefaultMethodRepresentation getReferenceMethod() {

        return referenceMethod;
    }
}
//
//class OutputObject implements ValueObject {
//
//    private Connector c;
//    private VisualObjectInspector inspector;
//    private DefaultObjectRepresentation oRep;
//    private Class<?> type;
//
//    public OutputObject(Connector c, DefaultObjectRepresentation oRep) {
//        this.c = c;
//        this.inspector = ((VisualCanvas) oRep.getMainCanvas()).getInspector();
//        this.oRep = oRep;
//        type = inspector.getObject(oRep).getClass();
//    }
//
//    @Override
//    public Class<?> getType() {
//        System.out.println("O: type = " + type);
//        return type;
//    }
//
//    @Override
//    public CanvasWindow getParentWindow() {
//        return oRep.getParentWindow();
//    }
//
//    @Override
//    public Component getParent() {
//        return c.getParent();
//    }
//
//    @Override
//    public void setValue(Object o) {
//        // nothing to do
//        System.out.println("O: Replace Instance : nothing to do");
//    }
//
//    @Override
//    public Object getValue() {
//
//        System.out.println("O: Return Instance");
//
//        return inspector.getObject(oRep);
//    }
//
//    @Override
//    public void setOutdated() {
//        //
//    }
//
//    @Override
//    public ConnectionResult compatible(ValueObject obj) {
//        if (getType().isAssignableFrom(obj.getType())
//                && getParentWindow() != obj.getParentWindow()) {
//            return new ConnectionResult(null, ConnectionStatus.VALID);
//        }
//
//        return new ConnectionResult(null,
//                ConnectionStatus.ERROR_VALUE_TYPE_MISSMATCH);
//    }
//
//    @Override
//    public Connector getConnector() {
//        return c;
//    }
//}
//
//class InputObject implements ValueObject {
//
//    private Connector c;
//    private VisualObjectInspector inspector;
//    private DefaultObjectRepresentation oRep;
//    private Class<?> type;
//
//    public InputObject(Connector c, DefaultObjectRepresentation oRep) {
//        this.c = c;
//        this.inspector = ((VisualCanvas) oRep.getMainCanvas()).getInspector();
//        this.oRep = oRep;
//        type = inspector.getObject(oRep).getClass();
//    }
//
//    @Override
//    public Class<?> getType() {
//        System.out.println("I: type = " + type);
//        return type;
//    }
//
//    @Override
//    public CanvasWindow getParentWindow() {
//        return oRep.getParentWindow();
//    }
//
//    @Override
//    public Component getParent() {
//        return c.getParent();
//    }
//
//    @Override
//    public void setValue(Object o) {
//        // replace instance
//        System.out.println("I: Replace Instance");
//
//        if (o != null) {
//            inspector.replaceInstance(oRep, o);
//        }
//    }
//
//    @Override
//    public Object getValue() {
//
//        System.out.println("I: Return Instance");
//
//        return inspector.getObject(oRep);
//    }
//
//    @Override
//    public void setOutdated() {
//        //
//    }
//
//    @Override
//    public ConnectionResult compatible(ValueObject obj) {
//
//        if (getType().isAssignableFrom(obj.getType())
//                && getParentWindow() != obj.getParentWindow()) {
//            return new ConnectionResult(null, ConnectionStatus.VALID);
//        }
//
//        return new ConnectionResult(null,
//                ConnectionStatus.ERROR_VALUE_TYPE_MISSMATCH);
//    }
//
//    @Override
//    public Connector getConnector() {
//        return c;
//    }
//}
