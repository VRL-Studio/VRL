/*
 * @(#)MovieHeaderAtom.java
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
import java.util.*;

import eu.mihosoft.vrl.ext.com.bric.io.GuardedOutputStream;
import eu.mihosoft.vrl.ext.javax.media.jai.PerspectiveTransform;

/** This is not a public class because I expect to make some significant
 * changes to this project in the next year.
 * <P>Use at your own risk.  This class (and its package) may change in future releases.
 * <P>Not that I'm promising there will be future releases.  There may not be.  :)
 */
class MovieHeaderAtom extends LeafAtom {
	int version = 0;
	int flags = 0;
	Date creationTime;
	Date modificationTime;
	long timeScale;
	long duration;
	float preferredRate = 1;
	float preferredVolume = 1;
	PerspectiveTransform matrix;
	long previewTime = 0;
	long previewDuration = 0;
	long posterTime = 0;
	long selectionTime = 0;
	long selectionDuration = 0;
	long currentTime = 0;
	long nextTrackID = -1;
	
	public MovieHeaderAtom(Atom parent,InputStream in) throws IOException {
		super(parent);
		
		version = in.read();
		flags = read24Int(in);
		creationTime = readDate(in);
		modificationTime = readDate(in);
		timeScale = read32Int(in);
		duration = read32Int(in);
		preferredRate = read16_16Float(in);
		preferredVolume = read8_8Float(in);
		skip(in,10); //reserved
		matrix = readMatrix(in);
		previewTime = read32Int(in);
		previewDuration = read32Int(in);
		posterTime = read32Int(in);
		selectionTime = read32Int(in);
		selectionDuration = read32Int(in);
		currentTime = read32Int(in);
		nextTrackID = read32Int(in);	
	}
	
	public MovieHeaderAtom(long timeScale,long duration) {
		super(null);
		creationTime = new Date();
		modificationTime = creationTime;
		this.duration = duration;
		this.timeScale = timeScale;
		matrix = new PerspectiveTransform();
	}
	
	protected String getIdentifier() {
		return "mvhd";
	}

	protected long getSize() {
		return 108;
	}

	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		write24Int(out,flags);
		writeDate(out,creationTime);
		writeDate(out,modificationTime);
		write32Int(out,timeScale);
		write32Int(out,duration);
		write16_16Float(out,preferredRate);
		write8_8Float(out,preferredVolume);
		write32Int(out,0);
		write32Int(out,0);
		write16Int(out,0);
		writeMatrix(out,matrix);
		write32Int(out,previewTime);
		write32Int(out,previewDuration);
		write32Int(out,posterTime);
		write32Int(out,selectionTime);
		write32Int(out,selectionDuration);
		write32Int(out,currentTime);
		write32Int(out,getRoot().getHighestTrackID()+1);
	}

	public String toString() {
		return "MovieHeaderAtom[ "+
		"version = "+version+", "+
		"flags = "+flags+", "+
		"creationTime = "+creationTime+", "+
		"modificationTime = "+modificationTime+", "+
		"timeScale = "+timeScale+", "+
		"duration = "+duration+", "+
		"preferredRate = "+preferredRate+", "+
		"preferredVolume = "+preferredVolume+", "+
		"matrix = "+matrix+", "+
		"previewTime = "+previewTime+", "+
		"previewDuration = "+previewDuration+", "+
		"posterTime = "+posterTime+", "+
		"selectionTime = "+selectionTime+", "+
		"selectionDuration = "+selectionDuration+", "+
		"currentTime = "+currentTime+", "+
		"nextTrackID = "+nextTrackID+"]";
	}

}
