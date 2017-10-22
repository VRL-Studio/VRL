/* 
 * AbstractCode.java
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

package eu.mihosoft.vrl.io.vrlx;

import eu.mihosoft.vrl.lang.CompilerNotFoundException;
import eu.mihosoft.vrl.lang.groovy.GroovyCompiler;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.lang.ClassEntry;
import eu.mihosoft.vrl.lang.CompilerProvider;
import eu.mihosoft.vrl.lang.InstanceCreator;
import eu.mihosoft.vrl.lang.VCompiler;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.visual.VCodeEditor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents source code.
 * <p>
 * Abstract codes can be used to define custom components at runtime.
 * The codes will be compiled when loading the corresponding file.
 * VRL provides two mamangement strategies for the code.
 * <ul>
 *   <li>source management by class instances</li>
 *   <li>the component management system</li>
 * </ul>
 * If the code does not provide a component info the code is only
 * accessible from objects of the defined class. If all objects have been
 * deleted the source code cannot be accessed anymore and will be deleted.
 * </p>
 * <p>
 * If the code provides a component info it will be added to the component menu
 * (canvas popup menu). From there it is possible to create new instances. The
 * code will only be deleted if manually requested.
 * </p>
 * <p>
 * Currently only Groovy code is supported. Although Groovy scripts are
 * theoretically possible it is not reccommended to define scripts. VRL
 * currently only supports the definition of classes. The first class defined in
 * the code will be used for visual representation. If the class is a subclass
 * of <code>TypeRepresentationBase</code> it will not be displayed as visual
 * representation but only added to the type factory. Custom type 
 * representations should always provide a component info. Otherwise they cannot
 * be accessed anymore and will not be saved.
 * </p>
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class AbstractCode {

    /**
     * the source code
     */
    private String code;
    /**
     * the language of this source
     */
    private String language;
    /**
     * the name of the class that is to be compiled
     */
    private String name;
    /**
     * indicates whether the class defines a component
     */
    private boolean component;
    /**
     * package name
     */
    private String packageName;
    private boolean recompile;

    /**
     * Compiles the code and adds the class to the class loader of
     * the specified main canvas.
     * @param mainCanvas the main canvas
     * @return the compiled class
     */
    public Class addToClassPath(VisualCanvas mainCanvas) {
        return addToClassPath(mainCanvas, null);
    }

    /**
     * Compiles the code and adds the class to the class loader of
     * the specified main canvas.
     * @param mainCanvas the main canvas
     * @param editor the code editor to use for error notification
     * @param addComponentToMenu defines whether the source code is to be added
     *                           to the component menu if the source defines a
     *                           component via  <code>@ComponentInfo</code>
     * @return the compiled class
     */
    public Class<?> addToClassPath(VisualCanvas mainCanvas, VCodeEditor editor,
            boolean addComponentToMenu) {

        // check whether class has already been compiled
        if (!isRecompile()) {
            try {
                if (name == null) {
                    name = VLangUtils.classNameFromCode(code);
                }
                Class<?> cls = mainCanvas.getClassLoader().loadClass(name);
                if (cls == null) {
                    throw new ClassNotFoundException();
                }

                System.out.println(">> addToClassPath: CLASS LOADED!");
                @SuppressWarnings("unchecked")
                Annotation a = cls.getAnnotation(ComponentInfo.class);

                if (a != null) {
                    component = true;
                    if (mainCanvas.getPopupMenu() != null && addComponentToMenu) {
                        mainCanvas.getComponentController().addComponent(cls);
                    }
                } else {
                    //
                }

                if (!Modifier.isPublic(cls.getModifiers())) {
                    System.err.println("JavaCompiler2: >> "
                            + VLangUtils.classNameFromCode(code)
                            + ": first class in file must be public!");
                    cls = null;
                }

                return cls;

            } catch (ClassNotFoundException ex) {
                //
            }
        }

        setRecompile(false);

        // compiling class
        
//        System.out.println("CLASS COMPILE:");

        VCompiler c = null;

        try {
            c = mainCanvas.getCompilerProvider().getCompiler(
                    CompilerProvider.LANG_GROOVY);
        } catch (CompilerNotFoundException ex) {
            Logger.getLogger(AbstractCode.class.getName()).
                    log(Level.SEVERE, null, ex);
            return null;
        }

        Class<?> result = c.compile(code, editor);

        InstanceCreator creator = new InstanceCreator();

        Object o = creator.newInstance(result);

//        if (o instanceof TypeRepresentationBase) {
//            mainCanvas.getTypeFactory().addType(
//                    (TypeRepresentationBase) o);
//        }
        
        @SuppressWarnings("unchecked")
        Annotation a = result.getAnnotation(ComponentInfo.class);

        if (a != null) {
            component = true;
            if (mainCanvas.getPopupMenu() != null && addComponentToMenu) {
                mainCanvas.getComponentController().addComponent(result);
            }
        } else {
            //
        }

        setName(result.getName());

        mainCanvas.getClassLoader().addClass(result);

        return result;
    }

    /**
     * Compiles the code and adds the class to the class loader of
     * the specified main canvas. If the source code defines a component via
     * <code>@ComponentInfo</code> it will be added to the component menu of the
     * main canvas.
     * @param mainCanvas the main canvas
     * @param editor the code editor to use for error notification
     * @return the compiled class
     */
    public Class addToClassPath(VisualCanvas mainCanvas, VCodeEditor editor) {
        return addToClassPath(mainCanvas, editor, true);
    }

    /**
     * Indicates whether the code defines a component, i.e., if the class
     * is annotaded with the <code>@ComponentInfo()</code> annotation.
     * @return <code>true</code>, if the class is a component;<code>false</code>
     * otherwise
     */
    public boolean isComponent() {
        return component;
    }

    /**
     * Returns the code.
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Define the code.
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Returns the name of the class defined by this code.
     * @return the name of the class, if the source has been compiled;
     * <code>null</code> otherwise
     */
    public String getName() {
        return name;
    }

    /**
     * <p>
     * Defines the class name of this code.
     * </p>
     * <p>
     * <b>Warning:</b> only use this method to restore the class
     * name from file. Usually the name will be set automatically
     * when the defined class has been added to the class path.
     * </p>
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @param packageName the packageName to set
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * @return the recompile
     */
    public boolean isRecompile() {
        return recompile;
    }

    /**
     * @param recompile the recompile to set
     */
    public void setRecompile(boolean recompile) {
        this.recompile = recompile;
    }
}
