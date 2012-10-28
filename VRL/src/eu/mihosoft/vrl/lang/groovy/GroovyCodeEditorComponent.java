/* 
 * GroovyCodeEditorComponent.java
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

import eu.mihosoft.vrl.annotation.AskIfCloseMethodInfo;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.asm.ByteCodeUtil;
import eu.mihosoft.vrl.asm.ClassFileDependency;
import eu.mihosoft.vrl.io.CommitListener;
import eu.mihosoft.vrl.io.TextLoader;
import eu.mihosoft.vrl.io.TextSaver;
import eu.mihosoft.vrl.io.VProjectController;
import eu.mihosoft.vrl.io.VersionManagement;
import eu.mihosoft.vrl.lang.*;
import eu.mihosoft.vrl.reflection.*;
import eu.mihosoft.vrl.system.Constants;
import eu.mihosoft.vrl.system.VMessage;
import eu.mihosoft.vrl.types.InputCodeType;
import eu.mihosoft.vrl.types.MethodRequest;
import eu.mihosoft.vrl.visual.*;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@ComponentInfo(name = "Groovy Code", category = "VRL/Language",
description = "Groovy Code Editor")
@ObjectInfo(name = "Groovy Code")
public class GroovyCodeEditorComponent implements Serializable {

    private static final long serialVersionUID = 1L;
    // code template
    private String code =
            "@ComponentInfo(name=\"CMPNAME\", category=\"Custom\")\n"
            + "public class CLSNAME implements java.io.Serializable {\n"
            + "\tprivate static final long serialVersionUID=1L;\n"
            + "\n"
            + "\t// add your code here\n\n"
            + "}";
    // detects whether this component has been initialized
    // otherwise type representation state would be overwritten by content
    // of code
    private boolean initialized;
    private transient VisualObject vObj;
    private transient Class<?> clazz;
    private transient String className;
    private transient boolean replaceVisualComponents;
    private transient boolean compileErrorsOrCancelled;
    private transient VisualCanvas canvas;
    private transient VButton createInstanceBtn;
    private transient DefaultMethodRepresentation mRep;

    public GroovyCodeEditorComponent() {
    }

    public GroovyCodeEditorComponent(String code) {
        if (code != null) {
            this.code = code;
        }
    }

    private String getCurrentCodeFromView() {

        updateMrep();

        if (mRep == null) {
            return null;
        } else {
            return (String) mRep.getParameter(1).getViewValue();
        }
    }

    private String getFullClassNameFromCode(String code) {
        String fullClassName = VLangUtils.fullClassNameFromCode(code);

        if (fullClassName == null || fullClassName.isEmpty()) {
            fullClassName = VLangUtils.fullInterfaceNameFromCode(code);
        }

        return fullClassName;
    }

    private boolean isCodeInProjectFileEqualToCode(String currentCode) {

        boolean isEqual = false;

        String fullClassName = getFullClassNameFromCode(currentCode);

        if (fullClassName != null && !fullClassName.isEmpty()) {

            File codeFile =
                    canvas.getProjectController().
                    getProject().
                    getSourceFileByEntryName(
                    fullClassName);

            TextLoader loader = new TextLoader();

            String loadedCode = "";

            try {
                loadedCode = (String) loader.loadFile(codeFile);

                loadedCode = VLangUtils.filterAutoGenCode(
                        loadedCode, "imports");

                if (!VLangUtils.packageDefinedInCode(currentCode)) {
                    currentCode = "package eu.mihosoft.vrl.user;\n" + currentCode;
                }

            } catch (IOException ex) {
                System.out.println(">> code file does not exist: " + codeFile);
//                Logger.getLogger(GroovyCodeEditorComponent.class.getName()).
//                        log(Level.SEVERE, null, ex);
            }

            // when comparing code we ignore last empty line at the end of code
            // if such a line exists
            if (removeEmptyLastLinesFromCode(loadedCode).equals(
                    removeEmptyLastLinesFromCode(currentCode))) {
                isEqual = true;
            }
        }

        return isEqual;
    }

    private void updateMrep() {
        if (mRep == null) {
            mRep = vObj.getObjectRepresentation().
                    getMethodBySignature("compile",
                    MethodRequest.class, String.class);
        }
    }

    @AskIfCloseMethodInfo
    private boolean askIfClose() {

        updateMrep();

        String currentCode = getCurrentCodeFromView();

        return !isCodeInProjectFileEqualToCode(currentCode);
    }

    private String removeEmptyLastLinesFromCode(String code) {

        if (code == null) {
            return "";
        }

        String[] splittedCode = code.split("\n");
        if (splittedCode.length > 0
                && splittedCode[splittedCode.length - 1].trim().isEmpty()) {
            String[] newCode = new String[splittedCode.length - 1];
            System.arraycopy(splittedCode, 0, newCode, 0, newCode.length);
            splittedCode = newCode;
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < splittedCode.length; i++) {
            String string = splittedCode[i];

            if (i > 0) {
                builder.append('\n');
            }

            builder.append(string);
        }

        return builder.toString();
    }

    @MethodInfo(noGUI = true, callOptions = "assign-window")
    public void showCode(VisualObject vObj) {

        this.vObj = vObj;

        canvas = (VisualCanvas) vObj.getMainCanvas();

        DefaultMethodRepresentation mRep = vObj.getObjectRepresentation().
                getMethodBySignature("compile",
                MethodRequest.class, String.class);

        final InputCodeType tRep = (InputCodeType) mRep.getParameter(1);

        // sets code if code is not empty and if not initialized
        if (code != null && !code.isEmpty() && !initialized) {

            initialized = true;

            //System.out.println(">> Code: " + code);

            code = VLangUtils.filterAutoGenCode(code, "imports");

            tRep.setViewValue(code);

            // revalidate after fade in to fix visual bugs
            Timer timer = new Timer();
            TimerTask t = new TimerTask() {
                @Override
                public void run() {
                    VSwingUtil.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            tRep.revalidate();
                        }
                    });
                }
            };

            timer.schedule(t,
                    (long) (1000 * vObj.getStyle().getBaseValues().
                    getDouble(CanvasWindow.FADE_IN_DURATION_KEY)));
        }

        initEditor(vObj, tRep);

        updateEditorTitle(tRep.getEditor().getEditor().getText(), false);
        updateCodeEditorsOfSameClass();
        checkIfEditorRepresentsNewestVersion();
    }

    private void updateEditorTitle(String code, boolean representsCurrent) {
        String fullClassName = VLangUtils.fullClassNameFromCode(code);

        boolean isClass = true;
        boolean isInterface = false;

        if (fullClassName == null || fullClassName.isEmpty()) {
            isClass = false;
            fullClassName = VLangUtils.fullInterfaceNameFromCode(code);
            isInterface = fullClassName != null && !fullClassName.isEmpty();
        }

        String title = "<html>Code (<b>no class or interface defined</b>)</html>";

        fullClassName = canvas.getProjectController().getProject().
                getEntryNameWithoutDefaultPackage(fullClassName);

        fullClassName = fullClassName.replace("/", ".");

        className = fullClassName;

        String printedName = fullClassName;

        if (!representsCurrent) {
            printedName += " (*)";
        }

        if (isClass) {
            title = "<html>Code of class <b>" + printedName + "</b></html>";
        } else if (isInterface) {
            title = "<html>Code of interface <b>" + printedName + "</b></html>";
        }

        if (vObj != null) {
            vObj.setTitle(title);
        }
    }

    private void initEditor(VisualObject vObj, final InputCodeType tRep) {

        KeyStroke comment = KeyStroke.getKeyStroke(
                KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK);
        KeyStroke uncomment = KeyStroke.getKeyStroke(
                KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK/* | InputEvent.SHIFT_DOWN_MASK*/);

        tRep.getEditor().getEditor().registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextArea editor = tRep.getEditor().getEditor();
                Document document = editor.getDocument();
                int selectionStart = editor.getSelectionStart();
                int selectionEnd = editor.getSelectionEnd();

                try {
                    int firstLineOffset = editor.getLineOfOffset(selectionStart);
                    int lastLineOffset = editor.getLineOfOffset(selectionEnd);

                    for (int l = firstLineOffset; l <= lastLineOffset; l++) {

                        int offset = editor.getLineStartOffset(l);

                        document.insertString(offset, "//", null);
                    }

                } catch (BadLocationException ex) {
                    Logger.getLogger(GroovyCodeEditorComponent.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }
        }, comment, JComponent.WHEN_FOCUSED);

        tRep.getEditor().getEditor().registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextArea editor = tRep.getEditor().getEditor();
                int caretPosition = editor.getCaretPosition();
                Document document = editor.getDocument();
                int selectionStart = editor.getSelectionStart();
                int selectionEnd = editor.getSelectionEnd();

                try {
                    int caretLinePos = editor.getLineOfOffset(caretPosition);

                    int firstLineOffset = editor.getLineOfOffset(selectionStart);
                    int lastLineOffset = editor.getLineOfOffset(selectionEnd);

                    Set<Integer> uncommentedLines = new HashSet<Integer>();

                    for (int l = firstLineOffset; l <= lastLineOffset; l++) {

                        int offset = editor.getLineStartOffset(l);
                        int len = editor.getLineEndOffset(l) - offset;

                        String line = document.getText(offset, len);
                        Pattern commentPattern = Pattern.compile("^\\s*\\/\\/.*", Pattern.DOTALL);
                        Matcher lineMatcher = commentPattern.matcher(line);

                        if (lineMatcher.matches()) {
                            uncommentedLines.add(l);
                            line = line.replaceFirst("\\/\\/", "");
                            editor.replaceRange(line, offset, editor.getLineEndOffset(l));
                        }
                    }

                    int caretReplacementOffset = 0;
                    int selectionStartReplacementOffset = 0;
                    int selectionEndReplacementOffset = 0;

                    for (Integer li : uncommentedLines) {
                        if (li <= caretLinePos) {
                            caretReplacementOffset -= 2;
                        }
                        if (li <= firstLineOffset) {
                            selectionStartReplacementOffset -= 2;
                        }
                        if (li <= lastLineOffset) {
                            selectionEndReplacementOffset -= 2;
                        }
                    }

                    editor.setCaretPosition(caretPosition + caretReplacementOffset);

                    editor.select(selectionStart + selectionStartReplacementOffset,
                            selectionEnd + selectionEndReplacementOffset);
                } catch (BadLocationException ex) {
                    Logger.getLogger(GroovyCodeEditorComponent.class.getName()).
                            log(Level.SEVERE, null, ex);
                }

            }
        }, uncomment, JComponent.WHEN_FOCUSED);

        tRep.getEditor().getEditor().getDocument().addDocumentListener(
                new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        updateEditorTitle(getCurrentCodeFromView(), false);
                        removeCreateInstanceBtn();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        updateEditorTitle(getCurrentCodeFromView(), false);
                        removeCreateInstanceBtn();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        updateEditorTitle(getCurrentCodeFromView(), false);
                        removeCreateInstanceBtn();
                    }
                });

        tRep.getEditor().getEditor().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                //throw new UnsupportedOperationException("Not supported yet.");
                if (arg0.isShiftDown()
                        && arg0.getKeyCode() == KeyEvent.VK_ENTER) {

                    Thread th = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            compileAndAddToCanvas();
                        }
                    });

                    th.start();

                    arg0.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent arg0) {
//                if (!code.equals(getCurrentCodeFromView())) {
//                    removeCreateInstanceBtn();
//                }
            }
        });

        vObj.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                if (arg0.isShiftDown()
                        && arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                    Thread th = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            compileAndAddToCanvas();
                        }
                    });

                    th.start();

                    arg0.consume();
                }
            }
        });

        vObj.addActionListener(new CanvasActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(CanvasWindow.ACTIVE_ACTION)) {
                    tRep.getEditor().getEditor().requestFocus();
                }
                if (e.getActionCommand().equals(CanvasWindow.MAXIMIZE_ACTION)) {
                    tRep.getEditor().getEditor().requestFocus();
                }
            }
        });
    }

    private void checkIfEditorRepresentsNewestVersion() {

        updateMrep();

        if (mRep == null && className != null && !className.trim().isEmpty()) {
            return;
        }

        String currentCode = getCurrentCodeFromView();

        boolean isEqual = isCodeInProjectFileEqualToCode(currentCode);

        if (isEqual) {
            addCreateInstanceBtn();

        } else {
            removeCreateInstanceBtn();
        }

        if (vObj != null) {
            updateEditorTitle(getCurrentCodeFromView(), isEqual);
        }
    }

    private void compileAndAddToCanvas() {
        mRep = vObj.getObjectRepresentation().
                getMethodBySignature("compile",
                MethodRequest.class, String.class);
        try {
            mRep.invokeAsCallParentNoNewThread();

            if (clazz == null) {
                return;
            }

//            Rectangle bounds = vObj.getBounds();
//            VisualCanvas canvas = (VisualCanvas) mRep.getMainCanvas();

//            VSwingUtil.invokeLater(new Runnable() {
//
//                @Override
//                public void run() {
//                    vObj.close();
//                }
//            });

        } catch (InvocationTargetException ex) {
            Logger.getLogger(GroovyCodeEditorComponent.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    private Collection<String> getRemovedInnerClasses(String code, File classFile) {
        Collection<String> classNamesFromCode =
                VLangUtils.getClassAndInterfaceNamesFromCode(code);

        Collection<String> classNamesFromClassFile = new ArrayList<String>();
        Collection<String> missingClasses = new ArrayList<String>();

        if (classFile.exists()) {

            try {
                classNamesFromClassFile =
                        ByteCodeUtil.getClassNames(classFile);
            } catch (IOException ex) {
                Logger.getLogger(GroovyCodeEditorComponent.class.getName()).
                        log(Level.SEVERE, null, ex);
            }

            for (String name : classNamesFromClassFile) {
                if (!classNamesFromCode.contains(name)) {
                    missingClasses.add(name);
                }
            }
        }

        return missingClasses;
    }

    private boolean validateMissingDependencies(
            String code, File classFile, VProjectController projectController) {

        Collection<String> missingDependencies =
                getRemovedInnerClasses(code, classFile);

        Collection<String> usedClasses = new ArrayList<String>();

        for (ClassFileDependency clsFileDep : projectController.getNamesOfUsedClasses()) {
            usedClasses.addAll(clsFileDep.getDependencies());
        }

        Collection<String> intersection = new ArrayList<String>();

        System.out.println(
                ">> Analyzing (search removed classes) " + classFile);

        for (String missing : missingDependencies) {
            System.out.print(" --> Removed Class: " + missing);
            if (usedClasses.contains(missing)) {
                System.out.println("   [USED]");
                intersection.add(missing);
            } else {
                System.out.println("   [UNUSED]");
            }
        }

        boolean invalidDependencies = !intersection.isEmpty();

        if (invalidDependencies) {

            String missingDepsMsgString = "<ul>";

            for (String mD : intersection) {
                missingDepsMsgString += "<li>" + mD.replace("$", ".") + "</li>";
            }

            missingDepsMsgString += "</ul>";

            projectController.getCurrentCanvas().getMessageBox().addMessage(
                    "Cannot Compile:",
                    "You have removed inner classes that are used by other"
                    + " classes of this project."
                    + "<br><br>"
                    + "<b>Missing dependencies:</b><br>"
                    + missingDepsMsgString
                    + "<br>"
                    + "<b>Solution:</b> either remove the dependencies or"
                    + " add the classes to this code again.", MessageType.ERROR);
        }

        return invalidDependencies;
    }

    private boolean checkIfCodeHasErrors(VCodeEditor editor, String code) {

        boolean errors = false;

        if (!VLangUtils.classDefinedInCode(code)
                && !VLangUtils.interfaceDefinedInCode(code)) {
            editor.addCodeErrorMessage(1,
                    "Class/Interface definition missing!");
            errors = true;
        }

        if (VLangUtils.numberOfTopLevelClassesAndInterfaces(code) > 1) {
            editor.addCodeErrorMessage(1,
                    "More than one toplevel class/interface in code! "
                    + "Only one toplevel class/interface per code file is "
                    + "supported.");
            errors = true;
        }

        String filteredCode =
                VLangUtils.removeCommentsAndStringsFromCode(code);

        // check whether classname contains unicode characters not matched 
        // by [a-z] or [A-Z]


        String fullClassName = VLangUtils.fullClassNameFromCode(code);

        if (fullClassName == null || fullClassName.isEmpty()) {
            fullClassName = VLangUtils.fullInterfaceNameFromCode(code);
        }

        // check whether class names contain '$' sign
        boolean containsDollarSign = fullClassName.contains("$");

        if (!containsDollarSign) {

            Pattern pCls = Patterns.CLASS_DEFINITION;
            Pattern pInt = Patterns.INTERFACE_DEFINITION;

            Matcher clsMatch = pCls.matcher(filteredCode);
            Matcher intMatch = pInt.matcher(filteredCode);

            while (clsMatch.find()) {
                String grp = clsMatch.group();
                if (grp.contains("$")) {
                    containsDollarSign = true;
                    break;
                }
            }

            while (intMatch.find()) {
                String grp = intMatch.group();
                if (grp.contains("$")) {
                    containsDollarSign = true;
                    break;
                }
            }
        }

        if (containsDollarSign) {
            editor.addCodeErrorMessage(1,
                    "VRL class and interface names "
                    + "must not contain '<b>$</b>' sign!");
            errors = true;
        }



        // check whether we try to overwrite existing class
        if (canvas.getProjectController().getProject().
                getSessionFileByEntryName(fullClassName).exists()) {
            editor.addCodeErrorMessage(1,
                    "Class "
                    + Message.EMPHASIZE_BEGIN
                    + fullClassName
                    + Message.EMPHASIZE_END
                    + " already exists (visual component)!");
            errors = true;
        }

        // check whether we try to overwrite project main class
        if (fullClassName.equals("eu.mihosoft.vrl.user.VSessionMainClass")) {
            editor.addCodeErrorMessage(1,
                    "Class "
                    + Message.EMPHASIZE_BEGIN
                    + fullClassName
                    + Message.EMPHASIZE_END
                    + " already exists (internal project class)!");
            errors = true;
        }

        return errors;
    }

    @MethodInfo(name = "compile", hide = false, buttonText = "Compile",
    askIfClose = true)
    public void compile(
            MethodRequest mReq,
            @ParamInfo(name = " ", style = "code") String compileCode) {
        mRep = mReq.getMethod();

        InputCodeType tRep = (InputCodeType) mRep.getParameter(1);
        VCodeEditor editor = tRep.getEditor();

        // we automatically add apackage definition if missing
        if (!VLangUtils.packageDefinedInCode(compileCode)) {
            compileCode = "package eu.mihosoft.vrl.user;" + compileCode;
        }

        compileErrorsOrCancelled = checkIfCodeHasErrors(editor, compileCode);

        String fullClassName = VLangUtils.fullClassNameFromCode(compileCode);
        String simpleClassName = VLangUtils.classNameFromCode(compileCode);

        if (simpleClassName == null || simpleClassName.isEmpty()) {
            simpleClassName = VLangUtils.interfaceNameFromCode(compileCode);
        }

        File classFile = canvas.getProjectController().
                getProject().getClassFileByEntryName(fullClassName);

        if (!compileErrorsOrCancelled) {
            compileErrorsOrCancelled = validateMissingDependencies(
                    compileCode, classFile, canvas.getProjectController());
        }

        boolean classFileExists = classFile.exists();

        String currentCode = null;

        if (classFileExists) {

            TextLoader loader = new TextLoader();

            File codeFile = new File(classFile.getAbsolutePath().substring(0,
                    classFile.getAbsolutePath().
                    lastIndexOf(".class")) + ".groovy");
            try {
                currentCode = (String) loader.loadFile(codeFile);
            } catch (IOException ex) {
                Logger.getLogger(GroovyCodeEditorComponent.class.getName()).
                        log(Level.SEVERE, null, ex);
            }

            if (currentCode == null) {
                throw new IllegalStateException(
                        "Code file missing: " + codeFile);
            }
        }

        String packageName = VLangUtils.packageNameFromCode(compileCode);

        VCompiler compiler = null;

        try {
            compiler = canvas.getCompilerProvider().getCompiler(
                    CompilerProvider.LANG_GROOVY);

        } catch (CompilerNotFoundException ex) {
            Logger.getLogger(GroovyCodeEditorComponent.class.getName()).
                    log(Level.SEVERE, null, ex);

            return;
        }

        File baseFolder = canvas.getProjectController().
                getProject().getContentLocation();

        File packageFolder = new File(baseFolder, VLangUtils.dotToSlash(
                packageName));

        packageFolder.mkdirs();

        File srcFile = new File(packageFolder,
                simpleClassName + ".groovy");

        Class<?> cls = null;

        if (!compileErrorsOrCancelled) {
            try {
                cls = compiler.compile(compileCode, editor);
            } catch (Exception ex) {
                Logger.getLogger(GroovyCodeEditorComponent.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

        String imports = "// <vrl-editor-autogen type=imports>\n";
        if (compiler instanceof GroovyCompiler) {
            GroovyCompiler gc = (GroovyCompiler) compiler;
            for (String importStr : gc.getImports()) {
                imports += importStr;
            }
        }
        imports += "\n// </vrl-editor-autogen>";


        Matcher m1 = Patterns.PACKAGE_DEFINITION.matcher(compileCode);

        String codeHeader = imports;

        if (m1.find()) {
            int pos = m1.end();
            String codePkgDef = compileCode.substring(0, pos);
            String codeAfterPkgDef = compileCode.substring(pos);
            if (!codePkgDef.endsWith("\n")) {
                codePkgDef += "\n";
            }
            if (!codeAfterPkgDef.startsWith("\n")) {
                codeAfterPkgDef = "\n" + codeAfterPkgDef;
            }
            compileCode = codePkgDef + codeHeader + codeAfterPkgDef;
        } else {
            compileCode = codeHeader + compileCode;
        }

        if (removeEmptyLastLinesFromCode(compileCode).
                equals(removeEmptyLastLinesFromCode(currentCode))) { // nothing to do if code is identical

            Message m = canvas.getMessageBox().addMessage("Code not changed:",
                    ">> implementation of class " + Message.EMPHASIZE_BEGIN
                    + cls.getSimpleName()
                    + Message.EMPHASIZE_END
                    + " has not been changed and won't be compiled.",
                    MessageType.INFO);

            canvas.getMessageBox().messageRead(m);
            clazz = cls;

            updateCodeEditorsOfSameClass();
            checkIfEditorRepresentsNewestVersion();

            code = compileCode;

        } else if (cls != null && !compileErrorsOrCancelled) { // save code and compile

            if (!ComponentUtil.isComponent(cls)) {
                editor.addCodeErrorMessage(1, "Component definition missing!");
                compileErrorsOrCancelled = true;
                return;
            }

            // 28.12.2011
            // TODO is it ok that we use the cls class object that has
            // groovy classloader?
            //
            // current solution:
            //
            // we rerun replaceVisualComponents() later if compilation via
            // ant script is performed, see (1)

            if (!replaceVisualComponents(canvas, cls)) {
                compileErrorsOrCancelled = true;
                return;
            }

            canvas.setActive(false);

            clazz = cls;

            Class oldCls =
                    canvas.getClassLoader().getClasses().get(cls.getName());
            if (oldCls != null) {

                canvas.getComponentController().removeComponent(oldCls);
                canvas.getClassLoader().getClasses().remove(oldCls.getName());
            }

            TextSaver ts = new TextSaver();

            try {

                boolean srcExists = srcFile.exists();

                ts.saveFile(compileCode, srcFile, ".groovy");

                // remove old classfiles (no clean necessary)
                canvas.getProjectController().removeInnerClassFilesOf(
                        cls.getName(), true);

                // try to build changed code
                if (canvas.getProjectController().build()) {

                    System.out.println(
                            ">> changes are valid. compiled successfully.");

                    // remove existing type representations
                    canvas.getTypeFactory().
                            removeTypeByClassName(cls.getName());

                    // close version management window if opened as we add
                    // new version 
                    // (live update of version list is currently not supported)
                    VersionManagement.closeDialog(canvas);

                    String nameForMsg = canvas.getProjectController().
                            getProject().
                            getEntryNameWithoutDefaultPackage(
                            fullClassName).replace("/", ".");

                    // commit changes
                    if (!srcExists) {
                        canvas.getProjectController().getProject().
                                getProjectFile().commit(
                                "class/interface "
                                + nameForMsg
                                + " created");
                    } else {
                        canvas.getProjectController().getProject().
                                getProjectFile().commit(
                                "class/interface "
                                + nameForMsg
                                + " changed");
                    }

                    // (1)
                    try {

                        if (!canvas.getInspector().
                                getObjectsByClassName(
                                cls.getName()).isEmpty()) {
                            System.out.println(
                                    " --> replace: replacing visual instances");
                            replaceVisualComponents(canvas,
                                    canvas.getClassLoader().
                                    getClasses().get(cls.getName()));

                        } else {
                            System.out.println(
                                    " --> replace: no visual instances to replace");
                        }

                        replaceInstancesThatDependOn(cls);

                    } catch (Exception ex) {
                        Message m = canvas.getMessageBox().addMessage(
                                "Error while replacing visual instances:",
                                ">> could not replace visual instances of "
                                + "class " + Message.EMPHASIZE_BEGIN
                                + cls.getSimpleName()
                                + Message.EMPHASIZE_END + "! "
                                + "Save the session and load it again.",
                                MessageType.ERROR);

                        Logger.getLogger(GroovyCodeEditorComponent.class.getName()).
                                log(Level.SEVERE, null, ex);
                    }

//                    Message m = canvas.getMessageBox().addMessage(
//                            "Compilation successful:",
//                            ">> class/interface " + Message.EMPHASIZE_BEGIN
//                            + cls.getSimpleName()
//                            + Message.EMPHASIZE_END + " successfully compiled.",
//                            MessageType.INFO);
//
//                    canvas.getMessageBox().messageRead(m);

                } else {
                    // revert changes
                    ts.saveFile(currentCode, srcFile, ".groovy");

                    System.out.println(
                            ">> changes are invalid. compilation failed."
                            + " reverting changes.");

                    boolean revertSuccessful =
                            canvas.getProjectController().build();

                    if (!revertSuccessful) {
                        Message m = canvas.getMessageBox().addMessage(
                                "Error while reverting code changes:",
                                ">> The project contains changes that produced "
                                + "non-functional "
                                + "code. These changes could not be reverted."
                                + "<br><br>"
                                + "Please revert the project to a previous version."
                                + "<br><br>"
                                + "This is a serious bug! "
                                + "Please " + Constants.WRITE_VRL_BUG_REPORT
                                + " that contains a detailed description of "
                                + "what you were doing until this message "
                                + "appeared.",
                                MessageType.ERROR);
                    }
                }

            } catch (IOException ex) {
                Logger.getLogger(GroovyCodeEditorComponent.class.getName()).
                        log(Level.SEVERE, null, ex);
            }

            canvas.setActive(true);
        }

        code = compileCode;

        updateCodeEditorsOfSameClass();
        checkIfEditorRepresentsNewestVersion();
    }

    private void updateCodeEditorsOfSameClass() {

        if (clazz == null) {
            return;
        }

        Collection<Object> editorInstances =
                canvas.getInspector().getObjectsByClassName(
                GroovyCodeEditorComponent.class.getName());

        for (Object o : editorInstances) {

            if (o == this) {
                continue;
            }

            GroovyCodeEditorComponent eObj = (GroovyCodeEditorComponent) o;

            if (eObj.clazz != null
                    && eObj.clazz.getName().equals(clazz.getName())) {
                eObj.checkIfEditorRepresentsNewestVersion();
            }
        }
    }

    /**
     * Checks whether all code editors on the specified canvas show the
     * newest version of the code and updates their ui accordingly.
     * @param canvas canvas to update
     */
    @MethodInfo(noGUI=true)
    public static void updateAllCodeEditorsOnCanvas(VisualCanvas canvas) {
        Collection<Object> editorInstances =
                canvas.getInspector().getObjectsByClassName(
                GroovyCodeEditorComponent.class.getName());

        for (Object o : editorInstances) {

            GroovyCodeEditorComponent eObj = (GroovyCodeEditorComponent) o;

            eObj.checkIfEditorRepresentsNewestVersion();

        }
    }

    private void addCreateInstanceBtn() {

        updateMrep();

        if (clazz == null) {
            String fullClassName =
                    getFullClassNameFromCode(getCurrentCodeFromView());

            if (fullClassName != null) {
                try {
                    clazz = canvas.getClassLoader().loadClass(fullClassName);
                } catch (ClassNotFoundException ex) {
                    // if no class available do nothing
                }
            }
        }

        if (mRep == null || clazz == null || clazz.isInterface() || compileErrorsOrCancelled) {
            return;
        }

        ArrayList<Component> btns =
                VSwingUtil.getAllChildren(mRep.getInvokeButtonContainer(), VButton.class);

        for (Component btn : btns) {
            btn.setVisible(false);
        }

        if (createInstanceBtn == null) {
            createInstanceBtn = new VButton("Create Instance");
            createInstanceBtn.setAlignmentX(0.5f);
            mRep.getInvokeButtonContainer().add(createInstanceBtn);

            createInstanceBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Class<?> cls = canvas.getClassLoader().reloadClass(clazz);

                    ComponentUtil.addObject(cls, canvas,
                            vObj.getLocation(), true);
                }
            });
        }

        createInstanceBtn.setVisible(true);
    }

    private void removeCreateInstanceBtn() {

        if (mRep == null) {
            return;
        }

        ArrayList<Component> btns =
                VSwingUtil.getAllChildren(mRep.getInvokeButtonContainer(), VButton.class);

        for (Component btn : btns) {
            btn.setVisible(true);
        }

        if (createInstanceBtn != null) {
            createInstanceBtn.setVisible(false);
        }

    }

    private void replaceInstancesThatDependOn(Class<?> cls)
            throws InterfaceChangedException {

        // 08.06.2012
        //TODO shall we use source code analysis to find static calls?
        // in Groovy static calls are not visible in byte code

        System.out.println(">> replacing instances on canvas that use: " + cls.getName());

        InterfaceChangedException exception1 = null;

        // replace all instances of classes that use cls
        for (String classThatUses : canvas.getProjectController().
                getNamesOfClassesThatUse(cls.getName())) {

            System.out.println(" -->: replacing instances of: " + classThatUses);

            Class<?> classToChange = null;

            try {
                classToChange = canvas.getClassLoader().
                        loadClass(classThatUses);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace(System.err);
                continue;
            }

            try {
                canvas.getInspector().replaceAllObjects(classToChange,
                        new InstanceCreator(canvas));
            } catch (InterfaceChangedException ex) {
                exception1 = ex;
            }
        }

        if (exception1 != null) {
            throw exception1;
        }

    }

    private boolean replaceVisualComponents(final VisualCanvas canvas,
            final Class<?> cls) {
        ArrayList<Object> oldInstances;

        // can not visualize interfaces
        if (cls.isInterface()) {
            // notification?
            return true;
        }
        // can not visualize abstract classes
        if (Modifier.isAbstract(cls.getModifiers())) {
            // notification?
            return true;
        }


        // try to replace instances
        try {
            oldInstances =
                    canvas.getInspector().replaceAllObjects(cls,
                    new InstanceCreator(canvas)).getFirst();

        } catch (InterfaceChangedException ex) {

            if (VDialog.showConfirmDialog(canvas,
                    "Remove visual instances?",
                    "<html><div align=left>"
                    + "<p>Interface of the component "
                    + Message.EMPHASIZE_BEGIN
                    + canvas.getProjectController().getProject().
                    getEntryNameWithoutDefaultPackage(
                    cls.getName()) + Message.EMPHASIZE_END + ""
                    + " has changed.</p>"
                    + "<br>"
                    + "<p>If this component shall be"
                    + " compiled all visual instances will be"
                    + " removed<!-- and the current session will be saved-->.</p>"
                    + "<br>"
                    + "<p>Do you want to compile the component?</p>"
                    + "</div></html>",
                    VDialog.DialogType.YES_NO)
                    == VDialog.AnswerType.YES) {

                Collection<Object> instances = canvas.getInspector().
                        getObjectsByClassName(cls.getName());

                // try to auto-readd components after they have been removed
                // due to interface changed
//                class InstanceEntry {
//                    private Class<?> cls;
//                    private int x;
//                    private int y;
//
//                    public InstanceEntry(Class<?> cls, int x, int y) {
//                        this.cls = cls;
//                        this.x = x;
//                        this.y = y;
//                    }
//
//                    /**
//                     * @return the cls
//                     */
//                    public Class<?> getCls() {
//                        return cls;
//                    }
//
//                    /**
//                     * @return the x
//                     */
//                    public int getX() {
//                        return x;
//                    }
//
//                    /**
//                     * @return the y
//                     */
//                    public int getY() {
//                        return y;
//                    }
// 
//                }

                for (Object o : instances) {
                    // convert from inspector id to window id
                    Collection<Integer> windowIDs =
                            canvas.getInspector().
                            getCanvasWindowIDs(o);

                    for (Integer winID : windowIDs) {
                        if (winID != null) {
                            canvas.getWindows().
                                    removeObject(winID);
                        }
                    }
                }

                replaceVisualComponents = true;
                return true;
            } else {
                replaceVisualComponents = false;
                return false;
            }
        }

        replaceVisualComponents = false;
        return true;
    }
}
