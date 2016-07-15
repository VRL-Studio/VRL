/*
 * @(#)MeasuredOutputStream.java
 *
 * Copyright (c) 2008 Jeremy Wood. All Rights Reserved.
 *
 * You have a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) You do not utilize the software in a manner
 * which is disparaging to the original author.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. THE AUTHOR SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL THE
 * AUTHOR BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF THE AUTHOR HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 */

package eu.mihosoft.vrl.ext.com.bric.io;

import java.io.*;

/** This <code>OutputStream</code> passes information along to an underlying
 * <code>OutputStream</code> while counting how many bytes are written.
 * <P>At any point calling <code>getWrittenCount()</code> tells how the amount
 * of data that has been written since this object was constructed.
 *
 */
public class MeasuredOutputStream extends OutputStream {
	protected long written = 0;
	OutputStream out;
	private boolean closed = false;
	
	public MeasuredOutputStream(OutputStream out) {
		this.out = out;
	}

	/** Returns the number of bytes written since this object was constructed.
	 *  
	 * @return the number of bytes written since this object was constructed.
	 */
	public long getWrittenCount() {
		return written;
	}
	
	public void close() throws IOException {
		out.close();
		closed = true;
	}

	public void flush() throws IOException {
		out.flush();
	}

	public void write(byte[] b, int off, int len) throws IOException {
		if(closed) throw new IOException("This OutputStream has already been closed.");
		written+=len;
		out.write(b, off, len);
	}

	public void write(byte[] b) throws IOException {
		write(b,0,b.length);
	}

	public void write(int b) throws IOException {
		if(closed) throw new IOException("This OutputStream has already been closed.");
		written++;
		out.write(b);
	}
}
