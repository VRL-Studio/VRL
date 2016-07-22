/*
 * @(#)UnknownLeafAtom.java
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

import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

import eu.mihosoft.vrl.ext.com.bric.io.*;

/** This is not a public class because I expect to make some significant
 * changes to this project in the next year.
 * <P>Use at your own risk.  This class (and its package) may change in future releases.
 * <P>Not that I'm promising there will be future releases.  There may not be.  :)
 */
class UnknownLeafAtom extends LeafAtom {
	byte[] data;
	String id;
	public UnknownLeafAtom(Atom parent,String id,GuardedInputStream in) throws IOException {
		super(parent);
		this.id = id;
		data = new byte[(int)in.getRemainingLimit()];
		read(in,data);
		/*try {
			readJPEGs(data);
		} catch(Exception e) {
			e.printStackTrace();
		}*/
	}
	
	protected String getIdentifier() {
		return id;
	}

	protected long getSize() {
		return 8+data.length;
	}

	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(data);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(int a = 0; a<Math.min(data.length,64); a++) {
			sb.append( (char)data[a] );
		}
		return sb.toString();
	}
	
	protected void readJPEGs(byte[] data) throws IOException {
		int i = 0;
		int start = -1;
		int lastStart = 0;
		while(i<data.length-1) {
			if( (data[i] & 0xff)==0xff) {
				if( (data[i+1] & 0xff)==0xd8) {
					start = i;
				} else if( (data[i+1] & 0xff)==0xd9) {
					System.out.println( start+", "+(start-lastStart)+", "+(i+1-start));
					readJPEG(data,start,i+1-start);
					lastStart = start;
					start = -1;
				}
			}
			i++;
		}
	}
	
	static private int jpegCtr = 1;
	protected void readJPEG(byte[] data,int pos,int length) throws IOException {
		byte[] d = new byte[length];
		System.arraycopy(data,pos,d,0,d.length);
		ByteArrayInputStream input = new ByteArrayInputStream(d);
		BufferedImage bi = ImageIO.read(input);
		File file = new File("Test "+(jpegCtr++)+".png");
		ImageIO.write(bi, "png", file);
	}
}
