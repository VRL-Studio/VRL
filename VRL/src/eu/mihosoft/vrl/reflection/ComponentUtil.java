/* 
 * ComponentUtil.java
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

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.dialogs.RemoveComponentDialog;
import eu.mihosoft.vrl.io.IOUtil;
import eu.mihosoft.vrl.io.VJarUtil;
import eu.mihosoft.vrl.io.vrlx.AbstractCode;
import eu.mihosoft.vrl.lang.InstanceCreator;
import eu.mihosoft.vrl.lang.ProjectBuilder;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.lang.groovy.GroovyCodeWindow;
import eu.mihosoft.vrl.system.VRL;
import eu.mihosoft.vrl.visual.CanvasWindow;
import eu.mihosoft.vrl.visual.Message;
import eu.mihosoft.vrl.visual.MessageType;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ComponentUtil {

    // no instanciation allowed
    private ComponentUtil() {
        throw new AssertionError(); // not in this class either!
    }

    /**
     * Adds an instance of the code (must define a class with default
     * constructor) to the canvas.
     *
     * @param code the code that defines the object that shall be added
     * @param canvas the canvas the object shall be added to
     * @param location location where to add the instance
     */
    public static void addObject(Class<?> compClass, VisualCanvas canvas,
            Point location, boolean centered) {

        boolean instantiate = true;

        // analyse annotations and try to find component info
        for (Annotation a : compClass.getAnnotations()) {
            if (a.annotationType().equals(ComponentInfo.class)) {
                ComponentInfo cInfo = (ComponentInfo) a;
                instantiate = cInfo.instantiate();
            }
        }

        instantiate
                = instantiate
                && !compClass.isInterface()
                && !TypeRepresentationBase.class.isAssignableFrom(compClass);

        Object o = null;

        if (instantiate) {
            InstanceCreator creator = new InstanceCreator(canvas);
            o = creator.newInstance(compClass);
        }

        boolean addCodeWindowToCanvas
                = !instantiate
                || compClass.isInterface()
                || o instanceof TypeRepresentationBase;

        AbstractCode code = canvas.getCodes().getByClass(compClass);

        if (addCodeWindowToCanvas) {
            addWindow(code.getCode(), canvas, location, centered);
        } else {

            VisualObject window = canvas.addObject(o);

            if (window == null) {
                canvas.getMessageBox().addMessage("Cannot add object:",
                        ">> instance cannot be added to canvas. Maybe the"
                        + " maximum number of instances of this class is"
                        + " limited.",
                        MessageType.ERROR);
                return;
            }

            int x = location.x;
            int y = (int) (location.y
                    - canvas.getStyle().getBaseValues().
                            getFloat(CanvasWindow.SHADOW_WIDTH_KEY));

            if (centered) {
                x = location.x - window.getWidth() / 2;
            }

            window.setLocation(new Point(x, y));

//            if (code != null || ComponentUtil.isVisualSessionComponent(compClass)) {
            try {
                window.addSourceIcon();
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
//            }
        }
    }

//    /**
//     * Adds a code window to the canvas.
//     *
//     * @param canvas the canvas the window shall be added to
//     * @param menu the menu
//     */
//    private static void addWindow(VisualCanvas canvas, VCanvasPopupMenu menu) {
//        GroovyCodeWindow window = new GroovyCodeWindow(canvas);
//
//        canvas.getWindows().
//                add(window);
//        int x = menu.getCurrentLocation().x - window.getWidth() / 2;
//        int y = (int) (menu.getCurrentLocation().y
//                - canvas.getStyle().getBaseValues().
//                getFloat(CanvasWindow.SHADOW_WIDTH_KEY));
//        window.setLocation(new Point(x, y));
//    }
    /**
     * Adds a code window to the canvas.
     *
     * @param code the code
     * @param canvas the canvas the window shall be added to
     * @param menu the menu
     */
    private static void addWindow(String code, VisualCanvas canvas,
            Point location, boolean centered) {
        GroovyCodeWindow window = new GroovyCodeWindow(canvas);
        window.setCode(code);

        canvas.getWindows().
                add(window);
        int x = location.x;
        int y = (int) (location.y
                - canvas.getStyle().getBaseValues().
                        getFloat(CanvasWindow.SHADOW_WIDTH_KEY));

        if (centered) {
            x = location.x - window.getWidth() / 2;
        }

        window.setLocation(new Point(x, y));
    }

    /**
     * Request the removal of a component (all instances of the specified
     * class).
     *
     * @param componentClass component class that shall be removed
     * @param mainCanvas canvas where to remove the component instances
     * @param controller component controller
     */
    public static void requestRemoval(
            Class<?> componentClass,
            VisualCanvas mainCanvas,
            ComponentController controller) {

        String componentName = getComponentName(componentClass);

        if (RemoveComponentDialog.show(mainCanvas, componentName)) {

            try {
                boolean result = mainCanvas.getProjectController().delete(
                        componentClass.getName());

                if (!result
                        && (ComponentUtil.isVisualSessionComponent(componentClass)
                        || isCodeSessionComponent(componentClass))) {
                    return;
                }

            } catch (IOException ex) {
                Logger.getLogger(ComponentUtil.class.getName()).
                        log(Level.SEVERE, null, ex);
                mainCanvas.getMessageBox().addMessage(
                        "Error while removing component:",
                        ">> component \"<b><tt>" + componentName
                        + "</tt></b>\" cannot be removed!",
                        MessageType.ERROR);
            }

            mainCanvas.getTypeFactory().
                    removeTypeByClassName(componentClass.getName());

            Collection<Object> instances = mainCanvas.getInspector().
                    getObjectsByClassName(componentClass.getName());

            for (Object o : instances) {
                // convert from inspector id to window id
                Collection<Integer> windowIDs
                        = mainCanvas.getInspector().
                                getCanvasWindowIDs(o);

                for (Integer winID : windowIDs) {
                    if (winID != null) {
                        mainCanvas.getWindows().
                                removeObject(winID);
                    }
                }
            }

            mainCanvas.getCodes().
                    removeByName(componentClass.getName());

//            mainCanvas.getClassLoader().removeClass(componentClass);
            mainCanvas.getClassLoader().removeClassByName(
                    componentClass.getName());

            controller.removeComponent(componentClass);

            Message m
                    = mainCanvas.getMessageBox().
                            addMessage("Component Removed:",
                                    ">> component \"<b><tt>" + componentName
                                    + "</tt></b>\" has been successfully removed!",
                                    null,
                                    MessageType.INFO, 5);
            mainCanvas.getMessageBox().messageRead(m);
        }
    }

    /**
     * Returns the name of the component specified by class.
     *
     * @param c component class
     * @return the component name as specified by its
     * {@link eu.annotation.@ComponentInfo} or the class name if no component
     * info has been specified
     */
    public static String getComponentName(Class<?> c) {
        ComponentInfo info = null;
        @SuppressWarnings("unchecked")
        Annotation a = c.getAnnotation(ComponentInfo.class);

        if (a != null) {
            info = (ComponentInfo) a;
            return info.name();
        } else {
            return c.getName();
        }
    }

    /**
     * Returns the category as defined by the
     * {@link eu.annotation.@ComponentInfo}.
     *
     * @param c component class
     * @return the category as defined by {@link eu.annotation.@ComponentInfo}
     * or an empty string if no category has been defined
     */
    public static String getComponentCategory(Class<?> c) {
        ComponentInfo info = null;
        @SuppressWarnings("unchecked")
        Annotation a = c.getAnnotation(ComponentInfo.class);

        if (a != null) {
            info = (ComponentInfo) a;
            return info.category();
        } else {
            return "";
        }
    }

    /**
     * Returns the description as defined by the
     * {@link eu.annotation.@ComponentInfo}.
     *
     * @param c component class
     * @return the description as defined by
     * {@link eu.annotation.@ComponentInfo} or the * * * string
     * <code>"no description"</code> if no component info has been defined
     */
    public static String getComponentDescription(Class<?> c) {
        ComponentInfo info = null;
        @SuppressWarnings("unchecked")
        Annotation a = c.getAnnotation(ComponentInfo.class);

        if (a != null) {
            info = (ComponentInfo) a;
            return info.description();
        } else {
            return "no description";
        }
    }

    /**
     * Indicates whether the specified class defines a VRL component, i.e., if a
     * {@link eu.annotation.@ComponentInfo} has been defined for the specified
     * class
     *
     * @param c component class
     * @return <code>true</code> if a component info has been defined for the
     * specified class;<code>false</code> otherwise
     */
    public static boolean isComponent(Class<?> c) {
        @SuppressWarnings("unchecked")
        Annotation a = c.getAnnotation(ComponentInfo.class);

        if (a != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Indicates whether the specified component shall be ignored as defined by
     * {@link eu.annotation.@ComponentInfo}.
     *
     * @param c component class
     * @return <code>true</code> if the component shall be * ignored;
     * <code>false</code> otherwise
     */
    public static boolean requestsIgnore(Class<?> c) {
        ComponentInfo cInfo = c.getAnnotation(ComponentInfo.class);

        if (cInfo == null || !cInfo.ignore()) {
            return false;
        }

        return true;
    }

    /**
     * Indicates whether the user may remove the specified component ( as
     * defined in its {@link eu.annotation.@ComponentInfo}).
     *
     * @param c component class
     * @return <code>true</code> if the user may remove the specified component;
     * <code>false</code> otherwise
     */
    public static boolean allowsRemoval(Class<?> c) {
        ComponentInfo info = null;
        @SuppressWarnings("unchecked")
        Annotation a = c.getAnnotation(ComponentInfo.class);

        if (a != null) {
            info = (ComponentInfo) a;
            return info.allowRemoval();
        } else {
            return true;
        }
    }

    /**
     * Returns the name of the <code>*.vrlx</code> session of the specified
     * component.
     *
     * @param c component class
     * @return the name of the <code>*.vrlx</code> session of the specified
     * component if the file exists in the current project
     */
    public static String getSessionName(Class<?> c) {
        String result = null;

        // 26.10.2017
        // as of JDK 8u15x we might run into trouble with
        // loading resources. that's why we try to find it directly
        try {
            String componentName = c.getName();
            File sessionFile
                    = VRL.getCurrentProjectController().getProject().
                            getSessionFileByEntryName(componentName);
            
            if(sessionFile.exists()) {
                return sessionFile.getAbsolutePath();
            }
        } catch (Exception ex) {
            // we have to try a different method
        }
        
        String sessionName = "/" + VLangUtils.dotToSlash(c.getName()) + ".vrlx";

        URL url = c.getResource(sessionName);

        if (url != null) {
            result = new File(url.getFile()).getAbsolutePath();

            // now we check whether file comes from tmp folder, i.e. from current project
            // if not we cannot use the file and return null
            if (!result.contains(
                    VRL.getPropertyFolderManager().getTmpFolder().getAbsolutePath())) {
                result = null;
            }
        }

        return result;
    }

    /**
     * Returns the name of the <code>*.groovy</code> code file of the specified
     * component.
     *
     * @param c component class
     * @return the name of the <code>*.groovy</code> code file of the specified
     * component if the file exists in the current project
     */
    public static String getSessionCodeName(Class<?> c) {
        String result = null;

        
        
        // 26.10.2017
        // as of JDK 8u15x we might run into trouble with
        // loading resources. that's why we try to find it directly
        try {
            String componentName = c.getName();
            File codeFile
                    = VRL.getCurrentProjectController().getProject().
                            getSourceFileByEntryName(componentName);
            
            if(codeFile.exists()) {
                return codeFile.getAbsolutePath();
            }
        } catch (Exception ex) {
            // we have to try a different method
        }
        
        String sessionName = "/" + VLangUtils.dotToSlash(c.getName()) + ".groovy";
        System.out.println("-> DEBUG: session code name: " + sessionName);

        URL url = c.getResource(sessionName);

        if (url != null) {

            try {
                result = URLDecoder.decode(url.getFile(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ComponentUtil.class.getName()).
                        log(Level.SEVERE, null, ex);

                // fallback
                result = URLDecoder.decode(url.getFile());
            }

            File file = new File(result);

            result = file.getAbsolutePath();

            // now we check whether file comes from tmp folder, i.e. from current project
            // if not we cannot use the file and return null
            if (!result.contains(
                    VRL.getPropertyFolderManager().getTmpFolder().getAbsolutePath())) {

                System.out.println("-> DEBUG: session code name: cannot use code, since it's outside of our project:" + result);

                result = null;
            }
        }

        System.out.println("-> DEBUG: session code name: -> final code: " + result);

        return result;
    }

    /**
     * Indicates whether the specified class is a visually defined session
     * component.
     *
     * @param c component class
     * @return <code>true</code> if the specified class is a visually defined
     * session component; <code>false</code> otherwise
     */
    public static boolean isVisualSessionComponent(Class<?> c) {
        return getSessionName(c) != null;
    }

    /**
     * Indicates whether the specified class is a code session component.
     *
     * @param c component class
     * @return <code>true</code> if the specified class is a code session
     * component; <code>false</code> otherwise
     */
    public static boolean isCodeSessionComponent(Class<?> c) {
        return getSessionName(c) == null && getSessionCodeName(c) != null;
    }

    /**
     * Defines whether serialization is enabled for the specified component
     * class.
     *
     * @param c component class
     * @return <code>true</code> if serialization is enabled for the specified
     * component class; <code>false</code> otherwise
     */
    public static boolean isSerializationEnabled(Class<?> c) {
        ObjectInfo info = null;
        @SuppressWarnings("unchecked")
        Annotation a = c.getAnnotation(ObjectInfo.class);

        if (a != null) {
            info = (ObjectInfo) a;
            return info.serialize();
        } else {
            return true;
        }
    }

    public static boolean isParameterSerializationEnabled(Class<?> c) {
        ObjectInfo info = null;
        @SuppressWarnings("unchecked")
        Annotation a = c.getAnnotation(ObjectInfo.class);

        if (a != null) {
            info = (ObjectInfo) a;
            return info.serializeParam();
        } else {
            return true;
        }
    }

    public static String getComponentCode(Class<?> c) {

        String packageName = VLangUtils.dotToSlash(VLangUtils.packageNameFromFullClassName(c.getName()));
        String classCodeName = VLangUtils.shortNameFromFullClassName(c.getName()) + ".groovy";

        String resourceName = "/" + packageName + "/" + classCodeName;
        InputStream in = c.getResourceAsStream(resourceName);

        if (in == null) {
            return null;
        }

        return IOUtil.convertStreamToString(in);
    }

}
