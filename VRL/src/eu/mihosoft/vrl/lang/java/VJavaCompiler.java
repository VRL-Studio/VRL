/* 
 * VJavaCompiler.java
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

package eu.mihosoft.vrl.lang.java;

import eu.mihosoft.vrl.lang.ClassEntry;
import eu.mihosoft.vrl.lang.CompilerProvider;
import eu.mihosoft.vrl.lang.Patterns;
import eu.mihosoft.vrl.lang.VCompiler;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.lang.groovy.GroovyCompiler;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.visual.MessageType;
import eu.mihosoft.vrl.visual.VCodeEditor;
import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VJavaCompiler implements VCompiler {

    private JavaCompiler compiler;
    private DiagnosticCollector<JavaFileObject> collector;
    private VClassFileManager manager;
    private VisualCanvas mainCanvas;

//    public VJavaCompiler() {
//        try {
//            init(null);
//        } catch (Exception ex) {
//            Logger.getLogger(VJavaCompiler.class.getName()).
//                    log(Level.SEVERE, null, ex);
//        }
//    }
    public VJavaCompiler(VisualCanvas canvas) {

        if (canvas == null) {
            throw new IllegalArgumentException("Argument \"null\" not supported!");
        }

        try {
            init(canvas);
        } catch (Exception ex) {
            Logger.getLogger(VJavaCompiler.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    private void init(VisualCanvas canvas) throws Exception {
        compiler = ToolProvider.getSystemJavaCompiler();
        collector = new DiagnosticCollector<JavaFileObject>();
        manager = new VClassFileManager(compiler, canvas);
    }

    @Override
    public Class<?> compile(String code) {
        return compile(code, null);
    }

    @Override
    public Class<?> compile(String code, VCodeEditor editor) {
        Class<?> clazz = null;

        String codeHeader = "";

        for (String s : new GroovyCompiler().getImports()) {
            codeHeader += s + ";";
        }

//        code = codeHeader + code;
        
        Matcher m1 = Patterns.PACKAGE_DEFINITION.matcher(code);
        
        if (m1.find()) {
            int pos = m1.end();
            code = code.substring(0,pos) + codeHeader + code.substring(pos+1);
        } else {
            code = codeHeader + code;
        }

        String fullName = VLangUtils.fullClassNameFromCode(code);

        // reset error messaqes
        collector = new DiagnosticCollector<JavaFileObject>();

        // remove previous class versions
        manager.removeClass(fullName);
//        manager = new VClassFileManager(compiler, mainCanvas);

        StringBuilder sb = new StringBuilder();
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        for (URL url : urlClassLoader.getURLs()) {
            sb.append(url.getFile()).append(File.pathSeparator);
        }

        for (String p : manager.getPackageNames()) {
            sb.append(p).append(File.pathSeparator);
        }


        List<String> optionList = new ArrayList<String>();

        // set compiler's classpath to be same as the runtime's
        optionList.addAll(Arrays.asList("-classpath",
                System.getProperty("java.class.path") + sb.toString() + ":."));

        // additional options
//        optionList.addAll(Arrays.asList("option"));

        StringJavaFileObject strFile = new StringJavaFileObject(fullName, code);
        Iterable<? extends JavaFileObject> units = Arrays.asList(strFile);

        JavaCompiler.CompilationTask task =
                compiler.getTask(
                null, manager, collector, optionList, null, units);

        boolean status = task.call();

        if (status) {
            try {
                clazz = manager.getClassLoader(null).loadClass(fullName);

                if (!Modifier.isPublic(clazz.getModifiers())) {
                    System.err.println("JavaCompiler: >> "
                            + VLangUtils.classNameFromCode(code)
                            + ": first class in file must be public!");

                    editor.getMainCanvas().getMessageBox().addUniqueMessage(
                            "Cannot compile class:",
                            " >> "
                            + VLangUtils.classNameFromCode(code)
                            + ": first class in file must be public!",
                            null, MessageType.ERROR);

                    clazz = null;
                }

                if (mainCanvas != null) {
                    for (ClassEntry e : manager.getClasses()) {
                        mainCanvas.getClassLoader().addClass(
                                manager.getClassLoader(null).
                                loadClass(e.getName()));
                    }
                }

            } catch (ClassNotFoundException ex) {
                Logger.getLogger(VJavaCompiler.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        } else {
            System.err.println("Message:");
            for (Diagnostic<?> d : collector.getDiagnostics()) {
                System.err.println(d.getMessage(null));
                editor.addCodeErrorMessage((int)d.getLineNumber(),
                        d.getMessage(null).replace(
                        "string:///", ">> In File "));
            }
        }

        return clazz;
    }

    public static void main(String[] args) {
        // TODO code application logic here

        String classCode = "package test123; public class Test123 { public void hello() {class blah{};System.out.println(\"Hello, World!\");} } class Test456 {}";

        VJavaCompiler compiler = new VJavaCompiler(null);

        try {
            Class<?> cls = compiler.compile(classCode);
            System.out.println("Class: " + cls.getName());
        } catch (Exception ex) {
            Logger.getLogger(VJavaCompiler.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        VJavaCompiler compiler2 = new VJavaCompiler(null);
//        compiler2.setClasses(compiler.getClasses());

        Class<?> cls = null;
        try {
            cls = compiler2.getClassLoader().loadClass(compiler2.getClasses().iterator().next().getName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(VJavaCompiler.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("FINALLY: " + cls.getName());
    }

    @Override
    public Collection<ClassEntry> getClasses() {
        return manager.getClasses();
    }

    public ClassLoader getClassLoader() {
        return manager.getClassLoader(null);
    }

//    public void setClasses(Collection<ClassEntry> classes) {
//        manager.setClasses(classes);
//    }
    @Override
    public Collection<String> supportedLanguages() {
        return Arrays.asList(CompilerProvider.LANG_JAVA);
    }

    @Override
    public void setClassLoader(ClassLoader loader) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addClassFiles(Collection<ClassFileObject> objects) {
        manager.addClassFiles(objects);
    }
}
