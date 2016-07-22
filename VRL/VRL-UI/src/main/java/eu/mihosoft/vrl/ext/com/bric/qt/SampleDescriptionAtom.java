/*
 * @(#)SampleDescriptionAtom.java
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
 */package eu.mihosoft.vrl.ext.com.bric.qt;

import java.io.*;

import eu.mihosoft.vrl.ext.com.bric.io.GuardedOutputStream;

/** This is not a public class because I expect to make some significant
 * changes to this project in the next year.
 * <P>Use at your own risk.  This class (and its package) may change in future releases.
 * <P>Not that I'm promising there will be future releases.  There may not be.  :)
 */
class SampleDescriptionAtom extends LeafAtom {
	int version = 0;
	int flags = 0;
	SampleDescriptionEntry[] entries = new SampleDescriptionEntry[0];
	
	public SampleDescriptionAtom() {
		super(null);
	}
	
	public SampleDescriptionAtom(Atom parent,InputStream in) throws IOException {
		super(parent);
		version = in.read();
		flags = read24Int(in);
		int tableSize = (int)read32Int(in);
		entries = new SampleDescriptionEntry[tableSize];
		for(int a = 0; a<entries.length; a++) {
			entries[a] = readEntry(in);
		}
	}
	
	public void addEntry(SampleDescriptionEntry e) {
		SampleDescriptionEntry[] newArray = new SampleDescriptionEntry[entries.length+1];
		System.arraycopy(entries,0,newArray,0,entries.length);
		newArray[newArray.length-1] = e;
		entries = newArray;
	}
	
	protected SampleDescriptionEntry readEntry(InputStream in) throws IOException {
		return new UnknownSampleDescriptionEntry(in);
	}
		
	protected String getIdentifier() {
		return "stsd";
	}

	protected long getSize() {
		long sum = 16;
		for(int a = 0; a<entries.length; a++) {
			sum += entries[a].getSize();
		}
		return sum;
	}

	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		write24Int(out,flags);
		write32Int(out,entries.length);
		for(int a = 0; a<entries.length; a++) {
			entries[a].write(out);
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[ ");
		for(int a = 0; a<entries.length; a++) {
			if(a!=0) {
				sb.append(", ");
			}
			sb.append(entries[a].toString());
		}
		sb.append(" ]");
		String entriesString = sb.toString();
		
		return getClassName()+"[ version="+version+", "+
		"flags="+flags+", "+
		"entries="+entriesString+"]";
	}
	
	protected String getClassName() {
		String s = this.getClass().getName();
		if(s.indexOf('.')!=-1)
			s = s.substring(s.lastIndexOf('.')+1);
		return s;
	}
}

abstract class SampleDescriptionEntry {
	/** If this entry is read from an <code>InputStream</code>, then this
	 * is the size that this entry should be.
	 * <P>Subclasses may consult this value when reading from a stream to
	 * determine how much more data to read in this entry.
	 * <P>Otherwise this field is unused.
	 */
	long inputSize;
	String type;
	int dataReference;
	public SampleDescriptionEntry(String type,int dataReference) {
		this.type = type;
		this.dataReference = dataReference;
	}
	
	public SampleDescriptionEntry(InputStream in) throws IOException {
		inputSize = Atom.read32Int(in);
		type = Atom.read32String(in);
		Atom.skip(in,6); //reserved
		dataReference = Atom.read16Int(in);
	}
	
	protected abstract long getSize();
	
	protected abstract void write(OutputStream out) throws IOException;
}

class UnknownSampleDescriptionEntry extends SampleDescriptionEntry {

	byte[] data = new byte[0];
	
	public UnknownSampleDescriptionEntry(InputStream in) throws IOException {
		super(in);
		if(inputSize>16) {
			data = new byte[(int)(inputSize-16)];
			Atom.read(in,data);
		} else {
			data = new byte[0];
		}
	}
	
	protected long getSize() {
		return 16+data.length;
	}
	
	protected void write(OutputStream out) throws IOException {
		Atom.write32Int(out, getSize());
		Atom.write32String(out, type);
		Atom.write48Int(out, 0);
		Atom.write16Int(out, dataReference);
		out.write(data);
	}
	
	public String toString() {
		if(data.length==0) {
			return "UnknownSampleDescriptionEntry[ type=\""+type+"\", "+
			"dataReference="+dataReference+" ];";
		}

		return "UnknownSampleDescriptionEntry[ type=\""+type+"\", "+
		"dataReference="+dataReference+", "+
		"data=\""+(new String(data))+"\" ]";
	}
}
