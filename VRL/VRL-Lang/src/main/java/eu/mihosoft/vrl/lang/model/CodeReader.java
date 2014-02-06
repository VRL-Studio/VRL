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
        char[] cbuf = new char[range.size()];

        reader.skip(range.getBegin().getCharIndex());

        int result = reader.read(cbuf, 0, range.size());

        for (char c : cbuf) {
            System.out.println("cb: " + c);
        }

        sb.append(cbuf);

        return result;
    }

    @Override
    public String read(ICodeRange range) throws IOException {
        char[] cbuf = new char[range.size()];

        reader.skip(range.getBegin().getCharIndex());

        int result = reader.read(cbuf, 0, range.size());

        StringBuilder sb = new StringBuilder();
        sb.append(cbuf);

        return sb.toString();
    }

    @Override
    public boolean canRead() throws IOException {
        return reader.ready();
    }

}
