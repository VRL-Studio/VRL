/*
 * @(#)ChunkOffsetAtom.java
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
package eu.mihosoft.vrl.ext.com.bric.qt;

import java.io.*;

import eu.mihosoft.vrl.ext.com.bric.io.GuardedOutputStream;

/** This is not a public class because I expect to make some significant
 * changes to this project in the next year.
 * <P>Use at your own risk.  This class (and its package) may change in future releases.
 * <P>Not that I'm promising there will be future releases.  There may not be.  :)
 */
class ChunkOffsetAtom extends LeafAtom {
	int version = 0;
	int flags = 0;
	long[] offsetTable = new long[0];
	
	public ChunkOffsetAtom() {
		super(null);
	}
	
	public long getChunkOffset(int index) {
		return offsetTable[index];
	}
	
	public int getChunkOffsetCount() {
		return offsetTable.length;
	}
	
	public void setChunkOffset(int index,long value) {
		offsetTable[index] = value;
	}
	
	public ChunkOffsetAtom(Atom parent,InputStream in) throws IOException {
		super(parent);
		version = in.read();
		flags = read24Int(in);
		int arraySize = (int)read32Int(in);
		offsetTable = new long[arraySize];
		for(int a = 0; a<offsetTable.length; a++) {
			offsetTable[a] = read32Int(in);
		}
	}
	
	public void addChunkOffset(long offset) {
		long[] newArray = new long[offsetTable.length+1];
		System.arraycopy(offsetTable,0,newArray,0,offsetTable.length);
		newArray[newArray.length-1] = offset;
		offsetTable = newArray;
	}
	
	protected String getIdentifier() {
		return "stco";
	}


	protected long getSize() {
		return 16+offsetTable.length*4;
	}


	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		write24Int(out,flags);
		write32Int(out,offsetTable.length);
		for(int a = 0; a<offsetTable.length; a++) {
			write32Int(out,offsetTable[a]);
		}
	}


	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[ ");
		for(int a = 0; a<offsetTable.length; a++) {
			if(a!=0) {
				sb.append(", ");
			}
			sb.append(offsetTable[a]);
		}
		sb.append(" ]");
		String entriesString = sb.toString();
		
		return "ChunkOffsetAtom[ version="+version+", "+
		"flags="+flags+", "+
		"sizeTable="+entriesString+"]";
	}
}
