/* 
 * NumberBasedTypeRepresentation.java
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

import groovy.lang.Script;

/**
 * 
 * <p>TypeRepresentation base class for number based type representations.</p>
 * <p><b>Supported value options:</b><br>
 * <ul>
 * <li>value (defines a default value)</li>
 * <li>min (defines a minimum value)</li>
 * <li>max (defines a maximum value)</li>
 * </ul>
 * <p><b>Example (Groovy code):</b></p>
 * <code>
 * <pre>
 * &#64;ComponentInfo(name="Add Integers")
 * class AddIntegers implements Serializable {
 *
 *   private static final long serialVersionUID=1;
 *
 *   public Integer add(&#64;ParamInfo(options="value=23;min=1;max=30") Integer a, Integer b){
 *     return a+b
 *   }
 * }
 * </pre>
 * </code>
 * </p>
 * 
 * <p>The visualization should look like this:</p>
 * <br/>
 * <img src="doc-files/AddIntegers.png"/>
 * <br/>
 * 
 * 
 * @author Michael Hoffer <info@michaelhoffer.de>
 * @see IntegerType
 */
public abstract class NumberBasedTypeRepresentation
        extends VTextFieldBasedTypeRepresentation {

    private RangeCheck<?> rangeCheck;

    /**
     * Constructor.
     */
    public NumberBasedTypeRepresentation() {
        //
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Object getViewValue() {
        Object o = null;

        String inputText = input.getText().trim();
        if (inputText.length() > 0) {
            try {

                o = getType().getConstructor(String.class).
                        newInstance(inputText);
            } catch (Exception e) {
                invalidateValue();
            }
        }

        return o;
    }

    @Override
    protected void evaluateContract() {

        super.evaluateContract();

        if (rangeCheck != null) {
            rangeCheck.evaluateContract(this, value);
        }

    }

//    /**
//     * @return the maxValue
//     */
//    public Integer getMaxValue() {
//        return maxValue;
//    }
//
//    /**
//     * @param maxValue the maxValue to set
//     */
//    public void setMaxValue(Number maxValue) {
//        this.maxValue = maxValue;
//    }
//
//    /**
//     * @return the minValue
//     */
//    public Number getMinValue() {
//        return minValue;
//    }
//
//    /**
//     * @param minValue the minValue to set
//     */
//    public void setMinValue(Integer minValue) {
//        this.minValue = minValue;
//    }
    @Override
    protected void evaluationRequest(Script script) {
        Object property = null;

        if (getValueOptions() != null) {

            if (getValueOptions().contains("value")) {
                property = script.getProperty("value");
            }

            if (property != null) {
                if (getViewValueWithoutValidation() == null) {
                    setViewValue((Number) property);
                }
            }
        }
        if (rangeCheck != null) {
            rangeCheck.evaluationRequest(script, getValueOptions());
        }
    }

    /**
     * @return the rangeCheck
     */
    protected RangeCheck getRangeCheck() {
        return rangeCheck;
    }

    /**
     * @param rangeCheck the rangeCheck to set
     */
    protected void setRangeCheck(RangeCheck rangeCheck) {
        this.rangeCheck = rangeCheck;
    }

    protected void setRangeType(Class<?> type) {
        setRangeCheck(RangeCheckProvider.newInstance(type));
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
