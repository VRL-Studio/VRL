/* 
 * TypeRepresentationBase.java
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

import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.effects.SelectionEffect;
import eu.mihosoft.vrl.system.VClassLoader;
import eu.mihosoft.vrl.types.BufferedImageType;
import eu.mihosoft.vrl.types.GetVisualIDType;
import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.*;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 * <p>
 * Base class for type representations.</p>
 * <p>
 * <b>Note:</b> inherit this class to create a completely new type representation. Additionally, it is possible to
 * customize an already existing type representation. For textfield based type representations,
 * {@link eu.mihosoft.vrl.types.VTextFieldBasedTypeRepresentation} is a good starting point. Image based type
 * representations should inherit {@link eu.mihosoft.vrl.types.BufferedImageType}. </p>
 * <p>
 * <b>Example (Groovy code):</b></p>
 * <p>
 * The class to visualize:</p>
 * <code>
 * <pre>
 * class Circle {
 *
 *   public int radius
 *
 *   public Circle(Integer radius) {
 *       this.radius = radius
 *   }
 * }
 * </pre>
 * </code>
 *
 * <p>
 * The corresponding type representation:</p>
 * <code>
 * <pre>
 * &#064;TypeInfo(type=Circle.class, input = false, output = true, style="default")
 * class CircleType extends BufferedImageType {
 *
 *   public CircleType(){
 *       setValueName(" ")
 *   }
 *
 *   public void setViewValue(Object o) {
 *       def circle = o as Circle
 *       def image = ImageUtils.createCompatibleImage(300,300)
 *       def g2 = image.createGraphics()
 *
 *       g2.setColor(Color.green)
 *       def thickness = 5
 *       g2.setStroke(new BasicStroke(thickness))
 *
 *       int centerX=image.getWidth()/2
 *       int centerY=image.getHeight()/2
 *
 *       int x = centerX + thickness/2 - circle.radius
 *       int y = centerY + thickness/2 - circle.radius
 *
 *       int width = 2 * circle.radius-thickness/2 - 1
 *       int height = 2 * circle.radius-thickness/2 - 1
 *
 *       g2.drawOval(x,y,width,height)
 *       g2.dispose()
 *
 *       super.setViewValue(image)
 *   }
 *
 *   public Object getViewValue() {
 *       return this.&#64;value
 *   }
 * }
 * </pre>
 * </code>
 *
 * <p>
 * And finally, a circle creator that uses the type representation:</p>
 *
 * <code>
 * <pre>
 * class CircleCreator {
 *
 *   public Circle createCircle(
 *       &#64;ParamInfo(name="Radius") Integer radius) {
 *       return new Circle(radius)
 *   }
 * }
 * </pre>
 * </code>
 *
 * <p>
 * The result should look like this:
 * <p>
 *
 * <br/> <img src="./doc-files/circlecreator.png"/> <br/>
 *
 * <p>
 * <b>Supported value options:</b><br> <ul> <li>hideConnector (defines whether to hide the connector of this type
 * representation)</li>
 * <li>invokeOnChange (defines whether to invoke the method this type representation belongs to on input change)</li>
 * </ul>
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 * @see BufferedImageType
 * @see ParamInfo
 */
public abstract class TypeRepresentationBase extends VComponent
        implements TypeRepresentation, Selectable, FullScreenComponent {

    protected Object value;
    protected Object viewValue;
    private Class type;
//    private Canvas mainCanvas;
    private DefaultMethodRepresentation parentMethod = null;
    private boolean upToDate = false;
//    /*DEPRECATED*/private ArrayList<RepresentationType> supportedRepresentationType =
//            new ArrayList<RepresentationType>();
    private String representationStyle = null;
    private RepresentationType currentRepresentationType;
    private Connector connector;
    protected TypeRepresentationLabel nameLabel;
    public boolean writable = true;
    private boolean validValue = true;
    private boolean nullValidInput = false;
    private JComponent inputComponent;
    private ArrayList<Document> inputDocuments = new ArrayList<Document>();
    private LayoutType layoutType = LayoutType.DYNAMIC;
    private boolean noValidation = false;
    private boolean selected = false;
    private SelectionEffect colorizeEffect;
    private String valueOptions = "";
    private boolean hideConnector = false;
    private boolean invokeMethodOnValueChange = false;
    private CustomParamData customData = new CustomParamData();
    private ArrayList<CapabilityChangedListener> capabilityChangedListeners
            = new ArrayList<CapabilityChangedListener>();
    private boolean fullScreenMode = false;
    private boolean updateLayoutOnValueChange = true;
    private boolean noGUI = false;
    protected boolean serialization = true;
    private boolean warningIfNoCodeGeneration = true;
    private boolean skipEmptyViewCallInSetValue = false;
    private ParamInfo paramInfo;
    /**
     * list of action listeners
     */
    private Collection<ActionListener> actionListeners
            = new ArrayList<ActionListener>();
    /**
     * Style key to access color that represents invalid values
     */
    public static final String INVALID_VALUE_COLOR_KEY
            = "TypeRepresentation:Value[invalid]:Color";
    /**
     * Style key to access color that represents valid values
     */
    public static final String VALID_VALUE_COLOR_KEY
            = "TypeRepresentation:Value[valid]:Color";
    /**
     * Style key to access color that represents values with warnings
     */
    public static final String WARNING_VALUE_COLOR_KEY
            = "TypeRepresentation:Value[warning]:Color";
    public static final String SET_VIEW_VALUE_ACTION = "set-view-value";

    /**
     * Constructor.
     */
    public TypeRepresentationBase() {
        super(null);
        init();
    }

    /**
     * Constructor.
     *
     * @param layout Swing layout manager
     */
    public TypeRepresentationBase(LayoutManager layout) {
        setLayout(layout);
        init();
    }

    private void init() {
//        setUpToDate(true);

        nameLabel = new TypeRepresentationLabel(this, "");
        setAlignmentY(0.5f);

        colorizeEffect = new SelectionEffect(this);
        colorizeEffect.setColor(new Color(0.f, 0.f, 1.f, 0.3f));
        getEffects().add(colorizeEffect);

        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));

        setOpaque(false);

//        setDebugMode();
    }

    @Override
    public Shape getShape() {
        return new RoundRectangle2D.Double(0, 0,
                getWidth(), getHeight(), 20, 20);
    }

    /**
     * @return the changeListeners
     */
    public Collection<ActionListener> getActionListeners() {
        return actionListeners;
    }

    /**
     * Fires an action.
     *
     * @param event the event
     */
    protected void fireAction(ActionEvent event) {
        for (ActionListener l : actionListeners) {
            l.actionPerformed(event);
        }
    }

    /**
     * Switches to debug mode. In debug mode the bounding box (rectangular border) will be drawn to indicate the
     * dimensions of this type representation.
     */
    public void setDebugMode() {
        setBackground(Color.RED);
        setBorder(BorderFactory.createEtchedBorder());
    }

    @Override
    public Insets getInsets() {
        return new Insets(3, 3, 3, 3);
    }

//    @Override
//    protected void paintComponent(Graphics g) {
//        // want the object to be invisible
//    }
    /**
     * Defines the return value as up to date. A return value usually is defined as up tp date if after the last
     * assignment from method call none of the input values (their representations) changed.
     */
    public void setReturnTypeUpToDate() {
        if (parentMethod != null) {
            TypeRepresentationBase retType
                    = parentMethod.getReturnValue();
            retType.setUpToDate(true);
        }
    }

    /**
     * Defines the data of this representation as outdated. If the data of a type representation is outdated a method
     * call will be triggered if data from the corresponding return type representation is requested.
     */
    public void setDataOutdated() {

        if (this.isInput() && getMainCanvas() != null) {

            if (!getMainCanvas().getDataProcessingMode().
                    equals(DataProcessingMode.UPDATE_OFF)) {
                setReturnTypeOutdated();
                setValidValue(true);
                //TODO: add remove-animations command!
            }

            // check whether all input type representations of the parent method
            // are valid, i.e., if they are not empty or allow null value
            boolean paramsAreNotEmpty = true;
            if (getParentMethod() != null) {
                for (TypeRepresentationBase t : getParentMethod().getParameters()) {
                    if (t.getViewValueWithoutValidation()
                            == null && t.isNullValidInput() == false) {
                        paramsAreNotEmpty = false;
                        break;
                    }
                }
            }

            // invoke method if paraminfo contains "autoinvoke" call option
            // and if all input type representations are not empty or allow
            // null value
            if (isInvokeMethodOnValueChange() && paramsAreNotEmpty) {
                VisualCanvas canvas = (VisualCanvas) getMainCanvas();
                if (!canvas.isLoadingSession()) {
                    getParentMethod().invokeAsCallParent(false);
                }
            }
        }
    }

    /**
     * Defines the value of the return type representation as outdated. It does forward this information recursively to
     * all methods that are connected as receiver.
     */
    public void setReturnTypeOutdated() {

        boolean weHaveAParentMethod = parentMethod != null;

        if (weHaveAParentMethod && !getMainCanvas().getDataProcessingMode().
                equals(DataProcessingMode.UPDATE_OFF)) {

            // get the return type representation of this type representation
            TypeRepresentationBase retType
                    = parentMethod.getReturnValue();

            // set return type representation to be not up to date
            retType.setUpToDate(false);

            retType.setValue(null);

            // if the return value is connected then get the connections and
            // call the setReturnTypeOutdated() from the other type
            // representations
            // get the return connector
            Connector c = getParentMethod().getOutputConnector();

            // if c==null this means the parent method does not return anything,
            // i.e. return type is void and we don't do anything in this case!
            if (c != null) {

                // get all of the return value's connections
                java.util.List<Connection> connections
                        = getMainCanvas().getDataConnections().getAllWith(c);

                // for every connection
                for (Connection connection : connections) {
                    Connector i = connection.getSender();

                    // get the receiver
                    Connector r = connection.getReceiver();

                    // and the type representation container it is atached to
                    TypeRepresentationContainer returnTypeContainer
                            = (TypeRepresentationContainer) r.getValueObject();

                    // get the returntype representation of the container
                    TypeRepresentationBase returnTypeRepresentation
                            = returnTypeContainer.getMethod().getReturnValue();

                    TypeRepresentationBase tb
                            = returnTypeContainer.getTypeRepresentation();

                    boolean differentMethods
                            = !this.getParentMethod().
                            equals(tb.getParentMethod());

                    if (differentMethods) {
//                        tb.setValue(new EmptyObject());
                        tb.setValue(null);
                    }

                    // if the returntype representation is not up to date
                    // then do nothing because we want to prevent circular
                    // method calls
                    // else recursively call this method from the return type
                    // representation
                    if (returnTypeRepresentation.isReturnTypeUpToDate()) {
                        returnTypeRepresentation.setReturnTypeOutdated();
                    }
                } // end for

            } // end if c!=null

        } // end if parentMethod != null
    }

    /**
     * Returns the method that is defined as parent method.
     *
     * @return the method that is defined as parent method
     */
    public DefaultMethodRepresentation getParentMethod() {
        return parentMethod;
    }

    /**
     * Defines the method that is defined as parent method.
     *
     * @param parentMethod the method that is to be defined as parent method
     */
    public void setParentMethod(DefaultMethodRepresentation parentMethod) {
        this.parentMethod = parentMethod;
    }

    /**
     * Indicates wether return value is up to date or not. A return value usually is defined as up tp date if after the
     * last assignment from method call none of the input values (their representations) changed.
     *
     * @return <code>true</code> if return value is up to date; <code>false</code> otherwise
     */
    public boolean isReturnTypeUpToDate() {
        boolean result = false;
        if (parentMethod != null) {
            TypeRepresentationBase retType
                    = parentMethod.getReturnValue();
            result = retType.isUpToDate();
        }
        return result;
    }

    /**
     * Indikates whether type representation is up to date.
     *
     * @return <code>true</code> if return value is up to date; <code>false</code> otherwise
     */
    protected boolean isUpToDate() {
        boolean result = this.upToDate;
        if (getMainCanvas().getDataProcessingMode().
                equals(DataProcessingMode.ALWAYS_INVOKE)) {
            result = false;
        }

        return result;
    }

    /**
     * Defines whether the value is up to date or not. A return value usually is defined as up tp date if after the last
     * assignment from method call none of the input values (their representations) changed.
     *
     * @param upToDate <code>true</code> if return value is to be defined as up to date; <code>false</code> otherwise
     */
    public void setUpToDate(boolean upToDate) {
        this.upToDate = upToDate;
    }

    /**
     * Defines the type representation as valid (input value is correct)
     */
    protected void validateValue() {
        if (!isNoValidation()) {
            setValidValue(true);
        }
    }

    /**
     * Defines the type representation as invalid (because of incorrect or missing input)
     */
    protected void invalidateValue() {
        if (!isNoValidation()) {
            setValidValue(false);
        }
    }

    /**
     * Will be called if the type representation is valid (input value is correct).
     */
    protected void valueValidated() {
        // default does not have an effect
    }

    /**
     * Will be called if the type representation is invalid (because of incorrect or missing input).
     */
    protected void valueInvalidated() {
        // default does not have an effect
    }

    @Override
    public void setCurrentRepresentationType(
            RepresentationType representationType) {
        currentRepresentationType = representationType;
    }

//    @Override
//    public void addSupportedRepresentationType(
//            RepresentationType representationType) {
//        if (!supportedRepresentationType.contains(representationType)) {
//            supportedRepresentationType.add(representationType);
//        }
//    }
//    /**
//     * Removes a specified representation type from the list of supported
//     * representation types.
//     *
//     * @param representationType the type to remove
//     */
//    public void removeSupportedRepresentationType(
//            RepresentationType representationType) {
//        supportedRepresentationType.remove(representationType);
//    }
    @Override
    public boolean isInput() {
        return getCurrentRepresentationType() == RepresentationType.INPUT;
    }

    @Override
    public boolean isOutput() {
        return getCurrentRepresentationType() == RepresentationType.OUTPUT;
    }

//    @Override
//    public boolean supportsInput() {
//        return supports(RepresentationType.INPUT);
//    }
//
//    @Override
//    public boolean supportsOutput() {
//        return supports(RepresentationType.OUTPUT);
//    }
//
////    @Override
//    public static boolean supports(RepresentationType representationType) {
//        return supportedRepresentationType.contains(representationType);
//    }
    /**
     * Returns the connector of this type representation.
     *
     * @return the connector of this type representation
     */
    public Connector getConnector() {
        return connector;
    }

    /**
     * Defines the connector of this type representation.
     *
     * @param connector the connector to set
     */
    public void setConnector(Connector connector) {
        this.connector = connector;
    }

//    /**
//     * Returns a deep copy of the type representation.
//     *
//     * @return a deep copy of the type representation
//     */
//    @Override
//    public TypeRepresentationBase copy() {
//        TypeRepresentationBase result = null;
//
//        try {
//            //        try {
//            result = getClass().newInstance();
//        } catch (InstantiationException ex) {
//            Logger.getLogger(TypeRepresentationBase.class.getName()).
//                    log(Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(TypeRepresentationBase.class.getName()).
//                    log(Level.SEVERE, null, ex);
//        }
//
//        result.setValueName(getValueName());
//        result.setValueOptions(getValueOptions());
//        result.setNullValidInput(isNullValidInput());
//
//        return result;
//    }
    @Override
    protected void newCanvasAssigned(Canvas oldCanvas, Canvas newCanvas) {

        try {
            if (capabilityChangedListeners != null) {

                if (oldCanvas != null) {
                    for (CapabilityChangedListener l : capabilityChangedListeners) {
                        oldCanvas.getCapabilityManager().
                                removeCapabilityChangedListener(l);
                    }
                }

                if (newCanvas != null) {
                    for (CapabilityChangedListener l : capabilityChangedListeners) {
                        newCanvas.getCapabilityManager().
                                addCapabilityChangedListener(l);
                    }
                }
            }
        } catch (Exception ex) {
            //
        } finally {
            super.newCanvasAssigned(oldCanvas, newCanvas);
        }
    }

    /**
     * Indicates whether the representation value is valid.
     *
     * @return <code>true</code> if the representation value is valid; <code>false</code> otherwise
     */
    public boolean isValidValue() {
        return validValue;
    }

    /**
     * Defines whether the representation value is valid.
     *
     * @param validValue the state to set
     */
    public void setValidValue(boolean validValue) {
        if (validValue) {
            valueValidated();
        } else {
            valueInvalidated();

//            if (isInput()) {
//
//                MessageBox box = mainCanvas.getMessageBox();
//
//                box.addUniqueMessage("Can't invoke method:",
//                        "TypeRepresentation&lt;" + type.toString() +
//                        "&gt;.getValue(): empty or invalid input data!",
//                        getConnector(),
//                        MessageType.ERROR);
//            }
        }
        this.validValue = validValue;
    }

    /**
     * Evaluates the contract, e.g., checks for correct data type or range condition.
     */
    protected void evaluateContract() {
        Connector valueConnector = null;

        if (!isHideConnector()) {
            valueConnector = getConnector();
        }

        if (value != null) {
            validateValue();

            double transparency
                    = getParentMethod().getParentObject().getParentWindow().
                    getTransparency();

            if (!isHideConnector() && VSwingUtil.isVisible(getConnector())) {

                getMainCanvas().getEffectPane().pulse(getConnector(),
                        MessageType.INFO_SINGLE, transparency);
            }

        } else if (isInput()) {
            if (!isValidValue()) {
                invalidateValue();
                System.out.println(getInvalidInputDataMessage().getText());

                MessageBox box = getMainCanvas().getMessageBox();
                box.addUniqueMessage(getInvalidInputDataMessage(),
                        valueConnector);
            }
            if ((!isNullValidInput()) && isValidValue()) {
                invalidateValue();
                System.out.println(getEmptyInputDataMessage().getText());

                MessageBox box = getMainCanvas().getMessageBox();
                box.addUniqueMessage(getEmptyInputDataMessage(), valueConnector);
            }
        }
    }

    /**
     * Defines the message to show if the input data is invalid. Override this method to define a custom message.
     *
     * @return the message to show if the input data is invalid
     */
    protected Message getInvalidInputDataMessage() {
        return new Message("Can't invoke method:",
                ">> TypeRepresentation&lt;" + getType().toString()
                + "&gt;.getValue(): invalid input data!", MessageType.ERROR);
    }

    /**
     * Defines the message to show if the input data is empty. verride this method to define a custom message
     *
     * @return the message to show if the input data is empty
     */
    protected Message getEmptyInputDataMessage() {
        return new Message("Can't invoke method:",
                ">> TypeRepresentation&lt;" + getType().toString()
                + "&gt;.getValue(): empty input data!", MessageType.ERROR);
    }

    @Override
    public void setValue(final Object o) {

        if (!skipEmptyViewCallInSetValue) {
            VSwingUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    emptyView();
                }
            });
        }

        value = o;

        if (value != null) {
            if (getType().isInstance(o)) {

                VSwingUtil.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        setViewValue(o);
//                        setUpToDate(true);
                        repaint();
                    }
                });

                if (isUpdateLayoutOnValueChange()) {

                    VSwingUtil.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            updateLayout();
                        }
                    });

                }
            } else if (getMainCanvas() != null) {
                VSwingUtil.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        MessageBox box = getMainCanvas().getMessageBox();
                        box.addUniqueMessage("Can't display value:",
                                ">> TypeRepresentation&lt;" + getType().toString()
                                + "&gt;.getValue(): value type does not match ("
                                + o.getClass().toString()
                                + "). Please "
                                + "check the implementation of your type "
                                + "representation or proxy object!",
                                getConnector(),
                                MessageType.ERROR);
                    }
                });
            }
        }
    }

    @Override
    public Object getValue() {
        VSwingUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                validateValue();
            }
        });

        // if outdated or null, we retrieve the value from the ui
        // unfortunately we have to wait for the ui thread, which makes things
        // slow :(
        if (isInput()
                //                && !isUpToDate()
                && !getMainCanvas().getDataConnections().
                alreadyConnected(getConnector())
                || value == null) {
            VSwingUtil.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    Object o = getViewValue();
//                    setUpToDate(true);
                    value = o;
                    evaluateContract();
                }
            });
        } else {
            evaluateContract();
        }

        if (isUpdateLayoutOnValueChange()) {
            VSwingUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    updateLayout();
                }
            });
        }

        return value;
    }

    void setType(Class<?> type) {
        this.type = type;
    }

    final public Class<?> getType() {

        // EXPERIMENTAL 04.05.2012
//        if (type.equals(void.class) || type.equals(Void.class)) {
//            return type;
//        }
//
//        if (getMainCanvas() == null) {
//            return type;
//        }
//
//        if (getCanvasClassLoader() == null) {
//            return type;
//        }
//
//        type = getCanvasClassLoader().reloadClass(type);
        if (type == null) {
            type = TypeUtil.getType(this.getClass());
        }

        return type;
    }

    @Override
    public void emptyView() {
        deleteViewValue();
    }

    public void deleteViewValue() {
        viewValue = null;
    }

    public void deleteValue() {
        value = null;
    }

    @Override
    public void setViewValue(Object o) {
        viewValue = o;

        fireAction(new ActionEvent(this, 0, SET_VIEW_VALUE_ACTION));
    }

    @Override
    public Object getViewValue() {

        return viewValue;
    }

    /**
     * Returns the view value without validating it.
     *
     * @return the view value without validating it
     */
    public Object getViewValueWithoutValidation() {
        Object result = null;
        setNoValidation(true);
        result = getViewValue();
        setNoValidation(false);
        return result;
    }

    /**
     * Returns the representation style.
     *
     * @return representation style
     */
    public String getStyleName() {
        if (representationStyle == null) {
            representationStyle = TypeUtil.getStyle(this.getClass());
        }

        return representationStyle;
    }

    /**
     * Updates the layout of this representation.
     */
    public void updateLayout() {
        if (inputComponent != null
                && getLayoutType() != layoutType.STATIC
                && !isFullScreenMode()) {
            // preferred width of input fields not correctly computed
            // therefore we need a correction vaule
            int widthCorrectionValue = 5;

            int width = 0;
            int height = 0;

            getInputComponent().setPreferredSize(null);
            getInputComponent().setMaximumSize(null);

            switch (getLayoutType()) {
                case DYNAMIC:
                    width = Math.max(
                            getInputComponent().getMinimumSize().width,
                            getInputComponent().getPreferredSize().width
                            + widthCorrectionValue);
                    height = Math.max(
                            getInputComponent().getMinimumSize().height,
                            getInputComponent().getPreferredSize().height);
                    break;
                case DYNAMIC_WIDTH:
                    width = Math.max(
                            getInputComponent().getMinimumSize().width,
                            getInputComponent().getPreferredSize().width
                            + widthCorrectionValue);
                    break;
                case DYNAMIC_HEIGHT:
                    height = Math.max(
                            getInputComponent().getMinimumSize().height,
                            getInputComponent().getPreferredSize().height);
                    break;
                case STATIC:
                    break;
            }

            getInputComponent().setMaximumSize(new Dimension(width, height));
            getInputComponent().setPreferredSize(new Dimension(width, height));

            if (connector != null && connector.getValueObject() != null) {
                TypeRepresentationContainer valueObject
                        = (TypeRepresentationContainer) getConnector().getValueObject();
                valueObject.setMinimumSize(null);
                valueObject.setPreferredSize(null);
                valueObject.setMaximumSize(null);

                valueObject.revalidate();
            }

            // TODO: find another solution
            Component c = getParentOfClass(UIWindow.class);

            if (c != null) {
                c.setMinimumSize(null);
                c.setPreferredSize(null);
                c.setMaximumSize(null);

            }

            c = getParentOfClass(CanvasWindow.class);

            if (c != null) {
                c.setMinimumSize(null);
                c.setPreferredSize(null);
                c.setMaximumSize(null);

            }

//            getParent().revalidate();
        } //end if (inputComponent != null)
    }

    /**
     * Write method used for binary serialization.
     *
     * @param oos the output stream to use
     * @throws java.io.IOException
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        throw new NotSerializableException("Binary session serialization is "
                + "evil! This class is not serializable!");
    }

    /**
     * Read method used for binary deserialization.
     *
     * @param ois the input stream to use
     * @throws java.io.IOException
     */
    private void readObject(ObjectInputStream ois) throws IOException {
        throw new NotSerializableException("Binary session serialization is "
                + "evil! This class is not serializable!");
    }

    /**
     * Returns the input documents of this representation.
     *
     * @return the input documents of this representation
     */
    public ArrayList<Document> getInputDocument() {
        return inputDocuments;
    }

    /**
     * Defines the input document and the input component of this representation. Calling this method will enable layout
     * updating on document changes for the specified input component.
     *
     * @param input the input component
     * @param inputDocuments the input documents to set
     */
    public void setInputDocument(final JComponent input,
            Document... inputDocuments) {
        this.setInputComponent(input);

        for (Document d : inputDocuments) {

            this.inputDocuments.add(d);

            d.addDocumentListener(
                    new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    setDataOutdated();
                    updateLayout();
//                        getMainCanvas().getEffectPane().pulse(getConnector(),
//                                MessageType.INFO_SINGLE);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    setDataOutdated();
                    updateLayout();
//                        getMainCanvas().getEffectPane().pulse(getConnector(),
//                                MessageType.INFO_SINGLE);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    //
                }
            });
        }
    }

    /**
     * Defines the input documents of this representation.
     *
     * @param inputDocument the input documents to set
     */
    public void setInputDocument(Document... inputDocuments) {
        this.inputDocuments = new ArrayList<Document>();

        for (Document d : inputDocuments) {

            this.inputDocuments.add(d);

            d.addDocumentListener(
                    new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    setDataOutdated();
//                        getMainCanvas().getEffectPane().pulse(getConnector(),
//                                MessageType.INFO_SINGLE);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    setDataOutdated();
//                        getMainCanvas().getEffectPane().pulse(getConnector(),
//                                MessageType.INFO_SINGLE);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    //
                }
            });
        }
    }

    /**
     * Returns the input component of this type representation.
     *
     * @return the input component of this type representation or <code>null</code> if no such component is defined
     */
    public JComponent getInputComponent() {
        return inputComponent;
    }

    /**
     * Defines the input component of this representation. Input components can dynamically change their size. The
     * representation and its parents also update their size if that is necessary
     *
     * @param inputComponent the input component to set
     */
    public void setInputComponent(JComponent inputComponent) {
        this.inputComponent = inputComponent;
    }

    /**
     * Indicates whether <code>null</code> is a valid value.
     *
     * @return <code>true</code> if <code>null</code> is a valid value; <code>false</code> otherwise
     */
    public boolean isNullValidInput() {
        return nullValidInput;
    }

    /**
     * Defines whether <code>null</code> is a valid value.
     *
     * @param nullValidInput the state to set
     */
    public void setNullValidInput(boolean nullValidInput) {
        this.nullValidInput = nullValidInput;
    }

    /**
     * Indicates whether are value validation is disabled.
     *
     * @return <code>true</code> if value valudation is disabled; <code>false</code> otherwise
     */
    protected boolean isNoValidation() {
        return noValidation;
    }

    /**
     * Defines whether to disable value validation.
     *
     * @param noValidation the state to set
     */
    protected void setNoValidation(boolean noValidation) {
        this.noValidation = noValidation;
    }

    @Override
    public String getValueName() {
        return nameLabel.getText();
    }

    @Override
    public void setValueName(String name) {
        nameLabel.setText(name);

        if (name.equals(" ") || name.equals("")) {
            nameLabel.setVisible(false);
        } else {
            nameLabel.setVisible(true);
        }
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;

        if (selected) {
            colorizeEffect.setUnselect(false);
            getEffectManager().startEffect(colorizeEffect, 0.3);
        } else {
            colorizeEffect.setUnselect(true);
            getEffectManager().startEffect(colorizeEffect, 0.3);
        }
    }

    /**
     * Returns the topmost parent of this representation that is instance of a given class object.
     *
     * @param cl the class object
     * @return the topmost parent of this representation that is instance of a given class object or <code>null</code>
     * if no such parent exists
     */
    public Component getParentOfClass(Class cl) {

        // find out top level parent of mainCanvas
        Container c = this;
        Container parent = c.getParent();

        while (parent != null) {
            parent = c.getParent();
            if (parent != null) {
                c = parent;
            }
            if (c.getClass().equals(cl)) {
                break;
            }
        }

        // if we iterated through all parents (reached the toplevel parent) and
        // didn't find an object of class cl we return null instead of the
        // toplevel parent
        if (parent == null) {
            c = null;
        }
        return c;
    }

    /**
     * Returns the layout type of the input component.
     *
     * @return the layout type
     */
    public LayoutType getLayoutType() {
        return layoutType;
    }

    /**
     * Defines the layout type of the input document of this representation.
     *
     * @param layoutType the layout type to set
     */
    public void setLayoutType(LayoutType layoutType) {
        this.layoutType = layoutType;
    }

    /**
     * Returns the value options of this representation.
     *
     * @return the value options of this representation
     */
    public String getValueOptions() {
        return valueOptions;
    }

    /**
     * Defines the value options of this representation.
     *
     * @param valueOptions the value options to set
     */
    public void setValueOptions(String valueOptions) {
        this.valueOptions = valueOptions;
    }

    /**
     * Returns the value options evaluator (Groovy script).
     *
     * @return the value options evaluator (Groovy script)
     */
    protected Script getOptionEvaluator() {
        Script result = null;

//        try {
        GroovyShell shell = new GroovyShell();
        result = shell.parse(getValueOptions());
        result.run();
//        } catch (Exception ex) {
//            if (getMainCanvas() != null) {
//                MessageBox mBox = getMainCanvas().getMessageBox();
//
//                mBox.addUniqueMessage("Can't evaluate value options:",
//                        "Class " + getType().getName() +
//                        getParentMethod().getName() + "<br>" + ">> " +
//                        ex.getMessage(), getConnector(), MessageType.WARNING);
//            }
//        }

        return result;
    }

    /**
     * Evaluates the value options of this representation.
     */
    public void evaluateValueOptions() {

        // should improve performance (we don't create script parser if empty)
        if (getValueOptions() == null || getValueOptions().isEmpty()) {
            return;
        }

        try {
            evaluationRequest(getOptionEvaluator());

            Object property = null;

            if (getValueOptions().contains("hideConnector")) {
                property = getOptionEvaluator().getProperty("hideConnector");
            }

            if (property != null) {
                hideConnector = (Boolean) property;
            }

            property = null;

            if (getValueOptions().contains("invokeOnChange")) {
                property = getOptionEvaluator().getProperty("invokeOnChange");

                if (this.isOutput()) {
                    MessageBox mBox = getMainCanvas().getMessageBox();

                    mBox.addUniqueMessage("Can't evaluate value options:",
                            "Class " + getType().getName() + "<br>" + ">> "
                            + "output type representations do not support the "
                            + "value option \"<b><i>invokeOnChange</i></b>\"!",
                            getConnector(), MessageType.WARNING);
                }
            }

            if (property != null) {
                invokeMethodOnValueChange = (Boolean) property;
            }

            property = null;

            if (getValueOptions().contains("serialization")) {
                property = getOptionEvaluator().getProperty("serialization");
            }

            if (property != null) {
                serialization = (boolean) (Boolean) property;
            }
            
            property = null;
            
            if (getValueOptions().contains("skipEmptyViewCallInSetValue")) {
                property = getOptionEvaluator().getProperty("skipEmptyViewCallInSetValue");
            }

            if (property != null) {
                setSkipEmptyViewCallInSetValue((boolean) (Boolean) property);
            }
            

        } catch (Exception ex) {
            if (getMainCanvas() != null) {
                MessageBox mBox = getMainCanvas().getMessageBox();

                String message = ex.toString();

                if (ex.getCause() != null) {
                    message += "\n>> " + ex.getCause().toString();
                }

                mBox.addUniqueMessage("Can't evaluate value options:",
                        "Class " + getType().getName() + "<br>" + ">> "
                        + message, getConnector(), MessageType.WARNING);

            }
            Logger.getLogger(TypeRepresentationBase.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Requests evaluation of the value options that are usually specified in
     * {@link eu.mihosoft.vrl.annotation.ParamInfo}. This method should be overloaded in custom type representations if
     * they depend on custom value options.
     * <p>
     * <b>Note:</b> if a custom class does not directly extend {@link TypeRepresentationBase}, e.g., if it derives
     * {@link BufferedImageType} it is necessary to call the super method before performing the custom evaluation as
     * these classes implement this method as well.</p>
     *
     * <p>
     * <b>Sample Code:</b></p>
     * <pre>
     * &#64;Override
     * protected void evaluationRequest(Script script) {
     *       super.evaluationRequest(script);
     *
     *       Object property = null;
     *
     *       if (getValueOptions().contains("myValue")) {
     *           property = getOptionEvaluator().getProperty("myValue");
     *       }
     *
     *       if (property != null) {
     *           this.myValue = (Integer) property; // TODO please add type check
     *       }
     * }
     * </pre>
     *
     * @param script the options evaluator to use
     */
    protected void evaluationRequest(Script script) {
    }

    /**
     * <p>
     * Defines whether to hide the conector of this type representation.
     * </p>
     * <p>
     * <b>Note:</b> This method does not fire action events etc to notify the value options about the visibility state.
     * </p>
     *
     * @param state the state to set
     */
    public void setHideConnector(boolean state) {
        hideConnector = state;

        if (getConnector() != null) {
            getConnector().setVisible(state);
        }
    }

    /**
     * Indicates whether to hide the connector of this type representation.
     *
     * @return the <code>true</code> if the connector shall be hidden; <code>false</code> otherwise
     */
    public boolean isHideConnector() {
        return hideConnector;
    }

    /**
     * Disposes additional resources, e.g., Java 3D render threads. It will be called when the parent canvas window is
     * closed.
     */
    @Override
    public void dispose() {

        if (isDisposed()) {
            return;
        }

        try {
            if (isSelected()) {
                getMainCanvas().getClipBoard().unselect(this);
            }

            for (CapabilityChangedListener l : capabilityChangedListeners) {
                if (l != null) {
                    getMainCanvas().getCapabilityManager().
                            removeCapabilityChangedListener(l);
                }
            }

            setMainCanvas(null);
            setParentMethod(null);
        } catch (Exception ex) {
            //
        } finally {
            super.dispose();
        }
    }

    /**
     * @return the invokeMethodOnValueChange
     */
    public boolean isInvokeMethodOnValueChange() {
        return invokeMethodOnValueChange;
    }

    /**
     * @param invokeMethodOnValueChange the invokeMethodOnValueChange to set
     */
    public void setInvokeMethodOnValueChange(boolean invokeMethodOnValueChange) {
        this.invokeMethodOnValueChange = invokeMethodOnValueChange;
    }

    /**
     * @return the customData
     */
    public CustomParamData getCustomData() {
        return customData;
    }

    /**
     * @param customData the customData to set
     */
    public void setCustomData(CustomParamData customData) {
        this.customData = customData;
    }

    /**
     * This method will be called when custom param data has been loaded from file. Override this method to handle
     * custom read operations or to convert the data to custom data structures.
     * <p>
     * <b>Note:</b> changes to the data other than loading will not trigger this method. If a change notification is
     * required, a custom solution must be provided. </p>
     */
    public void evaluateCustomParamData() {
        //
    }

    /**
     * Returns the current representation type (input or output).
     *
     * @return the current representation type
     * @see RepresentationType
     */
    public RepresentationType getCurrentRepresentationType() {
        return currentRepresentationType;
    }

    /**
     * The capability changed listeners of this window. To add listeners use the null null null null null null null null
     * null null null     {@link CanvasWindow#addCapabilityListener(
     * eu.mihosoft.vrl.visual.CapabilityChangedListener)} instead.
     *
     * @return the capabilityChangedListeners
     */
    public ArrayList<CapabilityChangedListener> getCapabilityChangedListeners() {
        return capabilityChangedListeners;
    }

    /**
     * Adds a capability changed listener to the capability manager. The listener will be removed if the
     * <code>dispose()</code> method of this window is called.
     *
     * @param listener the listener to add
     */
    public void addCapabilityListener(CapabilityChangedListener listener) {
        if (getMainCanvas() != null) {
            getMainCanvas().getCapabilityManager().
                    addCapabilityChangedListener(listener);
        }
        getCapabilityChangedListeners().add(listener);
    }

    /**
     * This method is called after this typerepresentation has been added to a method representation (including setting
     * connector etc.). It may be used to perform custom initialization based on option evaluation etc.
     */
    public void addedToMethodRepresentation() {
        //
    }

//  ONLY USED FOR DEBUGGING
//    @Override
//    protected void finalize() throws Throwable {
//
//        System.out.println(getClass().getName() + ": Finalize!!!");
//
//        super.finalize();
//    }
    @Override
    public void enterFullScreenMode(Dimension size) {
        setFullScreenMode(true);

        revalidate();
    }

    @Override
    public void paintComponent(Graphics g) {
        if (isFullScreenMode()) {
            g.setColor(getMainCanvas().getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        } else {
            super.paintComponent(g);
        }
    }

    @Override
    public void leaveFullScreenMode() {
        setFullScreenMode(false);

        revalidate();
    }

    @Override
    public JComponent customViewComponent() {
        return null;
    }

    /**
     * @return the fullScreenMode
     */
    public boolean isFullScreenMode() {
        return fullScreenMode;
    }

    /**
     * Indicates whether this type representation is connected via data connection.
     *
     * @return <code>true</code> if this type representation is connected; <code>false</code> otherwise
     */
    public boolean isConnected() {
        return getMainCanvas().
                getDataConnections().alreadyConnected(getConnector());
    }

    /**
     * @param fullScreenMode the fullScreenMode to set
     */
    protected void setFullScreenMode(boolean fullScreenMode) {
        this.fullScreenMode = fullScreenMode;
    }

    /**
     * @return the updateLayoutOnValueChange
     */
    public boolean isUpdateLayoutOnValueChange() {
        return updateLayoutOnValueChange;
    }

    /**
     * @param updateLayoutOnValueChange the updateLayoutOnValueChange to set
     */
    public void setUpdateLayoutOnValueChange(boolean updateLayoutOnValueChange) {
        this.updateLayoutOnValueChange = updateLayoutOnValueChange;
    }

    /**
     * Returns the value as source code.
     *
     * @return the value as source code
     */
    public String getValueAsCode() {
        return null;
    }

    /**
     * Indicates whether to prefer binary serialization over XML encoding. Sometimes it is very time consuming to encode
     * via XML, e.g. for Collection types such as {@link java.util.ArrayList} as for each of the entries in the list an
     * XML tag is used. Thus, a criterion might be the list size.
     *
     * @return <code>true</code> if binary serialization is prefered; <code>false</code> otherwise
     */
    public boolean preferBinarySerialization() {
        return false;
    }

    /**
     * Indicates whether to prevent value serialization. Sometimes the value may be too big or unimportant to be saved.
     * This property can be controlled via the variable <code>serialization</code> in the options string of the param
     * info.
     *
     * @return <code>true</code> if the value shall not be serialized; <code>false</code> otherwise
     */
    public boolean noSerialization() {

        boolean paramSerialization = true;

        if (value != null) {
            paramSerialization
                    = ComponentUtil.isParameterSerializationEnabled(value.getClass());
        }

        return !serialization || !paramSerialization;
    }

    /**
     * Indicates whether this type representation shall be visualized.
     *
     * @return <code>true</code> if this type representation shall not be visualized; <code>false</code> otherwise
     */
    public boolean isNoGUI() {
        return noGUI;
    }

    /**
     * Defines whether this type representation shall be visualized. Usually, values are visualized. This is the
     * preferred behavior. But there are some cases where it is preferable to completely hide this type representation
     * from the GUI. One use case is {@link GetVisualIDType}.
     *
     * @param noGUI the state to set
     */
    public void setNoGUI(boolean noGUI) {
        this.noGUI = noGUI;
    }

    protected VClassLoader getCanvasClassLoader() {
        return ((VisualCanvas) getMainCanvas()).getClassLoader();
    }

    /**
     * Indicates whether this type representation is compatible to a given type representation. It is assumed that this
     * type representation (where the method is called from) is an input type representation.
     *
     * @param tRep the type representation to check
     * @return <code>true</code> if this type representation is compatible to the given type representation;
     * <code>false</code> otherwise
     */
    protected ConnectionResult compatible(TypeRepresentationBase tRep) {

        boolean typeEqual = false;

        if (isInput() && tRep != null && tRep.isOutput()) {
            typeEqual = getType().isAssignableFrom(tRep.getType());
        } else if (isOutput() && tRep != null && tRep.isInput()) {
            typeEqual = tRep.getType().isAssignableFrom(getType());
        }

        if (typeEqual) {
            return new ConnectionResult(
                    null, ConnectionStatus.VALID);
        }

        Message errorMsg = null;

        String senderTypeName = getTypeString(tRep);
        String receiverTypeName = getTypeString(this);

        if (senderTypeName.equals(receiverTypeName)) {
            errorMsg = new Message("Cannot establish connection:",
                    "Connection: "
                    + "Source/Target value type not equal!"
                    + "<br>"
                    + " -> Source: "
                    + Message.EMPHASIZE_BEGIN
                    + senderTypeName
                    + Message.EMPHASIZE_END
                    + "<br>"
                    + " -> Target: "
                    + Message.EMPHASIZE_BEGIN
                    + receiverTypeName
                    + Message.EMPHASIZE_END
                    + "<br>"
                    + ">> <b>Note:</b> even though the type names are equal the"
                    + " corresponding classloaders are different."
                    + "Reloading the project will solve this problem.",
                    MessageType.ERROR_SINGLE);
        } else {
            errorMsg = new Message("Cannot establish connection:",
                    "Connection: "
                    + "Source/Target value type not equal!"
                    + "<br>"
                    + " -> Source: "
                    + Message.EMPHASIZE_BEGIN
                    + senderTypeName
                    + Message.EMPHASIZE_END
                    + "<br>"
                    + " -> Target: "
                    + Message.EMPHASIZE_BEGIN
                    + receiverTypeName
                    + Message.EMPHASIZE_END,
                    MessageType.ERROR_SINGLE);
        }

        return new ConnectionResult(
                errorMsg, ConnectionStatus.ERROR_VALUE_TYPE_MISSMATCH);
    }

    private String getTypeString(TypeRepresentationBase tRep) {
        String typeName = "<unknown type>";

        if (tRep.getType() != null) {
            typeName = tRep.getType().getName();
        }

        return typeName;
    }

    /**
     * @return the warningIfNoCodeGeneration
     */
    public boolean isWarningIfNoCodeGeneration() {
        return warningIfNoCodeGeneration;
    }

    /**
     * @param warningIfNoCodeGeneration the warningIfNoCodeGeneration to set
     */
    public void setWarningIfNoCodeGeneration(boolean warningIfNoCodeGeneration) {
        this.warningIfNoCodeGeneration = warningIfNoCodeGeneration;
    }

    /**
     * @return the paramInfo
     */
    public ParamInfo getParamInfo() {
        return paramInfo;
    }

    /**
     * @param paramInfo the paramInfo to set
     */
    public void setParamInfo(ParamInfo paramInfo) {
        this.paramInfo = paramInfo;
    }

    /**
     * Enables parameter serialization (default).
     * <p>
     * <b>Note:</b>May be overridden by custom param options.</p>
     *
     */
    public void enableSerialization() {
        this.serialization = true;
    }

    /**
     * Disables parameter serialization.
     * <p>
     * <b>Note:</b>May be overridden by custom param options.</p>
     *
     */
    public void disableSerialization() {
        this.serialization = false;
    }
    
    /**
     * Defines whether to skip {@link TypeRepresentation#emptyView()} call in
     * {@link TypeRepresentation#setValue(java.lang.Object) }.
     * 
     * @param skip value which determins whether to skip
     */
    protected void setSkipEmptyViewCallInSetValue(boolean skip) {
        this.skipEmptyViewCallInSetValue = skip;
    }

    /**
     * Indicates whether {@link TypeRepresentation#emptyView()} call is skipped in
     * {@link TypeRepresentation#setValue(java.lang.Object) }.
     * 
     * @return {@code true} if skipped; {@code false} otherwise
     */
    protected boolean isSkipEmptyViewCallInSetValue() {
        return skipEmptyViewCallInSetValue;
    }
    
    
}
