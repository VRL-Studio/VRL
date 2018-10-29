/* 
 * TypeUtil.java
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

package eu.mihosoft.vrl.reflection;

import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.annotation.TypeInfo;

/**
 * This class allows to extract type related annotation information.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class TypeUtil {

    // no instanciation allowed
    private TypeUtil() {
        throw new AssertionError(); // not in this class either!
    }

    /**
     * Returns the parameter type name of the specified parameter. If the type
     * name defined by the param info is
     * <code>null</code> or empty the class name will be used instead.
     *
     * @param info param info annotation
     * @param type class object
     * @return the parameter type name of the specified parameter
     */
    public static String getParamTypeName(ParamInfo info, Class<?> type) {

        try {
            if (info == null) {
                return type.getName();
            }

            if (info.typeName() == null) {
                return type.getName();
            }

            if (info.typeName().isEmpty()) {
                return type.getName();
            }

            return info.typeName();

        } catch (Throwable tr) {
            // to ensure file compatibility for files created before 20.10.2011
            // try/catch block is not necessary if old files don't need support
            // anymore
            // ignore java.lang.AbstractMethodError etc.
            System.out.println(
                    ">> warning: deprecated file format (before 20.10.2011)");
        }

        return type.getName();
    }

    /**
     * Returns the group name of the specified parameter. If the group name
     * defined by the param info is
     * <code>null</code> or empty the default name will be used instead.
     *
     * @param info param info annotation
     * @return the group name of the specified parameter
     */
    public static String getParamGroupName(ParamGroupInfo info) {

        try {
            if (info == null) {
                return "default";
            }

            if (info.group() == null) {
                return "default";
            }

            if (info.group().isEmpty()) {
                return "default";
            }

            return info.group();

        } catch (Throwable tr) {
            // to ensure file compatibility for files created before 20.10.2011
            // try/catch block is not necessary if old files don't need support
            // anymore
            // ignore java.lang.AbstractMethodError etc.
            System.out.println(
                    ">> warning: deprecated file format (before 20.10.2011)");
        }

        return "default";
    }

    /**
     * Returns the return type name of the specified return type. If the type
     * name defined by the method info is
     * <code>null</code> or empty the class name will be used instead.
     *
     * @param info param info annotation
     * @param type class object
     * @return the return type name of the specified type
     */
    public static String getReturnTypeName(MethodInfo info, Class<?> type) {

        try {
            if (info == null) {
                return type.getName();
            }

            if (info.valueTypeName() == null) {
                return type.getName();
            }

            if (info.valueTypeName().isEmpty()) {
                //
                return type.getName();
            }

            return info.valueTypeName();

        } catch (Throwable tr) {
            // to ensure file compatibility for files created before 20.10.2011
            // try/catch block is not necessary if old files don't need support
            // anymore
            // ignore java.lang.AbstractMethodError etc.
        }

        return type.getName();
    }

    public static Class<?> getType(Class<? extends TypeRepresentationBase> cls) {
        TypeInfo info = cls.getAnnotation(TypeInfo.class);

        if (info != null) {
            return info.type();
        }

        return Object.class;
    }

    public static String getStyle(Class<? extends TypeRepresentationBase> cls) {
        TypeInfo info = cls.getAnnotation(TypeInfo.class);

        if (info != null) {
            return info.style();
        }

        return "default";
    }

    public static boolean supportsInput(Class<? extends TypeRepresentationBase> cls) {

        TypeInfo info = cls.getAnnotation(TypeInfo.class);

        if (info != null) {
            return info.input();
        }

        return false;
    }

    public static boolean supportsOutput(Class<? extends TypeRepresentationBase> cls) {

        TypeInfo info = cls.getAnnotation(TypeInfo.class);

        if (info != null) {
            return info.output();
        }

        return false;
    }

    public static boolean supports(
            Class<? extends TypeRepresentationBase> cls, RepresentationType valueType) {
        switch (valueType) {
            case INPUT:
                return supportsInput(cls);
            case OUTPUT:
                return supportsOutput(cls);
            default:
                return false;
        }
    }
}
