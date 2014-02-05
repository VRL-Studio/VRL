/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.instrumentation;

import eu.mihosoft.vrl.lang.model.ICodeReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class CodeReaderWrapper implements ICodeReader {

    private final Reader reader;

    public CodeReaderWrapper(Reader reader) {
        this.reader = reader;
    }

    @Override
    public int read(CharBuffer cb) throws IOException {
        return reader.read(cb);
    }

    @Override
    public boolean canRead() throws IOException {
        return reader.ready();
    }

}
