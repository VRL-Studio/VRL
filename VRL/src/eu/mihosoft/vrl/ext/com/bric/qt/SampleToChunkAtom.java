/*
 * @(#)SampleToChunkAtom.java
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

/** As samples are added to a media, they are collected into chunks
* that allow optimized data access. A chunk contains one or more 
* samples. Chunks in a media may have different sizes, and the samples 
* within a chunk may have different sizes. The sample-to-chunk atom
* stores chunk information for the samples in a media. 
* 
* Sample-to-chunk atoms have an atom type of 'stsc'. The sample-to-chunk
* atom contains a table that maps samples to chunks in the media data
* stream. By examining the sample-to-chunk atom, you can determine the
* chunk that contains a specific sample.
*
* <P>This is not a public class because I expect to make some significant
* changes to this project in the next year.
* <P>Use at your own risk.  This class (and its package) may change in future releases.
* <P>Not that I'm promising there will be future releases.  There may not be.  :)
*/
class SampleToChunkAtom extends LeafAtom {
	int version = 0;
	int flags = 0;
	SampleToChunkEntry[] entries = new SampleToChunkEntry[0];
	
	public SampleToChunkAtom() {
		super(null);
	}
	
	public SampleToChunkAtom(Atom parent,InputStream in) throws IOException {
		super(parent);
		version = in.read();
		flags = read24Int(in);
		int entryCount = (int)read32Int(in);
		entries = new SampleToChunkEntry[entryCount];
		for(int a = 0; a<entryCount; a++) {
			entries[a] = new SampleToChunkEntry(in);
		}
	}
	
	public void addChunk(long chunkIndex,long samplesPerChunk,long sampleDescriptionID) {
		//TODO: this probably could use rewriting, but I'm not sure exactly how
		//much of this object is supposed to be black-box implementation...
		if(entries.length==0) {
			SampleToChunkEntry[] newArray = new SampleToChunkEntry[] {
					new SampleToChunkEntry(chunkIndex, samplesPerChunk, sampleDescriptionID)
			};
			entries = newArray;
		} else {
			for(int a = 0; a<entries.length; a++) {
				if(entries[a].firstChunk<=chunkIndex && (a==entries.length-1 || entries[a+1].firstChunk>chunkIndex)) {
					//this is where this chunk belongs:
					if(entries[a].samplesPerChunk==samplesPerChunk && entries[a].sampleDescriptionID==sampleDescriptionID) {
						//this new entry is implied; it doesn't need to be written.
						return;
					} else {
						SampleToChunkEntry[] newTable = new SampleToChunkEntry[entries.length+1];
						System.arraycopy(entries,0,newTable,0,entries.length);
						newTable[newTable.length-1] = new SampleToChunkEntry(chunkIndex,samplesPerChunk,sampleDescriptionID);
						entries = newTable;
					}
				}
			}
		}
	}
		
	protected String getIdentifier() {
		return "stsc";
	}

	protected long getSize() {
		return 16+12*entries.length;
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
		
		return "SampleToChunkAtom[ version="+version+", "+
		"flags="+flags+", "+
		"entries="+entriesString+"]";
	}
	
	private SampleToChunkEntry getSampleToChunkEntry(int chunkIndex) {
		for(int a = 0; a<entries.length-1; a++) {
			if(entries[a].firstChunk<=chunkIndex && chunkIndex<entries[a+1].firstChunk) {
				return entries[a];
			}
		}
		return entries[entries.length-1];
	}

	/** The sample description ID for a specific chunk.
	 * 
	 * @param chunkIndex the chunk to examine
	 * @return the sample description ID used in this chunk.
	 */
	public long getChunkSampleDescriptionID(int chunkIndex) {
		return getSampleToChunkEntry(chunkIndex).sampleDescriptionID;
	}
	
	/** Returns how many samples are in a given chunk.
	 * 
	 * @param chunkIndex the chunk to examine
	 * @return how many samples are in a given chunk.
	 */
	public long getChunkSampleCount(int chunkIndex) {
		return getSampleToChunkEntry(chunkIndex).samplesPerChunk;
	}
}

class SampleToChunkEntry {
	long firstChunk, samplesPerChunk, sampleDescriptionID;
	
	public SampleToChunkEntry(long firstChunk,long samplesPerChunk,long sampleDescriptionID) {
		this.firstChunk = firstChunk;
		this.samplesPerChunk = samplesPerChunk;
		this.sampleDescriptionID = sampleDescriptionID;
	}
	
	public SampleToChunkEntry(InputStream in) throws IOException {
		firstChunk = Atom.read32Int(in);
		samplesPerChunk = Atom.read32Int(in);
		sampleDescriptionID = Atom.read32Int(in);
	}
	
	public String toString() {
		return "[ "+firstChunk+", "+samplesPerChunk+", "+sampleDescriptionID+"]";
	}
	
	protected void write(OutputStream out) throws IOException {
		Atom.write32Int(out,firstChunk);
		Atom.write32Int(out,samplesPerChunk);
		Atom.write32Int(out,sampleDescriptionID);
	}
}
