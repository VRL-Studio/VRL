/*
 * @(#)TimeToSampleAtom.java
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

/** Time-to-sample atoms store duration information for a media samples.
 * The times used here are relative to an enclosing time scale.
 * 
 * <P>This is not a public class because I expect to make some significant
 * changes to this project in the next year.
 * <P>Use at your own risk.  This class (and its package) may change in future releases.
 * <P>Not that I'm promising there will be future releases.  There may not be.  :)
 */
class TimeToSampleAtom extends LeafAtom {
	int version = 0;
	int flags = 0;
	TimeToSampleEntry[] table = new TimeToSampleEntry[0];
	
	public TimeToSampleAtom() {
		super(null);
	}
	
	public TimeToSampleAtom(Atom parent,InputStream in) throws IOException {
		super(parent);
		version = in.read();
		flags = read24Int(in);
		int entryCount = (int)read32Int(in);
		table = new TimeToSampleEntry[entryCount];
		for(int a = 0; a<table.length; a++) {
			table[a] = new TimeToSampleEntry(in);
		}
	}
	
	protected String getIdentifier() {
		return "stts";
	}

	protected long getSize() {
		return 16+table.length*8;
	}

	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		write24Int(out,flags);
		write32Int(out,table.length);
		for(int a = 0; a<table.length; a++) {
			table[a].write(out);
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[ ");
		for(int a = 0; a<table.length; a++) {
			if(a!=0) {
				sb.append(", ");
			}
			sb.append(table[a].toString());
		}
		sb.append(" ]");
		String tableString = sb.toString();
		
		return "TimeToSampleAtom[ version="+version+", "+
		"flags="+flags+", "+
		"table="+tableString+"]";
	}
	
	/** Add a new sample time to this atom.
	 * 
	 * @param duration the new duration, relative to the enclosing
	 * media's time scale.
	 */
	public void addSampleTime(long duration) {
		if(table.length==0 || table[table.length-1].sampleDuration!=duration) {
			TimeToSampleEntry[] newTable = new TimeToSampleEntry[table.length+1];
			System.arraycopy(table,0,newTable,0,table.length);
			newTable[newTable.length-1] = new TimeToSampleEntry(1,duration);
			table = newTable;
		} else {
			table[table.length-1].sampleCount++;
		}
	}
	
	public long getDurationOfSample(long sampleIndex) {
		for(int a = 0; a<table.length; a++) {
			if(sampleIndex<table[a].sampleCount) {
				return table[a].sampleDuration;
			}
			sampleIndex = sampleIndex-table[a].sampleCount;
		}
		throw new RuntimeException("Could not find a sample at index "+sampleIndex);
	}
}

/** This represents the duration of a series of samples.
 * This indicates that <code>sampleCount</code>-many consecutive
 * samples have a duration of <code>sampleDuration</code>.
 * (The duration is relative to an enclosing time scale.)
 */
class TimeToSampleEntry {
	long sampleCount, sampleDuration;
	
	public TimeToSampleEntry(long count,long duration) {
		this.sampleCount = count;
		this.sampleDuration = duration;
	}
	
	public TimeToSampleEntry(InputStream in) throws IOException {
		sampleCount = Atom.read32Int(in);
		sampleDuration = Atom.read32Int(in);
	}
	
	public String toString() {
		return "["+sampleCount+", "+sampleDuration+"]";
	}
	
	protected void write(OutputStream out) throws IOException {
		Atom.write32Int(out, sampleCount);
		Atom.write32Int(out, sampleDuration);
	}
}
