/* 
 * VShortHandCompletion.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2012 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2006–2012 by Michael Hoffer
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
 * Third, add an additional notice, stating that you modified VRL. In addition
 * you must cite the publications listed below. A suitable notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Publications:
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, 2011, in press.
 */

package eu.mihosoft.vrl.lang.visual;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

/**
 * A completion where the input text is shorthand for (really, just different
 * than) the actual text to be inserted.  For example, the input text
 * "<code>sysout</code>" could be associated with the completion
 * "<code>System.out.println(</code>" in Java.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class VShortHandCompletion extends VBasicCompletion {

    /**
     * The text the user can start typing that will match this completion.
     */
    private String inputText;

    /**
     * Constructor.
     *
     * @param provider The provider that returns this completion.
     * @param inputText The text the user inputs to get this completion.
     * @param replacementText The replacement text of the completion.
     */
    public VShortHandCompletion(CompletionProvider provider, String inputText,
            String replacementText) {
        super(provider, replacementText, CompletionType.SHORTHAND);
        this.inputText = inputText;
    }

    /**
     * Constructor.
     *
     * @param provider The provider that returns this completion.
     * @param inputText The text the user inputs to get this completion.
     * @param replacementText The replacement text of the completion.
     * @param shortDesc A short description of the completion.  This will be
     *        displayed in the completion list.  This may be <code>null</code>.
     */
    public VShortHandCompletion(CompletionProvider provider, String inputText,
            String replacementText, String shortDesc) {
        super(provider, replacementText, shortDesc, CompletionType.SHORTHAND);
        this.inputText = inputText;
    }

    /**
     * Constructor.
     *
     * @param provider The provider that returns this completion.
     * @param inputText The text the user inputs to get this completion.
     * @param replacementText The replacement text of the completion.
     * @param shortDesc A short description of the completion.  This will be
     *        displayed in the completion list.  This may be <code>null</code>.
     * @param summary The summary of this completion.  This should be HTML.
     *        This may be <code>null</code>.
     */
    public VShortHandCompletion(CompletionProvider provider, String inputText,
            String replacementText, String shortDesc, String summary) {
        super(provider, replacementText, shortDesc, summary,
                CompletionType.SHORTHAND);
        this.inputText = inputText;
    }

    /**
     * Returns the text the user must start typing to get this completion.
     *
     * @return The text the user must start to input.
     */
    @Override
    public String getInputText() {
        return inputText;
    }

    /**
     * If a summary has been set, that summary is returned.  Otherwise, the
     * replacement text is returned.
     *
     * @return A description of this completion (the text that will be
     *         inserted).
     * @see #getReplacementText()
     */
    @Override
    public String getSummary() {
        String summary = super.getSummary();
        return summary != null ? summary : ("<html><body>" + getSummaryBody());
    }

    /**
     * Returns the "body" of the HTML returned by {@link #getSummary()} when
     * no summary text has been set.  This is defined to return the replacement
     * text in a monospaced font.
     *
     * @return The summary text's body, if no other summary has been defined.
     * @see #getReplacementText()
     */
    protected String getSummaryBody() {
        return "<tt>" + getReplacementText();
    }
    
    /**
	 * Returns a string representation of this completion.  If the short
	 * description is not <code>null</code>, this method will return:
	 * 
	 * <code>getInputText() + " - " + shortDesc</code>
	 * 
	 * otherwise, it will return <tt>getInputText()</tt>.
	 *
	 * @return A string representation of this completion.
	 */
    @Override
	public String toString() {
		if (getShortDescription()==null) {
			return getInputText();
		}
		return getInputText() + " - " + getShortDescription();
	}
}
