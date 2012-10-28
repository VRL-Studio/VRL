/*
 * @(#)HandlerReferenceAtom.java
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

import eu.mihosoft.vrl.ext.com.bric.io.GuardedInputStream;
import eu.mihosoft.vrl.ext.com.bric.io.GuardedOutputStream;

/** This is not a public class because I expect to make some significant
 * changes to this project in the next year.
 * <P>Use at your own risk.  This class (and its package) may change in future releases.
 * <P>Not that I'm promising there will be future releases.  There may not be.  :)
 */
class HandlerReferenceAtom extends LeafAtom {
	int version = 0;
	int flags = 0;
	String componentType;
	String componentSubtype;
	String componentManufacturer;
	long componentFlags = 0;
	long componentFlagsMask = 0;
	String componentName = "";
	
	public HandlerReferenceAtom(String componentType,String componentSubtype,String componentManufacturer) {
		super(null);
		this.componentType = componentType;
		this.componentSubtype = componentSubtype;
		this.componentManufacturer = componentManufacturer;
	}
	
	public HandlerReferenceAtom(Atom parent,GuardedInputStream in) throws IOException {
		super(parent);
		
		int bytesToRead = (int)in.getRemainingLimit();
		version = in.read();
		flags = read24Int(in);
		componentType = read32String(in);
		componentSubtype = read32String(in);
		componentManufacturer = read32String(in);
		componentFlags = read32Int(in);
		componentFlagsMask = read32Int(in);
		
		int stringSize = in.read();
		if(stringSize!=bytesToRead-25) {
			//this is NOT a counted string, as the API
			//suggests it is: instead it's a pascal string.
			//thanks to Chris Adamson for pointing this out.
			byte[] data = new byte[bytesToRead-24];
			data[0] = (byte)stringSize;
			read(in,data,1,data.length-1);
			componentName = new String(data);
		} else {
			byte[] data = new byte[stringSize];
			read(in,data);
			componentName = new String(data);
		}
	}
	
	protected String getIdentifier() {
		return "hdlr";
	}

	protected long getSize() {
		byte[] data = componentName.getBytes();
		return 33+data.length;
	}

	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		write24Int(out,flags);
		write32String(out,componentType);
		write32String(out,componentSubtype);
		write32String(out,componentManufacturer);
		write32Int(out,componentFlags);
		write32Int(out,componentFlagsMask);
		byte[] data = componentName.getBytes();
		out.write(data.length);
		out.write(data);
	}

	public String toString() {
		return "HandlerReferenceAtom[ version="+version+", "+
		"flags="+flags+", "+
		"componentType=\""+componentType+"\", "+
		"componentSubtype=\""+componentSubtype+"\", "+
		"componentManufacturer=\""+componentManufacturer+"\", "+
		"componentFlags="+componentFlags+", "+
		"componentFlagsMask="+componentFlagsMask+", "+
		"componentName=\""+componentName+"\" ]";
	}
}
