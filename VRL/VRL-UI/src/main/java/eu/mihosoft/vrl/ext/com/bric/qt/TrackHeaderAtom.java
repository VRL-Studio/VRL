/*
 * @(#)TrackHeaderAtom.java
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

import eu.mihosoft.vrl.ext.javax.media.jai.PerspectiveTransform;
import java.io.*;
import java.util.*;
import eu.mihosoft.vrl.ext.com.bric.io.GuardedOutputStream;

/** This is not a public class because I expect to make some significant
 * changes to this project in the next year.
 * <P>Use at your own risk.  This class (and its package) may change in future releases.
 * <P>Not that I'm promising there will be future releases.  There may not be.  :)
 */
class TrackHeaderAtom extends LeafAtom {
	public static final int FLAG_ENABLED = 0x001;
	public static final int FLAG_IN_MOVIE = 0x002;
	public static final int FLAG_IN_PREVIEW = 0x004;
	public static final int FLAG_IN_POSTER = 0x008;
	int version = 0;
	int flags = FLAG_ENABLED+FLAG_IN_MOVIE+FLAG_IN_PREVIEW+FLAG_IN_POSTER;
	Date creationTime;
	Date modificationTime;
	long trackID;
	long duration;
	int layer = 0;
	int alternateGroup = 0;
	float volume = 1;
	PerspectiveTransform matrix;
	float width;
	float height;
	
	public TrackHeaderAtom(long trackID,long duration,float width,float height) {
		super(null);
		this.trackID = trackID;
		this.duration = duration;
		creationTime = new Date();
		modificationTime = creationTime;
		matrix = new PerspectiveTransform();
		this.width = width;
		this.height = height;
	}
	
	public TrackHeaderAtom(Atom parent,InputStream in) throws IOException {
		super(parent);
		version = in.read();
		flags = read24Int(in);
		creationTime = readDate(in);
		modificationTime = readDate(in);
		trackID = read32Int(in);
		skip(in,4); //reserved
		duration = read32Int(in);
		skip(in,8); //more reserved
		layer = read16Int(in);
		alternateGroup = read16Int(in);
		volume = read8_8Float(in);
		skip(in,2); //even more reserved
		matrix = readMatrix(in);
		width = read16_16Float(in);
		height = read16_16Float(in);
	}
	
	
	protected String getIdentifier() {
		return "tkhd";
	}

	protected long getSize() {
		return 92;
	}


	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		write24Int(out,flags);
		writeDate(out,creationTime);
		writeDate(out,modificationTime);
		write32Int(out,trackID);
		write32Int(out,0);
		write32Int(out,duration);
		write32Int(out,0);
		write32Int(out,0);
		write16Int(out,layer);
		write16Int(out,alternateGroup);
		write8_8Float(out,volume);
		write16Int(out,0);
		writeMatrix(out,matrix);
		write16_16Float(out,width);
		write16_16Float(out,height);
	}


	public String toString() {
		return "TrackHeaderAtom[ version="+version+", "+
		"flags="+flags+", "+
		"creationTime="+creationTime+", "+
		"modificationTime="+modificationTime+", "+
		"trackID="+trackID+", "+
		"duration="+duration+", "+
		"layer="+layer+", "+
		"alternateGroup="+alternateGroup+", "+
		"volume="+volume+", "+
		"matrix="+matrix+", "+
		"width="+width+", "+
		"height="+height+"]";
	}
}
