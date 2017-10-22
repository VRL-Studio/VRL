/* 
 * DefaultParamInfo.java
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

package eu.mihosoft.vrl.annotation;

import eu.mihosoft.vrl.reflection.*;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 * Default param info.
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
final public class DefaultParamInfo implements ParamInfo, Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String valueStyle;
    private boolean nullIsValid;
    private String valueOptions;

    /**
     * Constructor.
     */
    public DefaultParamInfo() {
	setName("");
	setValueStyle("default");
	setNullIsValid(false);
	setValueOptions("");
    }

    /**
     * Constructor.
     * @param name the name
     */
    public DefaultParamInfo(String name) {
	setName(name);
	setValueStyle("default");
	setNullIsValid(false);
        setValueOptions("");
    }

    /**
     * Constructor.
     * @param name the name
     * @param valueStyle the style
     */
    public DefaultParamInfo(String name, String valueStyle) {
	setName(name);
	setValueStyle(valueStyle);
	setNullIsValid(false);
        setValueOptions("");
    }

    /**
     * Constructor.
     * @param name the name
     * @param valueStyle the style
     * @param nullIsValid defines whether <code>null</code> is valid
     */
    public DefaultParamInfo(String name, String valueStyle, boolean nullIsValid) {
	setName(name);
	setValueStyle(valueStyle);
	setNullIsValid(nullIsValid);
        setValueOptions("");
    }

    /**
     * Constructor.
     * @param name the name
     * @param valueStyle the style
     * @param valueOptions the value options
     * @param nullIsValid defines whether <code>null</code> is valid
     */
    public DefaultParamInfo(String name, String valueStyle, String valueOptions,
            boolean nullIsValid) {
	setName(name);
	setValueStyle(valueStyle);
	setNullIsValid(nullIsValid);
        setValueOptions("");
    }

    @Override
    public String name() {
	return name;
    }

    @Override
    public String style() {
	return valueStyle;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
	return null;
    }

    /**
     * Defines the name.
     * @param name the name to set
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * Defines the value style.
     * @param valueStyle the style to set
     */
    public void setValueStyle(String valueStyle) {
	this.valueStyle = valueStyle;
    }

    @Override
    public boolean nullIsValid() {
	return nullIsValid;
    }

    /**
     * Defines whether <code>null</code> is valid.
     * @param nullIsValid defines whether <code>null</code> is valid
     */
    public void setNullIsValid(boolean nullIsValid) {
	this.nullIsValid = nullIsValid;
    }

    @Override
    public String options() {
	return valueOptions;
    }

    /**
     * Defines the value options.
     * @param valueOptions the options to set
     */
    public void setValueOptions(String valueOptions) {
	this.valueOptions = valueOptions;
    }

    @Override
    public String typeName() {
        return "";
    }
}
