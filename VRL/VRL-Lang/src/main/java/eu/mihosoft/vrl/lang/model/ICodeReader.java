/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.lang.model;

import java.io.IOException;
import java.nio.CharBuffer;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface ICodeReader {
    public int read(CharBuffer cb) throws IOException;
    public boolean canRead() throws IOException;
}
