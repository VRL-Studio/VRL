/* 
 * RedirectableStream.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007–2018 by Michael Hoffer,
 * Copyright (c) 2015–2018 G-CSC, Uni Frankfurt,
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn)
 * 
 * This file is part of Visual Reflection Library (VRL).
 *
 * VRL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * see: http://opensource.org/licenses/LGPL-3.0
 *      file://path/to/VRL/src/eu/mihosoft/vrl/resources/license/lgplv3.txt
 *
 * VRL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * This version of VRL includes copyright notice and attribution requirements.
 * According to the LGPL this information must be displayed even if you modify
 * the source code of VRL. Neither the VRL Canvas attribution icon nor any
 * copyright statement/attribution may be removed.
 *
 * Attribution Requirements:
 *
 * If you create derived work you must do three things regarding copyright
 * notice and author attribution.
 *
 * First, the following text must be displayed on the Canvas:
 * "based on VRL source code". In this case the VRL canvas icon must be removed.
 * 
 * Second, the copyright notice must remain. It must be reproduced in any
 * program that uses VRL.
 *
 * Third, add an additional notice, stating that you modified VRL. A suitable
 * notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Please cite the publication(s) listed below.
 *
 * Publications:
 *
 * M. Hoffer, C. Poliwoda, & G. Wittum. (2013). Visual reflection library:
 * a framework for declarative GUI programming on the Java platform.
 * Computing and Visualization in Science, 2013, 16(4),
 * 181–192. http://doi.org/10.1007/s00791-014-0230-y
 */

package eu.mihosoft.vrl.system;

import eu.mihosoft.vrl.visual.VSwingUtil;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class RedirectableStream extends PrintStream {

    JTextArea view;
    private boolean redirectToUi;
    private boolean redirectToStdOut;
    SimpleAttributeSet attributeSet;

    public RedirectableStream(OutputStream out, JTextArea textPane,
            SimpleAttributeSet attributeSet) {
        super(out);
        this.view = textPane;
        this.attributeSet = attributeSet;
    }

    public RedirectableStream(
            OutputStream out, JTextArea textPane, Color c, boolean bold) {
        super(out);
        this.view = textPane;
        this.attributeSet = createAttributeSet(c, bold);
    }

    private static SimpleAttributeSet createAttributeSet(Color c, boolean bold) {
        SimpleAttributeSet outAttributes = new SimpleAttributeSet();
        StyleConstants.setForeground(outAttributes, c);
        StyleConstants.setBold(outAttributes, bold);
        return outAttributes;
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        if (isRedirectToUi()) {
            try {
                int startOffSet = view.getDocument().getLength();
                view.getDocument().insertString(startOffSet,
                        new String(buf, off, len), attributeSet);
                view.setCaretPosition(startOffSet + len);
            } catch (BadLocationException e) {
                // e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // e.printStackTrace();
            }

            VSwingUtil.invokeLater(new Runnable() {

                @Override
                public void run() {
                    // automatically scroll down to the last line
                    view.scrollRectToVisible(
                            new Rectangle(0, view.getHeight() - 1, 1, 1));

                }
            });

        }

        if (isRedirectToStdOut()) {
            super.write(buf, off, len);
        }
    }

    /**
     * @return the redirectToUi
     */
    public boolean isRedirectToUi() {
        return redirectToUi;
    }

    /**
     * @param redirectToUi the redirectToUi to set
     */
    public void setRedirectToUi(boolean redirectToUi) {
        this.redirectToUi = redirectToUi;
    }

    /**
     * @return the redirectToStdOut
     */
    public boolean isRedirectToStdOut() {
        return redirectToStdOut;
    }

    /**
     * @param redirectToStdOut the redirectToStdOut to set
     */
    public void setRedirectToStdOut(boolean redirectToStdOut) {
        this.redirectToStdOut = redirectToStdOut;
    }
}
