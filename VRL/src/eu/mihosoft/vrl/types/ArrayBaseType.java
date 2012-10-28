/* 
 * ArrayBaseType.java
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

import eu.mihosoft.vrl.animation.AnimatedLayout;
import eu.mihosoft.vrl.animation.FrameListener;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.reflection.CustomParamData;
import eu.mihosoft.vrl.reflection.DefaultMethodRepresentation;
import eu.mihosoft.vrl.reflection.RepresentationType;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.reflection.TypeRepresentationContainer;
import eu.mihosoft.vrl.reflection.TypeRepresentationFactory;
import eu.mihosoft.vrl.reflection.VConnector;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.system.VClassLoaderUtil;
import eu.mihosoft.vrl.visual.CanvasCapabilities;
import eu.mihosoft.vrl.visual.CanvasWindow;
import eu.mihosoft.vrl.visual.CapabilityChangedListener;
import eu.mihosoft.vrl.visual.CapabilityManager;
import eu.mihosoft.vrl.visual.Connection;
import eu.mihosoft.vrl.visual.Connector;
import eu.mihosoft.vrl.visual.ConnectorType;
import eu.mihosoft.vrl.visual.Message;
import eu.mihosoft.vrl.visual.MessageType;
import eu.mihosoft.vrl.visual.Style;
import eu.mihosoft.vrl.visual.TransparentPanel;
import eu.mihosoft.vrl.visual.VBoxLayout;
import eu.mihosoft.vrl.visual.VButton;
import groovy.lang.Script;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@TypeInfo(type = Object[].class, style = "array")
public class ArrayBaseType extends TypeRepresentationBase {

    private ArrayList<TypeRepresentationContainer> typeContainers =
            new ArrayList<TypeRepresentationContainer>();
    private int minArraySize = 0;
    private TransparentPanel valuePanel = new TransparentPanel();
    private Box buttonBox;
    private ParamInfo elementInputInfo;
    private MethodInfo elementOutputInfo;
    protected static final String ARRAY_SIZE_KEY = "array-size";
    protected static final String ELEMENT_TYPE_VALUE_OPTIONS_KEY =
            "element-type-value-options";
    protected static final String ELEMENT_TYPE_CUSTOM_DATA_KEY =
            "element-type-custom-data";
    private boolean hideButtonBox = false;
    private AnimatedLayout layout;
    private boolean useRealElementType = false;
    private ParamInfo[] customElementParamInfos;
    private boolean hover;
    private OutputInfo outputInfo;

    public ArrayBaseType() {

//        setBorder(VGraphicsUtil.createDebugBorder());

        setValueName("Array");

        VBoxLayout layoutM = new VBoxLayout(this, VBoxLayout.PAGE_AXIS);

        layout = new AnimatedLayout(layoutM);
        layout.setDuration(0.3);
        setLayout(layout);
        layout.setAnimated(false);

        layout.addFrameListener(new FrameListener() {
            @Override
            public void frameStarted(double time) {

                if (time >= 0.0) {
                    layout.getExcludedComponents().clear();
                }

                if (time == 1.0) {
                    layout.setAnimated(false);
                }
            }
        });

//        setLayout(layout);

        nameLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        nameLabel.setMaximumSize(
                new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        nameLabel.setPreferredSize(null);
        nameLabel.setMinimumSize(null);

        nameLabel.setHorizontalAlignment(JLabel.LEFT);

//        nameLabel.setBorder(VSwingUtil.createDebugBorder());

        nameLabel.setBorder(new EmptyBorder(0, 3, 0, 0));

        add(nameLabel);

        nameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hoverOn();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverOff();
            }
        });

        valuePanel.setLayout(new VBoxLayout(valuePanel, VBoxLayout.PAGE_AXIS));

//        valuePanel.setBorder(VSwingUtil.createDebugBorder());

        valuePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(valuePanel);

        buttonBox = Box.createHorizontalBox();
//
//        buttonBox.setBorder(VSwingUtil.createDebugBorder());

        buttonBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        VButton addItemButton = new VButton("+");

        addItemButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                layout.setAnimated(true);

                setArraySize(getArraySize() + 1);
//                System.out.println("SIZE:" + arraySize);
//                Object[] array = new Object[arraySize];
                Object array =
                        Array.newInstance(
                        getType().getComponentType(), getArraySize());
                updateView(getParentMethod(), array, false);
                revalidate();
                setDataOutdated();
            }
        });

        addItemButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hoverOn();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverOff();
            }
        });

        Dimension btnSize = new Dimension(38, 28);

        addItemButton.setPreferredSize(btnSize);
        addItemButton.setMinimumSize(btnSize);
        addItemButton.setMaximumSize(btnSize);

        buttonBox.add(addItemButton);

        VButton removeItemButton = new VButton("-");

        removeItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                layout.setAnimated(true);

                if (getArraySize() > minArraySize) {
                    setArraySize(getArraySize() - 1);
                    Object array =
                            Array.newInstance(
                            getType().getComponentType(), getArraySize());
                    updateView(getParentMethod(), array, false);

                    layout.excludeFromAnimation(buttonBox);

                    revalidate();
                    setDataOutdated();
                }
            }
        });



        removeItemButton.setPreferredSize(btnSize);
        removeItemButton.setMinimumSize(btnSize);
        removeItemButton.setMaximumSize(btnSize);

        removeItemButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hoverOn();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverOff();
            }
        });

        buttonBox.add(removeItemButton);
        buttonBox.add(Box.createHorizontalGlue());

        add(buttonBox);

        getCustomDataWithoutDataRefresh().
                put(ARRAY_SIZE_KEY, minArraySize);
        getCustomDataWithoutDataRefresh().
                put(ELEMENT_TYPE_VALUE_OPTIONS_KEY,
                new ArrayList<String>());
        getCustomDataWithoutDataRefresh().
                put(ELEMENT_TYPE_CUSTOM_DATA_KEY,
                new ArrayList<CustomParamData>());

        addCapabilityListener(new CapabilityChangedListener() {
            @Override
            public void capabilityChanged(
                    CapabilityManager manager, Integer bit) {

                if (manager.isCapable(CanvasCapabilities.ALLOW_CONNECT)
                        && isInput()) {
                    buttonBox.setVisible(!isHideButtonBox());
                    revalidate();
                    updateLayout();
                } else {
                    buttonBox.setVisible(false);
                    revalidate();
                    updateLayout();
                }
            }
        });

        // is always true for inputs, see isHideConnector()
        setHideConnector(false);
    }

    private void hoverOn() {
        hover = true;
        repaint();
    }

    private void hoverOff() {
        hover = false;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Style style = getStyle();

        if (style == null || !hover) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Composite original = g2.getComposite();

        if (style != null) {
            g2.setColor(style.getBaseValues().getColor(
                    CanvasWindow.BORDER_COLOR_KEY));

            AlphaComposite ac1 =
                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    style.getBaseValues().getFloat(
                    CanvasWindow.TRANSPARENCY_KEY));
            g2.setComposite(ac1);
        }

        BasicStroke stroke =
                new BasicStroke(1.f);

        g2.setStroke(stroke);

        g2.drawLine(0, 0 + nameLabel.getHeight() / 2, 3, 0 + nameLabel.getHeight() / 2);
        g2.drawLine(0, 0 + nameLabel.getHeight() / 2, 0, getHeight());
        g2.drawLine(0, getHeight() - 1, 3, getHeight() - 1);

        g2.drawLine(getWidth() - 1, 0 + nameLabel.getHeight() / 2, getWidth() - 1 - 3, 0 + nameLabel.getHeight() / 2);
        g2.drawLine(getWidth() - 1, 0 + nameLabel.getHeight() / 2, getWidth() - 1, getHeight());
        g2.drawLine(getWidth() - 1, getHeight() - 1, getWidth() - 1 - 3, getHeight() - 1);

        g2.setComposite(original);

    }

    @Override
    public void addedToMethodRepresentation() {
        Object array =
                Array.newInstance(
                getType().getComponentType(), getArraySize());

        if (getParentMethod() != null) {
            updateView(getParentMethod(), array, false);
        }

        updateConnectorIds();

        for (TypeRepresentationContainer tCont : typeContainers) {
            tCont.getTypeRepresentation().addedToMethodRepresentation();
        }
    }

    private void updateConnectorIds() {
        for (int i = 0; i < getArraySize(); i++) {
            // new map based connector handling
            String baseKey = getConnector().getId();
            String key = baseKey + ":" + i;
            typeContainers.get(i).getTypeRepresentation().getConnector().setId(key);
        }
    }

    @Override
    public boolean isHideConnector() {
        return super.isHideConnector() || isInput();
    }

    private void updateView(final DefaultMethodRepresentation mRep,
            Object array, boolean updateValues) {

        if (array.getClass().equals(getType())) {
            setArraySize(Array.getLength(array));
        }

        int containerSize = getTypeContainers().size();

        int diff = getArraySize() - containerSize;

        for (int i = 0; i < Math.abs(diff); i++) {

            if (diff > 0) {

                TypeRepresentationFactory typeFactory =
                        ((VisualCanvas) getMainCanvas()).getTypeFactory();

                TypeRepresentationBase type = null;

                if (isInput()) {

                    if (isUseRealElementType()) {

                        if (customElementParamInfos != null
                                && customElementParamInfos.length > i) {
                            elementInputInfo = customElementParamInfos[i];
                        }

                        if (Array.get(array, i) == null) {
                            continue;
                        }

                        type = typeFactory.getInputInstance(
                                Array.get(array, i).getClass(), elementInputInfo);

                    } else {
                        type = typeFactory.getInputInstance(
                                getType().getComponentType(), elementInputInfo);
                    }
                } else {

                    if (isUseRealElementType()) {

                        if (Array.get(array, i) == null) {
                            continue;
                        }

                        type = typeFactory.getOutputInstance(
                                Array.get(array, i).getClass(), elementOutputInfo, getOutputInfo());

                    } else if (getOutputInfo() != null) {

                        elementOutputInfo = generateOutputInfo(getOutputInfo(), i);

                        Class<?> elemType = getType().getComponentType();

                        if (i < getOutputInfo().elemTypes().length) {
                            elemType = getOutputInfo().elemTypes()[i];
                        }

                        type = typeFactory.getOutputInstance(
                                elemType, elementOutputInfo, getOutputInfo());

                        Object elemObj = Array.get(array, i);

                        if (elemObj != null
                                && elemType.isAssignableFrom(
                                elemObj.getClass())) {
                            throw new IllegalArgumentException(
                                    "Invalid element type: type of element[" + i + "]: "
                                    + elemObj.getClass() + ", requested type: "
                                    + elemType);
                        }

                    } else {
                        type = typeFactory.getOutputInstance(
                                getType().getComponentType(), elementOutputInfo, getOutputInfo());
                        type.setHideConnector(true);
                    }

                }

                type.setMainCanvas(mRep.getParentObject().getMainCanvas());
                type.setParentMethod(mRep);


                type.setCurrentRepresentationType(
                        getCurrentRepresentationType());

//                if (array != null && updateValues) {
//                    System.out.println("Index: " + i + containerSize);
//                    type.setValue(Array.get(array, i + containerSize));
//                }

//                System.out.println("++++++SIZE: "
//                        + (getElementTypeValueOptions().size())
//                        + " , INDEX: " + i + containerSize);

                if (getElementTypeValueOptions().size() <= i + containerSize) {
                    getElementTypeValueOptions().add(type.getValueOptions());
                    getElementTypeCustomData().add(type.getCustomData());
                } else {
                    type.setValueOptions(
                            getElementTypeValueOptions().get(i + containerSize));
                    type.evaluateCustomParamData();
                    type.evaluateValueOptions();
                }

                TypeRepresentationContainer tCont = null;

                if (isInput()) {
                    tCont = new TypeRepresentationContainer(
                            type, mRep, ConnectorType.INPUT, getMainCanvas());

                } else {
                    tCont = new TypeRepresentationContainer(
                            type, mRep, ConnectorType.OUTPUT, getMainCanvas());
                }

                if (getConnector() != null) {

                    // each element input connector will be added relative to
                    // the connector index of this type representation
                    //
                    // output connectors will be added to the end of the list
                    // because they are always added after all inputs have been
                    // added
                    int currentelementIndex = i + containerSize;
                    int parentIndex = getParentMethod().getConnectors().
                            getIndexById(getConnector().getID());

                    // ask other parents with index in [1,parentIndex-1]
                    // how many element connectors they provide if they are
                    // an instance of ArrayBaseType
                    int connectorOffset = currentelementIndex;

                    for (int j = 1; j < parentIndex; j++) {
//                        System.out.println("TREP: " + j);

                        TypeRepresentationContainer tContJ =
                                (TypeRepresentationContainer) getParentMethod().
                                getConnectors().get(j).getValueObject();

                        TypeRepresentationBase tRep = tContJ.getTypeRepresentation();

                        if (tRep instanceof ArrayBaseType) {
                            ArrayBaseType arrayBaseType = (ArrayBaseType) tRep;
                            connectorOffset += arrayBaseType.getArraySize();
                        }
                    }

                    int baseIndex =
                            getParentMethod().getDescription().
                            getParameterTypes().length;

                    int connectorIndex = baseIndex + connectorOffset + 1;

//                    System.out.println("INDEX: " + connectorIndex);

                    // if we are not loading from file and we are an input
                    // we need to specify the index at wich the connector will
                    // be added to
                    if (!((VisualCanvas) getMainCanvas()).isLoadingSession()
                            && isInput()) {
                        mRep.getConnectors().add(connectorIndex, (VConnector) tCont.getTypeRepresentation().getConnector());
                    } else {
                        // if we load from file or we are an output we use the
                        // id values as defined by the id table
                        mRep.getConnectors().add((VConnector) tCont.getTypeRepresentation().getConnector());
                    }
                }

                getTypeContainers().add(tCont);

                valuePanel.add(tCont);

                tCont.getTypeRepresentation().updateLayout();

                tCont.getTypeRepresentation().addedToMethodRepresentation();

            } else if (diff < 0) {

                TypeRepresentationContainer tC =
                        getTypeContainers().get(getTypeContainers().size() - 1);

                getMainCanvas().getDataConnections().removeAllWith(
                        tC.getTypeRepresentation().getConnector());

                getTypeContainers().remove(tC);
                valuePanel.remove(tC);
                
                mRep.getConnectors().remove(
                        tC.getTypeRepresentation().getConnector());

                if (getElementTypeValueOptions().size() > getTypeContainers().size()) {
                    getElementTypeValueOptions().remove(
                            getElementTypeValueOptions().size() - 1);
                    getElementTypeCustomData().remove(
                            getElementTypeCustomData().size() - 1);
                }

            }
        } // end for

        // store array values in type representations
        if (updateValues && array.getClass().equals(getType())) {

            setElementViewValues(array);
        }
    }
    
    protected void setElementViewValues(Object array) {
        // store array values in type representations
        if (array!=null && array.getClass().equals(getType())) {

            for (int i = 0; i < Array.getLength(array); i++) {
                Object elementValue = Array.get(array, i);
//                System.out.println("VALUE: " + i + " : " + value);

                if (elementValue != null) {

                    Class<?> elemType = getTypeContainers().get(i).getTypeRepresentation().
                            getType();

                    if (elemType.isAssignableFrom(elementValue.getClass())) {
                        
                        getTypeContainers().get(i).getTypeRepresentation().
                                setViewValue(elementValue);
                        
                    } else {

                        getMainCanvas().getMessageBox().addMessage(
                                "Invalid element type in output:",
                                ">> type of element[" + i + "]: "
                                + Message.EMPHASIZE_BEGIN
                                + elementValue.getClass()
                                + Message.EMPHASIZE_END + ", should be: "
                                + Message.EMPHASIZE_BEGIN
                                + elemType
                                + Message.EMPHASIZE_END + "<br><br>"
                                + ">> please check your implementation!",
                                getTypeContainers().get(i).getConnector(),
                                MessageType.ERROR);

                        throw new IllegalArgumentException(
                                "Invalid element type: type of element[" + i + "]: "
                                + elementValue.getClass() + ", should be: "
                                + elemType);
                    }
                }
            }
        }
    }
    
    protected void setElementValues(Object array) {
        // store array values in type representations
        if (array!=null && array.getClass().equals(getType())) {

            for (int i = 0; i < Array.getLength(array); i++) {
                Object elementValue = Array.get(array, i);
//                System.out.println("VALUE: " + i + " : " + value);

                if (elementValue != null) {

                    Class<?> elemType = getTypeContainers().get(i).getTypeRepresentation().
                            getType();

                    if (elemType.isAssignableFrom(elementValue.getClass())) {
                        
                        getTypeContainers().get(i).getTypeRepresentation().
                                setValue(elementValue);
                        
                    } else {

                        getMainCanvas().getMessageBox().addMessage(
                                "Invalid element type in output:",
                                ">> type of element[" + i + "]: "
                                + Message.EMPHASIZE_BEGIN
                                + elementValue.getClass()
                                + Message.EMPHASIZE_END + ", should be: "
                                + Message.EMPHASIZE_BEGIN
                                + elemType
                                + Message.EMPHASIZE_END + "<br><br>"
                                + ">> please check your implementation!",
                                getTypeContainers().get(i).getConnector(),
                                MessageType.ERROR);

                        throw new IllegalArgumentException(
                                "Invalid element type: type of element[" + i + "]: "
                                + elementValue.getClass() + ", should be: "
                                + elemType);
                    }
                }
            }
        }
    }

    @Override
    public void setUpToDate(boolean value) {
        super.setUpToDate(value);

        if (isUpToDate() == false) {

            for (TypeRepresentationContainer tC : getTypeContainers()) {
                Connector c = tC.getTypeRepresentation().getConnector();
                ArrayList<Connection> receivers =
                        getMainCanvas().getDataConnections().getAllWith(c);

                for (Connection conn : receivers) {
                    TypeRepresentationContainer receiverTCont =
                            (TypeRepresentationContainer) conn.getReceiver().getValueObject();
                    TypeRepresentationBase tRep = receiverTCont.getTypeRepresentation();

                    if (tRep.getParentMethod() != getParentMethod()) {
                        tRep.setDataOutdated();
                        tRep.setValue(null);
                        tRep.emptyView();
                    }
                }
            }
        }
    }

    @Override
    public void setViewValue(Object o) {
        
        try {
            super.setViewValue(o);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        if (o.getClass().equals(getType())) {
            updateView(getParentMethod(), o, true);
        }
        
        setElementViewValues(o);
    }
    
    @Override
    public void setValue(Object o) {
        super.setValue(o);
        setElementValues(o);
    }

    @Override
    public void emptyView() {
        for (TypeRepresentationContainer tC : getTypeContainers()) {
            tC.getTypeRepresentation().emptyView();
        }
        super.emptyView();
    }

//    @Override
//    public void setDataOutdated() {
//        super.setDataOutdated();
//
//        for (TypeRepresentationContainer tC : typeContainers) {
//            tC.getTypeRepresentation().setDataOutdated();
//        }
//    }
//
//    @Override
//    public void setReturnTypeOutdated() {
//        super.setReturnTypeOutdated();
//
//        for (TypeRepresentationContainer tC : typeContainers) {
//            tC.getTypeRepresentation().setReturnTypeOutdated();
//        }
//    }
    @Override
    public Object getViewValue() {

        Object result =
                Array.newInstance(getType().getComponentType(), getArraySize());

        if (!isNoValidation()) {

            boolean invalidData = false;

            for (int i = 0; i < getTypeContainers().size(); i++) {
                TypeRepresentationContainer tC = getTypeContainers().get(i);

                Array.set(result, i, tC.getTypeRepresentation().getValue());

                if (!tC.getTypeRepresentation().isValidValue()) {
                    invalidData = true;
                }
            }

            if (invalidData) {
                result = null;
            }
        } else {
            for (int i = 0; i < getTypeContainers().size(); i++) {
                TypeRepresentationContainer tC = getTypeContainers().get(i);

                Array.set(result, i,
                        tC.getTypeRepresentation().
                        getViewValueWithoutValidation());
            }
        }

        return result;
    }

    @Override
    public void setParentMethod(DefaultMethodRepresentation mRep) {
        super.setParentMethod(mRep);
    }

//    @Override
//    public void setConnector(Connector c) {
//        super.setConnector(c);
//
//        if (getParentMethod()!= null) {
//            getParentMethod().getConnectors().add(c);
//        }
//
//        Object array =
//                Array.newInstance(
//                getType().getComponentType(), getArraySize());
//
//        if (getParentMethod() != null) {
//            updateView(getParentMethod(), array, false);
//        }
//    }
    /**
     * @return the elementInfo
     */
    public ParamInfo getElementInputInfo() {
        return elementInputInfo;
    }

    /**
     * @param elementInfo the elementInfo to set
     */
    public void setElementInputInfo(ParamInfo elementInfo) {
        this.elementInputInfo = elementInfo;
    }

    @Override
    public void evaluateCustomParamData() {
        Object array =
                Array.newInstance(getType().getComponentType(), getArraySize());
        updateView(getParentMethod(), array, true);

        for (TypeRepresentationContainer tC : getTypeContainers()) {
            tC.getTypeRepresentation().evaluateCustomParamData();
        }
    }

    /**
     * @return the arraySize
     */
    protected int getArraySize() {

        Integer size = 0;

        try {
            size = (Integer) getCustomDataWithoutDataRefresh().get(ARRAY_SIZE_KEY);
        } catch (Exception ex) {
            //
        }

        return size;
    }

    /**
     * @param arraySize the arraySize to set
     */
    private void setArraySize(int arraySize) {
//        this.arraySize = arraySize;
        getCustomDataWithoutDataRefresh().put(ARRAY_SIZE_KEY, arraySize);
    }

    @SuppressWarnings(value = "unchecked")
    public ArrayList<String> getElementTypeValueOptions() {
        return (ArrayList<String>) getCustomDataWithoutDataRefresh().get(
                ELEMENT_TYPE_VALUE_OPTIONS_KEY);
    }

    @SuppressWarnings(value = "unchecked")
    public ArrayList<CustomParamData> getElementTypeCustomData() {
        return (ArrayList<CustomParamData>) getCustomDataWithoutDataRefresh().get(
                ELEMENT_TYPE_CUSTOM_DATA_KEY);
    }

    protected final CustomParamData getCustomDataWithoutDataRefresh() {
        return super.getCustomData();
    }

    @Override
    public CustomParamData getCustomData() {
        for (int i = 0; i < getTypeContainers().size(); i++) {
            TypeRepresentationBase tRep =
                    getTypeContainers().get(i).getTypeRepresentation();

            String valueOptions = tRep.getValueOptions();
            CustomParamData customData = tRep.getCustomData();

//            System.out.println("**GET*****INDEX: " + i + " : " + valueOptions);

            getElementTypeValueOptions().set(i, valueOptions);
            getElementTypeCustomData().set(i, customData);
        }

        return super.getCustomData();
    }

    @Override
    public void setCustomData(CustomParamData customData) {
        super.setCustomData(customData);


        if (getMainCanvas() != null) {
            Object array =
                    Array.newInstance(
                    getType().getComponentType(), getArraySize());

            updateView(getParentMethod(), array, false);
        }

        for (int i = 0; i < getTypeContainers().size(); i++) {
            TypeRepresentationBase tRep =
                    getTypeContainers().get(i).getTypeRepresentation();

            String elementValueOptions = getElementTypeValueOptions().get(i);
            CustomParamData elementCustomData = getElementTypeCustomData().get(i);

//            System.out.println("**SET*****INDEX: " + i + " : " + elementCustomData);

            tRep.setValueOptions(elementValueOptions);
            tRep.setCustomData(elementCustomData);
        }
    }

    @Override
    public void evaluateValueOptions() {
        for (int i = 0; i < getTypeContainers().size(); i++) {
            TypeRepresentationBase tRep =
                    getTypeContainers().get(i).getTypeRepresentation();
            tRep.evaluateValueOptions();

//            System.out.println("**EVAL*****INDEX: " + i + " : " + tRep.getValueOptions());
        }

        super.evaluateValueOptions();
    }

    @Override
    public void evaluationRequest(Script script) {

        super.evaluationRequest(script);

        Object property = null;

        if (getValueOptions().contains("minArraySize")) {
            property = getOptionEvaluator().getProperty("minArraySize");
        }

        if (property != null) {
            minArraySize = (Integer) property;

//                System.out.println("MIN-SIZE: " + minArraySize);

            if (getArraySize() < minArraySize) {
                setArraySize(minArraySize);
            }

//            Object array =
//                    Array.newInstance(
//                    getType().getComponentType(), getArraySize());
        }

        property = null;

        if (getValueOptions().contains("hideButtonBox")) {
            property = getOptionEvaluator().getProperty("hideButtonBox");
        }

        if (property != null) {
            setHideButtonBox((boolean) (Boolean) property);

            if (isInput()) {
                buttonBox.setVisible(!isHideButtonBox());
            }

        }
    }

    @Override
    public void setCurrentRepresentationType(
            RepresentationType representationType) {
        super.setCurrentRepresentationType(representationType);

        if (isOutput()) {
            buttonBox.setVisible(false);
        } else {
            buttonBox.setVisible(true);
        }
    }

    /**
     * @return the elementOutputInfo
     */
    public MethodInfo getElementOutputInfo() {
        return elementOutputInfo;
    }

    /**
     * @param elementOutputInfo the elementOutputInfo to set
     */
    public void setElementOutputInfo(MethodInfo elementOutputInfo) {
        this.elementOutputInfo = elementOutputInfo;
    }

    /**
     * @param elementOutputInfo the outputinfo to set
     */
    public void setElementOutputInfo(OutputInfo outputInfo) {
        this.outputInfo = outputInfo;
    }

    /**
     * @return the hideButtonBox
     */
    public boolean isHideButtonBox() {
        return hideButtonBox;
    }

    @Override
    public void dispose() {
        layout.removeAllFrameListeners();
    }

    @Override()
    public String getValueAsCode() {
        StringBuilder builder = new StringBuilder();

        builder.append("[");

        boolean firstRun = true;

        for (TypeRepresentationContainer tCont : getTypeContainers()) {

            if (firstRun) {
                firstRun = false;

            } else {
                builder.append(", ");
            }

            String elemCode = tCont.getTypeRepresentation().getValueAsCode();

            builder.append(elemCode);

            // if we cannot create code return null as code generation
            // is invalid if elements do not support code generation
            if (elemCode == null) {
                return null;
            }
        }

        builder.append("] as ").
                append(
                VClassLoaderUtil.arrayClass2Code(getType().getName()));

        return builder.toString();
    }

    /**
     * @return the typeContainers
     */
    public ArrayList<TypeRepresentationContainer> getTypeContainers() {
        return typeContainers;
    }

    /**
     * Indicates whether to use type from array element. This only works for
     * input types.
     *
     * @return the useRealElementType
     */
    public boolean isUseRealElementType() {
        return useRealElementType;
    }

    /**
     * Defines whether to use type from array element. This only works for input
     * types.
     *
     * @param useRealElementType the useRealElementType to set
     */
    public void setUseRealElementType(boolean useRealElementType) {
        this.useRealElementType = useRealElementType;
    }

    /**
     * @param hideButtonBox the hideButtonBox to set
     */
    public void setHideButtonBox(boolean hideButtonBox) {
        this.hideButtonBox = hideButtonBox;
        buttonBox.setVisible(!hideButtonBox);
    }

    /**
     * @return the customElementParamInfos
     */
    public ParamInfo[] getCustomElementParamInfos() {
        return customElementParamInfos;
    }

    /**
     * @param customElementParamInfos the customElementParamInfos to set
     */
    public void setCustomElementParamInfos(ParamInfo[] customElementParamInfos) {
        this.customElementParamInfos = customElementParamInfos;
    }

    private MethodInfo generateOutputInfo(final OutputInfo outputInfo, final int i) {

        if (i < 0) {
            throw new IllegalArgumentException("Indices < 0 not supported: " + i);
        }

        if (outputInfo == null) {
            return null;
        }

        MethodInfo result = new MethodInfo() {
            @Override
            public String name() {
                return "";
            }

            @Override
            public boolean interactive() {
                return true;
            }

            @Override
            public boolean hide() {
                return false;
            }

            @Override
            public boolean ignore() {
                return false;
            }

            @Override
            public boolean noGUI() {
                return false;
            }

            @Override
            public String valueName() {
                if (i < outputInfo.elemNames().length) {
                    return outputInfo.elemNames()[i];
                } else {
                    return "";
                }
            }

            @Override
            public String valueStyle() {
                if (i < outputInfo.elemStyles().length) {
                    return outputInfo.elemStyles()[i];
                } else {
                    return "default";
                }
            }

            @Override
            public boolean inheritGUI() {
                return true;
            }

            @Override
            public String callOptions() {
                return "";
            }

            @Override
            public String valueOptions() {

                if (i < outputInfo.elemOptions().length) {
                    return outputInfo.elemOptions()[i];
                } else {
                    return "";
                }
            }

            @Override
            public String buttonText() {
                return "invoke";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public boolean initializer() {
                return false;
            }

            @Override
            public String valueTypeName() {
                if (i < outputInfo.elemTypeNames().length) {
                    return outputInfo.elemTypeNames()[i];
                } else {
                    return "";
                }
            }

            @Override
            public boolean askIfClose() {
                return false;
            }

            @Override
            public boolean hideCloseIcon() {
                return false;
            }

            @Override
            public String propertyOf() {
                return "";
            }

            @Override
            public String parentName() {
                return "";
            }
        };





        return result;
    }

    /**
     * @return the outputInfo
     */
    public OutputInfo getOutputInfo() {
        return outputInfo;
    }
}
