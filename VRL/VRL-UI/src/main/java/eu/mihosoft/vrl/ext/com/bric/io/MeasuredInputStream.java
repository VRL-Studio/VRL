/*
 * @(#)MeasuredInputStream.java
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

import java.io.IOException;
import java.io.InputStream;

/** This <code>InputStream</code> relays information from an underlying
 * <code>InputStream</code> while measuring how many bytes have been
 * read or skipped.
 * <P>Note marking is not supported in this object.
 */
public class MeasuredInputStream extends InputStream {
	InputStream in;
	long read = 0;
	
	public MeasuredInputStream(InputStream i) {
		this.in = i;
	}

	public int available() throws IOException {
		return in.available();
	}
	
	/** Returns the number of bytes that have been read (or skipped).
	 * 
	 * @return the number of bytes that have been read (or skipped).
	 */
	public long getReadBytes() {
		return read;
	}

	public void close() throws IOException {
		in.close();
	}

	public synchronized void mark(int readlimit) {
		throw new RuntimeException();
	}

	public boolean markSupported() {
		return false;
	}

	public int read() throws IOException {
		int k = in.read();
		if(k==-1)
			return -1;
		read++;
		return k;
	}

	public int read(byte[] b, int off, int len) throws IOException {
		int returnValue = in.read(b, off, len);
		if(returnValue==-1)
			return -1;
		read += returnValue;
		
		return returnValue;
	}

	public int read(byte[] b) throws IOException {
		return read(b,0,b.length);
	}

	public synchronized void reset() throws IOException {
		throw new RuntimeException();
	}

	public long skip(long n) throws IOException {
		long returnValue = in.skip(n);
		if(returnValue==-1)
			return -1;
		read += returnValue;
		
		return returnValue;
	}
}
