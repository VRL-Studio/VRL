/*
 * @(#)AtomFactory.java
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

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.*;
import java.util.*;
import eu.mihosoft.vrl.ext.com.bric.io.*;

/** This is not a public class because I expect to make some significant
 * changes to this project in the next year.
 * <P>Use at your own risk.  This class (and its package) may change in future releases.
 * <P>Not that I'm promising there will be future releases.  There may not be.  :)
 */
class AtomFactory {
	private static boolean debug = false;
	
	public static void main(String[] args) {
		Frame frame = new Frame();
		FileDialog fd = new FileDialog(frame);
		fd.setVisible(true);
		if(fd.getFile()==null)
			throw new NullPointerException();
		File file = new File(fd.getDirectory()+fd.getFile());
		try {
			Atom[] atom = AtomFactory.readAll(file);
			
			long offset = 0;
			for(int a = 0; a<atom.length; a++) {
				if(atom[a].getIdentifier().equals("mdat")) {
					File file2 = new File("test2.mov");
					FileOutputStream out = new FileOutputStream(file2);
					for(int b = a; b<atom.length; b++) {
						filter(atom[b],offset);
						atom[b].write(out);
					}
					out.close();
					return;
				} else {
					offset += atom[a].getSize();
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void filter(Atom atom,long offset) {
		if(atom instanceof ChunkOffsetAtom) {
			ChunkOffsetAtom c = (ChunkOffsetAtom)atom;
			for(int a = 0; a<c.getChunkOffsetCount(); a++) {
				c.setChunkOffset(a,c.getChunkOffset(a)-offset);
			}
		}
		for(int a = 0; a<atom.getChildCount(); a++) {
			Atom atom2 = (Atom)atom.getChildAt(a);
			filter(atom2,offset);
		}
	}

	public static synchronized Atom[] readAll(File file) throws IOException {
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			MeasuredInputStream in2 = new MeasuredInputStream(in);
			Vector<Atom> v = new Vector<Atom>();
			while(in2.getReadBytes()<file.length()) {
				Atom atom = read(null,in2);
				v.add( atom );
			}
			return (Atom[])v.toArray(new Atom[v.size()]);
		} finally {
			try {
				in.close();
			} catch(Exception e) {}
		}
	}
	
	static byte[] sizeArray = new byte[4];
	static byte[] bigSizeArray = new byte[8];
	static Vector<String> parentTypes = new Vector<String>();
	private static final String[] PARENT_NODES = new String[] {
		"moov", "udta", "trak", "edts", "mdia", "minf", "dinf", "stbl", "tref"
	};
	static {
		for(int a = 0; a<PARENT_NODES.length; a++) {
			parentTypes.add(PARENT_NODES[a]);
		}
	}
	private static String padding = "";
		
	public static synchronized Atom read(Atom parent,InputStream in) throws IOException {
		long size = Atom.read32Int(in);
		/** When in debugging mode, we make a copy of the incoming
		 * array and then test the Atom.write() method later by
		 * validating it against what we just read.
		 */
		byte[] debugCopy = null;
		if(debug) {
			int s = (int)size;
			try {
				debugCopy = new byte[s];
				debugCopy[0] = (byte)((s >> 24) & 0xff);
				debugCopy[1] = (byte)((s >> 16) & 0xff);
				debugCopy[2] = (byte)((s >> 8) & 0xff);
				debugCopy[3] = (byte)(s & 0xff);
				Atom.read(in, debugCopy, 4, s-4);
				in = new ByteArrayInputStream(debugCopy);
				Atom.read32Int(in);
			} catch(OutOfMemoryError e) {
				System.err.println("Tried to allocate array of "+s+" bytes");
				throw e;
			}
		}
		
		String type = Atom.read32String(in);
		
		if(size==0) //yes, it can happen.  Don't know why.  This kind of atom has no type.
			return new EmptyAtom(parent);
		
		
		if(size==1) { //this is a special code indicating the size won't fit in 4 bytes
			Atom.read(in,bigSizeArray);
			size = ((sizeArray[0] & 0xff) << 56) + ((sizeArray[1] & 0xff) << 48) + 
			((sizeArray[2] & 0xff) << 40) + ((sizeArray[3] & 0xff) << 32) +
			((sizeArray[4] & 0xff) << 24) + ((sizeArray[5] & 0xff) << 16) + 
			((sizeArray[6] & 0xff) << 8) + ((sizeArray[7] & 0xff) << 0);
		}
	

		Atom atom = null;
		
		if(parentTypes.contains(type)) {
			System.out.println(padding+type+", "+size);
		}
		
		GuardedInputStream atomIn = new GuardedInputStream(in, size-8,false);
		
		if(parentTypes.contains(type)) {
			String oldPadding = padding;
			padding = padding+"\t";
			atom = new ParentAtom(parent,type,atomIn);
			padding = oldPadding;
		} else if(type.equals("mvhd")) {
			atom = new MovieHeaderAtom(parent,atomIn);
		} else if(type.equals("mdhd")) {
			atom = new MediaHeaderAtom(parent,atomIn);
		} else if(type.equals("hdlr")) {
			atom = new HandlerReferenceAtom(parent,atomIn);
		} else if(type.equals("vmhd")) {
			atom = new VideoMediaInformationHeaderAtom(parent,atomIn);
		} else if(type.equals("tkhd")) {
			atom = new TrackHeaderAtom(parent,atomIn);
		} else if(type.equals("dref")) {
			atom = new DataReferenceAtom(parent,atomIn);
		} else if(type.equals("stsd")) {
			if(parent.getParent()!=null && ((Atom)parent.getParent()).getChild(VideoMediaInformationHeaderAtom.class)!=null) {
				atom = new VideoSampleDescriptionAtom(parent,atomIn);
			} else {
				atom = new SampleDescriptionAtom(parent,atomIn);
			}
		} else if(type.equals("stts")) {
			atom = new TimeToSampleAtom(parent,atomIn);
		} else if(type.equals("stsc")) {
			atom = new SampleToChunkAtom(parent,atomIn);
		} else if(type.equals("stsz")) {
			atom = new SampleSizeAtom(parent,atomIn);
		} else if(type.equals("stco")) {
			atom = new ChunkOffsetAtom(parent,atomIn);
		} else if(type.charAt(0)==65449) {
			atom = new UserDataTextAtom(parent,type,atomIn);
		} else if(type.equals("WLOC")) {
			atom = new WindowLocationAtom(parent,atomIn);
		} else {
			atom = new UnknownLeafAtom(parent,type,atomIn);
		}
		
		if(debug) {
			if(atom.getSize()!=size) {
				System.err.println("Examine "+atom.getClass().getName()+", "+size+"!="+atom.getSize());
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				atom.write(out);
			} catch(IOException e) {
				e.printStackTrace();
			}
			byte[] newCopy = out.toByteArray();
			
			for(int a = 0; a<debugCopy.length; a++) {
				if(debugCopy[a]!=newCopy[a]) {
					ByteArrayInputStream in2 = new ByteArrayInputStream(newCopy);
					debug = false;
					Atom altAtom = read(null,in2);
					debug = true;
					System.err.println(altAtom);
					System.err.println("written block unequal to parsed block ("+atom.getClass().getName()+")");
					System.err.println("\tdebugCopy["+a+"] = "+debugCopy[a]+", newCopy["+a+"] = "+newCopy[a]+" (out of "+debugCopy.length+")");
					break;
				}
				if(a==debugCopy.length-1) {
					//System.err.println(atom.getClass().getName()+" passed inspection");
				}
			}
		}

		if(parentTypes.contains(type)==false) {
			System.out.println(padding+type+", "+size+", "+atom);
		}
		
		return atom;
	}
}
