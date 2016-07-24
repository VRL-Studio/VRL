/* 
 * IntSliderTextFieldType.java
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
import eu.mihosoft.vrl.visual.VTextField;
import eu.mihosoft.vrl.visual.MessageBox;
import eu.mihosoft.vrl.visual.MessageType;
import groovy.lang.Script;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BoxLayout;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * TypeRepresentation for <code>java.lang.Integer</code>.
 * 
 * Style name: "slider+text"
 *
 * @author David Wittum
 */
@TypeInfo(type=Integer.class, input = true, output = false, style="slider+text")
public class IntSliderTextFieldType extends TypeRepresentationBase {

    private Integer newValue = null;
    private VTextField sliderTextField;
    private JSlider improvedSlider;
    private Integer max = 255;
    private Integer min = 0;

    public IntSliderTextFieldType() {

        BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
        setLayout(layout);

        setValueOptions("min=" + getMinValue() + ";max =" + getMaxValue());

        setValueName("Integer:");
        sliderTextField = new VTextField(this);
        sliderTextField.setText("" + min);
        sliderTextField.addKeyListener(new SliderTextFieldListener());
        sliderTextField.setEditable(true);
        improvedSlider = new JSlider(min, max) {

            @Override
            public void setValue(int i) {
                super.setValue(i);

                if (i == getValue() || i > getMaximum() || i < getMinimum()) {
                    int userValue = (int) this.getValue();
                    setNewValue(userValue);
                    sliderTextField.setText("" + userValue);
                }
            }
        };
        improvedSlider.addChangeListener(new ImprovedSliderListener());

        setInputDocument(sliderTextField, sliderTextField.getDocument());

        nameLabel.setAlignmentX(LEFT_ALIGNMENT);
        sliderTextField.setAlignmentX(LEFT_ALIGNMENT);
        improvedSlider.setAlignmentX(LEFT_ALIGNMENT);

        add(nameLabel);
        add(improvedSlider);
        add(sliderTextField);

        improvedSlider.setValue(min);

        int height = (int) this.improvedSlider.getMinimumSize().getHeight();
        this.improvedSlider.setPreferredSize(new Dimension(100, height));
        this.improvedSlider.setMinimumSize(new Dimension(100, height));

    }

    public void setNewValue(Integer wantedValue) {
        newValue = wantedValue;
    }

    public Integer getNewValkue() {
        return newValue;
    }

    public Integer getMinValue() {
        return min;
    }

    public Integer getMaxValue() {
        return max;
    }

    public void setMinValue(Integer a) {
        min = a;
        improvedSlider.setMinimum(min);
    }

    public void setMaxValue(Integer a) {
        max = a;
        improvedSlider.setMaximum(max);
    }

    private class SliderTextFieldListener implements KeyListener {

        public void keyTyped(KeyEvent ke) {
            //unimportand not implemented
//            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void keyPressed(KeyEvent ke) {
            //unimportand not implemented
//            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void keyReleased(KeyEvent ke) {

            String userPost = sliderTextField.getText();

            try {
                int userValue = new Integer(userPost);
                improvedSlider.setValue(userValue);
            } catch (Exception ex) {
            }
        }
    }

    private class ImprovedSliderListener implements ChangeListener {

        public ImprovedSliderListener() {
        }

        public void stateChanged(ChangeEvent ce) {
            JSlider source = (JSlider) ce.getSource();
//            if (!source.getValueIsAdjusting()) {
            int userValue = (int) source.getValue();
            setNewValue(userValue);
            sliderTextField.setText("" + userValue);
//            }
        }
    }

    @Override
    public void setViewValue(Object o) {
        Integer i = (Integer) o;
        improvedSlider.setValue(i);
    }

    @Override
    public Object getViewValue() {
        return newValue;
    }

    @Override
    protected void valueInvalidated() {
        if (isInput()) {
            sliderTextField.setInvalidState(true);
        }
    }

    @Override
    protected void valueValidated() {
        if (isInput()) {
            sliderTextField.setInvalidState(false);
        }
    }

    @Override
    public void emptyView() {
        sliderTextField.setText("");
        setNewValue(null);
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
                        "IntSliderType: value does not meet range "
                        + "condition. Therefore value will be trimmed to "
                        + vString, getConnector(),
                        MessageType.WARNING_SINGLE);
                setViewValue(v);
                value = v;
                validateValue();
            }
        }

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

