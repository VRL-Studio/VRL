/* 
 * TestCompletionProvider.java
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

package eu.mihosoft.vrl.lang.visual;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class TestCompletionProvider extends DefaultCompletionProvider {

    /**
     * Returns the text just before the current caret position that could be
     * the start of something auto-completable.<p>
     *
     * This method returns all characters before the caret that are matched
     * by  {@link #isValidChar(char)}.
     *
     * {@inheritDoc}
     */
    @Override
    public String getAlreadyEnteredText(JTextComponent comp) {

        Document doc = comp.getDocument();

        int dot = comp.getCaretPosition();
//		Element root = doc.getDefaultRootElement();
//		int index = root.getElementIndex(dot);
//		Element elem = root.getElement(index);
//		int start = elem.getStartOffset();
//		int len = dot-start;
        try {
            return doc.getText(0, dot);
        } catch (BadLocationException ble) {
            ble.printStackTrace(System.err);
            return EMPTY_STRING;
        }

//		int segEnd = seg.offset + len;
//		start = segEnd - 1;
//		while (start>=seg.offset && isValidChar(seg.array[start])) {
//			start--;
//		}
//		start++;
//
//		len = segEnd - start;
//		return len==0 ? EMPTY_STRING : new String(seg.array, start, len);
    }
    
     @Override
    @SuppressWarnings("unchecked")
    public List getCompletions(JTextComponent tc) {

        String fullText = tc.getText();

        List<Completion> result = new ArrayList<Completion>();

        String text = getAlreadyEnteredText(tc);
        
        result.addAll(super.getCompletions(tc));
        
        
        
        return result;
     }
    
    @Override
    protected boolean isValidChar(char ch) {
        return true;
    }
}
