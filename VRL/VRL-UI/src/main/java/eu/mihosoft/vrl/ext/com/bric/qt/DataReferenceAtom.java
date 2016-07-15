/*
 * @(#)DataReferenceAtom.java
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
class DataReferenceAtom extends LeafAtom {
	int version = 0;
	int flags = 0;
	DataReferenceEntry[] entries = new DataReferenceEntry[0];
	
	public DataReferenceAtom() {
		super(null);
	}
	
	public void addEntry(String type,int version,int flags,byte[] data) {
		DataReferenceEntry e = new DataReferenceEntry(type,version,flags,data);
		DataReferenceEntry[] newArray = new DataReferenceEntry[entries.length+1];
		System.arraycopy(entries,0,newArray,0,entries.length);
		newArray[newArray.length-1] = e;
		entries = newArray;
	}
	
	public DataReferenceAtom(Atom parent,InputStream in) throws IOException {
		super(parent);
		
		version = in.read();
		flags = read24Int(in);
		int entryCount = (int)read32Int(in);
		entries = new DataReferenceEntry[entryCount];
		for(int a = 0; a<entries.length; a++) {
			entries[a] = new DataReferenceEntry(in);
		}
	}
	
	protected String getIdentifier() {
		return "dref";
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
		
		return "DataReferenceAtom[ version="+version+", "+
		"flags="+flags+", "+
		"entries="+entriesString+"]";
	}
}

class DataReferenceEntry {
	byte[] data;
	String type;
	int version;
	int flags;
	
	public DataReferenceEntry(String type,int version,int flags,byte[] data) {
		this.type = type;
		this.version = version;
		this.flags = flags;
		this.data = data;
	}
	
	public DataReferenceEntry(InputStream in) throws IOException {
		long size = Atom.read32Int(in);
		type = Atom.read32String(in);
		version = in.read();
		flags = Atom.read24Int(in);
		data = new byte[(int)size-12];
		Atom.read(in,data);
	}
	
	protected long getSize() {
		return 12+data.length;
	}
	
	protected void write(OutputStream out) throws IOException {
		Atom.write32Int(out,12+data.length);
		Atom.write32String(out,type);
		out.write(version);
		Atom.write24Int(out, flags);
		out.write(data);
	}
	
	public String toString() {
		return "DataReferenceEntry[ type=\""+type+"\", "+
		"version="+version+", "+
		"flags="+flags+", "+
		"data=\""+(new String(data))+"\"]";
	}
}
