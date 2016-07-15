/*
 * @(#)GuardedOutputStream.java
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

/** This restricts the amount of data that can be written
 * to an underlying <code>OutputStream</code>.
 * <P>This is especially useful in unit testing to ensure
 * that certain blocks of data do not exceed their allotted size.
 * <P>An IOException is thrown if you attempt to write more
 * data than this stream was told to allow.
 */
public class GuardedOutputStream extends MeasuredOutputStream {
	long limit;
	
	/** Constructs a new <code>GuardedOutputStream</code>.
	 * 
	 * @param out the underlying <code>OutputStream</code> to send data to.
	 * @param limit the number of bytes that can be written
	 */
	public GuardedOutputStream(OutputStream out,long limit) {
		super(out);
		this.limit = limit;
	}
	
	/** The number of bytes that can still be written to this stream.
	 * <P>(This value changes every time <code>write()</code> is called.)
	 * @return the number of bytes that can still be written to this stream.
	 */
	public long getLimit() {
		return limit;
	}

	public void write(byte[] b, int off, int len) throws IOException {
		if(len==0) return;
		
		if(limit<len) {
			throw new IOException("limit exceeded");
		}
		limit -= len;
		super.write(b, off, len);
	}

	public void write(byte[] b) throws IOException {
		write(b,0,b.length);
	}

	public void write(int b) throws IOException {
		if(limit==0)
			throw new IOException("limit exceeded");
		limit--;
		super.write(b);
	}
	
	
}
