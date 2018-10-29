/* 
 * GroovyFunction3DType.java
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
package eu.mihosoft.vrl.types;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.math.GroovyFunction3D;
import eu.mihosoft.vrl.reflection.RepresentationType;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.visual.Message;
import eu.mihosoft.vrl.visual.MessageType;
import eu.mihosoft.vrl.visual.VBoxLayout;
import eu.mihosoft.vrl.visual.VTextField;
import groovy.lang.Script;
import java.awt.Dimension;

/**
 * TypeRepresentation for <code>eu.mihosoft.vrl.math.GroovyFunction3D</code>.
 * 
 * Style name: "default"
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@TypeInfo(type = GroovyFunction3D.class, input = true, output = false, style = "default")
public class GroovyFunction3DType extends TypeRepresentationBase {

    private static final long serialVersionUID = -2138540530752923286L;
    private VTextField input;
    private Message evaluationError;
    private String xVarName;
    private String yVarName;
    private String zVarName;

    public GroovyFunction3DType() {
        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);

        setLayout(layout);

        nameLabel.setText("Groovy function:");
        nameLabel.setAlignmentX(0.0f);
        this.add(nameLabel);

        input = new VTextField(this, "");

        int height = (int) this.input.getMinimumSize().getHeight();
        this.input.setMinimumSize(new Dimension(50, height));

        setInputDocument(input, input.getDocument());

        input.setAlignmentY(0.5f);
        input.setAlignmentX(0.0f);
        this.add(input);
    }

    @Override
    public void setCurrentRepresentationType(
            RepresentationType representationType) {
        if (representationType == RepresentationType.OUTPUT) {
            input.setEditable(false);
        }
        super.setCurrentRepresentationType(representationType);
    }

    @Override
    protected void valueInvalidated() {
        if (isInput()) {
            input.setInvalidState(true);
        }
    }

    @Override
    protected void valueValidated() {
        if (isInput()) {
            input.setInvalidState(false);
        }
    }

    @Override
    public void emptyView() {
        input.setText("");
    }

    @Override
    public void setViewValue(Object o) {
        if (o instanceof GroovyFunction3D) {
            GroovyFunction3D f = (GroovyFunction3D) o;
            f.setXVarName(xVarName);
            f.setYVarName(yVarName);
            f.setZVarName(zVarName);

            input.setText(f.getExpression());
        } else {
            input.setText("");
        }
    }

    @Override
    public Object getViewValue() {
        Object o = null;

        try {

            GroovyFunction3D function =
                    (GroovyFunction3D) getType().newInstance();

            function.setXVarName(xVarName);
            function.setYVarName(yVarName);
            function.setZVarName(zVarName);

            function.setExpression(input.getText());
            function.run(0., 0., 0.);
            o = function;

        } catch (Exception ex) {

            String message = ex.getMessage();

            evaluationError = new Message("Can't evaluate expression:",
                    "TypeRepresentation&lt;" + getType().toString()
                    + "&gt;.getValue():<br>" + message,
                    MessageType.ERROR);

            invalidateValue();
        }

        return o;
    }

    @Override
    protected Message getInvalidInputDataMessage() {
        Message result = evaluationError;

//        if (result==null) {
//            return super.getInvalidInputDataMessage();
//        }

//        evaluationError = null;

        return result;
    }

    @Override
    protected void evaluationRequest(Script script) {

        Object property = null;

        if (getValueOptions() != null) {

            if (getValueOptions().contains("xVarName")) {
                property = script.getProperty("xVarName");
            }

            if (property != null) {
                setxVarName((String) property);
            }

            property = null;

            if (getValueOptions().contains("yVarName")) {
                property = script.getProperty("yVarName");
            }

            if (property != null) {
                setyVarName((String) property);
            }

            property = null;

            if (getValueOptions().contains("zVarName")) {
                property = script.getProperty("zVarName");
            }

            if (property != null) {
                setzVarName((String) property);
            }
        }
    }

    @Override
    public String getValueAsCode() {
        return "new GroovyFunction3D(\"" + input.getText() + "\", \"" + xVarName + "\", \"" + yVarName + "\", \"" + zVarName + "\")";
    }

    /**
     * @param xVarName the xVarName to set
     */
    public void setxVarName(String xVarName) {

        if (!VLangUtils.isVariableNameValid(xVarName)) {
            throw new IllegalArgumentException("The name \"" + xVarName + "\" isn't a valid variable name!");
        }

        this.xVarName = xVarName;
    }

    /**
     * @param yVarName the yVarName to set
     */
    public void setyVarName(String yVarName) {

        if (!VLangUtils.isVariableNameValid(yVarName)) {
            throw new IllegalArgumentException("The name \"" + yVarName + "\" isn't a valid variable name!");
        }

        this.yVarName = yVarName;
    }

    /**
     * @param zVarName the zVarName to set
     */
    public void setzVarName(String zVarName) {
        
        if (!VLangUtils.isVariableNameValid(zVarName)) {
            throw new IllegalArgumentException("The name \"" + zVarName + "\" isn't a valid variable name!");
        }
        
        this.zVarName = zVarName;
    }
}
