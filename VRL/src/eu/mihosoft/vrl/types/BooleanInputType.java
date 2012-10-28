/* 
 * BooleanInputType.java
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
import eu.mihosoft.vrl.reflection.RepresentationType;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.visual.VBoxLayout;
import eu.mihosoft.vrl.visual.VSwingUtil;
import groovy.lang.Script;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * TypeRepresentation for
 * <code>Java.lang.Boolean</code>.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@TypeInfo(type = Boolean.class, input = true, output = false, style="default")
public class BooleanInputType extends TypeRepresentationBase {

    private static final long serialVersionUID = 1478190886881456419L;
    private JCheckBox checkbox = new JCheckBox();
//    private JRadioButton radioBtn = new JRadioButton();

    /**
     * Constructor.
     */
    public BooleanInputType() {

        // Defines the value name. This will set the caption of the name label.
        setValueName("Boolean:");

        // Sets the layout.
        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);
        setLayout(layout);

        // Makes the background of checkbox button transparent.
        checkbox.setBackground(VSwingUtil.TRANSPARENT_COLOR);

        // Adds a change listener
        checkbox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setDataOutdated();
            }
        });

        // Adds components to the type representation.
        this.add(nameLabel);
        this.add(checkbox);
    }

    @Override
    public void setViewValue(Object o) {
        Boolean in = (Boolean) o;

        checkbox.setSelected(in);
    }

    @Override
    public Object getViewValue() {
        return checkbox.isSelected();
    }

    @Override
    public String getValueAsCode() {
        return ((Boolean) checkbox.isSelected()).toString();
    }
    
    @Override
    protected void evaluationRequest(Script script) {
        Object property = null;

        if (getValueOptions() != null) {

            if (getValueOptions().contains("value")) {
                property = script.getProperty("value");
            }

            if (property != null) {
                if (!getMainCanvas().isLoadingSession()) {
                    setViewValue(property);
                }
            }
        }
    }
}
