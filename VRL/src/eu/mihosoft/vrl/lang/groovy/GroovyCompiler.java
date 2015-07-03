/* 
 * GroovyCompiler.java
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

package eu.mihosoft.vrl.lang.groovy;

import eu.mihosoft.vrl.lang.ClassEntry;
import eu.mihosoft.vrl.lang.CompilerProvider;
import eu.mihosoft.vrl.lang.Patterns;
import eu.mihosoft.vrl.lang.java.ClassFileObject;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.system.VRL;
import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.MessageType;
import eu.mihosoft.vrl.visual.VCodeEditor;
import groovy.lang.GroovyClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;

/**
 * The purpose of this class is to provide an easy way for compiling Groovy
 * code. It supports VRL message generation to show error messages etc.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public final class GroovyCompiler implements eu.mihosoft.vrl.lang.VCompiler {

    private VisualCanvas mainCanvas;
    private ArrayList<String> imports = new ArrayList<String>();
    private boolean catchCompileException = true;

    /**
     * Constructor.
     */
    public GroovyCompiler() {
        init();
    }

    /**
     * Constructor.
     *
     * @param mainCanvas the main canvas object
     */
    public GroovyCompiler(Canvas mainCanvas) {
        this.mainCanvas = VisualCanvas.asVisualCanvas(mainCanvas);
//        this.loader = this.mainCanvas.getClassLoader();
        init();
    }

    private void init() {
        imports.add("import eu.mihosoft.vrl.animation.*;");
        imports.add("import eu.mihosoft.vrl.annotation.*;");
        imports.add("import eu.mihosoft.vrl.asm.*;");
        imports.add("import eu.mihosoft.vrl.devel.*;");
        imports.add("import eu.mihosoft.vrl.dialogs.*;");
        imports.add("import eu.mihosoft.vrl.effects.*;");
        imports.add("import eu.mihosoft.vrl.io.*;");
        imports.add("import eu.mihosoft.vrl.io.vrlx.*;");
        imports.add("import eu.mihosoft.vrl.lang.*;");
        imports.add("import eu.mihosoft.vrl.math.*;");
        imports.add("import eu.mihosoft.vrl.lang.groovy.*;");
        imports.add("import eu.mihosoft.vrl.lang.visual.*;");
        imports.add("import eu.mihosoft.vrl.reflection.*;");
        imports.add("import eu.mihosoft.vrl.security.*;");
        imports.add("import eu.mihosoft.vrl.system.*;");
        imports.add("import eu.mihosoft.vrl.types.*;");
        imports.add("import eu.mihosoft.vrl.media.*;");
        imports.add("import eu.mihosoft.vrl.v3d.*;");
        imports.add("import eu.mihosoft.vrl.visual.*;");
        imports.add("import javax.vecmath.*;");
        imports.add("import javax.media.j3d.*;");
//        imports.add("import com.sun.j3d.utils.*;");
        imports.add("import java.awt.*;");
        imports.add("import java.awt.image.*;");
        imports.add("import javax.swing.*;");
    }

    /**
     * Returns the line number where the error occured.
     *
     * @param message the error message from the compiler
     * @return the line number
     */
    private int getErrorLine(String message) {
        Pattern linePattern =
                Pattern.compile(" @ line \\d+");

        Matcher matcher = linePattern.matcher(message);

        int line = 0;

        if (matcher.find()) {
            String lineString = matcher.group();

            Pattern numberPattern =
                    Pattern.compile("\\d+");

            matcher = numberPattern.matcher(lineString);

            if (matcher.find()) {

                line = new Integer(matcher.group());
            }
        }
        return line;
    }

    /**
     * when set to false the exceptions occured during compliation will not be
     * handled.
     * 
     * @param catchCompileException 
     */
    public void setCatchCompileException(boolean catchCompileException) {
        this.catchCompileException = catchCompileException;
    }

    /**
     * returns if exceptions are handled that occured when compiling
     * 
     * @return true if handling exceptions, false else
     */
    public boolean isCatchCompileException() {
        return catchCompileException;
    }

    /**
     * Caution: this method does nothing as the classloader of the canvas is
     * used!
     *
     * @param loader
     */
    @Override
    public void setClassLoader(ClassLoader loader) {
//        this.loader = loader;
    }

    /**
     * Compiles Groovy code and returns the corresponding class object.
     *
     * @param code code to compile
     * @param editor the editor that shall be used to display error
     * notifications
     * @return the class object or <code>null</code> if the code could not be
     * compiled
     */
    @Override
    public Class<?> compile(String code, VCodeEditor editor) {
        Class<?> result = null;

        GroovyClassLoader gcl;
        
        CompilerConfiguration cfg = new CompilerConfiguration();
        cfg.setSourceEncoding("UTF-8");
        cfg.setTargetBytecode("1.6");

//        String packageName = VLangUtils.packageNameFromCode(code);

        if (mainCanvas != null) {
            gcl = new GroovyClassLoader(mainCanvas.getClassLoader(),cfg);
        } else {
            gcl = new GroovyClassLoader(VRL.getInternalPluginClassLoader(),cfg);
        }

        String codeHeader = "";
//
//        if (packageName != null && !packageName.equals("")) {
//            codeHeader = "package " + packageName + ";";
//        }
//
        for (String s : getImports()) {
            codeHeader += s;
        }

//        code = codeHeader + code;

        Matcher m1 = Patterns.PACKAGE_DEFINITION.matcher(code);

        if (m1.find()) {
            int pos = m1.end();

//            System.out.println("----- CODE 1 -----");
//            System.out.println(code);
//            System.out.println("------------------");            

            code = code.substring(0, pos) + codeHeader + code.substring(pos);

//            System.out.println("----- CODE 2 -----");
//            System.out.println(code);
//            System.out.println("------------------");

        } else {
            code = codeHeader + code;
        }

        if (catchCompileException) {
            try {

                result = gcl.parseClass(code);

                if (editor != null) {
                    editor.removeErrorNotifiers();
                }
            } catch (Exception ex) {
                if (getMainCanvas() != null) {
                    generateErrorMessage(editor, ex.getMessage());
                } else {
                    Logger.getLogger(GroovyCompiler.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }
        } else {
            result = gcl.parseClass(code);

            if (editor != null) {
                editor.removeErrorNotifiers();
            }
        }
         
        return result;
    }

    /**
     * Compiles Groovy code and returns the corresponding class object.
     *
     * @param code code to compile
     * @return the class object or <code>null</code> if the code could not be
     * compiled
     */
    @Override
    public Class<?> compile(String code) {
        return compile(code, null);
    }

    /**
     * Generates an error message.
     *
     * @param editor the editor to use for error notification
     * @param message the message
     */
    private void generateErrorMessage(VCodeEditor editor, String message) {

        int line = getErrorLine(message);

        message = message.replaceFirst(
                "startup failed:",
                "");

        message = message.replaceAll(
                "script\\d+.groovy: \\d+:",
                "");

        message = message.replaceAll(
                "\\d+ errors",
                "");

        if (editor != null) {

            editor.addCodeErrorMessage(line,
                    ">> compilation failed:<br>"
                    + "<pre><code>"
                    + message
                    + "</code></pre>");
        } else {

            mainCanvas.getMessageBox().
                    addMessage("Can't compile code:",
                    ">> compilation failed:<br>"
                    + "<pre><code>"
                    + message
                    + "</code></pre>", MessageType.ERROR);
        }
    }

    /**
     * Adds an import line to the code header.
     *
     * @param s the import command
     */
    public void addImport(String s) {
        getImports().add(s);
    }

    /**
     * Returns the main canvas.
     *
     * @return the main canvas
     */
    public Canvas getMainCanvas() {
        return mainCanvas;
    }

    /**
     * Defines the main canvas.
     *
     * @param mainCanvas the canvas to set
     */
    public void setMainCanvas(Canvas mainCanvas) {
        this.mainCanvas = VisualCanvas.asVisualCanvas(mainCanvas);
    }

    /**
     * Returns a list that contains all import commands.
     *
     * @return the import commands
     */
    public ArrayList<String> getImports() {
        return imports;
    }

    /**
     * <p> Attempts to parse the specified code with the specified tolerance.
     * Updates the
     * <code>parser</code> and
     * <code>error</code> members appropriately. Returns true if the text
     * parsed, false otherwise. The attempts to identify and suppress errors
     * resulting from the unfinished source text. </p> <p> <b>Note:</b> taken
     * from {@link groovy.ui.InteractiveShell}. </p>
     */
    private static boolean parse(final String code, final int tolerance) {
        assert code != null;

        boolean parsed = false;

        // Create the parser and attempt to parse the text as a top-level statement.
        try {
            SourceUnit parser = SourceUnit.create("vrl-script", code, tolerance);
            parser.parse();
            parsed = true;
        } // We report errors other than unexpected EOF to the user.
        catch (CompilationFailedException e) {
            //
        } catch (Exception e) {
            //
        }

        return parsed;
    }

    public static boolean parse(final String code) {
        return parse(code, 1);
    }

    @Override
    public Collection<String> supportedLanguages() {
        return Arrays.asList(CompilerProvider.LANG_GROOVY);
    }

    @Override
    public Collection<ClassEntry> getClasses() {
        return new ArrayList<ClassEntry>();
    }

    @Override
    public void addClassFiles(Collection<ClassFileObject> objects) {
        //
    }
}
