/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.lang.model;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public final class CodeReader implements ICodeReader {

    private final Reader reader;

    public CodeReader(Reader reader) {
        this.reader = reader;
    }

    @Override
    public int read(StringBuilder sb, ICodeRange range) throws IOException {

        if (reader.markSupported()) {
            reader.mark(Integer.MAX_VALUE);
        }

        char[] cbuf = new char[range.size()];

        int result = -1;

        IOException exception = null;
        
        try {
            reader.skip(range.getBegin().getCharIndex());

            result = reader.read(cbuf, 0, range.size());
        } catch (IOException ex) {
            exception = ex;
        } finally {
            reader.reset();
        }

        if (exception != null) {
            throw exception;
        }

        sb.append(cbuf);

        return result;
    }

    @Override
    public String read(ICodeRange range) throws IOException {

        if (reader.markSupported()) {
            reader.mark(Integer.MAX_VALUE);
        }

        char[] cbuf = new char[range.size()];

        IOException exception = null;

        try {
            reader.skip(range.getBegin().getCharIndex());
            int result = reader.read(cbuf, 0, range.size());
        } catch (IOException ex) {
            exception = ex;
        } finally {
            reader.reset();
        }

        if (exception != null) {
            throw exception;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(cbuf);

        return sb.toString();
    }

    @Override
    public boolean canRead() throws IOException {
        return reader.ready();
    }

}
