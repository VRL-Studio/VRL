/* 
 * IntSliderType.java
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
import eu.mihosoft.vrl.reflection.*;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import eu.mihosoft.vrl.visual.MessageBox;
import eu.mihosoft.vrl.visual.MessageType;
import eu.mihosoft.vrl.visual.VBoxLayout;
import eu.mihosoft.vrl.visual.VContainer;
import groovy.lang.Script;

/**
 * TypeRepresentation for <code>Java.lang.Integer</code>.
 * 
 * <p>Sample:</p>
 * <br/>
 * <img src="doc-files/integer-slider-01.png"/>
 * <br/>
 * 
 * Style name: "slider"
 * 
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@TypeInfo(type=Integer.class, input = true, output = false, style="slider")
public class IntSliderType extends TypeRepresentationBase {

    private static final long serialVersionUID = -1968462481826019249L;
    private Integer maxValue = 255;
    private Integer minValue = 0;
    private JSlider input = new JSlider();
    private TypeRepresentationLabel valueLabel;

    /**
     * Constructor.
     */
    public IntSliderType() {
        VBoxLayout layout = new VBoxLayout(this, VBoxLayout.Y_AXIS);

        setLayout(layout);

        valueLabel = new TypeRepresentationLabel(this,
                getMinValue().toString());
        nameLabel.setText("Integer:");
        nameLabel.setAlignmentX(0.0f);

        this.add(nameLabel);

        int height = (int) this.input.getMinimumSize().getHeight();
        this.input.setPreferredSize(new Dimension(100, height));
        this.input.setMinimumSize(new Dimension(100, height));

        this.add(input);

        height = (int) this.valueLabel.getMinimumSize().getHeight();
        this.valueLabel.setPreferredSize(new Dimension(40, height));
        this.valueLabel.setMinimumSize(new Dimension(40, height));

        this.add(valueLabel);

        input.setMinimum(getMinValue());
        input.setMaximum(getMaxValue());

        // enhances design for default laf, but breaks Nimbus support
//        input.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));

        input.setValue(getMinValue());

        setValueOptions("min=" + getMinValue() + ";max =" + getMaxValue());

        input.setAlignmentX(0.0f);

        input.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                Integer value = input.getValue();
                valueLabel.setText(value.toString());
                setDataOutdated();
            }
        });
    }

    @Override
    public void emptyView() {
        input.setValue(minValue);
        valueLabel.setText(minValue.toString());
    }

    @Override
    public void setViewValue(Object o) {
        Integer v = null;
        try {
            v = (Integer) o;
        } catch (Exception e) {
        }

        input.setValue(v);
        valueLabel.setText(o.toString());

        if (v < 0 || v > getMaxValue()) {
            getMainCanvas().getEffectPane().pulse(getConnector(),
                    MessageType.WARNING_SINGLE);
        }
    }

    @Override
    public Object getViewValue() {
        Integer o = null;
        try {
            o = new Integer(valueLabel.getText());
        } catch (Exception e) {
        }

        return o;
    }

    @Override
    protected void evaluateContract() {
        Integer v = null;

        try {
            v = (Integer) value;
        } catch (Exception e) {
            //
        }

        // range condition
        if (v != null) {
            if (v >= getMinValue() && v <= getMaxValue()) {
                double transparency =
                        getParentMethod().getParentObject().getParentWindow().
                        getTransparency();
                
                if (!isHideConnector()) {
                    getMainCanvas().getEffectPane().pulse(getConnector(),
                            MessageType.INFO_SINGLE, transparency);
                }
                
            } else {

                MessageBox box = getMainCanvas().getMessageBox();

                String vString = "";

                if (v < getMinValue()) {
                    v = getMinValue();
                    vString = "Min=" + getMinValue();
                }

                if (v > getMaxValue()) {
                    v = getMaxValue();
                    vString = "Max=" + getMaxValue();
                }

                box.addUniqueMessage("Value out of range:",
                        "IntSliderType: value does not meet range " +
                        "condition. Therefore value will be trimmed to " +
                        vString, getConnector(),
                        MessageType.WARNING_SINGLE);
                setViewValue(v);
                value = v;
                validateValue();
            }
        }

    }

    /**
     * @return the maxValue
     */
    public Integer getMaxValue() {
        return maxValue;
    }

    /**
     * @param maxValue the maxValue to set
     */
    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
        input.setMaximum(maxValue);
    }

    /**
     * @return the minValue
     */
    public Integer getMinValue() {
        return minValue;
    }

    /**
     * @param minValue the minValue to set
     */
    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
        input.setMinimum(minValue);
    }

    @Override
    protected void evaluationRequest(Script script) {
        Object property = null;

        if (getValueOptions() != null) {

            if (getValueOptions().contains("min")) {
                property = script.getProperty("min");
            }

            if (property != null) {
                setMinValue((Integer) property);
            }

            property = null;

            if (getValueOptions().contains("max")) {
                property = script.getProperty("max");
            }

            if (property != null) {
                setMaxValue((Integer) property);
            }

            property = null;
        }
    }

    @Override()
    public String getValueAsCode() {
        String result = "null";

        Object o = getValue();

        if (o != null) {
            result = o.toString();
        }

        return result;
    }
}
