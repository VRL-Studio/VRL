/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class CodeLocation implements ICodeLocation {

    private int index = -1;
    private int line = -1;
    private int column = -1;
    private Reader codeReader = null;

    public CodeLocation(int index) {
        this.index = index;
    }

    public CodeLocation(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public CodeLocation(int index, Reader codeReader) {
        this.index = index;
        this.codeReader = codeReader;
    }

    public CodeLocation(int line, int column, Reader codeReader) {
        this.line = line;
        this.column = column;
        this.codeReader = codeReader;
    }

    private void computeIndex() {
        // do nothing if already valid
        if (isIndexValid()) {
            return;
        }

        if (!isConvertible()) {
            throw new RuntimeException("No code specified: cannot compute line and column");
        }

        // l,c -> index
        ConvertingLineCodeReader lR = new ConvertingLineCodeReader(codeReader);
        this.index = lR.lineColumnToCharIndex(line, column);

        System.out.println("INDEX: " + index);
    }

    private void computeLineAndColumn() {

        // do nothing if already valid
        if (isLineValid() && isColumnValid()) {
            return;
        }

        if (!isConvertible()) {
            throw new RuntimeException("No code specified: cannot compute line and column");
        }

        // index -> l,c 
        ConvertingLineCodeReader lR = new ConvertingLineCodeReader(codeReader);
        int[] pos = lR.charIndexToLineColumn(index);

        this.line = pos[0];
        this.column = pos[1];
    }

    @Override
    public int getLine() {

        computeLineAndColumn();

        return line;
    }

    @Override
    public int getColumn() {

        computeLineAndColumn();

        return column;
    }

    @Override
    public int getCharIndex() {

        computeIndex();

        return index;
    }

    @Override
    public int compareTo(ICodeLocation o) {

        if (isIndexValid() || o.isIndexValid()) {

            try {
                return compareValues(getCharIndex(), o.getCharIndex());
            } catch (RuntimeException ex) {
                // index computation failed
            }
        }

        if ((isLineValid() && isColumnValid()) && (o.isLineValid() && o.isColumnValid())) {
            if (line == o.getLine()) {
                return compareValues(column, o.getColumn());
            } else {
                return compareValues(line, o.getLine());
            }
        }

        throw new RuntimeException("Cannot compare locations!");
    }

    private int compareValues(int first, int second) {
        if (first == second) {
            return 0;
        } else if (first > second) {
            return 1;
        } else {
            return -1;
        }
    }

//    @Override
//    public Reader getCode() {
//        return codeReader;
//    }
    @Override
    public boolean isLineValid() {
        return this.line >= 0;
    }

    @Override
    public boolean isColumnValid() {
        return this.column >= 0;
    }

    @Override
    public boolean isIndexValid() {
        return this.index >= 0;
    }

    @Override
    public boolean isConvertible() {
        return codeReader != null;
    }

    private void syncValues() {
        if (isConvertible()) {
            computeIndex();
            computeLineAndColumn();
        }
    }

    @Override
    public int hashCode() {

        syncValues();

        int hash = 3;
        hash = 67 * hash + this.index;
        hash = 67 * hash + this.line;
        hash = 67 * hash + this.column;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        syncValues();

        final CodeLocation other = (CodeLocation) obj;
        if (this.index != other.index) {
            return false;
        }
        if (this.line != other.line) {
            return false;
        }
        if (this.column != other.column) {
            return false;
        }
        return true;
    }

    @Override
    public void setSource(Reader source) {
        this.codeReader = source;
    }

}

class ConvertingLineCodeReader {

    private final Reader r;

    public ConvertingLineCodeReader(Reader r) {
        this.r = r;

        if (r.markSupported()) {
            try {
                r.mark(Integer.MAX_VALUE);
            } catch (IOException ex) {
                Logger.getLogger(ConvertingLineCodeReader.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }
    }

    public int lineColumnToCharIndex(int line, int column) {
        int chars = 0;

        if (line < 0 || column < 0) {
            return -1;
        }

        LineNumberReader lR = new LineNumberReader(r);
        try {
            for (int i = 0; i <= line; i++) {

                System.out.println("reading line: " + i);

                String l = lR.readLine();

                if (l == null) {
                    throw new IndexOutOfBoundsException(
                            "number of lines is less than line pos '"
                            + line
                            + "'");
                }

                int numChars = l.length() + 1; // TODO: newline on windows is two

                if (i < line) {
                    chars += numChars;
                } else {

                    if (column >= numChars) {
                        throw new IndexOutOfBoundsException(
                                "line contains only "
                                + numChars
                                + " chars, requested index "
                                + column + " is invalid");
                    }

                    chars += column;
                }

            }
        } catch (IOException | IndexOutOfBoundsException ex) {
            Logger.getLogger(ConvertingLineCodeReader.class.getName()).log(Level.SEVERE, null, ex);
            chars = -1;
        } finally {
            try {
                r.reset();
            } catch (IOException ex) {
                Logger.getLogger(ConvertingLineCodeReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return chars;
    }

    public int[] charIndexToLineColumn(int charIndex) {

        LineNumberReader lR = new LineNumberReader(r);

        try {

            int pos = 0;
            int numChars = 0;

            while (pos <= charIndex) {
                String l = lR.readLine();

                if (l == null) {
                    throw new IndexOutOfBoundsException(
                            "number of lines is less than requested char pos '"
                            + charIndex
                            + "'");
                }

                numChars = l.length() + 1; // TODO: newline on windows is two
                pos += numChars;
            }

            int line = lR.getLineNumber() - 1;

            int lastPos = pos - numChars;
            int diffBetweenLastPosAndRequestedCharIndex = charIndex - lastPos;

            System.out.println("pos: " + pos);
            System.out.println("lastPos: " + lastPos);
            System.out.println("diff:    " + diffBetweenLastPosAndRequestedCharIndex);

            int column = diffBetweenLastPosAndRequestedCharIndex;

            System.out.println("ci:" + charIndex + " -> (l: " + line + ", c: " + column + ")");

            return new int[]{line, column};

        } catch (IOException | IndexOutOfBoundsException ex) {
            Logger.getLogger(ConvertingLineCodeReader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                r.reset();
            } catch (IOException ex) {
                Logger.getLogger(ConvertingLineCodeReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return new int[]{-1, -1};
    }
}
