/* 
 * DefaultTypeRepresentationFactory.java
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

import eu.mihosoft.vrl.annotation.DefaultParamInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.math.TrajectoryDefaultType;
import eu.mihosoft.vrl.system.VClassLoaderUtil;
import eu.mihosoft.vrl.types.*;
import eu.mihosoft.vrl.types.observe.LoadObserveFileStringType;
import eu.mihosoft.vrl.types.observe.LoadObserveFileType;
import eu.mihosoft.vrl.types.observe.SaveObserveFileType;
import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.CanvasChild;
import eu.mihosoft.vrl.visual.VSwingUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p> A type representation factory is responsible for creating type
 * representations, specified by name. It is used from
 * <code>ObjectRepresentation</code> and
 * <code>MethodRepresentation</code> to create representations for the method
 * parameter and return types. </p> <p> If the type factory is advised to return
 * type representations of unknown types, i.e. if no type representation object
 * has been added for this type the representation factory will return a generic
 * type representation. </p> <p> <b>Warning:</b> Generic type representations
 * are only type safe if the corresponding class object is in the current class
 * path! That is not the case, it is possible to connect incompatible generic
 * type representations. </p>
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
final class DefaultTypeRepresentationFactory implements
        CanvasChild, TypeRepresentationFactory {

//    private static final long serialVersionUID = 3671980341726770061L;
    /**
     * the main canvas object
     */
    private Canvas mainCanvas;
    /**
     * instances of supported type representations
     */
    private ArrayList<Class<? extends TypeRepresentationBase>> supportedTypes =
            new ArrayList<Class<? extends TypeRepresentationBase>>();
    /**
     * permanent list
     */
    private static final ArrayList<Class<? extends TypeRepresentationBase>> defaultSupportedTypes =
            new ArrayList<Class<? extends TypeRepresentationBase>>();

    static {

        defaultSupportedTypes.add(ParameterGroupType.class);

        defaultSupportedTypes.add(VoidType.class);
        defaultSupportedTypes.add(ObjectType.class);
        defaultSupportedTypes.add(ObjectSilentType.class);
        defaultSupportedTypes.add(IntegerType.class);
        defaultSupportedTypes.add(LongType.class);
        defaultSupportedTypes.add(IntSliderType.class);
        defaultSupportedTypes.add(IntSliderTextFieldType.class);
        defaultSupportedTypes.add(IntSilentType.class);
        defaultSupportedTypes.add(DoubleType.class);
        defaultSupportedTypes.add(FloatType.class);
        defaultSupportedTypes.add(ShortType.class);
        defaultSupportedTypes.add(BooleanInputType.class);
        defaultSupportedTypes.add(BooleanOutputType.class);
        defaultSupportedTypes.add(StringType.class);
        defaultSupportedTypes.add(HtmlOutputType.class);
        defaultSupportedTypes.add(StringSilentType.class);
        defaultSupportedTypes.add(MessageOutputType.class);
        defaultSupportedTypes.add(InputTextType.class);
        defaultSupportedTypes.add(InputCodeType.class);
        defaultSupportedTypes.add(OutputTextType.class);
        defaultSupportedTypes.add(BufferedImageType.class);
        defaultSupportedTypes.add(ImageType.class);

        defaultSupportedTypes.add(Shape3DType.class);
        defaultSupportedTypes.add(Shape3DArrayType.class);
        defaultSupportedTypes.add(AnimatedShape3DArrayType.class);
        defaultSupportedTypes.add(SilentShape3DType.class);
        defaultSupportedTypes.add(SilentShape3DArrayType.class);
        defaultSupportedTypes.add(GeometryType.class);
        defaultSupportedTypes.add(GeometryListType.class);

        defaultSupportedTypes.add(Function1DType.class);
        defaultSupportedTypes.add(Function2DType.class);
        defaultSupportedTypes.add(Function3DType.class);
        defaultSupportedTypes.add(FunctionType.class);
        defaultSupportedTypes.add(DVectorType.class);
        defaultSupportedTypes.add(GroovyFunction1DType.class);
        defaultSupportedTypes.add(GroovyFunction2DType.class);
        defaultSupportedTypes.add(GroovyFunction3DType.class);
        defaultSupportedTypes.add(TrajectoryDefaultType.class);

        defaultSupportedTypes.add(AppearanceType.class);
        defaultSupportedTypes.add(AppearanceArrayType.class);
        defaultSupportedTypes.add(ColorInputType.class);
        defaultSupportedTypes.add(ColorType.class);
        defaultSupportedTypes.add(FileType.class);
        defaultSupportedTypes.add(FileListType.class);
        defaultSupportedTypes.add(LoadFileType.class);
        defaultSupportedTypes.add(SaveFileType.class);
        defaultSupportedTypes.add(LoadFileArrayType.class);
        defaultSupportedTypes.add(LoadFileArrayArrayType.class);
        defaultSupportedTypes.add(SaveFileArrayType.class);
        defaultSupportedTypes.add(CodeBlockType.class);
        defaultSupportedTypes.add(SelectionInputType.class);
        defaultSupportedTypes.add(SelectionOutputType.class);
        defaultSupportedTypes.add(MessageTypeSelectionType.class);

        //
        defaultSupportedTypes.add(IntegerArrayType.class);
        defaultSupportedTypes.add(LongArrayType.class);

        defaultSupportedTypes.add(FloatArrayType.class);
        defaultSupportedTypes.add(DoubleArrayType.class);

        defaultSupportedTypes.add(BooleanArrayType.class);
        defaultSupportedTypes.add(StringArrayType.class);
        defaultSupportedTypes.add(StringSilentArrayType.class);

        defaultSupportedTypes.add(Shape3DArrayOutputType.class);
        defaultSupportedTypes.add(BufferedImageArrayInputType.class);

        defaultSupportedTypes.add(ColorArrayType.class);

        //
        defaultSupportedTypes.add(AddToCanvasType.class);
        defaultSupportedTypes.add(GetSelectedObjectsType.class);
        defaultSupportedTypes.add(GetCanvasType.class);
        defaultSupportedTypes.add(GetMethodRepresentationType.class);
        defaultSupportedTypes.add(GetObjectRepresentation.class);
        defaultSupportedTypes.add(GetCanvasWindowType.class);
        defaultSupportedTypes.add(GetVisualIDType.class);

        defaultSupportedTypes.add(VGeometry3DType.class);
        defaultSupportedTypes.add(SilentVGeometry3DType.class);
        defaultSupportedTypes.add(VGeometry3DArrayType.class);
        defaultSupportedTypes.add(VGeometry3DAppearanceType.class);

        defaultSupportedTypes.add(ResolutionSelectionType.class);

        defaultSupportedTypes.add(LoadFolderType.class);
        defaultSupportedTypes.add(SaveFolderType.class);
        
        defaultSupportedTypes.add(SaveFolderStringType.class);

        defaultSupportedTypes.add(LoadFileStringType.class);
        defaultSupportedTypes.add(SaveFileStringType.class);

        defaultSupportedTypes.add(InputValueType.class);
        defaultSupportedTypes.add(OutputValueType.class);

        defaultSupportedTypes.add(PackageNameType.class);
        defaultSupportedTypes.add(ClassNameType.class);
        defaultSupportedTypes.add(MethodNameType.class);
        defaultSupportedTypes.add(VariableNameType.class);

        defaultSupportedTypes.add(StringSelectionInputType.class);
        defaultSupportedTypes.add(IntegerSelectionInputType.class);

        defaultSupportedTypes.add(PluginNameType.class);
        defaultSupportedTypes.add(PluginVersionType.class);


        defaultSupportedTypes.add(IntegerExceptionTestType.class);

        defaultSupportedTypes.add(MultipleOutputType.class);

        defaultSupportedTypes.add(ContainerOutputType.class);

        defaultSupportedTypes.add(LoadObserveFileType.class);
        defaultSupportedTypes.add(SaveObserveFileType.class);
        defaultSupportedTypes.add(LoadObserveFileStringType.class);
    }

    /**
     * Constructor.
     *
     * @param mainCanvas the main canvas
     */
    public DefaultTypeRepresentationFactory(Canvas mainCanvas) {
        setMainCanvas(mainCanvas);
        supportedTypes.addAll(defaultSupportedTypes);
    }

    /**
     * Adds a new type representation to the factory. If the factory already
     * contains a completely equal type representation this one will be
     * replaced. In this context equal means if their class object names and
     * their styles are the same.
     *
     * @param t an instance of the type representation that is to be added
     */
    @Override
    public final void addType(Class<? extends TypeRepresentationBase> tClass) {

        for (Class<? extends TypeRepresentationBase> i : getSupportedTypes()) {

            if (i.getClass().getName().equals(tClass.getName())
                    && TypeUtil.getStyle(i).equals(TypeUtil.getStyle(tClass))) {
                getSupportedTypes().remove(i);
                break;
            }
        } // end for

        getSupportedTypes().add(tClass); // we add only the class of t
//        t.setMainCanvas(mainCanvas);
    }

    /**
     * Returns a type representation that provides an input interface.
     *
     * @param type the class object of the parameter that is to be visualized
     * @param paramInfo the parameter annotation that is to be used to customize
     * the type representation
     * @return If a type representations with the requested properties exists it
     * will be returned; returns <code>null</code> otherwise.
     */
    @Override
    public TypeRepresentationBase getInputInstance(Class<?> type,
            ParamInfo paramInfo) {

        type = VClassLoaderUtil.convertPrimitiveToWrapper(type);

        TypeRepresentationBase result = null;

        if (paramInfo != null) {
            // define representation type as input
            RepresentationType valueType = RepresentationType.INPUT;

            // get compatible representations
            ArrayList<Class<? extends TypeRepresentationBase>> compatibleRepresentations =
                    findCompatibleTypes(type, valueType);

            // get requested style
            String representationStyle = paramInfo.style();

            result =
                    chooseRepresentation(type, compatibleRepresentations,
                    representationStyle);

            result.setMainCanvas(mainCanvas);
            result.setCurrentRepresentationType(valueType);

            result.setNullValidInput(paramInfo.nullIsValid());

            String valueName = paramInfo.name();
            if (!(valueName == null || valueName.length() == 0)) {
                result.setValueName(valueName);
            }

            // delete string if only whitespaces
            if (valueName.matches("\\s+")) {
                result.setValueName("");
            }

            String valueOptions = paramInfo.options();

            if (valueOptions == null) {
                valueOptions = "";
            }

            // setParamInfo must be called before setValueOptions
            // some plugins rely on that
            result.setParamInfo(paramInfo);
            result.setValueOptions(valueOptions);
            result.evaluateValueOptions();

        } else {
            result = getInputInstance(type);
            result.setParamInfo(paramInfo);
        }

        return result;
    }

    @Override
    public TypeRepresentationBase getOutputInstance(Class<?> type,
            MethodInfo methodInfo, OutputInfo outputInfo) {

        type = VClassLoaderUtil.convertPrimitiveToWrapper(type);

        TypeRepresentationBase result = null;
        if (methodInfo != null) {

            // define representation type as output
            RepresentationType valueType = RepresentationType.OUTPUT;

            // get compatible representations
            ArrayList<Class<? extends TypeRepresentationBase>> compatibleRepresentations =
                    findCompatibleTypes(type, valueType);

            // get requested style
            String representationStyle = methodInfo.valueStyle();

            result =
                    chooseRepresentation(type, compatibleRepresentations,
                    representationStyle);

            result.setMainCanvas(mainCanvas);

            result.setCurrentRepresentationType(valueType);

            String returnValueName = methodInfo.valueName();
            if (!(returnValueName == null || returnValueName.length() == 0)) {
                result.setValueName(returnValueName);
            }

            // delete string if only whitespaces
            if (returnValueName.matches("\\s+")) {
                result.setValueName("");
            }

            String valueOptions = methodInfo.valueOptions();

            if (valueOptions == null) {
                valueOptions = "";
            }

            result.setValueOptions(valueOptions.trim());

            result.evaluateValueOptions();
        } else if (outputInfo != null) {

            // define representation type as output
            RepresentationType valueType = RepresentationType.OUTPUT;

            // get compatible representations
            ArrayList<Class<? extends TypeRepresentationBase>> compatibleRepresentations =
                    findCompatibleTypes(type, valueType);

            // get requested style
            String representationStyle = outputInfo.style();

            result =
                    chooseRepresentation(type, compatibleRepresentations,
                    representationStyle);

            result.setMainCanvas(mainCanvas);

            result.setCurrentRepresentationType(valueType);

            String returnValueName = outputInfo.name();
            if (!(returnValueName == null || returnValueName.length() == 0)) {
                result.setValueName(returnValueName);
            }

            // delete string if only whitespaces
            if (returnValueName.matches("\\s+")) {
                result.setValueName("");
            }

            String valueOptions = outputInfo.options();

            if (valueOptions == null) {
                valueOptions = "";
            }

            result.setValueOptions(valueOptions.trim());

            result.evaluateValueOptions();
        } else {
            result = getOutputInstance(type);
        }

        if (result instanceof ArrayBaseType) {
            ((ArrayBaseType) result).setElementOutputInfo(outputInfo);
        }

        result.setParamInfo(null);

        return result;
    }

    /**
     * Tries to find a type representation that meets the demands. If no
     * representation with the rquested style support can be found it tries to
     * provide a representation that supports the default style. If no
     * representation can be found the eu.mihosoft.vrl.types.UnsupportedType is
     * used instead.
     *
     * @param type the class object of the parameter that is to be visualized
     * @param availableRepresentations a list containing the available
     * representations
     * @param representationStyle the requested representation style
     * @return the type representation that does meet the demanded properties
     * best
     */
    private TypeRepresentationBase chooseRepresentation(
            Class<?> type,
            ArrayList<Class<? extends TypeRepresentationBase>> availableRepresentations,
            String representationStyle) {

        // is no style entered 
        if (representationStyle.trim().isEmpty()) {
            representationStyle = "default";
        }

        TypeRepresentationBase value = new UnsupportedType(type);

        boolean errorWhileCreatingInstance = false;

        // tries to find a representation supports the requested style
        for (Class<? extends TypeRepresentationBase> t : availableRepresentations) {
            String style = TypeUtil.getStyle(t);

            if (style.equals(representationStyle)) {

                try {
                    value = t.newInstance();
                    value.setMainCanvas(mainCanvas);
                } catch (Throwable tr) {
                    errorWhileCreatingInstance = true;
                    Logger.getLogger(DefaultTypeRepresentationFactory.class.getName()).
                            log(Level.SEVERE, null, tr);
                }

//                System.out.println(
//                        ">> TypeRepresentationFactory.getInstance():"
//                        + " request satisfied!");

                break;

//
            }
        } // end for availableRepresentations

        // if no appropriate representation could been found
        // try to find a representation that supports the default style
        if (value instanceof UnsupportedType) {
            for (Class<? extends TypeRepresentationBase> t : availableRepresentations) {
                String style = TypeUtil.getStyle(t);

                if (style.equals("default")) {
                    try {
                        value = t.newInstance();
                        value.setMainCanvas(mainCanvas);

                    } catch (Throwable tr) {
                        errorWhileCreatingInstance = true;
                        Logger.getLogger(
                                DefaultTypeRepresentationFactory.class.getName()).
                                log(Level.SEVERE, null, tr);
                    }
                    
//                    System.out.println(
//                            ">> TypeRepresentationFactory.getInstance():"
//                            + " request not satisfied (using default)!");

                    break;


                }
            } // end for availableRepresentations
        }

        // if we could not find a representation show a warning message
//        if (value instanceof UnsupportedType) {
//            System.out.println(">> TypeRepresentationFactory.getInstance(): "
//                    + "no default defined (using unsupported)!");
//        }

        // we didn't find a matching representation. thus, an
        // UnsupportedType object will be returned
        if (value == null) {
            value = new UnsupportedType(type);
        }

        if (errorWhileCreatingInstance) {
            value.setBorder(VSwingUtil.createDebugBorder());
        }

        return value;
    }

//    private Class<?> getTypeFromTrepClass(Class<? extends TypeRepresentationBase> cls) {
//        Method m;
//        try {
//            m = cls.getMethod("getType");
//            return (Class<?>) m.invoke(null);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(DefaultTypeRepresentationFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(DefaultTypeRepresentationFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvocationTargetException ex) {
//            Logger.getLogger(DefaultTypeRepresentationFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchMethodException ex) {
//            Logger.getLogger(DefaultTypeRepresentationFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SecurityException ex) {
//            Logger.getLogger(DefaultTypeRepresentationFactory.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return Object.class;
//    }
//
//    private String getStyleNameFromTrepClass(Class<? extends TypeRepresentationBase> cls) {
//        Method m;
//        try {
//            m = cls.getMethod("getStyleName");
//            return (String) m.invoke(null);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(DefaultTypeRepresentationFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(DefaultTypeRepresentationFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvocationTargetException ex) {
//            Logger.getLogger(DefaultTypeRepresentationFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchMethodException ex) {
//            Logger.getLogger(DefaultTypeRepresentationFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SecurityException ex) {
//            Logger.getLogger(DefaultTypeRepresentationFactory.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return "";
//    }
//
//    private Boolean getSupportsFromTrepClass(Class<? extends TypeRepresentationBase> cls, RepresentationType repType) {
//        Method m;
//        try {
//            m = cls.getMethod("supports", RepresentationType.class);
//            return (Boolean) m.invoke(null, repType);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(DefaultTypeRepresentationFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(DefaultTypeRepresentationFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvocationTargetException ex) {
//            Logger.getLogger(DefaultTypeRepresentationFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchMethodException ex) {
//            Logger.getLogger(DefaultTypeRepresentationFactory.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SecurityException ex) {
//            Logger.getLogger(DefaultTypeRepresentationFactory.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return false;
//    }
    /**
     * This method tries to find type representations that meet the demands.
     * This method does not return the optimal representation. It only
     * preselects representations that are capable of displaying the requested
     * type of parameters.
     *
     * @param type the class object of the parameter that is to be visualized
     * @param valueType the representation type that is demanded
     * @return a list containing all representations that are capable of
     * displaying the requestedtype of parameters
     */
    private ArrayList<Class<? extends TypeRepresentationBase>> findCompatibleTypes(
            Class<?> type,
            RepresentationType valueType) {

        ArrayList<Class<? extends TypeRepresentationBase>> compatibleRepresentations =
                new ArrayList<Class<? extends TypeRepresentationBase>>();

        for (Class<? extends TypeRepresentationBase> t : getSupportedTypes()) {

//            System.out.println("T: " + t);
//            System.out.println("type:" + type);
//            System.out.println("supports:" + valueType);

            if (TypeUtil.getType(t).equals(type) && TypeUtil.supports(t, valueType)) {
//                System.out.println("TR: " + t.getType().getName());
//                System.out.println("TO: " + type.getName());
                compatibleRepresentations.add(t);
            }
        } // end for supportedTypes

        return compatibleRepresentations;
    }

    /**
     * Returns an instance of an input type representation specified by name.
     *
     * @param type the type, i.e., the class object of the parameters that are
     * visualized by the type representation that is to be returned
     * @return an instance of the specified type representation
     */
    private TypeRepresentationBase getInputInstance(Class<?> type) {
        return getInputInstance(type, new DefaultParamInfo());
    }

    /**
     * Returns an instance of an output type representation specified by name.
     *
     * @param type the class object of the parameter that is to be visualized
     * @return an instance of the specified type representation
     */
    private TypeRepresentationBase getOutputInstance(Class<?> type) {
        return getOutputInstance(type, new DefaultMethodInfo(), new DefaultOutputInfo());
    }

    @Override
    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    @Override
    public final void setMainCanvas(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;
    }

//    /**
//     * Loads type representations from xml decoder.
//     * @param d the xml decoder that is to be used to deserialize the
//     *          type representations
//     */
//    public void loadTypes(XMLDecoder d) {
//        Object result = d.readObject();
//        AbstractTypeRepresentations typeRepresentations =
//                (AbstractTypeRepresentations) result;
//        for (AbstractTypeRepresentation t : typeRepresentations) {
//            if (t.getTypeRepresentation() != null) {
//                addType(t.getTypeRepresentation());
//            }
//        }
//    }
//
//    /**
//     * Saves type representations using an xml encoder.
//     * @param e the xml encoder that is to be used to serialize the type
//     *          representations
//     */
//    public void saveTypes(XMLEncoder e) {
//        AbstractTypeRepresentations typeRepresentations =
//                new AbstractTypeRepresentations();
//        for (TypeRepresentationBase t : getSupportedTypes()) {
//            typeRepresentations.add(new AbstractTypeRepresentation(t));
//        }
//        e.writeObject(typeRepresentations);
//    }
    /**
     * Returns a list containing all supported type representations.
     *
     * @return the supportedTypes a list containing all supported type
     * representations
     */
    @Override
    public Collection<Class<? extends TypeRepresentationBase>> getSupportedTypes() {
        return supportedTypes;
    }

    /**
     * Removes all type representations with specified class name.
     *
     * @param className class name of the type representations that are to be
     * removed
     */
    @Override
    public boolean removeTypeByClassName(String className) {
        boolean result = false;

        ArrayList<Class<? extends TypeRepresentationBase>> delList =
                new ArrayList<Class<? extends TypeRepresentationBase>>();

        for (Class<? extends TypeRepresentationBase> t : supportedTypes) {
            if (t.getName().equals(className)) {
                delList.add(t);
            }
        }

        for (Class<? extends TypeRepresentationBase> t : delList) {
            System.out.println("(R)" + t.getClass().getName());
//            t.dispose();
            result = supportedTypes.remove(t);
        }

        return result;
    }

//    /**
//     * Removes all type representations with specified style name.
//     *
//     * @param style style name of the type representations that are to be
//     * removed
//     */
//    @Override
//    public boolean removeTypeByStyle(String style) {
//        TypeRepresentationBase type = null;
//
//        for (Class<TypeRepresentationBase> t : supportedTypes) {
//            if (t.getStyleName().equals(style)) {
//                type = t;
//            }
//        }
//
//        return supportedTypes.remove(type);
//    }
    /**
     * Defines the list of supported type representations.
     *
     * @param supportedTypes the type representations that are to be supported
     */
    @Override
    public void setSupportedTypes(
            Collection<Class<? extends TypeRepresentationBase>> supportedTypes) {
        this.supportedTypes.clear();
        this.supportedTypes.addAll(supportedTypes);
    }

    /**
     * Dispose additional resources, e.g., Java 3D render threads.
     */
    @Override
    public void dispose() {
//        for (TypeRepresentationBase t : getSupportedTypes()) {
//            // only dispose the prototype if it is not in the "permanent list".
//            // The prototypes in the "permanet list" are shared for all
//            // canvas instances
//            if (!defaultSupportedTypes.contains(t)) {
//                t.dispose();
//            }
//        }
    }
}
