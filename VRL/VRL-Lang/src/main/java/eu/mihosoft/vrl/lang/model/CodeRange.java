/* 
 * CodeRange.java
 *
 * Copyright (c) 2009–2014 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2014 by Michael Hoffer
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
 * First, the following text must be displayed on the Canvas or an equivalent location:
 * "based on VRL source code".
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
 * Computing and Visualization in Science, in press.
 */

package eu.mihosoft.vrl.lang.model;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public final class CodeRange implements ICodeRange {

    private final ICodeLocation begin;
    private final ICodeLocation end;
    private Reader source;

    public CodeRange(ICodeRange other) {
        this.begin = other.getBegin();
        this.end = other.getEnd();
        this.source = other.getSource();
    }

    public CodeRange(ICodeLocation begin, Reader code) {
        this.begin = begin;

        ICodeLocation result = new CodeLocation(-1, code);

        try {
//            if (code.markSupported()) {
//                code.mark(Integer.MAX_VALUE);
//            }
            LineNumberReader lR = new LineNumberReader(code);
            String line;
            int numChars = 0;
            while ((line = lR.readLine()) != null) {
                numChars += line.length() + 1; // TODO: newline on windows is two;
            }
            result = new CodeLocation(numChars-1, code);
        } catch (IOException ex) {
            Logger.getLogger(CodeRange.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                code.reset();
            } catch (IOException ex) {
                Logger.getLogger(CodeRange.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        this.end = result;

    }
    
    @Override
    public String toString() {
        return "[begin: " + this.begin + ", end: " + this.end + ")";
    }

    /**
     * Constructor.
     *
     * @param begin beginning index, inclusive
     * @param end ending index, exclusive
     */
    public CodeRange(ICodeLocation begin, ICodeLocation end) {
        this.begin = begin;
        this.end = end;
    }

    public CodeRange(int begin, int end) {
        this.begin = new CodeLocation(begin);
        this.end = new CodeLocation(end);
    }

    public CodeRange(int begin, int end, Reader code) {
        this.begin = new CodeLocation(begin, code);
        this.end = new CodeLocation(end, code);
        this.setSource(code);
    }

    public CodeRange(int lineBegin, int columnBegin, int lineEnd, int columnEnd, Reader code) {
        this.begin = new CodeLocation(lineBegin, columnBegin, code);
        this.end = new CodeLocation(lineEnd, columnEnd, code);
        this.setSource(code);
    }

    @Override
    public ICodeLocation getBegin() {
        return this.begin;
    }

    @Override
    public ICodeLocation getEnd() {
        return this.end;
    }

    @Override
    public int compareTo(ICodeRange o) {
        return this.getBegin().compareTo(o.getBegin());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CodeRange other = (CodeRange) obj;
        if (!Objects.equals(this.begin, other.begin)) {
            return false;
        }
        if (!Objects.equals(this.end, other.end)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.begin);
        hash = 29 * hash + Objects.hashCode(this.end);
        return hash;
    }

    @Override
    public boolean contains(ICodeRange o) {

        boolean weContainTheBeginningIndex = this.getBegin().compareTo(o.getBegin()) < 1;
        boolean weContainTheEndingIndex = this.getEnd().compareTo(o.getEnd()) > -1;

        return weContainTheBeginningIndex && weContainTheEndingIndex;
    }

    @Override
    public boolean contains(ICodeLocation o) {
        boolean weContainTheBeginningIndex = this.getBegin().compareTo(o) < 1;
        boolean weContainTheEndingIndex = this.getEnd().compareTo(o) > -1;

        return weContainTheBeginningIndex && weContainTheEndingIndex;
    }

    @Override
    public ICodeRange intersection(ICodeRange o) {
        return new CodeRange(Math.max(this.getBegin().getCharIndex(), o.getBegin().getCharIndex()),
                Math.min(this.getEnd().getCharIndex(), o.getEnd().getCharIndex()), getSource());
    }

    @Override
    public int size() {
        return getEnd().getCharIndex() - getBegin().getCharIndex();
    }

    @Override
    public boolean isEmpty() {
        return size() <= 0;
    }

    @Override
    public void setSource(Reader r) {
        this.begin.setSource(r);
        this.end.setSource(r);
        this.source = r;
    }

    @Override
    public Reader getSource() {
        return this.source;
    }

}
