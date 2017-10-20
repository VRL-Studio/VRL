/* 
 * RangeCheckBase.java
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

package eu.mihosoft.vrl.types;

import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.visual.MessageBox;
import eu.mihosoft.vrl.visual.MessageType;
import eu.mihosoft.vrl.visual.VSwingUtil;
import groovy.lang.Script;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class RangeCheckBase<T extends Number & Comparable> implements RangeCheck<T> {

    private T min;
    private T max;
    private Class<?> type;

    public RangeCheckBase(T min, T max, Class<?> type) {
        this.min = min;
        this.max = max;
        this.type = type;
    }

    @Override
    public T getMin() {
        return min;
    }

    @Override
    public void setMin(T n) {
        this.min = n;
//        if (type.isInstance(n)) {
//            this.min = n;
//        } else {
//            throw new IllegalArgumentException(
//                    "only values of class \""
//                    + type.getName() + "\" are supported!");
//        }
    }

    @Override
    public T getMax() {
        return max;
    }

    @Override
    public void setMax(T n) {
        this.max = n;
//        if (type.isInstance(n)) {
//            this.max = n;
//        } else {
//            throw new IllegalArgumentException(
//                    "only values of class \""
//                    + type.getName() + "\" are supported!");
//        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void evaluateContract(TypeRepresentationBase tRep,
            Object value) {

        T v = null;

        try {
            v = (T) value;
        } catch (Exception e) {
            //
        }

        // range condition
        if (v != null) {
            if (v.compareTo(getMin()) >= 0 && v.compareTo(getMax()) <= 0) {

//                if (!tRep.isHideConnector() && VSwingUtil.isVisible(tRep)) {
//                    tRep.getMainCanvas().getEffectPane().pulse(
//                            tRep.getConnector(),
//                            MessageType.INFO_SINGLE,
//                            tRep.getParentMethod().getParentObject().
//                            getParentWindow().getTransparency());
//                }

            } else {

                MessageBox box = tRep.getMainCanvas().getMessageBox();

                String vString = "";

                if (v.compareTo(min) < 0) {
                    v = getMin();
                    vString = "Min=" + getMin();
                }

                if (v.compareTo(min) > 0) {
                    v = getMax();
                    vString = "Max=" + getMax();
                }

                box.addUniqueMessage("Value out of range:",
                        ">> value does not meet range "
                        + "condition. Therefore value will be trimmed to "
                        + vString, tRep.getConnector(),
                        MessageType.WARNING_SINGLE);
                value = v;
                tRep.setValue(value);
                tRep.validate();
            }
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void evaluationRequest(Script script, String valueOptions) {
        Object property = null;

        if (valueOptions != null) {

            if (valueOptions.contains("min")) {
                property = script.getProperty("min");
            }

            if (property != null) {
                setMin((T) property);
            }

            property = null;

            if (valueOptions.contains("max")) {
                property = script.getProperty("max");
            }

            if (property != null) {
                setMax((T) property);
            }

            property = null;
        }
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    public RangeCheckBase<?> copy() {
        RangeCheckBase<?> result = null;
        try {

            result = getClass().getConstructor(
                    Number.class, Number.class, Class.class).
                    newInstance(getMin(), getMax(), getType());

        } catch (NoSuchMethodException ex) {
            Logger.getLogger(RangeCheckBase.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(RangeCheckBase.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(RangeCheckBase.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(RangeCheckBase.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(RangeCheckBase.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(RangeCheckBase.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        return result;
    }
}
