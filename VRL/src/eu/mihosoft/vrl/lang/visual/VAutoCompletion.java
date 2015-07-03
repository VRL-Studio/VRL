/* 
 * VAutoCompletion.java
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

import eu.mihosoft.vrl.annotation.AutoCompletionInfo;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VAutoCompletion extends AutoCompletion {

    private Collection<ReplacementRule> replacementRules =
            new ArrayList<ReplacementRule>();

    public VAutoCompletion(CompletionProvider cp) {
        super(cp);
    }

    public void addReplacementRule(ReplacementRule rule) {
        replacementRules.add(rule);
    }

    public void removeReplacementRule(ReplacementRule rule) {
        replacementRules.remove(rule);
    }

    @Override
    protected void insertCompletion(Completion c, boolean typedParamListStartChar) {

        super.insertCompletion(c, typedParamListStartChar);

        JTextComponent textComponent = getTextComponent();

        Caret caret = textComponent.getCaret();

        int dot = caret.getDot();
        int start = 0;

        String text = null;

        try {
            text = textComponent.getText(0, dot);
        } catch (BadLocationException ex) {
            Logger.getLogger(VAutoCompletion.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        for (ReplacementRule rule : replacementRules) {
            text = rule.replace(text, c);
        }

        caret.setDot(start);
        caret.moveDot(dot);

        textComponent.replaceSelection(text);
    }

    public static boolean isHideFromAutoCompleteEnabled(Method m) {
        AutoCompletionInfo info = null;
        @SuppressWarnings("unchecked")
        Annotation a = m.getAnnotation(AutoCompletionInfo.class);

        if (a != null) {
            info = (AutoCompletionInfo) a;
            return info.hide();
        } else {
            return false;
        }
    }
}
