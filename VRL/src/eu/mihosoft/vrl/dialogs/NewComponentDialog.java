/* 
 * NewComponentDialog.java
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

package eu.mihosoft.vrl.dialogs;

import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.lang.visual.ClassInfoObject;
import eu.mihosoft.vrl.reflection.VisualCanvas;

/**
 * Shows a dialog that allows to specify component information.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class NewComponentDialog {

    /**
     * Shows the dialog.
     * @param parent the parent component of the dialog
     * @param className the name of the class to recompile
     * @return <code>true</code> if the "yes" button has been clicked;
     *         <code>false</code> otherwise
     */
    public static ClassInfoObject show(VisualCanvas parent, String title) {

        final NewComponentInfo dialogObject = new NewComponentInfo();

        boolean result = RDialog.showConfirmDialog(
                parent, title, dialogObject, "Create").isValid();

        ClassInfoObject clsInfoObj = null;

        if (result) {
            clsInfoObj = new ClassInfoObject();

            clsInfoObj.setClassName(dialogObject.getComponentName());
            clsInfoObj.setDescription(dialogObject.getDescription());
            clsInfoObj.setComponentInfo(dialogObject.getComponentInfo());
            clsInfoObj.setObjectInfo(dialogObject.getObjectInfo());
            clsInfoObj.setMethodInfo(dialogObject.getMethodInfo());
            clsInfoObj.setMethodName(dialogObject.getMethodName());
            clsInfoObj.setPackageName("");
        }

        return clsInfoObj;
    }

    /**
     * Shows the dialog.
     * @param parent the parent component of the dialog
     * @param className the name of the class to recompile
     * @return <code>true</code> if the "yes" button has been clicked;
     *         <code>false</code> otherwise
     */
    public static ClassInfoObject show(VisualCanvas parent) {

        return show(parent, "New Component");
    }
}

@ObjectInfo(serialize = false, controlFlowIn = false, controlFlowOut = false)
class NewComponentInfo extends DialogUIClass {

    private String componentName;
    private String methodName;
    private String description;
    private String componentInfo;
    private String objectInfo;
    private String methodInfo;

    public NewComponentInfo() {
        super(1);
    }

    @MethodInfo(name = "Component Properties:",
    interactive = false, hide = false)
    public void setValues(
            @ParamInfo(name = "Name:",
            style = "class-name") String name,
            @ParamInfo(name = "Description:") String description) {
        this.componentName = name;
        this.description = description;
        validCall();
    }

//    @MethodInfo(name = "Additional Component Properties:",
//    interactive = false, hide = true)
//    public void setAdditionalValues(
//            @ParamInfo(name = "ComponentInfo") String componentInfo,
//            @ParamInfo(name = "ObjectInfo") String objectInfo,
//            @ParamInfo(name = "Method Name:", options = "value=\"run\"",
//            style = "method-name") String name,
//            @ParamInfo(name = "MethodInfo") String methodInfo) {
//        this.methodName = name;
//        this.componentInfo = componentInfo;
//        this.objectInfo = objectInfo;
//        this.methodInfo = methodInfo;
//    }

    /**
     * @return the name
     */
    @MethodInfo(noGUI = true)
    public String getComponentName() {
        return componentName;
    }

    /**
     * @return the description
     */
    @MethodInfo(noGUI = true)
    public String getDescription() {
        return description;
    }

    /**
     * @return the methodName
     */
    @MethodInfo(noGUI = true)
    public String getMethodName() {
        return methodName;
    }

    /**
     * @return the componentInfo
     */
    @MethodInfo(noGUI = true)
    public String getComponentInfo() {
        return componentInfo;
    }

    /**
     * @return the objectInfo
     */
    @MethodInfo(noGUI = true)
    public String getObjectInfo() {
        return objectInfo;
    }

    /**
     * @return the methodInfo
     */
    @MethodInfo(noGUI = true)
    public String getMethodInfo() {
        return methodInfo;
    }
}
