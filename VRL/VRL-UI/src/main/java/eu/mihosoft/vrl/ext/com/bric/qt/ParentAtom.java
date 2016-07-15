/*
 * @(#)ParentAtom.java
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

import java.util.*;
import java.io.*;

import javax.swing.tree.TreeNode;

import eu.mihosoft.vrl.ext.com.bric.io.*;

/** This is not a public class because I expect to make some significant
 * changes to this project in the next year.
 * <P>Use at your own risk.  This class (and its package) may change in future releases.
 * <P>Not that I'm promising there will be future releases.  There may not be.  :)
 */
class ParentAtom extends Atom {
	Vector<Atom> children = new Vector<Atom>();
	String id;
	
	public ParentAtom(String id) {
		super(null);
		this.id = id;
	}
	
	public void add(Atom a) {
		children.add(a);
		a.parent = this;
	}
	
	public ParentAtom(Atom parent,String id,GuardedInputStream in) throws IOException {
		super(parent);
		this.id = id;
		while(in.isAtLimit()==false) {
			children.add(AtomFactory.read(this,in));
		}
	}

	public Enumeration children() {
		return children.elements();
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public TreeNode getChildAt(int childIndex) {
		return (TreeNode)children.get(childIndex);
	}

	public int getChildCount() {
		return children.size();
	}

	public int getIndex(TreeNode node) {
		return children.indexOf(node);
	}

	public boolean isLeaf() {
		return children.size()==0;
	}

	protected long getSize() {
		long sum = 8;
		for(int a = 0; a<children.size(); a++) {
			Atom atom = (Atom)children.get(a);
			sum += atom.getSize();
		}
		return sum;
	}

	protected String getIdentifier() {
		return id;
	}

	protected void writeContents(GuardedOutputStream out) throws IOException {
		for(int a = 0; a<children.size(); a++) {
			Atom atom = (Atom)children.get(a);
			atom.write(out);
		}
	}
}
