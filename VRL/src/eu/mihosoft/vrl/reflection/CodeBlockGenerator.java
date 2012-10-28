/* 
 * CodeBlockGenerator.java
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

import eu.mihosoft.vrl.io.vrlx.AbstractCode;
import eu.mihosoft.vrl.lang.CompilerProvider;
import eu.mihosoft.vrl.lang.VCompiler;
import eu.mihosoft.vrl.visual.Selectable;
import eu.mihosoft.vrl.visual.MessageBox;
import eu.mihosoft.vrl.visual.MessageType;
import java.util.ArrayList;

/**
 * Generates a code block from methods that are selected in a VRL session.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class CodeBlockGenerator {

    private static String indentWidth = "    ";
    private int cursorPos = 0;

    /**
     * Decreases indentation.
     */
    private void decIndentation() {
        if (cursorPos > 0) {
            cursorPos--;
        }
    }

    /**
     * Increase indentation.
     */
    private void incIndentation() {
        cursorPos++;
    }

    /**
     * Returns indentation.
     * @return
     */
    private String getIndentation() {
        String result = "";
        for (int i = 0; i < cursorPos; i++) {
            result += indentWidth;
        }
        return result;
    }

    /**
     * Generates a code block from selected methods.
     * @param mainCanvas the canvas to use
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    public void generateCodeblock(VisualCanvas mainCanvas)
            throws InstantiationException, IllegalAccessException {
        ArrayList<Selectable> selectables = new ArrayList<Selectable>();
        ArrayList<DefaultMethodRepresentation> methods =
                new ArrayList<DefaultMethodRepresentation>();

        boolean onlyMethodsSelected = true;

        for (Selectable s : mainCanvas.getClipBoard()) {
            selectables.add(s);
            if (s instanceof DefaultMethodRepresentation) {
                DefaultMethodRepresentation mRep = (DefaultMethodRepresentation) s;
                methods.add(mRep);
            } else {
                onlyMethodsSelected = false;
            }
        }

        if (onlyMethodsSelected) {
            if (methods.size() > 0) {
                String codeString = generateCodeBlockCode(
                        generateObjectName(mainCanvas),
                        generateClassName(mainCanvas), methods);

                mainCanvas.addObjectAsCode(codeString,
                        CompilerProvider.LANG_GROOVY);

            } else {
                MessageBox mBox = mainCanvas.getMessageBox();
                mBox.addMessage("Can't create code block:",
                        ">> Please select at least one method.",
                        MessageType.ERROR);
            }
        } else {
            MessageBox mBox = mainCanvas.getMessageBox();
            mBox.addMessage("Can't create code block:",
                    ">> Code block generation only supports methods!"
                    + " Thus only methods may be selected.", MessageType.ERROR);

        }

        for (Selectable s : selectables) {
            mainCanvas.getClipBoard().unselect(s);
        }
    }

    /**
     * Generates a class name for the code block.
     * @param mainCanvas the main canvas
     * @return the class name
     */
    private String generateClassName(VisualCanvas mainCanvas) {
        String baseName = "CodeBlock";

        int i = 0;

        String result = baseName + i;

        boolean classNameExists = true;

        while (classNameExists) {
            AbstractCode c = mainCanvas.getCodes().getByName(result);
            if (c == null) {
                classNameExists = false;
            } else {
                i++;
                result = baseName + i;
            }
        }

        return result;
    }

    /**
     * Generates an object name (it's the same as the class name).
     * @param mainCanvas the main canvas
     * @return
     */
    private String generateObjectName(VisualCanvas mainCanvas) {
        String baseName = "CodeBlock";

        int i = 0;

        String result = baseName + i;

        boolean classNameExists = true;

        while (classNameExists) {
            AbstractCode c = mainCanvas.getCodes().getByName(result);
            if (c == null) {
                classNameExists = false;
            } else {
                i++;
                result = baseName + i;
            }
        }

        result = "Code Block " + i;

        return result;
    }

    /**
     * <p>
     * Generates correct class name codes, i.e., converts the class name of arrays
     * to the corresponding Java/Groovy code.
     * </p>
     * <p>
     * <b>Example:</b> The class name <code>[[[LInteger</code> will be converted
     * to <code>Integer[][][]</code>
     * </p>
     * @param className
     * @return the Java/Groovy code of the specified class name
     */
    public static String generateClassNameCode(String className) {
        String result = "";

        String arrayPrefix = "[";

        // check if class is an array
        if (!className.startsWith(arrayPrefix)) {
            result = className;
        } else {

            int dimensions = 0;

            // the "[" indicate the number of dimensions
            while (className.startsWith(arrayPrefix)) {
                dimensions++;
                arrayPrefix += "[";
            }

            // prefix looks like "[[[L" ,that is we have to remove the L too
            int classNameIndex = dimensions + 1;

            result = className.substring(classNameIndex,className.length()-1);

            for (int i = 0; i < dimensions; i++) {
                result += "[]";
            }
        }

        return result;
    }

    /**
     * Generates the method invoke code.
     * @param objectName the object name
     * @param mRep the method representation to invoke
     * @return the method invoke code
     */
    private String generateMethodInvokeCode(
            String objectName, DefaultMethodRepresentation mRep) {

        String methodName = mRep.getDescription().getMethodName();

        String result = "";
        result += "inspector.invokeFromGUI("
                + objectName + ", " + mRep.getParentObject().getID() + ", \""+ methodName + "\"";

        for (Class<?> t : mRep.getDescription().getParameterTypes()) {
            result += ", " + generateClassNameCode(t.getName());
        }

        result += ");\n";

        return result;
    }

    /**
     * Generates object reference variable.
     * @param objectName the object name
     * @param oRep the object representation
     * @return the object reference variable
     */
    private String generateObjectReference(
            String objectName, DefaultObjectRepresentation oRep) {
        String result = "";

        result += "Object " + objectName
                + " = inspector.getObject(" + oRep.getObjectID() + ");\n";

        return result;
    }

    /**
     * Generates the code block header.
     * @param objectName the object name
     * @param className the class name
     * @return the code block header code
     */
    private String generateCodeblockHeader(
            String objectName, String className) {
        String result = "";
        result += getIndentation()
                + "@ComponentInfo(name=\"" + objectName + "\")\n";
        result += getIndentation()
                + "@ObjectInfo(name=\"" + objectName + "\")\n";
        result += getIndentation() + "class " + className
                + " implements CodeBlock, Serializable {\n";
        incIndentation();
        result += getIndentation()
                + "private static final long serialVersionUID=1;\n\n";
        result += getIndentation()
                + "private transient VisualCanvas mainCanvas;\n";
        result += getIndentation()
                + "private transient VisualObjectInspector inspector;\n\n";
        decIndentation();
        return result;
    }

    /**
     * Generates the code block footer code.
     * @return the code block footer code
     */
    private String generateCodeBlockFooter() {
        return "}\n";
    }

    /**
     * Generates the call method.
     * @param methods the methods to call
     * @return the call method code
     */
    private String generateCallMethod(ArrayList<DefaultMethodRepresentation> methods) {
        String result = "";
        incIndentation();
        result += getIndentation() + "public void invoke() {\n";

        incIndentation();

        int i = 0;

        for (DefaultMethodRepresentation m : methods) {
            String objectName = generateObjectVarName(i);

            result += getIndentation()
                    + generateObjectReference(objectName, m.getParentObject());

            result += getIndentation()
                    + generateMethodInvokeCode(objectName, m);

            i++;
        }

        decIndentation();

        result += getIndentation() + "}\n";

        decIndentation();

        return result;
    }

    /**
     * Generates the object variable name.
     * @param i the variable index
     * @return the object variable name
     */
    private String generateObjectVarName(int i) {
        return "object" + i;
    }

    /**
     * Generates reference getter.
     * @return the reference getter code
     */
    private String generateReferenceCodeBlockMethodCode() {
        String result = "";
        incIndentation();
        result += getIndentation()
                + "@MethodInfo(hide=true)\n";
        result += getIndentation()
                + "public CodeBlock getReference(){\n";
        incIndentation();
        result += getIndentation()
                + "return this;\n";
        decIndentation();
        result += getIndentation()
                + "}\n\n";
        decIndentation();
        return result;
    }

    /**
     * Generates main canvas setter method.
     * @return the main canvas setter method code
     */
    private String generateSetMainCanvasCode() {
        String result = "";
        incIndentation();
        result += getIndentation()
                + "@MethodInfo(noGUI=true, callOptions=\"assign-canvas\")\n";
        result += getIndentation()
                + "public void setMainCanvas(VisualCanvas mainCanvas) {\n";
        incIndentation();
        result += getIndentation()
                + "this.mainCanvas = mainCanvas;\n";
        result += getIndentation()
                + "this.inspector = mainCanvas.getInspector();\n";
        decIndentation();
        result += getIndentation()
                + "}\n\n";
        decIndentation();

        return result;
    }

    /**
     * Generates the code block.
     * @param objectName the object name
     * @param className the class name
     * @param methods the methods to invoke
     * @return the code block code
     */
    private String generateCodeBlockCode(
            String objectName, String className,
            ArrayList<DefaultMethodRepresentation> methods) {
        String result = generateCodeblockHeader(objectName, className);

        result += generateSetMainCanvasCode();
        result += generateReferenceCodeBlockMethodCode();
        result += generateCallMethod(methods);
        result += generateCodeBlockFooter();

        return result;
    }
}
