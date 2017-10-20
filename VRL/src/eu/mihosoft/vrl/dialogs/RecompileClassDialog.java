/* 
 * RecompileClassDialog.java
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

package eu.mihosoft.vrl.dialogs;

import eu.mihosoft.vrl.visual.Canvas;
import eu.mihosoft.vrl.visual.Message;
import eu.mihosoft.vrl.visual.VDialog;
import java.awt.Component;
import javax.swing.JOptionPane;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class RecompileClassDialog {

    /**
     * Shows the dialog.
     * @param parent the parent component of the dialog
     * @param className the name of the class to recompile
     * @return <code>true</code> if the "yes" button has been clicked;
     *         <code>false</code> otherwise
     */
    public static boolean show(Canvas parent, String className) {
//        int answer = JOptionPane.showConfirmDialog(
//                parent,
//                "Do you really want to recompile class \""
//                + className + "\"?\n"
//                + "Existing instances of this class will be replaced!\n\n"
//                + "If the public interface of the new class is different from the\n"
//                + "old one visualizations will be removed.",
//                "Recompile Class?",
//                JOptionPane.YES_NO_OPTION);
//        if (answer == 0) {
//            result = true;
//        }
        
        VDialog.AnswerType answer = VDialog.showConfirmDialog(
                parent,
                "Recompile Class?",
                "<html>Do you really want to recompile class " 
                + Message.EMPHASIZE_BEGIN
                + className + Message.EMPHASIZE_END + "?<br>"
                + "Existing instances of this class will be replaced!<br><br>"
                + "If the public interface of the new class is different from the<br>"
                + "old one visualizations will be removed.</html>",
                
                VDialog.YES_NO);
        
        return answer == VDialog.YES;
    }
}
