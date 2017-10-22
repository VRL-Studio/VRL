/* 
 * ClassInfoObject.java
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

package eu.mihosoft.vrl.lang.visual;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.io.vrlx.AbstractCode;
import eu.mihosoft.vrl.io.vrlx.AbstractSession;
import eu.mihosoft.vrl.lang.VLangUtils;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.types.CanvasRequest;
import eu.mihosoft.vrl.visual.MessageType;
import java.io.Serializable;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
@ComponentInfo(name = "Create Session-Class",
category = "VRL/Session", allowRemoval = false)
@ObjectInfo(multipleViews = false, name = "Session-Class-Dialog", instances = 1)
public class ClassInfoObject implements Serializable {

    private static final long serialVersionUID = 1L;
    private String packageName;
    private String className;
    private String methodName;
    private String objectInfo;
    private String componentInfo;
    private String methodInfo;
    private String description;

    @MethodInfo(noGUI = true)
    public AbstractCode createSessionCode(VisualCanvas canvas,
            String className,
            String methodName) {
        return createSessionCode(canvas, className, methodName, false);
    }

    @MethodInfo(noGUI = true)
    public AbstractCode createSessionCode(VisualCanvas canvas,
            String className,
            String methodName,
            boolean showMessage) {
        this.setClassName(className);
        this.setMethodName(methodName);
        this.setMethodInfo(""); // empty methodInfo
        this.setObjectInfo(""); // empty methodInfo

        canvas.getSession().setCode(
                SessionClassUtils.createSessionClassCode(canvas, this));

        canvas.getSession().setDescription(getDescription());

        if (canvas.getSession().getCode() != null && showMessage) {
            canvas.getMessageBox().addMessage("Session Class Created:",
                    ">> session-class \"" + className + "\" has been"
                    + " successfully created. Save this file and import it to"
                    + " use this session-class.", MessageType.INFO);
        } else if (showMessage) {
            canvas.getMessageBox().addMessage("Cannot create Session Class:",
                    ">> session-class \"" + className + "\" cannot be created! "
                    + " If no other messages have been shown, the error is"
                    + " unknown. In this case, please contact the VRL developers"
                    + " for help!", MessageType.ERROR);
        }


        return canvas.getSession().getCode();
    }

    @MethodInfo(name = "Session-Class-Definition",
    interactive = true, valueStyle = "silent",
    valueName = " ",
    buttonText = "Create Session-Code")
    public void info(CanvasRequest cReq,
            @ParamInfo(name = "Class-Name:",
            style = "class-name",
            options = "hideConnector=true") String className,
            @ParamInfo(name = "Method-Name:",
            style = "method-name",
            options = "hideConnector=true") String methodName) {
        createSessionCode(cReq.getCanvas(), className, methodName, true);
    }

    /**
     * @return the name
     */
    @MethodInfo(noGUI = true)
    public String getClassName() {
        return className;
    }

    /**
     * @return the param info
     */
    @MethodInfo(noGUI = true)
    public String getMethodName() {
        return methodName;
    }

    /**
     * @return the objectInfo
     */
    @MethodInfo(noGUI = true)
    public String getObjectInfo() {

        if (objectInfo == null || objectInfo.equals("")) {
            setObjectInfo("name=\"" + getClassName() + "\"");
        }

        return objectInfo;
    }

    /**
     * @param objectInfo the objectInfo to set
     */
    @MethodInfo(hide = true,
    name = "Additional Options:",
    interactive = false,
    valueStyle = "silent",
    valueName = " ")
    public void additionalInfo(
            @ParamInfo(name = "Package-Name:",
            style = "package-name",
            options = "hideConnector=true") String packageName,
            @ParamInfo(name = "Component-Info:",
            options = "hideConnector=true") String componentInfo,
            @ParamInfo(name = "Object-Info:",
            options = "hideConnector=true") String objectInfo,
            @ParamInfo(name = "Method-Info:",
            options = "hideConnector=true") String methodInfo,
            @ParamInfo(name = "Description:", style = "editor",
            options = "hideConnector=true") String description) {
        this.setPackageName(packageName);
        this.setObjectInfo(objectInfo);
        this.setComponentInfo(componentInfo);
        this.setMethodInfo(methodInfo);
        this.setDescription(description);
    }

    /**
     * @return the componentInfo (escapes characters)
     * @see VLangUtils#addEscapesToCode(java.lang.String) 
     */
    @MethodInfo(noGUI = true)
    public String getComponentInfo() {
        if (componentInfo == null || componentInfo.equals("")) {
            setComponentInfo("name=\"" + getClassName()
                    + "\", category=\"Custom\"" + ", description=\""
                    + VLangUtils.addEscapesToCode(description) + "\"");
        }

        return componentInfo;
    }

    /**
     * @return the methodInfo
     */
    @MethodInfo(noGUI = true)
    public String getMethodInfo() {

        if (methodInfo == null) {
            methodInfo = "";
        }

        return methodInfo;
    }

    /**
     * @return the packageName
     */
    @MethodInfo(noGUI = true)
    public String getPackageName() {
        return packageName;
    }

    /**
     * @return the description
     */
    @MethodInfo(noGUI = true)
    public String getDescription() {
        return description;
    }

    /**
     * @param packageName the packageName to set
     */
    @MethodInfo(noGUI = true)
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * @param className the className to set
     */
    @MethodInfo(noGUI = true)
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @param methodName the methodName to set
     */
    @MethodInfo(noGUI = true)
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * @param objectInfo the objectInfo to set
     */
    @MethodInfo(noGUI = true)
    public void setObjectInfo(String objectInfo) {
        this.objectInfo = objectInfo;
    }

    /**
     * @param componentInfo the componentInfo to set
     */
    @MethodInfo(noGUI = true)
    public void setComponentInfo(String componentInfo) {
        this.componentInfo = componentInfo;
    }

    /**
     * @param methodInfo the methodInfo to set
     */
    @MethodInfo(noGUI = true)
    public void setMethodInfo(String methodInfo) {
        this.methodInfo = methodInfo;
    }

    /**
     * @param description the description to set
     */
    @MethodInfo(noGUI = true)
    public void setDescription(String description) {
        this.description = description;
    }
}
