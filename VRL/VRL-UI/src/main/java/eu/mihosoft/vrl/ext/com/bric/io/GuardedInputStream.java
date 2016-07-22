/*
 * @(#)GuardedInputStream.java
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

import java.io.InputStream;
import java.io.IOException;

/** This filtered stream places an initial limit on the number of bytes
 * that can be read.  Once this limit is reached, all methods in this object
 * return -1 indicating we're at the EOF, although the underlying InputStream
 * is not touched.
 * <P>This object is useful when parsing files where specific blocks of a file have
 * a predetermined size: with this object you can guarantee you read only
 * a fixed number of bytes.  So if data inside the block is corrupt, or if you
 * want to loosely guard how you read each block, this object will make sure you
 * don't read too far.
 * <P>The <code>mark()</code> and <code>reset()</code> methods are not supported.
 * 
 */
public class GuardedInputStream extends MeasuredInputStream {
	long limit;
	boolean canClose;
	
	/** Constructs a new <code>GuardedInputStream</code>.
	 * 
	 * @param in the underlying <code>InputStream</code> to use.
	 * @param limit the maximum number of bytes that will be read.
	 * @param canClose if this is <code>false</code>, then calling
	 * <code>close</code> will not actually close the underlying stream.
	 */
	public GuardedInputStream(InputStream in,long limit,boolean canClose) {
		super(in);
		this.limit = limit;
		this.canClose = canClose;
	}
	
	/** Whether any more data can be read from this stream (due to the limit
	 * it was constructed with).
	 * 
	 * @return
	 */
	public boolean isAtLimit() {
		return limit==0;
	}
	
	public void close() throws IOException {
		if(canClose) {
			super.close();
		}
	}
	public synchronized void mark(int readlimit) {
		throw new RuntimeException("mark is unsupported");
	}
	public boolean markSupported() {
		return false;
	}
	public int read() throws IOException {
		if(limit==0)
			return -1;
		
		limit--;
		return super.read();
	}
	public int read(byte[] b, int off, int len) throws IOException {
		if(limit==0)
			return -1;
		
		if(len>limit) {
			//we can cast here because len, which is an int, is greater than limit.
			//therefore limit can be expressed as an int, too.
			return read(b,off,(int)limit);
		}
		int returnValue = super.read(b, off, len);
		limit = limit-returnValue;
		return returnValue;
	}
	
	/** Returns the number of bytes that are allowed to be read.
	 * <P>This number has nothing to do with the number of bytes
	 * that are actually remaining in the underlying <code>InputStream</code>.
	 * For example, if this <code>GuardedInputStream</code> was designed to
	 * read at most 1000 bytes, then this method may return 1000 -- even though
	 * there may only be 500 bytes available in the underlying <code>InputStream</code>.
	 * @return the number of bytes that are allowed to be read.
	 */
	public long getRemainingLimit() {
		return limit;
	}
	
	public int read(byte[] b) throws IOException {
		return read(b,0,b.length);
	}
	
	public synchronized void reset() throws IOException {
		throw new RuntimeException("mark is unsupported");
	}
	public long skip(long n) throws IOException {
		if(limit==0)
			return -1;
		
		if(n>limit) {
			return skip(limit);
		}
		long returnValue = super.skip(n);
		limit = limit-returnValue;
		return returnValue;
	}
}
