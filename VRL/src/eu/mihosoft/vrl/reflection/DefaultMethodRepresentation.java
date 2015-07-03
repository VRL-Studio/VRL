/* 
 * DefaultMethodRepresentation.java
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

import eu.mihosoft.vrl.system.VThread;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.effects.ColorizeEffect;
import eu.mihosoft.vrl.effects.FadeInEffect;
import eu.mihosoft.vrl.effects.FadeOutEffect;
import eu.mihosoft.vrl.effects.SelectionEffect;
import eu.mihosoft.vrl.effects.SpinningWheelEffect;
import eu.mihosoft.vrl.lang.visual.InputValue;
import eu.mihosoft.vrl.lang.visual.OutputValue;
import eu.mihosoft.vrl.system.VParamUtil;
import eu.mihosoft.vrl.visual.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * A method representation is a Swing based visualization of a method.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class DefaultMethodRepresentation extends VComponent
        implements Selectable, OrderedLayoutConstraint {

    private static final long serialVersionUID = 6228430207773573570L;
    transient private BufferedImage buffer;
    private Style style;
//    private RoundTitledBorder titleBorder;
    private IDArrayList<TypeRepresentationContainer> parameterTypeRepresentations
            = new IDArrayList<TypeRepresentationContainer>();
    private TypeRepresentationBase returnTypeRepresentation;
//    private IDArrayList<VConnector> connectors = new IDArrayList<VConnector>();
    private Map<String, VConnector> connectorsMap = new HashMap<String, VConnector>();
    private TypeRepresentationFactory typeFactory;
    private MethodDescription description;
    private JPanel inputPanel;
    private JPanel outputPanel;
    private DefaultObjectRepresentation parentObject;
    private int ID = 0;
    private static CallTrace callTrace = new CallTrace();
    private boolean interactive;
    private MethodTitleBar titleBar;  // necessary to compute titlebar position
    private Box titlePanel = new Box(VBoxLayout.X_AXIS);
    private SelectionEffect selectionEffect;
//    private SpinningWheelEffect spinningWheelEffect;
    private boolean selected;
    private boolean calledByUser;
    private Thread thread;
    private boolean hidden;
    private String name;
    private String invokeWaitEffectName = null;
    private static String defaultInvokeWaitEffectName = "ColorizeEffect";
    public static int DEFAULT_BORDER_CORNER_ARC_WIDTH = 20;
    public static int DEFAULT_TITLEBAR_CORNER_ARC_WIDTH = 18;
    private String invokeButtonText = "invoke";
    private boolean noGUI = false;
    private boolean minimized;
    private JComponent buttonContainer;
    private boolean initializer;
    public static final String METHOD_TITLE_UPPER_COLOR_KEY
            = "MethodRepresentation:Title:Color[upper]";
    public static final String METHOD_TITLE_LOWER_COLOR_KEY
            = "MethodRepresentation:Title:Color[lower]";
    public static final String METHOD_TITLE_TRANSPARENCY_KEY
            = "MethodRepresentation:Title:Transparency";
    private RepresentationGroup parameterGroup;
    private Long threadId = 0L;
    private boolean paramsAreValid;

    private int visualMethodId;

    public static final String KEY_RETURN_VALUE_CONNECTOR = "return:0";
    public static final String KEY_INPUT_CONNECTOR_PREFIX = "input:";

    /**
     * Constructor.
     *
     * @param parentObject the parent object of the method
     */
    public DefaultMethodRepresentation(DefaultObjectRepresentation parentObject) {

        setMainCanvas(parentObject.getMainCanvas());

        this.setParentObject(parentObject);

        titleBar = new MethodTitleBar(this);

        this.typeFactory = parentObject.getTypeFactory();

        inputPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // invisible
            }
        };

//        titleBorder = new RoundTitledBorder(parentObject.getMainCanvas(),
//                "Method()");
//        ColLayout inputLayout = new ColLayout();
//        inputLayout.setMargins(0, 0, 0, 0);
//        inputLayout.setEqualWidth(false);
        setParameterGroup(new RepresentationGroup());

        VBoxLayout inputLayout = new VBoxLayout(inputPanel, VBoxLayout.Y_AXIS);

        inputPanel.setLayout(inputLayout);

        inputPanel.add(parameterGroup);

        outputPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                // invisible
            }
        };

        VBoxLayout outputLayout = new VBoxLayout(outputPanel, VBoxLayout.Y_AXIS);

        outputPanel.setLayout(outputLayout);

//        outputLabel.setBackground(Color.RED);
//        outputLabel.setBorder(BorderFactory.createEtchedBorder());
        titlePanel.add(Box.createGlue());
        titlePanel.add(titleBar);
        titlePanel.add(Box.createGlue());

//        this.setLayout(new BorderLayout(0, 0));
//        this.add(titlePanel, BorderLayout.NORTH);
//        this.add(inputPanel, BorderLayout.WEST);
//        this.add(outputPanel, BorderLayout.EAST);
        setLayout(new VBoxLayout(this, VBoxLayout.Y_AXIS));

        add(titlePanel);

        Box contentBox = Box.createHorizontalBox();

        contentBox.add(inputPanel);
        contentBox.add(outputPanel);

        add(contentBox);

//        titleBorder.setMargins(5, 7, 7, 7);
//        this.setBorder(titleBorder);
        this.setBorder(new EmptyBorder(2, 7, 2, 7));

        addMouseListener(new VisualObjectMouseAdapter());

//        setBackground(Color.RED);
//        setBorder(BorderFactory.createEtchedBorder());
//        this.setPreferredSize(new Dimension(10,20));
//        this.getParentObject().doLayout();
        selectionEffect = new SelectionEffect(this);
        selectionEffect.setColor(new Color(0.f, 0.f, 1.f, 0.3f));

        getEffectManager().getEffects().add(selectionEffect);

        SpinningWheelEffect spinningWheelEffect = new SpinningWheelEffect(this);
        spinningWheelEffect.setColor(new Color(0.f, 0.f, 0.f, 0.4f));
        getEffectManager().getEffects().add(spinningWheelEffect);

        ColorizeEffect colorizeEffect = new ColorizeEffect(this);
        colorizeEffect.setColor(new Color(0.f, 0.f, 0.f, 0.4f));
        getEffectManager().getEffects().add(colorizeEffect);

        FadeInEffect fadeIn = new FadeInEffect(this);
        getEffectManager().getEffects().add(fadeIn);

        FadeOutEffect fadeOut = new FadeOutEffect(this);
        getEffectManager().getEffects().add(fadeOut);

        setOpaque(false);
    }

    public void startWaitEffect() {
        ColorizeEffect effect
                = (ColorizeEffect) getEffectManager().getEffectByName(
                        getInvokeWaitEffectName());
        effect.setUncolorize(false);

        getEffectManager().startEffect(getInvokeWaitEffectName(), 1);
    }

    public void stopWaitEffect() {
        ColorizeEffect effect
                = (ColorizeEffect) getEffectManager().getEffectByName(
                        getInvokeWaitEffectName());
        effect.setUncolorize(true);

        getEffectManager().startEffect(getInvokeWaitEffectName(), 1);
    }

    @Override
    /**
     * Returns the title of this method.
     */
    public String getName() {
        return name;
    }

    public void minimize() {
        for (TypeRepresentationContainer tC : parameterTypeRepresentations) {
            tC.getTypeRepresentation().setVisible(false);
        }

        parameterGroup.setVisible(false);

        inputPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        outputPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

        getReturnValue().setVisible(false);
        getInvokeButtonContainer().setVisible(false);

        revalidate();
        minimized = true;
    }

    public void maximize() {
        for (TypeRepresentationContainer tC : parameterTypeRepresentations) {
            tC.getTypeRepresentation().setVisible(true);
        }

        parameterGroup.setTitleVisible(true);
        parameterGroup.setVisible(true);
        parameterGroup.updateMinimzedState();

        inputPanel.setBorder(null);
        outputPanel.setBorder(null);

        getReturnValue().setVisible(true);
        getInvokeButtonContainer().setVisible(true);

        revalidate();
        minimized = false;
    }

    public boolean isMinimized() {
        return minimized;
    }

    @Override
    public void paintComponent(Graphics g) {

        Style newStyle = getStyle();

        if (buffer == null
                || buffer.getWidth() != getWidth()
                || buffer.getHeight() != getHeight()
                || style == null || style != newStyle) {

            style = newStyle;

            buffer = ImageUtils.createCompatibleImage(getWidth(), getHeight());

            int top = this.getInsets().top + titleBar.getHeight() / 2;
            int bottom = this.getInsets().bottom;
            int left = this.getInsets().left;
            int right = this.getInsets().right;

            Graphics2D g2 = buffer.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            style = getStyle();

            Composite original = g2.getComposite();

            AlphaComposite ac1
                    = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                            style.getBaseValues().getFloat(
                                    CanvasWindow.TRANSPARENCY_KEY));
            g2.setComposite(ac1);

            Shape shape;

            shape = new RoundRectangle2D.Double(left, top,
                    getWidth() - right - left - 1,
                    getHeight() - bottom - top - 1,
                    DEFAULT_BORDER_CORNER_ARC_WIDTH,
                    DEFAULT_BORDER_CORNER_ARC_WIDTH);

            Stroke oldStroke = g2.getStroke();

            BasicStroke stroke
                    = new BasicStroke(style.getBaseValues().getFloat(
                                    CanvasWindow.BORDER_THICKNESS_KEY));

            g2.setStroke(stroke);
            g2.setColor(style.getBaseValues().getColor(
                    CanvasWindow.BORDER_COLOR_KEY));

            g2.draw(shape);

            g2.setComposite(AlphaComposite.Clear);
            g2.setPaint(new Color(0.f, 0.f, 0.f, 0.f));
            g2.fill(new RoundRectangle2D.Double(
                    titlePanel.getX() + titleBar.getX(),
                    titlePanel.getY(), titleBar.getWidth(),
                    titleBar.getHeight(),
                    DEFAULT_TITLEBAR_CORNER_ARC_WIDTH,
                    DEFAULT_TITLEBAR_CORNER_ARC_WIDTH));

            g2.dispose();
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(buffer, null, 0, 0);
    }

    public boolean askIfClose() {
        boolean result = false;
        MethodInfo info = this.getDescription().getMethodInfo();
        if (info != null) {
            result = info.askIfClose();
        }

        return result;
    }

    public void setDescription(MethodDescription mDesc) {

        VParamUtil.throwIfNull(mDesc);

        this.ID = mDesc.getMethodID();
        this.description = mDesc;

        try {

            // update type info in return-value-representations:
            if (returnTypeRepresentation != null) {
                if (returnTypeRepresentation.getType().getName().
                        equals(mDesc.getReturnType().getName())) {
                    returnTypeRepresentation.setType(mDesc.getReturnType());
                }
            }

            // update type info in param-representations:
            for (int i = 0; i < parameterTypeRepresentations.size(); i++) {
                TypeRepresentationBase tRep = parameterTypeRepresentations.get(i).
                        getTypeRepresentation();

                if (tRep.getType().getName().
                        equals(mDesc.getParameterTypes()[i].getName())) {
                    tRep.setType(mDesc.getParameterTypes()[i]);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    /**
     * Defines the method description used for visualization.
     *
     * @param description the method description used for visualization
     */
    protected void initRepresentation(MethodDescription description,
            IDTable idTable) {

        VParamUtil.throwIfNull(description);

        setDescription(description);

//        if (idTable != null) {
//            connectors.setIdTable(idTable);
//        }
        // if annotations are used to define custom method title then
        // use it; set it to method name retrieved from the Reflection API
        // otherwise
        String methodName = this.getDescription().getMethodTitle();
        if (methodName.length() > 0) {

            // delete string if only whitespaces
            if (methodName.matches("\\s+")) {
                methodName = "";
            }

            this.titleBar.getTitle().setText(methodName);
            name = methodName;
        } else {
            this.titleBar.getTitle().setText(
                    this.getDescription().getMethodName() + "()");

            name
                    = this.getDescription().getMethodName() + "()";
        }
        this.setInteractive(description.isInteractive());

        // init return type
        Class<?> returnType = this.getDescription().getReturnType();

        MethodInfo methodInfo = this.getDescription().getMethodInfo();
        OutputInfo outputInfo = this.getDescription().getOutputInfo();

        if (description.getMethodInfo() != null) {

            // TODO duplicated check (see DefaultObjectRepresentation.initSelectionView())
            boolean hideMethod
                    = methodInfo.hide() && !methodInfo.hideCloseIcon();

            setHidden(hideMethod);
            invokeButtonText = methodInfo.buttonText();
            setInitializer(methodInfo.initializer());

            titleBar.hideCloseIcon(methodInfo.hideCloseIcon());
        }

        // if we are a reference method we always use unsupported type
        if (isReferenceMethod() || isCustomReferenceMethod()) {
            returnTypeRepresentation = new UnsupportedType(returnType);
            returnTypeRepresentation.setCurrentRepresentationType(
                    RepresentationType.OUTPUT);
        } else {
            returnTypeRepresentation
                    = typeFactory.getOutputInstance(returnType, methodInfo, outputInfo);
        }

        returnTypeRepresentation.setParentMethod(this);

        TypeRepresentationContainer returnTypeContainer
                = new TypeRepresentationContainer(
                        returnTypeRepresentation, this,
                        ConnectorType.OUTPUT, parentObject.getMainCanvas());

        returnTypeContainer.setAlignmentX(RIGHT_ALIGNMENT);

        outputPanel.add(add(Box.createVerticalGlue()));
        outputPanel.add(returnTypeContainer);
        outputPanel.add(add(Box.createVerticalGlue()));

        Connector connector = returnTypeContainer.getConnector();
        if (connector != null) {
//            getConnectors().add((VConnector) connector);
            addConnectorByKey(KEY_RETURN_VALUE_CONNECTOR, (VConnector) connector);
            connector.setId(KEY_RETURN_VALUE_CONNECTOR);
        }

        returnTypeRepresentation.addedToMethodRepresentation();
        returnTypeRepresentation.updateLayout();

        // init parameter types
        for (int i = 0; i < this.getDescription().getParameterTypes().length; i++) {
            Class<?> parameterType
                    = this.getDescription().getParameterTypes()[i];

            ParamInfo paramInfo = this.getDescription().getParamInfos()[i];
            ParamGroupInfo paramGroupInfo = this.getDescription().getParamGroupInfos()[i];

            TypeRepresentationBase parameterTypeRep = null;

            // if we are a reference method we always use unsupported type
            if (isReferenceMethod() || isCustomReferenceMethod()) {
                parameterTypeRep = new UnsupportedType(returnType);
                parameterTypeRep.setCurrentRepresentationType(
                        RepresentationType.INPUT);
                parameterTypeRep.setNullValidInput(true);
            } else {
                parameterTypeRep
                        = typeFactory.getInputInstance(parameterType, paramInfo);
            }

            parameterTypeRep.setParentMethod(this);

            TypeRepresentationContainer paramTypeContainer
                    = new TypeRepresentationContainer(
                            parameterTypeRep, this,
                            ConnectorType.INPUT, parentObject.getMainCanvas());

            parameterTypeRepresentations.add(paramTypeContainer);

            paramTypeContainer.setAlignmentX(LEFT_ALIGNMENT);

//            inputPanel.add(add(Box.createVerticalGlue()));
//            inputPanel.add(paramTypeContainer);
//            inputPanel.add(add(Box.createVerticalGlue()));
            RepresentationGroup pGroup
                    = parameterGroup.addComponentToGroup(
                            TypeUtil.getParamGroupName(paramGroupInfo), i,
                            paramTypeContainer);

            parameterGroup.updateMinimzedState();

//            pGroup.add(paramTypeContainer);
//            if (inputPanel != pGroup.getParent()) {
//                inputPanel.add(add(Box.createVerticalGlue()));
//                inputPanel.add(pGroup);
//                inputPanel.add(add(Box.createVerticalGlue()));
//            }
//            getConnectors().add((VConnector) paramTypeContainer.getConnector());
            addConnectorByKey(KEY_INPUT_CONNECTOR_PREFIX + i, (VConnector) paramTypeContainer.getConnector());
            ((VConnector) paramTypeContainer.getConnector()).setId(KEY_INPUT_CONNECTOR_PREFIX + i);

            parameterTypeRep.updateLayout();

            if (paramTypeContainer.getTypeRepresentation().isNoGUI()) {
                parameterTypeRep.setVisible(false);
                paramTypeContainer.setVisible(false);
            }
        } // end for params

        // notify type representations
        for (TypeRepresentationBase tRep : getParameters()) {
            tRep.addedToMethodRepresentation();
            tRep.updateLayout();
        }

        buttonContainer = new TransparentPanel();

        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.PAGE_AXIS));

        getInvokeButtonContainer().setBorder(new EmptyBorder(0, 3, 3, 3));

        if (isInteractive()) {
            getInvokeButtonContainer().add(createInvokeButton());
        }

        this.add(getInvokeButtonContainer(), BorderLayout.SOUTH);
    }

    /**
     * Changes the text of the invoke button if an invoke button is present.
     * Does nothing otherwise.
     *
     * @param text new text
     */
    public void changeInvokeButtonTextIfButtonIsPresent(String text) {
        if (buttonContainer.getComponents().length == 0) {
            return;
        }

        if (!(buttonContainer.getComponents()[0] instanceof VButton)) {
            return;
        }

        invokeButtonText = text;
        ((VButton) buttonContainer.getComponents()[0]).setText(text);
    }

    /**
     * Creates an invoke button for this method.
     *
     * @return an invoke button for this method
     */
    public VButton createInvokeButton() {
        final VButton button = new VButton(invokeButtonText);

        button.setAlignmentX(0.5f);

        button.addActionListener(new CanvasActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getParentObject() != null) {
                    invokeAsCallParent();
                } else {
                    button.getParent().remove(button);
                }
            }
        });

        button.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });

        // prevent drawing bugs
        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                repaint();
            }
        });

        return button;
    }

    /**
     * Invokes the method as call parent. Usually the call parent is the user
     * who clicks an invoke button.
     *
     * @param enableAlreadyRunningMessages defines whether to show error
     * messages if the method is already running
     */
    public void invokeAsCallParent(
            boolean enableAlreadyRunningMessages) {

        //TODO: is multithreading really so easy?
        if (thread == null) {
            VThread t = new VThread(new Runnable() {
                @Override
                public void run() {
                    // if already called by user we must not set
                    // calledByUser to false
                    if (calledByUser) {

                        try {
                            invoke();
                        } catch (Exception ex) {
//                            MessageBox mBox =
//                                    getParentObject().getMainCanvas().
//                                    getMessageBox();
//
//                            //TODO improve message text
//                            String text = "Unexpected error: " + ex.toString();
//
//                            mBox.addUniqueMessage("Can't invoke method:", text,
//                                    null, MessageType.ERROR);
//                            Logger.getLogger(
//                                    DefaultMethodRepresentation.class.getName()).
//                                    log(Level.SEVERE, null, ex);
                        }

                        thread = null;
                    } else {
                        calledByUser = true;
                        try {
                            invoke();
                        } catch (Exception ex) {

                            // error output is now handled by objectinspector
                            // TODO cleanup
//                            MessageBox mBox =
//                                    getParentObject().getMainCanvas().
//                                    getMessageBox();
//                            //TODO improve message text
//                            String text = "Unexpected error: " + ex.toString();
//
//                            mBox.addUniqueMessage("Can't invoke method:", text,
//                                    null, MessageType.ERROR);
//                            Logger.getLogger(
//                                    DefaultMethodRepresentation.class.getName()).
//                                    log(Level.SEVERE, null, ex);
                        }
                        thread = null;
                        calledByUser = false;
                    }
                }
            });

            thread = t;

            t.start();

        } else {

            if (!thread.isAlive()) {
                thread = null;
            }

            if (enableAlreadyRunningMessages) {
                MessageBox mBox = getParentObject().getMainCanvas().
                        getMessageBox();

                //TODO improve message text
                String text = "Method <b><i>" + this.getName()
                        + "</i></b> still running! Please wait!";

                mBox.addUniqueMessage("Can't invoke method:", text,
                        null, MessageType.ERROR);
            }
        }
    }

    /**
     * Invokes the method as call parent. Usually the call parent is the user
     * who clicks an invoke button.
     */
    public Object invokeAsCallParentNoNewThread()
            throws InvocationTargetException {
        return invokeAsCallParentNoNewThread(true);
    }

    /**
     * Invokes the method as call parent. Usually the call parent is the user
     * who clicks an invoke button.
     *
     * @param enableAlreadyRunningMessages defines whether to show error
     * messages if the method is already running
     */
    public Object invokeAsCallParentNoNewThread(
            boolean enableAlreadyRunningMessages)
            throws InvocationTargetException {

        if (enableAlreadyRunningMessages && thread != null) {
            MessageBox mBox = getParentObject().getMainCanvas().
                    getMessageBox();

            //TODO improve message text
            String text = "Method <b><i>" + this.getName()
                    + "</i></b> still running! Please wait!";

            mBox.addUniqueMessage("Can't invoke method:", text,
                    null, MessageType.ERROR);

            if (getDescription().getReturnType() == void.class) {
                return false;
            } else {
                return null;
            }
        }

        thread = null;

        Object result = null;

        InvocationTargetException exception = null;

        // if already called by user we must not set
        // calledByUser to false
        calledByUser = true;
        try {
            result = invoke();
        } catch (InvocationTargetException ex) {
            exception = ex;
//            MessageBox mBox =
//                    getParentObject().getMainCanvas().
//                    getMessageBox();
//
//            //TODO improve message text
//            String text = "Unexpected error: " + ex.toString();
//
//            mBox.addUniqueMessage("Can't invoke method:", text,
//                    null, MessageType.ERROR);

            Logger.getLogger(
                    DefaultMethodRepresentation.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        calledByUser = false;

        if (exception != null) {
            throw exception;
        }
        return result;
    }

    /**
     * Invokes the method as call parent. Usually the call parent is the user
     * who clicks an invoke button. If the method is already running an error
     * message will be thrown.
     */
    public void invokeAsCallParent() {
        invokeAsCallParent(true);
    }

    /**
     * Indicates whether this method has input parameters.
     *
     * @return <code>true</code> if the method has input parameters;
     * <code>false</code> otherwise
     */
    public boolean hasInputs() {
        return getInputConnectors().size() > 0;
    }

    /**
     * Invokes the method.
     *
     * @param inspector the inspector the method is associated with
     * @return the return value of the method
     */
    Object invoke(VisualObjectInspector inspector) throws InvocationTargetException {
        return invoke(inspector, new CallTrace());
    }

    /**
     * Receives parameter data from connected type representations.
     *
     * @param methodDependencies the method dependencies
     */
    private void receiveParamData(CallTrace methodDependencies) {
        System.out.println("RECEIVE:");
        try {
            // set ParamValues
            for (VConnector c : this.connectorsMap.values()) {
                System.out.println("->rec: " + c.getId());
                c.receiveData(true, methodDependencies);
            }
        } catch (Exception ex) {
            MessageBox mBox
                    = getParentObject().getMainCanvas().getMessageBox();
//	    String message = "unknown error!";
//	    if (ex.getMessage() != null) {
//		message = ex.toString();
//	    }
            mBox.addMessage("Can't receive data:",
                    ex.toString(), MessageType.ERROR);
        }
    }

    /**
     * Sends the data from the return type representation to all connected type
     * representations.
     */
    public void sendReturnValueData() {

        Connector c = null;

        try {
            if (!connectorsMap.values().isEmpty()) {
                // return value connector
//                c = getConnector(0);
                c = getConnectorByKey(KEY_RETURN_VALUE_CONNECTOR);
            }

            // we are done if this method has no return value connector
            if (c == null) {
                return;
            }

            // only send param data if not using inputvalue/outputvalue
            // objects/type representations
            if (!c.getValueObject().getType().equals(InputValue.class)
                    && !c.getValueObject().getType().equals(OutputValue.class)) {
                c.sendData();
            }
        } catch (Exception ex) {
            MessageBox mBox
                    = getParentObject().getMainCanvas().getMessageBox();
            String message = ex.toString();
            if (ex.getCause() != null) {
                message += ex.getCause().toString();
            }
            mBox.addMessage("Can't send data:",
                    message, c, MessageType.ERROR);
        }
    }

    /**
     * Invokes the method.
     *
     * @param inspector the inspector the method is associated with
     * @return the return value of the method
     */
    Object invoke(VisualObjectInspector inspector,
            CallTrace methodDependencies) throws InvocationTargetException {

        if (calledByUser && thread == null) {
            callTrace.removeFromTrace(this);
        }

//        System.out.println("Call-Stack-SIZE (before): "
//                + callTrace.getCallStack().size());
        // CIRCULAR METHOD CALL
        // if a method is in trace but does not have input connectors it
        // cannot produce circular calls and is thus ignored
        if (callTrace.isInTrace(this) && this.hasInputs()) {
            System.out.println(">> Break: circular method call!");
            MessageBox mBox = getParentObject().getMainCanvas().getMessageBox();

            String text = "";

            //TODO improve message text
            text = "circular method call or connected method still running! "
                    + "Please check value dependencies!<br>>> " + this.getName();

            mBox.addUniqueMessage("Can't invoke method:", text,
                    null, MessageType.ERROR);

            throw new InvocationTargetException(
                    new IllegalStateException(text));
        }
        // return type is invalid and thus not up to date
        returnTypeRepresentation.setReturnTypeOutdated();

        // we add this call to the trace
        callTrace.addCall(this);

        // invokation slowdown and wait effect
        VisualCanvas canvas = (VisualCanvas) getParentObject().getMainCanvas();

        if (canvas.isInvokeWaitEffect()) {
            startWaitEffect();
        }

        receiveParamData(methodDependencies);

        try {
            Thread.sleep(canvas.getInvocationDelay());
        } catch (InterruptedException ex) {
            Logger.getLogger(
                    DefaultMethodRepresentation.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

//        System.out.println(">> M-DEP-SIZE: "
//                + methodDependencies.getCallStack().size());
        // reset return value
        getDescription().setReturnValue(null);

        // assign param values and invoke method
        ArrayList<Object> params = new ArrayList<Object>();

        boolean inputsAreValid = true;

//        boolean methodHasInputs =
//                getDescription().getParameterTypes().length > 0;
        paramsAreValid = true;

        Exception invocationException = null;

        // get parameter values from type representations
        for (int i = 0; i < getDescription().getParameterTypes().length; i++) {
            TypeRepresentationBase representation
                    = parameterTypeRepresentations.get(i).getTypeRepresentation();

            params.add(representation.getValue());

            if (!representation.isValidValue()) {
                paramsAreValid = false;
                // we don't break because we have to check all values

                invocationException = new InvocationTargetException(
                        new IllegalArgumentException());
            }
            // check if inputs are valid values
            if (inputsAreValid) {
                inputsAreValid = representation.isValidValue();
            }
        }

        getDescription().setParameters(params.toArray());

        if (paramsAreValid) {
            // we want to catch all exceptions because the rest of the
            // method has to be executed, no matter what exceptions are thrown,
            // otherwise methods remain in call trace even if they are not
            // running
            try {
                // invoke the method from the inspector and receive return value
                inspector.invoke(getDescription(), getParentObject().getID());

            } catch (Exception ex) {
                MessageBox mBox
                        = getParentObject().getMainCanvas().getMessageBox();
                String message = "see stack trace for details!";
                if (ex.getMessage() != null) {
                    message = ex.toString();
                }

                System.err.println("Can't invoke Method: " + message);

                invocationException = ex;
            }
        }

        try {
            returnTypeRepresentation.setValue(getDescription().getReturnValue());
        } catch (Exception ex) {
            MessageBox mBox
                    = getParentObject().getMainCanvas().getMessageBox();
//	    String message = "unknown error!";
//	    if (ex.getMessage() != null) {
//		message = ex.toString();
//	    }
            mBox.addMessage("Can't set return value:",
                    ex.toString(), MessageType.ERROR);
            invocationException = ex;
        }

        // return type is up to date now
        // TODO do we need methodHasInputs?
        // TODO are methods without inputs up to date or not?
        if (inputsAreValid /*
                 * && methodHasInputs
                 */) {
            returnTypeRepresentation.setReturnTypeUpToDate();
        }

        sendReturnValueData();

        // if we are the call parent then clear the trace because we may get
        // input data for other parameters
        if (calledByUser) {

            for (Object m : methodDependencies.getCallStack()) {
                callTrace.removeFromTrace(m);
            }
            callTrace.removeFromTrace(this);
//            System.out.println(">> Call-Stack-SIZE (after): "
//                    + callTrace.getCallStack().size());
            try {
                for (Object o : callTrace.getCallStack()) {
                    DefaultMethodRepresentation m
                            = (DefaultMethodRepresentation) o;
                    System.out.println(">> "
                            + m.getDescription().getMethodName());
                }
            } catch (Exception ex) {
                // the only known exception is a current modification exception
                // caused by automatic method calls (from gui)
                Logger.getLogger(DefaultMethodRepresentation.class.getName()).
                        log(Level.SEVERE, null, ex);
                invocationException = ex;
            }
        }

        if (canvas.isInvokeWaitEffect()) {
            stopWaitEffect();
        }

//        System.out.println(">> CalledByUser: " + calledByUser);
        if (invocationException != null) {
            throw new InvocationTargetException(
                    invocationException, "Cannot invoke method!");
        }

        return getDescription().getReturnValue();

    }

    public boolean areParamsValid() {
        return paramsAreValid;
    }

    /**
     * Invokes the method. WARNING: used for visual invocation only!
     *
     * @param inspector the inspector the method is associated with
     * @return the return value of the method
     */
    public Object automaticInvocation(VisualObjectInspector inspector)
            throws InvocationTargetException {
        // return type is invalid and thus not up to date
        returnTypeRepresentation.setReturnTypeOutdated();

        // reset return value
        getDescription().setReturnValue(null);

        // assign param values and invoke method
        ArrayList<Object> params = new ArrayList<Object>();

        boolean inputsAreValid = true;

        paramsAreValid = true;

        // get parameter values from type representations
        for (int i = 0; i < getDescription().getParameterTypes().length; i++) {
            TypeRepresentationBase representation
                    = parameterTypeRepresentations.get(i).getTypeRepresentation();

            params.add(representation.getValue());

            if (!representation.isValidValue()) {
                paramsAreValid = false;
                // we don't break because we have to check all values
            }
            // check if inputs are valid values
            if (inputsAreValid) {
                inputsAreValid = representation.isValidValue();
            }
        }

        getDescription().setParameters(params.toArray());

        InvocationTargetException exception = null;

        if (paramsAreValid) {
            // we want to catch all exceptions because the rest of the
            // method has to be executed, no matter what exceptions are thrown,
            // otherwise methods remain in call trace even if they are not
            // running
            try {
                // invoke the method from the inspector and receive return value
                inspector.invoke(
                        getDescription(), getParentObject().getID());
            } catch (Throwable ex) {

                exception = new InvocationTargetException(
                        ex, "Cannot invoke method!");
            }
        }

        try {

            returnTypeRepresentation.setValue(getDescription().getReturnValue());
        } catch (Exception ex) {

            exception = new InvocationTargetException(
                    ex, "Cannot invoke method!");
        }

        // return type is up to date now
        // TODO do we need methodHasInputs?
        // TODO are methods without inputs up to date or not?
        if (inputsAreValid /*
                 * && methodHasInputs
                 */) {
            returnTypeRepresentation.setReturnTypeUpToDate();
        }

        sendReturnValueData();

        if (exception != null) {
            throw exception;
        }

        return getDescription().getReturnValue();
    }

    /**
     * Invokes the method.
     *
     * @param methodDependencies the method dependency call trace
     * @return the return value of the method
     */
    public Object invoke(CallTrace methodDependencies) throws InvocationTargetException {
        // Getting the inspector this method is associated with
        VisualObjectInspector inspector = getParentObject().getInspector();

//	Object result = null;
        return invoke(inspector, methodDependencies);
    }

    /**
     * Invokes the method.
     *
     * @return the return value of the method
     */
    public Object invoke() throws InvocationTargetException {
        // Getting the inspector this method is associated with
//	ObjectInspector inspector = getParentObject().getInspector();

        return invoke(new CallTrace());
    }

    /**
     * Sets visual type representations of this method. It can be used to
     * initialize the visual representation with predefined values, which is
     * most likely used for session loading etc.
     *
     * @param params
     */
    public void setParameters(ArrayList<Object> params) {
        for (int i = 0; i < params.size(); i++) {
            TypeRepresentationBase t = parameterTypeRepresentations.getById(i).
                    getTypeRepresentation();
            if (params.get(i) != null) {
                t.setViewValue(params.get(i));
            }
        }
    }

    /**
     * Returns the object the method belongs to.
     *
     * @return the object the method belongs to
     */
    public final DefaultObjectRepresentation getParentObject() {
        return parentObject;
    }

    /**
     * Defines the parent object of the method.
     *
     * @param parentObject the parent object of the method
     */
    public final void setParentObject(DefaultObjectRepresentation parentObject) {
        this.parentObject = parentObject;
    }

    /**
     * Returns connector with index i.
     *
     * @param i index of the connector that is to be returned
     * @return the connector with index i
     */
    public Connector getConnector(int i) {
        if (i == 0) {
            return getConnectorByKey(KEY_RETURN_VALUE_CONNECTOR);
        } else {
            return getConnectorByKey(KEY_INPUT_CONNECTOR_PREFIX + (i - 1));
        }
    }

    /**
     * Returns all connectors of the method.
     *
     * @return all connectors of the method
     */
    public Collection<VConnector> getConnectors() {
        return Collections.unmodifiableCollection(connectorsMap.values());
    }

    /**
     * Returns all connections of this method.
     *
     * @return all connections of this method
     */
    public ArrayList<Connection> getConnections() {
        ArrayList<Connection> connections = new ArrayList<Connection>();
        try {
            for (Connector connector : connectorsMap.values()) {
                connections.addAll(this.getParentObject().
                        getMainCanvas().getDataConnections().getAllWith(connector));
            }
        } catch (Exception ex) {
            // may fail if initialization not finished, e.g.,
            //windows is to be added but closed before initialized
        }
        return connections;
    }

    /**
     * Returns parameter representations with index i.
     *
     * @param i index of the parameter representation that is to be returned
     * @return parameter representation with index i
     */
    public TypeRepresentationBase getParameter(int i) {
        return parameterTypeRepresentations.getById(i).getTypeRepresentation();
    }

    /**
     * Returns all parameter of the method.
     *
     * @return all parameter of the method
     */
    public ArrayList<TypeRepresentationBase> getParameters() {
        ArrayList<TypeRepresentationBase> parameterRepresentations
                = new ArrayList<TypeRepresentationBase>();
        for (TypeRepresentationContainer t : parameterTypeRepresentations) {
            parameterRepresentations.add(t.getTypeRepresentation());
        }
        return parameterRepresentations;
    }

    /**
     * Returns the return value representation of the method.
     *
     * @return the return value representation of the method
     */
    public TypeRepresentationBase getReturnValue() {
        return returnTypeRepresentation;
    }

//    /**
//     * Returns the method id (from description).
//     *
//     * @return the method id (from description)
//     */
//    @Override
//    public int getID() {
//        return ID;
//    }
//
//    /**
//     * Defines the method id (also defines the id of the description).
//     *
//     * @param ID the id to define
//     */
//    @Override
//    public void setID(int ID) {
//        this.ID = ID;
//        description.setMethodID(ID);
//    }
    /**
     * Indicates whether this method representation is interactive, i.e., if it
     * has an invoke button.
     *
     * @return <code>true</code> if this method representation is interactive;
     * <code>false</code> otherwise
     */
    public boolean isInteractive() {
        return interactive;
    }

    /**
     * Defines whether this method representation is to be interactive.
     *
     * @param interactive defines whether this method representation is to be
     * interactive
     */
    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    /**
     * Returns the output connector.
     *
     * @return the output connector or <code>null</code> if it doesn't have one
     */
    public Connector getOutputConnector() {
        Connector c = null;
        for (Connector i : connectorsMap.values()) {
            if (i.isOutput()) {
                c = i;
                break;
            }
        }
        return c;
    }

    /**
     * Returns a list containing all input connectors.
     *
     * @return a list containing all input connectors
     */
    public ArrayList<Connector> getInputConnectors() {
        ArrayList<Connector> inputs = new ArrayList<Connector>();

        for (Connector c : connectorsMap.values()) {
            if (c.isInput()) {
                inputs.add(c);
            }
        }
        return inputs;
    }

    /**
     * Returns the method description of this method representation.
     *
     * @return the method description of this method representation
     */
    public MethodDescription getDescription() {
        return description;
    }

    @Override
    public Shape getShape() {
        return new RoundRectangle2D.Double(0, 0,
                getWidth(), getHeight(),
                DEFAULT_BORDER_CORNER_ARC_WIDTH,
                DEFAULT_BORDER_CORNER_ARC_WIDTH);
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;

        if (selected) {
            selectionEffect.setUnselect(false);
            getEffectManager().startEffect(selectionEffect, 0.3);
        } else {
            selectionEffect.setUnselect(true);
            getEffectManager().startEffect(selectionEffect, 0.3);
        }
    }

    /**
     * Indicates whether this method representation is hidden.
     *
     * @return <code>true</code> if the method is hidden; <code>false</code>
     * otherwise
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Defines whether this method representation shall be hidden.
     *
     * @param hidden the state to set
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * Returns the name of the current invoke wait effect.
     *
     * @return the effect name (indicates the choosen effect)
     */
    public String getInvokeWaitEffectName() {
        String result = invokeWaitEffectName;
        if (result == null) {
            result = defaultInvokeWaitEffectName;
        }
        return result;
    }

    /**
     * Defines the name of the invoke wait effect.
     *
     * @param invokeWaitEffectName the name to set (defines the choosen effect)
     */
    public void setInvokeWaitEffectByName(String invokeWaitEffectName) {
        this.invokeWaitEffectName = invokeWaitEffectName;
    }

    /**
     * Defines the default name of the invoke wait effect (used for all method
     * representations that do not define an invoke wait effect).
     *
     * @param name the default invoke wait effect name to set
     */
    public static void setDefaultInvokeWaitEffectByName(String name) {
        defaultInvokeWaitEffectName = name;
    }

    /**
     * @return the noGUI
     */
    public boolean isNoGUI() {
        return noGUI;
    }

    /**
     * @param noGUI the noGUI to set
     */
    public void setNoGUI(boolean noGUI) {
        this.noGUI = noGUI;
    }

    /**
     * @param minimized the minimized to set
     */
    public void setMinimized(boolean minimized) {
        this.minimized = minimized;
    }

    /**
     * @return the initializer
     */
    public boolean isInitializer() {
        return initializer;
    }

    private void setInitializer(boolean initializer) {
        this.initializer = initializer;
    }

    @Override
    public Integer getLayoutPosition() {
        if (isInitializer()) {
            return 0;
        } else {
            return null;
        }
    }

    /**
     * @return the parameterGroups
     */
    public RepresentationGroup getParameterGroup() {
        return parameterGroup;
    }

    /**
     * @param parameterGroups the parameterGroups to set
     */
    private void setParameterGroup(RepresentationGroup parameterGroup) {
        this.parameterGroup = parameterGroup;
    }

    /**
     * @return the buttonContainer
     */
    public JComponent getInvokeButtonContainer() {
        return buttonContainer;
    }

    Collection<DefaultMethodRepresentation> getProperties() {
        return new ArrayList<DefaultMethodRepresentation>();
    }

    /**
     * @return the connectorsMap
     */
    public VConnector getConnectorByKey(String key) {
        return connectorsMap.get(key);
    }

    /**
     * @return the connectorsMap
     */
    public VConnector addConnectorByKey(String key, VConnector c) {
        return connectorsMap.put(key, c);
    }

    /**
     * @return the connectorsMap
     */
    public VConnector removeConnectorByKey(String key) {
        return connectorsMap.remove(key);
    }

    /**
     * @return the connectorsMap
     */
    public Set<String> getConnectorKeys() {
        return connectorsMap.keySet();
    }

    public Integer getVisualMethodID() {
        return visualMethodId;
    }

    /**
     * @param visualMethodId the visualMethodId to set
     */
    public void setVisualMethodID(int visualMethodId) {
        this.visualMethodId = visualMethodId;
    }

    /**
     * This object's mouse listener.
     */
    class VisualObjectMouseAdapter extends MouseAdapter
            implements Serializable {

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {

            if (getParentObject().getParentWindow() instanceof VisualObject) {
                // sometimes swing has bugs with mouseover events
                // to prevent the case of having more than one selected
                // connector, we first unselect all
                ((VisualObject) getParentObject().getParentWindow()).unselectConnectors();
            }
        }
    }

    @Override
    public String toString() {
        String result = getName();
//        result = result.replace("(", "");
//        result = result.replace(")", "");
//
//        String paramString = "";
//
//        for (int i = 0; i < parameterTypeRepresentations.size(); i++) {
//
//            TypeRepresentationContainer param =
//                    parameterTypeRepresentations.get(i);
//
//            if (i > 0) {
//                paramString += ", ";
//            }
//
//            paramString +=
//                    param.getTypeRepresentation().getType().getSimpleName();
//        }
//
//        result += "(" + paramString + ") : "
//                + getReturnValue().getType().getSimpleName();
        return result;
    }

    @Override
    public void contentChanged() {
        buffer = null;
        style = null;
        super.contentChanged();
    }

    public boolean isReferenceMethod() {
        return getDescription().getMethodType() == MethodType.REFERENCE;
    }

    public boolean isCustomReferenceMethod() {
        return getDescription().getMethodType() == MethodType.CUSTOM_REFERENCE;
    }

    /**
     * Disposes the type representations of this method representation.
     */
    @Override
    public void dispose() {

        if (isDisposed()) {
            return;
        }

        for (TypeRepresentationBase t : getParameters()) {
            try {
                t.dispose();
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }

        if (getReturnValue() != null) {
            try {
                getReturnValue().dispose();
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }

        if (isSelected()) {
            try {
                getParentObject().getMainCanvas().getClipBoard().remove(this);
            } catch (Exception ex) {
                //
            }
        }

        if (getParentObject() != null) {
            setParentObject(null);
        }

        super.dispose();
    }
//    @Override
//    protected void finalize() throws Throwable {
//
//        System.out.println(getClass().getName() + ": Finalize!!!");
//
//        super.finalize();
//    }
}

class MethodTitleBar extends JPanel {

    private CanvasLabel title;
    transient private BufferedImage buffer;
//        private int width = 0;
//        private int height = 0;
    private Style style;
    private Shape shape;
    private DefaultMethodRepresentation method;
    private CloseIcon closeIcon;
    private JPopupMenu menu;

    MethodTitleBar(final DefaultMethodRepresentation method) {

        this.method = method;

        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.LINE_AXIS);

        setLayout(layout);

        title = new CanvasLabel(method, "Title");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(method.getParentObject().
                getMainCanvas().getStyle().getBaseValues().getColor(
                        Canvas.TEXT_COLOR_KEY));

        closeIcon
                = new CloseIcon(method.getParentObject().getMainCanvas());
        closeIcon.setActionListener(new CanvasActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                boolean close = true;

                if (method.askIfClose()) {
                    close = VDialog.showConfirmDialog(method.getMainCanvas(),
                            "Close Method?",
                            "<html><div align=center>"
                            + "Shall the method be closed?<br><br>"
                            + "<b>"
                            + "All unsaved user input of this method will be lost!"
                            + "<b>"
                            + "</div></html>", VDialog.DialogType.YES_NO)
                            == VDialog.YES;
                }

                closeIcon.deactivate();

                if (close) {

                    method.getEffectManager().
                            startDisappearanceEffect(
                                    method, 0.5);

                    method.getParentObject().removeMethodFromView(
                            method);
                }
            }
        });

        int iconSize = 16;
        int iconBoxSize = 21;

        this.add(Box.createGlue());
        Component glue = Box.createHorizontalStrut(iconBoxSize);
        this.add(glue);
        this.add(title);
        this.add(Box.createGlue());
        Box iconBox = Box.createVerticalBox();
        iconBox.add(Box.createVerticalGlue());
        closeIcon.setMinimumSize(new Dimension(iconSize, iconSize));
        closeIcon.setPreferredSize(new Dimension(iconSize, iconSize));
        closeIcon.setMaximumSize(new Dimension(iconSize, iconSize));
        iconBox.add(closeIcon);
        iconBox.add(Box.createVerticalGlue());
        iconBox.setPreferredSize(new Dimension(iconBoxSize, iconBoxSize));
        iconBox.setMinimumSize(iconBox.getPreferredSize());
        this.add(iconBox);
        this.add(Box.createHorizontalStrut(0));

        this.setMinimumSize(null);
        this.setPreferredSize(null);
        this.setMaximumSize(new Dimension(title.getWidth()
                + iconSize + 5, iconSize + 5));
        revalidate();

        shape = new RoundRectangle2D.Double(0, 0, getWidth(),
                getHeight(),
                DefaultMethodRepresentation.DEFAULT_TITLEBAR_CORNER_ARC_WIDTH,
                DefaultMethodRepresentation.DEFAULT_TITLEBAR_CORNER_ARC_WIDTH);

        setOpaque(false);

        MethodMouseControl control = new MethodMouseControl(this);

        addMouseListener(control);
        addMouseMotionListener(control);

        menu = new JPopupMenu("Method");

        initPopupMenu(menu);
    }

    private void initPopupMenu(final JPopupMenu popupMenu) {
        JMenuItem copyItem = new JMenuItem("Copy Parameters");
        copyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ClipboardUtil.paramsToClipboard(getMethod().getParameters());
            }
        });

        popupMenu.add(copyItem);

        JMenuItem pasteItem = new JMenuItem("Paste Parameters");
        pasteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ClipboardUtil.clipboardToParams(getMethod().getParameters());
            }
        });

        popupMenu.add(pasteItem);

        JMenuItem duplicateItem = new JMenuItem("Duplicate");
        duplicateItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                DefaultObjectRepresentation oRep = method.getParentObject();

                // visualMethodID = 0 because it will be automatically
                // computed after ordered layout or getMethodOrder() call
                DefaultMethodRepresentation duplicate
                        = oRep.addMethodToView(
                                method.getDescription(), 0);

                // find insert position and move duplicated method
                // to that location
                OrderedBoxLayout boxLayout = oRep.getMethodLayout();
                int duplicatePos = boxLayout.getOrder().indexOf(method);
                oRep.getMethodLayout().moveTo(duplicate, duplicatePos);

                // copy and paste parameter data via xml
                String duplicateArgs = ClipboardUtil.
                        paramDataToXml(method.getParameters());
                ClipboardUtil.xmlToParamData(
                        duplicateArgs, duplicate.getParameters());

            }
        });

        popupMenu.add(duplicateItem);
    }

    JLabel getTitle() {
        return this.title;
    }

    @Override
    protected void paintComponent(Graphics g) {

        Style newStyle = getMethod().getStyle();

        if (buffer == null
                || buffer.getWidth() != getWidth()
                || buffer.getHeight() != getHeight()
                || style == null
                || style != newStyle) {

            style = newStyle;

            buffer = new BufferedImage(getWidth(), getHeight(),
                    BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2 = buffer.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Composite original = g2.getComposite();

            g2.setComposite(AlphaComposite.Clear);

            shape = new RoundRectangle2D.Double(0, 0, getWidth(),
                    getHeight(),
                    DefaultMethodRepresentation.DEFAULT_TITLEBAR_CORNER_ARC_WIDTH,
                    DefaultMethodRepresentation.DEFAULT_TITLEBAR_CORNER_ARC_WIDTH);

            g2.fill(getShape());

            AlphaComposite ac1
                    = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                            style.getBaseValues().getFloat(
                                    DefaultMethodRepresentation.METHOD_TITLE_TRANSPARENCY_KEY));
            g2.setComposite(ac1);

            Color upperColor = style.getBaseValues().getColor(
                    DefaultMethodRepresentation.METHOD_TITLE_UPPER_COLOR_KEY);
            Color lowerColor = style.getBaseValues().getColor(
                    DefaultMethodRepresentation.METHOD_TITLE_LOWER_COLOR_KEY);

            // TODO implement active state for DefaultMethodRepresentation
//                if (CanvasObject.this.isActive()) {
//                    upperColor = style.getObjectUpperActiveTitleColor();
//                    lowerColor = style.getObjectLowerActiveTitleColor();
//                }
            GradientPaint paint = new GradientPaint(0, 0,
                    upperColor,
                    0, this.getHeight(),
                    lowerColor,
                    false);

            g2.setPaint(paint);

            g2.fill(getShape());

            // aqua like button test
//                Shape originalClip = g2.getClip();
            g2.setComposite(original);

            g2.dispose();

//                float value = 1.0f / 9;
//                float[] BLUR = {
//                    value, value, value,
//                    value, value, value,
//                    value, value, value
//                };
//
//                float[] BLUR = {
//                    0.1f, 0.1f, 0.1f,
//                    0.1f, 0.3f, 0.1f,
//                    0.1f, 0.1f, 0.1f
//                };
//                ConvolveOp vBlurOp = new ConvolveOp(new Kernel(3, 3, BLUR));
//                buffer = vBlurOp.filter(buffer, null);
            g2 = buffer.createGraphics();

            //***button-end***
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            float thickness = 1.f;

            g2.setStroke(new BasicStroke(1));
            g2.setColor(style.getBaseValues().getColor(
                    CanvasWindow.BORDER_COLOR_KEY));

            float topLeftOffset = thickness / 2.f;
            float bottomRightOffset = topLeftOffset * 2;

            g2.draw(new RoundRectangle2D.Double(
                    topLeftOffset, topLeftOffset,
                    getWidth() - bottomRightOffset,
                    getHeight() - bottomRightOffset,
                    DefaultMethodRepresentation.DEFAULT_TITLEBAR_CORNER_ARC_WIDTH,
                    DefaultMethodRepresentation.DEFAULT_TITLEBAR_CORNER_ARC_WIDTH));

            g2.dispose();
        }

        g.drawImage(buffer, 0, 0, null);
    }

    public void contentChanged() {
        buffer = null;
    }

    public Shape getShape() {
        return shape;
    }
//        public Insets getInsets(){
//            return new Insets(3,20,3,20);
//        }

    /**
     * @return the method
     */
    public DefaultMethodRepresentation getMethod() {
        return method;
    }

    void hideCloseIcon(boolean hideCloseIcon) {
        closeIcon.setVisible(!hideCloseIcon);
    }

    /**
     * @return the menu
     */
    public JPopupMenu getMenu() {
        return menu;
    }
} // end class

class MethodMouseControl implements MouseListener, MouseMotionListener {

    private MethodTitleBar titleBar;
    private DefaultMethodRepresentation method;
    private JComponent methodView;
    private OrderedBoxLayout layout;
    private Point initMousePos;
    private Integer componentIndex = null;

    public MethodMouseControl(MethodTitleBar titleBar) {
        this.titleBar = titleBar;
        this.method = titleBar.getMethod();
        this.methodView = method.getParentObject().getMethodView();
        this.layout = method.getParentObject().getMethodLayout();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
            if (!method.isMinimized()) {
                method.minimize();
            } else {
                method.maximize();
            }
        } else if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1) {
            titleBar.getMenu().show(titleBar, e.getX(), e.getY());

            for (MenuElement mE : titleBar.getMenu().getSubElements()) {
                if (mE instanceof JMenuItem) {
                    if (((JMenuItem) mE).getText().contains("Paste")) {
                        ((JMenuItem) mE).setEnabled(
                                ClipboardUtil.
                                isClipboardContentCompatible(
                                        method.getParameters()));
                    }
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        initMousePos = e.getPoint();

    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if (method.isInitializer()) {
            return;
        }

        method.setIngoredByLayout(false);
        method.revalidate();

        if (method.isSelected()) {
            method.setSelected(false);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if (method.isInitializer()) {
            return;
        }

        if (!method.isSelected()) {
            method.setSelected(true);
        }

        method.setIngoredByLayout(true);

        final int moveY = (int) (e.getY() - initMousePos.getY());
        int y = method.getY() + moveY;

        y = Math.max(y, 0);
        y = Math.min(y, methodView.getHeight());

        method.setLocation(titleBar.getMethod().getX(), y);

        componentIndex = null;

        for (int i = 0; i < methodView.getComponentCount(); i++) {
            Component c = methodView.getComponent(i);

            int intersectionHeight = 0;

            if (method.getHeight() < c.getHeight()) {
                intersectionHeight = (method.getHeight() - c.getHeight());
            }

            if (c.getY() < method.getY()) {
                intersectionHeight *= -1;
            }

            if (c != method && c.contains(1,
                    intersectionHeight
                    + method.getY() - c.getY())) {
                componentIndex = i;
                break;
            }
        }

        if (componentIndex != null) {
            layout.moveTo(method, componentIndex);
        }

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //
    }
}
