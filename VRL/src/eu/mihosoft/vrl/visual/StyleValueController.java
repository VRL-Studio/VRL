/* 
 * StyleValueController.java
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

package eu.mihosoft.vrl.visual;

//Error reading included file Templates/Classes/../Licenses/license-gplv3classpath.txt
import java.awt.Color;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;

/**
 * 
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public final class StyleValueController {

    private StyleValues values = new StyleValues();

    public void set(String key, Object obj) {
        if (key == null) {
            throw new IllegalArgumentException(
                    "Argument \"key=null\" is not supported!");
        }

//        Object value = get(key);

//        if ((value != null && obj == null)
//                || (value != null && !value.getClass().isAssignableFrom(obj.getClass()))) {
//            throw new IllegalArgumentException(
//                    "A value with the specified key already exists."
//                    + " Overriding the value is only supported for instances of"
//                    + " a subclass (or the same class) as the current value!");
//        }

        values.put(key, obj);
    }

    public Object get(String key, Class<?> cls) {
        Object obj = values.get(key);

        if (cls == null || key == null) {
            throw new IllegalArgumentException(
                    "Argument \"null\" is not supported!");
        }

        if (obj != null && cls.isAssignableFrom(obj.getClass())) {
            return obj;
        }

        return null;
    }

    public Object get(String key) {
        return get(key, Object.class);
    }

    public Color getColor(String key) {
        Object obj = get(key);

        if (obj instanceof Color) {
            return (Color) obj;
        }

        return null;
    }

    public Integer getInteger(String key) {
        return (Integer) get(key, Integer.class);
    }

    public Long getLong(String key) {
        Object obj = get(key);

        if (obj instanceof Long) {
            return (Long) obj;
        }

        return null;
    }

    public String getString(String key) {
        Object obj = get(key);

        if (obj instanceof String) {
            return (String) obj;
        }

        return null;
    }

    public Style getStyle(String key) {
        Object obj = get(key);

        if (obj instanceof Style) {
            return (Style) obj;
        }

        return null;
    }

    /**
     * @return the elements
     */
    public StyleValues getValues() {
        return values;
    }

    /**
     * @param elements the elements to set
     */
    public void setValues(StyleValues values) {
        this.values = values;
    }

    public Float getFloat(String key) {
        Object obj = get(key);

        if (obj instanceof Float) {
            return (Float) obj;
        }

        return null;
    }

    public Double getDouble(String key) {
        Object obj = get(key);

        if (obj instanceof Double) {
            return (Double) obj;
        }

        return null;
    }

    public Boolean getBoolean(String key) {
        Object obj = get(key);

        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }

        return null;
    }

    public Painter getPainter(String key, VComponent parent) {
        Object obj = get(key);

        if (obj instanceof Painter) {
            return ((Painter) obj).newInstance(parent);
        }

        return null;
    }
    
    public SyntaxScheme getEditorStyle(String key) {
        String scheme = getString(key);

        if (scheme!=null) {
            return SyntaxScheme.loadFromString(scheme);
        }

        return null;
    }
    
    public void setEditorStyle(String key, SyntaxScheme sheme) {
        set(key, sheme.toCommaSeparatedString());
    }
}
