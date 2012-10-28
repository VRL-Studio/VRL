/* 
 * ClassNameType.java
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

package eu.mihosoft.vrl.types;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.lang.Patterns;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.visual.MessageType;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@TypeInfo(type=String.class, input = true, output = false, style="class-name")
public class ClassNameType extends StringType{

    public ClassNameType() {
        setValueName("Class-Name:");
    }

    @Override
    protected void evaluateContract() {

        super.evaluateContract();


        if(value==null || !isValidValue()) {
            return;
        }

        String s = value.toString();
        
        String packageName = VLangUtils.packageNameFromFullClassName(s);
        
        packageName = VLangUtils.slashToDot(s);
        
        if (packageName.isEmpty()) {
            packageName = "eu.mihosoft.vrl.user";
        }
        
        if (!VLangUtils.isPackageNameValid(packageName)) {
            getMainCanvas().getMessageBox().addMessage("Class-Name invalid:",
                    ">> the package-name you entered is invalid. "
                    + "Only names that match the following regular expression"
                    + " are allowed: " + Patterns.PACKAGE_NAME_STRING,
                    getConnector(),MessageType.ERROR);

            setValidValue(false);
        }
        
        String className = VLangUtils.shortNameFromFullClassName(s);
        
        
        if (!VLangUtils.isComponentClassNameValid(className)) {
            getMainCanvas().getMessageBox().addMessage("Class-Name invalid:",
                    ">> the class-name you entered is invalid. "
                    + "Only names that match the following regular expression"
                    + " are allowed: " + Patterns.VRL_CLASS_IDENTIFIER_STRING,
                    getConnector(),MessageType.ERROR);

            setValidValue(false);
        }
    }
}
