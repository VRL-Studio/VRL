/* 
 * ObjectInspector.java
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

import eu.mihosoft.vrl.annotation.*;
import eu.mihosoft.vrl.lang.InstanceCreator;
import eu.mihosoft.vrl.system.VParamUtil;
import eu.mihosoft.vrl.system.VRL;
import eu.mihosoft.vrl.visual.IDArrayList;
import eu.mihosoft.vrl.visual.IDObject;
import groovy.lang.GroovyObject;
import java.beans.ExceptionListener;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.spi.DirStateFactory.Result;

/**
 * The purpose of the object inspector is to analyse objects and to generate an
 * object description. The inspector keeps a reference to each object and makes
 * it possible to work with the object's properties throught it's methods, i.e.
 * through method descriptions it is possible to invoke methods of the object.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ObjectInspector {

    /**
     * the inspector's object descriptions
     */
    private Set<ObjectDescription> objectDescriptions;
    /**
     * the objects that are associated with the inspector
     */
    private IDArrayList<ObjectEntry> objects;

    /**
     * Constructor.
     *
     */
    public ObjectInspector() {
        objects = new IDArrayList<ObjectEntry>();
        objectDescriptions = new HashSet<ObjectDescription>();
    }

    /**
     * Generates a new error message based on the excpetion message.
     *
     * @param message the rror message
     */
    public void generateErrorMessage(String message, MethodDescription mDesc) {

        ObjectDescription oDesc = getObjectDescription(getObject(mDesc.getObjectID()));
        String methodName = oDesc.getName() + "." + mDesc.getMethodName() + "()";

        System.err.println("Method \""
                + methodName + "\" can't be invoked:" + message);
    }

    /**
     * Generates a new error message based on the excpetion message.
     *
     * @param message the rror message
     */
    public void generateErrorMessage(MethodDescription mDesc,
            Throwable ex) {

        ObjectDescription oDesc = getObjectDescription(getObject(mDesc.getObjectID()));
        String methodName = oDesc.getName() + "." + mDesc.getMethodName() + "()";

        System.err.println("Method \""
                + methodName + "\" can't be invoked:" + ex.toString());
    }

    /**
     * Invokes the method specified by a method description.
     *
     * @param methodDescription the method description of the method that is to
     * be invoked.
     */
    protected void invoke(MethodDescription methodDescription)
            throws InvocationTargetException {

        VParamUtil.throwIfNull(methodDescription);

        Object o = getObject(methodDescription.getObjectID());

        ObjectDescription oDesc = getObjectDescription(o);

        if (o == null) {
            throw new IllegalStateException("Object must not be null!");
        }

        if (methodDescription.getMethodType() == MethodType.REFERENCE) {

            Object[] parameters = methodDescription.getParameters();

            // error handling
            if (parameters.length > 1) {
                throw new IllegalArgumentException("More than one parameter "
                        + "for reference methods is not allowed!");
            }

            if (parameters.length > 0 && parameters[0] != null) {
                methodDescription.setReturnValue(parameters[0]);
            } else {
                methodDescription.setReturnValue(o);
            }

        } else if (o instanceof ProxyObject) {
            ProxyObject proxy = (ProxyObject) o;
            methodDescription.setReturnValue(proxy.invoke(methodDescription));
            if (!methodDescription.getReturnType().
                    equals(methodDescription.getReturnValue().getClass())) {
                System.err.println(
                        ">> ProxyObject: wrong type of return value!");
                generateErrorMessage(">> ProxyObject:"
                        + " wrong type of return value!",
                        methodDescription);
            }
        } else if (methodDescription.getMethodType() == MethodType.DEFAULT
                || methodDescription.getMethodType() == MethodType.CUSTOM_REFERENCE) {
            Method m = null;

            try {
                m = o.getClass().getMethod(
                        methodDescription.getMethodName(),
                        methodDescription.getParameterTypes());

                // access this method even if language visibility forbids
                // invocation of this method
                m.setAccessible(true);

                methodDescription.setReturnValue(
                        m.invoke(o, methodDescription.getParameters()));

            } catch (NoSuchMethodException ex) {
                Logger.getLogger(ObjectInspector.class.getName()).
                        log(Level.SEVERE, null, ex);
                generateErrorMessage(methodDescription.getMethodName()
                        + "(): " + ex.toString(),
                        methodDescription);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ObjectInspector.class.getName()).
                        log(Level.SEVERE, null, ex);
                generateErrorMessage(
                        methodDescription.getMethodName()
                        + "(): " + ex.toString(),
                        methodDescription);
            } catch (IllegalArgumentException ex) {
                System.err.println(">> ObjectInspector:"
                        + " invoked method with wrong arguments!");

                String yourParamStr = "";
                String expectedParamStr = "";

                if (methodDescription.getParameters() != null) {

                    for (int i = 0;
                            i < methodDescription.getParameters().length; i++) {
                        Object parameter = methodDescription.getParameters()[i];
                        if (parameter != null) {
                            yourParamStr
                                    += parameter.getClass().getName() + " ";
                        } else {
                            yourParamStr += "null ";
                        }
                    }
                } else {
                    yourParamStr = "no parameters given!";
                }

                for (int i = 0; i < m.getParameterTypes().length; i++) {
                    expectedParamStr
                            += m.getParameterTypes()[i].getName() + " ";
                }

                System.err.println(
                        "   your parameters:     " + yourParamStr);
                System.err.println(
                        "   expected parameters: " + expectedParamStr);

                generateErrorMessage(methodDescription.getMethodName()
                        + "(): method invoked with wrong arguments!",
                        methodDescription);

            } catch (InvocationTargetException ex) {
                Logger.getLogger(ObjectInspector.class.getName()).
                        log(Level.SEVERE, null, ex);
                generateErrorMessage(methodDescription, ex.getCause());
                throw ex;

            } catch (SecurityException ex) {
                Logger.getLogger(ObjectInspector.class.getName()).
                        log(Level.SEVERE, null, ex);
                generateErrorMessage(methodDescription.getMethodName()
                        + "(): "
                        + ex.toString(),
                        methodDescription);
            }
        }// end else if (o instanceof ProxyObject)
    }

    /**
     * Returns object with specific ID.
     *
     * @param ID Id of the object that is to be returned.
     * @return the object with specified ID
     */
    public Object getObject(int ID) {
        ObjectEntry obj = objects.getById(ID);
        Object result = null;
        if (obj != null) {
            result = obj.getObject();
        }
        return result;
    }

    /**
     * Returns a collection containing all instances controlled by this
     * inspector.
     *
     * @return a collection containing all instances controlled by this
     * inspector
     */
    public Collection<Object> getObjects() {
        ArrayList<Object> result = new ArrayList<Object>();

        for (ObjectEntry o : objects) {
            result.add(o.getObject());
        }

        return result;
    }

    /**
     * Returns a collection containing all instances of a given class object.
     *
     * @param name the name of the class object
     * @return a collection containing all instances of a given class object
     */
    public Collection<Object> getObjectsByClassName(String name) {
        ArrayList<Object> result = new ArrayList<Object>();

        for (ObjectEntry o : objects) {
            if (o.getObject() != null
                    && o.getObject().getClass().getName().equals(name)) {
                result.add(o.getObject());
//                System.out.println("TRUE: O1: " + o.getObject()
//                        + ", " + o.getObject().getClass().getName()
//                        + " O2: " + name);
            } else {
//                System.out.println("FALSE: O1: " + o.getObject()
//                        + ", " + o.getObject().getClass().getName()
//                        + " O2: " + name);
            }
        }

        return result;
    }

    /**
     * Returns the number of instances of the specified class.
     *
     * @param className class name
     * @return the number of instances of the specified class
     */
    public int numberOfInstances(String className) {
        return getObjectsByClassName(className).size();
    }

    /**
     * Removes object with specific ID from the inspector.
     *
     * @param ID the ID of the object that is to be removed
     */
    public void removeObject(int ID) {

        // remove object entry
        ObjectEntry removedElement1 = null;
        for (ObjectEntry oEntry : objects) {
            if (oEntry.getID() == ID) {
                removedElement1 = oEntry;
                break;
            }
        }
        objects.remove(removedElement1);

        // remove object description
        ObjectDescription removedElement2 = null;
        for (ObjectDescription oDesc : objectDescriptions) {
            if (oDesc.getID() == ID) {
                removedElement2 = oDesc;
                break;
            }
        }
        objectDescriptions.remove(removedElement2);
    }

    /**
     * Adds an object to the inspector.
     *
     * @param o the object that is to be added
     * @param objID the ID of the object (only used when loading from file or if
     * replacing instance, because then we need to restore the ID) if
     * <code>null</code> the id will be generated (usual case)
     * @return <code>true</code>, if successfully added, i.e. is not already
     * atached to the inspector; <code>false</code> otherwise
     */
    public boolean addObject(Object o, Integer objID) {

        System.out.println(">> Add Object: " + o + ", id: " + objID);

        boolean alreadyAdded = false;

        // search for an already existing reference to object o
        for (ObjectEntry oE : objects) {
            if (oE.getObject() == o) {
                System.out.println(" --> Object already added!");
                alreadyAdded = true;
                break;
            }
        }

        // Object annotations
        ObjectInfo objectInfo = null;

        Annotation[] objectAnnotations = o.getClass().getAnnotations();

        for (Annotation a : objectAnnotations) {
            if (a.annotationType().equals(ObjectInfo.class)) {
                objectInfo = (ObjectInfo) a;
                break;
            }
        }

        if (objectInfo != null
                && objectInfo.instances()
                <= numberOfInstances(o.getClass().getName())) {
            System.err.println(">> Cannot add Object: only "
                    + objectInfo.instances() + " instances allowed!");
            return false;
        }

        if (!alreadyAdded) {
            ObjectDescription oDesc = generateObjectDescription(o, objID, true);
            if (oDesc == null) {
                return false;
            }
            getObjectDescriptions().add(oDesc);
        } // end if(!alredyAdded)

        return !alreadyAdded;
    }

    public ObjectDescription generateObjectDescription(Object o, Integer objID, boolean addObject) {

        ObjectDescription result = null;

        // Object annotations
        ObjectInfo objectInfo = null;

        Annotation[] objectAnnotations = o.getClass().getAnnotations();

        for (Annotation a : objectAnnotations) {
            if (a.annotationType().equals(ObjectInfo.class)) {
                objectInfo = (ObjectInfo) a;
                break;
            }
        }

        if (addObject) {
            ObjectEntry oEntry = new ObjectEntry(o);
            if (objID != null) {
                objects.addWithID(oEntry, objID);
            } else {
                objects.add(oEntry);
                objID = oEntry.getID();
            }
        }

        boolean hasCustomReferenceMethod = false;

        // we need special treatment for proxy objects
        if (o instanceof ProxyObject) {
            ProxyObject proxy = (ProxyObject) o;

            result
                    = proxy.getObjectDescription();

            result.setName(o.getClass().getName());

            result.setID(objID);

            // update objID of methods
            for (MethodDescription mDesc : result.getMethods()) {
                mDesc.setObjectID(objID);
            }

        } else {
            result = new ObjectDescription();
            result.setInfo(objectInfo);

            result.setName(o.getClass().getName());

            result.setID(objID);

            Class<?> c = o.getClass();

            Method[] theMethods = null;

            if (objectInfo != null && objectInfo.showInheritedMethods()) {
                theMethods = c.getMethods();
            } else {
                ArrayList<Method> methods = new ArrayList<Method>();

                // methods declared by class c
                for (Method m : c.getDeclaredMethods()) {
                    methods.add(m);
                }

                // methods declared in superclasses of c
                for (Method m : c.getMethods()) {
                    MethodInfo info = m.getAnnotation(MethodInfo.class);
                    // if m is marked as inheritGUI this method will
                    // be visualized even if it is a method declared
                    // in a superclass of c
                    if (info != null && info.inheritGUI()) {
                        if (!methods.contains(m)) {
                            methods.add(m);
                        }
                    }
                }

                Method[] tmpArray = new Method[methods.size()];

                theMethods = methods.toArray(tmpArray);
            }

            for (int i = 0; i < theMethods.length; i++) {

                // filter groovy object specific methods
                String method = theMethods[i].getName();
                boolean isGroovyObject = o instanceof GroovyObject;
                boolean isGroovyMethod = method.equals("setProperty")
                        || method.equals("getProperty")
                        || method.equals("invokeMethod")
                        || method.equals("getMetaClass")
                        || method.equals("setMetaClass");// ||
//                            // the following methods are only present
//                            // for Groovy >= 1.6
//                            method.equals("super$1$wait") ||
//                            method.equals("super$1$toString") ||
//                            method.equals("super$1$notify") ||
//                            method.equals("super$1$notifyAll") ||
//                            method.equals("super$1$getClass") ||
//                            method.equals("super$1$equals") ||
//                            method.equals("super$1$clone") ||
//                            method.equals("super$1$hashCode") ||
//                            method.equals("super$1$finalize");

                // For Groovy >= 1.6 private methods have $ sign in their
                // name, e.g.,
                //   private doSomething()
                // will be changed to
                //   this$2$doSomething()
                // which is unfortunately accessible and thus will be
                // visualized by vrl.
                // To prevent groovys strange behavior
                // methods with $ sign are ignored and treated as private.
                boolean isPrivateGroovyMethod = method.contains("$");

                // TODO improve that code!
                if ((isGroovyObject && isGroovyMethod)
                        || isPrivateGroovyMethod) {
                    continue;
                }

                Class[] parameterTypes = theMethods[i].getParameterTypes();

                Annotation[][] allParameterAnnotations
                        = theMethods[i].getParameterAnnotations();

                ArrayList<String> paramNames = new ArrayList<String>();

                ArrayList<ParamInfo> paramAnnotations
                        = new ArrayList<ParamInfo>();
                ArrayList<ParamGroupInfo> paramGroupAnnotations
                        = new ArrayList<ParamGroupInfo>();

                // retrieving annotation information for each parameter
                for (int j = 0; j < allParameterAnnotations.length; j++) {

                    Annotation[] annotations = allParameterAnnotations[j];

                    // add name element and set it to null
                    // because we don't know if parameter j is annotated
                    paramNames.add(null);
                    paramAnnotations.add(null);
                    paramGroupAnnotations.add(null);

                    // check all annotations of parameter j
                    // if we have a ParamInfo annotation retrieve data
                    // and store it as element of paramName
                    for (Annotation a : annotations) {
                        if (a.annotationType().equals(ParamInfo.class)) {
                            ParamInfo n = (ParamInfo) a;
                            if (!n.name().equals("")) {
                                paramNames.set(j, n.name());
                            }
                            paramAnnotations.set(j, n);
                        } else {
                            if (a.annotationType().equals(ParamGroupInfo.class)) {
                                ParamGroupInfo n = (ParamGroupInfo) a;
                                paramGroupAnnotations.set(j, n);
                            }
                        }
                    } // end for a

                } // end for j

                // convert list to array
                String[] parameterNames
                        = paramNames.toArray(new String[paramNames.size()]);

                ParamInfo[] parameterAnnotations
                        = paramAnnotations.toArray(
                                new ParamInfo[paramAnnotations.size()]);

                ParamGroupInfo[] parameterGroupAnnotations
                        = paramGroupAnnotations.toArray(
                                new ParamGroupInfo[paramGroupAnnotations.size()]);

                Class returnType = theMethods[i].getReturnType();

                String methodString = theMethods[i].getName();
                String methodTitle = "";
                String returnValueName = null;

                int modifiers = theMethods[i].getModifiers();

                String modifierString = Modifier.toString(modifiers);

                // Method Annotations
                Annotation[] annotations = theMethods[i].getAnnotations();
                MethodInfo methodInfo = null;

                OutputInfo outputInfo = null;

                boolean interactive = true;
                boolean hide = false;

                for (Annotation a : annotations) {
                    if (a.annotationType().equals(MethodInfo.class)) {
                        MethodInfo n = (MethodInfo) a;

                        methodTitle = n.name();
                        interactive = n.interactive();
                        hide = n.hide();
                        returnValueName = n.valueName();

                        methodInfo = n;
                    }

                    if (a.annotationType().equals(OutputInfo.class)) {
                        outputInfo = (OutputInfo) a;
                    }
                }

                if (theMethods[i].getAnnotation(ReferenceMethodInfo.class) != null) {
                    hasCustomReferenceMethod = true;
                    MethodDescription customReferenceMethod
                            = new MethodDescription(objID,
                                    0,
                                    methodString, methodTitle, null, parameterTypes,
                                    parameterNames, parameterAnnotations,
                                    parameterGroupAnnotations,
                                    returnType, returnValueName, interactive,
                                    methodInfo, outputInfo);

                    if (customReferenceMethod.getParameterTypes() == null
                            || customReferenceMethod.getParameterTypes().length != 1) {
                        throw new IllegalArgumentException(" Cannot to use "
                                + "\"" + methodString + "\" as reference method"
                                + " because the number of parameters does not"
                                + " match. Exactly one parameter must be"
                                + " provided.");
                    }

                    if (customReferenceMethod.getReturnType() == void.class
                            || customReferenceMethod.getReturnType().isPrimitive()) {
                        throw new IllegalArgumentException(" Cannot to use "
                                + "\"" + methodString + "\" as reference method"
                                + " because it does not return an object."
                                + " Returning primitives or void is not"
                                + " allowed.");
                    }

                    customReferenceMethod.setMethodType(
                            MethodType.CUSTOM_REFERENCE);

                    result.addMethod(customReferenceMethod);
                }// 
                //TODO: at the moment method id is always 0 and will only be
                //      set from corresponding method representations
                //
                //      is it necessary to change that?
                else if (modifierString.contains("public")
                        && (methodInfo == null || !methodInfo.ignore())) {
                    result.addMethod(new MethodDescription(objID,
                            0,
                            methodString, methodTitle, null, parameterTypes,
                            parameterNames, parameterAnnotations,
                            parameterGroupAnnotations,
                            returnType, returnValueName, interactive,
                            methodInfo, outputInfo));
                }
            } // end for i

            // Object name
            if (objectInfo != null && !objectInfo.name().equals("")) {
                result.setName(objectInfo.name());
            }

        } // end else if (o instanceof ProxyObject)

        if (!hasCustomReferenceMethod) {
            result.addMethod(
                    MethodDescription.createReferenceMethod(objID, o.getClass()));
        }

        return result;
    }

    /**
     * Adds an object to the inspector.
     *
     * @param o the object that is to be added
     * @return <code>true</code>, if successfully added, i.e. is not already
     * atached to the inspector; <code>false</code> otherwise
     */
    public boolean addObject(Object o) {
        return addObject(o, null);
    }

    /**
     * Adds an object to this inspector.
     *
     * @param o the object that is to be added
     * @param objID object id of the object that shall be replaced
     */
    public synchronized void replaceObject(Object newObj, int objID)
            throws InterfaceChangedException {

        if (newObj == null) {
            throw new IllegalArgumentException(
                    "Argument \"null\" not supported!");
        }

        //objects.getById(objID).setObject(newObj);
        Collection<Object> instances
                = getObjectsByClassName(newObj.getClass().
                        getName());

        if (instances.size() <= objID) {
            addObject(newObj);
        } else {

            Object oldObj = getObject(objID);

            ObjectDescription oDescOld = getObjectDescription(oldObj);
            ObjectDescription oDescNew = generateObjectDescription(
                    newObj, objID, false);

            if (oDescOld.getSignature().equals(oDescNew.getSignature())) {

                removeObject(objID);
                addObject(newObj, objID);
                objectDescriptions.add(oDescNew);
            } else {
                throw new InterfaceChangedException(
                        "Cannot replace object because the interface of the new"
                        + " class and the old class are not equal!");
            }
        }
    }

    /**
     * For each instance of the given class a new instance for replacement will
     * be created if the interface of the specified class has not changed. The
     * purpose of this method is to create instances from updated class, i.e.,
     * if the specified class has been recompiled.
     *
     * @param cls the class
     * @param creator instance creator
     * @return a pair containing the old instances (first) and the new instances
     * (second)
     * @throws InterfaceChangedException if the interface of the specified class
     * has changed
     */
    public synchronized Pair<ArrayList<Object>, ArrayList<Object>> replaceAllObjects(
            Class<?> cls, InstanceCreator creator)
            throws InterfaceChangedException {

        if (cls == null) {
            throw new IllegalArgumentException(
                    "Argument \"null\" not supported!");
        }

        ArrayList<Object> newInstances = new ArrayList<Object>();
        ArrayList<Object> oldInstances = new ArrayList<Object>();

        Collection<Object> instances
                = getObjectsByClassName(cls.getName());

//        if (instances.isEmpty()) {
//            throw new IllegalStateException(
//                    "Cannot replace. No instances found!");
//        }
        for (Object oldObj : instances) {
            Object newObj;

            newObj = creator.newInstance(cls);

            if (newObj == null) {
                break;
            }

            int objID = getObjectID(oldObj);

            ObjectDescription oDescOld = getObjectDescription(oldObj);
            ObjectDescription oDescNew = generateObjectDescription(
                    newObj, objID, false);

            if (oDescOld.getSignature().equals(oDescNew.getSignature())) {
                oldInstances.add(getObject(objID));

                replaceObject(newObj, objID);
                newInstances.add(newObj);
            } else {
                throw new InterfaceChangedException(
                        "Cannot replace object because the interface of the new"
                        + " class and the old class are not equal!");
            }
        }

        return new Pair<ArrayList<Object>, ArrayList<Object>>(
                oldInstances, newInstances);
    }

    /**
     * Returns the object description of an object.
     *
     * @param o the object of the description that is to be returned
     * @return description of the object or <code>null</code> if the requested
     * description cannot be found
     */
    public ObjectDescription getObjectDescription(Object o) {
        Integer ID = getObjectID(o);

        if (ID == null) {
            return null;
        }

        for (ObjectDescription oD : this.getObjectDescriptions()) {
            if (oD.getID() == ID) {
                return oD;
            }
        }

        return null;
    }

    /**
     * Returns the ID of an object.
     *
     * @param o the object thats ID is to be returned
     * @return the ID of the object if the object could be found;
     * <code>null</code> otherwise
     */
    public Integer getObjectID(Object o) {
        // search for an already existing reference to object o
        for (ObjectEntry oE : objects) {
            if (oE.getObject() == o) {
//                System.out.println(">> Object found!");
                return oE.getID();
            }
        }

        return null;
    }

    /**
     * Returns the description of a method.
     *
     * @param o object of the method
     * @param methodName name of the method
     * @param params method parameters
     * @return description of the method or <code>null</code> if the requested
     * method could not be found
     */
    public MethodDescription getMethodDescription(Object o,
            String methodName, Class... params) {
        ObjectDescription oDesc = getObjectDescription(o);
        MethodDescription result = null;

        ArrayList<Class> paramTypes = new ArrayList<Class>();

        for (Class<?> p : params) {
//            Class c = p.getClass();
            paramTypes.add(p);
        }

        for (MethodDescription mDesc : oDesc.getMethods()) {
            if (mDesc.getMethodName().equals(methodName)) {
//                System.out.println("M: " + mDesc.getMethodName());
                boolean isEqual = true;
                for (int i = 0; i < mDesc.getParameterTypes().length; i++) {
//                    System.out.println(">> P: " +
//                            mDesc.getParameterTypes()[i].getName());
//                    System.out.println(">> PE: " + paramTypes.get(i).getName());
                    if (!paramTypes.get(i).equals(
                            mDesc.getParameterTypes()[i])) {
                        isEqual = false;
                        break;
                    }
                }
                if (isEqual) {
                    result = mDesc;
                    result.setParameters(params);

                    break;
                }
            }
        }

//        if (result == null) {
//            System.err.println(">> MethodDescription not found!"
//                    + " Wrong name or wrong parameters?");
//        }

        return result;
    }

    /**
     * Returns the description of a method.
     *
     * @param o object of the method
     * @param methodName name of the method
     * @param params method parameters
     * @return description of the method
     */
    public MethodDescription getMethodDescriptionFromGUI(Object o,
            String methodName, Object... params) {
        ObjectDescription oDesc = getObjectDescription(o);
        MethodDescription result = null;

//        ArrayList<Class> paramTypes = new ArrayList<Class>();
//
//        for (Object p : params) {
//            Class c = p.getClass();
//            paramTypes.add(c);
//        }
        for (MethodDescription mDesc : oDesc.getMethods()) {
            if (mDesc.getMethodName().equals(methodName)) {
                boolean isEqual = true;
                for (int i = 0; i < mDesc.getParameterTypes().length; i++) {

                    if (!params[i].equals(mDesc.getParameterTypes()[i])) {
                        isEqual = false;
                        break;
                    }
                }
                if (isEqual) {
                    result = mDesc;
                    result.setParameters(params);

                    break;
                }
            }
        }

        if (result == null) {
            System.out.println(">> MethodDescription not found!"
                    + " Wrong name or wrong parameters?");
        }

        return result;
    }

    /**
     * Invokes a method.
     *
     * @param o the object of the method that is to be invoked
     * @param methodName name of the method that is to be invoked
     * @param params all of the methods parameters. If params consists of class
     * objects the parameter values from the gui will be used (TOTO isn't there
     * a cleaner way?)
     * @return the return value of the method
     */
    private Object invoke(Object o, String methodName, Object... params)
            throws InvocationTargetException {

        ArrayList<Class> paramTypes = new ArrayList<Class>();

        for (Object p : params) {
            Class c = p.getClass();
            paramTypes.add(c);
        }

        MethodDescription methodDescription
                = this.getMethodDescription(o, methodName,
                        paramTypes.toArray(new Class[]{}));

        if (methodDescription != null) {
            this.invoke(methodDescription);

            return methodDescription.getReturnValue();
        }

        return null;
    }

    /**
     * Returns object descriptions of all objects that are atached to the
     * inspector.
     *
     * @return object descriptions of all objects that are atached to the
     * inspector
     */
    public Collection<ObjectDescription> getObjectDescriptions() {
        return objectDescriptions;
    }
}
