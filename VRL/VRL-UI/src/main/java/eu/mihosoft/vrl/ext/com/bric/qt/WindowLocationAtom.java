/*
 * @(#)WindowLocationAtom.java
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

import eu.mihosoft.vrl.ext.com.bric.io.*;

/** This is not a public class because I expect to make some significant
 * changes to this project in the next year.
 * <P>Use at your own risk.  This class (and its package) may change in future releases.
 * <P>Not that I'm promising there will be future releases.  There may not be.  :)
 */
class WindowLocationAtom extends LeafAtom {
	int x, y;
	public WindowLocationAtom(Atom parent,InputStream in) throws IOException {
		super(parent);
		x = read16Int(in);
		y = read16Int(in);
	}
	
	protected String getIdentifier() {
		return "WLOC";
	}

	protected long getSize() {
		return 12;
	}

	protected void writeContents(GuardedOutputStream out) throws IOException {
		write16Int(out,x);
		write16Int(out,y);
	}
}
