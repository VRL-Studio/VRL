/* 
 * PluginNameType.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn),
 * Copyright (c) 2007–2017 by Michael Hoffer
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

package eu.mihosoft.vrl.types;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.lang.Patterns;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.system.PluginIdentifier;
import eu.mihosoft.vrl.visual.Message;
import eu.mihosoft.vrl.visual.MessageType;
import groovy.lang.Script;

/**
 * TypeRepresentation for
 * <code>Java.lang.String</code>.
 * 
 * Style name: "plugin-name"
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@TypeInfo(type=String.class, input = true, output = false, style="plugin-name")
public class PluginNameType extends VTextFieldBasedTypeRepresentation {

    private static final long serialVersionUID = 5616265208794277347L;

    /**
     * Constructor.
     */
    public PluginNameType() {
        nameLabel.setText("Name:");
    }

    @Override
    public void setViewValue(Object o) {
        input.setText(o.toString());
    }

    @Override
    public Object getViewValue() {
        String o = null;
        try {
            o = input.getText();
        } catch (Exception e) {
        }

        return o;
    }

    @Override
    protected Message getInvalidInputDataMessage() {
        Message result = new Message("Invalid Input Data:",
                ">> only values that match the regex pattern "
                + Message.EMPHASIZE_BEGIN
                + Patterns.PLUGIN_NAME_STRING + Message.EMPHASIZE_END
                + " are allowed!",
                MessageType.ERROR);
        
        return result;
    }

    @Override
    protected void evaluationRequest(Script script) {
        Object property = null;

        if (getValueOptions() != null) {

            if (getValueOptions().contains("value")) {
                property = script.getProperty("value");
            }

            if (property != null) {

                Object v = getViewValueWithoutValidation();

                if (v == null
                        || (v instanceof String && ((String) v).isEmpty())) {
                    setViewValue((String) property);
                }
            }
        }
    }

    @Override
    public String getValueAsCode() {
        return "\""
                + VLangUtils.addEscapesToCode(getValue().toString()) + "\"";
    }
    
    @Override
    protected void evaluateContract() {

        super.evaluateContract();


        if(value==null || !isValidValue()) {
            return;
        }

        String s = value.toString();
        
        PluginIdentifier pId = new PluginIdentifier(s, "0");
        
        
        if (!pId.isNameValid()) {
            getMainCanvas().getMessageBox().addMessage("Plugin-Name invalid:",
                    ">> the plugin-name you entered is invalid. "
                    + "Only names that match the following regular expression"
                    + " are allowed: " + Patterns.PLUGIN_NAME_STRING
                    +"<br><br>"
                    + "Example: VRL-TestPlugin",
                    getConnector(),MessageType.ERROR);

            setValidValue(false);
        }

    }
}
