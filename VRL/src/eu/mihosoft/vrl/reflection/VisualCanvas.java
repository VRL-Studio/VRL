/* 
 * VisualCanvas.java
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
package eu.mihosoft.vrl.reflection;

import eu.mihosoft.vrl.system.VClassLoader;
import eu.mihosoft.vrl.io.vrlx.VSessionInitializer;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.io.VProjectController;
import eu.mihosoft.vrl.io.vrlx.AbstractCode;
import eu.mihosoft.vrl.io.vrlx.AbstractCodes;
import eu.mihosoft.vrl.lang.groovy.GroovyCompiler;
import eu.mihosoft.vrl.lang.groovy.GroovyCodeWindow;
import eu.mihosoft.vrl.io.vrlx.AbstractSession;
import eu.mihosoft.vrl.lang.CompilerProvider;
import eu.mihosoft.vrl.lang.CompilerProviderImpl;
import eu.mihosoft.vrl.lang.InstanceCreator;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.lang.visual.ClassInfoObject;
import eu.mihosoft.vrl.lang.visual.ForLoopBegin;
import eu.mihosoft.vrl.lang.visual.ForLoopEnd;
import eu.mihosoft.vrl.lang.visual.InputObject;
import eu.mihosoft.vrl.lang.visual.OutputObject;
import eu.mihosoft.vrl.lang.visual.StartObject;
import eu.mihosoft.vrl.lang.visual.StopObject;
import eu.mihosoft.vrl.system.VRL;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.util.ArrayList;
import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.CanvasCapabilities;
import eu.mihosoft.vrl.visual.CanvasWindow;
import eu.mihosoft.vrl.visual.CapabilityChangedListener;
import eu.mihosoft.vrl.visual.CapabilityManager;
import eu.mihosoft.vrl.visual.Connection;
import eu.mihosoft.vrl.visual.Connections;
import eu.mihosoft.vrl.visual.MenuAdapter;
import eu.mihosoft.vrl.visual.VFilter;
import eu.mihosoft.vrl.visual.VFilteredTreeModel;
import eu.mihosoft.vrl.visual.VSimpleFilteredTreeModel;
import eu.mihosoft.vrl.visual.VSwingUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * <p>
 * VisualCanvas is the toplevel container class. All visual elements use it as
 * drawing device. Its main purpose is to display object representations. As it
 * is a Java Bean it can easily be used from within the Swing designer that
 * comes with the Netbeans Platform. </p>
 * <p>
 * It also supports d&d gestures. It is possible to add objects directly by
 * dropping them over the canvas. Use the transfer handler and object
 * transferable of this package to add this functionality to the d&d source.
 * </p>
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
@ObjectInfo(showInheritedMethods = true)
public final class VisualCanvas extends Canvas {

    /**
     * the object inspector that is used by this canvas
     */
    private VisualObjectInspector inspector;
    /**
     * the type factory that is used by this canvas
     */
    private TypeRepresentationFactory typeFactory;
    /**
     * the object tree that is connected to this canvas
     */
    private transient ObjectTree objectTree;
    /**
     * a list containing reference tasks that are used to resolve reference
     * requests by UIWindow objects
     */
    private ArrayList<ReferenceTask> referenceTasks
            = new ArrayList<ReferenceTask>();
    /**
     * a list containing call option tasks that are used to resolve call
     * requests
     */
    private ArrayList<CallOptionEvaluationTask> callOptionTasks
            = new ArrayList<CallOptionEvaluationTask>();
    /**
     *
     */
    private AbstractCodes codes = new AbstractCodes();
    /**
     *
     */
    private VClassLoader classLoader = new VClassLoader();
    /**
     *
     */
    private VCanvasPopupMenu popupMenu;
//    /**
//     *
//     */
//    private String sessionFileName;
//    /**
//     *
//     */
//    private PluginController pluginController;
//    /**
//     *
//     */
//    private PluginController globalPluginController;
//    /**
//     *
//     */
//    private boolean loadPluginsAsGlobal = false;
    /**
     *
     */
    private long invocationDelay = 0;
    /**
     *
     */
    private boolean invokeWaitEffect = false;
    /**
     *
     */
    private DefaultObjectRepresentationFactory objectRepresentationFactory;
    /**
     * Indicates whether codes are already compiled when loading from file. This
     * can happen if custom code compilation is requested in sessioninitializer.
     */
    private boolean codesAlreadyCompiled = false;
    /**
     * Code of this session.
     */
    private AbstractSession session;
    /**
     * Compiler provider.
     */
    private CompilerProvider compilerProvider;
    /**
     *
     */
    private ComponentTree componentTree;
    /**
     *
     */
    private VFilteredTreeModel componentTreeModel;
    /**
     *
     */
    private VProjectController projectController;
    /**
     *
     */
    private Collection<VFilter> componentFilters = new ArrayList<VFilter>();
    /**
     *
     */
    private ComponentController componentController;
    /**
     *
     */
    private VisualObjects visualObjects;

    public void addComponentSearchFilter(VFilter f) {
        componentFilters.add(f);
    }

    public Iterable<VFilter> getComponentSearchFilters() {
        return componentFilters;
    }

    /**
     * Constructor.
     */
    public VisualCanvas() {
        init();
    }

    /**
     * Constructor.
     */
    public VisualCanvas(boolean nestedCanvas) {
        super(nestedCanvas);
        init();
    }

    /**
     * Initializes the canvas.
     */
    protected void init() {
        typeFactory = new DefaultTypeRepresentationFactory(this);

        visualObjects = new VisualObjects(this);
        setWindows(visualObjects);

        inspector = new VisualObjectInspector(this);

        DropTarget dropTarget = new DropTarget(
                this, new VisualDropTargetListener(this));

        setVCanvasPopupMenu(new VCanvasPopupMenu(this));
        setComponentTreeModel(new VFilteredTreeModel(new DefaultMutableTreeNode("")));
        setComponentTree(new ComponentTree(getComponentTreeModel(), this));

        componentController
                = new JTreeComponentController(getComponentTreeModel());
//        getPopupMenu().setParent(treeController);

        // if editing is not allowed then remove code windows from canvas
        getCapabilityManager().addCapabilityChangedListener(
                new CapabilityChangedListener() {

                    @Override
                    public void capabilityChanged(
                            CapabilityManager manager, Integer bit) {
                                if (!getCapabilityManager().isCapable(
                                        CanvasCapabilities.ALLOW_EDIT)) {
                                    for (CanvasWindow w : getWindows().
                                    getAllWindowsOfType(
                                            GroovyCodeWindow.class)) {
                                        w.close();
                                    }
                                }
                            }
                });

//        pluginController = new PluginController();
//        pluginController.setMainCanvas(this);
//
//        globalPluginController = new PluginController();
//        getGlobalPluginController().setMainCanvas(this);
        setSessionInitializer(new VSessionInitializer());
        setObjectRepresentationFactory(new DefaultObjectRepresentationFactory(this));

        getControlFlowConnections().setPrototype(new ControlFlowConnection());
        setCompilerProvider(new CompilerProviderImpl(this));
        setSession(new AbstractSession());
    }

    /**
     * Reduces VRL capabilities. This only allows changing type prepresentations
     * and invoking methods.
     */
    public void enableStaticPlayerMode() {
        getCapabilityManager().disableAllCapabilities();
        getCapabilityManager().enableCapability(
                CanvasCapabilities.ALLOW_VIEW);
    }

    /**
     * Reduces VRL capabilities. In addition to static mode this allows moving
     * objects around but not adding, removing and editing components.
     */
    public void enableSimplePlayerMode() {
        getCapabilityManager().disableAllCapabilities();
        getCapabilityManager().enableCapability(
                CanvasCapabilities.ALLOW_DRAG);
    }

    /**
     * Reduces VRL capabilities. In addition to moving objects around it allows
     * to change connections.
     */
    public void enableAdvancedPlayerMode() {
        getCapabilityManager().disableAllCapabilities();
        getCapabilityManager().enableCapability(
                CanvasCapabilities.ALLOW_DRAG);
        getCapabilityManager().enableCapability(
                CanvasCapabilities.ALLOW_CONNECT);
    }

    /**
     * Reduces VRL capabilities. In addition to moving objects around it allows
     * to instantiate objects, i.e., to add components from the popup menu.
     */
    public void enableFullPlayerMode() {
        getCapabilityManager().disableAllCapabilities();
        getCapabilityManager().enableCapability(
                CanvasCapabilities.ALLOW_INSTANCIATION);
    }

    /**
     * Enables all VRL capabilities. This is necessary for full development
     * support. Usually this is the default.
     */
    public void enableDevelopmentMode() {
        getCapabilityManager().disableAllCapabilities();
        getCapabilityManager().enableCapabilities(
                CanvasCapabilities.DEFAULT_CAPABILITIES);
    }

    /**
     * <p>
     * On Mac OS X it is necessary to compile a dummy class to ensure that
     * methods with same name but different parameters occure in the same order
     * as if they were saved to file, e.g.: </p>
     * <p>
     * void test1(); and void test1(Integer a); </p>
     * <p>
     * Without the dummy class different order is used! </p>
     * <p>
     * Another possible cause are visual representations of old object instances
     * where the order doesn't match the newest version. </p>
     */
    @Deprecated
    public void initGroovyCompiler() {
        // TODO remove this ugly hack!
        try {
            AbstractCode dummyCode = new AbstractCode();
            dummyCode.setCode("class Dummy { public void dummy(){};}");
            getCodes().add(dummyCode);
            getCodes().addToClassPath(this);
        } catch (Exception ex) {
            //
        }
    }

    /**
     * Adds an object to the canvas and to the canvas's object inspector.
     * <p>
     * <b>Note:</b> Please use
     * {@link eu.mihosoft.vrl.reflection.ComponentUtil#addObject(java.lang.Class, eu.mihosoft.vrl.reflection.VisualCanvas, java.awt.Point)}
     * to add an object with source code. </p>
     *
     * @param o the object that is to be added
     * @param loc the location at which the the object will be placed, the
     * origin's position on the x axis is <code>objectwith/2</code>
     * @return the graphical representation of the object
     */
    public synchronized VisualObject addObject(Object o, Point loc) {
//        Point location = new Point(loc);
//        VisualObject result = null;
//
//        if (getInspector().addObject(o) || getInspector().getObjectID(o) != null) {
//
//            ObjectDescription oDesc = getInspector().getObjectDescription(o);
//
//            VisualObject object = new VisualObject(getInspector(), oDesc, this);
//
//            AbstractCode a = getCodes().getByClass(o.getClass());
//
//            if (a != null) {
//                try {
//                    object.addSourceIcon();
//                } catch (Exception ex) {
//                }
//            }
//
//            this.getWindows().add(object);
//
//            // compute location offset
////            location.x -= object.getWidth() / 2;
//
//
//            // we also have to mind the insets
////            location.x += object.getInsets().left;
//
//            // we also have to mind the insets
////            location.y += object.getInsets().top;
//
//            // windows must not be added at invisible position
////            location.x = Math.min(location.x, object.getWidth() / 2);
//
//            location.x = Math.max(location.x, 0 - object.getInsets().left);
//            location.y = Math.max(location.y, 0 - object.getInsets().top);
//
//            object.setLocation(location);
//
//            CallOptionsEvaluator callOptionsEvaluator =
//                    new CallOptionsEvaluator(getInspector());
//
//            callOptionsEvaluator.evaluate(oDesc,
//                    object.getObjectRepresentation().getID());
//
//            this.validate();
//
//            result = object;
//        }

        Point location = new Point(loc);

        VisualObject result = addObject(o);

        if (result != null) {
            location.x = Math.max(location.x, 0 - result.getInsets().left);
            location.y = Math.max(location.y, 0 - result.getInsets().top);

            result.setLocation(location);
        }

        return result;
    }

    /**
     * Adds an object to the canvas and to the canvas's object inspector.
     * <p>
     * <b>Note:</b> Please use
     * {@link eu.mihosoft.vrl.reflection.ComponentUtil#addObject(java.lang.Class, eu.mihosoft.vrl.reflection.VisualCanvas, java.awt.Point)}
     * to add an object with source code. </p>
     *
     * @param o the object that is to be added
     * @return the window that displays the object
     */
    public synchronized VisualObject addObject(Object o) {

        if (o == null) {
            throw new IllegalArgumentException(
                    "Argument \"null\" not supported!");
        }

        VisualObject result = null;
        Point location = new Point(0, 0);

        if (getInspector().addObject(o)
                || getInspector().getObjectID(o) != null) {

            ObjectDescription oDesc = getInspector().getObjectDescription(o);

            VisualObject object = new VisualObject(getInspector(), oDesc, this);

            // compute location offset
            // location.x -= object.getWidth() / 2;
            this.getWindows().add(object);

            object.setLocation(location);

            this.doLayout();

            result = object;

            CallOptionsEvaluator callOptionsEvaluator
                    = new CallOptionsEvaluator(getInspector());

            callOptionsEvaluator.evaluate(oDesc,
                    object.getObjectRepresentation().getID());
        }

        return result;
    }

    /**
     * Adds an object to the canvas and to the canvas's object inspector.
     *
     * @param code the groovy code that defines the class
     * @return an instance of the compiled class if the class could be compiled
     * or <code>null</code> otherwise
     */
    public synchronized Object addObjectAsCode(String code, String language) {
        Object result = null;
        AbstractCode aCode = new AbstractCode();
        aCode.setCode(code);
        aCode.setLanguage(language);

        Class<?> clazz = aCode.addToClassPath(this);

        getCodes().add(aCode);

        InstanceCreator creator = new InstanceCreator(this);

        result = creator.newInstance(clazz);

        if (result != null) {
            VisualObject vObj = addObject(result);
            try {
                vObj.addSourceIcon();
            } catch (Exception ex) {
                // we just ignore the exception
            }
        }

        return result;
    }

    /**
     * Adds a class to the canvas and to the canvas's component menu.
     *
     * @param code the groovy code that defines the class
     * @return the compiled class if the class could be compiled or
     * <code>null</code> otherwise
     */
    public synchronized Class<?> addClassAsCode(String code) {
        Object result = null;
        AbstractCode aCode = new AbstractCode();
        aCode.setCode(code);

        Class<?> clazz = aCode.addToClassPath(this);

        getCodes().add(aCode);

        return clazz;
    }

    /**
     * Adds classes to the canvas, compiles them and adds them to the canvas's
     * component menu.
     *
     * @param code the groovy code that defines the class
     * @return the compiled classes
     */
    public synchronized ArrayList<Class<?>> addClassesAsCode(
            String[] codes) {
        return addClassesAsCode(codes, true);
    }

    /**
     * Adds classes to the canvas and to the canvas's component menu.
     *
     * @param code the groovy code that defines the class
     * @param compile defines whether to compile the codes (set this to
     * <code>false</code> when adding codes from the session initializer)
     * @return the compiled classes
     */
    public synchronized ArrayList<Class<?>> addClassesAsCode(
            String[] codes, boolean compile) {

        ArrayList<Class<?>> result = new ArrayList<Class<?>>();
        boolean errors = true;
        int counter = 0;

        ArrayList<String> sortedCodes = new ArrayList<String>();
        sortedCodes.addAll(Arrays.asList(codes));

        Collections.sort(sortedCodes);

        while (errors && counter < 10) {
            errors = false;
            for (String code : sortedCodes) {

                AbstractCode aCode = null;
                Class<?> clazz = null;

                try {
                    aCode = new AbstractCode();
                    aCode.setCode(code);

                    if (compile) {
                        clazz = aCode.addToClassPath(this);
                    }
                } catch (Exception ex) {
                    //
                }

                if (clazz != null || !compile) {

                    // set class name to allow comparison with already added
                    // codes (codes with same class name will be replaced) this object
                    // (because code was not compiled class name is unknown)
                    aCode.setName(VLangUtils.classNameFromCode(aCode));
                    getCodes().add(aCode);
                    result.add(clazz);
//                    System.out.println("OK: ");
                } else {
                    String s = code;//code.split("\n")[0];
//                    System.out.println("COUNTER: " + counter + " , " + s + "\n-------");
                    counter++;
                    errors = true;
                    break;
                }
            }
        }

//        ArrayDeque<String> codesToCompile = new ArrayDeque<String>();
//
//        codesToCompile.addAll(codes);
//
//        int counter = 0;
//
//        while (!codesToCompile.isEmpty()) {
//            String code = codesToCompile.pollFirst();
//
//            AbstractCode aCode = null;
//            Class<?> clazz = null;
//
//            try {
//                aCode = new AbstractCode();
//                aCode.setCode(code);
//
//                clazz = aCode.addToClassPath(this);
//            } catch (Exception ex) {
//            }
//
//            if (clazz != null) {
//                getCodes().add(aCode);
//
//                result.add(clazz);
//            } else {
//                counter++;
//                System.out.println("COUNTER:" + counter);
//                System.out.println(code);
//                codesToCompile.addLast(code);
//
//            }
//        }
        return result;
    }

    /**
     * Adds a class to the canvas and to the canvas's component menu.
     *
     * @param code the groovy code that defines the class
     * @return the compiled class if the class could be compiled or
     * <code>null</code> otherwise
     */
    /**
     * Adds a class to the canvas and to the canvas's component menu.
     *
     * @param clazz the class to add
     */
    public void addClass(Class<?> clazz) {

//        System.out.println("CLS: " + clazz);
//        System.out.println("CLSLOADER: " + clazz.getClassLoader());
//        getPopupMenu().addComponent(clazz);
        getComponentController().addComponent(clazz);
        getClassLoader().addClass(clazz);
    }

    /**
     * Adds classes to the canvas and to the canvas's component menu.
     *
     * @param classes the classes to add
     */
    public void addClasses(Class<?>[] classes) {
        for (Class<?> c : classes) {
//            getPopupMenu().addComponent(c);
            getComponentController().addComponent(c);
            getClassLoader().addClass(c);
        }

    }

    /**
     * Adds an object specified by groovy code to the canvas and to the canvas's
     * object inspector.
     *
     * @param code the groovy code that defines the class
     * @param location the location at which the the object will be placed, the
     * origin's position on the x axis is <code>objectwidth/2</code>
     * @return an instance of the compiled class if the class could be compiled
     * or <code>null</code> otherwise
     */
    public synchronized Object addObjectAsCode(
            String code, String language, Point location) {
        Object result = null;
        AbstractCode aCode = new AbstractCode();
        aCode.setCode(code);
        aCode.setLanguage(language);

        Class<?> clazz = aCode.addToClassPath(this);

        getCodes().add(aCode);

        InstanceCreator creator = new InstanceCreator();

        result = creator.newInstance(clazz);

        if (result != null) {
            VisualObject vObj = addObject(result, location);
            try {
                vObj.addSourceIcon();
            } catch (Exception ex) {
            }
        }

        return result;
    }

    public static VisualCanvas asVisualCanvas(Canvas canvas) {
        if (!(canvas instanceof VisualCanvas)) {
            throw new IllegalArgumentException("Canvas class \""
                    + canvas.getClass() + "\" not supported!\n"
                    + "only \"" + VisualCanvas.class.getName()
                    + "\" and derived classes are supported!");
        }

        return (VisualCanvas) canvas;
    }

    /**
     * Returns the window that displays the object representation of the
     * specified object. In addition to the object it is necessary to specify
     * the id of the requested visualization.
     *
     * @param o object
     * @param visualID visual id
     * @return the window that displays the object representation of the
     * specified object or <code>null</code> if no such window exists
     */
    public CanvasWindow getCanvasWindow(Object o, int visualID) {
        return getInspector().getCanvasWindow(o, visualID);
    }

    /**
     * Returns an object representation.
     *
     * @param o the object of the object representation that is to be returned
     * @return the object representation or <code>null</code> if such an object
     * representation could not be found
     */
    public DefaultObjectRepresentation getObjectRepresentationByReference(
            Object o, int visualID) {

        CanvasWindow vObj = getCanvasWindow(o, visualID);

        if (vObj instanceof VisualObject) {
            return ((VisualObject) vObj).getObjectRepresentation();
        }

        return null;
    }

    /**
     * Returns a method representation by method name and parameter types.
     *
     * @param o the object the method belongs to
     * @param methodName the method name
     * @param paramTypes the parameter types of the method
     * @return the method representation or <code>null</code> if such a method
     * representation could not be found
     */
    public DefaultMethodRepresentation getMethodRepresentation(
            Object o, int visualID,
            String methodName, Class<?>[] paramTypes) {

        DefaultObjectRepresentation oRep
                = getObjectRepresentationByReference(o, visualID);

        DefaultMethodRepresentation thisMethod = null;

        for (DefaultMethodRepresentation mRep : oRep.getMethods()) {

            boolean paramTypesAreEqual
                    = mRep.getDescription().getParameterTypes().length
                    == paramTypes.length;

            if (paramTypesAreEqual) {

                for (int i = 0; i < paramTypes.length; i++) {
                    Class<?> class1 = paramTypes[i];
                    Class<?> class2
                            = mRep.getDescription().getParameterTypes()[i];

                    if (!class1.equals(class2)) {
                        paramTypesAreEqual = false;
                        break;
                    }
                }
            }

            if (mRep.getDescription().getMethodName().equals(methodName)
                    && paramTypesAreEqual) {
                thisMethod = mRep;
                break;
            }
        }

        return thisMethod;
    }

    /**
     * Removes all objects and connections from canvas but not from the object
     * inspector. Components, Codes etc. won't be removed.
     */
    @Override
    public void clearCanvas() {
        VisualObjects objects = (VisualObjects) getWindows();
        objects.removeAllOnlyFromCanvas();
//        objects.resetID();
    }

    /**
     * Removes all objects, connections, codes and reference tasks from canvas
     * and from the object inspector and resets everything else.
     */
    public void resetCanvas() {
        getWindows().removeAllWithoutEffect();
        for (Class c : getClassLoader().getClasses().values()) {
//            getPopupMenu().removeComponent(c);
            getComponentController().removeComponent(c);
        }
        getCodes().clear();
        setClassLoader(new VClassLoader());
        getDataConnections().clear();
//        setTypeFactory(new DefaultTypeRepresentationFactory(this));
        referenceTasks.clear();
        callOptionTasks.clear();
        getWindowGroups().clear();

        getStyleMenuController().clear(null, false);
        getWindowGroupController().clear();

    }

    /**
     * Removes all objects and adds them again afterwards.
     */
    public void showObjects() {

        clearCanvas();

        for (ObjectDescription oDesc : getInspector().getObjectDescriptions()) {
            this.getWindows().add(new VisualObject(getInspector(), oDesc, this));
        }
    }

    /**
     * Returns the object inspector that is used by this canvas.
     *
     * @return the object inspector that is used by this canvas
     */
    public VisualObjectInspector getInspector() {
        return inspector;
    }

    /**
     * Defines the object inspector that is to be used by this canvas.
     *
     * @param inspector the object inspector that is to be used by this canvas
     */
    public void setInspector(VisualObjectInspector inspector) {
        this.inspector = inspector;
    }

    /**
     * Returns the type factory that is used by this canvas.
     *
     * @return the type factory that is used by this canvas
     */
    public TypeRepresentationFactory getTypeFactory() {
        return typeFactory;
    }

    /**
     * Defines the type factory that is to be used by this canvas.
     *
     * @param typeFactory the type factory that is to be used by this canvas
     */
    public void setTypeFactory(TypeRepresentationFactory typeFactory) {
        this.typeFactory = typeFactory;
    }

    /**
     * Returns the object tree that is connected with the canvas.
     *
     * @return the object tree that is connected with the canvas
     */
    public ObjectTree getObjectTree() {
        return objectTree;
    }

    /**
     * Defines the object tree that is to be connected with this canvas.
     *
     * @param objectTree the object tree that is to be connected with this
     * canvas
     */
    public void setObjectTree(ObjectTree objectTree) {
        this.objectTree = objectTree;
    }

    /**
     * Defines the abstract codes of this canvas.
     *
     * @param codes the codes to set
     */
    private void setCodes(AbstractCodes codes) {
        this.codes = codes;
    }

//
//
//    /**
//     * Loads a canvas session.
//     * @param d the xml decoder that is to be used for deserialization
//     */
//    public void loadSession(XMLDecoder d) {
//        System.out.println(">> Loading File ");
//        // while loading we need to disable effects due to time dependent
//        // reference problems
//        boolean effectState = isDisableEffects();
//        setLoadingSession(true);
//        setDisableEffects(true);
//
//        FileVersionInfo info = null;
//
//        try {
//            info = (FileVersionInfo) d.readObject();
//        } catch (Exception ex) {
//            getMessageBox().addMessage("Can't load File:",
//                    "File format is not supported "
//                    + "(file version info not found)!", MessageType.ERROR);
//        }
//
//        boolean fileHasSessionInitializer = false;
//
//        try {
//            // start loading if version info is ok
//            if (info != null && getFileVersionInfo().verify(info, this)) {
//                System.out.println(">>> Loading Codes");
//
//                Object o = null;
//
//                // try to load the session initializer
//                try {
//                    o = d.readObject();
//
//                    // this is for compatibility reasons for files without
//                    // session initializer
//                    // (this applies to versions before 15.04.2010 )
//                    if (o instanceof SessionInitializer) {
//                        setSessionInitializer((SessionInitializer) o);
//
//                        fileHasSessionInitializer = true;
//
//                        // try to set call the preInit() method of the initializer
//                        try {
//                            getSessionInitializer().preInit(this);
//                        } catch (Exception ex) {
//                            String message = ">> init method returns errors:<br><br>";
//
//                            message += ex.toString();
//
//                            if (ex.getCause() != null) {
//                                message += "<br>>> " + ex.getCause().toString();
//                            }
//
//                            getMessageBox().addMessage(
//                                    "Error while calling session initializer:", ">> "
//                                    + message, MessageType.ERROR);
//                        }
//
//                        o = null;
//                    } else if (o == null) {
//                        // the object we just loaded is not a plugin
//                        // and because it is null we know that we failed
//                        // to load the session initializer (presumably because)
//                        // we don't know its class
//
//                        String message = ">> it is likely that this file has been"
//                                + " generated with another VRL based program.";
//
//                        getMessageBox().addMessage(
//                                "Error while loading session initializer:",
//                                message, MessageType.ERROR);
//                    }
//
//                } catch (Exception ex) {
//                    String message = ">> it is likely that this file has been"
//                            + " generated with another VRL based program."
//                            + " See the following error message for details:<br><br>";
//
//                    message += ex.toString();
//
//                    if (ex.getCause() != null) {
//                        message += "<br>>> " + ex.getCause().toString();
//                    }
//
//                    getMessageBox().addMessage(
//                            "Error while loading session initializer:",
//                            message, MessageType.ERROR);
//
//                    o = d.readObject();
//                }
//
//                // if session initializer has been successfully loaded
//                // we can load plugins
//                if (o == null) {
//                    o = d.readObject();
//                }
//
//                // load plugins
//                if (o instanceof PluginGroup) {
////                    pluginController.
//                    if (isLoadPluginsAsGlobal()) {
//                        globalPluginController.unregisterPlugins();
//                        globalPluginController.setPlugins((PluginGroup) o);
//                        globalPluginController.registerPlugins();
//                    } else {
//                        pluginController.unregisterPlugins();
//                        pluginController.setPlugins((PluginGroup) o);
//                        pluginController.registerPlugins();
//                    }
//
//                    // read codes from new object
//                    setCodes((AbstractCodes) d.readObject());
//
//                } else {
//                    // read codes instead of plugins
//                    setCodes((AbstractCodes) o);
//                }
//
//                if (getSessionInitializer() != null) {
//                    getSessionInitializer().codesLoaded(this);
//                }
//
//                System.out.println(">>> Compiling Codes");
//
////        getClassLoader().loadClasses(d);
//
//                if (!isCodesAlreadyCompiled()) {
//                    getCodes().addToClassPath(this);
//                }
//
//                Thread.currentThread().setContextClassLoader(getClassLoader());
//                System.out.println(">>> Classloader defined");
//
//                revalidate();
////        getTypeFactory().loadTypes(d);
//
//                setStyle((CanvasStyle) d.readObject());
//                VisualObjects objects = (VisualObjects) getWindows();
//
//                System.out.println(">>> Loading objects");
//
//                objects.load(d);
//
//                revalidate();
//
//                Connections connections = getDataConnections();
//                connections.load(d);
//
//                revalidate();
//
//                // if effects where enabled before loading they will be
//                // enabled again
//                setLoadingSession(false);
//                setDisableEffects(effectState);
//
//                // resolve ui component references (UIWindow)
//                for (ReferenceTask task : referenceTasks) {
//                    task.resolve();
//                }
//
//                // load window groups
//                Object groupObj = null;
//                try {
//                    groupObj = d.readObject();
//                } catch (Exception e) {
//                }
//                if (groupObj != null) {
//                    setWindowGroups((WindowGroups) groupObj);
//                    getWindowGroups().setMainCanvas(this);
//                }
//
//                for (CallOptionEvaluationTask e :
//                        getCallOptionEvaluationTasks()) {
//                    e.evaluate();
////                    System.out.println("EVALUATE!");
//                }
//
//                if (fileHasSessionInitializer) {
//                    // try to call the postInit() method of the initializer
//                    try {
//                        getSessionInitializer().postInit(this);
//                    } catch (Exception ex) {
//                        String message = ">> init method returns errors:<br><br>";
//
//                        message += ex.toString();
//
//                        if (ex.getCause() != null) {
//                            message += "<br>>> " + ex.getCause().toString();
//                        }
//
//                        getMessageBox().addMessage(
//                                "Error while calling session initializer:", ">> "
//                                + message, MessageType.ERROR);
//                    }
//                }
//            }
//
//            refresh();
//
//        } catch (Exception ex) {
//
//            Logger.getLogger(VisualCanvas.class.getName()).
//                    log(Level.SEVERE, null, ex);
//
//            getMessageBox().addUniqueMessage("Can't load file:",
//                    ex.toString(), null, MessageType.ERROR);
//
//            if (ex.getCause() != null) {
//                getMessageBox().addUniqueMessage("Error Message:",
//                        ex.getCause().toString(), null, MessageType.ERROR);
//            }
//
//
//            // if effects where enabled before loading they will be
//            // enabled again
//            setLoadingSession(false);
//            setDisableEffects(effectState);
//        }
//
//        setPresentationMode(!getWindowGroups().isEmpty());
//
//        revalidate();
//    }
//
//    /**
//     * Loads a canvas session from file.
//     * @param fileName the name of the file that is to be loaded
//     *                 (including path)
//     * @throws java.io.FileNotFoundException
//     */
//    public void loadSession(String fileName) throws FileNotFoundException {
//        Thread.currentThread().setContextClassLoader(getClassLoader());
//        sessionFileName = fileName;
//        loadSession(
//                new BufferedInputStream(
//                new FileInputStream(fileName)));
//    }
    /**
     * Returns all reference tasks of this canvas.
     *
     * @return all reference tasks of this canvas
     */
    public ArrayList<ReferenceTask> getReferenceTasks() {
        return referenceTasks;
    }

    /**
     * Returns all call option evaluation tasks of this canvas.
     *
     * @return all call option evaluation tasks of this canvas
     */
    public ArrayList<CallOptionEvaluationTask> getCallOptionEvaluationTasks() {
        return callOptionTasks;
    }

    /**
     * Returns the abstract codes of this canvas.
     *
     * @return the codes of this canvas
     */
    public AbstractCodes getCodes() {
        return codes;
    }

    /**
     * Returns the class loader of this canvas.
     *
     * @return the class loader
     */
    public VClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Defines the class loader of this canvas.
     *
     * @param classLoader the class loader to set
     */
    public void setClassLoader(VClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Returns the popup menu of this canvas.
     *
     * @return the popup menu
     */
    public VCanvasPopupMenu getPopupMenu() {
        return popupMenu;
    }

    /**
     * Defines the popup menu of this canvas.
     *
     * @param popupMenu the popup menue to set
     */
    private void setVCanvasPopupMenu(VCanvasPopupMenu popupMenu) {
        if (this.popupMenu != null) {
            removeMouseListener(this.popupMenu.getMouseListener());
        }

        this.popupMenu = popupMenu;
        addMouseListener(
                this.popupMenu.getMouseListener());
    }

    @Override
    public void dispose() {
//        System.out.println("VisualCanvas.dispose()");

        if (getSessionInitializer() != null) {
            try {
                getSessionInitializer().dispose(this);
            } catch (Exception ex) {
                System.err.println(">> Warning: error in canvas finalization:");
                String message = ex.getMessage();
                if (ex.getCause() != null) {
                    message += "caused by: \n >> " + ex.getCause();
                }
                System.err.print(">> " + message);

            }
        }

        getPopupMenu().removeAll();

        getTypeFactory().dispose();

        super.dispose();

//        getPluginController().dispose();
//        getGlobalPluginController().dispose();
    }

//    /**
//     * Returns the plugin manager.
//     * @return the pluginManager
//     */
//    public PluginController getPluginController() {
//        return pluginController;
//    }
//
//    /**
//     * @return the globalPluginController
//     */
//    public PluginController getGlobalPluginController() {
//        return globalPluginController;
//    }
//    /**
//     * @return the loadPluginsAsGlobal
//     */
//    public boolean isLoadPluginsAsGlobal() {
//        return loadPluginsAsGlobal;
//    }
//
//    /**
//     * Defines whether plugins are to be loaded as global plugins
//     * @param loadPluginsAsGlobal the state to set
//     */
//    public void setLoadPluginsAsGlobal(boolean loadPluginsAsGlobal) {
//        this.loadPluginsAsGlobal = loadPluginsAsGlobal;
//    }
    /**
     * Returns the invokation delay. Use this to enable slow method invocation
     * for debugging purposes. This can be used to demonstrate method call order
     * (see {@link eu.mihosoft.vrl.effects.SpinningWheelEffect}).
     *
     * @return the invokation delay
     * @see #setInvokeWaitEffect(boolean)
     */
    public long getInvocationDelay() {
        return invocationDelay;
    }

    /**
     * Defines the invocation delay. Use this to enable slow method invocation
     * for debugging purposes. This can be used to demonstrate method call order
     * (see {@link eu.mihosoft.vrl.effects.SpinningWheelEffect}).
     *
     * @param invocationDelay the invokationDelay to set (in milliseconds)
     * @see #setInvokeWaitEffect(boolean)
     */
    public void setInvocationDelay(long invocationDelay) {
        this.invocationDelay = invocationDelay;
    }

    /**
     * Indicates whether wait for invocation effect is enabled.
     *
     * @return <true> if wait for invocation effect is enabled;
     * <code>false></code> otherwise
     */
    public boolean isInvokeWaitEffect() {
        return invokeWaitEffect;
    }

    /**
     * Defines whether to enable wait for invocation effect.
     *
     * @param invokeWaitEffect the state to set
     */
    public void enableInvokeWaitEffect(boolean invokeWaitEffect) {
        this.invokeWaitEffect = invokeWaitEffect;
    }

    /**
     * @return the objectRepresentationFactory
     */
    public DefaultObjectRepresentationFactory getObjectRepresentationFactory() {
        return objectRepresentationFactory;
    }

    /**
     * @param objectRepresentationFactory the objectRepresentationFactory to set
     */
    public void setObjectRepresentationFactory(DefaultObjectRepresentationFactory objectRepresentationFactory) {
        this.objectRepresentationFactory = objectRepresentationFactory;
    }

    /**
     * @return the codesAlreadyCompiled
     */
    public boolean isCodesAlreadyCompiled() {
        return codesAlreadyCompiled;
    }

    /**
     * @param codesAlreadyCompiled the codesAlreadyCompiled to set
     */
    public void setCodesAlreadyCompiled(boolean codesAlreadyCompiled) {
        this.codesAlreadyCompiled = codesAlreadyCompiled;
    }

    /**
     * @return the session
     */
    public AbstractSession getSession() {
        return session;
    }

    /**
     * @param session the session to set
     */
    public void setSession(AbstractSession session) {
        this.session = session;
    }

    /**
     * @return the compilerProvider
     */
    public CompilerProvider getCompilerProvider() {
        return compilerProvider;
    }

    /**
     * @param compilerProvider the compilerProvider to set
     */
    public void setCompilerProvider(CompilerProvider compilerProvider) {
        this.compilerProvider = compilerProvider;
    }

    /**
     * @return the componentTree
     */
    public ComponentTree getComponentTree() {
        return componentTree;
    }

    /**
     * @param componentTree the componentTree to set
     */
    public void setComponentTree(ComponentTree componentTree) {
        this.componentTree = componentTree;
    }

    /**
     * @return the componentTreeModel
     */
    public VFilteredTreeModel getComponentTreeModel() {
        return componentTreeModel;
    }

    /**
     * @param componentTreeModel the componentTreeModel to set
     */
    public void setComponentTreeModel(VFilteredTreeModel componentTreeModel) {
        this.componentTreeModel = componentTreeModel;
    }

    /**
     * @return the projectController
     */
    public VProjectController getProjectController() {
        return projectController;
    }

    /**
     * @param projectController the projectController to set
     */
    public void setProjectController(VProjectController projectController) {
        this.projectController = projectController;

        popupMenu.init();
    }

    /**
     * @return the componentController
     */
    public ComponentController getComponentController() {
        return componentController;
    }

    /**
     * Fires a workflow event. All objects on the canvas that implement the
     * method <code>public void handleVRLWorkflowEvent(WorkflowEvent event)</b> will
     * be notified.
     *
     * @param eventType event type
     */
    public void fireWorkflowEvent(String eventType) {
        fireWorkflowEvent(new WorkflowEventImpl(eventType));
    }

    /**
     * Fires a workflow event. All objects on the canvas that implement the
     * method <code>public void handleVRLWorkflowEvent(WorkflowEvent event)</b> will
     * be notified.
     *
     * @param event event
     */
    public void fireWorkflowEvent(WorkflowEvent event) {
        for (Object o : getInspector().getObjects()) {

            MethodDescription mDesc = getInspector().getMethodDescription(o,
                    "handleVRLWorkflowEvent", WorkflowEvent.class);

            if (mDesc != null) {
                try {
                    mDesc.setParameters(new Object[]{event});
                    getInspector().invoke(mDesc);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(VisualCanvas.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
