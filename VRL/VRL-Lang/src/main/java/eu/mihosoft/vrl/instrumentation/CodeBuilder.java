/* 
 * CodeBuilder.java
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

package eu.mihosoft.vrl.instrumentation;

import java.util.ArrayList;

/**
 * A simple code builder class. Its purpose is to allow easy code generation and
 * adjustable indentation depth.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class CodeBuilder {

    private String indentString = "    ";
    private StringBuilder currentLine = new StringBuilder();
    private int cursorPos = 0;
    private ArrayList<Line> code = new ArrayList<>();

    /**
     * Returns the indentation string which consists of whitespace characters.
     * The number of whitespaces is defined via {@link #setIndentDepth(int) }.
     * @return the indentation string
     */
    public String getIndentString() {
        return indentString;
    }

    /**
     * Defines indentation depth.
     * @param depth depth
     */
    private void setIndentDepth(int depth) {
        String result = "";

        for (int i = 0; i < depth; i++) {
            result += " ";
        }

        indentString = result;
    }

    /**
     * Decreases indentation.
     * @return this code builder
     */
    public CodeBuilder decIndentation() {
        if (cursorPos > 0) {
            cursorPos--;
        }

        return this;
    }

    /**
     * Increase indentation.
     * @return this code builder
     */
    public CodeBuilder incIndentation() {
        cursorPos++;

        return this;
    }

    /**
     * Returns indentation.
     * @return indentation string
     */
    private String getIndentation(int pos) {
        String result = "";
        for (int i = 0; i < pos; i++) {
            result += getIndentString();
        }
        return result;
    }

    /**
     * Adds a string to this code builder.
     * @param s string to add
     * @return this code builder
     */
    public CodeBuilder append(String s) {
        currentLine.append(s);
        return this;
    }

    /**
     * Inserts a line break.
     * @param indentCount indentation count
     * @return this code builder
     */
    public CodeBuilder newLine(int indentCount) {

        code.add(new Line(currentLine.toString(), indentCount));
        currentLine = new StringBuilder();

        return this;
    }

    /**
     * Inserts a line break.
     * @return this code builder
     */
    public CodeBuilder newLine() {
        return newLine(cursorPos);
    }

    /**
     * Adds one line of code to this code builder.
     * @param line code to add
     * @param indentCount indentation count
     * @return this code builder
     */
    public CodeBuilder addLine(String line, int indentCount) {

        if (currentLine.length() > 0) {
            code.add(new Line(currentLine.toString(), indentCount));
            currentLine = new StringBuilder();
        }

        code.add(new Line(line, indentCount));

        return this;
    }

    /**
     * Adds a line of code to this code builder.
     * @param line code to add
     * @return this code builder
     */
    public CodeBuilder addLine(String line) {
        return addLine(line, cursorPos);
    }

    /**
     * Clears this code builder (removes all lines).
     * @return this code builder
     */
    public CodeBuilder clear() {
        code.clear();

        return this;
    }

    /**
     * Returns the code defined by this code builder.
     * @param indentDepth indentation depth
     * @return the code defined by this code builder as String
     */
    public String getCode(int indentDepth) {

        // flush the current line
        if (currentLine.length() > 0) {
            newLine(indentDepth);
        }

        setIndentDepth(indentDepth);

        StringBuilder result = new StringBuilder();

        for (Line line : code) {
            result.append(getIndentation(line.getIndentCount())).append(
                    line.getLine()).append("\n");
        }

        return result.toString();
    }

    /**
     * Returns the code defined by this code builder with an indentation depth
     * of 4.
     * @return the code defined by this code builder as String
     */
    public String getCode() {
        return getCode(4);
    }

    /**
     *  Internal line class.
     */
    private static class Line {

        private final int indentCount;
        private final String line;

        public Line(String line, int indentCount) {
            this.indentCount = indentCount;
            this.line = line;
        }

        public Line(String line) {
            this.line = line;
            this.indentCount = 0;
        }

        public String getLine() {
            return line;
        }

        public int getIndentCount() {
            return indentCount;
        }
    }

    @Override
    public String toString() {
        return getCode();
    }
}
